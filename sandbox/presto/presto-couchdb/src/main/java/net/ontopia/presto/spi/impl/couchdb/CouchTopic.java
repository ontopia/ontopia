package net.ontopia.presto.spi.impl.couchdb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import net.ontopia.presto.spi.PrestoDataProvider;
import net.ontopia.presto.spi.PrestoField;
import net.ontopia.presto.spi.PrestoTopic;
import net.ontopia.presto.spi.PrestoType;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

public class CouchTopic implements PrestoTopic {

  protected final CouchDataProvider dataProvider;  
  protected final ObjectNode data;

  protected CouchTopic(CouchDataProvider dataProvider, ObjectNode data) {
    this.dataProvider = dataProvider;
    this.data = data;    
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

  static CouchTopic existing(CouchDataProvider dataProvider, ObjectNode doc) {
    return new CouchTopic(dataProvider, doc);
  }

  static CouchTopic newInstance(CouchDataProvider dataProvider, PrestoType type) {
    return new CouchTopic(dataProvider, CouchTopic.newInstanceObjectNode(dataProvider, type));
  }

  protected static ObjectNode newInstanceObjectNode(CouchDataProvider dataProvider, PrestoType type) {
    ObjectNode data = dataProvider.getDataStrategy().getObjectMapper().createObjectNode();
    data.put(":type", type.getId());
    return data;
  }
  
  ObjectNode getData() {
    return data;
  }
  
  public PrestoDataProvider getDataProvider() {
    return dataProvider;
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

  public Collection<Object> getValues(PrestoField field) {
    boolean isReferenceField = field.isReferenceField();
    boolean isExternalType = field.getExternalType() != null;
    List<Object> values = new ArrayList<Object>();
    ArrayNode fieldNode = dataProvider.getDataStrategy().getFieldValue(this, field);
    if (fieldNode != null) { 
      for (JsonNode value : fieldNode) {
        String textValue = value.getTextValue();
        if (isReferenceField && !isExternalType) {
          PrestoTopic valueTopic = dataProvider.getTopicById(textValue);
          values.add(valueTopic);
        } else {
          values.add(textValue);          
        }
      }
    }
    return values;
  }

  // methods for updating the state of a couchdb document

  void setValue(PrestoField field, Collection<? extends Object> values) {
    if (values.isEmpty()) {
      dataProvider.getDataStrategy().removeFieldValue(this, field);
    } else {
      ArrayNode arrayNode = dataProvider.getDataStrategy().getObjectMapper().createArrayNode();
      for (Object value : values) {
        if (value instanceof CouchTopic) {
          CouchTopic valueTopic = (CouchTopic)value;
          arrayNode.add(valueTopic.getId());
        } else {
          arrayNode.add((String)value);
        }
      }
      dataProvider.getDataStrategy().putFieldValue(this, field, arrayNode);
    }
  }

  void addValue(PrestoField field, Collection<? extends Object> values) {
    if (!values.isEmpty()) {
      Collection<String> result = new HashSet<String>();
      ArrayNode jsonNode = dataProvider.getDataStrategy().getFieldValue(this, field);
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
      ArrayNode arrayNode = dataProvider.getDataStrategy().getObjectMapper().createArrayNode();
      for (String value : result) {
        arrayNode.add(value);
      }

      //      System.out.println("A: " + arrayNode);
      dataProvider.getDataStrategy().putFieldValue(this, field, arrayNode);
    }
  }

  void removeValue(PrestoField field, Collection<? extends Object> values) {
    if (!values.isEmpty()) {
      ArrayNode jsonNode = dataProvider.getDataStrategy().getFieldValue(this, field);
      //      System.out.println("R: " + field.getId() + " " + values);
      if (jsonNode != null) {
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
        ArrayNode arrayNode  = dataProvider.getDataStrategy().getObjectMapper().createArrayNode();
        for (String value : existing) {
          arrayNode.add(value);
        }
        dataProvider.getDataStrategy().putFieldValue(this, field, arrayNode);
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
    data.put(":name", name);
  }

}
