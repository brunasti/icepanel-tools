package it.brunasti.icepanel.tools;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Convert the JSON export of a project from IcePanel
 * into a set of PlantUML diagrams.
 * For reference see:
 * - <a href="https://icepanel.io/">IcePanel</a>
 * - <a href="https://plantuml.com/">PlantUML</a>
 */
public class IcePanelToPlantUmlConverter {

  static Logger log = LogManager.getLogger(IcePanelToPlantUmlConverter.class);
  
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
  public IcePanelToPlantUmlConverter() {
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
  public IcePanelToPlantUmlConverter(PrintStream output) {
    setDefaultConfiguration();
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
      log.debug("  - includeFile [{}]", includeFileName);
    }
    Object includeFlowFile = jsonObject.get("includeFlowFile");
    if (includeFlowFile != null) {
      includeFlowFileName = includeFlowFile.toString();
      log.debug("  - includeFlowFile [{}]", includeFlowFileName);
    }
  }

  private void loadOutputPathConfiguration(JSONObject jsonObject) {
    Object outputPathObj = jsonObject.get("outputPath");
    if (outputPathObj != null) {
      outputPath = outputPathObj.toString();
      if (!outputPath.endsWith(IcePanelConstants.PATH_DELIMITER_STRING)) {
        outputPath = outputPath + IcePanelConstants.PATH_DELIMITER_STRING;
      }
      generateSubDiagrams = true;
      log.debug("  - outputPath [{}]", outputPath);
    }
  }


