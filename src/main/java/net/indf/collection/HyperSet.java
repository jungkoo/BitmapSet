package net.indf.collection;

import java.util.*;

/**
 * Created by nhn on 2014-12-23.
 */
public class HyperSet implements Set<Integer> {
    final Map<Integer, Set<Integer>> blockDict = new TreeMap<Integer, Set<Integer>>();
    final int maxBitSize;

    public HyperSet(final int maxBitSize) {
        this.maxBitSize = maxBitSize;
    }

    public HyperSet() {
        this(65536);
    }

    @Override
    public int size() {
        if (blockDict.isEmpty())
            return 0;
        int count = 0;
        for(Set<Integer> s : blockDict.values()) {
            count += s.size();
        }
        return count;
    }

    @Override
    public boolean isEmpty() {
        if (blockDict.isEmpty())
            return true;
        for(Set<Integer> s : blockDict.values()) {
            if(!isEmpty())
                return false;
        }
        return true;
    }

    @Override
    public boolean contains(Object o) {
        if (!blockDict.containsKey(toIndex(o)))
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
        Set<Integer> temp = get(item);
        if(temp instanceof AloneSet && !temp.isEmpty()) {     //is not empty
            final Integer value = ((AloneSet) temp).get();
            set(item, new FixedBitSet(maxBitSize));
            temp = get(item);
            temp.add(toValue(value));
        }
        final boolean r = temp.add(toValue(item));
        return r;
    }

    @Override
    public boolean remove(Object item) {
        if (!blockDict.containsKey(toIndex(item)))
            return false;
        final Set<Integer> temp = get(item);
        final boolean ret = temp.remove(toValue((Integer)item));
        if(temp.isEmpty()) {
            blockDict.remove(toIndex(item));
        }
        return ret;
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

    private Set<Integer> get(Object o) {
        final int index = toIndex(o);
        if (!blockDict.containsKey(index)) {
            set(o, new AloneSet()); // 최초는 이렇다.
        }
        return blockDict.get(index);
    }

    private void set(Object o, Set<Integer> set) {
        final int index = toIndex(o);
        blockDict.put(index, set);
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
        blockDict.clear();
    }


    private class itr implements Iterator<Integer> {
        final Iterator<Map.Entry<Integer, Set<Integer>>> mainIterator = blockDict.entrySet().iterator();
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

        @Override
        public void remove() {
            final Integer r = next();
            if (blockDict.containsKey(toIndex(r)))
                get(r).remove(toValue(r));
        }

        private Integer updateCurrentValue() {
            //currentIterator is empty
            if ((currentIterator==null||!currentIterator.hasNext()) && mainIterator.hasNext()) {
                final Map.Entry<Integer, Set<Integer>> t = mainIterator.next();
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
