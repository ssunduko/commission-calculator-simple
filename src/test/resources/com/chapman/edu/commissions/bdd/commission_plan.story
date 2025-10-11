Narrative:
As a sales operations manager
I want to manage commission plans
So that I can apply appropriate commission structures to different scenarios

Scenario: Create a new commission plan
Meta:
@category plan_management
@priority high

Given I want to create a commission plan named "Q4 2024 Plan"
And the plan currency is "USD"
And the plan effective start date is "2024-10-01"
When I create the commission plan
Then the plan status should be "DRAFT"
And the plan should be stored in the system

Scenario: Activate a commission plan
Meta:
@category plan_management
@priority high

Given a commission plan named "Standard Plan" in "DRAFT" status
And the plan has at least one commission rule defined
When I activate the commission plan
Then the plan status should change to "ACTIVE"
And the last modified date should be updated

Scenario: Check plan applicability for a specific date
Meta:
@category plan_application
@priority medium

Given a commission plan named "Holiday Plan"
And the plan is "ACTIVE"
And the plan effective start date is "2024-12-01"
And the plan effective end date is "2024-12-31"
When I check if the plan applies on "2024-12-15"
Then the plan should be applicable
When I check if the plan applies on "2025-01-15"
Then the plan should not be applicable

Scenario: Add commission tiers to a plan
Meta:
@category plan_configuration
@priority high

Given a commission plan named "Tiered Plan"
When I add the following commission tiers:
|minValue|maxValue|rate|
|0       |50000   |8%  |
|50001   |100000  |10% |
|100001  |null    |12% |
Then the plan should have 3 tiers
And tier calculations should use the appropriate rates