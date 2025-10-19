# SOAP API - Postman Examples

This file contains ready-to-use SOAP requests for testing with Postman.

## Setup in Postman

1. Create a new Request in Postman
2. Set method to **POST**
3. Set URL to the service endpoint (e.g., `http://localhost:8082/soap/DealService`)
4. Go to **Headers** tab and add:
   - `Content-Type: text/xml`
   - `SOAPAction: ""` (empty string)
5. Go to **Body** tab, select **raw**, and paste the SOAP envelope below

## Service Endpoints

- **DealService**: `http://localhost:8082/soap/DealService`
- **UserService**: `http://localhost:8082/soap/UserService`
- **CommissionPlanService**: `http://localhost:8082/soap/CommissionPlanService`
- **DisputeService**: `http://localhost:8082/soap/DisputeService`

## WSDL URLs (for service discovery)

- **DealService WSDL**: `http://localhost:8082/soap/DealService?wsdl`
- **UserService WSDL**: `http://localhost:8082/soap/UserService?wsdl`
- **CommissionPlanService WSDL**: `http://localhost:8082/soap/CommissionPlanService?wsdl`
- **DisputeService WSDL**: `http://localhost:8082/soap/DisputeService?wsdl`

---

## DealService Requests

### 1. Get All Deals

**POST** `http://localhost:8082/soap/DealService`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope
    xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:soap="http://soap.api.commissions.edu.chapman.com/">
  <soapenv:Header/>
  <soapenv:Body>
    <soap:getAllDeals/>
  </soapenv:Body>
</soapenv:Envelope>
```

### 2. Get Deal by ID

**POST** `http://localhost:8082/soap/DealService`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope
    xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:soap="http://soap.api.commissions.edu.chapman.com/">
  <soapenv:Header/>
  <soapenv:Body>
    <soap:getDealById>
      <id>DEAL-001</id>
    </soap:getDealById>
  </soapenv:Body>
</soapenv:Envelope>
```

### 3. Get Deals by Status

**POST** `http://localhost:8082/soap/DealService`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope
    xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:soap="http://soap.api.commissions.edu.chapman.com/">
  <soapenv:Header/>
  <soapenv:Body>
    <soap:getDealsByStatus>
      <status>WON</status>
    </soap:getDealsByStatus>
  </soapenv:Body>
</soapenv:Envelope>
```

**Valid Status Values:** OPEN, WON, LOST, PENDING

### 4. Get Deals by Sales Rep

**POST** `http://localhost:8082/soap/DealService`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope
    xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:soap="http://soap.api.commissions.edu.chapman.com/">
  <soapenv:Header/>
  <soapenv:Body>
    <soap:getDealsBySalesRep>
      <salesRepId>USER-001</salesRepId>
    </soap:getDealsBySalesRep>
  </soapenv:Body>
</soapenv:Envelope>
```

### 5. Create Deal

**POST** `http://localhost:8082/soap/DealService`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope
    xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:soap="http://soap.api.commissions.edu.chapman.com/">
  <soapenv:Header/>
  <soapenv:Body>
    <soap:createDeal>
      <deal>
        <title>New Enterprise Deal</title>
        <value>250000.00</value>
        <status>OPEN</status>
        <salesRepId>USER-001</salesRepId>
      </deal>
    </soap:createDeal>
  </soapenv:Body>
</soapenv:Envelope>
```

### 6. Update Deal

**POST** `http://localhost:8082/soap/DealService`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope
    xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:soap="http://soap.api.commissions.edu.chapman.com/">
  <soapenv:Header/>
  <soapenv:Body>
    <soap:updateDeal>
      <id>DEAL-001</id>
      <deal>
        <title>Updated Deal Title</title>
        <status>WON</status>
      </deal>
    </soap:updateDeal>
  </soapenv:Body>
</soapenv:Envelope>
```

### 7. Delete Deal

**POST** `http://localhost:8082/soap/DealService`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope
    xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:soap="http://soap.api.commissions.edu.chapman.com/">
  <soapenv:Header/>
  <soapenv:Body>
    <soap:deleteDeal>
      <id>DEAL-999</id>
    </soap:deleteDeal>
  </soapenv:Body>
</soapenv:Envelope>
```

---

## UserService Requests

### 1. Get All Users

**POST** `http://localhost:8082/soap/UserService`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope
    xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:soap="http://soap.api.commissions.edu.chapman.com/">
  <soapenv:Header/>
  <soapenv:Body>
    <soap:getAllUsers/>
  </soapenv:Body>
</soapenv:Envelope>
```

### 2. Get User by ID

**POST** `http://localhost:8082/soap/UserService`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope
    xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:soap="http://soap.api.commissions.edu.chapman.com/">
  <soapenv:Header/>
  <soapenv:Body>
    <soap:getUserById>
      <id>USER-001</id>
    </soap:getUserById>
  </soapenv:Body>
