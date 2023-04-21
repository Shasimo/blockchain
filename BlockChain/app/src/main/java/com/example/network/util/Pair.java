package com.example.network.util;

public abstract class Pair<T1, T2> {
    transient T1 first;
    transient T2 second;

    public Pair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    protected T1 getFirst() {
        return first;
    }

    protected T2 getSecond() {
        return second;
    }

    protected void setFirst(T1 first) {
        this.first = first;
    }

    protected void setSecond(T2 second) {
        this.second = second;
    }
}
