Narrative:
As a sales manager
I want to calculate commissions for sales representatives
So that I can accurately compensate them based on their performance

Lifecycle:
Before:
Scope: STORY
Given the commission calculation system is initialized

Scenario: Calculate basic commission for a closed deal
Meta:
@category calculation
@priority high

Given a sales representative named "John Doe" with ID "REP-001"
And a deal worth $100,000 for product "Enterprise Software"
And the deal status is "WON"
When I calculate the commission at 10% rate
Then the commission amount should be $10,000.00

Scenario: Commission calculation requires closed deal status
Meta:
@category validation
@priority critical

Given a sales representative named "Jane Smith" with ID "REP-002"
And a deal worth $50,000 for product "SaaS License"
And the deal status is "OPEN"
When I attempt to calculate the commission
Then the system should reject the calculation
And the error message should indicate "Deal must be WON"

Scenario: Tiered commission rates based on deal size
Meta:
@category calculation
@priority high

Given a sales representative named "Bob Wilson" with ID "REP-003"
When I process the following deals:
|dealValue|expectedRate|expectedCommission|
|$30,000  |8%          |$2,400.00         |
|$75,000  |10%         |$7,500.00         |
|$150,000 |12%         |$18,000.00        |
Then all commissions should be calculated correctly

Scenario: Apply performance bonus to commission
Meta:
@category bonus
@priority medium

Given a sales representative named "Alice Johnson" with ID "REP-004"
And a deal worth $100,000 for product "Enterprise Solution"
And the deal status is "WON"
And the base commission rate is 10%
When I apply a performance bonus of 15%
Then the base commission should be $10,000.00
And the total commission with bonus should be $11,500.00

Scenario: Full commission pipeline with validation
Meta:
@category integration
@priority critical

Given a sales representative named "Charlie Brown" with ID "REP-005"
And a deal worth $120,000 for product "Premium Package"
And the deal status is "WON"
And the deal was closed 5 days ago
When I calculate the full commission with 10% bonus
Then the tiered commission should be $14,400.00
And the final commission with bonus should be $15,840.00
And the calculation should be tracked in the system