package com.chapman.edu.commissions.patterns.behavioral.visitor;

import com.chapman.edu.commissions.model.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.chapman.edu.commissions.patterns.behavioral.visitor.VisitorStructure.*;

/**
 * VISITOR PATTERN - COMMISSION SYSTEM IMPLEMENTATION
 *
 * REAL-WORLD APPLICATION:
 * This implementation demonstrates using the Visitor pattern to perform various operations
 * on commission-related objects (Deals, CommissionPlans, Users, Disputes) without modifying
 * those domain model classes.
 *
 * BUSINESS CONTEXT:
 * In a commission system, you need to perform many different operations on the same
 * domain objects: reporting, validation, export, calculation, analysis, etc.
 * Adding all these operations directly to the domain models would:
 * - Violate Single Responsibility Principle
 * - Make domain models bloated and hard to maintain
 * - Mix business logic with reporting/analysis/export concerns
 *
 * The Visitor pattern solves this by:
 * - Keeping domain models clean and focused on business logic
 * - Grouping related operations in visitor classes
 * - Making it easy to add new operations without modifying domain models
 *
 * BENEFITS IN THIS CONTEXT:
 * 1. Domain models (Deal, CommissionPlan, User, Dispute) remain unchanged
 * 2. Can add new operations (visitors) without touching existing code
 * 3. Related operations are grouped together (e.g., all reporting in ReportVisitor)
 * 4. Operations can maintain state across multiple visits
 * 5. Easy to create complex multi-object reports and analyses
 *
 */
public class VisitorImplementation {

    /**
     * COMMISSION DEAL - Visitable Element
     *
     * Wrapper around the Deal model that implements the visitor pattern.
     * Note: We're wrapping instead of modifying the original domain model.
     */
    public static class CommissionDeal implements CommissionEntity {
        private final Deal deal;

        public CommissionDeal(Deal deal) {
            this.deal = deal;
        }

        @Override
        public void accept(CommissionEntityVisitor visitor) {
            visitor.visitDeal(this);
        }

        @Override
        public String getId() {
            return deal.getId();
        }

        @Override
        public String getEntityType() {
            return "Deal";
        }

        // Expose deal properties
        public Deal getDeal() {
            return deal;
        }

        public String getTitle() {
            return deal.getTitle();
        }

        public BigDecimal getValue() {
            return deal.getValue();
        }

        public DealStatus getStatus() {
            return deal.getStatus();
        }

        public String getSalesRepId() {
            return deal.getSalesRepId();
        }

        public LocalDate getCloseDate() {
            return deal.getCloseDate();
        }
    }

    /**
     * COMMISSION PLAN ENTITY - Visitable Element
     *
     * Wrapper around CommissionPlan for visitor pattern.
     */
    public static class CommissionPlanEntity implements CommissionEntity {
        private final CommissionPlan plan;

        public CommissionPlanEntity(CommissionPlan plan) {
            this.plan = plan;
        }

        @Override
        public void accept(CommissionEntityVisitor visitor) {
            visitor.visitCommissionPlan(this);
        }

        @Override
        public String getId() {
            return plan.getId();
        }

        @Override
        public String getEntityType() {
            return "CommissionPlan";
        }

        public CommissionPlan getPlan() {
            return plan;
        }

        public String getName() {
            return plan.getName();
        }

        public PlanStatus getStatus() {
            return plan.getStatus();
        }

        public LocalDate getEffectiveStartDate() {
            return plan.getEffectiveStartDate();
        }

        public LocalDate getEffectiveEndDate() {
            return plan.getEffectiveEndDate();
        }

        public int getRuleCount() {
            return plan.getRules() != null ? plan.getRules().size() : 0;
        }

        public int getTierCount() {
            return plan.getTiers() != null ? plan.getTiers().size() : 0;
        }
    }

    /**
     * USER ENTITY - Visitable Element
     *
     * Wrapper around User for visitor pattern.
     */
    public static class UserEntity implements CommissionEntity {
        private final User user;

        public UserEntity(User user) {
            this.user = user;
        }

        @Override
        public void accept(CommissionEntityVisitor visitor) {
            visitor.visitUser(this);
        }

        @Override
        public String getId() {
            return user.getId();
        }

        @Override
        public String getEntityType() {
            return "User";
        }

        public User getUser() {
            return user;
        }

        public String getName() {
            return user.getFirstName() + " " + user.getLastName();
        }

        public String getEmail() {
            return user.getEmail();
        }

