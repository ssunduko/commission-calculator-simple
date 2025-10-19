package com.chapman.edu.commissions.api.soap;

import com.chapman.edu.commissions.api.soap.dto.DealDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for DealService SOAP web service.
 *
 * TEST STRUCTURE:
 * --------------
 * Each test demonstrates a different SOAP operation:
 * - Query operations (getAllDeals, getDealById, etc.)
 * - Mutation operations (createDeal, updateDeal, deleteDeal)
 * - Filtering operations (getDealsByStatus, getDealsBySalesRep)
 *
 * ASSERTIONS:
 * ----------
 * - assertNotNull: Verifies object is not null
 * - assertEquals: Verifies expected value matches actual
 * - assertTrue: Verifies condition is true
 * - assertFalse: Verifies condition is false
 *
 * AAA PATTERN:
 * -----------
 * Each test follows Arrange-Act-Assert pattern:
 * 1. Arrange: Set up test data
 * 2. Act: Execute the operation
 * 3. Assert: Verify the result
 */
@DisplayName("SOAP DealService Integration Tests")
public class DealSoapIntegrationTest extends SoapIntegrationTestBase {

    @Test
    @DisplayName("Should get all deals via SOAP")
    void testGetAllDeals() {
        // Act
        List<DealDTO> deals = dealService.getAllDeals();

        // Assert
        assertNotNull(deals, "Deals list should not be null");
        assertTrue(deals.size() > 0, "Should have at least one deal");

        // Verify first deal structure
        DealDTO firstDeal = deals.get(0);
        assertNotNull(firstDeal.getId(), "Deal ID should not be null");
        assertNotNull(firstDeal.getTitle(), "Deal title should not be null");
        assertNotNull(firstDeal.getValue(), "Deal value should not be null");
        assertNotNull(firstDeal.getStatus(), "Deal status should not be null");
    }

    @Test
    @DisplayName("Should get a specific deal by ID via SOAP")
    void testGetDealById() {
        // Arrange
        List<DealDTO> allDeals = dealService.getAllDeals();
        String dealId = allDeals.get(0).getId();

        // Act
        DealDTO deal = dealService.getDealById(dealId);

        // Assert
        assertNotNull(deal, "Deal should not be null");
        assertEquals(dealId, deal.getId(), "Deal ID should match requested ID");
        assertNotNull(deal.getTitle(), "Deal title should not be null");
    }

    @Test
    @DisplayName("Should create a new deal via SOAP")
    void testCreateDeal() {
        // Arrange
        DealDTO newDeal = new DealDTO();
        newDeal.setTitle("SOAP Test Deal");
        newDeal.setValue(new BigDecimal("150000.00"));
        newDeal.setStatus("OPEN");
        newDeal.setSalesRepId("USER-001");

        // Act
        DealDTO created = dealService.createDeal(newDeal);

        // Assert
        assertNotNull(created, "Created deal should not be null");
        assertNotNull(created.getId(), "Created deal should have an ID");
        assertEquals("SOAP Test Deal", created.getTitle(), "Title should match");
        assertEquals(new BigDecimal("150000.00"), created.getValue(), "Value should match");
        assertEquals("OPEN", created.getStatus(), "Status should match");

        // Verify it was actually saved
        DealDTO retrieved = dealService.getDealById(created.getId());
        assertNotNull(retrieved, "Should be able to retrieve created deal");
        assertEquals(created.getId(), retrieved.getId(), "IDs should match");
    }

    @Test
    @DisplayName("Should update an existing deal via SOAP")
    void testUpdateDeal() {
        // Arrange - create a deal first
        DealDTO newDeal = new DealDTO();
        newDeal.setTitle("Deal to Update");
        newDeal.setValue(new BigDecimal("100000.00"));
        newDeal.setStatus("OPEN");
        newDeal.setSalesRepId("USER-001");
        DealDTO created = dealService.createDeal(newDeal);

        // Prepare update
        DealDTO updates = new DealDTO();
        updates.setTitle("Updated Title");
        updates.setStatus("WON");

        // Act
        DealDTO updated = dealService.updateDeal(created.getId(), updates);

        // Assert
        assertNotNull(updated, "Updated deal should not be null");
        assertEquals("Updated Title", updated.getTitle(), "Title should be updated");
        assertEquals("WON", updated.getStatus(), "Status should be updated");
    }

    @Test
    @DisplayName("Should delete a deal via SOAP")
    void testDeleteDeal() {
        // Arrange - create a deal first
        DealDTO newDeal = new DealDTO();
        newDeal.setTitle("Deal to Delete");
        newDeal.setValue(new BigDecimal("50000.00"));
        newDeal.setStatus("OPEN");
        newDeal.setSalesRepId("USER-001");
        DealDTO created = dealService.createDeal(newDeal);
        String dealId = created.getId();

        // Act
        boolean deleted = dealService.deleteDeal(dealId);

        // Assert
        assertTrue(deleted, "Delete operation should return true");

        // Verify it's actually deleted
        DealDTO retrieved = dealService.getDealById(dealId);
        assertNull(retrieved, "Deleted deal should not be retrievable");
    }

    @Test
    @DisplayName("Should get deals by status via SOAP")
    void testGetDealsByStatus() {
        // Arrange - create a deal with specific status
        DealDTO newDeal = new DealDTO();
        newDeal.setTitle("Won Deal");
        newDeal.setValue(new BigDecimal("200000.00"));
        newDeal.setStatus("WON");
        newDeal.setSalesRepId("USER-001");
        dealService.createDeal(newDeal);

        // Act
        List<DealDTO> wonDeals = dealService.getDealsByStatus("WON");

        // Assert
        assertNotNull(wonDeals, "Won deals list should not be null");
        assertTrue(wonDeals.size() > 0, "Should have at least one won deal");

        // Verify all deals have WON status
        for (DealDTO deal : wonDeals) {
            assertEquals("WON", deal.getStatus(), "All deals should have WON status");
        }
    }

    @Test
    @DisplayName("Should get deals by sales rep via SOAP")
    void testGetDealsBySalesRep() {
        // Arrange - create a deal for specific sales rep
        DealDTO newDeal = new DealDTO();
        newDeal.setTitle("Sales Rep Deal");
        newDeal.setValue(new BigDecimal("120000.00"));
        newDeal.setStatus("OPEN");
        newDeal.setSalesRepId("USER-001");
        dealService.createDeal(newDeal);

        // Act
        List<DealDTO> repDeals = dealService.getDealsBySalesRep("USER-001");

        // Assert
        assertNotNull(repDeals, "Sales rep deals list should not be null");
        assertTrue(repDeals.size() > 0, "Should have at least one deal for this sales rep");

        // Verify all deals belong to the sales rep
        for (DealDTO deal : repDeals) {
            assertEquals("USER-001", deal.getSalesRepId(), "All deals should belong to USER-001");
        }
    }

    @Test
    @DisplayName("Should return null for non-existent deal ID via SOAP")
    void testGetNonExistentDeal() {
        // Act
        DealDTO deal = dealService.getDealById("NON-EXISTENT-ID");

        // Assert
        assertNull(deal, "Non-existent deal should return null");
    }
}