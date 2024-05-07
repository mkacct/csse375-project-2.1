package datasource.configspec;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JsonFileConfigSpecLoaderTest {
	private static final String TEST_CONFIG_SPEC_RES_PATH = "/test-config-spec.json";
	private static final String CHECK_ENTITY_TYPE = "check";

	private ConfigSpec configSpec;

	@BeforeEach
	public void setup() throws IOException {
		JsonFileConfigSpecLoader loader = new JsonFileConfigSpecLoader(TEST_CONFIG_SPEC_RES_PATH);
		this.configSpec = loader.loadConfigSpec();
	}

	@Test
	public void testSectionCount() {
		List<ConfigSpec.Section> sections = this.configSpec.getSections();
		assertEquals(5, sections.size());
	}

	@Test
	public void testNonCheckSection() {
		ConfigSpec.Section section0 = this.configSpec.getSections().get(0);
		assertEquals("Execution settings", section0.title);
		assertFalse(section0.representsCheck());
		assertThrows(IllegalStateException.class, () -> {section0.getCheckName();});
		assertThrows(IllegalStateException.class, () -> {section0.getEntityType();});

		List<ConfigSpec.Setting> section0Settings = section0.getSettings();
		assertEquals(1, section0Settings.size());

		ConfigSpec.Setting section0Setting0 = section0Settings.get(0);
		assertEquals("skipUnmarkedChecks", section0Setting0.name);
		assertEquals(ConfigSpec.Setting.Type.BOOLEAN, section0Setting0.type);
		assertEquals(
			"If true, all checks only run if they are explicitly enabled. Otherwise, most checks (those that are not disabled by default) run unless they are explicitly disabled.",
			section0Setting0.desc
		);
		assertThrows(IllegalStateException.class, () -> {section0Setting0.hasStringOptions();});
		assertThrows(IllegalStateException.class, () -> {section0Setting0.getStringOptions();});
	}

	@Test
	public void testCheckSection() {
		ConfigSpec.Section section2 = this.configSpec.getSections().get(2);
		assertEquals("Method Length", section2.title);
		assertTrue(section2.representsCheck());
		assertEquals("methodLength", section2.getCheckName());
		assertEquals(CHECK_ENTITY_TYPE, section2.getEntityType());

		List<ConfigSpec.Setting> section2Settings = section2.getSettings();
		assertEquals(1, section2Settings.size());

		ConfigSpec.Setting section2Setting0 = section2Settings.get(0);
		assertEquals("maxMethodLengthInstrs", section2Setting0.name);
		assertEquals(ConfigSpec.Setting.Type.INT, section2Setting0.type);
		assertEquals(
			"Maximum method length, in bytecode instructions.",
			section2Setting0.desc
		);
		assertThrows(IllegalStateException.class, () -> {section2Setting0.hasStringOptions();});
		assertThrows(IllegalStateException.class, () -> {section2Setting0.getStringOptions();});
	}

	@Test
	public void testCheckSectionWithEntityTypeOverridden() {
		ConfigSpec.Section section4 = this.configSpec.getSections().get(4);
		assertEquals("Program to Interface, Not Implementation", section4.title);
		assertTrue(section4.representsCheck());
		assertEquals("programToInterface", section4.getCheckName());
		assertEquals("something else", section4.getEntityType());

		List<ConfigSpec.Setting> section4Settings = section4.getSettings();
		assertEquals(2, section4Settings.size());

		ConfigSpec.Setting section4Setting0 = section4Settings.get(0);
		assertEquals("domainPackageName", section4Setting0.name);
		assertEquals(ConfigSpec.Setting.Type.STRING, section4Setting0.type);
		assertEquals(
			"Name of the domain package (default is \"domain\").",
			section4Setting0.desc
		);
		assertFalse(section4Setting0.hasStringOptions());
		assertThrows(IllegalStateException.class, () -> {section4Setting0.getStringOptions();});

		ConfigSpec.Setting section4Setting1 = section4Settings.get(1);
		assertEquals("allowedDependencies", section4Setting1.name);
		assertEquals(ConfigSpec.Setting.Type.LIST_OF_STRING, section4Setting1.type);
		assertEquals(
			"List of user-specified allowed dependencies (add known interfaces and/or data classes here).",
			section4Setting1.desc
		);
		assertThrows(IllegalStateException.class, () -> {section4Setting1.hasStringOptions();});
		assertThrows(IllegalStateException.class, () -> {section4Setting1.getStringOptions();});
	}

	@Test
	public void testSectionWithNullSettings() {
		ConfigSpec.Section section3 = this.configSpec.getSections().get(3);
		assertEquals("No Global Variables", section3.title);
		assertTrue(section3.representsCheck());
		assertEquals("noGlobalVariables", section3.getCheckName());
		assertEquals(CHECK_ENTITY_TYPE, section3.getEntityType());

		List<ConfigSpec.Setting> section3Settings = section3.getSettings();
		assertEquals(0, section3Settings.size());
	}

	@Test
	public void testSectionWithStringOptions() {
		List<String> expectedNamingConventionSelect = List.of(
			"lowercase",
			"UPPERCASE",
			"UPPER_CASE",
			"lower_case",
			"camelCase",
			"PascalCase",
			"ANY"
		);

		ConfigSpec.Section section1 = this.configSpec.getSections().get(1);
		assertEquals("Naming Conventions", section1.title);
		assertTrue(section1.representsCheck());
		assertEquals("namingConventions", section1.getCheckName());
		assertEquals(CHECK_ENTITY_TYPE, section1.getEntityType());

		List<ConfigSpec.Setting> section1Settings = section1.getSettings();
		assertEquals(3, section1Settings.size());

		ConfigSpec.Setting section1Setting0 = section1Settings.get(0);
		assertEquals("convPackage", section1Setting0.name);
		assertEquals(ConfigSpec.Setting.Type.STRING, section1Setting0.type);
		assertNull(section1Setting0.desc);
		assertTrue(section1Setting0.hasStringOptions());
		assertEquals(expectedNamingConventionSelect, section1Setting0.getStringOptions());

		ConfigSpec.Setting section1Setting1 = section1Settings.get(1);
		assertEquals("convClass", section1Setting1.name);
		assertEquals(ConfigSpec.Setting.Type.STRING, section1Setting1.type);
		assertNull(section1Setting1.desc);
		assertTrue(section1Setting1.hasStringOptions());
		assertEquals(expectedNamingConventionSelect, section1Setting1.getStringOptions());

		ConfigSpec.Setting section1Setting2 = section1Settings.get(2);
		assertEquals("convInterface", section1Setting2.name);
		assertEquals(ConfigSpec.Setting.Type.STRING, section1Setting2.type);
		assertNull(section1Setting2.desc);
		assertTrue(section1Setting2.hasStringOptions());
		assertEquals(expectedNamingConventionSelect, section1Setting2.getStringOptions());
	}
}
