package com.chapman.edu.commissions.fundamentals.dynamic;

import com.chapman.edu.commissions.model.*;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Dynamic Tests for Model Object Validation
 * 
 * This class focuses on testing model object validation scenarios
 * using JUnit's dynamic test capabilities. It demonstrates:
 * 
 * 1. Data-driven testing - Tests generated from datasets
 * 2. Edge case validation - Boundary condition testing
 * 3. State transition testing - Object state change validation
 * 4. Cross-object relationship testing - Inter-model dependencies
 */
public class ModelValidationDynamicTest {

    /**
     * Dynamic tests for Deal object validation with edge cases
     * 
     * Tests various edge cases and boundary conditions for Deal objects,
     * including null values, empty collections, and extreme values.
     * 
     * @return Stream of DynamicTest instances for Deal edge case validation
     */
    @TestFactory
    Stream<DynamicTest> dealEdgeCaseTests() {
        // Edge case test scenarios for Deal validation
        List<ValidationTestCase> testCases = Arrays.asList(
            new ValidationTestCase(
                "Deal with Null Title",
                () -> new Deal(null, new BigDecimal("1000"), "REP001"),
                deal -> assertNull(((Deal) deal).getTitle())
            ),
            
            new ValidationTestCase(
                "Deal with Zero Value",
                () -> new Deal("Zero Deal", BigDecimal.ZERO, "REP001"),
                deal -> assertEquals(BigDecimal.ZERO, ((Deal) deal).getValue())
            ),
            
            new ValidationTestCase(
                "Deal with Negative Value",
                () -> new Deal("Negative Deal", new BigDecimal("-500"), "REP001"),
                deal -> assertTrue(((Deal) deal).getValue().compareTo(BigDecimal.ZERO) < 0)
            ),
            
            new ValidationTestCase(
                "Deal with Empty Product List",
                () -> {
                    Deal deal = new Deal("Empty Products", new BigDecimal("1000"), "REP001");
                    return deal;
                },
                deal -> {
                    assertTrue(((Deal) deal).getProducts().isEmpty());
                    assertEquals(BigDecimal.ZERO, ((Deal) deal).calculateTotalValue());
                }
            ),
            
            new ValidationTestCase(
                "Deal Equality with Null IDs",
                () -> {
                    Deal deal1 = new Deal("Test Deal", new BigDecimal("1000"), "REP001");
                    Deal deal2 = new Deal("Test Deal", new BigDecimal("1000"), "REP001");
                    // Both deals have null IDs by default
                    return new Deal[] {deal1, deal2};
                },
                deals -> {
                    Deal[] dealArray = (Deal[]) deals;
                    // Deals with null IDs should not be equal (as per equals implementation)
                    assertNotEquals(dealArray[0], dealArray[1]);
                }
            )
        );

        return testCases.stream()
            .map(testCase -> DynamicTest.dynamicTest(
                testCase.name,
                () -> testCase.validator.accept(testCase.supplier.get())
            ));
    }

    /**
     * Dynamic tests for User object state transitions
     * 
     * Tests user state changes including role assignments,
     * activation/deactivation, and login tracking.
     * 
     * @return Stream of DynamicTest instances for User state testing
     */
    @TestFactory
    Stream<DynamicTest> userStateTransitionTests() {
        return Stream.of(
            DynamicTest.dynamicTest("User Role Addition and Removal", () -> {
                User user = new User("testuser", "test@example.com", "John", "Doe");
                
                // Initially no roles
                assertTrue(user.getRoles().isEmpty());
                assertFalse(user.isSalesRep());
                
                // Add sales rep role
                user.addRole(UserRole.SALES_REP);
                assertTrue(user.isSalesRep());
                assertEquals(1, user.getRoles().size());
                
                // Add manager role
                user.addRole(UserRole.SALES_MANAGER);
                assertTrue(user.isSalesManager());
                assertEquals(2, user.getRoles().size());
            }),
            
            DynamicTest.dynamicTest("User Activation State Changes", () -> {
                User user = new User("testuser", "test@example.com", "John", "Doe");
                
                // User is active by default
                assertTrue(user.isActive());
                
                // Deactivate user
                user.setActive(false);
                assertFalse(user.isActive());
                
                // Reactivate user
                user.setActive(true);
                assertTrue(user.isActive());
            }),
            
            DynamicTest.dynamicTest("User Full Name Generation", () -> {
                User user = new User("jdoe", "john.doe@example.com", "John", "Doe");
                assertEquals("John Doe", user.getFullName());
                
                // Test with different names
                user.setFirstName("Jane");
                user.setLastName("Smith");
                assertEquals("Jane Smith", user.getFullName());
            })
        );
    }

    /**
     * Dynamic tests for DealProduct calculation scenarios
     * 
     * Tests various product calculation scenarios including
     * quantity changes, price updates, and discount applications.
     * 
     * @return Stream of DynamicTest instances for product calculations
     */
    @TestFactory
    Stream<DynamicTest> dealProductCalculationScenarios() {
        // Test data: [description, quantity, price, discount, expectedResult]
        Object[][] calculationData = {
            {"Standard Calculation", 2, "100.00", "0.00", "200.00"},
            {"With Small Discount", 3, "50.00", "10.00", "140.00"},
            {"With Large Discount", 1, "1000.00", "200.00", "800.00"},
            {"Discount Equals Price", 1, "100.00", "100.00", "0.00"},
            {"High Quantity", 100, "5.00", "50.00", "450.00"},
            {"Fractional Price", 2, "99.99", "0.01", "199.97"}
        };

        return Arrays.stream(calculationData)
            .map(data -> DynamicTest.dynamicTest(
                "Product Calculation: " + data[0],
                () -> {
                    DealProduct product = new DealProduct(
                        "PROD001",
                        "Test Product",
                        (Integer) data[1],
                        new BigDecimal((String) data[2])
                    );
                    product.setDiscount(new BigDecimal((String) data[3]));
                    
                    BigDecimal expected = new BigDecimal((String) data[4]);
                    BigDecimal actual = product.calculateTotalPrice();
                    
                    assertEquals(0, expected.compareTo(actual), 
                        String.format("Expected %s but got %s", expected, actual));
                }
            ));
    }

