package com.chapman.edu.commissions.api.soap;

import com.chapman.edu.commissions.api.rest.InMemoryRepository;
import com.chapman.edu.commissions.api.rest.Repository;
import com.chapman.edu.commissions.api.rest.SampleDataLoader;
import com.chapman.edu.commissions.api.soap.service.*;
import com.chapman.edu.commissions.model.*;
import jakarta.xml.ws.Endpoint;

import java.util.ArrayList;
import java.util.List;

/**
 * SOAP Server - Standalone JAX-WS SOAP web services.
 *
 * WHAT IS SOAP?
 * ------------
 * SOAP (Simple Object Access Protocol) is a protocol for exchanging structured information
 * in web services using XML. It's:
 * - Standards-based (W3C)
 * - Protocol-independent (usually HTTP)
 * - Language-independent
 * - Platform-independent
 *
 * SOAP VS REST VS GRAPHQL:
 * ------------------------
 * | Aspect        | SOAP          | REST          | GraphQL       |
 * |---------------|---------------|---------------|---------------|
 * | Protocol      | Protocol      | Architectural | Query Lang    |
 * | Data Format   | XML only      | JSON/XML/etc  | JSON          |
 * | Endpoints     | Multiple      | Multiple      | Single        |
 * | Contract      | WSDL          | OpenAPI       | SDL Schema    |
 * | State         | Stateless     | Stateless     | Stateless     |
 * | Caching       | Difficult     | Easy (HTTP)   | Complex       |
 * | Type Safety   | Strong        | Weak          | Strong        |
 * | Discovery     | WSDL          | HATEOAS       | Introspection |
 *
 * JAX-WS (Java API for XML Web Services):
 * ---------------------------------------
 * JAX-WS is the Java standard for creating SOAP web services.
 * - Annotations-based (@WebService, @WebMethod)
 * - Automatic WSDL generation
 * - Type mapping (JAXB for XML binding)
 * - Support for WS-* standards (security, transactions, etc.)
 * - Built-in HTTP server (no need for Tomcat!)
 *
 * ENDPOINT PUBLISHING:
 * -------------------
 * Endpoint.publish() creates a standalone HTTP server automatically.
 * No need for Tomcat or other servlet container.
 *
 * ARCHITECTURE:
 * ------------
 * SoapServer → Service Implementations → Repositories → Domain Models
 *                     ↓
 *                   DTOs (via Mapper)
 *
 * This mirrors the GraphQL architecture for consistency.
 */
public class SoapServer {

    private static final int DEFAULT_PORT = 8082;
    private static final String BASE_URL_TEMPLATE = "http://localhost:%d/soap/";

    private final int port;
    private final Repository<Deal> dealRepository;
    private final Repository<User> userRepository;
    private final Repository<CommissionPlan> planRepository;
    private final Repository<Dispute> disputeRepository;

    private DealServiceImpl dealService;
    private UserServiceImpl userService;
    private CommissionPlanServiceImpl commissionPlanService;
    private DisputeServiceImpl disputeService;

    private List<Endpoint> publishedEndpoints = new ArrayList<>();

    public SoapServer(int port) {
        this.port = port;

        // Initialize repositories
        this.dealRepository = new InMemoryRepository<>("DEAL-", Deal::getId, Deal::setId);
        this.userRepository = new InMemoryRepository<>("USER-", User::getId, User::setId);
        this.planRepository = new InMemoryRepository<>("PLAN-", CommissionPlan::getId, CommissionPlan::setId);
        this.disputeRepository = new InMemoryRepository<>("DISPUTE-", Dispute::getId, Dispute::setId);

        // Initialize service implementations
        this.dealService = new DealServiceImpl(dealRepository);
        this.userService = new UserServiceImpl(userRepository);
        this.commissionPlanService = new CommissionPlanServiceImpl(planRepository);
        this.disputeService = new DisputeServiceImpl(disputeRepository);

        System.out.println("SOAP Server configured on port " + port);
    }

