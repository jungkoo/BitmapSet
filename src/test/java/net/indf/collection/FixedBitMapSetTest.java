package net.indf.collection;

import org.junit.*;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

/**
 *
 * Created by tost on 2014-12-17.
 */
public class FixedBitMapSetTest {
    private FixedBitMapSet set;

    @Before
    public void setup() {
        set = new FixedBitMapSet(1000);
        set.add(7);
        set.add(23);
    }

    @Test
    public void countTest() {
        assertThat(2, is(set.size()));
    }

    @Test
    public void addTest() {
        set.add(111);
        set.add(144);
        set.add(7);
        assertThat(4, is(set.size()));
    }

    public void removeTest() {
        set.remove(7);
        set.remove(112);
        assertThat(1, is(set.size()));
        for(int num : set) {
            if (num == 23) continue;
            fail();
        }
    }

    public void toArrayTest() {
        for(int num: (Integer[])set.toArray()) {
            if (7==num || 23==num)
                continue;
            fail();
        }
    }
}
