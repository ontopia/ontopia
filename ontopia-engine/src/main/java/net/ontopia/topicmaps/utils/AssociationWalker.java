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

package net.ontopia.topicmaps.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.function.Predicate;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;

/**
 * PUBLIC: Computes the transitive closure of a relation characterized by
 * two specific roles within a specific association type.</p>
 *
 * The relation is characterized by an association type A and a pair
 * of roles R1 & R2 such that two topics T1 and T2 are related if T1
 * plays role R1 and T2 plays role R2 in an association A.
 *
 * A <i>transitive</i> relation is where "x is related to
 * y" and "y is related to z" always implies that "x is related to
 * z" Here, an association type, together with two roles
 * within that type of association, is taken as the relation 
 * which is transitive.</p>
 *
 * In topic map terms, if: T1 plays role R1 in association A1 and T2
 * plays role R2 in association A1 and: T2 plays role R1 in
 * association A2 and T3 plays role R2 in association A2 then: T1 and
 * T3 are also (transitively) related.</p>
 */
public class AssociationWalker {
  /**
   * PROTECTED: The decider used to filter associations to only those
   * which are being walked
   */
  protected Predicate<AssociationIF> assocDecider;
  /**
   * PROTECTED: The decider used to filter the left-hand role of the
   * transitive association
   */
  protected Predicate<AssociationRoleIF> leftRoleDecider;
  /**
   * PROTECTED: The decider used to filter the right-hand role of the
   * transitive association.
   */
  protected Predicate<AssociationRoleIF> rightRoleDecider;

  /**
   * PROTECTED: The listeners to be informed as the walker processes
   * the topic map.
   */
  protected List<AssociationWalkerListenerIF> listeners;
    
  /**
   * PUBLIC: Creates a walker which determines that a topic A is
   * related to topic B if A plays a role specified by
   * <code>leftRoleSpec</code> in an association of type
   * <code>associationType</code> and topic B plays a role specified
   * by <code>rightRoleSpec</code> in the same association.
   * 
   * @param associationType The given association type; an object
   * implementing TopicIF.
   * @param leftRoleSpec The first given association rolespec; an
   * object implementing TopicIF.
   * @param rightRoleSpec The second given association rolespec; an
   * object implementing TopicIF.
   */
  public AssociationWalker(TopicIF associationType, TopicIF leftRoleSpec, TopicIF rightRoleSpec) {
    assocDecider = a -> associationType.equals(a.getType());
    leftRoleDecider = r -> leftRoleSpec.equals(r.getType());
    rightRoleDecider = r -> rightRoleSpec.equals(r.getType());
    listeners = new ArrayList<AssociationWalkerListenerIF>();
  }

  /**
   * PUBLIC: Creates a walker which uses deciders to traverse the associations.
   *
   * @param assocDecider ; an object implementing DeciderIF.
   * @param fromRoleDecider ; an object implementing DeciderIF.
   * @param toRoleDecider ; an object implementing DeciderIF.
   */
  public AssociationWalker(Predicate<AssociationIF> assocDecider, Predicate<AssociationRoleIF> fromRoleDecider, Predicate<AssociationRoleIF> toRoleDecider) {
    this.assocDecider = assocDecider;
    leftRoleDecider = fromRoleDecider;
    rightRoleDecider = toRoleDecider;
    listeners = new ArrayList<AssociationWalkerListenerIF>();
  }
    

  /**
   * PUBLIC: Computes the transitive closure under the association
   * type and rolespec definitions provided in the constructor, and
   * returns the result as a set of topics.
   *
   * @param start The topic to start the computation from; an object
   * implementing TopicIF.
   * @return An unmodifiable Set of TopicIF objects; the topics
   * present in the closure.
   */
  public Set<TopicIF> walkTopics(TopicIF start) {
    WalkerState state = walk(start, false);
    return Collections.unmodifiableSet(state.closure);
  }

    
  /**
   * PUBLIC: Computes the transitive closure under the association
   * type and rolespec definitions provided in the constructor, and
   * returns a set containing the paths taken through the topic map in
   * computing the closure. Each path is a list consisting of
   * alternating TopicIF and AssociationIF entries.  The element at
   * the start of the list is the starting TopicIF. The following
   * AssociationIF is an association in which the TopicIF plays the
   * specified left-hand role. The next node is a TopicIF which plays
   * the specified right-hand role in the association and so on.  The
   * walker algorithm avoids cycles by cutting off paths as soon as a
   * duplicate topic is encountered.
   *
   * @param start The topic to start the computation from; an object
   * implementing TopicIF.
   * @return An unmodifiable Collection of List objects.
   */
  public Collection<List<TMObjectIF>> walkPaths(TopicIF start) {
    WalkerState state = walk(start, true);
    return Collections.unmodifiableCollection(state.paths);
  }
    
