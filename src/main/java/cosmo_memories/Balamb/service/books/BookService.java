package cosmo_memories.Balamb.service.books;

import cosmo_memories.Balamb.model.enums.Category;
import cosmo_memories.Balamb.model.enums.Genre;
import cosmo_memories.Balamb.model.items.Author;
import cosmo_memories.Balamb.model.items.Book;
import cosmo_memories.Balamb.model.items.BookDTO;
import cosmo_memories.Balamb.repository.books.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeParseException;
import java.util.*;

import static java.lang.Integer.parseInt;

@Service
public class BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookService.class);
    private final int pageSize = 15;

    @Autowired
    AuthorService authorService;

    @Autowired
    BookRepository bookRepository;

    public Book updateBook(long id, BookDTO bookDto) {
        Book newBook = mapDtoToBook(bookDto);
        Book currentBook = bookRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Book not found"));
        if (!Objects.equals(currentBook.getTitle(), newBook.getTitle())) {
            currentBook.setTitle(newBook.getTitle());
        }
        if (!Objects.equals(currentBook.getAuthors(), newBook.getAuthors())) {
            currentBook.setAuthors(newBook.getAuthors());
        }
        if (!Objects.equals(currentBook.getPublisher(), newBook.getPublisher())) {
            currentBook.setPublisher(newBook.getPublisher());
        }
        if (!Objects.equals(currentBook.getIsbn(), newBook.getIsbn())) {
            currentBook.setIsbn(newBook.getIsbn());
        }
        if (!Objects.equals(currentBook.getNote(), newBook.getNote())) {
            currentBook.setNote(newBook.getNote());
        }
        if (!Objects.equals(currentBook.getSeries(), newBook.getSeries())) {
            currentBook.setSeries(newBook.getSeries());
        }
        if (!Objects.equals(currentBook.getGenre(), newBook.getGenre())) {
            currentBook.setGenre(newBook.getGenre());
        }
        if (!Objects.equals(currentBook.getCategory(), newBook.getCategory())) {
            currentBook.setCategory(newBook.getCategory());
        }
        if (!Objects.equals(currentBook.getPubYear(), newBook.getPubYear())) {
            currentBook.setPubYear(newBook.getPubYear());
        }
        return saveBook(currentBook);
    }

    public Book saveNewBook(BookDTO bookDto) {
        return bookRepository.save(mapDtoToBook(bookDto));
    }

    public Book mapDtoToBook(BookDTO dto) {
        Book book = new Book();
        book.setTitle(dto.getTitle());
        if (dto.getPublisher() != null && !dto.getPublisher().isBlank()) {
            book.setPublisher(dto.getPublisher());
        }
        if (dto.getPubYear() != null && !dto.getPubYear().isBlank()) {
            try {
                book.setPubYear(Year.of(Integer.parseInt(dto.getPubYear())));
            } catch (NumberFormatException e) {
                book.setPubYear(null);
            }
        }
        if (dto.getIsbn() != null && !dto.getIsbn().isBlank()) {
            book.setIsbn(dto.getIsbn());
        }
        if (dto.getNote() != null && !dto.getNote().isBlank()) {
            book.setNote(dto.getNote());
        }
        if (dto.getSeries() != null && !dto.getSeries().isBlank()) {
            book.setSeries(dto.getSeries());
        }
        book.setGenre(dto.getGenre());
        book.setCategory(dto.getCategory());
        if (dto.getAuthors() != null && !dto.getAuthors().isEmpty()) {
            for (String author : dto.getAuthors()) {
                String[] names = author.split(",");
                Optional<Author> existingAuthor = authorService.findByFullName(names[1].trim(), names[0].trim());
                if (existingAuthor.isPresent()) {
                    book.addAuthor(existingAuthor.get());
                } else {
                    book.addAuthor(new Author(names[1].trim(), names[0].trim()));
                }
            }
        }
        book.setAdded(LocalDateTime.now());
        return book;
    }

    public BookDTO mapBookToDto(Book book) {
        BookDTO dto = new BookDTO();
        dto.setTitle(book.getTitle());
        dto.setPublisher(book.getPublisher());
        if (book.getPubYear() == null) {
            dto.setPubYear(null);
        } else {
            dto.setPubYear(book.getPubYear().toString());
        }
        dto.setIsbn(book.getIsbn());
        dto.setGenre(book.getGenre());
        dto.setCategory(book.getCategory());
        dto.setNote(book.getNote());
        dto.setSeries(book.getSeries());
        List<String> authors = new ArrayList<String>();
        for (Author author : book.getAuthors()) {
            authors.add(author.getCommaSeparatedFullName());
        }
        dto.setAuthors(authors);
        return dto;
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
        if (book.getSeries() != null && !book.getSeries().isBlank() && !validateSeries(book.getSeries())) {
            logger.info("Failed series validation");
            valid = false;
        }
        if (book.getNote() != null && !book.getNote().isBlank() && !validateNote(book.getNote())) {
            logger.info("Failed note validation");
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
        return !title.isBlank() && title.length() <= 60;
    }

    public boolean validatePublisher(String publisher) {
        return !publisher.isBlank() && publisher.length() <= 30;
    }

    public boolean validateSeries(String series) {
        return !series.isBlank() && series.length() <= 30;
    }

    public boolean validateNote(String note) {
        return !note.isBlank() && note.length() <= 500;
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

    public Page<Book> findAllBooksOnPage(int pageNo) {
        return bookRepository.findAll(PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "added")));
    }

    public Optional<Book> findBookById(Long id) {
        return bookRepository.findById(id);
    }

    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    public Page<Book> findBookByGenre(Genre genre, int pageNo) {
        return bookRepository.findByGenreOrderByAddedDesc(genre, PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "added")));
    }

    public Page<Book> findBookByCategory(Category category, int pageNo) {
        return bookRepository.findByCategoryOrderByAddedDesc(category, PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "added")));
    }

    public Page<Book> findBookByGenreAndCategory(Genre genre, Category category, int pageNo) {
        return bookRepository.findByGenreAndCategoryOrderByAddedDesc(genre, category, PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "added")));
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

}
