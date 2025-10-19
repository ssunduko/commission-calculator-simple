package com.chapman.edu.commissions.api.soap.service;

import com.chapman.edu.commissions.api.soap.dto.UserDTO;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import java.util.List;

/**
 * SOAP Web Service Interface for User operations.
 */
@WebService(name = "UserService", targetNamespace = "http://soap.api.commissions.edu.chapman.com/")
public interface UserService {

    @WebMethod(operationName = "getAllUsers")
    List<UserDTO> getAllUsers();

    @WebMethod(operationName = "getUserById")
    UserDTO getUserById(@WebParam(name = "id") String id);

    @WebMethod(operationName = "getUserByUsername")
    UserDTO getUserByUsername(@WebParam(name = "username") String username);

    @WebMethod(operationName = "getUsersByRole")
    List<UserDTO> getUsersByRole(@WebParam(name = "role") String role);

    @WebMethod(operationName = "createUser")
    UserDTO createUser(@WebParam(name = "user") UserDTO user);

    @WebMethod(operationName = "updateUser")
    UserDTO updateUser(@WebParam(name = "id") String id, @WebParam(name = "user") UserDTO user);

    @WebMethod(operationName = "deleteUser")
    boolean deleteUser(@WebParam(name = "id") String id);
}