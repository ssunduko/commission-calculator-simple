<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.chapman.edu.commissions.model.Deal" %>
<%@ page import="java.math.BigDecimal" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>All Deals - JSP Implementation</title>
    <link rel="stylesheet" href="/styles.css">
    <style>
        .jsp-badge {
            background: #ff6b6b;
            color: white;
            padding: 4px 12px;
            border-radius: 4px;
            font-size: 0.8rem;
            font-weight: bold;
            margin-left: 10px;
        }
    </style>
</head>
<body>
    <div class="container">
        <header>
            <h1>Deal Management System <span class="jsp-badge">JSP</span></h1>
            <p class="subtitle">Server-Side Rendering with JavaServer Pages</p>
        </header>

        <nav class="tabs">
            <a href="/jsp/deals" class="tab-button active">All Deals</a>
            <a href="/jsp/create" class="tab-button">Create Deal</a>
            <a href="/jsp/dashboard" class="tab-button">Dashboard</a>
            <a href="/index.html" class="tab-button">JavaScript UI</a>
            <a href="/ui" class="tab-button">PrintWriter UI</a>
        </nav>

        <div class="tab-content active">
            <div class="controls">
                <div class="filters">
                    <form method="GET" action="/jsp/deals" style="display: flex; gap: 15px; flex-wrap: wrap;">
                        <label>
                            Filter by Status:
                            <select name="status">
                                <option value="">All Statuses</option>
                                <option value="OPEN" <%= "OPEN".equals(request.getParameter("status")) ? "selected" : "" %>>Open</option>
                                <option value="WON" <%= "WON".equals(request.getParameter("status")) ? "selected" : "" %>>Won</option>
                                <option value="LOST" <%= "LOST".equals(request.getParameter("status")) ? "selected" : "" %>>Lost</option>
                                <option value="CANCELLED" <%= "CANCELLED".equals(request.getParameter("status")) ? "selected" : "" %>>Cancelled</option>
                            </select>
                        </label>
                        <label>
                            Sales Rep ID:
                            <input type="text" name="salesRepId" value="<%= request.getParameter("salesRepId") != null ? request.getParameter("salesRepId") : "" %>" placeholder="e.g., USER-123" />
                        </label>
                        <button type="submit" class="btn btn-primary">Apply Filters</button>
                        <a href="/jsp/deals" class="btn btn-secondary">Clear</a>
                    </form>
                </div>
            </div>

            <div id="dealsContainer">
                <%
                    @SuppressWarnings("unchecked")
                    List<Deal> deals = (List<Deal>) request.getAttribute("deals");

                    if (deals == null || deals.isEmpty()) {
                %>
                    <p class="loading">No deals found. <a href="/jsp/create">Create your first deal</a></p>
                <%
                    } else {
                        for (Deal deal : deals) {
                            String statusClass = "badge-" + deal.getStatus().toString().toLowerCase();
                            BigDecimal totalValue = deal.calculateTotalValue();
                %>
                    <div class="deal-card">
                        <div class="deal-header">
                            <div>
                                <div class="deal-title"><%= deal.getTitle() %></div>
                                <div class="deal-id"><%= deal.getId() %></div>
                            </div>
                            <span class="badge <%= statusClass %>"><%= deal.getStatus() %></span>
                        </div>

                        <div class="deal-info">
                            <div class="info-item">
                                <span class="info-label">Sales Rep</span>
                                <span class="info-value"><%= deal.getSalesRepId() %></span>
                            </div>
                            <div class="info-item">
                                <span class="info-label">Total Value</span>
                                <span class="info-value">$<%= String.format("%,.2f", totalValue) %></span>
                            </div>
                            <div class="info-item">
                                <span class="info-label">Created</span>
                                <span class="info-value"><%= deal.getCreatedDate() %></span>
                            </div>
                            <% if (deal.getCloseDate() != null) { %>
                            <div class="info-item">
                                <span class="info-label">Close Date</span>
                                <span class="info-value"><%= deal.getCloseDate() %></span>
                            </div>
                            <% } %>
                        </div>

                        <% if (deal.getProducts() != null && !deal.getProducts().isEmpty()) { %>
                        <div class="deal-products">
                            <h4>Products (<%= deal.getProducts().size() %>)</h4>
                            <div class="product-list">
                                <% for (var product : deal.getProducts()) { %>
                                <div class="product">
                                    <span><%= product.getProductId() %></span>
                                    <span><%= product.getProductName() %></span>
                                    <span>Qty: <%= product.getQuantity() %></span>
                                    <span>$<%= String.format("%,.2f", product.getPrice()) %></span>
                                </div>
                                <% } %>
                            </div>
                        </div>
                        <% } %>

                        <div class="deal-actions">
                            <% if ("OPEN".equals(deal.getStatus().toString())) { %>
                            <form method="POST" action="/jsp/close" style="display: inline;">
                                <input type="hidden" name="dealId" value="<%= deal.getId() %>">
                                <button type="submit" class="btn btn-secondary btn-small">Close Deal</button>
                            </form>
                            <form method="POST" action="/jsp/delete" style="display: inline;" onsubmit="return confirm('Delete this deal?');">
                                <input type="hidden" name="dealId" value="<%= deal.getId() %>">
                                <button type="submit" class="btn btn-danger btn-small">Delete</button>
                            </form>
                            <% } %>
                        </div>
                    </div>
                <%
                        }
                    }
                %>
            </div>
        </div>

        <footer style="text-align:center; padding:20px; color:#666; margin-top:40px;">
            <p>
                <strong>JSP Implementation</strong> - Server-side templating with JavaServer Pages<br>
                <a href="/index.html">JavaScript UI</a> |
                <a href="/ui">PrintWriter UI</a> |
                <a href="/dashboard.html">Dashboard</a>
            </p>
        </footer>
    </div>
</body>
</html>