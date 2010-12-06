package ontopoly.rest.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.utils.PSI;
import ontopoly.model.Cardinality;
import ontopoly.model.DataType;
import ontopoly.model.FieldAssignment;
import ontopoly.model.FieldDefinition;
import ontopoly.model.FieldInstance;
import ontopoly.model.FieldsView;
import ontopoly.model.IdentityField;
import ontopoly.model.InterfaceControl;
import ontopoly.model.NameField;
import ontopoly.model.OccurrenceField;
import ontopoly.model.QueryField;
import ontopoly.model.RoleField;
import ontopoly.model.Topic;
import ontopoly.model.TopicType;
import ontopoly.utils.OntopolyUtils;

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
    return uriInfo.getBaseUri() + "editor/topic/" + topic.getTopicMap().getId() + ":" + topic.getId();
  }
  
  protected static String getSelfLinkFor(UriInfo uriInfo, Topic topic, FieldsView fieldsView) {
    return uriInfo.getBaseUri() + "editor/topic/" + topic.getTopicMap().getId() + ":" + topic.getId() + ":" + fieldsView.getId();
  }

  public static Map<String,Object> createTopicInfo(UriInfo uriInfo, Topic topic, TopicType topicType, FieldsView fieldsView) {
    Map<String,Object> result = new LinkedHashMap<String,Object>();

    result.put("id", topic.getId());
    result.put("name", topic.getName());

//    List<Link> topicLinks = new ArrayList<Link>();
//    topicLinks.add(new Link("self", getSelfLinkFor(uriInfo, topic, fieldsView)));    
////    topicLinks.add(new Link("remove", "http://examples.org/topics/" + topic.getId() + "/remove"));
////    topicLinks.add(new Link("batch-update", "http://examples.org/topics/" + topic.getId() + "/batch-update"));
//    result.put("links", topicLinks);

    List<Map<String,Object>> fields = new ArrayList<Map<String,Object>>(); 

    for (FieldInstance fieldInstance : topic.getFieldInstances(topicType)) {
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
              fieldLinks.add(new Link("list", "http://localhost:8080/ontopoly/rest/editor/topic/" + topic.getTopicMap().getId() + ":" + topic.getId() + ":" + fieldDefinition.getId() + "/list"));
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

    List<FieldsView> fieldViews = topic.getFieldViews(topicType, fieldsView);
    System.out.println("V " + fieldViews);

    List<Map<String,Object>> views = new ArrayList<Map<String,Object>>(fieldViews.size()); 
    for (FieldsView _fieldsView : fieldViews) {
      Map<String,Object> view = new LinkedHashMap<String,Object>();
      view.put("name", _fieldsView.getName());
      
      List<Link> links = new ArrayList<Link>();
      links.add(new Link("edit-in-view", getSelfLinkFor(uriInfo, topic, _fieldsView)));    
      view.put("links", links);
      views.add(view);
    }
    result.put("views", views);

    System.out.println("M " + result);
    return result;
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

}
