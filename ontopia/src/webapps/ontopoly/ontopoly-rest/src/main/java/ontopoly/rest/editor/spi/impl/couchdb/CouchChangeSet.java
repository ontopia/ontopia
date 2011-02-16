package ontopoly.rest.editor.spi.impl.couchdb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import ontopoly.rest.editor.spi.PrestoChangeSet;
import ontopoly.rest.editor.spi.PrestoField;
import ontopoly.rest.editor.spi.PrestoFieldUsage;
import ontopoly.rest.editor.spi.PrestoTopic;
import ontopoly.rest.editor.spi.PrestoType;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.ektorp.CouchDbConnector;

public class CouchChangeSet implements PrestoChangeSet {

  private final CouchDataProvider dataProvider;

  private CouchTopic topic;
  private final PrestoType type;

  private final List<Change> changes = new ArrayList<Change>();
  private boolean saved;


  CouchChangeSet(CouchDataProvider dataProvider, CouchTopic topic) {
    this.dataProvider = dataProvider;
    this.topic = topic;
    this.type = null;
  }

  CouchChangeSet(CouchDataProvider dataProvider, PrestoType type) {
    this.dataProvider = dataProvider;
    this.topic = null;
    this.type = type;
  }

  public void setValues(PrestoFieldUsage field, Collection<Object> values) {
    if (saved) {
      throw new RuntimeException("Can only save a changeset once.");
    }
    changes.add(new Change(Change.ChangeType.SET, field, values));
  }

  public void addValues(PrestoFieldUsage field, Collection<Object> values) {
    if (saved) {
      throw new RuntimeException("Can only save a changeset once.");
    }
    changes.add(new Change(Change.ChangeType.ADD, field, values));
  }

  public void removeValues(PrestoFieldUsage field, Collection<Object> values) {
    if (saved) {
      throw new RuntimeException("Can only save a changeset once.");
    }
    changes.add(new Change(Change.ChangeType.REMOVE, field, values));
  }

  @SuppressWarnings("unchecked")
  public PrestoTopic save() {
    this.saved = true;

    boolean isNew = false;
    if (topic == null) {
      if (type != null) {
        topic = CouchTopic.newInstance(dataProvider, type);
        isNew = true;
      } else {
        throw new RuntimeException("No topic and no type. I'm sorry, Dave. I'm afraid I can't do that.");
      }
    }

    CouchDbConnector couchConnector = dataProvider.getCouchConnector();

    ObjectNode data = topic.getData();

    List<Change> inverseChanges = new ArrayList<Change>();
    
    for (Change change : changes) {
      PrestoFieldUsage field = change.getField();
      Collection<Object> values = change.getValues();
      switch(change.getType()) {
      case SET: {
        Collection<Object> existingValues = topic.getValues(field);
        Collection<Object> removableValues = new HashSet<Object>(existingValues);          
        Collection<Object> addableValues = new HashSet<Object>();

        for (Object value : values) {
          removableValues.remove(value);
          if (!existingValues.contains(value)) {
            addableValues.add(value);
          }
        }
        System.out.println("-----e> " + existingValues);
        System.out.println("-----a> " + addableValues);
        System.out.println("-----r> " + removableValues);
        
        if (!addableValues.isEmpty()) {
          inverseChanges.add(new Change(Change.ChangeType.ADD, field, addableValues));
        }
        if (!removableValues.isEmpty()) {
          inverseChanges.add(new Change(Change.ChangeType.REMOVE, field, removableValues));
        }
        
        setValue(data, field, values);        
        break;
      }
      case ADD: {
        addValue(data, field, values);
        inverseChanges.add(change);
        break;
      }
      case REMOVE: {
        removeValue(data, field, values);
        inverseChanges.add(change);
        break;
      }
      }
      System.out.println("F: " + field.getId());
      if (field.isNameField()) {
        String name = values.isEmpty() ? "No name" : values.iterator().next().toString();
        data.put(":name", name);
      }
    }
    if (isNew) {
      couchConnector.create(data);
      System.out.println("C: " + data.get("_id").toString() + " " + data.get("_rev").toString());
    } else {
      couchConnector.update(data);      
      System.out.println("U: " + data.get("_id").toString() + " " + data.get("_rev").toString());
    }

    // update inverse fields
    for (Change change : inverseChanges) {
      PrestoFieldUsage field = change.getField();
      Collection<Object> values = change.getValues();
      System.out.println("Ch: " + field.getId() + " " + change.getType() + " " +  values);
      switch(change.getType()) {
      case SET: {
        throw new RuntimeException("Should not get one of these.");
      }
      case ADD: {
        String inverseFieldId = field.getInverseFieldId();
        if (inverseFieldId != null) {
          for (Object value : values) {
            CouchTopic valueTopic = (CouchTopic)value;
            PrestoType type = field.getSchemaProvider().getTypeById(valueTopic.getTypeId());
            PrestoField inverseField = type.getFieldById(inverseFieldId);
            System.out.println("IF1: " + field.getId() + " " + inverseFieldId + " " + inverseField + " " + valueTopic.getData());
            ObjectNode valueData = valueTopic.getData(); 
            addValue(valueData, inverseField, Collections.singleton((Object)topic));
            System.out.println("IF2: " + field.getId() + " " + inverseFieldId + " " + inverseField + " " + valueTopic.getData());
            dataProvider.getCouchConnector().update(valueData);      
            System.out.println("U2: " + valueData.get("_id").toString() + " " + valueData.get("_rev").toString());
          }
        }
        break;
      }
      case REMOVE: {
        if (!isNew) {
          String inverseFieldId = field.getInverseFieldId();
          if (inverseFieldId != null) {
            for (Object value : values) {
              CouchTopic valueTopic = (CouchTopic)value;
              PrestoType type = field.getSchemaProvider().getTypeById(valueTopic.getTypeId());
              PrestoField inverseField = type.getFieldById(inverseFieldId);
              System.out.println("IF1: " + field.getId() + " " + inverseFieldId + " " + inverseField + " " + valueTopic.getData());
              ObjectNode valueData = valueTopic.getData(); 
              removeValue(valueData, inverseField, Collections.singleton((Object)topic));
              System.out.println("IF2: " + field.getId() + " " + inverseFieldId + " " + inverseField + " " + valueTopic.getData());
              dataProvider.getCouchConnector().update(valueData);      
              System.out.println("U2: " + valueData.get("_id").toString() + " " + valueData.get("_rev").toString());
            }
          }
        }
        break;
      }
      }
    }

    return topic;
  }

