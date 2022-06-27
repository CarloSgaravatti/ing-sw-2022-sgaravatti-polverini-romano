package it.polimi.ingsw.utils;

import java.io.Serializable;

/**
 * Triplet is a utility class that permits to associate tre objects together
 *
 * @param <T> the first object class
 * @param <V> the second object class
 * @param <R> the third object class
 */
public class Triplet<T, V, R> implements Serializable {
    private T t;
    private V v;
    private R r;

    /**
     * Constructs a new Triplet which has the tre specified objects
     *
     * @param t the first object of the triplet
     * @param v the second object of the triplet
     * @param r the third object of the triplet
     */
    public Triplet(T t, V v, R r) {
        this.t = t;
        this.v = v;
        this.r = r;
    }

    /**
     * Constructs a new Triplet where the first two objects are (in order) the first object and the second object of the
     * specified pair and the third object is the specified one
     *
     * @param pair the first two objects of the triplet
     * @param r the third object of the triplet
     */
    public Triplet(Pair<T, V> pair, R r) {
        this.t = pair.getFirst();
        this.v = pair.getSecond();
        this.r = r;
    }

    /**
     * Constructs a new empty Triplet
     */
    public Triplet() {

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
     * Returns the third element of this
     *
     * @return the third element of this
     */
    public R getThird() {
        return r;
    }

    /**
     * Assign the specified value to the third element of this
     *
     * @param r the new value of the third element
     */
    public void setThird(R r) {
        this.r = r;
    }
}
