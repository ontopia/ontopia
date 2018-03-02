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

import java.util.HashMap;
import java.util.Map;

import net.ontopia.topicmaps.query.impl.basic.BasicPredicateIF;
import net.ontopia.topicmaps.query.parser.ModuleIF;
import net.ontopia.topicmaps.query.parser.PredicateIF;

public class JDOBasicModule implements ModuleIF {
  
  protected ModuleIF module;
  protected Map predicates = new HashMap();
  
  public JDOBasicModule(ModuleIF module) {
    this.module = module;
  }
  
  @Override
  public PredicateIF getPredicate(String name) {
    PredicateIF pred = (PredicateIF) predicates.get(name);
    if (pred == null) {
      pred = module.getPredicate(name);
      if (pred != null) {
        pred = new JDOBasicPredicate((BasicPredicateIF)pred);
        predicates.put(name, pred);
      }
    }
    return pred;
  }

}
