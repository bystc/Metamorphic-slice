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

import java.util.HashMap;
import org.cactoos.io.ResourceOf;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;

/**
 * Random Java class.
 * @since 0.1
 */
public final class RandomJavaClass {

    /**
     * ANTLR parser grammar for Java.
     */
    private final String parser;

    /**
     * ANTLR lexer grammar for Java.
     */
    private final String lexer;

    /**
     * Start rule.
     */
    private final String rule;

    /**
     * Generation params.
     */
    private final Params params;

    /**
     * Default constructor.
     */
    public RandomJavaClass() {
        this(new Params());
    }

    /**
     * Constructor.
     * @param seed Seed.
     */
    public RandomJavaClass(final long seed) {
        this(new Params(seed));
    }

    /**
     * Constructor.
     * @param params Generation parameters.
     */
    public RandomJavaClass(final Params params) {
        this(
            "grammars/Java8ReducedParser.g4",
            "grammars/Java8ReducedLexer.g4",
            "compilationUnit",
            params
        );
    }

    /**
     * Constructor.
     * @param parser Parser.
     * @param lexer Lexer.
     * @param rule Rule.
     */
    public RandomJavaClass(
        final String parser,
        final String lexer,
        final String rule
    ) {
        this(parser, lexer, rule, new Params());
    }

    /**
     * Constructor.
     * @param parser Parser.
     * @param lexer Lexer.
     * @param rule Rule.
     * @param params Params.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public RandomJavaClass(
        final String parser,
        final String lexer,
        final String rule,
        final Params params
    ) {
        this.parser = parser;
        this.lexer = lexer;
        this.rule = rule;
        this.params = params;
    }

    /**
     * Source code of the class.
     * @return Source code of the class.
     */
    public String src() {
        final String output = new RandomScript(
            this.params,
            new ResourceOf(this.parser),
            new ResourceOf(this.lexer)
        ).generate(this.rule).output();
        try {
            final CodeFormatter formatter = ToolFactory.createCodeFormatter(new HashMap(0));
            final TextEdit format = formatter.format(
                CodeFormatter.K_COMPILATION_UNIT, output,
                0,
                output.length(),
                0,
                System.lineSeparator()
            );
            final IDocument document = new Document(output);
            final String result;
            if (format != null) {
                format.apply(document);
                result = document.get();
            } else {
                result = output;
            }
            return result;
        } catch (final BadLocationException exception) {
            throw new IllegalStateException(
                String.format("Failed to format source code %n%s%n", output), exception
            );
        }
    }
}
