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

import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.schema.core.ConstraintIF;
import net.ontopia.topicmaps.schema.core.SchemaViolationException;
import net.ontopia.topicmaps.schema.core.TMObjectMatcherIF;
import net.ontopia.topicmaps.schema.core.ValidationHandlerIF;
import net.ontopia.topicmaps.schema.impl.osl.AnyTopicMatcher;
import net.ontopia.topicmaps.schema.impl.osl.AssociationRoleConstraint;
import net.ontopia.topicmaps.schema.impl.osl.InternalTopicRefMatcher;
import net.ontopia.topicmaps.schema.impl.osl.OccurrenceConstraint;
import net.ontopia.topicmaps.schema.impl.osl.ScopeSpecification;
import net.ontopia.topicmaps.schema.impl.osl.ScopedConstraintIF;
import net.ontopia.topicmaps.schema.impl.osl.SourceLocatorMatcher;
import net.ontopia.topicmaps.schema.impl.osl.SubjectIndicatorMatcher;
import net.ontopia.topicmaps.schema.impl.osl.TopicNameConstraint;
import net.ontopia.topicmaps.schema.impl.osl.TopicRoleConstraint;
import net.ontopia.topicmaps.schema.impl.osl.TypeSpecification;
import net.ontopia.topicmaps.schema.impl.osl.TypedConstraintIF;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.utils.TopicStringifiers;

/**
 * INTERNAL: Validation handler that writes error reports (using some
 * HTML markup) on each violation out to the given Writer.
 */
public class HTMLValidationHandler implements ValidationHandlerIF {
  
  protected Writer out;
  protected int errors;

  public HTMLValidationHandler(Writer out) {
    this.out = out;
    this.errors = 0;
  }

  @Override
  public void startValidation() {
    // no-op
  }

