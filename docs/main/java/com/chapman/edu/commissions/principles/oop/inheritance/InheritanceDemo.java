package com.chapman.edu.commissions.principles.oop.inheritance;

import java.math.BigDecimal;

/**
 * This class demonstrates how inheritance works with the commission classes.
 * It shows:
 * 1. Creating instances of the base and derived classes
 * 2. How derived classes inherit properties and methods from the base class
 * 3. Method overriding in action
 * 4. Polymorphic behavior (treating derived class objects as base class objects)
 */
public class InheritanceDemo {
    
    public static void main(String[] args) {
        System.out.println("Inheritance Demonstration\n");
        
        // Create an instance of the base class
        BaseCommission baseCommission = new BaseCommission(
            "DEAL-001", 
            "SALES-001", 
            new BigDecimal("1000.00")
        );
        baseCommission.setDescription("Standard commission for basic deal");
        
        // Create an instance of the BonusCommission class
        BonusCommission bonusCommission = new BonusCommission(
            "DEAL-002", 
            "SALES-002", 
            new BigDecimal("1000.00"), 
            "PERFORMANCE", 
            "Exceeded quarterly target", 
            new BigDecimal("250.00")
        );
        
        // Create an instance of the AcceleratedCommission class
        AcceleratedCommission acceleratedCommission = new AcceleratedCommission(
            "DEAL-003", 
            "SALES-003", 
            new BigDecimal("1000.00"), 
            new BigDecimal("1.5"), 
            "TIER_ACHIEVEMENT", 
            "Reached Gold tier status"
        );
        
        // Demonstrate inheritance of properties and methods
        System.out.println("1. Inheritance of Properties and Methods:");
        System.out.println("Base Commission Deal ID: " + baseCommission.getDealId());
        System.out.println("Bonus Commission Deal ID: " + bonusCommission.getDealId());
        System.out.println("Accelerated Commission Deal ID: " + acceleratedCommission.getDealId());
        System.out.println();
        
        // Demonstrate method overriding
        System.out.println("2. Method Overriding:");
        System.out.println("Base Commission Calculation: " + baseCommission.calculateCommission());
        System.out.println("Bonus Commission Calculation: " + bonusCommission.calculateCommission());
        System.out.println("Accelerated Commission Calculation: " + acceleratedCommission.calculateCommission());
        System.out.println();
        
        System.out.println("3. Overridden getSummary Method:");
        System.out.println(baseCommission.getSummary());
        System.out.println(bonusCommission.getSummary());
        System.out.println(acceleratedCommission.getSummary());
        System.out.println();
        
        // Demonstrate polymorphism
        System.out.println("4. Polymorphism (treating derived classes as base class):");
        displayCommissionInfo(baseCommission);
        displayCommissionInfo(bonusCommission);
        displayCommissionInfo(acceleratedCommission);
        System.out.println();
        
        // Demonstrate specific methods in derived classes
        System.out.println("5. Specific Methods in Derived Classes:");
        System.out.println("Bonus Percentage: " + bonusCommission.getBonusPercentage() + "%");
        System.out.println("Has Acceleration: " + acceleratedCommission.hasAcceleration());
        System.out.println("Acceleration Percentage: " + acceleratedCommission.getAccelerationPercentage() + "%");
        System.out.println();
        
        // Demonstrate instanceof operator
        System.out.println("6. Using instanceof Operator:");
        checkInstanceType(baseCommission);
        checkInstanceType(bonusCommission);
        checkInstanceType(acceleratedCommission);
        
        System.out.println("\nInheritance Benefits:");
        System.out.println("1. Code Reusability: All commission types reuse code from BaseCommission");
        System.out.println("2. Extensibility: New commission types can be added by extending BaseCommission");
        System.out.println("3. Method Overriding: Derived classes can provide specialized implementations");
        System.out.println("4. Polymorphism: Base class references can point to derived class objects");
    }
    
    /**
     * This method demonstrates polymorphism by accepting any type of BaseCommission.
     * It works with BaseCommission and any class that extends BaseCommission.
     */
    private static void displayCommissionInfo(BaseCommission commission) {
        System.out.println("Commission for Deal " + commission.getDealId() + 
                          ": Amount = " + commission.calculateCommission());
    }
    
    /**
     * This method demonstrates the use of instanceof operator to determine the actual type.
     */
    private static void checkInstanceType(BaseCommission commission) {
        if (commission instanceof AcceleratedCommission) {
            System.out.println(commission.getDealId() + " is an AcceleratedCommission");
        } else if (commission instanceof BonusCommission) {
            System.out.println(commission.getDealId() + " is a BonusCommission");
        } else if (commission instanceof BaseCommission) {
            System.out.println(commission.getDealId() + " is a BaseCommission");
        }
    }
}