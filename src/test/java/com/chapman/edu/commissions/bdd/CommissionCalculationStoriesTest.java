package com.chapman.edu.commissions.bdd;

import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.junit.JUnit4StoryRunner;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.InstanceStepsFactory;
import org.junit.runner.RunWith;

import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;
import static org.jbehave.core.reporters.Format.*;

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * JBehave Story Runner
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * This class configures and runs BDD stories using JBehave.
 *
 * JBEHAVE CONFIGURATION:
 * - Story Loader: Finds .story files in classpath
 * - Step Factory: Maps steps to Java classes
 * - Reporters: Generates test reports (Console, HTML, etc.)
 *
 * RUNNING STORIES:
 * This test can be run like any JUnit test:
 * - From IDE: Right-click → Run Test
 * - From Maven: mvn test
 * - From CI/CD: Automated test execution
 *
 * NOTE: This runner executes ALL .story files in the package
 * ═══════════════════════════════════════════════════════════════════════════
 */
@RunWith(JUnit4StoryRunner.class)
public class CommissionCalculationStoriesTest extends org.jbehave.core.junit.JUnitStories {

    /**
     * Configure how JBehave should run stories.
     *

     * All BDD framework settings are explicit and version-controlled.
     */
    @Override
    public Configuration configuration() {
        return new MostUsefulConfiguration()
                // Load stories from classpath (src/test/resources/stories)
                .useStoryLoader(new LoadFromClasspath(this.getClass()))
                // Configure reporting (console output + HTML reports)
                .useStoryReporterBuilder(new StoryReporterBuilder()
                        .withCodeLocation(codeLocationFromClass(this.getClass()))
                        .withDefaultFormats()
                        .withFormats(CONSOLE, TXT, HTML, XML));
    }

    /**
     * Provide step definitions to JBehave.
     *
     * BDD PRINCIPLE: Step Mapping
     * This tells JBehave which Java classes contain step implementations.
     */
    @Override
    public InjectableStepsFactory stepsFactory() {
        return new InstanceStepsFactory(configuration(),
                new CommissionCalculationSteps());
    }

    /**
     * Specify which story files to run.
     *
     * This method tells JBehave to execute all .story files in the package.
     */
    @Override
    public java.util.List<String> storyPaths() {
        return java.util.Arrays.asList(
                "com/chapman/edu/commissions/bdd/commission_calculation.story",
                "com/chapman/edu/commissions/bdd/deal_validation.story",
                "com/chapman/edu/commissions/bdd/commission_plan.story"
        );
    }

    /**
     * ═══════════════════════════════════════════════════════════════════════
     * HOW TO RUN BDD STORIES
     * ═══════════════════════════════════════════════════════════════════════
     *
     * Method 1: IDE
     * Right-click this class → Run As → JUnit Test
     *
     * Method 2: Maven
     * mvn test -Dtest=CommissionCalculationStoriesTest
     *
     * Method 3: All BDD Tests
     * mvn test (runs all tests including BDD)
     *
     * ═══════════════════════════════════════════════════════════════════════
     * GENERATED REPORTS
     * ═══════════════════════════════════════════════════════════════════════
     *
     * After running, JBehave generates reports in:
     * target/jbehave/
     *
     * Reports include:
     * - Console output: Immediate feedback during execution
     * - HTML reports: Visual representation of scenario results
     * - XML reports: Machine-readable format for CI/CD integration
     *
     * HTML reports show:
     * - Which scenarios passed/failed
     * - Which steps passed/failed
     * - Actual vs expected values
     * - Execution time
     *
     * ═══════════════════════════════════════════════════════════════════════
     */
}