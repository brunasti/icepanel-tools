package it.brunasti.icepanel.tools;

/**
 * Constants used in the project
 */
public final class IcePanelConstants {

  private IcePanelConstants() {}


  public static final String PATH_DELIMITER_STRING = "/";



  // TODO: How can these be converted into an Enum?
  // Object types -------------------------
  public static final String TYPE_SYSTEM = "system";
  public static final String TYPE_ACTOR = "actor";
  public static final String TYPE_APP = "app";
  public static final String TYPE_STORE = "store";
  public static final String TYPE_AREA = "area";
  public static final String TYPE_COMPONENT = "component";
  public static final String TYPE_ROOT = "root";



  // IcePanel JSON fields and attributes
  public static final String NODE_TYPE = "type";
  public static final String ID = "id";
  public static final String NAME = "name";
  public static final String DESCRIPTION = "description";
  public static final String ORIGIN_ID = "originId";
  public static final String PARENT_ID = "parentId";
  public static final String TARGET_ID = "targetId";
  public static final String DIRECTION = "direction";
  public static final String MODEL_CONNECTIONS = "modelConnections";
  public static final String MODEL_OBJECTS = "modelObjects";
  public static final String MODEL_FLOWS = "flows";
  public static final String PARENT_IDS = "parentIds";
  public static final String FLOW_STEPS = "steps";
  public static final String STEP_INDEX = "index";


  public static final String DEBUG_TEXT_SET_TO = "{} set to [{}]";
  public static final String DEBUG_SKIP_TYPE = "skip type : [{}]";
  public static final String DEBUG_UNKNOWN_TYPE_FOR_OBJECT = "unknown type : [{}] for object [{}]";

}
