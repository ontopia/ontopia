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

package net.ontopia.topicmaps.query.impl.basic;

import java.util.HashMap;
import java.util.Map;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.parser.ModuleIF;
import net.ontopia.topicmaps.query.parser.ParsedRule;
import net.ontopia.topicmaps.query.parser.PredicateFactoryIF;
import net.ontopia.topicmaps.query.parser.PredicateIF;

/**
 * INTERNAL: The basic predicate factory implementation.
 */
public class PredicateFactory implements PredicateFactoryIF {

  protected Map predicatesById;
  protected TopicMapIF topicmap;
  protected LocatorIF base;

  public PredicateFactory(TopicMapIF topicmap, LocatorIF base) {
    this.topicmap = topicmap;
    this.base = base;
    this.predicatesById = new HashMap();
    
    // register built-in predicates    
    predicatesById.put("instance-of", new InstanceOfPredicate(topicmap));
    predicatesById.put("direct-instance-of", new DirectInstanceOfPredicate(topicmap));
    predicatesById.put("/=", new NotEqualsPredicate());    
    predicatesById.put("=", new EqualsPredicate());    
    predicatesById.put("<", new LessThanPredicate());    
    predicatesById.put(">", new GreaterThanPredicate());    
    predicatesById.put("<=", new LessThanEqualsPredicate());    
    predicatesById.put(">=", new GreaterThanEqualsPredicate());    
    predicatesById.put("role-player", new RolePlayerPredicate(topicmap));
    predicatesById.put("type", new TypePredicate(topicmap));
    predicatesById.put("scope", new ScopePredicate(topicmap));
    predicatesById.put("value", new ValuePredicate(topicmap));
    predicatesById.put("value-like", new ValueLikePredicate(topicmap));
    predicatesById.put("topic-name", new TopicNamePredicate(topicmap));
    predicatesById.put("reifies", new ReifiesPredicate(topicmap));
    predicatesById.put("occurrence", new OccurrencePredicate(topicmap));
    predicatesById.put("association", new AssociationPredicate(topicmap));
    predicatesById.put("association-role", new AssociationRolePredicate(topicmap));
    predicatesById.put("topicmap", new TopicMapPredicate(topicmap));
    predicatesById.put("resource", new ResourcePredicate(topicmap));
    predicatesById.put("topic", new TopicPredicate(topicmap));
    predicatesById.put("variant", new VariantPredicate(topicmap));
    predicatesById.put("item-identifier", new ItemIdentifierPredicate(topicmap, "item-identifier"));
    predicatesById.put("source-locator", new ItemIdentifierPredicate(topicmap, "source-locator")); // deprecated
    predicatesById.put("subject-identifier", new SubjectIdentifierPredicate(topicmap));
    predicatesById.put("subject-locator", new SubjectLocatorPredicate(topicmap));
    predicatesById.put("base-locator", new BaseLocatorPredicate(topicmap));
    predicatesById.put("object-id", new ObjectIdPredicate(topicmap));
    predicatesById.put("datatype", new DatatypePredicate(topicmap));
    predicatesById.put("coalesce", new CoalescePredicate());
    //predicatesById.put("name", new NamePredicate());
  }
  
  public PredicateIF createPredicate(String name) {
    return (PredicateIF) predicatesById.get(name);
  }

  public PredicateIF createPredicate(ParsedRule rule) {
    return new RulePredicate(rule);
  }

  public PredicateIF createPredicate(TopicIF type, boolean assoc) {
    if (assoc) 
      return new DynamicAssociationPredicate(topicmap, base, type);
    else
      return new DynamicOccurrencePredicate(topicmap, base, type);
  }
  
  public ModuleIF createModule(String uri) {
    if (uri.equals(ExperimentalModule.MODULE_URI))
      return new ExperimentalModule();
    else if (uri.equals(StringModule.MODULE_URI))
      return new StringModule();
    else if (uri.startsWith(JavaModule.MODULE_PREFIX))
      return new JavaModule(topicmap, uri);
    else if (uri.equals(NumbersModule.MODULE_URI))
      return new NumbersModule();
    else
      return null;
  }

  public boolean isBuiltInPredicate(String name) {
    return predicatesById.containsKey(name);
  }
  
  // --- The experimental module

  static class ExperimentalModule implements ModuleIF {
    private static final String MODULE_URI = "http://psi.ontopia.net/tolog/experimental/";
    
    public PredicateIF getPredicate(String name) {
      if (name.equals("in"))
        return new InPredicate();
      else if (name.equals("gt"))
        return new GreaterThanPredicate();
      else if (name.equals("lt"))
        return new LessThanPredicate();
      else if (name.equals("gteq"))
        return new GreaterThanEqualsPredicate();
      else if (name.equals("lteq"))
        return new LessThanEqualsPredicate();
      else if (name.equals("name"))
        return new NamePredicate();
      else
        return null;
    }
  }

}
