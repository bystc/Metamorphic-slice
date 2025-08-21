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
package com.github.lombrozo.jsmith.random;

import com.github.lombrozo.jsmith.antlr.Context;
import com.github.lombrozo.jsmith.antlr.rules.Rule;
import com.github.lombrozo.jsmith.antlr.rules.WrongPathException;
import com.github.lombrozo.jsmith.antlr.view.IntermediateNode;
import com.github.lombrozo.jsmith.antlr.view.Node;
import com.github.lombrozo.jsmith.antlr.view.TerminalNode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Rule that has several children.
 * WARNING: This is NOT a part of the ANTLR grammar!
 * @since 0.1
 */
final class Several implements Rule {

    /**
     * All children of the current node.
     */
    private final List<Rule> all;

    /**
     * Constructor.
     * @param all All children of the current node.
     */
    Several(final List<Rule> all) {
        this.all = all;
    }

    @Override
    public Rule parent() {
        throw new UnsupportedOperationException("'Several' node doesn't have a parent node");
    }

    @Override
    public Node generate(final Context context) throws WrongPathException {
        final Node result;
        if (this.all.isEmpty()) {
            result = new TerminalNode(this, "");
        } else {
            final List<Node> res = new ArrayList<>(0);
            for (final Rule rule : this.all) {
                res.add(rule.generate(context));
            }
            result = new IntermediateNode(this, res);
        }
        return result;
    }

    @Override
    public void append(final Rule rule) {
        throw new UnsupportedOperationException("'Several' node cannot add children");
    }

    @Override
    public String name() {
        return "several(not-a-rule)";
    }

    @Override
    public Rule copy() {
        return this.all.stream().map(Rule::copy)
            .collect(Collectors.collectingAndThen(Collectors.toList(), Several::new));
    }
}
