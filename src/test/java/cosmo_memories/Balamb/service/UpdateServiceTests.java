package cosmo_memories.Balamb.service;

import cosmo_memories.Balamb.model.accounts.LibraryUser;
import cosmo_memories.Balamb.model.enums.UpdateType;
import cosmo_memories.Balamb.model.site.Update;
import cosmo_memories.Balamb.service.accounts.UserService;
import cosmo_memories.Balamb.service.site.UpdateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UpdateServiceTests {

    @Autowired
    private UpdateService updateService;

    @Autowired
    private UserService userService;

    private final PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "created"));
    private LibraryUser user;

    @BeforeEach
    public void setUp() {
        user = userService.save(new LibraryUser("Test", "User", "test@test.test", "testPassword"));
    }

    @Test
    public void submitUpdate_Successful() {
        Update testUpdate = new Update("Submission test.", UpdateType.UPDATE);
        testUpdate.setAuthor(user);
        updateService.submitUpdate(testUpdate);

        Page<Update> result = updateService.findAllUpdates(0, 10);
        assertTrue(result.hasContent());
        assertEquals(1, result.getContent().size());
        assertEquals("Submission test.", result.getContent().getFirst().getDescription());
    }

    @Test
    public void deleteUpdate_Successful() {
        Update testUpdate = new Update("Submission test.", UpdateType.UPDATE);
        testUpdate.setAuthor(user);
        updateService.submitUpdate(testUpdate);

        Page<Update> result = updateService.findAllUpdates(0, 10);
        assertTrue(result.hasContent());
        assertEquals(1, result.getContent().size());
        assertEquals("Submission test.", result.getContent().getFirst().getDescription());

        updateService.deleteUpdate(result.getContent().getFirst().getId());

        result = updateService.findAllUpdates(0, 10);
        assertFalse(result.hasContent());
    }

    @Test
    public void deleteUpdate_Failed() {
        Update testUpdate = new Update("Submission test.", UpdateType.UPDATE);
        testUpdate.setAuthor(user);
        updateService.submitUpdate(testUpdate);

        Page<Update> result = updateService.findAllUpdates(0, 10);
        assertTrue(result.hasContent());
        assertEquals(1, result.getContent().size());
        assertEquals("Submission test.", result.getContent().getFirst().getDescription());

        Page<Update> finalResult = result;
        assertThrows(IllegalArgumentException.class, () -> updateService.deleteUpdate(finalResult.getContent().getFirst().getId() + 1));

        result = updateService.findAllUpdates(0, 10);
        assertTrue(result.hasContent());
    }

    @Test
    public void findAllUpdates_CorrectOrder() {
        Update testUpdate = new Update("Test 1", UpdateType.UPDATE);
        testUpdate.setAuthor(user);
        updateService.submitUpdate(testUpdate);
        testUpdate = new Update("Test 2", UpdateType.UPDATE);
        testUpdate.setAuthor(user);
        updateService.submitUpdate(testUpdate);
        testUpdate = new Update("Test 3", UpdateType.UPDATE);
        testUpdate.setAuthor(user);
        updateService.submitUpdate(testUpdate);

        Page<Update> result = updateService.findAllUpdates(0, 10);
        assertTrue(result.hasContent());
        assertEquals(3, result.getContent().size());
        assertEquals("Test 3", result.getContent().getFirst().getDescription());
        assertEquals("Test 1", result.getContent().getLast().getDescription());
    }

    @Test
    public void findUpdatesByType_CorrectOrder() {
        Update testUpdate = new Update("Test 1", UpdateType.TODO);
        testUpdate.setAuthor(user);
        updateService.submitUpdate(testUpdate);
        testUpdate = new Update("Test 2", UpdateType.TODO);
        testUpdate.setAuthor(user);
        updateService.submitUpdate(testUpdate);
        testUpdate = new Update("Test 3", UpdateType.TODO);
        testUpdate.setAuthor(user);
        updateService.submitUpdate(testUpdate);

        Page<Update> result = updateService.findUpdatesByType(UpdateType.TODO, 0, 10);
        assertTrue(result.hasContent());
        assertEquals(3, result.getContent().size());
        assertEquals("Test 3", result.getContent().getFirst().getDescription());
        assertEquals("Test 1", result.getContent().getLast().getDescription());
    }

    @Test
    public void findUpdatesByType_WrongTypeExcluded() {
        Update testUpdate = new Update("Test 1", UpdateType.UPDATE);
        testUpdate.setAuthor(user);
        updateService.submitUpdate(testUpdate);
        testUpdate = new Update("Test 2", UpdateType.TODO);
        testUpdate.setAuthor(user);
        updateService.submitUpdate(testUpdate);
        testUpdate = new Update("Test 3", UpdateType.TODO);
        testUpdate.setAuthor(user);
        updateService.submitUpdate(testUpdate);

        Page<Update> result = updateService.findUpdatesByType(UpdateType.TODO, 0, 10);
        assertTrue(result.hasContent());
        assertEquals(2, result.getContent().size());
        assertEquals("Test 3", result.getContent().getFirst().getDescription());
        assertEquals("Test 2", result.getContent().getLast().getDescription());
    }

    @Test
    public void resolveUpdate_Successful() {
        Update testUpdate = new Update("Test 1", UpdateType.UPDATE);
        testUpdate.setAuthor(user);
        testUpdate = updateService.submitUpdate(testUpdate);

        updateService.resolveUpdate(testUpdate.getId());

        Optional<Update> resolved = updateService.findById(testUpdate.getId());
        assertTrue(resolved.isPresent() && resolved.get().getResolved());
    }

    @Test
    public void resolveUpdate_UpdateDoesNotExist() {
        Update testUpdate = new Update("Test 1", UpdateType.UPDATE);
        testUpdate.setAuthor(user);
        testUpdate = updateService.submitUpdate(testUpdate);

        Update finalTestUpdate = testUpdate;
        assertThrows(IllegalArgumentException.class, () -> updateService.resolveUpdate(finalTestUpdate.getId() + 1));
    }


}
