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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
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

    @Autowired
    private BookService bookService;

    @Autowired
    private UpdateService updateService;

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

    @Test
    public void loadBrowse_NoSearchParams_NoBooks_LoggedOut() throws Exception {
        MvcResult result = mockMvc.perform(get("/browse"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/browse"))
                .andExpect(model().attribute("activePage", "browse"))
                .andExpect(model().attribute("bookList", hasProperty("content", hasSize(0))))
                .andReturn();

//        Page<Book> bookList = (Page<Book>) result.getModelAndView().getModelMap().get("bookList");
//        assertThat(bookList.getContent(), hasSize(0));
    }

    @Test
    public void loadBrowse_NoSearchParams_OneBook_LoggedOut() throws Exception {
        Book book = new Book();
        book.setAdded(LocalDateTime.now());
        book.setTitle("Test Book");
        book.addAuthor(new Author("Test", "Author"));
        book.setGenre(Genre.FANTASY);
        book.setCategory(Category.FICTION);
        bookService.saveBook(book);

        mockMvc.perform(get("/browse"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/browse"))
                .andExpect(model().attribute("activePage", "browse"))
                .andExpect(model().attribute("bookList", hasProperty("content", hasSize(1))))
                .andExpect(model().attribute("bookList", hasProperty("content", contains(book))));
    }

    @Test
    public void loadBrowse_NoSearchParams_NoBooks_LoggedIn() throws Exception {
        mockMvc.perform(get("/browse").with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/browse"))
                .andExpect(model().attribute("activePage", "browse"))
                .andExpect(model().attribute("bookList", hasProperty("content", hasSize(0))))
                .andReturn();
    }

    @Test
    public void loadBrowse_NoSearchParams_OneBook_LoggedIn() throws Exception {
        Book book = new Book();
        book.setAdded(LocalDateTime.now());
        book.setTitle("Test Book");
        book.addAuthor(new Author("Test", "Author"));
        book.setGenre(Genre.FANTASY);
        book.setCategory(Category.FICTION);
        bookService.saveBook(book);

        mockMvc.perform(get("/browse").with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/browse"))
                .andExpect(model().attribute("activePage", "browse"))
                .andExpect(model().attribute("bookList", hasProperty("content", hasSize(1))))
                .andExpect(model().attribute("bookList", hasProperty("content", contains(book))));
    }

    @Test
    public void loadBrowse_SearchGenre_LoggedOut() throws Exception {
        Book book = new Book();
        book.setAdded(LocalDateTime.now());
        book.setTitle("Test Book");
        book.addAuthor(new Author("Test", "Author"));
        book.setGenre(Genre.SCIFI);
        book.setCategory(Category.FICTION);
        bookService.saveBook(book);

        Book book2 = new Book();
        book2.setAdded(LocalDateTime.now());
        book2.setTitle("Test Book 2");
        book2.addAuthor(new Author("Another", "Author"));
        book2.setGenre(Genre.FANTASY);
        book2.setCategory(Category.FICTION);
        bookService.saveBook(book2);

        mockMvc.perform(get("/browse")
                        .param("genre", String.valueOf(Genre.FANTASY)))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/browse"))
                .andExpect(model().attribute("activePage", "browse"))
                .andExpect(model().attribute("bookList", hasProperty("content", hasSize(1))))
                .andExpect(model().attribute("bookList", hasProperty("content", contains(book2))))
                .andExpect(model().attribute("bookList", hasProperty("content", contains(not(book)))));
    }

    @Test
    public void loadBrowse_SearchGenre_LoggedIn() throws Exception {
        Book book = new Book();
        book.setAdded(LocalDateTime.now());
        book.setTitle("Test Book");
        book.addAuthor(new Author("Test", "Author"));
        book.setGenre(Genre.SCIFI);
        book.setCategory(Category.FICTION);
        bookService.saveBook(book);

        Book book2 = new Book();
        book2.setAdded(LocalDateTime.now());
        book2.setTitle("Test Book 2");
        book2.addAuthor(new Author("Another", "Author"));
        book2.setGenre(Genre.FANTASY);
        book2.setCategory(Category.FICTION);
        bookService.saveBook(book2);

        mockMvc.perform(get("/browse").with(user(userDetails))
                        .param("genre", String.valueOf(Genre.FANTASY)))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/browse"))
                .andExpect(model().attribute("activePage", "browse"))
                .andExpect(model().attribute("bookList", hasProperty("content", hasSize(1))))
                .andExpect(model().attribute("bookList", hasProperty("content", contains(book2))))
                .andExpect(model().attribute("bookList", hasProperty("content", contains(not(book)))));
    }

    @Test
    public void loadBrowse_SearchCategory_LoggedOut() throws Exception {
        Book book = new Book();
        book.setAdded(LocalDateTime.now());
        book.setTitle("Test Book");
        book.addAuthor(new Author("Test", "Author"));
        book.setGenre(Genre.SCIFI);
        book.setCategory(Category.NONFICTION);
        bookService.saveBook(book);

        Book book2 = new Book();
        book2.setAdded(LocalDateTime.now());
        book2.setTitle("Test Book 2");
        book2.addAuthor(new Author("Another", "Author"));
        book2.setGenre(Genre.FANTASY);
        book2.setCategory(Category.FICTION);
        bookService.saveBook(book2);

        mockMvc.perform(get("/browse")
                        .param("category", String.valueOf(Category.FICTION)))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/browse"))
                .andExpect(model().attribute("activePage", "browse"))
                .andExpect(model().attribute("bookList", hasProperty("content", hasSize(1))))
                .andExpect(model().attribute("bookList", hasProperty("content", contains(book2))))
                .andExpect(model().attribute("bookList", hasProperty("content", contains(not(book)))));
    }

    @Test
    public void loadBrowse_SearchCategory_LoggedIn() throws Exception {
        Book book = new Book();
        book.setAdded(LocalDateTime.now());
        book.setTitle("Test Book");
        book.addAuthor(new Author("Test", "Author"));
        book.setGenre(Genre.SCIFI);
        book.setCategory(Category.NONFICTION);
        bookService.saveBook(book);

        Book book2 = new Book();
        book2.setAdded(LocalDateTime.now());
        book2.setTitle("Test Book 2");
        book2.addAuthor(new Author("Another", "Author"));
        book2.setGenre(Genre.FANTASY);
        book2.setCategory(Category.FICTION);
        bookService.saveBook(book2);

        mockMvc.perform(get("/browse").with(user(userDetails))
                        .param("category", String.valueOf(Category.FICTION)))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/browse"))
                .andExpect(model().attribute("activePage", "browse"))
                .andExpect(model().attribute("bookList", hasProperty("content", hasSize(1))))
                .andExpect(model().attribute("bookList", hasProperty("content", contains(book2))))
                .andExpect(model().attribute("bookList", hasProperty("content", contains(not(book)))));
    }

    @Test
    public void loadBrowse_SearchGenreAndCategory_LoggedOut() throws Exception {
        Book book = new Book();
        book.setAdded(LocalDateTime.now());
        book.setTitle("Test Book");
        book.addAuthor(new Author("Test", "Author"));
        book.setGenre(Genre.SCIFI);
        book.setCategory(Category.FICTION);
        bookService.saveBook(book);

        Book book2 = new Book();
        book2.setAdded(LocalDateTime.now());
        book2.setTitle("Test Book 2");
        book2.addAuthor(new Author("Another", "Author"));
        book2.setGenre(Genre.FANTASY);
        book2.setCategory(Category.FICTION);
        bookService.saveBook(book2);

        Book book3 = new Book();
        book3.setAdded(LocalDateTime.now());
        book3.setTitle("Test Book 3");
        book3.addAuthor(new Author("Another", "One"));
        book3.setGenre(Genre.FANTASY);
        book3.setCategory(Category.NONFICTION);
        bookService.saveBook(book3);

        mockMvc.perform(get("/browse")
                        .param("category", String.valueOf(Category.FICTION))
                        .param("genre", String.valueOf(Genre.FANTASY)))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/browse"))
                .andExpect(model().attribute("activePage", "browse"))
                .andExpect(model().attribute("bookList", hasProperty("content", hasSize(1))))
                .andExpect(model().attribute("bookList", hasProperty("content", contains(book2))))
                .andExpect(model().attribute("bookList", hasProperty("content", contains(not(book)))))
                .andExpect(model().attribute("bookList", hasProperty("content", contains(not(book3)))));
    }

    @Test
    public void loadBrowse_SearchGenreAndCategory_LoggedIn() throws Exception {
        Book book = new Book();
        book.setAdded(LocalDateTime.now());
        book.setTitle("Test Book");
        book.addAuthor(new Author("Test", "Author"));
        book.setGenre(Genre.SCIFI);
        book.setCategory(Category.FICTION);
        bookService.saveBook(book);

        Book book2 = new Book();
        book2.setAdded(LocalDateTime.now());
        book2.setTitle("Test Book 2");
        book2.addAuthor(new Author("Another", "Author"));
        book2.setGenre(Genre.FANTASY);
        book2.setCategory(Category.FICTION);
        bookService.saveBook(book2);

        Book book3 = new Book();
        book3.setAdded(LocalDateTime.now());
        book3.setTitle("Test Book 3");
        book3.addAuthor(new Author("Another", "One"));
        book3.setGenre(Genre.FANTASY);
        book3.setCategory(Category.NONFICTION);
        bookService.saveBook(book3);

        mockMvc.perform(get("/browse").with(user(userDetails))
                        .param("category", String.valueOf(Category.FICTION))
                        .param("genre", String.valueOf(Genre.FANTASY)))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/browse"))
                .andExpect(model().attribute("activePage", "browse"))
                .andExpect(model().attribute("bookList", hasProperty("content", hasSize(1))))
                .andExpect(model().attribute("bookList", hasProperty("content", contains(book2))))
                .andExpect(model().attribute("bookList", hasProperty("content", contains(not(book)))))
                .andExpect(model().attribute("bookList", hasProperty("content", contains(not(book3)))));
    }

    @Test
    public void loadBrowse_NoSearchParams_MultiplePages() throws Exception {
        Author author = new Author("Test", "Author");
        for (int i = 1; i <= 40; i++) {
            Book book = new Book();
            book.setAdded(LocalDateTime.now());
            book.setTitle("Test Book " + i);
            book.addAuthor(author);
            book.setGenre(Genre.SCIFI);
            book.setCategory(Category.FICTION);
            bookService.saveBook(book);
        }

        mockMvc.perform(get("/browse"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/browse"))
                .andExpect(model().attribute("activePage", "browse"))
                .andExpect(model().attribute("bookList", hasProperty("content", hasSize(15))));

        mockMvc.perform(get("/browse").param("pageNo", "1"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("bookList", hasProperty("content", hasSize(15))));

        mockMvc.perform(get("/browse").param("pageNo", "2"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("bookList", hasProperty("content", hasSize(10))));

        mockMvc.perform(get("/browse").param("pageNo", "3"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("bookList", hasProperty("content", hasSize(0))));
    }

    @Test
    public void loadUpdates_NoParams_LoggedOut() throws Exception {
        MvcResult response = mockMvc.perform(get("/updates"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("activePage", "updates"))
                .andExpect(model().attribute("updateList", hasProperty("content", hasSize(0))))
                .andReturn();

        assertThat(response.getResponse().getContentAsString(), not(containsString("Add Update")));
    }

    @Test
    public void loadUpdates_NoParams_LoggedIn() throws Exception {
        MvcResult response = mockMvc.perform(get("/updates").with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("activePage", "updates"))
                .andExpect(model().attribute("updateList", hasProperty("content", hasSize(0))))
                .andReturn();

        assertThat(response.getResponse().getContentAsString(), containsString("New Update"));
    }

    @Test
    public void loadUpdates_NoParams_OneUpdate_LoggedOut() throws Exception {
        Update update = new Update("Test update.", UpdateType.UPDATE);
        update.setAuthor(userRepository.getReferenceById(userDetails.getId()));
        update.setCreated(LocalDateTime.now());
        updateService.save(update);

        mockMvc.perform(get("/updates"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("activePage", "updates"))
                .andExpect(model().attribute("updateList", hasProperty("content", hasSize(1))))
                .andExpect(model().attribute("updateList", hasProperty("content", contains(update))));
    }

    @Test
    public void loadUpdates_NoParams_OneUpdate_LoggedIn() throws Exception {
        Update update = new Update("Test update.", UpdateType.UPDATE);
        update.setAuthor(userRepository.getReferenceById(userDetails.getId()));
        update.setCreated(LocalDateTime.now());
        updateService.save(update);

        MvcResult response = mockMvc.perform(get("/updates").with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("activePage", "updates"))
                .andExpect(model().attribute("updateList", hasProperty("content", hasSize(1))))
                .andExpect(model().attribute("updateList", hasProperty("content", contains(update))))
                .andReturn();

        assertThat(response.getResponse().getContentAsString(), containsString("New Update"));
    }

    @Test
    public void loadUpdates_SearchByType() throws Exception {
        Update update = new Update("Test update.", UpdateType.TODO);
        update.setAuthor(userRepository.getReferenceById(userDetails.getId()));
        update.setCreated(LocalDateTime.now());
        updateService.save(update);

        Update update2 = new Update("Test update.", UpdateType.UPDATE);
        update2.setAuthor(userRepository.getReferenceById(userDetails.getId()));
        update2.setCreated(LocalDateTime.now());
        updateService.save(update2);

        Update update3 = new Update("Test update.", UpdateType.TODO);
        update3.setAuthor(userRepository.getReferenceById(userDetails.getId()));
        update3.setCreated(LocalDateTime.now());
        updateService.save(update3);

        mockMvc.perform(get("/updates")
                        .param("type", String.valueOf(UpdateType.UPDATE)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("activePage", "updates"))
                .andExpect(model().attribute("updateList", hasProperty("content", hasSize(1))))
                .andExpect(model().attribute("updateList", hasProperty("content", contains(not(update)))))
                .andExpect(model().attribute("updateList", hasProperty("content", contains(update2))))
                .andExpect(model().attribute("updateList", hasProperty("content", contains(not(update3)))));
    }

    @Test
    public void loadBookPage_LoggedOut() throws Exception {
        Book book = new Book();
        book.setAdded(LocalDateTime.now());
        book.setTitle("Test Book");
        book.addAuthor(new Author("Test", "Author"));
        book.setGenre(Genre.FANTASY);
        book.setCategory(Category.FICTION);
        bookService.saveBook(book);

        long id = bookService.findNewestBooks(1).getFirst().getId();

        MvcResult response = mockMvc.perform(get("/browse/" + id))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/book"))
                .andExpect(model().attribute("activePage", "browse"))
                .andExpect(model().attribute("book", book))
                .andReturn();

        assertThat(response.getResponse().getContentAsString(), not(containsString("admin-controls")));
    }

    @Test
    public void loadBookPage_LoggedIn() throws Exception {
        Book book = new Book();
        book.setAdded(LocalDateTime.now());
        book.setTitle("Test Book");
        book.addAuthor(new Author("Test", "Author"));
        book.setGenre(Genre.FANTASY);
        book.setCategory(Category.FICTION);
        bookService.saveBook(book);

        long id = bookService.findNewestBooks(1).getFirst().getId();

        MvcResult response = mockMvc.perform(get("/browse/" + id).with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/book"))
                .andExpect(model().attribute("activePage", "browse"))
                .andExpect(model().attribute("book", book))
                .andReturn();

        assertThat(response.getResponse().getContentAsString(), containsString("admin-controls"));
    }

}
