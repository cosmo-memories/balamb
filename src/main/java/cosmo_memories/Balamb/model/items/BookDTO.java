package cosmo_memories.Balamb.model.items;

import cosmo_memories.Balamb.model.enums.Category;
import cosmo_memories.Balamb.model.enums.Genre;
import cosmo_memories.Balamb.utils.YearRange;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class BookDTO {

    @NotBlank(message = "Title cannot be blank.")
    @Size(min=1, max=30, message = "Title must be between 1 and 30 characters.")
    private String title;

    @Size(max=30)
    private String publisher;

    @YearRange
    private String pubYear;

    @Pattern(
            regexp = "^$|^(?:[0-9A-Za-z]-?){10}$|^(?:[0-9A-Za-z]-?){13}$",
            message = "ISBNs should be either 10 or 13 characters (excluding hyphens)."
    )
    private String isbn;

    private Genre genre;
    private Category category;

    @NotNull(message = "At least one author is required.")
    @Size(min = 1, message = "At least one author is required.")
    @Valid
    private List<@Pattern(regexp = ".+,\\s*.+", message = "Names must be in the form 'Lastname, Firstname'.") String> authors = new ArrayList<>();

    public BookDTO() {}

    public Book mapToBook() {
        Book book = new Book();
        book.setTitle(title);
        if (publisher != null && !publisher.isBlank()) {
            book.setPublisher(publisher);
        }
        if (pubYear != null && !pubYear.isBlank()) {
            try {
                book.setPubYear(Year.of(Integer.parseInt(pubYear)));
            } catch (NumberFormatException e) {
                book.setPubYear(null);
            }
        }
        if (this.isbn != null && !isbn.isBlank()) {
            book.setIsbn(isbn);
        }
        book.setGenre(genre);
        book.setCategory(category);
        if (authors != null && !authors.isEmpty()) {
            for (String author : authors) {
                String[] names = author.split(",");
                book.addAuthor(new Author(names[1].trim(), names[0].trim()));
            }
        }
        book.setAdded(LocalDateTime.now());
        return book;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPubYear() {
        return pubYear;
    }

    public void setPubYear(String pubYear) {
        this.pubYear = pubYear;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }
}
