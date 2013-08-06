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

package net.ontopia.topicmaps.schema.impl.osl.cmdline;

import java.util.Iterator;
import java.util.Collection;
import java.util.HashMap;
import java.util.ArrayList;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.schema.core.TMObjectMatcherIF;
import net.ontopia.topicmaps.schema.impl.osl.OSLSchema;
import net.ontopia.topicmaps.schema.impl.osl.OccurrenceConstraint;
import net.ontopia.topicmaps.schema.impl.osl.ScopeSpecification;
import net.ontopia.topicmaps.schema.impl.osl.TopicClass;
import net.ontopia.topicmaps.schema.impl.osl.TopicNameConstraint;
import net.ontopia.topicmaps.schema.impl.osl.TopicRoleConstraint;
import net.ontopia.topicmaps.schema.impl.osl.TypeSpecification;
import net.ontopia.topicmaps.schema.impl.osl.VariantConstraint;

public class TopicClassAnalyzer extends AbstractSchemaAnalyzer {


  private TopicClass tclass;
  private TopicIF ttype;
  private Collection otherClasses;
  private OSLSchema schema;
  private HashMap occtypes, bnames, players;
  private Collection topics;

  /**
   * 
   */
  public TopicClassAnalyzer(OSLSchema schema, TopicIF topictype, Collection topics) {
    // init
    otherClasses =  new ArrayList();
    this.players =  new HashMap();
    this.occtypes = new HashMap();
    this.bnames =   new HashMap();

    this.schema = schema;
    this.ttype = topictype;
    this.topics = topics;

    // setup
    TypeSpecification spec = getTypeSpecification(ttype);
    
    if (spec != null) {
    
      tclass = new TopicClass(schema, "tc" + ttype.getObjectId());
      tclass.setIsStrict(false);
      tclass.setTypeSpecification(spec);
      schema.addTopicClass(tclass);
    }    
  }


  /**
   * Analyzes the topics for this TopicClassAnalyzerz
   */ 
  public void analyze() {
    // Not setup properly return with nothing.
    if (tclass == null) return;

    // Adding the current topic types id, so that no duplicate
    // statements is made.
    otherClasses.add(tclass.getId());

    Iterator it = topics.iterator();
    while (it.hasNext()) {
      TopicIF topic = (TopicIF) it.next();

      // Add otherclasses that the topics of this type can belong to.
      registerOtherClasses(topic);
      // Add occurrences of this topic
      registerOccurrences(topic);
      // Add basenames of this topic
      registerBasenames(topic);
      // Add the different association role player types of this topic.
      registerPlayerTypes(topic);
    }
  }


  /**
   * Register other classes of which this class is allowed to be an instance of.
   * If the result set is empty, topics of this class can not be instances of any
   * other class.
   * @param topic the topic in which to look for other classes.
   */
  public void registerOtherClasses(TopicIF topic) {

    // Add otherclasses that the topics of this type can belong to.
    Iterator othertypes = topic.getTypes().iterator();
    while (othertypes.hasNext()) {
      TopicIF othertype = (TopicIF)othertypes.next();
      // Add if not equal to primary type
      if (othertype != ttype &&
          !otherClasses.contains(othertype.getObjectId())) {
        tclass.addOtherClass(getTypeSpecification(othertype));
          otherClasses.add(othertype.getObjectId());
      }
    }
  }


