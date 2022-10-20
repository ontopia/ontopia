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

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.BodyContent;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.nav2.core.ContextManagerIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.utils.CollectionMap;
import net.ontopia.utils.CollectionUtils;
import net.ontopia.topicmaps.nav2.core.ScopeSupportIF;
import net.ontopia.topicmaps.nav2.impl.basic.AssocInfoStorage;
import net.ontopia.topicmaps.nav2.impl.basic.AssocInfoStorageComparator;
import net.ontopia.topicmaps.nav2.utils.ScopeUtils;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Special Tag for iterating over a sorted collection of
 * triples (Association Type, Association Role Type, Association),
 * creating new content for each iteration.
 */
public class AssociationTypeLoopTag extends BodyTagSupport implements ScopeSupportIF {

  // initialization of logging facility
  private static final Logger log = LoggerFactory
    .getLogger(AssociationTypeLoopTag.class.getName());
  
  // members
  private ContextManagerIF ctxtMgr;
  private AssocInfoStorage[] assocStore; // collection we loop over
  private int index;                     // current index we are in the loop
  
  // tag attributes
  private String varNameTopic;
  private String varNameAssociations;
  private String varNameAT;
  private String varNameART;

  private boolean useUserContextFilter = false;
  
  /**
   * Process the start tag for this instance.
   */
  @Override
  public int doStartTag() throws JspTagException {

    ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);
    ctxtMgr = contextTag.getContextManager();

    // get collection which contains topic to work with
    Collection coll = null;
    if (varNameTopic != null) {
      coll = ctxtMgr.getValue(varNameTopic);
    } else {
      coll = ctxtMgr.getDefaultValue();
    }
    
    // establish new lexical scope for this loop
    ctxtMgr.pushScope();

    // do not proceed if no elements in collection at all
    if (coll.isEmpty()) {
      log.debug("Cannot retrieve topic from empty collection in " +
                (varNameTopic != null ? varNameTopic : "<default>") );
      return SKIP_BODY;
    }

    // get first element in collection which should be a topic instance
    TopicIF topic = null;
    try {
      topic = (TopicIF) CollectionUtils.getFirstElement(coll);
    } catch (Exception e) {
      throw new NavigatorRuntimeException("AssociationTypeLoopTag: Could not retrieve " +
                                          "topic from " + (varNameTopic != null ?
                                                           varNameTopic : "<default>"), e);
    }

    // get scope decider based on the user context filer
    // FIXME: Should avoid creating a new decider for every loop.
    Predicate<ScopedIF> scopeDecider = null;
    if (useUserContextFilter) {
      scopeDecider = ScopeUtils.getScopeDecider(pageContext, contextTag,
                                                SCOPE_ASSOCIATIONS);
    }
    
    // create list of ordered associations for this topic
    assocStore = createOrderedAssociationTypes(topic, scopeDecider);

    if (assocStore == null || assocStore.length == 0) {
      log.info("No associations available for topic " + (varNameTopic != null ?
                                                         varNameTopic : "<default>"));
      return SKIP_BODY;
    }
    
    // set first element of collection in the beginning
    setVariableValues(assocStore[0]);
    index = 1;

    return EVAL_BODY_BUFFERED;
  }
  
  /**
   * Actions after some body has been evaluated.
   */
  @Override
  public int doAfterBody() throws JspTagException {
    // put out the evaluated body
    BodyContent body = getBodyContent();
    JspWriter out = null;
    try {
      out = body.getEnclosingWriter();
      out.print(body.getString());
      body.clearBody();
    } catch(IOException ioe) {
      throw new NavigatorRuntimeException("Error in AssociationTypeLoopTag.", ioe);
    }

    // test if we have to repeat the body 
    if (index < assocStore.length) {
      // set to next value in list
      setVariableValues(assocStore[index]);
      index++;
      return EVAL_BODY_AGAIN;
    } else {
      return SKIP_BODY;
    }
    
  }
  
  /**
   * Process the end tag.
   */
  @Override
  public int doEndTag() throws JspException {    
    // establish old lexical scope, back to outside of the loop
    ctxtMgr.popScope();

    // reset members
    ctxtMgr = null;
    assocStore = null;
    // index = 0;
    
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
  // set methods for tag attributes
  // -----------------------------------------------------------------
  
  public void setName(String varNameTopic) {
    this.varNameTopic = varNameTopic;
  }

  public void setSetAssociations(String varNameAssociations) {
    this.varNameAssociations = varNameAssociations;
  }
  
  public void setSetAT(String varNameAT) {
    this.varNameAT = varNameAT;
  }

  public void setSetART(String varNameART) {
    this.varNameART = varNameART;
  }

  /**
   * INTERNAL: sets up if the tag should use the context filter which is
   * implict contained in the user session. Default behaviour is to
   * not use the user context filter. Allowed values are:
   * <ul>
   *  <li>off</li>
   *  <li>user</li>
   * </ul>
   */
  public void setContextFilter(String contextFilter) {
    if (contextFilter.indexOf("off") != -1) {
      this.useUserContextFilter = false;
    }
    if (contextFilter.indexOf("user") != -1) {
      this.useUserContextFilter = true;
    }
  }
  
  // -----------------------------------------------------------------
  // helper methods
  // -----------------------------------------------------------------

  /**
   * INTERNAL: set variable values with help of the context manager
   */
  private void setVariableValues(AssocInfoStorage ais) {
    // set variables in current lexical scope
    if (varNameAssociations != null) {
      ctxtMgr.setValue(varNameAssociations, ais.getAssociations());
    }
    if (varNameAT != null) {
      ctxtMgr.setValue(varNameAT, ais.getType());
    }
    if (varNameART != null) {
      if (ais.getRoleType() != null) {
        ctxtMgr.setValue(varNameART, ais.getRoleType());
      } else {
        ctxtMgr.setValue(varNameART, Collections.EMPTY_LIST);
      }
    }
  }

  /**
   * INTERNAL: create array of triples (AssocInfoStorage objects) and
   * order them by the name of the association type (in scope of the
   * association role type).
   */
  private AssocInfoStorage[] createOrderedAssociationTypes(TopicIF topic, Predicate<ScopedIF> _scopeDec) {
    // contains AssocInfoStorage objects in the first instance without
    // associations assigned
    Set assocTypeStore = new HashSet();
    // contains as key: AssocInfoStorage object and value: associations 
    CollectionMap assocMap = new CollectionMap();
    Iterator it = topic.getRoles().iterator();
    // get all association this topic is involved as a role player
    while (it.hasNext()) {
      AssociationRoleIF assocRole = (AssociationRoleIF) it.next();
      AssociationIF assoc = assocRole.getAssociation();
      if (_scopeDec != null && !_scopeDec.test(assoc)) {
        continue;
      }
        
      TopicIF assocType = assoc.getType();
      TopicIF assocRoleType = assocRole.getType();
      AssocInfoStorage ais = new AssocInfoStorage(assocType, assocRoleType);
      assocTypeStore.add(ais);
      assocMap.add(ais, assoc);
    }

    // order
    AssocInfoStorageComparator assocStoreComparator = new AssocInfoStorageComparator();
    AssocInfoStorage[] assocList = new AssocInfoStorage[assocTypeStore.size()];
    assocTypeStore.toArray(assocList);
    Arrays.sort( assocList, assocStoreComparator);
    
    // assign associations 
    for (int i=0; i < assocList.length; i++) {
      AssocInfoStorage ais = assocList[i];
      ais.setAssociations((Collection) assocMap.get(ais));
    }

    return assocList;
  }
  
}
