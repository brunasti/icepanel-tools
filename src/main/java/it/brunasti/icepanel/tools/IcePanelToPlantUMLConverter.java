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
public class IcePanelToPlantUMLConverter implements IcePanelConstants {


  public static final String FLOW_STEPS = "steps";
  // Reference to a PrintStream to be used for the diagram
  // By default is the Standard.out, but it can be redirected
  // to a file.
  private final PrintStream output;

  private String includeFileName = "";
  private String includeFlowFileName = "";
  private String outputPath = "./";
  private boolean generateSubDiagrams = false;

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
    includeFlowFileName = "";
    outputPath = "";
    generateSubDiagrams = false;
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
    Object includeFlowFile = jsonObject.get("includeFlowFile");
    if (includeFlowFile != null) {
      includeFlowFileName = includeFlowFile.toString();
      Debugger.debug(4, "  - includeFlowFile [" + includeFlowFileName + "]");
    }
  }

  private void loadOutputPathConfiguration(JSONObject jsonObject) {
    Object outputPathObj = jsonObject.get("outputPath");
    if (outputPathObj != null) {
      outputPath = outputPathObj.toString();
      if (!outputPath.endsWith("/")) {
        outputPath = outputPath + "/";
      }
      generateSubDiagrams = true;
      Debugger.debug(4, "  - outputPath [" + outputPath + "]");
    }
  }


  private boolean loadJsonConfigurationFromFile(String configurationFileName) {
    Debugger.debug(1, "loadJsonConfigurationFromFile : " + configurationFileName);
    JSONObject jsonObject = Utils.loadJsonFile(configurationFileName);
    if (null == jsonObject) {
      Debugger.debug(2,
              "loadJsonConfigurationFromFile : no data in config file " + configurationFileName);
      generateSubDiagrams = false;
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
    if (modelObject.get(key) != null) {
      String value = modelObject.get(key).toString();
      value = value.replace("\n", nlReplace);
      return value;
    } else {
      return null;
    }
  }

  private String getName(Object object, final JSONObject modelObject) {
    String name = getValue(modelObject, "name");
    if ((name == null) || (name.isBlank())) {
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

              if (parentIds != null) {
                parentIds.forEach(
                        parentId -> {
                          if (!rootName.equals(parentId)) {
                            output.println("  " + parentId + " <.. " + object + " ");
                          }
                        }
                );
              }
            }
    );
    output.println();
  }

  // output, base, children, neighbors
  private void generateSubDiagramLinks(
          final PrintStream output,
          final JSONObject icePanelDiagramJson,
          ArrayList<JSONObject> children) {
    Debugger.debug(2, "generateSubDiagramLinks() ------------------");

    HashMap<String, JSONObject> childrenMap = new HashMap<>();
    children.forEach(jsonObject -> {
      String id = getValue(jsonObject, "id", "");
      childrenMap.put(id, jsonObject);
    });

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
    Debugger.debug(2, "generateSubDiagramHeader() ------------------");
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

  private void generateFlowDiagramHeader(final PrintStream output,
                                         final String icePanelJsonFile,
                                         final String configurationFile,
                                         final String title) {
    Debugger.debug(2, "generateFlowDiagramHeader() ------------------");
    output.println("@startuml");
    output.println("' CONVERT ICEPANEL FLOW DIAGRAM ===========");
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
    output.println("title " + title);
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
    } else if (TYPE_ROOT.equals(type)) {
      output.println(SYSTEM_BOUNDARY
              + id + ", \"" + name + "\" ) {");
    } else if (TYPE_ACTOR.equals(type)) {
      output.println(PERSON
              + id + ", \"" + name + OUTPUT_VAL_SEPARATOR_STRING + description + OUTPUT_SUBDIAGRAM_CLOSER_STRING);
    } else if (TYPE_APP.equals(type)) {
      output.println(COMPONENT
              + id + ", \"" + name + OUTPUT_VAL_SEPARATOR_STRING + description + OUTPUT_SUBDIAGRAM_CLOSER_STRING);
    } else if (TYPE_STORE.equals(type)) {
      output.println(CONTAINER_DB
              + id + ", \"" + name + OUTPUT_VAL_SEPARATOR_STRING + description + OUTPUT_VAL_SEPARATOR_STRING + OUTPUT_SUBDIAGRAM_CLOSER_STRING);
    } else if (TYPE_AREA.equals(type)) {
      output.println(SYSTEM_BOUNDARY
              + id + ", \"" + name + OUTPUT_VAL_SEPARATOR_STRING + description + OUTPUT_VAL_SEPARATOR_STRING + OUTPUT_SUBDIAGRAM_CLOSER_STRING);
    } else if (TYPE_COMPONENT.equals(type)) {
      // TODO: Add technologies
      output.println(CONTAINER
              + id + ", \"" + name + OUTPUT_VAL_SEPARATOR_STRING + description + OUTPUT_VAL_SEPARATOR_STRING + OUTPUT_SUBDIAGRAM_CLOSER_STRING);
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
    if ((name == null) || (name.isBlank())) {
      name = id;
    }
    String type = getValue(jsonObject, "type", "");
    String description = getValue(jsonObject, "description", " ");

    if (TYPE_SYSTEM.equals(type)) {
      output.println(head + "System(" + id + ", \"" + name
              + OUTPUT_VAL_SEPARATOR_STRING + description + OUTPUT_VAL_CLOSER_STRING);
    } else if (TYPE_ACTOR.equals(type)) {
      output.println(head + PERSON + id + ", \"" + name
              + OUTPUT_VAL_SEPARATOR_STRING + description + OUTPUT_VAL_CLOSER_STRING);
    } else if (TYPE_APP.equals(type)) {
      output.println(head + COMPONENT + id + ", \"" + name
              + OUTPUT_VAL_SEPARATOR_STRING + description + OUTPUT_VAL_CLOSER_STRING);
    } else if (TYPE_STORE.equals(type)) {
      output.println(head + CONTAINER_DB + id + ", \"" + name
              + OUTPUT_VAL_SEPARATOR_STRING + description + OUTPUT_VAL_SEPARATOR_STRING + OUTPUT_VAL_CLOSER_STRING);
    } else if (TYPE_AREA.equals(type)) {
      Debugger.debug(2, "skip type : [" + type + "]");
    } else if (TYPE_COMPONENT.equals(type)) {
      // TODO: Add technologies
      output.println(head + CONTAINER + id + ", \"" + name
              + OUTPUT_VAL_SEPARATOR_STRING + description + OUTPUT_VAL_SEPARATOR_STRING + OUTPUT_VAL_CLOSER_STRING);
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

  private void generateFlowDiagramEntities(final PrintStream output,
                                         final JSONObject icePanelDiagramJson,
                                         final JSONObject flow) {
    Debugger.debug(2, "generateFlowDiagramEntities() ------------------");
    output.println();
    JSONObject steps = (JSONObject)flow.get(FLOW_STEPS);
    output.println("' Elements =======");
    Debugger.debug(3, "Steps : " + steps);

    steps.values().forEach( step -> {
        Debugger.debug(3, "   Step - : " + step);
        output.println("' Step (" + step + ")");
            });

    HashMap<String, JSONObject> entities = new HashMap<>();
    steps.values().forEach( object -> {
      JSONObject step = (JSONObject) object;
      String originId = getValue(step, ORIGIN_ID);
      Debugger.debug(3, "   Step - originId : " + originId);
      JSONObject origin = getObject(icePanelDiagramJson, originId);
      entities.put(originId, origin);

      String targetId = getValue(step, TARGET_ID);
      Debugger.debug(3, "   Step - targetId : " + targetId);
      JSONObject target = getObject(icePanelDiagramJson, targetId);
      entities.put(targetId, target);

            });

    entities.values().forEach( entity -> {
        output.println("participant \"" + getValue(entity, NAME) + "\" as " + getValue(entity, ID)+ " ");
    });

    output.println("' Steps =======");

    steps.values().forEach( object -> {
      JSONObject step = (JSONObject) object;
      output.println("" + getValue(step, ORIGIN_ID) + " -> " + getValue(step, TARGET_ID) + " : " + getValue(step, DESCRIPTION));
    });

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

  private int depth(JSONObject icePanelDiagramJson, JSONObject node) {
    Debugger.debug(2, "depth ------------------");
    String id = getValue(node, "id");
    String parentId = getValue(node, PARENT_ID);

    String type = getValue(node, NODE_TYPE);
    if (TYPE_ROOT.equalsIgnoreCase(type)) {
      Debugger.debug(2, "depth ROOT ------------------");
      return 1;
    } else {
      JSONObject parent = getObject(icePanelDiagramJson, parentId);
      int depth = depth(icePanelDiagramJson, parent) + 1;
      Debugger.debug(2, "depth ("+depth+") ------------------");
      return depth;
    }
  }


  private void generateSubDiagram(
          final String icePanelJsonFile,
          final JSONObject icePanelDiagramJson,
          final JSONObject base,
          final String configurationFile,
          final String subOutputFileNameBase) {
    Debugger.debug(2, "generateSubDiagram() ------------------");

    String name = getValue(base, "name");
    String id = getValue(base, "id");
    Debugger.debug(2, "generateSubDiagram (" + id + ", " + name + ") ---------");

    ArrayList<JSONObject> children = extractChildren(icePanelDiagramJson, base);

    if (children.isEmpty()) {
      Debugger.debug(2, "NOT generateSubDiagram for (" + base + ") because no children");
      return;
    }

    int depth = depth(icePanelDiagramJson, base);

    String toFileName = subOutputFileNameBase + "-" + depth + "-" + name + ".puml";
    Debugger.debug(2, "==== generateSubDiagram to file (" + toFileName + ") ---------");

    PrintStream printStream;

    try {
      // Creates a FileOutputStream
      FileOutputStream file = new FileOutputStream(toFileName);

      // Creates a PrintWriter
      printStream = new PrintStream(file, true);
    } catch (FileNotFoundException fnf) {
      fnf.printStackTrace();
      return;
    }

    ArrayList<JSONObject> neighbors = extractNeighbors(icePanelDiagramJson, base, children);

    generateSubDiagramHeader(printStream, icePanelJsonFile, configurationFile);
    generateSubDiagramClasses(printStream, base, children);
    generateSubDiagramNeighborClasses(printStream, neighbors);
    generateSubDiagramLinks(printStream, icePanelDiagramJson, children);
    generateFooter(printStream);

    printStream.close();

    Debugger.debug(2, "==== generateSubDiagram to file (" + toFileName + ") END ---------");
  }


  private void generateFlowDiagram(
          final String icePanelJsonFile,
          final JSONObject icePanelDiagramJson,
          final JSONObject flow,
          final String configurationFile,
          final String subOutputFileNameBase) {
    Debugger.debug(2, "generateFlowDiagram() ------------------");

    String name = getValue(flow, "name");
    String id = getValue(flow, "id");
    Debugger.debug(2, "generateFlowDiagram (" + id + ", " + name + ") ---------");

    String toFileName = subOutputFileNameBase + "-Flow-" + name + ".puml";
    Debugger.debug(2, "==== generateFlowDiagram to file (" + toFileName + ") ---------");

    PrintStream printStream;

    try {
      // Creates a FileOutputStream
      FileOutputStream file = new FileOutputStream(toFileName);

      // Creates a PrintWriter
      printStream = new PrintStream(file, true);
    } catch (FileNotFoundException fnf) {
      fnf.printStackTrace();
      return;
    }

    generateFlowDiagramHeader(printStream, icePanelJsonFile, configurationFile, name);
    generateFlowDiagramEntities(printStream, icePanelDiagramJson, flow);
    generateFooter(printStream);

    printStream.close();

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

  private ArrayList<JSONObject> findFlows(final JSONObject icePanelDiagramJson) {
    Debugger.debug(2, "findFlows() ------------------");
    JSONObject modelObjects = (JSONObject) icePanelDiagramJson.get(MODEL_FLOWS);
    ArrayList<JSONObject> flows = new ArrayList<>();
    modelObjects.keySet().forEach(
        object -> {
          JSONObject modelObject = (JSONObject) modelObjects.get(object);
          flows.add(modelObject);
        }
    );
    return flows;
  }

  private ArrayList<JSONObject> findParents(
          final JSONObject icePanelDiagramJson,
          final String rootName) {
    Debugger.debug(2, "findSystems() ------------------");
    JSONObject modelObjects = (JSONObject) icePanelDiagramJson.get(MODEL_OBJECTS);
    HashMap<String, JSONObject> parentsMap = new HashMap<>();
    modelObjects.keySet().forEach(
        object -> {
          JSONObject modelObject = (JSONObject) modelObjects.get(object);
          if (modelObject != null) {
            String parentId = getValue(modelObject, "parentId");
            if (!rootName.equals(parentId)) {
              JSONObject parent = getObject(icePanelDiagramJson, parentId);
              if (parent != null) {
                parentsMap.put(parentId, parent);
              }
            }
          }
        }
    );
    ArrayList<JSONObject> systems = new ArrayList<>();
    parentsMap.values().forEach(system -> {
        systems.add(system);
    });
    return systems;
  }



  /** Generate a diagram for each flow in the IcePanel JSON file.
   *
   * @param icePanelJsonFile The JSON file exported from IcePanel to be converted
   * @param configurationFile The configuration file with the list
   *                          of packages and classes to exclude.
   * @param subOutputFileNameBase Base part of the name for the sub diagrams
   */
  public void convertIcePanelFlowToUml(final String icePanelJsonFile,
                                   final JSONObject icePanelDiagramJson,
                                   final String configurationFile,
                                   final String subOutputFileNameBase) {
    Debugger.debug(2, "convertIcePanelToUML [" + icePanelJsonFile
            + "] (jsonObject) [" + configurationFile
            + "] [" + subOutputFileNameBase + "]");

    ArrayList<JSONObject> flows = findFlows(icePanelDiagramJson);

    if (generateSubDiagrams) {
      flows.forEach(flow -> {
        generateFlowDiagram(icePanelJsonFile,
                icePanelDiagramJson,
                flow,
                configurationFile,
                subOutputFileNameBase);
      });
    }
  }



  /** Generate a diagram for the IcePanel JSON file.
   *
   * @param icePanelJsonFile The JSON file exported from IcePanel to be converted
   * @param configurationFile The configuration file with the list
   *                          of packages and classes to exclude.
   * @param subOutputFileNameBase Base part of the name for the sub diagrams
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

    if (generateSubDiagrams) {
      generateSubDiagrams(icePanelJsonFile,
              icePanelDiagramJson,
              findParents(icePanelDiagramJson, rootName),
              configurationFile,
              subOutputFileNameBase);
    }
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

    convertIcePanelFlowToUml(icePanelJsonFile,
            icePanelDiagramJson,
            configurationFile,
            outputPath + subOutputFileNameBase);
  }

}
