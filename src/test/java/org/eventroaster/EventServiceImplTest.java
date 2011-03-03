package org.eventroaster;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eventroaster.EventService;
import org.eventroaster.EventServiceFactory;
import org.eventroaster.EventServiceKey;
import org.eventroaster.annotation.Event;
import org.eventroaster.annotation.EventHandler;
import org.junit.Before;
import org.junit.Test;


public class EventServiceImplTest {

    private boolean called = false;
    private boolean calledWithEvent = false;
    private EventService eventService;

    @Before
    public void setUp() {
	eventService = EventServiceFactory.getEventService(TestServiceKey.TEST_KEY);
	called = false;
	calledWithEvent = false;
    }

    @Test
    public void fireEventWithoutHandler() throws Exception {
	eventService.fire(new TestEvent());
	waitForEvent();
	assertNotCalled();
    }

    private void assertNotCalled() {
	assertFalse(called);
	assertFalse(calledWithEvent);
    }

    private void assertCalled() {
	assertTrue(called);
	assertTrue(calledWithEvent);
    }

    @Test
    public void fireEventWithHandler() throws Exception {
	register();
	eventService.fire(new TestEvent());
	waitForEvent();
	assertCalled();
    }


    @Test
    public void registerTwiceShouldFail() throws Exception {
	register();
	try {
	    register();
	    fail("trying to register twice - should fail");
	} catch (final IllegalArgumentException e) {
	    //good
	}
    }

    @Test
    public void registerListnerWithoutEventHandler() throws Exception {
	try {
	    eventService.register(new Object());
	    fail("no event handler method - should fail");
	} catch (final IllegalArgumentException e) {
	    //good
	}
    }

    @Test
    public void unregisterNonExistingListenerShouldNotFail() throws Exception {
	eventService.unregister(new Object());
    }

    @Test
    public void failOnObjectNotAnnotatedAsEvent() throws Exception {
	try {
	    eventService.fire(new Object());
	    fail("this is not an object with event annotation");
	} catch (final IllegalArgumentException e) {
	    // good
	}
    }

    private void register() {
	eventService.register(this);
    }

    private void waitForEvent() throws InterruptedException {
	Thread.sleep(2L);
    }

    @EventHandler(event = TestEvent.class)
    public void handlerTest() {
	called = true;
    }

    @EventHandler(event = TestEvent.class)
    public void handlerTestWithEvent(final TestEvent event) {
	calledWithEvent = true;
    }

    private enum TestServiceKey implements EventServiceKey {
	TEST_KEY;
    }

    @Event
    private static final class TestEvent {}

}
