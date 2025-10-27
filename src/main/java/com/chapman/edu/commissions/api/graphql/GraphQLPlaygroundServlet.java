package com.chapman.edu.commissions.api.graphql;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * GraphQL Playground Servlet - Serves the GraphQL Playground IDE.
 *
 * WHAT IS GRAPHQL PLAYGROUND?
 * --------------------------
 * GraphQL Playground is a powerful GraphQL IDE with advanced features:
 * - Multiple tabs for different queries
 * - Schema documentation viewer
 * - Query history
 * - Customizable themes
 * - Subscription support
 * - Tracing integration
 *
 * GRAPHQL PLAYGROUND VS GRAPHIQL:
 * ------------------------------
 * GraphiQL:
 * - Simpler, lightweight interface
 * - Official GraphQL Foundation project
 * - Good for quick testing
 *
 * Playground:
 * - More feature-rich
 * - Better for complex workflows
 * - Multi-tab support
 * - Better visualization
 *
 * IMPLEMENTATION:
 * --------------
 * This servlet serves a simple HTML page that loads GraphQL Playground from CDN.
 * The Playground interface connects to the /graphql endpoint.
 */
public class GraphQLPlaygroundServlet extends HttpServlet {

    private final String graphQLEndpoint;

    public GraphQLPlaygroundServlet(String graphQLEndpoint) {
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
    <title>GraphQL Playground - Commission Calculator API</title>
    <style>
        body {
            margin: 0;
            padding: 0;
            height: 100vh;
            overflow: hidden;
        }
        #root {
            height: 100vh;
        }
    </style>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/graphql-playground-react@1.7.28/build/static/css/index.css" />
    <script src="https://cdn.jsdelivr.net/npm/graphql-playground-react@1.7.28/build/static/js/middleware.js"></script>
</head>
<body>
    <div id="root">Loading GraphQL Playground...</div>

    <script>
        window.addEventListener('load', function (event) {
            GraphQLPlayground.init(document.getElementById('root'), {
                endpoint: '%s',
                settings: {
                    'editor.theme': 'light',
                    'editor.cursorShape': 'line',
                    'editor.reuseHeaders': true,
                    'tracing.hideTracingResponse': false,
                    'queryPlan.hideQueryPlanResponse': false,
                    'editor.fontSize': 14,
                    'editor.fontFamily': '"Source Code Pro", "Consolas", "Inconsolata", "Droid Sans Mono", "Monaco", monospace',
                    'request.credentials': 'omit'
                },
                tabs: [
                    {
                        endpoint: '%s',
                        query: `# Welcome to Commission Calculator GraphQL Playground!
#
# Playground provides a powerful environment for exploring GraphQL APIs
# with features like multi-tab support, schema exploration, and more.
#
# Try these example queries:

# Get all deals with their sales representatives
query GetDealsWithReps {
  deals {
    id
    title
    value
    status
    salesRep {
      id
      fullName
      email
      role
    }
    products {
      productId
      productName
      quantity
      unitPrice
      totalPrice
    }
  }
}

# Get deals by status
query GetDealsByStatus {
  dealsByStatus(status: PENDING) {
    id
    title
    value
    status
    createdDate
  }
}

# Get user with their deals
query GetUserWithDeals {
  user(id: "USER-001") {
    id
    fullName
    email
    role
    deals {
      id
      title
      value
      status
    }
  }
}

# Get active commission plans on a specific date
query GetActiveCommissionPlans {
  activeCommissionPlansOnDate(date: "2024-01-15") {
    id
    name
    description
    effectiveDate
    expirationDate
  }
}

# Create a new user (mutation)
mutation CreateUser {
  createUser(input: {
    firstName: "John"
    lastName: "Doe"
    username: "jdoe"
    email: "john.doe@example.com"
    role: SALES_REP
  }) {
    id
    fullName
    email
    username
    role
  }
}

# Update a deal (mutation)
mutation UpdateDeal {
  updateDeal(
    id: "DEAL-001"
    input: {
      status: WON
      value: 75000
    }
  ) {
    id
    title
    value
    status
    lastModifiedDate
  }
}
`
                    }
                ]
            });
        });
    </script>
</body>
</html>
""".formatted(graphQLEndpoint, graphQLEndpoint);

        response.getWriter().write(html);
    }
}