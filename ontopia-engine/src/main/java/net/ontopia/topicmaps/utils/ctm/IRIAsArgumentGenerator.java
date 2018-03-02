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

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.utils.PSI;

/**
 * INTERNAL: A special generator that's used when an IRI is passed as
 * an argument to a template because this can be either a topic
 * reference or an IRI literal, and we don't know which.
 */
public class IRIAsArgumentGenerator implements ValueGeneratorIF {
  private ParseContextIF context;
  private LocatorIF locator;
  
  public IRIAsArgumentGenerator(ParseContextIF context, LocatorIF locator) {
    this.context = context;
    this.locator = locator;
  }

  @Override
  public boolean isTopic() {
    return true;
  }  
  
  @Override
  public TopicIF getTopic() {
    return context.makeTopicBySubjectIdentifier(locator);
  }

  @Override
  public ValueGeneratorIF copy() {
    return this; // should be OK
  }
  
  @Override
  public String getLiteral() {
    return locator.getAddress();
  }
  
  @Override
  public LocatorIF getDatatype() {
    return PSI.getXSDURI();
  }

  @Override
  public LocatorIF getLocator() {
    return locator;
  }
}
