package com.chapman.edu.commissions.principles.solid.fixed.dip;

import com.chapman.edu.commissions.model.CommissionCalculation;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the Database interface for MySQL.
 * It follows the Dependency Inversion Principle by implementing an interface
 * that high-level modules depend on, rather than having them depend on this concrete implementation.
 */
public class MySqlDatabase implements Database {
    
    private final String url;
    private final String username;
    private final String password;
    
    /**
     * Constructor with database connection details.
     */
    public MySqlDatabase(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }
    
    /**
     * Gets a database connection.
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
    
    @Override
    public Deal getDealById(String dealId) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM deals WHERE id = ?")) {
            
            stmt.setString(1, dealId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Deal deal = new Deal();
                deal.setId(rs.getString("id"));
                deal.setTitle(rs.getString("title"));
                // Set other properties
                return deal;
            }
            
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }
    
    @Override
    public User getUserById(String userId) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE id = ?")) {
            
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getString("id"));
                user.setUsername(rs.getString("username"));
                // Set other properties
                return user;
            }
            
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }
    
    @Override
    public void saveCommissionCalculation(CommissionCalculation calculation) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO commission_calculations (id, deal_id, sales_rep_id, base_commission, calculation_date) " +
                             "VALUES (?, ?, ?, ?, ?)")) {
            
            stmt.setString(1, calculation.getId());
            stmt.setString(2, calculation.getDealId());
            stmt.setString(3, calculation.getSalesRepId());
            stmt.setBigDecimal(4, calculation.getBaseCommission());
            stmt.setObject(5, calculation.getCalculationDate());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }
    
    @Override
    public List<CommissionCalculation> getCommissionCalculationsBySalesRep(String salesRepId) {
        List<CommissionCalculation> calculations = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM commission_calculations WHERE sales_rep_id = ?")) {
            
            stmt.setString(1, salesRepId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                CommissionCalculation calculation = new CommissionCalculation();
                calculation.setId(rs.getString("id"));
                calculation.setDealId(rs.getString("deal_id"));
                calculation.setSalesRepId(rs.getString("sales_rep_id"));
                calculation.setBaseCommission(rs.getBigDecimal("base_commission"));
                // Set other properties
                
                calculations.add(calculation);
            }
            
            return calculations;
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }
}