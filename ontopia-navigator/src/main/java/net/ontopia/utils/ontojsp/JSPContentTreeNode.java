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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * INTERNAL: The Ontopia JSPTreeNode class. This class is the node of
 * the JSPTree build by the JSPContentHandler.
 *
 * @see net.ontopia.utils.ontojsp.JSPContentHandler
 */ 
public class JSPContentTreeNode implements JSPTreeNodeIF {
  
  protected String content;
  protected JSPTreeNodeIF parent;

  /**
   * Constructor that accepts the parent of this JSPTreeNode as an
   * argument. If this is the root node, the parent should null.
   */
  public JSPContentTreeNode(JSPTreeNodeIF parent, String content) {
    this.parent = parent;
    this.content = content; // content.trim();
    // logger.debug("JSPContentTreeNode(" + content + ")");
  }

  @Override
  public Map<String, String> getAttributes() {
    return Collections.emptyMap();
  }

  @Override
  public void addAttribute(String key, String value) {
    throw new UnsupportedOperationException("Content nodes can't have attributes");
  }

  @Override
  public void setTagName(String name) {
    throw new UnsupportedOperationException("Content nodes don't have tag names");
  }

  @Override
  public String getTagName() {
    return "#CONTENT";
  }

  @Override
  public TagSupport getTag() {
    return null;
  }

  @Override
  public void setTag(TagSupport tag) {
    throw new UnsupportedOperationException("Content nodes have no tags");
  }
  
  @Override
  public JSPTreeNodeIF getParent() {
    return parent;
  }

  @Override
  public void setParent(JSPTreeNodeIF parent) {
    this.parent = parent;
  }
    
  @Override
  public void addChild(JSPTreeNodeIF node) {
    throw new UnsupportedOperationException("Content nodes can't have children");
  }

  @Override
  public List<JSPTreeNodeIF> getChildren() {
    return Collections.emptyList();
  }

  @Override
  public String getContent() {
    return content;
  }
  
  public void setContent(String content) {
    this.content = content;
  }

  @Override
  public String toString() {
    String contentExtract = content;
    if (content.length() > 42) {
      contentExtract = content.substring(0, 42);
    }
    return "[JSPContentTreeNode - content: " + contentExtract +
      ", parent: <" + (parent!=null ? parent.getTagName() : "null") + ">]";
  }

  @Override
  public JSPTreeNodeIF makeClone() {
    // must clone these nodes, too, because of parent pointer
    return new JSPContentTreeNode(null, content); // parent will set parent
  }
}
