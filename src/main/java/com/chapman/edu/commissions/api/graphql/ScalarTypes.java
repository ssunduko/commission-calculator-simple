package com.chapman.edu.commissions.api.graphql;

import graphql.language.StringValue;
import graphql.schema.*;
import graphql.language.IntValue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Custom scalar type implementations for GraphQL.
 *
 * WHAT ARE SCALARS?
 * -----------------
 * Scalars are the primitive leaf values in GraphQL. Built-in scalars include:
 * - String: UTF-8 character sequences
 * - Int: Signed 32-bit integers
 * - Float: Signed double-precision floating-point values
 * - Boolean: true or false
 * - ID: Unique identifier (serialized as String)
 *
 * WHY CUSTOM SCALARS?
 * ------------------
 * Custom scalars allow us to:
 * 1. Represent domain-specific types (Date, DateTime, BigDecimal)
 * 2. Enforce type safety at the GraphQL layer
 * 3. Provide consistent serialization/deserialization
 * 4. Improve API documentation and client code generation
 *
 * HOW SCALARS WORK:
 * ----------------
 * Each scalar must implement three core operations:
 * 1. Serialize: Convert Java object to GraphQL output (response to client)
 * 2. parseValue: Convert GraphQL input variable to Java object
 * 3. parseLiteral: Convert GraphQL query literal to Java object
 *
 * COERCION PATTERN:
 * ----------------
 * GraphQL uses "coercion" to convert values between representations:
 * - Serialize: Java → JSON (for responses)
 * - Parse: JSON/Literal → Java (for inputs)
 *
 * This class demonstrates:
 * - Custom scalar implementation
 * - Type coercion and validation
 * - Error handling for invalid inputs
 * - ISO-8601 date/time formatting
 * - Precise decimal number handling
 */
public class ScalarTypes {

    /**
     * DATE SCALAR
     * -----------
     * Represents a LocalDate (date without time).
     * Format: ISO-8601 (YYYY-MM-DD)
     * Example: "2024-01-15"
     *
     * Use cases:
     * - Birth dates
     * - Deal close dates
     * - Plan effective dates
     * - Any date-only value (no time component)
     */
    public static final GraphQLScalarType DATE = GraphQLScalarType.newScalar()
            .name("Date")
            .description("A custom scalar representing a date in ISO-8601 format (YYYY-MM-DD). " +
                    "Example: \"2024-01-15\"")
            .coercing(new Coercing<LocalDate, String>() {

                /**
                 * SERIALIZE
                 * ---------
                 * Called when sending data to the client.
                 * Converts LocalDate → String for JSON response.
                 *
                 * @param dataFetcherResult The LocalDate value from our data fetcher
                 * @return String representation in ISO-8601 format
                 * @throws CoercingSerializeException if value cannot be serialized
                 */
                @Override
                public String serialize(Object dataFetcherResult) throws CoercingSerializeException {
                    if (dataFetcherResult instanceof LocalDate localDate) {
                        return localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
                    }
                    throw new CoercingSerializeException(
                            "Expected a LocalDate object but got: " + dataFetcherResult.getClass().getName()
                    );
                }

                /**
                 * PARSE VALUE
                 * -----------
                 * Called when receiving input variables.
                 * Converts String → LocalDate for our Java code.
                 *
                 * Example: In a mutation like:
                 * mutation($date: Date!) {
                 *   createDeal(input: { closeDate: $date })
                 * }
                 * variables: { "date": "2024-01-15" }
                 *
                 * @param input The input value from variables
                 * @return LocalDate object
                 * @throws CoercingParseValueException if value cannot be parsed
                 */
                @Override
                public LocalDate parseValue(Object input) throws CoercingParseValueException {
                    if (input instanceof String stringValue) {
                        try {
                            return LocalDate.parse(stringValue, DateTimeFormatter.ISO_LOCAL_DATE);
                        } catch (DateTimeParseException e) {
                            throw new CoercingParseValueException(
                                    "Invalid date format. Expected ISO-8601 (YYYY-MM-DD): " + stringValue, e
                            );
                        }
                    }
                    throw new CoercingParseValueException(
                            "Expected a String value but got: " + input.getClass().getName()
                    );
                }

                /**
                 * PARSE LITERAL
                 * -------------
                 * Called when parsing inline literals in queries.
                 * Converts AST literal → LocalDate for our Java code.
                 *
                 * Example: In a query like:
                 * query {
                 *   activeCommissionPlansOnDate(date: "2024-01-15") {
                 *     id name
                 *   }
                 * }
                 *
                 * @param input The AST literal value
                 * @return LocalDate object
                 * @throws CoercingParseLiteralException if literal cannot be parsed
                 */
                @Override
                public LocalDate parseLiteral(Object input) throws CoercingParseLiteralException {
                    if (input instanceof StringValue stringValue) {
                        try {
                            return LocalDate.parse(stringValue.getValue(), DateTimeFormatter.ISO_LOCAL_DATE);
                        } catch (DateTimeParseException e) {
                            throw new CoercingParseLiteralException(
                                    "Invalid date format. Expected ISO-8601 (YYYY-MM-DD): " + stringValue.getValue(), e
                            );
                        }
                    }
                    throw new CoercingParseLiteralException(
                            "Expected a StringValue but got: " + input.getClass().getName()
                    );
                }
            })
            .build();