        public Set<UserRole> getRoles() {
            return user.getRoles();
        }
    }

    /**
     * DISPUTE ENTITY - Visitable Element
     *
     * Wrapper around Dispute for visitor pattern.
     */
    public static class DisputeEntity implements CommissionEntity {
        private final Dispute dispute;

        public DisputeEntity(Dispute dispute) {
            this.dispute = dispute;
        }

        @Override
        public void accept(CommissionEntityVisitor visitor) {
            visitor.visitDispute(this);
        }

        @Override
        public String getId() {
            return dispute.getId();
        }

        @Override
        public String getEntityType() {
            return "Dispute";
        }

        public Dispute getDispute() {
            return dispute;
        }

        public String getCalculationId() {
            return dispute.getCalculationId();
        }

        public String getSalesRepId() {
            return dispute.getSalesRepId();
        }

        public DisputeStatus getStatus() {
            return dispute.getStatus();
        }

        public String getTitle() {
            return dispute.getTitle();
        }

        public LocalDateTime getCreatedDate() {
            return dispute.getCreatedDate();
        }
    }

    /**
     * REPORT VISITOR
     *
     * Generates comprehensive reports for commission entities.
     * Maintains state (report content) across multiple visits.
     *
     * USE CASE: Generate management reports showing all system entities.
     */
    public static class ReportVisitor implements CommissionEntityVisitor {
        private final StringBuilder report;
        private int dealCount = 0;
        private int planCount = 0;
        private int userCount = 0;
        private int disputeCount = 0;
        private BigDecimal totalDealValue = BigDecimal.ZERO;

        public ReportVisitor() {
            this.report = new StringBuilder();
            report.append("╔═══════════════════════════════════════════════════════════╗\n");
            report.append("║          COMMISSION SYSTEM COMPREHENSIVE REPORT           ║\n");
            report.append("╚═══════════════════════════════════════════════════════════╝\n\n");
        }

        @Override
        public void visitDeal(CommissionDeal deal) {
            dealCount++;
            if (deal.getValue() != null) {
                totalDealValue = totalDealValue.add(deal.getValue());
            }

            report.append("📊 DEAL: ").append(deal.getTitle()).append("\n");
            report.append("   ID: ").append(deal.getId()).append("\n");
            report.append("   Value: $").append(deal.getValue()).append("\n");
            report.append("   Status: ").append(deal.getStatus()).append("\n");
            report.append("   Sales Rep: ").append(deal.getSalesRepId()).append("\n");
            if (deal.getCloseDate() != null) {
                report.append("   Close Date: ").append(deal.getCloseDate()).append("\n");
            }
            report.append("\n");
        }

        @Override
        public void visitCommissionPlan(CommissionPlanEntity plan) {
            planCount++;

            report.append("📋 COMMISSION PLAN: ").append(plan.getName()).append("\n");
            report.append("   ID: ").append(plan.getId()).append("\n");
            report.append("   Status: ").append(plan.getStatus()).append("\n");
            report.append("   Rules: ").append(plan.getRuleCount()).append("\n");
            report.append("   Tiers: ").append(plan.getTierCount()).append("\n");
            if (plan.getEffectiveStartDate() != null) {
                report.append("   Effective: ").append(plan.getEffectiveStartDate());
                if (plan.getEffectiveEndDate() != null) {
                    report.append(" to ").append(plan.getEffectiveEndDate());
                }
                report.append("\n");
            }
            report.append("\n");
        }

        @Override
        public void visitUser(UserEntity user) {
            userCount++;

            report.append("👤 USER: ").append(user.getName()).append("\n");
            report.append("   ID: ").append(user.getId()).append("\n");
            report.append("   Email: ").append(user.getEmail()).append("\n");
            report.append("   Roles: ").append(user.getRoles()).append("\n");
            report.append("\n");
        }

        @Override
        public void visitDispute(DisputeEntity dispute) {
            disputeCount++;

            report.append("⚠️  DISPUTE: ").append(dispute.getTitle() != null ? dispute.getTitle() : dispute.getId()).append("\n");
            report.append("   ID: ").append(dispute.getId()).append("\n");
            report.append("   Calculation ID: ").append(dispute.getCalculationId()).append("\n");
            report.append("   Sales Rep: ").append(dispute.getSalesRepId()).append("\n");
            report.append("   Status: ").append(dispute.getStatus()).append("\n");
            report.append("   Created: ").append(dispute.getCreatedDate()).append("\n");
            report.append("\n");
        }

