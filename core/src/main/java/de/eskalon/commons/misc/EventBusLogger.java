package de.eskalon.commons.misc;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.ExceptionEvent;
import com.google.common.eventbus.Subscribe;

import de.damios.guacamole.gdx.Log;

/**
 * A simple logger for events thrown by an {@link EventBus}. Has to be
 * {@linkplain EventBus#register(Object) registered} as subscriber.
 * 
 * @author damios
 */
public class EventBusLogger {

	@Subscribe
	public void onExceptionEvent(ExceptionEvent ev) {
		Log.error("EventHandler",
				"Exception thrown by subscriber method '%s(%s)' on subscriber '%s' when dispatching event '%s'",
				ev.getSubscriberMethod().getName(),
				ev.getSubscriberMethod().getParameterTypes()[0].getSimpleName(),
				ev.getSubscriber(), ev.getEvent());
	}

	@Subscribe
	public void onDeadEvent(DeadEvent ev) {
		Log.debug("EventHandler",
				"The event '%s' was dispatched, but there were no matching subscribers registered",
				ev.getEvent());
	}

}
