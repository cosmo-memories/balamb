package cosmo_memories.Balamb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BalambApplication {

	public static void main(String[] args) {
		SpringApplication.run(BalambApplication.class, args);
	}

}
