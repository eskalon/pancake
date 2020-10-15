package de.eskalon.commons.misc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.google.common.eventbus.Subscribe;

public class EventQueueBusTest {

	int i = 0;

	@Test
	public void testEventBus() {
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
		bus.post(ev);

		assertEquals(0, i);

		bus.distributeEvents();
		assertEquals(1, i);

		bus.post(ev);
		assertEquals(1, i);
		bus.distributeEvents();
		assertEquals(2, i);
	}

	public abstract class TestSubscriber {
		@Subscribe
		public abstract void test(TestEvent ev);
	}

	public class TestEvent {
		public int integer;
	}

}
