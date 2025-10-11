Narrative:
As a commission system administrator
I want to validate deals before processing commissions
So that only eligible deals receive commission payments

Scenario: Validate deal has required fields
Meta:
@category validation
@priority critical

Given a new deal for commission processing
When the deal is missing the sales representative ID
Then the validation should fail
And the error should indicate "Sales representative ID is required"

Scenario: Validate deal value is positive
Meta:
@category validation
@priority high

Given a deal with a value of $-5000
When I validate the deal for commission eligibility
Then the validation should fail
And the error should indicate "Deal value must be positive"

Scenario: Validate deal close date is within eligibility window
Meta:
@category validation
@priority high

Given a sales representative with ID "REP-006"
And a deal worth $80,000 that was closed 100 days ago
And the deal status is "WON"
When I check commission eligibility
Then the deal should be ineligible
And the reason should be "Deal closed more than 90 days ago"

Scenario: Boundary test for deal close date eligibility
Meta:
@category validation
@priority medium

Given a sales representative with ID "REP-007"
When I check deals closed at different times:
|daysAgo|expectedEligibility|
|30     |eligible           |
|90     |eligible           |
|91     |ineligible         |
|100    |ineligible         |
Then the eligibility should match expectations