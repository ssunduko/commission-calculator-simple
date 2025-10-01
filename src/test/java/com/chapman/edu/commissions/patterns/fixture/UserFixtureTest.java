package com.chapman.edu.commissions.patterns.fixture;

import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.UserRole;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class demonstrating the use of UserFixture for creating consistent test data.
 * 
 * This class shows how the Fixture pattern simplifies test setup and ensures
 * consistent object creation across different test scenarios. Instead of manually
 * constructing User objects in each test, we use the fixture methods to get
 * pre-configured objects that meet our testing needs.
 * 
 * Key advantages demonstrated:
 * - Reduced test setup code
 * - Consistent test data across tests
 * - Easy maintenance when User model changes
 * - Clear test intent through descriptive fixture method names
 */
class UserFixtureTest {

    /**
     * Test that demonstrates creating a basic sales representative using fixtures.
     * This test focuses on verifying the core properties of a sales rep
     * without getting bogged down in object construction details.
     */
    @Test
    void testCreateSalesRep() {
        // Arrange: Use fixture to create a sales representative
        // The fixture handles all the complex setup, allowing the test to focus on behavior
        User salesRep = UserFixture.createSalesRep();
        
        // Act & Assert: Verify the sales rep has the expected characteristics
        assertNotNull(salesRep, "Sales rep should not be null");
        assertEquals("user-001", salesRep.getId(), "Sales rep should have correct ID");
        assertEquals("john.doe", salesRep.getUsername(), "Sales rep should have correct username");
        assertTrue(salesRep.isSalesRep(), "User should have sales rep role");
        assertTrue(salesRep.isActive(), "Sales rep should be active by default");
        assertEquals("Sales", salesRep.getDepartment(), "Sales rep should be in Sales department");
        assertEquals("West Coast", salesRep.getTerritory(), "Sales rep should have correct territory");
    }

    /**
     * Test that demonstrates creating a sales manager using fixtures.
     * Sales managers have different permissions and responsibilities than sales reps.
     */
    @Test
    void testCreateSalesManager() {
        // Arrange: Use fixture to create a sales manager
        User manager = UserFixture.createSalesManager();
        
        // Act & Assert: Verify manager-specific properties
        assertNotNull(manager, "Sales manager should not be null");
        assertEquals("user-002", manager.getId(), "Manager should have correct ID");
        assertTrue(manager.isSalesManager(), "User should have sales manager role");
        assertNotNull(manager.getLastLogin(), "Manager should have a last login time");
        assertEquals("Sales", manager.getDepartment(), "Manager should be in Sales department");
    }

    /**
     * Test that demonstrates creating a finance administrator using fixtures.
     * Finance admins have access to commission configuration and financial data.
     */
    @Test
    void testCreateFinanceAdmin() {
        // Arrange: Use fixture to create a finance administrator
        User financeAdmin = UserFixture.createFinanceAdmin();
        
        // Act & Assert: Verify finance admin properties
        assertNotNull(financeAdmin, "Finance admin should not be null");
        assertEquals("user-003", financeAdmin.getId(), "Finance admin should have correct ID");
        assertTrue(financeAdmin.isFinanceAdmin(), "User should have finance admin role");
        assertEquals("Finance", financeAdmin.getDepartment(), "Finance admin should be in Finance department");
        assertFalse(financeAdmin.isSalesRep(), "Finance admin should not be a sales rep");
    }

    /**
     * Test that demonstrates creating an inactive user using fixtures.
     * Inactive users should not be able to perform normal operations.
     */
    @Test
    void testCreateInactiveUser() {
        // Arrange: Use fixture to create an inactive user
        User inactiveUser = UserFixture.createInactiveUser();
        
        // Act & Assert: Verify inactive user properties
        assertNotNull(inactiveUser, "Inactive user should not be null");
        assertFalse(inactiveUser.isActive(), "User should be inactive");
        assertTrue(inactiveUser.isSalesRep(), "Inactive user should still have sales rep role");
        assertEquals("inactive.user", inactiveUser.getUsername(), "Should have correct username");
    }

    /**
     * Test that demonstrates creating a user with multiple roles using fixtures.
     * Some users may have both sales and management responsibilities.
     */
    @Test
    void testCreateMultiRoleUser() {
        // Arrange: Use fixture to create a user with multiple roles
        User multiRoleUser = UserFixture.createMultiRoleUser();
        
        // Act & Assert: Verify multiple role assignment
        assertNotNull(multiRoleUser, "Multi-role user should not be null");
        assertTrue(multiRoleUser.isSalesRep(), "User should have sales rep role");
        assertTrue(multiRoleUser.isSalesManager(), "User should have sales manager role");
        assertEquals(2, multiRoleUser.getRoles().size(), "User should have exactly 2 roles");
        assertEquals("East Coast", multiRoleUser.getTerritory(), "Should have correct territory");
    }

