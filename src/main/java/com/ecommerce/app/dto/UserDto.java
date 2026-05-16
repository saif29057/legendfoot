package com.ecommerce.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Data Transfer Object for User operations.
 * 
 * This DTO follows the Single Responsibility Principle by containing
 * only user-related data for transfer between layers.
 * It prevents entity exposure and provides clean separation
 * between controller and service layers.
 * 
 * The DTO uses validation annotations to ensure data integrity
 * and follows best practices for data transfer objects.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    /**
     * User ID for update operations.
     * Can be null for create operations.
     */
    private Long id;

    /**
     * Username for authentication and display.
     * Must be between 3-50 characters.
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    /**
     * Email address for communication and authentication.
     * Must be valid email format.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    /**
     * Password for authentication.
     * Must be at least 6 characters for security.
     * Not included in responses for security reasons.
     */
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    /**
     * Current password for verification during password changes.
     * Used only for password update operations.
     */
    private String currentPassword;

    /**
     * New password for password changes.
     * Used only for password update operations.
     */
    private String newPassword;

    /**
     * User role for authorization.
     * Defaults to USER for new registrations.
     */
    private String role;

    /**
     * Flag indicating if user account is enabled.
     */
    private Boolean enabled;

    /**
     * Static factory method for creating UserDto from User entity.
     * 
     * This method provides clean conversion between entity and DTO,
     * following the Single Responsibility Principle by centralizing
     * conversion logic.
     * 
     * @param user User entity to convert
     * @return UserDto with user data
     */
    public static UserDto fromEntity(com.ecommerce.app.entity.User user) {
        if (user == null) {
            return null;
        }
        
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole() != null ? user.getRole().name() : null);
        dto.setEnabled(user.isEnabled());
        
        return dto;
    }

    /**
     * Static factory method for creating UserDto for registration.
     * 
     * This method provides a clean way to create DTOs
     * for user registration operations.
     * 
     * @param username username for new user
     * @param email    email for new user
     * @param password password for new user
     * @return UserDto with registration data
     */
    public static UserDto forRegistration(String username, String email, String password) {
        UserDto dto = new UserDto();
        dto.setUsername(username);
        dto.setEmail(email);
        dto.setPassword(password);
        dto.setRole("USER");
        dto.setEnabled(true);
        
        return dto;
    }

    /**
     * Static factory method for creating UserDto for profile updates.
     * 
     * This method provides a clean way to create DTOs
     * for user profile update operations without password changes.
     * 
     * @param username username for update
     * @param email    email for update
     * @return UserDto with profile update data
     */
    public static UserDto forProfileUpdate(String username, String email) {
        UserDto dto = new UserDto();
        dto.setUsername(username);
        dto.setEmail(email);
        dto.setRole(null); // Role not changed in profile updates
        dto.setEnabled(null); // Status not changed in profile updates
        
        return dto;
    }

    /**
     * Static factory method for creating UserDto for password changes.
     * 
     * This method provides a clean way to create DTOs
     * for password change operations.
     * 
     * @param currentPassword current password for verification
     * @param newPassword     new password to set
     * @return UserDto with password change data
     */
    public static UserDto forPasswordChange(String currentPassword, String newPassword) {
        UserDto dto = new UserDto();
        dto.setPassword(newPassword);
        dto.setCurrentPassword(currentPassword);
        dto.setUsername(null); // Not needed for password change
        dto.setEmail(null);    // Not needed for password change
        dto.setRole(null);    // Not needed for password change
        dto.setEnabled(null);    // Not needed for password change
        
        return dto;
    }
}
