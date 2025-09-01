# Sequence Diagrams

![diagram](Commission%20Calculator%20Sequence%20Diagram.png)

# Dispute Service - Sequence Diagram Description

## Overview

The Dispute Service sequence diagram illustrates the complete workflow for creating, processing, and resolving commission disputes. The process involves multiple system components working together to handle dispute lifecycle management with proper validation, auditing, and notifications.

## Participants

### Actors
- **Sales Rep**: The user who initiates and manages disputes
- **Sales Manager**: The approving authority who reviews and decides on disputes

### System Components
- **Dispute Controller**: REST API endpoint handler for dispute operations
- **Dispute Service**: Core business logic for dispute processing
- **Document Service**: Handles file uploads and document management
- **Notification Service**: Manages email notifications and communications
- **Audit Service**: Records all system activities for compliance tracking
- **Dispute Repository**: Data access layer for dispute-related operations
- **User Repository**: Data access layer for user and manager information
- **Database**: Persistent storage for all system data

## Process Flow

### Phase 1: Dispute Creation

#### Initial Request
1. **Sales Rep Initiates**: Sales representative sends POST request to `/disputes` endpoint with dispute details including title, description, and calculationId
2. **Request Validation**: Controller validates the incoming request format and required fields
3. **Business Logic Processing**: Dispute Service performs business rule validation to ensure dispute meets system criteria

#### Data Persistence
4. **Database Storage**: Repository saves the new dispute record to the database with initial "PENDING" status
5. **Audit Logging**: Audit Service creates an audit entry recording the dispute creation action
6. **Manager Identification**: System queries User Repository to find the appropriate manager for the sales representative

#### Notification Process
7. **Notification Generation**: Notification Service creates and sends email notification to the identified manager
8. **Response**: Controller returns HTTP 201 Created status with dispute ID and initial status to the sales representative

### Phase 2: Document Upload

#### File Handling
1. **Document Submission**: Sales representative uploads supporting documents via POST to `/disputes/{disputeId}/documents`
2. **File Validation**: Document Service performs comprehensive validation including file type checking, size limits, and security scanning
3. **File Processing**: System generates file hash for integrity verification and saves file to secure file system

#### Document Association
4. **Database Linking**: Repository creates association between the uploaded document and the specific dispute
5. **Confirmation**: System returns document ID and filename confirmation to the sales representative

### Phase 3: Manager Review

#### Information Retrieval
1. **Dispute Access**: Sales manager requests dispute details via GET `/disputes/{disputeId}`
2. **Data Aggregation**: Service retrieves complete dispute information including comments and associated documents
3. **Manager Interface**: System presents comprehensive dispute view to manager for informed decision-making

### Phase 4: Manager Decision - Approval Process

#### Approval Workflow
1. **Decision Submission**: Manager submits approval via PUT `/disputes/{disputeId}/approve` with resolution details and comments
2. **Status Update**: Repository updates dispute status to "APPROVED" in the database
3. **Commission Calculation**: Service calculates corrected commission amounts based on dispute resolution

#### Audit and Notification
4. **Activity Logging**: Audit Service records the approval action with manager identification
5. **Stakeholder Notification**: Notification Service sends approval confirmation to the original sales representative
6. **Commission Update**: System updates the commission record to reflect approved changes

### Phase 5: Auto-Resolution

#### Automated Closure
1. **Schedule Resolution**: System schedules automatic resolution after 24-hour waiting period
2. **Status Finalization**: After waiting period expires, Repository updates dispute status to "RESOLVED"
3. **Final Notification**: Notification Service sends resolution confirmation to all involved parties

## Technical Implementation Details

### Database Operations
The system performs several key database operations throughout the process:
- **INSERT Operations**: New dispute records, audit entries, and document associations
- **UPDATE Operations**: Dispute status changes and commission record modifications  
- **SELECT Operations**: User and manager data retrieval, dispute information queries

### HTTP Response Codes
The API uses standard HTTP status codes for communication:
- **201 Created**: Successful dispute creation
- **200 OK**: Successful operations and data retrieval
- **400 Bad Request**: Invalid request format or missing required fields
- **401 Unauthorized**: Authentication failure
- **403 Forbidden**: Insufficient permissions for requested action
- **404 Not Found**: Requested resource does not exist
- **500 Server Error**: Internal system errors

### Security and Validation

#### Request Validation
- Input sanitization and format validation at controller level
- Business rule enforcement at service layer
- File type and size validation for document uploads

#### Audit Trail
- Comprehensive logging of all dispute-related actions
- User identification tracking for all operations
- Timestamp recording for compliance requirements

### Notification System

#### Email Templates
- Dispute creation notifications for managers
- Approval confirmations for sales representatives  
- Resolution notifications for all stakeholders

#### Delivery Mechanism
- Asynchronous email processing to prevent system delays
- Delivery confirmation tracking
- Template-based messaging for consistency

## Error Handling

The system includes robust error handling at multiple levels:
- **Controller Level**: Input validation and HTTP error responses
- **Service Level**: Business logic validation and exception handling
- **Repository Level**: Database constraint enforcement and transaction management
- **Integration Level**: External service failure handling and retry logic

## Performance Considerations

### Asynchronous Operations
- File upload processing occurs independently of main workflow
- Email notifications are sent asynchronously to prevent blocking
- Audit logging is performed in parallel with main operations

### Data Optimization
- Database queries are optimized for dispute retrieval operations
- Document storage uses efficient file system organization
- Audit data is structured for fast compliance reporting

## Business Rules

### Dispute Validation
- Commission calculation validation against original records
- Business rule compliance checking before dispute creation
- Manager assignment based on organizational hierarchy

### Approval Authority
- Manager verification before approval processing
- Commission correction calculations with proper validation
- Automatic resolution timing based on business requirements

This sequence diagram represents a comprehensive dispute management system that ensures proper validation, auditing, and stakeholder communication throughout the entire dispute lifecycle.