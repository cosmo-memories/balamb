package cosmo_memories.Balamb.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * https://www.baeldung.com/spring-security-block-brute-force-authentication-attempts
 */
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        if (exception instanceof BadCredentialsException) {
            String errorMessage = exception.getMessage();
            if (errorMessage.contains("temporarily locked")) {
                response.sendRedirect("/blocked");
                return;
            }
        }

        response.sendRedirect("/login?error");
    }
}