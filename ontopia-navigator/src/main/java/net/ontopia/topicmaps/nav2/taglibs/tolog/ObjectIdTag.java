/*
 * #!
 * Ontopia Navigator
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

package net.ontopia.topicmaps.nav2.taglibs.tolog;

import java.io.IOException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Output Producing Tag for selecting the ID
 * of an object and writing it out.
 */
public class ObjectIdTag extends BaseOutputProducingTag {

  // initialization of logging facility
  private static final Logger log = LoggerFactory
    .getLogger(ObjectIdTag.class.getName());
  
  @Override
  public final void generateOutput(JspWriter out, Object outObject)
    throws JspTagException, IOException {
    
    if (outObject.equals(fallbackValue)) {
      print2Writer(out, fallbackValue);
      return;
    }

    String objectId;
    
    // --- first try if object is instance of TMObjectIF
    try {      
      objectId = ((TMObjectIF) outObject).getObjectId();
    } catch (ClassCastException e) {
      // --- TopicMapReferenceIF
      if (outObject instanceof TopicMapReferenceIF) { 
        objectId = ((TopicMapReferenceIF) outObject).getId();
      } else {
        // --- otherwise signal error
        String msg = "<tolog:oid> expected collection which contains " +
          "object instances of TMObjectIF or TopicMapReferenceIF, but got " +
          "instance of " + outObject.getClass().getName() + ". Please " +
          "control variable '" +
          ((variableName!= null) ?  variableName : "_default_") + "'.";
        log.error(msg);
        throw new NavigatorRuntimeException(msg);
      }
    }
    
    // finally write out String with help of the Stringifier
    if (objectId != null) {
      print2Writer( out, objectId );
    }
  }

  @Override
  public String getName() {
    return getClass().getName();
  }
}
