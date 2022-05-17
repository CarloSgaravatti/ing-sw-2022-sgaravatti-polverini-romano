package it.polimi.ingsw.utils;

import java.io.Serializable;

public class Triplet<T, V, R> implements Serializable {
    private T t;
    private V v;
    private R r;

    public Triplet(T t, V v, R r) {
        this.t = t;
        this.v = v;
        this.r = r;
    }

    public Triplet(Pair<T, V> pair, R r) {
        this.t = pair.getFirst();
        this.v = pair.getSecond();
        this.r = r;
    }

    public Triplet() {

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

    public R getThird() {
        return r;
    }

    public void setThird(R r) {
        this.r = r;
    }
}
