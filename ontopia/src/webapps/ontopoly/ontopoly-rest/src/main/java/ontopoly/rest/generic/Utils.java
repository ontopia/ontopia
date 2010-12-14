package ontopoly.rest.generic;

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
import ontopoly.model.ViewModes;
import ontopoly.utils.OntopolyUtils;

public class Utils {
  
  public static Map<String,Object> createFieldConfigMap(Topic topic, TopicType topicType, FieldsView fieldsView) {
    Map<String,Object> result = new LinkedHashMap<String,Object>();
    
    for (FieldInstance fieldInstance : topic.getFieldInstances(topicType)) {
      FieldAssignment fieldAssignment = fieldInstance.getFieldAssignment();
      FieldDefinition fieldDefinition = fieldAssignment.getFieldDefinition();
      String key = getTopicKey(fieldDefinition);
      
      Collection<? extends Object> fieldValues = fieldInstance.getValues();
      List<Object> values = new ArrayList<Object>(fieldValues.size());
      if (fieldDefinition.getFieldType() == FieldDefinition.FIELD_TYPE_ROLE && 
          ((RoleField)fieldDefinition).getAssociationField().getArity() == 1) {
        System.out.println("R1: " + fieldDefinition.getName() + " " + fieldValues);
        values.add(!fieldValues.isEmpty());
      } else {
        for (Object value : fieldValues) {
          values.add(getValue(topic, topicType, fieldsView, fieldDefinition, value));
        }
      }
      result.put(key, values);
    }    
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
        FieldsView valueView = fieldDefinition.getValueView(fieldsView);
        ViewModes viewModes = fieldDefinition.getViewModes(valueView);
        for (RoleField rf : value.getRoleFields()) {
          if (!rf.equals(roleField)) {
            Topic valueTopic = value.getPlayer(rf, topic);
            if (viewModes.isEmbedded()) {
              TopicType valueType = OntopolyUtils.getDefaultTopicType(valueTopic);
              return createFieldConfigMap(valueTopic, valueType, valueView);
            } else {
              return getTopicValue(valueTopic);
            }
          }
        }
      }
      return null;
    case FieldDefinition.FIELD_TYPE_QUERY: 
      return getTopicValue((Topic)fieldValue);
    default:
      throw new RuntimeException("Unknown field definition type: " + fieldDefinition);
    }
  }

  protected static String getTopicKey(Topic topic) {
    for (LocatorIF loc : topic.getTopicIF().getSubjectIdentifiers()) {
      if (loc.getAddress().startsWith("jsonid:")) {
        System.out.println("X: " + loc.getAddress());
        System.out.println("Y: " + loc.getAddress().substring("jsonid:".length()));
        return loc.getAddress().substring("jsonid:".length());
      }
    }
    String name = topic.getName();
    return name == null ? topic.getId() : name;
  }
  
  protected static Object getTopicValue(Topic topic) {
    for (LocatorIF loc : topic.getTopicIF().getSubjectIdentifiers()) {
      if (loc.getAddress().startsWith("jsonid:")) {
        System.out.println("X: " + loc.getAddress());
        System.out.println("Y: " + loc.getAddress().substring("jsonid:".length()));
        return loc.getAddress().substring("jsonid:".length());
      }
    }
    Map<String, Object> result = new LinkedHashMap<String,Object>();
    result.put("id", topic.getId());
    result.put("name", topic.getName());
    return result;
  }
 
  public static FieldConfig createFieldConfig(FieldDefinition fieldDefinition, FieldsView fieldsView) {
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
      FieldsView childView = fieldDefinition.getValueView(fieldsView);
      ViewModes viewModes = fieldDefinition.getViewModes(childView);
      if (viewModes.isEmbedded()) {
        field.setEmbeddedView(childView.getId());
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
