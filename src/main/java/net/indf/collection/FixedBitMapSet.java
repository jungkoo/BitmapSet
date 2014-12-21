package net.indf.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
        System.out.println(Long.toBinaryString(bitmap[arr_idx]));
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
        throw new RuntimeException("sorry.");
//        if (c instanceof FixedBitMapSet) {
//            final FixedBitMapSet items = FixedBitMapSet.class.cast(c);
//            if (limitValue != items.limitValue) {
//                throw new IllegalArgumentException("bitSize is not equals. (src="+limitValue+", target="+items.limitValue+")");
//            }
//
//            return true;
//        }
//
//
//		final FixedBitMapSet items;
//		if (!(c instanceof FixedBitMapSet)) {
//			items = new FixedBitMapSet(this.limitValue);
//			Iterator<Integer> iterator = (Iterator<Integer>) c.iterator();
//			while(iterator.hasNext()) {
//				items.add(iterator.next());
//			}
//		}else{
//			items = (FixedBitMapSet)c;
//		}
//
//		if (limitValue != items.limitValue) {
//            throw new IllegalArgumentException("bitSize is not equals. (src="+limitValue+", target="+items.limitValue+")");
//        }
//		int cnt = 0;
//		for(int i=0; i< size(); i++) {
//			bitmap[i] = bitmap[i] & items.bitmap[i];
//			cnt += Long.bitCount(bitmap[i]);
//		}
//		this.count = cnt;
//		return true;

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
