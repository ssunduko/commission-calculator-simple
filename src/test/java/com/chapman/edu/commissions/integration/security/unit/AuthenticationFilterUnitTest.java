package com.chapman.edu.commissions.integration.security.unit;

import com.chapman.edu.commissions.integration.security.AuthenticationFilter;
import com.chapman.edu.commissions.integration.service.UserService;
import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.UserRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UNIT TESTING - AuthenticationFilter (Security Layer)
 *
 * PURPOSE:
 * Unit testing servlet filters demonstrates how to test security mechanisms
 * in isolation. Filters intercept HTTP requests before they reach controllers,
 * making them critical for authentication, authorization, and request validation.
 *
 * CONCEPTS DEMONSTRATED:
 * 1. SERVLET FILTER TESTING:
 *    - Mock HttpServletRequest and HttpServletResponse
 *    - Mock FilterChain to control request flow
 *    - Verify filter chain continuation (doFilter)
 *    - Verify response modification (status codes, headers)
 *    - Test request attribute setting
 *
 * 2. AUTHENTICATION TESTING:
 *    - HTTP Basic Authentication header parsing
 *    - Base64 decoding of credentials
 *    - Credential validation
 *    - Unauthorized access handling
 *    - Public endpoint bypass
 *
 * 3. MOCKITO ADVANCED PATTERNS:
 *    - ArgumentCaptor for inspecting method arguments
 *    - Verify exact interactions with mocks
 *    - Mock chaining (request.getHeader().substring())
 *    - Stubbing void methods
 *
 * 4. SECURITY TESTING PATTERNS:
 *    - Test both authenticated and unauthenticated paths
 *    - Test public vs protected endpoints
 *    - Test invalid credentials
 *    - Test missing Authorization header
 *    - Test malformed Authorization header
 *
 * LAYER: Security Layer (Filter)
 * TEST TYPE: Unit Test (Isolated with Mocks)
 *
 * FILTER CHAIN FLOW:
 * 1. Client sends HTTP request
 * 2. AuthenticationFilter intercepts (this is what we're testing)
 * 3. Filter validates credentials
 * 4. If valid: calls filterChain.doFilter() to continue
 * 5. If invalid: sends 401 response, stops chain
 *
 * TESTING CHALLENGES:
 * - Filters work with low-level servlet APIs
 * - Need to mock HttpServletRequest, HttpServletResponse, FilterChain
 * - Need to verify both request modification and response sending
 */
@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
@DisplayName("Unit Tests - AuthenticationFilter (Security)")
class AuthenticationFilterUnitTest {

    @Mock
    private UserService mockUserService;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private FilterChain mockFilterChain;

    @Mock
    private PrintWriter mockPrintWriter;

    private AuthenticationFilter filter;

    /**
     * SETUP: Create filter with mocked dependencies
     */
    @BeforeEach
    void setUp() throws IOException {
        filter = new AuthenticationFilter(mockUserService);

        // Setup common mock behavior for response (lenient because not all tests use it)
        lenient().when(mockResponse.getWriter()).thenReturn(mockPrintWriter);
    }

    // ============================================================
    // PUBLIC ENDPOINT TESTS (No Authentication Required)
    // ============================================================

    /**
     * TEST: Public endpoints bypass authentication
     *
     * SECURITY DESIGN:
     * - Certain paths are public (/, /index.html, /webjars/*)
     * - Filter should NOT check credentials for these paths
     * - Filter should immediately call filterChain.doFilter()
     *
     * DEMONSTRATES:
     * - Request path extraction
     * - Conditional filter logic
     * - Filter chain continuation without authentication
     */
    @Test
    @DisplayName("Should allow access to root path without authentication")
    void testPublicEndpointRoot() throws ServletException, IOException {
        // Arrange: Request to root path
        when(mockRequest.getRequestURI()).thenReturn("/");
        lenient().when(mockRequest.getContextPath()).thenReturn("");

        // Act: Filter processes request
        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        // Assert: Filter chain continues (request passed through)
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);

        // Verify: No authentication was attempted
        verify(mockUserService, never()).authenticateBasic(anyString());

        // Verify: No 401 response sent
        verify(mockResponse, never()).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    /**
     * TEST: Static resources are public
     */
    @Test
    @DisplayName("Should allow access to /index.html without authentication")
    void testPublicEndpointIndexHtml() throws ServletException, IOException {
        when(mockRequest.getRequestURI()).thenReturn("/index.html");
        lenient().when(mockRequest.getContextPath()).thenReturn("");

        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
        verify(mockUserService, never()).authenticateBasic(anyString());
    }

    /**
     * TEST: WebJars paths are public
     */
    @Test
    @DisplayName("Should allow access to /webjars/* without authentication")
    void testPublicEndpointWebjars() throws ServletException, IOException {
        when(mockRequest.getRequestURI()).thenReturn("/webjars/bootstrap/5.0.0/bootstrap.min.css");
        lenient().when(mockRequest.getContextPath()).thenReturn("");

        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
    }

    // ============================================================
    // PROTECTED ENDPOINT TESTS (Authentication Required)
    // ============================================================

    /**
     * TEST: Protected endpoint requires authentication
     *
     * SECURITY FLOW:
     * 1. Request to /api/* (protected)
     * 2. No Authorization header
     * 3. Filter sends 401 Unauthorized
     * 4. Filter adds WWW-Authenticate challenge header
     * 5. Filter does NOT continue chain
     *
     * DEMONSTRATES:
     * - Missing authentication detection
     * - 401 response generation
     * - WWW-Authenticate header
     * - Filter chain interruption
     */
    @Test
    @DisplayName("Should return 401 for protected endpoint without auth header")
    void testProtectedEndpointWithoutAuth() throws ServletException, IOException {
        // Arrange: Request to protected path with no auth
        when(mockRequest.getRequestURI()).thenReturn("/api/v1/integration/deals");
        lenient().when(mockRequest.getContextPath()).thenReturn("");
        lenient().when(mockRequest.getHeader("Authorization")).thenReturn(null);

        // Act: Filter processes request
        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        // Assert: 401 Unauthorized sent
        verify(mockResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Assert: WWW-Authenticate header added (HTTP Basic Auth challenge)
        verify(mockResponse).setHeader("WWW-Authenticate", "Basic realm=\"Commission Calculator API\"");

        // Assert: Error response body sent
        verify(mockResponse).setContentType("application/json");
        verify(mockResponse).getWriter();

        // Assert: Filter chain NOT continued (request blocked)
        verify(mockFilterChain, never()).doFilter(any(), any());
    }

    /**
     * TEST: Valid credentials allow access
     *
     * SECURITY FLOW:
     * 1. Request with valid Authorization header
     * 2. Filter parses header
     * 3. Filter calls UserService.authenticateBasic()
     * 4. UserService returns authenticated User
     * 5. Filter sets user in request attribute
     * 6. Filter continues chain
     *
     * DEMONSTRATES:
     * - Base64 credential parsing
     * - Service integration (UserService)
     * - Request attribute setting
     * - Successful authentication flow
     */
    @Test
    @DisplayName("Should allow access with valid credentials")
    void testValidCredentials() throws ServletException, IOException {
        // Arrange: Valid Basic Auth header
        String credentials = Base64.getEncoder().encodeToString("john@example.com:password123".getBytes());
        String authHeader = "Basic " + credentials;

        when(mockRequest.getRequestURI()).thenReturn("/api/v1/integration/deals");
        lenient().when(mockRequest.getContextPath()).thenReturn("");
        when(mockRequest.getHeader("Authorization")).thenReturn(authHeader);

        // Mock authenticated user
        User authenticatedUser = new User();
        authenticatedUser.setId("USER-123");
        authenticatedUser.setEmail("john@example.com");
        authenticatedUser.setRoles(new HashSet<>(java.util.Arrays.asList(UserRole.SALES_REP)));

        when(mockUserService.authenticateBasic(authHeader)).thenReturn(Optional.of(authenticatedUser));

        // Act: Filter processes request
        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        // Assert: UserService was called to authenticate
        verify(mockUserService).authenticateBasic(authHeader);

        // Assert: User was set in request attribute
        verify(mockRequest).setAttribute(AuthenticationFilter.USER_ATTRIBUTE, authenticatedUser);

        // Assert: Filter chain continued (request allowed)
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);

        // Assert: No 401 response sent
        verify(mockResponse, never()).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    /**
     * TEST: Invalid credentials return 401
     *
     * SECURITY FLOW:
     * 1. Request with invalid credentials
     * 2. UserService returns Optional.empty()
     * 3. Filter sends 401
     * 4. Filter does not continue chain
     */
    @Test
    @DisplayName("Should return 401 for invalid credentials")
    void testInvalidCredentials() throws ServletException, IOException {
        // Arrange: Invalid credentials
        String credentials = Base64.getEncoder().encodeToString("wrong@example.com:wrongpass".getBytes());
        String authHeader = "Basic " + credentials;

        when(mockRequest.getRequestURI()).thenReturn("/api/v1/integration/deals");
        lenient().when(mockRequest.getContextPath()).thenReturn("");
        when(mockRequest.getHeader("Authorization")).thenReturn(authHeader);

        // Mock failed authentication
        when(mockUserService.authenticateBasic(authHeader)).thenReturn(Optional.empty());

        // Act: Filter processes request
        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        // Assert: Authentication was attempted
        verify(mockUserService).authenticateBasic(authHeader);

        // Assert: 401 sent
        verify(mockResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Assert: Chain NOT continued
        verify(mockFilterChain, never()).doFilter(any(), any());
    }

    // ============================================================
    // MALFORMED REQUEST TESTS
    // ============================================================

    /**
     * TEST: Malformed Authorization header
     *
     * EDGE CASE:
     * - Authorization header exists but is not "Basic ..."
     * - Could be "Bearer ...", malformed, etc.
     *
     * EXPECTED: Treated as unauthenticated, return 401
     */
    @Test
    @DisplayName("Should return 401 for malformed Authorization header")
    void testMalformedAuthHeader() throws ServletException, IOException {
        // Arrange: Malformed header
        when(mockRequest.getRequestURI()).thenReturn("/api/v1/integration/deals");
        lenient().when(mockRequest.getContextPath()).thenReturn("");
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer some-token");

        // Act & Assert
        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(mockFilterChain, never()).doFilter(any(), any());
    }

    /**
     * TEST: Empty Authorization header
     */
    @Test
    @DisplayName("Should return 401 for empty Authorization header")
    void testEmptyAuthHeader() throws ServletException, IOException {
        when(mockRequest.getRequestURI()).thenReturn("/api/v1/integration/deals");
        lenient().when(mockRequest.getContextPath()).thenReturn("");
        when(mockRequest.getHeader("Authorization")).thenReturn("");

        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    // ============================================================
    // ARGUMENT CAPTOR TESTS (Advanced Mockito)
    // ============================================================

    /**
     * TEST: Verify exact user set in request attribute
     *
     * ADVANCED MOCKITO:
     * - Use ArgumentCaptor to capture the User object passed to setAttribute
     * - Verify the exact user object and its properties
     *
     * DEMONSTRATES:
     * - ArgumentCaptor usage
     * - Deep verification of mock interactions
     */
    @Test
    @DisplayName("Should set correct user in request attribute")
    void testUserAttributeSet() throws ServletException, IOException {
        // Arrange
        String credentials = Base64.getEncoder().encodeToString("admin@example.com:adminpass".getBytes());
        String authHeader = "Basic " + credentials;

        when(mockRequest.getRequestURI()).thenReturn("/api/v1/integration/deals");
        lenient().when(mockRequest.getContextPath()).thenReturn("");
        when(mockRequest.getHeader("Authorization")).thenReturn(authHeader);

        User adminUser = new User();
        adminUser.setId("USER-ADMIN");
        adminUser.setEmail("admin@example.com");
        adminUser.setRoles(new HashSet<>(java.util.Arrays.asList(UserRole.SYSTEM_ADMIN, UserRole.SALES_REP)));

        when(mockUserService.authenticateBasic(authHeader)).thenReturn(Optional.of(adminUser));

        // Act
        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        // Assert with ArgumentCaptor
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(mockRequest).setAttribute(eq(AuthenticationFilter.USER_ATTRIBUTE), userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertNotNull(capturedUser);
        assertEquals("USER-ADMIN", capturedUser.getId());
        assertEquals("admin@example.com", capturedUser.getEmail());
        assertTrue(capturedUser.getRoles().contains(UserRole.SYSTEM_ADMIN));
    }

    // ============================================================
    // MULTIPLE PROTECTED PATHS TESTS
    // ============================================================

    /**
     * TEST: All API paths require authentication
     */
    @Test
    @DisplayName("Should protect all /api/* endpoints")
    void testAllApiEndpointsProtected() throws ServletException, IOException {
        // Test various API paths
        String[] protectedPaths = {
            "/api/v1/integration/deals",
            "/api/v1/integration/users",
            "/api/v1/integration/deals/123",
            "/api/test"
        };

        for (String path : protectedPaths) {
            // Reset mocks
            reset(mockRequest, mockResponse, mockFilterChain);
            // Use lenient() for stubs that may not always be called in the loop
            lenient().when(mockResponse.getWriter()).thenReturn(mockPrintWriter);

            when(mockRequest.getRequestURI()).thenReturn(path);
            lenient().when(mockRequest.getContextPath()).thenReturn("");
            lenient().when(mockRequest.getHeader("Authorization")).thenReturn(null);

            filter.doFilter(mockRequest, mockResponse, mockFilterChain);

            verify(mockResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            verify(mockFilterChain, never()).doFilter(any(), any());
        }
    }

    /**
     * TEST: Swagger endpoints require authentication
     */
    @Test
    @DisplayName("Should protect /swagger-ui/* endpoints")
    void testSwaggerProtected() throws ServletException, IOException {
        when(mockRequest.getRequestURI()).thenReturn("/swagger-ui/index.html");
        lenient().when(mockRequest.getContextPath()).thenReturn("");
        lenient().when(mockRequest.getHeader("Authorization")).thenReturn(null);

        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    /**
     * KEY TAKEAWAYS - FILTER TESTING:
     *
     * WHAT WE TESTED:
     * ✓ Public endpoint bypass (no auth required)
     * ✓ Protected endpoint authentication requirement
     * ✓ Valid credentials allow access
     * ✓ Invalid credentials return 401
     * ✓ Missing Authorization header returns 401
     * ✓ Malformed Authorization header returns 401
     * ✓ User set in request attribute for downstream use
     * ✓ WWW-Authenticate challenge header
     * ✓ Filter chain continuation vs interruption
     *
     * MOCKING TECHNIQUES:
     * - Mock HttpServletRequest (getRequestURI, getHeader, setAttribute)
     * - Mock HttpServletResponse (setStatus, setHeader, getWriter)
     * - Mock FilterChain (doFilter)
     * - Mock UserService (authenticateBasic)
     * - ArgumentCaptor for verifying exact arguments
     *
     * SECURITY TESTING BEST PRACTICES:
     * - Test both authenticated and unauthenticated paths
     * - Test public vs protected endpoints
     * - Test edge cases (malformed headers, empty values)
     * - Verify filter chain behavior (continue vs block)
     * - Verify response codes and headers
     * - Test integration with authentication service
     *
     * FILTER TESTING CHALLENGES:
     * - Filters use low-level servlet APIs
     * - Need comprehensive mocking
     * - Must verify both request processing and response generation
     * - Need to test request attribute setting for downstream use
     *
     * LAYERED ARCHITECTURE:
     * - Filters intercept requests BEFORE controllers
     * - Filters can modify request/response
     * - Filters can block requests (security)
     * - Controllers receive pre-authenticated requests
     */
}