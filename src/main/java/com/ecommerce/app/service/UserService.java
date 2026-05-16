package com.ecommerce.app.service;

import com.ecommerce.app.entity.User;
import com.ecommerce.app.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for User business operations.
 * 
 * This interface follows the Single Responsibility Principle by defining only
 * user-related business operations. It also follows the Interface Segregation
 * Principle by providing only methods relevant to user management.
 * 
 * The interface uses dependency inversion by allowing implementations to be
 * injected without depending on concrete classes.
 */
public interface UserService {

    /**
     * Creates a new user in the system.
     * 
     * This method handles user registration, including password encryption
     * and validation of user data. It follows the Single Responsibility
     * Principle by focusing only on user creation logic.
     * 
     * @param user the user to create (with plain password)
     * @return the created user (with encrypted password)
     * @throws IllegalArgumentException if user data is invalid
     * @throws RuntimeException if username or email already exists
     */
    User createUser(User user);

    /**
     * Updates an existing user.
     * 
     * This method handles user profile updates while maintaining
     * data integrity and business rules.
     * 
     * @param id       the ID of the user to update
     * @param user     the updated user data
     * @return the updated user
     * @throws IllegalArgumentException if user data is invalid
     * @throws RuntimeException if user not found
     */
    User updateUser(Long id, User user);

    /**
     * Updates an existing user using DTO.
     * 
     * This method handles user profile updates from DTO while maintaining
     * data integrity and business rules.
     * 
     * @param id       the ID of the user to update
     * @param userDto  the updated user data as DTO
     * @return the updated user
     * @throws IllegalArgumentException if user data is invalid
     * @throws RuntimeException if user not found
     */
    User updateUser(Long id, UserDto userDto);

    /**
     * Deletes a user by their ID.
     * 
     * This method handles user deletion with proper cleanup
     * of related data and maintaining referential integrity.
     * 
     * @param id the ID of the user to delete
     * @throws RuntimeException if user not found
     */
    void deleteUser(Long id);

    /**
     * Retrieves a user by their ID.
     * 
     * @param id the ID of the user to retrieve
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> getUserById(Long id);

    /**
     * Retrieves a user by their username.
     * 
     * @param username the username of the user to retrieve
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> getUserByUsername(String username);

    /**
     * Retrieves a user by their email address.
     * 
     * @param email the email address of the user to retrieve
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> getUserByEmail(String email);

    /**
     * Retrieves all users with pagination support.
     * 
     * @param pageable pagination information
     * @return Page of users
     */
    Page<User> getAllUsers(Pageable pageable);

    /**
     * Retrieves all users with a specific role.
     * 
     * @param role the role to filter by
     * @return List of users with the specified role
     */
    List<User> getUsersByRole(User.Role role);

    /**
     * Checks if a username is available for registration.
     * 
     * @param username the username to check
     * @return true if available, false if already taken
     */
    boolean isUsernameAvailable(String username);

    /**
     * Checks if an email address is available for registration.
     * 
     * @param email the email address to check
     * @return true if available, false if already taken
     */
    boolean isEmailAvailable(String email);

    /**
     * Changes a user's password.
     * 
     * This method handles secure password updates with proper
     * encryption and validation of current password.
     * 
     * @param userId          the ID of the user
     * @param currentPassword  the current password for verification
     * @param newPassword     the new password to set
     * @return true if password was changed successfully
     * @throws IllegalArgumentException if passwords are invalid
     * @throws RuntimeException if current password doesn't match
     */
    boolean changePassword(Long userId, String currentPassword, String newPassword);

    /**
     * Enables or disables a user account.
     * 
     * This method is used for account management and
     * security purposes (e.g., suspending accounts).
     * 
     * @param userId  the ID of the user
     * @param enabled true to enable, false to disable
     * @return the updated user
     * @throws RuntimeException if user not found
     */
    User setUserEnabled(Long userId, boolean enabled);

    /**
     * Changes a user's role.
     * 
     * This method is used for administrative purposes
     * to manage user permissions.
     * 
     * @param userId the ID of the user
     * @param role   the new role to assign
     * @return the updated user
     * @throws RuntimeException if user not found
     */
    User changeUserRole(Long userId, User.Role role);

    /**
     * Searches for users by username or email.
     * 
     * This method provides flexible search functionality
     * for user management and administrative purposes.
     * 
     * @param keyword the search keyword
     * @return List of users matching the search criteria
     */
    List<User> searchUsers(String keyword);

    /**
     * Retrieves the total number of users in the system.
     * 
     * @return the total count of users
     */
    long getTotalUserCount();

    /**
     * Retrieves the number of users with a specific role.
     * 
     * @param role the role to count
     * @return the count of users with the specified role
     */
    long getUserCountByRole(User.Role role);

    /**
     * Retrieves the number of enabled users.
     * 
     * @return the count of enabled user accounts
     */
    long getEnabledUserCount();

    /**
     * Validates user data for registration or update.
     * 
     * This method performs comprehensive validation of user data
     * according to business rules and constraints.
     * 
     * @param user the user to validate
     * @return true if valid, false otherwise
     */
    boolean validateUserData(User user);

    /**
     * Authenticates a user with username/email and password.
     * 
     * This method handles user authentication for login purposes.
     * It should only be used by authentication mechanisms.
     * 
     * @param usernameOrEmail the username or email of the user
     * @param password        the password to verify
     * @return Optional containing the authenticated user if successful
     * @throws RuntimeException if authentication fails
     */
    Optional<User> authenticateUser(String usernameOrEmail, String password);
}
