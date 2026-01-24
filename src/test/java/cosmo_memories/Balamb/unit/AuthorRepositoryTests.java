package cosmo_memories.Balamb.unit;

import cosmo_memories.Balamb.model.items.Author;
import cosmo_memories.Balamb.repository.books.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AuthorRepositoryTests {

    @Autowired
    AuthorRepository authorRepository;

    @BeforeEach
    public void setUp() {
        Author author = new Author("Test", "Author");
        authorRepository.save(author);
    }

    @Test
    public void searchAuthorByFirstAndLastName_AuthorFound() {
        Optional<Author> result = authorRepository.findByFirstNameAndLastName("Test", "Author");
        assertTrue(result.isPresent());
        assertEquals("Test Author", result.get().getFullName());
    }

    @Test
    public void searchAuthorByFirstAndLastName_NoneFound() {
        Optional<Author> result = authorRepository.findByFirstNameAndLastName("Another", "Author");
        assertTrue(result.isEmpty());
    }

}
