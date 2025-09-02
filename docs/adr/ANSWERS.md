# Architectural Decision Records (ADR) Knowledge Test - Answers

## Multiple Choice Questions

1. What does ADR stand for in software development?
   **Answer: b) Architectural Decision Records**

   ADR stands for Architectural Decision Records, which are documents that capture important architectural decisions made along with their context and consequences.

2. What is the primary purpose of an Architectural Decision Record?
   **Answer: c) To document important architectural decisions and their context**

   ADRs serve as a record of significant architectural decisions, capturing the context, the decision itself, and the expected consequences, which helps teams understand why certain choices were made.

3. Which of the following is NOT typically a section in an ADR?
   **Answer: d) Budget constraints**

   While budget might influence a decision (and could be mentioned in the context section), it's not a standard section in an ADR. Standard sections typically include Title, Status, Context, Decision, and Consequences.

4. What is the typical status of a newly created ADR?
   **Answer: b) Proposed**

   New ADRs typically start with a "Proposed" status, indicating that the decision is being considered but hasn't been officially accepted yet.

5. When should an ADR be created?
   **Answer: c) When making significant architectural decisions that impact the system**

   ADRs should be created when making important architectural decisions that will have a significant impact on the system, not just at specific project phases.

## Short Answer Questions

6. Explain the difference between "Deprecated" and "Superseded" statuses in an ADR.

   **Answer:** The "Deprecated" status indicates that a decision is no longer recommended or applicable, but hasn't necessarily been replaced by another decision. It suggests that the team should avoid following this decision in the future, but doesn't point to an alternative.

   The "Superseded" status indicates that a decision has been explicitly replaced by a newer decision. It not only indicates that the original decision should no longer be followed, but also points to the new ADR that replaces it. This creates a clear chain of decision evolution.

7. Describe how ADRs contribute to knowledge sharing within a development team.

   **Answer:** ADRs contribute to knowledge sharing in several important ways:

   - **Capturing rationale:** They document not just what was decided, but why, preserving the reasoning that might otherwise be lost.
   - **Onboarding tool:** New team members can quickly understand key architectural decisions without needing extensive verbal explanations.
   - **Reducing knowledge silos:** Knowledge isn't limited to the original decision-makers but is accessible to the entire team.
   - **Historical context:** They provide historical context for decisions, helping team members understand the evolution of the architecture.
   - **Asynchronous communication:** Team members can learn about decisions even if they weren't present when they were made.
   - **Standardized format:** The consistent format makes it easier to absorb and compare different decisions.
   - **Decision transparency:** They make the decision-making process more transparent, which builds trust within the team.

8. What information should be included in the "Context" section of an ADR?

   **Answer:** The "Context" section of an ADR should include:

   - **Problem statement:** A clear description of the issue or opportunity being addressed
   - **Technical constraints:** Any technical limitations or requirements that influenced the decision
   - **Business drivers:** Business goals, requirements, or constraints that impacted the decision
   - **Current state:** Description of the existing system or architecture relevant to the decision
   - **External factors:** Market trends, regulatory requirements, or other external influences
   - **Stakeholder concerns:** Key concerns or requirements from different stakeholders
   - **Options considered:** A brief mention of the alternatives that were evaluated (detailed analysis may be in the Decision section)
   - **Decision criteria:** The factors or metrics used to evaluate different options

   The Context section should provide enough background information that someone unfamiliar with the situation can understand why a decision needed to be made and what factors influenced it.

9. How do ADRs help with onboarding new team members to a project?

   **Answer:** ADRs significantly facilitate the onboarding of new team members in several ways:

   - **Architectural overview:** They provide a map of key architectural decisions, giving new members a quick understanding of the system's design.
   - **Decision rationale:** New members learn not just how the system is built, but why certain approaches were chosen over others.
   - **Historical context:** They understand the evolution of the architecture and why things might be implemented in certain ways.
   - **Reduced questioning:** New members can find answers to many "why" questions in ADRs without having to interrupt experienced team members.
   - **Independent learning:** ADRs enable self-directed learning about the project's architecture.
   - **Avoiding repeated explanations:** Senior team members don't need to repeatedly explain the same architectural decisions.
   - **Preventing misconceptions:** Clear documentation prevents new members from making incorrect assumptions about the architecture.
   - **Faster productivity:** Understanding the architectural decisions helps new members contribute meaningful work more quickly.

