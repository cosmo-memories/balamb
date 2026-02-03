package cosmo_memories.Balamb.controller;

import cosmo_memories.Balamb.model.accounts.LibraryUser;
import cosmo_memories.Balamb.model.accounts.LibraryUserDetails;
import cosmo_memories.Balamb.model.enums.Category;
import cosmo_memories.Balamb.model.enums.Genre;
import cosmo_memories.Balamb.model.items.Book;
import cosmo_memories.Balamb.repository.accounts.UserRepository;
import cosmo_memories.Balamb.service.books.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
public class AdminControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookService bookService;

    private LibraryUserDetails userDetails;

    @BeforeEach
    public void setUp() {
        userRepository.save(new LibraryUser("Test", "User", "admin@example.com", "$2a$10$HqEj3CU.FwUWtNtjI31h4OO7YzcIQDBbk5G96r1VuhihOInaAetD."));
        Optional<LibraryUser> user = userRepository.findByEmail("admin@example.com");
        if (user.isPresent()) {
            user.get().grantAuthority("ROLE_ADMIN");
            userRepository.save(user.get());
            userDetails = new LibraryUserDetails(user.get());
        }
    }

    @Test
    public void loadAdminPanel_LoggedOut() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    public void loadAdminPanel_LoggedIn() throws Exception {
        mockMvc.perform(get("/admin").with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/admin/admin"))
                .andExpect(model().attribute("activePage", "admin"));
    }

    @Test
    public void loadLoginPage_LoggedOut() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/login"))
                .andExpect(model().attribute("activePage", "login"));
    }

    @Test
    public void loadLoginPage_LoggedIn() throws Exception {
        mockMvc.perform(get("/login").with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/login"))
                .andExpect(model().attribute("activePage", "login"));
    }

    @Test
    public void logIn_validDetails() throws Exception {
        mockMvc.perform(post("/login")
                        .param("email", "admin@example.com")
                        .param("password", "Abc123!!")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
    }

    @Test
    public void logIn_invalidDetails() throws Exception {
        mockMvc.perform(post("/login")
                        .param("email", "admin@example.com")
                        .param("password", "Abc123!")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    public void loadAddBookPage_LoggedOut() throws Exception {
        mockMvc.perform(get("/admin/add/book"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    public void loadAddBookPage_LoggedIn() throws Exception {
        mockMvc.perform(get("/admin/add/book").with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/admin/add"));
    }

    @Test
    public void postNewBook_ValidData_LoggedOut() throws Exception {
        mockMvc.perform(post("/admin/add/book")
                        .param("title", "Test Book A")
                        .param("authors", "Author, Test")
                        .param("authors", "Author 2, Test")
                        .param("genre", String.valueOf(Genre.FANTASY))
                        .param("category", String.valueOf(Category.FICTION)))
                .andExpect(status().is4xxClientError());

        List<Book> book = bookService.findNewestBooks(1);
        assertTrue(book.isEmpty());
    }

    @Test
    public void postNewBook_ValidData_LoggedIn() throws Exception {
        mockMvc.perform(post("/admin/add/book").with(user(userDetails))
                        .with(csrf())
                        .param("title", "Test Book A")
                        .param("authors", "Author, Test")
                        .param("authors", "Author 2, Test")
                        .param("genre", String.valueOf(Genre.FANTASY))
                        .param("category", String.valueOf(Category.FICTION)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/add/book"));

        Book book = bookService.findNewestBooks(1).getFirst();
        assertThat(book.getTitle(), equalTo("Test Book A"));
        assertThat(book.getAuthors().getFirst().getCommaSeparatedFullName(), equalTo("Author, Test"));
        assertThat(book.getAuthors().getLast().getCommaSeparatedFullName(), equalTo("Author 2, Test"));
        assertThat(book.getAuthors().size(), equalTo(2));
        assertThat(book.getGenre(), equalTo(Genre.FANTASY));
        assertThat(book.getCategory(), equalTo(Category.FICTION));
    }

    @Test
    public void postNewBook_InvalidTitle() throws Exception {
        MvcResult response = mockMvc.perform(post("/admin/add/book").with(user(userDetails))
                        .with(csrf())
                        .param("title", "")
                        .param("authors", "Author, Test")
                        .param("authors", "Author 2, Test")
                        .param("genre", String.valueOf(Genre.FANTASY))
                        .param("category", String.valueOf(Category.FICTION)))
//                .andExpect(status().is4xxClientError())
                .andReturn();

        assertThat(response.getResponse().getContentAsString(), containsString("Title cannot be blank."));
    }

    @Test
    public void postNewBook_InvalidAuthor() throws Exception {
        MvcResult response = mockMvc.perform(post("/admin/add/book").with(user(userDetails))
                        .with(csrf())
                        .param("title", "Test Title")
                        .param("authors", "Invalid Author")
                        .param("genre", String.valueOf(Genre.FANTASY))
                        .param("category", String.valueOf(Category.FICTION)))
                .andReturn();

        assertThat(response.getResponse().getContentAsString(), containsString("Names must be in the form Lastname, Firstname."));
    }

    @Test
    public void postNewBook_InvalidGenre() throws Exception {
        MvcResult response = mockMvc.perform(post("/admin/add/book").with(user(userDetails))
                        .with(csrf())
                        .param("title", "Test Title")
                        .param("authors", "Author, Test")
                        .param("authors", "Author 2, Test")
                        .param("genre", "lol")
                        .param("category", String.valueOf(Category.FICTION)))
                .andReturn();

        assertThat(response.getResponse().getContentAsString(), containsString("Failed to convert"));
    }

    @Test
    public void postNewBook_InvalidSubgenre() throws Exception {
        MvcResult response = mockMvc.perform(post("/admin/add/book").with(user(userDetails))
                        .with(csrf())
                        .param("title", "Test Title")
                        .param("authors", "Author, Test")
                        .param("authors", "Author 2, Test")
                        .param("genre", String.valueOf(Genre.FANTASY))
                        .param("subgenre", "lol")
                        .param("category", String.valueOf(Category.FICTION)))
                .andReturn();

        assertThat(response.getResponse().getContentAsString(), containsString("Failed to convert"));
    }

    @Test
    public void postNewBook_InvalidCategory() throws Exception {
        MvcResult response = mockMvc.perform(post("/admin/add/book").with(user(userDetails))
                        .with(csrf())
                        .param("title", "Test Title")
                        .param("authors", "Author, Test")
                        .param("authors", "Author 2, Test")
                        .param("genre", String.valueOf(Genre.FANTASY))
                        .param("category", "lol"))
                .andReturn();

        assertThat(response.getResponse().getContentAsString(), containsString("Failed to convert"));
    }

    @Test
    public void postNewBook_InvalidPublisher() throws Exception {
        MvcResult response = mockMvc.perform(post("/admin/add/book").with(user(userDetails))
                        .with(csrf())
                        .param("title", "Test Title")
                        .param("authors", "Author, Test")
                        .param("authors", "Author 2, Test")
                        .param("genre", String.valueOf(Genre.FANTASY))
                        .param("category", String.valueOf(Category.FICTION))
                        .param("publisher", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"))
                .andReturn();

        assertThat(response.getResponse().getContentAsString(), containsString("Publisher cannot be more than 30 characters."));
    }

    @Test
    public void postNewBook_InvalidDate_Negative() throws Exception {
        MvcResult response = mockMvc.perform(post("/admin/add/book").with(user(userDetails))
                        .with(csrf())
                        .param("title", "Test Title")
                        .param("authors", "Author, Test")
                        .param("authors", "Author 2, Test")
                        .param("genre", String.valueOf(Genre.FANTASY))
                        .param("category", String.valueOf(Category.FICTION))
                        .param("pubYear", "-1"))
                .andReturn();

        assertThat(response.getResponse().getContentAsString(), containsString("Year must be between 0000 and today."));
    }

    @Test
    public void postNewBook_InvalidDate_Future() throws Exception {
        MvcResult response = mockMvc.perform(post("/admin/add/book").with(user(userDetails))
                        .with(csrf())
                        .param("title", "Test Title")
                        .param("authors", "Author, Test")
                        .param("authors", "Author 2, Test")
                        .param("genre", String.valueOf(Genre.FANTASY))
                        .param("category", String.valueOf(Category.FICTION))
                        .param("pubYear", "2050"))
                .andReturn();

        assertThat(response.getResponse().getContentAsString(), containsString("Year must be between 0000 and today."));
    }

    @Test
    public void postNewBook_InvalidISBN() throws Exception {
        MvcResult response = mockMvc.perform(post("/admin/add/book").with(user(userDetails))
                        .with(csrf())
                        .param("title", "Test Title")
                        .param("authors", "Author, Test")
                        .param("authors", "Author 2, Test")
                        .param("genre", String.valueOf(Genre.FANTASY))
                        .param("category", String.valueOf(Category.FICTION))
                        .param("isbn", "12345"))
                .andReturn();

        assertThat(response.getResponse().getContentAsString(), containsString("ISBNs should be either 10 or 13 characters (excluding hyphens)."));
    }

    public void postNewBook_InvalidNote() throws Exception {
        MvcResult response = mockMvc.perform(post("/admin/add/book").with(user(userDetails))
                        .with(csrf())
                        .param("title", "Test Title")
                        .param("authors", "Author, Test")
                        .param("authors", "Author 2, Test")
                        .param("genre", String.valueOf(Genre.FANTASY))
                        .param("category", String.valueOf(Category.FICTION))
                        .param("note", "a".repeat(600)))
                .andReturn();

        assertThat(response.getResponse().getContentAsString(), containsString("Note cannot be more than 500 characters."));
    }

    public void postNewBook_InvalidSeries() throws Exception {
        MvcResult response = mockMvc.perform(post("/admin/add/book").with(user(userDetails))
                        .with(csrf())
                        .param("title", "Test Title")
                        .param("authors", "Author, Test")
                        .param("authors", "Author 2, Test")
                        .param("genre", String.valueOf(Genre.FANTASY))
                        .param("category", String.valueOf(Category.FICTION))
                        .param("Series", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"))
                .andReturn();

        assertThat(response.getResponse().getContentAsString(), containsString("Series cannot be more than 60 characters"));
    }


    // Invalid:
    // Assert that books were not created

    // Post Update

    // Delete Update

    // Delete Book

    // Edit Books

}
