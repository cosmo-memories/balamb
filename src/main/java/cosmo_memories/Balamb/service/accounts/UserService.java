package cosmo_memories.Balamb.service.accounts;

import cosmo_memories.Balamb.model.accounts.LibraryUser;
import cosmo_memories.Balamb.model.accounts.LibraryUserDetails;
import cosmo_memories.Balamb.repository.accounts.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service for User-related functions.
 */
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Save user. [for testing purposes]
     * @param user      LibraryUser
     * @return          LibraryUser
     */
    public LibraryUser save(LibraryUser user) {
        return userRepository.save(user);
    }

    /**
     * Find User by ID
     * @param id        User ID
     * @return          Optional containing User if found
     */
    public Optional<LibraryUser> findUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Find User by "username" (email)
     * @param email                         Email
     * @return                              LibraryUserDetails for User
     * @throws UsernameNotFoundException    Thrown if User not found
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LibraryUser libraryUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new LibraryUserDetails(libraryUser);
    }

}
