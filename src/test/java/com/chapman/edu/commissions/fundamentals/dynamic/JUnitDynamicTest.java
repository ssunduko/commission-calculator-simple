package com.chapman.edu.commissions.fundamentals.dynamic;

import com.chapman.edu.commissions.model.*;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit Dynamic Tests for Commission Model Classes
 * 
 * Dynamic tests are created at runtime, allowing for flexible test generation
 * based on data sets, configurations, or other runtime conditions.
 * 
 * Key concepts demonstrated:
 * 1. @TestFactory - Marks methods that generate dynamic tests
 * 2. DynamicTest.dynamicTest() - Creates individual test instances
 * 3. Stream-based test generation - Efficient test creation from data
 * 4. Runtime test discovery - Tests are discovered and executed at runtime
 */
public class JUnitDynamicTest {

    /**
     * Dynamic tests for Deal model validation
     * 
     * This factory method generates multiple test cases for Deal objects
     * using different input scenarios. Each test case validates specific
     * aspects of Deal functionality.
     * 
     * @return Stream of DynamicTest instances for Deal validation
     */
    @TestFactory
    Stream<DynamicTest> dealValidationTests() {
        // Test data: Different deal scenarios to validate
        List<TestScenario<Deal>> scenarios = Arrays.asList(
            new TestScenario<>("Valid Deal Creation", 
                () -> new Deal("Software License", new BigDecimal("10000"), "REP001"),
                deal -> {
                    assertNotNull(deal.getTitle());
                    assertEquals("Software License", deal.getTitle());
                    assertEquals(new BigDecimal("10000"), deal.getValue());
                    assertEquals("REP001", deal.getSalesRepId());
                    assertEquals(DealStatus.OPEN, deal.getStatus());
                }),
            
            new TestScenario<>("Deal with Products", 
                () -> {
                    Deal deal = new Deal("Hardware Bundle", new BigDecimal("5000"), "REP002");
                    deal.addProduct(new DealProduct("PROD001", "Laptop", 2, new BigDecimal("1500")));
                    deal.addProduct(new DealProduct("PROD002", "Monitor", 4, new BigDecimal("500")));
                    return deal;
                },
                deal -> {
                    assertEquals(2, deal.getProducts().size());
                    assertEquals(new BigDecimal("5000"), deal.calculateTotalValue());
                }),
            
            new TestScenario<>("Deal Status Updates", 
                () -> {
                    Deal deal = new Deal("Service Contract", new BigDecimal("25000"), "REP003");
                    deal.setStatus(DealStatus.WON);
                    return deal;
                },
                deal -> {
                    assertEquals(DealStatus.WON, deal.getStatus());
                    assertNotNull(deal.getLastModifiedDate());
                })
        );

        // Generate dynamic tests from scenarios
        return scenarios.stream()
            .map(scenario -> DynamicTest.dynamicTest(
                scenario.name,
                () -> {
                    Deal deal = scenario.supplier.get();
                    scenario.validator.accept(deal);
                }
            ));
    }

    /**
     * Dynamic tests for User model role management
     * 
     * Tests various user role combinations and validates role-based
     * functionality. Demonstrates how dynamic tests can handle
     * complex object state validation.
     * 
     * @return Stream of DynamicTest instances for User role testing
     */
    @TestFactory
    Stream<DynamicTest> userRoleTests() {
        // Different user role configurations to test
        Object[][] roleTestData = {
            {"Sales Rep User", UserRole.SALES_REP, true, false, false, false},
            {"Sales Manager User", UserRole.SALES_MANAGER, false, true, false, false},
            {"Finance Admin User", UserRole.FINANCE_ADMIN, false, false, true, false},
            {"System Admin User", UserRole.SYSTEM_ADMIN, false, false, false, true}
        };

        return Arrays.stream(roleTestData)
            .map(data -> DynamicTest.dynamicTest(
                (String) data[0],
                () -> {
                    // Create user and assign role
                    User user = new User("testuser", "test@example.com", "John", "Doe");
                    user.addRole((UserRole) data[1]);
                    
                    // Validate role-specific methods
                    assertEquals((Boolean) data[2], user.isSalesRep(), "Sales Rep check failed");
                    assertEquals((Boolean) data[3], user.isSalesManager(), "Sales Manager check failed");
                    assertEquals((Boolean) data[4], user.isFinanceAdmin(), "Finance Admin check failed");
                    assertEquals((Boolean) data[5], user.isSystemAdmin(), "System Admin check failed");
                }
            ));
    }

