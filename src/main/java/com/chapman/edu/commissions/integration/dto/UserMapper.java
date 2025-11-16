package com.chapman.edu.commissions.integration.dto;

import com.chapman.edu.commissions.model.User;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UserMapper - Converts between User entities and User DTOs.
 *
 * This mapper demonstrates an important security principle: NEVER expose sensitive data.
 * Notice that the toDTO method deliberately excludes the password field.
 *
 * **Security in Mappers:**
 * - DTOs act as a security filter
 * - Mappers control exactly what data crosses the API boundary
 * - Sensitive fields (passwords, internal IDs, audit data) are excluded
 * - Even if someone accidentally serializes a User entity, the DTO provides a safe layer
 *
 * **Best Practice:**
 * Always use DTOs for API responses, never serialize domain entities directly.
 * Domain entities may contain:
 * - Passwords or credentials
 * - Internal system fields
 * - Lazy-loaded relationships (causing N+1 queries or errors)
 * - Business logic methods (confusing API consumers)
 *
 * @author Sergey L. Sundukovskiy
 * @version 1.0
 * @see UserDTO The DTO that excludes password field
 */
public class UserMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    /**
     * Converts a User domain entity to a UserDTO (for API responses).
     *
     * **IMPORTANT SECURITY NOTE:**
     * This method deliberately EXCLUDES the password/passwordHash field.
     * Passwords should NEVER be included in API responses, even if hashed.
     *
     * @param user The domain entity
     * @return The DTO for API response, or null if input is null
     */
    public static UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setRoles(user.getRoles());
        dto.setActive(user.isActive());

        // Format date as string for consistent API representation
        if (user.getCreatedDate() != null) {
            dto.setCreatedDate(user.getCreatedDate().format(DATE_FORMATTER));
        }

        // Note: lastModifiedDate is not in the User entity currently
        // This demonstrates that DTOs can have fields that entities don't have

        // CRITICAL: We do NOT include the password field
        // Even though the entity has passwordHash, we never expose it

        return dto;
    }

    /**
     * Converts a list of User entities to a list of UserDTOs.
     *
     * @param users List of domain entities
     * @return List of DTOs (all without password fields)
     */
    public static List<UserDTO> toDTOList(List<User> users) {
        if (users == null) {
            return new ArrayList<>();
        }
        return users.stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Converts a UserDTO back to a User entity (if needed for updates).
     *
     * Note: This is typically NOT used for user creation/updates because:
     * 1. Password must be handled separately (hashing, validation)
     * 2. Roles might need authorization checks
     * 3. Created date should be server-controlled
     *
     * This method is included for completeness and educational purposes.
     *
     * @param dto The DTO
     * @return The domain entity (without password set)
     */
    public static User fromDTO(UserDTO dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setId(dto.getId());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setRoles(dto.getRoles());
        user.setActive(dto.isActive());

        // Note: Password is NOT set here
        // Password updates require special handling (validation, hashing)
        // and should go through a separate change-password endpoint

        return user;
    }
}