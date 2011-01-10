package ontopoly.rest.editor.spi.impl.couchdb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ontopoly.rest.editor.spi.PrestoChangeSet;
import ontopoly.rest.editor.spi.PrestoField;
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


  public void setValues(PrestoField field, Collection<Object> values) {
    if (saved) {
      throw new RuntimeException("Can only save a changeset once.");
    }
    changes.add(new Change(Change.ChangeType.SET, field, values));
  }

  public void addValues(PrestoField field, Collection<Object> values) {
    if (saved) {
      throw new RuntimeException("Can only save a changeset once.");
    }
    changes.add(new Change(Change.ChangeType.ADD, field, values));
  }

  public void removeValues(PrestoField field, Collection<Object> values) {
    if (saved) {
      throw new RuntimeException("Can only save a changeset once.");
    }
    changes.add(new Change(Change.ChangeType.REMOVE, field, values));
  }

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

    for (Change change : changes) {
      PrestoField field = change.getField();
      Collection<Object> values = change.getValues();
      switch(change.getType()) {
      case SET: {
        setValue(data, field, values);
        break;
      }
      case ADD: {
        addValue(data, field, values);
        break;
      }
      case REMOVE: {
        break;
      }
      }
    }
    if (isNew) {
      couchConnector.create(data);
      System.out.println("C: " + data.get("_id").toString() + " " + data.get("_rev").toString());
    } else {
      couchConnector.update(data);      
      System.out.println("U: " + data.get("_id").toString() + " " + data.get("_rev").toString());
    }
    return topic;
  }

  private void addValue(ObjectNode data, PrestoField field, Collection<Object> values) {
    JsonNode jsonNode = data.get(field.getId());
    if (jsonNode.isArray()) {
      ArrayNode arrayNode = (ArrayNode)jsonNode;
      for (Object value : values) {
        if (value instanceof PrestoTopic) {
          PrestoTopic topic = (PrestoTopic)value;
          arrayNode.add(topic.getId());
        } else {
          arrayNode.add((String)value);
        }
      }
    }
  }

  private void setValue(ObjectNode data, PrestoField field, Collection<Object> values) {
    ArrayNode arrayNode = dataProvider.getObjectMapper().createArrayNode();
    for (Object value : values) {
      if (value instanceof PrestoTopic) {
        PrestoTopic topic = (PrestoTopic)value;
        arrayNode.add(topic.getId());
      } else {
        arrayNode.add((String)value);
      }
    }
    data.put(field.getId(), arrayNode);
  }

  private static class Change {

    static enum ChangeType { SET, ADD, REMOVE };

    private ChangeType type;
    private final PrestoField field;
    private final Collection<Object> values;

    Change(ChangeType type, PrestoField field, Collection<Object> values) {
      this.type = type;
      this.field = field;
      this.values = values;      
    }

    public ChangeType getType() {
      return type;
    }

    public PrestoField getField() {
      return field;
    }
    public Collection<Object> getValues() {
      return values;
    }
  }

}
