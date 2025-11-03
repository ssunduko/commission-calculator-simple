package com.chapman.edu.commissions.integration.security.integration;

import com.chapman.edu.commissions.integration.database.DatabaseManager;
import com.chapman.edu.commissions.integration.repository.H2UserRepository;
import com.chapman.edu.commissions.integration.security.AuthenticationFilter;
import com.chapman.edu.commissions.integration.service.UserService;
import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.UserRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * INTEGRATION TESTING - AuthenticationFilter (Filter + UserService + Repository)
 *
 * PURPOSE:
 * Integration tests verify that the FILTER and SERVICE layers work together correctly.
 * This tests authentication logic WITH real user validation from the database.
 *
 * CONCEPTS DEMONSTRATED:
 * 1. FILTER-SERVICE-REPOSITORY INTEGRATION:
 *    - Filter intercepts HTTP requests
 *    - Filter delegates authentication to UserService
 *    - UserService queries UserRepository
 *    - Repository validates credentials against database
 *    - Filter continues or blocks based on authentication result
 *
 * 2. INTEGRATION TEST FOCUS:
 *    - Test authentication with REAL user data in database
 *    - Verify filter correctly interprets service results
 *    - Test end-to-end authentication flow
 *    - Validate filter chain behavior with real authentication
 *
 * 3. LAYER INTEGRATION PATTERN:
 *    - Filter Layer (security) → Service Layer (auth logic) → Repository Layer (data access) → Database
 *    - Tests verify the contract between these layers
 *
 * 4. DIFFERENCE FROM UNIT TESTS:
 *    - Unit tests: Mock UserService, test filter logic only
 *    - Integration tests: Real UserService + Repository + Database
 *
 * LAYER: Filter Layer + Service Layer + Repository Layer
 * TEST TYPE: Integration Test (Filter with real UserService)
 *
 * WHEN TO USE:
 * - Verify authentication flow with real user data
 * - Test password hashing and validation
 * - Validate filter behavior with actual service responses
 * - Test security across multiple layers
 */
