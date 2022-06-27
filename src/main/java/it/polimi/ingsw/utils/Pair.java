package it.polimi.ingsw.utils;

import java.io.Serializable;

/**
 * Pair is a utility class that permit to associate two objects.
 *
 * @param <T> the first object class
 * @param <V> the second object class
 */
public class Pair<T, V> implements Serializable {
    private T t;
    private V v;

    /**
     * Constructs a new empty pair
     */
    public Pair() {
        super();
    }

    /**
     * Construct a new Pair with the specified objects
     *
     * @param t the first element
     * @param v the second element
     */
    public Pair(T t, V v) {
        this();
        this.t = t;
        this.v = v;
    }

    /**
     * Returns the first element of this
     *
     * @return the first element of this
     */
    public T getFirst() {
        return t;
    }

    /**
     * Assign the specified value to the first element of this
     *
     * @param t the new value of the first element
     */
    public void setFirst(T t) {
        this.t = t;
    }

    /**
     * Returns the second element of this
     *
     * @return the second element of this
     */
    public V getSecond() {
        return v;
    }

    /**
     * Assign the specified value to the second element of this
     *
     * @param v the new value of the second element
     */
    public void setSecond(V v) {
        this.v = v;
    }

    /**
     * Returns a string representation of the pair, which is in the (t,v) form
     *
     * @return a string representation of the pair
     */
    @Override
    public String toString() {
        return "(" + t.toString() + "," + v.toString() + ")";
    }
}