        public String getReport() {
            // Add summary
            StringBuilder finalReport = new StringBuilder(report);
            finalReport.append("─".repeat(60)).append("\n");
            finalReport.append("SUMMARY:\n");
            finalReport.append("  Total Deals: ").append(dealCount).append("\n");
            finalReport.append("  Total Deal Value: $").append(totalDealValue).append("\n");
            finalReport.append("  Commission Plans: ").append(planCount).append("\n");
            finalReport.append("  Users: ").append(userCount).append("\n");
            finalReport.append("  Disputes: ").append(disputeCount).append("\n");
            finalReport.append("═".repeat(60)).append("\n");

            return finalReport.toString();
        }
    }

    /**
     * VALIDATION VISITOR
     *
     * Validates commission entities according to business rules.
     * Accumulates validation errors across multiple visits.
     *
     * USE CASE: Pre-processing validation before commission calculations or reports.
     */
    public static class ValidationVisitor implements CommissionEntityVisitor {
        private final List<String> errors;
        private int validEntityCount = 0;

        public ValidationVisitor() {
            this.errors = new ArrayList<>();
        }

        @Override
        public void visitDeal(CommissionDeal deal) {
            boolean isValid = true;

            // Validate deal value
            if (deal.getValue() == null || deal.getValue().compareTo(BigDecimal.ZERO) <= 0) {
                errors.add("Deal " + deal.getId() + ": Invalid value (must be positive)");
                isValid = false;
            }

            // Validate title
            if (deal.getTitle() == null || deal.getTitle().trim().isEmpty()) {
                errors.add("Deal " + deal.getId() + ": Missing title");
                isValid = false;
            }

            // Validate sales rep
            if (deal.getSalesRepId() == null || deal.getSalesRepId().trim().isEmpty()) {
                errors.add("Deal " + deal.getId() + ": Missing sales representative");
                isValid = false;
            }

            // Validate won deals have close date
            if (deal.getStatus() == DealStatus.WON && deal.getCloseDate() == null) {
                errors.add("Deal " + deal.getId() + ": Won deal must have close date");
                isValid = false;
            }

            if (isValid) {
                validEntityCount++;
            }
        }

        @Override
        public void visitCommissionPlan(CommissionPlanEntity plan) {
            boolean isValid = true;

            // Validate name
            if (plan.getName() == null || plan.getName().trim().isEmpty()) {
                errors.add("Plan " + plan.getId() + ": Missing name");
                isValid = false;
            }

            // Validate active plans have dates
            if (plan.getStatus() == PlanStatus.ACTIVE) {
                if (plan.getEffectiveStartDate() == null) {
                    errors.add("Plan " + plan.getId() + ": Active plan must have start date");
                    isValid = false;
                }
            }

            // Validate has rules or tiers
            if (plan.getRuleCount() == 0 && plan.getTierCount() == 0) {
                errors.add("Plan " + plan.getId() + ": Must have at least one rule or tier");
                isValid = false;
            }

            if (isValid) {
                validEntityCount++;
            }
        }

        @Override
        public void visitUser(UserEntity user) {
            boolean isValid = true;

            // Validate email
            if (user.getEmail() == null || !user.getEmail().contains("@")) {
                errors.add("User " + user.getId() + ": Invalid email");
                isValid = false;
            }

            // Validate name
            if (user.getName() == null || user.getName().trim().isEmpty()) {
                errors.add("User " + user.getId() + ": Missing name");
                isValid = false;
            }

            // Validate roles
            if (user.getRoles() == null || user.getRoles().isEmpty()) {
                errors.add("User " + user.getId() + ": Missing role");
                isValid = false;
            }

            if (isValid) {
                validEntityCount++;
            }
        }

        @Override
        public void visitDispute(DisputeEntity dispute) {
            boolean isValid = true;

            // Validate calculation reference
            if (dispute.getCalculationId() == null || dispute.getCalculationId().trim().isEmpty()) {
                errors.add("Dispute " + dispute.getId() + ": Missing calculation reference");
                isValid = false;
            }

            // Validate sales rep
            if (dispute.getSalesRepId() == null || dispute.getSalesRepId().trim().isEmpty()) {
                errors.add("Dispute " + dispute.getId() + ": Missing sales rep");
                isValid = false;
            }

            // Validate title
            if (dispute.getTitle() == null || dispute.getTitle().trim().isEmpty()) {
                errors.add("Dispute " + dispute.getId() + ": Missing title");
                isValid = false;
            }

            // Validate created date
            if (dispute.getCreatedDate() == null) {
                errors.add("Dispute " + dispute.getId() + ": Missing created date");
                isValid = false;
            }

            if (isValid) {
                validEntityCount++;
            }
        }

