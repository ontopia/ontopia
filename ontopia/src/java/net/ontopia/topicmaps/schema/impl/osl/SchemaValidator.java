
// $Id: SchemaValidator.java,v 1.20 2008/06/13 08:36:28 geir.gronmo Exp $

package net.ontopia.topicmaps.schema.impl.osl;

import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.utils.TypeHierarchyUtils;
import net.ontopia.topicmaps.schema.core.ConstraintIF;
import net.ontopia.topicmaps.schema.core.CardinalityConstraintIF;
import net.ontopia.topicmaps.schema.core.SchemaValidatorIF;
import net.ontopia.topicmaps.schema.core.SchemaViolationException;
import net.ontopia.topicmaps.schema.core.ValidationHandlerIF;
import net.ontopia.topicmaps.schema.core.TMObjectMatcherIF;
import net.ontopia.topicmaps.schema.utils.ExceptionValidationHandler;

/**
 * INTERNAL: A schema validator that can be used to validate topic map
 * constructs against an OSL schema. The schema validator is bound to
 * a particular OSL schema.
 */
public class SchemaValidator implements SchemaValidatorIF {
  protected OSLSchema schema;
  protected ValidationHandlerIF handler;

  SchemaValidator(OSLSchema schema) {
    this.schema = schema;
    this.handler = new ExceptionValidationHandler();
  }

  // --- SchemaValidatorIF methods
  
  public void validate(TopicIF topic) throws SchemaViolationException {
    // find appropriate class
    TopicClass klass = (TopicClass) findClass(topic, schema.getTopicClasses());
    if (klass == null) {
      if (schema.isStrict())
        handler.violation("No matching rule for topic",
                          topic.getTopicMap(), topic, null);
      return;
    }

    // pick out constraint collections
    Collection basenamecs = klass.getAllTopicNameConstraints();
    Collection occurrencecs = klass.getAllOccurrenceConstraints();
    Collection rolecs = klass.getAllRoleConstraints();
    
    // other classes
    Collection others = klass.getOtherClasses();
    Iterator it = topic.getTypes().iterator();
    while (it.hasNext()) {
      TopicIF tclass = (TopicIF) it.next();
      if (klass.getTypeSpecification().matchType(tclass))
        continue;

      boolean found = false;
      Iterator it2 = others.iterator();
      while (it2.hasNext()) {
        TypeSpecification typespec = (TypeSpecification) it2.next();

        if (typespec.matchType(tclass)) {
          found = true;
          TopicClass otherclass =
            (TopicClass) findClassFor(tclass, schema.getTopicClasses());

          if (otherclass != null) {
            basenamecs.addAll(otherclass.getAllTopicNameConstraints());
            occurrencecs.addAll(otherclass.getAllOccurrenceConstraints());
            rolecs.addAll(otherclass.getAllRoleConstraints());
          }
          break;
        }
      }

      if (!found)
        handler.violation("Topic instance of illegal other class",
                          topic, tclass, null);
    }

    // characteristics
    validate(topic, basenamecs, topic.getTopicNames(), klass.isStrict());
    validate(topic, occurrencecs, topic.getOccurrences(), klass.isStrict());
    validate(topic, rolecs, topic.getRoles(), klass.isStrict());
  }

  public void validate(TopicMapIF topicmap) throws SchemaViolationException {
    TypeHierarchyUtils utils = new TypeHierarchyUtils();
      
    handler.startValidation();

    // required superclass/subclass relationships
    Iterator it = schema.getTopicClasses().iterator();
    while (it.hasNext()) {
      TopicClass klass = (TopicClass) it.next();
      TopicClass superclass = klass.getSuperclass();
      if (superclass != null) {
        TopicIF subtopic = getTopic(klass, topicmap);
        if (subtopic != null) {
          TopicIF supertopic = getTopic(superclass, topicmap);
          if (!utils.getSuperclasses(subtopic).contains(supertopic))
            handler.violation("Topic class not subclass of other class, as " +
                              "required by schema", topicmap, subtopic, null);
        }
      }
    }

    // topics
    it = topicmap.getTopics().iterator();
    while (it.hasNext())
      validate((TopicIF) it.next());

    // associations
    it = topicmap.getAssociations().iterator();
    while (it.hasNext())
      validate((AssociationIF) it.next());    
    handler.endValidation();
  }

