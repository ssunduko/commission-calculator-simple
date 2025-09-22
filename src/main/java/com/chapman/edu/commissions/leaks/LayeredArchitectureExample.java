package com.chapman.edu.commissions.leaks;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.CommissionPlan;
import com.chapman.edu.commissions.model.PlanStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

/**
 * This class demonstrates a leaky abstraction related to the "Layered Architecture" principle.
 * 
 * Layered Architecture is a design principle that organizes code into layers with specific responsibilities:
 * - Presentation Layer: User interface and user interaction
 * - Business Logic Layer: Application logic and rules
 * - Data Access Layer: Database interactions and data persistence
 * 
 * When layers are not properly separated, abstractions leak, causing higher layers to depend on
 * implementation details of lower layers, making the system rigid and difficult to change.
 */
public class LayeredArchitectureExample {

    /**
     * This example demonstrates a leaky layered architecture where the presentation layer
     * directly accesses the data layer, bypassing the business logic layer.
     */
    public static class LeakyLayeredArchitecture {
        
        /**
         * Presentation Layer - User Interface
         * This class violates layered architecture by directly accessing the data layer.
         */
        public static class DealUI {
            private DealDatabase database;
            
            public DealUI(DealDatabase database) {
                this.database = database;
            }
            
            /**
             * This method leaks the data layer abstraction by directly accessing the database
             * and manipulating data without going through the business logic layer.
             */
            public void createDeal(String title, BigDecimal value, String salesRepId) {
                // Leaky abstraction: UI directly creates and saves a deal to the database
                Deal deal = new Deal(title, value, salesRepId);
                deal.setId("DEAL-" + System.currentTimeMillis());
                deal.setCreatedDate(LocalDate.now());
                
                // UI knows database implementation details
                database.saveDeal(deal);
                
                System.out.println("Deal created: " + deal.getTitle());
            }
            
            /**
             * This method leaks the data layer abstraction by directly querying the database
             * and manipulating the results without going through the business logic layer.
             */
            public void calculateCommission(String dealId) {
                // Leaky abstraction: UI directly queries the database
                Deal deal = database.getDealById(dealId);
                
                if (deal == null) {
                    System.out.println("Deal not found");
                    return;
                }
                
                // UI contains business logic for commission calculation
                BigDecimal commission = deal.getValue().multiply(new BigDecimal("0.05"));
                
                System.out.println("Commission for deal " + deal.getTitle() + ": " + commission);
            }
        }
        
        /**
         * Data Layer - Database Access
         */
        public static class DealDatabase {
            private List<Deal> deals = new ArrayList<>();
            
            public void saveDeal(Deal deal) {
                deals.add(deal);
            }
            
            public Deal getDealById(String id) {
                return deals.stream()
                        .filter(deal -> deal.getId().equals(id))
                        .findFirst()
                        .orElse(null);
            }
            
            public List<Deal> getAllDeals() {
                return new ArrayList<>(deals);
            }
        }
    }
    
    /**
     * This example demonstrates a proper layered architecture with clear separation of concerns.
     */
    public static class ProperLayeredArchitecture {
        
        /**
         * Presentation Layer - User Interface
         * This class follows layered architecture by only interacting with the business logic layer.
         */
        public static class DealUI {
            private DealService dealService;
            
            public DealUI(DealService dealService) {
                this.dealService = dealService;
            }
            
            /**
             * This method respects the layered architecture by delegating to the business logic layer.
             */
            public void createDeal(String title, BigDecimal value, String salesRepId) {
                // UI only knows about the business logic layer
                dealService.createDeal(title, value, salesRepId);
                System.out.println("Deal creation request submitted");
            }
            
