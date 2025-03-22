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
package org.trasler.utils.deciders;

import org.trasler.utils.deciders.EwmaDecider;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Simon Trasler
 */
public class EwmaDeciderTest {
    private static final double INITIAL_HALFLIFE = 10.0;

    @Test
    public void testInitializedOn() {
        EwmaDecider decider = new EwmaDecider.Builder()
                .withHalflife(INITIAL_HALFLIFE)
                .withInitialRate(1.0)
                .build();

        assertTrue(decider.decide());
    }

    @Test
    public void testInitializedOff() {
        EwmaDecider decider = new EwmaDecider.Builder()
                .withHalflife(INITIAL_HALFLIFE)
                .withInitialRate(0.0)
                .build();

        assertFalse(decider.decide());
    }

    @Test
    public void testHighMinimumRate() {
        EwmaDecider decider = new EwmaDecider.Builder()
                .withHalflife(INITIAL_HALFLIFE)
                .withInitialRate(0.0)
                .withMinimumRate(1.0)
                .build();

        assertTrue(decider.decide());
    }
}
