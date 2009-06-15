
// $Id: RDFPropertyMapping.java,v 1.1 2004/06/21 18:14:37 larsga Exp $

package net.ontopia.topicmaps.utils.rdf;

/**
 * INTERNAL: Represents the mapping of a single RDF property.
 */
public class RDFPropertyMapping {
  private String property; // full uri
  private String mapsto;   // full uri
  private String inscope;  // full uri
  private String type;     // full uri
  private String subject;  // full uri
  private String object;   // full uri

  public RDFPropertyMapping(String property) {
    this.property = property;
  }

  public String getProperty() {
    return property;
  }
 
  public String getMapsTo() {
    return mapsto;
  }

  public void setMapsTo(String mapsto) {
    this.mapsto = mapsto;
  }

  public String getInScope() {
    return inscope;
  }

  public void setInScope(String inscope) {
    this.inscope = inscope;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getSubjectRole() {
    return subject;
  }

  public void setSubjectRole(String subject) {
    this.subject = subject;
  }

  public String getObjectRole() {
    return object;
  }

  public void setObjectRole(String object) {
    this.object = object;
  }
}
