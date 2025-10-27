package com.chapman.edu.commissions.api.rest.security;

import java.util.Objects;

/**
 * Represents an authentication token containing credentials.
 *
 * This class encapsulates the authentication credentials extracted from
 * HTTP requests. It supports different authentication schemes (Basic, Bearer, etc.).
 *
 * Design Pattern: Value Object
 * Immutable object representing authentication credentials.
 */
public class AuthenticationToken {

    private final String scheme;
    private final String credentials;
    private final String username;
    private final String password;

    /**
     * Private constructor for creating tokens.
     */
    private AuthenticationToken(String scheme, String credentials, String username, String password) {
        this.scheme = scheme;
        this.credentials = credentials;
        this.username = username;
        this.password = password;
    }

    /**
     * Creates a Basic authentication token.
     *
     * @param username The username
     * @param password The password
     * @return A new authentication token
     */
    public static AuthenticationToken basic(String username, String password) {
        return new AuthenticationToken("Basic", null, username, password);
    }

    /**
     * Creates a Bearer token (JWT).
     *
     * @param token The JWT token
     * @return A new authentication token
     */
    public static AuthenticationToken bearer(String token) {
        return new AuthenticationToken("Bearer", token, null, null);
    }

    /**
     * Creates an API key token.
     *
     * @param apiKey The API key
     * @return A new authentication token
     */
    public static AuthenticationToken apiKey(String apiKey) {
        return new AuthenticationToken("ApiKey", apiKey, null, null);
    }

    /**
     * Gets the authentication scheme.
     *
     * @return The scheme (e.g., "Basic", "Bearer")
     */
    public String getScheme() {
        return scheme;
    }

    /**
     * Gets the raw credentials string.
     *
     * @return The credentials (for Bearer/ApiKey tokens)
     */
    public String getCredentials() {
        return credentials;
    }

    /**
     * Gets the username (for Basic auth).
     *
     * @return The username, or null if not applicable
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the password (for Basic auth).
     *
     * @return The password, or null if not applicable
     */
    public String getPassword() {
        return password;
    }

    /**
     * Checks if this is a Basic authentication token.
     *
     * @return true if Basic auth, false otherwise
     */
    public boolean isBasic() {
        return "Basic".equalsIgnoreCase(scheme);
    }

    /**
     * Checks if this is a Bearer token (JWT).
     *
     * @return true if Bearer token, false otherwise
     */
    public boolean isBearer() {
        return "Bearer".equalsIgnoreCase(scheme);
    }

    /**
     * Checks if this is an API key token.
     *
     * @return true if API key, false otherwise
     */
    public boolean isApiKey() {
        return "ApiKey".equalsIgnoreCase(scheme);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthenticationToken that = (AuthenticationToken) o;
        return Objects.equals(scheme, that.scheme) &&
               Objects.equals(credentials, that.credentials) &&
               Objects.equals(username, that.username) &&
               Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scheme, credentials, username, password);
    }

    @Override
    public String toString() {
        // Don't include sensitive credentials in toString
        return "AuthenticationToken{scheme='" + scheme + "'}";
    }
}