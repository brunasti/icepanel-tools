package it.brunasti.icepanel.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Convert the JSON export of a project from IcePanel
 * into a set of Mermaid diagrams.
 * For reference see:
 * - <a href="https://icepanel.io/">IcePanel</a>
 * - <a href="https://mermaid.js.org/syntax/c4.html">Mermaid</a>
 */
public class IcePanelToMermaidConverter extends AbstractIcePanelConverter {

  static Logger log = LogManager.getLogger(IcePanelToMermaidConverter.class);

  /**
   * Instantiate a IcePanelToMermaidConverter with output directed to StandardOut.
   */
  public IcePanelToMermaidConverter() {
    super();
  }

  /**
   * Instantiate a IcePanelToMermaidConverter with output directed to a passed PrintStream.
   * Used in case to create an output file, passing a PrintStream pointing
   * to the desired file.
   * If System.out is passed, the output is to StandardOut.
   *
   * @param output The PrintStream to which the output will be directed.
   */
  public IcePanelToMermaidConverter(PrintStream output) {
    super(output);
  }


  // =====================================================================
  // C4 Sub Diagrams =====================================================

  private void generateSubDiagramHeader(final PrintStream output,
                                        final JSONObject root) {
    log.debug( "generateSubDiagramHeader() root : [{}]", root);
    String title = getValue(root, IcePanelConstants.DIRECTION, "");

    output.println("C4Context");
    output.println("  title System Context diagram for " + title);
  }


  private String generateSubDiagramBoundaryHeader(
          final String header,
          final JSONObject jsonObject,
          final int depth ) {
    String type = getValue(jsonObject, "type", "");

    // TODO: Convert into a switch and manage not tested options
    if (type != null) {
      switch(depth) {
        case 0:
          switch(type) {
            case IcePanelConstants.TYPE_SYSTEM, IcePanelConstants.TYPE_ROOT:
              return header + IcePanelPlantUmlConstants.OUTPUT_SYSTEM_BOUNDARY;
            case IcePanelConstants.TYPE_APP:
            default:
              return header + IcePanelPlantUmlConstants.OUTPUT_COMPONENT;
          }
        case 1:
        default:
          switch(type) {
            case IcePanelConstants.TYPE_SYSTEM, IcePanelConstants.TYPE_ROOT:
              return header + IcePanelPlantUmlConstants.OUTPUT_SYSTEM_BOUNDARY;
            case IcePanelConstants.TYPE_APP:
              return header + IcePanelPlantUmlConstants.OUTPUT_CONTAINER_BOUNDARY;
            default:
              log.error("unknown type : [{}] from object [{}]", type, jsonObject);
          }
      }
    } else {
      log.error("null type from object [{}]", jsonObject);
    }
    return "";
  }


