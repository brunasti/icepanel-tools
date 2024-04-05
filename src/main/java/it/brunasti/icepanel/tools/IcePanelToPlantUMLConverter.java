package it.brunasti.icepanel.tools;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
    generateSubDiagramHeader(output,icePanelJSONFile,configurationFile);
//    Debugger.debug(2, "generateHeader() ------------------");
//    output.println("@startuml");
//    output.println(
//"""
//!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Context.puml
//!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Component.puml
//' uncomment the following line and comment the first to use locally
//' !include C4_Context.puml
//""");
//    output.println("'https://plantuml.com/class-diagram");
//    output.println();
//    output.println("' CONVERT ICEPANEL DIAGRAM ===========");
//    output.println("' Converter          : " + this.getClass().getName());
//    output.println("' IcePanel JSON File : [" + icePanelJSONFile + "]");
//    output.println("' Configuration      : [" + configurationFile + "]");
//    output.println("' Generated at       : " + new Date());
//    String includeFileContent = Utils.readFileToString(includeFileName);
//    if (!includeFileContent.isBlank()) {
//      output.println();
//      output.println("' Include         : [" + includeFileName + "] ---------");
//      output.println(includeFileContent);
//      output.println("' Include end     : --------------------------");
//    }
//    output.println();
  }

  private void generateFooter() {
    generateFooter(output);
  }

  private void generateFooter(final PrintStream output) {
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
              generateClassInDiagram(output, modelObject);
      }
    );
    output.println();
  }


  private ArrayList<JSONObject> extractChildren(final JSONObject icePanelDiagramJSON, final JSONObject parent) {
    Debugger.debug(2, "extractChildren(" + parent + ") ------------------");

    String givenParentId = getValue(parent, "id");
    Debugger.debug(2, "extractChildren(" + givenParentId + ") ------------------");
    JSONObject modelObjects = (JSONObject)icePanelDiagramJSON.get("modelObjects");
    ArrayList<JSONObject> children = new ArrayList<JSONObject>();
    modelObjects.keySet().forEach(
      object -> {
        JSONObject modelObject = (JSONObject) modelObjects.get(object);
        String type = getValue(modelObject, "type", "");

        if (!"root".equals(type)) {
          String parentId = getValue(modelObject, "parentId");

          if (givenParentId.equals(parentId)) {
            Debugger.debug(2, "extractChildren add object (" + object + ") ------------------");
            children.add(modelObject);
          }
        }
      }
    );
    return children;
  }

  private void generateSubDiagramHeader(final PrintStream output,
                                        final String icePanelJSONFile,
                                        final String configurationFile) {
    Debugger.debug(2, "generateHeader() ------------------");
    output.println("@startuml");
    output.println(
"""
!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Context.puml
!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Component.puml
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


  private void generateSubDiagramBoundaryEnd(final PrintStream output) {
    output.println("}");
  }

  private void generateSubDiagramBoundaryStart(final PrintStream output,
                                       final JSONObject jsonObject) {
    Debugger.debug(2, "generateSubDiagramBoundary() ------------------");
    String id = getValue(jsonObject, "id");
    String name = getValue(jsonObject, "name");
    String type = getValue(jsonObject, "type", "");
    String description = getValue(jsonObject,"description", " ");

    if ("system".equals(type)) {
      output.println("System_Boundary(" + id + ", \"" + name + "\" ) {");
    } else if ("actor".equals(type)) {
      output.println("Person(" + id + ", \"" + name + "\", \"" + description + "\" ) { ");
    } else if ("app".equals(type)) {
      output.println("Component(" + id + ", \"" + name + "\", \"" + description + "\" ) { ");
    } else if ("store".equals(type)) {
      output.println("ContainerDb(" + id + ", \"" + name + "\", \"" + description + "\", \"\" ) { ");
    } else if ("area".equals(type)) {
      output.println("System_Boundary(" + id + ", \"" + name + "\", \"" + description + "\", \"\" ) { ");
    } else if ("component".equals(type)) {
      // TODO: Add technologies
      output.println("Container(" + id + ", \"" + name + "\", \"" + description + "\", \"\" ) { ");
    } else {
      Debugger.debug(2, "unknown type : [" + type + "]");
    }
  }

  private void generateClassInDiagram(final PrintStream output,
                                      final JSONObject jsonObject) {
    generateClassInDiagram("", output, jsonObject);
  }
  private void generateClassInDiagram(final String head,
                                      final PrintStream output,
                                      final JSONObject jsonObject) {
    Debugger.debug(2, "generateClassInDiagram() ------------------");
    String id = getValue(jsonObject, "id");
    String name = getValue(jsonObject, "name");
    String type = getValue(jsonObject, "type", "");
    String description = getValue(jsonObject,"description", " ");

    if ("system".equals(type)) {
      output.println(head + "System(" + id + ", \"" + name + "\", \"" + description + "\" ) ");
    } else if ("actor".equals(type)) {
      output.println(head + "Person(" + id + ", \"" + name + "\", \"" + description + "\" ) ");
    } else if ("app".equals(type)) {
      output.println(head + "Component(" + id + ", \"" + name + "\", \"" + description + "\" ) ");
    } else if ("store".equals(type)) {
      output.println(head + "ContainerDb(" + id + ", \"" + name + "\", \"" + description + "\", \"\" ) ");
    } else if ("area".equals(type)) {
      output.println(head + "System_Boundary(" + id + ", \"" + name + "\", \"" + description + "\", \"\" ) ");
    } else if ("component".equals(type)) {
      // TODO: Add technologies
      output.println(head + "Container(" + id + ", \"" + name + "\", \"" + description + "\", \"\" ) ");
    } else {
      Debugger.debug(2, "unknown type : [" + type + "]");
    }
  }

  private void generateSubDiagramClasses(final PrintStream output,
                                         final JSONObject icePanelDiagramJSON,
                                         final ArrayList<JSONObject> children) {
    Debugger.debug(2, "generateSubDiagramClasses() ------------------");
    output.println();
    generateSubDiagramBoundaryStart(output,icePanelDiagramJSON);
    output.println("    ' CLASSES =======");
    children.forEach(
      object -> {
        generateClassInDiagram("    ", output, object);
      }
    );
    generateSubDiagramBoundaryEnd(output);
    output.println();
  }



  private void generateSubDiagram(
          final String icePanelJSONFile,
          final JSONObject icePanelDiagramJSON,
          final JSONObject base,
          final String configurationFile,
          final String subOutputFileNameBase) {
    Debugger.debug(2, "generateSubDiagram() ------------------");

    String name = getValue(base, "name");
    String id = getValue(base, "id");
    Debugger.debug(2, "generateSubDiagram (" + id + ", " + name + ") ---------");

    String toFileName = subOutputFileNameBase + "-" + name + ".puml";
    Debugger.debug(2, "==== generateSubDiagram to file (" + toFileName + ") ---------");

    ArrayList<JSONObject> children = extractChildren(icePanelDiagramJSON, base);

    PrintStream output = System.out;

    if ((null != toFileName) && (!toFileName.isBlank())) {
      try {
        // Creates a FileOutputStream
        FileOutputStream file = new FileOutputStream(toFileName);

        // Creates a PrintWriter
        output = new PrintStream(file, true);

        generateSubDiagramHeader(output, icePanelJSONFile, configurationFile);

        generateSubDiagramClasses(output, base, children);

        generateFooter(output);

        output.close();
      } catch (FileNotFoundException fnf) {
        fnf.printStackTrace();
        return;
      }
    }

    Debugger.debug(2, "==== generateSubDiagram to file (" + toFileName + ") END ---------");
  }


  private void generateSubDiagrams(
          final String icePanelJSONFile,
          final JSONObject icePanelDiagramJSON,
          final ArrayList<JSONObject> bases,
          final String configurationFile,
          final String subOutputFileNameBase) {
    Debugger.debug(2, "generateSubDiagrams() ------------------");

    bases.forEach( base -> {
      generateSubDiagram(icePanelJSONFile, icePanelDiagramJSON, base, configurationFile, subOutputFileNameBase);
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
                                   final String configurationFile,
                                   final String subOutputFileNameBase) {
    Debugger.debug(2, "convertIcePanelToUML [" + icePanelJSONFile + "] (jsonObject) [" + configurationFile + "] [" +subOutputFileNameBase + "]");

    String rootName = findRoot(icePanelDiagramJSON);

    generateHeader(icePanelJSONFile, configurationFile);
    generateClasses(icePanelDiagramJSON);
    generateConnections(icePanelDiagramJSON, rootName);
    generateFooter();

    generateSubDiagrams(icePanelJSONFile, icePanelDiagramJSON, findSystems(icePanelDiagramJSON), configurationFile, subOutputFileNameBase);
  }



  /** Generate a diagram for the IcePanel JSON file.
   *
   * @param icePanelJSONFile The JSON file exported from IcePanel to be converted
   * @param configurationFile The configuration file with the list
   *                          of packages and classes to exclude.
   */
  public void convertIcePanelToUML(final String icePanelJSONFile,
                                   final String configurationFile,
                                   final String subOutputFileNameBase) {
    cleanLocalVars();
    Debugger.debug(2, "convertIcePanelToUML [" + icePanelJSONFile + "][" + configurationFile + "][" + subOutputFileNameBase + "]");

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

    convertIcePanelToUML(icePanelJSONFile, icePanelDiagramJSON, configurationFile, subOutputFileNameBase);
  }

}
