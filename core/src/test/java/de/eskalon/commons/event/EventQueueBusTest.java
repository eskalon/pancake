package de.eskalon.commons.event;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import de.eskalon.commons.LibgdxUnitTest;

public class EventQueueBusTest extends LibgdxUnitTest {

	int i = 0;
	int j = 0;

	@Test
	public void testEventBus1() {
		/* Register subscriber and create event */
		EventQueueBus bus = new EventQueueBus();
		TestSubscriber sub = new TestSubscriber() {

			@Override
			public void test(TestEvent ev) {
				i++;
				assertEquals(43, ev.integer);
			}
		};
		TestEvent ev = new TestEvent();
		ev.integer = 43;
		bus.register(sub);

		/* First round */
		bus.post(ev);
		assertEquals(0, i);
		bus.dispatchEvents();
		assertEquals(1, i);

		/* Second round */
		bus.post(ev);
		assertEquals(1, i);
		bus.dispatchEvents();
		assertEquals(2, i);

		/* Unregister subscriber */
		bus.unregister(sub);
		bus.post(ev);
		bus.dispatchEvents();
		assertEquals(2, i);
	}

	@Test
	public void testEventBus2() {
		/* Register subscriber and create event */
		EventQueueBus bus = new EventQueueBus();
		Consumer<TestEvent> c = (ev) -> {
			j++;
			assertEquals(34, ev.integer);
		};

		bus.register(TestEvent.class, c);

		TestEvent ev = new TestEvent();
		ev.integer = 34;

		/* First round */
		bus.post(ev);
		assertEquals(0, j);
		bus.dispatchEvents();
		assertEquals(1, j);

		/* Second round */
		bus.post(ev);
		assertEquals(1, j);
		bus.dispatchEvents();
		assertEquals(2, j);

		/* Unregister subscriber */
		bus.unregister(TestEvent.class, c);

		bus.post(ev);
		bus.dispatchEvents();
		assertEquals(2, j);
	}

	public abstract class TestSubscriber {
		@Subscribe
		public abstract void test(TestEvent ev);
	}

	public class TestEvent {
		public int integer;
	}

}