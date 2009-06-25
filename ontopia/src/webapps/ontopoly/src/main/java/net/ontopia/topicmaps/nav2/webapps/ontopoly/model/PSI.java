package net.ontopia.topicmaps.nav2.webapps.ontopoly.model;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;

public class PSI {

	public static final LocatorIF ON = URILocator.create(TopicMap.ON);
	public static final LocatorIF XTM = URILocator.create(TopicMap.XTM);
	public static final LocatorIF TEST = URILocator.create(TopicMap.TEST);
	public static final LocatorIF TECH = URILocator.create(TopicMap.TECH);
	public static final LocatorIF DC = URILocator.create(TopicMap.DC);
	public static final LocatorIF XSD = URILocator.create(TopicMap.XSD);

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

  public static final LocatorIF ON_UNTYPED_NAME = PSI.ON.resolveAbsolute("untyped-name");
  public static final LocatorIF ON_UNTYPED_TOPIC = PSI.ON.resolveAbsolute("untyped-topic");

  public static final LocatorIF ON_SUBJECT_LOCATOR = PSI.ON.resolveAbsolute("subject-locator");
  public static final LocatorIF ON_SUBJECT_IDENTIFIER = PSI.ON.resolveAbsolute("subject-identifier");
  public static final LocatorIF ON_ITEM_IDENTIFIER = PSI.ON.resolveAbsolute("item-identifier");

  public static final LocatorIF ON_ASSOCIATION_FIELD = PSI.ON.resolveAbsolute("association-field");
  public static final LocatorIF ON_IDENTITY_FIELD = PSI.ON.resolveAbsolute("identity-field");
  public static final LocatorIF ON_NAME_FIELD = PSI.ON.resolveAbsolute("name-field");
  public static final LocatorIF ON_OCCURRENCE_FIELD = PSI.ON.resolveAbsolute("occurrence-field");
  public static final LocatorIF ON_ROLE_FIELD = PSI.ON.resolveAbsolute("role-field");

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

  public static final LocatorIF XTM_SUPERCLASS_SUBCLASS = XTM.resolveAbsolute("superclass-subclass");
  public static final LocatorIF XTM_SUPERCLASS = XTM.resolveAbsolute("superclass");
  public static final LocatorIF XTM_SUBCLASS = XTM.resolveAbsolute("subclass");

}
