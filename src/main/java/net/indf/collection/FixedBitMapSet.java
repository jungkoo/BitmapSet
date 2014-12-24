package net.indf.collection;

import java.util.*;

/**
 * 고정크기의 bitmask를 이용한 Set
 *
 * @author : deajang@gmail.com , 정민철
 */
public class FixedBitMapSet implements Set<Integer> {

	/**
	 * 최대값을 의미한다, bit의 갯수와 동일한 의미를 가진다.
	 */
    final private int limitValue;

	/**
	 * bit연산으로 표현된 데이터가 저장되는 값이다.
	 */
    private long[] bitmap;

	/**
	 * size() 를 위한 매번 카운팅하지 않고 변수값을 활용하는 구조로 사용한다.
	 */
	private int count;


	/**
	 * 고정크기의 bitmap을 생성한다.
	 * limitvalue는 최대값을 의미하기도 하고, bit의 갯수를 의미하기도한다.
	 * @param limitValue 사용할 최대값을 의미한다 (default : 65536)
	 */
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

	/**
	 * 데이터가 꽉채워졌을때, 데이터절약을 위해 bitmap변수를 null로 비웠는지를 의미한다.
	 * @return
	 */
    private boolean isPack() {
        return bitmap==null;
    }

	/**
	 * 데이터가 꽉찼을때 null로 채워 공간을 절약하는 기능을 담당한다.
	 */
    private void pack() {
        if(count>=limitValue)
            bitmap = null;
    }

	/**
	 * pack()된 데이터를 복구한다
	 * 다시말해, bitmap=null 로 된 데이터를 다시 배열데이터로 변경해준다.
	 */
    private void unpack() {
        if(isPack())
            full();
    }

	/**
	 * 허용된 값인지 체크한다. litmitValue범위 한도에 있는지를 체크한다.
	 * @param item
	 */
    private void valueCheck(int item) {
        if (item<=0 || item>limitValue) {
            throw new RuntimeException("value range is error. (1 ~ "+limitValue+", but "+item+")");
        }
    }

	/**
	 * 데이터형을 체크한다.
	 * @param o
	 */
    private void objectCheck(Object o) {
        if (o instanceof FixedBitMapSet) {
            final int targetLimitValue = FixedBitMapSet.class.cast(o).limitValue;
            if (limitValue != targetLimitValue)
                throw new RuntimeException("limit error (original="+limitValue+", target="+targetLimitValue+")");
        }
    }

	/**
	 * 비어있는지 유무를 의미한다.
	 * @return
	 */
	@Override
	public boolean isEmpty() {		
		return count<=0;
	}


	/**
	 * 해당값이 이미 존재하는지 체크한다
	 * @param o
	 * @return
	 */
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
			return true;
        }
        return false;
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

	protected void printDebug() {
		unpack();
		for(long l : bitmap) {
			System.out.println(Long.toBinaryString(l));
		}
		pack();
	}
}
