// Configuration
const API_BASE_URL = '/api/v1/integration/deals';
const USERS_API_URL = '/api/v1/integration/users';

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    loadDeals();
    loadDashboard();
    loadUsers();
    document.getElementById('apiUrl').textContent = window.location.origin + API_BASE_URL;
});

// Tab Management
function showTab(tabName) {
    // Hide all tabs
    const tabs = document.querySelectorAll('.tab-content');
    tabs.forEach(tab => tab.classList.remove('active'));

    // Remove active class from all buttons
    const buttons = document.querySelectorAll('.tab-button');
    buttons.forEach(btn => btn.classList.remove('active'));

    // Show selected tab
    document.getElementById(tabName + '-tab').classList.add('active');

    // Add active class to clicked button
    event.target.classList.add('active');

    // Refresh data for dashboard when switching to it
    if (tabName === 'dashboard') {
        loadDashboard();
    }
}

// Load Deals
async function loadDeals() {
    const container = document.getElementById('dealsContainer');
    container.innerHTML = '<p class="loading">Loading deals...</p>';

    try {
        // Build query string from filters
        const status = document.getElementById('statusFilter').value;
        const salesRepId = document.getElementById('salesRepFilter').value.trim();

        let url = API_BASE_URL;
        const params = new URLSearchParams();

        if (status) params.append('status', status);
        if (salesRepId) params.append('salesRepId', salesRepId);

        if (params.toString()) {
            url += '?' + params.toString();
        }

        const response = await fetch(url);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const deals = await response.json();

        if (!Array.isArray(deals) || deals.length === 0) {
            container.innerHTML = '<p class="loading">No deals found. Try adjusting your filters or create a new deal.</p>';
            return;
        }

        container.innerHTML = deals.map(deal => createDealCard(deal)).join('');
        updateConnectionStatus('connected');

    } catch (error) {
        console.error('Error loading deals:', error);
        container.innerHTML = `<p class="loading" style="color: #dc3545;">Error loading deals: ${error.message}</p>`;
        updateConnectionStatus('error');
    }
}

// Create Deal Card HTML
function createDealCard(deal) {
    const statusClass = 'badge-' + (deal.status || 'open').toLowerCase();
    const totalValue = deal.products && Array.isArray(deal.products)
        ? deal.products.reduce((sum, p) => {
            const quantity = Number(p.quantity) || 0;
            const price = Number(p.unitPrice || p.price) || 0;
            return sum + (quantity * price);
          }, 0).toFixed(2)
        : '0.00';

    const productsHtml = deal.products && deal.products.length > 0
        ? `
            <div class="deal-products">
                <h4>Products (${deal.products.length})</h4>
                <div class="product-list">
                    ${deal.products.map(p => {
                        const price = Number(p.unitPrice || p.price) || 0;
                        return `
                        <div class="product">
                            <span>${p.productId || 'N/A'}</span>
                            <span>${p.productName || 'Unknown'}</span>
                            <span>Qty: ${p.quantity || 0}</span>
                            <span>$${price.toFixed(2)}</span>
                        </div>
                        `;
                    }).join('')}
                </div>
            </div>
        `
        : '<p style="color: #6c757d; font-style: italic;">No products</p>';

    return `
        <div class="deal-card">
            <div class="deal-header">
                <div>
                    <div class="deal-title">${escapeHtml(deal.title)}</div>
                    <div class="deal-id">${deal.id}</div>
                </div>
                <span class="badge ${statusClass}">${deal.status}</span>
            </div>

            <div class="deal-info">
                <div class="info-item">
                    <span class="info-label">Sales Rep</span>
                    <span class="info-value">${deal.salesRepId || 'N/A'}</span>
                </div>
                <div class="info-item">
                    <span class="info-label">Total Value</span>
                    <span class="info-value">$${totalValue}</span>
                </div>
                <div class="info-item">
                    <span class="info-label">Created</span>
                    <span class="info-value">${formatDate(deal.createdDate)}</span>
                </div>
                ${deal.closeDate ? `
                <div class="info-item">
                    <span class="info-label">Close Date</span>
                    <span class="info-value">${formatDate(deal.closeDate)}</span>
                </div>
                ` : ''}
            </div>

            ${productsHtml}

            <div class="deal-actions">
                <button onclick="viewDeal('${deal.id}')" class="btn btn-primary btn-small">View Details</button>
                ${deal.status === 'OPEN' ? `
                    <button onclick="closeDeal('${deal.id}')" class="btn btn-secondary btn-small">Close Deal</button>
                ` : ''}
                ${deal.status === 'OPEN' ? `
                    <button onclick="deleteDeal('${deal.id}')" class="btn btn-danger btn-small">Delete</button>
                ` : ''}
            </div>
        </div>
    `;
}

