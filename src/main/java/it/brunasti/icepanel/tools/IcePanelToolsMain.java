package it.brunasti.icepanel.tools;

import org.apache.commons.cli.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

public class IcePanelToolsMain {

  static CommandLine commandLine;
  static IcePanelToPlantUMLConverter icePanelToPlantUMLConverter;

//  private static String javaPackagePath = "";
  private static String icePanelJSONExportFile = "";
  private static String outputFile = "";
  private static String configurationFile = "";

  private static Options options;

  private static void reset() {
    Debugger.reset();

    icePanelJSONExportFile = "";
//    javaPackagePath = "";
    outputFile = "";
    configurationFile = "";

    options = null;
  }



  private static boolean processCommandLine(String[] args) {

    // Reset all the flags, to avoid multiple sequence runs interfering
    reset();

    options = new Options();
    Option optionHelp = new Option("h", "help", false, "Help");
    Option optionShortUsage = new Option("?", false, "Quick Reference");
    Option optionDebug = Option.builder().option("d")
            .longOpt("debug").hasArg(true).optionalArg(true)
            .desc("Execute in debug mode, optionally with desired level").build();
    Option optionOutputFile = new Option("o", "output", true, "Output File");
    Option optionConfigFile = new Option("c", "config", true,
            "Configuration File");

    Option optionInputJsonFile = new Option("j", "json", true,
            "IcePanel JSON export file to be transformed");

    options.addOption(optionHelp);
    options.addOption(optionShortUsage);
    options.addOption(optionDebug);
    options.addOption(optionOutputFile);
    options.addOption(optionConfigFile);
    options.addOption(optionInputJsonFile);

    try {
      CommandLineParser parser = new DefaultParser();

      commandLine = parser.parse(options, args);

      if (commandLine.hasOption(optionDebug.getOpt())) {
        String debugLevelString = commandLine.getOptionValue(optionDebug.getOpt());
        System.err.println(optionDebug.getDescription()
                + " set to [" + debugLevelString + "]");
        if (debugLevelString != null) {
          try {
            int dl = Integer.parseInt(debugLevelString);
            Debugger.setDebug(dl);
          } catch (NumberFormatException ex) {
            System.err.println("Error the option Debug ("
                    + optionDebug.getDescription() + ") : " + ex.getMessage());
            return false;
          }
        } else {
          Debugger.setDebug(true);
        }
      }
      if (Debugger.isDebug()) {
        Utils.dump("ARGS", args, System.err);
        Utils.dump("CMD", commandLine.getArgs(), System.err);
      }
      if (commandLine.hasOption("h")) {
        printHelp(options);
        return false;
      }

      if (commandLine.hasOption("?")) {
        printUsage(options);
        return false;
      }

      if (commandLine.getArgs().length > 0) {
        icePanelJSONExportFile = commandLine.getArgs()[0];
        if (commandLine.getArgs().length > 1) {
          outputFile = commandLine.getArgs()[1];
        }
      }

      if (commandLine.hasOption(optionInputJsonFile.getOpt())) {
        icePanelJSONExportFile = commandLine.getOptionValue(optionInputJsonFile.getOpt());
        Debugger.debug(optionInputJsonFile.getDescription() + " set to [" + icePanelJSONExportFile + "]");
      }
      if (commandLine.hasOption(optionOutputFile.getOpt())) {
        outputFile = commandLine.getOptionValue(optionOutputFile.getOpt());
        Debugger.debug(optionOutputFile.getDescription() + " set to [" + outputFile + "]");
      }
      if (commandLine.hasOption(optionConfigFile.getOpt())) {
        configurationFile = commandLine.getOptionValue(optionConfigFile.getOpt());
        Debugger.debug(optionConfigFile.getDescription() + " set to [" + configurationFile + "]");
      }

    } catch (ParseException | NullPointerException e) {
      System.err.println(e.getMessage());
      printHelp(options);
      return false;
    }
    return true;
  }

  static void printHelp() {
    printHelp(options);
  }

  static void printHelp(Options options) {
    if (null == options) {
      System.err.println("ERROR: No options provided to printHelp");
      return;
    }

    HelpFormatter helper = new HelpFormatter();

    String className = IcePanelToolsMain.class.getCanonicalName();
    PrintWriter outError = new PrintWriter(System.err);

    helper.printHelp(outError,
            100,
            "java " + className,
            "",
            options,
            4,
            4,
            "");
    outError.flush();
  }

  static void printUsage(Options options) {
    if (null == options) {
      System.err.println("ERROR: No options provided to printUsage");
      return;
    }

    HelpFormatter helper = new HelpFormatter();

    String className = IcePanelToolsMain.class.getCanonicalName();
    PrintWriter outError = new PrintWriter(System.err);

    helper.printUsage(outError, 100, "java " + className, options);
  }

  /**
   * Entry point for the CLI version of the ClassDiagrammer.
   *
   * @param args Command Line options
   */
  public static void main(String[] args) {
    reset();

    boolean cliIsCorrect = processCommandLine(args);

    Debugger.debug("CommandLine parsed [" + cliIsCorrect + "]");

    if (!cliIsCorrect) {
      printHelp();
      return;
    }

    Debugger.debug( "IcePanel JSON file [" + icePanelJSONExportFile + "]\n"
                  + "OutputFile         [" + outputFile + "]\n"
                  + "ConfigurationFile  [" + configurationFile + "]");

    if ((null == icePanelJSONExportFile) || (icePanelJSONExportFile.isBlank())) {
      System.err.println("IcePanel JSON export file to be transformed not defined");
      printHelp();
      return;
    }

    FileOutputStream file = null;

    try {
      PrintStream output = System.out;

      if ((null != outputFile) && (!outputFile.isBlank())) {
        // Creates a FileOutputStream
        file = new FileOutputStream(outputFile);

        // Creates a PrintWriter
        output = new PrintStream(file, true);
      }

      icePanelToPlantUMLConverter = new IcePanelToPlantUMLConverter(output);
//      icePanelToPlantUMLConverter.generateDiagram(classesPackagePath, configurationFile, javaPackagePath);

      if (null != file) {
        file.close();
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}
