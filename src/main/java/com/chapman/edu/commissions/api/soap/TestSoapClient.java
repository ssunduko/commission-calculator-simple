package com.chapman.edu.commissions.api.soap;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class TestSoapClient {
    public static void main(String[] args) {
        try {
            // Test if server is running by checking WSDL
            System.out.println("Testing SOAP Server connectivity...");
            
            // First try to access WSDL
            URL wsdlUrl = new URL("http://localhost:8082/soap/DealService?wsdl");
            HttpURLConnection wsdlConn = (HttpURLConnection) wsdlUrl.openConnection();
            wsdlConn.setRequestMethod("GET");
            wsdlConn.setConnectTimeout(5000);
            wsdlConn.setReadTimeout(5000);
            
            try {
                int wsdlResponseCode = wsdlConn.getResponseCode();
                System.out.println("WSDL Response Code: " + wsdlResponseCode);
                
                if (wsdlResponseCode == 200) {
                    System.out.println("✓ SOAP Server is running and WSDL is accessible");
                    
                    // Now test the SOAP request
                    testGetDealById();
                } else {
                    System.out.println("✗ SOAP Server may not be running or WSDL not accessible");
                }
            } catch (IOException e) {
                System.out.println("✗ Cannot connect to SOAP server: " + e.getMessage());
                System.out.println("Please start the SOAP server first:");
                System.out.println("mvn exec:java -Dexec.mainClass=\"com.chapman.edu.commissions.api.soap.SoapServer\"");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void testGetDealById() {
        try {
            System.out.println("\nTesting getDealById with DEAL-001...");
            
            URL url = new URL("http://localhost:8082/soap/DealService");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            // Set up the connection for SOAP
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            connection.setRequestProperty("SOAPAction", "");
            connection.setDoOutput(true);
            
            // SOAP envelope from the issue description
            String soapEnvelope = 
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                "                  xmlns:soap=\"http://soap.api.commissions.edu.chapman.com/\">\n" +
                "  <soapenv:Header/>\n" +
                "  <soapenv:Body>\n" +
                "    <soap:getDealById>\n" +
                "      <id>DEAL-001</id>\n" +
                "    </soap:getDealById>\n" +
                "  </soapenv:Body>\n" +
                "</soapenv:Envelope>";
            
            // Send the SOAP request
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = soapEnvelope.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            // Get the response
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);
            
            Scanner scanner;
            if (responseCode >= 200 && responseCode < 300) {
                scanner = new Scanner(connection.getInputStream());
            } else {
                scanner = new Scanner(connection.getErrorStream());
            }
            
            StringBuilder response = new StringBuilder();
            while (scanner.hasNextLine()) {
                response.append(scanner.nextLine()).append("\n");
            }
            scanner.close();
            
            System.out.println("Response Body:");
            System.out.println(response.toString());
            
        } catch (Exception e) {
            System.out.println("Error testing SOAP request: " + e.getMessage());
            e.printStackTrace();
        }
    }
}