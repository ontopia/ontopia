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

package net.ontopia.persistence.query.sql;


/**
 * INTERNAL: SQL optimizer that removes A = A and A != A expressions.
 */

public class EqualsSQLOptimizer extends BooleanSQLOptimizer {

  // + RULE 1: 'A == A'  =>  true
  // + RULE 2: 'A != A'  =>  false
  
  public SQLQuery optimize(SQLQuery query) {
    optimizeQuery(query);
    return query;    
  }

  protected int optimizeEquals(SQLEquals expr) {
    // RULE 1: 'A == A'  =>  true
    if (expr.getLeft().equals(expr.getRight())) {
      //! System.out.println("Optimizing out: " + expr);
      //! optimizeValue(expr.getLeft());
      //! optimizeValue(expr.getRight());
      return 1;

    } else {
      return 0;
    }
  }

  protected int optimizeNotEquals(SQLNotEquals expr) {
    // RULE 2: 'A != A'  =>  false
    if (expr.getLeft().equals(expr.getRight())) {
      //! System.out.println("Optimizing out: " + expr);
      return -1;

    } else {
      //! optimizeValue(expr.getLeft());
      //! optimizeValue(expr.getRight());
      return 0;
    }
  }

}
