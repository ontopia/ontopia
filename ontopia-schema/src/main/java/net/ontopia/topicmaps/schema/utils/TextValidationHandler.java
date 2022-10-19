/*
 * #!
 * Ontopia OSL Schema
 * #-
 * Copyright (C) 2001 - 2014 The Ontopia Project
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

package net.ontopia.topicmaps.schema.utils;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Iterator;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.utils.TopicStringifiers;
import net.ontopia.topicmaps.schema.core.ValidationHandlerIF;
import net.ontopia.topicmaps.schema.core.ConstraintIF;
import net.ontopia.topicmaps.schema.impl.osl.TypedConstraintIF;

/**
 * PUBLIC: Validation handler that writes error reports on each
 * violation out to the given PrintStream. The default PrintStream is
 * System.err.
 */
public class TextValidationHandler implements ValidationHandlerIF {
  protected PrintStream err;
  protected int errors;

  // --- TextValidationHandler methods
  
  /**
   * PUBLIC: Creates a validation handler that writes to System.err.
   */
  public TextValidationHandler() {
    this(System.err);
  }

  /**
   * PUBLIC: Creates a validation handler that writes to the given
   * PrintStream instance.
   */
  public TextValidationHandler(PrintStream err) {
    this.err = err;
    this.errors = 0;
  }

  // --- ValidationHandlerIF methods
  
  @Override
  public void violation(String message, TMObjectIF container,
                        Object offender, ConstraintIF constraint) {
    err.println();
    err.println("ERROR: " + message);
    errors++;

    if (offender instanceof AssociationRoleIF) {
      AssociationRoleIF role = (AssociationRoleIF) offender;
      err.println("  Association role player: " +
                         printTopic(role.getPlayer()));
      if (role.getPlayer() != null)
        err.println("  Association role player type: " +
                           printTopic(getType(role.getPlayer())));
      err.println("  Association role type: " +
                         printTopic(role.getType()));
      err.println("  Association type: " +
                         printTopic(role.getAssociation().getType()));
      if (container instanceof AssociationIF)
        err.println("  Container: association");
      else
        err.println("  Container: topic");
        
    } else if (offender instanceof TopicNameIF) {
      TopicNameIF bn = (TopicNameIF) offender;
      err.println("  Base name: '" + bn.getValue() + "'");
      printScope(bn.getScope());
      err.println("  Owner: " + printTopic(bn.getTopic()));
        
    } else if (offender instanceof VariantNameIF) {
      VariantNameIF vn = (VariantNameIF) offender;
      err.println("  Variant name: '" + vn.getValue() + "'");
      printScope(vn.getScope());
      err.println("  Topic: " + printTopic(vn.getTopicName().getTopic()));
        
    } else if (offender instanceof OccurrenceIF) {
      OccurrenceIF occ = (OccurrenceIF) offender;
      err.println("  Occurrence: " + occ);
      err.println("  Occurrence type: " + printTopic(occ.getType()));
      printScope(occ.getScope());
      err.println("  Owner: " + printTopic(occ.getTopic()));

    } else if (offender instanceof TopicIF) {
      TopicIF topic = (TopicIF) offender;
      err.println("  Topic: " + printTopic(topic));
      err.println("  Topic type: " + printTopic(getType(topic)));
      if (container != null && container instanceof TopicIF)
        err.println("  Owner: " + printTopic((TopicIF) container));

    } else if (offender instanceof AssociationIF) {
      AssociationIF assoc = (AssociationIF) offender;
      err.println("  Association type: " + printTopic(assoc.getType()));

    } else {
      err.println("  Owner: " + container);
      err.println("  Object: " + offender);
      err.println("  Constraint: " + printConstraint(constraint));
    }
  }

  @Override
  public void startValidation() {
    // no-op
  }
    
  @Override
  public void endValidation() {
    err.println();
    err.println("" + errors + " error(s).");
  }
    
  // --- Internal methods

  protected void printScope(Collection scope) {
    err.print("  Scope: ");
    Iterator<TopicIF> it = scope.iterator();
    while(it.hasNext()) {
      err.print(TopicStringifiers.toString(it.next()));
      if (it.hasNext()) err.print(", ");
    }
    err.println();
  }

  protected String printTopic(TopicIF topic) {
    if (topic == null)
      return "<null>";
      
    String name = TopicStringifiers.toString(topic);
    if ("[No name]".equals(name))
      return topic.toString();
    else
      return name;
  }

  protected String printConstraint(ConstraintIF constraint) {
    if (constraint instanceof TypedConstraintIF) 
      return getClassName(constraint) + " " +
             ((TypedConstraintIF) constraint).getTypeSpecification().getClassMatcher();
    else
      return constraint.toString();
  }

  protected TopicIF getType(TopicIF topic) {
    Iterator it = topic.getTypes().iterator();
    if (it.hasNext())
      return (TopicIF) it.next();
    else
      return null;
  }

  protected String getClassName(Object object) {
    String name = object.getClass().getName();
    int pos = name.lastIndexOf(".");
    return name.substring(pos + 1);
  }
}