</soapenv:Envelope>
```

### 3. Get User by Username

**POST** `http://localhost:8082/soap/UserService`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope
    xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:soap="http://soap.api.commissions.edu.chapman.com/">
  <soapenv:Header/>
  <soapenv:Body>
    <soap:getUserByUsername>
      <username>alice.johnson</username>
    </soap:getUserByUsername>
  </soapenv:Body>
</soapenv:Envelope>
```

### 4. Get Users by Role

**POST** `http://localhost:8082/soap/UserService`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope
    xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:soap="http://soap.api.commissions.edu.chapman.com/">
  <soapenv:Header/>
  <soapenv:Body>
    <soap:getUsersByRole>
      <role>SALES_REP</role>
    </soap:getUsersByRole>
  </soapenv:Body>
</soapenv:Envelope>
```

**Valid Role Values:** SALES_REP, SALES_MANAGER, ADMIN, FINANCE

### 5. Create User

**POST** `http://localhost:8082/soap/UserService`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope
    xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:soap="http://soap.api.commissions.edu.chapman.com/">
  <soapenv:Header/>
  <soapenv:Body>
    <soap:createUser>
      <user>
        <username>john.doe</username>
        <email>john.doe@example.com</email>
        <firstName>John</firstName>
        <lastName>Doe</lastName>
        <roles>SALES_REP</roles>
        <active>true</active>
        <department>Sales</department>
        <territory>West</territory>
      </user>
    </soap:createUser>
  </soapenv:Body>
</soapenv:Envelope>
```

### 6. Update User

**POST** `http://localhost:8082/soap/UserService`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope
    xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:soap="http://soap.api.commissions.edu.chapman.com/">
  <soapenv:Header/>
  <soapenv:Body>
    <soap:updateUser>
      <id>USER-001</id>
      <user>
        <email>newemail@example.com</email>
        <department>Enterprise Sales</department>
      </user>
    </soap:updateUser>
  </soapenv:Body>
</soapenv:Envelope>
```

### 7. Delete User

**POST** `http://localhost:8082/soap/UserService`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope
    xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:soap="http://soap.api.commissions.edu.chapman.com/">
  <soapenv:Header/>
  <soapenv:Body>
    <soap:deleteUser>
      <id>USER-999</id>
    </soap:deleteUser>
  </soapenv:Body>
</soapenv:Envelope>
```

---

## CommissionPlanService Requests

### 1. Get All Commission Plans

**POST** `http://localhost:8082/soap/CommissionPlanService`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope
    xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:soap="http://soap.api.commissions.edu.chapman.com/">
  <soapenv:Header/>
  <soapenv:Body>
    <soap:getAllCommissionPlans/>
  </soapenv:Body>
</soapenv:Envelope>
```

### 2. Get Commission Plan by ID

**POST** `http://localhost:8082/soap/CommissionPlanService`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope
    xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:soap="http://soap.api.commissions.edu.chapman.com/">
  <soapenv:Header/>
  <soapenv:Body>
    <soap:getCommissionPlanById>
      <id>PLAN-001</id>
    </soap:getCommissionPlanById>
  </soapenv:Body>
</soapenv:Envelope>
```

### 3. Get Commission Plans by Status

**POST** `http://localhost:8082/soap/CommissionPlanService`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope
    xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:soap="http://soap.api.commissions.edu.chapman.com/">
  <soapenv:Header/>
  <soapenv:Body>
    <soap:getCommissionPlansByStatus>
      <status>ACTIVE</status>
    </soap:getCommissionPlansByStatus>
  </soapenv:Body>
</soapenv:Envelope>
```

**Valid Status Values:** DRAFT, ACTIVE, ARCHIVED

### 4. Create Commission Plan

**POST** `http://localhost:8082/soap/CommissionPlanService`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope
    xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:soap="http://soap.api.commissions.edu.chapman.com/">
  <soapenv:Header/>
  <soapenv:Body>
    <soap:createCommissionPlan>
      <plan>
        <name>Q1 2024 Commission Plan</name>
        <currency>USD</currency>
        <status>DRAFT</status>
      </plan>
    </soap:createCommissionPlan>
  </soapenv:Body>
</soapenv:Envelope>
```

---

## DisputeService Requests

### 1. Get All Disputes

**POST** `http://localhost:8082/soap/DisputeService`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope
    xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:soap="http://soap.api.commissions.edu.chapman.com/">
  <soapenv:Header/>
  <soapenv:Body>
    <soap:getAllDisputes/>
  </soapenv:Body>
