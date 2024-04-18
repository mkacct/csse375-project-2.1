package domain.checks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.Test;

import datasource.Configuration;
import domain.Message;
import domain.MessageLevel;

public class CountCheckPropertyValidatorTest {
	private static final String KEY = "key";

	@Test
	public void testOk() {
		Configuration config = new Configuration(Map.of(KEY, 32));

		CountCheckPropertyValidator validator = new CountCheckPropertyValidator();
		Integer value = validator.validateGetInt(config, KEY);

		assertEquals(32, value);
		assertThrows(IllegalStateException.class, () -> {validator.getValidationFailureMessage();});
	}

	@Test
	public void testKeyNotPresent() {
		Configuration config = new Configuration(Map.of());

		CountCheckPropertyValidator validator = new CountCheckPropertyValidator();
		Integer value = validator.validateGetInt(config, KEY);

		assertEquals(null, value);
		Message expectedMsg = new Message(MessageLevel.INFO, "Config property \"key\" not found; skipping check");
		assertEquals(expectedMsg, validator.getValidationFailureMessage());
	}

	@Test
	public void testKeyWrongType() {
		Configuration config = new Configuration(Map.of(KEY, "32"));

		CountCheckPropertyValidator validator = new CountCheckPropertyValidator();
		Integer value = validator.validateGetInt(config, KEY);

		assertEquals(null, value);
		Message expectedMsg = new Message(MessageLevel.ERROR, "Config property \"key\" must be an integer; could not run check");
		assertEquals(expectedMsg, validator.getValidationFailureMessage());
	}
}
