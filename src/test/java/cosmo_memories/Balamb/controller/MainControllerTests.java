package cosmo_memories.Balamb.controller;

import cosmo_memories.Balamb.model.accounts.LibraryUser;
import cosmo_memories.Balamb.model.accounts.LibraryUserDetails;
import cosmo_memories.Balamb.repository.accounts.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
public class MainControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private LibraryUserDetails userDetails;

    @BeforeEach
    public void setUp() {
        userRepository.save(new LibraryUser("Test", "User", "admin@example.com", "password"));
        Optional<LibraryUser> user = userRepository.findByEmail("admin@example.com");
        user.ifPresent(libraryUser -> userDetails = new LibraryUserDetails(libraryUser));
    }

    @Test
    public void loadMain_LoggedOut() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/home"))
                .andExpect(model().attribute("activePage", "home"));
    }

    @Test
    public void loadMain_LoggedIn() throws Exception {
        mockMvc.perform(get("/").with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/home"))
                .andExpect(model().attribute("activePage", "home"));
    }

    @Test
    public void loadAbout_LoggedOut() throws Exception {
        mockMvc.perform(get("/about"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/about"))
                .andExpect(model().attribute("activePage", "about"));
    }

    @Test
    public void loadAbout_LoggedIn() throws Exception {
        mockMvc.perform(get("/about").with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/about"))
                .andExpect(model().attribute("activePage", "about"));
    }

}
