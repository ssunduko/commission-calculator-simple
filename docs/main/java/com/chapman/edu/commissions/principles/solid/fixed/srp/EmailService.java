package com.chapman.edu.commissions.principles.solid.fixed.srp;

import com.chapman.edu.commissions.model.CommissionCalculation;
import com.chapman.edu.commissions.model.User;

/**
 * This interface defines email notification operations.
 * It follows the Single Responsibility Principle by focusing only on email notifications.
 */
public interface EmailService {

    /**
     * Sends an email notification about a commission calculation to a sales rep.
     * 
     * @param salesRep The sales rep to notify
     * @param calculation The commission calculation details
     */
    void sendCommissionNotification(User salesRep, CommissionCalculation calculation);

    /**
     * Sends a report via email.
     * 
     * @param recipientEmail The recipient email address
     * @param subject The email subject
     * @param reportContent The content of the report
     * @param attachmentPath Optional path to an attachment
     */
    void sendReportEmail(String recipientEmail, String subject, String reportContent, String attachmentPath);
}
