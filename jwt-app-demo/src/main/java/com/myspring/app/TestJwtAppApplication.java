package com.myspring.app;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * Spring Security with JWT
 * Author Munkush-
 */
@SpringBootApplication
@RequiredArgsConstructor
public class TestJwtAppApplication {
	
//	private final PasswordEncoder passwordEncoder;
//	private final UserController userRepository;

	public static void main(String[] args) {
		SpringApplication.run(TestJwtAppApplication.class, args);
	}


//    @Bean
//    public CommandLineRunner lineRunner(){
//        return args -> {
//
//            User testUser = User.builder()
//                    .username("example")
//                    .password(passwordEncoder.encode("asd"))
//                    .email("so678ail@gmail.com")
//                    .roles(Set.of(new Role("ADMIN"), new Role("USER")))
//                    .build();
//
//
//
//            userRepository.save(testUser);
//
//            User testUser2 = User.builder()
//                    .username("qwe")
//                    .password(passwordEncoder.encode("asd"))
//                    .email("some678ail@gmail.com")
//                    .roles(Set.of(new Role("ADMIN"), new Role("USER")))
//                    .build();
//
//
//
//            userRepository.save(testUser2);
//
//            User testUser3 = User.builder()
//                    .username("asd")
//                    .password(passwordEncoder.encode("asd"))
//                    .email("som678il@gmail.com")
//                    .roles(Set.of(new Role("ADMIN"), new Role("USER")))
//                    .build();
//
//
//            userRepository.save(testUser3);
//
//        };
//	}

}
