package com.chapman.edu.commissions.app.servlet;

import com.chapman.edu.commissions.integration.service.DealService;
import com.chapman.edu.commissions.integration.service.UserService;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;
import com.chapman.edu.commissions.model.User;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;

/**
 * WebUIController - Server-Side Rendered UI using PrintWriter.
 *
 * This servlet demonstrates traditional server-side rendering approach
 * where HTML is generated dynamically using Java PrintWriter instead of
 * client-side JavaScript.
 *
 * <b>COMPARISON WITH JAVASCRIPT APPROACH:</b>
 *
 * JavaScript Approach (SPA - Single Page Application):
 * - HTML/CSS/JS files served statically
 * - Browser fetches data via AJAX/fetch API
 * - DOM manipulation on client side
 * - Better user experience (no full page reloads)
 * - More complex debugging
 *
 * PrintWriter Approach (Traditional Server-Side Rendering):
 * - HTML generated on server for each request
 * - Full page reload on every action
 * - Simpler architecture (no API calls needed)
 * - Better SEO and initial load time
 * - Easier to debug (just view page source)
 *
 * <b>ENDPOINTS:</b>
 * - GET  /ui         - List all deals
 * - GET  /ui?page=create - Show create deal form
 * - POST /ui         - Handle deal creation
 * - GET  /ui?page=dashboard - Show dashboard
 *
 * @author Commission Calculator Team
 * @version 1.0
 */
public class WebUIController extends HttpServlet {

    private final DealService dealService;
    private final UserService userService;

    /**
     * Constructor with dependency injection.
     *
     * @param dealService Service for deal operations
     * @param userService Service for user operations
     */
    public WebUIController(DealService dealService, UserService userService) {
        this.dealService = dealService;
        this.userService = userService;
    }

    /**
     * Handles GET requests - Display UI pages.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=UTF-8");

        String page = request.getParameter("page");
        String action = request.getParameter("action");

        if ("create".equals(page)) {
            renderCreateDealPage(response);
        } else if ("dashboard".equals(page)) {
            renderDashboardPage(response);
        } else if ("delete".equals(action)) {
            handleDeleteDeal(request, response);
        } else {
            renderDealsListPage(request, response);
        }
    }

    /**
     * Handles POST requests - Process form submissions.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");

        if ("create".equals(action)) {
            handleCreateDeal(request, response);
        } else if ("close".equals(action)) {
            handleCloseDeal(request, response);
        } else {
            response.sendRedirect("/ui");
        }
    }

    /**
     * Renders the deals list page.
     */
    private void renderDealsListPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();

        // Get filter parameters
        String statusFilter = request.getParameter("status");
        String salesRepFilter = request.getParameter("salesRepId");

        // Fetch deals based on filters
        List<Deal> deals;
        if (statusFilter != null && !statusFilter.isEmpty()) {
            deals = dealService.getDealsByStatus(DealStatus.valueOf(statusFilter));
        } else if (salesRepFilter != null && !salesRepFilter.isEmpty()) {
            deals = dealService.getDealsBySalesRep(salesRepFilter);
        } else {
            deals = dealService.getAllDeals();
        }

        // Generate HTML
        renderPageHeader(out, "All Deals");

        out.println("<div class='container'>");
        out.println("  <header>");
        out.println("    <h1>Deal Management System</h1>");
        out.println("    <p class='subtitle'>Server-Side Rendered with PrintWriter</p>");
        out.println("  </header>");

        // Navigation
        renderNavigation(out, "deals");

        // Filters
        out.println("  <div class='filters-section'>");
        out.println("    <form method='GET' action='/ui' class='filters-form'>");
        out.println("      <label>");
        out.println("        Status:");
        out.println("        <select name='status'>");
        out.println("          <option value=''>All</option>");
        out.println("          <option value='OPEN'" + ("OPEN".equals(statusFilter) ? " selected" : "") + ">Open</option>");
        out.println("          <option value='WON'" + ("WON".equals(statusFilter) ? " selected" : "") + ">Won</option>");
        out.println("          <option value='LOST'" + ("LOST".equals(statusFilter) ? " selected" : "") + ">Lost</option>");
        out.println("          <option value='CANCELLED'" + ("CANCELLED".equals(statusFilter) ? " selected" : "") + ">Cancelled</option>");
        out.println("        </select>");
        out.println("      </label>");
        out.println("      <label>");
        out.println("        Sales Rep ID:");
        out.println("        <input type='text' name='salesRepId' value='" + (salesRepFilter != null ? salesRepFilter : "") + "' placeholder='e.g., USER-123'>");
        out.println("      </label>");
        out.println("      <button type='submit' class='btn btn-primary'>Apply Filters</button>");
        out.println("      <a href='/ui' class='btn btn-secondary'>Clear</a>");
        out.println("    </form>");
        out.println("  </div>");

