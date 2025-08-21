/*
 * MIT License
 *
 * Copyright (c) 2023-2024 Volodya Lombrozo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.lombrozo.jsmith.antlr.semantic;

import com.github.lombrozo.jsmith.random.Rand;
import java.util.Arrays;
import java.util.List;

/**
 * Generator for variable operations.
 * This class generates various operations that can be performed on variables,
 * replacing the hardcoded operations in grammar files.
 * @since 0.1
 */
public final class VariableOperationGenerator {

    /**
     * Available arithmetic assignment operators for long variables.
     */
    private static final List<String> LONG_ASSIGNMENT_OPS = Arrays.asList(
        "+=", "-=", "*=", "/=", "%=", "&=", "|=", "^=", "<<=", ">>=", ">>>="
    );

    /**
     * Available assignment operators for boolean variables.
     */
    private static final List<String> BOOLEAN_ASSIGNMENT_OPS = Arrays.asList(
        "=", "^="
    );

    /**
     * Available arithmetic operators for long variables.
     */
    private static final List<String> LONG_ARITHMETIC_OPS = Arrays.asList(
        "+", "-", "*", "/", "%", "&", "|", "^", "<<", ">>", ">>>"
    );

    /**
     * Available boolean operators.
     */
    private static final List<String> BOOLEAN_OPS = Arrays.asList(
        "&&", "||", "^"
    );

    /**
     * Available comparison operators.
     */
    private static final List<String> COMPARISON_OPS = Arrays.asList(
        ">", "<", ">=", "<=", "==", "!="
    );

    /**
     * Available Math functions for long variables.
     */
    private static final List<String> MATH_FUNCTIONS = Arrays.asList(
        "Math.abs", "Math.max", "Math.min"
    );

    /**
     * Random generator.
     */
    private final Rand random;

    /**
     * Constructor.
     * @param random Random generator.
     */
    public VariableOperationGenerator(final Rand random) {
        this.random = random;
    }

    /**
     * Generate a random assignment operation for a variable.
     * @param variable Variable name.
     * @param value Value to assign or operate with.
     * @param type Variable type.
     * @return Generated operation string.
     */
    public String generateAssignmentOperation(final String variable, final String value, final String type) {
        if ("boolean".equals(type)) {
            // For boolean, only use boolean-compatible assignment operators
            final String operator = BOOLEAN_ASSIGNMENT_OPS.get(
                this.random.range(BOOLEAN_ASSIGNMENT_OPS.size())
            );
            return String.format("%s %s %s", variable, operator, value);
        } else {
            // For numeric types, use all assignment operators
            final String operator = LONG_ASSIGNMENT_OPS.get(
                this.random.range(LONG_ASSIGNMENT_OPS.size())
            );
            return String.format("%s %s %s", variable, operator, value);
        }
    }

    /**
     * Generate a random arithmetic operation for numeric variables only.
     * @param left Left operand.
     * @param right Right operand.
     * @param type Variable type.
     * @return Generated operation string.
     */
    public String generateArithmeticOperation(final String left, final String right, final String type) {
        // Only generate arithmetic operations for numeric types
        if ("boolean".equals(type)) {
            throw new IllegalArgumentException("Arithmetic operations not supported for boolean type");
        }
        final String operator = LONG_ARITHMETIC_OPS.get(
            this.random.range(LONG_ARITHMETIC_OPS.size())
        );
        return String.format("%s %s %s", left, operator, right);
    }

    /**
     * Generate a random boolean operation.
     * @param left Left operand.
     * @param right Right operand.
     * @return Generated operation string.
     */
    public String generateBooleanOperation(final String left, final String right) {
        final String operator = BOOLEAN_OPS.get(
            this.random.range(BOOLEAN_OPS.size())
        );
        return String.format("%s %s %s", left, operator, right);
    }

    /**
     * Generate a random comparison operation.
     * @param left Left operand.
     * @param right Right operand.
     * @return Generated operation string.
     */
    public String generateComparisonOperation(final String left, final String right) {
        final String operator = COMPARISON_OPS.get(
            this.random.range(COMPARISON_OPS.size())
        );
        return String.format("%s %s %s", left, operator, right);
    }