        public boolean isValid() {
            return errors.isEmpty();
        }

        public List<String> getErrors() {
            return new ArrayList<>(errors);
        }

        public int getValidEntityCount() {
            return validEntityCount;
        }

        public String getValidationReport() {
            StringBuilder report = new StringBuilder();
            report.append("VALIDATION REPORT:\n");
            report.append("─".repeat(60)).append("\n");
            report.append("Valid Entities: ").append(validEntityCount).append("\n");
            report.append("Errors Found: ").append(errors.size()).append("\n");

            if (!errors.isEmpty()) {
                report.append("\nERROR DETAILS:\n");
                for (String error : errors) {
                    report.append("  ✗ ").append(error).append("\n");
                }
            } else {
                report.append("\n✓ All entities passed validation\n");
            }

            return report.toString();
        }
    }

    /**
     * STATISTICS VISITOR
     *
     * Collects statistical data about commission entities.
     * Performs aggregations and calculations across all visited entities.
     *
     * USE CASE: Dashboard statistics, analytics, KPI calculations.
     */
    public static class StatisticsVisitor implements CommissionEntityVisitor {
        private int totalEntities = 0;
        private Map<String, Integer> entityCounts = new HashMap<>();
        private Map<DealStatus, Integer> dealStatusCounts = new HashMap<>();
        private Map<UserRole, Integer> userRoleCounts = new HashMap<>();
        private Map<DisputeStatus, Integer> disputeStatusCounts = new HashMap<>();

        private BigDecimal totalDealValue = BigDecimal.ZERO;
        private BigDecimal wonDealValue = BigDecimal.ZERO;
        private BigDecimal totalDisputedAmount = BigDecimal.ZERO;

        private int wonDeals = 0;
        private int openDeals = 0;

        @Override
        public void visitDeal(CommissionDeal deal) {
            totalEntities++;
            entityCounts.merge("Deal", 1, Integer::sum);

            // Track deal status
            dealStatusCounts.merge(deal.getStatus(), 1, Integer::sum);

            // Track values
            if (deal.getValue() != null) {
                totalDealValue = totalDealValue.add(deal.getValue());

                if (deal.getStatus() == DealStatus.WON) {
                    wonDealValue = wonDealValue.add(deal.getValue());
                    wonDeals++;
                } else if (deal.getStatus() == DealStatus.OPEN) {
                    openDeals++;
                }
            }
        }

        @Override
        public void visitCommissionPlan(CommissionPlanEntity plan) {
            totalEntities++;
            entityCounts.merge("CommissionPlan", 1, Integer::sum);
        }

        @Override
        public void visitUser(UserEntity user) {
            totalEntities++;
            entityCounts.merge("User", 1, Integer::sum);

            // Track user roles
            if (user.getRoles() != null && !user.getRoles().isEmpty()) {
                for (UserRole role : user.getRoles()) {
                    userRoleCounts.merge(role, 1, Integer::sum);
                }
            }
        }

        @Override
        public void visitDispute(DisputeEntity dispute) {
            totalEntities++;
            entityCounts.merge("Dispute", 1, Integer::sum);

            // Track dispute status
            if (dispute.getStatus() != null) {
                disputeStatusCounts.merge(dispute.getStatus(), 1, Integer::sum);
            }
        }

