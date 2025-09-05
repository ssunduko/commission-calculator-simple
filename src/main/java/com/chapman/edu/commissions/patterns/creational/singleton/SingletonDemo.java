package com.chapman.edu.commissions.patterns.creational.singleton;

import com.chapman.edu.commissions.model.CommissionCalculation;
import java.math.BigDecimal;

/**
 * Demonstration of different Singleton implementations.
 * 
 * This class shows how to use the different Singleton implementations
 * and verifies that they work correctly.
 */
public class SingletonDemo {
    
    /**
     * Main method to demonstrate the Singleton implementations.
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        // Demonstrate Basic Singleton
        demonstrateBasicSingleton();
        
        // Demonstrate Thread-Safe Singleton - Synchronized Method
        demonstrateSynchronizedMethodSingleton();
        
        // Demonstrate Thread-Safe Singleton - Double-Checked Locking
        demonstrateDoubleCheckedLockingSingleton();
        
        // Demonstrate Thread-Safe Singleton - Eager Initialization
        demonstrateEagerInitializationSingleton();
        
        // Demonstrate Thread-Safe Singleton - Initialization-on-Demand
        demonstrateInitializationOnDemandSingleton();
        
        // Demonstrate Enum Singleton
        demonstrateEnumSingleton();
    }
    
    /**
     * Demonstrate Basic Singleton
     */
    private static void demonstrateBasicSingleton() {
        System.out.println("\n=== Basic Singleton ===");
        
        // Get the singleton instance
        BasicSingleton singleton = BasicSingleton.getInstance();
        
        // Get the commission calculation
        CommissionCalculation calculation = singleton.getCommissionCalculation();
        System.out.println("Initial calculation ID: " + calculation.getId());
        
        // Calculate a commission
        CommissionCalculation newCalculation = singleton.calculateCommission(
                "DEAL-001", "SALES-001", new BigDecimal("1000.00"));
        System.out.println("New calculation amount: " + newCalculation.getNetCommission());
        
        // Verify that we get the same instance
        BasicSingleton anotherReference = BasicSingleton.getInstance();
        System.out.println("Same instance: " + (singleton == anotherReference));
    }
    
    /**
     * Demonstrate Thread-Safe Singleton - Synchronized Method
     */
    private static void demonstrateSynchronizedMethodSingleton() {
        System.out.println("\n=== Thread-Safe Singleton - Synchronized Method ===");
        
        // Get the singleton instance
        ThreadSafeSingletonSynchronizedMethod singleton = ThreadSafeSingletonSynchronizedMethod.getInstance();
        
        // Get the commission calculation
        CommissionCalculation calculation = singleton.getCommissionCalculation();
        System.out.println("Initial calculation ID: " + calculation.getId());
        
        // Calculate a commission
        CommissionCalculation newCalculation = singleton.calculateCommission(
                "DEAL-002", "SALES-002", new BigDecimal("2000.00"));
        System.out.println("New calculation amount: " + newCalculation.getNetCommission());
        
        // Verify that we get the same instance
        ThreadSafeSingletonSynchronizedMethod anotherReference = ThreadSafeSingletonSynchronizedMethod.getInstance();
        System.out.println("Same instance: " + (singleton == anotherReference));
    }
    
    /**
     * Demonstrate Thread-Safe Singleton - Double-Checked Locking
     */
    private static void demonstrateDoubleCheckedLockingSingleton() {
        System.out.println("\n=== Thread-Safe Singleton - Double-Checked Locking ===");
        
        // Get the singleton instance
        ThreadSafeSingletonDoubleCheckedLocking singleton = ThreadSafeSingletonDoubleCheckedLocking.getInstance();
        
        // Get the commission calculation
        CommissionCalculation calculation = singleton.getCommissionCalculation();
        System.out.println("Initial calculation ID: " + calculation.getId());
        
        // Calculate a commission
        CommissionCalculation newCalculation = singleton.calculateCommission(
                "DEAL-003", "SALES-003", new BigDecimal("3000.00"));
        System.out.println("New calculation amount: " + newCalculation.getNetCommission());
        
        // Verify that we get the same instance
        ThreadSafeSingletonDoubleCheckedLocking anotherReference = ThreadSafeSingletonDoubleCheckedLocking.getInstance();
        System.out.println("Same instance: " + (singleton == anotherReference));
    }
    
    /**
     * Demonstrate Thread-Safe Singleton - Eager Initialization
     */
    private static void demonstrateEagerInitializationSingleton() {
        System.out.println("\n=== Thread-Safe Singleton - Eager Initialization ===");
        
        // Get the singleton instance
        ThreadSafeSingletonEagerInitialization singleton = ThreadSafeSingletonEagerInitialization.getInstance();
        
        // Get the commission calculation
        CommissionCalculation calculation = singleton.getCommissionCalculation();
        System.out.println("Initial calculation ID: " + calculation.getId());
        
        // Calculate a commission
        CommissionCalculation newCalculation = singleton.calculateCommission(
                "DEAL-004", "SALES-004", new BigDecimal("4000.00"));
        System.out.println("New calculation amount: " + newCalculation.getNetCommission());
        
        // Verify that we get the same instance
        ThreadSafeSingletonEagerInitialization anotherReference = ThreadSafeSingletonEagerInitialization.getInstance();
        System.out.println("Same instance: " + (singleton == anotherReference));
    }
    
    /**
     * Demonstrate Thread-Safe Singleton - Initialization-on-Demand
     */
    private static void demonstrateInitializationOnDemandSingleton() {
        System.out.println("\n=== Thread-Safe Singleton - Initialization-on-Demand ===");
        
        // Get the singleton instance
        ThreadSafeSingletonInitializationOnDemand singleton = ThreadSafeSingletonInitializationOnDemand.getInstance();
        
        // Get the commission calculation
        CommissionCalculation calculation = singleton.getCommissionCalculation();
        System.out.println("Initial calculation ID: " + calculation.getId());
        
        // Calculate a commission
        CommissionCalculation newCalculation = singleton.calculateCommission(
                "DEAL-005", "SALES-005", new BigDecimal("5000.00"));
        System.out.println("New calculation amount: " + newCalculation.getNetCommission());
        
        // Verify that we get the same instance
        ThreadSafeSingletonInitializationOnDemand anotherReference = ThreadSafeSingletonInitializationOnDemand.getInstance();
        System.out.println("Same instance: " + (singleton == anotherReference));
    }
    
    /**
     * Demonstrate Enum Singleton
     */
    private static void demonstrateEnumSingleton() {
        System.out.println("\n=== Enum Singleton ===");
        
        // Get the singleton instance
        EnumSingleton singleton = EnumSingleton.INSTANCE;
        
        // Get the commission calculation
        CommissionCalculation calculation = singleton.getCommissionCalculation();
        System.out.println("Initial calculation ID: " + calculation.getId());
        
        // Calculate a commission
        CommissionCalculation newCalculation = singleton.calculateCommission(
                "DEAL-006", "SALES-006", new BigDecimal("6000.00"));
        System.out.println("New calculation amount: " + newCalculation.getNetCommission());
        
        // Verify that we get the same instance
        EnumSingleton anotherReference = EnumSingleton.INSTANCE;
        System.out.println("Same instance: " + (singleton == anotherReference));
    }
}