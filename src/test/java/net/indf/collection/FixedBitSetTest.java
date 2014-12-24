package net.indf.collection;

import org.junit.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 * Created by tost on 2014-12-17.
 */
public class FixedBitSetTest {
    private FixedBitSet set;

    @Before
    public void setup() {
        set = new FixedBitSet(1000);
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
    public void addAllFixedBitSetTest() {
        FixedBitSet bm = new FixedBitSet(1000);
        bm.add(23);
        bm.add(24);
        set.addAll(bm);
        assertThat(set.size(), is(3));
        assertTrue(set.contains(7));
        assertTrue(set.contains(23));
        assertTrue(set.contains(24));

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
    public void removeAllSetTest() {
        // 7 23
        set.removeAll(Arrays.asList(7, 44));
        assertThat(set.size(), is(1));
        assertTrue(set.contains(23));
    }

    @Test
    public void removeAllFixedBitSetTest() {
        FixedBitSet bm = new FixedBitSet(1000);
        bm.add(23);
        bm.add(878);
        set.removeAll(bm);
        assertThat(set.size(), is(1));
        assertTrue(set.contains(7));
    }

    @Test
    public void toArrayTest() {
        for(Object o: set.toArray()) {
            int num = Integer.class.cast(o);
            if (7==num || 23==num)
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

    @Test
    public void retainAllCollectionTest() {
        final List<Integer> t = Arrays.asList(1, 7, 34);
        set.retainAll(t);
        assertThat(set.size(), is(1));
        assertThat(t.size(), is(3));
        assertTrue(set.contains(7));
    }

    @Test
    public void retainAllBitMapTest() {
        FixedBitSet t = new FixedBitSet(1000);
        t.add(7);
        t.add(3);
        t.add(34);
        set.retainAll(t);
        assertThat(set.size(), is(1));
        assertThat(t.size(), is(3));
        assertTrue(set.contains(7));
    }

    @Test
    public void iteratorTest() {
        assertThat(set.size(), is(2));
        final Iterator<Integer> it = set.iterator();
        int count = 0;
        while(it.hasNext()) {
            if (Arrays.asList(7,23).contains(it.next())) {
                count += 1;
                continue;
            }
            fail();
        }
        assertThat(count, is(2));
    }

    @Test
    public void iteratorRemoveTest() {
        assertThat(set.size(), is(2));
        final Iterator<Integer> it = set.iterator();
        while(it.hasNext()) {
            if (23==it.next()) {
                it.remove();
            }
        }
        assertThat(set.size(), is(1));
        assertTrue(set.contains(7));
    }
}
