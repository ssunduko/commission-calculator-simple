package com.chapman.edu.commissions.api.soap.dto;

import jakarta.xml.bind.annotation.*;

/**
 * Data Transfer Object for DisputeComment entity in SOAP web services.
 */
@XmlRootElement(name = "DisputeComment")
@XmlAccessorType(XmlAccessType.FIELD)
public class DisputeCommentDTO {

    @XmlElement
    private String id;

    @XmlElement(required = true)
    private String disputeId;

    @XmlElement
    private String userId;

    @XmlElement
    private String userName;

    @XmlElement(required = true)
    private String text;

    @XmlElement
    private Boolean isSystemComment;

    @XmlElement
    private String createdDate;

    public DisputeCommentDTO() {
    }

    public DisputeCommentDTO(String disputeId, String text) {
        this.disputeId = disputeId;
        this.text = text;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDisputeId() { return disputeId; }
    public void setDisputeId(String disputeId) { this.disputeId = disputeId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public Boolean getIsSystemComment() { return isSystemComment; }
    public void setIsSystemComment(Boolean isSystemComment) { this.isSystemComment = isSystemComment; }

    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }
}