    /**
     * Generate a random Math function call for numeric types only.
     * @param variable Variable name.
     * @param type Variable type.
     * @return Generated Math function call.
     */
    public String generateMathFunction(final String variable, final String type) {
        // Only generate Math functions for numeric types
        if (!"long".equals(type) && !"int".equals(type)) {
            throw new IllegalArgumentException("Math functions only supported for numeric types, got: " + type);
        }
        final String function = MATH_FUNCTIONS.get(
            this.random.range(MATH_FUNCTIONS.size())
        );
        if ("Math.max".equals(function) || "Math.min".equals(function)) {
            final int value = this.random.range(1, 100);
            return String.format("%s(%s, %d)", function, variable, value);
        } else {
            return String.format("%s(%s)", function, variable);
        }
    }

    /**
     * Generate a random unary operation.
     * @param variable Variable name.
     * @param type Variable type.
     * @return Generated unary operation.
     */
    public String generateUnaryOperation(final String variable, final String type) {
        if ("boolean".equals(type)) {
            return String.format("%s = !%s", variable, variable);
        } else {
            final List<String> unaryOps = Arrays.asList("++", "--");
            final String operator = unaryOps.get(this.random.range(unaryOps.size()));
            return String.format("%s%s", variable, operator);
        }
    }

    /**
     * Generate a random literal value for the given type.
     * @param type Variable type.
     * @return Generated literal value.
     */
    public String generateLiteral(final String type) {
        switch (type.toLowerCase()) {
            case "long":
                return String.valueOf(this.random.range(1, 100));
            case "boolean":
                return this.random.range(2) == 0 ? "true" : "false";
            case "int":
                return String.valueOf(this.random.range(1, 50));
            default:
                return "1";
        }
    }

    /**
     * Generate a complex operation chain for a variable.
     * @param variable Variable name.
     * @param type Variable type.
     * @param chainLength Number of operations in the chain.
     * @return List of generated operations.
     */
    public List<String> generateOperationChain(
        final String variable,
        final String type,
        final int chainLength
    ) {
        final List<String> operations = new java.util.ArrayList<>();

        // Validate variable type
        if (type == null || type.isEmpty()) {
            throw new IllegalArgumentException("Variable type cannot be null or empty");
        }

        for (int i = 0; i < chainLength; i++) {
            final String operation = this.generateSingleOperation(variable, type);
            operations.add(operation);
        }
        return operations;
    }

    /**
     * Generate a single operation for a variable based on its type.
     * @param variable Variable name.
     * @param type Variable type.
     * @return Generated operation string.
     */
    private String generateSingleOperation(final String variable, final String type) {
        // Strict type checking to prevent errors
        if (type == null || type.isEmpty()) {
            return "// Error: null type for variable " + variable;
        }

        if ("boolean".equals(type)) {
            return this.generateBooleanOperation(variable);
        } else if ("long".equals(type) || "int".equals(type)) {
            return this.generateNumericOperation(variable, type);
        } else {
            // For unknown types, generate a simple comment instead of operations
            return "// Unsupported type: " + type + " for variable: " + variable;
        }
    }

    /**
     * Generate a boolean-specific operation.
     * @param variable Variable name.
     * @return Generated boolean operation.
     */
    private String generateBooleanOperation(final String variable) {
        // Only generate safe boolean operations
        final int opType = this.random.range(2);
        switch (opType) {
            case 0:
                // Simple negation
                return String.format("%s = !%s", variable, variable);
            default:
                // Assignment with literal
                return String.format("%s = %s", variable, this.generateLiteral("boolean"));
        }
    }

    /**
     * Generate a numeric-specific operation.
     * @param variable Variable name.
     * @param type Variable type.
     * @return Generated numeric operation.
     */
    private String generateNumericOperation(final String variable, final String type) {
        // Greatly expanded operation types for maximum complexity
        final int opType = this.random.range(10);
        switch (opType) {
            case 0:
                // Simple assignment operation
                return this.generateAssignmentOperation(
                    variable,
                    this.generateLiteral(type),
                    type
                );
            case 1:
                // Math function (only for long/int)
                if ("long".equals(type) || "int".equals(type)) {
                    return String.format(
                        "%s = %s",
                        variable,
                        this.generateMathFunction(variable, type)
                    );
                } else {
                    // Fallback to assignment
                    return this.generateAssignmentOperation(
                        variable,
                        this.generateLiteral(type),
                        type
                    );
                }
            case 2:
                // Unary operation (increment/decrement)
                return this.generateUnaryOperation(variable, type);
            case 3:
                // Loop operation - for loop with variable modification
                return this.generateLoopOperation(variable, type);
            case 4:
                // While loop operation
                return this.generateWhileLoopOperation(variable, type);
            case 5:
                // Conditional operation
                return this.generateConditionalOperation(variable, type);
            case 6:
                // Do-while loop operation
                return this.generateDoWhileLoopOperation(variable, type);
            case 7:
                // Nested loop operation
                return this.generateNestedLoopOperation(variable, type);
            case 8:
                // Complex arithmetic chain
                return this.generateComplexArithmeticChain(variable, type);
            default:
                // Switch-case operation
                return this.generateSwitchOperation(variable, type);
        }
    }