            /**
             * This method respects the layered architecture by delegating to the business logic layer.
             */
            public void calculateCommission(String dealId) {
                try {
                    // UI only knows about the business logic layer
                    BigDecimal commission = dealService.calculateCommission(dealId);
                    System.out.println("Commission calculated: " + commission);
                } catch (DealNotFoundException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        }
        
        /**
         * Business Logic Layer - Services
         * This layer encapsulates the application logic and rules.
         */
        public static class DealService {
            private DealRepository dealRepository;
            private CommissionCalculator commissionCalculator;
            
            public DealService(DealRepository dealRepository, CommissionCalculator commissionCalculator) {
                this.dealRepository = dealRepository;
                this.commissionCalculator = commissionCalculator;
            }
            
            /**
             * Creates a new deal with business validation and processing.
             */
            public Deal createDeal(String title, BigDecimal value, String salesRepId) {
                // Business logic for creating a deal
                Deal deal = new Deal(title, value, salesRepId);
                deal.setId("DEAL-" + System.currentTimeMillis());
                deal.setCreatedDate(LocalDate.now());
                
                // Validate the deal
                if (value.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Deal value must be positive");
                }
                
                // Save the deal through the repository
                return dealRepository.save(deal);
            }
            
            /**
             * Calculates commission for a deal with business rules.
             */
            public BigDecimal calculateCommission(String dealId) throws DealNotFoundException {
                // Get the deal through the repository
                Deal deal = dealRepository.findById(dealId)
                        .orElseThrow(() -> new DealNotFoundException("Deal not found with ID: " + dealId));
                
                // Use the commission calculator to apply business rules
                return commissionCalculator.calculateCommission(deal);
            }
        }
        
        /**
         * Business Logic Layer - Commission Calculator
         * This class encapsulates the commission calculation logic.
         */
        public static class CommissionCalculator {
            public BigDecimal calculateCommission(Deal deal) {
                // Business logic for calculating commission
                if (deal.getValue().compareTo(new BigDecimal("10000")) > 0) {
                    return deal.getValue().multiply(new BigDecimal("0.08"));
                } else {
                    return deal.getValue().multiply(new BigDecimal("0.05"));
                }
            }
        }
        
        /**
         * Data Access Layer - Repository
         * This layer encapsulates data access and persistence.
         */
        public static interface DealRepository {
            Deal save(Deal deal);
            java.util.Optional<Deal> findById(String id);
            List<Deal> findAll();
        }
        
        /**
         * Data Access Layer - Database Implementation
         * This class implements the repository interface with database operations.
         */
        public static class DealDatabaseRepository implements DealRepository {
            private List<Deal> deals = new ArrayList<>();
            
            @Override
            public Deal save(Deal deal) {
                deals.add(deal);
                return deal;
            }
            
            @Override
            public java.util.Optional<Deal> findById(String id) {
                return deals.stream()
                        .filter(deal -> deal.getId().equals(id))
                        .findFirst();
            }
            
            @Override
            public List<Deal> findAll() {
                return new ArrayList<>(deals);
            }
        }
        
        /**
         * Custom exception for deal not found.
         */
        public static class DealNotFoundException extends Exception {
            public DealNotFoundException(String message) {
                super(message);
            }
        }
    }
    
    /**
     * Main method to demonstrate the leaky and proper layered architectures.
     */
    public static void main(String[] args) {
        // Demonstrate leaky layered architecture
        System.out.println("=== Leaky Layered Architecture ===");
        LeakyLayeredArchitecture.DealDatabase leakyDatabase = new LeakyLayeredArchitecture.DealDatabase();
        LeakyLayeredArchitecture.DealUI leakyUI = new LeakyLayeredArchitecture.DealUI(leakyDatabase);
        
        leakyUI.createDeal("Standard Deal", new BigDecimal("10000"), "REP001");
        leakyUI.calculateCommission("DEAL-1");  // This will fail because the deal ID doesn't match
        
        // Demonstrate proper layered architecture
        System.out.println("\n=== Proper Layered Architecture ===");
        ProperLayeredArchitecture.DealRepository repository = new ProperLayeredArchitecture.DealDatabaseRepository();
        ProperLayeredArchitecture.CommissionCalculator calculator = new ProperLayeredArchitecture.CommissionCalculator();
        ProperLayeredArchitecture.DealService service = new ProperLayeredArchitecture.DealService(repository, calculator);
        ProperLayeredArchitecture.DealUI properUI = new ProperLayeredArchitecture.DealUI(service);
        
        properUI.createDeal("Premium Deal", new BigDecimal("20000"), "REP002");
        properUI.calculateCommission("DEAL-1");  // This will handle the error properly
    }
}