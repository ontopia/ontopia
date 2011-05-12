package net.ontopia.presto.spi.impl.couchdb;

import net.ontopia.presto.spi.PrestoField;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;

public class CouchDataStrategy {

  private final ObjectMapper mapper = new ObjectMapper();

  ObjectMapper getObjectMapper() {
    return mapper;
  }

  // json data access strategy
  
  public ArrayNode getFieldValue(CouchTopic topic, PrestoField field) {
    JsonNode value = topic.getData().get(field.getId());
    return (ArrayNode)(value != null && value.isArray() ? value : null); 
  }
  
  public void putFieldValue(CouchTopic topic, PrestoField field, ArrayNode value) {
    topic.getData().put(field.getId(), value);
  }
  
  public void removeFieldValue(CouchTopic topic, PrestoField field) {
    topic.getData().remove(field.getId());
  }

}
