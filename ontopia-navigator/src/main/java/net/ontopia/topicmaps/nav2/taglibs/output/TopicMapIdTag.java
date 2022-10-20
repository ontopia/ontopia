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

package net.ontopia.topicmaps.nav2.taglibs.output;

import java.io.IOException;
import java.util.Iterator;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.nav2.core.NavigatorApplicationIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
  
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Output Producing Tag for selecting the ID of the topicmap
 * the specified object belongs to and writing it out.
 *
 * @since 1.2.5
 */
public class TopicMapIdTag extends BaseOutputProducingTag {

  // initialization of logging facility
  private static final Logger log = LoggerFactory
    .getLogger(TopicMapIdTag.class.getName());
  
  @Override
  public final void generateOutput(JspWriter out, Iterator iter)
    throws JspTagException, IOException {

    String topicMapId = null;
    Object elem = null;
    
    // --- try if object is instance of TMObjectIF
    try {
      elem = iter.next();
      TopicMapIF tm = ((TMObjectIF) elem).getTopicMap();
      NavigatorApplicationIF navApp = contextTag.getNavigatorApplication();
      topicMapId = navApp.getTopicMapRefId(tm);
    } catch (ClassCastException e) {
      // --- signal error if from wrong object type
      String msg = "TopicMapIdTag expected collection which contains " +
        "object instances of TMObjectIF, but got " +
        "instance of " + elem.getClass().getName() + ". Please " +
        "control variable '" +
        ((variableName!= null) ? variableName : "_default_") + "'.";
      log.error(msg);
      throw new NavigatorRuntimeException(msg, e);
    } catch (NullPointerException ne) {
      String msg = "NullPointerException while trying to get topicmap id for  '" +
        ((variableName!= null) ? variableName : "_default_") + "': " + ne.getMessage();
      log.error(msg);
      // throw new NavigatorRuntimeException(msg, ne); //FIXME
    }
    
    // finally write out String with help of the Stringifier
    if (topicMapId != null) {
      print2Writer( out, topicMapId );
    }
  }

}
