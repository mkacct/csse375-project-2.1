package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Map;
import java.util.Set;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import datasource.Configuration;
import datasource.DirLoader;
import datasource.FilesLoader;
import domain.javadata.ClassData;

/**
 * Test Class Graph on 
 */
public class ObserverPatternCheckTest {
	private static final String STRING_RESOURCE_PATH = "src/test/resources/observertest";
    Check obc = new ObserverPatternCheck();
    Map<String, ClassData> map;

	@BeforeEach
	public void setup() throws IOException {
		FilesLoader fl = new DirLoader(STRING_RESOURCE_PATH);

        map = TestUtility.getMap(fl.loadFiles("class"));
	}



	@Test
	public void testDefaultConfig() {
		Set<Message> out = obc.run(map, new Configuration(Map.of()));
        Set<Message> exp = Set.of(
            new Message(MessageLevel.INFO, "(Interface) Observer pattern found", Set.of("complicatedyes.Subject", "complicatedyes.ConcreteObserver", "complicatedyes.ConcreteObserver2", "complicatedyes.ConcreteSubject", "complicatedyes.Observer")),
            new Message(MessageLevel.INFO, "(Interface) Observer pattern found", Set.of("normaliobserver.ConcreteObserver", "normaliobserver.Observer", "normaliobserver.ConcreteSubject", "normaliobserver.Subject")),
            new Message(MessageLevel.INFO, "(Abstract) Observer pattern found", Set.of("normalaobserver.ConcreteObserver", "normalaobserver.ConcreteSubject", "normalaobserver.Subject", "normalaobserver.Observer")),
            new Message(MessageLevel.INFO, "(Concrete) Observer pattern found", Set.of("normaliobserver.ConcreteObserver", "normaliobserver.Observer", "normaliobserver.ConcreteSubject")),
            new Message(MessageLevel.INFO, "(Concrete) Observer pattern found", Set.of("normalcobserver.ConcreteObserver", "normalcobserver.Observer", "normalcobserver.ConcreteSubject")),
            new Message(MessageLevel.INFO, "(Concrete) Observer pattern found", Set.of("complicatedyes.ConcreteObserver", "complicatedyes.ConcreteObserver2", "complicatedyes.ConcreteSubject", "complicatedyes.Observer"))
        );
        assertEquals(exp, out);
        // assertEquals(exp.size(), out.size());
        // for (Message m : out) {
        //     if (exp.contains(m)) {
        //         assertTrue(exp.contains(m));
        //     } else {
        //         assertEquals(null, m);
        //     }
        // }
	}

    @Test
	public void testAllOffConfig() {
		Set<Message> out = obc.run(map, new Configuration(Map.of(
            "obsInterface", false,
            "obsAbstract", false,
            "obsConcrete", false
        )));
        Set<Message> exp = Set.of(

        );

        assertEquals(exp, out);
        // assertEquals(exp.size(), out.size());
        // for (Message m : out) {
        //     if (exp.contains(m)) {
        //         assertTrue(exp.contains(m));
        //     } else {
        //         assertEquals(null, m);
        //     }
        // }
	}


}
