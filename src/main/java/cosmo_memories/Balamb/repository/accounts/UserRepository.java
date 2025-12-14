package cosmo_memories.Balamb.repository.accounts;

import cosmo_memories.Balamb.model.accounts.LibraryUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<LibraryUser, Long> {

    Optional<LibraryUser> findByEmail(String email);

}
