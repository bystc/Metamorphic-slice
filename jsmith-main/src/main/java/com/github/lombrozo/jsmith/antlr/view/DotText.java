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
package com.github.lombrozo.jsmith.antlr.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Tree in DOT format.
 * You can read more about the format
 * <a href="https://graphviz.gitlab.io">here</a>
 * @since 0.1
 */
public final class DotText implements Text {

    /**
     * Origin text.
     */
    private final Text origin;

    /**
     * Filters.
     */
    private final List<? extends Predicate<Text>> filters;

    /**
     * Constructor.
     * @param origin Origin text.
     * @param filters Filters.
     */
    @SafeVarargs
    public DotText(final Text origin, final Predicate<Text>... filters) {
        this(origin, Arrays.asList(filters));
    }

    /**
     * Constructor.
     * @param origin Origin text.
     */
    DotText(final Text origin) {
        this(origin, new ArrayList<>(0));
    }

    /**
     * Constructor.
     * @param origin Origin text.
     * @param filters Filters.
     */
    private DotText(final Text origin, final List<? extends Predicate<Text>> filters) {
        this.origin = origin;
        this.filters = filters;
    }

    @Override
    public List<Text> children() {
        return this.origin.children();
    }

    @Override
    public String output() {
        final StringBuilder builder = new StringBuilder(
            "digraph JsmithGenerativeTree{\n// Node labels\n"
        );
        final Map<String, String> labels = new HashMap<>(0);
        final List<String> leafs = new ArrayList<>(0);
        this.travers(new PlainText("root", "root"), this.origin, builder, labels, leafs);
        labels.forEach(
            (key, value) -> builder.append(
                String.format(
                    "\"#%s\" [label=\"%s\" tooltip=\"%s\"];\n",
                    key,
                    value,
                    labels.get(key)
                )
            )
        );
        return builder.append(
            leafs.stream()
                .map(s -> String.format("  \"#%s\"", s))
                .collect(
                    Collectors.joining(
                        "\n",
                        " subgraph cluster_leafs {\nrank=same\n",
                        "\n  }\n"
                    )
                )
        ).append('}').toString();
    }

    @Override
    public Labels labels() {
        return this.origin.labels();
    }

    /**
     * Traverses the tree.
     * @param parent Parent node.
     * @param current Current node.
     * @param builder Builder where to append transitions.
     * @param labels All node labels.
     * @param leafs All leafs.
     * @checkstyle ParameterNumberCheck (10 lines)
     */
    private void travers(
        final Text parent,
        final Text current,
        final StringBuilder builder,
        final Map<String, String> labels,
        final List<String> leafs
    ) {
        if (this.filters.stream().anyMatch(filter -> filter.test(current))) {
            for (final Text child : current.children()) {
                this.travers(parent, child, builder, labels, leafs);
            }
        } else {
            final int pnumber = System.identityHashCode(parent);
            final int cnumber = System.identityHashCode(current);
            labels.put(String.valueOf(cnumber), current.labels().author());
            labels.put(String.valueOf(pnumber), parent.labels().author());
            builder.append(
                String.format(
                    "\"#%d\" -> \"#%d\" [tooltip=\"%s\"];\n",
                    pnumber,
                    cnumber,
                    String.format("%s -> %s", parent.labels().author(), current.labels().author())
                )
            );
            if (current.children().isEmpty()) {
                final String leaf = current.output();
                final String nleaf = UUID.randomUUID().toString();
                leafs.add(nleaf);
                labels.put(nleaf, leaf);
                builder.append(
                    String.format(
                        "\"#%d\" -> \"#%s\" [label=\"%s\" tooltip=\"%s\"];\n",
                        cnumber,
                        nleaf,
                        String.format(
                            "%s -> %s -> %s",
                            parent.labels().author(),
                            current.labels().author(),
                            leaf
                        ),
                        String.format(
                            "%s -> %s -> %s",
                            parent.labels().author(),
                            current.labels().author(),
                            leaf
                        )
                    )
                );
            }
            for (final Text child : current.children()) {
                this.travers(current, child, builder, labels, leafs);
            }
        }
    }

}
