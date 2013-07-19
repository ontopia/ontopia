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

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ontopia.topicmaps.core.TopicIF;

/**
 * EXPERIMENTAL.
 * @since 1.2
 */
public class TopicTreeNode {
  // initialization of logging facility
  private static Logger log = LoggerFactory.getLogger(TopicTreeNode.class
      .getName());

  protected TopicTreeNode parent;
  protected List children;
  protected TopicIF topic;
  protected HashMap attributes;

  public TopicTreeNode(TopicIF topic) {
    this.topic = topic;
    this.children = new ArrayList();
    this.attributes = new HashMap();
  }

  /**
   * Returns a List containing TopicTreeNode objects.
   */
  public List getChildren() {
    return children;
  }

  public TopicTreeNode getParent() {
    return parent;
  }

  public void setParent(TopicTreeNode parent) {
    if (this.parent != null)
      this.parent.getChildren().remove(this);
    this.parent = parent;
    
    if (this.parent != null)
      this.parent.getChildren().add(this);
  }
  
  public TopicIF getTopic() {
    return topic;
  }

  public void setAttribute(String name, Object value) {
    attributes.put(name, value);
  }

  public Object getAttribute(String name) {
    return attributes.get(name);
  }
}