10. Explain how ADRs can be used to track the evolution of a system's architecture over time.

    **Answer:** ADRs serve as powerful tools for tracking architectural evolution:

    - **Chronological record:** By dating ADRs, they create a timeline of architectural decisions.
    - **Status changes:** As ADRs move from "Proposed" to "Accepted" to potentially "Deprecated" or "Superseded," they document the lifecycle of decisions.
    - **Decision chains:** "Superseded" ADRs link to their replacements, creating chains that show how specific aspects of the architecture evolved.
    - **Contextual changes:** By documenting the context at different points in time, ADRs show how changing requirements or constraints led to architectural shifts.
    - **Technical debt tracking:** ADRs can document known limitations or trade-offs that might need to be addressed in the future.
    - **Retrospective analysis:** Teams can review past ADRs to understand patterns in their decision-making and improve future processes.
    - **Architectural drift detection:** By comparing current implementation with documented decisions, teams can identify where the system has drifted from the intended architecture.
    - **Knowledge preservation:** As team members come and go, ADRs preserve the reasoning behind architectural evolution that might otherwise be lost.

## Practical Application Questions

11. Review the following ADR and identify any missing or incomplete sections:
```markdown
# Using Java Instead of Python

## Decision
We will implement our backend services using Java instead of Python.

## Consequences
- Better performance
- Stronger type safety
```

**Answer:** This ADR is missing several important sections and has incomplete information in the existing sections:

**Missing sections:**
1. **Status:** There's no indication of whether this decision is proposed, accepted, etc.
2. **Context:** There's no explanation of why this decision is being considered, what problem it's solving, or what alternatives were evaluated.

**Incomplete sections:**
1. **Title:** While present, the title could be more specific, e.g., "Adopting Java for Backend Services Instead of Python"
2. **Decision:** The decision is very brief and lacks details about the specific approach to Java implementation, frameworks to be used, version considerations, etc.
3. **Consequences:** The consequences section lists only two points and is not comprehensive. It should include more detailed positive and negative consequences, and potentially mitigation strategies for the negative ones.

A more complete ADR would include:
- A clear status
- A detailed context explaining the current Python implementation challenges
- A more specific decision with implementation details about Java frameworks and tools
- A comprehensive list of consequences with mitigations for negative impacts
- Potentially references to related decisions or resources

12. Write a brief ADR for deciding to use a NoSQL database (MongoDB) instead of a relational database for a user profile service. Include all standard sections.

**Answer:**

```markdown
# Title: Using MongoDB for User Profile Service

## Status
Accepted

## Context
Our user profile service needs to store varied user profile data with flexible schema requirements. Users can have different sets of profile attributes based on their account type and preferences. The current relational database approach requires frequent schema migrations as we add new profile features.

We considered the following options:
1. Continue with our current relational database (PostgreSQL)
2. Implement a document-based NoSQL solution (MongoDB)
3. Use a hybrid approach with a relational DB for core data and a separate store for variable attributes

Key factors influencing this decision include:
- Need for schema flexibility
- Query performance for profile data retrieval
- Development velocity
- Operational complexity

## Decision
We will implement MongoDB as the primary data store for the user profile service. This includes:
- Migrating existing user profile data from PostgreSQL to MongoDB
- Implementing a document-based data model that accommodates variable user attributes
- Using MongoDB's indexing capabilities for query optimization
- Implementing data validation at the application level

## Consequences

### Positive
- Increased schema flexibility allowing for rapid iteration of user profile features
- Simplified data model that better represents the variable nature of user profiles
- Reduced need for migrations when adding new profile attributes
- Better performance for read-heavy operations typical in profile service
- JSON-native data structure aligns well with our API response format

### Negative
- Reduced data consistency guarantees compared to a relational database
- Team will need to learn MongoDB best practices and query patterns
- Some complex queries may be more difficult to express
- Need for application-level validation to ensure data integrity

### Mitigations
- Implement comprehensive data validation in the application layer
- Create a data migration strategy with validation steps
- Provide MongoDB training for the development team
- Start with a small subset of profile data before full migration
- Implement monitoring to track MongoDB performance and resource usage
```
