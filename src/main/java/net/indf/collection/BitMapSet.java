package net.indf.collection;

import java.util.*;

/**
 * Created by nhn on 2014-12-23.
 */
public class BitMapSet implements Set<Integer> {
    final Map<Integer, FixedBitMapSet> fixedBitMapSetMap = new TreeMap<Integer, FixedBitMapSet>();
    final int maxBitSize;

    public BitMapSet(final int maxBitSize) {
        this.maxBitSize = maxBitSize;
    }

    public BitMapSet() {
        this(65536);
    }

    @Override
    public int size() {
        if (fixedBitMapSetMap.isEmpty())
            return 0;
        int count = 0;
        for(FixedBitMapSet s : fixedBitMapSetMap.values()) {
            count += s.size();
        }
        return count;
    }

    @Override
    public boolean isEmpty() {
        if (fixedBitMapSetMap.isEmpty())
            return true;
        for(FixedBitMapSet s : fixedBitMapSetMap.values()) {
            if(!isEmpty())
                return false;
        }
        return true;
    }

    @Override
    public boolean contains(Object o) {
        if (!fixedBitMapSetMap.containsKey(toIndex(o)))
            return false;
        return get(o).contains(toValue((Integer) o));
    }

    public List<Integer> toList() {
        final List<Integer> list = new LinkedList<Integer>();
        final Iterator<Integer> it = iterator();
        while(it.hasNext()) {
            list.add(it.next());
        }
        return list;
    }

    @Override
    public Iterator<Integer> iterator() {
        return new itr();
    }

    @Override
    public Object[] toArray() {
        return toList().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return toList().toArray(a);
    }

    @Override
    public boolean add(Integer item) {
        return get(item).add(toValue(item));
    }

    @Override
    public boolean remove(Object item) {
        if (!fixedBitMapSetMap.containsKey(toIndex(item)))
            return false;
        return get(item).remove(toValue((Integer)item));
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for(final Integer item : (Collection<? extends Integer>)c) {
            if (!contains(item))
                return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends Integer> c) {
        for(final Integer item : c) {
            add(item);
        }
        return true;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        final Iterator<Integer> it = iterator();
        while(it.hasNext()) {
            if(c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        for(final Integer item : (Collection<? extends Integer>)c) {
            remove(item);
        }
        return true;
    }

    private FixedBitMapSet get(Object o) {
        final int index = toIndex(o);
        if (!fixedBitMapSetMap.containsKey(index)) {
            fixedBitMapSetMap.put(index, new FixedBitMapSet(maxBitSize));
        }
        return fixedBitMapSetMap.get(index);
    }
    private int toIndex(Object o) {
        if (!(o instanceof Integer))
            throw new IllegalArgumentException("No Integer type.");
        int value = Integer.class.cast(o);
        return (value-1) / maxBitSize;
    }

    private int toValue(int value) {
        final int v = value % (maxBitSize);
        return v==0 ? maxBitSize : v;
    }

    @Override
    public void clear() {
        fixedBitMapSetMap.clear();
    }

    protected void printDebug() {
        for(Map.Entry<Integer,FixedBitMapSet> e : fixedBitMapSetMap.entrySet()) {
            System.out.println(">> index: " + e.getKey());
            e.getValue().printDebug();
        }
    }

    private class itr implements Iterator<Integer> {
        final Iterator<Map.Entry<Integer, FixedBitMapSet>> mainIterator = fixedBitMapSetMap.entrySet().iterator();
        Iterator<Integer> currentIterator = null;
        int currentIndex = -1;
        Integer current = updateCurrentValue();

        @Override
        public boolean hasNext() {
            return current!=null;
        }

        @Override
        public Integer next() {
            final Integer r = currentIndex * maxBitSize + current;
            updateCurrentValue();
            return r;
        }

        private Integer updateCurrentValue() {
            //currentIterator is empty
            if ((currentIterator==null||!currentIterator.hasNext()) && mainIterator.hasNext()) {
                final Map.Entry<Integer, FixedBitMapSet> t = mainIterator.next();
                currentIndex = t.getKey();
                currentIterator = t.getValue().iterator();
            }

            if (currentIterator!=null && currentIterator.hasNext()) {
                current = currentIterator.next();
                return current;
            }
            current = null;
            return null;
        }
    }
}
