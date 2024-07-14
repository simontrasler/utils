package org.trasler.utils;

/**
 *
 * @author simon
 */
public class Counter {
    private int count = 0;

    public void increment() {
        count++;
    }

    public void increment(int add) {
        count += add;
    }

    public void compareAndExchange(int from, int to) {
        if (count == from) {
            count = to;
        }
    }

    public int count() {
        return count;
    }
}
