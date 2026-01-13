package cosmo_memories.Balamb.repository.books;

import cosmo_memories.Balamb.model.items.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Author entities.
 */
@Repository
public interface AuthorRepository extends JpaRepository<Author, Long>, PagingAndSortingRepository<Author, Long> {

    Optional<Author> findByFirstNameAndLastName(String firstName, String lastName);

}
