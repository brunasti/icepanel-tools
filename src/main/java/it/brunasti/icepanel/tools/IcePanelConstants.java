package it.brunasti.icepanel.tools;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 *
 */
public interface IcePanelConstants {

  String NODE_TYPE = "type";
  String ORIGIN_ID = "originId";
  String PARENT_ID = "parentId";
  String TARGET_ID = "targetId";
  String DIRECTION = "direction";
  String MODEL_CONNECTIONS = "modelConnections";
  String MODEL_OBJECTS = "modelObjects";
  String PARENT_IDS = "parentIds";

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



}
