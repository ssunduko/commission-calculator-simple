<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.math.BigDecimal" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - JSP Implementation</title>
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
            <a href="/jsp/deals" class="tab-button">All Deals</a>
            <a href="/jsp/create" class="tab-button">Create Deal</a>
            <a href="/jsp/dashboard" class="tab-button active">Dashboard</a>
            <a href="/index.html" class="tab-button">JavaScript UI</a>
            <a href="/ui" class="tab-button">PrintWriter UI</a>
        </nav>

        <div class="tab-content active">
            <h2>Dashboard</h2>

            <%
                @SuppressWarnings("unchecked")
                Map<String, Object> stats = (Map<String, Object>) request.getAttribute("stats");

                if (stats != null) {
                    Long totalDeals = (Long) stats.get("totalDeals");
                    Long openDeals = (Long) stats.get("openDeals");
                    Long wonDeals = (Long) stats.get("wonDeals");
                    Long lostDeals = (Long) stats.get("lostDeals");
                    BigDecimal totalValue = (BigDecimal) stats.get("totalValue");
                    BigDecimal openValue = (BigDecimal) stats.get("openValue");
                    BigDecimal wonValue = (BigDecimal) stats.get("wonValue");
                    Double winRate = (Double) stats.get("winRate");
            %>

            <div class="stats-grid">
                <div class="stat-card">
                    <h3>Total Deals</h3>
                    <p class="stat-value"><%= totalDeals %></p>
                </div>
                <div class="stat-card">
                    <h3>Open Deals</h3>
                    <p class="stat-value"><%= openDeals %></p>
                </div>
                <div class="stat-card">
                    <h3>Won Deals</h3>
                    <p class="stat-value"><%= wonDeals %></p>
                </div>
                <div class="stat-card">
                    <h3>Total Value</h3>
                    <p class="stat-value">$<%= String.format("%,.2f", totalValue) %></p>
                </div>
            </div>

            <div class="dashboard-info">
                <h3>Financial Overview</h3>
                <div style="background: white; padding: 15px; border-radius: 6px;">
                    <div class="info-row">
                        <span class="info-label">Total Value</span>
                        <span class="info-value">$<%= String.format("%,.2f", totalValue) %></span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">Open Value</span>
                        <span class="info-value">$<%= String.format("%,.2f", openValue) %></span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">Won Value</span>
                        <span class="info-value">$<%= String.format("%,.2f", wonValue) %></span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">Win Rate</span>
                        <span class="info-value"><%= String.format("%.1f%%", winRate) %></span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">Lost Deals</span>
                        <span class="info-value"><%= lostDeals %></span>
                    </div>
                </div>
            </div>

            <%
                } else {
            %>
                <p class="loading">Error loading dashboard statistics.</p>
            <%
                }
            %>
        </div>

        <footer style="text-align:center; padding:20px; color:#666; margin-top:40px;">
            <p>
                <strong>JSP Implementation</strong> - Server-side templating with JavaServer Pages<br>
                <a href="/index.html">JavaScript UI</a> |
                <a href="/ui">PrintWriter UI</a> |
                <a href="/dashboard.html">Dashboard (Live)</a>
            </p>
        </footer>
    </div>
</body>
</html>