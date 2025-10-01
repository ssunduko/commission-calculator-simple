package com.chapman.edu.commissions.patterns.fixture;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Fixture class for creating Deal test data.
 * 
 * Deals are central to commission calculations, so having consistent test data
 * is crucial for reliable testing. This fixture provides various deal scenarios
 * commonly encountered in commission processing.
 * 
 * The fixture pattern helps ensure that:
 * - Test data is consistent across different test classes
 * - Complex object graphs are created correctly
 * - Tests focus on behavior rather than object construction
 */
public class DealFixture {
    
    /**
     * Creates a basic open deal with standard configuration.
     * This represents a typical deal that's still in progress.
     * 
     * @return a Deal in OPEN status with basic configuration
     */
    public static Deal createOpenDeal() {
        Deal deal = new Deal("Software License Deal", new BigDecimal("10000.00"), "user-001");
        deal.setId("deal-001");
        deal.setStatus(DealStatus.OPEN);
        deal.setCreatedDate(LocalDate.now().minusDays(30));
        return deal;
    }
    
    /**
     * Creates a won deal that should trigger commission calculations.
     * Won deals are the primary source of commission income.
     * 
     * @return a Deal in WON status with close date
     */
    public static Deal createWonDeal() {
        Deal deal = new Deal("Enterprise Software Sale", new BigDecimal("50000.00"), "user-001");
        deal.setId("deal-002");
        deal.setStatus(DealStatus.WON);
        deal.setCloseDate(LocalDate.now().minusDays(5));
        deal.setCreatedDate(LocalDate.now().minusDays(45));
        return deal;
    }
    
    /**
     * Creates a lost deal for testing scenarios where no commission is earned.
     * Lost deals should not generate commissions but may affect quotas.
     * 
     * @return a Deal in LOST status
     */
    public static Deal createLostDeal() {
        Deal deal = new Deal("Competitive Loss", new BigDecimal("25000.00"), "user-001");
        deal.setId("deal-003");
        deal.setStatus(DealStatus.LOST);
        deal.setCreatedDate(LocalDate.now().minusDays(60));
        return deal;
    }
    
    /**
     * Creates a high-value deal for testing tier-based commission calculations.
     * Large deals often trigger higher commission rates or bonuses.
     * 
     * @return a Deal with high value
     */
    public static Deal createHighValueDeal() {
        Deal deal = new Deal("Major Enterprise Deal", new BigDecimal("100000.00"), "user-001");
        deal.setId("deal-004");
        deal.setStatus(DealStatus.WON);
        deal.setCloseDate(LocalDate.now().minusDays(2));
        deal.setCreatedDate(LocalDate.now().minusDays(90));
        return deal;
    }
    
    /**
     * Creates a deal with multiple products for testing complex commission scenarios.
     * Multi-product deals may have different commission rates per product type.
     * 
     * @return a Deal with multiple DealProduct objects
     */
    public static Deal createMultiProductDeal() {
        Deal deal = new Deal("Multi-Product Solution", new BigDecimal("75000.00"), "user-001");
        deal.setId("deal-005");
        deal.setStatus(DealStatus.WON);
        deal.setCloseDate(LocalDate.now().minusDays(1));
        
        // Add multiple products to test product-specific commission rules
        DealProduct software = new DealProduct("prod-001", "Software License", 5, new BigDecimal("10000.00"));
        software.setId("dp-001");
        software.setDealId(deal.getId());
        
        DealProduct support = new DealProduct("prod-002", "Support Contract", 1, new BigDecimal("15000.00"));
        support.setId("dp-002");
        support.setDealId(deal.getId());
        
        DealProduct training = new DealProduct("prod-003", "Training Services", 3, new BigDecimal("3333.33"));
        training.setId("dp-003");
        training.setDealId(deal.getId());
        
        deal.addProduct(software);
        deal.addProduct(support);
        deal.addProduct(training);
        
        return deal;
    }
    
    /**
     * Creates a deal for a specific sales representative.
     * This is useful for testing user-specific commission calculations.
     * 
     * @param salesRepId the ID of the sales representative
     * @return a Deal assigned to the specified sales rep
     */
    public static Deal createDealForSalesRep(String salesRepId) {
        Deal deal = new Deal("Custom Rep Deal", new BigDecimal("30000.00"), salesRepId);
        deal.setId("deal-006");
        deal.setStatus(DealStatus.WON);
        deal.setCloseDate(LocalDate.now().minusDays(3));
        deal.setCreatedDate(LocalDate.now().minusDays(40));
        return deal;
    }
    
    /**
     * Creates a deal with a specific close date for testing time-based commission rules.
     * Commission plans may have different rates based on when deals close.
     * 
     * @param closeDate the date when the deal was closed
     * @return a Deal with the specified close date
     */
    public static Deal createDealWithCloseDate(LocalDate closeDate) {
        Deal deal = new Deal("Dated Deal", new BigDecimal("20000.00"), "user-001");
        deal.setId("deal-007");
        deal.setStatus(DealStatus.WON);
        deal.setCloseDate(closeDate);
        deal.setCreatedDate(closeDate.minusDays(30));
        return deal;
    }
    
    /**
     * Creates a cancelled deal for testing edge cases in commission processing.
     * Cancelled deals should not generate commissions and may need special handling.
     * 
     * @return a Deal in CANCELLED status
     */
    public static Deal createCancelledDeal() {
        Deal deal = new Deal("Cancelled Project", new BigDecimal("40000.00"), "user-001");
        deal.setId("deal-008");
        deal.setStatus(DealStatus.CANCELLED);
        deal.setCreatedDate(LocalDate.now().minusDays(20));
        return deal;
    }
    
    /**
     * Creates a small deal for testing minimum threshold scenarios.
     * Some commission plans may have minimum deal values for commission eligibility.
     * 
     * @return a Deal with low value
     */
    public static Deal createSmallDeal() {
        Deal deal = new Deal("Small Purchase", new BigDecimal("1000.00"), "user-001");
        deal.setId("deal-009");
        deal.setStatus(DealStatus.WON);
        deal.setCloseDate(LocalDate.now().minusDays(1));
        deal.setCreatedDate(LocalDate.now().minusDays(15));
        return deal;
    }
    
    /**
     * Creates a deal with discounted products for testing discount impact on commissions.
     * Discounts may affect commission calculations differently than base prices.
     * 
     * @return a Deal with discounted products
     */
    public static Deal createDiscountedDeal() {
        Deal deal = new Deal("Discounted Sale", new BigDecimal("15000.00"), "user-001");
        deal.setId("deal-010");
        deal.setStatus(DealStatus.WON);
        deal.setCloseDate(LocalDate.now());
        
        DealProduct product = new DealProduct("prod-004", "Discounted Software", 2, new BigDecimal("10000.00"));
        product.setId("dp-004");
        product.setDiscount(new BigDecimal("5000.00")); // 25% discount
        product.setDealId(deal.getId());
        
        deal.addProduct(product);
        
        return deal;
    }
}