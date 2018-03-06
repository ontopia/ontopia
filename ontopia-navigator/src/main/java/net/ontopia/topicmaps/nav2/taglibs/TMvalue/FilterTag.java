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
import java.util.HashSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Predicate;
import javax.servlet.jsp.JspTagException;
import net.ontopia.utils.CollectionUtils;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.TypedIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.nav2.core.NavigatorCompileException;
import net.ontopia.topicmaps.nav2.core.NavigatorDeciderIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.core.NavigatorTagException;
import net.ontopia.topicmaps.utils.SupersetOfContextDecider;
import net.ontopia.topicmaps.nav2.utils.NavigatorUtils;
import net.ontopia.topicmaps.nav2.taglibs.value.BaseValueProducingAndAcceptingTag;
import net.ontopia.topicmaps.nav2.impl.basic.DeciderIFWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Value Producing Tag for taking an input collection
 * and filtering it, passing only some of the elements on
 * to its parent (could be a set tag or another filter element).
 */
public class FilterTag extends BaseValueProducingAndAcceptingTag {

  // constants
  public static final String CLASS_TOPICMAP = "topicmap";
  public static final String CLASS_TOPIC    = "topic";
  public static final String CLASS_ASSOC    = "association";
  public static final String CLASS_OCC      = "occurrence";
  public static final String CLASS_BASENAME = "basename";
  public static final String CLASS_VARIANT  = "variant";
  public static final String CLASS_ROLE     = "role";
  public static final String CLASS_LOCATOR  = "locator";

  // initialization of logging facility
  private static final Logger log = LoggerFactory
    .getLogger(FilterTag.class.getName());

  // constants
  public static final String UNTYPED = "{NONE}";
  
  // tag attributes
  private String instanceOf;
  private String deciderClassName;
  private NavigatorDeciderIF decider;
  private String inScopeOfValue;
  private String isKind;
  private boolean invert = false;
  private boolean randomElement = false;

  @Override
  public Collection process(Collection tmObjects) throws JspTagException {
    Collection result = null;
    // check first if unique attribute settings
    int paramCount = ((instanceOf!=null) ? 1 : 0)
      + ((deciderClassName!=null) ? 1 : 0)
      + ((isKind!=null) ? 1 : 0)
      + ((inScopeOfValue!=null) ? 1 : 0)
      + ((randomElement) ? 1 : 0);
    if (paramCount != 1) {
      throw new NavigatorCompileException("FilterTag: Ambiguous attribute " +
                                          "settings (" + paramCount +" attrs "+
                                          "specified, must be exactly 1).");
    }
    // do the fun part
    if (instanceOf != null) { 
      result = filterInstanceOf(tmObjects, instanceOf);
    } else if (deciderClassName != null) {
      decider = getDeciderInstance(deciderClassName);
      if (decider == null) {
        throw new NavigatorCompileException("FilterTag: Could not retrieve " +
                                            "decider instance for " + deciderClassName);
      }
        
      result = filterWithDecider(tmObjects, decider);
    } else if (inScopeOfValue != null) {
      result = filterInScopeOf(tmObjects, inScopeOfValue);
    } else if (isKind != null) { 
      result = filterIs(tmObjects, isKind);
    } else if (randomElement) {
      result = Collections.singleton(CollectionUtils.getRandom(tmObjects));
    }
    
    return result;
  }
  

  // -----------------------------------------------------------------
  // internal helper / working methods
  // -----------------------------------------------------------------

