package org.eventroaster;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class EventHandlingCallbackTest {

    private EventHandlingCallback tested;



    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        tested = null;
    }

    @Test
    public void testCall() throws SecurityException, NoSuchMethodException {
        final StubListener mockListener = new StubListener();
        final Method mockMethod = getMethod("mockMethod");
        tested = new EventHandlingCallback(new Object(), mockListener, mockMethod);
        final Boolean result = tested.call();
        assertTrue("result", result);
        assertTrue("method called", mockListener.mockMethodCalled);
        assertFalse("method shouldn't be called", mockListener.mockMethodWithArgsCalled);
    }

    @Test
    public void testCallWithArgs() {
        final StubListener mockListener = new StubListener();
        final Method mockMethod = getMethod("mockMethodWithArgs", Object.class);
        tested = new EventHandlingCallback(new Object(), mockListener, mockMethod);
        final Boolean result = tested.call();
        assertTrue("result", result);
        assertTrue("method called",  mockListener.mockMethodWithArgsCalled);
        assertFalse("method shouldn't be called",mockListener.mockMethodCalled);
    }

    @Test
    public void testCallWithException() {
        final StubListener mockListener = new StubListener();
        final Method mockMethod = getMethod("mockMethodThrowingException");
        tested = new EventHandlingCallback(new Object(), mockListener, mockMethod);
        final Boolean result = tested.call();
        assertFalse("result", result);
        assertFalse("method shouln't be called",  mockListener.mockMethodWithArgsCalled);
        assertFalse("method shouldn't be called",mockListener.mockMethodCalled);
    }
    private Method getMethod(final String name, final Class<?> ...classes)  {
        try {
            return StubListener.class.getMethod(name, classes);
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("unused")
    private static class StubListener {
        boolean mockMethodCalled = false;
        boolean mockMethodWithArgsCalled = false;

        public void mockMethod() {
            mockMethodCalled = true;
        }

        public void mockMethodWithArgs(final Object obj){
            mockMethodWithArgsCalled = true;
        }

        public void mockMethodThrowingException() {
            throw new UnsupportedOperationException();
        }
    }

}