    /**
     * Generate a for loop operation that modifies the variable.
     * @param variable Variable name.
     * @param type Variable type.
     * @return Generated for loop operation.
     */
    private String generateLoopOperation(final String variable, final String type) {
        final int iterations = this.random.range(2, 6); // 2-5 iterations
        final String loopVar = "loop" + this.random.range(1000);
        final StringBuilder loop = new StringBuilder();

        // Choose different types of for loop operations
        final int loopType = this.random.range(3);

        switch (loopType) {
            case 0:
                // Simple increment/decrement loop
                loop.append("for (int ").append(loopVar).append(" = 0; ");
                loop.append(loopVar).append(" < ").append(iterations).append("; ");
                loop.append(loopVar).append("++) {\n");
                loop.append("    ").append(this.generateUnaryOperation(variable, type)).append(";\n");
                loop.append("}");
                break;
            case 1:
                // Loop with arithmetic operations
                loop.append("for (int ").append(loopVar).append(" = 0; ");
                loop.append(loopVar).append(" < ").append(iterations).append("; ");
                loop.append(loopVar).append("++) {\n");
                if ("boolean".equals(type)) {
                    loop.append("    ").append(variable).append(" = !").append(variable).append(";\n");
                } else {
                    loop.append("    ").append(variable).append(" += ").append(loopVar).append(";\n");
                }
                loop.append("}");
                break;
            default:
                // Loop with conditional operations
                loop.append("for (int ").append(loopVar).append(" = 0; ");
                loop.append(loopVar).append(" < ").append(iterations).append("; ");
                loop.append(loopVar).append("++) {\n");
                loop.append("    if (").append(loopVar).append(" % 2 == 0) {\n");
                loop.append("        ").append(this.generateUnaryOperation(variable, type)).append(";\n");
                loop.append("    }\n");
                loop.append("}");
                break;
        }

        return loop.toString();
    }

    /**
     * Generate a while loop operation that modifies the variable.
     * @param variable Variable name.
     * @param type Variable type.
     * @return Generated while loop operation.
     */
    private String generateWhileLoopOperation(final String variable, final String type) {
        final StringBuilder loop = new StringBuilder();

        if ("long".equals(type) || "int".equals(type)) {
            final int whileType = this.random.range(3);
            final int limit = this.random.range(5, 15);

            switch (whileType) {
                case 0:
                    // Simple increment while loop
                    loop.append("while (").append(variable).append(" < ").append(limit).append(") {\n");
                    loop.append("    ").append(variable).append("++;\n");
                    loop.append("}");
                    break;
                case 1:
                    // Decrement while loop (with safety check)
                    loop.append("while (").append(variable).append(" > 0) {\n");
                    loop.append("    ").append(variable).append("--;\n");
                    loop.append("    if (").append(variable).append(" <= 0) break;\n");
                    loop.append("}");
                    break;
                default:
                    // Complex while loop with multiple operations
                    loop.append("while (").append(variable).append(" < ").append(limit).append(") {\n");
                    loop.append("    ").append(variable).append(" += 2;\n");
                    loop.append("    if (").append(variable).append(" % 3 == 0) {\n");
                    loop.append("        ").append(variable).append("++;\n");
                    loop.append("    }\n");
                    loop.append("}");
                    break;
            }
        } else {
            // For boolean, create different types of counting loops
            final String counter = "count" + this.random.range(1000);
            final int boolLoopType = this.random.range(2);

            switch (boolLoopType) {
                case 0:
                    // Simple boolean toggle loop
                    loop.append("int ").append(counter).append(" = 0;\n");
                    loop.append("while (").append(counter).append(" < 3) {\n");
                    loop.append("    ").append(variable).append(" = !").append(variable).append(";\n");
                    loop.append("    ").append(counter).append("++;\n");
                    loop.append("}");
                    break;
                default:
                    // Conditional boolean loop
                    loop.append("int ").append(counter).append(" = 0;\n");
                    loop.append("while (").append(counter).append(" < 5) {\n");
                    loop.append("    if (").append(counter).append(" % 2 == 0) {\n");
                    loop.append("        ").append(variable).append(" = true;\n");
                    loop.append("    } else {\n");
                    loop.append("        ").append(variable).append(" = false;\n");
                    loop.append("    }\n");
                    loop.append("    ").append(counter).append("++;\n");
                    loop.append("}");
                    break;
            }
        }

        return loop.toString();
    }

