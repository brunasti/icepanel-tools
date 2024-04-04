package it.brunasti.icepanel.tools;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest implements TestConstants {

  // Test loadConfigurationFile function ---------------------------
  @Test
  @DisplayName("Read the configuration file with the correct content")
  void testLoadConfigurationFile_Successful() {
    JSONObject jsonObject = Utils.loadJsonFile(configurationFileName);
    assertNotNull(jsonObject);

    JSONObject exclude = (JSONObject) jsonObject.get("exclude");
    assertNotNull(exclude);

    JSONArray classes = (JSONArray) exclude.get("classes");
    assertNotNull(classes);

    JSONArray packages = (JSONArray) exclude.get("packages");
    assertNotNull(packages);
  }

  @Test
  @DisplayName("Read the configuration file with the wrong content")
  void testLoadConfigurationFile_WrongJSON() {
    JSONObject jsonObject = Utils.loadJsonFile(wrongJsonFileName);
    assertNotNull(jsonObject);

    JSONObject exclude = (JSONObject) jsonObject.get("exclude");
    assertNotNull(exclude);

    JSONArray classes = (JSONArray) exclude.get("classes");
    assertNull(classes);
  }

  @Test
  @DisplayName("Read the wrong configuration file that doesn't exist or is not json")
  void testLoadConfigurationFile_Fail() {
    JSONObject jsonObject = Utils.loadJsonFile(configurationFileName + ".error");
    assertNull(jsonObject);

    jsonObject = Utils.loadJsonFile(wrongConfigurationFileName);
    assertNull(jsonObject);
  }


  // Test listDirectories function ---------------------------
//  @Test
//  @DisplayName("List Temp sub directories")
//  void testListDirectories_Temp() {
//    assertDoesNotThrow(() -> Utils.listDirectories(tempDirectory));
//    try {
//      Set<String> list = Utils.listDirectories(tempDirectory);
//      assertNotNull(list);
//      assertEquals(0,list.size());
//    } catch (IOException ioex) {
//      ioex.printStackTrace();
//    }
//  }
//
//  @Test
//  @DisplayName("List Src sub directories")
//  void testListDirectories_Src() {
//    assertDoesNotThrow(() -> Utils.listDirectories(srcDirectory));
//    try {
//      Set<String> list = Utils.listDirectories(srcDirectory);
//      assertNotNull(list);
//      assertEquals(2,list.size());
//    } catch (IOException ioex) {
//      ioex.printStackTrace();
//    }
//  }
//
//  @Test
//  @DisplayName("List non existing directory")
//  void testListDirectories_NonExisting() {
//    Throwable exception = assertThrows(NoSuchFileException.class, () -> Utils.listDirectories(nonExistingDirectory));
//    assertEquals("./errorDir", exception.getMessage());
//  }
//
//
//  // Test listFilesUsingFilesList function ---------------------------
//  @Test
//  @DisplayName("List Files in test resources directory")
//  void testListFilesUsingFilesList_Resources() {
//    assertDoesNotThrow(() -> Utils.listFiles(testResourcesDirectory));
//    try {
//      Set<String> list = Utils.listFiles(testResourcesDirectory);
//      assertNotNull(list);
//      assertEquals(2,list.size());
//    } catch (IOException ioex) {
//      ioex.printStackTrace();
//    }
//  }
//
//  @Test
//  @DisplayName("List Files in Src directory")
//  void testListFilesUsingFilesList_Src() {
//    assertDoesNotThrow(() -> Utils.listFiles(srcDirectory));
//    try {
//      Set<String> list = Utils.listFiles(srcDirectory);
//      assertNotNull(list);
//      assertEquals(0,list.size());
//    } catch (IOException ioex) {
//      ioex.printStackTrace();
//    }
//  }
//
//  @Test
//  @DisplayName("List Files in non existing directory")
//  void testListFilesUsingFilesList_NonExisting() {
//    Throwable exception = assertThrows(NoSuchFileException.class, () -> Utils.listFiles(nonExistingDirectory));
//    assertEquals("./errorDir", exception.getMessage());
//  }


  // Test dump function ---------------------------
  @Test
  @DisplayName("Dump null array")
  void testDump_Void() {
    Object[] objects = null;
    assertDoesNotThrow(() -> Utils.dump("", objects));

    ArrayList<Object> arrayList = null;
    assertDoesNotThrow(() -> Utils.dump("", arrayList));

    Set<Object> set = null;
    assertDoesNotThrow(() -> Utils.dump("", set));
  }

  @Test
  @DisplayName("Dump Empty array")
  void testDump_Empty() {
    Object[] objects = new Object[0];
    assertDoesNotThrow(() -> Utils.dump("", objects));

    ArrayList<Object> arrayList = new ArrayList<>();
    assertDoesNotThrow(() -> Utils.dump("", arrayList));

    Set<Object> set = new HashSet<>();
    assertDoesNotThrow(() -> Utils.dump("", set));
  }

  @Test
  @DisplayName("Dump Simple array")
  void testDump_Simple() {
    Object[] objects = new Object[1];
    objects[0] = "0";
    assertDoesNotThrow(() -> Utils.dump("", objects));

    ArrayList<Object> arrayList = new ArrayList<>();
    arrayList.add(objects);
    assertDoesNotThrow(() -> Utils.dump("", arrayList));

    Set<Object> set = new HashSet<>();
    set.add(arrayList);
    assertDoesNotThrow(() -> Utils.dump("", set));
  }

  @Test
  @DisplayName("Dump Array with null")
  void testDump_WithNull() {
    Object[] objects = new Object[3];
    objects[1] = "1";
    assertDoesNotThrow(() -> Utils.dump("", objects));

    ArrayList<Object> arrayList = new ArrayList<>();
    arrayList.add(objects);
    assertDoesNotThrow(() -> Utils.dump("", arrayList));

    Set<Object> set = new HashSet<>();
    set.add(arrayList);
    assertDoesNotThrow(() -> Utils.dump("", set));
  }


  // Test Constructor ---------------------------
  @Test
  @DisplayName("Constructor")
  void testConstructor() {
    Utils utils = getUtils();
    assertNotNull(utils);
  }

  Utils getUtils() {
    return new Utils();
  }


  // Test listDirectories function ---------------------------
  @Test
  @DisplayName("Read File to String")
  void testReadFileToString() {
    assertDoesNotThrow(() -> Utils.readFileToString(configurationFileName));
    String fileContent = Utils.readFileToString(configurationFileName);
    assertNotNull(fileContent);
    System.out.println(fileContent);

    assertDoesNotThrow(() -> Utils.readFileToString(nonExistingJsonFileName));
    fileContent = Utils.readFileToString(nonExistingJsonFileName);
    assertNotNull(fileContent);
    System.out.println(fileContent);

    assertDoesNotThrow(() -> Utils.readFileToString(defaultLegendFileName));
    fileContent = Utils.readFileToString(defaultLegendFileName);
    assertNotNull(fileContent);
    System.out.println(fileContent);

  }


}