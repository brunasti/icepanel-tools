package it.brunasti.icepanel.tools;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class IcePanelToPlantUMLConverterTest implements TestConstants {

  // Test generateDiagram function ---------------------------
  @Test
  @DisplayName("Generate Diagram")
  void testGenerateDiagram() {
    IcePanelToPlantUMLConverter IcePanelToPlantUMLConverter = new IcePanelToPlantUMLConverter();
    assertDoesNotThrow(() -> IcePanelToPlantUMLConverter.convertIcePanelToUML(icePanelJSONFile, configurationFileName));
  }

//  @Test
//  @DisplayName("Generate Diagram from this same system")
//  void testGenerateDiagram_Successful() {
//    Debugger.setDebug(5);
//    IcePanelToPlantUMLConverter IcePanelToPlantUMLConverter = new IcePanelToPlantUMLConverter();
//    assertDoesNotThrow(() -> IcePanelToPlantUMLConverter.generateDiagram(classesDirectory,configurationFileName,icePanelJSONFile));
//    assertDoesNotThrow(() -> IcePanelToPlantUMLConverter.generateDiagram(classesDirectory, configurationWithIncludeFileName,icePanelJSONFile));
//    Debugger.setDebug(false);
//  }
//
//  @Test
//  @DisplayName("Generate Diagram from non existing directory")
//  void testGenerateDiagram_Nodir() {
//    Debugger.setDebug(5);
//    IcePanelToPlantUMLConverter IcePanelToPlantUMLConverter = new IcePanelToPlantUMLConverter();
//    assertDoesNotThrow(() ->
//            IcePanelToPlantUMLConverter.generateDiagram(
//                    nonExistingDirectory,
//                    configurationFileName,
//                    null));
//  }
//
//  @Test
//  @DisplayName("Config file non existing")
//  void testGenerateDiagram_NoConfig() {
//    IcePanelToPlantUMLConverter IcePanelToPlantUMLConverter = new IcePanelToPlantUMLConverter();
//    assertDoesNotThrow(() -> IcePanelToPlantUMLConverter.generateDiagram(classesDirectory,nonExistingJsonFileName, null));
//  }

}