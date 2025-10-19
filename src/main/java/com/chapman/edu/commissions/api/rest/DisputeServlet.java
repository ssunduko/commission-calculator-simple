package com.chapman.edu.commissions.api.rest;

import com.chapman.edu.commissions.model.Dispute;
import com.chapman.edu.commissions.model.DisputeStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servlet for handling Dispute-related HTTP requests.
 *
 * Implements RESTful endpoints for Dispute management.
 */
public class DisputeServlet extends BaseServlet {

    private final Repository<Dispute> disputeRepository;

    public DisputeServlet(Repository<Dispute> disputeRepository) {
        this.disputeRepository = disputeRepository;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String disputeId = extractResourceId(request);

        if (disputeId == null) {
            handleGetAllDisputes(request, response);
        } else {
            handleGetDisputeById(disputeId, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String requestBody = readRequestBody(request);
            Dispute dispute = JsonHelper.fromJson(requestBody, Dispute.class);

            Dispute savedDispute = disputeRepository.save(dispute);
            sendJsonResponse(response, savedDispute, HttpServletResponse.SC_CREATED);
        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid dispute data: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String disputeId = extractResourceId(request);

        if (disputeId == null) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Dispute ID is required for update");
            return;
        }

        try {
            Optional<Dispute> existingDispute = disputeRepository.findById(disputeId);
            if (existingDispute.isEmpty()) {
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND,
                        "Dispute not found: " + disputeId);
                return;
            }

            String requestBody = readRequestBody(request);
            Dispute dispute = JsonHelper.fromJson(requestBody, Dispute.class);
            dispute.setId(disputeId);

            Dispute updatedDispute = disputeRepository.save(dispute);
            sendJsonResponse(response, updatedDispute, HttpServletResponse.SC_OK);
        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid dispute data: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String disputeId = extractResourceId(request);

        if (disputeId == null) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Dispute ID is required for deletion");
            return;
        }

        boolean deleted = disputeRepository.deleteById(disputeId);

        if (deleted) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND,
                    "Dispute not found: " + disputeId);
        }
    }

    private void handleGetAllDisputes(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Dispute> disputes = disputeRepository.findAll();

        String statusParam = request.getParameter("status");
        if (statusParam != null && !statusParam.isEmpty()) {
            try {
                DisputeStatus status = DisputeStatus.valueOf(statusParam.toUpperCase());
                disputes = disputes.stream()
                        .filter(dispute -> dispute.getStatus() == status)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid status value: " + statusParam);
                return;
            }
        }

        sendJsonResponse(response, disputes, HttpServletResponse.SC_OK);
    }

    private void handleGetDisputeById(String disputeId, HttpServletResponse response) throws IOException {
        Optional<Dispute> dispute = disputeRepository.findById(disputeId);

        if (dispute.isPresent()) {
            sendJsonResponse(response, dispute.get(), HttpServletResponse.SC_OK);
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND,
                    "Dispute not found: " + disputeId);
        }
    }
}