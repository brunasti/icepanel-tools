package it.brunasti.icepanel.tools;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DebuggerTest implements TestConstants {

  // Test Debugger methods ---------------------------
  @Test
  @DisplayName("Call Debugger public methods")
  void testMainPublicMethods() {
    System.err.println("Debugger.debug ------ false");
    assertDoesNotThrow(() -> Debugger.debug("Message false"));

    System.err.println("Debugger.debug ------ true");
    Debugger.setDebug(true);
    assertDoesNotThrow(() -> Debugger.debug("Message true"));

    System.err.println("Debugger.debug ------ 4");
    Debugger.setDebug(4);
    assertDoesNotThrow(() -> Debugger.debug("Message"));
    assertDoesNotThrow(() -> Debugger.debug(2,"Message 2"));
    assertDoesNotThrow(() -> Debugger.debug(4,"Message 4"));

    System.err.println("Debugger.debug ------ 0");
    Debugger.setDebug(0);
    assertDoesNotThrow(() -> Debugger.debug("Message"));
    assertDoesNotThrow(() -> Debugger.debug(0,"Message 0"));
    assertDoesNotThrow(() -> Debugger.debug(2,"Message 2"));
    assertDoesNotThrow(() -> Debugger.debug(4,"Message 4"));

    System.err.println("Debugger.debug ------ 2");
    Debugger.setDebug(2);
    assertDoesNotThrow(() -> Debugger.debug("Message"));
    assertDoesNotThrow(() -> Debugger.debug(0,"Message 0"));
    assertDoesNotThrow(() -> Debugger.debug(2,"Message 2"));
    assertDoesNotThrow(() -> Debugger.debug(4,"Message 4"));

    System.err.println("Debugger.debug ------ false");
    Debugger.setDebug(false);
    assertDoesNotThrow(() -> Debugger.debug("Message"));
    assertDoesNotThrow(() -> Debugger.debug(0,"Message 0"));
    assertDoesNotThrow(() -> Debugger.debug(2,"Message 2"));
    assertDoesNotThrow(() -> Debugger.debug(4,"Message 4"));

    System.err.println("Debugger.debug ------ end -----");

    System.err.println("Debugger.setDebug ------");
    Debugger.reset();

    Debugger.setDebug(false);
    assertFalse(Debugger.isDebug());
    assertEquals(0, Debugger.getDebug());

    Debugger.setDebug(true);
    assertTrue(Debugger.isDebug());
    assertEquals(3, Debugger.getDebug());

    Debugger.setDebug(false);
    assertFalse(Debugger.isDebug());
    assertEquals(0, Debugger.getDebug());

    Debugger.setDebug(5);
    assertTrue(Debugger.isDebug());
    assertEquals(5, Debugger.getDebug());

    Debugger.setDebug(false);
    assertFalse(Debugger.isDebug());
    assertEquals(0, Debugger.getDebug());

    Debugger.setDebug(true);
    assertTrue(Debugger.isDebug());
    assertEquals(5, Debugger.getDebug());

    System.err.println("Debugger.setDebug ------ end -----");

  }

  // Test Constructor ---------------------------
  @Test
  @DisplayName("Constructor")
  void testConstructor() {
    Debugger debugger = getDebugger();
    assertNotNull(debugger);
  }

  Debugger getDebugger() {
    return new Debugger();
  }


}
