package com.chapman.edu.commissions.api.graphql;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * GraphQL Index Servlet - Landing page for GraphQL development tools.
 *
 * WHAT IS THIS?
 * ------------
 * This servlet provides a central landing page that links to all
 * available GraphQL development tools:
 * - GraphiQL: Interactive query editor
 * - GraphQL Playground: Advanced IDE with multi-tab support
 *
 * WHY A LANDING PAGE?
 * ------------------
 * - Single entry point for developers
 * - Overview of available tools
 * - Quick access to documentation
 * - Tool comparison and recommendations
 */
public class GraphQLIndexServlet extends HttpServlet {

    private final int port;

    public GraphQLIndexServlet(int port) {
        this.port = port;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        String html = """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Commission Calculator - GraphQL API Tools</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
            background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
            min-height: 100vh;
            padding: 40px 20px;
        }

        .container {
            max-width: 1200px;
            margin: 0 auto;
        }

        h1 {
            color: white;
            text-align: center;
            margin-bottom: 10px;
            font-size: 2.5em;
            text-shadow: 0 2px 4px rgba(0,0,0,0.2);
        }

        .subtitle {
            color: rgba(255,255,255,0.9);
            text-align: center;
            margin-bottom: 40px;
            font-size: 1.2em;
        }

        .tools-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 30px;
            margin-bottom: 40px;
        }

        .tool-card {
            background: white;
            border-radius: 12px;
            padding: 30px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.2);
            transition: transform 0.3s ease, box-shadow 0.3s ease;
        }

        .tool-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 15px 40px rgba(0,0,0,0.3);
        }

        .tool-card h2 {
            color: #667eea;
            margin-bottom: 15px;
            font-size: 1.5em;
        }

        .tool-card p {
            color: #666;
            line-height: 1.6;
            margin-bottom: 20px;
        }

        .tool-card ul {
            list-style: none;
            margin-bottom: 20px;
        }

        .tool-card li {
            color: #666;
            padding: 5px 0;
            padding-left: 20px;
            position: relative;
        }

        .tool-card li:before {
            content: "✓";
            position: absolute;
            left: 0;
            color: #667eea;
            font-weight: bold;
        }

        .btn {
            display: inline-block;
            padding: 12px 30px;
            background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
            color: white;
            text-decoration: none;
            border-radius: 6px;
            font-weight: 600;
            transition: opacity 0.3s ease;
            text-align: center;
        }

        .btn:hover {
            opacity: 0.9;
        }

        .endpoint-info {
            background: white;
            border-radius: 12px;
            padding: 30px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.2);
        }

        .endpoint-info h2 {
            color: #667eea;
            margin-bottom: 20px;
        }

        .endpoint {
            background: #f5f5f5;
            padding: 15px;
            border-radius: 6px;
            margin-bottom: 15px;
            font-family: 'Courier New', monospace;
            color: #333;
        }

        .endpoint strong {
            color: #667eea;
        }

        .example {
            background: #f5f5f5;
            padding: 15px;
            border-radius: 6px;
            margin-top: 15px;
            font-family: 'Courier New', monospace;
            font-size: 0.9em;
            overflow-x: auto;
        }

        .example pre {
            margin: 0;
            white-space: pre-wrap;
            word-wrap: break-word;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Commission Calculator</h1>
        <p class="subtitle">GraphQL API Development Tools</p>

        <div class="tools-grid">
            <div class="tool-card">
                <h2>GraphiQL</h2>
                <p>The official GraphQL IDE for exploring and testing queries.</p>
                <ul>
                    <li>Syntax highlighting</li>
                    <li>Auto-completion</li>
                    <li>Documentation explorer</li>
                    <li>Query history</li>
                </ul>
                <a href="/graphiql" class="btn">Open GraphiQL</a>
            </div>

            <div class="tool-card">
                <h2>GraphQL Playground</h2>
                <p>An advanced GraphQL IDE with powerful features for complex workflows.</p>
                <ul>
                    <li>Multi-tab support</li>
                    <li>Customizable themes</li>
                    <li>Advanced schema viewer</li>
                    <li>Tracing integration</li>
                </ul>
                <a href="/playground" class="btn">Open Playground</a>
            </div>
        </div>

        <div class="endpoint-info">
            <h2>API Endpoint Information</h2>

            <div class="endpoint">
                <strong>GraphQL Endpoint:</strong> http://localhost:%d/graphql
            </div>

            <div class="endpoint">
                <strong>Schema Endpoint (SDL):</strong> http://localhost:%d/schema
            </div>

            <p style="margin-top: 20px; color: #666;">
                <strong>Example Query (POST):</strong>
            </p>
            <div class="example">
                <pre>POST http://localhost:%d/graphql
Content-Type: application/json

{
  "query": "{ deals { id title value } }"
}</pre>
            </div>

            <p style="margin-top: 20px; color: #666;">
                <strong>Example Query (GET):</strong>
            </p>
            <div class="example">
                <pre>GET http://localhost:%d/graphql?query={deals{id title value}}</pre>
            </div>

            <p style="margin-top: 20px; color: #666;">
                <strong>Schema Introspection:</strong>
            </p>
            <div class="example">
                <pre>POST http://localhost:%d/graphql
Content-Type: application/json

{
  "query": "{ __schema { types { name } } }"
}</pre>
            </div>
        </div>
    </div>
</body>
</html>
""".formatted(port, port, port, port, port);

        response.getWriter().write(html);
    }
}