  private void generateSubDiagramBoundaryStart(final PrintStream output,
                                               final JSONObject jsonObject,
                                               final int depth ) {
    log.debug( "generateSubDiagramBoundary({}) ------------------", depth);
    String id = getValue(jsonObject, "id");
    String name = getValue(jsonObject, "name");
    String type = getValue(jsonObject, "type", "");
    String description = Utils.wrapString(getValue(jsonObject, "description", "\\n"), "\\n", 100);
    log.debug("generateSubDiagramBoundaryStart - description [{}]", description);

    // TODO: Convert into a switch and manage not tested options
    if (type != null) {
      switch(type) {
        case IcePanelConstants.TYPE_SYSTEM, IcePanelConstants.TYPE_ROOT:
          output.println(generateSubDiagramBoundaryHeader("", jsonObject, depth)
                  + id + ", \"" + name + "\") {");
          break;
        case IcePanelConstants.TYPE_APP:
        default:
          output.println(generateSubDiagramBoundaryHeader("", jsonObject, depth)
                  + id + ", \"" + name + IcePanelPlantUmlConstants.OUTPUT_VAL_SEPARATOR_STRING
                  + description + IcePanelPlantUmlConstants.OUTPUT_SUBDIAGRAM_CLOSER_STRING);
          break;
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
                                         final ArrayList<JSONObject> children,
                                         final int depth) {
    log.debug( "generateSubDiagramClasses() ------------------");
    output.println();
    generateSubDiagramBoundaryStart(output, icePanelDiagramJson, depth);
    children.forEach( object -> generateClassInDiagram("    ", output, object, depth) );
    generateSubDiagramBoundaryEnd(output);
    output.println();
  }


  private void generateSubDiagramNeighborClasses(final PrintStream output,
                                                 final ArrayList<JSONObject> children) {
    log.debug( "generateSubDiagramNeighborClasses() ------------------");
    output.println();
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
                          + ", \"" + name + "\")");
                } else if ("bidirectional".equals(direction)) {
                  output.println("Rel(" + source + ", " + target
                          + ", \"" + name + "\")");
                  output.println("Rel(" + target + ", " + source
                          + ", \"return " + name + "\")");
                }
              }
            }
    );
    output.println();
  }

  private void generateSubDiagram(
          final JSONObject icePanelDiagramJson,
          final JSONObject base,
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

    String toFileName = subOutputFileNameBase + "-" + depth + "-" + name + ".md";
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

    ArrayList<JSONObject> neighbors = extractNeighbors(icePanelDiagramJson, base, children);

    generateSubDiagramHeader(printStream, base);
    generateSubDiagramClasses(printStream, base, children, depth);
    generateSubDiagramNeighborClasses(printStream, neighbors);
    generateSubDiagramLinks(printStream, icePanelDiagramJson, children);
    generateFooter(printStream);

    printStream.close();

    log.debug( "==== generateSubDiagram to file ({}) END ---------", toFileName);
  }

  private void generateSubDiagrams(
          final JSONObject icePanelDiagramJson,
          final ArrayList<JSONObject> bases,
          final String subOutputFileNameBase) {
    log.debug( "generateSubDiagrams() ------------------");

    bases.forEach(base -> generateSubDiagram(
              icePanelDiagramJson,
              base,
              subOutputFileNameBase) );
  }


  // =================================================================
  // C4 Diagrams =====================================================

  private void generateConnections(final JSONObject icePanelDiagramJson, String header) {
    log.debug( "generateConnections() ------------------");
    output.println();
    JSONObject modelConnections = (JSONObject) icePanelDiagramJson.get(IcePanelConstants.MODEL_CONNECTIONS);
    modelConnections.keySet().forEach(
            object -> {
              JSONObject modelObject = (JSONObject) modelConnections.get(object);
              String name = getName(object, modelObject);
              String source = getValue(modelObject, IcePanelConstants.ORIGIN_ID);
              String target = getValue(modelObject, IcePanelConstants.TARGET_ID);
              String direction = getValue(modelObject, IcePanelConstants.DIRECTION);
              if ("outgoing".equals(direction)) {
                output.println(header + "Rel(" + source + ", " + target + ", \"" + name + "\")");
              } else if ("bidirectional".equals(direction)) {
                output.println(header + "BiRel(" + source + ", " + target + ", \"" + name + "\")");
              }
            }
    );
  }

  // TODO: Add technologies
  private String generateClassHeader( final String head,
                                      final JSONObject jsonObject,
                                      int depth) {
    log.debug( "generateClassHeader() ------------------");
    log.debug( "generateClassHeader() head "+head);
    log.debug( "generateClassHeader() depth "+depth);
    String type = getValue(jsonObject, "type", "");
    if (type != null) {
      switch (depth) {
        case 1:
          switch (type) {
            case IcePanelConstants.TYPE_SYSTEM, IcePanelConstants.TYPE_ACTOR, IcePanelConstants.TYPE_COMPONENT:
              return head + IcePanelPlantUmlConstants.OUTPUT_CONTAINER;
            case IcePanelConstants.TYPE_STORE:
              return head + IcePanelPlantUmlConstants.OUTPUT_CONTAINER_DB;
            case IcePanelConstants.TYPE_AREA:
              log.debug(IcePanelConstants.DEBUG_SKIP_TYPE, type);
              break;
            default:
              log.error(IcePanelConstants.DEBUG_UNKNOWN_TYPE_FOR_OBJECT, type, jsonObject);
          }
          break;
        case 0:
        default:
          switch (type) {
            case IcePanelConstants.TYPE_SYSTEM:
              return head + IcePanelPlantUmlConstants.OUTPUT_SYSTEM;
            case IcePanelConstants.TYPE_ACTOR:
              return head + IcePanelPlantUmlConstants.OUTPUT_PERSON;
            case IcePanelConstants.TYPE_APP:
              return head + IcePanelPlantUmlConstants.OUTPUT_COMPONENT;
            case IcePanelConstants.TYPE_STORE:
              return head + IcePanelPlantUmlConstants.OUTPUT_CONTAINER_DB;
            case IcePanelConstants.TYPE_AREA:
              log.debug(IcePanelConstants.DEBUG_SKIP_TYPE, type);
              break;
            case IcePanelConstants.TYPE_COMPONENT:
              return head + IcePanelPlantUmlConstants.OUTPUT_CONTAINER;
            default:
              log.error(IcePanelConstants.DEBUG_UNKNOWN_TYPE_FOR_OBJECT, type, jsonObject);
          }
          break;
      }
    } else {
      log.error( "null type for object [{}]", jsonObject);
      return "";
    }
    return "";
  }

  private void generateClassInDiagram(final PrintStream output,
                                      final JSONObject jsonObject) {
    generateClassInDiagram("      ", output, jsonObject);
  }

  private void generateClassInDiagram(final String head,
                                      final PrintStream output,
                                      final JSONObject jsonObject) {
    generateClassInDiagram(head, output, jsonObject, 0);
  }

  private void generateClassInDiagram(final String head,
                                      final PrintStream output,
                                      final JSONObject jsonObject,
                                      int depth) {
    log.debug( "generateClassInDiagram() ------------------");
    String id = getValue(jsonObject, "id");
    String name = getValue(jsonObject, "name");
    if ((name == null) || (name.isBlank())) {
      name = id;
    }
    String type = getValue(jsonObject, "type", "");
    String description = Utils.wrapString(getValue(jsonObject, "description", "\\n"), "\\n", 100);
    log.debug("generateClassInDiagram - description [{}]", description);

    // TODO: Add technologies
    if (type != null) {
      switch (type) {
        case IcePanelConstants.TYPE_SYSTEM, IcePanelConstants.TYPE_ACTOR, IcePanelConstants.TYPE_APP:
          output.println(generateClassHeader(head,jsonObject,depth) + id + ", \"" + name
                  + IcePanelPlantUmlConstants.OUTPUT_VAL_SEPARATOR_STRING + description + IcePanelPlantUmlConstants.OUTPUT_VAL_CLOSER_STRING);
          break;
        case IcePanelConstants.TYPE_STORE, IcePanelConstants.TYPE_COMPONENT:
          output.println(generateClassHeader(head,jsonObject,depth) + id + ", \"" + name
                  + IcePanelPlantUmlConstants.OUTPUT_VAL_SEPARATOR_STRING + description
                  + IcePanelPlantUmlConstants.OUTPUT_VAL_SEPARATOR_STRING + IcePanelPlantUmlConstants.OUTPUT_VAL_CLOSER_STRING);
          break;
        case IcePanelConstants.TYPE_AREA:
          log.debug(IcePanelConstants.DEBUG_SKIP_TYPE, type);
          break;
        default:
          log.error(IcePanelConstants.DEBUG_UNKNOWN_TYPE_FOR_OBJECT, type, jsonObject);
      }
    } else {
      log.error( "null type for object [{}]", jsonObject);
    }
  }

  private void generateClasses(final JSONObject icePanelDiagramJson) {
    log.debug( "generateClasses() ------------------");
    output.println("    {");
    JSONObject modelObjects = (JSONObject) icePanelDiagramJson.get(IcePanelConstants.MODEL_OBJECTS);
    modelObjects.keySet().forEach(
            object -> {
              JSONObject modelObject = (JSONObject) modelObjects.get(object);
              generateClassInDiagram(output, modelObject);
            }
    );
    output.println("    }");
    output.println();
  }

  private void generateHeader(final JSONObject icePanelDiagramJson,
                              final String rootName) {
    log.debug( "generateHeader() ------------------");
    JSONObject root = getObject(icePanelDiagramJson, rootName);
    log.debug( "generateHeader() root : [{}]", root);
    String title = getValue(root, "type", "");

    output.println("C4Context");
    output.println("  title System Context diagram for " + title);
    output.println("  Enterprise_Boundary(" + rootName + ", \"" + title + "\")");
  }

  private void generateFooter() {
    generateFooter(output);
  }

  private void generateFooter(final PrintStream output) {
    log.debug( "generateFooter() ------------------");
    output.println();
    output.println("  UpdateLayoutConfig($c4ShapeInRow=\"5\", $c4BoundaryInRow=\"1\")");
  }

  /** Generate a diagram for the IcePanel JSON file.
   *
   * @param icePanelJsonFile The JSON file exported from IcePanel to be converted
   * @param configurationFile The configuration file with the list
   *                          of packages and classes to exclude.
   * @param subOutputFileNameBase Base part of the name for the sub diagrams
   */
  public void convertIcePanelToMermaid(final String icePanelJsonFile,
                                       final JSONObject icePanelDiagramJson,
                                       final String configurationFile,
                                       final String subOutputFileNameBase) {
    log.debug( "convertIcePanelToMermaid [{}][{}][{}]", icePanelJsonFile, configurationFile, subOutputFileNameBase);

    String rootName = findRoot(icePanelDiagramJson);

    generateHeader(icePanelDiagramJson, rootName);
    generateClasses(icePanelDiagramJson);
    generateConnections(icePanelDiagramJson, "   ");
    generateFooter();

    if (generateSubDiagrams) {
      generateSubDiagrams(icePanelDiagramJson,
              findParents(icePanelDiagramJson, rootName),
              subOutputFileNameBase);
    }
  }


  // ======================================================================
  // Main entry point =====================================================
  /** Generate a diagram for the IcePanel JSON file in Mermaid format.
   *
   * @param icePanelJsonFile The JSON file exported from IcePanel to be converted
   * @param configurationFile The configuration file with the list
   *                          of packages and classes to exclude.
   */
  public void convertIcePanelToMermaid(final String icePanelJsonFile,
                                       final String configurationFile,
                                       final String subOutputFileNameBase) {
    cleanLocalVars();
    log.debug( "convertIcePanelToMermaid [{}][{}][{}]", icePanelJsonFile, configurationFile, subOutputFileNameBase);

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

    convertIcePanelToMermaid(icePanelJsonFile,
            icePanelDiagramJson,
            configurationFile,
            outputPath + subOutputFileNameBase);
  }

}
