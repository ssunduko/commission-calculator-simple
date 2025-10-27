package com.chapman.edu.commissions.app.jsp;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;
import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.integration.service.DealService;
import com.chapman.edu.commissions.integration.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JSP Controller for Deal Management System
 *
 * This servlet demonstrates server-side rendering using JavaServer Pages (JSP).
 * Unlike the PrintWriter approach which uses out.println() to generate HTML,
 * JSP uses HTML templates with embedded Java code.
 *
 * Architecture Pattern:
 * - Servlet acts as Controller (handles routing and business logic)
 * - JSP files act as Views (template-based presentation)
 * - Services/Models act as Model layer (data and business rules)
 *
 * Endpoints:
 * - GET  /jsp/deals      - List all deals (with optional filters)
 * - GET  /jsp/create     - Show create deal form
 * - POST /jsp/create     - Process deal creation
 * - GET  /jsp/dashboard  - Show statistics dashboard
 * - POST /jsp/close      - Close a deal
 * - POST /jsp/delete     - Delete a deal
 */
public class JSPController extends HttpServlet {

    private final DealService dealService;
    private final UserService userService;

    public JSPController(DealService dealService, UserService userService) {
        this.dealService = dealService;
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/deals")) {
            handleListDeals(request, response);
        } else if (pathInfo.equals("/create")) {
            handleShowCreateForm(request, response);
        } else if (pathInfo.equals("/dashboard")) {
            handleDashboard(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Page not found");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/create")) {
            handleCreateDeal(request, response);
        } else if (pathInfo.equals("/close")) {
            handleCloseDeal(request, response);
        } else if (pathInfo.equals("/delete")) {
            handleDeleteDeal(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Action not found");
        }
    }

