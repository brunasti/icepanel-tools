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
  String examplesDirectory = "./examples/";
  String testResourcesDirectory = "./src/test/resources/";
  String docsDirectory = "./docs/";
  String nonExistingDirectoryAndFile = "./errorDir/test.txt";


  String icePanelJSONFile = testResourcesDirectory + "icepanel-plain.json";
  String icePanelJSONFileCompact_02 = testResourcesDirectory + "icepanel-plain.json";


  String outputFileName = docsDirectory + "output.puml";
  String outputFileNameMermaid = docsDirectory + "output.md";

  String configurationFileName = docsDirectory + "config.json";
  String wrongConfigurationFileName = outputFileName;

  String wrongJsonFileName = testResourcesDirectory + "wrong.json";
  String nonExistingJsonFileName = tempDirectory + "nofile.json";

  String defaultLegendFileName = docsDirectory + "includedFile.txt";



  String PG_DIR = examplesDirectory + "pg/";
  String PG_JSON = PG_DIR + "pg-001.json";
  String PG_OUT = PG_DIR + "pg-001.puml";
  String PG_CONF = PG_DIR + "config.json";

  String SG_DIR = examplesDirectory + "sg/";
  String SG_JSON = SG_DIR + "sg-001.json";
  String SG_OUT = SG_DIR + "sg-001.puml";
  String SG_CONF = SG_DIR + "config.json";

  String ML_DIR = examplesDirectory + "ml/";
  String ML_JSON = ML_DIR + "ml-001.json";
  String ML_OUT = ML_DIR + "ml-001.puml";
  String ML_CONF = ML_DIR + "config.json";

  String AI_DIR = examplesDirectory + "ai/";
  String AI_JSON = AI_DIR + "ARS-AI_Readiness_Survey.json";
  String AI_OUT = AI_DIR + "ARS-AI_Readiness_Survey.puml";
  String AI_CONF = AI_DIR + "config.json";

  String TADAAH_DIR = examplesDirectory + "tadaah/";
  String TADAAH_JSON = TADAAH_DIR + "tadaah-001.json";
  String TADAAH_OUT = TADAAH_DIR + "tadaah-001.puml";
  String TADAAH_CONF = TADAAH_DIR + "config.json";


}
