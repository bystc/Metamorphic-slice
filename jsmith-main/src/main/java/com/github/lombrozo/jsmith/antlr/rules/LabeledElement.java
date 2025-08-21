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
package com.github.lombrozo.jsmith.antlr.rules;

/**
 * LabeledElement rule.
 * The ANTLR grammar definition:
 * {@code
 * labeledElement
 *     : {@link Identifier} (ASSIGN | PLUS_ASSIGN) ({@link Atom} | {@link Block})
 *     ;
 * }
 * @since 0.1
 */
public final class LabeledElement extends Unimplemented {

    /**
     * Labeled element name.
     */
    private static final String ALIAS = "labeledElement";

    /**
     * Constructor.
     * @param parent Parent rule.
     */
    public LabeledElement(final Rule parent) {
        super(parent);
    }

    @Override
    public String name() {
        return LabeledElement.ALIAS;
    }

    @Override
    public Rule copy() {
        return new LabeledElement(this.parent());
    }

    /**
     * Check if the rule is labeled element.
     * @param rule Rule.
     * @return True if the rule is labeled element.
     */
    static boolean isLabeledElement(final Rule rule) {
        return LabeledElement.ALIAS.equals(rule.name());
    }
}
