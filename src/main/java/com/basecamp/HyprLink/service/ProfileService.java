package com.basecamp.HyprLink.service;

import com.basecamp.HyprLink.entity.User;
import com.basecamp.HyprLink.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    private final UserRepository userRepository;

    public ProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserProfileById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getUserProfileByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}