    /**
     * Generate a conditional operation (ternary operator or if-else).
     * @param variable Variable name.
     * @param type Variable type.
     * @return Generated conditional operation.
     */
    private String generateConditionalOperation(final String variable, final String type) {
        final StringBuilder conditional = new StringBuilder();

        if ("boolean".equals(type)) {
            // Boolean conditional operations
            final int condType = this.random.range(2);
            switch (condType) {
                case 0:
                    // Ternary operator
                    conditional.append(variable).append(" = (").append(variable).append(" ? false : true)");
                    break;
                default:
                    // If-else statement
                    conditional.append("if (").append(variable).append(") {\n");
                    conditional.append("    ").append(variable).append(" = false;\n");
                    conditional.append("} else {\n");
                    conditional.append("    ").append(variable).append(" = true;\n");
                    conditional.append("}");
                    break;
            }
        } else {
            // Numeric conditional operations
            final int value1 = this.random.range(1, 20);
            final int value2 = this.random.range(21, 50);
            final int condType = this.random.range(2);

            switch (condType) {
                case 0:
                    // Ternary operator
                    conditional.append(variable).append(" = (").append(variable).append(" > 10 ? ")
                              .append(value1).append(" : ").append(value2).append(")");
                    break;
                default:
                    // If-else statement
                    conditional.append("if (").append(variable).append(" % 2 == 0) {\n");
                    conditional.append("    ").append(variable).append(" *= 2;\n");
                    conditional.append("} else {\n");
                    conditional.append("    ").append(variable).append(" += 1;\n");
                    conditional.append("}");
                    break;
            }
        }

        return conditional.toString();
    }

    /**
     * Generate a do-while loop operation.
     * @param variable Variable name.
     * @param type Variable type.
     * @return Generated do-while loop operation.
     */
    private String generateDoWhileLoopOperation(final String variable, final String type) {
        final StringBuilder loop = new StringBuilder();

        if ("boolean".equals(type)) {
            // Boolean do-while loop
            final String counter = "count" + this.random.range(1000);
            loop.append("int ").append(counter).append(" = 0;\n");
            loop.append("do {\n");
            loop.append("    ").append(variable).append(" = !").append(variable).append(";\n");
            loop.append("    ").append(counter).append("++;\n");
            loop.append("} while (").append(counter).append(" < 3)");
        } else {
            // Numeric do-while loop
            final int limit = this.random.range(3, 8);
            loop.append("do {\n");
            loop.append("    ").append(variable).append("++;\n");
            loop.append("} while (").append(variable).append(" < ").append(limit).append(")");
        }

        return loop.toString();
    }

    /**
     * Generate nested loop operations for maximum complexity.
     * @param variable Variable name.
     * @param type Variable type.
     * @return Generated nested loop operation.
     */
    private String generateNestedLoopOperation(final String variable, final String type) {
        final StringBuilder nested = new StringBuilder();
        final String outerLoop = "outer" + this.random.range(1000);
        final String innerLoop = "inner" + this.random.range(1000);

        if ("boolean".equals(type)) {
            // Nested loop for boolean
            nested.append("for (int ").append(outerLoop).append(" = 0; ").append(outerLoop).append(" < 3; ").append(outerLoop).append("++) {\n");
            nested.append("    for (int ").append(innerLoop).append(" = 0; ").append(innerLoop).append(" < 2; ").append(innerLoop).append("++) {\n");
            nested.append("        if ((").append(outerLoop).append(" + ").append(innerLoop).append(") % 2 == 0) {\n");
            nested.append("            ").append(variable).append(" = !").append(variable).append(";\n");
            nested.append("        }\n");
            nested.append("    }\n");
            nested.append("}");
        } else {
            // Nested loop for numeric
            nested.append("for (int ").append(outerLoop).append(" = 0; ").append(outerLoop).append(" < 3; ").append(outerLoop).append("++) {\n");
            nested.append("    for (int ").append(innerLoop).append(" = 0; ").append(innerLoop).append(" < 2; ").append(innerLoop).append("++) {\n");
            nested.append("        ").append(variable).append(" += ").append(outerLoop).append(" * ").append(innerLoop).append(";\n");
            nested.append("        if (").append(variable).append(" > 100) {\n");
            nested.append("            ").append(variable).append(" /= 2;\n");
            nested.append("        }\n");
            nested.append("    }\n");
            nested.append("}");
        }

        return nested.toString();
    }

