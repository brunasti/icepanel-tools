package it.brunasti.icepanel.tools;

/**
 * Constants used in the project
 */
public final class IcePanelConstants {

  private IcePanelConstants() {}


  static final String PATH_DELIMITER_STRING = "/";



  // TODO: How can these be converted into an Enum?
  // Object types -------------------------
  static final String TYPE_SYSTEM = "system";
  static final String TYPE_ACTOR = "actor";
  static final String TYPE_APP = "app";
  static final String TYPE_STORE = "store";
  static final String TYPE_AREA = "area";
  static final String TYPE_COMPONENT = "component";
  static final String TYPE_ROOT = "root";



  // IcePanel JSON fields and attributes
  static final String NODE_TYPE = "type";
  static final String ID = "id";
  static final String NAME = "name";
  static final String DESCRIPTION = "description";
  static final String ORIGIN_ID = "originId";
  static final String PARENT_ID = "parentId";
  static final String TARGET_ID = "targetId";
  static final String DIRECTION = "direction";
  static final String MODEL_CONNECTIONS = "modelConnections";
  static final String MODEL_OBJECTS = "modelObjects";
  static final String MODEL_FLOWS = "flows";
  static final String PARENT_IDS = "parentIds";
  static final String FLOW_STEPS = "steps";
  static final String STEP_INDEX = "index";


  static final String DEBUG_TEXT_SET_TO = "{} set to [{}]";

}