// View Deal Details
async function viewDeal(dealId) {
    try {
        const response = await fetch(`${API_BASE_URL}/${dealId}`);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const deal = await response.json();
        alert(JSON.stringify(deal, null, 2));

    } catch (error) {
        console.error('Error viewing deal:', error);
        alert('Error loading deal details: ' + error.message);
    }
}

// Close Deal
async function closeDeal(dealId) {
    if (!confirm('Are you sure you want to close this deal?')) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/${dealId}/close`, {
            method: 'POST'
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || `HTTP error! status: ${response.status}`);
        }

        alert('Deal closed successfully!');
        loadDeals();

    } catch (error) {
        console.error('Error closing deal:', error);
        alert('Error closing deal: ' + error.message);
    }
}

// Delete Deal
async function deleteDeal(dealId) {
    if (!confirm('Are you sure you want to delete this deal? This action cannot be undone.')) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/${dealId}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || `HTTP error! status: ${response.status}`);
        }

        alert('Deal deleted successfully!');
        loadDeals();

    } catch (error) {
        console.error('Error deleting deal:', error);
        alert('Error deleting deal: ' + error.message);
    }
}

// Clear Filters
function clearFilters() {
    document.getElementById('statusFilter').value = '';
    document.getElementById('salesRepFilter').value = '';
    loadDeals();
}

// Add Product to Form
function addProduct() {
    const container = document.getElementById('productsContainer');
    const productItem = document.createElement('div');
    productItem.className = 'product-item';
    productItem.innerHTML = `
        <input type="text" class="product-id" placeholder="Product ID (e.g., PROD-001)" required>
        <input type="text" class="product-name" placeholder="Product Name" required>
        <input type="number" class="product-quantity" placeholder="Quantity" min="1" value="1" required>
        <input type="number" class="product-price" placeholder="Price" step="0.01" min="0" required>
        <button type="button" onclick="removeProduct(this)" class="btn btn-danger btn-small">Remove</button>
    `;
    container.appendChild(productItem);
}

// Remove Product from Form
function removeProduct(button) {
    const productItem = button.parentElement;
    const container = document.getElementById('productsContainer');

    // Keep at least one product
    if (container.children.length > 1) {
        productItem.remove();
    } else {
        alert('At least one product is required.');
    }
}

// Create Deal
async function createDeal(event) {
    event.preventDefault();

    const messageDiv = document.getElementById('createMessage');
    messageDiv.className = 'message';
    messageDiv.style.display = 'none';

    try {
        // Collect form data
        const title = document.getElementById('dealTitle').value.trim();
        const salesRepId = document.getElementById('dealSalesRepId').value.trim();
        const status = document.getElementById('dealStatus').value;

        // Collect products
        const productItems = document.querySelectorAll('#productsContainer .product-item');
        const products = [];

        for (const item of productItems) {
            const productId = item.querySelector('.product-id').value.trim();
            const productName = item.querySelector('.product-name').value.trim();
            const quantity = parseInt(item.querySelector('.product-quantity').value);
            const price = parseFloat(item.querySelector('.product-price').value);

            if (productId && productName && quantity > 0 && price >= 0) {
                products.push({
                    productId,
                    productName,
                    quantity,
                    price,  // Changed from unitPrice to price to match Java model
                    discount: 0  // Always include discount field (default to 0)
                });
            }
        }

        if (products.length === 0) {
            throw new Error('At least one valid product is required.');
        }

        // Create deal object
        const deal = {
            title,
            salesRepId,
            status,
            products
        };

        // Send to API
        const response = await fetch(API_BASE_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(deal)
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || `HTTP error! status: ${response.status}`);
        }

        const createdDeal = await response.json();

        // Show success message
        messageDiv.className = 'message success';
        messageDiv.textContent = `Deal created successfully! Deal ID: ${createdDeal.id}`;
        messageDiv.style.display = 'block';

        // Reset form
        resetForm();

        // Switch to deals tab after 2 seconds
        setTimeout(() => {
            showTab('deals');
            loadDeals();
        }, 2000);

    } catch (error) {
        console.error('Error creating deal:', error);
        messageDiv.className = 'message error';
        messageDiv.textContent = 'Error creating deal: ' + error.message;
        messageDiv.style.display = 'block';
    }
}

// Reset Form
function resetForm() {
    document.getElementById('createDealForm').reset();

    // Reset products to single item
    const container = document.getElementById('productsContainer');
    container.innerHTML = `
        <div class="product-item">
            <input type="text" class="product-id" placeholder="Product ID (e.g., PROD-001)" required>
            <input type="text" class="product-name" placeholder="Product Name" required>
            <input type="number" class="product-quantity" placeholder="Quantity" min="1" value="1" required>
            <input type="number" class="product-price" placeholder="Price" step="0.01" min="0" required>
            <button type="button" onclick="removeProduct(this)" class="btn btn-danger btn-small">Remove</button>
        </div>
    `;
}

// Load Dashboard
async function loadDashboard() {
    try {
        const response = await fetch(API_BASE_URL);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const deals = await response.json();

        // Calculate statistics
        const totalDeals = deals.length;
        const openDeals = deals.filter(d => d.status === 'OPEN').length;
        const wonDeals = deals.filter(d => d.status === 'WON').length;

        const totalValue = deals.reduce((sum, deal) => {
            const dealValue = deal.products && Array.isArray(deal.products)
                ? deal.products.reduce((s, p) => {
                    const quantity = Number(p.quantity) || 0;
                    const price = Number(p.unitPrice || p.price) || 0;
                    return s + (quantity * price);
                  }, 0)
                : 0;
            return sum + dealValue;
        }, 0);

        // Update stats
        document.getElementById('totalDeals').textContent = totalDeals;
        document.getElementById('openDeals').textContent = openDeals;
        document.getElementById('wonDeals').textContent = wonDeals;
        document.getElementById('totalValue').textContent = '$' + totalValue.toFixed(2);

        updateConnectionStatus('connected');

    } catch (error) {
        console.error('Error loading dashboard:', error);
        document.getElementById('totalDeals').textContent = 'Error';
        document.getElementById('openDeals').textContent = 'Error';
        document.getElementById('wonDeals').textContent = 'Error';
        document.getElementById('totalValue').textContent = 'Error';
        updateConnectionStatus('error');
    }
}

// Update Connection Status
function updateConnectionStatus(status) {
    const statusElement = document.getElementById('connectionStatus');

    if (status === 'connected') {
        statusElement.textContent = 'Connected';
        statusElement.className = 'badge badge-open';
    } else if (status === 'error') {
        statusElement.textContent = 'Disconnected';
        statusElement.className = 'badge badge-lost';
    } else {
        statusElement.textContent = 'Checking...';
        statusElement.className = 'badge badge-pending';
    }
}

// Utility Functions
function formatDate(dateString) {
    if (!dateString) return 'N/A';

    try {
        const date = new Date(dateString);
        return date.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        });
    } catch (e) {
        return dateString;
    }
}

function escapeHtml(text) {
    const map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;'
    };
    return text.replace(/[&<>"']/g, m => map[m]);
}

// Load Users
async function loadUsers() {
    try {
        const response = await fetch(USERS_API_URL);

        if (!response.ok) {
            console.warn('Could not load users:', response.status);
            return;
        }

        const users = await response.json();
        const select = document.getElementById('dealSalesRepId');

        // Clear existing options (except the first placeholder)
        select.innerHTML = '<option value="">-- Select a Sales Rep --</option>';

        // Add user options
        users.forEach(user => {
            const option = document.createElement('option');
            option.value = user.id;
            option.textContent = `${user.firstName} ${user.lastName} (${user.email})`;
            select.appendChild(option);
        });

        console.log(`Loaded ${users.length} users into dropdown`);

    } catch (error) {
        console.error('Error loading users:', error);
        // Non-critical error, users can still manually enter ID if needed
    }
}