
// $Id: BasicLiteralGenerator.java,v 1.3 2009/02/27 11:59:30 lars.garshol Exp $

package net.ontopia.topicmaps.utils.ctm;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.utils.PSI;

public class BasicLiteralGenerator implements LiteralGeneratorIF {
  private String value;      // if set, locator is null
  private LocatorIF locator; // contains value if set, value is then null
  private LocatorIF datatype;

  public BasicLiteralGenerator() {
  }
  
  public BasicLiteralGenerator(String value, LocatorIF locator, LocatorIF datatype) {
    this.value = value;
    this.locator = locator;
    this.datatype = datatype;
  }

  public String getLiteral() {
    if (value != null)
      return value;
    else
      return locator.getAddress();
  }
  
  public LocatorIF getDatatype() {
    if (value != null)
      return datatype;
    else
      return PSI.getXSDURI();
  }

  public LocatorIF getLocator() {
    if (value != null)
      throw new net.ontopia.utils.OntopiaRuntimeException("ERROR!"); // FIXME: proper exception
    return locator;
  }

  public LiteralGeneratorIF copyLiteral() {
    return new BasicLiteralGenerator(value, locator, datatype);
  }

  public void setLiteral(String literal) {
    this.value = literal;
    this.locator = null;
  }

  public void setDatatype(LocatorIF datatype) {
    this.datatype = datatype;
  }

  public void setLocator(LocatorIF locator) {
    this.datatype = null;
    this.value = null;
    this.locator = locator;
  }
}