  private void setValue(ObjectNode data, PrestoField field, Collection<Object> values) {
    if (!values.isEmpty()) {
      ArrayNode arrayNode = dataProvider.getObjectMapper().createArrayNode();
      for (Object value : values) {
        if (value instanceof CouchTopic) {
          CouchTopic valueTopic = (CouchTopic)value;
          arrayNode.add(valueTopic.getId());
        } else {
          arrayNode.add((String)value);
        }
      }
      data.put(field.getId(), arrayNode);
    }
  }

  private void addValue(ObjectNode data, PrestoField field, Collection<Object> values) {
    if (!values.isEmpty()) {
      Collection<String> result = new HashSet<String>();
      JsonNode jsonNode = data.get(field.getId());
      if (jsonNode != null && jsonNode.isArray()) {
          for (JsonNode existing : jsonNode) {
              result.add(existing.getTextValue());
          }
      }
      for (Object value : values) {
        if (value instanceof CouchTopic) {
          CouchTopic valueTopic = (CouchTopic)value;          
          result.add(valueTopic.getId());
        } else {
          result.add((String)value);
        }
      }
      ArrayNode arrayNode = dataProvider.getObjectMapper().createArrayNode();
      for (String value : result) {
          arrayNode.add(value);
      }
      
      System.out.println("A: " + arrayNode);
      data.put(field.getId(), arrayNode);
    }
  }

  private void removeValue(ObjectNode data, PrestoField field, Collection<Object> values) {
    if (!values.isEmpty()) {
      JsonNode jsonNode = data.get(field.getId());
      System.out.println("R: " + field.getId() + " " + values);
      if (jsonNode != null && jsonNode.isArray()) {
        Collection<String> existing = new HashSet<String>(jsonNode.size());
        for (JsonNode item : jsonNode) {
          existing.add(item.getValueAsText());
        }
        for (Object value : values) {
          if (value instanceof CouchTopic) {
            CouchTopic valueTopic = (CouchTopic)value;
            existing.remove(valueTopic.getId());
          } else {
            existing.remove((String)value);
          }
        }
        ArrayNode arrayNode  = dataProvider.getObjectMapper().createArrayNode();
        for (String value : existing) {
          arrayNode.add(value);
        }
        data.put(field.getId(), arrayNode);
      }
    }
  }

  private static class Change {

    static enum ChangeType { SET, ADD, REMOVE };

    private ChangeType type;
    private final PrestoFieldUsage field;
    private final Collection<Object> values;

    Change(ChangeType type, PrestoFieldUsage field, Collection<Object> values) {
      this.type = type;
      this.field = field;
      this.values = values;      
    }

    public ChangeType getType() {
      return type;
    }

    public PrestoFieldUsage getField() {
      return field;
    }
    public Collection<Object> getValues() {
      return values;
    }
  }

}
