package net.indf.collection;

import org.junit.*;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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

    @Test
    public void removeTest() {
        set.remove(7);
        set.remove(112);
        assertThat(1, is(set.size()));
        for(int num : set) {
            if (num == 23) continue;
            fail();
        }
    }

    @Test
    public void toArrayTest() {
        for(Object o: set.toArray()) {
            int num = Integer.class.cast(o);
            System.out.println(num);
            if (7==num || 23==num)
                continue;
            fail();
        }
    }

    @Test
    public void addAllSetTest() {
        set.addAll(Arrays.asList(749, 64,31));
        assertThat(5, is(set.size()));
        for(Object o : set.toArray()) {
            final int num = Integer.class.cast(o);
            if (Arrays.asList(7,23,31,64,749).contains(num))
                continue;
            fail();
        }
    }

    @Test
    public void containsTest() {
        set.add(555); // 7 23 555
        assertThat(set.size(), is(3));
        assertTrue(set.contains(7));
        assertTrue(set.contains(23));
        assertTrue(set.contains(555));
        set.addAll(Arrays.asList(749,64,31));
        assertThat(set.size(), is(6));
        assertTrue(set.contains(7));
        assertTrue(set.contains(23));
        assertTrue(set.contains(555));
        assertTrue(set.contains(749));
        assertTrue(set.contains(64));
        assertTrue(set.contains(31));

    }
}
