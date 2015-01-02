package net.indf.collection.block;

import java.util.Iterator;

/**
 * Created by 정민철 on 2014-12-26.
 */
public interface Block extends Iterable<Integer> {
    int count();
    void add (int item);
    void remove(int item);
    boolean contains(int item);
}
