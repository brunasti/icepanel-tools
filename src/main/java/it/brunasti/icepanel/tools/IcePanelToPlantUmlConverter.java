package it.brunasti.icepanel.tools;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

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
public class IcePanelToPlantUmlConverter extends AbstractIcePanelConverter {

  // TODO: Check the order of the target entity in the flow generation

  static Logger log = LogManager.getLogger(IcePanelToPlantUmlConverter.class);

  /**
   * Instantiate a IcePanelToPlantUmlConverter with output directed to StandardOut.
   */
  public IcePanelToPlantUmlConverter() {
    super();
  }

  /**
   * Instantiate a IcePanelToPlantUmlConverter with output directed to a passed PrintStream.
   * Used in case to create an output file, passing a PrintStream pointing
   * to the desired file.
   * If System.out is passed, the output is to StandardOut.
   *
   * @param output The PrintStream to which the output will be directed.
   */
  public IcePanelToPlantUmlConverter(PrintStream output) {
    super(output);
  }


  // --------------------------------------------------------------------
  // IcePanel Flows =====================================================


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

    String name = getValue(flow, "name").replace(' ','_');
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
      log.error(fnf);
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


  // =====================================================================
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
    String description = Utils.wrapString(getValue(jsonObject, "description", "\\n"), "\\n", 100);
    log.debug("generateSubDiagramBoundaryStart - description [{}]", description);

    // TODO: Convert into a switch and manage not tested options
    if (type != null) {
      switch(type) {
        case IcePanelConstants.TYPE_SYSTEM, IcePanelConstants.TYPE_ROOT:
          output.println(IcePanelPlantUmlConstants.OUTPUT_SYSTEM_BOUNDARY
                  + id + ", \"" + name + "\" ) {");
          break;
        case IcePanelConstants.TYPE_APP:
          output.println(IcePanelPlantUmlConstants.OUTPUT_COMPONENT
                  + id + ", \"" + name + IcePanelPlantUmlConstants.OUTPUT_VAL_SEPARATOR_STRING
                  + description + IcePanelPlantUmlConstants.OUTPUT_SUBDIAGRAM_CLOSER_STRING);
          break;
        default:
          log.error("unknown type : [{}] from object [{}]", type, jsonObject);
      }
    } else {
      log.error("null type from object [{}]", jsonObject);
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

    String name = getValue(base, "name").replace(' ','_');
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
      log.error(fnf);
      return;
    }

    // TODO: check the inclusion of entities of lower level and the corresponding links
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


  // =================================================================
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
    String description = Utils.wrapString(getValue(jsonObject, "description", "\\n"), "\\n", 100);
    log.debug("generateClassInDiagram - description [{}]", description);

    if (type != null) {
      switch (type) {
        case IcePanelConstants.TYPE_SYSTEM:
          output.println(head + "System(" + id + ", \"" + name
                  + IcePanelPlantUmlConstants.OUTPUT_VAL_SEPARATOR_STRING + description + IcePanelPlantUmlConstants.OUTPUT_VAL_CLOSER_STRING);
          break;
        case IcePanelConstants.TYPE_ACTOR:
          output.println(head + IcePanelPlantUmlConstants.OUTPUT_PERSON + id + ", \"" + name
                  + IcePanelPlantUmlConstants.OUTPUT_VAL_SEPARATOR_STRING + description + IcePanelPlantUmlConstants.OUTPUT_VAL_CLOSER_STRING);
          break;
        case IcePanelConstants.TYPE_APP:
          output.println(head + IcePanelPlantUmlConstants.OUTPUT_COMPONENT + id + ", \"" + name
                  + IcePanelPlantUmlConstants.OUTPUT_VAL_SEPARATOR_STRING + description + IcePanelPlantUmlConstants.OUTPUT_VAL_CLOSER_STRING);
          break;
        case IcePanelConstants.TYPE_STORE:
          output.println(head + IcePanelPlantUmlConstants.OUTPUT_CONTAINER_DB + id + ", \"" + name
                  + IcePanelPlantUmlConstants.OUTPUT_VAL_SEPARATOR_STRING + description
                  + IcePanelPlantUmlConstants.OUTPUT_VAL_SEPARATOR_STRING + IcePanelPlantUmlConstants.OUTPUT_VAL_CLOSER_STRING);
          break;
        case IcePanelConstants.TYPE_AREA:
          log.debug("skip type : [{}]", type);
          break;
        case IcePanelConstants.TYPE_COMPONENT:
          // TODO: Add technologies
          output.println(head + IcePanelPlantUmlConstants.OUTPUT_CONTAINER + id + ", \"" + name
                  + IcePanelPlantUmlConstants.OUTPUT_VAL_SEPARATOR_STRING + description
                  + IcePanelPlantUmlConstants.OUTPUT_VAL_SEPARATOR_STRING + IcePanelPlantUmlConstants.OUTPUT_VAL_CLOSER_STRING);
          break;
        default:
          log.error("unknown type : [{}] for object [{}]", type, jsonObject);
      }
    } else {
      log.error( "null type for object [{}]", jsonObject);
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


  // ======================================================================
  // Main entry point =====================================================
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
      log.error("Configuration JSON not loaded");
      return;
    }

    JSONObject icePanelDiagramJson = loadIcePanelJsonFromFile(icePanelJsonFile);
    if (icePanelDiagramJson == null) {
      log.error("IcePanel JSON file not loaded");
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
