package de.eskalon.commons.inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.damios.guacamole.gdx.log.Logger;
import de.damios.guacamole.gdx.log.LoggerService;
import de.eskalon.commons.LibgdxUnitTest;
import de.eskalon.commons.inject.providers.LoggerProvider;
import de.eskalon.commons.inject.providers.LoggerProvider.Log;

public class EskalonInjectorTest extends LibgdxUnitTest {

	@Test
	public void testInjector() {
		/* Create injector and bindings */
		IInjector injector = EskalonInjector.getInstance();

		// Linked binding
		injector.bindTo(SuperClassC.class, ClassC.class);
		ClassC inst = new ClassC();
		inst.value = 35;
		injector.bindToInstance(ClassC.class, inst);

		// Instance binding
		ClassA inst2 = new ClassA();
		inst2.value = 47;
		injector.bindToInstance(ClassA.class, inst2);

		// Provider bindings
		injector.bindToProvider(ClassB.class, () -> {
			ClassB tmp = new ClassB();
			tmp.value = 59;
			return tmp;
		});

		// Qualified provider binding
		injector.bindToQualifiedProvider(Logger.class, Log.class,
				new LoggerProvider());

		/* Inject stuff */
		TestTarget target = new TestTarget();
		injector.injectMembers(target);

		/* Check the results */
		assertEquals(47, target.classA.value);
		assertEquals(59, target.classB.value);
		assertEquals(35, target.classC.value);
		assertEquals(null, target.classD);
		assertEquals(LoggerService.getLogger(ClassD.class), target.logger);
	}

	public class TestTarget {
		public @Inject ClassA classA;
		public @Inject ClassB classB;
		public @Inject SuperClassC classC;
		public @Inject ClassD classD;
		public @Inject @Log(ClassD.class) Logger logger;
	}

	public class ClassA {
		public int value;
	}

	public class ClassB {
		public int value;
	}

	public class SuperClassC {
		public int value;
	}

	public class ClassC extends SuperClassC {
	}

	public class ClassD {
	}

}