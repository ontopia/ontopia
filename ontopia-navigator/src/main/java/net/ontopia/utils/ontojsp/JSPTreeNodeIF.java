
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
  public Map getAttributes();

  /**
   * Add a attribute to this JSPTreeNode.
   */
  public void addAttribute(String key, String value);

  /**
   * Sets the name of the tag which this JSPTreeNode represents.
   */
  public void setTagName(String tagName);

  /**
   * Gets the tag name that this JSPTreeNode represents.
   */
  public String getTagName();
  
  /**
   * Sets the tag which this JSPTreeNode represents.
   */
  public void setTag(TagSupport tag);

  /**
   * Gets the tag that this JSPTreeNode represents.
   */
  public TagSupport getTag();
  
  /**
   * Gets the parent node of this JSPTreeNode.
   */
  public JSPTreeNodeIF getParent();

  /**
   * Sets the parent node of this JSPTreeNode.
   */
  public void setParent(JSPTreeNodeIF parentNode);

  /**
   * Adds a child node to this JSPTreeNode.
   */
  public void addChild(JSPTreeNodeIF node);

  /**
   * Gets the children (ordered) of this JSPTreeNode.
   */
  public List getChildren();

  /**
   * The string content attached to this JSPTreeNode.
   */
  public String getContent();

  /**
   * A string representation of this JSPTreeNode.
   */
  public String toString();

  /**
   * Returns a node of the same class with the same internal state,
   * but with a different tag object internally. Calling this method
   * 'makeClone' instead of 'clone' so we can override the return type
   * (we want Eiffel, NOW!).
   */
  public JSPTreeNodeIF makeClone();
}




