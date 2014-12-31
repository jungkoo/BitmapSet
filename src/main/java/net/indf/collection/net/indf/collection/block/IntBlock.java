package net.indf.collection.net.indf.collection.block;

/**
 * Created by 정민철 on 2014-12-30.
 */
public class IntBlock implements Block<Integer> {
    private int count;
    private long[] bits;
    
    public IntBlock() {
        bits = new long[(int)Math.ceil((double)BLOCK_SIZE/64)];
    }
    
    
    @Override
    public int count() {
        return count;
    }

    @Override
    public void add(Integer item) {
        final int arr_idx = (item-1) / 64;
        final int bit_offset = 64 - (item % 64);
        final long bit_value = bits[arr_idx] | (1l << bit_offset);
        if (bit_value != bits[arr_idx]) {
            bits[arr_idx] = bit_value;
            count+=1;
        }
    }

    @Override
    public void remove(Integer item) {
        final int arr_idx = (item-1) / 64;
        final int bit_offset = 64 - (item % 64);
        final long bit_value = bits[arr_idx] & ~(1l << bit_offset);
        if (bit_value != bits[arr_idx]) {
            bits[arr_idx] = bit_value;
            count-=1;
            return;
        }
    }

    @Override
    public boolean contains(Integer item) {
        final int arr_idx = (item-1) / 64;
        final int bit_offset = 64 - (item % 64);
        return (bits[arr_idx] & (1l << bit_offset))>0;
    }

    public static int BLOCK_SIZE = 2048;
    static {
        try {
            BLOCK_SIZE = Integer.parseInt(System.getenv("net.indf.collection.blocksize"));
        }catch (Exception e) {
            System.err.println("[WARN] HyperSet, BLOCK_SIZE is '"+BLOCK_SIZE+"'");
        }
    }

    public final static Block<Integer> EMPTY = new Block<Integer>() {
        @Override
        public int count() {
            return 0;
        }

        @Override
        public void add(Integer item) {
            throw new IllegalAccessError();
        }

        @Override
        public void remove(Integer item) {
            return;
        }

        @Override
        public boolean contains(Integer item) {
            return false;
        }
    };

    public final static Block<Integer> FULL = new Block<Integer>() {

        @Override
        public int count() {
            return BLOCK_SIZE;
        }

        @Override
        public void add(Integer item) {
            if(item<=0 && item>BLOCK_SIZE)
                throw new IllegalArgumentException("overflow block size. (item="+item+", blocksize="+BLOCK_SIZE+")");

        }

        @Override
        public void remove(Integer item) {
            throw new IllegalAccessError();

        }

        @Override
        public boolean contains(Integer item) {
            return item>0 && item<=BLOCK_SIZE;
        }
    };
}
