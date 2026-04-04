package cosmo_memories.Balamb.controller;

import cosmo_memories.Balamb.model.accounts.LibraryUser;
import cosmo_memories.Balamb.model.accounts.LibraryUserDetails;
import cosmo_memories.Balamb.model.enums.Category;
import cosmo_memories.Balamb.model.enums.Genre;
import cosmo_memories.Balamb.model.enums.UpdateType;
import cosmo_memories.Balamb.model.items.Author;
import cosmo_memories.Balamb.model.items.Book;
import cosmo_memories.Balamb.model.site.Update;
import cosmo_memories.Balamb.repository.accounts.UserRepository;
import cosmo_memories.Balamb.service.books.BookService;
import cosmo_memories.Balamb.service.site.UpdateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.spring6.expression.Mvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Autowired
    private UpdateService updateService;

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
                        .param("subgenre", String.valueOf(Genre.SCIFI))
                        .param("category", String.valueOf(Category.FICTION))
                        .param("series", "Series Name")
                        .param("note", "This is a test book.")
                        .param("pubYear", "1999")
                        .param("publisher", "Cosmo Memories"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/browse/**"));

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
        List<Book> book = bookService.findNewestBooks(1);
        assertTrue(book.isEmpty());
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
        List<Book> book = bookService.findNewestBooks(1);
        assertTrue(book.isEmpty());
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
        List<Book> book = bookService.findNewestBooks(1);
        assertTrue(book.isEmpty());
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
        List<Book> book = bookService.findNewestBooks(1);
        assertTrue(book.isEmpty());
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
        List<Book> book = bookService.findNewestBooks(1);
        assertTrue(book.isEmpty());
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
        List<Book> book = bookService.findNewestBooks(1);
        assertTrue(book.isEmpty());
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
        List<Book> book = bookService.findNewestBooks(1);
        assertTrue(book.isEmpty());
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
        List<Book> book = bookService.findNewestBooks(1);
        assertTrue(book.isEmpty());
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
        List<Book> book = bookService.findNewestBooks(1);
        assertTrue(book.isEmpty());
    }

    @Test
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
        List<Book> book = bookService.findNewestBooks(1);
        assertTrue(book.isEmpty());
    }

    @Test
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
        List<Book> book = bookService.findNewestBooks(1);
        assertTrue(book.isEmpty());
    }

    @Test
    public void postUpdate_LoggedOut() throws Exception {
        mockMvc.perform(post("/admin/update")
                        .param("description", "This is a test update.")
                        .param("updateType", String.valueOf(UpdateType.UPDATE)))
                .andExpect(status().is4xxClientError());

        Page<Update> update = updateService.findAllUpdates(0, 1);
        assertTrue(update.isEmpty());
    }

    @Test
    public void postUpdate_LoggedIn() throws Exception {
        mockMvc.perform(post("/admin/update").with(user(userDetails))
                        .with(csrf())
                        .param("description", "This is a test update.")
                        .param("updateType", String.valueOf(UpdateType.UPDATE)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/updates"));

        Update update = updateService.findAllUpdates(0, 1).getContent().getFirst();
        assertThat(update.getDescription(), equalTo("This is a test update."));
        assertThat(update.getUpdateType(), equalTo(UpdateType.UPDATE));
        assertThat(update.getAuthor().getEmail(), equalTo(userDetails.getUsername()));
    }

    @Test
    public void postUpdate_InvalidDescription() throws Exception {
        MvcResult response = mockMvc.perform(post("/admin/update").with(user(userDetails))
                        .with(csrf())
                        .param("description", "")
                        .param("updateType", String.valueOf(UpdateType.UPDATE)))
                .andReturn();

        assertThat(response.getResponse().getContentAsString(), containsString("Something went wrong posting your update."));
        Page<Update> update = updateService.findAllUpdates(0, 1);
        assertTrue(update.isEmpty());
    }

    @Test
    public void postUpdate_InvalidType() throws Exception {
        MvcResult response = mockMvc.perform(post("/admin/update").with(user(userDetails))
                        .with(csrf())
                        .param("description", "This is a test update.")
                        .param("updateType", "lol"))
                .andReturn();

        assertThat(response.getResponse().getContentAsString(), containsString("Something went wrong posting your update."));
        Page<Update> update = updateService.findAllUpdates(0, 1);
        assertTrue(update.isEmpty());
    }

    @Test
    public void resolveUpdate_LoggedOut() throws Exception {
        mockMvc.perform(post("/admin/update").with(user(userDetails))
                        .with(csrf())
                        .param("description", "This is a test update.")
                        .param("updateType", String.valueOf(UpdateType.UPDATE)));

        Update update = updateService.findAllUpdates(0, 1).getContent().getFirst();
        assertFalse(update.getResolved());

        mockMvc.perform(post("/admin/update/resolve" + update.getId()))
                .andExpect(status().is4xxClientError());

        update = updateService.findAllUpdates(0, 1).getContent().getFirst();
        assertFalse(update.getResolved());
    }

    @Test
    public void resolveUpdate_LoggedIn() throws Exception {
        mockMvc.perform(post("/admin/update").with(user(userDetails))
                .with(csrf())
                .param("description", "This is a test update.")
                .param("updateType", String.valueOf(UpdateType.UPDATE)));

        Update update = updateService.findAllUpdates(0, 1).getContent().getFirst();
        assertFalse(update.getResolved());

        mockMvc.perform(post("/admin/update/resolve/" + update.getId()).with(user(userDetails)).with(csrf()));

        update = updateService.findAllUpdates(0, 1).getContent().getFirst();
        assertTrue(update.getResolved());
    }

    @Test
    public void resolveUpdate_InvalidID() throws Exception {
        mockMvc.perform(post("/admin/update").with(user(userDetails))
                .with(csrf())
                .param("description", "This is a test update.")
                .param("updateType", String.valueOf(UpdateType.UPDATE)));

        Update update = updateService.findAllUpdates(0, 1).getContent().getFirst();
        assertFalse(update.getResolved());

        mockMvc.perform(post("/admin/update/resolve/" + update.getId() + 1).with(user(userDetails)).with(csrf()))
                .andExpect(status().is4xxClientError());

        update = updateService.findAllUpdates(0, 1).getContent().getFirst();
        assertFalse(update.getResolved());
    }

    @Test
    public void deleteUpdate_LoggedOut() throws Exception {
        mockMvc.perform(post("/admin/update").with(user(userDetails))
                .with(csrf())
                .param("description", "This is a test update.")
                .param("updateType", String.valueOf(UpdateType.UPDATE)));

        Update update = updateService.findAllUpdates(0, 1).getContent().getFirst();
        assertThat(update.getDescription(), equalTo("This is a test update."));

        mockMvc.perform(delete("/admin/update/" + update.getId()))
                .andExpect(status().is4xxClientError());

        update = updateService.findAllUpdates(0, 1).getContent().getFirst();
        assertThat(update.getDescription(), equalTo("This is a test update."));
    }

    @Test
    public void deleteUpdate_LoggedIn() throws Exception {
        mockMvc.perform(post("/admin/update").with(user(userDetails))
                .with(csrf())
                .param("description", "This is a test update.")
                .param("updateType", String.valueOf(UpdateType.UPDATE)));

        Update update = updateService.findAllUpdates(0, 1).getContent().getFirst();
        assertThat(update.getDescription(), equalTo("This is a test update."));

        mockMvc.perform(delete("/admin/update/" + update.getId()).with(user(userDetails)).with(csrf()));

        Page<Update> updatePage = updateService.findAllUpdates(0, 1);
        assertTrue(updatePage.isEmpty());
    }

    @Test
    public void deleteUpdate_InvalidID() throws Exception {
        mockMvc.perform(post("/admin/update").with(user(userDetails))
                .with(csrf())
                .param("description", "This is a test update.")
                .param("updateType", String.valueOf(UpdateType.UPDATE)));

        Update update = updateService.findAllUpdates(0, 1).getContent().getFirst();
        assertThat(update.getDescription(), equalTo("This is a test update."));

        mockMvc.perform(delete("/admin/update/" + update.getId() + 1).with(user(userDetails)).with(csrf()))
                .andExpect(status().is4xxClientError());

        update = updateService.findAllUpdates(0, 1).getContent().getFirst();
        assertThat(update.getDescription(), equalTo("This is a test update."));
    }

    @Test
    public void deleteBook_LoggedOut() throws Exception {
        Book book = new Book();
        book.setAdded(LocalDateTime.now());
        book.setTitle("Test Book");
        book.addAuthor(new Author("Test", "Author"));
        book.setGenre(Genre.SCIFI);
        book.setCategory(Category.FICTION);
        bookService.saveBook(book);

        book = bookService.findNewestBooks(1).getFirst();

        mockMvc.perform(delete("/admin/book/" + book.getId()))
                .andExpect(status().is4xxClientError());

        book = bookService.findNewestBooks(1).getFirst();
        assertThat(book.getTitle(), equalTo("Test Book"));
    }

    @Test
    public void deleteBook_LoggedIn() throws Exception {
        Book book = new Book();
        book.setAdded(LocalDateTime.now());
        book.setTitle("Test Book");
        book.addAuthor(new Author("Test", "Author"));
        book.setGenre(Genre.SCIFI);
        book.setCategory(Category.FICTION);
        bookService.saveBook(book);

        book = bookService.findNewestBooks(1).getFirst();

        mockMvc.perform(delete("/admin/book/" + book.getId()).with(user(userDetails)).with(csrf()));

        List<Book> bookList = bookService.findNewestBooks(1);
        assertTrue(bookList.isEmpty());
    }

    @Test
    public void deleteBook_InvalidID() throws Exception {
        Book book = new Book();
        book.setAdded(LocalDateTime.now());
        book.setTitle("Test Book");
        book.addAuthor(new Author("Test", "Author"));
        book.setGenre(Genre.SCIFI);
        book.setCategory(Category.FICTION);
        bookService.saveBook(book);

        book = bookService.findNewestBooks(1).getFirst();

        mockMvc.perform(delete("/admin/book/").with(user(userDetails)).with(csrf()))
                .andExpect(status().is4xxClientError());

        book = bookService.findNewestBooks(1).getFirst();
        assertThat(book.getTitle(), equalTo("Test Book"));
    }

    @Test
    public void postEditBook_ValidData_LoggedOut() throws Exception {
        Book book = new Book();
        book.setAdded(LocalDateTime.now());
        book.setTitle("Test Book");
        book.addAuthor(new Author("Test", "Author"));
        book.setGenre(Genre.SCIFI);
        book.setCategory(Category.FICTION);
        bookService.saveBook(book);
        book = bookService.findNewestBooks(1).getFirst();

        mockMvc.perform(post("/admin/edit/" + book.getId())
                        .param("title", "Test Book B")
                        .param("authors", "Author, Test")
                        .param("authors", "Author 2, Test")
                        .param("genre", String.valueOf(Genre.FANTASY))
                        .param("subgenre", String.valueOf(Genre.SCIFI))
                        .param("category", String.valueOf(Category.GRAPHIC_NOVEL)))
                .andExpect(status().is4xxClientError());

        book = bookService.findNewestBooks(1).getFirst();
        assertThat(book.getTitle(), equalTo("Test Book"));
        assertThat(book.getAuthors().getFirst().getCommaSeparatedFullName(), equalTo("Author, Test"));
        assertThat(book.getAuthors().size(), equalTo(1));
        assertThat(book.getGenre(), equalTo(Genre.SCIFI));
        assertThat(book.getCategory(), equalTo(Category.FICTION));
    }

    @Test
    public void postEditBook_ValidData_LoggedIn() throws Exception {
        Book book = new Book();
        book.setAdded(LocalDateTime.now());
        book.setTitle("Test Book");
        book.addAuthor(new Author("Test", "Author"));
        book.setGenre(Genre.SCIFI);
        book.setCategory(Category.FICTION);
        bookService.saveBook(book);
        book = bookService.findNewestBooks(1).getFirst();

        mockMvc.perform(post("/admin/edit/" + book.getId()).with(user(userDetails))
                        .with(csrf())
                        .param("title", "Test Book A")
                        .param("authors", "Author, Test")
                        .param("authors", "Author 2, Test")
                        .param("genre", String.valueOf(Genre.FANTASY))
                        .param("subgenre", String.valueOf(Genre.SCIFI))
                        .param("category", String.valueOf(Category.FICTION))
                        .param("series", "Series Name")
                        .param("note", "This is a test book.")
                        .param("pubYear", "1999")
                        .param("publisher", "Cosmo Memories"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/browse/" + book.getId()));

        book = bookService.findNewestBooks(1).getFirst();
        assertThat(book.getTitle(), equalTo("Test Book A"));
        assertThat(book.getAuthors().getFirst().getCommaSeparatedFullName(), equalTo("Author, Test"));
        assertThat(book.getAuthors().getLast().getCommaSeparatedFullName(), equalTo("Author 2, Test"));
        assertThat(book.getAuthors().size(), equalTo(2));
        assertThat(book.getGenre(), equalTo(Genre.FANTASY));
        assertThat(book.getSubgenre(), equalTo(Genre.SCIFI));
        assertThat(book.getCategory(), equalTo(Category.FICTION));
    }

    @Test
    public void postEditBook_InvalidTitle() throws Exception {
        Book book = new Book();
        book.setAdded(LocalDateTime.now());
        book.setTitle("Test Book");
        book.addAuthor(new Author("Test", "Author"));
        book.setGenre(Genre.SCIFI);
        book.setCategory(Category.FICTION);
        bookService.saveBook(book);
        book = bookService.findNewestBooks(1).getFirst();

        MvcResult response = mockMvc.perform(post("/admin/edit/" + book.getId()).with(user(userDetails))
                        .with(csrf())
                        .param("title", "")
                        .param("authors", "Author, Test")
                        .param("authors", "Author 2, Test")
                        .param("genre", String.valueOf(Genre.FANTASY))
                        .param("subgenre", String.valueOf(Genre.SCIFI))
                        .param("category", String.valueOf(Category.FICTION))
                        .param("series", "Series Name")
                        .param("note", "This is a test book.")
                        .param("pubYear", "1999")
                        .param("publisher", "Cosmo Memories"))
                .andReturn();

        assertThat(response.getResponse().getContentAsString(), containsString("Title cannot be blank."));

        book = bookService.findNewestBooks(1).getFirst();
        assertThat(book.getTitle(), equalTo("Test Book"));
        assertThat(book.getAuthors().getFirst().getCommaSeparatedFullName(), equalTo("Author, Test"));
        assertThat(book.getAuthors().size(), equalTo(1));
        assertThat(book.getGenre(), equalTo(Genre.SCIFI));
        assertThat(book.getSubgenre(), equalTo(null));
        assertThat(book.getCategory(), equalTo(Category.FICTION));
    }

    @Test
    public void postEditBook_InvalidAuthor() throws Exception {
        Book book = new Book();
        book.setAdded(LocalDateTime.now());
        book.setTitle("Test Book");
        book.addAuthor(new Author("Test", "Author"));
        book.setGenre(Genre.SCIFI);
        book.setCategory(Category.FICTION);
        bookService.saveBook(book);
        book = bookService.findNewestBooks(1).getFirst();

        MvcResult response = mockMvc.perform(post("/admin/edit/" + book.getId()).with(user(userDetails))
                        .with(csrf())
                        .param("title", "Test Book")
                        .param("authors", "Author Test")
                        .param("genre", String.valueOf(Genre.SCIFI))
                        .param("category", String.valueOf(Category.FICTION))
                        .param("series", "Series Name")
                        .param("note", "This is a test book.")
                        .param("pubYear", "1999")
                        .param("publisher", "Cosmo Memories"))
                .andReturn();

        assertThat(response.getResponse().getContentAsString(), containsString("Names must be in the form Lastname, Firstname."));

        book = bookService.findNewestBooks(1).getFirst();
        assertThat(book.getTitle(), equalTo("Test Book"));
        assertThat(book.getAuthors().getFirst().getCommaSeparatedFullName(), equalTo("Author, Test"));
        assertThat(book.getAuthors().size(), equalTo(1));
        assertThat(book.getGenre(), equalTo(Genre.SCIFI));
        assertThat(book.getSubgenre(), equalTo(null));
        assertThat(book.getCategory(), equalTo(Category.FICTION));
    }

    @Test
    public void postEditBook_InvalidPublisher() throws Exception {
        Book book = new Book();
        book.setAdded(LocalDateTime.now());
        book.setTitle("Test Book");
        book.addAuthor(new Author("Test", "Author"));
        book.setGenre(Genre.SCIFI);
        book.setCategory(Category.FICTION);
        bookService.saveBook(book);
        book = bookService.findNewestBooks(1).getFirst();

        MvcResult response = mockMvc.perform(post("/admin/edit/" + book.getId()).with(user(userDetails))
                        .with(csrf())
                        .param("title", "")
                        .param("authors", "Author, Test")
                        .param("authors", "Author 2, Test")
                        .param("genre", String.valueOf(Genre.FANTASY))
                        .param("subgenre", String.valueOf(Genre.SCIFI))
                        .param("category", String.valueOf(Category.FICTION))
                        .param("series", "Series Name")
                        .param("note", "This is a test book.")
                        .param("pubYear", "1999")
                        .param("publisher", "a".repeat(600)))
                .andReturn();

        assertThat(response.getResponse().getContentAsString(), containsString("Publisher cannot be more than 30 characters."));

        book = bookService.findNewestBooks(1).getFirst();
        assertThat(book.getTitle(), equalTo("Test Book"));
        assertThat(book.getAuthors().getFirst().getCommaSeparatedFullName(), equalTo("Author, Test"));
        assertThat(book.getAuthors().size(), equalTo(1));
        assertThat(book.getGenre(), equalTo(Genre.SCIFI));
        assertThat(book.getSubgenre(), equalTo(null));
        assertThat(book.getCategory(), equalTo(Category.FICTION));
    }

    @Test
    public void postEditBook_InvalidISBN() throws Exception {
        Book book = new Book();
        book.setAdded(LocalDateTime.now());
        book.setTitle("Test Book");
        book.addAuthor(new Author("Test", "Author"));
        book.setGenre(Genre.SCIFI);
        book.setCategory(Category.FICTION);
        bookService.saveBook(book);
        book = bookService.findNewestBooks(1).getFirst();

        MvcResult response = mockMvc.perform(post("/admin/edit/" + book.getId()).with(user(userDetails))
                        .with(csrf())
                        .param("title", "")
                        .param("authors", "Author, Test")
                        .param("authors", "Author 2, Test")
                        .param("genre", String.valueOf(Genre.FANTASY))
                        .param("subgenre", String.valueOf(Genre.SCIFI))
                        .param("category", String.valueOf(Category.FICTION))
                        .param("series", "Series Name")
                        .param("note", "This is a test book.")
                        .param("isbn", "3")
                        .param("pubYear", "1999")
                        .param("publisher", "Cosmo Memories"))
                .andReturn();

        assertThat(response.getResponse().getContentAsString(), containsString("ISBNs should be either 10 or 13 characters (excluding hyphens)."));

        book = bookService.findNewestBooks(1).getFirst();
        assertThat(book.getTitle(), equalTo("Test Book"));
        assertThat(book.getAuthors().getFirst().getCommaSeparatedFullName(), equalTo("Author, Test"));
        assertThat(book.getAuthors().size(), equalTo(1));
        assertThat(book.getGenre(), equalTo(Genre.SCIFI));
        assertThat(book.getSubgenre(), equalTo(null));
        assertThat(book.getCategory(), equalTo(Category.FICTION));
    }

    @Test
    public void postEditBook_InvalidSeries() throws Exception {
        Book book = new Book();
        book.setAdded(LocalDateTime.now());
        book.setTitle("Test Book");
        book.addAuthor(new Author("Test", "Author"));
        book.setGenre(Genre.SCIFI);
        book.setCategory(Category.FICTION);
        bookService.saveBook(book);
        book = bookService.findNewestBooks(1).getFirst();

        MvcResult response = mockMvc.perform(post("/admin/edit/" + book.getId()).with(user(userDetails))
                        .with(csrf())
                        .param("title", "")
                        .param("authors", "Author, Test")
                        .param("authors", "Author 2, Test")
                        .param("genre", String.valueOf(Genre.FANTASY))
                        .param("subgenre", String.valueOf(Genre.SCIFI))
                        .param("category", String.valueOf(Category.FICTION))
                        .param("series", "a".repeat(600))
                        .param("note", "This is a test book.")
                        .param("pubYear", "1999")
                        .param("publisher", "Cosmo Memories"))
                .andReturn();

        assertThat(response.getResponse().getContentAsString(), containsString("Series cannot be more than 60 characters."));

        book = bookService.findNewestBooks(1).getFirst();
        assertThat(book.getTitle(), equalTo("Test Book"));
        assertThat(book.getAuthors().getFirst().getCommaSeparatedFullName(), equalTo("Author, Test"));
        assertThat(book.getAuthors().size(), equalTo(1));
        assertThat(book.getGenre(), equalTo(Genre.SCIFI));
        assertThat(book.getSubgenre(), equalTo(null));
        assertThat(book.getCategory(), equalTo(Category.FICTION));
    }

    @Test
    public void postEditBook_InvalidNote() throws Exception {
        Book book = new Book();
        book.setAdded(LocalDateTime.now());
        book.setTitle("Test Book");
        book.addAuthor(new Author("Test", "Author"));
        book.setGenre(Genre.SCIFI);
        book.setCategory(Category.FICTION);
        bookService.saveBook(book);
        book = bookService.findNewestBooks(1).getFirst();

        MvcResult response = mockMvc.perform(post("/admin/edit/" + book.getId()).with(user(userDetails))
                        .with(csrf())
                        .param("title", "")
                        .param("authors", "Author, Test")
                        .param("authors", "Author 2, Test")
                        .param("genre", String.valueOf(Genre.FANTASY))
                        .param("subgenre", String.valueOf(Genre.SCIFI))
                        .param("category", String.valueOf(Category.FICTION))
                        .param("series", "Series Name")
                        .param("note", "a".repeat(600))
                        .param("pubYear", "1999")
                        .param("publisher", "Cosmo Memories"))
                .andReturn();

        assertThat(response.getResponse().getContentAsString(), containsString("Note cannot be more than 500 characters."));

        book = bookService.findNewestBooks(1).getFirst();
        assertThat(book.getTitle(), equalTo("Test Book"));
        assertThat(book.getAuthors().getFirst().getCommaSeparatedFullName(), equalTo("Author, Test"));
        assertThat(book.getAuthors().size(), equalTo(1));
        assertThat(book.getGenre(), equalTo(Genre.SCIFI));
        assertThat(book.getSubgenre(), equalTo(null));
        assertThat(book.getCategory(), equalTo(Category.FICTION));
    }

    @Test
    public void postEditBook_InvalidCategory() throws Exception {
        Book book = new Book();
        book.setAdded(LocalDateTime.now());
        book.setTitle("Test Book");
        book.addAuthor(new Author("Test", "Author"));
        book.setGenre(Genre.SCIFI);
        book.setCategory(Category.FICTION);
        bookService.saveBook(book);
        book = bookService.findNewestBooks(1).getFirst();

        MvcResult response = mockMvc.perform(post("/admin/edit/" + book.getId()).with(user(userDetails))
                        .with(csrf())
                        .param("title", "")
                        .param("authors", "Author, Test")
                        .param("authors", "Author 2, Test")
                        .param("genre", String.valueOf(Genre.FANTASY))
                        .param("subgenre", String.valueOf(Genre.SCIFI))
                        .param("category", "owo")
                        .param("series", "Series Name")
                        .param("note", "This is a test book.")
                        .param("pubYear", "1999")
                        .param("publisher", "Cosmo Memories"))
                .andReturn();

        assertThat(response.getResponse().getContentAsString(), containsString("Failed to convert"));

        book = bookService.findNewestBooks(1).getFirst();
        assertThat(book.getTitle(), equalTo("Test Book"));
        assertThat(book.getAuthors().getFirst().getCommaSeparatedFullName(), equalTo("Author, Test"));
        assertThat(book.getAuthors().size(), equalTo(1));
        assertThat(book.getGenre(), equalTo(Genre.SCIFI));
        assertThat(book.getSubgenre(), equalTo(null));
        assertThat(book.getCategory(), equalTo(Category.FICTION));
    }

    @Test
    public void postEditBook_InvalidYear_Negative() throws Exception {
        Book book = new Book();
        book.setAdded(LocalDateTime.now());
        book.setTitle("Test Book");
        book.addAuthor(new Author("Test", "Author"));
        book.setGenre(Genre.SCIFI);
        book.setCategory(Category.FICTION);
        bookService.saveBook(book);
        book = bookService.findNewestBooks(1).getFirst();

        MvcResult response = mockMvc.perform(post("/admin/edit/" + book.getId()).with(user(userDetails))
                        .with(csrf())
                        .param("title", "")
                        .param("authors", "Author, Test")
                        .param("authors", "Author 2, Test")
                        .param("genre", String.valueOf(Genre.FANTASY))
                        .param("subgenre", String.valueOf(Genre.SCIFI))
                        .param("category", String.valueOf(Category.FICTION))
                        .param("series", "Series Name")
                        .param("note", "This is a test book.")
                        .param("pubYear", "-1")
                        .param("publisher", "Cosmo Memories"))
                .andReturn();

        assertThat(response.getResponse().getContentAsString(), containsString("Year must be between 0000 and today."));

        book = bookService.findNewestBooks(1).getFirst();
        assertThat(book.getTitle(), equalTo("Test Book"));
        assertThat(book.getAuthors().getFirst().getCommaSeparatedFullName(), equalTo("Author, Test"));
        assertThat(book.getAuthors().size(), equalTo(1));
        assertThat(book.getGenre(), equalTo(Genre.SCIFI));
        assertThat(book.getSubgenre(), equalTo(null));
        assertThat(book.getCategory(), equalTo(Category.FICTION));
    }

    @Test
    public void postEditBook_InvalidYear_Future() throws Exception {
        Book book = new Book();
        book.setAdded(LocalDateTime.now());
        book.setTitle("Test Book");
        book.addAuthor(new Author("Test", "Author"));
        book.setGenre(Genre.SCIFI);
        book.setCategory(Category.FICTION);
        bookService.saveBook(book);
        book = bookService.findNewestBooks(1).getFirst();

        MvcResult response = mockMvc.perform(post("/admin/edit/" + book.getId()).with(user(userDetails))
                        .with(csrf())
                        .param("title", "")
                        .param("authors", "Author, Test")
                        .param("authors", "Author 2, Test")
                        .param("genre", String.valueOf(Genre.FANTASY))
                        .param("subgenre", String.valueOf(Genre.SCIFI))
                        .param("category", String.valueOf(Category.FICTION))
                        .param("series", "Series Name")
                        .param("note", "This is a test book.")
                        .param("pubYear", "2999")
                        .param("publisher", "Cosmo Memories"))
                .andReturn();

        assertThat(response.getResponse().getContentAsString(), containsString("Year must be between 0000 and today."));

        book = bookService.findNewestBooks(1).getFirst();
        assertThat(book.getTitle(), equalTo("Test Book"));
        assertThat(book.getAuthors().getFirst().getCommaSeparatedFullName(), equalTo("Author, Test"));
        assertThat(book.getAuthors().size(), equalTo(1));
        assertThat(book.getGenre(), equalTo(Genre.SCIFI));
        assertThat(book.getSubgenre(), equalTo(null));
        assertThat(book.getCategory(), equalTo(Category.FICTION));
    }

    @Test
    public void postEditBook_InvalidGenre() throws Exception {
        Book book = new Book();
        book.setAdded(LocalDateTime.now());
        book.setTitle("Test Book");
        book.addAuthor(new Author("Test", "Author"));
        book.setGenre(Genre.SCIFI);
        book.setCategory(Category.FICTION);
        bookService.saveBook(book);
        book = bookService.findNewestBooks(1).getFirst();

        MvcResult response = mockMvc.perform(post("/admin/edit/" + book.getId()).with(user(userDetails))
                        .with(csrf())
                        .param("title", "")
                        .param("authors", "Author, Test")
                        .param("authors", "Author 2, Test")
                        .param("genre", "owo")
                        .param("subgenre", String.valueOf(Genre.SCIFI))
                        .param("category", String.valueOf(Category.FICTION))
                        .param("series", "Series Name")
                        .param("note", "This is a test book.")
                        .param("pubYear", "1999")
                        .param("publisher", "Cosmo Memories"))
                .andReturn();

        assertThat(response.getResponse().getContentAsString(), containsString("Failed to convert"));

        book = bookService.findNewestBooks(1).getFirst();
        assertThat(book.getTitle(), equalTo("Test Book"));
        assertThat(book.getAuthors().getFirst().getCommaSeparatedFullName(), equalTo("Author, Test"));
        assertThat(book.getAuthors().size(), equalTo(1));
        assertThat(book.getGenre(), equalTo(Genre.SCIFI));
        assertThat(book.getSubgenre(), equalTo(null));
        assertThat(book.getCategory(), equalTo(Category.FICTION));
    }

    @Test
    public void postEditBook_InvalidSubgenre() throws Exception {
        Book book = new Book();
        book.setAdded(LocalDateTime.now());
        book.setTitle("Test Book");
        book.addAuthor(new Author("Test", "Author"));
        book.setGenre(Genre.SCIFI);
        book.setCategory(Category.FICTION);
        bookService.saveBook(book);
        book = bookService.findNewestBooks(1).getFirst();

        MvcResult response = mockMvc.perform(post("/admin/edit/" + book.getId()).with(user(userDetails))
                        .with(csrf())
                        .param("title", "")
                        .param("authors", "Author, Test")
                        .param("authors", "Author 2, Test")
                        .param("genre", String.valueOf(Genre.FANTASY))
                        .param("subgenre", "owo")
                        .param("category", String.valueOf(Category.FICTION))
                        .param("series", "Series Name")
                        .param("note", "This is a test book.")
                        .param("pubYear", "1999")
                        .param("publisher", "Cosmo Memories"))
                .andReturn();

        assertThat(response.getResponse().getContentAsString(), containsString("Failed to convert"));

        book = bookService.findNewestBooks(1).getFirst();
        assertThat(book.getTitle(), equalTo("Test Book"));
        assertThat(book.getAuthors().getFirst().getCommaSeparatedFullName(), equalTo("Author, Test"));
        assertThat(book.getAuthors().size(), equalTo(1));
        assertThat(book.getGenre(), equalTo(Genre.SCIFI));
        assertThat(book.getSubgenre(), equalTo(null));
        assertThat(book.getCategory(), equalTo(Category.FICTION));
    }

    @Test
    public void readBook_LoggedOut() throws Exception {
        Book book = new Book();
        book.setAdded(LocalDateTime.now());
        book.setTitle("Test Book");
        book.addAuthor(new Author("Test", "Author"));
        book.setGenre(Genre.SCIFI);
        book.setCategory(Category.FICTION);
        bookService.saveBook(book);
        book = bookService.findNewestBooks(1).getFirst();
        assertFalse(book.getComplete());

        mockMvc.perform(post("/admin/update/" + book.getId() + "/complete"))
                .andExpect(status().is4xxClientError());

        book = bookService.findNewestBooks(1).getFirst();
        assertFalse(book.getComplete());
    }

    @Test
    public void readBook_LoggedIn() throws Exception {
        Book book = new Book();
        book.setAdded(LocalDateTime.now());
        book.setTitle("Test Book");
        book.addAuthor(new Author("Test", "Author"));
        book.setGenre(Genre.SCIFI);
        book.setCategory(Category.FICTION);
        bookService.saveBook(book);
        book = bookService.findNewestBooks(1).getFirst();
        assertFalse(book.getComplete());

        mockMvc.perform(post("/admin/update/" + book.getId() + "/complete").with(user(userDetails))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/browse/" + book.getId()));

        book = bookService.findNewestBooks(1).getFirst();
        assertTrue(book.getComplete());
    }

    @Test
    public void readBook_InvalidID() throws Exception {
        Book book = new Book();
        book.setAdded(LocalDateTime.now());
        book.setTitle("Test Book");
        book.addAuthor(new Author("Test", "Author"));
        book.setGenre(Genre.SCIFI);
        book.setCategory(Category.FICTION);
        bookService.saveBook(book);
        book = bookService.findNewestBooks(1).getFirst();
        assertFalse(book.getComplete());

        mockMvc.perform(post("/admin/update/" + book.getId() + 1 + "/complete").with(user(userDetails)).with(csrf()));

        book = bookService.findNewestBooks(1).getFirst();
        assertFalse(book.getComplete());
    }

    // TODO: image upload tests (after refactoring controller)
}