        // Deals grid
        out.println("  <div class='deals-grid'>");

        if (deals.isEmpty()) {
            out.println("    <p class='no-data'>No deals found. <a href='/ui?page=create'>Create your first deal</a></p>");
        } else {
            for (Deal deal : deals) {
                renderDealCard(out, deal);
            }
        }

        out.println("  </div>");
        out.println("</div>");

        renderPageFooter(out);
    }

    /**
     * Renders the create deal page with form.
     */
    private void renderCreateDealPage(HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();

        // Fetch users for dropdown
        List<User> users = userService.getAllUsers();

        renderPageHeader(out, "Create New Deal");

        out.println("<div class='container'>");
        out.println("  <header>");
        out.println("    <h1>Deal Management System</h1>");
        out.println("    <p class='subtitle'>Server-Side Rendered with PrintWriter</p>");
        out.println("  </header>");

        renderNavigation(out, "create");

        out.println("  <div class='form-container'>");
        out.println("    <h2>Create New Deal</h2>");
        out.println("    <form method='POST' action='/ui'>");
        out.println("      <input type='hidden' name='action' value='create'>");

        out.println("      <div class='form-group'>");
        out.println("        <label for='title'>Deal Title *</label>");
        out.println("        <input type='text' id='title' name='title' required placeholder='e.g., Enterprise Software License - Acme Corp'>");
        out.println("      </div>");

        out.println("      <div class='form-group'>");
        out.println("        <label for='salesRepId'>Sales Representative *</label>");
        out.println("        <select id='salesRepId' name='salesRepId' required>");
        out.println("          <option value=''>-- Select a Sales Rep --</option>");

        for (User user : users) {
            out.println("          <option value='" + user.getId() + "'>" +
                    user.getFirstName() + " " + user.getLastName() +
                    " (" + user.getEmail() + ")</option>");
        }

        out.println("        </select>");
        out.println("      </div>");

        out.println("      <div class='form-group'>");
        out.println("        <label for='status'>Status</label>");
        out.println("        <select id='status' name='status'>");
        out.println("          <option value='OPEN'>Open</option>");
        out.println("          <option value='WON'>Won</option>");
        out.println("          <option value='LOST'>Lost</option>");
        out.println("          <option value='CANCELLED'>Cancelled</option>");
        out.println("        </select>");
        out.println("      </div>");

        out.println("      <div class='form-section'>");
        out.println("        <h3>Products</h3>");
        out.println("        <p class='info-text'>Note: For simplicity, this form creates one product. Use the JavaScript UI for multiple products.</p>");

        out.println("        <div class='form-group'>");
        out.println("          <label for='productId'>Product ID *</label>");
        out.println("          <input type='text' id='productId' name='productId' required placeholder='e.g., PROD-001'>");
        out.println("        </div>");

        out.println("        <div class='form-group'>");
        out.println("          <label for='productName'>Product Name *</label>");
        out.println("          <input type='text' id='productName' name='productName' required placeholder='e.g., Software License'>");
        out.println("        </div>");

        out.println("        <div class='form-row'>");
        out.println("          <div class='form-group'>");
        out.println("            <label for='quantity'>Quantity *</label>");
        out.println("            <input type='number' id='quantity' name='quantity' required min='1' value='1'>");
        out.println("          </div>");

        out.println("          <div class='form-group'>");
        out.println("            <label for='price'>Price *</label>");
        out.println("            <input type='number' id='price' name='price' required min='0' step='0.01' placeholder='0.00'>");
        out.println("          </div>");
        out.println("        </div>");

        out.println("      </div>");

        out.println("      <div class='form-actions'>");
        out.println("        <button type='submit' class='btn btn-primary'>Create Deal</button>");
        out.println("        <a href='/ui' class='btn btn-secondary'>Cancel</a>");
        out.println("      </div>");

        out.println("    </form>");
        out.println("  </div>");
        out.println("</div>");

        renderPageFooter(out);
    }

    /**
     * Renders the dashboard page.
     */
    private void renderDashboardPage(HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();

        List<Deal> allDeals = dealService.getAllDeals();

        // Calculate statistics
        long totalDeals = allDeals.size();
        long openDeals = allDeals.stream().filter(d -> d.getStatus() == DealStatus.OPEN).count();
        long wonDeals = allDeals.stream().filter(d -> d.getStatus() == DealStatus.WON).count();
        long lostDeals = allDeals.stream().filter(d -> d.getStatus() == DealStatus.LOST).count();

        BigDecimal totalValue = allDeals.stream()
                .map(Deal::calculateTotalValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal wonValue = allDeals.stream()
                .filter(d -> d.getStatus() == DealStatus.WON)
                .map(Deal::calculateTotalValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        double winRate = (totalDeals > 0 && (wonDeals + lostDeals) > 0)
                ? (wonDeals * 100.0 / (wonDeals + lostDeals))
                : 0.0;

        renderPageHeader(out, "Dashboard");

        out.println("<div class='container'>");
        out.println("  <header>");
        out.println("    <h1>Deal Management System</h1>");
        out.println("    <p class='subtitle'>Server-Side Rendered with PrintWriter</p>");
        out.println("  </header>");

        renderNavigation(out, "dashboard");

        out.println("  <h2>Dashboard</h2>");
        out.println("  <div class='stats-grid'>");

        renderStatCard(out, "Total Deals", String.valueOf(totalDeals));
        renderStatCard(out, "Open Deals", String.valueOf(openDeals));
        renderStatCard(out, "Won Deals", String.valueOf(wonDeals));
        renderStatCard(out, "Total Value", String.format("$%,.2f", totalValue));

        out.println("  </div>");

        out.println("  <div class='dashboard-detail'>");
        out.println("    <h3>Financial Overview</h3>");
        out.println("    <div class='detail-row'>");
        out.println("      <span>Total Value:</span>");
        out.println("      <span class='value'>$" + String.format("%,.2f", totalValue) + "</span>");
        out.println("    </div>");
        out.println("    <div class='detail-row'>");
        out.println("      <span>Won Value:</span>");
        out.println("      <span class='value'>$" + String.format("%,.2f", wonValue) + "</span>");
        out.println("    </div>");
        out.println("    <div class='detail-row'>");
        out.println("      <span>Win Rate:</span>");
        out.println("      <span class='value'>" + String.format("%.1f%%", winRate) + "</span>");
        out.println("    </div>");
        out.println("    <div class='detail-row'>");
        out.println("      <span>Lost Deals:</span>");
        out.println("      <span class='value'>" + lostDeals + "</span>");
        out.println("    </div>");
        out.println("  </div>");

        out.println("</div>");

        renderPageFooter(out);
    }

    /**
     * Handles deal creation form submission.
     */
    private void handleCreateDeal(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String title = request.getParameter("title");
            String salesRepId = request.getParameter("salesRepId");
            String status = request.getParameter("status");

            String productId = request.getParameter("productId");
            String productName = request.getParameter("productName");
            int quantity = Integer.parseInt(request.getParameter("quantity"));
            BigDecimal price = new BigDecimal(request.getParameter("price"));

            // Create deal
            Deal deal = new Deal();
            deal.setTitle(title);
            deal.setSalesRepId(salesRepId);
            deal.setStatus(DealStatus.valueOf(status));

            // Add product
            DealProduct product = new DealProduct(productId, productName, quantity, price);
            deal.setProducts(List.of(product));

            dealService.createDeal(deal);

            // Redirect to deals list with success message
            response.sendRedirect("/ui?success=created");

        } catch (Exception e) {
            response.sendRedirect("/ui?page=create&error=" + e.getMessage());
        }
    }

    /**
     * Handles deal close action.
     */
    private void handleCloseDeal(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String dealId = request.getParameter("dealId");
            dealService.closeDealAsWon(dealId);
            response.sendRedirect("/ui?success=closed");
        } catch (Exception e) {
            response.sendRedirect("/ui?error=" + e.getMessage());
        }
    }

    /**
     * Handles deal deletion.
     */
    private void handleDeleteDeal(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String dealId = request.getParameter("dealId");
            dealService.deleteDeal(dealId);
            response.sendRedirect("/ui?success=deleted");
        } catch (Exception e) {
            response.sendRedirect("/ui?error=" + e.getMessage());
        }
    }

    /**
     * Renders a single deal card.
     */
    private void renderDealCard(PrintWriter out, Deal deal) {
        String statusClass = "badge-" + deal.getStatus().toString().toLowerCase();
        BigDecimal totalValue = deal.calculateTotalValue();

        out.println("    <div class='deal-card'>");
        out.println("      <div class='deal-header'>");
        out.println("        <div>");
        out.println("          <div class='deal-title'>" + escapeHtml(deal.getTitle()) + "</div>");
        out.println("          <div class='deal-id'>" + deal.getId() + "</div>");
        out.println("        </div>");
        out.println("        <span class='badge " + statusClass + "'>" + deal.getStatus() + "</span>");
        out.println("      </div>");

        out.println("      <div class='deal-info'>");
        out.println("        <div class='info-item'>");
        out.println("          <span class='info-label'>Sales Rep</span>");
        out.println("          <span class='info-value'>" + deal.getSalesRepId() + "</span>");
        out.println("        </div>");
        out.println("        <div class='info-item'>");
        out.println("          <span class='info-label'>Total Value</span>");
        out.println("          <span class='info-value'>$" + String.format("%,.2f", totalValue) + "</span>");
        out.println("        </div>");
        out.println("        <div class='info-item'>");
        out.println("          <span class='info-label'>Created</span>");
        out.println("          <span class='info-value'>" + deal.getCreatedDate() + "</span>");
        out.println("        </div>");
        out.println("      </div>");

        // Products
        if (deal.getProducts() != null && !deal.getProducts().isEmpty()) {
            out.println("      <div class='deal-products'>");
            out.println("        <h4>Products (" + deal.getProducts().size() + ")</h4>");
            for (DealProduct product : deal.getProducts()) {
                out.println("        <div class='product'>");
                out.println("          <span>" + product.getProductId() + "</span>");
                out.println("          <span>" + escapeHtml(product.getProductName()) + "</span>");
                out.println("          <span>Qty: " + product.getQuantity() + "</span>");
                out.println("          <span>$" + String.format("%,.2f", product.getPrice()) + "</span>");
                out.println("        </div>");
            }
            out.println("      </div>");
        }

        // Actions
        out.println("      <div class='deal-actions'>");
        if (deal.getStatus() == DealStatus.OPEN) {
            out.println("        <form method='POST' action='/ui' style='display:inline;'>");
            out.println("          <input type='hidden' name='action' value='close'>");
            out.println("          <input type='hidden' name='dealId' value='" + deal.getId() + "'>");
            out.println("          <button type='submit' class='btn btn-secondary btn-small'>Close Deal</button>");
            out.println("        </form>");
            out.println("        <a href='/ui?action=delete&dealId=" + deal.getId() + "' onclick='return confirm(\"Delete this deal?\")' class='btn btn-danger btn-small'>Delete</a>");
        }
        out.println("      </div>");

        out.println("    </div>");
    }

    /**
     * Renders a stat card.
     */
    private void renderStatCard(PrintWriter out, String title, String value) {
        out.println("    <div class='stat-card'>");
        out.println("      <h3>" + title + "</h3>");
        out.println("      <p class='stat-value'>" + value + "</p>");
        out.println("    </div>");
    }

    /**
     * Renders navigation tabs.
     */
    private void renderNavigation(PrintWriter out, String activePage) {
        out.println("  <nav class='tabs'>");
        out.println("    <a href='/ui' class='tab-button" + ("deals".equals(activePage) ? " active" : "") + "'>All Deals</a>");
        out.println("    <a href='/ui?page=create' class='tab-button" + ("create".equals(activePage) ? " active" : "") + "'>Create Deal</a>");
        out.println("    <a href='/ui?page=dashboard' class='tab-button" + ("dashboard".equals(activePage) ? " active" : "") + "'>Dashboard</a>");
        out.println("    <a href='/index.html' class='tab-button'>JavaScript UI</a>");
        out.println("  </nav>");
    }

    /**
     * Renders common page header with CSS.
     */
    private void renderPageHeader(PrintWriter out, String title) {
        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("  <meta charset='UTF-8'>");
        out.println("  <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("  <title>" + title + " - Deal Management</title>");
        out.println("  <link rel='stylesheet' href='/styles.css'>");
        out.println("</head>");
        out.println("<body>");
    }

    /**
     * Renders common page footer.
     */
    private void renderPageFooter(PrintWriter out) {
        out.println("  <footer style='text-align:center; padding:20px; color:#666; margin-top:40px;'>");
        out.println("    <p>Server-Side Rendered UI using Java PrintWriter | <a href='/index.html'>Switch to JavaScript UI</a></p>");
        out.println("  </footer>");
        out.println("</body>");
        out.println("</html>");
    }

    /**
     * Escapes HTML to prevent XSS.
     */
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#039;");
    }
}