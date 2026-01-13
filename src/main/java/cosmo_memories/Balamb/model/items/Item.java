package cosmo_memories.Balamb.model.items;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.Year;

/**
 * Superclass for Books and other related entities. To be extended in future with different media types.
 */
@MappedSuperclass
public abstract class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String title;

    private String publisher;
    private Year pubYear;
    private String image;
    private LocalDateTime added;
    private boolean complete;

    protected Item() {}

    public Item(String title) {
        this.title = title;
        this.complete = false;
        this.added = LocalDateTime.now();
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public Year getPubYear() {
        return pubYear;
    }

    public void setPubYear(Year pubYear) {
        this.pubYear = pubYear;
    }

    public LocalDateTime getAdded() { return added; }

    public void setAdded(LocalDateTime added) { this.added = added; }

    public String getImage() { return image; }

    public void setImage(String image) { this.image = image; }

    public boolean getComplete() { return complete; }

    public void setComplete(boolean complete) { this.complete = complete; }
}
