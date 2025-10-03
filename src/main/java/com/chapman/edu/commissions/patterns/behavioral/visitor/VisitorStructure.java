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
 * @author Commission Calculator Educational Project
 */
public class VisitorStructure {

    /**
     * VISITOR INTERFACE
     *
     * Declares a visit method for each type of ConcreteElement in the object structure.
     * Each visit method's name and signature identifies the class of element that can be visited.
     *
     * KEY CHARACTERISTICS:
     * - One visit method per concrete element type
     * - Enables the visitor to determine the concrete class of the element being visited
     * - This is the first half of the "double dispatch" mechanism
     */
    public interface Visitor {
        /**
         * Visit a ConcreteElementA.
         * Operation specific to ElementA.
         */
        void visitConcreteElementA(ConcreteElementA element);

        /**
         * Visit a ConcreteElementB.
         * Operation specific to ElementB.
         */
        void visitConcreteElementB(ConcreteElementB element);

        /**
         * Visit a ConcreteElementC.
         * Operation specific to ElementC.
         */
        void visitConcreteElementC(ConcreteElementC element);
    }

    /**
     * ELEMENT INTERFACE
     *
     * Declares an accept method that takes a visitor as an argument.
     * This is the second half of the "double dispatch" mechanism.
     *
     * KEY RESPONSIBILITY:
     * - Provide an entry point for the visitor
     * - Enable the visitor to access the element
     */
    public interface Element {
        /**
         * Accept a visitor.
         * The element calls the appropriate visit method on the visitor,
         * passing itself as the argument.
         *
         * @param visitor The visitor to accept
         */
        void accept(Visitor visitor);
    }

    /**
     * CONCRETE ELEMENT A
     *
     * Implements the Element interface and defines the accept method.
     * Has its own unique data and methods.
     *
     * KEY POINT: The accept method implements the "trick" of the pattern.
     * It calls visitor.visitConcreteElementA(this), which:
     * 1. Tells the visitor this is an ElementA (not B or C)
     * 2. Passes itself so the visitor can access its specific data/methods
     */
    public static class ConcreteElementA implements Element {
        private String dataA;

        public ConcreteElementA(String dataA) {
            this.dataA = dataA;
        }

        /**
         * DOUBLE DISPATCH IMPLEMENTATION
         *
         * This is where the magic happens!
         * The element calls the visitor's method that's specific to its type.
         * The visitor now knows it's visiting an ElementA and can access ElementA-specific methods.
         */
        @Override
        public void accept(Visitor visitor) {
            // Call the visit method specific to this element type
            visitor.visitConcreteElementA(this);
        }

        // Element-specific method
        public String getDataA() {
            return dataA;
        }

        public String operationA() {
            return "ElementA operation: " + dataA;
        }
    }

    /**
     * CONCRETE ELEMENT B
     *
     * Another concrete element with different data and operations.
     */
    public static class ConcreteElementB implements Element {
        private int dataB;

        public ConcreteElementB(int dataB) {
            this.dataB = dataB;
        }

        @Override
        public void accept(Visitor visitor) {
            // Call the visit method specific to this element type
            visitor.visitConcreteElementB(this);
        }

        // Element-specific method
        public int getDataB() {
            return dataB;
        }

        public String operationB() {
            return "ElementB operation: " + dataB;
        }
    }

    /**
     * CONCRETE ELEMENT C
     *
     * Yet another concrete element with its own unique characteristics.
     */
    public static class ConcreteElementC implements Element {
        private boolean dataC;

        public ConcreteElementC(boolean dataC) {
            this.dataC = dataC;
        }

        @Override
        public void accept(Visitor visitor) {
            // Call the visit method specific to this element type
            visitor.visitConcreteElementC(this);
        }

        // Element-specific method
        public boolean getDataC() {
            return dataC;
        }

        public String operationC() {
            return "ElementC operation: " + dataC;
        }
    }

    /**
     * CONCRETE VISITOR 1 - Display Operation
     *
     * Implements a specific operation (displaying element information)
     * for each type of element.
     *
     * KEY BENEFIT: All display logic is in one class, not scattered across elements.
     */
    public static class DisplayVisitor implements Visitor {
        private StringBuilder output = new StringBuilder();

