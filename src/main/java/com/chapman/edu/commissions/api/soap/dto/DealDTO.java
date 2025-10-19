package com.chapman.edu.commissions.api.soap.dto;

import jakarta.xml.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for Deal entity in SOAP web services.
 *
 * WHAT IS A DTO?
 * --------------
 * A DTO (Data Transfer Object) is a design pattern used to transfer data between processes.
 * In SOAP, DTOs are annotated with JAXB annotations to enable XML serialization/deserialization.
 *
 * JAXB ANNOTATIONS:
 * ----------------
 * - @XmlRootElement: Marks this class as the root of an XML document
 * - @XmlAccessorType: Controls which fields are serialized (FIELD = all non-static/non-transient fields)
 * - @XmlElement: Maps a field to an XML element
 * - @XmlAttribute: Maps a field to an XML attribute
 *
 * WHY SEPARATE DTOs FROM DOMAIN MODELS?
 * ------------------------------------
 * 1. Separation of Concerns: API contract separate from domain logic
 * 2. Version Control: Can evolve API without changing domain models
 * 3. Security: Control what data is exposed
 * 4. Flexibility: Different representations for different API versions
 */
@XmlRootElement(name = "Deal")
@XmlAccessorType(XmlAccessType.FIELD)
public class DealDTO {

    @XmlElement
    private String id;

    @XmlElement(required = true)
    private String title;

    @XmlElement(required = true)
    private BigDecimal value;

    @XmlElement(required = true)
    private String status;

    @XmlElement(required = true)
    private String salesRepId;

    @XmlElement
    private String closeDate; // ISO-8601 format

    @XmlElement
    private String createdDate;

    @XmlElement
    private String lastModifiedDate;

    @XmlElement
    private List<DealProductDTO> products = new ArrayList<>();

    @XmlElement
    private BigDecimal calculatedTotalValue;

    // JAXB requires a no-arg constructor
    public DealDTO() {
    }

    public DealDTO(String id, String title, BigDecimal value, String status, String salesRepId) {
        this.id = id;
        this.title = title;
        this.value = value;
        this.status = status;
        this.salesRepId = salesRepId;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSalesRepId() { return salesRepId; }
    public void setSalesRepId(String salesRepId) { this.salesRepId = salesRepId; }

    public String getCloseDate() { return closeDate; }
    public void setCloseDate(String closeDate) { this.closeDate = closeDate; }

    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }

    public String getLastModifiedDate() { return lastModifiedDate; }
    public void setLastModifiedDate(String lastModifiedDate) { this.lastModifiedDate = lastModifiedDate; }

    public List<DealProductDTO> getProducts() { return products; }
    public void setProducts(List<DealProductDTO> products) { this.products = products; }

    public BigDecimal getCalculatedTotalValue() { return calculatedTotalValue; }
    public void setCalculatedTotalValue(BigDecimal calculatedTotalValue) { this.calculatedTotalValue = calculatedTotalValue; }
}