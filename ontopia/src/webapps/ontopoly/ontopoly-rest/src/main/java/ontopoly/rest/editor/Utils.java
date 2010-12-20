package ontopoly.rest.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.utils.PSI;
import net.ontopia.utils.ObjectUtils;
import ontopoly.model.AssociationField;
import ontopoly.model.Cardinality;
import ontopoly.model.DataType;
import ontopoly.model.EditMode;
import ontopoly.model.FieldAssignment;
import ontopoly.model.FieldDefinition;
import ontopoly.model.FieldInstance;
import ontopoly.model.FieldsView;
import ontopoly.model.InterfaceControl;
import ontopoly.model.OccurrenceField;
import ontopoly.model.RoleField;
import ontopoly.model.RoleField.ValueIF;
import ontopoly.model.Topic;
import ontopoly.model.TopicType;
import ontopoly.model.ViewModes;
import ontopoly.utils.OntopolyUtils;
import ontopoly.utils.TopicIdComparator;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class Utils {

  public static class Link {
    private String rel;
    private String href;

    public Link(String rel, String href) {
      this.rel = rel;
      this.href = href;
    }

    public void setRel(String rel) {
      this.rel = rel;
    }
    public String getRel() {
      return rel;
    }
    public void setHref(String href) {
      this.href = href;
    }
    public String getHref() {
      return href;
    }

  }

  protected static String getEditLinkFor(UriInfo uriInfo, Topic topic, FieldsView fieldsView) {
    return uriInfo.getBaseUri() + "editor/topic/" + topic.getTopicMap().getId() + "/" + topic.getId() + "/" + fieldsView.getId();
  }

  protected static String getCreateLinkFor(UriInfo uriInfo, TopicType topicType, FieldsView fieldsView) {
    return uriInfo.getBaseUri() + "editor/topic/" + topicType.getTopicMap().getId() + "/_" + topicType.getId() + "/" + fieldsView.getId();
  }

  protected static String getCreateInstanceLinkFor(UriInfo uriInfo, TopicType topicType) {
    return uriInfo.getBaseUri() + "editor/create-instance/" + topicType.getTopicMap().getId() + "/" + topicType.getId();
  }
  
  public static Map<String,Object> getTopicInfo(UriInfo uriInfo, Topic topic, TopicType topicType, FieldsView fieldsView) {
    Map<String,Object> result = new LinkedHashMap<String,Object>();

    result.put("id", topic.getId());
    result.put("name", topic.getName());
    
    Map<String,Object> typeInfo = new LinkedHashMap<String,Object>();    
    typeInfo.put("id", topicType.getId());
    typeInfo.put("name", topicType.getName());
    
    List<Link> typeLinks = new ArrayList<Link>();
    if (!topicType.isAbstract()) {
      typeLinks.add(new Link("create-instance", getCreateInstanceLinkFor(uriInfo, topicType)));
    }
    typeInfo.put("links", typeLinks);

    result.put("type", typeInfo);

    result.put("view", fieldsView.getId());

    List<Link> topicLinks = new ArrayList<Link>();
    topicLinks.add(new Link("edit", getEditLinkFor(uriInfo, topic, fieldsView)));    
    //    topicLinks.add(new Link("remove", "http://examples.org/topics/" + topic.getId() + "/remove"));
    result.put("links", topicLinks);

    List<Map<String,Object>> fields = new ArrayList<Map<String,Object>>(); 

    for (FieldInstance fieldInstance : topic.getFieldInstances(topicType, fieldsView)) {
      fields.add(getFieldInfo(uriInfo, topic, topicType, fieldsView, fieldInstance));
    }
    result.put("fields", fields);
    result.put("views", getViews(uriInfo, topic, topicType, fieldsView));
    return result;
  }
  
  public static Map<String,Object> getNewTopicInfo(UriInfo uriInfo, TopicType topicType, FieldsView fieldsView) {
    return getNewTopicInfo(uriInfo, topicType, fieldsView, null, null);
  }
  
  public static Map<String,Object> getNewTopicInfo(UriInfo uriInfo, TopicType topicType, FieldsView fieldsView, String parentId, String parentFieldId) {
    Map<String,Object> result = new LinkedHashMap<String,Object>();

    if (parentId != null) {
      Map<String,Object> origin = new LinkedHashMap<String,Object>();    
      origin.put("topicId", parentId);
      origin.put("fieldId", parentFieldId);
      result.put("origin", origin);
    }
    
    Map<String,Object> typeInfo = new LinkedHashMap<String,Object>();    
    typeInfo.put("id", topicType.getId());
    typeInfo.put("name", topicType.getName());
    result.put("type", typeInfo);

    result.put("view", fieldsView.getId());

    List<Link> topicLinks = new ArrayList<Link>();
    topicLinks.add(new Link("create", getCreateLinkFor(uriInfo, topicType, fieldsView)));    
    result.put("links", topicLinks);

    List<Map<String,Object>> fields = new ArrayList<Map<String,Object>>(); 

    Topic topic = null;
    for (FieldAssignment fieldAssignment : topicType.getFieldAssignments(fieldsView)) {
      FieldDefinition fieldDefinition = fieldAssignment.getFieldDefinition();
      fields.add(getFieldInfo(uriInfo, topic, topicType, fieldsView, fieldDefinition, Collections.emptyList()));
    }
    result.put("fields", fields);
    result.put("views", Collections.singleton(getView(uriInfo, null, fieldsView)));
    return result;
  }

  private static Map<String, Object> getFieldInfo(UriInfo uriInfo,
      Topic topic, TopicType topicType, FieldsView parentView,
      FieldInstance fieldInstance) {

    FieldAssignment fieldAssignment = fieldInstance.getFieldAssignment();
    FieldDefinition fieldDefinition = fieldAssignment.getFieldDefinition();
    
    Collection<? extends Object> fieldValues = fieldInstance.getValues();
    
    return getFieldInfo(uriInfo, topic, topicType, parentView, fieldDefinition, fieldValues);
  }
  
  private static Map<String, Object> getFieldInfo(UriInfo uriInfo,
      Topic topic, TopicType topicType, FieldsView parentView,
      FieldDefinition fieldDefinition, Collection<? extends Object> fieldValues) {

    boolean isNewTopic = topic == null;
    
    String topicMapId = fieldDefinition.getTopicMap().getId();
    String topicId = isNewTopic ? "_" + topicType.getId() : topic.getId();
    String parentViewId = parentView.getId();
    String fieldDefinitionId = fieldDefinition.getId();
    
    String fieldReference = topicMapId + "/" + topicId + "/" + parentViewId + "/" + fieldDefinitionId;
    
    Map<String,Object> field = new LinkedHashMap<String,Object>();

    int fieldType = fieldDefinition.getFieldType();

    FieldsView childView = fieldDefinition.getValueView(parentView);
    ViewModes viewModes = fieldDefinition.getViewModes(childView);

    field.put("id", fieldDefinition.getId());
    field.put("name", fieldDefinition.getFieldName());

    Cardinality cardinality = fieldDefinition.getCardinality();
    field.put("cardinality", cardinality.getLocator().getExternalForm());

    if (fieldType == FieldDefinition.FIELD_TYPE_ROLE) {
      RoleField roleField = (RoleField)fieldDefinition; 
      field.put("type", fieldDefinition.getLocator().getExternalForm());
      field.put("arity", roleField.getAssociationField().getArity());

      int arity = roleField.getAssociationField().getArity();
      if (arity == 2) {
        EditMode editMode = roleField.getEditMode();
        boolean allowEdit = !viewModes.isReadOnly() && !editMode.isNoEdit();
        boolean allowAddRemove = allowEdit && !editMode.isNewValuesOnly();
        boolean allowCreate = allowEdit && !editMode.isExistingValuesOnly();
        if (!allowEdit) {
          field.put("readOnly", Boolean.TRUE);
        }
        
        for (RoleField otherRoleField : roleField.getOtherRoleFields()) {
          List<Link> fieldLinks = new ArrayList<Link>();

          InterfaceControl interfaceControl = otherRoleField.getInterfaceControl();
          field.put("interfaceControl", interfaceControl.getLocator().getExternalForm());          
          if (interfaceControl.isDropDownList()) {
            if (allowCreate && !isNewTopic) {
              fieldLinks.add(new Link("available-field-types", uriInfo.getBaseUri() + "editor/available-field-types/" + fieldReference));
            }
            if (allowAddRemove) {
              // ISSUE: should add-values and remove-values be links on list result instead?
              fieldLinks.add(new Link("available-field-values", uriInfo.getBaseUri() + "editor/available-field-values/" + fieldReference));
              if (!isNewTopic) {
                fieldLinks.add(new Link("add-field-values", uriInfo.getBaseUri() + "editor/add-field-values/" + fieldReference));
                fieldLinks.add(new Link("remove-field-values", uriInfo.getBaseUri() + "editor/remove-field-values/" + fieldReference));
              }
            }
          }
          field.put("links", fieldLinks);
        }
      }
      // TODO: n-ary roles not yet supported
      
    } else if (fieldType == FieldDefinition.FIELD_TYPE_OCCURRENCE) {
      OccurrenceField occurrenceField = (OccurrenceField)fieldDefinition;
      field.put("type", fieldDefinition.getLocator().getExternalForm());
      DataType dataType = occurrenceField.getDataType();
      LocatorIF locator = dataType.getLocator();
      field.put("datatype", locator.getExternalForm());
      field.put("height", occurrenceField.getHeight());
      field.put("width", occurrenceField.getWidth());
      if (viewModes.isReadOnly()) {
        field.put("readOnly", Boolean.TRUE);
        field.put("links", Collections.EMPTY_LIST);
      } else {
        List<Link> fieldLinks = new ArrayList<Link>();
        fieldLinks.add(new Link("add-field-values", uriInfo.getBaseUri() + "editor/add-field-values/" + fieldReference));
        fieldLinks.add(new Link("remove-field-values", uriInfo.getBaseUri() + "editor/remove-field-values/" + fieldReference));
        field.put("links", fieldLinks);
      }
    } else if (fieldType == FieldDefinition.FIELD_TYPE_NAME) {
      //        NameField nameField = (NameField)fieldDefinition;
      field.put("type", fieldDefinition.getLocator().getExternalForm());
      field.put("datatype", PSI.XSD_STRING);
      if (viewModes.isReadOnly()) {
        field.put("readOnly", Boolean.TRUE);
        field.put("links", Collections.EMPTY_LIST);
      } else {
        List<Link> fieldLinks = new ArrayList<Link>();
        fieldLinks.add(new Link("add-field-values", uriInfo.getBaseUri() + "editor/add-field-values/" + fieldReference));
        fieldLinks.add(new Link("remove-field-values", uriInfo.getBaseUri() + "editor/remove-field-values/" + fieldReference));
        field.put("links", fieldLinks);
      }
    } else if (fieldType == FieldDefinition.FIELD_TYPE_IDENTITY) {
      //        IdentityField identityField = (IdentityField)fieldDefinition;
      field.put("type", fieldDefinition.getLocator().getExternalForm());
      field.put("datatype", PSI.XSD_URI);
      field.put("validation", "uri");
      if (viewModes.isReadOnly()) {
        field.put("readOnly", Boolean.TRUE);
        field.put("links", Collections.EMPTY_LIST);
      } else {
        List<Link> fieldLinks = new ArrayList<Link>();
        fieldLinks.add(new Link("add-field-values", uriInfo.getBaseUri() + "editor/add-field-values/" + fieldReference));
        fieldLinks.add(new Link("remove-field-values", uriInfo.getBaseUri() + "editor/remove-field-values/" + fieldReference));
        field.put("links", fieldLinks);
      }
    } else if (fieldType == FieldDefinition.FIELD_TYPE_QUERY) {
      //        QueryField queryField = (QueryField)fieldDefinition;
      field.put("type", fieldDefinition.getLocator().getExternalForm());
      if (viewModes.isReadOnly()) {
        field.put("readOnly", Boolean.TRUE);
      }
      field.put("links", Collections.EMPTY_LIST);
    } else {
      throw new RuntimeException("Unknown field type: " + fieldDefinition);
    }

    field.put("values", getValues(uriInfo, topic, topicType, parentView, childView, viewModes, fieldDefinition, fieldValues));
    return field;
  }

  public static List<Map<String, Object>> getViews(UriInfo uriInfo,
      Topic topic, TopicType topicType, FieldsView fieldsView) {

    List<FieldsView> fieldViews = topic.getFieldViews(topicType, fieldsView);

    List<Map<String,Object>> views = new ArrayList<Map<String,Object>>(fieldViews.size()); 
    for (FieldsView _fieldsView : fieldViews) {
      views.add( getView(uriInfo, topic, _fieldsView));
    }
    return views;
  }

  public static Map<String, Object> getView(UriInfo uriInfo, Topic topic,
      FieldsView _fieldsView) {
    Map<String,Object> view = new LinkedHashMap<String,Object>();
    view.put("id", _fieldsView.getId());
    view.put("name", _fieldsView.getName());

    List<Link> links = new ArrayList<Link>();
    if (topic != null) {
      links.add(new Link("edit-in-view", getEditLinkFor(uriInfo, topic, _fieldsView)));
    }
    view.put("links", links);
    return view;
  }

  protected static List<Object> getValues(UriInfo uriInfo, Topic topic, TopicType topicType, FieldsView parentView, FieldsView childView, ViewModes viewModes, FieldDefinition fieldDefinition, Collection<? extends Object> fieldValues) {
    List<Object> result = new ArrayList<Object>(fieldValues.size());
    if (fieldDefinition.getFieldType() == FieldDefinition.FIELD_TYPE_ROLE && 
        ((RoleField)fieldDefinition).getAssociationField().getArity() == 1) {
      result.add(!fieldValues.isEmpty());
    } else {
      for (Object value : fieldValues) {
        result.add(getValue(uriInfo, topic, topicType, fieldDefinition, childView, viewModes, value));
      }
      Collections.sort(result, new Comparator<Object>() {
        public int compare(Object o1, Object o2) {
          if (o1 instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String,Object> v1 = (Map<String,Object>)o1;
            @SuppressWarnings("unchecked")
            Map<String,Object> v2 = (Map<String,Object>)o2;
            String vx1 = (String)v1.get("name");
            if (vx1 == null) {
              vx1 = (String)v1.get("value");
            }
            String vx2 = (String)v2.get("name");
            if (vx2 == null) {
              vx2 = (String)v2.get("value");
            }
            return ObjectUtils.compare(vx1, vx2);
          } else {
            @SuppressWarnings("unchecked")
            List<Map<String,Object>> v1 = (List<Map<String,Object>>)o1;
            @SuppressWarnings("unchecked")
            List<Map<String,Object>> v2 = (List<Map<String,Object>>)o2;
            return compare(v1.get(0), v2.get(0));
          } 
        }
      });
    }
    return result;
  }

  protected static Object getValue(UriInfo uriInfo, Topic topic, TopicType topicType, FieldDefinition fieldDefinition, FieldsView childView, ViewModes viewModes, Object fieldValue) {
    switch (fieldDefinition.getFieldType()) {
    case FieldDefinition.FIELD_TYPE_NAME:
      return getNameValue((TopicNameIF)fieldValue, viewModes);
    case FieldDefinition.FIELD_TYPE_IDENTITY: 
      return getIdentityValue((LocatorIF)fieldValue, viewModes);
    case FieldDefinition.FIELD_TYPE_OCCURRENCE:
      return getOccurrenceValue((OccurrenceIF)fieldValue, viewModes);
    case FieldDefinition.FIELD_TYPE_ROLE:
      RoleField roleField = (RoleField)fieldDefinition;
      RoleField.ValueIF value = (RoleField.ValueIF)fieldValue;
      int arity = value.getArity(); 
      if (arity == 2) {
        for (RoleField rf : value.getRoleFields()) {
          if (!rf.equals(roleField)) {
            Topic valueTopic = value.getPlayer(rf, topic);
            if (viewModes.isEmbedded()) {
              TopicType valueType = OntopolyUtils.getDefaultTopicType(valueTopic);
              return getTopicInfo(uriInfo, valueTopic, valueType, childView);
            } else {
              return getExistingTopicFieldValue(uriInfo, topic, fieldDefinition, valueTopic, rf, childView, viewModes);
            }
          }
        }
      } else if (arity > 2) {
        List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
        for (RoleField rf : value.getRoleFields()) {
          if (!rf.equals(roleField)) {
            Topic valueTopic = value.getPlayer(rf, topic);
            Map<String,Object> roleValue = new LinkedHashMap<String,Object>();
            roleValue.put("id", rf.getId());
            if (viewModes.isEmbedded()) {
              TopicType valueType = OntopolyUtils.getDefaultTopicType(valueTopic);
              roleValue.put("value", getTopicInfo(uriInfo, valueTopic, valueType, childView));
            } else {
              roleValue.put("value", getExistingTopicFieldValue(uriInfo, topic, fieldDefinition, valueTopic, rf, childView, viewModes));
            }
            result.add(roleValue);
          }
        }
        return result;
      }
      return null;
    case FieldDefinition.FIELD_TYPE_QUERY: 
      if (fieldValue instanceof Topic) {
        return getExistingTopicFieldValue(uriInfo, topic, fieldDefinition, (Topic)fieldValue, null, childView, viewModes);
      } else {
        return fieldValue;
      }
    default:
      throw new RuntimeException("Unknown field definition type: " + fieldDefinition);
    }
  }

  public static Map<String,Object> getNameValue(TopicNameIF topicName, ViewModes viewModes) {
    Map<String,Object> result = new LinkedHashMap<String, Object>();
    result.put("value", topicName.getValue());
    boolean removable = !viewModes.isReadOnly();
    if (removable) {
      result.put("removable", Boolean.TRUE);
    }
    return result;
  }

  public static Map<String,Object> getIdentityValue(LocatorIF identity, ViewModes viewModes) {
    Map<String,Object> result = new LinkedHashMap<String, Object>();
    result.put("value", identity.getExternalForm());
    boolean removable = !viewModes.isReadOnly();
    if (removable) {
      result.put("removable", Boolean.TRUE);
    }
    return result;
  }

  public static Map<String,Object> getOccurrenceValue(OccurrenceIF occurrence, ViewModes viewModes) {
    Map<String,Object> result = new LinkedHashMap<String, Object>();
    result.put("value", occurrence.getValue());
    boolean removable = !viewModes.isReadOnly();
    if (removable) {
      result.put("removable", Boolean.TRUE);
    }
    return result;
  }

  public static Object getExistingTopicFieldValue(UriInfo uriInfo, 
      Topic parentTopic, FieldDefinition parentFieldDefinition, 
      Topic value, FieldDefinition childFieldDefinition, FieldsView childView, ViewModes viewModes) {
    
    Map<String, Object> result = new LinkedHashMap<String,Object>();
    result.put("id", value.getId());
    result.put("name", value.getName());

    if (!viewModes.isReadOnly()) {
      result.put("removable", Boolean.TRUE);
    }

    List<Link> links = new ArrayList<Link>();
    if (viewModes.isTraversable()) {
      links.add(new Link("edit", getEditLinkFor(uriInfo, value, childView)));
    }
    result.put("links", links);

    return result;
  }

  public static Object getAllowedTopicFieldValue(UriInfo uriInfo, 
      Topic parentTopic, TopicType parentTopicType, FieldDefinition parentFieldDefinition, 
      Topic value, FieldDefinition childFieldDefinition, FieldsView childView, boolean traversable, boolean addable) {
    
    Map<String, Object> result = new LinkedHashMap<String,Object>();
    result.put("id", value.getId());
    result.put("name", value.getName());

    List<Link> links = new ArrayList<Link>();
    if (traversable) {
      links.add(new Link("edit", getEditLinkFor(uriInfo, value, childView)));
    }
    if (parentTopic != null && addable) {
      String topicMapId = parentFieldDefinition.getTopicMap().getId();

      String topicId = parentTopic.getId();

      links.add(new Link("add-field-value", uriInfo.getBaseUri() + "editor/add-field-value/" + topicMapId + "/" + topicId + "/" + parentFieldDefinition.getId() + "/" + value.getId()));
    }
    result.put("links", links);

    return result;
  }

  public static Object getCreateFieldInstance(UriInfo uriInfo, 
      Topic parentTopic, FieldDefinition parentFieldDefinition, 
      TopicType playerType, FieldDefinition childFieldDefinition, FieldsView childView, ViewModes viewModes) {
    
    Map<String, Object> result = new LinkedHashMap<String,Object>();
    result.put("id", playerType.getId());
    result.put("name", playerType.getName());

    List<Link> links = new ArrayList<Link>();
    String topicMapId = parentTopic.getTopicMap().getId();
    links.add(new Link("create-field-instance", uriInfo.getBaseUri() + "editor/create-field-instance/" + topicMapId + "/" + parentTopic.getId() + "/" + parentFieldDefinition.getId() + "/" + playerType.getId()));
    result.put("links", links);

    return result;
  }

  public static Map<String, Object> addFieldValues(UriInfo uriInfo, Topic topic,
      TopicType topicType, FieldsView fieldsView, String fieldId, JSONObject fieldObject) {

    try {
      FieldInstance fieldInstance = getFieldInstanceFor(topic, topicType, fieldsView, fieldId);

      if  (fieldInstance != null) {
        FieldDefinition fieldDefinition = fieldInstance.getFieldAssignment().getFieldDefinition();
        int fieldType = fieldDefinition.getFieldType();

        RoleFields roleFields = getRoleFieldsObject(fieldDefinition, fieldType);

        JSONArray values = fieldObject.getJSONArray("values");
        int valuesCount = values.length();
        for (int vc=0; vc < valuesCount; vc++) {

          Object value;
          if (fieldType == FieldDefinition.FIELD_TYPE_ROLE) {
            RoleField roleField = (RoleField)fieldDefinition;
            value = getRoleFieldValue(topic, roleField, roleFields, values, vc);
          } else {
            value = getFieldValue(topic, fieldDefinition, values, vc);
          }
          System.out.println("+V: " + value);

          if (value instanceof RoleData) {
            System.out.println("---> "+ ((RoleData)value).createValue());
            fieldInstance.addValue(((RoleData)value).createValue(), null);
          } else {
            fieldInstance.addValue(value, null);
          }
        }
      }
      return getFieldInfo(uriInfo, topic, topicType, fieldsView, fieldInstance);
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
  }

  public static Map<String, Object>  removeFieldValues(UriInfo uriInfo, Topic topic,
      TopicType topicType, FieldsView fieldsView, String fieldId, JSONObject fieldObject) {

    try {
      FieldInstance fieldInstance = getFieldInstanceFor(topic, topicType, fieldsView, fieldId);

      if  (fieldInstance != null) {
        FieldDefinition fieldDefinition = fieldInstance.getFieldAssignment().getFieldDefinition();
        int fieldType = fieldDefinition.getFieldType();

        RoleFields roleFields = getRoleFieldsObject(fieldDefinition, fieldType);

        JSONArray values = fieldObject.getJSONArray("values");
        int valuesCount = values.length();
        for (int vc=0; vc < valuesCount; vc++) {

          Object value;
          if (fieldType == FieldDefinition.FIELD_TYPE_ROLE) {
            RoleField roleField = (RoleField)fieldDefinition;
            value = getRoleFieldValue(topic, roleField, roleFields, values, vc);
          } else {
            value = getFieldValue(topic, fieldDefinition, values, vc);
          }
          System.out.println("-V: " + value);

          if (value instanceof RoleData) {
            System.out.println("---> "+ ((RoleData)value).createValue());
            fieldInstance.removeValue(((RoleData)value).createValue(), null);
          } else {
            fieldInstance.removeValue(value, null);
          }
        }
      }
      return getFieldInfo(uriInfo, topic, topicType, fieldsView, fieldInstance);
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
  }

  private static RoleFields getRoleFieldsObject(
      FieldDefinition fieldDefinition, int fieldType) {
    if (fieldType == FieldDefinition.FIELD_TYPE_ROLE) {
      RoleField roleField = (RoleField)fieldDefinition;
      AssociationField associationField = roleField.getAssociationField();
      return new RoleFields(associationField);
    } else {
      return null;
    }
  }

  public static Map<String, Object> updateTopic(UriInfo uriInfo, Topic topic,
      TopicType topicType, FieldsView fieldsView, JSONObject data) {

    Map<String, FieldInstance> fields = getFieldInstanceMap(topic, topicType, fieldsView);

    try {
      JSONArray fieldsArray = data.getJSONArray("fields");
      int fieldsCount = fieldsArray.length();
      for (int fc=0; fc < fieldsCount; fc++) {

        JSONObject fieldObject = fieldsArray.getJSONObject(fc);
        String fieldId = fieldObject.getString("id");

        FieldInstance fieldInstance = fields.get(fieldId);
        FieldDefinition fieldDefinition = fieldInstance.getFieldAssignment().getFieldDefinition();
        int fieldType = fieldDefinition.getFieldType();

        if (fieldType == FieldDefinition.FIELD_TYPE_NAME ||
            fieldType == FieldDefinition.FIELD_TYPE_OCCURRENCE ||
            fieldType == FieldDefinition.FIELD_TYPE_IDENTITY ||
            fieldType == FieldDefinition.FIELD_TYPE_ROLE) {

          if  (fields.containsKey(fieldId)) {

            RoleFields roleFields = getRoleFieldsObject(fieldDefinition, fieldType);

            // TODO: support unary associations with boolean value

            Collection<? extends Object> _existingValues = fieldInstance.getValues();

            Collection<Object> removableValues = new HashSet<Object>(_existingValues.size());          
            Collection<Object> addableValues = new HashSet<Object>();

            for (Object existingValue : _existingValues) {
              Object actualValue = null;
              if (fieldType == FieldDefinition.FIELD_TYPE_NAME) {
                actualValue = ((TopicNameIF)existingValue).getValue();
              }
              else if (fieldType == FieldDefinition.FIELD_TYPE_OCCURRENCE) {
                actualValue = ((OccurrenceIF)existingValue).getValue();
              }
              else if (fieldType == FieldDefinition.FIELD_TYPE_IDENTITY) {
                actualValue = ((LocatorIF)existingValue).getExternalForm();
              }
              else if (fieldType == FieldDefinition.FIELD_TYPE_ROLE) {
                RoleField.ValueIF value = (RoleField.ValueIF)existingValue;
                actualValue = roleFields.createData(value.getRoleFields(), value.getPlayers());
              }
              if (actualValue != null) {
                removableValues.add(actualValue);
              }
            }
            Collection<Object> existingValues = new HashSet<Object>(removableValues);          

            JSONArray values = fieldObject.getJSONArray("values");
            int valuesCount = values.length();
            for (int vc=0; vc < valuesCount; vc++) {

              Object value;
              if (fieldType == FieldDefinition.FIELD_TYPE_ROLE) {
                RoleField roleField = (RoleField)fieldDefinition;
                value = getRoleFieldValue(topic, roleField, roleFields, values, vc);
              } else {
                value = getFieldValue(topic, fieldDefinition, values, vc);
              }

              System.out.println("E: " + value + " : " + existingValues);
              removableValues.remove(value);
              if (!existingValues.contains(value)) {
                addableValues.add(value);
              }
            }
            for (Object value : removableValues) {
              System.out.println("-V: " + value);
              if (value instanceof RoleData) {
                fieldInstance.removeValue(((RoleData)value).createValue(), null);
              } else {
                fieldInstance.removeValue(value, null);
              }
            }          
            for (Object value : addableValues) {
              System.out.println("+V: " + value);
              if (value instanceof RoleData) {
                System.out.println("---> "+ ((RoleData)value).createValue());
                fieldInstance.addValue(((RoleData)value).createValue(), null);
              } else {
                fieldInstance.addValue(value, null);
              }
            }
          } else {
            throw new RuntimeException("Field " + fieldId + " not declared on topic " + topic.getId());
          }
        }
      }
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
    return Utils.getTopicInfo(uriInfo, topic, topicType, fieldsView);
  }

  private static FieldInstance getFieldInstanceFor(Topic topic,
      TopicType topicType, FieldsView fieldsView, String fieldId) {
    Map<String, FieldInstance> fields = getFieldInstanceMap(topic, topicType, fieldsView);
    return fields.get(fieldId);
  }

  private static Map<String, FieldInstance> getFieldInstanceMap(Topic topic,
      TopicType topicType, FieldsView fieldsView) {
    Map<String,FieldInstance> fields = new HashMap<String,FieldInstance>();
    for (FieldInstance fieldInstance : topic.getFieldInstances(topicType, fieldsView)) {
      FieldDefinition fieldDefinition = fieldInstance.getFieldAssignment().getFieldDefinition();
      fields.put(fieldDefinition.getId(), fieldInstance);
    }
    return fields;
  }

  private static Object getFieldValue(
      Topic topic, FieldDefinition fieldDefinition,
      JSONArray values, int vindex) throws JSONException {

    JSONObject valueObject = values.getJSONObject(vindex);
    return valueObject.getString("value");              
  }

  private static Object getRoleFieldValue(Topic topic,
      RoleField roleField, RoleFields roleFields, 
      JSONArray values, int vindex) throws JSONException {

    int arity = roleFields.getArity();
    RoleField.ValueIF roleValue = RoleField.createValue(arity);
    roleValue.addPlayer(roleField, topic); 
    System.out.println("R1: " + roleField + " " + topic);
    if (arity == 2) {
      JSONObject valueObject = values.getJSONObject(vindex);
      System.out.println("VO: " + valueObject + " " + roleField.getId());
      String id = valueObject.getString("id");
      Topic player = topic.getTopicMap().getTopicById(id);
      System.out.println("R2: " + roleField.getOtherRoleFields().iterator().next() + " " + player);
      roleValue.addPlayer(roleField.getOtherRoleFields().iterator().next(), player);
    } else if (arity > 2) {
      JSONArray rolesArray = values.getJSONArray(vindex);
      int rolesCount = rolesArray.length();
      for (int rc=0; rc < rolesCount; rc++) {
        JSONObject valueObject = values.getJSONObject(vindex);
        String roleFieldId = valueObject.getString("id");
        RoleField rfield = roleFields.getRoleFieldById(roleFieldId);
        String playerId = valueObject.getJSONObject("value").getString("id");
        Topic player = topic.getTopicMap().getTopicById(playerId);
        roleValue.addPlayer(rfield, player);                    
      }
    }
    return roleFields.createData(roleValue.getRoleFields(), roleValue.getPlayers());
  }

  static class RoleFields {

    private final RoleField[] roleFields;

    public RoleFields(AssociationField associationField) {
      roleFields = new RoleField[associationField.getArity()];
      List<RoleField> fieldsForRoles = associationField.getFieldsForRoles();
      for (int i=0; i < fieldsForRoles.size(); i++) {
        roleFields[i] = fieldsForRoles.get(i);
      }
      Arrays.sort(roleFields, TopicIdComparator.INSTANCE);
    }  

    public int getArity() {
      return roleFields.length;
    }

    public RoleField getRoleFieldById(String id) {
      for (int i=0;i < roleFields.length; i++) {
        if (roleFields[i].getId().equals(id))
          return roleFields[i];
      }
      return null;
    }

    public RoleField[] getRoleFields() {
      return roleFields;
    }

    public RoleData createData(RoleField[] roles, Topic[] players) {
      Topic[] result = new Topic[players.length];
      for (int i=0; i < roles.length; i++) {
        int index = indexOf(roles[i]);
        result[index] = players[i];
      }
      return new RoleData(this, result);
    }

    private int indexOf(RoleField roleField) {
      for (int i=0; i < roleFields.length; i++) {
        if (roleFields[i].equals(roleField))
          return i;
      }
      return -1;
    }

  }

  static class RoleData {
    private final RoleFields roleFields;
    private final Topic[] players;
    private final int hashCode;

    public RoleData(RoleFields roleFields, Topic[] players) {
      this.roleFields = roleFields;
      this.players = players;
      int hc = 0;
      for (int i=0; i < players.length; i++) {
        hc += players[i].hashCode();
      }
      this.hashCode = hc;
    }

    public Topic getPlayer(int index) {
      return players[index];
    }

    public int hashCode() {
      return hashCode;
    }

    public boolean equals(Object o) {
      if (o instanceof RoleData) {
        RoleData other = (RoleData)o;
        if (this.players.length == other.players.length) {
          for (int i=0; i < this.players.length; i++) {
            if (!ObjectUtils.equals(this.players[i], other.players[i]))
              return false;
          }
          return true;
        }
      }
      return false;
    }

    public RoleField.ValueIF createValue() {
      RoleField[] fields = roleFields.getRoleFields();
      ValueIF value = RoleField.createValue(roleFields.getArity());
      for (int i=0; i < fields.length; i++) {
        value.addPlayer(fields[i], players[i]);
      }
      return value;
    }
  }

}
