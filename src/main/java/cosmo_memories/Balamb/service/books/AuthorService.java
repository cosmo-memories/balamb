package cosmo_memories.Balamb.service.books;

import cosmo_memories.Balamb.model.items.Author;
import cosmo_memories.Balamb.repository.books.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service method for Author-related functions.
 */
@Service
public class AuthorService {

    @Autowired
    AuthorRepository authorRepository;

    public AuthorService() {}

    /**
     * Validate Author names.
     * @param fullName      Full name in form "Last, First"
     * @return              Boolean
     */
    public boolean validateAuthor(String fullName) {
        boolean valid = true;
        if (!fullName.contains(",")) {
            valid = false;
        } else {
            String[] names = fullName.split(",");
            if (names.length != 2 || names[1].trim().isEmpty() || names[0].trim().isEmpty()) {
                valid = false;
            }
        }
        return valid;
    }

    /**
     * Find Author by first and last name.
     * @param firstName     First name
     * @param lastName      Last name
     * @return              Optional containing Author if found
     */
    public Optional<Author> findByFullName(String firstName, String lastName) {
        return authorRepository.findByFirstNameAndLastName(firstName, lastName);
    }

}
