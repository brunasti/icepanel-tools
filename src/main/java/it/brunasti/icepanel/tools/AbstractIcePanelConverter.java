package it.brunasti.icepanel.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Convert the JSON export of a project from IcePanel
 * into a set of diagrams.
 * For reference see:
 * - <a href="https://icepanel.io/">IcePanel</a>
 */
public class AbstractIcePanelConverter {

  static Logger log = LogManager.getLogger(AbstractIcePanelConverter.class);

  // Reference to a PrintStream to be used for the diagram
  // By default is the Standard.out, but it can be redirected
  // to a file.
  protected final PrintStream output;

  protected String includeFileName = "";
  protected String includeFlowFileName = "";
  protected String outputPath = "./";
  protected boolean generateSubDiagrams = false;

  /**
   * Instantiate a AbstractIcePanelConverter with output directed to StandardOut.
   */
  public AbstractIcePanelConverter() {
    this.output = System.out;
  }

  /**
   * Instantiate a AbstractIcePanelConverter with output directed to a passed PrintStream.
   * Used in case to create an output file, passing a PrintStream pointing
   * to the desired file.
   * If System.out is passed, the output is to StandardOut.
   *
   * @param output The PrintStream to which the output will be directed.
   */
  public AbstractIcePanelConverter(PrintStream output) {
    setDefaultConfiguration();
    this.output = output;
  }

  protected void cleanLocalVars() {
    // Reset all variables to avoid conflicts in case of multiple run
    setDefaultConfiguration();
  }


  // ------------------------------------------------------
  // Load and manage Configuration ------------------------

  protected void setDefaultConfiguration() {
    includeFileName = "";
    includeFlowFileName = "";
    outputPath = "";
    generateSubDiagrams = false;
  }

  protected void loadJsonConfiguration(JSONObject jsonObject) {
    loadIncludeFileConfiguration(jsonObject);
    loadOutputPathConfiguration(jsonObject);
  }

