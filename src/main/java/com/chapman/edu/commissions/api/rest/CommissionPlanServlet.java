package com.chapman.edu.commissions.api.rest;

import com.chapman.edu.commissions.model.CommissionPlan;
import com.chapman.edu.commissions.model.PlanStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servlet for handling CommissionPlan-related HTTP requests.
 *
 * Implements RESTful endpoints for Commission Plan management.
 */
public class CommissionPlanServlet extends BaseServlet {

    private final Repository<CommissionPlan> planRepository;

    public CommissionPlanServlet(Repository<CommissionPlan> planRepository) {
        this.planRepository = planRepository;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String planId = extractResourceId(request);

        if (planId == null) {
            handleGetAllPlans(request, response);
        } else {
            handleGetPlanById(planId, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String requestBody = readRequestBody(request);
            CommissionPlan plan = JsonHelper.fromJson(requestBody, CommissionPlan.class);

            CommissionPlan savedPlan = planRepository.save(plan);
            sendJsonResponse(response, savedPlan, HttpServletResponse.SC_CREATED);
        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid commission plan data: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String planId = extractResourceId(request);

        if (planId == null) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Commission plan ID is required for update");
            return;
        }

        try {
            Optional<CommissionPlan> existingPlan = planRepository.findById(planId);
            if (existingPlan.isEmpty()) {
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND,
                        "Commission plan not found: " + planId);
                return;
            }

            String requestBody = readRequestBody(request);
            CommissionPlan plan = JsonHelper.fromJson(requestBody, CommissionPlan.class);
            plan.setId(planId);

            CommissionPlan updatedPlan = planRepository.save(plan);
            sendJsonResponse(response, updatedPlan, HttpServletResponse.SC_OK);
        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid commission plan data: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String planId = extractResourceId(request);

        if (planId == null) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Commission plan ID is required for deletion");
            return;
        }

        boolean deleted = planRepository.deleteById(planId);

        if (deleted) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND,
                    "Commission plan not found: " + planId);
        }
    }

    private void handleGetAllPlans(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<CommissionPlan> plans = planRepository.findAll();

        String statusParam = request.getParameter("status");
        if (statusParam != null && !statusParam.isEmpty()) {
            try {
                PlanStatus status = PlanStatus.valueOf(statusParam.toUpperCase());
                plans = plans.stream()
                        .filter(plan -> plan.getStatus() == status)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid status value: " + statusParam);
                return;
            }
        }

        sendJsonResponse(response, plans, HttpServletResponse.SC_OK);
    }

    private void handleGetPlanById(String planId, HttpServletResponse response) throws IOException {
        Optional<CommissionPlan> plan = planRepository.findById(planId);

        if (plan.isPresent()) {
            sendJsonResponse(response, plan.get(), HttpServletResponse.SC_OK);
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND,
                    "Commission plan not found: " + planId);
        }
    }
}