  /**
   * PROTECTED: Computes the transitive closure under the association
   * type and rolespec definitions provided in the constructor; this
   * method is used by both walkTopics and walkPaths. If the
   * <code>storePaths</code> parameter is <code>false</code> then the
   * walker will collect only the set of topics which form the
   * transitive closure and will not store the individual paths
   * discovered.
   *
   * @param start The topic to start the computation from; an object
   * implementing TopicIF.
   * @param storePaths Boolean: if true, store paths walked; if
   * false, store only topics found.
   * @return A WalkerState object; the state of the walk at completion.
   */
  protected WalkerState walk(TopicIF start, boolean storePaths) {
    WalkerState state = new WalkerState(start, storePaths);
    doWalk(start, state);
    return state;
  }

  /**
   * PRIVATE: Iterates through the roles played by fromTopic which are
   * of the type defined as the leftRoleSpec in the constructor, then
   * for each role, grabs the association and iterates through the
   * roles which are of the type defined as the rightRoleSpec in the
   * constructor. The heart of the walker function.
   *
   * <p> Whenever a (left-role-player, association, right-role-player)
   * triple are found, the walkAssociation() member function is
   * invoked; the association and right-role-player are appended to
   * the current tree path; and the function is called recursively to
   * walk from the right-role-player.
   *
   * <p> Whenever no right-role-players are found, the current tree
   * path is added to the set of processed tree paths and the
   * recursion unwinds.  Cycles are avoided by never recursively
   * processing a right-role-player if it is already part of the
   * closure.
   * 
   * @param fromTopic The topic from which to start the walk; an
   * object implementing TopicIF.
   * @param state A WalkerState object which contains the final
   * state at the end of the walk.
   */
  private void doWalk(TopicIF fromTopic, WalkerState state) {
    // ignore if from topic is null
    if (fromTopic == null) return;

    Collection<AssociationRoleIF> fromRoles = fromTopic.getRoles();
    if (fromRoles.isEmpty()) {
      foundLeaf(state);
    } else {
      Iterator<AssociationRoleIF> leftRolesIt = fromRoles.stream().filter(leftRoleDecider).iterator();
      if (!leftRolesIt.hasNext()) {
        foundLeaf(state);
      }
      while (!state.foundTopic && leftRolesIt.hasNext()) {
        AssociationRoleIF leftRole = leftRolesIt.next();
        AssociationIF assoc = leftRole.getAssociation();
        if (assocDecider.test(assoc)) {
          Collection<AssociationRoleIF> assocRoles = assoc.getRoles();
          Iterator<AssociationRoleIF> rightRolesIt = assocRoles.stream().filter(rightRoleDecider).iterator();
          if (!rightRolesIt.hasNext()) {
            // We have traversed to a leaf. Add the current path to the tree set
            foundLeaf(state);
          } else {
            // This association is another node in the tree so we can add it to
            // the current path and then traverse it.
            state.pushPath(assoc);
            while (!state.foundTopic && rightRolesIt.hasNext()) {
              AssociationRoleIF rightRole = rightRolesIt.next();
              TopicIF rightPlayer = rightRole.getPlayer();
              state.pushPath(rightPlayer);
              if (state.closure.contains(rightPlayer)) {
                                // Reached a topic which we have already traversed.
                foundLeaf(state);
              } else {
                state.closure.add(rightPlayer);
                notifyListeners(fromTopic, assoc, rightPlayer);
                if ((state.toTopic != null) && (state.toTopic.equals(rightPlayer))) {
                  state.foundTopic = true;
                  return;
                } else {
                  doWalk(rightPlayer, state);
                }
              }
              state.popPath();
            }
            state.popPath();
          }
        }
      }
    }
  }

