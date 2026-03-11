package com.basecamp.springunitproject.config;

import com.basecamp.springunitproject.entity.SocialLink;
import com.basecamp.springunitproject.entity.User;
import com.basecamp.springunitproject.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository) {
        return args -> {
            // Check if data already exists so we don't duplicate it on every restart
            if (userRepository.count() == 0) {

                // ==========================================
                // PROFILE 1: The Developer (Uses Default Theme)
                // ==========================================
                User user1 = new User();
                user1.setName("Jonathan Tutor");
                user1.setAge("19");
                user1.setPronouns("he/him");
                user1.setBio("Software Developer & BCCA Student. Building cool things with Spring Boot, Python, and JavaScript.");
                user1.setProfilePicture("https://api.dicebear.com/7.x/avataaars/svg?seed=Jonathan");
                user1.setTheme("default");

                SocialLink link1 = new SocialLink();
                link1.setTitle("GitHub");
                link1.setUrl("https://github.com/jttutor10");

                SocialLink link2 = new SocialLink();
                link2.setTitle("LinkedIn");
                link2.setUrl("https://linkedin.com/");

                // Attach links to user1
                user1.setSocialLinks(List.of(link1, link2));


                // ==========================================
                // PROFILE 2: The Gamer (Uses Dark Theme)
                // ==========================================
                User user2 = new User();
                user2.setName("Socioside");
                user2.setAge("Unknown");
                user2.setPronouns("he/him");
                user2.setBio("Currently surviving the extraction zone. Big fan of Metal Gear Solid, Resident Evil, and FromSoftware titles.");
                user2.setProfilePicture("https://api.dicebear.com/7.x/bottts/svg?seed=Snake");
                user2.setTheme("dark");

                SocialLink link3 = new SocialLink();
                link3.setTitle("PlayStation Network");
                link3.setUrl("https://my.playstation.com/");

                SocialLink link4 = new SocialLink();
                link4.setTitle("Steam Profile");
                link4.setUrl("https://steamcommunity.com/");

                SocialLink link5 = new SocialLink();
                link5.setTitle("Currently Listening: Radiohead");
                link5.setUrl("https://spotify.com/");

                // Attach links to user2
                user2.setSocialLinks(List.of(link3, link4, link5));


                // ==========================================
                // SAVE TO DATABASE
                // ==========================================
                // Saving the users automatically saves their associated links!
                userRepository.saveAll(List.of(user1, user2));

                System.out.println("✅ Dummy Data Loaded Successfully!");
            }
        };
    }
}