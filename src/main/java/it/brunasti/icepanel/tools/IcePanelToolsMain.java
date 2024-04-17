package it.brunasti.icepanel.tools;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Entry point for the CLI version of the ClassDiagrammer.
 * This ClassDiagrammerMain uses the library form Apache for Command Line Interface:
 * <a href="https://commons.apache.org/proper/commons-cli/usage.html">commons-cli</a>
 */
public class IcePanelToolsMain {

  // TODO: Enable conversion to Mermaid https://mermaid.js.org/syntax/c4.html
  // TODO: Check the diagrams: examples/ml/icePanel-C4-output-Mollie
  //  - API Extension - Julian.puml : Description too long

  /* Get actual class name to be printed on */
  static Logger log = LogManager.getLogger(IcePanelToolsMain.class);

  static CommandLine commandLine;
  static IcePanelToPlantUmlConverter icePanelToPlantUMLConverter;

  private static String icePanelJSONExportFile = "";
  private static String outputFile = "";
  private static String configurationFile = "";
  private static boolean debug = false;

  private static Options options;

  private static void reset() {
    icePanelJSONExportFile = "";
    outputFile = "";
    configurationFile = "";

    debug = false;

    options = null;
  }

  private static boolean setDebugOption(Option optionDebug) {
    if (commandLine.hasOption(optionDebug.getOpt())) {
      debug = true;
    }
    return true;
  }

  private static boolean processCommandLine(String[] args) {

    // Reset all the flags, to avoid multiple sequence runs interfering
    reset();

    options = new Options();
    Option optionHelp = new Option("h", "help", false, "Help");
    Option optionShortUsage = new Option("?", false, "Quick Reference");
    Option optionDebug = Option.builder().option("d")
            .longOpt("debug").hasArg(false)
            .desc("Execute in debug mode").build();
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

      setDebugOption(optionDebug);

      if (debug) {
        Utils.dump("ARGS", args, System.err);
        Utils.dump("CMD", commandLine.getArgs(), System.err);
      }
      if (commandLine.hasOption(optionHelp.getOpt())) {
        printHelp(options);
        return false;
      }

      if (commandLine.hasOption(optionShortUsage.getOpt())) {
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
        log.debug(IcePanelConstants.DEBUG_TEXT_SET_TO,
                optionInputJsonFile.getDescription(), icePanelJSONExportFile);
      }
      if (commandLine.hasOption(optionOutputFile.getOpt())) {
        outputFile = commandLine.getOptionValue(optionOutputFile.getOpt());
        log.debug(IcePanelConstants.DEBUG_TEXT_SET_TO,
                optionOutputFile.getDescription(), outputFile);
      }
      if (commandLine.hasOption(optionConfigFile.getOpt())) {
        configurationFile = commandLine.getOptionValue(optionConfigFile.getOpt());
        log.debug(IcePanelConstants.DEBUG_TEXT_SET_TO,
                optionConfigFile.getDescription(), configurationFile);
      }

    } catch (ParseException | NullPointerException e) {
      log.info(e.getMessage());
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
      log.info("ERROR: No options provided to printHelp");
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
      log.info("ERROR: No options provided to printUsage");
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
    log.info("Start");
    reset();

    boolean cliIsCorrect = processCommandLine(args);

    log.debug("CommandLine parsed [{}]", cliIsCorrect);

    if (!cliIsCorrect) {
      printHelp();
      return;
    }

    log.debug("IcePanel JSON file [{}]", icePanelJSONExportFile);
    log.debug("OutputFile         [{}]", outputFile);
    log.debug( "ConfigurationFile  [{}]",configurationFile);

    if ((null == icePanelJSONExportFile) || (icePanelJSONExportFile.isBlank())) {
      log.info("IcePanel JSON export file to be transformed not defined");
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

      String subOutputFileNameBase = "icePanel-C4";

      icePanelToPlantUMLConverter = new IcePanelToPlantUmlConverter(output);
      icePanelToPlantUMLConverter.convertIcePanelToUml(
              icePanelJSONExportFile, configurationFile, subOutputFileNameBase);

      if (null != file) {
        file.close();
      }
    } catch (IOException ex) {
      log.error(ex);
    }
  }
}
