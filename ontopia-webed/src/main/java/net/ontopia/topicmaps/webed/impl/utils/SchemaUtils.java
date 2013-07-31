/*
 * #!
 * Ontopia Webed
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

package net.ontopia.topicmaps.webed.impl.utils;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.core.index.*;
import net.ontopia.topicmaps.schema.core.*;
import net.ontopia.topicmaps.schema.impl.osl.*;
import net.ontopia.topicmaps.utils.TypeHierarchyUtils;

/**
 * INTERNAL: Utitlity class for providing help around making topic map
 * schema support available to the web editor.
 */
public class SchemaUtils {

  private static TypeHierarchyUtils hierUtils = new TypeHierarchyUtils();
  
  /**
   * INTERNAL: Helper method for resolving a topic object from a
   * matcher instance of a schema.
   */
  private TopicIF getTopic(TopicMapIF tm, TMObjectMatcherIF matcher) {
    if (matcher instanceof InternalTopicRefMatcher) {
      InternalTopicRefMatcher m = (InternalTopicRefMatcher) matcher;
      LocatorIF loc = tm.getStore().getBaseAddress()
        .resolveAbsolute(m.getRelativeURI());
      return (TopicIF) tm.getObjectByItemIdentifier(loc);
      
    } else if (matcher instanceof SourceLocatorMatcher) {
      SourceLocatorMatcher m = (SourceLocatorMatcher) matcher;
      return (TopicIF) tm.getObjectByItemIdentifier(m.getLocator());

    } else if (matcher instanceof SubjectIndicatorMatcher) {
      SubjectIndicatorMatcher m = (SubjectIndicatorMatcher) matcher;
      return tm.getTopicBySubjectIdentifier(m.getLocator());

    } else
      throw new OntopiaRuntimeException("INTERNAL ERROR: Illegal topic class" +
                                        " matcher: " + matcher);
  }

  /**
   * INTERNAL: Add topic(s) matching a topic matcher to a collection.
   */
  private void addTopic(TopicMapIF tm, TMObjectMatcherIF matcher,
                        Collection topics) {
    if (matcher instanceof TypeSpecification) {
      TypeSpecification m = (TypeSpecification) matcher;
      TopicIF type = getTopic(tm, m.getClassMatcher());
      if (type != null) {
        ClassInstanceIndexIF typeIndex = (ClassInstanceIndexIF)
          tm.getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");
        topics.addAll(typeIndex.getTopics(type));
      }
      
    } else {
      TopicIF topic = getTopic(tm, matcher);
      if (topic != null)
        topics.add(topic);
    }
  }
  
  /**
   * INTERNAL: Gets the topic objects which define a context theme by
   * the given scope specification typically attached to a constraint
   * object.
   *
   * @return A collection of TopicIF objects.
   */
  public Collection getMatchingTopics(TopicMapIF tm, ScopeSpecification scopeSpec) {
    Collection result = new HashSet();
    if (scopeSpec != null) {
      Collection matchers = scopeSpec.getThemeMatchers();
      Iterator itM = matchers.iterator();
      while (itM.hasNext()) {
        TMObjectMatcherIF matcher = (TMObjectMatcherIF) itM.next();
        addTopic(tm, matcher, result);
      }
    }
    return result;
  }

  /**
   * INTERNAL: Gets the topic objects which define a class type by
   * the given type specification typically attached to a constraint
   * object.
   *
   * @return A collection of TopicIF objects.
   */
  public Collection getMatchingTopics(TopicMapIF tm, TypeSpecification typeSpec) {
    Collection result = new HashSet();
    if (typeSpec != null) {
      TMObjectMatcherIF matcher = typeSpec.getClassMatcher();
      addTopic(tm, matcher, result);
    }
    return result;
  }
  

  // -------------------------------------------------------------------------
  // Constraints about topic names
  // -------------------------------------------------------------------------  
  
  /**
   * INTERNAL: Returns the complete set of topic name constraints in
   * this collection, including those of sub collections.
   *
   * @return A collection of TopicNameConstraint objects.
   */
  public Collection getAllTopicNameConstraints(OSLSchema schema, TopicIF topic) {
    // find appropriate class
    TopicClass klass = (TopicClass) findClass(topic, schema.getTopicClasses());
    if (klass == null)
      return Collections.EMPTY_LIST;
    return klass.getAllTopicNameConstraints();
  }

