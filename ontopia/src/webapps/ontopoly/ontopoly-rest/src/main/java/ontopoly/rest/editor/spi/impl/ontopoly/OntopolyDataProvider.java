package ontopoly.rest.editor.spi.impl.ontopoly;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import ontopoly.model.FieldDefinition;
import ontopoly.model.RoleField;
import ontopoly.model.Topic;
import ontopoly.rest.editor.spi.PrestoChangeSet;
import ontopoly.rest.editor.spi.PrestoDataProvider;
import ontopoly.rest.editor.spi.PrestoField;
import ontopoly.rest.editor.spi.PrestoTopic;
import ontopoly.rest.editor.spi.PrestoType;

public class OntopolyDataProvider implements PrestoDataProvider {

  OntopolySession session;
  
  public OntopolyDataProvider(OntopolySession session) {
    this.session = session;
  }

  public PrestoTopic getTopicById(String id) {
    Topic topic = session.getTopicMap().getTopicById(id);
    if (topic == null) {
      throw new RuntimeException("Unknown topic: " + id);
    }
    return new OntopolyTopic(session, topic);
  }

  public PrestoChangeSet createTopic(PrestoType type) {
    return new OntopolyChangeSet(session, type);
  }
  
  public PrestoChangeSet updateTopic(PrestoTopic topic) {
    return new OntopolyChangeSet(session, topic);
  }

  public boolean removeTopic(PrestoTopic topic) {
    OntopolyTopic.getWrapped(topic).remove(null);
    return true;
  }

  public void close() {
    // no-op
  }
  
  public Collection<Object> getValues(PrestoTopic topic, PrestoField field) {
    FieldDefinition fieldDefinition = FieldDefinition.getFieldDefinition(field.getId(), session.getTopicMap());
    Topic topic_ = OntopolyTopic.getWrapped(topic);
    
    Collection<? extends Object> fieldValues = fieldDefinition.getValues(topic_);
    List<Object> result = new ArrayList<Object>(fieldValues.size());
    if (fieldDefinition.getFieldType() == FieldDefinition.FIELD_TYPE_ROLE && 
        ((RoleField)fieldDefinition).getAssociationField().getArity() == 1) {
      result.add(!fieldValues.isEmpty());
    } else {
      for (Object value : fieldValues) {
        result.add(normalizeValue(topic_, fieldDefinition, value));
      }
    }
    return result;
  }

  private  Object normalizeValue(Topic topic, FieldDefinition fieldDefinition, Object fieldValue) {
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
            return new OntopolyTopic(session, valueTopic);
          }
        }
        return null;
      } else {
        throw new RuntimeException("N-ary role fields not supported.");
      }
    case FieldDefinition.FIELD_TYPE_QUERY: 
      if (fieldValue instanceof Topic) {        
        return new OntopolyTopic(session, (Topic)fieldValue);
      } else {
        return fieldValue;
      }
    default:
      throw new RuntimeException("Unknown field type: " + fieldDefinition);
    }
  }

}
