# Sales Commission Calculator - Model Class Diagram Description

## Overview

The Sales Commission Calculator class diagram represents the core domain model for a comprehensive commission management system. The design follows object-oriented principles with clear separation of concerns, proper inheritance hierarchies, and well-defined relationships between entities that handle users, deals, commissions, disputes, and audit trails.

## Core Classes

### User Management

#### User
**Purpose**: Represents system users including sales representatives, managers, and administrators.

**Attributes**:
- `id: String` - Unique identifier for the user
- `username: String` - Login username
- `email: String` - User's email address for notifications
- `role: UserRole` - Enumerated role determining system permissions

**Methods**:
- `authenticate(): boolean` - Validates user credentials
- `getPermissions(): List<Permission>` - Returns role-based permissions

### Deal Management

#### Deal
**Purpose**: Represents sales transactions that generate commissions.

**Attributes**:
- `id: String` - Unique deal identifier
- `amount: BigDecimal` - Total deal value using precise decimal arithmetic
- `closeDate: LocalDateTime` - When the deal was completed
- `status: DealStatus` - Current state of the deal (e.g., PENDING, CLOSED, CANCELLED)

**Methods**:
- `calculateCommission(): BigDecimal` - Computes commission based on deal value
- `validate(): boolean` - Ensures deal data integrity

#### DealProduct
**Purpose**: Represents individual products within a deal for detailed commission calculations.

**Attributes**:
- `productId: String` - Reference to product catalog
- `quantity: int` - Number of units sold
- `unitPrice: BigDecimal` - Price per individual unit

**Methods**:
- `getTotalValue(): BigDecimal` - Calculates total value for this product line

### Commission Management

#### Commission
**Purpose**: Represents calculated commission payments for sales representatives.

**Attributes**:
- `id: String` - Unique commission identifier
- `amount: BigDecimal` - Calculated commission amount
- `status: CommissionStatus` - Payment status (e.g., PENDING, APPROVED, PAID)
- `calculationDate: LocalDateTime` - When commission was calculated

**Methods**:
- `calculate(): BigDecimal` - Computes final commission amount
- `approve(): void` - Approves commission for payment

#### CommissionPlan
**Purpose**: Defines the structure and rules for commission calculations.

**Attributes**:
- `id: String` - Plan identifier
- `name: String` - Descriptive name for the commission plan
- `isActive: boolean` - Whether this plan is currently in use

**Methods**:
- `getTiers(): List<CommissionTier>` - Returns all commission tiers
- `calculateCommission(deal: Deal): BigDecimal` - Applies plan to specific deal

#### CommissionTier
**Purpose**: Defines tiered commission rates based on deal amounts.

**Attributes**:
- `id: String` - Tier identifier
- `minAmount: BigDecimal` - Minimum deal amount for this tier
- `maxAmount: BigDecimal` - Maximum deal amount for this tier
- `rate: BigDecimal` - Commission percentage for this tier

**Methods**:
- `calculateTierCommission(amount: BigDecimal): BigDecimal` - Applies tier rate to amount

#### CommissionRule
**Purpose**: Implements specific business rules for commission calculations.

**Attributes**:
- `id: String` - Rule identifier
- `ruleType: String` - Type of rule (e.g., BONUS, PENALTY, MULTIPLIER)
- `threshold: BigDecimal` - Amount threshold for rule application

**Methods**:
- `apply(deal: Deal): BigDecimal` - Applies rule logic to deal
- `validate(): boolean` - Ensures rule configuration is valid

### Dispute Management

#### Dispute
**Purpose**: Handles commission disputes raised by sales representatives.

**Attributes**:
- `id: String` - Dispute identifier
- `title: String` - Brief description of the dispute
- `description: String` - Detailed dispute explanation
- `status: DisputeStatus` - Current dispute state (e.g., OPEN, UNDER_REVIEW, RESOLVED)
- `createdDate: LocalDateTime` - When dispute was created

**Methods**:
- `escalate(): void` - Escalates dispute to higher authority
- `resolve(): void` - Marks dispute as resolved

#### DisputeComment
**Purpose**: Stores comments and discussion history for disputes.

**Attributes**:
- `id: String` - Comment identifier
- `comment: String` - Comment text content
- `createdDate: LocalDateTim