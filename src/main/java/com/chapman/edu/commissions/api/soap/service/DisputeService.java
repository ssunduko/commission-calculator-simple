package com.chapman.edu.commissions.api.soap.service;

import com.chapman.edu.commissions.api.soap.dto.DisputeDTO;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import java.util.List;

/**
 * SOAP Web Service Interface for Dispute operations.
 */
@WebService(name = "DisputeService", targetNamespace = "http://soap.api.commissions.edu.chapman.com/")
public interface DisputeService {

    @WebMethod(operationName = "getAllDisputes")
    List<DisputeDTO> getAllDisputes();

    @WebMethod(operationName = "getDisputeById")
    DisputeDTO getDisputeById(@WebParam(name = "id") String id);

    @WebMethod(operationName = "getDisputesBySalesRep")
    List<DisputeDTO> getDisputesBySalesRep(@WebParam(name = "salesRepId") String salesRepId);

    @WebMethod(operationName = "getDisputesByStatus")
    List<DisputeDTO> getDisputesByStatus(@WebParam(name = "status") String status);

    @WebMethod(operationName = "createDispute")
    DisputeDTO createDispute(@WebParam(name = "dispute") DisputeDTO dispute);

    @WebMethod(operationName = "updateDispute")
    DisputeDTO updateDispute(@WebParam(name = "id") String id, @WebParam(name = "dispute") DisputeDTO dispute);

    @WebMethod(operationName = "deleteDispute")
    boolean deleteDispute(@WebParam(name = "id") String id);

    @WebMethod(operationName = "addDisputeComment")
    DisputeDTO addDisputeComment(@WebParam(name = "disputeId") String disputeId,
                                  @WebParam(name = "userId") String userId,
                                  @WebParam(name = "userName") String userName,
                                  @WebParam(name = "text") String text);
}