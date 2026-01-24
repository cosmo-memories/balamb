package cosmo_memories.Balamb.unit;

import cosmo_memories.Balamb.model.enums.Category;
import cosmo_memories.Balamb.model.enums.Genre;
import cosmo_memories.Balamb.model.items.Book;
import cosmo_memories.Balamb.repository.books.BookRepository;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryTests {

    @Autowired
    BookRepository bookRepository;

    PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "added"));

    @BeforeEach
    public void setUp() {
        Book book = new Book();
        book.setAdded(LocalDateTime.now());
        book.setTitle("Test Book");
        book.setGenre(Genre.FANTASY);
        book.setCategory(Category.FICTION);
        bookRepository.save(book);
    }

    @Test
    public void findByGenre_NoneFound() {
        Page<Book> result = bookRepository.findByGenreOrSubgenreOrderByAddedDesc(Genre.ART, Genre.ART, pageable);
        assertTrue(result.isEmpty());
    }

    @Test
    public void findByGenre_OneFound() {
        Page<Book> result = bookRepository.findByGenreOrSubgenreOrderByAddedDesc(Genre.FANTASY, Genre.FANTASY, pageable);
        assertTrue(result.hasContent());
        assertEquals(1, result.getContent().size());
        assertSame("Test Book", result.getContent().getFirst().getTitle());
    }

    @Test
    public void findByGenre_TwoFound() {
        Book newBook = new Book();
        newBook.setTitle("Test Book 2");
        newBook.setGenre(Genre.FANTASY);
        bookRepository.save(newBook);

        Page<Book> result = bookRepository.findByGenreOrSubgenreOrderByAddedDesc(Genre.FANTASY, Genre.FANTASY, pageable);
        assertTrue(result.hasContent());
        assertEquals(2, result.getContent().size());
    }

    @Test
    public void findBySubgenre_OneFound() {
        Book newBook = new Book();
        newBook.setTitle("Test Book 2");
        newBook.setSubgenre(Genre.SCIFI);
        bookRepository.save(newBook);

        Page<Book> result = bookRepository.findByGenreOrSubgenreOrderByAddedDesc(Genre.SCIFI, Genre.SCIFI, pageable);
        assertTrue(result.hasContent());
        assertEquals(1, result.getContent().size());
        assertSame("Test Book 2", result.getContent().getFirst().getTitle());
    }

    @Test
    public void findByGenreAndSubgenre_TwoFound() {
        Book newBook = new Book();
        newBook.setTitle("Test Book 2");
        newBook.setSubgenre(Genre.FANTASY);
        bookRepository.save(newBook);

        Page<Book> result = bookRepository.findByGenreOrSubgenreOrderByAddedDesc(Genre.FANTASY, Genre.FANTASY, pageable);
        assertTrue(result.hasContent());
        assertEquals(2, result.getContent().size());
    }

    @Test
    public void findByGenreAndSubgenre_WithDifferentMainGenres_TwoFound() {
        Book newBook = new Book();
        newBook.setTitle("Test Book 2");
        newBook.setGenre(Genre.BIOGRAPHY);
        newBook.setSubgenre(Genre.FANTASY);
        bookRepository.save(newBook);

        Page<Book> result = bookRepository.findByGenreOrSubgenreOrderByAddedDesc(Genre.FANTASY, Genre.FANTASY, pageable);
        assertTrue(result.hasContent());
        assertEquals(2, result.getContent().size());
    }

    @Test
    public void findByCategory_NoneFound() {
        Page<Book> result = bookRepository.findByCategoryOrderByAddedDesc(Category.NONFICTION, pageable);
        assertTrue(result.isEmpty());
    }

    @Test
    public void findByCategory_OneFound() {
        Page<Book> result = bookRepository.findByCategoryOrderByAddedDesc(Category.FICTION, pageable);
        assertTrue(result.hasContent());
        assertEquals(1, result.getContent().size());
        assertSame("Test Book", result.getContent().getFirst().getTitle());
    }

    @Test
    public void findByCategory_TwoFound() {
        Book newBook = new Book();
        newBook.setTitle("Test Book 2");
        newBook.setCategory(Category.FICTION);
        bookRepository.save(newBook);

        Page<Book> result = bookRepository.findByCategoryOrderByAddedDesc(Category.FICTION, pageable);
        assertTrue(result.hasContent());
        assertEquals(2, result.getContent().size());
    }

    @Test
    public void findByCategoryAndGenre_NoneFound() {
        Page<Book> result = bookRepository.findByGenreOrSubgenreAndCategoryOrderByAddedDesc(Genre.ART, Genre.ART, Category.CHILDRENS, pageable);
        assertTrue(result.isEmpty());
    }

    @Test
    public void findByCategoryAndGenre_OneFound() {
        Page<Book> result = bookRepository.findByGenreOrSubgenreAndCategoryOrderByAddedDesc(Genre.FANTASY, Genre.FANTASY, Category.FICTION, pageable);
        assertTrue(result.hasContent());
        assertEquals(1, result.getContent().size());
        assertSame("Test Book", result.getContent().getFirst().getTitle());
    }

    @Test
    public void findByCategoryAndGenre_TwoFound() {
        Book newBook = new Book();
        newBook.setTitle("Test Book 2");
        newBook.setCategory(Category.FICTION);
        newBook.setGenre(Genre.BIOGRAPHY);
        newBook.setSubgenre(Genre.FANTASY);
        bookRepository.save(newBook);

        Page<Book> result = bookRepository.findByGenreOrSubgenreAndCategoryOrderByAddedDesc(Genre.FANTASY, Genre.FANTASY, Category.FICTION, pageable);
        assertTrue(result.hasContent());
        assertEquals(2, result.getContent().size());
    }

    @Test
    public void findNewestBooks_CorrectOrder() {
        Book newBook = new Book();
        newBook.setTitle("Test Book 2");
        newBook.setAdded(LocalDateTime.now());
        bookRepository.save(newBook);

        Book newerBook = new Book();
        newerBook.setTitle("Test Book 3");
        newerBook.setAdded(LocalDateTime.now());
        bookRepository.save(newerBook);

        List<Book> result = bookRepository.findNewestBooks(pageable);
        assertFalse(result.isEmpty());
        assertEquals("Test Book 3", result.getFirst().getTitle());
        assertEquals("Test Book", result.getLast().getTitle());
    }

}