        @Override
        public void visitConcreteElementA(ConcreteElementA element) {
            // Can access ElementA-specific methods
            output.append("Displaying ElementA: ").append(element.operationA()).append("\n");
            System.out.println("→ DisplayVisitor visiting ElementA: " + element.getDataA());
        }

        @Override
        public void visitConcreteElementB(ConcreteElementB element) {
            // Can access ElementB-specific methods
            output.append("Displaying ElementB: ").append(element.operationB()).append("\n");
            System.out.println("→ DisplayVisitor visiting ElementB: " + element.getDataB());
        }

        @Override
        public void visitConcreteElementC(ConcreteElementC element) {
            // Can access ElementC-specific methods
            output.append("Displaying ElementC: ").append(element.operationC()).append("\n");
            System.out.println("→ DisplayVisitor visiting ElementC: " + element.getDataC());
        }

        public String getOutput() {
            return output.toString();
        }
    }

    /**
     * CONCRETE VISITOR 2 - Validation Operation
     *
     * Implements a completely different operation (validation)
     * on the same element structure.
     *
     * KEY BENEFIT: We can add this new operation WITHOUT modifying any element classes!
     */
    public static class ValidationVisitor implements Visitor {
        private List<String> errors = new ArrayList<>();
        private int validCount = 0;

        @Override
        public void visitConcreteElementA(ConcreteElementA element) {
            // Validate ElementA
            if (element.getDataA() == null || element.getDataA().isEmpty()) {
                errors.add("ElementA has empty data");
                System.out.println("→ ValidationVisitor: ElementA INVALID (empty data)");
            } else {
                validCount++;
                System.out.println("→ ValidationVisitor: ElementA VALID");
            }
        }

        @Override
        public void visitConcreteElementB(ConcreteElementB element) {
            // Validate ElementB
            if (element.getDataB() < 0) {
                errors.add("ElementB has negative value: " + element.getDataB());
                System.out.println("→ ValidationVisitor: ElementB INVALID (negative)");
            } else {
                validCount++;
                System.out.println("→ ValidationVisitor: ElementB VALID");
            }
        }

        @Override
        public void visitConcreteElementC(ConcreteElementC element) {
            // ElementC is always valid in this example
            validCount++;
            System.out.println("→ ValidationVisitor: ElementC VALID");
        }

        public boolean isValid() {
            return errors.isEmpty();
        }

        public List<String> getErrors() {
            return new ArrayList<>(errors);
        }

        public int getValidCount() {
            return validCount;
        }
    }

    /**
     * CONCRETE VISITOR 3 - Export Operation
     *
     * Yet another operation - exporting to a specific format.
     *
     * KEY BENEFIT: Easy to add new operations (visitors) without touching element code.
     */
    public static class ExportVisitor implements Visitor {
        private StringBuilder exportData = new StringBuilder();

        public ExportVisitor() {
            exportData.append("=== EXPORT DATA ===\n");
        }

        @Override
        public void visitConcreteElementA(ConcreteElementA element) {
            exportData.append("TYPE: ElementA | DATA: ")
                     .append(element.getDataA())
                     .append("\n");
            System.out.println("→ ExportVisitor: Exported ElementA");
        }

        @Override
        public void visitConcreteElementB(ConcreteElementB element) {
            exportData.append("TYPE: ElementB | DATA: ")
                     .append(element.getDataB())
                     .append("\n");
            System.out.println("→ ExportVisitor: Exported ElementB");
        }

        @Override
        public void visitConcreteElementC(ConcreteElementC element) {
            exportData.append("TYPE: ElementC | DATA: ")
                     .append(element.getDataC())
                     .append("\n");
            System.out.println("→ ExportVisitor: Exported ElementC");
        }

        public String getExportData() {
            return exportData.toString();
        }
    }

    /**
     * OBJECT STRUCTURE
     *
     * Holds a collection of elements and provides methods to:
     * 1. Add elements
     * 2. Allow visitors to visit all elements
     *
     * This class demonstrates how to use the pattern with a collection of elements.
     */
    public static class ObjectStructure {
        private List<Element> elements = new ArrayList<>();

