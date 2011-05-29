
package net.ontopia.topicmaps.nav2.taglibs.TMvalue;

import java.util.HashSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import javax.servlet.jsp.JspTagException;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.utils.DeciderIF;

import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.taglibs.value.BaseScopedTag;

/**
 * INTERNAL: Value Producing Tag for finding all the associations
 * of all the topics or association roles in a collection.
 */
public class AssociationsTag extends BaseScopedTag {
  
  public Collection process(Collection topics) throws JspTagException {
    // find all associations (TopicIF objects) of all topics in collection
    // avoid duplicate entries therefore use a HashSet
    if (topics == null || topics.isEmpty())
      return Collections.EMPTY_SET;
    else {
      HashSet associations = new HashSet();
      Iterator iter = topics.iterator();
      TopicIF topic = null;
      AssociationRoleIF assocRole = null;
      AssociationIF currentAssoc = null;
      Object obj = null;
      Iterator iterRoles = null;
      DeciderIF scopeDecider = null;

      // setup scope filter for user context filtering
      if (useUserContextFilter)
        scopeDecider = getScopeDecider(SCOPE_ASSOCIATIONS);
      
      while (iter.hasNext()) {
        obj = iter.next();
        // --- TopicIF
        try {
          topic = (TopicIF) obj;
          Collection _roles = topic.getRoles();
          if (!_roles.isEmpty()) {
            iterRoles = _roles.iterator();
            while (iterRoles.hasNext()) {
              assocRole = (AssociationRoleIF) iterRoles.next();
              currentAssoc = assocRole.getAssociation();
              // add current assoc if within user context if specified
              if (scopeDecider == null || scopeDecider.ok(currentAssoc))
                associations.add( currentAssoc );
            } // while iterRoles
          }
        } catch (ClassCastException e) {
          // --- AssociationRoleIF
          if (obj instanceof AssociationRoleIF) {
            assocRole = (AssociationRoleIF) obj;
            currentAssoc = assocRole.getAssociation();
            // add current assoc if within user context if specified
            if (scopeDecider != null) {
              if (scopeDecider.ok(currentAssoc))
                associations.add(currentAssoc);
            } else
              associations.add(currentAssoc);
          }
          // --- otherwise
          else {
            String msg = "AssociationsTag expected to get a input collection of " +
              "topic or association role instances, " +
              "but got instance of class " + obj.getClass().getName();
            throw new NavigatorRuntimeException(msg);
          }
        }
      } // while iter 
      return associations;
    }
  }

}
