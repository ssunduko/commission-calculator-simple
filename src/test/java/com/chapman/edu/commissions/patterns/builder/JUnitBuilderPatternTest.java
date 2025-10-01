package com.chapman.edu.commissions.patterns.builder;

import com.chapman.edu.commissions.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite demonstrating the Builder Pattern implementation
 * for commission system model objects.
 * 
 * The Builder Pattern is a creational design pattern that provides a flexible
 * solution for constructing complex objects step by step. This test class
 * demonstrates various aspects of the pattern including:
 * 
 * 1. Basic object construction with required fields
 * 2. Method chaining (fluent interface)
 * 3. Optional parameter handling
 * 4. Validation of required fields
 * 5. Default value assignment
 * 6. Complex object composition
 * 
 * Key Benefits Demonstrated:
 * - Improved readability compared to constructor overloading
 * - Flexible object construction with optional parameters
 * - Immutable object creation (if desired)
 * - Clear separation of construction logic
 * - Easy testing and maintenance
 */
@DisplayName("Builder Pattern Tests")
class JUnitBuilderPatternTest {

    /**
     * Tests for the DealBuilder class demonstrating various construction scenarios.
     */
    @Nested
    @DisplayName("Deal Builder Tests")
    class DealBuilderTests {

        @Test
        @DisplayName("Should create a basic deal with required fields only")
        void shouldCreateBasicDeal() {
            // Arrange & Act
            // The Builder Pattern allows us to create objects with a fluent, readable syntax
            Deal deal = DealBuilder.create()
                    .withTitle("Enterprise Software License")
                    .withSalesRepId("REP001")
                    .withValue(50000.00)
                    .build();

            // Assert
            // Verify that all required fields are set correctly
            assertNotNull(deal, "Deal should not be null");
            assertEquals("Enterprise Software License", deal.getTitle());
            assertEquals("REP001", deal.getSalesRepId());
            assertEquals(BigDecimal.valueOf(50000.00), deal.getValue());
            
            // Verify default values are applied
            assertEquals(DealStatus.OPEN, deal.getStatus());
            assertNotNull(deal.getProducts());
            assertTrue(deal.getProducts().isEmpty());
            assertNotNull(deal.getCreatedDate());
        }

        @Test
        @DisplayName("Should create a complex deal with all optional fields")
        void shouldCreateComplexDeal() {
            // Arrange
            LocalDate closeDate = LocalDate.of(2024, 12, 31);
            LocalDate createdDate = LocalDate.of(2024, 1, 15);
            
            // Create products using the DealProductBuilder
            DealProduct product1 = DealProductBuilder.create()
                    .withProduct("PROD001", "Software License")
                    .withQuantity(5)
                    .withPrice(10000.00)
                    .build();
                    
            DealProduct product2 = DealProductBuilder.create()
                    .withProduct("PROD002", "Support Package")
                    .withQuantity(1)
                    .withPrice(5000.00)
                    .withDiscount(500.00)
                    .build();

            // Act
            // Demonstrate the fluent interface with method chaining
            Deal deal = DealBuilder.create()
                    .withId("DEAL001")
                    .withTitle("Complete Enterprise Solution")
                    .withSalesRepId("REP001")
                    .withValue(54500.00)
                    .withStatus(DealStatus.WON)
                    .withProduct(product1)
                    .withProduct(product2)
                    .withCloseDate(closeDate)
                    .withCreatedDate(createdDate)
                    .build();

            // Assert
            assertNotNull(deal);
            assertEquals("DEAL001", deal.getId());
            assertEquals("Complete Enterprise Solution", deal.getTitle());
            assertEquals("REP001", deal.getSalesRepId());
            assertEquals(BigDecimal.valueOf(54500.00), deal.getValue());
            assertEquals(DealStatus.WON, deal.getStatus());
            assertEquals(closeDate, deal.getCloseDate());
            assertEquals(createdDate, deal.getCreatedDate());
            
            // Verify products were added correctly
            assertEquals(2, deal.getProducts().size());
            assertTrue(deal.getProducts().contains(product1));
            assertTrue(deal.getProducts().contains(product2));
        }

