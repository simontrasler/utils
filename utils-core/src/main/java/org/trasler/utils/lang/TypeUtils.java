/**
 * The MIT License
 *
 * Copyright 2025 Simon Trasler
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.trasler.utils.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Simon Trasler
 */
public class TypeUtils {
    private static final Logger logger = LoggerFactory.getLogger(TypeUtils.class);

    public static Boolean makeBoolean(Object value) {
        switch (value) {
            case String s -> {
                if ("1".equals(s) || "true".equals(s) || "TRUE".equals(s)) {
                    return Boolean.TRUE;
                }
            }
            case Boolean b -> {
                return b;
            }
            case Integer i -> {
                return (i != 0);
            }
            case Long l -> {
                return (l != 0);
            }
            case Float f -> {
                return (f != 0.0);
            }
            case Double d -> {
                return (d != 0.0);
            }
            default -> { }
        }

        // Conversion not possible.
        return null;
    }

    public static Integer makeInteger(Object value) {
        switch (value) {
            case String s -> {
                try {
                    return Integer.valueOf(s);
                }
                catch (NumberFormatException e) {
                    // Fall through.
                    logger.warn("Could not convert from:{} to:{}", value.getClass(), Double.class);
                }
            }
            case Boolean b -> {
                return b ? 1 : 0;
            }
            case Integer i -> {
                return i;
            }
            case Long l -> {
                if (l >= Integer.MIN_VALUE && l <= Integer.MAX_VALUE) {
                    return l.intValue();
                }
            }
            case Float f -> {
                return f.intValue();
            }
            case Double d -> {
                return d.intValue();
            }
            default -> { }
        }

        // Conversion not possible.
        return null;
    }

    public static Long makeLong(Object value) {
        switch (value) {
            case String s -> {
                try {
                    return Long.valueOf(s);
                }
                catch (NumberFormatException e) {
                    // Fall through.
                    logger.warn("Could not convert from:{} to:{}", value.getClass(), Double.class);
                }
            }
            case Boolean b -> {
                return b ? 1L : 0L;
            }
            case Integer i -> {
                return i.longValue();
            }
            case Long l -> {
                return l;
            }
            case Float f -> {
                return f.longValue();
            }
            case Double d -> {
                return d.longValue();
            }
            default -> { }
        }

        // Conversion not possible.
        return null;
    }

    public static Float makeFloat(Object value) {
        switch (value) {
            case String s -> {
                try {
                    return Float.valueOf(s);
                }
                catch (NumberFormatException e) {
                    // Fall through.
                    logger.warn("Could not convert from:{} to:{}", value.getClass(), Float.class);
                }
            }
            case Boolean b -> {
                return b ? 1.0f : 0.0f;
            }
            case Integer i -> {
                return i.floatValue();
            }
            case Long l -> {
                return l.floatValue();
            }
            case Float f -> {
                return f;
            }
            case Double d -> {
                if (d >= Float.MIN_VALUE && d <= Float.MAX_VALUE) {
                    return d.floatValue();
                }
            }
            default -> { }
        }

        // Conversion not possible.
        return null;
    }

    public static Double makeDouble(Object value) {
        switch (value) {
            case String s -> {
                try {
                    return Double.valueOf(s);
                }
                catch (NumberFormatException e) {
                    // Fall through.
                    logger.warn("Could not convert from:{} to:{}", value.getClass(), Double.class);
                }
            }
            case Boolean b -> {
                return b ? 1.0 : 0.0;
            }
            case Integer i -> {
                return i.doubleValue();
            }
            case Long l -> {
                return l.doubleValue();
            }
            case Float f -> {
                return f.doubleValue();
            }
            case Double d -> {
                return d;
            }
            default -> { }
        }

        // Conversion not possible.
        return null;
    }

    public static String makeString(Object value) {
        switch (value) {
            case String s -> {
                return s;
            }
            case Boolean b -> {
                return b ? "1" : "0";
            }
            case Integer i -> {
                return i.toString();
            }
            case Long l -> {
                return l.toString();
            }
            case Float f -> {
                return f.toString();
            }
            case Double d -> {
                return d.toString();
            }
            default -> { }
        }

        // Conversion not possible.
        return null;
    }
}
