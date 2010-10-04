package ontopoly.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.utils.ObjectUtils;
import ontopoly.OntopolyContext;
import ontopoly.model.FieldDefinitionIF;
import ontopoly.model.IdentityFieldIF;
import ontopoly.model.NameFieldIF;
import ontopoly.model.OccurrenceFieldIF;
import ontopoly.model.RoleFieldIF;
import ontopoly.model.OntopolyTopicMapIF;

import org.apache.wicket.model.LoadableDetachableModel;

public class FieldDefinitionModel extends LoadableDetachableModel<FieldDefinitionIF> {
  private String topicMapId; 
  private int fieldType;
  private String fieldId;
  
  public FieldDefinitionModel(FieldDefinitionIF fieldDefinition) {
    super(fieldDefinition);
    if (fieldDefinition == null)
      throw new NullPointerException("fieldDefinition parameter cannot be null.");
    
    OntopolyTopicMapIF topicMap = fieldDefinition.getTopicMap();
    this.topicMapId = topicMap.getId();
    this.fieldType = fieldDefinition.getFieldType();     
    this.fieldId = fieldDefinition.getId();
  }
  
  public FieldDefinitionIF getFieldDefinition() {
    return getObject();
  }

  @Override
  protected FieldDefinitionIF load() {
    OntopolyTopicMapIF tm = OntopolyContext.getTopicMap(topicMapId);
    return tm.findFieldDefinition(fieldId, fieldType);
  }

  public static List<FieldDefinitionModel> wrapInFieldDefinitionModels(List<FieldDefinitionIF> fieldDefinitions) {
    List<FieldDefinitionModel> result = new ArrayList<FieldDefinitionModel>(fieldDefinitions.size());
    Iterator<FieldDefinitionIF> iter = fieldDefinitions.iterator();
    while (iter.hasNext()) {
      FieldDefinitionIF fieldDefinition = iter.next();
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