  /**
   * PROTECTED: Invoked when the walker encounters the end of a
   * transitive association path.  This function is used to store the
   * association path for later retrieval.  If the current association
   * path is a singleton, it is not stored.
   * 
   * @param state A WalkerState object; the current state of the walk.
   */
  protected void foundLeaf(WalkerState state) {
    if (state.storePaths && (state.currPath.size() > 1)) {
      state.addCurrPath();
    }
  }
    
            
  /**
   * PUBLIC: Returns true if the two topics are directly or indirectly
   * associated under the association type and rolespec definitions
   * provided in the constructor for this walker.  The calculation is
   * performed using a depth-first traversal of the tree formed by the
   * associations concerned, which aborts as soon as the associated
   * topic is found.
   *
   * @param start The topic to begin computation from; an object implementing TopicIF.
   * @param associated The topic to be found in the association; an object implementing TopicIF.
   *
   * @return Boolean: true iff the given topics are directly or indirectly associated
   */
  public boolean isAssociated(TopicIF start, TopicIF associated) {
    WalkerState state = new WalkerState(start, false);
    state.toTopic = associated;
    doWalk(start, state);
    return state.foundTopic;
  }

  /**
   * PUBLIC: Registers a listener with the walker. The listener will
   * be notified each time the walker encounters a topic, association,
   * associated-topic triple.
   *
   * @param listener The listener to be registered; an object
   * implementing AssociationWalkerListenerIF.
   * @see AssociationWalkerListenerIF
   */
  public void addListener(AssociationWalkerListenerIF listener) {
    listeners.add(listener);
  }

  /**
   * PUBLIC: Unregisters a listener with the walker.
   *
   * @param listener The listener to be unregistered; an object
   * implementing AssociationWalkerListenerIF.
   * @see AssociationWalkerListenerIF
   */
  public void removeListener(AssociationWalkerListenerIF listener) {
    listeners.remove(listener);
  }
    
  /**
   * PRIVATE: This function is invoked by the walk() function, for each 
   * topic, association, associated-topic triple found during
   * the computation of the transitive closure.
   * It notifies each registered listener of the triple encountered.
   *
   * @param leftRolePlayer The first topic in the triple; an object implementing TopicIF.
   * @param assoc           The association in the triple; an object implementing AssociationIF.
   * @param rightRolePlayer The second topic in the triple; an object implementing TopicIF.
   */
  private void notifyListeners(TopicIF leftRolePlayer, AssociationIF assoc, TopicIF rightRolePlayer) {
    Iterator<AssociationWalkerListenerIF> it = listeners.iterator();
    while (it.hasNext()) {
      AssociationWalkerListenerIF listener = it.next();
      listener.walkAssociation(leftRolePlayer, assoc, rightRolePlayer);
    }
  }
}

/**
 * PRIVATE: A simple data structure which maintains the state of a walk.
 */
class WalkerState {
  /**
   * PROTECTED: The topics which form the transitive closure.
   * This variable is null if no walk has been
   * performed.
   */
  protected Set<TopicIF> closure;

  /**
   * PROTECTED: The paths followed by the last walk. The storage of
   * the paths in the transitive closure is optional. This variable is
   * null if no walk has been performed or if the last walk was
   * invoked without storing paths.
   */
  protected Collection<List<TMObjectIF>> paths;

  /**
   * PROTECTED: The tree path currently being walked.
   */
  protected Stack<TMObjectIF> currPath;

  /**
   * PROTECTED: The topic to start walking from
   */
  protected TopicIF startTopic;
    
  /**
   * PROTECTED: The topic to be located by the walk.
   */
  protected TopicIF toTopic;
    
  /**
   * PROTECTED: Flag indicating if the walk has found the topic it was
   * looking for.
   */
  protected boolean foundTopic;

  /**
   * PROTECTED: Flag indicating whether to store the paths found
   * through the associated topics set.
   */
  protected boolean storePaths;

  protected WalkerState(TopicIF start, boolean storePaths) {
    startTopic = start;
    this.storePaths = storePaths;
    if (storePaths) paths = new ArrayList<List<TMObjectIF>>();
    currPath = new Stack<TMObjectIF>();
    currPath.push(start);
    foundTopic = false;
    toTopic = null;
    closure = new HashSet<TopicIF>();
  }
    
  protected void addCurrPath() {
    paths.add(new ArrayList<TMObjectIF>(currPath));
  }
    
  protected void pushPath(TMObjectIF lastElement) {
    if (storePaths) currPath.push(lastElement);
  }

  protected void popPath() {
    if (storePaths) currPath.pop();
  }
    
}
