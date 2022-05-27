package it.polimi.ingsw.utils;

import java.io.Serializable;

public class Pair<T, V> implements Serializable {
    private T t;
    private V v;

    public Pair() {
        super();
    }

    public Pair(T t, V v) {
        this();
        this.t = t;
        this.v = v;
    }

    public T getFirst() {
        return t;
    }

    public void setFirst(T t) {
        this.t = t;
    }

    public V getSecond() {
        return v;
    }

    public void setSecond(V v) {
        this.v = v;
    }

    @Override
    public String toString() {
        return "(" + t.toString() + "," + v.toString() + ")";
    }
}
