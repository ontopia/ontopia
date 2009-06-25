
// $Id: VariableLiteralGenerator.java,v 1.3 2009/02/27 12:03:49 lars.garshol Exp $

package net.ontopia.topicmaps.utils.ctm;

import java.net.MalformedURLException;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.utils.PSI;
import net.ontopia.topicmaps.xml.InvalidTopicMapException;

/**
 * INTERNAL: Represents a variable as defined in a specific template.
 */
public class VariableLiteralGenerator implements LiteralGeneratorIF {
  private Template template;
  private String variable;
  private String value;
  private LocatorIF datatype;

  public VariableLiteralGenerator(Template template, String variable) {
    this.template = template;
    this.variable = variable;
  }

  public String getLiteral() {
    return value;
  }
  
  public LocatorIF getDatatype() {
    return datatype;
  }

  public LocatorIF getLocator() {
    if (!datatype.equals(PSI.getXSDURI()))
      throw new OntopiaRuntimeException("Parameter $" + variable + " is not a locator");
    try {
      return new URILocator(value);
    } catch (MalformedURLException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  public LiteralGeneratorIF copyLiteral() {
    return this;
  }

  public void setValue(Object value) {
    if (!(value instanceof LiteralGeneratorIF))
      throw new InvalidTopicMapException("Parameter " + variable + " to " +
                                         "template " + template.getName() + " " +
                                         "must be a literal, but got " +
                                         value);
    LiteralGeneratorIF gen = (LiteralGeneratorIF) value;
    this.value = gen.getLiteral();
    this.datatype = gen.getDatatype();
  }
}
