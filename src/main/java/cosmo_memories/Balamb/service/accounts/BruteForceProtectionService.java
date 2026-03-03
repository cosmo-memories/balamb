package cosmo_memories.Balamb.service.accounts;

import cosmo_memories.Balamb.controller.AdminController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.Map;

/**
 * https://www.geeksforgeeks.org/advance-java/prevent-brute-force-authentication-attempts-with-spring-security/
 */
@Service
public class BruteForceProtectionService {

    private static final Logger logger = LoggerFactory.getLogger(BruteForceProtectionService.class);

    private static final int MAX_ATTEMPT = 6;
    private static final long LOCK_TIME = TimeUnit.MINUTES.toMillis(15);

    private final Map<String, Integer> attemptsCache = new ConcurrentHashMap<>();
    private final Map<String, Long> lockCache = new ConcurrentHashMap<>();

    public void loginSucceeded(String key) {
        attemptsCache.remove(key); // Clear failed attempts on successful login
        lockCache.remove(key); // Unlock user on successful login
        logger.info("Login succeeded for {}", key);
    }

    public void loginFailed(String key) {
        int attempts = attemptsCache.getOrDefault(key, 0);
        attempts++;
        attemptsCache.put(key, attempts);
        logger.info("Login failed for {}", key);
        if (attempts >= MAX_ATTEMPT) {
            logger.info("{} locked", key);
            lockCache.put(key, System.currentTimeMillis()); // Lock user if max attempts exceeded
        }
    }

    public boolean isBlocked(String key) {
        if (!lockCache.containsKey(key)) {
            return false;
        }

        long lockTime = lockCache.get(key);
        if (System.currentTimeMillis() - lockTime > LOCK_TIME) {
            lockCache.remove(key); // Remove lock if lock time has expired
            return false;
        }

        return true; // User is still locked
    }
}