  /**
   * INTERNAL: Create a new collection which consists only of objects
   * that are instances of the topic with the given URI as its
   * subject indicator.
   */
  private Collection filterInstanceOf(Collection tmObjects, String instanceOf)
    throws NavigatorTagException {
    
    Collection filtered = new HashSet();

    // log.debug("filterInstanceOf (" + instanceOf + "): " + tmObjects);
    
    // separate typed from untyped objects
    if (instanceOf.equals(UNTYPED)) {
      if (tmObjects != null) {
        Iterator iter = tmObjects.iterator();
        Object obj;
        while (iter.hasNext()) {
          obj = iter.next();
          if ((obj instanceof TypedIF) &&
              ( (((TypedIF) obj).getType() == null && !invert)
                || (((TypedIF) obj).getType() != null && invert))) {
            filtered.add(obj);
          } else if ((obj instanceof TopicIF) &&
                   ( (((TopicIF) obj).getTypes().isEmpty() && !invert)
                     || (((TopicIF) obj).getTypes().isEmpty() && invert))) {
            filtered.add(obj);
          }
        } // while
      }
    } else {
      Collection classes = null;
      // --- 1st try: interpret <instanceOf> as an URI
      try {
        classes = new ArrayList();
        // get Topic for filtering by subject indicator
        TopicMapIF topicmap = contextTag.getTopicMap();
        if (topicmap == null) {
          throw new NavigatorRuntimeException("FilterTag found no topic map.");
        }
        
        TopicIF topic = topicmap.getTopicBySubjectIdentifier(new URILocator(instanceOf));
        if (topic != null) {
          classes.add(topic);
        }
      }
      catch (URISyntaxException e) {
        // --- 2nd try: interpret <instanceOf> as variable name
        classes = contextTag.getContextManager().getValue(instanceOf);
      }
      if (tmObjects != null) {
        Iterator iter = tmObjects.iterator();
        Object obj;
        while (iter.hasNext()) {
          obj = iter.next();

          // ---- for a single-typed object:
          if (obj instanceof TypedIF &&
              ( (classes.contains(((TypedIF) obj).getType()) && !invert)
                || (!classes.contains(((TypedIF) obj).getType()) && invert) )) {
            filtered.add(obj);
          }
          // ---- for a topic:
          else if (obj instanceof TopicIF) {
            Iterator it = ((TopicIF) obj).getTypes().iterator();
            while (it.hasNext()) {
              TopicIF type = (TopicIF) it.next();
              if ((classes.contains(type) && !invert)
                  || (!classes.contains(type) && invert)) {
                filtered.add(obj);
                break;
              }
            } // while it
          }
          
        } // while iter
      }
    }
    
    // log.debug("filter result " + filtered);
    return filtered;
  }


  /**
   * INTERNAL: Creates a filtered collection with the help of
   * the specified decider class.
   */
  private Collection filterWithDecider(Collection tmObjects,
                                       NavigatorDeciderIF decider)  {
    Collection filtered = new HashSet();
    if (tmObjects != null) {
      Iterator iter = tmObjects.iterator();
      Object obj;
      while (iter.hasNext()) {
        obj = iter.next();
        if (decider.ok(contextTag, obj)) {
          if (!invert) {
            filtered.add(obj);
            // log.debug("not invert - ok - " + obj);
          } else {
            // log.debug("invert - ok - ! " + obj);
          }
        } else {
          if (invert) {
            filtered.add(obj);
            // log.debug("invert - not ok - " + obj);
          } else {
            // log.debug("not invert - ok - ! " + obj);
          }
        }
      } // while
    }
    
    return filtered;
  }

  /**
   * INTERNAL: Create a new collection which consists only of objects
   * which have the topic with the given URI as a theme in their scope,
   * or which have a topic in the named collection in their scopes.
   */
  private Collection filterInScopeOf(Collection<ScopedIF> tmObjects, String inScopeOf)
    throws NavigatorCompileException {
    
    Collection context = new ArrayList(1);
    // --- 1st try: interpret <inScopeOf> as an URI
    TopicIF topic = NavigatorUtils.stringID2Topic(contextTag.getTopicMap(),
                                                  inScopeOf);
    if (topic != null) {
      context.add(topic);
    } else {
      // --- 2nd try: interpret <inScopeOf> as variable name
      context = contextTag.getContextManager().getValue(inScopeOf);
    }
    
    Collection filtered = new HashSet();
    if (tmObjects != null) {
      SupersetOfContextDecider decider = new SupersetOfContextDecider(context);
      Iterator<ScopedIF> iter = tmObjects.iterator();
      ScopedIF obj;
      while (iter.hasNext()) {
        obj = iter.next();
        if (decider.test(obj)) {
          if (!invert) {
            filtered.add(obj);
          }
        } else {
          if (invert) {
            filtered.add(obj);
          }
        }
      } // while
    }

    return filtered;
  }  