  public void registerOccurrences(TopicIF topic) {
    Iterator it = topic.getOccurrences().iterator();
    while (it.hasNext()) {
      OccurrenceIF occ = (OccurrenceIF) it.next();
      //analyzer.registerOccurrence(occ);

      // try to find if this occurrence type is already
      // registered.
      OccurrenceConstraint constraint =
        (OccurrenceConstraint) occtypes.get(occ.getType());
        
      if (constraint == null) {
        // Create a new OccurrenceConstraint
        constraint = new OccurrenceConstraint(tclass);
        constraint.setTypeSpecification(getTypeSpecification(occ.getType()));
        occtypes.put(occ.getType(), constraint);
        tclass.addOccurrenceConstraint(constraint);
        
        ScopeSpecification spec = getScopeSpecification(occ);
        spec.setMatch(ScopeSpecification.MATCH_SUPERSET);
        constraint.setScopeSpecification(spec);
        
        if (occ.getLocator() != null)
          constraint.setInternal(OccurrenceConstraint.RESOURCE_EXTERNAL);
        else if (occ.getValue() != null)
          constraint.setInternal(OccurrenceConstraint.RESOURCE_INTERNAL);
      } else {
        // Gets the old scope specs
        ScopeSpecification spec = constraint.getScopeSpecification();
        Collection matchers = spec.getThemeMatchers();
        // Gets the new scope specs
        ScopeSpecification newSpec = getScopeSpecification(occ);
        // Adds them together
        Iterator iter = newSpec.getThemeMatchers().iterator();
        while (iter.hasNext()) {
          TMObjectMatcherIF matcher = (TMObjectMatcherIF)iter.next();
          // Check if this scope already is registered.
          boolean contains = false;
          Iterator iter2 = spec.getThemeMatchers().iterator();
          while (iter2.hasNext()) {
            TMObjectMatcherIF tmp = (TMObjectMatcherIF)iter2.next();
            if (tmp.equals(matcher)) contains = true;
          }
          // If not already registered, register the new scope.
          if (!contains)
            spec.addThemeMatcher(matcher);
        }
        // Adds all the scope specs to the constraint
        spec.setMatch(ScopeSpecification.MATCH_SUPERSET);
        constraint.setScopeSpecification(spec);
        
        switch (constraint.getInternal()) {
        case OccurrenceConstraint.RESOURCE_EXTERNAL:
          if (occ.getValue() != null)
            constraint.setInternal(OccurrenceConstraint.RESOURCE_EITHER);
          break;
        case OccurrenceConstraint.RESOURCE_INTERNAL:
          if (occ.getLocator() != null)
            constraint.setInternal(OccurrenceConstraint.RESOURCE_EITHER);
        }
      }
    }
  }


  public void registerBasenames(TopicIF topic) {

    Iterator it = topic.getTopicNames().iterator();
    while (it.hasNext()) {
      TopicNameIF bname = (TopicNameIF)it.next();

      TopicNameConstraint constraint;
        
      Iterator iter = bname.getScope().iterator();
      while (iter.hasNext()) {
        TopicIF scope = (TopicIF)iter.next();
        if (!bnames.containsKey(scope)) {
          constraint = new TopicNameConstraint(tclass);
          ScopeSpecification spec = getScopeSpecification(bname);
          if (spec != null)
            constraint.setScopeSpecification(spec);
          
          Iterator iter2 = bname.getVariants().iterator();
          while (iter2.hasNext()) {
            VariantNameIF variant = (VariantNameIF)iter2.next();
            VariantConstraint vconstraint = new VariantConstraint(constraint);
            ScopeSpecification sspec = getScopeSpecification(variant);
            
            if (sspec != null)
              vconstraint.setScopeSpecification(sspec);
            
            constraint.addVariantConstraint(vconstraint);
          }
          bnames.put(scope, constraint);
          tclass.addTopicNameConstraint(constraint);
        }
      }
    }
  }

  public void registerPlayerTypes(TopicIF topic) {

    Iterator it = topic.getRoles().iterator();
    while (it.hasNext()) {
      AssociationRoleIF assocrl = (AssociationRoleIF)it.next();
      TopicIF atype = assocrl.getAssociation().getType();
      TopicIF rtype = assocrl.getType();

      String constraint_key = makeKey(ttype, atype, rtype);
        
      // BUG: Need to key by player type + role type + association type
      TopicRoleConstraint constraint;
      if (!players.containsKey(constraint_key)) {
        // If the constraint is null, then register a new one.
        constraint = new TopicRoleConstraint(tclass);
        constraint.setTypeSpecification(getTypeSpecification(rtype));
        constraint.addAssociationType(getTypeSpecification(atype));
        players.put(constraint_key, constraint);
        // Register playing constraint
        tclass.addRoleConstraint(constraint);
      }
    }
  }
  
}
