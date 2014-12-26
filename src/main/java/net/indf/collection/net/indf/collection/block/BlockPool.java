package net.indf.collection.net.indf.collection.block;

import java.util.HashMap;
import java.util.Map;

/**
 * 블럭을 풀링하여 사용한다
 * *
 * Created by 정민철 on 2014-12-26.
 */
public class BlockPool {
    private static Map<Integer, BlockPool> BLOCK_POOLS = new HashMap<Integer, BlockPool>();
    
    private BlockPool(final int blockSize) {
        //TODO: 구현하자
        
    }
    
    public Block get(Integer number) {
        //TODO: 블럭을 풀링하여 던져준다
        
        return null;
    }
    
    public static synchronized BlockPool getInstance(int blockSize) {
        if (!BLOCK_POOLS.containsKey(blockSize)) {
            BLOCK_POOLS.put(blockSize, new BlockPool(blockSize));
        }
        return BLOCK_POOLS.get(blockSize);
    }
}