  /**
   * INTERNAL: Returns a set of topic objects which belong to themes
   * that are allowed to add to the topic name by the given topic.
   *
   * @return A ScopeStorage object containing a collection of TopicIF objects.
   */
  public ScopeStorage getRemainingTopicNameThemes(OSLSchema schema, TopicIF topic) {
    Collection bnC = this.getAllTopicNameConstraints(schema, topic);
    Set bnThemes = new HashSet();
    boolean unconstrainedIncluded = false;
    if (bnC.size() > 0) {
      Collection curTopicNames = topic.getTopicNames();
      Iterator itC = bnC.iterator();
      // loop over all topic name constraints
      while (itC.hasNext()) {
        TopicNameConstraint constraint = (TopicNameConstraint) itC.next();
        // int min = constraint.getMinimum();
        int max = constraint.getMaximum();
        if (max == CardinalityConstraintIF.INFINITY)
          max = Integer.MAX_VALUE;
        ScopeSpecification scsp = constraint.getScopeSpecification();
        Collection topicThemes = this.getMatchingTopics(topic.getTopicMap(), scsp);
        int curNumber = this.getCharacteristicsInScope(curTopicNames, topicThemes).size();
        // check if theme can be added theoretically
        if (curNumber < max) {
          // add the scope to the allowed set of topic name scopes
          Iterator itT = topicThemes.iterator();
          while (itT.hasNext())
            bnThemes.add((TopicIF) itT.next());
          if (topicThemes.isEmpty())
            unconstrainedIncluded = true;
        }
      } // while itC
    }
    return new ScopeStorage(bnThemes, unconstrainedIncluded);
  }

  
  // -------------------------------------------------------------------------
  // Constraints about Occurrences
  // -------------------------------------------------------------------------

  /**
   * INTERNAL: Returns the complete set of constraints for internal
   * occurrences for the given topic in the specified schema.
   *
   * @return A collection of OccurrenceConstraint objects.
   */
  public Collection getIntOccConstraints(OSLSchema schema, TopicIF topic) {
    return getOccurrenceConstraints(schema, topic, true);
  }
  
  /**
   * INTERNAL: Returns the complete set of constraints for external
   * occurrences for the given topic in the specified schema.
   *
   * @return A collection of OccurrenceConstraint objects.
   */
  public Collection getExtOccConstraints(OSLSchema schema, TopicIF topic) {
    return getOccurrenceConstraints(schema, topic, false);
  }
  
  protected Collection getOccurrenceConstraints(OSLSchema schema, TopicIF topic,
                                                boolean internal) {
    List selOccCons = new ArrayList();
    // find appropriate class
    TopicClass klass = (TopicClass) findClass(topic, schema.getTopicClasses());
    if (klass == null)
      return Collections.EMPTY_LIST;

    Collection occConstraints = klass.getAllOccurrenceConstraints();
    Iterator it = occConstraints.iterator();
    while (it.hasNext()) {
      OccurrenceConstraint oc = (OccurrenceConstraint) it.next();
      if ((internal && (oc.getInternal() == OccurrenceConstraint.RESOURCE_INTERNAL
                        || oc.getInternal() == OccurrenceConstraint.RESOURCE_EITHER))
          || (!internal && (oc.getInternal() == OccurrenceConstraint.RESOURCE_EXTERNAL
                            || oc.getInternal() == OccurrenceConstraint.RESOURCE_EITHER)))
        selOccCons.add(oc);
    }
    return selOccCons;
  }

  /**
   * INTERNAL: Returns a set of topic objects which belong to internal
   * occurrence types that are allowed to add to the given topic.
   *
   * @return A TypeStorage object containing a collection of TopicIF objects.
   */
  public TypeStorage getRemainingIntOccTypes(OSLSchema schema, TopicIF topic) {
    return this.getRemainingOccTypes(schema, topic,
                                     this.getIntOccConstraints(schema, topic));    
  }
  
  /**
   * INTERNAL: Returns a set of topic objects which belong to external
   * occurrence types that are allowed to add to the given topic.
   *
   * @return A TypeStorage object containing a collection of TopicIF objects.
   */
  public TypeStorage getRemainingExtOccTypes(OSLSchema schema, TopicIF topic) {
    return this.getRemainingOccTypes(schema, topic,
                                     this.getExtOccConstraints(schema, topic));
  }
  
