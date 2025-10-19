package com.chapman.edu.commissions.api.soap.service;

import com.chapman.edu.commissions.api.soap.dto.DealDTO;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import java.util.List;

/**
 * SOAP Web Service Interface for Deal operations.
 *
 * WHAT IS A WEB SERVICE INTERFACE?
 * --------------------------------
 * In SOAP, the interface defines the contract (WSDL) that clients use.
 * It's annotated with JAX-WS annotations to enable automatic WSDL generation.
 *
 * JAX-WS ANNOTATIONS:
 * ------------------
 * @WebService: Marks this interface as a web service
 *   - name: The name of the port type in the WSDL
 *   - targetNamespace: XML namespace for the service
 *
 * @WebMethod: Marks a method as a web service operation
 *   - operationName: The name of the operation in the WSDL
 *
 * @WebParam: Defines a parameter for the web service operation
 *   - name: The name of the parameter in the WSDL
 *
 * SOAP VS GRAPHQL:
 * ---------------
 * - SOAP: One service interface per entity, multiple operations
 * - GraphQL: Single endpoint, client specifies fields
 * - SOAP: Strongly typed with WSDL
 * - GraphQL: Strongly typed with SDL schema
 */
@WebService(name = "DealService", targetNamespace = "http://soap.api.commissions.edu.chapman.com/")
public interface DealService {

    /**
     * Get all deals.
     *
     * SOAP REQUEST EXAMPLE:
     * <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
     *                   xmlns:soap="http://soap.api.commissions.edu.chapman.com/">
     *   <soapenv:Header/>
     *   <soapenv:Body>
     *     <soap:getAllDeals/>
     *   </soapenv:Body>
     * </soapenv:Envelope>
     */
    @WebMethod(operationName = "getAllDeals")
    List<DealDTO> getAllDeals();

    /**
     * Get a deal by ID.
     *
     * SOAP REQUEST EXAMPLE:
     * <soap:getDealById>
     *   <id>DEAL-001</id>
     * </soap:getDealById>
     */
    @WebMethod(operationName = "getDealById")
    DealDTO getDealById(@WebParam(name = "id") String id);

    /**
     * Get deals by status.
     */
    @WebMethod(operationName = "getDealsByStatus")
    List<DealDTO> getDealsByStatus(@WebParam(name = "status") String status);

    /**
     * Get deals by sales representative.
     */
    @WebMethod(operationName = "getDealsBySalesRep")
    List<DealDTO> getDealsBySalesRep(@WebParam(name = "salesRepId") String salesRepId);

    /**
     * Create a new deal.
     */
    @WebMethod(operationName = "createDeal")
    DealDTO createDeal(@WebParam(name = "deal") DealDTO deal);

    /**
     * Update an existing deal.
     */
    @WebMethod(operationName = "updateDeal")
    DealDTO updateDeal(@WebParam(name = "id") String id, @WebParam(name = "deal") DealDTO deal);

    /**
     * Delete a deal.
     */
    @WebMethod(operationName = "deleteDeal")
    boolean deleteDeal(@WebParam(name = "id") String id);
}