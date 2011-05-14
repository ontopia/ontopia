package net.ontopia.presto.spi.impl.couchdb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import net.ontopia.presto.spi.PrestoField;
import net.ontopia.presto.spi.PrestoTopic;
import net.ontopia.presto.spi.PrestoType;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

public abstract class CouchTopic implements PrestoTopic {

  private final CouchDataProvider dataProvider;  
  private final ObjectNode data;

  protected CouchTopic(CouchDataProvider dataProvider, ObjectNode data) {
    this.dataProvider = dataProvider;
    this.data = data;    
  }

  protected CouchDataProvider getDataProvider() {
    return dataProvider;
  }
  
  public boolean equals(Object o) {
    if (o instanceof CouchTopic) {
      CouchTopic other = (CouchTopic)o;
      return other.getId().equals(getId());
    }
    return false;
  }

  public int hashCode() {
    return getId().hashCode();
  }

  protected static ObjectNode newInstanceObjectNode(CouchDataProvider dataProvider, PrestoType type) {
    ObjectNode data = dataProvider.getObjectMapper().createObjectNode();
    data.put(":type", type.getId());
    return data;
  }

  ObjectNode getData() {
    return data;
  }

  public String getId() {
    return data.get("_id").getTextValue();
  }

  public String getName() {
    JsonNode name = data.get(":name");
    return name == null ? null : name.getTextValue();
  }

  public String getTypeId() {
    return data.get(":type").getTextValue();
  }

  // json data access strategy

  protected abstract ArrayNode getFieldValue(PrestoField field);

  protected abstract void putFieldValue(PrestoField field, ArrayNode value);

  protected abstract void removeFieldValue(PrestoField field);

  // methods for retrieving the state of a couchdb document

  public Collection<Object> getValues(PrestoField field) {
    boolean isReferenceField = field.isReferenceField();
    boolean isExternalType = field.getExternalType() != null;
    List<Object> values = new ArrayList<Object>();
    ArrayNode fieldNode = getFieldValue(field);
    if (fieldNode != null) { 
      if (isReferenceField && !isExternalType) {
        List<String> topicIds = new ArrayList<String>(fieldNode.size());
        for (JsonNode value : fieldNode) {
          topicIds.add(value.getTextValue());
        }
        values.addAll(dataProvider.getTopicsByIds(topicIds));
      } else {
        for (JsonNode value : fieldNode) {
          String textValue = value.getTextValue();
          values.add(textValue);          
        }
      }
    }
    return values;
  }

  // methods for updating the state of a couchdb document

  void setValue(PrestoField field, Collection<? extends Object> values) {
    if (values.isEmpty()) {
      removeFieldValue(field);
    } else {
      ArrayNode arrayNode = dataProvider.getObjectMapper().createArrayNode();
      for (Object value : values) {
        if (value instanceof CouchTopic) {
          CouchTopic valueTopic = (CouchTopic)value;
          arrayNode.add(valueTopic.getId());
        } else {
          arrayNode.add((String)value);
        }
      }
      putFieldValue(field, arrayNode);
    }
  }

  void addValue(PrestoField field, Collection<? extends Object> values) {
    if (!values.isEmpty()) {
      Collection<String> result = new LinkedHashSet<String>(); // FIXME: should not be hashset if duplicates allowed
      ArrayNode jsonNode = getFieldValue(field);
      if (jsonNode != null) {
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

      //      System.out.println("A: " + arrayNode);
      putFieldValue(field, arrayNode);
    }
  }

  void removeValue(PrestoField field, Collection<? extends Object> values) {
    if (!values.isEmpty()) {
      ArrayNode jsonNode = getFieldValue(field);
      //      System.out.println("R: " + field.getId() + " " + values);
      if (jsonNode != null) {
        Collection<String> existing = new LinkedHashSet<String>(jsonNode.size());
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
        putFieldValue(field, arrayNode);
      }
    }
  }

  void updateNameProperty(Collection<? extends Object> values) {
    String name;
    Object value = values.isEmpty() ? null : values.iterator().next();
    if (value == null) {
      name = "No name";
    } else if (value instanceof CouchTopic) {
      name = ((CouchTopic)value).getName();
    } else {
      name = value.toString();
    }
    getData().put(":name", name);
  }

}
