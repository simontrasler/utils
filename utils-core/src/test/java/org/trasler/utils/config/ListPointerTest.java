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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author Simon Trasler
 */
public class ListPointerTest {
    @Test
    public void testBounds() {
        ListPointer<Integer> listPointer = new ListPointer<>(List.of(1, 2, 3));

        assertEquals(Integer.valueOf(1), listPointer.peek());
        listPointer.next();

        assertEquals(Integer.valueOf(2), listPointer.peek());
        listPointer.next();

        assertEquals(Integer.valueOf(3), listPointer.peek());
        listPointer.next();

        try {
            listPointer.peek();
            fail("Expected exception not thrown");
        }
        catch (ArrayIndexOutOfBoundsException e) {
            // Expected.
        }
    }

    @Test
    public void testNavigation() {
        ListPointer<Integer> listPointer = new ListPointer<>(List.of(1, 2, 3));

        assertEquals(Integer.valueOf(1), listPointer.peek());
        listPointer.next();

        assertEquals(Integer.valueOf(2), listPointer.peek());
        listPointer.back();

        assertEquals(Integer.valueOf(1), listPointer.peek());
        listPointer.next();
        listPointer.next();
        
        assertEquals(Integer.valueOf(3), listPointer.peek());
    }
}
