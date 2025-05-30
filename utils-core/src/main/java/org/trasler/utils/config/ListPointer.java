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
package org.trasler.utils.config;

import java.util.List;

/**
 *
 * @author Simon Trasler
 * @param <T>
 */
public class ListPointer<T> {
    private final List<T> list;
    private int index;

    public ListPointer(List<T> list) {
        this.list = list;
        this.index = -1;
    }

    public ListPointer(T[] array) {
        this.list = List.of(array);
        this.index = -1;
    }

    public T peek() {
        if (list.size() <= index + 1) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return list.get(index + 1);
    }

    public ListPointer<T> next() {
        if (list.size() <= index + 1) {
            throw new ArrayIndexOutOfBoundsException();
        }

        index++;

        return this;
    }

    public boolean hasNext() {
        return (list.size() > index + 1);
    }

    public void back() {
        if (index < 0) {
            throw new ArrayIndexOutOfBoundsException();
        }

        index--;
    }
}