  public void validate(AssociationIF association)
    throws SchemaViolationException {
    TopicMapIF tm = association.getTopicMap();
    
    // find appropriate class
    AssociationClass klass = (AssociationClass) findClass(association, schema.getAssociationClasses());
    if (klass == null) {
      if (schema.isStrict())
        handler.violation("No matching rule for association",
                          tm, association, null);
      return;
    }

    validateScope(tm, association, klass);

    // characteristics
    validate(association, klass.getRoleConstraints(), association.getRoles(),
             true);
  }

  public void setValidationHandler(ValidationHandlerIF handler) {
    this.handler = handler;
  }

  public ValidationHandlerIF getValidationHandler() {
    return handler;
  }
  
  // --- Internal methods

  protected void validate(TMObjectIF container, Collection constraints,
                          Collection objects) throws SchemaViolationException {
    validate(container, constraints, objects, false);
  }
  
  protected void validate(TMObjectIF container, Collection constraints,
                          Collection objects, boolean strict)
    throws SchemaViolationException {
    HashMap counts = new HashMap();

    // initialize the counts
    Iterator it = constraints.iterator();
    while (it.hasNext()) {
      CardinalityConstraintIF constraint = (CardinalityConstraintIF) it.next();
      counts.put(constraint, new Counter());
    }

    // now validate
    it = objects.iterator();
    while (it.hasNext()) {
      TMObjectIF object = (TMObjectIF) it.next();

      boolean ok = false;
      Iterator it2 = constraints.iterator();
      while (it2.hasNext()) {
        CardinalityConstraintIF constraint =
          (CardinalityConstraintIF) it2.next();
        
        if (constraint.matches(object)) {
          validate(object, constraint);
          ok = true;

          Counter counter = (Counter) counts.get(constraint);
          counter.count++;
          if (counter.count > constraint.getMaximum() &&
              constraint.getMaximum() != CardinalityConstraintIF.INFINITY)
            handler.violation(counter.count + " matches to constraint (" +
                              getRange(constraint) + " required)",
                              container, object, constraint);
          // break;
          // not breaking means that we do implicit merging of
          // constraints from otherclasses by validating against *all*
          // constraints to see if that works. a proper semantic
          // analysis of the constraints merged in would be better,
          // but this works for now. see bug 557 for more information.
        }
      }

      if (!ok && strict)
        handler.violation("No matching rule for characteristic",
                          container, object, null);
    }

    // check the minimum constraints
    it = counts.keySet().iterator();
    while (it.hasNext()) {
      CardinalityConstraintIF constraint = (CardinalityConstraintIF) it.next();
      Counter counter = (Counter) counts.get(constraint);

      if (counter.count < constraint.getMinimum())
        handler.violation("" + counter.count + " matches to constraint; " +
                          constraint.getMinimum() + " required", container,
                          null, constraint);
    }
  }  


  protected void validate(TMObjectIF object, ConstraintIF constraint)
    throws SchemaViolationException {
    if (object instanceof TopicNameIF)
      validate((TopicNameIF) object, (TopicNameConstraint) constraint);
    else if (object instanceof OccurrenceIF)
      validate((OccurrenceIF) object, (OccurrenceConstraint) constraint);
    else if (object instanceof AssociationRoleIF &&
             constraint instanceof TopicRoleConstraint)
      validate((AssociationRoleIF) object, (TopicRoleConstraint) constraint);
    else if (object instanceof AssociationRoleIF &&
             constraint instanceof AssociationRoleConstraint)
      validate((AssociationRoleIF) object, (AssociationRoleConstraint) constraint);
    else if (object instanceof VariantNameIF)
      validate((VariantNameIF) object, (VariantConstraint) constraint);
    else
      handler.violation("INTERNAL: Unknown object: " + object, null, null,
                        constraint);
  }
  
  protected void validate(TopicNameIF basename, TopicNameConstraint constraint)
    throws SchemaViolationException {
    validate(basename,
             constraint.getVariantConstraints(),
             basename.getVariants());
  }

  protected void validate(VariantNameIF variant, VariantConstraint constraint) {
    // all the checking is already done
  }
  
  protected void validate(OccurrenceIF occ, OccurrenceConstraint constraint)
    throws SchemaViolationException {

    TopicIF topic = occ.getTopic();
    
    validateScope(topic, occ, constraint);
    if (constraint.getInternal() == OccurrenceConstraint.RESOURCE_INTERNAL &&
        occ.getLocator() != null)
      handler.violation("Occurrence " + occ + " is not internal",
                        topic, occ, constraint);
    if (constraint.getInternal() == OccurrenceConstraint.RESOURCE_EXTERNAL &&
        occ.getValue() != null)
      handler.violation("Occurrence " + occ + " is not external",
                        topic, occ, constraint);
  }

