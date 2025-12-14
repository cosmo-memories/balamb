package cosmo_memories.Balamb.service.books;

import cosmo_memories.Balamb.model.enums.Category;
import cosmo_memories.Balamb.model.enums.Genre;
import cosmo_memories.Balamb.model.items.Book;
import cosmo_memories.Balamb.model.items.BookDTO;
import cosmo_memories.Balamb.repository.books.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import static java.lang.Integer.parseInt;

@Service
public class BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    @Autowired
    AuthorService authorService;

    @Autowired
    BookRepository bookRepository;

    public Book save(BookDTO bookDto) {
        return bookRepository.save(bookDto.mapToBook());
    }

    public boolean validateBook(BookDTO book) {
        boolean valid = true;
        if (!validateTitle(book.getTitle())) {
            logger.info("Failed title validation");
            valid = false;
        }
        if (book.getPublisher() != null && !book.getPublisher().isBlank() && !validatePublisher(book.getPublisher())) {
            logger.info("Failed publisher validation");
            valid = false;
        }
        if (book.getPubYear() != null && !book.getPubYear().isBlank() && !validatePubYear(book.getPubYear())) {
            logger.info("Failed year validation");
            valid = false;
        }
        if (book.getIsbn() != null && !book.getIsbn().isBlank() && !validateIsbn(book.getIsbn())) {
            logger.info("Failed ISBN validation");
            valid = false;
        }
        if (book.getAuthors() == null || book.getAuthors().isEmpty()) {
            logger.info("Failed authors validation");
            valid = false;
        } else for (String fullName : book.getAuthors()) {
            if (!authorService.validateAuthor(fullName)) {
                logger.info("Failed author validation");
                valid = false;
            }
        }
        return valid;
    }

    public boolean validateTitle(String title) {
        return !title.isBlank() && title.length() <= 30;
    }

    public boolean validatePublisher(String publisher) {
        return !publisher.isBlank() && publisher.length() <= 30;
    }

    public boolean validatePubYear(String pubYear) {
        boolean valid = true;
        try {
            if (Year.of(parseInt(pubYear)).getValue() > Year.now().getValue()) {
                valid = false;
            }
        } catch (DateTimeParseException | NullPointerException e) {
            valid = false;
        }
        return valid;
    }

    public boolean validateIsbn(String isbn) {
        return isbn.replace("-", "").length() >= 10 && isbn.replace("-", "").length() <= 13;
    }

    public List<Book> findAllBooks() {
        return bookRepository.findAll(Sort.by(Sort.Direction.DESC, "added"));
    }

    public Optional<Book> findBookById(Long id) {
        return bookRepository.findById(id);
    }

    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    public List<Book> findBookByGenre(Genre genre) {
        return bookRepository.findByGenreOrderByAddedDesc(genre);
    }

    public List<Book> findBookByCategory(Category category) {
        return bookRepository.findByCategoryOrderByAddedDesc(category);
    }

    public List<Book> findBookByGenreAndCategory(Genre genre, Category category) {
        return bookRepository.findByGenreAndCategoryOrderByAddedDesc(genre, category);
    }

}
