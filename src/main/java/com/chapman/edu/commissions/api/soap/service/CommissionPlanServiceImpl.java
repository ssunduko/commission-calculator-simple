package com.chapman.edu.commissions.api.soap.service;

import com.chapman.edu.commissions.api.rest.Repository;
import com.chapman.edu.commissions.api.soap.dto.CommissionPlanDTO;
import com.chapman.edu.commissions.api.soap.mapper.DomainMapper;
import com.chapman.edu.commissions.model.CommissionPlan;
import com.chapman.edu.commissions.model.PlanStatus;
import jakarta.jws.WebService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SOAP Web Service Implementation for CommissionPlan operations.
 */
@WebService(
        endpointInterface = "com.chapman.edu.commissions.api.soap.service.CommissionPlanService",
        serviceName = "CommissionPlanService",
        portName = "CommissionPlanServicePort",
        targetNamespace = "http://soap.api.commissions.edu.chapman.com/"
)
public class CommissionPlanServiceImpl implements CommissionPlanService {

    private final Repository<CommissionPlan> planRepository;

    public CommissionPlanServiceImpl(Repository<CommissionPlan> planRepository) {
        this.planRepository = planRepository;
    }

    @Override
    public List<CommissionPlanDTO> getAllCommissionPlans() {
        return DomainMapper.plansToDTO(planRepository.findAll());
    }

    @Override
    public CommissionPlanDTO getCommissionPlanById(String id) {
        return planRepository.findById(id)
                .map(DomainMapper::toDTO)
                .orElse(null);
    }

    @Override
    public List<CommissionPlanDTO> getCommissionPlansByStatus(String status) {
        PlanStatus planStatus = PlanStatus.valueOf(status);
        return planRepository.findAll().stream()
                .filter(plan -> plan.getStatus() == planStatus)
                .map(DomainMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CommissionPlanDTO createCommissionPlan(CommissionPlanDTO planDTO) {
        CommissionPlan plan = DomainMapper.fromDTO(planDTO);
        CommissionPlan saved = planRepository.save(plan);
        return DomainMapper.toDTO(saved);
    }

    @Override
    public CommissionPlanDTO updateCommissionPlan(String id, CommissionPlanDTO planDTO) {
        return planRepository.findById(id)
                .map(existingPlan -> {
                    if (planDTO.getName() != null) {
                        existingPlan.setName(planDTO.getName());
                    }
                    if (planDTO.getStatus() != null) {
                        existingPlan.setStatus(PlanStatus.valueOf(planDTO.getStatus()));
                    }

                    CommissionPlan updated = planRepository.save(existingPlan);
                    return DomainMapper.toDTO(updated);
                })
                .orElse(null);
    }

    @Override
    public boolean deleteCommissionPlan(String id) {
        return planRepository.deleteById(id);
    }
}