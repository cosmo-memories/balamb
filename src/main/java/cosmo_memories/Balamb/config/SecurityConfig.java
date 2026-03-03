package cosmo_memories.Balamb.config;

import cosmo_memories.Balamb.service.accounts.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomAuthenticationProvider authenticationProvider;
    private final UserService userService;
    private final CustomAuthenticationFailureHandler authenticationFailureHandler;

    public SecurityConfig(CustomAuthenticationProvider authenticationProvider, UserService userService, CustomAuthenticationFailureHandler authenticationFailureHandler) {
        this.authenticationProvider = authenticationProvider;
        this.userService = userService;
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .requestCache(cache -> cache.disable())
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                        .requestMatchers("/", "/about", "/updates", "/login", "/logout", "/blocked").permitAll()
                        .requestMatchers("/browse", "/browse/**", "/browse**").permitAll()
                        .requestMatchers("/uploads", "/uploads/**", "/uploads**").permitAll()
//                        .requestMatchers("/h2", "/h2/**", "/h2**").permitAll()
                        .requestMatchers("/admin", "/admin/**").hasAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2/**"))
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .formLogin((form) -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .failureHandler(authenticationFailureHandler)
                        .successHandler((request, response, authentication) -> {
                            response.sendRedirect("/admin");})
                        .permitAll()
                )
                .logout((logout) -> logout.permitAll())
                .authenticationProvider(authenticationProvider);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userService);
//        authProvider.setPasswordEncoder(AppConfig.passwordEncoder());
        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public RoleHierarchyImpl roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy("ROLE_ADMIN > ROLE_USER");
    }
}