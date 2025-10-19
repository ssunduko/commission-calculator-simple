package com.chapman.edu.commissions.api.soap.dto;

import jakarta.xml.bind.annotation.*;

/**
 * Data Transfer Object for CommissionPlan entity in SOAP web services.
 */
@XmlRootElement(name = "CommissionPlan")
@XmlAccessorType(XmlAccessType.FIELD)
public class CommissionPlanDTO {

    @XmlElement
    private String id;

    @XmlElement(required = true)
    private String name;

    @XmlElement(required = true)
    private String currency;

    @XmlElement(required = true)
    private String status;

    @XmlElement
    private String effectiveStartDate;

    @XmlElement
    private String effectiveEndDate;

    @XmlElement
    private String createdDate;

    @XmlElement
    private String lastModifiedDate;

    @XmlElement
    private String createdBy;

    public CommissionPlanDTO() {
    }

    public CommissionPlanDTO(String id, String name, String currency, String status) {
        this.id = id;
        this.name = name;
        this.currency = currency;
        this.status = status;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getEffectiveStartDate() { return effectiveStartDate; }
    public void setEffectiveStartDate(String effectiveStartDate) { this.effectiveStartDate = effectiveStartDate; }

    public String getEffectiveEndDate() { return effectiveEndDate; }
    public void setEffectiveEndDate(String effectiveEndDate) { this.effectiveEndDate = effectiveEndDate; }

    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }

    public String getLastModifiedDate() { return lastModifiedDate; }
    public void setLastModifiedDate(String lastModifiedDate) { this.lastModifiedDate = lastModifiedDate; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}