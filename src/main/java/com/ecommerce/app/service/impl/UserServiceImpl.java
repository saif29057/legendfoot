package com.ecommerce.app.service.impl;

import com.ecommerce.app.entity.User;
import com.ecommerce.app.dto.UserDto;
import com.ecommerce.app.repository.UserRepository;
import com.ecommerce.app.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Implementation of UserService interface.
 * 
 * This class follows SOLID principles:
 * - Single Responsibility: Handles only user-related business logic
 * - Open/Closed: Open for extension through interfaces, closed for modification
 * - Liskov Substitution: Can be substituted with any UserService implementation
 * - Interface Segregation: Implements only methods needed for user operations
 * - Dependency Inversion: Depends on UserService interface, not concrete
 * classes
 * 
 * The class uses constructor injection for dependency management
 * and follows best practices for error handling and logging.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates a new user in the system.
     * 
     * This method validates user data, encrypts the password,
     * and saves the user to the database. It follows
     * Single Responsibility by focusing only on user creation.
     * 
     * @param user user to create (with plain password)
     * @return created user (with encrypted password)
     * @throws IllegalArgumentException if user data is invalid
     * @throws RuntimeException         if username or email already exists
     */
    @Override
    public User createUser(User user) {
        log.info("Creating new user: {}", user.getUsername());

        // Validate user data
        if (!validateUserData(user)) {
            throw new IllegalArgumentException("Invalid user data provided");
        }

        // Check if username already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists: " + user.getUsername());
        }

        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }

        // Encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Set default values
        user.setRole(User.Role.USER);
        user.setEnabled(true);

        User savedUser = userRepository.save(user);
        log.info("Successfully created user with ID: {}", savedUser.getId());

        return savedUser;
    }

    /**
     * Updates an existing user.
     * 
     * This method validates the updated data and preserves
     * sensitive fields like password and role if not provided.
     * 
     * @param id   ID of user to update
     * @param user updated user data
     * @return updated user
     * @throws IllegalArgumentException if user data is invalid
     * @throws RuntimeException         if user not found
     */
    @Override
    public User updateUser(Long id, User user) {
        log.info("Updating user with ID: {}", id);

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        // Validate user data
        if (!validateUserData(user)) {
            throw new IllegalArgumentException("Invalid user data provided");
        }

        // Check if username is being changed and if it's already taken
        if (!existingUser.getUsername().equals(user.getUsername()) &&
                userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists: " + user.getUsername());
        }

        // Check if email is being changed and if it's already taken
        if (!existingUser.getEmail().equals(user.getEmail()) &&
                userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }

        // Update fields
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());

        // Only update password if provided
        if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        User updatedUser = userRepository.save(existingUser);
        log.info("Successfully updated user with ID: {}", updatedUser.getId());

        return updatedUser;
    }

    /**
     * Updates an existing user using DTO.
     * 
     * This method handles user profile updates from DTO while maintaining
     * data integrity and business rules.
     * 
     * @param id      the ID of the user to update
     * @param userDto the updated user data as DTO
     * @return the updated user
     * @throws IllegalArgumentException if user data is invalid
     * @throws RuntimeException         if user not found
     */
    @Override
    public User updateUser(Long id, UserDto userDto) {
        log.info("Updating user with ID: {} using DTO", id);

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        // Check if username is being changed and if it's already taken
        if (userDto.getUsername() != null &&
                !existingUser.getUsername().equals(userDto.getUsername()) &&
                userRepository.existsByUsername(userDto.getUsername())) {
            throw new RuntimeException("Username already exists: " + userDto.getUsername());
        }

        // Check if email is being changed and if it's already taken
        if (userDto.getEmail() != null &&
                !existingUser.getEmail().equals(userDto.getEmail()) &&
                userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("Email already exists: " + userDto.getEmail());
        }

        // Update fields if they are provided in DTO
        if (userDto.getUsername() != null) {
            existingUser.setUsername(userDto.getUsername());
        }
        if (userDto.getEmail() != null) {
            existingUser.setEmail(userDto.getEmail());
        }

        // Only update password if provided
        if (userDto.getPassword() != null && !userDto.getPassword().trim().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        // Update role if provided
        if (userDto.getRole() != null) {
            existingUser.setRole(User.Role.valueOf(userDto.getRole()));
        }

        // Update enabled status if provided
        if (userDto.getEnabled() != existingUser.isEnabled()) {
            existingUser.setEnabled(userDto.getEnabled());
        }

        User updatedUser = userRepository.save(existingUser);
        log.info("Successfully updated user with ID: {} using DTO", updatedUser.getId());

        return updatedUser;
    }

    /**
     * Deletes a user by their ID.
     * 
     * This method performs soft deletion by disabling the user
     * account rather than hard deletion to maintain data integrity.
     * 
     * @param id ID of user to delete
     * @throws RuntimeException if user not found
     */
    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        // Soft delete by disabling account
        user.setEnabled(false);
        userRepository.save(user);

        log.info("Successfully deleted (disabled) user with ID: {}", id);
    }

    /**
     * Retrieves a user by their ID.
     * 
     * @param id ID of user to retrieve
     * @return Optional containing user if found, empty otherwise
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        log.debug("Retrieving user with ID: {}", id);
        return userRepository.findById(id);
    }

    /**
     * Retrieves a user by their username.
     * 
     * @param username username of user to retrieve
     * @return Optional containing user if found, empty otherwise
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        log.debug("Retrieving user with username: {}", username);
        return userRepository.findByUsername(username);
    }

    /**
     * Retrieves a user by their email address.
     * 
     * @param email email address of user to retrieve
     * @return Optional containing user if found, empty otherwise
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        log.debug("Retrieving user with email: {}", email);
        return userRepository.findByEmail(email);
    }

    /**
     * Retrieves all users with pagination support.
     * 
     * @param pageable pagination information
     * @return Page of users
     */
    @Override
    @Transactional(readOnly = true)
    public Page<User> getAllUsers(Pageable pageable) {
        log.debug("Retrieving all users with pagination: {}", pageable);
        return userRepository.findAll(pageable);
    }

    /**
     * Retrieves all users with a specific role.
     * 
     * @param role role to filter by
     * @return List of users with specified role
     */
    @Override
    @Transactional(readOnly = true)
    public List<User> getUsersByRole(User.Role role) {
        log.debug("Retrieving users with role: {}", role);
        return StreamSupport.stream(userRepository.findByRole(role).spliterator(), false).toList();
    }

    /**
     * Checks if a username is available for registration.
     * 
     * @param username username to check
     * @return true if available, false if already taken
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isUsernameAvailable(String username) {
        log.debug("Checking username availability: {}", username);
        return !userRepository.existsByUsername(username);
    }

    /**
     * Checks if an email address is available for registration.
     * 
     * @param email email address to check
     * @return true if available, false if already taken
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email) {
        log.debug("Checking email availability: {}", email);
        return !userRepository.existsByEmail(email);
    }

    /**
     * Changes a user's password.
     * 
     * This method validates the current password before
     * allowing the password change to proceed.
     * 
     * @param userId          ID of user
     * @param currentPassword current password for verification
     * @param newPassword     new password to set
     * @return true if password was changed successfully
     * @throws IllegalArgumentException if passwords are invalid
     * @throws RuntimeException         if current password doesn't match
     */
    @Override
    public boolean changePassword(Long userId, String currentPassword, String newPassword) {
        log.info("Changing password for user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Validate passwords
        if (currentPassword == null || currentPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Current password is required");
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("New password is required");
        }

        if (newPassword.length() < 6) {
            throw new IllegalArgumentException("New password must be at least 6 characters");
        }

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("Successfully changed password for user with ID: {}", userId);
        return true;
    }

    /**
     * Enables or disables a user account.
     * 
     * @param userId  ID of user
     * @param enabled true to enable, false to disable
     * @return updated user
     * @throws RuntimeException if user not found
     */
    @Override
    public User setUserEnabled(Long userId, boolean enabled) {
        log.info("{} user with ID: {}", enabled ? "Enabling" : "Disabling", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        user.setEnabled(enabled);
        User updatedUser = userRepository.save(user);

        log.info("Successfully {} user with ID: {}", enabled ? "enabled" : "disabled", userId);
        return updatedUser;
    }

    /**
     * Changes a user's role.
     * 
     * @param userId ID of user
     * @param role   new role to assign
     * @return updated user
     * @throws RuntimeException if user not found
     */
    @Override
    public User changeUserRole(Long userId, User.Role role) {
        log.info("Changing role for user with ID: {} to {}", userId, role);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        user.setRole(role);
        User updatedUser = userRepository.save(user);

        log.info("Successfully changed role for user with ID: {} to {}", userId, role);
        return updatedUser;
    }

    /**
     * Searches for users by username or email.
     * 
     * @param keyword search keyword
     * @return List of users matching search criteria
     */
    @Override
    @Transactional(readOnly = true)
    public List<User> searchUsers(String keyword) {
        log.debug("Searching users with keyword: {}", keyword);
        return StreamSupport.stream(userRepository.findByUsernameContainingIgnoreCase(keyword).spliterator(), false)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves total number of users in the system.
     * 
     * @return total count of users
     */
    @Override
    @Transactional(readOnly = true)
    public long getTotalUserCount() {
        log.debug("Getting total user count");
        return userRepository.count();
    }

    /**
     * Retrieves number of users with a specific role.
     * 
     * @param role role to count
     * @return count of users with specified role
     */
    @Override
    @Transactional(readOnly = true)
    public long getUserCountByRole(User.Role role) {
        log.debug("Getting user count for role: {}", role);
        return userRepository.countByRole(role);
    }

    /**
     * Retrieves number of enabled users.
     * 
     * @return count of enabled user accounts
     */
    @Override
    @Transactional(readOnly = true)
    public long getEnabledUserCount() {
        log.debug("Getting enabled user count");
        return userRepository.countEnabledUsers();
    }

    /**
     * Validates user data for registration or update.
     * 
     * This method performs comprehensive validation according to
     * business rules and constraints.
     * 
     * @param user user to validate
     * @return true if valid, false otherwise
     */
    @Override
    public boolean validateUserData(User user) {
        if (user == null) {
            return false;
        }

        // Validate username
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            log.warn("Username validation failed: null or empty");
            return false;
        }

        if (user.getUsername().length() < 3 || user.getUsername().length() > 50) {
            log.warn("Username validation failed: invalid length");
            return false;
        }

        // Validate email presence only here.
        // The web layer already applies format validation via @Email,
        // so duplicating a stricter regex here causes inconsistent failures.
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            log.warn("Email validation failed: null or empty");
            return false;
        }

        // Validate password (for new users)
        if (user.getPassword() != null && user.getPassword().length() < 6) {
            log.warn("Password validation failed: too short");
            return false;
        }

        return true;
    }

    /**
     * Authenticates a user with username/email and password.
     * 
     * This method should only be used by authentication mechanisms
     * and not directly by business logic.
     * 
     * @param usernameOrEmail username or email of user
     * @param password        password to verify
     * @return Optional containing authenticated user if successful
     * @throws RuntimeException if authentication fails
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<User> authenticateUser(String usernameOrEmail, String password) {
        log.debug("Authenticating user: {}", usernameOrEmail);

        Optional<User> userOpt = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);

        if (userOpt.isEmpty()) {
            log.warn("Authentication failed: user not found");
            return Optional.empty();
        }

        User user = userOpt.get();

        if (!user.isEnabled()) {
            log.warn("Authentication failed: user is disabled");
            return Optional.empty();
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("Authentication failed: incorrect password");
            return Optional.empty();
        }

        log.debug("Authentication successful for user: {}", user.getUsername());
        return Optional.of(user);
    }
}
