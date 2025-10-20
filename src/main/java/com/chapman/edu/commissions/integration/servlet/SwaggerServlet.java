package com.chapman.edu.commissions.integration.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * SwaggerServlet - Serves Swagger UI and OpenAPI documentation.
 *
 * This servlet provides:
 * - Swagger UI interface for interactive API testing
 * - OpenAPI 3.0 specification in JSON format
 * - API documentation accessible via browser
 *
 * Endpoints:
 * - /swagger-ui/ - Interactive Swagger UI
 * - /api-docs - OpenAPI JSON specification
 *
 * Demonstrates:
 * - API documentation as code
 * - Self-documenting REST APIs
 * - OpenAPI specification format
 */
public class SwaggerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestURI = request.getRequestURI();
        String pathInfo = request.getPathInfo();

        // Debug logging
        System.out.println("SwaggerServlet - RequestURI: " + requestURI + ", PathInfo: " + pathInfo);

        // Handle /api-docs endpoint
        if (requestURI.endsWith("/api-docs") || (pathInfo != null && pathInfo.equals("/api-docs"))) {
            serveOpenAPISpec(response);
            return;
        }

        // Handle /swagger-ui/ endpoint - serve the UI
        if (requestURI.contains("/swagger-ui") || pathInfo == null || pathInfo.equals("/") || pathInfo.isEmpty()) {
            serveSwaggerUI(response);
            return;
        }

        // Not found
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    /**
     * Serves the Swagger UI HTML page.
     * Uses CDN-hosted Swagger UI for simplicity.
     */
    private void serveSwaggerUI(HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("""
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <title>Commission Calculator API - Swagger UI</title>
                <link rel="stylesheet" href="https://unpkg.com/swagger-ui-dist@5.10.3/swagger-ui.css">
                <style>
                    body {
                        margin: 0;
                        padding: 0;
                        font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
                    }
                    .topbar { display: none; }
                </style>
            </head>
            <body>
                <div id="swagger-ui"></div>
                <script src="https://unpkg.com/swagger-ui-dist@5.10.3/swagger-ui-bundle.js"></script>
                <script src="https://unpkg.com/swagger-ui-dist@5.10.3/swagger-ui-standalone-preset.js"></script>
                <script>
                    window.onload = function() {
                        const ui = SwaggerUIBundle({
                            url: '/api-docs',
                            dom_id: '#swagger-ui',
                            deepLinking: true,
                            presets: [
                                SwaggerUIBundle.presets.apis,
                                SwaggerUIStandalonePreset
                            ],
                            plugins: [
                                SwaggerUIBundle.plugins.DownloadUrl
                            ],
                            layout: "StandaloneLayout"
                        });
                        window.ui = ui;
                    };
                </script>
            </body>
            </html>
        """);
    }

    /**
     * Serves the OpenAPI 3.0 specification in JSON format.
     * Documents all available API endpoints, request/response schemas, and authentication.
     */
    private void serveOpenAPISpec(HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // OpenAPI 3.0 specification for the Commission Calculator API
        out.println("""
            {
              "openapi": "3.0.0",
              "info": {
                "title": "Commission Calculator Integration API",
                "description": "RESTful API for managing sales deals and commissions using Layered MVC Architecture",
                "version": "1.0.0",
                "contact": {
                  "name": "Chapman University"
                }
              },
              "servers": [
                {
                  "url": "http://localhost:8080",
                  "description": "Local development server"
                }
              ],
              "security": [
                {
                  "basicAuth": []
                }
              ],
              "paths": {
                "/api/v1/integration/deals": {
                  "get": {
                    "summary": "Get all deals",
                    "description": "Retrieves all deals with optional filtering by status or sales rep",
                    "tags": ["Deals"],
                    "parameters": [
                      {
                        "name": "status",
                        "in": "query",
                        "description": "Filter by deal status",
                        "schema": {
                          "type": "string",
                          "enum": ["OPEN", "WON", "LOST", "CANCELLED"]
                        }
                      },
                      {
                        "name": "salesRepId",
                        "in": "query",
                        "description": "Filter by sales representative ID",
                        "schema": {
                          "type": "string"
                        }
                      }
                    ],
                    "responses": {
                      "200": {
                        "description": "List of deals",
                        "content": {
                          "application/json": {
                            "schema": {
                              "type": "array",
                              "items": {
                                "$ref": "#/components/schemas/Deal"
                              }
                            }
                          }
                        }
                      },
                      "401": {
                        "description": "Unauthorized - authentication required"
                      }
                    }
                  },
                  "post": {
                    "summary": "Create a new deal",
                    "description": "Creates a new deal with validation",
                    "tags": ["Deals"],
                    "requestBody": {
                      "required": true,
                      "content": {
                        "application/json": {
                          "schema": {
                            "$ref": "#/components/schemas/Deal"
                          }
                        }
                      }
                    },
                    "responses": {
                      "201": {
                        "description": "Deal created successfully",
                        "content": {
                          "application/json": {
                            "schema": {
                              "$ref": "#/components/schemas/Deal"
                            }
                          }
                        }
                      },
                      "400": {
                        "description": "Validation error"
                      },
                      "401": {
                        "description": "Unauthorized"
                      }
                    }
                  }
                },
                "/api/v1/integration/deals/{id}": {
                  "get": {
                    "summary": "Get deal by ID",
                    "description": "Retrieves a specific deal by its ID",
                    "tags": ["Deals"],
                    "parameters": [
                      {
                        "name": "id",
                        "in": "path",
                        "required": true,
                        "schema": {
                          "type": "string"
                        }
                      }
                    ],
                    "responses": {
                      "200": {
                        "description": "Deal found",
                        "content": {
                          "application/json": {
                            "schema": {
                              "$ref": "#/components/schemas/Deal"
                            }
                          }
                        }
                      },
                      "404": {
                        "description": "Deal not found"
                      }
                    }
                  },
                  "put": {
                    "summary": "Update deal",
                    "description": "Updates an existing deal",
                    "tags": ["Deals"],
                    "parameters": [
                      {
                        "name": "id",
                        "in": "path",
                        "required": true,
                        "schema": {
                          "type": "string"
                        }
                      }
                    ],
                    "requestBody": {
                      "required": true,
                      "content": {
                        "application/json": {
                          "schema": {
                            "$ref": "#/components/schemas/Deal"
                          }
                        }
                      }
                    },
                    "responses": {
                      "200": {
                        "description": "Deal updated successfully"
                      },
                      "400": {
                        "description": "Validation error"
                      },
                      "404": {
                        "description": "Deal not found"
                      }
                    }
                  },
                  "delete": {
                    "summary": "Delete deal",
                    "description": "Deletes a deal (only OPEN deals can be deleted)",
                    "tags": ["Deals"],
                    "parameters": [
                      {
                        "name": "id",
                        "in": "path",
                        "required": true,
                        "schema": {
                          "type": "string"
                        }
                      }
                    ],
                    "responses": {
                      "204": {
                        "description": "Deal deleted successfully"
                      },
                      "404": {
                        "description": "Deal not found"
                      },
                      "409": {
                        "description": "Cannot delete non-OPEN deals"
                      }
                    }
                  }
                },
                "/api/v1/integration/deals/{id}/close": {
                  "post": {
                    "summary": "Close deal as WON",
                    "description": "Closes an OPEN deal as WON",
                    "tags": ["Deals"],
                    "parameters": [
                      {
                        "name": "id",
                        "in": "path",
                        "required": true,
                        "schema": {
                          "type": "string"
                        }
                      }
                    ],
                    "responses": {
                      "200": {
                        "description": "Deal closed successfully"
                      },
                      "404": {
                        "description": "Deal not found"
                      },
                      "409": {
                        "description": "Deal cannot be closed"
                      }
                    }
                  }
                }
              },
              "components": {
                "securitySchemes": {
                  "basicAuth": {
                    "type": "http",
                    "scheme": "basic",
                    "description": "HTTP Basic Authentication using email and password"
                  }
                },
                "schemas": {
                  "Deal": {
                    "type": "object",
                    "required": ["title", "customerName", "salesRepId", "products"],
                    "properties": {
                      "id": {
                        "type": "string",
                        "description": "Unique deal identifier",
                        "example": "DEAL-123e4567-e89b-12d3-a456-426614174000"
                      },
                      "title": {
                        "type": "string",
                        "description": "Deal title",
                        "example": "Enterprise Software License"
                      },
                      "customerName": {
                        "type": "string",
                        "description": "Customer company name",
                        "example": "Acme Corporation"
                      },
                      "status": {
                        "type": "string",
                        "enum": ["OPEN", "WON", "LOST", "CANCELLED"],
                        "description": "Current deal status",
                        "example": "OPEN"
                      },
                      "salesRepId": {
                        "type": "string",
                        "description": "ID of the sales representative",
                        "example": "USER-123e4567-e89b-12d3-a456-426614174000"
                      },
                      "expectedCloseDate": {
                        "type": "string",
                        "format": "date",
                        "description": "Expected closing date",
                        "example": "2025-12-31"
                      },
                      "actualCloseDate": {
                        "type": "string",
                        "format": "date",
                        "description": "Actual closing date (for WON/LOST deals)",
                        "example": "2025-11-15"
                      },
                      "products": {
                        "type": "array",
                        "description": "List of products in the deal",
                        "items": {
                          "$ref": "#/components/schemas/DealProduct"
                        }
                      }
                    }
                  },
                  "DealProduct": {
                    "type": "object",
                    "properties": {
                      "name": {
                        "type": "string",
                        "example": "Software License"
                      },
                      "price": {
                        "type": "number",
                        "format": "decimal",
                        "example": 50000.00
                      },
                      "quantity": {
                        "type": "integer",
                        "example": 1
                      }
                    }
                  }
                }
              }
            }
        """);
    }
}