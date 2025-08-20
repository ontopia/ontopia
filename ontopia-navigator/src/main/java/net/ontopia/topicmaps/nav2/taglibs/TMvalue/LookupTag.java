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

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.servlet.jsp.JspTagException;

import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.index.NameIndexIF;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;

import net.ontopia.topicmaps.nav2.core.NavigatorCompileException;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.utils.NavigatorUtils;
import net.ontopia.topicmaps.nav2.taglibs.value.BaseValueProducingAndAcceptingTag;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;

/**
 * INTERNAL: Value Producing Tag for looking up objects by their
 * <ul>
 *   <li>subject address,
 *   <li>subject indicator,
 *   <li>source locator,
 *   <li>or object id.
 * </ul>
 */
public class LookupTag extends BaseValueProducingAndAcceptingTag {

  // constants
  public final static String KIND_SUBJECT   = "subject";
  public final static String KIND_INDICATOR = "indicator";
  public final static String KIND_SOURCE   = "source";
  
  // tag attributes
  private String indicator;
  private String subject;
  private String source;
  private String objectid;
  private String parameter;
  private String basenameValue;
  private String variantValue;
  private String lookupAs;
  private boolean expect = false; // do not expect result by default
  
  @Override
  public Collection process(Collection values) throws JspTagException {
    
    // check first if unique attribute settings
    int setParams = ((indicator!=null) ? 1 : 0)
      + ((subject!=null) ? 1 : 0)
      + ((source!=null) ? 1 : 0)
      + ((objectid!=null) ? 1 : 0)
      + ((lookupAs!=null) ? 1 : 0)
      + ((parameter!=null) ? 1 : 0)
      + ((basenameValue!=null) ? 1 : 0)
      + ((variantValue!=null) ? 1 : 0);
    if (setParams != 1) {
      throw new NavigatorCompileException("LookupTag: Ambiguous attribute " +
                                          "settings (need 1, got " + setParams + ").");
    }

    // try to retrieve default value from ContextManager
    ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);

    // get topicmap object on which we should compute 
    TopicMapIF topicmap = contextTag.getTopicMap();
    if (topicmap == null) {
      throw new NavigatorRuntimeException("LookupTag found no topic map.");
    }
                                            
    // result collection which contains as first entry one topic map object
    Collection resColl = new ArrayList();
    
    // try to lookup topic map object
    try {
      if (lookupAs != null) {
        if (values != null && !values.isEmpty()) {
          Iterator iter = values.iterator();
          while (iter.hasNext()) {            
            Object locator = iter.next();
            if (locator instanceof LocatorIF) { 
              if (lookupAs.equals(KIND_SUBJECT)) {
                resColl.add(topicmap.getTopicBySubjectLocator((LocatorIF)locator));
              } else if (lookupAs.equals(KIND_INDICATOR)) {
                resColl.add(topicmap.getTopicBySubjectIdentifier((LocatorIF)locator));
              } else if (lookupAs.equals(KIND_SOURCE)) {
                resColl.add(topicmap.getObjectByItemIdentifier((LocatorIF)locator));
              } else {
                throw new NavigatorCompileException("LookupTag: Unknown as attribute value:" + lookupAs);
              }
            }
          }
        }
      } else {
        if (indicator != null) {
          resColl.add(topicmap.getTopicBySubjectIdentifier(getLocator(topicmap, indicator)));
        } else if (subject != null) {
          resColl.add(topicmap.getTopicBySubjectLocator(getLocator(topicmap, subject)));
        } else if (source != null) {
          resColl.add(topicmap.getObjectByItemIdentifier(getLocator(topicmap, source)));
        } else if (objectid != null) {
          resColl.add(topicmap.getObjectById(objectid));
        } else if (parameter != null) {
          String id = pageContext.getRequest().getParameter(parameter);
          TMObjectIF object = NavigatorUtils.stringID2Object(topicmap, id);
          if (object != null) {
            resColl.add(object);
          }
        } else if (basenameValue != null) {
          NameIndexIF nameIndex = getNameIndex(topicmap);
          // add Collection of TopicNameIF objects
          resColl.addAll(nameIndex.getTopicNames(basenameValue));
        } else if (variantValue != null) {
          NameIndexIF nameIndex = getNameIndex(topicmap);
          // add Collection of VariantIF objects
          resColl.addAll(nameIndex.getVariants(variantValue));
        }
      }
    } catch (URISyntaxException e) {
      throw new NavigatorCompileException("Invalid URI specified in LookupTag.", e);
    }

    // eventually a null element was added so be sure to remove that
    resColl.remove(null);

    // complain if a result was expected
    if (expect && resColl.isEmpty()) {
      throw new NavigatorRuntimeException("LookupTag expected to find an object, but none was found.");
    }
    
    return resColl;
  }

  // -----------------------------------------------------------------
  // set methods for tag attributes
  // -----------------------------------------------------------------

  public void setIndicator(String indicator) {
    this.indicator = indicator;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public void setObjectid(String objectid) {
    this.objectid = objectid;
  }

  public void setParameter(String parameter) {
    this.parameter = parameter;
  }

  public void setBasename(String nameValue) {
    this.basenameValue = nameValue;
  }
  
  public void setVariant(String nameValue) {
    this.variantValue = nameValue;
  }
  
  public void setExpect(String expect) {
    if (expect.equalsIgnoreCase("true") ||
        expect.equalsIgnoreCase("yes") ) {
      this.expect = true;
    } else {
      this.expect = false;
    }
  }

  public void setAs(String as) throws NavigatorRuntimeException {
    if (!as.equals(KIND_SUBJECT)
        && !as.equals(KIND_INDICATOR)
        && !as.equals(KIND_SOURCE)) {
      throw new NavigatorRuntimeException("Non-supported value ('" + as +
                                          "') given for attribute 'as' in element 'lookup'.");
    }

    this.lookupAs = as;
  }
  
  // -----------------------------------------------------------------
  // internal helper method(s)
  // -----------------------------------------------------------------

  private NameIndexIF getNameIndex(TopicMapIF topicmap) {
    return (NameIndexIF) topicmap
      .getIndex("net.ontopia.topicmaps.core.index.NameIndexIF");
  }
  
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
