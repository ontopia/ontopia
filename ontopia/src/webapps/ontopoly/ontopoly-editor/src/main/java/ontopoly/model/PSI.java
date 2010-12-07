package ontopoly.model;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;

public class PSI {

  public static final LocatorIF ON = URILocator.create(TopicMap.ON);
  public static final LocatorIF XTM = URILocator.create(TopicMap.XTM);
  public static final LocatorIF TEST = URILocator.create(TopicMap.TEST);
  public static final LocatorIF TECH = URILocator.create(TopicMap.TECH);
  public static final LocatorIF DC = URILocator.create(TopicMap.DC);
  public static final LocatorIF XSD = URILocator.create(TopicMap.XSD);
  public static final LocatorIF TMDM = URILocator.create(TopicMap.TMDM);

  public static final LocatorIF ON_TED_TOPIC_MAP = PSI.ON.resolveAbsolute("ted-topic-map");
  public static final LocatorIF ON_TOPIC_MAP_ID = PSI.ON.resolveAbsolute("topic-map-id");

  public static final LocatorIF ON_ONTOLOGY_VERSION = PSI.ON.resolveAbsolute("ted-ontology-version");

  public static final LocatorIF ON_SYSTEM_TOPIC = PSI.ON.resolveAbsolute("system-topic");
  public static final LocatorIF ON_PUBLIC_SYSTEM_TOPIC = PSI.ON.resolveAbsolute("public-system-topic");
  
  public static final LocatorIF ON_TOPIC_MAP = PSI.ON.resolveAbsolute("topic-map");
  public static final LocatorIF ON_TOPIC_TYPE = PSI.ON.resolveAbsolute("topic-type");
  public static final LocatorIF ON_ASSOCIATION_TYPE = PSI.ON.resolveAbsolute("association-type");
  public static final LocatorIF ON_ROLE_TYPE = PSI.ON.resolveAbsolute("role-type");
  public static final LocatorIF ON_OCCURRENCE_TYPE = PSI.ON.resolveAbsolute("occurrence-type");
  public static final LocatorIF ON_NAME_TYPE = PSI.ON.resolveAbsolute("name-type");
  public static final LocatorIF ON_IDENTITY_TYPE = PSI.ON.resolveAbsolute("identity-type");

  public static final LocatorIF ON_ONTOLOGY_TYPE = PSI.ON.resolveAbsolute("ontology-type");

  /**
   * Until version 1.9 of the Ontopoly meta-schema we used our own PSI
   * for the default name type, instead of the TMDM PSI. From version
   * 2.0 that changed, and we are now using the TMDM PSI. This PSI
   * should *never* occur in *any* post-1.9 topic map. The field
   * remains here because the Upgrade_2_0 class uses it.
   */
  public static final LocatorIF ON_DEPRECATED_UNTYPED_NAME =
    PSI.ON.resolveAbsolute("untyped-name");
  public static final LocatorIF ON_UNTYPED_TOPIC = PSI.ON.resolveAbsolute("untyped-topic");

  public static final LocatorIF ON_SUBJECT_LOCATOR = PSI.ON.resolveAbsolute("subject-locator");
  public static final LocatorIF ON_SUBJECT_IDENTIFIER = PSI.ON.resolveAbsolute("subject-identifier");
  public static final LocatorIF ON_ITEM_IDENTIFIER = PSI.ON.resolveAbsolute("item-identifier");

  public static final LocatorIF ON_FIELD_DEFINITION = PSI.ON.resolveAbsolute("field-definition");

  public static final LocatorIF ON_ASSOCIATION_FIELD = PSI.ON.resolveAbsolute("association-field");
  public static final LocatorIF ON_IDENTITY_FIELD = PSI.ON.resolveAbsolute("identity-field");
  public static final LocatorIF ON_NAME_FIELD = PSI.ON.resolveAbsolute("name-field");
  public static final LocatorIF ON_OCCURRENCE_FIELD = PSI.ON.resolveAbsolute("occurrence-field");
  public static final LocatorIF ON_ROLE_FIELD = PSI.ON.resolveAbsolute("role-field");
  public static final LocatorIF ON_QUERY_FIELD = PSI.ON.resolveAbsolute("query-field");

  public static final LocatorIF ON_INTERFACE_CONTROL_DROP_DOWN_LIST = PSI.ON.resolveAbsolute("drop-down-list");
  public static final LocatorIF ON_INTERFACE_CONTROL_SEARCH_DIALOG = PSI.ON.resolveAbsolute("search-dialog");
  public static final LocatorIF ON_INTERFACE_CONTROL_BROWSE_DIALOG = PSI.ON.resolveAbsolute("browse-dialog");
  public static final LocatorIF ON_INTERFACE_CONTROL_AUTO_COMPLETE = PSI.ON.resolveAbsolute("auto-complete");

  public static final LocatorIF ON_CARDINALITY_0_1 = PSI.ON.resolveAbsolute("cardinality-0-1");
  public static final LocatorIF ON_CARDINALITY_1_1 = PSI.ON.resolveAbsolute("cardinality-1-1");
  public static final LocatorIF ON_CARDINALITY_0_M = PSI.ON.resolveAbsolute("cardinality-0-M");
  public static final LocatorIF ON_CARDINALITY_1_M = PSI.ON.resolveAbsolute("cardinality-1-M");

