package net.indf.collection.block;

import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
/**
 * Created by nhn on 2015-01-02.
 */
public class BitBlockTest {
    private Block block;
    
    @Before
    public void setup() {
        block = new BitBlock();
        block.add(100);
        block.add(1);
        block.add(2048);
    }
    
    @Test
    public void addTest() {
        block.add(1111);
        block.add(1); // duplicate
        assertThat(block.count(), is(4));
    }
    
    @Test
    public void removeTest() {
        block.remove(100);
        block.remove(1024); // not exist number
        assertThat(block.count(), is(2));
    }
    
    @Test
    public void containsTest() {
        assertThat(block.contains(100),     is(true));
        assertThat(block.contains(1),       is(true));
        assertThat(block.contains(222),     is(false));
        assertThat(block.contains(1024),    is(false));

//        assertThat(block.contains(9999),    is(false)); //overflow number
//        assertThat(block.contains(-111),    is(false)); //minus number
    }
    
    


    //        System.out.println(block.count());
//        for(Integer n : block) {
//            System.out.println(n);
//        }
//

}
