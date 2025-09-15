# Decorator Pattern Implementation

## Overview
The Decorator Pattern is a structural design pattern that allows behavior to be added to individual objects, either statically or dynamically, without affecting the behavior of other objects from the same class. It is a flexible alternative to subclassing for extending functionality.

In this implementation, we demonstrate the Decorator Pattern using the commission calculator model classes. We've created two separate examples:

1. **Simple Commission Decorators**: A basic implementation showing how to decorate a commission calculation with bonuses, accelerators, and tax calculations.
2. **Deal Decorators**: A more complex implementation showing how to decorate Deal objects with various behaviors like discounts, premiums, urgency handling, and logging.

## Key Components

### Component Interface
Defines the interface for objects that can have responsibilities added to them.
- `Commission` interface for the simple example
- `DealComponent` interface for the complex example

### Concrete Component
Defines an object to which additional responsibilities can be attached.
- `BaseCommission` class for the simple example
- `BasicDeal` class for the complex example

### Decorator
Maintains a reference to a Component object and defines an interface that conforms to Component's interface.
- `CommissionDecorator` abstract class for the simple example
- `DealDecorator` abstract class for the complex example

### Concrete Decorators
Add responsibilities to the component.
- Simple example: `BonusDecorator`, `AcceleratorDecorator`, `TaxDecorator`
- Complex example: `DiscountDecorator`, `PremiumDecorator`, `UrgencyDecorator`, `LoggingDecorator`

## Implementation Details

### Simple Commission Decorators
The simple commission decorators demonstrate the basic structure of the Decorator Pattern. They allow you to:
- Add a bonus amount to a base commission
- Apply an accelerator multiplier to a commission
- Apply tax calculations to a commission

### Deal Decorators
The deal decorators demonstrate a more complex implementation of the Decorator Pattern using the actual model classes from the project. They allow you to:
- Apply a discount to a deal's value
- Apply a premium to a deal's value
- Add urgency handling based on deadlines
- Add logging functionality to track method calls

## Usage Examples

### Simple Commission Decorators
```java
// Create a base commission
Commission baseCommission = new BaseCommission(new BigDecimal("1000.00"));

// Add a bonus to the commission
Commission commissionWithBonus = new BonusDecorator(baseCommission, new BigDecimal("200.00"));

// Add an accelerator to the commission
Commission commissionWithAccelerator = new AcceleratorDecorator(baseCommission, new BigDecimal("1.5"));

// Apply tax to the commission
Commission commissionAfterTax = new TaxDecorator(baseCommission, new BigDecimal("0.3"));
```

### Deal Decorators
```java
// Create a sample deal
Deal deal = createSampleDeal();

// Create a basic deal component
DealComponent basicDeal = new BasicDeal(deal);

// Apply a discount to the deal
DealComponent discountedDeal = new DiscountDecorator(basicDeal, new BigDecimal("0.1")); // 10% discount

// Apply a premium to the deal
DealComponent premiumDeal = new PremiumDecorator(basicDeal, new BigDecimal("0.15")); // 15% premium

// Apply urgency to the deal
DealComponent urgentDeal = new UrgencyDecorator(basicDeal, LocalDate.now().plusDays(5)); // Deadline in 5 days

// Apply logging to the deal
DealComponent loggingDeal = new LoggingDecorator(basicDeal);
```

### Combining Multiple Decorators
One of the key benefits of the Decorator Pattern is the ability to combine multiple decorators to create complex behavior:

```java
// Combine discount and urgency
DealComponent discountedUrgentDeal = new UrgencyDecorator(
    new DiscountDecorator(basicDeal, new BigDecimal("0.1")),
    LocalDate.now().plusDays(5)
);

// Complex combination
DealComponent complexDeal = new LoggingDecorator(
    new UrgencyDecorator(
        new PremiumDecorator(
            new DiscountDecorator(basicDeal, new BigDecimal("0.05")),
            new BigDecimal("0.1")
        ),
        LocalDate.now().plusDays(3)
    )
);
```

## Benefits of the Decorator Pattern

1. **More flexibility than static inheritance**: The Decorator Pattern provides a more flexible way to add responsibilities to objects than can be had with static inheritance.

2. **Avoids feature-laden classes high up in the hierarchy**: The pattern allows functionality to be divided between classes with unique areas of concern.

3. **Enhances the object's behavior without modifying its code**: You can add new behavior to an object without changing its underlying code.

4. **Allows for combining behaviors**: You can combine multiple decorators to create complex behaviors.

5. **Follows the Single Responsibility Principle**: Each decorator class has a single responsibility, making the code more maintainable.

6. **Follows the Open/Closed Principle**: You can extend the behavior of an object without modifying its code.

## UML Diagram
A UML diagram showing the structure of the Decorator Pattern implementation can be found in the `decorator_pattern.puml` file.