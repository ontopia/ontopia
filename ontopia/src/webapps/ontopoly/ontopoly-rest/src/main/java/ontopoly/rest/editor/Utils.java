package ontopoly.rest.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
import ontopoly.model.FieldAssignment;
import ontopoly.model.FieldDefinition;
import ontopoly.model.FieldInstance;
import ontopoly.model.FieldsView;
import ontopoly.model.InterfaceControl;
import ontopoly.model.OccurrenceField;
import ontopoly.model.RoleField;
import ontopoly.model.RoleField.ValueIF;
import ontopoly.model.Topic;
import ontopoly.model.TopicMap;
import ontopoly.model.TopicType;
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

  protected static String getSelfLinkFor(UriInfo uriInfo, Topic topic) {
    return uriInfo.getBaseUri() + "editor/topic/" + topic.getTopicMap().getId() + "/" + topic.getId();
  }

  protected static String getSelfLinkFor(UriInfo uriInfo, Topic topic, FieldsView fieldsView) {
    return uriInfo.getBaseUri() + "editor/topic/" + topic.getTopicMap().getId() + "/" + topic.getId() + "/" + fieldsView.getId();
  }

  public static Map<String,Object> createTopicInfo(UriInfo uriInfo, Topic topic, TopicType topicType, FieldsView fieldsView) {
    Map<String,Object> result = new LinkedHashMap<String,Object>();

    result.put("id", topic.getId());
    result.put("name", topic.getName());
    result.put("view", fieldsView.getId());

    List<Link> topicLinks = new ArrayList<Link>();
    topicLinks.add(new Link("self", getSelfLinkFor(uriInfo, topic, fieldsView)));    
    //    topicLinks.add(new Link("remove", "http://examples.org/topics/" + topic.getId() + "/remove"));
    //    topicLinks.add(new Link("batch-update", "http://examples.org/topics/" + topic.getId() + "/batch-update"));
    result.put("links", topicLinks);

    List<Map<String,Object>> fields = new ArrayList<Map<String,Object>>(); 

    for (FieldInstance fieldInstance : topic.getFieldInstances(topicType, fieldsView)) {
      Map<String,Object> field = new LinkedHashMap<String,Object>();

      FieldAssignment fieldAssignment = fieldInstance.getFieldAssignment();
      FieldDefinition fieldDefinition = fieldAssignment.getFieldDefinition();
      int fieldType = fieldDefinition.getFieldType();

      field.put("id", fieldDefinition.getId());
      field.put("name", fieldDefinition.getFieldName());

      List<Link> fieldLinks = new ArrayList<Link>();

      Cardinality cardinality = fieldDefinition.getCardinality();
      field.put("cardinality", cardinality.getLocator().getExternalForm());

      //fieldLinks.add(new Link("self", "http://examples.org/topics/" + fieldDefinition.getId()));    
      if (fieldType == FieldDefinition.FIELD_TYPE_ROLE) {
        RoleField roleField = (RoleField)fieldDefinition; 
        field.put("type", fieldDefinition.getLocator().getExternalForm());
        field.put("arity", roleField.getAssociationField().getArity());

        int arity = roleField.getAssociationField().getArity();
        if (arity == 2) {
          for (RoleField otherRoleField : roleField.getOtherRoleFields()) {

            InterfaceControl interfaceControl = otherRoleField.getInterfaceControl();
            field.put("interfaceControl", interfaceControl.getLocator().getExternalForm());
            if (interfaceControl.isDropDownList()) {
              TopicMap topicMap = topic.getTopicMap();
              fieldLinks.add(new Link("list", uriInfo.getBaseUri() + "editor/available-field-values/" + topicMap.getId() + "/" + topic.getId() + "/" + fieldDefinition.getId()));
              fieldLinks.add(new Link("add-values", uriInfo.getBaseUri() + "editor/add-field-values/" + topicMap.getId() + "/" + topic.getId() + "/" + fieldDefinition.getId()));
              fieldLinks.add(new Link("remove-values", uriInfo.getBaseUri() + "editor/remove-field-values/" + topicMap.getId() + "/" + topic.getId() + "/" + fieldDefinition.getId()));
            }
            field.put("links", fieldLinks);
          }
        }
      } else if (fieldType == FieldDefinition.FIELD_TYPE_OCCURRENCE) {
        OccurrenceField occurrenceField = (OccurrenceField)fieldDefinition;
        field.put("type", fieldDefinition.getLocator().getExternalForm());
        DataType dataType = occurrenceField.getDataType();
        LocatorIF locator = dataType.getLocator();
        field.put("datatype", locator.getExternalForm());
        field.put("height", occurrenceField.getHeight());
        field.put("width", occurrenceField.getWidth());
      } else if (fieldType == FieldDefinition.FIELD_TYPE_NAME) {
        //        NameField nameField = (NameField)fieldDefinition;
        field.put("type", fieldDefinition.getLocator().getExternalForm());
        field.put("datatype", PSI.XSD_STRING);
        field.put("links", fieldLinks);
      } else if (fieldType == FieldDefinition.FIELD_TYPE_IDENTITY) {
        //        IdentityField identityField = (IdentityField)fieldDefinition;
        field.put("type", fieldDefinition.getLocator().getExternalForm());
        field.put("datatype", PSI.XSD_URI);
        field.put("links", fieldLinks);
      } else if (fieldType == FieldDefinition.FIELD_TYPE_QUERY) {
        //        QueryField queryField = (QueryField)fieldDefinition;
        field.put("type", fieldDefinition.getLocator().getExternalForm());
        field.put("links", fieldLinks);
      }

      Collection<? extends Object> fieldValues = fieldInstance.getValues();
      field.put("values", getValues(uriInfo, topic, topicType, fieldsView, fieldDefinition, fieldValues));
      fields.add(field);
    }
    result.put("fields", fields);

    result.put("views", getViews(uriInfo, topic, topicType, fieldsView));

    System.out.println("M " + result);
    return result;
  }

  public static List<Map<String, Object>> getViews(UriInfo uriInfo,
      Topic topic, TopicType topicType, FieldsView fieldsView) {

    List<FieldsView> fieldViews = topic.getFieldViews(topicType, fieldsView);
    System.out.println("V " + fieldViews);

    List<Map<String,Object>> views = new ArrayList<Map<String,Object>>(fieldViews.size()); 
    for (FieldsView _fieldsView : fieldViews) {
      Map<String,Object> view = new LinkedHashMap<String,Object>();
      view.put("id", _fieldsView.getId());
      view.put("name", _fieldsView.getName());

      List<Link> links = new ArrayList<Link>();
      links.add(new Link("edit-in-view", getSelfLinkFor(uriInfo, topic, _fieldsView)));    
      view.put("links", links);
      views.add(view);
    }
    return views;
  }

  protected static List<Object> getValues(UriInfo uriInfo, Topic topic, TopicType topicType, FieldsView fieldsView, FieldDefinition fieldDefinition, Collection<? extends Object> fieldValues) {
    List<Object> result = new ArrayList<Object>(fieldValues.size());
    if (fieldDefinition.getFieldType() == FieldDefinition.FIELD_TYPE_ROLE && 
        ((RoleField)fieldDefinition).getAssociationField().getArity() == 1) {
      result.add(!fieldValues.isEmpty());
    } else {
      for (Object value : fieldValues) {
        result.add(getValue(uriInfo, topic, topicType, fieldsView, fieldDefinition, value));
      }
    }
    return result;
  }

  protected static Object getValue(UriInfo uriInfo, Topic topic, TopicType topicType, FieldsView fieldsView, FieldDefinition fieldDefinition, Object fieldValue) {
    switch (fieldDefinition.getFieldType()) {
    case FieldDefinition.FIELD_TYPE_NAME:
      return getNameValue((TopicNameIF)fieldValue);
    case FieldDefinition.FIELD_TYPE_IDENTITY: 
      return getIdentityValue((LocatorIF)fieldValue);
    case FieldDefinition.FIELD_TYPE_OCCURRENCE:
      return getOccurrenceValue((OccurrenceIF)fieldValue);
    case FieldDefinition.FIELD_TYPE_ROLE:
      RoleField roleField = (RoleField)fieldDefinition;
      RoleField.ValueIF value = (RoleField.ValueIF)fieldValue;
      int arity = value.getArity(); 
      if (arity == 2) {
        for (RoleField rf : value.getRoleFields()) {
          if (!rf.equals(roleField)) {
            Topic valueTopic = value.getPlayer(rf, topic);
            FieldsView valueView = fieldDefinition.getValueView(fieldsView);
            if (fieldDefinition.isEmbedded(valueView)) {
              TopicType valueType = OntopolyUtils.getDefaultTopicType(valueTopic);
              return createTopicInfo(uriInfo, valueTopic, valueType, valueView);
            } else {
              return getExistingTopicValue(uriInfo, topic, fieldDefinition, valueTopic);
            }
          }
        }
      } else if (arity > 2) {
        List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
        for (RoleField rf : value.getRoleFields()) {
          if (!rf.equals(roleField)) {
            Topic valueTopic = value.getPlayer(rf, topic);
            FieldsView valueView = fieldDefinition.getValueView(fieldsView);
            Map<String,Object> roleValue = new LinkedHashMap<String,Object>();
            roleValue.put("id", rf.getId());
            if (fieldDefinition.isEmbedded(valueView)) {
              TopicType valueType = OntopolyUtils.getDefaultTopicType(valueTopic);
              roleValue.put("value", createTopicInfo(uriInfo, valueTopic, valueType, valueView));
            } else {
              roleValue.put("value", getExistingTopicValue(uriInfo, topic, fieldDefinition, valueTopic));
            }
            result.add(roleValue);
          }
        }
        return result;
      }
      return null;
    case FieldDefinition.FIELD_TYPE_QUERY: 
      if (fieldValue instanceof Topic) {
        return getExistingTopicValue(uriInfo, topic, fieldDefinition, (Topic)fieldValue);
      } else {
        return fieldValue;
      }
    default:
      throw new RuntimeException("Unknown field definition type: " + fieldDefinition);
    }
  }

  public static Map<String,Object> getNameValue(TopicNameIF topicName) {
    Map<String,Object> result = new LinkedHashMap<String, Object>();
    result.put("value", topicName.getValue());
    return result;
  }

  public static Map<String,Object> getIdentityValue(LocatorIF identity) {
    Map<String,Object> result = new LinkedHashMap<String, Object>();
    result.put("value", identity.getExternalForm());
    return result;
  }

  public static Map<String,Object> getOccurrenceValue(OccurrenceIF occurrence) {
    Map<String,Object> result = new LinkedHashMap<String, Object>();
    result.put("value", occurrence.getValue());
    return result;
  }

  public static Object getExistingTopicValues(UriInfo uriInfo, Topic parentTopic, FieldDefinition fieldDefinition, Collection<Topic> values) {
    List<Object> result = new ArrayList<Object>(values.size());
    for (Topic value : values) {
      result.add(getExistingTopicValue(uriInfo, parentTopic, fieldDefinition, value));
    }
    return result;
  }

  public static Object getExistingTopicValue(UriInfo uriInfo, Topic parentTopic, FieldDefinition fieldDefinition, Topic value) {
    Map<String, Object> result = new LinkedHashMap<String,Object>();
    result.put("id", value.getId());
    result.put("name", value.getName());

    List<Link> links = new ArrayList<Link>();
    links.add(new Link("edit", getSelfLinkFor(uriInfo, value)));    
    //    links.add(new Link("edit", "http://examples.org/topics/" + value.getId()));    
    //    //links.add(new Link("add", "http://examples.org/topics/" + value.getId() + "/" + fieldDefinition.getId() + "/add/" + value.getId()));
    //    links.add(new Link("remove", "http://examples.org/topics/" + value.getId() + "/" + fieldDefinition.getId() + "/remove/" + value.getId()));
    result.put("links", links);

    return result;
  }

  public static Map<String, Object> updateTopic(UriInfo uriInfo, Topic topic,
      TopicType topicType, FieldsView fieldsView, JSONObject data) {

    Map<String,FieldInstance> fields = new HashMap<String,FieldInstance>();
    for (FieldInstance fieldInstance : topic.getFieldInstances(topicType, fieldsView)) {
      FieldDefinition fieldDefinition = fieldInstance.getFieldAssignment().getFieldDefinition();
      fields.put(fieldDefinition.getId(), fieldInstance);
    }

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
          System.out.println("jsonA: " + fieldDefinition + " " + fieldObject);        
          if  (fields.containsKey(fieldId)) {

            RoleFields roleFields = null;
            if (fieldType == FieldDefinition.FIELD_TYPE_ROLE) {
              RoleField roleField = (RoleField)fieldDefinition;
              AssociationField associationField = roleField.getAssociationField();
              
              if (associationField.getArity() > 2)
                continue; // NOTE: only unary and binary associations for now
              
              roleFields = new RoleFields(associationField);
            }
            
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
              JSONObject valueObject = values.getJSONObject(vc);
              System.out.println("jsonV: " + valueObject);

              if (fieldType == FieldDefinition.FIELD_TYPE_ROLE) {
                RoleField roleField = (RoleField)fieldDefinition;
                
                int arity = roleFields.getArity();
                RoleField.ValueIF value = RoleField.createValue(arity);
                value.addPlayer(roleField, topic); 
                if (arity == 2) {
                  String id = valueObject.getString("id");
                  Topic player = topic.getTopicMap().getTopicById(id);
                  value.addPlayer(roleField.getOtherRoleFields().iterator().next(), player);
                }
                RoleData roleData = roleFields.createData(value.getRoleFields(), value.getPlayers());
                System.out.println("E: " + roleData + " : " + existingValues);

                removableValues.remove(roleData);
                if (!existingValues.contains(roleData)) {
                  addableValues.add(roleData);
                }

              } else {
                String value = valueObject.getString("value");              
  
                System.out.println("E: " + value + " : " + existingValues);
                removableValues.remove(value);
                if (!existingValues.contains(value)) {
                  addableValues.add(value);
                }
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
    return Utils.createTopicInfo(uriInfo, topic, topicType, fieldsView);
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
