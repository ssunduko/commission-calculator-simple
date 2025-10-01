package com.chapman.edu.commissions.fundamentals.lifecycle;

import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.UserRole;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class demonstrates the JUnit 5 lifecycle methods and their execution order.
 * 
 * JUnit 5 provides several lifecycle methods that are executed at different points
 * during test execution. Understanding these lifecycle methods is crucial for
 * proper test setup and cleanup.
 * 
 * The lifecycle methods are:
 * 1. @BeforeAll - executed once before all test methods in the class
 * 2. @BeforeEach - executed before each test method
 * 3. @Test - the actual test method
 * 4. @AfterEach - executed after each test method
 * 5. @AfterAll - executed once after all test methods in the class
 */
public class JUnitLifecycleTest {

    // Static variable to be used in @BeforeAll and @AfterAll methods
    private static User sharedUser;
    
    // Instance variable to be used in @BeforeEach, @Test, and @AfterEach methods
    private User testUser;
    
    /**
     * This method is executed once before all test methods in this class.
     * It's useful for setup operations that are expensive and shared by all tests,
     * such as connecting to a database or starting a server.
     * 
     * Note: This method must be static.
     */
    @BeforeAll
    public static void setUpAll() {
        System.out.println("@BeforeAll - This method is executed once before all test methods");
        
        // Initialize the shared user that will be used by all tests
        sharedUser = new User();
        sharedUser.setId("shared-user");
        sharedUser.setUsername("shared.user");
        sharedUser.setEmail("shared.user@example.com");
        sharedUser.addRole(UserRole.SYSTEM_ADMIN);
    }
    
    /**
     * This method is executed before each test method.
     * It's useful for setting up the test environment for each test,
     * such as creating fresh test data or resetting state.
     */
    @BeforeEach
    public void setUp() {
        System.out.println("@BeforeEach - This method is executed before each test method");
        
        // Initialize a fresh test user for each test
        testUser = new User();
        testUser.setId("test-user");
        testUser.setUsername("test.user");
        testUser.setEmail("test.user@example.com");
        testUser.addRole(UserRole.SALES_REP);
    }
    
    /**
     * A simple test method that verifies the testUser was properly initialized.
     * This demonstrates a basic test using the test instance created in @BeforeEach.
     */
    @Test
    public void testUserProperties() {
        System.out.println("@Test - Testing user properties");
        
        assertEquals("test-user", testUser.getId(), "User ID should match");
        assertEquals("test.user", testUser.getUsername(), "Username should match");
        assertEquals("test.user@example.com", testUser.getEmail(), "Email should match");
        assertTrue(testUser.hasRole(UserRole.SALES_REP), "User should have SALES_REP role");
    }
    
    /**
     * Another test method that verifies the sharedUser was properly initialized.
     * This demonstrates using the shared instance created in @BeforeAll.
     */
    @Test
    public void testSharedUserProperties() {
        System.out.println("@Test - Testing shared user properties");
        
        assertEquals("shared-user", sharedUser.getId(), "Shared user ID should match");
        assertEquals("shared.user", sharedUser.getUsername(), "Shared username should match");
        assertEquals("shared.user@example.com", sharedUser.getEmail(), "Shared email should match");
        assertTrue(sharedUser.hasRole(UserRole.SYSTEM_ADMIN), "Shared user should have SYSTEM_ADMIN role");
    }
    
    /**
     * This method is executed after each test method.
     * It's useful for cleaning up after each test, such as
     * removing test data or resetting state.
     */
    @AfterEach
    public void tearDown() {
        System.out.println("@AfterEach - This method is executed after each test method");
        
        // Clean up the test user
        testUser = null;
    }
    
    /**
     * This method is executed once after all test methods in this class.
     * It's useful for cleanup operations that are shared by all tests,
     * such as closing database connections or stopping servers.
     * 
     * Note: This method must be static.
     */
    @AfterAll
    public static void tearDownAll() {
        System.out.println("@AfterAll - This method is executed once after all test methods");
        
        // Clean up the shared user
        sharedUser = null;
    }
}