    /**
     * Generate complex arithmetic chain operations.
     * @param variable Variable name.
     * @param type Variable type.
     * @return Generated complex arithmetic chain.
     */
    private String generateComplexArithmeticChain(final String variable, final String type) {
        final StringBuilder chain = new StringBuilder();

        if ("boolean".equals(type)) {
            // Complex boolean chain
            chain.append(variable).append(" = !").append(variable).append(";\n");
            chain.append(variable).append(" = ").append(variable).append(" ? false : true;\n");
            chain.append("if (").append(variable).append(") {\n");
            chain.append("    ").append(variable).append(" = false;\n");
            chain.append("} else {\n");
            chain.append("    ").append(variable).append(" = true;\n");
            chain.append("}\n");
            chain.append(variable).append(" ^= true");
        } else {
            // Complex numeric chain
            final int val1 = this.random.range(1, 10);
            final int val2 = this.random.range(11, 20);
            final int val3 = this.random.range(21, 30);

            chain.append(variable).append(" += ").append(val1).append(";\n");
            chain.append(variable).append(" = Math.abs(").append(variable).append(");\n");
            chain.append(variable).append(" *= ").append(val2).append(";\n");
            chain.append(variable).append(" = Math.max(").append(variable).append(", ").append(val3).append(");\n");
            chain.append(variable).append(" %= 100;\n");
            chain.append("if (").append(variable).append(" > 50) {\n");
            chain.append("    ").append(variable).append(" -= 25;\n");
            chain.append("} else {\n");
            chain.append("    ").append(variable).append(" += 25;\n");
            chain.append("}\n");
            chain.append(variable).append(" = Math.min(").append(variable).append(", 75)");
        }

        return chain.toString();
    }

    /**
     * Generate switch-case operations.
     * @param variable Variable name.
     * @param type Variable type.
     * @return Generated switch operation.
     */
    private String generateSwitchOperation(final String variable, final String type) {
        final StringBuilder switchOp = new StringBuilder();

        if ("boolean".equals(type)) {
            // Boolean switch using a helper variable
            final String helper = "helper" + this.random.range(1000);
            switchOp.append("int ").append(helper).append(" = ").append(variable).append(" ? 1 : 0;\n");
            switchOp.append("switch (").append(helper).append(") {\n");
            switchOp.append("    case 0:\n");
            switchOp.append("        ").append(variable).append(" = true;\n");
            switchOp.append("        break;\n");
            switchOp.append("    case 1:\n");
            switchOp.append("        ").append(variable).append(" = false;\n");
            switchOp.append("        break;\n");
            switchOp.append("    default:\n");
            switchOp.append("        ").append(variable).append(" = !").append(variable).append(";\n");
            switchOp.append("}");
        } else {
            // Numeric switch
            switchOp.append("switch ((int)(").append(variable).append(" % 4)) {\n");
            switchOp.append("    case 0:\n");
            switchOp.append("        ").append(variable).append(" += 10;\n");
            switchOp.append("        break;\n");
            switchOp.append("    case 1:\n");
            switchOp.append("        ").append(variable).append(" -= 5;\n");
            switchOp.append("        break;\n");
            switchOp.append("    case 2:\n");
            switchOp.append("        ").append(variable).append(" *= 2;\n");
            switchOp.append("        break;\n");
            switchOp.append("    default:\n");
            switchOp.append("        ").append(variable).append(" = Math.abs(").append(variable).append(");\n");
            switchOp.append("}");
        }

        return switchOp.toString();
    }
}
