package ontopoly.rest;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FieldConfig {

  private String id;
  private String name;
  private String cardinality;
  private String type;
  private String interfaceControl;
  private String embeddedView;
  
  public FieldConfig() {
  }
  
  public FieldConfig(String id, String name) {
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

  public void setCardinality(String cardinality) {
    this.cardinality = cardinality;
  }

  public String getCardinality() {
    return cardinality;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public void setEmbeddedView(String embeddedView) {
    this.embeddedView = embeddedView;
  }

  public String getEmbeddedView() {
    return embeddedView;
  }

  public void setInterfaceControl(String interfaceControl) {
    this.interfaceControl = interfaceControl;
  }

  public String getInterfaceControl() {
    return interfaceControl;
  }
  
}
