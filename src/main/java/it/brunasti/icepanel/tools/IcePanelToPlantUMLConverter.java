package it.brunasti.icepanel.tools;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

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
    includeFileName = "";
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
    output.println(
"""
            !include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Context.puml
            !include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Component.puml
            ' uncomment the following line and comment the first to use locally
            ' !include C4_Context.puml
""");
    output.println("'https://plantuml.com/class-diagram");
    output.println();
    output.println("' CONVERT ICEPANEL DIAGRAM ===========");
    output.println("' Converter          : " + this.getClass().getName());
    output.println("' IcePanel JSON File : [" + icePanelJSONFile + "]");
    output.println("' Configuration      : [" + configurationFile + "]");
    output.println("' Generated at       : " + new Date());
    String includeFileContent = Utils.readFileToString(includeFileName);
    if (!includeFileContent.isBlank()) {
      output.println();
      output.println("' Include         : [" + includeFileName + "] ---------");
      output.println(includeFileContent);
      output.println("' Include end     : --------------------------");
    }
    output.println();
  }

  private void generateFooter() {
    Debugger.debug(2, "generateFooter() ------------------");
    output.println();
    output.println("@enduml");
  }

  private String getValue(final JSONObject modelObject, String key) {
    return getValue(modelObject, key, " - ");
  }

  private String getValue(final JSONObject modelObject, String key, String nlReplace) {
    String value = modelObject.get(key).toString();
    value = value.replace("\n", nlReplace);
    return value;
  }

  private String getName(Object object, final JSONObject modelObject) {
    String name = getValue(modelObject, "name");
    if ((name == null) || (name.isBlank())) {
      name = object.toString();
    }
    return name;
  }

  private void generateConnections(final JSONObject icePanelDiagramJSON, String rootName) {
    Debugger.debug(2, "generateConnections() ------------------");
    output.println();
    output.println("' CONNECTIONS =======");
    JSONObject modelConnections = (JSONObject)icePanelDiagramJSON.get("modelConnections");
    modelConnections.keySet().forEach(
            object -> {
              JSONObject modelObject = (JSONObject) modelConnections.get(object);
              String name = getName(object, modelObject);
              String source = getValue(modelObject, "originId");
              String target = getValue(modelObject, "targetId");
              String direction = getValue(modelObject, "direction");
              if ("outgoing".equals(direction)) {
                output.println("Rel(" + source + ", " + target + ", \"" + name + "\" )");
              } else if ("bidirectional".equals(direction)) {
                output.println("Rel(" + source + ", " + target + ", \"" + name + "\" )");
                output.println("Rel(" + target + ", " + source + ", \"Return of " + name + "\" )");
              }
            }
    );
    output.println();

    output.println();
    output.println("' PARENT CONNECTIONS =======");
    JSONObject modelObjects = (JSONObject)icePanelDiagramJSON.get("modelObjects");
    modelObjects.keySet().forEach(
            object -> {
              JSONObject modelObject = (JSONObject) modelObjects.get(object);
              JSONArray parentIds = (JSONArray)modelObject.get("parentIds");

              parentIds.forEach(
                parentId -> {
                  if (!rootName.equals(parentId)) {
                    output.println("  " + parentId + " <.. " + object + " ");
                  }
                }
              );
            }
    );
    output.println();
  }

  private void generateClasses(final JSONObject icePanelDiagramJSON) {
    Debugger.debug(2, "generateClasses() ------------------");
    output.println();
    output.println("' CLASSES =======");
    JSONObject modelObjects = (JSONObject)icePanelDiagramJSON.get("modelObjects");
    modelObjects.keySet().forEach(
            object -> {
              JSONObject modelObject = (JSONObject) modelObjects.get(object);
              String name = getName(object, modelObject);
              String type = getValue(modelObject, "type", "");
              String description = getValue(modelObject,"description", " ");

              if ("system".equals(type)) {
                output.println("System(" + object + ", \"" + name + "\", \"" + description + "\" ) ");
              } else if ("actor".equals(type)) {
                output.println("Person(" + object + ", \"" + name + "\", \"" + description + "\" ) ");
              } else if ("app".equals(type)) {
                output.println("Component(" + object + ", \"" + name + "\", \"" + description + "\" ) ");
              } else if ("store".equals(type)) {
                output.println("ContainerDb(" + object + ", \"" + name + "\", \"" + description + "\", \"\" ) ");
              } else if ("area".equals(type)) {
                output.println("System_Boundary(" + object + ", \"" + name + "\", \"" + description + "\", \"\" ) ");
              } else if ("component".equals(type)) {
                // TODO: Add technologies
                output.println("Container(" + object + ", \"" + name + "\", \"" + description + "\", \"\" ) ");
              } else {
                Debugger.debug(2, "unknown type : [" + type + "]");
              }
      }
    );
    output.println();
  }


  private void generateSubDiagram(final JSONObject icePanelDiagramJSON, final ArrayList<JSONObject> bases) {
    Debugger.debug(2, "generateSubDiagram() ------------------");

    bases.forEach( base -> {
      String name = getValue(base, "name");
      Debugger.debug(2, "generateSubDiagram (" + name + ") ------------------");
    });

  }


  private String findRoot(final JSONObject icePanelDiagramJSON) {
    Debugger.debug(2, "findRoot() ------------------");
    JSONObject modelObjects = (JSONObject)icePanelDiagramJSON.get("modelObjects");
    AtomicReference<String> root = new AtomicReference<>("");
    modelObjects.keySet().forEach(
      object -> {
        JSONObject modelObject = (JSONObject) modelObjects.get(object);
        String type = getValue(modelObject, "type", "");

        if ("root".equals(type)) {
          root.set(object.toString());
        }
      }
    );
    return root.get();
  }

  private ArrayList<JSONObject> findSystems(final JSONObject icePanelDiagramJSON) {
    Debugger.debug(2, "findSystems() ------------------");
    JSONObject modelObjects = (JSONObject)icePanelDiagramJSON.get("modelObjects");
    ArrayList<JSONObject> systems = new ArrayList<JSONObject>();
    modelObjects.keySet().forEach(
      object -> {
        JSONObject modelObject = (JSONObject) modelObjects.get(object);
        String type = getValue(modelObject, "type", "");

        if ("system".equals(type)) {
          systems.add(modelObject);
        }
      }
    );
    return systems;
  }



  /** Generate a diagram for the IcePanel JSON file.
   *
   * @param icePanelJSONFile The JSON file exported from IcePanel to be converted
   * @param configurationFile The configuration file with the list
   *                          of packages and classes to exclude.
   */
  public void convertIcePanelToUML(final String icePanelJSONFile,
                                   final JSONObject icePanelDiagramJSON,
                                   final String configurationFile) {
    Debugger.debug(2, "convertIcePanelToUML [" + icePanelJSONFile + "] (jsonObject) [" + configurationFile + "]");

    String rootName = findRoot(icePanelDiagramJSON);

    generateHeader(icePanelJSONFile, configurationFile);
    generateClasses(icePanelDiagramJSON);
    generateConnections(icePanelDiagramJSON, rootName);
    generateFooter();

    generateSubDiagram(icePanelDiagramJSON, findSystems(icePanelDiagramJSON));
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

    JSONObject icePanelDiagramJSON = loadIcePanelJsonFromFile(icePanelJSONFile);
    if (icePanelDiagramJSON == null) {
      System.err.println("IcePanel JSON file not loaded");
      return;
    }

    convertIcePanelToUML(icePanelJSONFile, icePanelDiagramJSON, configurationFile);
  }

}
