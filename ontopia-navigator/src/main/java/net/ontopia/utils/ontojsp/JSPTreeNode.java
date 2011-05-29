
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
  protected List children;
  protected Map attr;
  protected TagSupport tag;

  public JSPTreeNode(String name, JSPTreeNodeIF parent) {
    this.name = name;
    this.children = new ArrayList();
    this.attr = new HashMap();
    this.parent = parent;
    this.tag = null; // will be set later using setTag
  }

  public Map getAttributes() {
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

  public List getChildren() {
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
    JSPTreeNode clone = new JSPTreeNode(name, null); // parent will set parent, if any
    clone.attr = attr;

    // clone tag, too
    if (tag != null) {
      try {
        clone.setTag((TagSupport) tag.getClass().newInstance());
      } catch (Exception e) {
        throw new OntopiaRuntimeException(e);
      }
    }

    // clone children
    for (int ix = 0; ix < children.size(); ix++) {
      JSPTreeNodeIF child = (JSPTreeNodeIF) children.get(ix);

      JSPTreeNodeIF childclone = child.makeClone();
      clone.addChild(childclone); // add to parent
      childclone.setParent(clone); // set parent
    }

    return clone;
  }
}
