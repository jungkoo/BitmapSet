package net.indf.collection;

/**
 * 블럭관련 인터페이스
 *
 * Created by 정민철 on 2014-12-24.
 */
public interface DataBlock<E>  {
    public int size();
    public boolean contains(Object o);
    public boolean add(E item);
    public boolean remove(Object o);
}
