package cosmo_memories.Balamb.service.books;

import cosmo_memories.Balamb.model.items.Author;
import cosmo_memories.Balamb.repository.books.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthorService {

    @Autowired
    AuthorRepository authorRepository;

    public AuthorService() {}

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

    public Optional<Author> findByFullName(String firstName, String lastName) {
        return authorRepository.findByFirstNameAndLastName(firstName, lastName);
    }

}