        @Test
        @DisplayName("Should throw exception when required fields are missing")
        void shouldThrowExceptionForMissingRequiredFields() {
            // Test missing title
            IllegalStateException titleException = assertThrows(
                IllegalStateException.class,
                () -> DealBuilder.create()
                        .withSalesRepId("REP001")
                        .withValue(10000.00)
                        .build(),
                "Should throw exception when title is missing"
            );
            assertEquals("Deal title is required", titleException.getMessage());

            // Test missing sales rep ID
            IllegalStateException salesRepException = assertThrows(
                IllegalStateException.class,
                () -> DealBuilder.create()
                        .withTitle("Test Deal")
                        .withValue(10000.00)
                        .build(),
                "Should throw exception when sales rep ID is missing"
            );
            assertEquals("Sales representative ID is required", salesRepException.getMessage());
        }

        @Test
        @DisplayName("Should demonstrate method chaining fluency")
        void shouldDemonstrateMethodChaining() {
            // This test shows how the Builder Pattern enables a fluent interface
            // where multiple method calls can be chained together in a readable way
            Deal deal = DealBuilder.create()
                    .withTitle("Method Chaining Demo")
                    .withSalesRepId("REP002")
                    .withValue(25000.00)
                    .withStatus(DealStatus.OPEN)
                    .withCloseDate(LocalDate.now().plusDays(30))
                    .build();

            assertNotNull(deal);
            assertEquals("Method Chaining Demo", deal.getTitle());
            assertEquals("REP002", deal.getSalesRepId());
            assertEquals(DealStatus.OPEN, deal.getStatus());
        }
    }

    /**
     * Tests for the UserBuilder class demonstrating user object construction.
     */
    @Nested
    @DisplayName("User Builder Tests")
    class UserBuilderTests {

        @Test
        @DisplayName("Should create a basic user with required fields")
        void shouldCreateBasicUser() {
            // Act
            User user = UserBuilder.create()
                    .withUsername("john.doe")
                    .withEmail("john.doe@company.com")
                    .withFirstName("John")
                    .withLastName("Doe")
                    .build();

            // Assert
            assertNotNull(user);
            assertEquals("john.doe", user.getUsername());
            assertEquals("john.doe@company.com", user.getEmail());
            assertEquals("John", user.getFirstName());
            assertEquals("Doe", user.getLastName());
            assertEquals("John Doe", user.getFullName());
            
            // Verify default values
            assertTrue(user.isActive());
            assertNotNull(user.getRoles());
            assertTrue(user.getRoles().isEmpty());
            assertNotNull(user.getCreatedDate());
        }

        @Test
        @DisplayName("Should create a sales representative with roles")
        void shouldCreateSalesRepresentative() {
            // Act
            User salesRep = UserBuilder.create()
                    .withId("USER001")
                    .withUsername("jane.smith")
                    .withEmail("jane.smith@company.com")
                    .withName("Jane", "Smith")  // Convenience method
                    .withRole(UserRole.SALES_REP)
                    .withDepartment("Sales")
                    .withTerritory("West Coast")
                    .withManagerId("MGR001")
                    .build();

            // Assert
            assertNotNull(salesRep);
            assertEquals("USER001", salesRep.getId());
            assertEquals("jane.smith", salesRep.getUsername());
            assertEquals("Jane Smith", salesRep.getFullName());
            assertTrue(salesRep.hasRole(UserRole.SALES_REP));
            assertTrue(salesRep.isSalesRep());
            assertEquals("Sales", salesRep.getDepartment());
            assertEquals("West Coast", salesRep.getTerritory());
            assertEquals("MGR001", salesRep.getManagerId());
        }

        @Test
        @DisplayName("Should create a manager with multiple roles")
        void shouldCreateManagerWithMultipleRoles() {
            // Act
            User manager = UserBuilder.create()
                    .withUsername("bob.manager")
                    .withEmail("bob.manager@company.com")
                    .withName("Bob", "Manager")
                    .withRoles(UserRole.SALES_MANAGER, UserRole.SALES_REP)  // Multiple roles
                    .withDepartment("Sales")
                    .withLastLogin(LocalDateTime.now().minusHours(2))
                    .build();

            // Assert
            assertNotNull(manager);
            assertTrue(manager.hasRole(UserRole.SALES_MANAGER));
            assertTrue(manager.hasRole(UserRole.SALES_REP));
            assertTrue(manager.isSalesManager());
            assertTrue(manager.isSalesRep());
            assertEquals(2, manager.getRoles().size());
            assertNotNull(manager.getLastLogin());
        }

