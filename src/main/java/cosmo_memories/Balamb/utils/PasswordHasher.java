package cosmo_memories.Balamb.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHasher {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "Abc123!!";
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println(encodedPassword);
    }
}