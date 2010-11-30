package ontopoly.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import ontopoly.model.Cardinality;
import ontopoly.model.FieldAssignment;
import ontopoly.model.FieldDefinition;
import ontopoly.model.FieldInstance;
import ontopoly.model.FieldsView;
import ontopoly.model.InterfaceControl;
import ontopoly.model.RoleField;
import ontopoly.model.Topic;
import ontopoly.model.TopicType;
import ontopoly.utils.OntopolyUtils;

public class DuperUtils {

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
  public static Map<String,Object> createFieldConfigMap(Topic topic, TopicType topicType, FieldsView fieldsView) {
    Map<String,Object> result = new LinkedHashMap<String,Object>();

    result.put("id", topic.getId());
    result.put("name", topic.getName());

    List<Link> topicLinks = new ArrayList<Link>();
    topicLinks.add(new Link("self", "http://examples.org/topics/" + topic.getId()));    
    // TODO: if removeable
    topicLinks.add(new Link("remove", "http://examples.org/topics/" + topic.getId() + "/remove"));
    result.put("links", topicLinks);

    List<Map<String,Object>> fields = new ArrayList<Map<String,Object>>(); 

    for (FieldInstance fieldInstance : topic.getFieldInstances(topicType)) {
      Map<String,Object> field = new LinkedHashMap<String,Object>();

      FieldAssignment fieldAssignment = fieldInstance.getFieldAssignment();
      FieldDefinition fieldDefinition = fieldAssignment.getFieldDefinition();
      int fieldType = fieldDefinition.getFieldType();
      
      field.put("id", fieldDefinition.getId());
      field.put("name", fieldDefinition.getFieldName());

//      TopicType fieldTopicType = OntopolyUtils.getDefaultTopicType(fieldDefinition);
//      FieldsView fieldFieldsView = FieldsView.getDefaultFieldsView(fieldDefinition.getTopicMap());      
//      field.put("config", Utils.createFieldConfigMap(fieldDefinition, fieldTopicType, fieldFieldsView));

      List<Link> fieldLinks = new ArrayList<Link>();
      //fieldLinks.add(new Link("self", "http://examples.org/topics/" + fieldDefinition.getId()));    
      if (fieldType == FieldDefinition.FIELD_TYPE_ROLE && ((RoleField)fieldDefinition).getInterfaceControl().isDropDownList()) { 
        fieldLinks.add(new Link("list", "http://examples.org/topics/" + topic.getId() + "/" + fieldDefinition.getId() + "/list"));
      }
      field.put("links", fieldLinks);

      Collection<? extends Object> fieldValues = fieldInstance.getValues();
      List<Object> values = new ArrayList<Object>(fieldValues.size());
      if (fieldType == FieldDefinition.FIELD_TYPE_ROLE && 
          ((RoleField)fieldDefinition).getAssociationField().getArity() == 1) {
        values.add(!fieldValues.isEmpty());
      } else {
        for (Object value : fieldValues) {
          values.add(getValue(topic, topicType, fieldsView, fieldDefinition, value));
        }
      }
      field.put("values", values);
      fields.add(field);
    }
    result.put("fields", fields);

    System.out.println("M " + result);
    return result;
  }

  protected static Object getValue(Topic topic, TopicType topicType, FieldsView fieldsView, FieldDefinition fieldDefinition, Object fieldValue) {
    switch (fieldDefinition.getFieldType()) {
    case FieldDefinition.FIELD_TYPE_NAME:
      return ((TopicNameIF)fieldValue).getValue();
    case FieldDefinition.FIELD_TYPE_IDENTITY: 
      return ((LocatorIF)fieldValue).getExternalForm();
    case FieldDefinition.FIELD_TYPE_OCCURRENCE:
      return ((OccurrenceIF)fieldValue).getValue();
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
              return createFieldConfigMap(valueTopic, valueType, valueView);
            } else {
              return getExistingTopicValue(topic, fieldDefinition, valueTopic);
            }
          }
        }
      }
      return null;
    case FieldDefinition.FIELD_TYPE_QUERY: 
      return getExistingTopicValue(topic, fieldDefinition, (Topic)fieldValue);
    default:
      throw new RuntimeException("Unknown field definition type: " + fieldDefinition);
    }
  }

  protected static Object getExistingTopicValue(Topic parentTopic, FieldDefinition fieldDefinition, Topic value) {
    Map<String, Object> result = new LinkedHashMap<String,Object>();
    result.put("id", value.getId());
    result.put("name", value.getName());

    List<Link> links = new ArrayList<Link>();
    links.add(new Link("edit", "http://examples.org/topics/" + value.getId()));    
    //links.add(new Link("add", "http://examples.org/topics/" + value.getId() + "/" + fieldDefinition.getId() + "/add/" + value.getId()));
    links.add(new Link("remove", "http://examples.org/topics/" + value.getId() + "/" + fieldDefinition.getId() + "/remove/" + value.getId()));
    result.put("links", links);

    return result;
  }

  public static FieldConfig createdFieldConfig(FieldDefinition fieldDefinition, FieldsView fieldsView) {
    FieldConfig field = new FieldConfig(fieldDefinition.getId(), fieldDefinition.getName());

    Cardinality cardinality = fieldDefinition.getCardinality();
    if (cardinality.isZeroOrOne())
      field.setCardinality("0:1");
    else if (cardinality.isExactlyOne())
      field.setCardinality("1:1");
    else if (cardinality.isZeroOrMore())
      field.setCardinality("0:M");
    else if (cardinality.isOneOrMore())
      field.setCardinality("1:M");

    switch (fieldDefinition.getFieldType()) {
    case FieldDefinition.FIELD_TYPE_NAME: 
      field.setType("name");
      break;
    case FieldDefinition.FIELD_TYPE_IDENTITY: 
      field.setType("identity");
      break;
    case FieldDefinition.FIELD_TYPE_OCCURRENCE: 
      field.setType("occurrence");
      break;
    case FieldDefinition.FIELD_TYPE_ROLE: 
      field.setType("role");
      if (fieldDefinition.isEmbedded(fieldsView)) {
        FieldsView embeddedView = fieldDefinition.getValueView(fieldsView);
        field.setEmbeddedView(embeddedView.getId());
      }
      RoleField roleField = (RoleField)fieldDefinition;
      InterfaceControl interfaceControl = roleField.getInterfaceControl();
      field.setInterfaceControl(interfaceControl.getName());
      break;
    case FieldDefinition.FIELD_TYPE_QUERY: 
      field.setType("query");
      break;
    default:
      throw new RuntimeException("Unknown field definition type: " + fieldDefinition);
    };

    return field;
  }

}
