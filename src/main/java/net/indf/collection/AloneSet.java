package net.indf.collection;

import java.util.*;

/**
 * 1개의 값만 가지는 Set이다.
 *
 * tip: 숫자의 빈도 파편화가 심한 경우 단일값에서 메모리 절약을 위한 구현체이다.
 * Created by 정민철 on 2014-12-24.
 */
public class AloneSet implements Set<Integer>  {
    private Integer value;

    public Integer get() {
        return value;
    }
    
    @Override
    public int size() {
        return value==null?0:1;
    }

    @Override
    public boolean isEmpty() {
        return size()<=0;
    }

    @Override
    public boolean contains(Object o) {
        return value!=null && value.equals(o);
    }

    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {
            private Integer current = value;

            @Override
            public boolean hasNext() {
                return current!=null;
            }

            @Override
            public Integer next() {
                final Integer t = current;
                current = null;
                return t;
            }

            @Override
            public void remove() {
                value = null;
            }
        };
    }

    @Override
    public Object[] toArray() {
        if(isEmpty())
            return new Object[0];
        else
            return new Object[]{value};
    }

    @Override
    public <T> T[] toArray(T[] a) {
        if (a.length < size())
            return (T[]) Arrays.copyOf(new Object[]{value}, size(), a.getClass());
        System.arraycopy(new Object[]{value}, 0, a, 0, size());
        if (a.length > size())
            a[size()] = null;
        return a;
    }

    @Override
    public boolean add(Integer integer) {
        if (isEmpty()) {
            value = integer;
            return true;
        }
        throw new IllegalArgumentException();
    }

    @Override
    public boolean remove(Object o) {
        if (!isEmpty() && value.equals(o)) {
            value = null;
            return true;
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return c.contains(value);
    }

    @Override
    public boolean addAll(Collection<? extends Integer> c) {
        if(isEmpty() && c.size()==1) {
            for(Integer v : c) {
                value = v;
                return true;
            }
        }
        throw new IllegalArgumentException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (c.contains(value)) {
            return true;
        }
        value = null;
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c.contains(value)) {
            value = null;
            return true;
        }
        return false;
    }

    @Override
    public void clear() {
        value = null;
    }
}
