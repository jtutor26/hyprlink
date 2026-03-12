package com.basecamp.LinkGarden.config;

import com.basecamp.LinkGarden.entity.SocialLink;
import com.basecamp.LinkGarden.entity.User;
import com.basecamp.LinkGarden.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Check if data already exists so we don't duplicate it on every restart
            if (userRepository.count() == 0) {

                // ==========================================
                // GENERIC PROFILE 1 (Uses Default Theme)
                // ==========================================
                User user1 = new User();
                user1.setUsername("johndoe");
                user1.setPassword(passwordEncoder.encode("password123"));
                user1.setName("John Doe");
                user1.setAge("30");
                user1.setPronouns("he/him");
                user1.setBio("Hello world! I am a generic test user. Check out my links below.");
                user1.setProfilePicture("https://api.dicebear.com/7.x/avataaars/svg?seed=John");
                user1.setTheme("default");

                SocialLink link1 = new SocialLink();
                link1.setTitle("My Personal Blog");
                link1.setUrl("https://example.com/blog");

                SocialLink link2 = new SocialLink();
                link2.setTitle("My Portfolio");
                link2.setUrl("https://example.com/portfolio");

                // Attach links to user1
                user1.setSocialLinks(List.of(link1, link2));


                // ==========================================
                // GENERIC PROFILE 2 (Uses Dark Theme)
                // ==========================================
                User user2 = new User();
                user2.setUsername("janesmith");
                user2.setPassword(passwordEncoder.encode("password123"));
                user2.setName("Jane Smith");
                user2.setAge("28");
                user2.setPronouns("she/her");
                user2.setBio("Welcome to my profile. I love designing generic templates and testing code!");
                user2.setProfilePicture("https://api.dicebear.com/7.x/avataaars/svg?seed=Jane");
                user2.setTheme("default");

                SocialLink link3 = new SocialLink();
                link3.setTitle("Social Media Profile");
                link3.setUrl("https://example.com/social");

                SocialLink link4 = new SocialLink();
                link4.setTitle("Online Store");
                link4.setUrl("https://example.com/store");

                // Attach links to user2
                user2.setSocialLinks(List.of(link3, link4));


                // ==========================================
                // SAVE TO DATABASE
                // ==========================================
                // Saving the users automatically saves their associated links!
                userRepository.saveAll(List.of(user1, user2));

                System.out.println("✅ Generic Dummy Data Loaded Successfully!");
            }
        };
    }
}