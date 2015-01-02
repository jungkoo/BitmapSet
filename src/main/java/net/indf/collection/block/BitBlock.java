package net.indf.collection.block;
import java.util.Iterator;

import static net.indf.collection.block.Blocks.BLOCK_LIMITED_SIZE;
/**
 * Created by 정민철 on 2014-12-30.
 */
public class BitBlock implements Block {
    private long[] bits;
    
    public BitBlock() {
        bits = new long[(int)Math.ceil((double)BLOCK_LIMITED_SIZE/64)];
    }
    
    @Override
    public int count() {
        int sum = 0;
        for(long num : bits) {
            sum += Long.bitCount(num);
        }
        return sum;
    }

    @Override
    public void add(int item) {
        final int arr_idx = (item-1) / 64;
        final int bit_offset = 64 - (item % 64);
        final long bit_value = bits[arr_idx] | (1l << bit_offset);
        if (bit_value != bits[arr_idx]) {
            bits[arr_idx] = bit_value;
        }
    }

    @Override
    public void remove(int item) {
        final int arr_idx = (item-1) / 64;
        final int bit_offset = 64 - (item % 64);
        final long bit_value = bits[arr_idx] & ~(1l << bit_offset);
        if (bit_value != bits[arr_idx]) {
            bits[arr_idx] = bit_value;
            return;
        }
    }

    @Override
    public boolean contains(int item) {
        final int arr_idx = (item-1) / 64;
        final int bit_offset = 64 - (item % 64);
        return (bits[arr_idx] & (1l << bit_offset))!=0;
    }


    @Override
    public Iterator<Integer> iterator() {
        return new Iter();
    }
    
    class Iter implements Iterator<Integer> {
        int currentArrIndex = 0;
        int currentBitSeq   = 1; // 1 ~ 64
        Integer current = updateCurrentValue();
        int nextItem;
        boolean isOver;

        @Override
        public boolean hasNext() {
            return current!=null;
        }

        @Override
        public Integer next() {
            nextItem = current;
            updateCurrentValue();
            return nextItem;
        }


        private Integer updateCurrentValue() {
            if (isOver==false) {
                if(currentBitSeq>64) {
                    currentBitSeq = 1;
                    currentArrIndex += 1;
                }
                if(currentArrIndex<bits.length) {
                    long bitMask = 1l << (64-currentBitSeq);
                    if ((bits[currentArrIndex] & bitMask) != 0) {
                        current = currentBitSeq + (currentArrIndex*64);
                        currentBitSeq += 1;
                        return current;
                    }else{
                        currentBitSeq += 1;
                        return updateCurrentValue();
                    }
                }
            }
            isOver = true;
            current = null;
            return null;
        }
    }
}
