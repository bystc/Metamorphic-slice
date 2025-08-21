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
package com.github.lombrozo.jsmith.antlr;

import com.github.lombrozo.jsmith.antlr.rules.AltList;
import com.github.lombrozo.jsmith.antlr.rules.Literal;
import com.github.lombrozo.jsmith.antlr.rules.WrongPathException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.IntStream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.RepeatedTest;

/**
 * Unparser test.
 * @since 0.1
 */
final class UnparserTest {

    @RepeatedTest(10)
    void generatesDifferentAlternativesForTheSameRule() throws WrongPathException {
        final Unparser unparser = new Unparser();
        final AltList alternatives = new AltList();
        IntStream.range(0, 5)
            .mapToObj(String::valueOf)
            .map(Literal::new)
            .forEach(alternatives::append);
        unparser.with("stat", alternatives);
        final Set<String> chosen = new LinkedHashSet<>(0);
        for (int index = 0; index < 50; ++index) {
            chosen.add(unparser.generate("stat", new Context()).text().output());
        }
        MatcherAssert.assertThat(
            "We expect that the result will contain all different alternatives",
            chosen.size(),
            Matchers.is(5)
        );
    }
}
