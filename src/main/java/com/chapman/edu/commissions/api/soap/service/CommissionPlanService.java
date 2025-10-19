package com.chapman.edu.commissions.api.soap.service;

import com.chapman.edu.commissions.api.soap.dto.CommissionPlanDTO;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import java.util.List;

/**
 * SOAP Web Service Interface for CommissionPlan operations.
 */
@WebService(name = "CommissionPlanService", targetNamespace = "http://soap.api.commissions.edu.chapman.com/")
public interface CommissionPlanService {

    @WebMethod(operationName = "getAllCommissionPlans")
    List<CommissionPlanDTO> getAllCommissionPlans();

    @WebMethod(operationName = "getCommissionPlanById")
    CommissionPlanDTO getCommissionPlanById(@WebParam(name = "id") String id);

    @WebMethod(operationName = "getCommissionPlansByStatus")
    List<CommissionPlanDTO> getCommissionPlansByStatus(@WebParam(name = "status") String status);

    @WebMethod(operationName = "createCommissionPlan")
    CommissionPlanDTO createCommissionPlan(@WebParam(name = "plan") CommissionPlanDTO plan);

    @WebMethod(operationName = "updateCommissionPlan")
    CommissionPlanDTO updateCommissionPlan(@WebParam(name = "id") String id, @WebParam(name = "plan") CommissionPlanDTO plan);

    @WebMethod(operationName = "deleteCommissionPlan")
    boolean deleteCommissionPlan(@WebParam(name = "id") String id);
}