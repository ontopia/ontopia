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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.tagext.TagSupport;

import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Tree node representing a JSP tag.
 *
 * @see net.ontopia.utils.ontojsp.JSPContentHandler
 */ 
public class JSPTreeNode implements JSPTreeNodeIF {

  protected String name;
  protected JSPTreeNodeIF parent;
  protected List<JSPTreeNodeIF> children;
  protected Map<String, String> attr;
  protected TagSupport tag;
  protected final boolean dontCloneTags;

  public JSPTreeNode(String name, JSPTreeNodeIF parent) {
    this(name, parent, TaglibTagFactory.TAGPOOLING_DEFAULT);
  }

  public JSPTreeNode(String name, JSPTreeNodeIF parent, boolean dontCloneTags) {
    this.name = name;
    this.children = new ArrayList<JSPTreeNodeIF>();
    this.attr = new HashMap<String, String>();
    this.parent = parent;
    this.tag = null; // will be set later using setTag
    this.dontCloneTags = dontCloneTags;
  }

  public Map<String, String> getAttributes() {
    return attr;
  }

  public void addAttribute(String key, String value) {
    attr.put(key, value);
  }

  public void setTagName(String name) {
    this.name = name;
  }

  public String getTagName() {
    return name;
  }

  public TagSupport getTag() {
    return tag;
  }

  public void setTag(TagSupport tag) {
    this.tag = tag;
  }
  
  public JSPTreeNodeIF getParent() {
    return parent;
  }

  public void setParent(JSPTreeNodeIF parent) {
    this.parent = parent;
  }

  public void addChild(JSPTreeNodeIF node) {
    children.add(node);
  }

  public List<JSPTreeNodeIF> getChildren() {
    return children;
  }

  public String getContent() {
    // not supported
    return "";
  }
  
  public String toString() {
    return "[JSPTreeNode - tag: <" + name + ">, parent: <" +
      (parent!=null ? parent.getTagName() : "null") + ">; " +
      "children=" + children.size() + "]";
  }

  public JSPTreeNodeIF makeClone() {
    // clone node
    JSPTreeNode clone = new JSPTreeNode(name, null, dontCloneTags); // parent will set parent, if any
    clone.attr = attr;

    // clone tag, too
    if (tag != null) {
      if (dontCloneTags) {
        clone.setTag(tag);
      } else
      try {
        clone.setTag(tag.getClass().newInstance());
      } catch (Exception e) {
        throw new OntopiaRuntimeException(e);
      }
    }

    // clone children
    for (int ix = 0; ix < children.size(); ix++) {
      JSPTreeNodeIF child = children.get(ix);

      JSPTreeNodeIF childclone = child.makeClone();
      clone.addChild(childclone); // add to parent
      childclone.setParent(clone); // set parent
    }

    return clone;
  }
}
