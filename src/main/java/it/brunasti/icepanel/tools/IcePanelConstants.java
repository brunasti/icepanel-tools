package it.brunasti.icepanel.tools;

/**
 * Constants used in the project
 */
public interface IcePanelConstants {

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

  String TYPE_SYSTEM = "system";
  String TYPE_ACTOR = "actor";
  String TYPE_APP = "app";
  String TYPE_STORE = "store";
  String TYPE_AREA = "area";
  String TYPE_COMPONENT = "component";
  String TYPE_ROOT = "root";

  String SYSTEM_BOUNDARY = "System_Boundary(";
  String PERSON = "Person(";
  String COMPONENT = "Component(";
  String CONTAINER_DB = "ContainerDb(";
  String CONTAINER = "Container(";
  String DEBUG_ENDING_STRING = ") ------------------";

  String OUTPUT_VAL_SEPARATOR_STRING = "\", \"";
  String OUTPUT_SUBDIAGRAM_CLOSER_STRING = "\" ) { ";
  String OUTPUT_VAL_CLOSER_STRING = "\" )";

  String DEBUG_TEXT_SET_TO = " set to [";


}
