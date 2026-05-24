package com.ecommerce.app.controller;

import com.ecommerce.app.dto.UserDto;
import com.ecommerce.app.entity.User;
import com.ecommerce.app.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;

/**
 * Controller for handling user-related operations.
 * 
 * This controller follows SOLID principles:
 * - Single Responsibility: Handles only user-related web requests
 * - Open/Closed: Open for extension through additional endpoints
 * - Liskov Substitution: Can be substituted with any UserController
 * implementation
 * - Interface Segregation: Provides only user-related methods
 * - Dependency Inversion: Depends on UserService interface, not concrete
 * classes
 * 
 * The class provides a clean separation between web layer
 * and business logic, following best practices for controller design.
 */
@Controller
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Displays user registration form.
     * 
     * This method shows the registration page for new users.
     * 
     * @return name of registration view template
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        log.debug("Displaying registration form");

        model.addAttribute("userDto", new UserDto());
        model.addAttribute("pageTitle", "Register");
        model.addAttribute("currentPage", "register");

        return "users/register";
    }

    /**
     * Processes user registration.
     * 
     * This method handles new user registration with validation
     * and provides appropriate feedback to the user.
     * 
     * @param userDto            user registration data
     * @param bindingResult      validation results
     * @param redirectAttributes for flash messages
     * @param model              for view attributes
     * @return redirect to login page on success
     */
    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("userDto") UserDto userDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        log.debug("Processing user registration for username: {}", userDto.getUsername());

        if (bindingResult.hasErrors()) {
            log.warn("Registration validation failed for username: {}", userDto.getUsername());
            model.addAttribute("userDto", userDto);
            model.addAttribute("pageTitle", "Register");
            model.addAttribute("currentPage", "register");
            return "users/register";
        }

        try {
            User user = new User();
            user.setUsername(userDto.getUsername());
            user.setEmail(userDto.getEmail());
            user.setPassword(userDto.getPassword());
            user.setRole(User.Role.USER);
            user.setEnabled(true);
            userService.createUser(user);
            log.info("User registered successfully: {}", userDto.getUsername());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Registration successful! Please login with your credentials.");
            return "redirect:/login";

        } catch (Exception e) {
            log.error("Registration failed for username: {}", userDto.getUsername(), e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Registration failed: " + e.getMessage());
            return "redirect:/users/register";
        }
    }

    /**
     * Displays user profile page.
     * 
     * This method shows the current user's profile information.
     * 
     * @return name of profile view template
     */
    @GetMapping("/profile")
    public String showProfile(Model model, Authentication authentication) {
        log.debug("Displaying user profile");

        User user = loadCurrentUser(authentication);
        UserDto userDto = UserDto.fromEntity(user);

        model.addAttribute("user", user);
        model.addAttribute("userDto", userDto);
        model.addAttribute("pageTitle", "My Profile");
        model.addAttribute("currentPage", "profile");

        return "users/profile";
    }

    /**
     * Displays user profile edit form.
     * 
     * This method shows the form for editing user profile.
     * 
     * @return name of profile edit view template
     */
    @GetMapping("/profile/edit")
    public String showEditProfileForm(Model model, Authentication authentication) {
        log.debug("Displaying profile edit form");

        UserDto userDto = UserDto.fromEntity(loadCurrentUser(authentication));

        model.addAttribute("userDto", userDto);
        model.addAttribute("pageTitle", "Edit Profile");
        model.addAttribute("currentPage", "edit-profile");

        return "users/edit-profile";
    }

    /**
     * Processes user profile update.
     * 
     * This method handles profile updates with validation
     * and provides appropriate feedback to the user.
     * 
     * @param userDto            user profile data
     * @param bindingResult      validation results
     * @param redirectAttributes for flash messages
     * @param model              for view attributes
     * @return redirect to profile page on success
     */
    @PostMapping("/profile/edit")
    public String updateProfile(
            @Valid @ModelAttribute("userDto") UserDto userDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        log.debug("Processing profile update for user: {}", userDto.getUsername());

        if (bindingResult.hasErrors()) {
            log.warn("Profile update validation failed for user: {}", userDto.getUsername());
            model.addAttribute("userDto", userDto);
            model.addAttribute("pageTitle", "Edit Profile");
            model.addAttribute("currentPage", "edit-profile");
            return "users/edit-profile";
        }

        try {
            if (userDto.getId() == null) {
                throw new IllegalArgumentException("User ID is required for profile update");
            }

            userService.updateUser(userDto.getId(), userDto);
            log.info("Profile updated successfully for user: {}", userDto.getUsername());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Profile updated successfully!");
            return "redirect:/users/profile";

        } catch (Exception e) {
            log.error("Profile update failed for user: {}", userDto.getUsername(), e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Profile update failed: " + e.getMessage());
            return "redirect:/users/profile/edit";
        }
    }

    /**
     * Displays change password form.
     * 
     * This method shows the form for changing user password.
     * 
     * @return name of change password view template
     */
    @GetMapping("/change-password")
    public String showChangePasswordForm(Model model, Authentication authentication) {
        log.debug("Displaying change password form");

        User currentUser = loadCurrentUser(authentication);
        UserDto passwordDto = new UserDto();
        passwordDto.setId(currentUser.getId());
        passwordDto.setUsername(currentUser.getUsername());

        model.addAttribute("passwordDto", passwordDto);
        model.addAttribute("pageTitle", "Change Password");
        model.addAttribute("currentPage", "change-password");

        return "users/change-password";
    }

    /**
     * Processes password change request.
     * 
     * This method handles password changes with validation
     * and provides appropriate feedback to the user.
     * 
     * @param passwordDto        password change data
     * @param bindingResult      validation results
     * @param redirectAttributes for flash messages
     * @param model              for view attributes
     * @return redirect to profile page on success
     */
    @PostMapping("/change-password")
    public String changePassword(
            @Valid @ModelAttribute("passwordDto") UserDto passwordDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        log.debug("Processing password change for user: {}", passwordDto.getUsername());

        if (bindingResult.hasErrors()) {
            log.warn("Password change validation failed for user: {}", passwordDto.getUsername());
            model.addAttribute("passwordDto", passwordDto);
            model.addAttribute("pageTitle", "Change Password");
            model.addAttribute("currentPage", "change-password");
            return "users/change-password";
        }

        try {
            if (passwordDto.getId() == null) {
                throw new IllegalArgumentException("User ID is required for password change");
            }

            userService.changePassword(passwordDto.getId(), passwordDto.getPassword(),
                    passwordDto.getNewPassword());
            log.info("Password changed successfully for user: {}", passwordDto.getUsername());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Password changed successfully!");
            return "redirect:/users/profile";

        } catch (Exception e) {
            log.error("Password change failed for user: {}", passwordDto.getUsername(), e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Password change failed: " + e.getMessage());
            return "redirect:/users/change-password";
        }
    }

    private User loadCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User must be authenticated");
        }

        return userService.getUserByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException(
                        "Authenticated user not found: " + authentication.getName()));
    }
}
