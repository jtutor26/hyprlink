package com.basecamp.HyprLink.service;

import com.basecamp.HyprLink.entity.SocialLink;
import com.basecamp.HyprLink.entity.User;
import com.basecamp.HyprLink.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User prepareRegistrationFormData() {
        User user = new User();
        List<SocialLink> initialLinks = new ArrayList<>();
        initialLinks.add(new SocialLink());
        user.setSocialLinks(initialLinks);
        return user;
    }

    //This method can be expanded to fetch themes from the database in the future
    public List<String> getAvailableThemes() {
        return List.of("default");
    }
}

