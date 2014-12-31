package net.indf.collection.net.indf.collection.block;

/**
 * Created by 정민철 on 2014-12-26.
 */
public interface Block<T> {
    int count();
    void add (T item);
    void remove(T item);
    boolean contains(T item);
}
