package it.brunasti.icepanel.tools;

import org.apache.commons.cli.Options;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class IcePanelToolsMainTest implements TestConstants {

  // Test IcePanelToolsMain methods ---------------------------
  @Test
  @DisplayName("Call IcePanelToolsMain public methods")
  void testMainPublicMethods() {
    Options options = new Options();


    System.err.println("IcePanelToolsMain.printHelp ------");
    assertDoesNotThrow(() -> IcePanelToolsMain.printHelp());
    assertDoesNotThrow(() -> IcePanelToolsMain.printHelp(null));
    assertDoesNotThrow(() -> IcePanelToolsMain.printHelp(options));

    System.err.println("IcePanelToolsMain.printUsage ------");
    assertDoesNotThrow(() -> IcePanelToolsMain.printUsage(null));
    assertDoesNotThrow(() -> IcePanelToolsMain.printUsage(options));
  }

  @Test
  @DisplayName("Call IcePanelToolsMain main without parameters")
  void testMainMethod_NoParams() {
    assertDoesNotThrow(() -> IcePanelToolsMain.main(null));
    String[] args = new String[0];
    assertDoesNotThrow(() -> IcePanelToolsMain.main(args));
    String[] args2 = new String[2];
    assertDoesNotThrow(() -> IcePanelToolsMain.main(args2));
  }

  @Test
  @DisplayName("Call IcePanelToolsMain main with parameters")
  void testMainMethod_Params() {
    System.err.println("IcePanelToolsMain.main ------ 2 args -----");
    String[] args = new String[2];
    args[0] = icePanelJSONFile;
    args[1] = outputFileName;
    assertDoesNotThrow(() -> IcePanelToolsMain.main(args));

    System.err.println("IcePanelToolsMain.main ------ 9 args -----");
    String[] fullArgs = new String[9];
    fullArgs[0] = icePanelJSONFile;
    fullArgs[1] = outputFileName;
    fullArgs[2] = "-d";
    fullArgs[3] = "-c";
    fullArgs[4] = configurationFileName;
    fullArgs[5] = "-o";
    fullArgs[6] = outputFileName;
    fullArgs[7] = "-j";
    fullArgs[8] = icePanelJSONFile;
    assertDoesNotThrow(() -> IcePanelToolsMain.main(fullArgs));

    System.err.println("IcePanelToolsMain.main ------ 6 args -----");
    String[] moreArgs = new String[6];
    moreArgs[0] = "-c";
    moreArgs[1] = configurationFileName;
    moreArgs[2] = "-o";
    moreArgs[3] = outputFileName;
    moreArgs[4] = "-j";
    moreArgs[5] = icePanelJSONFile;
    assertDoesNotThrow(() -> IcePanelToolsMain.main(moreArgs));
  }

  @Test
  @DisplayName("Call IcePanelToolsMain main with parameters in Mermaid format")
  void testMainMethod_Params_Mermaid() {
    System.err.println("IcePanelToolsMain.main ------ 6 args -----");
    String[] moreArgs = new String[7];
    moreArgs[0] = "-c";
    moreArgs[1] = configurationFileName;
    moreArgs[2] = "-o";
    moreArgs[3] = outputFileNameMermaid;
    moreArgs[4] = "-j";
    moreArgs[5] = icePanelJSONFile;
    moreArgs[6] = "-m";
    assertDoesNotThrow(() -> IcePanelToolsMain.main(moreArgs));
  }


  @Test
  @DisplayName("Call IcePanelToolsMain main to create Tadaah examples")
  void testMainMethod_Tadaah() {
    System.err.println("testMainMethod_Tadaah ------ Tadaah -----");
    String[] args = new String[6];

    args[0] = TADAAH_JSON;
    args[1] = TADAAH_OUT;
    args[2] = "-d";
    args[3] = "10";
    args[4] = "-c";
    args[5] = TADAAH_CONF;
    assertDoesNotThrow(() -> IcePanelToolsMain.main(args));

  }

  @Test
  @DisplayName("Call IcePanelToolsMain main to create examples")
  void testMainMethod_Example() {
    System.err.println("testMainMethod_Examples ------ BA -----");
    String[] args = new String[6];

    args[0] = icePanelJSONFileCompact_02;
    args[1] = outputFileName;
    args[2] = "-d";
    args[3] = "10";
    args[4] = "-c";
    args[5] = configurationFileName;
    assertDoesNotThrow(() -> IcePanelToolsMain.main(args));

    System.err.println("testMainMethod_Examples ------ PG -----");
    args[0] = PG_JSON;
    args[1] = PG_OUT;
    args[2] = "-d";
    args[3] = "10";
    args[4] = "-c";
    args[5] = PG_CONF;
    assertDoesNotThrow(() -> IcePanelToolsMain.main(args));

    System.err.println("testMainMethod_Examples ------ SG -----");
    args[0] = SG_JSON;
    args[1] = SG_OUT;
    args[2] = "-d";
    args[3] = "10";
    args[4] = "-c";
    args[5] = SG_CONF;
    assertDoesNotThrow(() -> IcePanelToolsMain.main(args));

    System.err.println("testMainMethod_Examples ------ ML -----");
    args[0] = ML_JSON;
    args[1] = ML_OUT;
    args[2] = "-d";
    args[3] = "10";
    args[4] = "-c";
    args[5] = ML_CONF;
    assertDoesNotThrow(() -> IcePanelToolsMain.main(args));

    System.err.println("testMainMethod_Examples ------ AI -----");
    args[0] = AI_JSON;
    args[1] = AI_OUT;
    args[2] = "-d";
    args[3] = "10";
    args[4] = "-c";
    args[5] = AI_CONF;
    assertDoesNotThrow(() -> IcePanelToolsMain.main(args));

  }

  @Test
  @DisplayName("Call IcePanelToolsMain main to create sub diagrams")
  void testMainMethod_Example_02() {
    System.err.println("testMainMethod_Example_02 ------ BA -----");
    String[] args = new String[6];

    System.err.println("testMainMethod_Examples ------ AI -----");
    args[0] = AI_JSON;
    args[1] = AI_OUT;
    args[2] = "-d";
    args[3] = "10";
    args[4] = "-c";
    args[5] = AI_CONF;
    assertDoesNotThrow(() -> IcePanelToolsMain.main(args));

  }

  @Test
  @DisplayName("Call IcePanelToolsMain main helps")
  void testMainMethod_Helps() {
    String[] args = new String[1];
    args[0] = "-h";
    assertDoesNotThrow(() -> IcePanelToolsMain.main(args));

    String[] argsHelp = new String[1];
    argsHelp[0] = "-?";
    assertDoesNotThrow(() -> IcePanelToolsMain.main(argsHelp));

//    String[] multiArgs = new String[6];
//
//    multiArgs[0] = icePanelJSONFileCompact_02;
//    multiArgs[1] = outputFileName;
//    multiArgs[2] = "-d";
//    multiArgs[3] = "10";
//    multiArgs[4] = "-c";
//    multiArgs[5] = nonExistingJsonFileName;
//    assertDoesNotThrow(() -> IcePanelToolsMain.main(multiArgs));
//
//    multiArgs[0] = nonExistingJsonFileName;
//    multiArgs[1] = outputFileName;
//    multiArgs[2] = "-d";
//    multiArgs[3] = "10";
//    multiArgs[4] = "-c";
//    multiArgs[5] = configurationFileName;
//    assertDoesNotThrow(() -> IcePanelToolsMain.main(multiArgs));
  }

  @Test
  @DisplayName("Call IcePanelToolsMain main with errors")
  void testMainMethod_Errors() {
    String[] args = new String[1];
    args[0] = "-x";
    assertDoesNotThrow(() -> IcePanelToolsMain.main(args));

    String[] argsHelp = new String[1];
    argsHelp[0] = "-z";
    assertDoesNotThrow(() -> IcePanelToolsMain.main(argsHelp));
  }

  @Test
  @DisplayName("Call IcePanelToolsMain main with debug")
  void testMainMethod_Debug() {
    System.err.println("Call IcePanelToolsMain main with debug ---- ");

    System.err.println("[-d]");
    String[] args = new String[1];
    args[0] = "-d";
    assertDoesNotThrow(() -> IcePanelToolsMain.main(args));

    System.err.println("[-d 2]");
    String[] argsHelp = new String[2];
    argsHelp[0] = "-d";
    argsHelp[1] = "2";
    assertDoesNotThrow(() -> IcePanelToolsMain.main(argsHelp));

    System.err.println("[-d 999]");
    argsHelp[0] = "-d";
    argsHelp[1] = "999";
    assertDoesNotThrow(() -> IcePanelToolsMain.main(argsHelp));

    System.err.println("[-d xxx]");
    argsHelp[0] = "-d";
    argsHelp[1] = "xxx";
    assertDoesNotThrow(() -> IcePanelToolsMain.main(argsHelp));
  }

  @Test
  @DisplayName("Call IcePanelToolsMain main to StdOut")
  void testMainMethod_StdOut() {
    String[] args = new String[1];
    args[0] = icePanelJSONFile;
    assertDoesNotThrow(() -> IcePanelToolsMain.main(args));
  }

  @Test
  @DisplayName("Call IcePanelToolsMain main with output for non existing file")
  void testMainMethod_ErrorOutputFile() {
    String[] args = new String[2];
    args[0] = nonExistingJsonFileName;
    args[1] = nonExistingDirectoryAndFile;
    assertDoesNotThrow(() -> IcePanelToolsMain.main(args));
  }

  // Test Constructor ---------------------------
  @Test
  @DisplayName("Constructor")
  void testConstructor() {
    IcePanelToolsMain main = getMain();
    assertNotNull(main);
  }

  IcePanelToolsMain getMain() {
    return new IcePanelToolsMain();
  }


}
