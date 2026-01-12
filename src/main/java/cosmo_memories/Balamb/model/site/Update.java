package cosmo_memories.Balamb.model.site;

import cosmo_memories.Balamb.model.accounts.LibraryUser;
import cosmo_memories.Balamb.model.enums.UpdateType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
public class Update {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private LocalDateTime created;
    private LocalDateTime edited;
    private boolean resolved;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private LibraryUser author;

    @Size(min=1, max=1000)
    private String description;

    private UpdateType updateType;

    public Update() {}

    public Update(String description, UpdateType type) {
        this.created = LocalDateTime.now();
        this.edited = LocalDateTime.now();
        this.description = description;
        this.updateType = type;
        this.resolved = false;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getEdited() {
        return edited;
    }

    public void setEdited(LocalDateTime edited) {
        this.edited = edited;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UpdateType getUpdateType() {
        return updateType;
    }

    public void setUpdateType(UpdateType updateType) {
        this.updateType = updateType;
    }

    public LibraryUser getAuthor() { return author; }

    public void setAuthor(LibraryUser author) { this.author = author; }

    public boolean getResolved() { return resolved; }

    public void setResolved(boolean resolved) { this.resolved = resolved; }
}
