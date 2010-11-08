package ontopoly.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.utils.ObjectUtils;
import ontopoly.OntopolyContext;
import ontopoly.model.FieldDefinition;
import ontopoly.model.IdentityField;
import ontopoly.model.NameField;
import ontopoly.model.OccurrenceField;
import ontopoly.model.QueryField;
import ontopoly.model.RoleField;
import ontopoly.model.TopicMap;

import org.apache.wicket.model.LoadableDetachableModel;

public class FieldDefinitionModel extends LoadableDetachableModel<FieldDefinition> {

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
    return getObject();
  }

  @Override
  protected FieldDefinition load() {
    TopicMap tm = OntopolyContext.getTopicMap(topicMapId);
 
    TopicIF fieldTopic = tm.getTopicIFById(fieldId);
      
    return getFieldDefinition(fieldTopic, fieldType, tm);
  }

  static FieldDefinition getFieldDefinition(TopicIF fieldTopic, int fieldType, TopicMap tm) {
    switch (fieldType) {
    case FieldDefinition.FIELD_TYPE_ROLE:
      return new RoleField(fieldTopic, tm);
    case FieldDefinition.FIELD_TYPE_OCCURRENCE:
      return new OccurrenceField(fieldTopic, tm);
    case FieldDefinition.FIELD_TYPE_NAME:
      return new NameField(fieldTopic, tm);
    case FieldDefinition.FIELD_TYPE_IDENTITY:
      return new IdentityField(fieldTopic, tm);
    case FieldDefinition.FIELD_TYPE_QUERY:
      return new QueryField(fieldTopic, tm);
    default:
      throw new RuntimeException("Unknown field type: " + fieldType);
    }    
  }

  public static List<FieldDefinitionModel> wrapInFieldDefinitionModels(List<FieldDefinition> fieldDefinitions) {
    List<FieldDefinitionModel> result = new ArrayList<FieldDefinitionModel>(fieldDefinitions.size());
    Iterator<FieldDefinition> iter = fieldDefinitions.iterator();
    while (iter.hasNext()) {
      FieldDefinition fieldDefinition = iter.next();
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
