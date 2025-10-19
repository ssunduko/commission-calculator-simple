package com.chapman.edu.commissions.api.soap.service;

import com.chapman.edu.commissions.api.rest.Repository;
import com.chapman.edu.commissions.api.soap.dto.UserDTO;
import com.chapman.edu.commissions.api.soap.mapper.DomainMapper;
import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.UserRole;
import jakarta.jws.WebService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SOAP Web Service Implementation for User operations.
 */
@WebService(
        endpointInterface = "com.chapman.edu.commissions.api.soap.service.UserService",
        serviceName = "UserService",
        portName = "UserServicePort",
        targetNamespace = "http://soap.api.commissions.edu.chapman.com/"
)
public class UserServiceImpl implements UserService {

    private final Repository<User> userRepository;

    public UserServiceImpl(Repository<User> userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return DomainMapper.usersToDTO(userRepository.findAll());
    }

    @Override
    public UserDTO getUserById(String id) {
        return userRepository.findById(id)
                .map(DomainMapper::toDTO)
                .orElse(null);
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        return userRepository.findAll().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .map(DomainMapper::toDTO)
                .orElse(null);
    }

    @Override
    public List<UserDTO> getUsersByRole(String role) {
        UserRole userRole = UserRole.valueOf(role);
        return userRepository.findAll().stream()
                .filter(user -> user.getRoles().contains(userRole))
                .map(DomainMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        User user = DomainMapper.fromDTO(userDTO);
        User saved = userRepository.save(user);
        return DomainMapper.toDTO(saved);
    }

    @Override
    public UserDTO updateUser(String id, UserDTO userDTO) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    if (userDTO.getEmail() != null) {
                        existingUser.setEmail(userDTO.getEmail());
                    }
                    if (userDTO.getFirstName() != null) {
                        existingUser.setFirstName(userDTO.getFirstName());
                    }
                    if (userDTO.getLastName() != null) {
                        existingUser.setLastName(userDTO.getLastName());
                    }
                    if (userDTO.getActive() != null) {
                        existingUser.setActive(userDTO.getActive());
                    }
                    if (userDTO.getDepartment() != null) {
                        existingUser.setDepartment(userDTO.getDepartment());
                    }
                    if (userDTO.getTerritory() != null) {
                        existingUser.setTerritory(userDTO.getTerritory());
                    }

                    User updated = userRepository.save(existingUser);
                    return DomainMapper.toDTO(updated);
                })
                .orElse(null);
    }

    @Override
    public boolean deleteUser(String id) {
        return userRepository.deleteById(id);
    }
}