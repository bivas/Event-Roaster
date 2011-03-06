package org.eventroaster;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eventroaster.annotation.EventHandler;
import org.junit.Before;
import org.junit.Test;

public class EventServiceImplTest {

    private boolean called = false;
    private boolean calledWithEvent = false;
    private final List<String> priorityCheck = new ArrayList<String>(2);
    private EventService eventService;

    @Before
    public void setUp() {
        eventService = EventServiceFactory.getEventService(TestServiceKey.TEST_KEY);
        called = false;
        calledWithEvent = false;
    }

    @Test
    public void fireEventWithoutHandler() {
        eventService.fire(new StubTestEvent());
        waitForEvent();
        assertNotCalled();
    }

    private void assertNotCalled() {
        assertFalse("default hander called", called);
        assertFalse("hander called", calledWithEvent);
    }

    private void assertCalled() {
        assertTrue("default hander not called", called);
        assertTrue("hander not called", calledWithEvent);
        assertTrue("priority", priorityCheck.get(0).equals("handlerTest"));
    }

    @Test
    public void fireEventWithHandler() {
        register();
        eventService.fire(new StubTestEvent());
        waitForEvent();
        assertCalled();
    }

    @Test(expected = IllegalArgumentException.class)
    public void registerTwiceShouldFail() {
        register();
        register();
    }

    @Test(expected = IllegalArgumentException.class)
    public void registerListnerWithoutEventHandler() {
        eventService.register(new Object());
    }

    @Test
    public void unregister() throws Exception {
        register();
        eventService.unregister(this);
    }

    @Test
    public void unregisterNonExistingListenerShouldNotFail() {
        eventService.unregister(new Object());
    }

    @Test(expected = IllegalArgumentException.class)
    public void failOnObjectNotAnnotatedAsEvent() {
        eventService.fire(new Object());
    }

    @Test
    public void autoUnregister() throws Exception {
        TestEventHandler testEventHandler = new TestEventHandler();
        eventService.register(testEventHandler);
        eventService.fire(new StubTestEvent());
        waitForEvent();
        assertTrue("should be called", TestEventHandler.called);
        TestEventHandler.called = false;
        testEventHandler = null;
        System.gc();
        eventService.fire(new StubTestEvent());
        waitForEvent();
        assertFalse("should not be called", TestEventHandler.called);
    }

    private void register() {
        eventService.register(this);
    }

    private void waitForEvent() {
        try {
            Thread.sleep(2L);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @EventHandler(event = StubTestEvent.class, priority = 10)
    public void handlerTest() {
        called = true;
        priorityCheck.add("handlerTest");
    }

    @EventHandler(event = StubTestEvent.class)
    public void handlerTestWithEvent(final StubTestEvent event) {
        calledWithEvent = true;
        priorityCheck.add("handlerTestWithEvent");
    }

    private enum TestServiceKey implements EventServiceKey {
        TEST_KEY;
    }

    @SuppressWarnings("unused")
    private static final class TestEventHandler {

        static boolean called = false;

        @EventHandler(event = StubTestEvent.class)
        public void dummy(){
            called = true;
        }
    }

}
