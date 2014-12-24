package net.indf.collection;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;


/**
 * Created by nhn on 2014-12-24.
 */
public class BitMapSetTest {
    private BitMapSet set;
    private List<Integer> list = Arrays.asList(7,23,8282072,435,12125,1234123,12312);

    @Before
    public void setup() {
        set = new BitMapSet();
        for(Integer i : list) {
            set.add(i);
        }
    }

    @Test
    public void iteratorTest() {
        assertThat(set.size(), is(list.size()));

        final Iterator<Integer> it = set.iterator();
        while(it.hasNext()) {
            final Integer num = it.next();
            System.out.println(num);
            if (list.remove(num)) {
                continue;
            }
            fail();
        }
    }
}
