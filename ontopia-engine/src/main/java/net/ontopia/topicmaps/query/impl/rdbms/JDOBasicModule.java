
// $Id: JDOBasicModule.java,v 1.3 2006/06/01 08:21:20 grove Exp $

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
