package net.indf.collection;

import java.util.Arrays;
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
	private long[] bitmap;
	private int count;
	private int bitSize;
	
	public FixedBitMapSet(final int bitSize) {
		this.bitmap 	= new long[(int)Math.ceil((double)bitSize/64)];
		this.bitSize 	= bitSize;		
	}
	
	public FixedBitMapSet() {
		this(65536);
	}
	
	@Override
	public int size() {
		return count;
	}

	@Override
	public boolean isEmpty() {		
		return count<=0;
	}

	@Override
	public boolean contains(Object o) {
		final int item = (Integer)o;
        final int arr_idx = (item-1) / 64;
        final int bit_offset = (item % 64) -1;
        if (item<0 || arr_idx>=bitmap.length)
            return false;
        return (bitmap[arr_idx] & (1 << bit_offset))>0;
	}

	private List<Integer> toList() {
		final List<Integer> list = new LinkedList<Integer>();
		for(int i=0; i<bitmap.length; i++) {
			long bitmask = 1;
			for(int j=1; j<=64; j++) {				
				if ((bitmap[i] & bitmask) != 0) {
					list.add(j+(i*64));
				}
				bitmask = bitmask << 1;
			}			
		}
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
        if (item<=0 || bitSize<item)
            throw new RuntimeException("item value error. (value="+item+")");
        final int arr_idx = (item-1) / 64;
        final int bit_offset = (item % 64) -1;
        final long bit_value = bitmap[arr_idx] | (1l << bit_offset);
        if (bit_value != bitmap[arr_idx]) {
        	bitmap[arr_idx] = bit_value;
            count+=1;
        }
        return true;
	}

	@Override
	public boolean remove(Object o) {
		final int item = (Integer)o;		
        if (item<=0 || bitSize<item || !contains(o))
            return false;
        
        final int arr_idx = (item-1) / 64;
        final int bit_offset = (item % 64) -1;
        final long bit_value = bitmap[arr_idx] & ~(1 << bit_offset);
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
		if (c instanceof FixedBitMapSet) {
			final FixedBitMapSet items = (FixedBitMapSet)c;
			
		    long[] src = bitmap;
		    long[] target = items.bitmap;
		    int cnt = size();
		    int maxBit = bitSize;
			if (bitSize < items.bitSize) {	
				src = Arrays.copyOf(items.bitmap, items.bitmap.length);
				target = bitmap;
				cnt = items.count;
				maxBit = items.bitSize;
	        }
                    
	        for(int end_idx = target.length-1; end_idx>=0; end_idx--) {
	            if (src[end_idx] != target[end_idx]) {
	                cnt += Long.bitCount(target[end_idx]) - Long.bitCount(src[end_idx] & target[end_idx]);
	                src[end_idx] = src[end_idx] | target[end_idx];
	            }
	        }
	        
	        this.bitmap = src;
	        this.count = cnt;
	        this.bitSize = maxBit;
			return true;
		}
		
		for(Object o: c) {
			add((Integer)o);
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean retainAll(Collection<?> c) {
		final FixedBitMapSet items;
		if (!(c instanceof FixedBitMapSet)) {
			items = new FixedBitMapSet(this.bitSize);		
			Iterator<Integer> iterator = (Iterator<Integer>) c.iterator();
			while(iterator.hasNext()) {
				items.add(iterator.next());
			}
		}else{
			items = (FixedBitMapSet)c;
		}
		
		if (bitSize != items.bitSize) {
            throw new IllegalArgumentException("bitSize is not equals. (src="+bitSize+", target="+items.bitSize+")");
        }
		int cnt = 0;
		for(int i=0; i< size(); i++) {
			bitmap[i] = bitmap[i] & items.bitmap[i];
			cnt += Long.bitCount(bitmap[i]);
		}
		this.count = cnt;			
		return true;

	}

	@Override
	public boolean removeAll(Collection<?> c) {
		if (c instanceof FixedBitMapSet) {
			final FixedBitMapSet items = (FixedBitMapSet)c;						
			int cnt = 0;
			for(int i=0; i< bitmap.length; i++) {
				if(items.bitmap.length>i)
					bitmap[i] = bitmap[i] & ~items.bitmap[i];				
				cnt += Long.bitCount(bitmap[i]);
			}
			this.count = cnt;			
			return true;
		}		
		for(Object o: c) {
			remove(o);
		}
		return true;
	}

	@Override
	public void clear() {
		if (count<=0) return;
        for(int i=bitmap.length-1; i>=0; i--)
            bitmap[i] = 0;
        count = 0;
	}


	public static void main(String...argv) {
		FixedBitMapSet set = new FixedBitMapSet(64);
		set.add(2);
		set.add(31);
		set.add(63);
		set.add(64);
		set.remove(60);
		System.out.println("size="+ set.size());

		Iterator<Integer> iter = set.iterator();
		while(iter.hasNext())
			System.out.println(iter.next());

		FixedBitMapSet set2 = new FixedBitMapSet(128);
		set2.add(4);
		set2.add(63);
		set2.add(1);


		set.addAll(set2); // 2 31 63 64   U 4 63 1 ==> 1 2 4 31 63 64

		System.out.println("contains test");
		System.out.println(set.contains(1));
		System.out.println(set.contains(63));
		System.out.println(set.contains(66));
		System.out.println(set.contains(4));
		System.out.println("contains all");
		System.out.println(set.containsAll(Arrays.asList(1,4,5)));
		System.out.println(set.containsAll(Arrays.asList(1,4,63)));


		System.out.println("=---------------=");
		Iterator<Integer> iter2 = set.iterator();
		while(iter2.hasNext())
			System.out.println(iter2.next());


		System.out.println("clear-");
		set.clear();
		System.out.println(set.size());
		for(int i=1; i<=128; i++) {
			set.add(i);
		}
		System.out.println(set.size());
		System.out.println(">>>>> remove");
		set.remove(11);
		System.out.println(set.size());// 128-1 = 127
		set.removeAll(Arrays.asList(11,12,55,1235));
		System.out.println(set.size()); // 125
		FixedBitMapSet s3 = new FixedBitMapSet(6);
		s3.add(5);
		s3.add(6);
		s3.add(3);
		set.removeAll(s3); //122
		System.out.println(set.size());

	}
}