  protected TypeStorage getRemainingOccTypes(OSLSchema schema, TopicIF topic,
                                             Collection occCons) {
    Set remOccTypes = new HashSet();
    boolean untypedIncluded = false;
    if (occCons.size() > 0) {
      // get all types of all occurrences of the given topic
      Collection topicOccTypes = new ArrayList();
      // System.out.println("getRemainingOccTypes for topic: " + topic);
      // System.out.println("-----------\n" + topic.getOccurrences() + "\n------");
      Iterator itOT = topic.getOccurrences().iterator();
      while (itOT.hasNext())
        topicOccTypes.add(((OccurrenceIF) itOT.next()).getType());
      // loop over all occurrence constraints
      Iterator itC = occCons.iterator();
      while (itC.hasNext()) {
        OccurrenceConstraint constraint = (OccurrenceConstraint) itC.next();
        // int min = constraint.getMinimum();
        int max = constraint.getMaximum();
        if (max == CardinalityConstraintIF.INFINITY)
          max = Integer.MAX_VALUE;
        TypeSpecification typeSpec = constraint.getTypeSpecification();
        Collection types = this.getMatchingTopics(topic.getTopicMap(), typeSpec);
        int curNumber = this.getObjectsOfType(topicOccTypes, types).size();
        // System.out.println(" - cur types: " + types);
        // System.out.println("   --> " + curNumber);
        // -- check if type can be used for a new occurrence theoretically
        if (curNumber < max) {
          // add the type to the allowed set of role types
          Iterator itT = types.iterator();
          while (itT.hasNext())
            remOccTypes.add((TopicIF) itT.next());
          if (types.isEmpty())
            untypedIncluded = true;
        }
      } // while itC
    }
    return new TypeStorage(remOccTypes, untypedIncluded);
  }

  
  // -------------------------------------------------------------------------
  // Constraints about Association roles
  // -------------------------------------------------------------------------
  
  /**
   * INTERNAL: Returns the collection of role constraints in this
   * class definition.
   *
   * @return A collection of AssociationRoleConstraint objects.
   */
  public Collection getRoleConstraints(OSLSchema schema, AssociationIF assoc) {
    // find appropriate class
    AssociationClass klass = (AssociationClass) findClass(assoc, schema.getAssociationClasses());
    if (klass == null)
      return Collections.EMPTY_LIST;
    return klass.getRoleConstraints();
  }

  /**
   * INTERNAL: Returns a collection of association types that can
   * be played by the given topic.
   *
   * @return A collection of TopicIF objects.
   */
  public Collection getAllowedAssocTypes(OSLSchema schema, TopicIF topic) {
    Set result = new HashSet();
    Collection topicTypes = hierUtils.getSupertypes(topic);
    Collection assocClasses = schema.getAssociationClasses();
    Iterator itAC = assocClasses.iterator();
    while (itAC.hasNext()) {
      AssociationClass klass = (AssociationClass) itAC.next();
      Collection assocTypes = this.getMatchingTopics(topic.getTopicMap(),
                                                     klass.getTypeSpecification());
      Collection roleCons = klass.getRoleConstraints();
      // System.out.println("getAllowedAssocTypes for topic: " + topic +
      //                   "\n  roleCons: " + roleCons + "\n----------");
      Iterator itRC = roleCons.iterator();
      while (itRC.hasNext()) {
        AssociationRoleConstraint con = (AssociationRoleConstraint) itRC.next();
        TypeSpecification typeSpec = con.getTypeSpecification();
        Collection types = this.getMatchingTopics(topic.getTopicMap(), typeSpec);
        if (topicTypes.containsAll(types))
          result.addAll(assocTypes);
      } // while itRC
    } // while itAC
    return result;
  }
  
