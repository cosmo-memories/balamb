package cosmo_memories.Balamb.service;

import cosmo_memories.Balamb.model.enums.Category;
import cosmo_memories.Balamb.model.enums.Genre;
import cosmo_memories.Balamb.model.items.Author;
import cosmo_memories.Balamb.model.items.Book;
import cosmo_memories.Balamb.model.items.BookDTO;
import cosmo_memories.Balamb.service.books.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookServiceTests {

    @Autowired
    private BookService bookService;

    private BookDTO bookDto;
    private MultipartFile file = mock(MultipartFile.class);

    @BeforeEach
    public void setUp() {
        bookDto = new BookDTO();
        bookDto.setTitle("Test Book");
        bookDto.setPublisher("Test Publisher");
        bookDto.setPubYear("2026");
        bookDto.setIsbn("978-1-529-15746-8");
        bookDto.setSeries("Test Series");
        bookDto.setGenre(Genre.FANTASY);
        bookDto.setSubgenre(Genre.SCIFI);
        bookDto.setCategory(Category.FICTION);
        bookDto.setAuthors(List.of("Author, Test", "Author 2, Test"));
        bookDto.setNote("Test note.");
    }

    @Test
    public void validateDto_Valid() {
        assertTrue(bookService.validateBookDto(bookDto));
    }

    @Test
    public void validateDto_BlankTitle() {
        BookDTO invalidDto = new BookDTO();
        invalidDto.setTitle("");
        invalidDto.setAuthors(List.of("Author, Test", "Author 2, Test"));

        assertFalse(bookService.validateBookDto(invalidDto));
    }

    @Test
    public void validateDto_BlankAuthor() {
        BookDTO invalidDto = new BookDTO();
        invalidDto.setTitle("Test Book");
        invalidDto.setAuthors(List.of("Author, Test", ""));

        assertFalse(bookService.validateBookDto(invalidDto));
    }

    @Test
    public void validateDto_BlankPublisher() {
        BookDTO invalidDto = new BookDTO();
        invalidDto.setTitle("Test Book");
        invalidDto.setAuthors(List.of("Author, Test", "Author 2, Test"));
        invalidDto.setPublisher("");

        assertFalse(bookService.validateBookDto(invalidDto));
    }

    @Test
    public void validateDto_BlankPubYear() {
        BookDTO invalidDto = new BookDTO();
        invalidDto.setTitle("Test Book");
        invalidDto.setAuthors(List.of("Author, Test", "Author 2, Test"));
        invalidDto.setPubYear("");

        assertFalse(bookService.validateBookDto(invalidDto));
    }

    @Test
    public void validateDto_BlankIsbn() {
        BookDTO invalidDto = new BookDTO();
        invalidDto.setTitle("Test Book");
        invalidDto.setAuthors(List.of("Author, Test", "Author 2, Test"));
        invalidDto.setIsbn("");

        assertFalse(bookService.validateBookDto(invalidDto));
    }

    @Test
    public void validateDto_BlankSeries() {
        BookDTO invalidDto = new BookDTO();
        invalidDto.setTitle("Test Book");
        invalidDto.setAuthors(List.of("Author, Test", "Author 2, Test"));
        invalidDto.setSeries("");

        assertFalse(bookService.validateBookDto(invalidDto));
    }

    @Test
    public void validateDto_BlankNote() {
        BookDTO invalidDto = new BookDTO();
        invalidDto.setTitle("Test Book");
        invalidDto.setAuthors(List.of("Author, Test", "Author 2, Test"));
        invalidDto.setNote("");

        assertFalse(bookService.validateBookDto(invalidDto));
    }

    @Test
    public void validateDto_InvalidTitle() {
        BookDTO invalidDto = new BookDTO();
        invalidDto.setTitle("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        invalidDto.setAuthors(List.of("Author, Test", "Author 2, Test"));

        assertFalse(bookService.validateBookDto(invalidDto));
    }

    @Test
    public void validateDto_InvalidAuthor() {
        BookDTO invalidDto = new BookDTO();
        invalidDto.setTitle("Test Book");
        invalidDto.setAuthors(List.of("Author, Test", "Test 2"));

        assertFalse(bookService.validateBookDto(invalidDto));
    }

    @Test
    public void validateDto_InvalidPublisher() {
        BookDTO invalidDto = new BookDTO();
        invalidDto.setTitle("Test Book");
        invalidDto.setAuthors(List.of("Author, Test", "Author 2, Test"));
        invalidDto.setPublisher("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

        assertFalse(bookService.validateBookDto(invalidDto));
    }

    @Test
    public void validateDto_InvalidPubYear() {
        BookDTO invalidDto = new BookDTO();
        invalidDto.setTitle("Test Book");
        invalidDto.setAuthors(List.of("Author, Test", "Author 2, Test"));
        invalidDto.setPubYear("-10");

        assertFalse(bookService.validateBookDto(invalidDto));
    }

    @Test
    public void validateDto_InvalidIsbn() {
        BookDTO invalidDto = new BookDTO();
        invalidDto.setTitle("Test Book");
        invalidDto.setAuthors(List.of("Author, Test", "Author 2, Test"));
        invalidDto.setIsbn("69");

        assertFalse(bookService.validateBookDto(invalidDto));
    }

    @Test
    public void validateDto_InvalidSeries() {
        BookDTO invalidDto = new BookDTO();
        invalidDto.setTitle("Test Book");
        invalidDto.setAuthors(List.of("Author, Test", "Author 2, Test"));
        invalidDto.setSeries("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

        assertFalse(bookService.validateBookDto(invalidDto));
    }

    @Test
    public void validateDto_InvalidNote() {
        BookDTO invalidDto = new BookDTO();
        invalidDto.setTitle("Test Book");
        invalidDto.setAuthors(List.of("Author, Test", "Author 2, Test"));
        invalidDto.setNote("a".repeat(600));
        assertFalse(bookService.validateBookDto(invalidDto));
    }

    static Stream<String> validTitles() {
        return Stream.of("Project Hail Mary",
                "Wings of Fire: The Brightest Night",
                "Play Nice: The Rise, Fall, and Future of Blizzard Entertainment",
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                "X");
    }

    static Stream<String> invalidTitles() {
        return Stream.of("",
                " ",
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
    }

    @ParameterizedTest
    @MethodSource("validTitles")
    public void validateTitle_Valid(String title) {
        assertTrue(bookService.validateTitle(title));
    }

    @ParameterizedTest
    @MethodSource("invalidTitles")
    public void validateTitle_Invalid(String title) {
        assertFalse(bookService.validateTitle(title));
    }

    static Stream<String> validPublishers() {
        return Stream.of("Penguin",
                "Sort Of",
                "Penguin-RandomHouse",
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
    }

    static Stream<String> invalidPublishers() {
        return Stream.of("",
                " ",
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
    }

    @ParameterizedTest
    @MethodSource("validPublishers")
    public void validatePublisher_Valid(String publisher) {
        assertTrue(bookService.validatePublisher(publisher));
    }

    @ParameterizedTest
    @MethodSource("invalidPublishers")
    public void validatePublisher_Invalid(String publisher) {
        assertFalse(bookService.validatePublisher(publisher));
    }

    static Stream<String> validSeries() {
        return Stream.of("Wings of Fire",
                "Wings of Fire: The Graphic Novel",
                "Wings of Fire - The Graphic Novel",
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
    }

    static Stream<String> invalidSeries() {
        return Stream.of("",
                " ",
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
    }

    @ParameterizedTest
    @MethodSource("validSeries")
    public void validateSeries_Valid(String series) {
        assertTrue(bookService.validateSeries(series));
    }

    @ParameterizedTest
    @MethodSource("invalidSeries")
    public void validateSeries_Invalid(String series) {
        assertFalse(bookService.validateSeries(series));
    }

    static Stream<String> validNotes() {
        return Stream.of("This is a note.",
                "X",
                "!",
                "a".repeat(500));
    }

    static Stream<String> invalidNotes() {
        return Stream.of("",
                " ",
                "a".repeat(501));
    }

    @ParameterizedTest
    @MethodSource("validNotes")
    public void validateNote_Valid(String note) {
        assertTrue(bookService.validateNote(note));
    }

    @ParameterizedTest
    @MethodSource("invalidNotes")
    public void validateNote_Invalid(String note) {
        assertFalse(bookService.validateNote(note));
    }

    static Stream<String> validYears() {
        return Stream.of("1",
                "1000",
                "2006",
                "2025",
                "2026");
    }

    static Stream<String> invalidYears() {
        return Stream.of("0",
                "-1",
                "2027",
                "3000000",
                "",
                " ",
                "text",
                "two thousand and six");
    }

    @ParameterizedTest
    @MethodSource("validYears")
    public void validatePubYear_Valid(String pubYear) {
        assertTrue(bookService.validatePubYear(pubYear));
    }

    @ParameterizedTest
    @MethodSource("invalidYears")
    public void validatePubYear_Invalid(String pubYear) {
        assertFalse(bookService.validatePubYear(pubYear));
    }

    static Stream<String> validISBNs() {
        return Stream.of("987-1-529-15746-8",
                "9781529157468",
                "1761202100",
                "1-76120-210-0");
    }

    static Stream<String> invalidISBNs() {
        return Stream.of("",
                " ",
                "             ",
                "-",
                "abcde",
                "978-1-529-15746",
                "xxxxxxxxx",
                "xxxxxxxxxxx",
                "xxxxxxxxxxxx",
                "xxxxxxxxxxxxxx",
                "xxxx-xxxxx",
                "xxx-xx-xxxxxx",
                "xxxxxxxxx-xxx",
                "xxxxxxx--xxxxxxx");
    }

    @ParameterizedTest
    @MethodSource("validISBNs")
    public void validateISBNs_Valid(String isbn) {
        assertTrue(bookService.validateIsbn(isbn));
    }

    @ParameterizedTest
    @MethodSource("invalidISBNs")
    public void validateISBNs_Invalid(String isbn) {
        assertFalse(bookService.validateIsbn(isbn));
    }

    @Test
    public void findRandom_NoneInDB() {
        assertNull(bookService.findRandomBook());
    }

    @Test
    public void findRandom_OnlyOne() {
        Book book = bookService.saveBookFromDto(bookDto);
        assertEquals(book, bookService.findRandomBook());
    }

    @Test
    public void findRandom_OneOfTwo() {
        Book book1 = bookService.saveBookFromDto(bookDto);
        Book book2 = bookService.saveBookFromDto(bookDto);

        Book random = bookService.findRandomBook();
        assertTrue(book1 == random || book2 == random);
    }

    @Test
    public void findAll_NoneInDB() {
        assertEquals(0, bookService.findAllBooksOnPage(0).getContent().size());
    }

    @Test
    public void findAll_OnlyOne() {
        Book book = bookService.saveBookFromDto(bookDto);
        assertEquals(1, bookService.findAllBooksOnPage(0).getContent().size());
        assertEquals(book, bookService.findAllBooksOnPage(0).getContent().getFirst());
    }

    @Test
    public void findAll_OnePage() {
        Book book1 = bookService.saveBookFromDto(bookDto);
        for (int i = 2; i <= 14; i++) {
            bookService.saveBookFromDto(bookDto);
        }
        Book book15 = bookService.saveBookFromDto(bookDto);

        assertEquals(15, bookService.findAllBooksOnPage(0).getContent().size());
        assertEquals(book1, bookService.findAllBooksOnPage(0).getContent().getLast());
        assertEquals(book15, bookService.findAllBooksOnPage(0).getContent().getFirst());
    }

    @Test
    public void findAll_SecondPage() {
        Book book1 = bookService.saveBookFromDto(bookDto);
        for (int i = 2; i <= 14; i++) {
            bookService.saveBookFromDto(bookDto);
        }
        Book book15 = bookService.saveBookFromDto(bookDto);
        for (int i = 1; i <= 15; i++) {
            bookService.saveBookFromDto(bookDto);
        }

        assertEquals(15, bookService.findAllBooksOnPage(1).getContent().size());
        assertEquals(book1, bookService.findAllBooksOnPage(1).getContent().getLast());
        assertEquals(book15, bookService.findAllBooksOnPage(1).getContent().getFirst());
    }

    @Test
    public void findAll_SecondPartialPage() {
        Book book1 = bookService.saveBookFromDto(bookDto);
        Book book2 = bookService.saveBookFromDto(bookDto);
        for (int i = 1; i <= 15; i++) {
            bookService.saveBookFromDto(bookDto);
        }

        assertEquals(2, bookService.findAllBooksOnPage(1).getContent().size());
        assertEquals(book1, bookService.findAllBooksOnPage(1).getContent().getLast());
        assertEquals(book2, bookService.findAllBooksOnPage(1).getContent().getFirst());
    }

    @Test
    public void findAll_SecondPageEmpty() {
        for (int i = 1; i <= 15; i++) {
            bookService.saveBookFromDto(bookDto);
        }

        assertEquals(0, bookService.findAllBooksOnPage(1).getContent().size());
    }

    @Test
    public void findByID_Success() {
        Book book = bookService.saveBookFromDto(bookDto);

        Optional<Book> result = bookService.findBookById(book.getId());
        assertTrue(result.isPresent());
        assertEquals(book, result.get());
    }

    @Test
    public void findByID_NotFound() {
        Book book = bookService.saveBookFromDto(bookDto);

        Optional<Book> result = bookService.findBookById(book.getId() + 1);
        assertTrue(result.isEmpty());
    }

    @Test
    public void findByGenre_NoneFound() {
        Page<Book> result = bookService.findBookByGenre(Genre.BIOGRAPHY, 0);
        assertTrue(result.isEmpty());
    }

    @Test
    public void findByGenre_OneFoundByMainGenre() {
        Book book = bookService.saveBookFromDto(bookDto);
        Page<Book> result = bookService.findBookByGenre(Genre.FANTASY, 0);

        assertEquals(1, result.getContent().size());
        assertEquals(book, result.getContent().getFirst());
    }

    @Test
    public void findByGenre_OneFoundBySubgenre() {
        Book book = bookService.saveBookFromDto(bookDto);
        Page<Book> result = bookService.findBookByGenre(Genre.SCIFI, 0);

        assertEquals(1, result.getContent().size());
        assertEquals(book, result.getContent().getFirst());
    }

    @Test
    public void findByCategory_NoneFound() {
        Page<Book> result = bookService.findBookByCategory(Category.GRAPHIC_NOVEL, 0);
        assertTrue(result.isEmpty());
    }

    @Test
    public void findByCategory_OneFound() {
        Book book = bookService.saveBookFromDto(bookDto);
        Page<Book> result = bookService.findBookByCategory(Category.FICTION, 0);

        assertEquals(1, result.getContent().size());
        assertEquals(book, result.getContent().getFirst());
    }

    @Test
    public void findByCategoryAndGenre_NoneFound() {
        bookService.saveBookFromDto(bookDto);
        Page<Book> result = bookService.findBookByGenreAndCategory(Genre.FANTASY, Category.GRAPHIC_NOVEL, 0);
        assertTrue(result.isEmpty());
    }

    @Test
    public void findByCategoryAndGenre_OneFoundByMainGenre() {
        Book book = bookService.saveBookFromDto(bookDto);
        Page<Book> result = bookService.findBookByGenreAndCategory(Genre.FANTASY, Category.FICTION, 0);

        assertEquals(1, result.getContent().size());
        assertEquals(book, result.getContent().getFirst());
    }

    @Test
    public void findByCategoryAndGenre_OneFoundBySubgenre() {
        Book book = bookService.saveBookFromDto(bookDto);
        Page<Book> result = bookService.findBookByGenreAndCategory(Genre.SCIFI, Category.FICTION, 0);

        assertEquals(1, result.getContent().size());
        assertEquals(book, result.getContent().getFirst());
    }

    @Test
    public void deleteBook_Success() {
        Book book = bookService.saveBookFromDto(bookDto);
        bookService.deleteBook(book.getId());

        Optional<Book> result = bookService.findBookById(book.getId());
        assertTrue(result.isEmpty());
    }

    @Test
    public void deleteBook_InvalidID_BookNotDeleted() {
        Book book = bookService.saveBookFromDto(bookDto);
        bookService.deleteBook(book.getId() + 1);

        Optional<Book> result = bookService.findBookById(book.getId());
        assertTrue(result.isPresent());
    }

    @Test
    public void findNewest_NoneInDB() {
        List<Book> result = bookService.findNewestBooks(10);
        assertTrue(result.isEmpty());
    }

    @Test
    public void findNewest_OneInDB() {
        Book book = bookService.saveBookFromDto(bookDto);
        List<Book> result = bookService.findNewestBooks(10);
        assertEquals(1, result.size());
        assertEquals(book, result.getFirst());
    }

    @Test
    public void findNewest_ManyInDB() {
        for (int i = 1; i <= 20; i++) {
            bookService.saveBookFromDto(bookDto);
        }
        Book book = bookService.saveBookFromDto(bookDto);
        List<Book> result = bookService.findNewestBooks(10);
        assertEquals(10, result.size());
        assertEquals(book, result.getFirst());
    }

    @Test
    public void toggleComplete_On() {
        Book book = bookService.saveBookFromDto(bookDto);
        assertFalse(book.getComplete());

        bookService.toggleComplete(book);
        Optional<Book> updated = bookService.findBookById(book.getId());
        assertTrue(updated.isPresent());
        assertTrue(updated.get().getComplete());
    }

    @Test
    public void toggleComplete_Off() {
        Book book = bookService.saveBookFromDto(bookDto);
        assertFalse(book.getComplete());

        bookService.toggleComplete(book);
        Optional<Book> updated = bookService.findBookById(book.getId());
        assertTrue(updated.isPresent());
        assertTrue(updated.get().getComplete());

        bookService.toggleComplete(book);
        updated = bookService.findBookById(book.getId());
        assertTrue(updated.isPresent());
        assertFalse(updated.get().getComplete());
    }

    @Test
    public void mapDtoToBook_AllFields() {
        Book book = bookService.mapDtoToBook(bookDto);
        assertEquals(book.getTitle(), bookDto.getTitle());
        assertEquals(book.getPublisher(), bookDto.getPublisher());
        assertEquals(book.getPubYear().toString(), bookDto.getPubYear());
        assertEquals(book.getIsbn(), bookDto.getIsbn());
        assertEquals(book.getNote(), bookDto.getNote());
        assertEquals(book.getGenre(), bookDto.getGenre());
        assertEquals(book.getSubgenre(), bookDto.getSubgenre());
        assertEquals(book.getCategory(), bookDto.getCategory());
        assertEquals(book.getSeries(), bookDto.getSeries());
        assertEquals(book.getAuthors().size(), bookDto.getAuthors().size());
        for (int i = 0; i < book.getAuthors().size(); i++) {
            assertEquals(book.getAuthors().get(i).getCommaSeparatedFullName(), bookDto.getAuthors().get(i));
        }
        assertFalse(book.getComplete());
        assertTrue(book.getAdded().isBefore(LocalDateTime.now()));
        assertTrue(book.getAdded().isAfter(LocalDateTime.now().minusDays(1)));
    }

    @Test
    public void mapBookToDto_AllFields() {
        Book book = bookService.mapDtoToBook(bookDto);
        BookDTO mappedDto = bookService.mapBookToDto(book);
        assertEquals(book.getTitle(), mappedDto.getTitle());
        assertEquals(book.getPublisher(), mappedDto.getPublisher());
        assertEquals(book.getPubYear().toString(), mappedDto.getPubYear());
        assertEquals(book.getIsbn(), mappedDto.getIsbn());
        assertEquals(book.getNote(), mappedDto.getNote());
        assertEquals(book.getGenre(), mappedDto.getGenre());
        assertEquals(book.getSubgenre(), mappedDto.getSubgenre());
        assertEquals(book.getCategory(), mappedDto.getCategory());
        assertEquals(book.getSeries(), mappedDto.getSeries());
        assertEquals(book.getAuthors().size(), mappedDto.getAuthors().size());
        for (int i = 0; i < book.getAuthors().size(); i++) {
            assertEquals(book.getAuthors().get(i).getCommaSeparatedFullName(), mappedDto.getAuthors().get(i));
        }
    }

    @Test
    public void mapDtoToBook_MinimalFields() {
        BookDTO dto = new BookDTO();
        dto.setTitle("Title");
        dto.setAuthors(List.of("Author, The"));

        Book book = bookService.mapDtoToBook(dto);
        assertEquals(book.getTitle(), dto.getTitle());
        assertEquals(book.getAuthors().size(), dto.getAuthors().size());
        for (int i = 0; i < book.getAuthors().size(); i++) {
            assertEquals(book.getAuthors().get(i).getCommaSeparatedFullName(), dto.getAuthors().get(i));
        }
        assertFalse(book.getComplete());
        assertTrue(book.getAdded().isBefore(LocalDateTime.now()));
        assertTrue(book.getAdded().isAfter(LocalDateTime.now().minusDays(1)));
    }

    @Test
    public void mapBookToDto_MinimalFields() {
        Book book = new Book();
        book.setTitle("Title");
        book.setAuthors(List.of(new Author("The", "Author")));
        book.setAdded(LocalDateTime.now());
        BookDTO dto = bookService.mapBookToDto(book);

        assertEquals(book.getTitle(), dto.getTitle());
        assertEquals(book.getAuthors().size(), dto.getAuthors().size());
        for (int i = 0; i < book.getAuthors().size(); i++) {
            assertEquals(book.getAuthors().get(i).getCommaSeparatedFullName(), dto.getAuthors().get(i));
        }
    }

    @Test
    public void validateImage_Valid() {
        Mockito.when(file.getContentType()).thenReturn("image/jpg");
        Mockito.when(file.getSize()).thenReturn(10L * 1024 * 1024);
        assertEquals("", bookService.validateImage(file));
    }

    @Test
    public void validateImage_NoFile() {
        assertEquals("No file selected.", bookService.validateImage(null));
    }

    @Test
    public void validateImage_FileTooLarge() {
        Mockito.when(file.getSize()).thenReturn(11L * 1024 * 1024);
        assertEquals("File must be smaller than 10MB.", bookService.validateImage(file));
    }

    @Test
    public void validateImage_WrongFileType_Exe() {
        Mockito.when(file.getContentType()).thenReturn("image/exe");
        Mockito.when(file.getSize()).thenReturn(10L * 1024 * 1024);
        assertEquals("File type must be PNG or JPG.", bookService.validateImage(file));
    }

    @Test
    public void validateImage_WrongFileType_NoSlash() {
        Mockito.when(file.getContentType()).thenReturn("gif");
        Mockito.when(file.getSize()).thenReturn(10L * 1024 * 1024);
        assertEquals("File type must be PNG or JPG.", bookService.validateImage(file));
    }

    @Test
    public void validateImage_WrongFileType_Null() {
        Mockito.when(file.getContentType()).thenReturn(null);
        Mockito.when(file.getSize()).thenReturn(10L * 1024 * 1024);
        assertEquals("File type must be PNG or JPG.", bookService.validateImage(file));
    }

}
