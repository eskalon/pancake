package de.eskalon.commons.misc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.eskalon.commons.LibgdxUnitTest;
import de.eskalon.commons.event.EventQueueBus;

public class EventQueueBusTest extends LibgdxUnitTest {

	int i;
	boolean b;

	@Test
	public void testConsumer() {
		EventQueueBus bus = new EventQueueBus();
		TestEvent ev = new TestEvent();
		ev.integer = 43;
		bus.on(TestEvent.class, e -> {
			i++;
			assertEquals(43, ev.integer);
		});

		// Post & Dispatch
		i = 0;
		bus.post(ev);
		assertEquals(0, i);

		bus.dispatchEvents();
		assertEquals(1, i);

		// Dispatch manually
		bus.dispatch(ev);
		assertEquals(2, i);

		bus.dispatchEvents();
		assertEquals(2, i);
	}

	@Test
	public void testRunnable() {
		EventQueueBus bus = new EventQueueBus();
		TestEvent ev = new TestEvent();
		bus.on(TestEvent.class, () -> {
			b = true;
		});
		// Post & Dispatch
		b = false;
		bus.post(ev);
		assertEquals(false, b);
		bus.dispatchEvents();
		assertEquals(true, b);

		// Dispatch manually
		b = false;
		bus.dispatchEvents();
		assertEquals(false, b);

		bus.dispatch(ev);
		assertEquals(true, b);
	}

	@Test
	public void testException() {
		EventQueueBus bus = new EventQueueBus();
		TestEvent ev = new TestEvent();
		bus.on(TestEvent.class, () -> {
			throw new RuntimeException("hello world!");
		});
		// Post & Dispatch
		bus.post(ev);
		System.out.println(1);
		bus.dispatchEvents();
		System.out.println(2);
	}

	public class TestEvent {
		public int integer;
	}

}
