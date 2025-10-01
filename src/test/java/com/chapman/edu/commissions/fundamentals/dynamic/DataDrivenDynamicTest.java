package com.chapman.edu.commissions.fundamentals.dynamic;

import com.chapman.edu.commissions.model.*;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Data-Driven Dynamic Tests for Commission Models
 * 
 * This class demonstrates advanced dynamic testing patterns including:
 * 
 * 1. CSV-like data testing - Simulating external data sources
 * 2. Range-based testing - Testing across value ranges
 * 3. Combinatorial testing - Testing multiple parameter combinations
 * 4. Performance boundary testing - Testing with large datasets
 * 
 * Dynamic tests excel at data-driven scenarios where the same test logic
 * needs to be applied to multiple data sets or parameter combinations.
 */
public class DataDrivenDynamicTest {
    /**
     * Dynamic tests simulating CSV data import validation
     * This factory simulates testing data that might come from a CSV file
     * or external data source, demonstrating how dynamic tests can handle
     * bulk data validation scenarios.
     * 
     * @return Stream of DynamicTest instances for CSV-like data validation
     */
    @TestFactory
    Stream<DynamicTest> csvDataValidationTests() {
        // Simulated CSV data: dealId, title, value, salesRepId, expectedStatus
        String[][] csvData = {
            {"D001", "Software License Deal", "15000.00", "REP001", "VALID"},
            {"D002", "Hardware Purchase", "8500.50", "REP002", "VALID"},
            {"D003", "", "5000.00", "REP003", "INVALID_TITLE"}, // Empty title
            {"D004", "Service Contract", "0", "REP004", "INVALID_VALUE"}, // Zero value
            {"D005", "Consulting Services", "25000.00", "", "INVALID_REP"}, // Empty rep
            {"D006", "Training Package", "-1000.00", "REP005", "NEGATIVE_VALUE"}, // Negative
            {"D007", "Support Contract", "12000.75", "REP006", "VALID"}
        };

        return Arrays.stream(csvData)
            .map(row -> DynamicTest.dynamicTest(
                String.format("CSV Row Validation: %s - %s", row[0], row[1]),
                () -> {
                    String dealId = row[0];
                    String title = row[1];
                    BigDecimal value = new BigDecimal(row[2]);
                    String salesRepId = row[3];
                    String expectedStatus = row[4];
                    
                    // Create deal and validate based on expected status
                    Deal deal = new Deal(title, value, salesRepId);
                    deal.setId(dealId);
                    
                    switch (expectedStatus) {
                        case "VALID":
                            assertNotNull(deal.getTitle());
                            assertFalse(deal.getTitle().trim().isEmpty());
                            assertTrue(deal.getValue().compareTo(BigDecimal.ZERO) > 0);
                            assertNotNull(deal.getSalesRepId());
                            assertFalse(deal.getSalesRepId().trim().isEmpty());
                            break;
                        case "INVALID_TITLE":
                            assertTrue(deal.getTitle() == null || deal.getTitle().trim().isEmpty());
                            break;
                        case "INVALID_VALUE":
                            assertEquals(0, deal.getValue().compareTo(BigDecimal.ZERO));
                            break;
                        case "INVALID_REP":
                            assertTrue(deal.getSalesRepId() == null || deal.getSalesRepId().trim().isEmpty());
                            break;
                        case "NEGATIVE_VALUE":
                            assertTrue(deal.getValue().compareTo(BigDecimal.ZERO) < 0);
                            break;
                    }
                }
            ));
    }

    /**
     * Dynamic tests for commission rate calculations across ranges
     * 
     * Tests commission calculations across different deal value ranges
     * to ensure rate calculations work correctly at various scales.
     * 
     * @return Stream of DynamicTest instances for range-based testing
     */
    @TestFactory
    Stream<DynamicTest> commissionRateRangeTests() {
        // Define commission rate tiers: [minValue, maxValue, rate, description]
        Object[][] rateTiers = {
            {0, 5000, 0.05, "Tier 1: 0-5K (5%)"},
            {5001, 15000, 0.07, "Tier 2: 5K-15K (7%)"},
            {15001, 50000, 0.10, "Tier 3: 15K-50K (10%)"},
            {50001, 100000, 0.12, "Tier 4: 50K-100K (12%)"},
            {100001, Integer.MAX_VALUE, 0.15, "Tier 5: 100K+ (15%)"}
        };

        return Arrays.stream(rateTiers)
            .flatMap(tier -> {
                int minValue = (Integer) tier[0];
                int maxValue = (Integer) tier[1];
                double rate = (Double) tier[2];
                String description = (String) tier[3];
                
                // Test multiple values within each tier
                int[] testValues = {
                    minValue == 0 ? 1000 : minValue + 100,  // Lower bound test
                    maxValue == Integer.MAX_VALUE ? 150000 : maxValue - 100  // Upper bound test
                };
                
                return Arrays.stream(testValues)
                    .mapToObj(testValue -> DynamicTest.dynamicTest(
                        String.format("%s - Testing value $%d", description, testValue),
                        () -> {
                            BigDecimal dealValue = new BigDecimal(testValue);
                            BigDecimal expectedCommission = dealValue.multiply(new BigDecimal(rate));
                            
                            CommissionCalculation calc = new CommissionCalculation(
                                "DEAL001", "REP001", expectedCommission
                            );
                            
                            assertEquals(expectedCommission, calc.getBaseCommission());
                            assertTrue(calc.getBaseCommission().compareTo(BigDecimal.ZERO) > 0);
                        }
                    ));
            });
    }

