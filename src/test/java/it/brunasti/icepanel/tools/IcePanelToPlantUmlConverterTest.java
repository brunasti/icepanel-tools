package it.brunasti.icepanel.tools;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class IcePanelToPlantUmlConverterTest implements TestConstants {

  // Test generateDiagram function ---------------------------
  @Test
  @DisplayName("Generate Diagram")
  void testGenerateDiagram() {
    IcePanelToPlantUmlConverter IcePanelToPlantUMLConverter = new IcePanelToPlantUmlConverter();
    assertDoesNotThrow(() -> IcePanelToPlantUMLConverter.convertIcePanelToUml(icePanelJSONFile, configurationFileName, outputFileName));
  }

//  @Test
//  @DisplayName("Generate Diagram from this same system")
//  void testGenerateDiagram_Successful() {
//    Debugger.setDebug(5);
//    IcePanelToPlantUmlConverter IcePanelToPlantUmlConverter = new IcePanelToPlantUmlConverter();
//    assertDoesNotThrow(() -> IcePanelToPlantUmlConverter.generateDiagram(classesDirectory,configurationFileName,icePanelJSONFile));
//    assertDoesNotThrow(() -> IcePanelToPlantUmlConverter.generateDiagram(classesDirectory, configurationWithIncludeFileName,icePanelJSONFile));
//    Debugger.setDebug(false);
//  }
//
//  @Test
//  @DisplayName("Generate Diagram from non existing directory")
//  void testGenerateDiagram_Nodir() {
//    Debugger.setDebug(5);
//    IcePanelToPlantUmlConverter IcePanelToPlantUmlConverter = new IcePanelToPlantUmlConverter();
//    assertDoesNotThrow(() ->
//            IcePanelToPlantUmlConverter.generateDiagram(
//                    nonExistingDirectory,
//                    configurationFileName,
//                    null));
//  }
//
//  @Test
//  @DisplayName("Config file non existing")
//  void testGenerateDiagram_NoConfig() {
//    IcePanelToPlantUmlConverter IcePanelToPlantUmlConverter = new IcePanelToPlantUmlConverter();
//    assertDoesNotThrow(() -> IcePanelToPlantUmlConverter.generateDiagram(classesDirectory,nonExistingJsonFileName, null));
//  }

}