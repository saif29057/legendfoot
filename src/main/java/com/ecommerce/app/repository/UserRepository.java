package com.ecommerce.app.repository;

import com.ecommerce.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity operations.
 * 
 * This interface follows the Repository pattern and Single Responsibility Principle
 * by being responsible only for data access operations related to User entities.
 * It extends JpaRepository to inherit standard CRUD operations and defines
 * custom query methods for specific user-related business requirements.
 * 
 * The interface uses Spring Data JPA to automatically generate implementation
 * classes at runtime, reducing boilerplate code and following DRY principles.
 * 
 * @Repository annotation indicates this is a Spring repository bean.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their username.
     * 
     * This method follows the Single Responsibility Principle by providing
     * a specific query method for username-based user lookup.
     * Spring Data JPA automatically generates the query from the method name.
     * 
     * @param username the username to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by their email address.
     * 
     * This method provides a specific query method for email-based user lookup,
     * which is commonly used for authentication and user management features.
     * 
     * @param email the email address to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user exists with the given username.
     * 
     * This method is useful for validation during user registration
     * to ensure username uniqueness.
     * 
     * @param username the username to check
     * @return true if a user with this username exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a user exists with the given email address.
     * 
     * This method is useful for validation during user registration
     * to ensure email uniqueness.
     * 
     * @param email the email address to check
     * @return true if a user with this email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Finds a user by their username or email address.
     * 
     * This method provides a flexible lookup that allows users to login
     * with either their username or email address, improving user experience.
     * 
     * @param username the username to search for
     * @param email the email address to search for
     * @return Optional containing the user if found, empty otherwise
     */
    @Query("SELECT u FROM User u WHERE u.username = :username OR u.email = :email")
    Optional<User> findByUsernameOrEmail(@Param("username") String username, @Param("email") String email);

    /**
     * Finds a user by their username and ensures the account is enabled.
     * 
     * This method is specifically designed for authentication purposes,
     * ensuring that only active/enabled accounts can be used for login.
     * 
     * @param username the username to search for
     * @return Optional containing the enabled user if found, empty otherwise
     */
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.enabled = true")
    Optional<User> findEnabledByUsername(@Param("username") String username);

    /**
     * Finds users by their role.
     * 
     * This method is useful for administrative functions where we need to
     * retrieve all users with a specific role (e.g., all admins).
     * 
     * @param role the role to filter by
     * @return Iterable of users with the specified role
     */
    @Query("SELECT u FROM User u WHERE u.role = :role")
    Iterable<User> findByRole(@Param("role") User.Role role);

    /**
     * Finds users whose username contains the given string (case-insensitive).
     * 
     * This method supports search functionality where users can be found
     * by partial username matches.
     * 
     * @param username the username fragment to search for
     * @return Iterable of users with matching usernames
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))")
    Iterable<User> findByUsernameContainingIgnoreCase(@Param("username") String username);

    /**
     * Finds enabled users only.
     * 
     * This method is useful for reports and administrative functions
     * where we only want to see active user accounts.
     * 
     * @return Iterable of enabled users
     */
    @Query("SELECT u FROM User u WHERE u.enabled = true")
    Iterable<User> findEnabledUsers();

    /**
     * Counts users by their role.
     * 
     * This method provides statistical information about user distribution
     * across different roles, useful for administrative dashboards.
     * 
     * @param role the role to count
     * @return the number of users with the specified role
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    long countByRole(@Param("role") User.Role role);

    /**
     * Counts enabled users.
     * 
     * This method provides statistical information about the number
     * of active user accounts in the system.
     * 
     * @return the number of enabled users
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.enabled = true")
    long countEnabledUsers();
}
