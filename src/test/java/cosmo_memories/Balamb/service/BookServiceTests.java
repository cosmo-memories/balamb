package cosmo_memories.Balamb.service;

import cosmo_memories.Balamb.model.enums.Category;
import cosmo_memories.Balamb.model.enums.Genre;
import cosmo_memories.Balamb.model.items.BookDTO;
import cosmo_memories.Balamb.service.books.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookServiceTests {

    @Autowired
    private BookService bookService;

    private BookDTO bookDto;

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


}
