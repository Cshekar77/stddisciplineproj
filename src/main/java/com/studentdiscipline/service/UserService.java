package com.studentdiscipline.service;

import com.studentdiscipline.enums.Role;
import com.studentdiscipline.model.User;
import com.studentdiscipline.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    public User createUser(String username, String rawPassword, Role role) {
        if (userRepository.existsByUsername(username))
            throw new RuntimeException("Username already exists: " + username);
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        return userRepository.save(user);
    }

    public List<User> getAllUsers() { return userRepository.findAll(); }
    public Optional<User> getUserById(Long id) { return userRepository.findById(id); }
    public Optional<User> getUserByUsername(String username) { return userRepository.findByUsername(username); }

    // Used by AdminController — searches by username (login name)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByUsername(email);
    }

    public List<User> getUsersByRole(Role role) { return userRepository.findByRole(role); }

    // ── Update password ───────────────────────────────────────────────────────
    public User updatePassword(Long id, String newRawPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setPassword(passwordEncoder.encode(newRawPassword));
        return userRepository.save(user);
    }

    // ── Update username ───────────────────────────────────────────────────────
    public User updateUsername(Long id, String newUsername) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        // Check if new username is taken by someone else
        Optional<User> existing = userRepository.findByUsername(newUsername);
        if (existing.isPresent() && !existing.get().getId().equals(id))
            throw new RuntimeException("Username already taken: " + newUsername);
        user.setUsername(newUsername);
        return userRepository.save(user);
    }

    public User setUserEnabled(Long id, boolean enabled) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setEnabled(enabled);
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id))
            throw new RuntimeException("User not found with id: " + id);
        userRepository.deleteById(id);
    }

    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }
}