    /**
     * Publish SOAP web services.
     *
     * ENDPOINT PUBLISHING:
     * Each service is published at its own URL path.
     * JAX-WS automatically generates WSDL for each service.
     *
     * WSDL ACCESS:
     * - DealService: http://localhost:8082/soap/DealService?wsdl
     * - UserService: http://localhost:8082/soap/UserService?wsdl
     * - etc.
     */
    public void publishServices() {
        String baseUrl = String.format(BASE_URL_TEMPLATE, port);

        System.out.println("\nPublishing SOAP Web Services...");

        // Publish each service and keep references
        Endpoint dealEndpoint = Endpoint.publish(baseUrl + "DealService", dealService);
        publishedEndpoints.add(dealEndpoint);
        System.out.println("  - DealService: " + baseUrl + "DealService");
        System.out.println("    WSDL: " + baseUrl + "DealService?wsdl");

        Endpoint userEndpoint = Endpoint.publish(baseUrl + "UserService", userService);
        publishedEndpoints.add(userEndpoint);
        System.out.println("  - UserService: " + baseUrl + "UserService");
        System.out.println("    WSDL: " + baseUrl + "UserService?wsdl");

        Endpoint planEndpoint = Endpoint.publish(baseUrl + "CommissionPlanService", commissionPlanService);
        publishedEndpoints.add(planEndpoint);
        System.out.println("  - CommissionPlanService: " + baseUrl + "CommissionPlanService");
        System.out.println("    WSDL: " + baseUrl + "CommissionPlanService?wsdl");

        Endpoint disputeEndpoint = Endpoint.publish(baseUrl + "DisputeService", disputeService);
        publishedEndpoints.add(disputeEndpoint);
        System.out.println("  - DisputeService: " + baseUrl + "DisputeService");
        System.out.println("    WSDL: " + baseUrl + "DisputeService?wsdl");

        System.out.println("\nAll services published successfully!");
    }

    public void start() {
        System.out.println("\nStarting SOAP Server...");

        // Publish SOAP services - Endpoint.publish() creates its own HTTP server
        publishServices();

        System.out.println("\nSOAP Server started successfully!");
        System.out.println("Base URL: " + String.format(BASE_URL_TEMPLATE, port));

        System.out.println("\nExample SOAP Request (getAllDeals):");
        System.out.println("  POST " + String.format(BASE_URL_TEMPLATE, port) + "DealService");
        System.out.println("  Content-Type: text/xml");
        System.out.println("  Body:");
        System.out.println("  <soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"");
        System.out.println("                    xmlns:soap=\"http://soap.api.commissions.edu.chapman.com/\">");
        System.out.println("    <soapenv:Header/>");
        System.out.println("    <soapenv:Body>");
        System.out.println("      <soap:getAllDeals/>");
        System.out.println("    </soapenv:Body>");
        System.out.println("  </soapenv:Envelope>");
        System.out.println("\nPress Ctrl+C to stop the server.");

        // Keep the server running
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            System.out.println("Server interrupted");
        }
    }

    public void stop() {
        // Stop all published endpoints
        for (Endpoint endpoint : publishedEndpoints) {
            endpoint.stop();
        }
        publishedEndpoints.clear();
        System.out.println("SOAP Server stopped successfully");
    }

    // Getters for repositories
    public Repository<Deal> getDealRepository() { return dealRepository; }
    public Repository<User> getUserRepository() { return userRepository; }
    public Repository<CommissionPlan> getPlanRepository() { return planRepository; }
    public Repository<Dispute> getDisputeRepository() { return disputeRepository; }

    /**
     * Main method to run the SOAP server.
     *
     * COMMAND LINE ARGUMENTS:
     * ----------------------
     * - [port]: Port number (default: 8082)
     * - --no-sample-data: Start without loading sample data
     *
     * EXAMPLES:
     * --------
     * mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.api.soap.SoapServer"
     * mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.api.soap.SoapServer" -Dexec.args="9000"
     * mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.api.soap.SoapServer" -Dexec.args="--no-sample-data"
     */
    public static void main(String[] args) {
        try {
            System.out.println("Starting Commission Calculator SOAP Server...");

            int port = DEFAULT_PORT;
            boolean loadSampleData = true;

            // Parse command line arguments
            for (String arg : args) {
                if ("--no-sample-data".equals(arg)) {
                    loadSampleData = false;
                } else {
                    try {
                        port = Integer.parseInt(arg);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid argument: " + arg);
                    }
                }
            }

            System.out.println("Port: " + port);

            SoapServer server = new SoapServer(port);

            // Load sample data if requested
            if (loadSampleData) {
                System.out.println("\n=== Loading Sample Data ===");
                SampleDataLoader dataLoader = new SampleDataLoader(
                        server.getDealRepository(),
                        server.getUserRepository(),
                        server.getPlanRepository(),
                        server.getDisputeRepository()
                );
                dataLoader.loadAllData();
                System.out.println("===========================\n");
            }

            // Add shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                server.stop();
            }));

            // Start the server
            server.start();

        } catch (Exception e) {
            System.err.println("Failed to start SOAP server: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}