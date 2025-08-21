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
import java.util.List;
import java.util.logging.Logger;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link VariableOperationGenerator}.
 * @since 0.1
 */
final class VariableOperationGeneratorTest {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(
        VariableOperationGeneratorTest.class.getName()
    );

    @Test
    void generatesAssignmentOperation() {
        final VariableOperationGenerator generator = new VariableOperationGenerator(new Rand(42L));
        final String operation = generator.generateAssignmentOperation("var1", "10", "long");
        LOGGER.info(String.format("Generated assignment: %s", operation));
        MatcherAssert.assertThat(
            "Generated operation should contain variable name",
            operation,
            Matchers.containsString("var1")
        );
        MatcherAssert.assertThat(
            "Generated operation should contain value",
            operation,
            Matchers.containsString("10")
        );
    }

    @Test
    void generatesArithmeticOperation() {
        final VariableOperationGenerator generator = new VariableOperationGenerator(new Rand(42L));
        final String operation = generator.generateArithmeticOperation("var1", "var2", "long");
        LOGGER.info(String.format("Generated arithmetic operation: %s", operation));
        MatcherAssert.assertThat(
            "Generated operation should contain both variables",
            operation,
            Matchers.allOf(
                Matchers.containsString("var1"),
                Matchers.containsString("var2")
            )
        );
    }

    @Test
    void generatesMathFunction() {
        final VariableOperationGenerator generator = new VariableOperationGenerator(new Rand(42L));
        final String operation = generator.generateMathFunction("var1", "long");
        LOGGER.info(String.format("Generated Math function: %s", operation));
        MatcherAssert.assertThat(
            "Generated operation should contain Math function",
            operation,
            Matchers.containsString("Math.")
        );
        MatcherAssert.assertThat(
            "Generated operation should contain variable",
            operation,
            Matchers.containsString("var1")
        );
    }

    @RepeatedTest(5)
    void generatesOperationChain() {
        final VariableOperationGenerator generator = new VariableOperationGenerator(new Rand());
        final List<String> operations = generator.generateOperationChain("testVar", "long", 3);
        LOGGER.info(String.format("Generated operation chain: %s", operations));
        MatcherAssert.assertThat(
            "Should generate requested number of operations",
            operations,
            Matchers.hasSize(3)
        );
        operations.forEach(operation -> 
            MatcherAssert.assertThat(
                "Each operation should contain the variable name",
                operation,
                Matchers.containsString("testVar")
            )
        );
    }

    @Test
    void generatesBooleanOperations() {
        final VariableOperationGenerator generator = new VariableOperationGenerator(new Rand(42L));
        final List<String> operations = generator.generateOperationChain("boolVar", "boolean", 2);
        LOGGER.info(String.format("Generated boolean operations: %s", operations));
        MatcherAssert.assertThat(
            "Should generate boolean operations",
            operations,
            Matchers.hasSize(2)
        );
    }

    @Test
    void generatesLiterals() {
        final VariableOperationGenerator generator = new VariableOperationGenerator(new Rand(42L));
        final String longLiteral = generator.generateLiteral("long");
        final String boolLiteral = generator.generateLiteral("boolean");
        LOGGER.info(String.format("Generated literals - long: %s, boolean: %s", longLiteral, boolLiteral));
        MatcherAssert.assertThat(
            "Long literal should be numeric",
            longLiteral,
            Matchers.matchesRegex("\\d+")
        );
        MatcherAssert.assertThat(
            "Boolean literal should be true or false",
            boolLiteral,
            Matchers.anyOf(Matchers.equalTo("true"), Matchers.equalTo("false"))
        );
    }
}
