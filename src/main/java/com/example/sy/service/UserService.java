package com.example.sy.service;

import com.example.sy.model.User;
import com.example.sy.repository.UserRepository;
import com.example.sy.util.SimpleEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public Optional<User> findByResetToken(String token) {
        return userRepository.findByResetToken(token);
    }

    public User saveUser(User user) {
        if (user.getId() == null) {
            user.setPassword(SimpleEncoder.encode(user.getPassword()));
            User savedUser = userRepository.save(user);
            emailService.sendAccountActivationEmail(savedUser);
            return savedUser;
        } else {
            User existingUser = userRepository.findById(user.getId()).orElse(null);
            if (existingUser != null && !user.getPassword().equals(existingUser.getPassword())) {
                user.setPassword(SimpleEncoder.encode(user.getPassword()));
            }
            return userRepository.save(user);
        }
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User registerUser(User user) throws IllegalArgumentException {
        validateUser(user);

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        return saveUser(user);
    }

    public User authenticate(String usernameOrEmail, String password) throws IllegalArgumentException {
        // Determine if input is email or username
        boolean isEmail = usernameOrEmail.contains("@");

        Optional<User> user;
        if (isEmail) {
            // Search by email
            user = userRepository.findByEmail(usernameOrEmail);
            if (!user.isPresent()) {
                throw new IllegalArgumentException("Email not found");
            }
        } else {
            // Search by username
            user = userRepository.findByUsername(usernameOrEmail);
            if (!user.isPresent()) {
                throw new IllegalArgumentException("Username not found");
            }
        }

        User foundUser = user.get();

        // Verify password
        if (!SimpleEncoder.verify(password, foundUser.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        // Check if account is activated
        if (!foundUser.isEnabled()) {
            throw new IllegalArgumentException("Account not activated. Please check your email.");
        }

        return foundUser;
    }

    public boolean activateUser(String activationCode) {
        Optional<User> userOptional = userRepository.findByActivationCode(activationCode);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setEnabled(true);
            user.setActivationCode(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public String generatePasswordResetToken(String email) {
        User user = getUserByEmail(email);
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        userRepository.save(user);
        return token;
    }

    public void resetPassword(String token, String newPassword) {
        System.out.println("Resetting password with token: " + token);
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> {
                    System.out.println("Invalid token: " + token);
                    return new IllegalArgumentException("Invalid or expired token");
                });

        System.out.println("Resetting password for user: " + user.getUsername());
        user.setPassword(SimpleEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setEnabled(true); // Activate account on password reset
        userRepository.save(user);
        System.out.println("Password reset complete for: " + user.getUsername());
    }

    private void validateUser(User user) throws IllegalArgumentException {
        if (!StringUtils.hasText(user.getUsername())) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (user.getUsername().length() < 4) {
            throw new IllegalArgumentException("Username too short (min 4 chars)");
        }
        if (!StringUtils.hasText(user.getEmail()) || !user.getEmail().contains("@")) {
            throw new IllegalArgumentException("Invalid email address");
        }
        if (!StringUtils.hasText(user.getPassword())) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (user.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
    }
    public User registerAdmin(User user) throws IllegalArgumentException {
        validateUser(user);

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        user.setRole(User.ROLE_ADMIN);
        return saveUser(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}