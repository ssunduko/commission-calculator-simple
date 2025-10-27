<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.chapman.edu.commissions.model.User" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Deal - JSP Implementation</title>
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
        .success-message {
            background: #d4edda;
            color: #155724;
            padding: 15px;
            border-radius: 6px;
            border: 1px solid #c3e6cb;
            margin-bottom: 20px;
        }
        .error-message {
            background: #f8d7da;
            color: #721c24;
            padding: 15px;
            border-radius: 6px;
            border: 1px solid #f5c6cb;
            margin-bottom: 20px;
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
            <a href="/jsp/create" class="tab-button active">Create Deal</a>
            <a href="/jsp/dashboard" class="tab-button">Dashboard</a>
            <a href="/index.html" class="tab-button">JavaScript UI</a>
            <a href="/ui" class="tab-button">PrintWriter UI</a>
        </nav>

        <div class="tab-content active">
            <div class="form-container">
                <h2>Create New Deal</h2>

                <%
                    String successMessage = (String) request.getAttribute("successMessage");
                    String errorMessage = (String) request.getAttribute("errorMessage");

                    if (successMessage != null) {
                %>
                    <div class="success-message"><%= successMessage %></div>
                <% } %>

                <% if (errorMessage != null) { %>
                    <div class="error-message"><%= errorMessage %></div>
                <% } %>

                <form method="POST" action="/jsp/create">
                    <div class="form-group">
                        <label for="title">Deal Title *</label>
                        <input type="text" id="title" name="title" required placeholder="e.g., Enterprise Software License - Acme Corp">
                    </div>

                    <div class="form-group">
                        <label for="salesRepId">Sales Representative *</label>
                        <select id="salesRepId" name="salesRepId" required>
                            <option value="">-- Select a Sales Rep --</option>
                            <%
                                @SuppressWarnings("unchecked")
                                List<User> users = (List<User>) request.getAttribute("users");
                                if (users != null) {
                                    for (User user : users) {
                            %>
                                <option value="<%= user.getId() %>">
                                    <%= user.getFirstName() %> <%= user.getLastName() %> (<%= user.getEmail() %>)
                                </option>
                            <%
                                    }
                                }
                            %>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="status">Status</label>
                        <select id="status" name="status">
                            <option value="OPEN" selected>Open</option>
                            <option value="WON">Won</option>
                            <option value="LOST">Lost</option>
                            <option value="CANCELLED">Cancelled</option>
                        </select>
                    </div>

                    <div class="form-section">
                        <h3>Product</h3>
                        <p class="info-text" style="color: #666; font-size: 0.9rem; margin-bottom: 15px;">
                            Note: This JSP form supports one product. Use the JavaScript UI for multiple products.
                        </p>

                        <div class="form-group">
                            <label for="productId">Product ID *</label>
                            <input type="text" id="productId" name="productId" required placeholder="e.g., PROD-001">
                        </div>

                        <div class="form-group">
                            <label for="productName">Product Name *</label>
                            <input type="text" id="productName" name="productName" required placeholder="e.g., Software License">
                        </div>

                        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 15px;">
                            <div class="form-group">
                                <label for="quantity">Quantity *</label>
                                <input type="number" id="quantity" name="quantity" required min="1" value="1">
                            </div>

                            <div class="form-group">
                                <label for="price">Price *</label>
                                <input type="number" id="price" name="price" required min="0" step="0.01" placeholder="0.00">
                            </div>
                        </div>
                    </div>

                    <div class="form-actions">
                        <button type="submit" class="btn btn-primary">Create Deal</button>
                        <a href="/jsp/deals" class="btn btn-secondary">Cancel</a>
                    </div>
                </form>
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