package com.chapman.edu.commissions.principles.soc.fixed;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible only for data access operations
 * It follows the Separation of Concerns principle by focusing solely on data storage and retrieval
 */
public class OrderRepository {
    
    private List<Order> orders = new ArrayList<>();
    
    /**
     * Saves an order to the repository
     * 
     * @param order the order to save
     */
    public void saveOrder(Order order) {
        orders.add(order);
    }
    
    /**
     * Retrieves all orders from the repository
     * 
     * @return a list of all orders
     */
    public List<Order> getAllOrders() {
        return new ArrayList<>(orders); // Return a copy to prevent external modification
    }
    
    /**
     * Retrieves an order by its ID
     * 
     * @param orderId the order ID
     * @return the order, or null if not found
     */
    public Order getOrderById(String orderId) {
        for (Order order : orders) {
            if (order.getOrderId().equals(orderId)) {
                return order;
            }
        }
        return null;
    }
    
    /**
     * Updates an existing order
     * 
     * @param order the updated order
     * @return true if the order was updated, false if not found
     */
    public boolean updateOrder(Order order) {
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getOrderId().equals(order.getOrderId())) {
                orders.set(i, order);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Deletes an order by its ID
     * 
     * @param orderId the order ID
     * @return true if the order was deleted, false if not found
     */
    public boolean deleteOrder(String orderId) {
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getOrderId().equals(orderId)) {
                orders.remove(i);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns the number of orders in the repository
     * 
     * @return the number of orders
     */
    public int getOrderCount() {
        return orders.size();
    }
    
    /**
     * Updates the commission for all orders
     * 
     * @param commissionRate the new commission rate
     */
    public void updateAllCommissions(double commissionRate) {
        for (Order order : orders) {
            double newCommission = order.getTotalAmount() * commissionRate;
            order.setCommission(newCommission);
        }
    }
}