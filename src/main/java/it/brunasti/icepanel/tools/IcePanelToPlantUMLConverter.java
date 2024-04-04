package it.brunasti.icepanel.tools;

import org.apache.bcel.classfile.JavaClass;
import org.json.simple.JSONObject;

import java.io.PrintStream;
import java.util.*;

public class IcePanelToPlantUMLConverter {

  // Reference to a PrintStream to be used for the diagram
  // By default is the Standard.out, but it can be redirected
  // to a file.
  private final PrintStream output;

  private String includeFileName = "";

  /**
   * Instantiate a ClassDiagrammer with output directed to StandardOut.
   */
  public IcePanelToPlantUMLConverter() {
    this.output = System.out;
  }

  /**
   * Instantiate a ClassDiagrammer with output directed to a passed PrintStream.
   * Used in case to create an output file, passing a PrintStream pointing
   * to the desired file.
   * If System.out is passed, the output is to StandardOut.
   *
   * @param output The PrintStream to which the output will be directed.
   */
  public IcePanelToPlantUMLConverter(PrintStream output) {
    this.output = output;
  }

  private void cleanLocalVars() {
    // Reset all variables to avoid conflicts in case of multiple run
    includeFileName = "";
  }


  // ------------------------------------------------------
  // Load and manage Configuration ------------------------

  private void setDefaultConfiguration() {
    // TODO: set default values
  }

  private void loadJsonConfiguration(JSONObject jsonObject) {
    loadIncludeFileConfiguration(jsonObject);
  }

  private void loadIncludeFileConfiguration(JSONObject jsonObject) {
    Object includeFile = jsonObject.get("includeFile");
    if (includeFile != null) {
      includeFileName = includeFile.toString();
      Debugger.debug(4, "  - includeFile [" + includeFileName + "]");
    }
  }


  private boolean loadJsonConfigurationFromFile(String configurationFileName) {
    Debugger.debug(1, "loadJsonConfigurationFromFile : " + configurationFileName);
    JSONObject jsonObject = Utils.loadJsonFile(configurationFileName);
    if (null == jsonObject) {
      Debugger.debug(2,
              "loadJsonConfigurationFromFile : no data in config file " + configurationFileName);
      return false;
    }

    loadJsonConfiguration(jsonObject);
    return true;
  }


  private boolean loadConfiguration(String configurationFileName) {
    if ((null == configurationFileName) || (configurationFileName.isBlank())) {
      setDefaultConfiguration();
      return true;
    } else {
      return loadJsonConfigurationFromFile(configurationFileName);
    }
  }


  // Load the JSON file exported from IcePanel ---------------------------

  private JSONObject loadIcePanelJsonFromFile(String icePanelJSONFileName) {
    Debugger.debug(1, "loadIcePanelJsonFromFile : " + icePanelJSONFileName);
    JSONObject jsonObject = Utils.loadJsonFile(icePanelJSONFileName);
    if (null == jsonObject) {
      Debugger.debug(2,
              "loadIcePanelJsonFromFile : no data in config file " + icePanelJSONFileName);
      return null;
    }
    return jsonObject;
  }


  private void generateHeader(final String icePanelJSONFile,
                              final String configurationFile) {
    Debugger.debug(2, "generateHeader() ------------------");
    output.println("@startuml");
    output.println("'https://plantuml.com/class-diagram");
    output.println();
    output.println("' CONVERT ICEPANEL DIAGRAM ===========");
    output.println("' Convert            : " + this.getClass().getName());
    output.println("' IcePanel JSON File : [" + icePanelJSONFile + "]");
    output.println("' Configuration      : [" + configurationFile + "]");
    output.println("' Generated at       : " + new Date());
//    String includeFileContent = Utils.readFileToString(includeFileName);
//    if (!includeFileContent.isBlank()) {
//      output.println();
//      output.println("' Include         : [" + includeFileName + "] ---------");
//      output.println(includeFileContent);
//      output.println("' Include end     : --------------------------");
//    }
    output.println();
  }

  private void generateFooter() {
    Debugger.debug(2, "generateFooter() ------------------");
    output.println();
    output.println("@enduml");
  }

  private String getName(Object object, final JSONObject modelObject) {
    String name = modelObject.get("name").toString();
    if ((name == null) || (name.isBlank())) {
      name = object.toString();
    }
    name = name.replaceAll("\n", " - ");
    return name;
  }

  private void generateClasses(final JSONObject jsonObject) {
    Debugger.debug(2, "generateClasses() ------------------");
    output.println();
    output.println("' CLASSES =======");
    JSONObject modelObjects = (JSONObject)jsonObject.get("modelObjects");
    output.println("' "+modelObjects);
    modelObjects.keySet().forEach(
            object -> {
//              output.println("class \"" + object + "\" as " + object);
              JSONObject modelObject = (JSONObject) modelObjects.get(object);
//              \\ (JSONObject)object;
              String name = getName(object, modelObject);
              output.println("class \"" + name + "\" as " + object + " ");
      }
    );

//    classes.forEach(javaClass -> {
//      if (javaClass.isEnum()) {
//        generateEnum(javaClass, classLoader);
//      } else if (javaClass.isInterface()) {
//        output.println("interface " + javaClass.getClassName());
//      } else if (javaClass.isAbstract()) {
//        output.println("abstract " + javaClass.getClassName());
//      } else {
//        output.println("class " + javaClass.getClassName());
//      }
//    });
    output.println();
  }



  /** Generate a diagram for the IcePanel JSON file.
   *
   * @param icePanelJSONFile The JSON file exported from IcePanel to be converted
   * @param configurationFile The configuration file with the list
   *                          of packages and classes to exclude.
   */
  public void convertIcePanelToUML(final String icePanelJSONFile,
                                   final String configurationFile) {
    cleanLocalVars();
    Debugger.debug(2, "convertIcePanelToUML [" + icePanelJSONFile + "][" + configurationFile + "]");

    boolean initiated = loadConfiguration(configurationFile);
    if (!initiated) {
      System.err.println("Configuration JSON not loaded");
      return;
    }

    JSONObject jsonObject = loadIcePanelJsonFromFile(icePanelJSONFile);
    if (!initiated) {
      System.err.println("Exclusion config not loaded");
      return;
    }


//    ArrayList<String> files = new ArrayList<>();
//    try {
//      Set<String> dirs = Utils.listDirectories(icePanelJSONFile);
//      dirs.forEach(dir -> files.addAll(iterateSubDirectories(icePanelJSONFile, dir)));
//    } catch (IOException e) {
//      System.err.println("Error listing directories : " + e.getMessage());
//    }
//
//    ClassLoader classLoader = getClassLoader(icePanelJSONFile);
//    Debugger.debug(2, "classLoader [" + classLoader + "]");
//    if (null == classLoader) {
//      System.err.println("Class loader not created");
//      return;
//    }
//
//    ClassLoaderRepository rep = new ClassLoaderRepository(classLoader);
//
//    ArrayList<JavaClass> classes = new ArrayList<>();
//
//    files.forEach(file -> {
//      try {
//        JavaClass javaClass = rep.loadClass(file);
//        classes.add(javaClass);
//      } catch (ClassNotFoundException e) {
//        System.err.println("Error loading class : " + e.getMessage());
//      }
//    });

    generateHeader(icePanelJSONFile, configurationFile);
    generateClasses(jsonObject);
//    generateInheritances(classes);
//    generateImplements(classes);
//    generateFields(classes);
//    generateUses(classes);
//    generateImports(classes, javaFilesPath);
    generateFooter();
  }

}
