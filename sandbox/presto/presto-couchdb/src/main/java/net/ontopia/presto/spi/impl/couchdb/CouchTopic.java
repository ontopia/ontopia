package net.ontopia.presto.spi.impl.couchdb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.ontopia.presto.spi.PrestoDataProvider;
import net.ontopia.presto.spi.PrestoField;
import net.ontopia.presto.spi.PrestoTopic;
import net.ontopia.presto.spi.PrestoType;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

public class CouchTopic implements PrestoTopic {

  private final PrestoDataProvider dataProvider;
  
  private final ObjectNode data;

  private CouchTopic(CouchDataProvider dataProvider, ObjectNode data) {
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
    ObjectNode data = dataProvider.getObjectMapper().createObjectNode();
    data.put(":type", type.getId());
    return new CouchTopic(dataProvider, data);
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
    JsonNode fieldNode = data.get(field.getId());
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

}