  protected void loadIncludeFileConfiguration(JSONObject jsonObject) {
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

  protected void loadOutputPathConfiguration(JSONObject jsonObject) {
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


  protected boolean loadJsonConfigurationFromFile(String configurationFileName) {
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


  protected boolean loadConfiguration(String configurationFileName) {
    if ((null == configurationFileName) || (configurationFileName.isBlank())) {
      setDefaultConfiguration();
      return true;
    } else {
      return loadJsonConfigurationFromFile(configurationFileName);
    }
  }


  // ---------------------------------------------------------------------
  // Load the JSON file exported from IcePanel ---------------------------

  protected JSONObject loadIcePanelJsonFromFile(String icePanelJsonFileName) {
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


  // -------------------------------------------------------------
  // Generic JSON functions --------------------------------------

  protected String getValue(final JSONObject modelObject, String key) {
    return getValue(modelObject, key, " - ");
  }

  protected String getValue(final JSONObject modelObject, String key, String nlReplace) {
    if (modelObject.get(key) != null) {
      String value = modelObject.get(key).toString();
      value = value.replace("\n", nlReplace);
      return value;
    } else {
      return null;
    }
  }

  protected String getName(Object object, final JSONObject modelObject) {
    String name = getValue(modelObject, "name");
    if ((name == null) || (name.isBlank())) {
      name = object.toString();
    }
    return name;
  }

  protected ArrayList<JSONObject> extractChildren(
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

  protected JSONObject getObject(final JSONObject icePanelDiagramJson, String objectId) {
    JSONObject modelObjects = (JSONObject) icePanelDiagramJson.get(IcePanelConstants.MODEL_OBJECTS);
    return (JSONObject) modelObjects.get(objectId);
  }

  protected ArrayList<JSONObject> extractChildNodeNeighbors(
          final JSONObject icePanelDiagramJson,
          final JSONObject child) {
    ArrayList<JSONObject> neighbors = new ArrayList<>();

    String childId = getValue(child, "id");

    log.debug("extractChildNodeNeighbors( {} ) ------------------", childId);
    JSONObject modelConnections = (JSONObject) icePanelDiagramJson.get(IcePanelConstants.MODEL_CONNECTIONS);
    modelConnections.keySet().forEach(
        object -> {
          JSONObject modelObject = (JSONObject) modelConnections.get(object);
          String source = getValue(modelObject, IcePanelConstants.ORIGIN_ID);
          String target = getValue(modelObject, IcePanelConstants.TARGET_ID);

          if (childId.equals(source) || childId.equals(target)) {
            log.debug( "   extractChildNodeNeighbors [{}][{}]------", source, target);
            JSONObject jsonObject;
            if (childId.equals(source)) {
              jsonObject = getObject(icePanelDiagramJson, target);
            } else {
              jsonObject = getObject(icePanelDiagramJson, source);
            }
            log.debug( "   ---> extractChildNodeNeighbors add [{}][{}]---", childId, jsonObject.get(IcePanelConstants.NAME));
            neighbors.add(jsonObject);
          }
        }
    );

    // Reiterate on the child children
    ArrayList<JSONObject> children = extractChildren(icePanelDiagramJson, child);
    children.forEach(subChild -> {
      ArrayList<JSONObject> childNeighbors = extractChildNodeNeighbors(icePanelDiagramJson, subChild);
      childNeighbors.forEach(subNeighbor -> {
        neighbors.add(subNeighbor);
      });
    });

    return neighbors;
  }

  // TODO: refactor for a more recursive way, so to extract the neighbors of the children too
  protected ArrayList<JSONObject> extractNeighbors(
          final JSONObject icePanelDiagramJson,
          final JSONObject parent,
          final ArrayList<JSONObject> children) {

    String givenParentId = getValue(parent, IcePanelConstants.ID);
    String givenParentName = getValue(parent, IcePanelConstants.NAME);
    log.debug("#### - extractNeighbors({}) [{}]------------------",  givenParentId, givenParentName );

    // Definition of neighbors: not part of the group, but connected to one of the group

    // Map of children to facilitate checks
    HashMap<String, JSONObject> childrenMap = new HashMap<>();
    children.forEach(jsonObject -> {
      jsonObject.put(IcePanelConstants.PARENT_NAME, givenParentName);

      String id = getValue(jsonObject, IcePanelConstants.ID, "");
      log.debug("#### - - extractNeighbors children ({}) [{}]------------------",  id, jsonObject.get(IcePanelConstants.NAME) );
      childrenMap.put(id, jsonObject);
    });

    // Map of the neighbors to add and check existence
    HashMap<String, JSONObject> neighborsMap = new HashMap<>();

    // For each child search for its neighbors
    children.forEach(child -> {
      log.debug( "extractNeighbors of child ({}) --------", child.get(IcePanelConstants.NAME));
      ArrayList<JSONObject> childNeighbors
              = extractChildNodeNeighbors(icePanelDiagramJson, child);
      childNeighbors.forEach(neighbor -> {
        String neighborId = getValue(neighbor, "id");
        log.debug("   extractNeighbors add object ({},{}) ------------------", neighborId, neighbor.get(IcePanelConstants.NAME) );
        neighborsMap.put(neighborId, neighbor);
      });
    });

    // Should iterate down one level....


    // Create the response, avoiding the children themselves
    ArrayList<JSONObject> neighbors = new ArrayList<>();
    neighborsMap.values().forEach(
        object -> {
          log.debug( "extractNeighbors LAST add object ({}) -------", object.get(IcePanelConstants.NAME));
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

  protected int depth(JSONObject icePanelDiagramJson, JSONObject node) {
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

  protected boolean checkIsAncestor(JSONObject icePanelDiagramJson, JSONObject base, JSONObject node) {
    log.debug( "checkIsAncestor ------------------");
    String parentId = getValue(node, IcePanelConstants.PARENT_ID);
    if (parentId == null) {
      return false;
    }

    String baseId = getValue(base, IcePanelConstants.ID);
    if (baseId.equalsIgnoreCase(parentId)) {
      return true;
    } else {
      JSONObject parent = getObject(icePanelDiagramJson, parentId);
      if (parent == null) {
        return false;
      } else {
        return checkIsAncestor(icePanelDiagramJson, base, parent);
      }
    }
  }

  protected JSONObject getFirstChild(JSONObject icePanelDiagramJson, JSONObject base, JSONObject node) {
    String parentId = getValue(node, IcePanelConstants.PARENT_ID);
    log.debug( "getFirstChild ------------------ {} - {} - {} - {} GFC",
            getValue(base, IcePanelConstants.ID),
            parentId, getValue(node,IcePanelConstants.PARENT_ID), getValue(node,IcePanelConstants.ID));
    if (parentId == null) {
      log.debug( "getFirstChild ------------------ parentId null");
      return null;
    }

    if (parentId.equalsIgnoreCase(getValue(node,IcePanelConstants.PARENT_ID))) {
      log.debug( "getFirstChild ----------- FOUND : {}", getValue(node, IcePanelConstants.ID));
      return getObject(icePanelDiagramJson, parentId);
    } else {
      JSONObject parent = getObject(icePanelDiagramJson, parentId);
      if (parent == null) {
        log.debug( "getFirstChild ------------------ parent null");
        return null;
      }
      return getFirstChild(icePanelDiagramJson, base, parent);
    }
  }

  protected String findRoot(final JSONObject icePanelDiagramJson) {
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

  protected ArrayList<JSONObject> findParents(
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


  // --------------------------------------------------------------------
  // IcePanel Flows =====================================================


  protected JSONObject findSequenceStepNumberX(JSONObject steps, int index) {
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

  protected int findSequenceStepMax(JSONObject steps) {
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

  protected ArrayList<JSONObject> findFlows(final JSONObject icePanelDiagramJson) {
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


}
