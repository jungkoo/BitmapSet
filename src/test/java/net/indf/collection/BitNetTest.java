package net.indf.collection;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;


/**
 * Created by nhn on 2014-12-24.
 */
public class BitNetTest {
    private BitNet set;
    private List<Integer> list = Arrays.asList(7,23,8282072);

    @Before
    public void setup() {
        set = new BitNet(10);
        for(Integer i : list) {
            set.add(i);
        }
    }

    @Test
    public void containsTest() {
        assertTrue(set.contains(7));
        assertTrue(set.contains(23));
        assertTrue(set.contains(8282072));
    }

    @Test
    public void removeTest() {
        assertFalse(set.remove(10));
        assertThat(set.size(), is(3));
        assertTrue(set.remove(7));
        assertThat(set.size(), is(2));
        assertTrue(set.contains(23));
        assertTrue(set.contains(8282072));
        assertTrue(set.remove(23));
        assertThat(set.size(), is(1));
        assertTrue(set.remove(8282072));
        assertThat(set.size(), is(0));
    }

    @Test
    public void removeAllTest() {
        assertThat(set.removeAll(Arrays.asList(23,444)), is(true));
        assertThat(set.size(), is(2));
    }

    @Test
    public void iteratorTest() {
        assertThat(set.size(), is(list.size()));
        final Iterator<Integer> it = set.iterator();
        while(it.hasNext()) {
            final Integer num = it.next();
            if (list.contains(num)) {
                continue;
            }
            fail();
        }
    }
}
