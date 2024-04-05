package it.brunasti.icepanel.tools;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 * Generic utility functions.
 */
public final class Utils {

  Utils() {
  }

  /**
   * Read into memory (String) the content of a file.
   *
   * @param fileName Name of the file to be read from.
   * @return The content of the file as a String.
   *     In case of exception, an empty String.
   */
  public static String readFileToString(String fileName) {
    try {
      return new Scanner(new File(fileName)).useDelimiter("\\Z").next();
    } catch (Exception ex) {
      return "";
    }
  }

  /**
   * Writes to System.out the content of the Object[] array.
   *
   * @param title Identification title of the output text.
   * @param array Array of Objects to be dumped.
   */
  public static void dump(final String title, final Object[] array) {
    dump(title, array, System.out);
  }

  /**
   * Writes to the passes PrintStream the content of the Object[] array.
   *
   * @param title Identification title of the output text.
   * @param array Array of Objects to be dumped to the output PrintStream
   * @param output PrintStream to be written to.
   */
  public static void dump(final String title, final Object[] array, PrintStream output) {
    output.println("--------" + title + "-------------");
    if (array != null) {
      if (array.length == 0) {
        output.println("-- EMPTY --");
      } else {
        for (int i = 0; i < array.length; i++) {
          output.println("#" + i + "='" + array[i] + "'");
        }
      }
    } else {
      output.println("-- NULL --");
    }
  }

  /**
   * Writes to System.out the content of an ArrayList of Objects.
   *
   * @param title Identification title of the output text.
   * @param array ArrayList of Objects to be dumped.
   */
  public static void dump(final String title, final List<Object> array) {
    if (null == array) {
      dump(title, (Object[]) null);
    } else {
      dump(title, array.toArray());
    }
  }

  /**
   * Writes to System.out the content of a Set of Objects.
   *
   * @param title Identification title of the output text.
   * @param set Set of Objects to be dumped.
   */
  public static void dump(final String title, final Set<Object> set) {
    if (null == set) {
      dump(title, (Object[]) null);
    } else {
      dump(title, set.toArray());
    }
  }


  // JSON functions ----------------------------------

  /**
   * Load a JSON File content.
   *
   * @param jsonFileName Name of the file in JSON format
   * @return The JSONObject corresponding to the file content
   */
  public static JSONObject loadJsonFile(String jsonFileName) {
    JSONParser parser = new JSONParser();
    try {
      Object obj = parser.parse(new FileReader(jsonFileName));
      return (JSONObject) obj;
    } catch (IOException | ParseException e) {
      e.printStackTrace(System.err);
      return null;
    }
  }

}
