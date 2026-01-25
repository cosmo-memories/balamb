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
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

/**
 * Service class for Book-related functionality.
 */
@Service
public class BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookService.class);
    private final int pageSize = 15;

    @Autowired
    AuthorService authorService;

    @Autowired
    BookRepository bookRepository;

    /**
     * Update existing Book with information provided via DTO.
     * @param id            Book ID
     * @param bookDto       Book DTO
     * @return              Book
     */
    public Book updateBook(long id, BookDTO bookDto) {
        Book newBook = mapDtoToBook(bookDto);
        Book currentBook = bookRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Book not found"));
        if (!Objects.equals(currentBook.getTitle(), newBook.getTitle())) {
            currentBook.setTitle(newBook.getTitle());
        }
        if (!Objects.equals(currentBook.getAuthors(), newBook.getAuthors())) {
            currentBook.setAuthors(newBook.getAuthors());
            logger.info(currentBook.getAuthors().getFirst().getFirstName());
            logger.info(String.valueOf(currentBook.getAuthors().size()));
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
        if (!Objects.equals(currentBook.getSubgenre(), newBook.getSubgenre())) {
            currentBook.setSubgenre(newBook.getSubgenre());
        }
        if (!Objects.equals(currentBook.getCategory(), newBook.getCategory())) {
            currentBook.setCategory(newBook.getCategory());
        }
        if (!Objects.equals(currentBook.getPubYear(), newBook.getPubYear())) {
            currentBook.setPubYear(newBook.getPubYear());
        }
        logger.info("Saving book.");
        return saveBook(currentBook);
    }

    /**
     * Save new Book with information provided via DTO.
     * @param bookDto       DTO
     * @return              New Book
     */
    public Book saveBookFromDto(BookDTO bookDto) {
        return bookRepository.save(mapDtoToBook(bookDto));
    }

    /**
     * Map information from DTO to Book.
     * @param dto           DTO
     * @return              New Book
     */
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
        if (dto.getSubgenre() != null && dto.getSubgenre() != dto.getGenre()) {
            book.setSubgenre(dto.getSubgenre());
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
                    Author newAuthor = new Author(names[1].trim(), names[0].trim());
                    authorService.save(newAuthor);
                    book.addAuthor(newAuthor);
                    logger.info("Adding author:{}", book.getAuthors().getLast().getFullName());
                }
            }
        }
        book.setAdded(LocalDateTime.now());
        return book;
    }

    /**
     * Create DTO with information from Book.
     * @param book      Book
     * @return          New DTO
     */
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
        dto.setSubgenre(book.getSubgenre());
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

    /**
     * Run all validation for Book DTO.
     * @param book          DTO
     * @return              Boolean
     */
    public boolean validateBookDto(BookDTO book) {
        boolean valid = true;
        if (!validateTitle(book.getTitle())) {
            logger.info("Failed title validation");
            valid = false;
        }
        if (book.getPublisher() != null && !validatePublisher(book.getPublisher())) {
            logger.info("Failed publisher validation");
            valid = false;
        }
        if (book.getPubYear() != null && !validatePubYear(book.getPubYear())) {
            logger.info("Failed year validation");
            valid = false;
        }
        if (book.getIsbn() != null && !validateIsbn(book.getIsbn())) {
            logger.info("Failed ISBN validation");
            valid = false;
        }
        if (book.getSeries() != null && !validateSeries(book.getSeries())) {
            logger.info("Failed series validation");
            valid = false;
        }
        if (book.getNote() != null && !validateNote(book.getNote())) {
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

    /**
     * Validate Book title.
     * @param title     Title
     * @return          Boolean
     */
    public boolean validateTitle(String title) {
        return !title.isBlank() && title.length() <= 90;
    }

    /**
     * Validate Book publisher.
     * @param publisher     Publisher
     * @return              Boolean
     */
    public boolean validatePublisher(String publisher) {
        return !publisher.isBlank() && publisher.length() <= 30;
    }

    /**
     * Validate Book series.
     * @param series        Series
     * @return              Boolean
     */
    public boolean validateSeries(String series) {
        return !series.isBlank() && series.length() <= 60;
    }

    /**
     * Validate Book notes.
     * @param note          Note
     * @return              Boolean
     */
    public boolean validateNote(String note) {
        return !note.isBlank() && note.length() <= 500;
    }

    /**
     * Validate Book publication year.
     * @param pubYear       Year
     * @return              Boolean
     */
    public boolean validatePubYear(String pubYear) {
        boolean valid = true;
        try {
            if (Year.of(parseInt(pubYear)).getValue() > Year.now().getValue() || Year.of(parseInt(pubYear)).getValue() <= 0) {
                valid = false;
            }
        } catch (Exception e) {
            valid = false;
        }
        return valid;
    }

    /**
     * Validate Book ISBN. Only validates based on length, not that the given string is a real ISBN.
     * @param isbn      ISBN
     * @return          Boolean
     */
    public boolean validateIsbn(String isbn) {
        return isbn.replace("-", "").length() >= 10 && isbn.replace("-", "").length() <= 13;
    }

    /**
     * Select a random Book from the database.
     * @return          Book
     */
    public Book findRandomBook() {
        int rand = (int)(Math.random() * bookRepository.count());
        Page<Book> book = bookRepository.findAll(PageRequest.of(rand, 1));
        if (book.hasContent()) {
            return book.getContent().getFirst();
        } else {
            return null;
        }
    }

    /**
     * Find all Books on given Browse page.
     * @param pageNo        Page number
     * @return              Page of Books
     */
    public Page<Book> findAllBooksOnPage(int pageNo) {
        return bookRepository.findAll(PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "added")));
    }

    /**
     * Find Book by ID.
     * @param id        Book ID
     * @return          Optional containing Book if found
     */
    public Optional<Book> findBookById(Long id) {
        return bookRepository.findById(id);
    }

    /**
     * Save Book.
     * @param book      Book
     * @return          Book
     */
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    /**
     * Find all Books on given page, filtered by Genre.
     * @param genre         Genre
     * @param pageNo        Page number
     * @return              Page of Books
     */
    public Page<Book> findBookByGenre(Genre genre, int pageNo) {
        return bookRepository.findByGenreOrSubgenreOrderByAddedDesc(genre, genre, PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "added")));
    }

    /**
     * Find all Books on given page, filtered by Category.
     * @param category      Category
     * @param pageNo        Page number
     * @return              Page of Books
     */
    public Page<Book> findBookByCategory(Category category, int pageNo) {
        return bookRepository.findByCategoryOrderByAddedDesc(category, PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "added")));
    }

    /**
     * Find all Books on given page, filtered by Genre and Category.
     * @param genre         Genre
     * @param category      Category
     * @param pageNo        Page number
     * @return              Page of Books
     */
    public Page<Book> findBookByGenreAndCategory(Genre genre, Category category, int pageNo) {
        return bookRepository.findByGenreOrSubgenreAndCategoryOrderByAddedDesc(genre, genre, category, PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "added")));
    }

    /**
     * Delete Book.
     * @param id            Book ID
     */
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    /**
     * Find most recently added Books.
     * @param numBooks      Number of Books
     * @return              List of Books
     */
    public List<Book> findNewestBooks(int numBooks) {
        return bookRepository.findNewestBooks(PageRequest.of(0, numBooks)).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Validate image file upload.
     * @param file      File
     * @return          Empty string if valid, error string if invalid
     */
    public String validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "No file selected.";
        }
        if (file.getSize() > 10 * 1024 * 1024) {
            return "File must be smaller than 10MB.";
        }
        String extension = Objects.requireNonNull(file.getContentType()).split("/")[1];
        List<String> allowed = Arrays.asList("png", "jpg", "jpeg");
        if (!allowed.contains(extension)) {
            return "File type must be PNG or JPG.";
        }
        return "";
    }

    /**
     * Toggle complete/incomplete boolean on given Book.
     * @param book          Book
     */
    public void toggleComplete(Book book) {
        book.setComplete(!book.getComplete());
        bookRepository.save(book);
    }

}
