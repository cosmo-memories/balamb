package cosmo_memories.Balamb.model.items;

/**
 * DTO for Author entities.
 */
public class AuthorDTO {

    private String firstName;
    private String lastName;

    public AuthorDTO() {}

    public AuthorDTO(String fullName) {
        String[] names = fullName.split(",");
        this.firstName = names[1].trim();
        this.lastName = names[0].trim();
    }

    public Author mapToAuthor() {
        Author author = new Author();
        author.setFirstName(this.firstName);
        author.setLastName(this.lastName);
        return author;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
