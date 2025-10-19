package com.chapman.edu.commissions.api.soap;

import com.chapman.edu.commissions.api.rest.SampleDataLoader;
import com.chapman.edu.commissions.api.soap.service.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import jakarta.xml.ws.Service;
import javax.xml.namespace.QName;
import java.net.URL;

/**
 * Base class for SOAP integration tests.
 *
 * WHAT ARE INTEGRATION TESTS?
 * --------------------------
 * Integration tests verify that multiple components work together correctly.
 * For SOAP services, this means:
 * 1. Starting the SOAP server
 * 2. Publishing web services
 * 3. Creating SOAP clients
 * 4. Making real SOAP requests
 * 5. Verifying responses
 *
 * TEST LIFECYCLE:
 * --------------
 * @BeforeAll: Runs once before all tests - starts server and loads data
 * @AfterAll: Runs once after all tests - stops server
 *
 * JAX-WS CLIENT CREATION:
 * ----------------------
 * JAX-WS provides a Service class to create web service clients from WSDL.
 * The client stubs handle all SOAP envelope creation and parsing.
 *
 * DESIGN PATTERN:
 * --------------
 * Template Method Pattern: Base class provides common setup/teardown,
 * subclasses implement specific test cases.
 */
public abstract class SoapIntegrationTestBase {

    protected static SoapServer server;
    protected static final int TEST_PORT = 8083; // Different from default to avoid conflicts
    protected static final String BASE_URL = "http://localhost:" + TEST_PORT + "/soap/";
    protected static final String NAMESPACE = "http://soap.api.commissions.edu.chapman.com/";

    // Service clients
    protected static DealService dealService;
    protected static UserService userService;
    protected static CommissionPlanService commissionPlanService;
    protected static DisputeService disputeService;

    /**
     * Start the SOAP server and load sample data before all tests.
     */
    @BeforeAll
    public static void startServer() throws Exception {
        System.out.println("Starting SOAP server for integration tests...");

        // Create and configure server
        server = new SoapServer(TEST_PORT);

        // Load sample data
        System.out.println("Loading sample data...");
        SampleDataLoader dataLoader = new SampleDataLoader(
                server.getDealRepository(),
                server.getUserRepository(),
                server.getPlanRepository(),
                server.getDisputeRepository()
        );
        dataLoader.loadAllData();

        // Publish services immediately (they create their own HTTP server)
        server.publishServices();

        // Wait a bit for services to be fully available
        Thread.sleep(2000);

        // Create service clients
        createServiceClients();

        System.out.println("SOAP server started and clients created successfully!");
    }

    /**
     * Create JAX-WS service clients from WSDL.
     *
     * JAX-WS CLIENT CREATION STEPS:
     * 1. Create Service object with WSDL URL and QName
     * 2. Get port (service endpoint) from the Service
     * 3. Use the port to make SOAP calls
     */
    private static void createServiceClients() throws Exception {
        // Create DealService client
        URL dealWsdl = new URL(BASE_URL + "DealService?wsdl");
        QName dealQName = new QName(NAMESPACE, "DealService");
        Service dealServiceObj = Service.create(dealWsdl, dealQName);
        dealService = dealServiceObj.getPort(DealService.class);

        // Create UserService client
        URL userWsdl = new URL(BASE_URL + "UserService?wsdl");
        QName userQName = new QName(NAMESPACE, "UserService");
        Service userServiceObj = Service.create(userWsdl, userQName);
        userService = userServiceObj.getPort(UserService.class);

        // Create CommissionPlanService client
        URL planWsdl = new URL(BASE_URL + "CommissionPlanService?wsdl");
        QName planQName = new QName(NAMESPACE, "CommissionPlanService");
        Service planServiceObj = Service.create(planWsdl, planQName);
        commissionPlanService = planServiceObj.getPort(CommissionPlanService.class);

        // Create DisputeService client
        URL disputeWsdl = new URL(BASE_URL + "DisputeService?wsdl");
        QName disputeQName = new QName(NAMESPACE, "DisputeService");
        Service disputeServiceObj = Service.create(disputeWsdl, disputeQName);
        disputeService = disputeServiceObj.getPort(DisputeService.class);
    }

    /**
     * Stop the server after all tests.
     */
    @AfterAll
    public static void stopServer() throws Exception {
        if (server != null) {
            System.out.println("Stopping SOAP server...");
            server.stop();
            System.out.println("SOAP server stopped successfully!");
        }
    }
}