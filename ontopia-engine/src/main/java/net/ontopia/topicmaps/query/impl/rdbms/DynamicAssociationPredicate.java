/*
 * #!
 * Ontopia Engine
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

package net.ontopia.topicmaps.query.impl.rdbms;

import java.util.List;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.persistence.query.jdo.JDOContains;
import net.ontopia.persistence.query.jdo.JDOEquals;
import net.ontopia.persistence.query.jdo.JDOField;
import net.ontopia.persistence.query.jdo.JDONotEquals;
import net.ontopia.persistence.query.jdo.JDONull;
import net.ontopia.persistence.query.jdo.JDOObject;
import net.ontopia.persistence.query.jdo.JDOValueIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.impl.rdbms.Association;
import net.ontopia.topicmaps.impl.rdbms.AssociationRole;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.parser.Pair;

/**
 * INTERNAL: Implements dynamic association predicates.
 */
public class DynamicAssociationPredicate
  extends net.ontopia.topicmaps.query.impl.basic.DynamicAssociationPredicate
  implements JDOPredicateIF {

  public DynamicAssociationPredicate(TopicMapIF topicmap, LocatorIF base, TopicIF type) {
    super(topicmap, base, type);
  }

  // --- JDOPredicateIF implementation

  @Override
  public boolean isRecursive() {
    return false;
  }

  @Override
  public void prescan(QueryBuilder builder, List arguments) {
    // no-op
  }
  
  @Override
  public boolean buildQuery(QueryBuilder builder, List expressions, List arguments)
    throws InvalidQueryException {
    
    // Interpret arguments
    Object[] args = arguments.toArray();

    // TOLOG: assoc-type ( PLAYER1 : RTYPE1, ..., PLAYERn : RTYPEn )

    JDOValueIF jv_assoc = builder.createJDOVariable("A", Association.class);
    JDOValueIF jv_atype = builder.createJDOValue(type);

    // JDOQL: A.type = AT
    expressions.add(new JDOEquals(new JDOField(jv_assoc, "type"), jv_atype));

    // if variable: filter out nulls
    if (jv_atype.getType() == JDOValueIF.VARIABLE) {
      expressions.add(new JDONotEquals(jv_atype, new JDONull()));
    }

    // JDOQL: A.topicmap = TOPICMAP
    expressions.add(new JDOEquals(new JDOField(jv_assoc, "topicmap"),
                                  new JDOObject(topicmap)));

    JDOValueIF[] jv_roles = new JDOValueIF[args.length];

    for (int i=0; i < args.length; i++) {
      Object arg = args[i];

      Pair pair = (Pair)arg;
      Object first = pair.getFirst();
      Object second = pair.getSecond();

      JDOValueIF jv_player = builder.createJDOValue(first);
      JDOValueIF jv_rtype = builder.createJDOValue(second);

      JDOValueIF jv_role = builder.createJDOVariable("R", AssociationRole.class);
      jv_roles[i] = jv_role;

      // JDOQL: A.roles.contains(R) & R.type = RT & R.player = RP
      expressions.add(new JDOContains(new JDOField(jv_assoc, "roles"), jv_role));
      expressions.add(new JDOEquals(new JDOField(jv_role, "player"), jv_player));
      expressions.add(new JDOEquals(new JDOField(jv_role, "type"), jv_rtype));

      // if variable: filter out nulls
      if (jv_rtype.getType() == JDOValueIF.VARIABLE) {
        expressions.add(new JDONotEquals(jv_rtype, new JDONull()));
      }
      // if variable: filter out nulls
      if (jv_player.getType() == JDOValueIF.VARIABLE) {
        expressions.add(new JDONotEquals(jv_player, new JDONull()));
      }
    }

    // TODO: Append expression that makes sure that there are no
    // duplicates among the R1, ..., Rn tables.
    if (jv_roles.length == 2) {
      expressions.add(new JDONotEquals(jv_roles[0], jv_roles[1]));
    }
    else if (jv_roles.length == 3) {
      expressions.add(new JDONotEquals(jv_roles[0], jv_roles[1]));
      expressions.add(new JDONotEquals(jv_roles[1], jv_roles[2]));
      expressions.add(new JDONotEquals(jv_roles[0], jv_roles[2]));
    } else {
      // FIXME: add support for more than two and three roles
    }

    return true;

    // TODO: All association predicates get it's topic map bound by
    // the fact that the association predicate is a fixed topic. If we
    // in the future can let the association type be a varibale we
    // need to make sure that the topic map is bound properly.
    
    // FIXME: The AssociationIF cannot be a tolog variable at this
    // time, but if it can we must make sure that the topic map is
    // bound.
    
    // Need to make sure that just get results for the current topic map
    //! if (builder.getAttribute("TOPICMAP") == null ) {
    //!   expressions.add(new JDOEquals(new JDOField(jv_atype, "topicmap"),
    //!                                new JDOObject(topicmap)));
    //!   builder.setAttribute("TOPICMAP", topicmap);
    //! }
  }

  
}
