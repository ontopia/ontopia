
package net.ontopia.topicmaps.utils.ctm;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.utils.PSI;
import net.ontopia.topicmaps.xml.InvalidTopicMapException;

/**
 * Simple generator storing values to be generated.
 */
public class ValueGenerator implements ValueGeneratorIF {
  // one of three alternatives will be populated:
  // (1) our value is a topic
  private TopicIF topic;
  // (2) our value is a literal
  private String literal;
  private LocatorIF datatype;
  // (3) our value is a locator literal
  private LocatorIF locator;

  public ValueGenerator() {
  }
  
  // copy constructor
  public ValueGenerator(TopicIF topic, String literal, LocatorIF datatype,
                        LocatorIF locator) {
    this.topic = topic;
    this.literal = literal;
    this.datatype = datatype;
    this.locator = locator;
  }

  public boolean isTopic() {
    return (topic != null);
  }
  
  public String getLiteral() {
    if (literal == null && locator != null)
      return locator.getAddress();
    else
      return literal;
  }
  
  public LocatorIF getDatatype() {
    if (literal == null)
      return PSI.getXSDURI(); // assuming we are a locator
    else if (datatype == null && literal != null)
      return PSI.getXSDString();
    else
      return datatype;
  }

  public LocatorIF getLocator() {
    return locator;
  }  
  
  public ValueGeneratorIF copy() {
    return new ValueGenerator(topic, literal, datatype, locator);
  }

  public TopicIF getTopic() {
    if (topic == null) {
      if (literal == null && locator == null)
        throw new InvalidTopicMapException("Parameter not specified!");
      else
        throw new InvalidTopicMapException("Parameter used as topic, but was '" +
                                           literal + "'");
    }
    return topic;
  }

  public void setLocator(LocatorIF locator) {
    this.locator = locator;
    this.datatype = null;
    this.literal = null;
  }

  public void setLiteral(String literal) {
    this.literal = literal;
    this.datatype = null;
  }

  public void setDatatype(LocatorIF datatype) {
    this.datatype = datatype;
  }

  public String toString() {
    return "[ValueGenerator, topic: " + topic + ", literal: '" + literal + "', "+
      "locator: " + locator + ", datatype: " + datatype + "]";
  }
}
