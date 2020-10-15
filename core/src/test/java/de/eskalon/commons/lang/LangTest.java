package de.eskalon.commons.lang;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.I18NBundle;

import de.eskalon.commons.LibgdxUnitTest;

public class LangTest extends LibgdxUnitTest {

	@Test
	public void test() {
		// Mock bundle
		I18NBundle bundle = I18NBundle.createBundle(Gdx.files.internal("lang"));

		Lang.setBundle(bundle);

		// Test methods
		assertEquals("test_abc", Lang.get("unloc.test"));
		assertEquals("test2_abc", Lang.get("unloc.test2"));
		assertEquals("test_abc", Lang.get(new A()));
		assertEquals("TEST-123", Lang.get(new B()));

		assertEquals("test_abc 123 b test_abc TEST-123",
				Lang.get("unloc.test3", 123, true, new A(), new B()));
		assertEquals("test_abc cd a test_abc TEST-123",
				Lang.get("unloc.test3", "cd", false, new A(), new B()));

		assertEquals("test_abc", Lang.get("unloc.test", (Object[]) null));
	}

	public class A implements ILocalizable {
		@Override
		public String getUnlocalizedName() {
			return "unloc.test";
		}
	}

	public class B implements ILocalized {
		@Override
		public String getLocalizedName() {
			return "TEST-123";
		}
	}

}
