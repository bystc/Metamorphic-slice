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

import com.github.lombrozo.jsmith.antlr.Context;
import com.github.lombrozo.jsmith.antlr.Unparser;
import java.util.ArrayList;
import java.util.stream.Collectors;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link Ruleref}.
 *
 * @since 1.0
 */
final class RulerefTest {

    @Test
    void generatesRuleReferenceByUsingLink() throws WrongPathException {
        final String ref = "reference";
        final String expected = "Linked rule value";
        final ParserRuleSpec rule = new ParserRuleSpec(ref, new Empty());
        rule.append(new Literal(expected));
        final Unparser unparser = new Unparser();
        unparser.with(ref, rule);
        final Ruleref ruleref = new Ruleref(new Empty(), ref, unparser);
        MatcherAssert.assertThat(
            String.format(
                "We expect that %s will invoke the linked rule to generate output, but it didn't happen",
                ruleref
            ),
            ruleref.generate(new Context()).text().output(),
            Matchers.equalTo(expected)
        );
    }

    @Test
    void generatesRuleReferenceByUsingArgActionBlock() throws WrongPathException {
        final String ref = "reference";
        final ArrayList<String> expected = new ArrayList<>(2);
        expected.add("Linked rule value(1)");
        expected.add("Linked rule value(2)");
        final ArgActionBlock block = new ArgActionBlock(
            new Root(),
            expected.stream().map(Literal::new).collect(Collectors.toList())
        );
        final Unparser unparser = new Unparser();
        unparser.with(ref, block);
        final Ruleref ruleref = new Ruleref(new Empty(), ref, unparser);
        MatcherAssert.assertThat(
            String.format(
                "We expect that %s will invoke the linked argActionBlock to generate output",
                ruleref
            ),
            ruleref.generate(new Context()).text().output(),
            Matchers.equalTo(block.generate(new Context()).text().output())
        );
    }

    @Test
    void generatesRuleReferenceByUsingElementOptions() throws WrongPathException {
        final String ref = "reference";
        final ElementOptions options = new ElementOptions(new Empty());
        final String expected = "Linked rule value";
        final ElementOption element = new ElementOption(options);
        element.append(new Identifier(element, expected));
        options.append(element);
        final Unparser unparser = new Unparser();
        unparser.with(ref, options);
        final Ruleref ruleref = new Ruleref(new Empty(), ref, unparser);
        MatcherAssert.assertThat(
            String.format(
                "We expect that %s will invoke the linked elementOptions to generate output",
                ruleref
            ),
            ruleref.generate(new Context()).text().output(),
            Matchers.equalTo(expected)
        );
    }
}
