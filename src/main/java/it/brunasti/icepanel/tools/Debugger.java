package it.brunasti.icepanel.tools;

/**
 * Debugging functionalities of general use.
 */
public class Debugger {
  private static final int DEFAULT_DEBUG_LEVEL = 3;

  private static boolean debug = false;
  private static int debugLevel = DEFAULT_DEBUG_LEVEL;

  public static void reset() {
    debug = false;
    debugLevel = DEFAULT_DEBUG_LEVEL;
  }

  /**
   * Set debug flag, on or off.
   * If the level is false, then the debug system is set off.
   * N.B.: By setting the flag off you do not change the value of the debug level,
   * so the next time you switch it on again, it will still have the previous level.
   *
   * @param value New flag value
   */
  public static void setDebug(boolean value) {
    debug = value;
  }

  /**
   * Set debug flag to the desired level.
   * If the level is 0, then the debug system is set off, using
   * the overloaded setDebug(boolean) function.
   *
   * @param value Desired level of the debug system
   */
  public static void setDebug(int value) {
    setDebug(value > 0);
    debugLevel = value;
  }

  /**
   * Print to StdError a message if the debug level is higher than the DEFAULT_DEBUG_LEVEL.
   *
   * @param message Debugging message
   */
  public static void debug(String message) {
    debug(DEFAULT_DEBUG_LEVEL, message);
  }

  /**
   * Print to StdError a message if the debug level is higher than the one passed in the level.
   *
   * @param level Level of this message, to be compared with the current debug level
   * @param message Debugging message
   */
  public static void debug(int level, String message) {
    if (debug) {
      if (level <= debugLevel) {
        System.err.println(message);
      }
    }
  }

  /**
   * Return the current debug flag value.
   *
   * @return The debug flag value
   */
  public static boolean isDebug() {
    return debug;
  }

  /**
   * Return the current debug level.
   *
   * @return The current debug level
   */
  public static int getDebug() {
    if (debug) {
      return debugLevel;
    }
    return 0;
  }
}