  @Override
  public void violation(String message, TMObjectIF container, Object offender,
                        ConstraintIF constraint)
    throws SchemaViolationException {
    try {
      errors++;
      out.write("<p><b>" + message + "</b>\n");
      out.write("<blockquote>\n");
      out.write("<table>\n");
      if (offender instanceof AssociationRoleIF) {
        AssociationRoleIF role = (AssociationRoleIF) offender;
        out.write(printAssociationRole(role));
        out.write("<tr><th>Association type</th><td>" + 
                  printTopic(role.getAssociation().getType()) + "</td></tr>\n");
        
      } else if (offender instanceof AssociationIF) {
        AssociationIF assoc = (AssociationIF) offender;
        out.write("<tr><th>Association type</th><td>" +
                  printTopic(assoc.getType()) + "</td></tr>\n");
        
      } else if (offender instanceof TopicNameIF) {
        TopicNameIF bn = (TopicNameIF) offender;
        out.write("<tr><th>Base name</th><td>'" + bn.getValue() +
                  "'</td></tr>\n");
        printScope(bn.getScope());
        out.write("<tr><th>Topic</th><td>" + printTopic(bn.getTopic()) +
                  "</td></tr>\n");
        
      } else if (offender instanceof OccurrenceIF) {
        OccurrenceIF occ = (OccurrenceIF) offender;
        out.write("<tr><th>Occurrence</th><td>" + occ + "</td></tr>\n");
        out.write("<tr><th>Type</th><td>" + printTopic(occ.getType()) +
                  "</td></tr>\n");
        printScope(occ.getScope());
        out.write("<tr><th>Topic</th><td>" + printTopic(occ.getTopic()) +
                  "</td></tr>\n");
        
      } else if (offender == null && constraint != null) {
        out.write("<tr><th>Constraint</th><td>" + printConstraint(constraint) +
                  "</td></tr>\n");
        if (container instanceof TopicIF)
          out.write("<tr><th>Topic</th><td>"+ printTopic((TopicIF) container) +
                    "</td></tr>\n");
        if (container instanceof AssociationIF) {
          AssociationIF assoc = (AssociationIF) container; 
          out.write("<tr><th>Association type</th><td>"+ printTopic(assoc.getType()) +
                    "</td></tr>\n");

          Iterator it = assoc.getRoles().iterator();
          while (it.hasNext()) {
            AssociationRoleIF role = (AssociationRoleIF) it.next();
            out.write(printAssociationRole(role));
          }
        }
      } else {
        if (container instanceof TopicIF) 
          out.write("<tr><th>Owner</th><td>" + printTopic((TopicIF)container) + "</td></tr>\n");
        else
          out.write("<tr><th>Owner</th><td>" + container + "</td></tr>\n");
          
        if (offender instanceof TopicIF)
          out.write("<tr><th>Object</th><td>" + printTopic((TopicIF) offender) +
                    "</td></tr>\n");
        else
          out.write("<tr><th>Object</th><td>" + offender + "</td></tr>\n");
      }
      out.write("</table>\n");
      out.write("</blockquote>\n");
    }
    catch (java.io.IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
    
  @Override
  public void endValidation() {
    // no-op
  }
  
  // --- Internal methods
  
  protected void printScope(Collection scope) throws java.io.IOException {
    out.write("<tr><th>Scope</th><td>");
    Iterator it = scope.iterator();
    while(it.hasNext()) {
      out.write(printTopic((TopicIF)it.next()));
      if (it.hasNext()) out.write(", ");
    }
    out.write("</td></tr>\n");
  }

  protected String printTypes(TopicIF topic) {
    StringBuilder buf = new StringBuilder();
    Iterator it = topic.getTypes().iterator();
    while (it.hasNext()) {
      buf.append(printTopic((TopicIF) it.next()));
      if (it.hasNext())
        buf.append(", ");
    }
    return buf.toString();
  }
  
  protected String printTopic(TopicIF topic) {
    if (topic == null)
      return "[null]";
    
    String name = TopicStringifiers.toString(topic);
    if ("[No name]".equals(name))
      return topic.toString();
    else
      return name;
  }

  protected String printConstraint(ConstraintIF constraint) {
    if (constraint instanceof TopicRoleConstraint) 
      return "Topic role constraint, role type: " + 
        printTypeSpec(((TypedConstraintIF) constraint).getTypeSpecification()) +
        ", association types: " +
        printTypeSpecs(((TopicRoleConstraint) constraint).getAssociationTypes());
    else if (constraint instanceof AssociationRoleConstraint) 
      return "Association role constraint, role type: " + 
        printTypeSpec(((TypedConstraintIF) constraint).getTypeSpecification());
    else if (constraint instanceof OccurrenceConstraint) 
      return "Occurrence constraint, type: " + 
        printTypeSpec(((TypedConstraintIF) constraint).getTypeSpecification());
    else if (constraint instanceof TopicNameConstraint) 
      return "Base name constraint, scope: " + 
        printScopeSpec(((ScopedConstraintIF) constraint).getScopeSpecification());
    else
      return "[unknown]";
  }

  protected String printMatcher(TMObjectMatcherIF matcher) {
    if (matcher instanceof SubjectIndicatorMatcher)
      return "subject indicator " + ((SubjectIndicatorMatcher) matcher).getLocator().getAddress();
    else if (matcher instanceof SourceLocatorMatcher)
      return "source locator " + ((SourceLocatorMatcher) matcher).getLocator().getAddress();
    else if (matcher instanceof InternalTopicRefMatcher)
      return "<span title=\"relative source locator\">" + ((InternalTopicRefMatcher) matcher).getRelativeURI() + "</span>";
    else if (matcher instanceof AnyTopicMatcher)
      return "any";
    else
      return "[unknown]";
  }

  protected String printTypeSpecs(Collection specs) {
    StringBuilder buf = new StringBuilder();
    Iterator it = specs.iterator();

    while (it.hasNext()) {
      buf.append(printTypeSpec((TypeSpecification) it.next()));
      if (it.hasNext())
        buf.append(", ");
    }
    return buf.toString();
  }

  protected String printTypeSpec(TypeSpecification typespec) {
    return printMatcher(typespec.getClassMatcher());
  }

  protected String printScopeSpec(ScopeSpecification scopespec) {
    StringBuilder buf = new StringBuilder();
    Iterator it = scopespec.getThemeMatchers().iterator();

    if (!it.hasNext())
      return "[unconstrained]";

    while (it.hasNext()) {
      buf.append(printMatcher((TMObjectMatcherIF) it.next()));
      if (it.hasNext())
        buf.append(", ");
    }
    return buf.toString();
  }

  protected String printAssociationRole(AssociationRoleIF role) {
    StringBuilder buf = new StringBuilder();
    buf.append("<tr><th>Association role player</th><td>" +
               printTopic(role.getPlayer()) + "</td></tr>\n");
    buf.append("<tr><th>Association role type</th><td>" + 
               printTopic(role.getType()) + "</td></tr>\n");
    return buf.toString();
  }

  // ---

  public int getErrors() {
    return errors;
  }
}