    /**
     * Dynamic tests for CommissionCalculation complex scenarios
     * 
     * Tests commission calculations with multiple bonuses,
     * accelerators, and status transitions.
     * 
     * @return Stream of DynamicTest instances for complex commission scenarios
     */
    @TestFactory
    Stream<DynamicTest> commissionComplexScenarios() {
        return Stream.of(
            DynamicTest.dynamicTest("Multiple Bonuses Calculation", () -> {
                CommissionCalculation calc = new CommissionCalculation(
                    "DEAL001", "REP001", new BigDecimal("1000")
                );
                
                // Add multiple bonuses
                BonusCalculation bonus1 = new BonusCalculation();
                bonus1.setAmount(new BigDecimal("200"));
                calc.addBonus(bonus1);
                
                BonusCalculation bonus2 = new BonusCalculation();
                bonus2.setAmount(new BigDecimal("150"));
                calc.addBonus(bonus2);
                
                calc.recalculate();
                
                // Base (1000) + Bonus1 (200) + Bonus2 (150) = 1350
                assertEquals(new BigDecimal("1350"), calc.getGrossCommission());
            }),
            
            DynamicTest.dynamicTest("Multiple Accelerators Calculation", () -> {
                CommissionCalculation calc = new CommissionCalculation(
                    "DEAL002", "REP002", new BigDecimal("1000")
                );
                
                // Add multiple accelerators (they multiply)
                AcceleratorCalculation acc1 = new AcceleratorCalculation();
                acc1.setMultiplier(new BigDecimal("1.2")); // 20% increase
                calc.addAccelerator(acc1);
                
                AcceleratorCalculation acc2 = new AcceleratorCalculation();
                acc2.setMultiplier(new BigDecimal("1.1")); // 10% increase
                calc.addAccelerator(acc2);
                
                calc.recalculate();
                
                // Base (1000) * 1.2 * 1.1 = 1320
                assertEquals(new BigDecimal("1320.00"), calc.getGrossCommission());
            }),
            
            DynamicTest.dynamicTest("Combined Bonuses and Accelerators", () -> {
                CommissionCalculation calc = new CommissionCalculation(
                    "DEAL003", "REP003", new BigDecimal("1000")
                );
                
                // Add bonus first
                BonusCalculation bonus = new BonusCalculation();
                bonus.setAmount(new BigDecimal("500"));
                calc.addBonus(bonus);
                
                // Then accelerator (applied to base + bonus)
                AcceleratorCalculation accelerator = new AcceleratorCalculation();
                accelerator.setMultiplier(new BigDecimal("1.5"));
                calc.addAccelerator(accelerator);
                
                calc.recalculate();
                
                // (Base 1000 + Bonus 500) * Accelerator 1.5 = 2250
                assertEquals(new BigDecimal("2250.0"), calc.getGrossCommission());
            })
        );
    }

    /**
     * Dynamic tests for object equality and hash code validation
     * 
     * Tests equals() and hashCode() implementations across model objects
     * to ensure proper behavior in collections and comparisons.
     * 
     * @return Stream of DynamicTest instances for equality testing
     */
    @TestFactory
    Stream<DynamicTest> objectEqualityTests() {
        return Stream.of(
            DynamicTest.dynamicTest("Deal Equality with Same ID", () -> {
                Deal deal1 = new Deal("Test Deal", new BigDecimal("1000"), "REP001");
                deal1.setId("DEAL001");
                
                Deal deal2 = new Deal("Different Title", new BigDecimal("2000"), "REP002");
                deal2.setId("DEAL001");
                
                // Should be equal because they have the same ID
                assertEquals(deal1, deal2);
                assertEquals(deal1.hashCode(), deal2.hashCode());
            }),
            
            DynamicTest.dynamicTest("User Equality with Same ID", () -> {
                User user1 = new User("user1", "user1@example.com", "John", "Doe");
                user1.setId("USER001");
                
                User user2 = new User("user2", "user2@example.com", "Jane", "Smith");
                user2.setId("USER001");
                
                // Should be equal because they have the same ID
                assertEquals(user1, user2);
                assertEquals(user1.hashCode(), user2.hashCode());
            }),
            
            DynamicTest.dynamicTest("DealProduct Equality without ID", () -> {
                DealProduct product1 = new DealProduct("PROD001", "Laptop", 2, new BigDecimal("1500"));
                DealProduct product2 = new DealProduct("PROD001", "Laptop", 2, new BigDecimal("1500"));
                
                // Should be equal because all fields match and both have null IDs
                assertEquals(product1, product2);
                assertEquals(product1.hashCode(), product2.hashCode());
            })
        );
    }

    // Helper class for validation test cases
    private static class ValidationTestCase {
        final String name;
        final java.util.function.Supplier<Object> supplier;
        final java.util.function.Consumer<Object> validator;

        ValidationTestCase(String name, java.util.function.Supplier<Object> supplier, 
                          java.util.function.Consumer<Object> validator) {
            this.name = name;
            this.supplier = supplier;
            this.validator = validator;
        }
    }
}