@DisplayName("Integration Tests - AuthenticationFilter (Filter + Service + Repository + Database)")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthenticationFilterIntegrationTest {

    private static DatabaseManager dbManager;
    private static H2UserRepository userRepository;
    private static UserService userService;
    private static AuthenticationFilter filter;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private FilterChain mockFilterChain;

    private StringWriter responseWriter;
    private PrintWriter printWriter;

    // Test user credentials
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static User testUser;

    @BeforeAll
    static void setUpDatabase() {
        dbManager = DatabaseManager.getInstance();
        userRepository = new H2UserRepository(dbManager);
        userService = new UserService(userRepository);
        filter = new AuthenticationFilter(userService);
    }

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        dbManager.resetDatabase();

        // Create test user in database
        testUser = new User();
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEmail(TEST_EMAIL);
        testUser.setPasswordHash(TEST_PASSWORD); // In real app, this would be hashed
        testUser.setRoles(new HashSet<>(Arrays.asList(UserRole.SALES_REP)));
        testUser.setActive(true);
        testUser = userRepository.save(testUser);

        // Setup response writer
        responseWriter = new StringWriter();
        printWriter = new PrintWriter(responseWriter);
        when(mockResponse.getWriter()).thenReturn(printWriter);
    }

    @AfterAll
    static void tearDown() {
        if (dbManager != null) {
            dbManager.close();
        }
    }

    // ============================================================
    // INTEGRATION TEST: AUTHENTICATION WITH REAL USER DATA
    // ============================================================

    /**
     * TEST: Valid credentials authenticate with database
     *
     * INTEGRATION FLOW:
     * 1. Filter receives request with Authorization header
     * 2. Filter calls userService.authenticateBasic()
     * 3. UserService calls userRepository.findByEmail()
     * 4. Repository queries database for user
     * 5. UserService validates password
     * 6. Filter sets user in request attribute
     * 7. Filter continues chain
     *
     * DEMONSTRATES:
     * - Full authentication flow through all layers
     * - Real database lookup
     * - Password validation
     * - Request attribute setting
     */
    @Test
    @Order(1)
    @DisplayName("Integration: Should authenticate with valid credentials from database")
    void testAuthenticateWithValidCredentials() throws Exception {
        // Arrange: Request with valid credentials
        String credentials = Base64.getEncoder().encodeToString(
            (TEST_EMAIL + ":" + TEST_PASSWORD).getBytes()
        );
        String authHeader = "Basic " + credentials;

        when(mockRequest.getRequestURI()).thenReturn("/api/v1/integration/deals");
        when(mockRequest.getContextPath()).thenReturn("");
        when(mockRequest.getHeader("Authorization")).thenReturn(authHeader);

        // Act: Filter processes request
        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        // Assert: User authenticated from database
        verify(mockRequest).setAttribute(eq(AuthenticationFilter.USER_ATTRIBUTE), argThat(user ->
            user instanceof User &&
            ((User) user).getEmail().equals(TEST_EMAIL) &&
            ((User) user).getId().equals(testUser.getId())
        ));

        // Assert: Filter chain continued
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);

        // Assert: No 401 response
        verify(mockResponse, never()).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    /**
     * TEST: Invalid credentials rejected by database
     *
     * INTEGRATION FLOW:
     * 1. Filter receives request with invalid password
     * 2. UserService queries database
     * 3. User found but password doesn't match
     * 4. UserService returns Optional.empty()
     * 5. Filter sends 401 Unauthorized
     *
     * DEMONSTRATES:
     * - Invalid credentials caught by service layer
     * - Database validation
     * - Filter blocks request
     */
    @Test
    @Order(2)
    @DisplayName("Integration: Should reject invalid password")
    void testAuthenticateWithInvalidPassword() throws Exception {
        // Arrange: Request with wrong password
        String credentials = Base64.getEncoder().encodeToString(
            (TEST_EMAIL + ":wrongpassword").getBytes()
        );
        String authHeader = "Basic " + credentials;

        when(mockRequest.getRequestURI()).thenReturn("/api/v1/integration/deals");
        when(mockRequest.getContextPath()).thenReturn("");
        when(mockRequest.getHeader("Authorization")).thenReturn(authHeader);

        // Act: Filter processes request
        filter.doFilter(mockRequest, mockResponse, mockFilterChain);
        printWriter.flush();

        // Assert: Authentication failed
        verify(mockResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Assert: Filter chain NOT continued
        verify(mockFilterChain, never()).doFilter(any(), any());

        // Assert: User NOT set in request
        verify(mockRequest, never()).setAttribute(eq(AuthenticationFilter.USER_ATTRIBUTE), any());
    }

    /**
     * TEST: Non-existent user rejected
     *
     * INTEGRATION FOCUS:
     * - User doesn't exist in database
     * - Service returns Optional.empty()
     * - Filter blocks request
     */
    @Test
    @Order(3)
    @DisplayName("Integration: Should reject non-existent user")
    void testAuthenticateWithNonExistentUser() throws Exception {
        // Arrange: Request with non-existent email
        String credentials = Base64.getEncoder().encodeToString(
            "nonexistent@example.com:password".getBytes()
        );
        String authHeader = "Basic " + credentials;

        when(mockRequest.getRequestURI()).thenReturn("/api/v1/integration/deals");
        when(mockRequest.getContextPath()).thenReturn("");
        when(mockRequest.getHeader("Authorization")).thenReturn(authHeader);

        // Act: Filter processes request
        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        // Assert: Authentication failed
        verify(mockResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(mockFilterChain, never()).doFilter(any(), any());
    }

    // ============================================================
    // INTEGRATION TEST: INACTIVE USER HANDLING
    // ============================================================

    /**
     * TEST: Inactive user cannot authenticate
     *
     * INTEGRATION FLOW:
     * - User exists in database but is inactive
     * - Service checks active status
     * - Authentication fails
     * - Filter blocks request
     */
    @Test
    @Order(4)
    @DisplayName("Integration: Should reject inactive user")
    void testAuthenticateWithInactiveUser() throws Exception {
        // Arrange: Deactivate user in database
        testUser.setActive(false);
        userRepository.save(testUser);

        String credentials = Base64.getEncoder().encodeToString(
            (TEST_EMAIL + ":" + TEST_PASSWORD).getBytes()
        );
        String authHeader = "Basic " + credentials;

        when(mockRequest.getRequestURI()).thenReturn("/api/v1/integration/deals");
        when(mockRequest.getContextPath()).thenReturn("");
        when(mockRequest.getHeader("Authorization")).thenReturn(authHeader);

        // Act: Filter processes request
        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        // Assert: Authentication failed
        verify(mockResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(mockFilterChain, never()).doFilter(any(), any());

        // Cleanup: Reactivate for other tests
        testUser.setActive(true);
        userRepository.save(testUser);
    }

    // ============================================================
    // INTEGRATION TEST: PUBLIC ENDPOINTS
    // ============================================================

    /**
     * TEST: Public endpoints bypass authentication
     *
     * INTEGRATION FOCUS:
     * - Filter checks path against public list
     * - No service call for public paths
     * - No database access
     * - Filter continues immediately
     */
    @Test
    @Order(5)
    @DisplayName("Integration: Should allow public endpoints without authentication")
    void testPublicEndpoint() throws Exception {
        when(mockRequest.getRequestURI()).thenReturn("/");
        when(mockRequest.getContextPath()).thenReturn("");

        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        // Assert: Filter chain continued
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);

        // Assert: No authentication attempted
        verify(mockRequest, never()).getHeader("Authorization");
        verify(mockResponse, never()).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    // ============================================================
    // INTEGRATION TEST: MULTIPLE USERS
    // ============================================================

    /**
     * TEST: Different users authenticate correctly
     *
     * INTEGRATION FOCUS:
     * - Multiple users in database
     * - Each authenticates with their own credentials
     * - Correct user retrieved for each request
     */
    @Test
    @Order(6)
    @DisplayName("Integration: Should authenticate multiple different users")
    void testMultipleUsers() throws Exception {
        // Arrange: Create second user
        User user2 = new User();
        user2.setFirstName("Second");
        user2.setLastName("User");
        user2.setEmail("user2@example.com");
        user2.setPasswordHash("password456");
        user2.setRoles(new HashSet<>(Arrays.asList(UserRole.SYSTEM_ADMIN)));
        user2.setActive(true);
        user2 = userRepository.save(user2);

        // Test User 1
        String creds1 = Base64.getEncoder().encodeToString(
            (TEST_EMAIL + ":" + TEST_PASSWORD).getBytes()
        );
        when(mockRequest.getRequestURI()).thenReturn("/api/v1/integration/deals");
        when(mockRequest.getContextPath()).thenReturn("");
        when(mockRequest.getHeader("Authorization")).thenReturn("Basic " + creds1);

        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockRequest).setAttribute(eq(AuthenticationFilter.USER_ATTRIBUTE), argThat(user ->
            user instanceof User && ((User) user).getEmail().equals(TEST_EMAIL)
        ));

        // Reset mocks
        reset(mockRequest, mockResponse, mockFilterChain);
        when(mockResponse.getWriter()).thenReturn(printWriter);

        // Test User 2
        String creds2 = Base64.getEncoder().encodeToString(
            ("user2@example.com:password456").getBytes()
        );
        when(mockRequest.getRequestURI()).thenReturn("/api/v1/integration/deals");
        when(mockRequest.getContextPath()).thenReturn("");
        when(mockRequest.getHeader("Authorization")).thenReturn("Basic " + creds2);

        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockRequest).setAttribute(eq(AuthenticationFilter.USER_ATTRIBUTE), argThat(user ->
            user instanceof User && ((User) user).getEmail().equals("user2@example.com")
        ));
    }

    // ============================================================
    // INTEGRATION TEST: USER ROLES
    // ============================================================

    /**
     * TEST: User roles loaded from database
     *
     * INTEGRATION FOCUS:
     * - User authenticated with roles
     * - Roles loaded from database
     * - Filter sets complete user object in request
     */
    @Test
    @Order(7)
    @DisplayName("Integration: Should load user roles from database")
    void testUserRolesLoaded() throws Exception {
        // Arrange: User has SALES_REP role
        String credentials = Base64.getEncoder().encodeToString(
            (TEST_EMAIL + ":" + TEST_PASSWORD).getBytes()
        );

        when(mockRequest.getRequestURI()).thenReturn("/api/v1/integration/deals");
        when(mockRequest.getContextPath()).thenReturn("");
        when(mockRequest.getHeader("Authorization")).thenReturn("Basic " + credentials);

        // Act: Filter processes request
        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        // Assert: User with roles set in request
        verify(mockRequest).setAttribute(eq(AuthenticationFilter.USER_ATTRIBUTE), argThat(user ->
            user instanceof User &&
            ((User) user).getRoles().contains(UserRole.SALES_REP)
        ));
    }

    /**
     * KEY TAKEAWAYS - FILTER INTEGRATION TESTING:
     *
     * WHAT WE TESTED:
     * ✓ Authentication with real user data from database
     * ✓ Password validation through service layer
     * ✓ Invalid credentials rejected
     * ✓ Non-existent users rejected
     * ✓ Inactive users blocked
     * ✓ Public endpoints bypass authentication
     * ✓ Multiple users authenticate correctly
     * ✓ User roles loaded from database
     *
     * INTEGRATION PATTERN:
     * - Filter handles HTTP concerns
     * - Service handles authentication logic
     * - Repository handles database queries
     * - Tests verify all layers work together
     *
     * DIFFERENCE FROM UNIT TESTS:
     * - Unit: Mock UserService, test filter logic only
     * - Integration: Real service + repository + database
     * - Unit: Fast, isolated
     * - Integration: Verify actual authentication flow
     *
     * BEST PRACTICES:
     * - Test with real user data in database
     * - Verify both success and failure paths
     * - Test edge cases (inactive users, non-existent users)
     * - Validate filter chain behavior
     * - Test request attribute setting
     * - Combine with unit tests for full coverage
     */
}