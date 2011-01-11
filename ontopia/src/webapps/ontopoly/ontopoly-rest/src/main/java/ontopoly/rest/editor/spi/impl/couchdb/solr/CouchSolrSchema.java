package ontopoly.rest.editor.spi.impl.couchdb.solr;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

public class CouchSolrSchema {

  private final ObjectNode config;

  public CouchSolrSchema(ObjectNode config) {
    this.config = config;
  }

  public TypeConfig getType(String typeId) {
    ObjectNode typeConfig = (ObjectNode)config.get(typeId);
//    System.out.println("tt: " + typeConfig);
    if (typeConfig != null) {
      return new TypeConfig(typeId, typeConfig);
    } else {
      return null;
    }
  }

  public static CouchSolrSchema parse(String schemaFile) throws Exception {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    ObjectMapper mapper = new ObjectMapper();
    InputStream istream = cl.getResourceAsStream(schemaFile);
    Reader reader = new InputStreamReader(istream, "UTF-8");
    ObjectNode objectNode = mapper.readValue(reader, ObjectNode.class);
    return new CouchSolrSchema(objectNode);      
  }

  public static class TypeConfig {

    private final ObjectNode config;
    private final String typeId;

    TypeConfig(String typeId, ObjectNode config) {
      this.typeId = typeId;
      this.config = config;
    }
    
    public String getId() {
      return typeId;      
    }
    
    public Collection<FieldConfig> getFields() {
      List<FieldConfig> result = new ArrayList<FieldConfig>();
      Iterator<Entry<String, JsonNode>> fields = config.getFields();
      while (fields.hasNext()) {
        Entry<String, JsonNode> next = fields.next();
        String fieldId = next.getKey();
        ObjectNode fieldConfig = (ObjectNode)next.getValue();
        result.add(new FieldConfig(fieldId, fieldConfig));
      }
      return result;
    }
    
    public String toString() {
      return config.toString();
    }

  }

  public static class FieldConfig {

    private final String fieldId;
    private final ObjectNode config;

    FieldConfig(String fieldId, ObjectNode config) {
      this.fieldId = fieldId;
      this.config = config;
    }
    
    public String getId() {
      return fieldId;      
    }

    public String getValue() {
      JsonNode valueNode = config.get("value");
      return valueNode == null || !valueNode.isTextual() ? null : valueNode.getTextValue();
    }
    
    public List<String> getPath() {
      JsonNode pathNode = config.get("path");
      if (pathNode == null || !pathNode.isArray()) return null;
      
      List<String> result = new ArrayList<String>(pathNode.size());                                   
      for (JsonNode node : pathNode) {
        result.add(node.getTextValue());
      }
      return result;
    }
    
    public String toString() {
      return config.toString();
    }
  }
  
}
