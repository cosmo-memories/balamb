package cosmo_memories.Balamb.config;

import cosmo_memories.Balamb.model.accounts.LibraryUser;
import cosmo_memories.Balamb.model.accounts.LibraryUserDetails;
import cosmo_memories.Balamb.service.accounts.BruteForceProtectionService;
import cosmo_memories.Balamb.service.accounts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * https://www.geeksforgeeks.org/advance-java/prevent-brute-force-authentication-attempts-with-spring-security/
 */
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private BruteForceProtectionService bruteForceProtectionService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        // Check if the user is blocked due to too many failed login attempts
        if (bruteForceProtectionService.isBlocked(username)) {
            throw new BadCredentialsException("You have been temporarily locked due to too many failed login attempts.");
        }

        LibraryUserDetails details = (LibraryUserDetails) userDetailsService.loadUserByUsername(username);
        Optional<LibraryUser> user = userDetailsService.findUserById(details.getId());

        // Verify user credentials
        if (user.isEmpty() || !passwordEncoder.matches(password, user.get().getPassword())) {
            bruteForceProtectionService.loginFailed(username); // Record failed login attempt
            throw new BadCredentialsException("Invalid username or password.");
        }

        bruteForceProtectionService.loginSucceeded(username); // Record successful login
        return new UsernamePasswordAuthenticationToken(details, password, details.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}