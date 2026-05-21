package com.automart.service;

import com.automart.model.User;
import com.automart.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /** Toggle user ban status */
    public void banUser(Long id) {
        User user = getUserById(id);
        if (user != null) {
            user.setBanned(true);
            userRepository.save(user);
        }
    }

    /** Unban a user */
    public void unbanUser(Long id) {
        User user = getUserById(id);
        if (user != null) {
            user.setBanned(false);
            userRepository.save(user);
        }
    }

    /** Change password — returns false if currentPassword doesn't match */
    public boolean changePassword(Long id, String currentPassword, String newPassword) {
        User user = getUserById(id);
        if (user != null && user.getPassword().equals(currentPassword)) {
            user.setPassword(newPassword);
            userRepository.save(user);
            return true;
        }
        return false;
    }
}
