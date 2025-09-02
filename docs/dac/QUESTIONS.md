# Documentation as Code (DaC) Knowledge Test

## Multiple Choice Questions

1. What does "Documentation as Code" (DaC) mean?
   a) Writing documentation in programming languages
   b) Treating documentation with the same practices as source code
   c) Automatically generating all documentation from code comments
   d) Storing documentation in binary formats

2. Which of the following is NOT a core principle of Documentation as Code?
   a) Version control for documentation
   b) Documentation lives alongside code
   c) Documentation is written only by technical writers
   d) Documentation follows the same review process as code

3. What is the primary benefit of storing documentation in the same repository as code?
   a) Reduces repository size
   b) Keeps documentation synchronized with code changes
   c) Makes documentation harder to access
   d) Eliminates the need for documentation reviews

4. Which markup language is most commonly used for Documentation as Code?
   a) HTML
   b) XML
   c) Markdown
   d) LaTeX

5. What is a key advantage of using plain text formats for documentation?
   a) Better visual formatting
   b) Smaller file sizes
   c) Version control compatibility
   d) Requires specialized tools to edit

## Short Answer Questions

6. Explain how Documentation as Code helps maintain documentation quality over time.

7. Describe three specific tools or practices that support Documentation as Code workflows.

8. What are the benefits of treating documentation changes the same way as code changes (pull requests, reviews, etc.)?

9. How does Documentation as Code support collaborative development in distributed teams?

10. What challenges might a team face when adopting Documentation as Code, and how can they be addressed?

## Practical Application Questions

11. Your team has been maintaining API documentation in a separate wiki that frequently becomes outdated. How would you implement Documentation as Code to solve this problem?

12. Describe how you would structure documentation in a repository that contains multiple microservices, ensuring each service's documentation stays current with its implementation.

13. A developer makes a breaking change to an API but forgets to update the documentation. How would Documentation as Code practices help prevent this issue?

14. Your organization wants to generate multiple output formats (HTML, PDF, mobile-friendly) from the same documentation source. How would Documentation as Code support this requirement?

15. How would you implement automated testing for documentation to ensure it remains accurate and up-to-date?

## Code Analysis Questions

16. Review this repository structure and identify what makes it a good example of Documentation as Code:
```
project/
├── src/
│   ├── main/java/
│   └── test/java/
├── docs/
│   ├── api/
│   │   ├── authentication.md
│   │   └── endpoints.md
│   ├── deployment/
│   │   └── setup.md
│   └── README.md
├── README.md
├── CONTRIBUTING.md
└── CHANGELOG.md
```

17. Examine this documentation workflow and explain how it implements Documentation as Code principles:
```yaml
name: Documentation CI
on:
  pull_request:
    paths:
      - 'docs/**'
      - 'src/**'
jobs:
  validate-docs:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Check links
        run: markdown-link-check docs/**/*.md
      - name: Validate code examples
        run: ./scripts/validate-code-examples.sh
      - name: Generate docs
        run: mkdocs build --strict
```

18. A team is struggling with keeping their OpenAPI specification synchronized with their actual API implementation. How would you design a Documentation as Code solution to address this challenge?