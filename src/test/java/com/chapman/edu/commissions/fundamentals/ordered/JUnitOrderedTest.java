package com.chapman.edu.commissions.fundamentals.ordered;

import com.chapman.edu.commissions.model.CommissionPlan;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;
import com.chapman.edu.commissions.model.PlanStatus;
import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.UserRole;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class demonstrates JUnit 5 test execution order concepts.
 * 
 * By default, JUnit 5 does not guarantee the order of test execution.
 * However, there are several ways to control the order of test execution:
 * 
 * 1. Using @TestMethodOrder annotation at the class level
 * 2. Using @Order annotation on individual test methods
 * 3. Using custom MethodOrderer implementations
 * 
 * This class demonstrates these concepts using the model classes from
 * the Commission Calculator application.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JUnitOrderedTest {

    // Static variables to be shared across test methods
    private static User salesRep;
    private static Deal deal;
    private static CommissionPlan commissionPlan;
    private static List<String> executionOrder = new ArrayList<>();

    /**
     * This method is executed once before all test methods in this class.
     * It initializes the shared variables that will be used by all tests.
     */
    @BeforeAll
    public static void setUpAll() {
        System.out.println("@BeforeAll - Initializing test data");

        // Initialize the sales rep
        salesRep = new User();
        salesRep.setId("sales-rep-1");
        salesRep.setUsername("sales.rep");
        salesRep.setEmail("sales.rep@example.com");
        salesRep.addRole(UserRole.SALES_REP);

        // Initialize the execution order list
        executionOrder.clear();
    }

    /**
     * This method is executed before each test method.
     * It's useful for setting up the test environment for each test.
     */
    @BeforeEach
    public void setUp() {
        System.out.println("@BeforeEach - Setting up test environment");
    }

    /**
     * First test method - creates a new deal.
     * 
     * This test is marked with @Order(1) to ensure it runs first.
     * It creates a new deal that will be used by subsequent tests.
     */
    @Test
    @Order(1)
    public void createDeal() {
        System.out.println("@Test - Creating a new deal");

        // Create a new deal
        deal = new Deal();
        deal.setId("deal-1");
        deal.setTitle("Test Deal");
        deal.setValue(new BigDecimal("10000.00"));
        deal.setSalesRepId(salesRep.getId());
        deal.setStatus(DealStatus.OPEN);

        // Add the test name to the execution order list
        executionOrder.add("createDeal");

        // Verify the deal was created correctly
        assertNotNull(deal, "Deal should not be null");
        assertEquals("deal-1", deal.getId(), "Deal ID should match");
        assertEquals("Test Deal", deal.getTitle(), "Deal title should match");
        assertEquals(new BigDecimal("10000.00"), deal.getValue(), "Deal value should match");
        assertEquals(salesRep.getId(), deal.getSalesRepId(), "Sales rep ID should match");
        assertEquals(DealStatus.OPEN, deal.getStatus(), "Deal status should be OPEN");
    }

    /**
     * Second test method - adds products to the deal.
     * 
     * This test is marked with @Order(2) to ensure it runs after createDeal.
     * It adds products to the deal created in the first test.
     */
    @Test
    @Order(2)
    public void addProductsToDeal() {
        System.out.println("@Test - Adding products to the deal");

        // Verify the deal exists
        assertNotNull(deal, "Deal should not be null");

        // Add products to the deal
        DealProduct product1 = new DealProduct();
        product1.setId("product-1");
        product1.setProductName("Product 1");
        product1.setPrice(new BigDecimal("5000.00"));
        product1.setQuantity(1);

        DealProduct product2 = new DealProduct();
        product2.setId("product-2");
        product2.setProductName("Product 2");
        product2.setPrice(new BigDecimal("2500.00"));
        product2.setQuantity(2);

        deal.addProduct(product1);
        deal.addProduct(product2);

        // Add the test name to the execution order list
        executionOrder.add("addProductsToDeal");

        // Verify the products were added correctly
        assertEquals(2, deal.getProducts().size(), "Deal should have 2 products");
        assertEquals("Product 1", deal.getProducts().get(0).getProductName(), "First product name should match");
        assertEquals("Product 2", deal.getProducts().get(1).getProductName(), "Second product name should match");
        assertEquals(new BigDecimal("10000.00"), deal.calculateTotalValue(), "Deal total value should match");
    }

    /**
     * Third test method - creates a commission plan for the deal.
     * 
     * This test is marked with @Order(3) to ensure it runs after addProductsToDeal.
     * It creates a commission plan for the deal created in the first test.
     */
    @Test
    @Order(3)
    public void createCommissionPlan() {
        System.out.println("@Test - Creating a commission plan");

        // Verify the deal exists
        assertNotNull(deal, "Deal should not be null");

        // Create a commission plan
        commissionPlan = new CommissionPlan();
        commissionPlan.setId("plan-1");
        commissionPlan.setName("Test Plan");
        commissionPlan.setEffectiveStartDate(LocalDate.now());
        commissionPlan.setEffectiveEndDate(LocalDate.now().plusMonths(3));
        commissionPlan.setStatus(PlanStatus.ACTIVE);

        // Add the test name to the execution order list
        executionOrder.add("createCommissionPlan");

        // Verify the commission plan was created correctly
        assertNotNull(commissionPlan, "Commission plan should not be null");
        assertEquals("plan-1", commissionPlan.getId(), "Commission plan ID should match");
        assertEquals("Test Plan", commissionPlan.getName(), "Commission plan name should match");
        assertEquals(PlanStatus.ACTIVE, commissionPlan.getStatus(), "Commission plan status should be ACTIVE");
    }

    /**
     * Fourth test method - closes the deal.
     * 
     * This test is marked with @Order(4) to ensure it runs after createCommissionPlan.
     * It closes the deal created in the first test.
     */
    @Test
    @Order(4)
    public void closeDeal() {
        System.out.println("@Test - Closing the deal");

        // Verify the deal exists
        assertNotNull(deal, "Deal should not be null");

        // Close the deal
        deal.setStatus(DealStatus.WON);
        deal.setCloseDate(LocalDate.now());

        // Add the test name to the execution order list
        executionOrder.add("closeDeal");

        // Verify the deal was closed correctly
        assertEquals(DealStatus.WON, deal.getStatus(), "Deal status should be WON");
        assertNotNull(deal.getCloseDate(), "Deal close date should not be null");
    }

    /**
     * Fifth test method - verifies the execution order.
     * 
     * This test is marked with @Order(5) to ensure it runs last.
     * It verifies that the tests were executed in the correct order.
     */
    @Test
    @Order(5)
    public void verifyExecutionOrder() {
        System.out.println("@Test - Verifying execution order");

        // Add the test name to the execution order list
        executionOrder.add("verifyExecutionOrder");

        // Verify the execution order
        List<String> expectedOrder = List.of(
            "createDeal",
            "addProductsToDeal",
            "createCommissionPlan",
            "closeDeal",
            "verifyExecutionOrder"
        );

        assertEquals(expectedOrder, executionOrder, "Tests should be executed in the correct order");
    }

    /**
     * This method is executed after each test method.
     * It's useful for cleaning up after each test.
     */
    @AfterEach
    public void tearDown() {
        System.out.println("@AfterEach - Cleaning up after test");
    }

    /**
     * This method is executed once after all test methods in this class.
     * It's useful for cleanup operations that are shared by all tests.
     */
    @AfterAll
    public static void tearDownAll() {
        System.out.println("@AfterAll - Final cleanup");

        // Print the execution order
        System.out.println("Test execution order: " + executionOrder);

        // Clean up
        salesRep = null;
        deal = null;
        commissionPlan = null;
        executionOrder.clear();
    }
}
