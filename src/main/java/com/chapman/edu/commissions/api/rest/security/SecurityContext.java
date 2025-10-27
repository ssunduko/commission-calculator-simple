package com.chapman.edu.commissions.api.rest.security;

/**
 * Thread-local storage for the current security context.
 *
 * This class provides access to the currently authenticated user (principal)
 * in a thread-safe manner. Each HTTP request thread has its own security context.
 *
 * Design Pattern: Thread-Local Storage Pattern
 * Stores request-scoped authentication information without passing it explicitly.
 *
 * Usage:
 * <pre>
 * // Set context (usually done by SecurityFilter)
 * SecurityContext.setCurrentUser(principal);
 *
 * // Access context anywhere in the request handling
 * UserPrincipal user = SecurityContext.getCurrentUser();
 *
 * // Clear context (must be done after request completes)
 * SecurityContext.clear();
 * </pre>
 */
public class SecurityContext {

    private static final ThreadLocal<UserPrincipal> CURRENT_USER = new ThreadLocal<>();

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static methods.
     */
    private SecurityContext() {
        throw new AssertionError("Cannot instantiate SecurityContext");
    }

    /**
     * Sets the current user principal for this thread.
     *
     * @param principal The authenticated user principal
     */
    public static void setCurrentUser(UserPrincipal principal) {
        CURRENT_USER.set(principal);
    }

    /**
     * Gets the current user principal for this thread.
     *
     * @return The current principal, or anonymous if not authenticated
     */
    public static UserPrincipal getCurrentUser() {
        UserPrincipal principal = CURRENT_USER.get();
        return principal != null ? principal : UserPrincipal.anonymous();
    }

    /**
     * Checks if there is an authenticated user in the current context.
     *
     * @return true if user is authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        return getCurrentUser().isAuthenticated();
    }

    /**
     * Clears the security context for this thread.
     * This should be called after request processing completes.
     */
    public static void clear() {
        CURRENT_USER.remove();
    }

    /**
     * Gets the current user's ID.
     *
     * @return The user ID, or null if not authenticated
     */
    public static String getCurrentUserId() {
        return getCurrentUser().getUserId();
    }

    /**
     * Gets the current username.
     *
     * @return The username, or "anonymous" if not authenticated
     */
    public static String getCurrentUsername() {
        return getCurrentUser().getUsername();
    }
}