  protected void validate(AssociationRoleIF role, TopicRoleConstraint constraint)
    throws SchemaViolationException {
    //      System.out.println("Validating " + occ + " against " + constraint);
  }


  protected void validate(AssociationRoleIF role, AssociationRoleConstraint constraint)
    throws SchemaViolationException {
    // validating player
    TopicIF player = role.getPlayer();
    if (player == null)
      return;
    
    Iterator it = constraint.getPlayerTypes().iterator();
    if (!it.hasNext()) 
      return; // nothing was said about the type of the player, so...
    
    while (it.hasNext()) {
      TypeSpecification typespec = (TypeSpecification) it.next();
      if (typespec.matches(player)) 
        return;
    }
    
    handler.violation("Association role player of illegal type",
                      role.getAssociation(), role, constraint);
  }


  protected void validateScope(TMObjectIF container, ScopedIF scoped,
                               ScopedConstraintIF constraint)
    throws SchemaViolationException {

    ScopeSpecification spec = constraint.getScopeSpecification();
    if (spec == null)
      return; // no spec, everything is allowed

    if (!spec.matches(scoped))
      handler.violation("Scope did not match constraint", container, scoped,
                        constraint);
  }
  
  protected ConstraintIF findClass(TMObjectIF object, Collection classes) {
    Iterator it = classes.iterator();
    while (it.hasNext()) {
      ConstraintIF candidate = (ConstraintIF) it.next();
      if (candidate.matches(object))
        return candidate;
    }

    return null;
  }

  protected ConstraintIF findClass(TopicIF topic, Collection classes) {
    TopicClass klass = (TopicClass) findClass((TMObjectIF) topic, classes);
    if (klass == null)
      return klass;

    // go through and check if we ought to use a subclass instead
    while (!klass.getSubclasses().isEmpty()) {
      TopicClass prev = klass;
      Iterator it = klass.getSubclasses().iterator();
      while (it.hasNext()) {
        TopicClass candidate = (TopicClass) it.next();
        if (candidate.matches(topic)) {
          klass = candidate;
          break;
        }
      }

      if (prev == klass)
        break;
    }
    
    return klass;
  }

  protected ConstraintIF findClassFor(TopicIF tclass, Collection classes) {
    Iterator it = classes.iterator();
    while (it.hasNext()) {
      TopicClass klass = (TopicClass) it.next();
      TypeSpecification spec = klass.getTypeSpecification();
      if (spec.matchType(tclass))
        return klass;
    }    
    return null;
  }
  
  protected TopicIF getTopic(TopicClass klass, TopicMapIF tm) {
    TypeSpecification spec = klass.getTypeSpecification();
    if (spec == null)
      return null;

    TMObjectMatcherIF matcher = spec.getClassMatcher();
    if (matcher == null)
      return null;
    
    if (matcher instanceof InternalTopicRefMatcher) {
      InternalTopicRefMatcher m = (InternalTopicRefMatcher) matcher;
      LocatorIF loc = tm.getStore().getBaseAddress().resolveAbsolute(m.getRelativeURI());
      return (TopicIF) tm.getObjectByItemIdentifier(loc);

    } else if (matcher instanceof SourceLocatorMatcher) {
      SourceLocatorMatcher m = (SourceLocatorMatcher) matcher;
      return (TopicIF) tm.getObjectByItemIdentifier(m.getLocator());

    } else if (matcher instanceof SubjectIndicatorMatcher) {
      SubjectIndicatorMatcher m = (SubjectIndicatorMatcher) matcher;
      return tm.getTopicBySubjectIdentifier(m.getLocator());

    } else
      throw new OntopiaRuntimeException("INTERNAL ERROR: Illegal topic class type matcher: " + matcher);
  }

  // --- Internal helper methods

  private String getRange(CardinalityConstraintIF constraint) {
    String range = Integer.toString(constraint.getMinimum()) + "-";
    if (constraint.getMaximum() == CardinalityConstraintIF.INFINITY)
      return range + "inf";
    else
      return range + Integer.toString(constraint.getMaximum());
  }
  
  // --- Counter class

  class Counter {
    public int count = 0;
  }

}
