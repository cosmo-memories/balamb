package cosmo_memories.Balamb.service.books;

import cosmo_memories.Balamb.model.items.Author;
import org.springframework.stereotype.Service;

@Service
public class AuthorService {

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

}
