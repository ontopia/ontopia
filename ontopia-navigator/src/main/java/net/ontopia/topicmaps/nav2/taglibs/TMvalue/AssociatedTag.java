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

import java.util.HashSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Predicate;
import javax.servlet.jsp.JspTagException;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TypedIF;
import net.ontopia.topicmaps.nav2.core.ContextManagerIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.taglibs.value.BaseScopedTag;

/**
 * INTERNAL: Value producing tag that finds all the topics associated
 * with this the topics in the input collection, subject to some
 * constraints.
 */
public class AssociatedTag extends BaseScopedTag {

  // tag attributes
  protected String startrole;
  protected String endrole;
  protected String assoctype;
  protected String totopic;
  protected boolean produceTopics = true;
  
  @Override
  public Collection process(Collection topics) throws JspTagException {
    if (topics == null || topics.isEmpty()) {
      return Collections.EMPTY_SET;
    } else{
      ContextManagerIF ctxtMgr = contextTag.getContextManager();
      Collection startroles  = getValue(ctxtMgr, startrole);
      Collection endroles    = getValue(ctxtMgr, endrole);
      Collection assoctypes  = getValue(ctxtMgr, assoctype);
      Collection totopics    = getValue(ctxtMgr, totopic);
    
      Collection associated = new HashSet();

      // setup scope filter for user context filtering
      Predicate scopeDecider = null;
      if (useUserContextFilter) {
        scopeDecider = getScopeDecider(SCOPE_ASSOCIATIONS);
      }

      try {
        for (Iterator it = topics.iterator(); it.hasNext(); ) {
          TopicIF start = (TopicIF) it.next();

          for (Iterator roles = start.getRoles().iterator(); roles.hasNext(); ) {
            AssociationRoleIF role = (AssociationRoleIF) roles.next();
            if (!isInstanceOf(role, startroles)) {
              continue;
            }

            AssociationIF assoc = role.getAssociation();
            if (!isInstanceOf(assoc, assoctypes)) {
              continue;
            }

            for (Iterator roles2 = assoc.getRoles().iterator(); roles2.hasNext();){
              AssociationRoleIF role2 = (AssociationRoleIF) roles2.next();
              if (role2.equals(role) ||
                  !isInstanceOf(role2, endroles)) {
                continue;
              }

              TopicIF currentPlayer = role2.getPlayer();
              if (totopics != null && !totopics.contains(currentPlayer)) {
                continue;
              }

              if (currentPlayer != null) {
                // add current player if within user context if specified
                if (scopeDecider == null || scopeDecider.test(currentPlayer)) {
                  if (produceTopics) {
                    associated.add(currentPlayer);
                  } else {
                    associated.add(assoc);
                  }
                }
              }
          
            } // for roles2
          } // for roles
        } // for it
      } catch (ClassCastException e) {
        throw new NavigatorRuntimeException("'associated' tag got a collection containing non-topic objects.", e);
      }    
      return associated;
    }
  }
  
  // -----------------------------------------------------------------
  // internal helpers
  // -----------------------------------------------------------------

  protected boolean isInstanceOf(TypedIF object, Collection types) {
    if (types == null) {
      return true;
    }

    return types.contains(object.getType());
  }

  protected Collection getValue(ContextManagerIF ctxtMgr, String var)
    throws NavigatorRuntimeException {
    if (var == null) {
      return null;
    }

    return ctxtMgr.getValue(var);
  }
  
  // -----------------------------------------------------------------
  // set methods for tag attributes
  // -----------------------------------------------------------------

  public void setStartrole(String startrole) {
    this.startrole = startrole;
  }

  public void setEndrole(String endrole) {
    this.endrole = endrole;
  }

  public void setType(String type) {
    this.assoctype = type;
  }

  public void setFrom(String from) {
    this.variableName = from;
  }

  public void setTo(String to) {
    this.totopic = to;
  }

  public void setProduce(String produce) {
    if ("topics".equals(produce)) {
      produceTopics = true;
    } else if ("associations".equals(produce)) {
      produceTopics = false;
    } else {
      throw new IllegalArgumentException("Invalid value for 'produce' attribute of element 'associated' tag: <" + produce + ">");
    }
      
  }
  
}