  public static final LocatorIF ON_DATATYPE_HTML = PSI.ON.resolveAbsolute("datatype-html");
  public static final LocatorIF ON_DATATYPE_IMAGE = PSI.ON.resolveAbsolute("datatype-image");

  public static final LocatorIF ON_FIELD_VALUE_ORDER = PSI.ON.resolveAbsolute("field-value-order");

  public static final LocatorIF ON_DEFAULT_FIELDS_VIEW = PSI.ON.resolveAbsolute("default-fields-view");

  public static final LocatorIF ON_EDIT_MODE_EXISTING_VALUES_ONLY = PSI.ON.resolveAbsolute("edit-mode-existing-values-only");
  public static final LocatorIF ON_EDIT_MODE_NEW_VALUES_ONLY = PSI.ON.resolveAbsolute("edit-mode-new-values-only");
  public static final LocatorIF ON_EDIT_MODE_OWNED_VALUES = PSI.ON.resolveAbsolute("edit-mode-owned-values");
  public static final LocatorIF ON_EDIT_MODE_NORMAL = PSI.ON.resolveAbsolute("edit-mode-normal");
  public static final LocatorIF ON_EDIT_MODE_NO_EDIT = PSI.ON.resolveAbsolute("edit-mode-no-edit");

  public static final LocatorIF ON_CREATE_ACTION_NONE = PSI.ON.resolveAbsolute("create-action-none");
  public static final LocatorIF ON_CREATE_ACTION_POPUP = PSI.ON.resolveAbsolute("create-action-popup");
  public static final LocatorIF ON_CREATE_ACTION_NAVIGATE = PSI.ON.resolveAbsolute("create-action-navigate");

  public static final LocatorIF ON_FIELDS_VIEW = PSI.ON.resolveAbsolute("fields-view");
  public static final LocatorIF ON_IS_EMBEDDED_VIEW = PSI.ON.resolveAbsolute("is-embedded-view");

  public static final LocatorIF ON_DESCRIPTION = PSI.ON.resolveAbsolute("description");
  public static final LocatorIF ON_CREATOR = PSI.ON.resolveAbsolute("creator");
  public static final LocatorIF ON_VERSION = PSI.ON.resolveAbsolute("version");

  public static final LocatorIF ON_SUPERCLASS_SUBCLASS = PSI.ON.resolveAbsolute("superclass-subclass");
  public static final LocatorIF ON_SUPERCLASS = PSI.ON.resolveAbsolute("superclass");
  public static final LocatorIF ON_SUBCLASS = PSI.ON.resolveAbsolute("subclass");
  
  public static final LocatorIF TMDM_TOPIC_NAME = PSI.TMDM.resolveAbsolute("topic-name");

  public static final LocatorIF ON_LONGITUDE = PSI.ON.resolveAbsolute("longitude");
  public static final LocatorIF ON_LATITUDE = PSI.ON.resolveAbsolute("latitude");

  public static final LocatorIF ON_HEIGHT = PSI.ON.resolveAbsolute("height");
  public static final LocatorIF ON_WIDTH = PSI.ON.resolveAbsolute("width");

  public static final LocatorIF ON_HAS_OCCURRENCE_TYPE = PSI.ON.resolveAbsolute("has-occurrence-type");
  public static final LocatorIF ON_HAS_ASSOCIATION_TYPE = PSI.ON.resolveAbsolute("has-association-type");
  public static final LocatorIF ON_HAS_NAME_TYPE = PSI.ON.resolveAbsolute("has-name-type");
  public static final LocatorIF ON_HAS_IDENTITY_TYPE = PSI.ON.resolveAbsolute("has-identity-type");
  public static final LocatorIF ON_HAS_ROLE_TYPE = PSI.ON.resolveAbsolute("has-role-type");

  public static final LocatorIF ON_HAS_ASSOCIATION_FIELD = PSI.ON.resolveAbsolute("has-association-field");

  public static final LocatorIF ON_HAS_DATATYPE = PSI.ON.resolveAbsolute("has-datatype");
  public static final LocatorIF ON_HAS_CARDINALITY = PSI.ON.resolveAbsolute("has-cardinality");

  public static final LocatorIF ON_USE_EDIT_MODE = PSI.ON.resolveAbsolute("use-edit-mode");
  public static final LocatorIF ON_USE_CREATE_ACTION = PSI.ON.resolveAbsolute("use-create-action");
  public static final LocatorIF ON_USE_INTERFACE_CONTROL = PSI.ON.resolveAbsolute("use-interface-control");

  public static final LocatorIF ON_DATATYPE = PSI.ON.resolveAbsolute("datatype");
  public static final LocatorIF ON_CARDINALITY = PSI.ON.resolveAbsolute("cardinality");
  public static final LocatorIF ON_EDIT_MODE = PSI.ON.resolveAbsolute("edit-mode");
  public static final LocatorIF ON_CREATE_ACTION = PSI.ON.resolveAbsolute("create-action");
  public static final LocatorIF ON_INTERFACE_CONTROL = PSI.ON.resolveAbsolute("interface-control");

}
