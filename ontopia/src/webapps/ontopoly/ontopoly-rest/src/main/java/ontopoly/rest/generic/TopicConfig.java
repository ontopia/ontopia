package ontopoly.rest.generic;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TopicConfig {

  private String id;
  private String name;
  private String type;
  private List<FieldConfig> fields;
  
  public TopicConfig() {
  }
  
  public TopicConfig(String id, String name) {
    this.id = id;
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setFields(List<FieldConfig> fields) {
    this.fields = fields;
  }

  public List<FieldConfig> getFields() {
    return fields;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }
  
}