  /**
   * INTERNAL: Get the allowed association role type for the given
   * association type with the help of the schema.
   */
  public TopicIF getRoleType4AssocType(OSLSchema schema, TopicIF assocType,
                                       TopicIF player) {
    if (assocType == null)
      return null;
    Collection playerTypes = hierUtils.getSupertypes(player);
    Collection assocClasses = schema.getAssociationClasses();
    Iterator itAC = assocClasses.iterator();
    while (itAC.hasNext()) {
      AssociationClass klass = (AssociationClass) itAC.next();
      Collection assocTypes = this.getMatchingTopics(assocType.getTopicMap(),
                                                     klass.getTypeSpecification());
      // figure out if that's the association type we have information about
      if (assocTypes.contains(assocType)) {
        Collection roleCons = klass.getRoleConstraints();
        Iterator itRC = roleCons.iterator();
        while (itRC.hasNext()) {
          AssociationRoleConstraint con = (AssociationRoleConstraint) itRC.next();
          TypeSpecification typeSpec = con.getTypeSpecification();
          Collection roleTypes = this.getMatchingTopics(assocType.getTopicMap(), typeSpec);
          Iterator itRT = roleTypes.iterator();
          while (itRT.hasNext()) {
            TopicIF curRoleType = (TopicIF) itRT.next();
            if (playerTypes.contains(curRoleType))
              return curRoleType;
          } // while itRT
        } // while itRC
      }
    } // while itAC
    return null;
  }
  
  /**
   * INTERNAL: Returns a set of topic objects which belong to role types
   * that are allowed to add to the given association.
   *
   * @return A TypeStorage object containing a collection of TopicIF objects.
   */
  public TypeStorage getRemainingRoleTypes(OSLSchema schema, AssociationIF assoc) {
    Collection roleCons = this.getRoleConstraints(schema, assoc);
    Set remRoleTypes = new HashSet();
    boolean untypedIncluded = false;
    if (roleCons.size() > 0) {
      // get all types of all roles of the given association
      Collection assocRoleTypes = new ArrayList();
      Iterator itRT = assoc.getRoles().iterator();
      while (itRT.hasNext())
        assocRoleTypes.add(((AssociationRoleIF) itRT.next()).getType());
      // loop over all association role constraints
      Iterator itC = roleCons.iterator();
      while (itC.hasNext()) {
        AssociationRoleConstraint constraint = (AssociationRoleConstraint) itC.next();
        // int min = constraint.getMinimum();
        int max = constraint.getMaximum();
        if (max == CardinalityConstraintIF.INFINITY)
          max = Integer.MAX_VALUE;
        TypeSpecification typeSpec = constraint.getTypeSpecification();
        Collection types = this.getMatchingTopics(assoc.getTopicMap(), typeSpec);
        int curNumber = this.getObjectsOfType(assocRoleTypes, types).size();
        // check if type can be used for a new role theoretically
        if (curNumber < max) {
          // add the type to the allowed set of role types
          Iterator itT = types.iterator();
          while (itT.hasNext())
            remRoleTypes.add((TopicIF) itT.next());
          if (types.isEmpty())
            untypedIncluded = true;
        }
      } // while itC
    }
    return new TypeStorage(remRoleTypes, untypedIncluded);
  }
  


  
  // ---------------------------------------------------------------------
  // internal helper methods
  // ---------------------------------------------------------------------

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
  
  protected ConstraintIF findClass(TMObjectIF object, Collection classes) {
    Iterator it = classes.iterator();
    while (it.hasNext()) {
      ConstraintIF candidate = (ConstraintIF) it.next();
      if (candidate.matches(object))
        return candidate;
    }

    return null;
  }


  /**
   * INTERNAL: Return all objects that are in the given scope.
   */
  protected Collection getCharacteristicsInScope(Collection objects, Collection scope) {
    Iterator it = objects.iterator();
    Collection objectsInScope = new ArrayList();
    while (it.hasNext()) {
      ScopedIF scopedObj = (ScopedIF) it.next();
      if (scopedObj.getScope().equals(scope))
        objectsInScope.add(scopedObj);
    }
    return objectsInScope;
  }

  /**
   * INTERNAL: Return all typing objects that are equal to one
   * of the given types.
   */
  protected Collection getObjectsOfType(Collection allTypes, Collection curTypes) {
    Collection objectsOfType = new ArrayList();
    Iterator itA = allTypes.iterator();
    while (itA.hasNext()) {
      Object objAT = itA.next();
      if (objAT == null)
        continue;
      Iterator itB = curTypes.iterator();
      while (itB.hasNext()) {
        Object objCT = itB.next();
        if (objAT.equals(objCT))
          objectsOfType.add(objCT);
      } // while itB
    } // while itA
    return objectsOfType;    
  }
  
}