        @Test
        @DisplayName("Should create inactive user")
        void shouldCreateInactiveUser() {
            // Act
            User inactiveUser = UserBuilder.create()
                    .withUsername("former.employee")
                    .withEmail("former.employee@company.com")
                    .withName("Former", "Employee")
                    .inactive()  // Convenience method for setting active = false
                    .build();

            // Assert
            assertNotNull(inactiveUser);
            assertFalse(inactiveUser.isActive());
        }

        @Test
        @DisplayName("Should validate required fields for user creation")
        void shouldValidateRequiredUserFields() {
            // Test missing username
            assertThrows(IllegalStateException.class,
                () -> UserBuilder.create()
                        .withEmail("test@company.com")
                        .withName("Test", "User")
                        .build());

            // Test missing email
            assertThrows(IllegalStateException.class,
                () -> UserBuilder.create()
                        .withUsername("testuser")
                        .withName("Test", "User")
                        .build());

            // Test missing first name
            assertThrows(IllegalStateException.class,
                () -> UserBuilder.create()
                        .withUsername("testuser")
                        .withEmail("test@company.com")
                        .withLastName("User")
                        .build());

            // Test missing last name
            assertThrows(IllegalStateException.class,
                () -> UserBuilder.create()
                        .withUsername("testuser")
                        .withEmail("test@company.com")
                        .withFirstName("Test")
                        .build());
        }
    }

    /**
     * Tests for the DealProductBuilder class.
     */
    @Nested
    @DisplayName("Deal Product Builder Tests")
    class DealProductBuilderTests {

        @Test
        @DisplayName("Should create a basic deal product")
        void shouldCreateBasicDealProduct() {
            // Act
            DealProduct product = DealProductBuilder.create()
                    .withProductId("PROD001")
                    .withProductName("Software License")
                    .withQuantity(3)
                    .withPrice(1000.00)
                    .build();

            // Assert
            assertNotNull(product);
            assertEquals("PROD001", product.getProductId());
            assertEquals("Software License", product.getProductName());
            assertEquals(3, product.getQuantity());
            assertEquals(BigDecimal.valueOf(1000.00), product.getPrice());
            assertEquals(BigDecimal.ZERO, product.getDiscount());
        }

        @Test
        @DisplayName("Should create deal product with discount")
        void shouldCreateDealProductWithDiscount() {
            // Act
            DealProduct product = DealProductBuilder.create()
                    .withProduct("PROD002", "Support Package")  // Convenience method
                    .withQuantity(1)
                    .withPrice(5000.00)
                    .withDiscount(500.00)
                    .withDealId("DEAL001")
                    .build();

            // Assert
            assertNotNull(product);
            assertEquals("PROD002", product.getProductId());
            assertEquals("Support Package", product.getProductName());
            assertEquals(1, product.getQuantity());
            assertEquals(BigDecimal.valueOf(5000.00), product.getPrice());
            assertEquals(BigDecimal.valueOf(500.00), product.getDiscount());
            assertEquals("DEAL001", product.getDealId());
            
            // Test calculated total price
            BigDecimal expectedTotal = BigDecimal.valueOf(4500.00); // 5000 * 1 - 500
            assertEquals(expectedTotal, product.calculateTotalPrice());
        }

        @Test
        @DisplayName("Should validate required fields for deal product")
        void shouldValidateRequiredDealProductFields() {
            // Test missing product ID
            assertThrows(IllegalStateException.class,
                () -> DealProductBuilder.create()
                        .withProductName("Test Product")
                        .withQuantity(1)
                        .withPrice(100.00)
                        .build());

            // Test missing product name
            assertThrows(IllegalStateException.class,
                () -> DealProductBuilder.create()
                        .withProductId("PROD001")
                        .withQuantity(1)
                        .withPrice(100.00)
                        .build());

            // Test missing price
            assertThrows(IllegalStateException.class,
                () -> DealProductBuilder.create()
                        .withProductId("PROD001")
                        .withProductName("Test Product")
                        .withQuantity(1)
                        .build());

            // Test invalid quantity
            assertThrows(IllegalStateException.class,
                () -> DealProductBuilder.create()
                        .withProductId("PROD001")
                        .withProductName("Test Product")
                        .withQuantity(0)
                        .withPrice(100.00)
                        .build());
        }
    }

