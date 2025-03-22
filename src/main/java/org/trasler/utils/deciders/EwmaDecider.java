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

import org.trasler.utils.lang.Randomizer;

/**
 *
 * @author Simon Trasler
 */
public class EwmaDecider implements Decider {
    private final double alpha;

    private double rate;

    private double minimumRate;

    private EwmaDecider(Builder builder) {
        this.alpha = Math.pow(2.0, -1.0 / builder.halflife);
        this.rate = builder.initialRate;
        this.minimumRate = builder.minimumRate;
    }

    @Override
    public boolean decide() {
        return Randomizer.decide(Math.max(rate, minimumRate));
    }

    @Override
    public void onSuccess() {
        updateRate(1.0);
    }

    @Override
    public void onFailure() {
        updateRate(0.0);
    }

    private void updateRate(double input) {
        rate = (1.0 - alpha) * rate + alpha * input;
    }

    public static class Builder {
        /**
         * The half-life of any observation, measured in observations. In other
         * words, this is the number of observations it will take for a given
         * observation to lose half its impact. If a large number, the effect of
         * new observations will decay slowly: the Decider will have a long
         * memory. It must be a positive number.
         */
        private double halflife;

        /**
         * The sample rate to start with. For example, if using this class in a
         * decision to call a dependent system, typically the caller will want
         * to assume success and iterate from there. In this case, the initial
         * rate should be 1.0. Defaults to 0.0.
         */
        private double initialRate;

        /**
         * The minimum rate for a positive decision. Without this, a system with
         * a high rate of failure can get stuck in a doom loop, where the chance
         * of a positive decision becomes tiny. Defaults to 0.0.
         */
        private double minimumRate;

        public Builder withHalflife(double halflife) {
            this.halflife = halflife;
            return this;
        }

        public Builder withInitialRate(double initialRate) {
            this.initialRate = initialRate;
            return this;
        }

        public Builder withMinimumRate(double minimumRate) {
            this.minimumRate = minimumRate;
            return this;
        }

        public EwmaDecider build() {
            if (halflife > 0.0) {
                return new EwmaDecider(this);
            } else {
                throw new IllegalArgumentException("Halflife must be a positive number");
            }
        }
    }
}