    /**
     * DATETIME SCALAR
     * ---------------
     * Represents a LocalDateTime (date with time).
     * Format: ISO-8601 (YYYY-MM-DDTHH:mm:ss)
     * Example: "2024-01-15T14:30:00"
     *
     * Use cases:
     * - Created/modified timestamps
     * - Last login times
     * - Dispute creation/resolution times
     * - Any timestamp requiring precision to the second
     *
     * Note: This implementation uses LocalDateTime (no timezone).
     * For timezone-aware timestamps, consider using ZonedDateTime or Instant.
     */
    public static final GraphQLScalarType DATETIME = GraphQLScalarType.newScalar()
            .name("DateTime")
            .description("A custom scalar representing a date-time in ISO-8601 format (YYYY-MM-DDTHH:mm:ss). " +
                    "Example: \"2024-01-15T14:30:00\"")
            .coercing(new Coercing<LocalDateTime, String>() {

                @Override
                public String serialize(Object dataFetcherResult) throws CoercingSerializeException {
                    if (dataFetcherResult instanceof LocalDateTime localDateTime) {
                        return localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    }
                    throw new CoercingSerializeException(
                            "Expected a LocalDateTime object but got: " + dataFetcherResult.getClass().getName()
                    );
                }

                @Override
                public LocalDateTime parseValue(Object input) throws CoercingParseValueException {
                    if (input instanceof String stringValue) {
                        try {
                            return LocalDateTime.parse(stringValue, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        } catch (DateTimeParseException e) {
                            throw new CoercingParseValueException(
                                    "Invalid datetime format. Expected ISO-8601 (YYYY-MM-DDTHH:mm:ss): " + stringValue, e
                            );
                        }
                    }
                    throw new CoercingParseValueException(
                            "Expected a String value but got: " + input.getClass().getName()
                    );
                }

                @Override
                public LocalDateTime parseLiteral(Object input) throws CoercingParseLiteralException {
                    if (input instanceof StringValue stringValue) {
                        try {
                            return LocalDateTime.parse(stringValue.getValue(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        } catch (DateTimeParseException e) {
                            throw new CoercingParseLiteralException(
                                    "Invalid datetime format. Expected ISO-8601 (YYYY-MM-DDTHH:mm:ss): " +
                                            stringValue.getValue(), e
                            );
                        }
                    }
                    throw new CoercingParseLiteralException(
                            "Expected a StringValue but got: " + input.getClass().getName()
                    );
                }
            })
            .build();

    /**
     * BIGDECIMAL SCALAR
     * -----------------
     * Represents precise decimal numbers.
     * Format: String representation of decimal number
     * Example: "1234.56"
     *
     * WHY BIGDECIMAL INSTEAD OF FLOAT?
     * --------------------------------
     * Float (Double) has precision issues with decimal arithmetic:
     * - 0.1 + 0.2 = 0.30000000000000004 (in floating-point)
     * - Financial calculations require exact decimal precision
     * - Commission amounts, deal values, prices must be exact
     *
     * BigDecimal provides:
     * - Arbitrary precision
     * - Exact decimal arithmetic
     * - Control over rounding modes
     * - No floating-point errors
     *
     * Use cases:
     * - Money amounts (deal value, commission, price)
     * - Percentages requiring precision
     * - Any financial calculation
     *
     * IMPORTANT: GraphQL transmits BigDecimal as String to preserve precision.
     * Clients should parse these as decimal types (not floating-point).
     */
    public static final GraphQLScalarType BIGDECIMAL = GraphQLScalarType.newScalar()
            .name("BigDecimal")
            .description("A custom scalar representing a precise decimal number. " +
                    "Transmitted as String to preserve precision. Example: \"1234.56\"")
            .coercing(new Coercing<BigDecimal, String>() {

                @Override
                public String serialize(Object dataFetcherResult) throws CoercingSerializeException {
                    if (dataFetcherResult instanceof BigDecimal bigDecimal) {
                        // Convert to String to preserve exact precision
                        return bigDecimal.toPlainString();
                    }
                    throw new CoercingSerializeException(
                            "Expected a BigDecimal object but got: " + dataFetcherResult.getClass().getName()
                    );
                }

                @Override
                public BigDecimal parseValue(Object input) throws CoercingParseValueException {
                    try {
                        // Accept String or Number types
                        if (input instanceof String stringValue) {
                            return new BigDecimal(stringValue);
                        } else if (input instanceof Number numberValue) {
                            return BigDecimal.valueOf(numberValue.doubleValue());
                        }
                        throw new CoercingParseValueException(
                                "Expected a String or Number value but got: " + input.getClass().getName()
                        );
                    } catch (NumberFormatException e) {
                        throw new CoercingParseValueException(
                                "Invalid decimal number format: " + input, e
                        );
                    }
                }

                @Override
                public BigDecimal parseLiteral(Object input) throws CoercingParseLiteralException {
                    try {
                        // Handle both String and Int literals in queries
                        if (input instanceof StringValue stringValue) {
                            return new BigDecimal(stringValue.getValue());
                        } else if (input instanceof IntValue intValue) {
                            return new BigDecimal(intValue.getValue());
                        }
                        throw new CoercingParseLiteralException(
                                "Expected a StringValue or IntValue but got: " + input.getClass().getName()
                        );
                    } catch (NumberFormatException e) {
                        throw new CoercingParseLiteralException(
                                "Invalid decimal number format", e
                        );
                    }
                }
            })
            .build();

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static members.
     *
     * Design Pattern: Utility Class
     * - All members are static
     * - No instance state
     * - Prevent instantiation with private constructor
     */
    private ScalarTypes() {
        throw new AssertionError("Cannot instantiate utility class");
    }
}