    /**
     * Dynamic tests for CommissionCalculation scenarios
     * 
     * Generates tests for different commission calculation scenarios,
     * including base commissions, bonuses, and accelerators.
     * 
     * @return Stream of DynamicTest instances for commission calculations
     */
    @TestFactory
    Stream<DynamicTest> commissionCalculationTests() {
        // Commission calculation test scenarios
        List<CommissionTestCase> testCases = Arrays.asList(
            new CommissionTestCase(
                "Basic Commission Calculation",
                "DEAL001", "REP001", new BigDecimal("1000"),
                calc -> {
                    assertEquals(new BigDecimal("1000"), calc.getBaseCommission());
                    assertEquals(new BigDecimal("1000"), calc.calculateTotalCommission());
                    assertEquals(CommissionCalculation.CommissionStatus.CALCULATED, calc.getStatus());
                }
            ),
            
            new CommissionTestCase(
                "Commission with Bonus",
                "DEAL002", "REP002", new BigDecimal("2000"),
                calc -> {
                    // Add a bonus
                    BonusCalculation bonus = new BonusCalculation();
                    bonus.setAmount(new BigDecimal("500"));
                    calc.addBonus(bonus);
                    calc.recalculate();
                    
                    assertEquals(new BigDecimal("2500"), calc.getGrossCommission());
                }
            ),
            
            new CommissionTestCase(
                "Commission with Accelerator",
                "DEAL003", "REP003", new BigDecimal("1500"),
                calc -> {
                    // Add an accelerator (multiplier)
                    AcceleratorCalculation accelerator = new AcceleratorCalculation();
                    accelerator.setMultiplier(new BigDecimal("1.2"));
                    calc.addAccelerator(accelerator);
                    calc.recalculate();
                    
                    assertEquals(new BigDecimal("1800.0"), calc.getGrossCommission());
                }
            )
        );

        return testCases.stream()
            .map(testCase -> DynamicTest.dynamicTest(
                testCase.name,
                () -> {
                    CommissionCalculation calc = new CommissionCalculation(
                        testCase.dealId, testCase.salesRepId, testCase.baseAmount
                    );
                    testCase.validator.accept(calc);
                }
            ));
    }

    /**
     * Dynamic tests for DealProduct calculations
     * 
     * Tests product pricing calculations with various quantities,
     * prices, and discount scenarios.
     * 
     * @return Stream of DynamicTest instances for product calculations
     */
    @TestFactory
    Stream<DynamicTest> dealProductCalculationTests() {
        // Product calculation test data: [name, quantity, price, discount, expectedTotal]
        Object[][] productTestData = {
            {"Single Product No Discount", 1, "100.00", "0.00", "100.00"},
            {"Multiple Quantity No Discount", 5, "50.00", "0.00", "250.00"},
            {"Single Product With Discount", 1, "100.00", "10.00", "90.00"},
            {"Multiple Quantity With Discount", 3, "75.00", "25.00", "200.00"},
            {"Zero Discount Edge Case", 2, "150.00", "0.00", "300.00"}
        };

        return Arrays.stream(productTestData)
            .map(data -> DynamicTest.dynamicTest(
                (String) data[0],
                () -> {
                    DealProduct product = new DealProduct(
                        "PROD001", 
                        "Test Product", 
                        (Integer) data[1], 
                        new BigDecimal((String) data[2])
                    );
                    product.setDiscount(new BigDecimal((String) data[3]));
                    
                    BigDecimal expectedTotal = new BigDecimal((String) data[4]);
                    assertEquals(expectedTotal, product.calculateTotalPrice());
                }
            ));
    }

    /**
     * Dynamic tests for enum validation
     * 
     * Validates enum values and their display names across different
     * model enums. Demonstrates testing of enum behavior.
     * 
     * @return Stream of DynamicTest instances for enum validation
     */
    @TestFactory
    Stream<DynamicTest> enumValidationTests() {
        return Stream.of(
            // DealStatus enum tests
            DynamicTest.dynamicTest("DealStatus Enum Values", () -> {
                assertEquals("Open", DealStatus.OPEN.getDisplayName());
                assertEquals("Won", DealStatus.WON.getDisplayName());
                assertEquals("Lost", DealStatus.LOST.getDisplayName());
                assertEquals("Cancelled", DealStatus.CANCELLED.getDisplayName());
            }),
            
            // UserRole enum tests
            DynamicTest.dynamicTest("UserRole Enum Values", () -> {
                assertEquals("Sales Representative", UserRole.SALES_REP.getDisplayName());
                assertEquals("Sales Manager", UserRole.SALES_MANAGER.getDisplayName());
                assertEquals("Finance Administrator", UserRole.FINANCE_ADMIN.getDisplayName());
                assertEquals("System Administrator", UserRole.SYSTEM_ADMIN.getDisplayName());
            }),
            
            // CommissionStatus enum tests
            DynamicTest.dynamicTest("CommissionStatus Enum Values", () -> {
                assertEquals("Calculated", CommissionCalculation.CommissionStatus.CALCULATED.getDisplayName());
                assertEquals("Approved", CommissionCalculation.CommissionStatus.APPROVED.getDisplayName());
                assertEquals("Paid", CommissionCalculation.CommissionStatus.PAID.getDisplayName());
                assertEquals("Disputed", CommissionCalculation.CommissionStatus.DISPUTED.getDisplayName());
            })
        );
    }

    // Helper classes for test organization

    /**
     * Generic test scenario holder
     * Encapsulates test name, object creation, and validation logic
     */
    private static class TestScenario<T> {
        final String name;
        final java.util.function.Supplier<T> supplier;
        final java.util.function.Consumer<T> validator;

        TestScenario(String name, java.util.function.Supplier<T> supplier, java.util.function.Consumer<T> validator) {
            this.name = name;
            this.supplier = supplier;
            this.validator = validator;
        }
    }

    /**
     * Commission calculation test case holder
     * Specific structure for commission-related test scenarios
     */
    private static class CommissionTestCase {
        final String name;
        final String dealId;
        final String salesRepId;
        final BigDecimal baseAmount;
        final java.util.function.Consumer<CommissionCalculation> validator;

        CommissionTestCase(String name, String dealId, String salesRepId, BigDecimal baseAmount,
                          java.util.function.Consumer<CommissionCalculation> validator) {
            this.name = name;
            this.dealId = dealId;
            this.salesRepId = salesRepId;
            this.baseAmount = baseAmount;
            this.validator = validator;
        }
    }
}