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

package net.ontopia.topicmaps.nav2.taglibs.logic;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import javax.servlet.jsp.tagext.TagSupport;
import net.ontopia.infoset.impl.basic.URILocator; 
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.utils.TopicComparators;
import net.ontopia.topicmaps.utils.TopicStringifiers;
import net.ontopia.topicmaps.utils.PSI;
import net.ontopia.topicmaps.nav.context.UserFilterContextStore;
import net.ontopia.topicmaps.nav.utils.comparators.TopicComparator;
import net.ontopia.topicmaps.nav2.core.ContextManagerIF;
import net.ontopia.topicmaps.nav2.core.NavigatorConfigurationIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.core.UserIF;
import net.ontopia.topicmaps.nav2.core.ValueAcceptingTagIF;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Logic Tag for establishing the outermost
 * lexical scope in which computation happens.
 */
public class SetTag extends TagSupport implements ValueAcceptingTagIF {

  // initialization of logging facility
  private static final Logger log = LoggerFactory.getLogger(SetTag.class.getName());

  // constants
  private static final Function DEF_TOPIC_STRINGIFIER = TopicStringifiers
    .getSortNameStringifier();
  private static final Comparator DEF_TOPIC_COMPARATOR = TopicComparators
    .getCaseInsensitiveComparator(DEF_TOPIC_STRINGIFIER);
  private static final URILocator SORT_LOCATOR = PSI.getXTMSort();
  
  // members
  private ContextTag contextTag;
  private Object currentScope;
  private Collection value; // the value set by the child
  
  // tag attributes
  private String variableName;
  private Comparator listComparator;
  private boolean sortItems = true;
  
  /**
   * Process the start tag for this instance.
   */
  @Override
  public int doStartTag() {
    this.contextTag = FrameworkUtils.getContextTag(pageContext);
    this.currentScope = contextTag.getContextManager().getCurrentScope();
    this.value = Collections.EMPTY_SET;
    return EVAL_BODY_INCLUDE;
  }

  /**
   * Process the end tag.
   */
  @Override
  public int doEndTag() {
    
    // Called when we hit the end tag, so that even if the tag has no
    // content we will still get a value. That is, it is no longer
    // necessary to call accept() in order to set the variable.

    // DEFAULT: sorting of elements in collection
    if (sortItems && value.size() > 1) {
      Object[] items = value.toArray();
      Comparator c = null;
      try { 
        // --- sort the items
        c = getComparator();
        Arrays.sort(items, c);
        
      } catch (Exception e) {
        log.info("Sorting the list '" + variableName +
                 "' with comparator " + c + " raised an exception: " + e);
      }
      // update the value variable so that it points to the sorted list.
      this.value = Arrays.asList(items);
    }
    // bind variable to name in current scope
    ContextManagerIF ctxtMgr = contextTag.getContextManager();
    ctxtMgr.setValueInScope(currentScope, variableName, value);
   
    //log.debug("set '" + variableName +"' (" + items + ") to context.");

    // reset members
    this.contextTag = null;
    this.currentScope = null;
    this.value = null;
    
    return EVAL_PAGE;
  }

  /**
   * reset the state of the Tag.
   */
  @Override
  public void release() {
    // overwrite default behaviour
    // do not set parent to null!!!
  }
    
  // -----------------------------------------------------------------
  // Set methods for tag attributes
  // -----------------------------------------------------------------
  
  public void setName(String name) {
    this.variableName = name;
  }

  public void setComparator(String classname)
    throws NavigatorRuntimeException {

    if (classname.equalsIgnoreCase("off")) {
      listComparator = null;
      sortItems = false;
    } else {
      sortItems = true;

      // NOTE: we're looking up the context tag here because the doStartTag will not have been called yet.
      if (contextTag == null)
        this.contextTag = FrameworkUtils.getContextTag(pageContext);

      listComparator = getComparatorInstance(classname);
    }
  }

  
  // -----------------------------------------------------------------
  // ValueAcceptingTagIF implementation
  // -----------------------------------------------------------------
  
  @Override
  public void accept(Collection value) {
    this.value = value;
  }

  private List getTopicNameContext() throws NavigatorRuntimeException {
    UserIF user = FrameworkUtils.getUser(pageContext);
    UserFilterContextStore filterContext = user.getFilterContext();
    if (filterContext == null) {
      return Collections.EMPTY_LIST;
    } else{
      TopicMapIF topicmap = contextTag.getTopicMap();
      if (topicmap == null)
        throw new NavigatorRuntimeException("SetTag found no topic map.");

      return new ArrayList(filterContext.getScopeTopicNames(topicmap));
    }
  }

  // --- internal helper method

  protected Comparator getComparator() throws Exception {
    // use default comparator if setting not overwritten by attribute
    if (listComparator != null)
      return listComparator;
    
    String defaultComparatorClassName = contextTag.getNavigatorConfiguration()
      .getProperty(NavigatorConfigurationIF.DEF_COMPARATOR,
                   NavigatorConfigurationIF.DEFVAL_COMPARATOR);
    
    // defaultComparator == TopicComparator
    if (defaultComparatorClassName
        .equals(NavigatorConfigurationIF.DEFVAL_COMPARATOR)) {
      TopicMapIF topicmap = contextTag.getTopicMap();
      if (topicmap == null)
        throw new NavigatorRuntimeException("SetTag found no topic map.");
      TopicIF sortTopic = topicmap.getTopicBySubjectIdentifier( SORT_LOCATOR );
      if (sortTopic != null) {
        // add base name themes that are in user context filter
        listComparator = new TopicComparator(getTopicNameContext(), Collections.singleton(sortTopic));
      } else {
        List bc = getTopicNameContext();
        if (bc.isEmpty())
          listComparator = DEF_TOPIC_COMPARATOR;
        else
          listComparator = new TopicComparator(bc, Collections.EMPTY_LIST);
      }
    } else {
      listComparator = getComparatorInstance(defaultComparatorClassName);
    }

    return listComparator;
  }
  
  protected Comparator getComparatorInstance(String classname)
    throws NavigatorRuntimeException {
    
    if (contextTag == null)
      return null;
    Object obj = contextTag.getNavigatorApplication().getInstanceOf(classname);
    if (obj != null && obj instanceof Comparator)
      return (Comparator) obj;
    else
      return null;
  }
  
}
