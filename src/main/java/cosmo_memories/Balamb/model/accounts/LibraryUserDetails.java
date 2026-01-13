package cosmo_memories.Balamb.model.accounts;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * UserDetails implementation for LibraryUsers.
 */
public class LibraryUserDetails implements UserDetails {

    private final LibraryUser libraryUser;

    public LibraryUserDetails(LibraryUser libraryUser) {
        this.libraryUser = libraryUser;
    }

    public String getFirstName() {
        return libraryUser.getFirstName();
    }

    public String getLastName() {
        return libraryUser.getLastName();
    }

    public Long getId() {
        return libraryUser.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return libraryUser.getUserRoles();
    }

    @Override
    public String getPassword() {
        return libraryUser.getPassword();
    }

    @Override
    public String getUsername() {
        return libraryUser.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