        public void addElement(Element element) {
            elements.add(element);
        }

        /**
         * Allow a visitor to visit all elements in the structure.
         * This is the main entry point for using the pattern.
         *
         * @param visitor The visitor to apply to all elements
         */
        public void acceptVisitor(Visitor visitor) {
            for (Element element : elements) {
                element.accept(visitor);
            }
        }

        public int getElementCount() {
            return elements.size();
        }
    }

    /**
     * DEMONSTRATION
     *
     * Shows how the Visitor pattern works and its key benefits.
     */
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║       VISITOR PATTERN - STRUCTURE DEMONSTRATION           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        // Create an object structure and populate it with elements
        System.out.println("Step 1: Creating object structure with various elements\n");
        ObjectStructure structure = new ObjectStructure();
        structure.addElement(new ConcreteElementA("Hello"));
        structure.addElement(new ConcreteElementB(42));
        structure.addElement(new ConcreteElementC(true));
        structure.addElement(new ConcreteElementA("World"));
        structure.addElement(new ConcreteElementB(-5)); // This one has invalid data

        System.out.println("Created structure with " + structure.getElementCount() + " elements\n");
        System.out.println("=".repeat(60) + "\n");

        // Operation 1: Display all elements
        System.out.println("Operation 1: DISPLAY (using DisplayVisitor)\n");
        DisplayVisitor displayVisitor = new DisplayVisitor();
        structure.acceptVisitor(displayVisitor);
        System.out.println("\nDisplay Output:\n" + displayVisitor.getOutput());
        System.out.println("=".repeat(60) + "\n");

        // Operation 2: Validate all elements
        System.out.println("Operation 2: VALIDATE (using ValidationVisitor)\n");
        ValidationVisitor validationVisitor = new ValidationVisitor();
        structure.acceptVisitor(validationVisitor);
        System.out.println("\nValidation Results:");
        System.out.println("  Valid elements: " + validationVisitor.getValidCount());
        System.out.println("  Is valid: " + validationVisitor.isValid());
        if (!validationVisitor.isValid()) {
            System.out.println("  Errors found:");
            validationVisitor.getErrors().forEach(e -> System.out.println("    - " + e));
        }
        System.out.println("\n" + "=".repeat(60) + "\n");

        // Operation 3: Export all elements
        System.out.println("Operation 3: EXPORT (using ExportVisitor)\n");
        ExportVisitor exportVisitor = new ExportVisitor();
        structure.acceptVisitor(exportVisitor);
        System.out.println("\nExported Data:\n" + exportVisitor.getExportData());
        System.out.println("=".repeat(60) + "\n");

        // Summary
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                      KEY OBSERVATIONS                     ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("1. SEPARATION OF CONCERNS");
        System.out.println("   → Display logic is in DisplayVisitor");
        System.out.println("   → Validation logic is in ValidationVisitor");
        System.out.println("   → Export logic is in ExportVisitor");
        System.out.println("   → Element classes remain simple and focused");
        System.out.println();
        System.out.println("2. EASY TO ADD NEW OPERATIONS");
        System.out.println("   → Added 3 different operations without modifying elements");
        System.out.println("   → Can add more visitors (e.g., SerializationVisitor) easily");
        System.out.println("   → Elements don't need to know about new operations");
        System.out.println();
        System.out.println("3. DOUBLE DISPATCH");
        System.out.println("   → element.accept(visitor) - 1st dispatch: element knows visitor");
        System.out.println("   → visitor.visitElementX(element) - 2nd dispatch: visitor knows element type");
        System.out.println("   → Result: operation depends on both visitor AND element type");
        System.out.println();
        System.out.println("4. SINGLE RESPONSIBILITY PRINCIPLE");
        System.out.println("   → Each visitor has one responsibility (one operation)");
        System.out.println("   → Element classes don't mix multiple concerns");
        System.out.println();
        System.out.println("5. OPEN/CLOSED PRINCIPLE");
        System.out.println("   → Open for extension: add new visitors");
        System.out.println("   → Closed for modification: elements unchanged");
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println();
    }
}