</soapenv:Envelope>
```

### 2. Get Dispute by ID

**POST** `http://localhost:8082/soap/DisputeService`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope
    xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:soap="http://soap.api.commissions.edu.chapman.com/">
  <soapenv:Header/>
  <soapenv:Body>
    <soap:getDisputeById>
      <id>DISPUTE-001</id>
    </soap:getDisputeById>
  </soapenv:Body>
</soapenv:Envelope>
```

### 3. Get Disputes by Sales Rep

**POST** `http://localhost:8082/soap/DisputeService`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope
    xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:soap="http://soap.api.commissions.edu.chapman.com/">
  <soapenv:Header/>
  <soapenv:Body>
    <soap:getDisputesBySalesRep>
      <salesRepId>USER-001</salesRepId>
    </soap:getDisputesBySalesRep>
  </soapenv:Body>
</soapenv:Envelope>
```

### 4. Get Disputes by Status

**POST** `http://localhost:8082/soap/DisputeService`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope
    xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:soap="http://soap.api.commissions.edu.chapman.com/">
  <soapenv:Header/>
  <soapenv:Body>
    <soap:getDisputesByStatus>
      <status>OPEN</status>
    </soap:getDisputesByStatus>
  </soapenv:Body>
</soapenv:Envelope>
```

**Valid Status Values:** OPEN, UNDER_REVIEW, RESOLVED, CLOSED

### 5. Create Dispute

**POST** `http://localhost:8082/soap/DisputeService`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope
    xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:soap="http://soap.api.commissions.edu.chapman.com/">
  <soapenv:Header/>
  <soapenv:Body>
    <soap:createDispute>
      <dispute>
        <calculationId>CALC-001</calculationId>
        <salesRepId>USER-001</salesRepId>
        <title>Commission Calculation Discrepancy</title>
        <description>The commission amount seems lower than expected based on the deal value.</description>
        <status>OPEN</status>
      </dispute>
    </soap:createDispute>
  </soapenv:Body>
</soapenv:Envelope>
```

### 6. Add Comment to Dispute

**POST** `http://localhost:8082/soap/DisputeService`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope
    xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:soap="http://soap.api.commissions.edu.chapman.com/">
  <soapenv:Header/>
  <soapenv:Body>
    <soap:addDisputeComment>
      <disputeId>DISPUTE-001</disputeId>
      <userId>USER-002</userId>
      <userName>Manager Smith</userName>
      <text>I have reviewed this dispute and will investigate the calculation.</text>
    </soap:addDisputeComment>
  </soapenv:Body>
</soapenv:Envelope>
```

---

## Testing Tips

### Postman Collection Setup

1. **Import from WSDL** (Recommended):
   - In Postman, click Import
   - Select "Link" tab
   - Paste WSDL URL (e.g., `http://localhost:8082/soap/DealService?wsdl`)
   - Postman will auto-generate all requests

2. **Manual Setup**:
   - Create a new Collection called "SOAP API"
   - Add folders for each service (DealService, UserService, etc.)
   - Create requests using the examples above

### Environment Variables

Create a Postman environment with:
- `baseUrl`: `http://localhost:8082/soap`
- `namespace`: `http://soap.api.commissions.edu.chapman.com/`

Then use: `{{baseUrl}}/DealService`

### Response Validation

Expected successful response format:

```xml
<S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
  <S:Body>
    <ns2:getDealByIdResponse xmlns:ns2="http://soap.api.commissions.edu.chapman.com/">
      <return>
        <id>DEAL-001</id>
        <title>Enterprise Software License</title>
        <value>500000.00</value>
        <status>WON</status>
        <!-- More fields... -->
      </return>
    </ns2:getDealByIdResponse>
  </S:Body>
</S:Envelope>
```

### Common SOAP Faults

If something goes wrong, you'll receive a SOAP fault:

```xml
<S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
  <S:Body>
    <S:Fault>
      <faultcode>S:Server</faultcode>
      <faultstring>Deal not found</faultstring>
    </S:Fault>
  </S:Body>
</S:Envelope>
```

---

## Curl Examples (Alternative to Postman)

### Get All Deals

```bash
curl -X POST http://localhost:8082/soap/DealService \
  -H "Content-Type: text/xml" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:soap="http://soap.api.commissions.edu.chapman.com/">
  <soapenv:Header/>
  <soapenv:Body>
    <soap:getAllDeals/>
  </soapenv:Body>
</soapenv:Envelope>'
```

### Get Deal by ID

```bash
curl -X POST http://localhost:8082/soap/DealService \
  -H "Content-Type: text/xml" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:soap="http://soap.api.commissions.edu.chapman.com/">
  <soapenv:Header/>
  <soapenv:Body>
    <soap:getDealById>
      <id>DEAL-001</id>
    </soap:getDealById>
  </soapenv:Body>
</soapenv:Envelope>'
```