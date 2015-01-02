package net.indf.collection.block;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import static net.indf.collection.block.Blocks.SOME_ITEM_SIZE;
/**
 * Created by 정민철 on 2015-01-02.
 */
public class SomeBlock implements Block {
    private Set<Integer> set = new HashSet<Integer>();
    
    @Override
    public int count() {
        return set.size();
    }

    @Override
    public void add(int item) {
        set.add(item);
//        if(count()>SOME_BLOCK_SIZE)
//            throw
    }

    @Override
    public void remove(int item) {
        set.remove(item);
    }

    @Override
    public boolean contains(int item) {
        return set.contains(item);
    }

    @Override
    public Iterator<Integer> iterator() {
        return null;
    }
}
