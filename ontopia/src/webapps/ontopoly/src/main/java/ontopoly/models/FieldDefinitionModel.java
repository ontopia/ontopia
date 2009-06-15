package ontopoly.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.FieldDefinition;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.IdentityField;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.NameField;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.OccurrenceField;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.RoleField;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.TopicMap;
import net.ontopia.utils.ObjectUtils;
import ontopoly.utils.OntopolyContext;

import org.apache.wicket.model.LoadableDetachableModel;

public class FieldDefinitionModel extends LoadableDetachableModel {

  private String topicMapId;
  
  private int fieldType;
  private String fieldId;
  
  public FieldDefinitionModel(FieldDefinition fieldDefinition) {
    super(fieldDefinition);
    if (fieldDefinition == null)
      throw new NullPointerException("fieldDefinition parameter cannot be null.");
    
    TopicMap topicMap = fieldDefinition.getTopicMap();
    this.topicMapId = topicMap.getId();
    this.fieldType = fieldDefinition.getFieldType();     
    this.fieldId = fieldDefinition.getId();
  }
  
  public FieldDefinition getFieldDefinition() {
    return (FieldDefinition)getObject();
  }

  @Override
  protected Object load() {
    TopicMap tm = OntopolyContext.getTopicMap(topicMapId);
 
    TopicIF fieldTopic = tm.getTopicIFById(fieldId);
      
    FieldDefinition fieldDefinition;
    switch (fieldType) {
    case FieldDefinition.FIELD_TYPE_ROLE:
      fieldDefinition = new RoleField(fieldTopic, tm);
      break;
    case FieldDefinition.FIELD_TYPE_OCCURRENCE:
      fieldDefinition = new OccurrenceField(fieldTopic, tm);
      break;
    case FieldDefinition.FIELD_TYPE_NAME:
      fieldDefinition = new NameField(fieldTopic, tm);
      break;
    case FieldDefinition.FIELD_TYPE_IDENTITY:
      fieldDefinition = new IdentityField(fieldTopic, tm);
      break;
    default:
      throw new RuntimeException("Unknown field type: " + fieldType);
    }
    return fieldDefinition;
  }

  public static List wrapInFieldDefinitionModels(List fieldDefinitions) {
    List result = new ArrayList(fieldDefinitions.size());
    Iterator iter = fieldDefinitions.iterator();
    while (iter.hasNext()) {
      FieldDefinition fieldDefinition = (FieldDefinition)iter.next();
      result.add(new FieldDefinitionModel(fieldDefinition));
    }
    return result;
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof FieldDefinitionModel))
      return false;
    
    FieldDefinitionModel fam = (FieldDefinitionModel)other;
    return ObjectUtils.equals(getFieldDefinition(), fam.getFieldDefinition());
  }
  @Override
  public int hashCode() {
    return getFieldDefinition().hashCode();
  }

}
