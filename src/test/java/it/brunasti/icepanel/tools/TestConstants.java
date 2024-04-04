/*
 * Copyright (c) 2024. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package it.brunasti.icepanel.tools;

public interface TestConstants {

  // Test config ---------------------------
  String tempDirectory = "./temp/";
  String testResourcesDirectory = "./src/test/resources/";
  String docsDirectory = "./docs/";
  String nonExistingDirectory = "./errorDir";
  String nonExistingDirectoryAndFile = "./errorDir/test.txt";


  String srcDirectory = "./src/";
  String javaSrcDirectory = srcDirectory + "main/java";
  String testJavaSrcDirectory = srcDirectory + "test/java";

  String classesDirectory = "./target/classes";
  String testClassesDirectory = "./target/test-classes";

  String outputFileName = docsDirectory + "output.puml";
  String outputWithImportFileName = docsDirectory + "output-import.puml";
  String outputWithIncludeFileName = docsDirectory + "output-include.puml";
  String testOutputFileName = docsDirectory + "output-test.puml";

  String configurationFileName = docsDirectory + "config.json";
  String configurationWithIncludeFileName = docsDirectory + "config-includeFile.json";
  String wrongConfigurationFileName = outputFileName;

  String wrongJsonFileName = testResourcesDirectory + "wrong.json";
  String nonExistingJsonFileName = tempDirectory + "nofile.json";

  String defaultLegendFileName = docsDirectory + "includedFile.txt";

}
