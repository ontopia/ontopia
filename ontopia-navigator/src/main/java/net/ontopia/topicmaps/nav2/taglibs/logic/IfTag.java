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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import net.ontopia.topicmaps.nav2.core.NavigatorPageIF;
import net.ontopia.topicmaps.nav2.core.NavigatorConfigurationIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.core.NavigatorDeciderIF;
import net.ontopia.topicmaps.nav2.core.ContextManagerIF;
import net.ontopia.topicmaps.nav2.core.ValueProducingTagIF;
import net.ontopia.topicmaps.nav2.core.ValueAcceptingTagIF;
import net.ontopia.topicmaps.nav2.impl.basic.DefaultIfDecider;
import net.ontopia.topicmaps.nav2.impl.basic.DeciderIFWrapper;

import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Logic Tag for iterating over each object in a collection,
 * creating new content for each iteration.
 */
public class IfTag extends TagSupport
  implements ValueProducingTagIF, ValueAcceptingTagIF {

  // initialization of logging facility
  private static final Logger log = LoggerFactory.getLogger(IfTag.class.getName());

  // members
  private NavigatorPageIF contextTag;
  private Collection collection; // to test on
  private List inputs; // collections received from below
  private NavigatorDeciderIF decider;
  
  // tag attributes
  private String deciderClassName;
  private String collVariableName;
  private String equalsVariableName;
  private int    lessThanNumber    = -1;
  private int    greaterThanNumber = -1;
  private int    equalsSize        = -1;

  /**
   * Process the start tag for this instance.
   */
  @Override
  public int doStartTag() throws JspTagException {

    this.contextTag = FrameworkUtils.getContextTag(pageContext);
    ContextManagerIF ctxtMgr = contextTag.getContextManager();

    // get Collection to test matching on 
    if (collVariableName != null) {
      // Policy: treat none existing collections like empty
      // collections (see bug 413).
      collection = ctxtMgr.getValue(collVariableName, Collections.EMPTY_LIST);
    } else {
      collection = ctxtMgr.getDefaultValue();
    }
    
    // establish new lexical scope for this condition...
    ctxtMgr.pushScope();
    // ...and set up the test collection as default value
    ctxtMgr.setDefaultValue( collection );
    // ...then get ready to receive inputs from descendants
    inputs = new ArrayList();
    
    // use the decider specified by tag attribute
    if (deciderClassName != null)
      this.decider = getDeciderInstance(deciderClassName);
    if (decider == null) {
      // get name of default decider class
      NavigatorConfigurationIF navConf =
        contextTag.getNavigatorApplication().getConfiguration();
      String dcn = navConf.getProperty(NavigatorConfigurationIF.DEF_DECIDER,
                                       NavigatorConfigurationIF.DEFVAL_DECIDER);
      if (dcn.equals(NavigatorConfigurationIF.DEFVAL_DECIDER))
        // TODO: Should replace with generic attributes support in NavigatorDeciderIF
        decider = new DefaultIfDecider(equalsVariableName, equalsSize,
                                       lessThanNumber, greaterThanNumber);
      else
        decider = getDeciderInstance(dcn);
    }

    return EVAL_BODY_INCLUDE;
  }
  
  /**
   * Process the end tag.
   */
  @Override
  public int doEndTag() throws JspException {
    // establish old lexical scope, back to outside of the condition
    contextTag.getContextManager().popScope();

    // pass received collections further upwards
    ValueAcceptingTagIF acceptingTag = (ValueAcceptingTagIF)
      findAncestorWithClass(this, ValueAcceptingTagIF.class);
    if (acceptingTag != null)
      for (int ix = 0; ix < inputs.size(); ix++)
        acceptingTag.accept((Collection) inputs.get(ix));
    
    // Reset members
    contextTag = null;
    collection = null;
    decider = null;
    
    return EVAL_PAGE;
  }

  /**
   * Resets the state of the Tag.
   */
  @Override
  public void release() {
    // overwrite default behaviour
    // do not set parent to null!!!
  }
  
  /**
   * If all conditions are matched deliver true, otherwise false.
   */
  protected boolean matchCondition() {
    return decider.ok(contextTag, collection);
  }
  
  // -----------------------------------------------------------------
  // Implementation of ValueProducingTagIF
  // -----------------------------------------------------------------

  // this probably isn't needed any more. keeping it so that we can
  // continue to consider (the really rather meaningless)
  // ValueProducingTagIF as a marker interface.  
  @Override
  public Collection process(Collection input) {
    return input;
  }

  // -----------------------------------------------------------------
  // Implementation of ValueAcceptingTagIF
  // -----------------------------------------------------------------

  // because of bug #1476 it's not enough for us simply to receive a
  // collection, and then pass it on. we may receive more than one
  // collection from our descendants, and we don't know how to combine
  // them, since we don't know our parent tag. the solution is to keep
  // them all, and then pass them upwards one by one.
  @Override
  public void accept(Collection inputCollection) {
    inputs.add(inputCollection);
  }
  
  // -----------------------------------------------------------------
  // Set methods for all attributes
  // -----------------------------------------------------------------
  
  public void setName(String collectionName) {
    this.collVariableName = collectionName;
  }

  public void setDecider(String classname) throws NavigatorRuntimeException {
    deciderClassName = classname;
  }

  public void setEquals(String equalsVariableName) {
    this.equalsVariableName = equalsVariableName;
  }

  /**
   * Sets Lower Bound test value against Collection size.
   *
   * @param lessThanString try to convert to valid integer,
   * otherwise fallback to default value (-1).
   */
  public void setLessThan(String lessThanString) {
    try {
      this.lessThanNumber = Integer.parseInt( lessThanString );
    } catch (NumberFormatException e) {
      log.warn("Reset invalid lessThan value to '-1' was '" +
               lessThanString + "'.");
      this.lessThanNumber = -1;
    }
  }

  /**
   * Sets Upper Bound test value against Collection size.
   *
   * @param greaterThanString try to convert to valid integer,
   * otherwise fallback to default value (-1).
   */
  public void setGreaterThan(String greaterThanString) {
    try {
      this.greaterThanNumber = Integer.parseInt( greaterThanString );
    } catch (NumberFormatException e) {
      log.warn("Reset invalid greaterThan value to '-1' was '" +
               greaterThanString + "'.");
      this.greaterThanNumber = -1;
    }
  }

  /**
   * Sets exact equals size test value against Collection size.
   *
   * @param equalsSizeString try to convert to valid integer,
   * otherwise fallback to default value (-1).
   */
  public void setSizeEquals(String equalsSizeString) {
    try {
      this.equalsSize = Integer.parseInt( equalsSizeString );
    } catch (NumberFormatException e) {
      log.warn("Reset invalid equalsSize value to '-1' was '" +
               equalsSizeString + "'.");
      this.equalsSize = -1;
    }
  }

  // ---------------------------------------------------------------
  // Internal method(s)
  // ---------------------------------------------------------------
  
  protected NavigatorDeciderIF getDeciderInstance(String classname)
    throws NavigatorRuntimeException {

    Object obj = null;
    try {
      // Create decider instance
      obj = contextTag.getNavigatorApplication().getInstanceOf(classname);
      // if instance of DeciderIF we need to wrap in NavigatorDeciderWrapper
      if (obj instanceof NavigatorDeciderIF)
        return (NavigatorDeciderIF) obj;
      else if (obj instanceof Predicate)
        return new DeciderIFWrapper((Predicate)obj);
      
    } catch (NavigatorRuntimeException e) {
      log.warn("Unable to retrieve instance of " + classname);
    }
    // We weren't able to create an instance so let's return null.
    return null;
  }
  
}