    /**
     * Integration tests demonstrating how builders work together to create complex object graphs.
     */
    @Nested
    @DisplayName("Builder Integration Tests")
    class BuilderIntegrationTests {

        @Test
        @DisplayName("Should create complete deal with products using multiple builders")
        void shouldCreateCompleteDealWithProducts() {
            // Arrange - Create products using DealProductBuilder
            DealProduct license = DealProductBuilder.create()
                    .withProduct("LIC001", "Enterprise License")
                    .withQuantity(10)
                    .withPrice(2000.00)
                    .build();

            DealProduct support = DealProductBuilder.create()
                    .withProduct("SUP001", "Premium Support")
                    .withQuantity(1)
                    .withPrice(5000.00)
                    .withDiscount(1000.00)
                    .build();

            DealProduct training = DealProductBuilder.create()
                    .withProduct("TRN001", "Training Package")
                    .withQuantity(2)
                    .withPrice(1500.00)
                    .build();

            // Act - Create deal using DealBuilder with the products
            Deal completeDeal = DealBuilder.create()
                    .withId("DEAL_COMPLETE_001")
                    .withTitle("Complete Enterprise Package")
                    .withSalesRepId("REP001")
                    .withStatus(DealStatus.WON)
                    .withProduct(license)
                    .withProduct(support)
                    .withProduct(training)
                    .withCloseDate(LocalDate.now())
                    .build();

            BigDecimal expectedTotal = BigDecimal.valueOf(28000.00);

            // Assert
            assertNotNull(completeDeal);
            assertEquals("DEAL_COMPLETE_001", completeDeal.getId());
            assertEquals("Complete Enterprise Package", completeDeal.getTitle());
            assertEquals(DealStatus.WON, completeDeal.getStatus());
            assertEquals(3, completeDeal.getProducts().size());
            
            // Verify the calculated total matches our expectation
            assertEquals(expectedTotal, completeDeal.calculateTotalValue());
        }

        @Test
        @DisplayName("Should demonstrate builder pattern benefits over constructor overloading")
        void shouldDemonstrateBuilderPatternBenefits() {
            /*
             * This test demonstrates why the Builder Pattern is superior to constructor overloading:
             * 
             * 1. READABILITY: Method names clearly indicate what each parameter represents
             * 2. FLEXIBILITY: Can set parameters in any order
             * 3. OPTIONAL PARAMETERS: Easy to handle optional parameters without multiple constructors
             * 4. VALIDATION: Can validate the complete object before construction
             * 5. IMMUTABILITY: Can create immutable objects easily
             * 6. MAINTAINABILITY: Adding new parameters doesn't break existing code
             */

            // Without Builder Pattern, you might need multiple constructors like:
            // new Deal(title, salesRepId)
            // new Deal(title, salesRepId, value)
            // new Deal(title, salesRepId, value, status)
            // new Deal(title, salesRepId, value, status, closeDate)
            // ... and so on, leading to constructor explosion

            // With Builder Pattern, we have a clean, readable, and flexible approach:
            Deal deal1 = DealBuilder.create()
                    .withTitle("Minimal Deal")
                    .withSalesRepId("REP001")
                    .build();

            Deal deal2 = DealBuilder.create()
                    .withTitle("Complex Deal")
                    .withSalesRepId("REP002")
                    .withValue(100000.00)
                    .withStatus(DealStatus.CANCELLED)
                    .withCloseDate(LocalDate.now().plusDays(15))
                    .build();

            // Both deals are created with the same builder, but with different configurations
            assertNotNull(deal1);
            assertNotNull(deal2);
            assertNotEquals(deal1.getTitle(), deal2.getTitle());
            
            // The builder pattern makes the code self-documenting and easy to understand
            assertTrue(deal1.getTitle().contains("Minimal"));
            assertTrue(deal2.getTitle().contains("Complex"));
        }
    }
}