  /**
   * INTERNAL: test if objects of input collection are a (Java)
   * instance of a specific class (OccurrenceIF, TopicIF, TopicMapIF,
   * AssociationIF, TopicNameIF, VariantIF, LocatorIF,
   * AssociationRoleIF).
   */
  private Collection filterIs(Collection tmObjects, String kind)
    throws NavigatorCompileException {

    // TODO: Make kind an int and get class from class array by kind index.
    
    // what are we filtering by?
    Class klass = null;
    if (kind.equalsIgnoreCase(CLASS_OCC)) {
      klass = OccurrenceIF.class;
    } else if (kind.equalsIgnoreCase(CLASS_TOPIC)) {
      klass = TopicIF.class;
    } else if (kind.equalsIgnoreCase(CLASS_TOPICMAP)) {
      klass = TopicMapIF.class;
    } else if (kind.equalsIgnoreCase(CLASS_ASSOC)) {
      klass = AssociationIF.class;
    } else if (kind.equalsIgnoreCase(CLASS_BASENAME)) {
      klass = TopicNameIF.class;
    } else if (kind.equalsIgnoreCase(CLASS_VARIANT)) {
      klass = VariantNameIF.class;
    } else if (kind.equalsIgnoreCase(CLASS_LOCATOR)) {
      klass = LocatorIF.class;
    } else if (kind.equalsIgnoreCase(CLASS_ROLE)) {
      klass = AssociationRoleIF.class;
    } else {
      throw new NavigatorCompileException("FilterTag got wrong value for the" +
                                          " kind attribute: '" + klass + "'");
    }

    // do the filtering
    if (tmObjects == null) {
      return Collections.EMPTY_SET;
    } else {
      Collection filtered = new HashSet(tmObjects.size());
      Iterator iter = tmObjects.iterator();
      while (iter.hasNext()) {
        Object obj = iter.next();
        if ((klass.isInstance(obj) && !invert)
            || (!klass.isInstance(obj) && invert)) {
          filtered.add(obj);
        }
      }
      return filtered;
    }
  }
  
  // -----------------------------------------------------------------
  // set methods for tag attributes
  // -----------------------------------------------------------------

  /**
   * Sets instanceOf string, which will first interpreted as an URI
   * for retrieving a topic by it's subject indicator. If this is without
   * success instanceOf will interpreted as a variable name.
   * <p>
   * Special string "{NONE}" will be interpreted to retrieve all untyped
   * instances.
   */
  public void setInstanceOf(String instanceOf) {
    this.instanceOf = instanceOf;
  }

  public void setDecider(String classname) {
    this.deciderClassName = classname;
  }

  public void setInScopeOf(String inScopeOfValue) {
    this.inScopeOfValue = inScopeOfValue;
  }
  
  public void setInvert(String invert) {
    if (invert.equalsIgnoreCase("true") ||
        invert.equalsIgnoreCase("yes") ) {
      this.invert = true;
    } else {
      this.invert = false;
    }
  }

  /**
   * Set the behaviour of the filter to choose a random element from
   * the given input collection.
   *
   * @since 1.3.2
   */
  public void setRandomElement(String randomElement) {
    if (randomElement.equalsIgnoreCase("true") ||
        randomElement.equalsIgnoreCase("yes") ) {
      this.randomElement = true;
    } else {
      this.randomElement = false;
    }
  }

  /**
   * Sets the kind of topic map objects that should be passed on.
   *
   * @param kind String which should contain one of the following
   *        values: topicmap | association | occurrence | topic |
   *                basename | variant | role | locator
   */
  public void setIs(String kind) {
    if (kind.equalsIgnoreCase(CLASS_TOPICMAP)
        || kind.equalsIgnoreCase(CLASS_ASSOC)
        || kind.equalsIgnoreCase(CLASS_OCC)
        || kind.equalsIgnoreCase(CLASS_TOPIC)
        || kind.equalsIgnoreCase(CLASS_LOCATOR)
        || kind.equalsIgnoreCase(CLASS_ROLE)
        || kind.equalsIgnoreCase(CLASS_BASENAME)
        || kind.equalsIgnoreCase(CLASS_VARIANT)) {
      this.isKind = kind;
    } else {
      throw new IllegalArgumentException("Invalid value '" + kind +
                                         "' in attribute 'is' " +
                                         " of tag 'filter'.");
    }
  }

  // ---------------------------------------------------------------
  // internal methods
  // ---------------------------------------------------------------
  
  public NavigatorDeciderIF getDeciderInstance(String classname)
    throws NavigatorRuntimeException {
    
    Object obj = null;
    try {
      // Create decider instance
      obj = contextTag.getNavigatorApplication().getInstanceOf(classname);
      // if instance of DeciderIF we need to wrap in NavigatorDeciderWrapper
      if (obj instanceof NavigatorDeciderIF) {
        return (NavigatorDeciderIF) obj;
      } else if (obj instanceof Predicate) {
        return new DeciderIFWrapper((Predicate)obj);
      }
      
    } catch (NavigatorRuntimeException e) {
      log.warn("Unable to retrieve instance of " + classname);
    }
    // We weren't able to create an instance so let's return null.
    return null;
  }
  
}
