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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.ontopia.persistence.query.jdo.JDOBoolean;
import net.ontopia.persistence.query.jdo.JDOCollection;
import net.ontopia.persistence.query.jdo.JDOContains;
import net.ontopia.persistence.query.jdo.JDOEquals;
import net.ontopia.persistence.query.jdo.JDOField;
import net.ontopia.persistence.query.jdo.JDOObject;
import net.ontopia.persistence.query.jdo.JDOValueIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.impl.rdbms.Topic;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.utils.TypeHierarchyUtils;
import net.ontopia.utils.CompactHashSet;

/**
 * INTERNAL: Implements the 'instance-of' predicate.
 */
public class InstanceOfPredicate
  extends net.ontopia.topicmaps.query.impl.basic.InstanceOfPredicate
  implements JDOPredicateIF {

  public InstanceOfPredicate(TopicMapIF topicmap) {
    super(topicmap);
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

    // TODO: 'instance-of' does not yet support JDO expressions.

    // Interpret arguments
    Object[] args = arguments.toArray();
    
    if (!(args[0] instanceof TopicIF || args[1] instanceof TopicIF)) {
      // instance-of(<any>, <any>)

      // NOTE: We cannot yet process this query if neither of the arguments are topics.
      return false;
            
    } else if (args[0] instanceof TopicIF && args[1] instanceof TopicIF) {
      // instance-of(topic, topic)

      TopicIF instance = (TopicIF)args[0];
      TopicIF type = (TopicIF)args[1];

      // Get all the instance types
      Collection itypes = getSuperclasses(instance.getTypes());

      // Get all the type types
      TypeHierarchyUtils tu = new TypeHierarchyUtils();
      Collection ttypes = new CompactHashSet(tu.getSubclasses(type));
      ttypes.add(type);
        
      // TODO: this code can be optimized
        
      // Check to see if there is an overlap
      Iterator iter = itypes.iterator();
      boolean overlap = false;
      while (iter.hasNext()) {
        if (ttypes.contains(iter.next())) {
          overlap = true;
        }          
      }
      if (!overlap) {
        expressions.add(JDOBoolean.FALSE);
      }
        
    } else if (args[0] instanceof TopicIF) {
      // instance-of(topic, <any>)

      TopicIF instance = (TopicIF)args[0];
      JDOValueIF jv_type = builder.createJDOValue(args[1]);

      // Get all the supertypes [including the direct types]
      Collection types = getSuperclasses(instance.getTypes());

      if (types.isEmpty()) {
        expressions.add(JDOBoolean.FALSE);
          
      } else {          
        // JDOQL: INSTANCE_TYPES.containsAll(TYPES)
        expressions.add(new JDOContains(new JDOCollection(types, Topic.class), jv_type));
      }

      // JDOQL: INSTANCE.topicmap = TOPICMAP
      expressions.add(new JDOEquals(new JDOField(jv_type, "topicmap"),
                                    new JDOObject(topicmap)));
        
    } else if (args[1] instanceof TopicIF) {
      // instance-of(<any>, topic)

      JDOValueIF jv_instance = builder.createJDOValue(args[0]);        
      TopicIF type = (TopicIF)args[1];
        
      // Get all the subtypes
      TypeHierarchyUtils tu = new TypeHierarchyUtils();
      Collection types = tu.getSubclasses(type);

      if (types.isEmpty()) {
        // JDOQL: INSTANCE.types.contains(TYPE)
        JDOValueIF jv_type = builder.createJDOValue(type);
        expressions.add(new JDOContains(new JDOField(jv_instance, "types"), jv_type));
          
      } else {
        // JDOQL: TYPES.containsAll(INSTANCE_TYPES)
        types = new CompactHashSet(types);
        types.add(type);
          
        // JDOQL: INSTANCE_TYPES.containsAll(TYPES)
        // Note: that this is really a M:M comparison
        
        //! // With set pooling
        //! JDOVariable jv_types_var = builder.createJDOVariable("TYPES", TopicSet.class);
        //! JDOVariable jv_type_var = builder.createJDOVariable("TYPE", Topic.class);
        //! expressions.add(new JDOAnd(new JDOEquals(jv_types_var, new JDOField(jv_instance, "types")),
        //!                new JDOContains(jv_types_var, jv_type_var),
        //!                new JDOContains(new JDOCollection(types, Topic.class), jv_type_var)));
        
        // Without set pooling
        expressions.add(new JDOContains(new JDOCollection(types, Topic.class),
                                        new JDOField(jv_instance, "types")));

      }
      // JDOQL: INSTANCE.topicmap = TOPICMAP
      expressions.add(new JDOEquals(new JDOField(jv_instance, "topicmap"),
                                    new JDOObject(topicmap)));
    }
    
    return true;
    
  }

  protected Collection getSuperclasses(Collection types) {
    if (types.isEmpty()) {
      return Collections.EMPTY_SET;
    } else {
      TypeHierarchyUtils tu = new TypeHierarchyUtils();
      Collection result = new CompactHashSet(types);
      Iterator iter = types.iterator();
      while (iter.hasNext()) {
        result.addAll(tu.getSuperclasses((TopicIF)iter.next()));
      }
      return result;
    }
  }
  
}





