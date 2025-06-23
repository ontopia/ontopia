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

package net.ontopia.topicmaps.nav2.taglibs.TMvalue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.net.URISyntaxException;
import javax.servlet.jsp.JspTagException;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;

import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.taglibs.value.BaseValueProducingAndAcceptingTag;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;

/**
 * INTERNAL: Value producing and value accepting tag of retrieving the
 * locators of OccurrenceIF and VariantNameIF instances. The tag also
 * accepts uri-as-strings. Note that if the string is not a valid URI
 * it is silently ignored.
 */
public class LocatorTag extends BaseValueProducingAndAcceptingTag {
  
  @Override
  public Collection process(Collection values) throws JspTagException {
    // find the locators of the occurrences and variant names [and uri-as-string]
    if (values == null || values.isEmpty()) {
      return Collections.EMPTY_LIST;
    } else {
      
      List locators = new ArrayList(values.size());
      Iterator iter = values.iterator();      
      while (iter.hasNext()) {
        Object value = iter.next();
        
        if (value instanceof OccurrenceIF) {
          LocatorIF locator = ((OccurrenceIF)value).getLocator();
          if (locator != null) {
            locators.add(locator);
          }

        } else if (value instanceof VariantNameIF) {
          LocatorIF locator = ((VariantNameIF)value).getLocator();
          if (locator != null) {
            locators.add(locator);
          }

        } else if (value instanceof String) {
          
          // try to retrieve default value from ContextManager
          ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);
          
          // get topicmap object on which we should compute 
          TopicMapIF topicmap = contextTag.getTopicMap();
          if (topicmap == null) {
            throw new NavigatorRuntimeException("LookupTag found no topic map.");
          }
          
          try {
            // Attempt to parse string as URI
            locators.add(getLocator(topicmap, (String)value));
          } catch (URISyntaxException e) {
            // ignore string, since it is not a valid URI
          }
        }
      }
      return locators;
    }
  }
  
  // -----------------------------------------------------------------
  // internal helper method(s)
  // -----------------------------------------------------------------
  
  /**
   * INTERNAL: try to convert a URI String to a LocatorIF object,
   * check if String is specifying a relative URI.
   */
  private LocatorIF getLocator(TopicMapIF topicmap, String locString)
    throws URISyntaxException {

    LocatorIF base = topicmap.getStore().getBaseAddress();
    if (base != null) {
      return base.resolveAbsolute(locString);
    } else {
      return new URILocator(locString);
    }
  }

}
