package de.eskalon.commons.inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.damios.guacamole.gdx.log.Logger;
import de.damios.guacamole.gdx.log.LoggerService;
import de.eskalon.commons.LibgdxUnitTest;
import de.eskalon.commons.inject.annotations.Inject;
import de.eskalon.commons.inject.annotations.Singleton;
import de.eskalon.commons.inject.providers.LoggerProvider;
import de.eskalon.commons.inject.providers.LoggerProvider.Log;

public class DefaultInjectorTest extends LibgdxUnitTest {

	@Test
	// Prints: ClassD cannot be resolved; ClassB cannot be resolved (for now)
	public void testInjector() {
		/* Create injector and bindings */
		IInjector injector = new DefaultInjector();

		// Linked binding
		injector.bindToSubclass(SuperClassC.class, ClassC.class);
		ClassC inst = new ClassC();
		inst.value = 35;
		injector.bindToInstance(ClassC.class, inst);

		// Instance binding
		ClassA inst2 = new ClassA();
		inst2.value = 47;
		injector.bindToInstance(ClassA.class, inst2);

		// Constructor bindings
		injector.bindToConstructor(ClassE.class);

		// Provider bindings
		//@formatter:off
//		injector.bindToProvider(ClassB.class, ClassBProvider.class);
		//@formatter:on

		// Qualified provider binding
		injector.bindToQualifiedProvider(Logger.class, Log.class,
				LoggerProvider.class);

		/* Inject stuff */
		TestTarget target = injector.injectMembers(new TestTarget());

		/* Check the results */
		assertEquals(47, target.classA.value);
		//@formatter:off
//		assertEquals(59, target.classB.value);
		//@formatter:on
		assertEquals(35, target.classC.value);
		assertEquals(null, target.classD);
		//@formatter:off
//		assertEquals(59, target.classE.b.value);
		//@formatter:on
		System.out.println(target.classE);
		target.classE.value = 22;
		assertEquals(injector.getInstance(ClassE.class).value,
				target.classE.value);
		assertEquals(LoggerService.getLogger(DefaultInjectorTest.class),
				target.logger);

		assertEquals(null, injector.getInstance(ClassF.class));
	}

	//@formatter:off
//	public class ClassBProvider implements Provider<ClassB> {
//		@Override
//		public ClassB provide() {
//			ClassB tmp = new ClassB();
//			tmp.value = 59;
//			return tmp;
//		}
//	}
//
//	public class ClassEProvider implements Provider<ClassE> {
//		@Override
//		@Singleton
//		public ClassE provide() {
//			return new ClassE();
//		}
//	}
	//@formatter:on

	public class TestTarget {
		public @Inject ClassA classA;
		//@formatter:off
//		public @Inject ClassB classB;
		//@formatter:on
		public @Inject SuperClassC classC;
		public @Inject ClassD classD;
		public @Inject ClassE classE;
		public @Inject @Log(DefaultInjectorTest.class) Logger logger;
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

	public static class ClassE {

		public int value;

		@Inject
		@Singleton
		public ClassE() {
			// default constructor
		}

		private @Inject ClassB b;
	}

	public class ClassF {
	}

}