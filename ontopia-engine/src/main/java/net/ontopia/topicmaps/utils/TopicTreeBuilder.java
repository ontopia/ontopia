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

import java.util.Iterator;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;

/**
 * EXPERIMENTAL.
 * @since 1.2
 */
public class TopicTreeBuilder {
  protected TopicIF assocType;
  protected TopicIF parentRole;
  protected TopicIF childRole;
  protected Collection types;
  
  public TopicTreeBuilder(TopicIF assocType,
                          TopicIF parentRole,
                          TopicIF childRole) {
    if (assocType == null)
      throw new NullPointerException("Association type cannot be null");
    if (parentRole == null)
      throw new NullPointerException("Parent role type cannot be null");
    if (childRole == null)
      throw new NullPointerException("Child role type cannot be null");
    this.assocType = assocType;
    this.parentRole = parentRole;
    this.childRole = childRole;
    this.types = null;
  }

  public void setFilterTypes(Collection types) {
    this.types = types;
  }
  
  /**
   * EXPERIMENTAL: Builds a tree consisting of all the topics directly
   * related to the one given. The node given can be placed at any
   * position in the tree.
   */
  public TopicTreeNode build(TopicIF topic) {
    TopicMapIF tm = assocType.getTopicMap();

    TopicIF root = getRoot(topic, new HashSet());
    return build(root, new HashSet());
  }
       
  /**
   * EXPERIMENTAL: Builds a tree consisting of all the topics
   * participating in any associations of the given type. A node with
   * a null topic will be introduced as the common root of all the
   * subtrees.
   */
  public TopicTreeNode build() {
    TopicTreeNode falseroot = new TopicTreeNode(null);
    TopicMapIF tm = assocType.getTopicMap();
    ClassInstanceIndexIF index = (ClassInstanceIndexIF) tm.getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");
    Map trees = new HashMap(); // trees, registered by root topic

    Iterator it = index.getAssociations(assocType).iterator();
    while (it.hasNext()) {
      AssociationIF assoc = (AssociationIF) it.next();
      TopicIF parent = getPlayer(assoc, parentRole);
      if (parent == null || !filter(parent))
        continue;

      TopicIF root = getRoot(parent, new HashSet());
      if (trees.containsKey(root))
        continue;

      TopicTreeNode rootnode = build(root, new HashSet());
      trees.put(root, rootnode);
      falseroot.getChildren().add(rootnode);
    }

    return falseroot;
  }

  // --- Internal methods

  protected TopicIF getRoot(TopicIF topic, Set visited) {
    visited.add(topic);
    Iterator it = topic.getRoles().iterator();
    while (it.hasNext()) {
      AssociationRoleIF role = (AssociationRoleIF) it.next();
      if (!childRole.equals(role.getType()) ||
          !assocType.equals(role.getAssociation().getType()))
        continue;

      TopicIF parent = getPlayer(role.getAssociation(), parentRole);
      if (parent == null || !filter(parent))
        continue;

      // if we've made a loop, consider this the root
      if (visited.contains(parent))
        return parent;
      
      TopicIF root = getRoot(parent, visited);
      return root;
    }

    return topic;
  }
  
  protected TopicTreeNode build(TopicIF topic, Set visited) {
    TopicTreeNode node = new TopicTreeNode(topic);
    if (visited.contains(topic))
      return node; // we've already been here, so don't look for children again
    
    visited.add(topic);

    Iterator it = topic.getRoles().iterator();
    while (it.hasNext()) {
      AssociationRoleIF role = (AssociationRoleIF) it.next();
      if (!parentRole.equals(role.getType()) ||
          !assocType.equals(role.getAssociation().getType()))
        continue;

      TopicIF child = getPlayer(role.getAssociation(), childRole);
      if (child == null || !filter(child))
        continue;

      node.getChildren().add(build(child, visited));     
    }

    return node;
  }

  protected TopicIF getPlayer(AssociationIF assoc, TopicIF roleType) {
    Iterator it = assoc.getRoles().iterator(); 
    while (it.hasNext()) {
      AssociationRoleIF role = (AssociationRoleIF) it.next();
      if (roleType.equals(role.getType()))
        return role.getPlayer();
    }
    return null;
  }

  protected boolean filter(TopicIF topic) {
    if (types == null)
      return true;
    
    Iterator it = topic.getTypes().iterator();
    while (it.hasNext())
      if (types.contains(it.next()))
        return true;

    return false;
  }
}
