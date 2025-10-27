package com.chapman.edu.commissions.api.graphql;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * GraphiQL Servlet - Serves the GraphiQL in-browser IDE.
 *
 * WHAT IS GRAPHIQL?
 * ----------------
 * GraphiQL is an in-browser IDE for exploring GraphQL APIs.
 * It provides:
 * - Interactive query editor with syntax highlighting
 * - Auto-completion based on schema introspection
 * - Documentation explorer
 * - Query history
 * - Variable editor
 *
 * WHY USE GRAPHIQL?
 * ----------------
 * - Test GraphQL queries without external tools
 * - Explore the schema interactively
 * - Understand available queries and mutations
 * - Debug API issues
 * - Learn GraphQL syntax
 *
 * IMPLEMENTATION:
 * --------------
 * This servlet serves a simple HTML page that loads GraphiQL from CDN.
 * The GraphiQL interface connects to the /graphql endpoint.
 */
public class GraphiQLServlet extends HttpServlet {

    private final String graphQLEndpoint;

    public GraphiQLServlet(String graphQLEndpoint) {
        this.graphQLEndpoint = graphQLEndpoint;
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
    <title>GraphiQL - Commission Calculator API</title>
    <style>
        body {
            margin: 0;
            padding: 0;
            height: 100vh;
            overflow: hidden;
        }
        #graphiql {
            height: 100vh;
        }
    </style>
    <link rel="stylesheet" href="https://unpkg.com/graphiql@3.0.10/graphiql.min.css" />
</head>
<body>
    <div id="graphiql">Loading GraphiQL...</div>

    <script crossorigin src="https://unpkg.com/react@18/umd/react.production.min.js"></script>
    <script crossorigin src="https://unpkg.com/react-dom@18/umd/react-dom.production.min.js"></script>
    <script src="https://unpkg.com/graphiql@3.0.10/graphiql.min.js"></script>

    <script>
        // GraphQL fetcher function
        const fetcher = GraphiQL.createFetcher({
            url: '%s',
        });

        // Default query to help users get started
        const defaultQuery = `# Welcome to Commission Calculator GraphQL API!
#
# Type queries in the editor on the left and press Ctrl+Enter or
# click the Play button to execute them. Results appear on the right.
#
# Try these example queries:

# Get all deals
query GetAllDeals {
  deals {
    id
    title
    value
    status
    salesRep {
      fullName
      email
    }
  }
}

# Get a specific user
query GetUser {
  user(id: "USER-001") {
    id
    firstName
    lastName
    fullName
    email
    role
    deals {
      id
      title
      value
    }
  }
}

# Get all commission plans
query GetCommissionPlans {
  commissionPlans {
    id
    name
    description
    status
  }
}

# Create a new deal (mutation)
mutation CreateDeal {
  createDeal(input: {
    title: "New Software Deal"
    customerId: "CUST-001"
    salesRepId: "USER-001"
    value: 50000
    status: PENDING
  }) {
    id
    title
    value
    status
  }
}

# Explore the schema using introspection
query IntrospectSchema {
  __schema {
    types {
      name
      description
    }
  }
}
`;

        // Render GraphiQL
        ReactDOM.render(
            React.createElement(GraphiQL, {
                fetcher: fetcher,
                defaultQuery: defaultQuery,
            }),
            document.getElementById('graphiql')
        );
    </script>
</body>
</html>
""".formatted(graphQLEndpoint);

        response.getWriter().write(html);
    }
}