package com.chapman.edu.commissions.principles.solid.fixed.srp;

import com.chapman.edu.commissions.model.CommissionCalculation;
import com.chapman.edu.commissions.model.User;

import java.util.logging.Logger;

/**
 * This class implements the EmailService interface.
 * It is responsible only for sending email notifications, following the Single Responsibility Principle.
 */
public class EmailServiceImpl implements EmailService {
    private static final Logger LOGGER = Logger.getLogger(EmailServiceImpl.class.getName());
    
    private String smtpServer;
    private int smtpPort;
    private String fromAddress;
    
    public EmailServiceImpl(String smtpServer, int smtpPort, String fromAddress) {
        this.smtpServer = smtpServer;
        this.smtpPort = smtpPort;
        this.fromAddress = fromAddress;
    }
    
    @Override
    public void sendCommissionNotification(User salesRep, CommissionCalculation calculation) {
        String emailSubject = "Commission Calculated for Deal " + calculation.getDealId();
        String emailBody = "Dear " + salesRep.getFullName() + ",\n\n" +
                "Your commission for deal " + calculation.getDealId() + " has been calculated.\n" +
                "Amount: $" + calculation.getNetCommission() + "\n\n" +
                "Thank you,\nCommission Department";
        
        sendEmail(salesRep.getEmail(), emailSubject, emailBody);
    }
    
    @Override
    public void sendReportEmail(String recipientEmail, String subject, String reportContent, String attachmentPath) {
        sendEmail(recipientEmail, subject, reportContent, attachmentPath);
    }
    
    private void sendEmail(String toAddress, String subject, String body) {
        sendEmail(toAddress, subject, body, null);
    }
    
    private void sendEmail(String toAddress, String subject, String body, String attachmentPath) {
        // In a real implementation, this would use JavaMail or similar
        LOGGER.info("Sending email to: " + toAddress);
        LOGGER.info("Subject: " + subject);
        LOGGER.info("Body: " + body);
        
        if (attachmentPath != null) {
            LOGGER.info("Attachment: " + attachmentPath);
        }
        
        // Simulate sending email
        LOGGER.info("Email sent successfully");
    }
}