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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Attributes.
 * @since 0.1
 */
public final class Attributes {

    /**
     * Comment to activate this rule.
     */
    private static final String TYPE = "$jsmith-type";

    /**
     * Comment to activate this rule.
     */
    private static final String TARGET = "$jsmith-var-target";

    /**
     * Attributes.
     */
    private final Map<String, String> attrs;

    /**
     * Default constructor.
     */
    public Attributes() {
        this(new HashMap<>(0));
    }

    /**
     * Constructor.
     * @param attributes Attributes.
     */
    public Attributes(final Map<String, String> attributes) {
        this.attrs = attributes;
    }

    /**
     * Add an attribute.
     * @param other Other attributes.
     * @return New attributes.
     */
    public Attributes add(final Attributes other) {
        final Map<String, String> res = this.copy();
        res.putAll(other.attrs);
        return new Attributes(res);
    }

    /**
     * Variable target name.
     * @return Variable name.
     */
    public Optional<String> variableTarget() {
        return Optional.ofNullable(this.attrs.get(Attributes.TARGET));
    }

    /**
     * Current type.
     * @return Type name.
     */
    public Optional<String> currentType() {
        return Optional.ofNullable(this.attrs.get(Attributes.TYPE));
    }

    /**
     * With target.
     * @param target Variable name.
     * @return The same attributes.
     */
    public Attributes withTarget(final String target) {
        return this.with(Attributes.TARGET, target);
    }

    /**
     * With type.
     * @param type Type name.
     * @return The same attributes.
     */
    public Attributes withType(final String type) {
        return this.with(Attributes.TYPE, type);
    }

    /**
     * With.
     * @param key Key.
     * @param value Value.
     * @return New attributes.
     */
    private Attributes with(final String key, final String value) {
        final Map<String, String> copy = this.copy();
        copy.put(key, value);
        return new Attributes(copy);
    }

    /**
     * Copy attributes.
     * @return New attributes.
     */
    private Map<String, String> copy() {
        return new HashMap<>(this.attrs);
    }
}