    /**
     * GET /jsp/deals - List all deals with optional filtering
     */
    private void handleListDeals(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Get filter parameters
            String statusFilter = request.getParameter("status");
            String salesRepIdFilter = request.getParameter("salesRepId");

            // Fetch deals from service
            List<Deal> deals;
            if (statusFilter != null && !statusFilter.isEmpty()) {
                try {
                    DealStatus status = DealStatus.valueOf(statusFilter.toUpperCase());
                    deals = dealService.getDealsByStatus(status);
                } catch (IllegalArgumentException e) {
                    deals = dealService.getAllDeals();
                }
            } else if (salesRepIdFilter != null && !salesRepIdFilter.isEmpty()) {
                deals = dealService.getDealsBySalesRep(salesRepIdFilter);
            } else {
                deals = dealService.getAllDeals();
            }

            // Set attributes for JSP
            request.setAttribute("deals", deals);

            // Forward to JSP view
            request.getRequestDispatcher("/WEB-INF/jsp/deals.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading deals: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/deals.jsp").forward(request, response);
        }
    }

    /**
     * GET /jsp/create - Show the create deal form
     */
    private void handleShowCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Fetch users for dropdown
            List<User> users = userService.getAllUsers();
            request.setAttribute("users", users);

            // Forward to JSP view
            request.getRequestDispatcher("/WEB-INF/jsp/create-deal.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading users: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/create-deal.jsp").forward(request, response);
        }
    }

    /**
     * POST /jsp/create - Process deal creation
     */
    private void handleCreateDeal(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Extract form parameters
            String title = request.getParameter("title");
            String salesRepId = request.getParameter("salesRepId");
            String statusParam = request.getParameter("status");

            // Extract product parameters (JSP form supports single product)
            String productId = request.getParameter("productId");
            String productName = request.getParameter("productName");
            String quantityStr = request.getParameter("quantity");
            String priceStr = request.getParameter("price");

            // Validate required fields
            if (title == null || title.isEmpty() ||
                salesRepId == null || salesRepId.isEmpty() ||
                productId == null || productId.isEmpty() ||
                productName == null || productName.isEmpty() ||
                quantityStr == null || quantityStr.isEmpty() ||
                priceStr == null || priceStr.isEmpty()) {

                request.setAttribute("errorMessage", "All required fields must be filled");
                request.setAttribute("users", userService.getAllUsers());
                request.getRequestDispatcher("/WEB-INF/jsp/create-deal.jsp").forward(request, response);
                return;
            }

            // Parse status
            DealStatus status = DealStatus.OPEN;
            if (statusParam != null && !statusParam.isEmpty()) {
                try {
                    status = DealStatus.valueOf(statusParam.toUpperCase());
                } catch (IllegalArgumentException e) {
                    // Default to OPEN if invalid
                }
            }

            // Parse quantity and price
            int quantity;
            BigDecimal price;
            try {
                quantity = Integer.parseInt(quantityStr);
                price = new BigDecimal(priceStr);
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Invalid quantity or price format");
                request.setAttribute("users", userService.getAllUsers());
                request.getRequestDispatcher("/WEB-INF/jsp/create-deal.jsp").forward(request, response);
                return;
            }

            // Create product
            DealProduct product = new DealProduct();
            product.setProductId(productId);
            product.setProductName(productName);
            product.setQuantity(quantity);
            product.setPrice(price);
            product.setDiscount(BigDecimal.ZERO);

            List<DealProduct> products = new ArrayList<>();
            products.add(product);

            // Create deal
            Deal deal = new Deal();
            deal.setTitle(title);
            deal.setSalesRepId(salesRepId);
            deal.setStatus(status);
            deal.setProducts(products);
            deal.setCreatedDate(LocalDate.now());

            // Save via service
            Deal createdDeal = dealService.createDeal(deal);

            // Redirect to deals list with success message
            response.sendRedirect("/jsp/deals?success=created&dealId=" + createdDeal.getId());

        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error creating deal: " + e.getMessage());
            request.setAttribute("users", userService.getAllUsers());
            request.getRequestDispatcher("/WEB-INF/jsp/create-deal.jsp").forward(request, response);
        }
    }

    /**
     * GET /jsp/dashboard - Show statistics dashboard
     */
    private void handleDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Fetch all deals
            List<Deal> allDeals = dealService.getAllDeals();

            // Calculate statistics
            long totalDeals = allDeals.size();
            long openDeals = allDeals.stream()
                .filter(d -> d.getStatus() == DealStatus.OPEN)
                .count();
            long wonDeals = allDeals.stream()
                .filter(d -> d.getStatus() == DealStatus.WON)
                .count();
            long lostDeals = allDeals.stream()
                .filter(d -> d.getStatus() == DealStatus.LOST)
                .count();

            BigDecimal totalValue = allDeals.stream()
                .map(Deal::calculateTotalValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal openValue = allDeals.stream()
                .filter(d -> d.getStatus() == DealStatus.OPEN)
                .map(Deal::calculateTotalValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal wonValue = allDeals.stream()
                .filter(d -> d.getStatus() == DealStatus.WON)
                .map(Deal::calculateTotalValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            double winRate = (totalDeals > 0)
                ? (wonDeals * 100.0 / totalDeals)
                : 0.0;

            // Create stats map
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalDeals", totalDeals);
            stats.put("openDeals", openDeals);
            stats.put("wonDeals", wonDeals);
            stats.put("lostDeals", lostDeals);
            stats.put("totalValue", totalValue);
            stats.put("openValue", openValue);
            stats.put("wonValue", wonValue);
            stats.put("winRate", winRate);

            // Set attributes for JSP
            request.setAttribute("stats", stats);

            // Forward to JSP view
            request.getRequestDispatcher("/WEB-INF/jsp/dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading dashboard: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/dashboard.jsp").forward(request, response);
        }
    }

    /**
     * POST /jsp/close - Close a deal
     */
    private void handleCloseDeal(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String dealId = request.getParameter("dealId");

            if (dealId == null || dealId.isEmpty()) {
                response.sendRedirect("/jsp/deals?error=missing_id");
                return;
            }

            // Close the deal
            dealService.closeDealAsWon(dealId);

            // Redirect back to deals list
            response.sendRedirect("/jsp/deals?success=closed");

        } catch (Exception e) {
            response.sendRedirect("/jsp/deals?error=" + e.getMessage());
        }
    }

    /**
     * POST /jsp/delete - Delete a deal
     */
    private void handleDeleteDeal(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String dealId = request.getParameter("dealId");

            if (dealId == null || dealId.isEmpty()) {
                response.sendRedirect("/jsp/deals?error=missing_id");
                return;
            }

            // Delete the deal
            dealService.deleteDeal(dealId);

            // Redirect back to deals list
            response.sendRedirect("/jsp/deals?success=deleted");

        } catch (Exception e) {
            response.sendRedirect("/jsp/deals?error=" + e.getMessage());
        }
    }
}