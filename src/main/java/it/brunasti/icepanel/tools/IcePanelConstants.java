package it.brunasti.icepanel.tools;

/**
 * Constants used in the project
 */
public interface IcePanelConstants {
// TODO: Replace with final class and / or enum
  String PATH_DELIMITER_STRING = "/";


  // IcePanel JSON fields and attributes
  String NODE_TYPE = "type";
  String ID = "id";
  String NAME = "name";
  String DESCRIPTION = "description";
  String ORIGIN_ID = "originId";
  String PARENT_ID = "parentId";
  String TARGET_ID = "targetId";
  String DIRECTION = "direction";
  String MODEL_CONNECTIONS = "modelConnections";
  String MODEL_OBJECTS = "modelObjects";
  String MODEL_FLOWS = "flows";
  String PARENT_IDS = "parentIds";
  String FLOW_STEPS = "steps";
  String STEP_INDEX = "index";


  // Object types -------------------------
  String TYPE_SYSTEM = "system";
  String TYPE_ACTOR = "actor";
  String TYPE_APP = "app";
  String TYPE_STORE = "store";
  String TYPE_AREA = "area";
  String TYPE_COMPONENT = "component";
  String TYPE_ROOT = "root";


  // Generation keywords --------------------
  String OUTPUT_SYSTEM_BOUNDARY = "System_Boundary(";
  String OUTPUT_PERSON = "Person(";
  String OUTPUT_COMPONENT = "Component(";
  String OUTPUT_CONTAINER_DB = "ContainerDb(";
  String OUTPUT_CONTAINER = "Container(";
  String OUTPUT_VAL_SEPARATOR_STRING = "\", \"";
  String OUTPUT_VAL_CLOSER_STRING = "\" )";
  String OUTPUT_SUBDIAGRAM_CLOSER_STRING = "\" ) { ";


  String DEBUG_TEXT_SET_TO = " set to [";
  String DEBUG_ENDING_STRING = ") ------------------";
  String DEBUG_CLOSE_LINE_SQUARED_STRING = "]";


}
