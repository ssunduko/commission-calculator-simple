package com.chapman.edu.commissions.patterns.fixture;

import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.UserRole;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Fixture class for creating User test data.
 * 
 * The Fixture pattern provides a consistent way to create test objects with predefined data.
 * This eliminates duplication in test setup and ensures consistent test data across different tests.
 * 
 * Key benefits:
 * - Centralized test data creation
 * - Consistent object state across tests
 * - Easy maintenance when model changes
 * - Improved test readability
 */
public class UserFixture {
    
    /**
     * Creates a basic sales representative user with minimal required fields.
     * This is the most common user type in commission calculations.
     * 
     * @return a User configured as a sales representative
     */
    public static User createSalesRep() {
        User user = new User("john.doe", "john.doe@company.com", "John", "Doe");
        user.setId("user-001");
        user.addRole(UserRole.SALES_REP);
        user.setDepartment("Sales");
        user.setTerritory("West Coast");
        user.setCreatedBy("system");
        return user;
    }
    
    /**
     * Creates a sales manager user who can oversee sales representatives.
     * Managers typically have different commission structures and can approve deals.
     * 
     * @return a User configured as a sales manager
     */
    public static User createSalesManager() {
        User user = new User("jane.smith", "jane.smith@company.com", "Jane", "Smith");
        user.setId("user-002");
        user.addRole(UserRole.SALES_MANAGER);
        user.setDepartment("Sales");
        user.setTerritory("West Coast");
        user.setCreatedBy("system");
        user.setLastLogin(LocalDateTime.now().minusDays(1));
        return user;
    }
    
    /**
     * Creates a finance administrator who manages commission plans and calculations.
     * Finance admins have access to financial data and commission configuration.
     * 
     * @return a User configured as a finance administrator
     */
    public static User createFinanceAdmin() {
        User user = new User("bob.wilson", "bob.wilson@company.com", "Bob", "Wilson");
        user.setId("user-003");
        user.addRole(UserRole.FINANCE_ADMIN);
        user.setDepartment("Finance");
        user.setCreatedBy("system");
        return user;
    }
    
    /**
     * Creates an inactive user for testing scenarios where user status matters.
     * Inactive users should not be able to create deals or receive commissions.
     * 
     * @return an inactive User
     */
    public static User createInactiveUser() {
        User user = createSalesRep();
        user.setId("user-004");
        user.setUsername("inactive.user");
        user.setEmail("inactive.user@company.com");
        user.setActive(false);
        return user;
    }
    
    /**
     * Creates a user with multiple roles for testing complex permission scenarios.
     * Some users may have both sales and administrative responsibilities.
     * 
     * @return a User with multiple roles
     */
    public static User createMultiRoleUser() {
        User user = new User("admin.sales", "admin.sales@company.com", "Admin", "Sales");
        user.setId("user-005");
        user.addRole(UserRole.SALES_REP);
        user.addRole(UserRole.SALES_MANAGER);
        user.setDepartment("Sales");
        user.setTerritory("East Coast");
        user.setCreatedBy("system");
        return user;
    }
    
    /**
     * Creates a user with a specific manager relationship for testing hierarchical structures.
     * This is useful for testing manager-based commission calculations and approvals.
     * 
     * @param managerId the ID of the user's manager
     * @return a User with a manager relationship
     */
    public static User createUserWithManager(String managerId) {
        User user = createSalesRep();
        user.setId("user-006");
        user.setUsername("managed.user");
        user.setEmail("managed.user@company.com");
        user.setManagerId(managerId);
        return user;
    }
    
    /**
     * Creates a user with custom territory for testing territory-based commission rules.
     * Different territories may have different commission rates or bonus structures.
     * 
     * @param territory the territory to assign to the user
     * @return a User with the specified territory
     */
    public static User createUserWithTerritory(String territory) {
        User user = createSalesRep();
        user.setId("user-007");
        user.setUsername("territory.user");
        user.setEmail("territory.user@company.com");
        user.setTerritory(territory);
        return user;
    }
    
    /**
     * Creates a user with a specific creation date for testing time-based scenarios.
     * This is useful for testing commission plans that have effective date ranges.
     * 
     * @param createdDate the date when the user was created
     * @return a User with the specified creation date
     */
    public static User createUserWithCreationDate(LocalDate createdDate) {
        User user = createSalesRep();
        user.setId("user-008");
        user.setUsername("dated.user");
        user.setEmail("dated.user@company.com");
        user.setCreatedDate(createdDate);
        return user;
    }
}