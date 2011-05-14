package net.ontopia.presto.spi.impl.couchdb;

import net.ontopia.presto.spi.PrestoField;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

public class CouchDefaultTopic extends CouchTopic {
  
  protected CouchDefaultTopic(CouchDataProvider dataProvider, ObjectNode data) {
    super(dataProvider, data);
  }
  
  // json data access strategy
  
  @Override
  protected ArrayNode getFieldValue(PrestoField field) {
    JsonNode value = getData().get(field.getId());
    return (ArrayNode)(value != null && value.isArray() ? value : null); 
  }
  
  @Override
  protected void putFieldValue(PrestoField field, ArrayNode value) {
    getData().put(field.getId(), value);
  }
  
  @Override
  protected void removeFieldValue(PrestoField field) {
    getData().remove(field.getId());
  }

}
