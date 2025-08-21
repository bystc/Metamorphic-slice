/*
 * MIT License
 *
 * Copyright (c) 2023-2025 Volodya Lombrozo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.lombrozo.jsmith;

import com.github.lombrozo.jsmith.antlr.view.Text;
import java.util.logging.Logger;
import org.cactoos.io.ResourceOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.RepeatedTest;

/**
 * Tests for {@link RandomScript}.
 * @since 0.1
 */
final class RandomScriptTest {

    /**
     * Logger.
     */
    private final Logger logger = Logger.getLogger("RandomScriptTest");

    @RepeatedTest(10)
    void generatesSimpleGrammarSuccessfully() {
        final RandomScript script = new RandomScript(new ResourceOf("grammars/Simple.g4"));
        this.logger.info(String.format("Simple spec (lisp format): %s", script.specification()));
        final String example = script.generate("expr").output();
        this.logger.info(String.format("Generated simple example:%n%s%n", example));
        MatcherAssert.assertThat(
            "We expect that the example for Simple grammar will be generated successfully",
            example,
            Matchers.not(Matchers.emptyString())
        );
    }

    @RepeatedTest(10)
    void repeatsTheSameOutputForTheSameSeed() {
        final RandomScript script = new RandomScript(
            new Params(42L),
            new ResourceOf("grammars/Simple.g4")
        );
        MatcherAssert.assertThat(
            "We expect that generated output will be the same for the same seed",
            script.generate("expr").output(),
            Matchers.equalTo(script.generate("expr").output())
        );
    }

    @RepeatedTest(10)
    void generatesArithmeticGrammarSuccessfully() {
        final RandomScript script = new RandomScript(new ResourceOf("grammars/Arithmetic.g4"));
        this.logger.info(
            String.format("Arithmetic spec (lisp format): %s", script.specification())
        );
        final String example = script.generate("stat").output();
        this.logger.info(String.format("Generated Arithmetic example:%n%s%n", example));
        MatcherAssert.assertThat(
            "We expect that the example for Arithmetic grammar will be generated successfully",
            example,
            Matchers.not(Matchers.emptyString())
        );
    }

    @RepeatedTest(10)
    void generatesLetterGrammarUsingCombinedGrammar() {
        final RandomScript script = new RandomScript(
            new ResourceOf("grammars/separated/LettersParser.g4"),
            new ResourceOf("grammars/separated/LettersLexer.g4")
        );
        this.logger.info(String.format("Letters spec (lisp format): %s", script.specification()));
        final String example = script.generate("sentences").output();
        this.logger.info(String.format("Generated Letters example:%n%s%n", example));
        MatcherAssert.assertThat(
            "We expect that the example for Letter grammar will be generated successfully and what is the most important - the grammar combined from two separate files - LettersLexer and LettersParser",
            example,
            Matchers.not(Matchers.emptyString())
        );
    }

    @RepeatedTest(10)
    void generatesWordsAndNumbersGrammarUsingCombinedGrammar() {
        final RandomScript script = new RandomScript(
            new ResourceOf("grammars/separated/WordsAndNumbersLexer.g4"),
            new ResourceOf("grammars/separated/WordsAndNumbersParser.g4")
        );
        this.logger.info(
            String.format("WordsAndNumbers spec (lisp format): %s", script.specification())
        );
        final String example = script.generate("words").output();
        this.logger.info(String.format("Generated WordsAndNumbers example:%n%s%n", example));
        MatcherAssert.assertThat(
            "We expect that the example for WordsAndNumbers grammar will be generated successfully and what is the most important - the grammar combined from two separate files - WordsAndNumbersLexer and WordsAndNumbersParser",
            example,
            Matchers.not(Matchers.emptyString())
        );
    }

    @RepeatedTest(10)
    void generatesJsonGrammarSuccessfully() {
        final RandomScript script = new RandomScript(new ResourceOf("grammars/Json.g4"));
        this.logger.info(String.format("Json spec (lisp format): %s", script.specification()));
        final String example = script.generate("json").output();
        this.logger.info(String.format("Generated Json example:%n%s%n", example));
        MatcherAssert.assertThat(
            "We expect that the example for Json grammar will be generated successfully",
            example,
            Matchers.not(Matchers.emptyString())
        );
    }

    @RepeatedTest(10)
    void generatesXmlGrammarSuccessfully() {
        final RandomScript script = new RandomScript(
            new ResourceOf("grammars/separated/XMLLexer.g4"),
            new ResourceOf("grammars/separated/XMLParser.g4")
        );
        this.logger.info(String.format("XML spec (lisp format): %s", script.specification()));
        final Text document = script.generate("document");
        this.logger.info(String.format("XML document:%n%s%n", document.output()));
        final String example = document.output();
        this.logger.info(String.format("Generated tree:%n%s%n", example));
        MatcherAssert.assertThat(
            "We expect that the example for XML grammar will be generated successfully and what is the most important - the grammar combined from two separate files - XMLLexer.g4 and XMLParser.g4",
            example,
            Matchers.not(Matchers.emptyString())
        );
    }

    @RepeatedTest(10)
    void generatesCsv() {
        final RandomScript script = new RandomScript(new ResourceOf("grammars/CSV.g4"));
        this.logger.info(String.format("CSV spec (lisp format): %s", script.specification()));
        final String example = script.generate("csvFile").output();
        this.logger.info(String.format("Generated CSV example:%n%s%n", example));
        MatcherAssert.assertThat(
            "We expect that the example for CSV grammar will be generated successfully",
            example,
            Matchers.not(Matchers.emptyString())
        );
    }

    @RepeatedTest(10)
    void generatesJavaReducedGrammarSuccessfully() {
        final RandomScript script = new RandomScript(
            new ResourceOf("grammars/Java8ReducedLexer.g4"),
            new ResourceOf("grammars/Java8ReducedParser.g4")
        );
        this.logger.info(
            String.format("Java 8 Reduced spec (lisp format): %s", script.specification())
        );
        final Text document = script.generate("compilationUnit");
        this.logger.info(String.format("Java 8 Reduced:%n%s%n", document.output()));
        MatcherAssert.assertThat(
            "We expect that the example for Java 8 Reduced grammar will be generated successfully and what is the most important - the grammar combined from two separate files - Java8ReducedLexer.g4 and Java8ReducedParser.g4",
            document.output(),
            Matchers.not(Matchers.emptyString())
        );
    }
}
