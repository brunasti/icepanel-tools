package it.brunasti.icepanel.tools;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 */
public class IcePanelToPlantUMLConverter {

  public static final String ORIGIN_ID = "originId";
  public static final String TARGET_ID = "targetId";
  public static final String DIRECTION = "direction";
  public static final String MODEL_CONNECTIONS = "modelConnections";
  public static final String MODEL_OBJECTS = "modelObjects";
  public static final String PARENT_IDS = "parentIds";

  public static final String TYPE_SYSTEM = "system";
  public static final String TYPE_ACTOR = "actor";
  public static final String TYPE_APP = "app";
  public static final String TYPE_STORE = "store";
  public static final String TYPE_AREA = "area";
  public static final String TYPE_COMPONENT = "component";

  public static final String SYSTEM_BOUNDARY = "System_Boundary(";
  public static final String PERSON = "Person(";
  public static final String COMPONENT = "Component(";
  public static final String CONTAINER_DB = "ContainerDb(";
  public static final String CONTAINER = "Container(";
  public static final String DEBUG_ENDING_STRING = ") ------------------";


  // Reference to a PrintStream to be used for the diagram
  // By default is the Standard.out, but it can be redirected
  // to a file.
  private final PrintStream output;

  private String includeFileName = "";
  private String outputPath = "./";

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
    setDefaultConfiguration();
  }


  // ------------------------------------------------------
  // Load and manage Configuration ------------------------

  private void setDefaultConfiguration() {
    includeFileName = "";
    outputPath = "";
  }

  private void loadJsonConfiguration(JSONObject jsonObject) {
    loadIncludeFileConfiguration(jsonObject);
    loadOutputPathConfiguration(jsonObject);
  }

  private void loadIncludeFileConfiguration(JSONObject jsonObject) {
    Object includeFile = jsonObject.get("includeFile");
    if (includeFile != null) {
      includeFileName = includeFile.toString();
      Debugger.debug(4, "  - includeFile [" + includeFileName + "]");
    }
  }

  private void loadOutputPathConfiguration(JSONObject jsonObject) {
    Object outputPathObj = jsonObject.get("outputPath");
    if (outputPathObj != null) {
      outputPath = outputPathObj.toString();
      if (!outputPath.endsWith("/")) {
        outputPath = outputPath + "/";
      }
      Debugger.debug(4, "  - outputPath [" + outputPath + "]");
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

  private JSONObject loadIcePanelJsonFromFile(String icePanelJsonFileName) {
    Debugger.debug(1, "loadIcePanelJsonFromFile : " + icePanelJsonFileName);
    JSONObject jsonObject = Utils.loadJsonFile(icePanelJsonFileName);
    if (null == jsonObject) {
      Debugger.debug(2,
              "loadIcePanelJsonFromFile : no data in config file " + icePanelJsonFileName);
      return null;
    }
    return jsonObject;
  }


  private void generateHeader(final String icePanelJsonFile,
                              final String configurationFile) {
    generateSubDiagramHeader(output, icePanelJsonFile, configurationFile);
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
    if (name.isBlank()) {
      name = object.toString();
    }
    return name;
  }

  private void generateConnections(final JSONObject icePanelDiagramJson, String rootName) {
    Debugger.debug(2, "generateConnections() ------------------");
    output.println();
    output.println("' CONNECTIONS =======");
    JSONObject modelConnections = (JSONObject) icePanelDiagramJson.get(MODEL_CONNECTIONS);
    modelConnections.keySet().forEach(
            object -> {
              JSONObject modelObject = (JSONObject) modelConnections.get(object);
              String name = getName(object, modelObject);
              String source = getValue(modelObject, ORIGIN_ID);
              String target = getValue(modelObject, TARGET_ID);
              String direction = getValue(modelObject, DIRECTION);
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
    JSONObject modelObjects = (JSONObject) icePanelDiagramJson.get(MODEL_OBJECTS);
    modelObjects.keySet().forEach(
            object -> {
              JSONObject modelObject = (JSONObject) modelObjects.get(object);
              JSONArray parentIds = (JSONArray) modelObject.get(PARENT_IDS);

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

  // output, base, children, neighbors
  private void generateSubDiagramLinks(
          final PrintStream output,
          final JSONObject icePanelDiagramJson,
          ArrayList<JSONObject> children,
          ArrayList<JSONObject> neighbors) {
    Debugger.debug(2, "generateSubDiagramLinks() ------------------");

    HashMap<String, JSONObject> childrenMap = new HashMap<>();
    children.forEach(jsonObject -> {
      String id = getValue(jsonObject, "id", "");
      childrenMap.put(id, jsonObject);
    });

//    // Map of the neighbors to add and check existence
//    HashMap<String, JSONObject> neighborsMap = new HashMap<>();
//    neighbors.forEach(jsonObject -> {
//      String id = getValue(jsonObject, "id", "");
//      neighborsMap.put(id, jsonObject);
//    });

    output.println();
    output.println("' CONNECTIONS =======");
    JSONObject modelConnections = (JSONObject) icePanelDiagramJson.get(MODEL_CONNECTIONS);
    modelConnections.keySet().forEach(
            object -> {
              JSONObject connectionObject = (JSONObject) modelConnections.get(object);
              String name = getName(object, connectionObject);
              String source = getValue(connectionObject, ORIGIN_ID);
              String target = getValue(connectionObject, TARGET_ID);
              String direction = getValue(connectionObject, DIRECTION);

              JSONObject sourceObject = getObject(icePanelDiagramJson, source);
              JSONObject targetObject = getObject(icePanelDiagramJson, target);

              if (childrenMap.containsValue(sourceObject)
                      || childrenMap.containsValue(targetObject)) {
                if ("outgoing".equals(direction)) {
                  output.println("Rel(" + source + ", " + target
                          + ", \"" + name + "\" )");
                } else if ("bidirectional".equals(direction)) {
                  output.println("Rel(" + source + ", " + target
                          + ", \"" + name + "\" )");
                  output.println("Rel(" + target + ", " + source
                          + ", \"Return of " + name + "\" )");
                }
              }
            }
    );
    output.println();
  }

  private void generateClasses(final JSONObject icePanelDiagramJson) {
    Debugger.debug(2, "generateClasses() ------------------");
    output.println();
    output.println("' CLASSES =======");
    JSONObject modelObjects = (JSONObject) icePanelDiagramJson.get(MODEL_OBJECTS);
    modelObjects.keySet().forEach(
            object -> {
              JSONObject modelObject = (JSONObject) modelObjects.get(object);
              generateClassInDiagram(output, modelObject);
      }
    );
    output.println();
  }


  private ArrayList<JSONObject> extractChildren(
          final JSONObject icePanelDiagramJson, 
          final JSONObject parent) {
    Debugger.debug(2, "extractChildren(" + parent + DEBUG_ENDING_STRING);

    String givenParentId = getValue(parent, "id");
    Debugger.debug(2, "extractChildren(" + givenParentId + DEBUG_ENDING_STRING);
    JSONObject modelObjects = (JSONObject) icePanelDiagramJson.get(MODEL_OBJECTS);
    ArrayList<JSONObject> children = new ArrayList<>();
    modelObjects.keySet().forEach(
        object -> {
          JSONObject modelObject = (JSONObject) modelObjects.get(object);
          String type = getValue(modelObject, "type", "");

          if (!"root".equals(type)) {
            String parentId = getValue(modelObject, "parentId");

            if (givenParentId.equals(parentId)) {
              Debugger.debug(2, "extractChildren add object (" + object + DEBUG_ENDING_STRING);
              children.add(modelObject);
            }
          }
        }
    );
    return children;
  }


  private JSONObject getObject(final JSONObject icePanelDiagramJson, String objectId) {
    JSONObject modelObjects = (JSONObject) icePanelDiagramJson.get(MODEL_OBJECTS);
    return (JSONObject) modelObjects.get(objectId);
  }

  private ArrayList<JSONObject> extractNodeNeighbors(
          final JSONObject icePanelDiagramJson,
          final JSONObject child) {
    ArrayList<JSONObject> neighbors = new ArrayList<>();

    String childId = getValue(child, "id");

    Debugger.debug(2, "extractNodeNeighbors( " + childId + DEBUG_ENDING_STRING);
    JSONObject modelConnections = (JSONObject) icePanelDiagramJson.get(MODEL_CONNECTIONS);
    modelConnections.keySet().forEach(
        object -> {
          JSONObject modelObject = (JSONObject) modelConnections.get(object);
          String source = getValue(modelObject, ORIGIN_ID);
          String target = getValue(modelObject, TARGET_ID);

          if (childId.equals(source) || childId.equals(target)) {
            Debugger.debug(2, "   extractNodeNeighbors ["
                    + source + "][" + target + "]------");
            JSONObject jsonObject;
            if (childId.equals(source)) {
              jsonObject = getObject(icePanelDiagramJson, target);
            } else {
              jsonObject = getObject(icePanelDiagramJson, source);
            }
            Debugger.debug(2, "   ---> extractNodeNeighbors add ["
                    + childId + "][" + jsonObject + "]---");
            neighbors.add(jsonObject);
          }
        }
    );

    return neighbors;
  }

  private ArrayList<JSONObject> extractNeighbors(
          final JSONObject icePanelDiagramJson,
          final JSONObject parent,
          final ArrayList<JSONObject> children) {
    Debugger.debug(2, "extractNeighbors(" + parent + DEBUG_ENDING_STRING);

    String givenParentId = getValue(parent, "id");
    Debugger.debug(2, "extractNeighbors(" + givenParentId + DEBUG_ENDING_STRING);

    // Definition of neighbors: not part of the group, but connected to one of the group

    // Map of children to facilitate checks
    HashMap<String, JSONObject> childrenMap = new HashMap<>();
    children.forEach(jsonObject -> {
      String id = getValue(jsonObject, "id", "");
      childrenMap.put(id, jsonObject);
    });

    // Map of the neighbors to add and check existence
    HashMap<String, JSONObject> neighborsMap = new HashMap<>();

    // For each child search for its neighbors
    children.forEach(child -> {
      Debugger.debug(2, "extractNeighbors of child (" + child + ") --------");
      ArrayList<JSONObject> childNeighbors
              = extractNodeNeighbors(icePanelDiagramJson, child);
      childNeighbors.forEach(neighbor -> {
        String neighborId = getValue(neighbor, "id");
        Debugger.debug(2, "   extractNeighbors add object (" 
                + neighborId + "," + neighbor + DEBUG_ENDING_STRING);
        neighborsMap.put(neighborId, neighbor);
      });
    });

    ArrayList<JSONObject> neighbors = new ArrayList<>();
    neighborsMap.values().forEach(
        object -> {
          Debugger.debug(2, "extractNeighbors LAST add object (" + object + ") -------");
          String objectId = getValue(object, "id");
          // Check if it's one of the children
          JSONObject child = childrenMap.get(objectId);
          if (child == null) {
            neighbors.add(object);
          }
        }
    );
    return neighbors;
  }

  private void generateSubDiagramHeader(final PrintStream output,
                                        final String icePanelJsonFile,
                                        final String configurationFile) {
    Debugger.debug(2, "generateHeader() ------------------");
    output.println("@startuml");
    output.println("'https://plantuml.com/class-diagram");
    output.println("' CONVERT ICEPANEL DIAGRAM ===========");
    output.println("' Converter          : " + this.getClass().getName());
    output.println("' IcePanel JSON File : [" + icePanelJsonFile + "]");
    output.println("' Configuration      : [" + configurationFile + "]");
    output.println("' Generated at       : " + new Date());
    output.println();
    output.println(
        """
        !include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Context.puml
        !include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Component.puml
        """);
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
    String description = getValue(jsonObject, "description", " ");

    if (TYPE_SYSTEM.equals(type)) {
      output.println(SYSTEM_BOUNDARY
              + id + ", \"" + name + "\" ) {");
    } else if (TYPE_ACTOR.equals(type)) {
      output.println(PERSON
              + id + ", \"" + name + "\", \"" + description + "\" ) { ");
    } else if (TYPE_APP.equals(type)) {
      output.println(COMPONENT
              + id + ", \"" + name + "\", \"" + description + "\" ) { ");
    } else if (TYPE_STORE.equals(type)) {
      output.println(CONTAINER_DB
              + id + ", \"" + name + "\", \"" + description + "\", \"\" ) { ");
    } else if (TYPE_AREA.equals(type)) {
      output.println(SYSTEM_BOUNDARY
              + id + ", \"" + name + "\", \"" + description + "\", \"\" ) { ");
    } else if (TYPE_COMPONENT.equals(type)) {
      // TODO: Add technologies
      output.println(CONTAINER
              + id + ", \"" + name + "\", \"" + description + "\", \"\" ) { ");
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
    String description = getValue(jsonObject, "description", " ");

    if (TYPE_SYSTEM.equals(type)) {
      output.println(head + "System(" + id + ", \"" + name
              + "\", \"" + description + "\" ) ");
    } else if (TYPE_ACTOR.equals(type)) {
      output.println(head + PERSON + id + ", \"" + name
              + "\", \"" + description + "\" ) ");
    } else if (TYPE_APP.equals(type)) {
      output.println(head + COMPONENT + id + ", \"" + name
              + "\", \"" + description + "\" ) ");
    } else if (TYPE_STORE.equals(type)) {
      output.println(head + CONTAINER_DB + id + ", \"" + name
              + "\", \"" + description + "\", \"\" ) ");
    } else if (TYPE_AREA.equals(type)) {
      output.println(head + SYSTEM_BOUNDARY + id + ", \"" + name
              + "\", \"" + description + "\", \"\" ) ");
    } else if (TYPE_COMPONENT.equals(type)) {
      // TODO: Add technologies
      output.println(head + CONTAINER + id + ", \"" + name
              + "\", \"" + description + "\", \"\" ) ");
    } else {
      Debugger.debug(2, "unknown type : [" + type + "]");
    }
  }

  private void generateSubDiagramClasses(final PrintStream output,
                                         final JSONObject icePanelDiagramJson,
                                         final ArrayList<JSONObject> children) {
    Debugger.debug(2, "generateSubDiagramClasses() ------------------");
    output.println();
    generateSubDiagramBoundaryStart(output, icePanelDiagramJson);
    output.println("    ' CLASSES =======");
    children.forEach(
        object -> {
          generateClassInDiagram("    ", output, object);
        }
    );
    generateSubDiagramBoundaryEnd(output);
    output.println();
  }


  private void generateSubDiagramNeighborClasses(final PrintStream output,
                                         final ArrayList<JSONObject> children) {
    Debugger.debug(2, "generateSubDiagramNeighborClasses() ------------------");
    output.println();
    output.println("' NEIGHBOR CLASSES =======");
    children.forEach(
        object -> {
          generateClassInDiagram("", output, object);
        }
    );
    output.println();
  }



  private void generateSubDiagram(
          final String icePanelJsonFile,
          final JSONObject icePanelDiagramJson,
          final JSONObject base,
          final String configurationFile,
          final String subOutputFileNameBase) {
    // TODO: Reiterate in sub sub diagrams (as BAHPCA API Component Diagram)
    Debugger.debug(2, "generateSubDiagram() ------------------");

    String name = getValue(base, "name");
    String id = getValue(base, "id");
    Debugger.debug(2, "generateSubDiagram (" + id + ", " + name + ") ---------");

    String toFileName = subOutputFileNameBase + "-" + name + ".puml";
    Debugger.debug(2, "==== generateSubDiagram to file (" + toFileName + ") ---------");

    ArrayList<JSONObject> children = extractChildren(icePanelDiagramJson, base);

    PrintStream output;

    try {
      // Creates a FileOutputStream
      FileOutputStream file = new FileOutputStream(toFileName);

      // Creates a PrintWriter
      output = new PrintStream(file, true);
    } catch (FileNotFoundException fnf) {
      fnf.printStackTrace();
      return;
    }

    ArrayList<JSONObject> neighbors = extractNeighbors(icePanelDiagramJson, base, children);

    generateSubDiagramHeader(output, icePanelJsonFile, configurationFile);
    generateSubDiagramClasses(output, base, children);
    generateSubDiagramNeighborClasses(output, neighbors);
    generateSubDiagramLinks(output, icePanelDiagramJson, children, neighbors);
    generateFooter(output);

    output.close();

    Debugger.debug(2, "==== generateSubDiagram to file (" + toFileName + ") END ---------");
  }


  private void generateSubDiagrams(
          final String icePanelJsonFile,
          final JSONObject icePanelDiagramJson,
          final ArrayList<JSONObject> bases,
          final String configurationFile,
          final String subOutputFileNameBase) {
    Debugger.debug(2, "generateSubDiagrams() ------------------");

    bases.forEach(base -> {
      generateSubDiagram(icePanelJsonFile,
              icePanelDiagramJson,
              base,
              configurationFile,
              subOutputFileNameBase);
    });

  }


  private String findRoot(final JSONObject icePanelDiagramJson) {
    Debugger.debug(2, "findRoot() ------------------");
    JSONObject modelObjects = (JSONObject) icePanelDiagramJson.get(MODEL_OBJECTS);
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

  private ArrayList<JSONObject> findSystems(final JSONObject icePanelDiagramJson) {
    Debugger.debug(2, "findSystems() ------------------");
    JSONObject modelObjects = (JSONObject) icePanelDiagramJson.get(MODEL_OBJECTS);
    ArrayList<JSONObject> systems = new ArrayList<>();
    modelObjects.keySet().forEach(
        object -> {
          JSONObject modelObject = (JSONObject) modelObjects.get(object);
          String type = getValue(modelObject, "type", "");

          if (TYPE_SYSTEM.equals(type)) {
            systems.add(modelObject);
          }
        }
    );
    return systems;
  }



  /** Generate a diagram for the IcePanel JSON file.
   *
   * @param icePanelJsonFile The JSON file exported from IcePanel to be converted
   * @param configurationFile The configuration file with the list
   *                          of packages and classes to exclude.
   */
  public void convertIcePanelToUml(final String icePanelJsonFile,
                                   final JSONObject icePanelDiagramJson,
                                   final String configurationFile,
                                   final String subOutputFileNameBase) {
    Debugger.debug(2, "convertIcePanelToUML [" + icePanelJsonFile
            + "] (jsonObject) [" + configurationFile
            + "] [" + subOutputFileNameBase + "]");

    String rootName = findRoot(icePanelDiagramJson);

    generateHeader(icePanelJsonFile, configurationFile);
    generateClasses(icePanelDiagramJson);
    generateConnections(icePanelDiagramJson, rootName);
    generateFooter();

    generateSubDiagrams(icePanelJsonFile,
            icePanelDiagramJson,
            findSystems(icePanelDiagramJson),
            configurationFile,
            subOutputFileNameBase);
  }


  /** Generate a diagram for the IcePanel JSON file.
   *
   * @param icePanelJsonFile The JSON file exported from IcePanel to be converted
   * @param configurationFile The configuration file with the list
   *                          of packages and classes to exclude.
   */
  public void convertIcePanelToUml(final String icePanelJsonFile,
                                   final String configurationFile,
                                   final String subOutputFileNameBase) {
    cleanLocalVars();
    Debugger.debug(2, "convertIcePanelToUML [" + icePanelJsonFile
            + "][" + configurationFile
            + "][" + subOutputFileNameBase + "]");

    boolean initiated = loadConfiguration(configurationFile);
    if (!initiated) {
      System.err.println("Configuration JSON not loaded");
      return;
    }

    JSONObject icePanelDiagramJson = loadIcePanelJsonFromFile(icePanelJsonFile);
    if (icePanelDiagramJson == null) {
      System.err.println("IcePanel JSON file not loaded");
      return;
    }

    convertIcePanelToUml(icePanelJsonFile,
            icePanelDiagramJson,
            configurationFile,
            outputPath + subOutputFileNameBase);
  }

}
