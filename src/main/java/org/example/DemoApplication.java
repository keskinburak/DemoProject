package org.example;

import org.example.config.security.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(RsaKeyProperties.class)
@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}


//	@Bean
//	CommandLineRunner commandLineRunner(UserService userService){
//		return args -> {
//			userService.saveRole(new Role(null, "ROLE_MANAGER"));
//			userService.saveRole(new Role(null, "ROLE_USER"));
//			userService.saveRole(new Role(null, "ROLE_ADMIN"));
//			userService.saveRole(new Role(null, "ROLE_SUPER_ADMIN"));
//
//			userService.createUser(new User(null, "burak", "keskin", "1234", "burakuser2", null, null));
//			userService.createUser(new User(null, "merve", "altın keskin", "1234", "merveuser", null, null));
//			userService.createUser(new User(null, "burak", "keskin", "1234", "burakuser3", null, null));
//
//			userService.addRoleToUser("burakuser2", "ROLE_SUPER_ADMIN");
//			userService.addRoleToUser("burakuser2", "ROLE_ADMIN");
//			userService.addRoleToUser("burakuser2", "ROLE_USER");
//
//			userService.addRoleToUser("merveuser", "ROLE_MANAGER");
//
//			userService.addRoleToUser("burakuser3", "ROLE_USER");
//		};
//	}

}
