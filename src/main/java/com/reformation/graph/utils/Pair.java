package com.reformation.graph.utils;

import java.util.Objects;

/** Key Value Pair */
public class Pair<K, V> {
    public final K key;
    public final V value;

    /**
     * Constructs a pair with a key and value.
     **
     * @param key The key of this pair.
     * @param value The value of this pair.
     */
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override public String toString() {
        return new StringBuilder()
            .append('[')
            .append(key)
            .append(", ")
            .append(value)
            .append(']')
            .toString();
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair)) { return false; }

        Pair<?, ?> pair = (Pair<?, ?>) o;
        if (key != null ? !key.equals(pair.key) : pair.key != null) return false;
        return value != null ? value.equals(pair.value) : pair.value == null;
    }

    @Override public int hashCode() {
        return Objects.hash(key, value);
    }
}
