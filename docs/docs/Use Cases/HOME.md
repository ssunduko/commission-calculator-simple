# Use Cases

![diagram](Commission%20Calculator%20Use%20Case%20Diagram.png)

# Dispute Service System - Use Case Diagram Description

## Overview

The Dispute Service System is a comprehensive platform designed to manage commission disputes through a structured workflow involving multiple actors with different levels of access and responsibilities.

## Actors

### Primary Actors
- **Sales Representative**: Front-line users who create and manage their own disputes
- **Sales Manager**: Supervisory role with approval authority over team disputes
- **Admin**: System administrators with full configuration and management capabilities
- **External Auditor**: Read-only access for compliance and audit purposes

### System Actor
- **System**: Automated processes that handle notifications, escalations, and business rule validation

## Core Functionality

### Dispute Management (Row 1)
The system provides comprehensive dispute lifecycle management:
- **Create Dispute**: Sales representatives can initiate new disputes
- **View Dispute Details**: All actors can view dispute information based on their permissions
- **Update Dispute**: Modify dispute information and status
- **Add Comment**: Collaborative commenting system for all stakeholders
- **Upload Document**: Attach supporting documentation to disputes
- **Download Document**: Retrieve dispute-related documents

### Search and Workflow (Row 2)
Advanced search and review capabilities:
- **Search Disputes**: Query disputes using various criteria
- **Filter Disputes**: Apply filters to narrow down dispute lists
- **Submit for Review**: Sales reps submit disputes to management
- **Review Dispute**: Managers evaluate submitted disputes
- **Approve Dispute**: Management approval of valid disputes
- **Reject Dispute**: Management rejection of invalid disputes

### Advanced Workflow (Row 3)
Escalation and resolution processes:
- **Escalate Dispute**: Manual escalation for complex cases
- **Resolve Dispute**: Mark disputes as resolved
- **Close Dispute**: Final closure of dispute cases
- **Send Notification**: System-generated notifications
- **View Notifications**: Users can review their notifications
- **Configure Preferences**: Personal notification and display settings

### Administrative Functions (Row 4)
System configuration and reporting:
- **Manage Dispute Types**: Admin configuration of dispute categories
- **Configure Workflow Rules**: Setup of business process rules
- **Generate Reports**: Create various analytical reports
- **Export Data**: Extract dispute data for external analysis
- **View Audit Trail**: Complete activity logging for compliance
- **Authenticate User**: Security validation for system access

### Automated System Functions (Row 5)
System-driven processes:
- **Auto-Escalate Overdue**: Automatic escalation of stale disputes
- **Send Reminder Notifications**: Automated reminder system
- **Archive Resolved Disputes**: Automatic archiving of closed cases
- **Validate Business Rules**: System enforcement of business logic
- **Authorize Action**: Permission validation for user actions

## External System Integration

The Dispute Service integrates with four external systems:

### Commission Calculation System
- Validates dispute data against commission calculations
- Ensures disputes are based on accurate commission information

### Email Service
- Powers the notification system
- Sends automated emails for workflow events

### User Management System
- Handles user authentication
- Manages user roles and permissions

### Document Storage System
- Stores uploaded dispute documents
- Provides secure document retrieval

## Relationship Types

### Include Relationships (<<include>>)
Required functionality that must be performed:
- **Authentication**: All dispute creation and updates require user authentication
- **Authorization**: Actions require proper permission validation
- **Business Rule Validation**: Dispute operations must comply with business rules
- **Document Storage**: File uploads and downloads use external document storage

### Extend Relationships (<<extend>>)
Optional additional behaviors:
- **Auto-Escalation**: System can automatically escalate overdue disputes
- **Reminder Notifications**: System extends basic notifications with reminders
- **Manual Escalation**: Reviews can be extended with escalation processes

## Actor Permissions

### Sales Representative
- **Scope**: Own disputes only
- **Permissions**: Create, view, update own disputes; add comments; upload/download documents; search and filter; submit for review; configure personal preferences

### Sales Manager
- **Scope**: Team disputes plus approval authority
- **Permissions**: View team disputes; review, approve, reject disputes; escalate complex cases; resolve and close disputes; generate reports; view audit trails

### Admin
- **Scope**: All disputes plus system configuration
- **Permissions**: Full system access; manage dispute types; configure workflow rules; generate comprehensive reports; export data; view complete audit trails; authorize system-wide actions

### External Auditor
- **Scope**: Read-only access for compliance
- **Permissions**: View disputes; search and filter; generate reports; export data; view audit trails (no modification rights)

### System
- **Scope**: Automated processes
- **Functions**: Send notifications; auto-escalate overdue disputes; send reminders; archive resolved cases; validate business rules; authenticate users; authorize actions

## Workflow Summary

The typical dispute workflow follows this pattern:
1. Sales Rep creates dispute (with authentication and validation)
2. Sales Rep submits dispute for review
3. Manager reviews and either approves/rejects (triggering notifications)
4. If needed, dispute can be escalated
5. Approved disputes are resolved and eventually closed
6. System automatically handles reminders and archives completed disputes
7. All activities are logged for audit purposes