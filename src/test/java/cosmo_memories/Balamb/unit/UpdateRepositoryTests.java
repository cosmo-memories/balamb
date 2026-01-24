package cosmo_memories.Balamb.unit;

import cosmo_memories.Balamb.model.accounts.LibraryUser;
import cosmo_memories.Balamb.model.enums.UpdateType;
import cosmo_memories.Balamb.model.site.Update;
import cosmo_memories.Balamb.repository.accounts.UserRepository;
import cosmo_memories.Balamb.repository.site.UpdateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UpdateRepositoryTests {

    @Autowired
    UpdateRepository updateRepository;

    @Autowired
    UserRepository userRepository;

    PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "created"));
    LibraryUser user;

    @BeforeEach
    public void setUp() {
        user = userRepository.save(new LibraryUser("Test", "User", "test@test.test", "testPassword"));

        Update update = new Update();
        update.setCreated(LocalDateTime.now());
        update.setUpdateType(UpdateType.UPDATE);
        update.setDescription("Test update.");
        update.setAuthor(user);
        updateRepository.save(update);
    }

    @Test
    public void findByType_NoneFound() {
        Page<Update> result = updateRepository.findByUpdateType(UpdateType.ISSUE, pageable);
        assertTrue(result.isEmpty());
    }

    @Test
    public void findByType_OneFound() {
        Page<Update> result = updateRepository.findByUpdateType(UpdateType.UPDATE, pageable);
        assertTrue(result.hasContent());
        assertSame("Test update.", result.getContent().getFirst().getDescription());
    }

    @Test
    public void findByType_TwoFound() {
        Update newUpdate = new Update();
        newUpdate.setUpdateType(UpdateType.UPDATE);
        newUpdate.setDescription("Another test.");
        newUpdate.setAuthor(userRepository.findByEmail("test@test.test").get());
        updateRepository.save(newUpdate);

        Page<Update> result = updateRepository.findByUpdateType(UpdateType.UPDATE, pageable);
        assertTrue(result.hasContent());
        assertEquals(2, result.getContent().size());
    }

}
