package cosmo_memories.Balamb.service;

import cosmo_memories.Balamb.model.items.Author;
import cosmo_memories.Balamb.service.books.AuthorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AuthorServiceTests {

    @Autowired
    private AuthorService authorService;

    private Author author;

    @BeforeEach
    public void setUp() {
        author = new Author("Richard", "Adams");
        authorService.save(author);
    }

    static Stream<String> validNames() {
        return Stream.of("Irvine, Ian",
                "Shannon, Samantha",
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                "T,T",
                "T, T",
                "T ,T");
    }

    @ParameterizedTest
    @MethodSource("validNames")
    public void validateAuthorName_PassedValidation(String name) {
        assertTrue(authorService.validateAuthor(name));
    }

    static Stream<String> invalidNames() {
        return Stream.of("Ian Irvine",
                "Samantha Shannon",
                " ",
                "",
                ",",
                " , ",
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
    }

    @ParameterizedTest
    @MethodSource("invalidNames")
    public void validateAuthorName_FailedValidation(String name) {
        assertFalse(authorService.validateAuthor(name));
    }

    @Test
    public void findByName_NoneFound() {
        Optional<Author> result = authorService.findByFullName("Test", "Author");
        assertTrue(result.isEmpty());
    }

    @Test
    public void findByName_Found() {
        Optional<Author> result = authorService.findByFullName("Richard", "Adams");
        assertTrue(result.isPresent());
        assertEquals("Richard Adams", result.get().getFullName());
        assertEquals("Adams, Richard", result.get().getCommaSeparatedFullName());
    }

}
