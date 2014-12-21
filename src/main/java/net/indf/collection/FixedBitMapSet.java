package net.indf.collection;

import java.util.*;

/**
 * 
 * @author : deajang@gmail.com
 */
public class FixedBitMapSet implements Set<Integer> {
    final private int limitValue;
    private long[] bitmap;
	private int count;

	
	public FixedBitMapSet(final int limitValue) {
		this.limitValue = limitValue;
        clear();
	}
	
	public FixedBitMapSet() {
		this(65536);
	}
	
	@Override
	public int size() {
		return count;
	}

    private boolean isPack() {
        return bitmap==null;
    }


    private void pack() {
        if(count>=limitValue)
            bitmap = null;
    }

    private void unpack() {
        if(isPack())
            full();
    }


    private void valueCheck(int item) {
        if (item<=0 || item>limitValue) {
            throw new RuntimeException("value range is error. (1 ~ "+limitValue+", but "+item+")");
        }
    }

    private void objectCheck(Object o) {
        if (o instanceof FixedBitMapSet) {
            final int targetLimitValue = FixedBitMapSet.class.cast(o).limitValue;
            if (limitValue != targetLimitValue)
                throw new RuntimeException("limit error (original="+limitValue+", target="+targetLimitValue+")");
        }
    }


	@Override
	public boolean isEmpty() {		
		return count<=0;
	}


	@Override
	public boolean contains(Object o) {
        final int item = (Integer)o;
        valueCheck(item);
        if (isPack()) {
            return true;
        }
        final int arr_idx = (item-1) / 64;
        final int bit_offset = 64 - (item % 64);
        return (bitmap[arr_idx] & (1l << bit_offset))>0;
	}

	private List<Integer> toList() {
		final List<Integer> list = new LinkedList<Integer>();
        unpack();
		for(int i=0; i<bitmap.length; i++) {
			for(int j=1; j<=64; j++) {
                long bitMask = 1l << (64-j);
				if ((bitmap[i] & bitMask) != 0) {
					list.add(j+(i*64));
				}
			}			
		}
        pack();
		return list;
	}
	
	
	@Override
	public Iterator<Integer> iterator() {
//        int v = 0;
//
//        for(int i=0; i<bitmap.length; i++) {
//            for(int j=1;j<=64; j++) {
//                long bitMask = 1l << (64-j);
//                if ((bitmap[i] & bitMask) != 0) {
//                    v = j+(i*64);
//                    break;
//                }
//            }
//        }
//        final int pv = v;
//        final int pi = v / 64;
//        final int pj = (v+1) % 64;
//        return new Iterator<Integer>() {
//            private int preValue = pv;
//            private int arrIdx = pi;
//            private int bitIdx = pj;
//
//            @Override
//            public boolean hasNext() {
//                return preValue>0;
//            }
//
//            @Override
//            public Integer next() {
//                if (!hasNext()) {
//                    throw new NullPointerException("next() item is empty.");
//                }
//                final int nextValue = preValue;
//                preValue = 0;
//                for(; arrIdx<bitmap.length; arrIdx++) {
//                    for(;bitIdx<=64; bitIdx++) {
//                        long bitMask = 1l << (64-bitIdx);
//                        if ((bitmap[arrIdx] & bitMask) != 0) {
//                            preValue = bitIdx+(arrIdx*64);
//                            break;
//                        }
//                    }
//                }
//                return nextValue;
//            }
//
//            @Override
//            public void remove() {
//                throw new RuntimeException();
//            }
//        };
		return toList().iterator();
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
        valueCheck(item);
        if(isPack()) {
            return true;
        }

        final int arr_idx = (item-1) / 64;
        final int bit_offset = 64 - (item % 64);
        final long bit_value = bitmap[arr_idx] | (1l << bit_offset);
        if (bit_value != bitmap[arr_idx]) {
        	bitmap[arr_idx] = bit_value;
            count+=1;
        }
        pack();
        return true;
	}

	@Override
	public boolean remove(Object o) {
		final int item = (Integer)o;
        valueCheck(item);
        unpack();

        final int arr_idx = (item-1) / 64;
        final int bit_offset = 64 - (item % 64);
        final long bit_value = bitmap[arr_idx] & ~(1l << bit_offset);
        if (bit_value != bitmap[arr_idx]) {
        	bitmap[arr_idx] = bit_value;
            count-=1;
        }
        return true;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for(Object o: c) {
			if(!contains(o))
				return false;
		}
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends Integer> c) {
        objectCheck(c);
        if (!(c instanceof FixedBitMapSet)) {
            for(Object o: c)
                add((Integer)o);
            return true;
        }

        if (isPack()) return true;
        final FixedBitMapSet items = (FixedBitMapSet)c;
        if (items.isPack()) { // 다 엎어씌울때
            fullAndPack();
            return true;
        }
        final long[] target = items.bitmap;
        for(int end_idx = target.length-1; end_idx>=0; end_idx--) {
            if (bitmap[end_idx] != target[end_idx]) {
                count += Long.bitCount(target[end_idx]) - Long.bitCount(bitmap[end_idx] & target[end_idx]);
                bitmap[end_idx] = bitmap[end_idx] | target[end_idx];
            }
        }
        pack();
        return true;

	}


	@Override
	public boolean retainAll(Collection<?> c) {
        objectCheck(c);
        final FixedBitMapSet target;
        if (c instanceof FixedBitMapSet) {
            target = FixedBitMapSet.class.cast(c);
        }else{
            target = new FixedBitMapSet(this.limitValue);
            for(final Object num : c) {
                target.add((Integer)num);
            }
        }
        unpack();
        int cnt = 0;
        for(int i=0; i<bitmap.length; i++) {
            bitmap[i] = bitmap[i] & target.bitmap[i];
            cnt += Long.bitCount(bitmap[i]);
        }
        this.count = cnt;
        pack();
        return true;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
        objectCheck(c);
        if (!(c instanceof FixedBitMapSet)) {
            for(Object o: c) {
                remove(o);
            }
            return true;
        }

        final FixedBitMapSet items = (FixedBitMapSet)c;
        if(isEmpty())
            return true;
        if(items.isEmpty())
            return true;
        if(items.isPack()) {
            clear();
            return true;
        }
        unpack();
        int cnt = 0;
        for(int i=0; i< bitmap.length; i++) {
            if(items.bitmap.length>i)
                bitmap[i] = bitmap[i] & ~items.bitmap[i];
            cnt += Long.bitCount(bitmap[i]);
        }
        this.count = cnt;
        return true;
	}

	@Override
	public void clear() {
        count = 0;
        if(isPack()) {
            bitmap 	= new long[(int)Math.ceil((double)limitValue/64)];
            return;
        }
        for(int i=bitmap.length-1; i>=0; i--)
            bitmap[i] = 0;
	}

    private void full() {
        count = limitValue;
        if(isPack()) {
            bitmap = new long[(int)Math.ceil((double)limitValue/64)];
        }
        int lastIndex = bitmap.length - 1;
        bitmap[lastIndex--] = ~01 << (limitValue%64);
        for( ;lastIndex>=0; lastIndex--) {
            bitmap[lastIndex] = ~0l;
        }
    }

    private void fullAndPack() {
        count = limitValue;
        bitmap = null;
    }
}
