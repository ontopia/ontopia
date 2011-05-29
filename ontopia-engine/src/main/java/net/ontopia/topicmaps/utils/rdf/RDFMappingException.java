
package net.ontopia.topicmaps.utils.rdf;

import net.ontopia.utils.OntopiaRuntimeException;

/**
 * PUBLIC: Thrown when an error occurs during RDF-to-topic maps
 * conversion.
 * @since 2.0
 */
public class RDFMappingException extends OntopiaRuntimeException {
  private String subject;
  private String property;
  
  public RDFMappingException(String msg) {
    super(msg);
  }

  /**
   * PUBLIC: Creates an exception that remembers the subject and the
   * property of the statement that caused the error.
   * @since 2.0.4
   */
  public RDFMappingException(String msg, String subject, String property) {
    super(msg + "\n  Subject: " + subject + "\n  Property: " + property);
    this.subject = subject;
    this.property = property;
  }

  /**
   * PUBLIC: Returns the URI of the subject of the statement that
   * caused this exception, if any, and if known.
   * @since 2.0.4
   */
  public String getSubject() {
    return subject;
  }

  /**
   * PUBLIC: Returns the URI of the property of the statement that
   * caused this exception, if any, and if known.
   * @since 2.0.4
   */
  public String getProperty() {
    return property;
  }
}
