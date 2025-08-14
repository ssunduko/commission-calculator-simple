package com.chapman.edu.commissions.principles.solid.fixed.dip;

import java.util.logging.Logger;

/**
 * This class implements the EmailService interface for SMTP-based email sending.
 * It follows the Dependency Inversion Principle by implementing an interface
 * that high-level modules depend on, rather than having them depend on this concrete implementation.
 */
public class SmtpEmailService implements EmailService {
    
    private static final Logger LOGGER = Logger.getLogger(SmtpEmailService.class.getName());
    
    private final String smtpServer;
    private final int smtpPort;
    private final String fromAddress;
    
    /**
     * Constructor with SMTP server details.
     */
    public SmtpEmailService(String smtpServer, int smtpPort, String fromAddress) {
        this.smtpServer = smtpServer;
        this.smtpPort = smtpPort;
        this.fromAddress = fromAddress;
    }
    
    @Override
    public void sendEmail(String toAddress, String subject, String body) {
        // In a real implementation, this would use JavaMail or similar
        LOGGER.info("Sending email via SMTP");
        LOGGER.info("SMTP Server: " + smtpServer + ":" + smtpPort);
        LOGGER.info("From: " + fromAddress);
        LOGGER.info("To: " + toAddress);
        LOGGER.info("Subject: " + subject);
        LOGGER.info("Body: " + body);
        
        // Simulate sending email
        LOGGER.info("Email sent successfully");
    }
    
    @Override
    public void sendEmailWithAttachment(String toAddress, String subject, String body, String attachmentPath) {
        // In a real implementation, this would use JavaMail or similar
        LOGGER.info("Sending email with attachment via SMTP");
        LOGGER.info("SMTP Server: " + smtpServer + ":" + smtpPort);
        LOGGER.info("From: " + fromAddress);
        LOGGER.info("To: " + toAddress);
        LOGGER.info("Subject: " + subject);
        LOGGER.info("Body: " + body);
        LOGGER.info("Attachment: " + attachmentPath);
        
        // Simulate sending email
        LOGGER.info("Email with attachment sent successfully");
    }
}