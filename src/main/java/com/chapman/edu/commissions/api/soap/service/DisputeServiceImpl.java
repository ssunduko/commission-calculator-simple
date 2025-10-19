package com.chapman.edu.commissions.api.soap.service;

import com.chapman.edu.commissions.api.rest.Repository;
import com.chapman.edu.commissions.api.soap.dto.DisputeDTO;
import com.chapman.edu.commissions.api.soap.mapper.DomainMapper;
import com.chapman.edu.commissions.model.Dispute;
import com.chapman.edu.commissions.model.DisputeStatus;
import jakarta.jws.WebService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SOAP Web Service Implementation for Dispute operations.
 */
@WebService(
        endpointInterface = "com.chapman.edu.commissions.api.soap.service.DisputeService",
        serviceName = "DisputeService",
        portName = "DisputeServicePort",
        targetNamespace = "http://soap.api.commissions.edu.chapman.com/"
)
public class DisputeServiceImpl implements DisputeService {

    private final Repository<Dispute> disputeRepository;

    public DisputeServiceImpl(Repository<Dispute> disputeRepository) {
        this.disputeRepository = disputeRepository;
    }

    @Override
    public List<DisputeDTO> getAllDisputes() {
        return DomainMapper.disputesToDTO(disputeRepository.findAll());
    }

    @Override
    public DisputeDTO getDisputeById(String id) {
        return disputeRepository.findById(id)
                .map(DomainMapper::toDTO)
                .orElse(null);
    }

    @Override
    public List<DisputeDTO> getDisputesBySalesRep(String salesRepId) {
        return disputeRepository.findAll().stream()
                .filter(dispute -> dispute.getSalesRepId().equals(salesRepId))
                .map(DomainMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DisputeDTO> getDisputesByStatus(String status) {
        DisputeStatus disputeStatus = DisputeStatus.valueOf(status);
        return disputeRepository.findAll().stream()
                .filter(dispute -> dispute.getStatus() == disputeStatus)
                .map(DomainMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DisputeDTO createDispute(DisputeDTO disputeDTO) {
        Dispute dispute = DomainMapper.fromDTO(disputeDTO);
        Dispute saved = disputeRepository.save(dispute);
        return DomainMapper.toDTO(saved);
    }

    @Override
    public DisputeDTO updateDispute(String id, DisputeDTO disputeDTO) {
        return disputeRepository.findById(id)
                .map(existingDispute -> {
                    if (disputeDTO.getManagerId() != null) {
                        existingDispute.setManagerId(disputeDTO.getManagerId());
                    }
                    if (disputeDTO.getStatus() != null) {
                        existingDispute.setStatus(DisputeStatus.valueOf(disputeDTO.getStatus()));
                    }
                    if (disputeDTO.getResolution() != null) {
                        existingDispute.setResolution(disputeDTO.getResolution());
                    }
                    if (disputeDTO.getEscalated() != null) {
                        existingDispute.setEscalated(disputeDTO.getEscalated());
                    }

                    Dispute updated = disputeRepository.save(existingDispute);
                    return DomainMapper.toDTO(updated);
                })
                .orElse(null);
    }

    @Override
    public boolean deleteDispute(String id) {
        return disputeRepository.deleteById(id);
    }

    @Override
    public DisputeDTO addDisputeComment(String disputeId, String userId, String userName, String text) {
        return disputeRepository.findById(disputeId)
                .map(dispute -> {
                    dispute.addUserComment(userId, userName, text);
                    Dispute updated = disputeRepository.save(dispute);
                    return DomainMapper.toDTO(updated);
                })
                .orElse(null);
    }
}