        public String getStatisticsReport() {
            StringBuilder report = new StringBuilder();
            report.append("╔═══════════════════════════════════════════════════════════╗\n");
            report.append("║              COMMISSION SYSTEM STATISTICS                 ║\n");
            report.append("╚═══════════════════════════════════════════════════════════╝\n\n");

            report.append("OVERALL:\n");
            report.append("  Total Entities Processed: ").append(totalEntities).append("\n\n");

            report.append("ENTITY BREAKDOWN:\n");
            entityCounts.forEach((type, count) ->
                report.append("  ").append(type).append(": ").append(count).append("\n"));

            report.append("\nDEAL STATISTICS:\n");
            report.append("  Total Deal Value: $").append(formatCurrency(totalDealValue)).append("\n");
            report.append("  Won Deal Value: $").append(formatCurrency(wonDealValue)).append("\n");
            report.append("  Pipeline (Open): ").append(openDeals).append(" deals\n");
            report.append("  Win Rate: ").append(calculateWinRate()).append("%\n");

            if (!dealStatusCounts.isEmpty()) {
                report.append("\n  Deal Status Breakdown:\n");
                dealStatusCounts.forEach((status, count) ->
                    report.append("    ").append(status).append(": ").append(count).append("\n"));
            }

            if (!userRoleCounts.isEmpty()) {
                report.append("\nUSER ROLE DISTRIBUTION:\n");
                userRoleCounts.forEach((role, count) ->
                    report.append("  ").append(role).append(": ").append(count).append("\n"));
            }

            if (!disputeStatusCounts.isEmpty()) {
                report.append("\nDISPUTE STATISTICS:\n");
                report.append("  Dispute Status Breakdown:\n");
                disputeStatusCounts.forEach((status, count) ->
                    report.append("    ").append(status).append(": ").append(count).append("\n"));
            }

            report.append("\n").append("═".repeat(60)).append("\n");

            return report.toString();
        }

        private String formatCurrency(BigDecimal amount) {
            return amount.setScale(2, RoundingMode.HALF_UP).toString();
        }

        private String calculateWinRate() {
            int totalDeals = dealStatusCounts.values().stream().mapToInt(Integer::intValue).sum();
            if (totalDeals == 0) return "0.00";

            BigDecimal rate = new BigDecimal(wonDeals)
                .divide(new BigDecimal(totalDeals), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));

            return rate.setScale(2, RoundingMode.HALF_UP).toString();
        }

        public Map<String, Integer> getEntityCounts() {
            return new HashMap<>(entityCounts);
        }

        public BigDecimal getTotalDealValue() {
            return totalDealValue;
        }

        public BigDecimal getWonDealValue() {
            return wonDealValue;
        }
    }

    /**
     * EXPORT VISITOR
     *
     * Exports entities to CSV format.
     * Demonstrates how visitors can transform data into different formats.
     *
     * USE CASE: Data export for external systems, reporting, backups.
     */
    public static class CsvExportVisitor implements CommissionEntityVisitor {
        private final StringBuilder csvData;
        private final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

        public CsvExportVisitor() {
            this.csvData = new StringBuilder();
            csvData.append("Entity Type,ID,Primary Field,Status,Value,Date\n");
        }

        @Override
        public void visitDeal(CommissionDeal deal) {
            csvData.append("Deal,")
                   .append(escape(deal.getId())).append(",")
                   .append(escape(deal.getTitle())).append(",")
                   .append(deal.getStatus()).append(",")
                   .append(deal.getValue()).append(",")
                   .append(deal.getCloseDate() != null ? deal.getCloseDate().format(dateFormatter) : "")
                   .append("\n");
        }

        @Override
        public void visitCommissionPlan(CommissionPlanEntity plan) {
            csvData.append("CommissionPlan,")
                   .append(escape(plan.getId())).append(",")
                   .append(escape(plan.getName())).append(",")
                   .append(plan.getStatus()).append(",")
                   .append(plan.getRuleCount() + plan.getTierCount()).append(",")
                   .append(plan.getEffectiveStartDate() != null ?
                          plan.getEffectiveStartDate().format(dateFormatter) : "")
                   .append("\n");
        }

        @Override
        public void visitUser(UserEntity user) {
            csvData.append("User,")
                   .append(escape(user.getId())).append(",")
                   .append(escape(user.getName())).append(",")
                   .append(user.getRoles()).append(",")
                   .append(",")  // No value for users
                   .append("")   // No date for users
                   .append("\n");
        }

        @Override
        public void visitDispute(DisputeEntity dispute) {
            csvData.append("Dispute,")
                   .append(escape(dispute.getId())).append(",")
                   .append(escape(dispute.getTitle())).append(",")
                   .append(dispute.getStatus()).append(",")
                   .append(escape(dispute.getCalculationId())).append(",")
                   .append(dispute.getCreatedDate() != null ?
                          dispute.getCreatedDate().toString() : "")
                   .append("\n");
        }

        private String escape(String value) {
            if (value == null) return "";
            // Escape commas and quotes for CSV
            if (value.contains(",") || value.contains("\"")) {
                return "\"" + value.replace("\"", "\"\"") + "\"";
            }
            return value;
        }

        public String getCsvData() {
            return csvData.toString();
        }
    }
}