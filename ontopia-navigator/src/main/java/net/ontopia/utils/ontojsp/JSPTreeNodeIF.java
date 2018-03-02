/*
 * #!
 * Ontopia Navigator
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

package net.ontopia.utils.ontojsp;

import java.util.List;
import java.util.Map;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * INTERNAL: Interface for classes which implement the Ontopia
 * JSPTreeNodeIF. This class is the node of the JSPTree build by the
 * JSPContentHandler.
 *
 * @see net.ontopia.utils.ontojsp.JSPContentHandler
 */ 
public interface JSPTreeNodeIF {

  /**
   * Returns the attributes for this JSPTreeNode.
   */
  Map<String, String> getAttributes();

  /**
   * Add a attribute to this JSPTreeNode.
   */
  void addAttribute(String key, String value);

  /**
   * Sets the name of the tag which this JSPTreeNode represents.
   */
  void setTagName(String tagName);

  /**
   * Gets the tag name that this JSPTreeNode represents.
   */
  String getTagName();
  
  /**
   * Sets the tag which this JSPTreeNode represents.
   */
  void setTag(TagSupport tag);

  /**
   * Gets the tag that this JSPTreeNode represents.
   */
  TagSupport getTag();
  
  /**
   * Gets the parent node of this JSPTreeNode.
   */
  JSPTreeNodeIF getParent();

  /**
   * Sets the parent node of this JSPTreeNode.
   */
  void setParent(JSPTreeNodeIF parentNode);

  /**
   * Adds a child node to this JSPTreeNode.
   */
  void addChild(JSPTreeNodeIF node);

  /**
   * Gets the children (ordered) of this JSPTreeNode.
   */
  List<JSPTreeNodeIF> getChildren();

  /**
   * The string content attached to this JSPTreeNode.
   */
  String getContent();

  /**
   * A string representation of this JSPTreeNode.
   */
  @Override
  String toString();

  /**
   * Returns a node of the same class with the same internal state,
   * but with a different tag object internally. Calling this method
   * 'makeClone' instead of 'clone' so we can override the return type
   * (we want Eiffel, NOW!).
   */
  JSPTreeNodeIF makeClone();
}