    /**
     * Test that demonstrates creating a user with a manager relationship using fixtures.
     * This tests hierarchical relationships important for commission approvals.
     */
    @Test
    void testCreateUserWithManager() {
        // Arrange: Create a manager first, then a user reporting to that manager
        User manager = UserFixture.createSalesManager();
        User subordinate = UserFixture.createUserWithManager(manager.getId());
        
        // Act & Assert: Verify manager relationship
        assertNotNull(subordinate, "Subordinate user should not be null");
        assertEquals(manager.getId(), subordinate.getManagerId(), "Should have correct manager ID");
        assertTrue(subordinate.isSalesRep(), "Subordinate should be a sales rep");
    }

    /**
     * Test that demonstrates creating a user with a specific territory using fixtures.
     * Different territories may have different commission structures.
     */
    @Test
    void testCreateUserWithTerritory() {
        // Arrange: Use fixture to create a user with a specific territory
        String customTerritory = "International";
        User territoryUser = UserFixture.createUserWithTerritory(customTerritory);
        
        // Act & Assert: Verify territory assignment
        assertNotNull(territoryUser, "Territory user should not be null");
        assertEquals(customTerritory, territoryUser.getTerritory(), "Should have correct territory");
        assertTrue(territoryUser.isSalesRep(), "Should be a sales rep");
    }

    /**
     * Test that demonstrates creating a user with a specific creation date using fixtures.
     * This is useful for testing time-based commission rules and eligibility.
     */
    @Test
    void testCreateUserWithCreationDate() {
        // Arrange: Use fixture to create a user with a specific creation date
        LocalDate customDate = LocalDate.of(2023, 1, 15);
        User datedUser = UserFixture.createUserWithCreationDate(customDate);
        
        // Act & Assert: Verify creation date
        assertNotNull(datedUser, "Dated user should not be null");
        assertEquals(customDate, datedUser.getCreatedDate(), "Should have correct creation date");
        assertTrue(datedUser.isSalesRep(), "Should be a sales rep");
    }

    /**
     * Test that demonstrates the consistency of fixture-created objects.
     * Multiple calls to the same fixture method should create similar objects
     * with the same base configuration but potentially different instances.
     */
    @Test
    void testFixtureConsistency() {
        // Arrange: Create multiple sales reps using the same fixture method
        User salesRep1 = UserFixture.createSalesRep();
        User salesRep2 = UserFixture.createSalesRep();
        
        // Act & Assert: Verify consistency while allowing for different instances
        assertNotSame(salesRep1, salesRep2, "Should be different object instances");
        assertEquals(salesRep1.getUsername(), salesRep2.getUsername(), "Should have same username");
        assertEquals(salesRep1.getDepartment(), salesRep2.getDepartment(), "Should have same department");
        assertEquals(salesRep1.getRoles(), salesRep2.getRoles(), "Should have same roles");
        assertTrue(salesRep1.isActive() && salesRep2.isActive(), "Both should be active");
    }

    /**
     * Test that demonstrates using fixtures for role-based testing.
     * This shows how fixtures make it easy to test different user types
     * without complex setup code in each test.
     */
    @Test
    void testRoleBasedBehavior() {
        // Arrange: Create users with different roles using fixtures
        User salesRep = UserFixture.createSalesRep();
        User manager = UserFixture.createSalesManager();
        User financeAdmin = UserFixture.createFinanceAdmin();
        
        // Act & Assert: Verify role-specific behavior
        // Sales rep should only have sales rep role
        assertTrue(salesRep.isSalesRep(), "Sales rep should have sales rep role");
        assertFalse(salesRep.isSalesManager(), "Sales rep should not have manager role");
        assertFalse(salesRep.isFinanceAdmin(), "Sales rep should not have finance admin role");
        
        // Manager should have manager role
        assertTrue(manager.isSalesManager(), "Manager should have manager role");
        assertFalse(manager.isSalesRep(), "Manager should not have sales rep role");
        assertFalse(manager.isFinanceAdmin(), "Manager should not have finance admin role");
        
        // Finance admin should have finance admin role
        assertTrue(financeAdmin.isFinanceAdmin(), "Finance admin should have finance admin role");
        assertFalse(financeAdmin.isSalesRep(), "Finance admin should not have sales rep role");
        assertFalse(financeAdmin.isSalesManager(), "Finance admin should not have manager role");
    }
}