  private boolean loadJsonConfigurationFromFile(String configurationFileName) {
    log.debug( "loadJsonConfigurationFromFile : {}", configurationFileName);
    JSONObject jsonObject = Utils.loadJsonFile(configurationFileName);
    if (null == jsonObject) {
      log.debug("loadJsonConfigurationFromFile : no data in config file {}",
              configurationFileName);
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
    log.debug( "loadIcePanelJsonFromFile : {}", icePanelJsonFileName);
    JSONObject jsonObject = Utils.loadJsonFile(icePanelJsonFileName);
    if (null == jsonObject) {
      log.debug("loadIcePanelJsonFromFile : no data in config file {}",
              icePanelJsonFileName);
      // TODO: Convert into an exception
      return null;
    }
    return jsonObject;
  }


  // Generic JSON functions --------------------------------------

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

  private ArrayList<JSONObject> extractChildren(
          final JSONObject icePanelDiagramJson, 
          final JSONObject parent) {
    log.debug("extractChildren({}) ------------------", parent);

    String givenParentId = getValue(parent, "id");
    log.debug("extractChildren({}) ------------------",  givenParentId);
    JSONObject modelObjects = (JSONObject) icePanelDiagramJson.get(IcePanelConstants.MODEL_OBJECTS);
    ArrayList<JSONObject> children = new ArrayList<>();
    modelObjects.keySet().forEach(
        object -> {
          JSONObject modelObject = (JSONObject) modelObjects.get(object);
          String type = getValue(modelObject, "type", "");

          if (!"root".equals(type)) {
            String parentId = getValue(modelObject, "parentId");

            if (givenParentId.equals(parentId)) {
              log.debug("extractChildren add object ({}) ------------------", object);
              children.add(modelObject);
            }
          }
        }
    );
    return children;
  }

  private JSONObject getObject(final JSONObject icePanelDiagramJson, String objectId) {
    JSONObject modelObjects = (JSONObject) icePanelDiagramJson.get(IcePanelConstants.MODEL_OBJECTS);
    return (JSONObject) modelObjects.get(objectId);
  }

  private ArrayList<JSONObject> extractNodeNeighbors(
          final JSONObject icePanelDiagramJson,
          final JSONObject child) {
    ArrayList<JSONObject> neighbors = new ArrayList<>();

    String childId = getValue(child, "id");

    log.debug("extractNodeNeighbors( {} ) ------------------", childId);
    JSONObject modelConnections = (JSONObject) icePanelDiagramJson.get(IcePanelConstants.MODEL_CONNECTIONS);
    modelConnections.keySet().forEach(
        object -> {
          JSONObject modelObject = (JSONObject) modelConnections.get(object);
          String source = getValue(modelObject, IcePanelConstants.ORIGIN_ID);
          String target = getValue(modelObject, IcePanelConstants.TARGET_ID);

          if (childId.equals(source) || childId.equals(target)) {
            log.debug( "   extractNodeNeighbors [{}][{}]------", source, target);
            JSONObject jsonObject;
            if (childId.equals(source)) {
              jsonObject = getObject(icePanelDiagramJson, target);
            } else {
              jsonObject = getObject(icePanelDiagramJson, source);
            }
            log.debug( "   ---> extractNodeNeighbors add [{}][{}]---", childId, jsonObject);
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
    log.debug("extractNeighbors({}) ------------------", parent );

    String givenParentId = getValue(parent, "id");
    log.debug("extractNeighbors({}) ------------------",  givenParentId );

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
      log.debug( "extractNeighbors of child ({}) --------", child);
      ArrayList<JSONObject> childNeighbors
              = extractNodeNeighbors(icePanelDiagramJson, child);
      childNeighbors.forEach(neighbor -> {
        String neighborId = getValue(neighbor, "id");
        log.debug("   extractNeighbors add object ({},{}) ------------------", neighborId, neighbor );
        neighborsMap.put(neighborId, neighbor);
      });
    });

    ArrayList<JSONObject> neighbors = new ArrayList<>();
    neighborsMap.values().forEach(
        object -> {
          log.debug( "extractNeighbors LAST add object ({}) -------", object);
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

  private int depth(JSONObject icePanelDiagramJson, JSONObject node) {
    log.debug( "depth ------------------");
    String parentId = getValue(node, IcePanelConstants.PARENT_ID);

    String type = getValue(node, IcePanelConstants.NODE_TYPE);
    if (IcePanelConstants.TYPE_ROOT.equalsIgnoreCase(type)) {
      log.debug( "depth ROOT ------------------");
      return 1;
    } else {
      JSONObject parent = getObject(icePanelDiagramJson, parentId);
      int depth = depth(icePanelDiagramJson, parent) + 1;
      log.debug( "depth ({}) ------------------", depth);
      return depth;
    }
  }

  private String findRoot(final JSONObject icePanelDiagramJson) {
    log.debug( "findRoot() ------------------");
    JSONObject modelObjects = (JSONObject) icePanelDiagramJson.get(IcePanelConstants.MODEL_OBJECTS);
    AtomicReference<String> root = new AtomicReference<>("");
    modelObjects.keySet().forEach(
            objectId -> {
              JSONObject modelObject = (JSONObject) modelObjects.get(objectId);
              String type = getValue(modelObject, IcePanelConstants.NODE_TYPE, "");

              if (IcePanelConstants.TYPE_ROOT.equals(type)) {
                root.set(objectId.toString());
              }
            }
    );
    return root.get();
  }

  private ArrayList<JSONObject> findParents(
          final JSONObject icePanelDiagramJson,
          final String rootName) {
    log.debug( "findSystems() ------------------");
    JSONObject modelObjects = (JSONObject) icePanelDiagramJson.get(IcePanelConstants.MODEL_OBJECTS);
    HashMap<String, JSONObject> parentsMap = new HashMap<>();
    modelObjects.keySet().forEach(
        objectId -> {
          JSONObject modelObject = (JSONObject) modelObjects.get(objectId);
          if (modelObject != null) {
            String parentId = getValue(modelObject, IcePanelConstants.PARENT_ID);
            if (!rootName.equals(parentId)) {
              JSONObject parent = getObject(icePanelDiagramJson, parentId);
              if (parent != null) {
                parentsMap.put(parentId, parent);
              }
            }
          }
        }
    );
    return new ArrayList<>(parentsMap.values());
  }


  // IcePanel Flows =====================================================

  private ArrayList<JSONObject> findFlows(final JSONObject icePanelDiagramJson) {
    log.debug( "findFlows() ------------------");
    JSONObject modelObjects = (JSONObject) icePanelDiagramJson.get(IcePanelConstants.MODEL_FLOWS);
    ArrayList<JSONObject> flows = new ArrayList<>();
    modelObjects.keySet().forEach(
            object -> {
              JSONObject modelObject = (JSONObject) modelObjects.get(object);
              flows.add(modelObject);
            }
    );
    return flows;
  }

  private void generateFlowDiagramHeader(final PrintStream output,
                                         final String icePanelJsonFile,
                                         final String configurationFile,
                                         final String title) {
    log.debug( "generateFlowDiagramHeader() ------------------");
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

  private JSONObject findSequenceStepNumberX(JSONObject steps, int index) {
    String indexString = "" + index;
    AtomicReference<JSONObject> result = new AtomicReference<>();
    steps.values().forEach(object -> {
      JSONObject step = (JSONObject) object;
      String stepIndex = getValue(step, IcePanelConstants.STEP_INDEX);
      if (indexString.equalsIgnoreCase(stepIndex)) {
        result.set(step);
      }
    });
    return result.get();
  }

  private int findSequenceStepMax(JSONObject steps) {
    AtomicInteger index = new AtomicInteger();
    steps.values().forEach(object -> {
      JSONObject step = (JSONObject) object;
      String stepIndex = getValue(step, IcePanelConstants.STEP_INDEX);
      int stepIndexInt = Integer.parseInt(stepIndex);
      if (stepIndexInt > index.get()) {
        index.set(stepIndexInt);
      }
    });
    return index.get();
  }

  private void generateFlowDiagramEntities(final PrintStream output,
                                           final JSONObject icePanelDiagramJson,
                                           final JSONObject flow) {
    log.debug( "generateFlowDiagramEntities() ------------------");
    output.println();
    JSONObject steps = (JSONObject) flow.get(IcePanelConstants.FLOW_STEPS);
    output.println("' Elements =======");
    log.debug( "Steps : {}", steps);

    log.debug( "generateFlowDiagramEntities() Steps ------------------");
    output.println("' Steps =======");
    int maxSteps = findSequenceStepMax(steps);
    log.debug( "generateFlowDiagramEntities() steps : {}", maxSteps);

    HashMap<String, JSONObject> entities = new HashMap<>();
    steps.values().forEach(object -> {
      JSONObject step = (JSONObject) object;
      String originId = getValue(step, IcePanelConstants.ORIGIN_ID);
      log.debug( "   Step - originId : {}", originId);
      JSONObject origin = getObject(icePanelDiagramJson, originId);
      entities.put(originId, origin);

      String targetId = getValue(step, IcePanelConstants.TARGET_ID);
      log.debug( "   Step - targetId : {}", targetId);
      JSONObject target = getObject(icePanelDiagramJson, targetId);
      entities.put(targetId, target);
    });

    HashMap<String, JSONObject> tempEntities = (HashMap<String, JSONObject>) entities.clone();
    for (int i = 1; i <= maxSteps; i++) {
      log.debug( "generateFlowDiagramEntities() steps entities : {}", i);
      JSONObject step = findSequenceStepNumberX(steps, i);
      if (step != null) {
        String originId = getValue(step, IcePanelConstants.ORIGIN_ID);
        log.debug( "   Step - originId : {}", originId);
        JSONObject origin = tempEntities.get(originId);
        if (origin != null) {
          log.debug( "   Step - origin : {}", origin);
          output.println("participant \"" + getValue(origin, IcePanelConstants.NAME) + "\" as "
                  + getValue(origin, IcePanelConstants.ID) + " ");
          tempEntities.remove(originId);
        }

        String targetId = getValue(step, IcePanelConstants.TARGET_ID);
        log.debug( "Step - targetId : {}", targetId);
        JSONObject target = tempEntities.get(targetId);
        if (target != null) {
          log.debug( "Step - target : {}",target);
          output.println("participant \"" + getValue(target, IcePanelConstants.NAME) + "\" as "
                  + getValue(target, IcePanelConstants.ID) + " ");
          tempEntities.remove(originId);
        }
      }
    }

    output.println();

    for (int i = 1; i <= maxSteps; i++) {
      log.debug( "generateFlowDiagramEntities() steps : {}", i);
      JSONObject step = findSequenceStepNumberX(steps, i);
      if (step != null) {
        output.println(getValue(step, IcePanelConstants.ORIGIN_ID) + " -> " + getValue(step, IcePanelConstants.TARGET_ID)
                + " : " + getValue(step, IcePanelConstants.STEP_INDEX) + " " + getValue(step, IcePanelConstants.DESCRIPTION));
      }
    }
    output.println();
  }

  private void generateFlowDiagram(
          final String icePanelJsonFile,
          final JSONObject icePanelDiagramJson,
          final JSONObject flow,
          final String configurationFile,
          final String subOutputFileNameBase) {
    log.debug( "generateFlowDiagram() ---------");

    String name = getValue(flow, "name");
    String id = getValue(flow, "id");
    log.debug("generateFlowDiagram ({}, {}) ------------------", id, name);

    String toFileName = subOutputFileNameBase + "-Flow-" + name + ".puml";
    log.debug("==== generateFlowDiagram to file ({}) ------------------", toFileName);

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

    log.debug( "==== generateFlowDiagram to file ({}) END ---------",toFileName);
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
    log.debug( "convertIcePanelFlowToUml [{}] (jsonObject) [{}] [{}]",
            icePanelJsonFile, configurationFile,subOutputFileNameBase);

    ArrayList<JSONObject> flows = findFlows(icePanelDiagramJson);

    if (generateSubDiagrams) {
      flows.forEach(flow -> generateFlowDiagram(icePanelJsonFile,
                icePanelDiagramJson,
                flow,
                configurationFile,
                subOutputFileNameBase) );
    }
  }


  // C4 Sub Diagrams =====================================================

  private void generateSubDiagramHeader(final PrintStream output,
                                        final String icePanelJsonFile,
                                        final String configurationFile) {
    log.debug( "generateSubDiagramHeader() ---------");
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


  private void generateSubDiagramBoundaryStart(final PrintStream output,
                                               final JSONObject jsonObject) {
    log.debug( "generateSubDiagramBoundary() ------------------");
    String id = getValue(jsonObject, "id");
    String name = getValue(jsonObject, "name");
    String type = getValue(jsonObject, "type", "");
    String description = getValue(jsonObject, "description", " ");

    // TODO: Convert into a switch and manage not tested options
    if (IcePanelConstants.TYPE_SYSTEM.equals(type)) {
      output.println(IcePanelConstants.OUTPUT_SYSTEM_BOUNDARY
              + id + ", \"" + name + "\" ) {");
    } else if (IcePanelConstants.TYPE_ROOT.equals(type)) {
      output.println(IcePanelConstants.OUTPUT_SYSTEM_BOUNDARY
              + id + ", \"" + name + "\" ) {");
    } else if (IcePanelConstants.TYPE_APP.equals(type)) {
      output.println(IcePanelConstants.OUTPUT_COMPONENT
              + id + ", \"" + name + IcePanelConstants.OUTPUT_VAL_SEPARATOR_STRING
              + description + IcePanelConstants.OUTPUT_SUBDIAGRAM_CLOSER_STRING);
    } else {
      log.debug( "unknown type : [{}]", type);
    }
  }

  private void generateSubDiagramBoundaryEnd(final PrintStream output) {
    output.println("}");
  }

  private void generateSubDiagramClasses(final PrintStream output,
                                         final JSONObject icePanelDiagramJson,
                                         final ArrayList<JSONObject> children) {
    log.debug( "generateSubDiagramClasses() ------------------");
    output.println();
    generateSubDiagramBoundaryStart(output, icePanelDiagramJson);
    output.println("    ' CLASSES =======");
    children.forEach( object -> generateClassInDiagram("    ", output, object) );
    generateSubDiagramBoundaryEnd(output);
    output.println();
  }


  private void generateSubDiagramNeighborClasses(final PrintStream output,
                                                 final ArrayList<JSONObject> children) {
    log.debug( "generateSubDiagramNeighborClasses() ------------------");
    output.println();
    output.println("' NEIGHBOR CLASSES =======");
    children.forEach( object -> generateClassInDiagram("", output, object) );
    output.println();
  }

  private void generateSubDiagramLinks(
          final PrintStream output,
          final JSONObject icePanelDiagramJson,
          ArrayList<JSONObject> children) {
    log.debug( "generateSubDiagramLinks() ------------------");

    HashMap<String, JSONObject> childrenMap = new HashMap<>();
    children.forEach(jsonObject -> {
      String id = getValue(jsonObject, "id", "");
      childrenMap.put(id, jsonObject);
    });

    output.println();
    output.println("' CONNECTIONS =======");
    JSONObject modelConnections = (JSONObject) icePanelDiagramJson.get(IcePanelConstants.MODEL_CONNECTIONS);
    modelConnections.keySet().forEach(
            object -> {
              JSONObject connectionObject = (JSONObject) modelConnections.get(object);
              String name = getName(object, connectionObject);
              String source = getValue(connectionObject, IcePanelConstants.ORIGIN_ID);
              String target = getValue(connectionObject, IcePanelConstants.TARGET_ID);
              String direction = getValue(connectionObject, IcePanelConstants.DIRECTION);

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

  private void generateSubDiagram(
          final String icePanelJsonFile,
          final JSONObject icePanelDiagramJson,
          final JSONObject base,
          final String configurationFile,
          final String subOutputFileNameBase) {
    log.debug( "generateSubDiagram() ------------------");

    String name = getValue(base, "name");
    String id = getValue(base, "id");
    log.debug("generateSubDiagram ({},{}) ------------------", id , name);

    ArrayList<JSONObject> children = extractChildren(icePanelDiagramJson, base);

    if (children.isEmpty()) {
      log.debug( "NOT generateSubDiagram for ({}) because no children", base);
      return;
    }

    int depth = depth(icePanelDiagramJson, base);

    String toFileName = subOutputFileNameBase + "-" + depth + "-" + name + ".puml";
    log.debug("==== generateSubDiagram to file ({}) ------------------", toFileName);

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

    log.debug( "==== generateSubDiagram to file ({}) END ---------", toFileName);
  }

  private void generateSubDiagrams(
          final String icePanelJsonFile,
          final JSONObject icePanelDiagramJson,
          final ArrayList<JSONObject> bases,
          final String configurationFile,
          final String subOutputFileNameBase) {
    log.debug( "generateSubDiagrams() ------------------");

    bases.forEach(base -> generateSubDiagram(icePanelJsonFile,
              icePanelDiagramJson,
              base,
              configurationFile,
              subOutputFileNameBase) );
  }

  // C4 Diagrams =====================================================

  private void generateConnections(final JSONObject icePanelDiagramJson, String rootName) {
    log.debug( "generateConnections() ------------------");
    output.println();
    output.println("' CONNECTIONS =======");
    JSONObject modelConnections = (JSONObject) icePanelDiagramJson.get(IcePanelConstants.MODEL_CONNECTIONS);
    modelConnections.keySet().forEach(
            object -> {
              JSONObject modelObject = (JSONObject) modelConnections.get(object);
              String name = getName(object, modelObject);
              String source = getValue(modelObject, IcePanelConstants.ORIGIN_ID);
              String target = getValue(modelObject, IcePanelConstants.TARGET_ID);
              String direction = getValue(modelObject, IcePanelConstants.DIRECTION);
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
    JSONObject modelObjects = (JSONObject) icePanelDiagramJson.get(IcePanelConstants.MODEL_OBJECTS);
    modelObjects.keySet().forEach(
            object -> {
              JSONObject modelObject = (JSONObject) modelObjects.get(object);
              JSONArray parentIds = (JSONArray) modelObject.get(IcePanelConstants.PARENT_IDS);

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


  private void generateClassInDiagram(final PrintStream output,
                                      final JSONObject jsonObject) {
    generateClassInDiagram("", output, jsonObject);
  }

  private void generateClassInDiagram(final String head,
                                      final PrintStream output,
                                      final JSONObject jsonObject) {
    log.debug( "generateClassInDiagram() ------------------");
    String id = getValue(jsonObject, "id");
    String name = getValue(jsonObject, "name");
    if ((name == null) || (name.isBlank())) {
      name = id;
    }
    String type = getValue(jsonObject, "type", "");
    String description = getValue(jsonObject, "description", " ");

    // TODO: Convert into a switch
    if (IcePanelConstants.TYPE_SYSTEM.equals(type)) {
      output.println(head + "System(" + id + ", \"" + name
              + IcePanelConstants.OUTPUT_VAL_SEPARATOR_STRING + description + IcePanelConstants.OUTPUT_VAL_CLOSER_STRING);
    } else if (IcePanelConstants.TYPE_ACTOR.equals(type)) {
      output.println(head + IcePanelConstants.OUTPUT_PERSON + id + ", \"" + name
              + IcePanelConstants.OUTPUT_VAL_SEPARATOR_STRING + description + IcePanelConstants.OUTPUT_VAL_CLOSER_STRING);
    } else if (IcePanelConstants.TYPE_APP.equals(type)) {
      output.println(head + IcePanelConstants.OUTPUT_COMPONENT + id + ", \"" + name
              + IcePanelConstants.OUTPUT_VAL_SEPARATOR_STRING + description + IcePanelConstants.OUTPUT_VAL_CLOSER_STRING);
    } else if (IcePanelConstants.TYPE_STORE.equals(type)) {
      output.println(head + IcePanelConstants.OUTPUT_CONTAINER_DB + id + ", \"" + name
              + IcePanelConstants.OUTPUT_VAL_SEPARATOR_STRING + description
              + IcePanelConstants.OUTPUT_VAL_SEPARATOR_STRING + IcePanelConstants.OUTPUT_VAL_CLOSER_STRING);
    } else if (IcePanelConstants.TYPE_AREA.equals(type)) {
      log.debug( "skip type : [{}]",  type);
    } else if (IcePanelConstants.TYPE_COMPONENT.equals(type)) {
      // TODO: Add technologies
      output.println(head + IcePanelConstants.OUTPUT_CONTAINER + id + ", \"" + name
              + IcePanelConstants.OUTPUT_VAL_SEPARATOR_STRING + description
              + IcePanelConstants.OUTPUT_VAL_SEPARATOR_STRING + IcePanelConstants.OUTPUT_VAL_CLOSER_STRING);
    } else {
      log.debug( "unknown type : [{}]", type);
    }
  }

  private void generateClasses(final JSONObject icePanelDiagramJson) {
    log.debug( "generateClasses() ------------------");
    output.println();
    output.println("' CLASSES =======");
    JSONObject modelObjects = (JSONObject) icePanelDiagramJson.get(IcePanelConstants.MODEL_OBJECTS);
    modelObjects.keySet().forEach(
            object -> {
              JSONObject modelObject = (JSONObject) modelObjects.get(object);
              generateClassInDiagram(output, modelObject);
            }
    );
    output.println();
  }

  private void generateHeader(final String icePanelJsonFile,
                              final String configurationFile) {
    generateSubDiagramHeader(output, icePanelJsonFile, configurationFile);
  }

  private void generateFooter() {
    generateFooter(output);
  }

  private void generateFooter(final PrintStream output) {
    log.debug( "generateFooter() ------------------");
    output.println();
    output.println("@enduml");
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
    log.debug( "convertIcePanelToUML [{}][{}][{}]", icePanelJsonFile, configurationFile, subOutputFileNameBase);

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
    log.debug( "convertIcePanelToUML [{}][{}][{}]", icePanelJsonFile, configurationFile, subOutputFileNameBase);

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
