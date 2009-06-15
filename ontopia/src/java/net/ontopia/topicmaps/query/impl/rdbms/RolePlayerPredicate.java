
// $Id: RolePlayerPredicate.java,v 1.11 2006/04/27 16:03:12 grove Exp $

package net.ontopia.topicmaps.query.impl.rdbms;

import java.util.List;

import net.ontopia.persistence.query.jdo.JDOBoolean;
import net.ontopia.persistence.query.jdo.JDOEquals;
import net.ontopia.persistence.query.jdo.JDOField;
import net.ontopia.persistence.query.jdo.JDONotEquals;
import net.ontopia.persistence.query.jdo.JDONull;
import net.ontopia.persistence.query.jdo.JDOObject;
import net.ontopia.persistence.query.jdo.JDOValueIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * INTERNAL: Implements the 'role-player(role, player)' predicate.
 */
public class RolePlayerPredicate
  extends net.ontopia.topicmaps.query.impl.basic.RolePlayerPredicate
  implements JDOPredicateIF {

  public RolePlayerPredicate(TopicMapIF topicmap) {
    super(topicmap);
  }

  // --- JDOPredicateIF implementation

  public boolean isRecursive() {
    return false;
  }

  public void prescan(QueryBuilder builder, List arguments) {
  }

  public boolean buildQuery(QueryBuilder builder, List expressions, List arguments)
    throws InvalidQueryException {
    
    // Interpret arguments
    Object[] args = arguments.toArray();
    
    if (args[0] instanceof AssociationRoleIF && args[1] instanceof TopicIF) {

      // Do direct predicate evaluation
      AssociationRoleIF role = (AssociationRoleIF)args[0];

      if (args[1].equals(role.getPlayer()))
        expressions.add(JDOBoolean.TRUE);
      else
        expressions.add(JDOBoolean.FALSE);

    } else {                  

      JDOValueIF jv_role = builder.createJDOValue(args[0]);
      JDOValueIF jv_player = builder.createJDOValue(args[1]);
        
      // JDOQL: R.player = PLAYER
      expressions.add(new JDOEquals(new JDOField(jv_role, "player"), jv_player));

      // if variable: filter out nulls
      if (jv_player.getType() == JDOValueIF.VARIABLE)
        expressions.add(new JDONotEquals(jv_player, new JDONull()));
        
      // JDOQL: R.topicmap = TOPICMAP
      expressions.add(new JDOEquals(new JDOField(jv_role, "topicmap"),
                                    new JDOObject(topicmap)));
    }
    
    return true;
  }
  
}
