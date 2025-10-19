package com.chapman.edu.commissions.api.soap.service;

import com.chapman.edu.commissions.api.rest.Repository;
import com.chapman.edu.commissions.api.soap.dto.DealDTO;
import com.chapman.edu.commissions.api.soap.mapper.DomainMapper;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealStatus;
import jakarta.jws.WebService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SOAP Web Service Implementation for Deal operations.
 *
 * IMPLEMENTATION PATTERN:
 * ----------------------
 * This class implements the DealService interface and provides actual business logic.
 *
 * @WebService annotation parameters:
 * - endpointInterface: Points to the service interface
 * - serviceName: The name of the service in WSDL
 * - portName: The name of the port in WSDL
 * - targetNamespace: Must match the interface's namespace
 *
 * DEPENDENCY INJECTION:
 * --------------------
 * The repository is injected via constructor, following Dependency Inversion Principle.
 *
 * MAPPER PATTERN:
 * --------------
 * Uses DomainMapper to convert between domain models and DTOs.
 */
@WebService(
        endpointInterface = "com.chapman.edu.commissions.api.soap.service.DealService",
        serviceName = "DealService",
        portName = "DealServicePort",
        targetNamespace = "http://soap.api.commissions.edu.chapman.com/"
)
public class DealServiceImpl implements DealService {

    private final Repository<Deal> dealRepository;

    public DealServiceImpl(Repository<Deal> dealRepository) {
        this.dealRepository = dealRepository;
    }

    @Override
    public List<DealDTO> getAllDeals() {
        return DomainMapper.dealsToDTO(dealRepository.findAll());
    }

    @Override
    public DealDTO getDealById(String id) {
        return dealRepository.findById(id)
                .map(DomainMapper::toDTO)
                .orElse(null);
    }

    @Override
    public List<DealDTO> getDealsByStatus(String status) {
        DealStatus dealStatus = DealStatus.valueOf(status);
        return dealRepository.findAll().stream()
                .filter(deal -> deal.getStatus() == dealStatus)
                .map(DomainMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DealDTO> getDealsBySalesRep(String salesRepId) {
        return dealRepository.findAll().stream()
                .filter(deal -> deal.getSalesRepId().equals(salesRepId))
                .map(DomainMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DealDTO createDeal(DealDTO dealDTO) {
        Deal deal = DomainMapper.fromDTO(dealDTO);
        Deal saved = dealRepository.save(deal);
        return DomainMapper.toDTO(saved);
    }

    @Override
    public DealDTO updateDeal(String id, DealDTO dealDTO) {
        return dealRepository.findById(id)
                .map(existingDeal -> {
                    // Update fields
                    if (dealDTO.getTitle() != null) {
                        existingDeal.setTitle(dealDTO.getTitle());
                    }
                    if (dealDTO.getValue() != null) {
                        existingDeal.setValue(dealDTO.getValue());
                    }
                    if (dealDTO.getStatus() != null) {
                        existingDeal.setStatus(DealStatus.valueOf(dealDTO.getStatus()));
                    }
                    if (dealDTO.getSalesRepId() != null) {
                        existingDeal.setSalesRepId(dealDTO.getSalesRepId());
                    }

                    Deal updated = dealRepository.save(existingDeal);
                    return DomainMapper.toDTO(updated);
                })
                .orElse(null);
    }

    @Override
    public boolean deleteDeal(String id) {
        return dealRepository.deleteById(id);
    }
}