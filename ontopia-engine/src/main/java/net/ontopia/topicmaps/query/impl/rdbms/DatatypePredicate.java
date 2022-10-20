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

import net.ontopia.persistence.query.jdo.JDOBoolean;
import net.ontopia.persistence.query.jdo.JDOEquals;
import net.ontopia.persistence.query.jdo.JDOField;
import net.ontopia.persistence.query.jdo.JDONotEquals;
import net.ontopia.persistence.query.jdo.JDONull;
import net.ontopia.persistence.query.jdo.JDOObject;
import net.ontopia.persistence.query.jdo.JDOValueIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * INTERNAL: Implements the 'datatype(occ|var, datatype-uri)' predicate.
 */
public class DatatypePredicate
  extends net.ontopia.topicmaps.query.impl.basic.DatatypePredicate
  implements JDOPredicateIF {

  public DatatypePredicate(TopicMapIF topicmap) {
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
    
    // Interpret arguments
    Object[] args = arguments.toArray();
    
    // TOLOG: datatype(OBJECT, DATATYPE)
    if (args[0] instanceof VariantNameIF && args[1] instanceof String) {

      // Do direct predicate evaluation
      if (args[1].equals(((VariantNameIF)args[0]).getDataType().getAddress())) {
        expressions.add(JDOBoolean.TRUE);
      } else {
        expressions.add(JDOBoolean.FALSE);
      }

    } else if (args[0] instanceof OccurrenceIF && args[1] instanceof String) {

      // Do direct predicate evaluation
      if (args[1].equals(((OccurrenceIF)args[0]).getDataType().getAddress())) {
        expressions.add(JDOBoolean.TRUE);
      } else {
        expressions.add(JDOBoolean.FALSE);
      }
            
    } else {                  
            
      JDOValueIF jv_object = builder.createJDOValue(args[0]);
      JDOValueIF jv_datatype = builder.createJDOValue(args[1]);
            
      // JDOQL: O.datatype = T
      expressions.add(new JDOEquals(new JDOField(jv_object, "datatype", "address"), jv_datatype));

      // if variable: filter out nulls
      if (jv_datatype.getType() == JDOValueIF.VARIABLE) {
        expressions.add(new JDONotEquals(jv_datatype, new JDONull()));
      }
            
      // JDOQL: O.topicmap = TOPICMAP
      expressions.add(new JDOEquals(new JDOField(jv_object, "topicmap"),
                                    new JDOObject(topicmap)));
    }
          
    return true;
  }
  
}
