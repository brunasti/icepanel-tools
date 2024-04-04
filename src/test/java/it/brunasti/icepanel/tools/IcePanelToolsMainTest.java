package it.brunasti.icepanel.tools;

import org.apache.commons.cli.Options;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class IcePanelToolsMainTest implements TestConstants {

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
    args[0] = classesDirectory;
    args[1] = outputFileName;
    assertDoesNotThrow(() -> IcePanelToolsMain.main(args));

    System.err.println("IcePanelToolsMain.main ------ 11 args -----");
    String[] fullArgs = new String[11];
    fullArgs[0] = classesDirectory;
    fullArgs[1] = outputFileName;
    fullArgs[2] = "-d";
    fullArgs[3] = "-c";
    fullArgs[4] = configurationFileName;
    fullArgs[5] = "-o";
    fullArgs[6] = outputFileName;
    fullArgs[7] = "-p";
    fullArgs[8] = classesDirectory;
    fullArgs[9] = "-i";
    fullArgs[10] = javaSrcDirectory;
    assertDoesNotThrow(() -> IcePanelToolsMain.main(fullArgs));

    System.err.println("IcePanelToolsMain.main ------ 8 args -----");
    String[] moreArgs = new String[8];
    moreArgs[0] = "-c";
    moreArgs[1] = configurationFileName;
    moreArgs[2] = "-o";
    moreArgs[3] = outputFileName;
    moreArgs[4] = "-p";
    moreArgs[5] = classesDirectory;
    moreArgs[6] = "-i";
    moreArgs[7] = javaSrcDirectory;
    assertDoesNotThrow(() -> IcePanelToolsMain.main(moreArgs));

    System.err.println("IcePanelToolsMain.main ------ 6 args ----- configurationWithLegendFileName");
    String[] lastArgs = new String[6];
    lastArgs[0] = "-c";
    lastArgs[1] = configurationWithIncludeFileName;
    lastArgs[2] = "-o";
    lastArgs[3] = outputFileName;
    lastArgs[4] = "-p";
    lastArgs[5] = classesDirectory;
    assertDoesNotThrow(() -> IcePanelToolsMain.main(lastArgs));
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
    args[0] = classesDirectory;
    assertDoesNotThrow(() -> IcePanelToolsMain.main(args));
  }

  @Test
  @DisplayName("Call IcePanelToolsMain main on test classes")
  void testMainMethod_Test() {

    String[] fullArgs = new String[12];
    fullArgs[0] = testClassesDirectory;
    fullArgs[1] = testOutputFileName;
    fullArgs[2] = "-d";
    fullArgs[3] = "6";
    fullArgs[4] = "-c";
    fullArgs[5] = configurationFileName;
    fullArgs[6] = "-o";
    fullArgs[7] = testOutputFileName;
    fullArgs[8] = "-p";
    fullArgs[9] = testClassesDirectory;
    fullArgs[10] = "-i";
    fullArgs[11] = testJavaSrcDirectory;
    assertDoesNotThrow(() -> IcePanelToolsMain.main(fullArgs));

    String[] args = new String[3];
    args[0] = testClassesDirectory;
    args[1] = testOutputFileName;
    args[2] = "-d";
    assertDoesNotThrow(() -> IcePanelToolsMain.main(args));


  }

  @Test
  @DisplayName("Call IcePanelToolsMain main with output for non existing file")
  void testMainMethod_ErrorOutputFile() {
    String[] args = new String[2];
    args[0] = testClassesDirectory;
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
