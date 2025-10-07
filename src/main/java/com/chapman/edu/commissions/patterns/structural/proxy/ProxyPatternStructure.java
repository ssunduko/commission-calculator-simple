package com.chapman.edu.commissions.patterns.structural.proxy;

import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.CommissionCalculation;

/**
 * This class demonstrates the structure of the Proxy Pattern.
 * 
 * The Proxy Pattern provides a surrogate or placeholder for another object to control
 * access to it. It is a structural pattern that involves an interface, a real subject,
 * and a proxy subject. The proxy controls access to the real subject.
 * 
 * Key components:
 * - Subject: An interface that defines the common operations for RealSubject and Proxy
 * - RealSubject: The real object that the proxy represents
 * - Proxy: Maintains a reference to the RealSubject and controls access to it
 */
public class ProxyPatternStructure {

    /**
     * Subject interface that defines the common operations for RealSubject and Proxy
     */
    public interface UserService {
        User getUserById(String userId);
        void updateUser(User user);
    }

    /**
     * RealSubject class that implements the Subject interface
     */
    public static class RealUserService implements UserService {
        @Override
        public User getUserById(String userId) {
            System.out.println("RealUserService: Fetching user data from database for ID: " + userId);
            // In a real application, this would fetch the user from a database
            User user = new User();
            user.setId(userId);
            user.setUsername("user" + userId);
            user.setEmail("user" + userId + "@example.com");
            user.setFirstName("John");
            user.setLastName("Doe");
            return user;
        }

        @Override
        public void updateUser(User user) {
            System.out.println("RealUserService: Updating user in database: " + user.getUsername());
            // In a real application, this would update the user in a database
        }
    }

    /**
     * Proxy class that implements the Subject interface and controls access to RealSubject
     */
    public static class UserServiceProxy implements UserService {
        private RealUserService realUserService;
        private boolean hasAdminAccess;

        public UserServiceProxy(boolean hasAdminAccess) {
            this.realUserService = new RealUserService();
            this.hasAdminAccess = hasAdminAccess;
        }

        @Override
        public User getUserById(String userId) {
            System.out.println("UserServiceProxy: Forwarding request to get user by ID: " + userId);
            return realUserService.getUserById(userId);
        }

        @Override
        public void updateUser(User user) {
            if (hasAdminAccess) {
                System.out.println("UserServiceProxy: Access granted - forwarding update request");
                realUserService.updateUser(user);
            } else {
                System.out.println("UserServiceProxy: Access denied - user does not have admin privileges");
                throw new IllegalStateException("Access denied: Admin privileges required to update user information");
            }
        }
    }
}