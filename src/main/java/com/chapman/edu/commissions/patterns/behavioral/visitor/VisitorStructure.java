package com.chapman.edu.commissions.patterns.behavioral.visitor;

import java.util.ArrayList;
import java.util.List;

/**
 * VISITOR PATTERN - STRUCTURAL DEMONSTRATION
 *
 * PURPOSE:
 * The Visitor Pattern lets you separate algorithms from the objects on which they operate.
 * It allows you to add new operations to existing object structures without modifying those
 * structures. The pattern achieves this by moving operational logic into separate visitor classes.
 *
 * PROBLEM IT SOLVES:
 * - Adding new operations to a class hierarchy requires modifying all classes
 * - Operations on object structures are scattered across many classes
 * - You want to perform many distinct operations on objects without polluting their classes
 * - The object structure rarely changes but you frequently add new operations
 * - You want to gather related operations into one place
 *
 * WHEN TO USE:
 * - An object structure contains many classes with different interfaces
 * - You need to perform many distinct and unrelated operations on these objects
 * - The object structure classes rarely change, but you often add new operations
 * - You want to avoid polluting classes with unrelated operations
 * - You want to gather related operations and separate unrelated ones
 *
 * COMPONENTS:
 * 1. Visitor (Interface): Declares visit methods for each ConcreteElement type
 * 2. ConcreteVisitor: Implements each operation declared by Visitor
 * 3. Element (Interface): Declares accept method that takes a visitor
 * 4. ConcreteElement: Implements accept method (calls visitor's visit method)
 * 5. ObjectStructure: Can enumerate elements and allow visitors to visit them
 *
 * KEY TECHNIQUE: Double Dispatch
 * The pattern uses "double dispatch" - the operation depends on both:
 * 1. The type of Visitor (first dispatch via accept)
 * 2. The type of Element (second dispatch via visit)
 *
 */
public class VisitorStructure {

    /**
     * COMMISSION ENTITY VISITOR INTERFACE
     *
     * Defines visit methods for each type of commission-related entity.
     * Each method represents the ability to perform an operation on that specific entity type.
     */
    public interface CommissionEntityVisitor {
        void visitDeal(VisitorImplementation.CommissionDeal deal);
        void visitCommissionPlan(VisitorImplementation.CommissionPlanEntity plan);
        void visitUser(VisitorImplementation.UserEntity user);
        void visitDispute(VisitorImplementation.DisputeEntity dispute);
    }

    /**
     * COMMISSION ENTITY INTERFACE
     *
     * All visitable commission entities implement this interface.
     * The accept method enables the double-dispatch mechanism.
     */
    public interface CommissionEntity {
        void accept(CommissionEntityVisitor visitor);
        String getId();
        String getEntityType();
    }
}