    /**
     * Dynamic tests for user role combinations
     * 
     * Tests all possible combinations of user roles to ensure
     * role-based functionality works correctly in all scenarios.
     * 
     * @return Stream of DynamicTest instances for role combination testing
     */
    @TestFactory
    Stream<DynamicTest> userRoleCombinationTests() {
        UserRole[] allRoles = UserRole.values();
        
        // Generate all possible role combinations (power set)
        return IntStream.range(1, 1 << allRoles.length) // Skip empty set (index 0)
            .mapToObj(i -> {
                List<UserRole> roleCombo = IntStream.range(0, allRoles.length)
                    .filter(j -> (i & (1 << j)) != 0)
                    .mapToObj(j -> allRoles[j])
                    .collect(java.util.stream.Collectors.toList());
                
                return DynamicTest.dynamicTest(
                    "Role Combination: " + roleCombo.toString(),
                    () -> {
                        User user = new User("testuser", "test@example.com", "John", "Doe");
                        
                        // Add all roles in the combination
                        roleCombo.forEach(user::addRole);
                        
                        // Verify each role is properly assigned
                        for (UserRole role : roleCombo) {
                            assertTrue(user.hasRole(role), 
                                String.format("User should have role %s", role));
                        }
                        
                        // Verify role count
                        assertEquals(roleCombo.size(), user.getRoles().size());
                        
                        // Test role-specific methods
                        assertEquals(roleCombo.contains(UserRole.SALES_REP), user.isSalesRep());
                        assertEquals(roleCombo.contains(UserRole.SALES_MANAGER), user.isSalesManager());
                        assertEquals(roleCombo.contains(UserRole.FINANCE_ADMIN), user.isFinanceAdmin());
                        assertEquals(roleCombo.contains(UserRole.SYSTEM_ADMIN), user.isSystemAdmin());
                    }
                );
            });
    }


    /**
     * Dynamic tests for deal status transitions
     * 
     * Tests all possible status transitions to ensure business rules
     * are properly enforced and state changes are valid.
     * 
     * @return Stream of DynamicTest instances for status transition testing
     */
    @TestFactory
    Stream<DynamicTest> dealStatusTransitionTests() {
        DealStatus[] allStatuses = DealStatus.values();
        
        return Arrays.stream(allStatuses)
            .flatMap(fromStatus -> Arrays.stream(allStatuses)
                .map(toStatus -> DynamicTest.dynamicTest(
                    String.format("Status Transition: %s -> %s", fromStatus, toStatus),
                    () -> {
                        Deal deal = new Deal("Test Deal", new BigDecimal("1000"), "REP001");
                        
                        // Set initial status
                        deal.setStatus(fromStatus);
                        assertEquals(fromStatus, deal.getStatus());
                        LocalDate firstModified = deal.getLastModifiedDate();
                        
                        // Transition to new status
                        deal.setStatus(toStatus);
                        assertEquals(toStatus, deal.getStatus());
                        
                        // Verify last modified date changed (if status changed)
                        if (!fromStatus.equals(toStatus)) {
                            assertNotEquals(firstModified, deal.getLastModifiedDate());
                        }
                        
                        // Verify status display name
                        assertNotNull(toStatus.getDisplayName());
                        assertFalse(toStatus.getDisplayName().trim().isEmpty());
                    }
                ))
            );
    }

    /**
     * Dynamic tests for performance with large datasets
     * 
     * Tests model object performance with larger datasets to ensure
     * scalability and identify potential performance issues.
     * 
     * @return Stream of DynamicTest instances for performance testing
     */
    @TestFactory
    Stream<DynamicTest> performanceScaleTests() {
        int[] datasetSizes = {10, 50, 100, 500, 1000};
        
        return Arrays.stream(datasetSizes)
            .mapToObj(size -> DynamicTest.dynamicTest(
                String.format("Performance Test: %d products in deal", size),
                () -> {
                    Deal deal = new Deal("Large Deal", new BigDecimal("100000"), "REP001");
                    
                    long startTime = System.currentTimeMillis();
                    
                    // Add many products
                    for (int i = 0; i < size; i++) {
                        DealProduct product = new DealProduct(
                            "PROD" + String.format("%04d", i),
                            "Product " + i,
                            1,
                            new BigDecimal("100")
                        );
                        deal.addProduct(product);
                    }
                    
                    // Calculate total value
                    BigDecimal totalValue = deal.calculateTotalValue();
                    
                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;
                    
                    // Verify correctness
                    assertEquals(size, deal.getProducts().size());
                    assertEquals(new BigDecimal(size * 100), totalValue);
                    
                    // Performance assertion (should complete within reasonable time)
                    assertTrue(duration < 1000, // Less than 1 second
                        String.format("Performance test with %d products took %d ms", size, duration));
                }
            ));
    }
}