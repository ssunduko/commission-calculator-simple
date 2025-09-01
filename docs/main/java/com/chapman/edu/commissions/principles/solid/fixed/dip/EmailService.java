package com.chapman.edu.commissions.principles.solid.fixed.dip;

/**
 * This interface defines email operations.
 * It follows the Dependency Inversion Principle by providing an abstraction
 * that high-level modules can depend on, rather than depending on concrete implementations.
 */
public interface EmailService {
    
    /**
     * Sends an email.
     * 
     * @param toAddress The recipient's email address
     * @param subject The email subject
     * @param body The email body
     */
    void sendEmail(String toAddress, String subject, String body);
    
    /**
     * Sends an email with an attachment.
     * 
     * @param toAddress The recipient's email address
     * @param subject The email subject
     * @param body The email body
     * @param attachmentPath The path to the attachment file
     */
    void sendEmailWithAttachment(String toAddress, String subject, String body, String attachmentPath);
}