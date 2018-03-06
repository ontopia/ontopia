/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.topicmaps.utils.ctm;

import java.net.URISyntaxException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.utils.PSI;
import net.ontopia.topicmaps.xml.InvalidTopicMapException;
import net.ontopia.utils.OntopiaRuntimeException;

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

  @Override
  public boolean isTopic() {
    return (topic != null);
  }
  
  @Override
  public String getLiteral() {
    if (literal == null && locator != null) {
      return locator.getAddress();
    } else {
      return literal;
    }
  }
  
  @Override
  public LocatorIF getDatatype() {
    if (literal == null) {
      return PSI.getXSDURI(); // assuming we are a locator
    } else if (datatype == null && literal != null) {
      return PSI.getXSDString();
    } else {
      return datatype;
    }
  }

  @Override
  public LocatorIF getLocator() {
    if (locator == null && literal != null &&
        datatype.equals(DataTypes.TYPE_STRING)) {
      // it's possible for tolog updates to put us in this position, because
      // tolog may produce string values which it then attempts to use as
      // locator values in the CTM part. here we make a best-effort attempt
      // to handle this.
      try {
        return new URILocator(literal);
      } catch (URISyntaxException e) {
        throw new OntopiaRuntimeException("Malformed URL: <" + literal + ">");
      }
    }
    return locator;
  }  
  
  @Override
  public ValueGeneratorIF copy() {
    return new ValueGenerator(topic, literal, datatype, locator);
  }

  @Override
  public TopicIF getTopic() {
    if (topic == null) {
      if (literal == null && locator == null) {
        throw new InvalidTopicMapException("Parameter not specified!");
      } else {
        throw new InvalidTopicMapException("Parameter used as topic, but was '" +
                                           literal + "'");
      }
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

  @Override
  public String toString() {
    return "[ValueGenerator, topic: " + topic + ", literal: '" + literal + "', "+
      "locator: " + locator + ", datatype: " + datatype + "]";
  }
}
