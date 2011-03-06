package org.eventroaster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;

import org.eventroaster.annotation.EventHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class EventPriorityComparatorTest {


    private EventPriorityComparator tested;

    @Before
    public void setUp() throws Exception {
        tested = new EventPriorityComparator();
    }

    @After
    public void tearDown() throws Exception {
        tested = null;
    }

    @Test
    public void testCompareNoPriority() {
        final CompareNoPriority object = new CompareNoPriority();
        final int result = comparePriorities(object);
        assertEquals("should be same", 0, result);
    }

    @Test
    public void testCompareAWithPriority() {
        final CompareAWithPriority object = new CompareAWithPriority();
        final int result = comparePriorities(object);
        assertTrue("should be same", result < 0);
    }

    @Test
    public void testCompareAAndBWithSamePriority() {
        final CompareAAndBWithSamePriority object = new CompareAAndBWithSamePriority();
        final int result = comparePriorities(object);
        assertTrue("should be same", result == 0);
    }

    @Test
    public void testCompareAAndBWithPriority() {
        final CompareAAndBWithPriority object = new CompareAAndBWithPriority();
        final int result = comparePriorities(object);
        assertTrue("should be same", result > 0);
    }

    private int comparePriorities(final Object object) {
        final Method a = getMethod(object, "methodA");
        final Method b = getMethod(object, "methodB");

        final int result = tested.compare(a, b);
        return result;
    }

    private Method getMethod(final Object object, final String name) {
        try {
            return object.getClass().getMethod(name);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unused")
    private static final class CompareNoPriority {

        @EventHandler(event = StubTestEvent.class)
        public void methodA() {}

        @EventHandler(event = StubTestEvent.class)
        public void methodB() {}
    }

    @SuppressWarnings("unused")
    private static final class CompareAWithPriority {

        @EventHandler(event = StubTestEvent.class, priority = 10)
        public void methodA() {}

        @EventHandler(event = StubTestEvent.class)
        public void methodB() {}
    }

    @SuppressWarnings("unused")
    private static final class CompareAAndBWithSamePriority {

        @EventHandler(event = StubTestEvent.class, priority = 20)
        public void methodA() {}

        @EventHandler(event = StubTestEvent.class, priority = 20)
        public void methodB() {}
    }

    @SuppressWarnings("unused")
    private static final class CompareAAndBWithPriority {

        @EventHandler(event = StubTestEvent.class, priority = 20)
        public void methodA() {}

        @EventHandler(event = StubTestEvent.class, priority = 10)
        public void methodB() {}
    }
}
