package com.chapman.edu.commissions.api.soap.dto;

import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for Dispute entity in SOAP web services.
 */
@XmlRootElement(name = "Dispute")
@XmlAccessorType(XmlAccessType.FIELD)
public class DisputeDTO {

    @XmlElement
    private String id;

    @XmlElement(required = true)
    private String calculationId;

    @XmlElement(required = true)
    private String salesRepId;

    @XmlElement
    private String managerId;

    @XmlElement(required = true)
    private String title;

    @XmlElement(required = true)
    private String description;

    @XmlElement(required = true)
    private String status;

    @XmlElement
    private List<DisputeCommentDTO> comments = new ArrayList<>();

    @XmlElement
    private String createdDate;

    @XmlElement
    private String lastUpdatedDate;

    @XmlElement
    private String resolvedDate;

    @XmlElement
    private String resolvedBy;

    @XmlElement
    private String resolution;

    @XmlElement
    private Boolean escalated;

    public DisputeDTO() {
    }

    public DisputeDTO(String id, String calculationId, String salesRepId, String title, String description, String status) {
        this.id = id;
        this.calculationId = calculationId;
        this.salesRepId = salesRepId;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCalculationId() { return calculationId; }
    public void setCalculationId(String calculationId) { this.calculationId = calculationId; }

    public String getSalesRepId() { return salesRepId; }
    public void setSalesRepId(String salesRepId) { this.salesRepId = salesRepId; }

    public String getManagerId() { return managerId; }
    public void setManagerId(String managerId) { this.managerId = managerId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<DisputeCommentDTO> getComments() { return comments; }
    public void setComments(List<DisputeCommentDTO> comments) { this.comments = comments; }

    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }

    public String getLastUpdatedDate() { return lastUpdatedDate; }
    public void setLastUpdatedDate(String lastUpdatedDate) { this.lastUpdatedDate = lastUpdatedDate; }

    public String getResolvedDate() { return resolvedDate; }
    public void setResolvedDate(String resolvedDate) { this.resolvedDate = resolvedDate; }

    public String getResolvedBy() { return resolvedBy; }
    public void setResolvedBy(String resolvedBy) { this.resolvedBy = resolvedBy; }

    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }

    public Boolean getEscalated() { return escalated; }
    public void setEscalated(Boolean escalated) { this.escalated = escalated; }
}