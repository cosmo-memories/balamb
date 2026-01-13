package cosmo_memories.Balamb.model.items;

import cosmo_memories.Balamb.model.enums.Category;
import cosmo_memories.Balamb.model.enums.Genre;
import cosmo_memories.Balamb.utils.YearRange;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO for Book entities.
 */
public class BookDTO {

    @NotBlank(message = "Title cannot be blank.")
    @Size(min=1, max=60, message = "Title must be between 1 and 60 characters.")
    private String title;

    @Size(max=30, message = "Publisher cannot be more than 30 characters.")
    private String publisher;

    @YearRange
    private String pubYear;

    @Pattern(
            regexp = "^$|^(?:[0-9A-Za-z]-?){10}$|^(?:[0-9A-Za-z]-?){13}$",
            message = "ISBNs should be either 10 or 13 characters (excluding hyphens)."
    )
    private String isbn;

    private Genre genre;
    private Genre subgenre;
    private Category category;

    @Size(max=500, message = "Note cannot be more than 500 characters.")
    private String note;

    @Size(max=30, message = "Series cannot be more than 30 characters.")
    private String series;

    @NotNull(message = "At least one author is required.")
    @Size(min = 1, message = "At least one author is required.")
    @Valid
    private List<@Pattern(regexp = ".+,\\s*.+", message = "Names must be in the form 'Lastname, Firstname'.") String> authors = new ArrayList<>();

    public BookDTO() {}

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

    public Genre getSubgenre() {
        return subgenre;
    }

    public void setSubgenre(Genre subgenre) {
        this.subgenre = subgenre;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }
}
