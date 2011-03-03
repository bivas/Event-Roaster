package org.eventroaster;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eventroaster.annotation.Event;
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
	eventService.fire(new TestEvent());
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
	eventService.fire(new TestEvent());
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
    public void unregisterNonExistingListenerShouldNotFail() {
	eventService.unregister(new Object());
    }

    @Test(expected = IllegalArgumentException.class)
    public void failOnObjectNotAnnotatedAsEvent() {
	eventService.fire(new Object());
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

    @EventHandler(event = TestEvent.class, priority = 10)
    public void handlerTest() {
	called = true;
	priorityCheck.add("handlerTest");
    }

    @EventHandler(event = TestEvent.class)
    public void handlerTestWithEvent(final TestEvent event) {
	calledWithEvent = true;
	priorityCheck.add("handlerTestWithEvent");
    }

    private enum TestServiceKey implements EventServiceKey {
	TEST_KEY;
    }

    @Event
    private static final class TestEvent {}

}
