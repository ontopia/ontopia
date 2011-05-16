
// $Id: ListTag.java,v 1.49 2007/07/13 19:05:54 eirik.opland Exp $

package net.ontopia.topicmaps.webed.taglibs.form;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.nav2.core.NavigatorPageIF;
import net.ontopia.topicmaps.nav2.impl.framework.InteractionELSupport;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;
import net.ontopia.topicmaps.nav2.utils.Stringificator;
import net.ontopia.topicmaps.webed.impl.utils.TagUtils;

import org.apache.velocity.VelocityContext;

/**
 * INTERNAL: Custom tag that represents a list to select one or
 * multiple values from (aka drop-down and scrolling menus) in an
 * input form.
 */
public class ListTag extends TagSupport {

  // initialization of logging facility
  private static final String CATEGORY_NAME = ListTag.class.getName();
  
  /**
   * The default location where the velocity template can be retrieved
   * from.
   */
  protected final static String TEMPLATE_FILE = "list.vm";

  // --- Tag Attributes
  
  protected String id;
  protected String readonly;
  protected String klass;
  protected String action_name;
  protected String params;
  protected String collection_name;
  protected String selected_name;
  protected String type = "dropdown";   // default type
  protected String unspecified = "unspecified"; // default label
  
  /**
   * Process the start tag, do nothing.
   *
   * @return <code>EVAL_BODY_INCLUDE</code>
   */
  public int doStartTag() {
    return EVAL_BODY_INCLUDE;
  }
  
  /**
   * Generate the required select tag.
   *
   * @exception JspException if a JSP exception has occurred
   */
  public int doEndTag() throws JspException {
    // original value; used to detect changes
    Set value = new HashSet();
    
    // already selected objects
    Collection selectedObjs = java.util.Collections.EMPTY_LIST;
    if (selected_name == null)
      value.add("-1"); // equals choosing "-- unspecified --"
    else {
      selectedObjs = InteractionELSupport
          .extendedGetValue(selected_name, pageContext);
      

      // create collection to store in
      Iterator it = selectedObjs.iterator();
      while (it.hasNext())
        value.add(((TMObjectIF) it.next()).getObjectId());
      if (value.isEmpty())
        value.add(null); // this is how empty sets look to ProcessServlet
    }

    // multiselect is a special case; if nothing's selected there, then
    // null is the value we get, not -1; see bug #1252
    if ((type.equals("multiselect") || type.equals("scrolling")) && selected_name == null)
      value = java.util.Collections.singleton(null);

    boolean readonly = TagUtils.isComponentReadOnly(pageContext, this.readonly);
        
    VelocityContext vc = TagUtils.getVelocityContext(pageContext);

    // register action data and produce input field name
    if (action_name != null && !readonly) {
      // retrieve the action group
      String group_name = TagUtils.getActionGroup(pageContext);
      if (group_name == null)
        throw new JspException("list tag has no action group available.");
      
      String name = TagUtils.registerData(pageContext, action_name, group_name,
                                          params, value);

      if (!(type.equals("multiselect") || type.equals("scrolling"))&& !unspecified.equals("none"))
        vc.put("unspecified", unspecified);
      
      vc.put("name", name);
    }    

    if (id != null) vc.put("id", id);
    vc.put("readonly", new Boolean(readonly));
    if (klass != null) vc.put("class", klass);
    vc.put("type", type);

    // retrieve the elements
    Collection elements = new ArrayList();
    // since collection_name is a required attribute no extra validation needed
    NavigatorPageIF contextTag = FrameworkUtils.getContextTag(pageContext);
    if (contextTag == null)
      throw new JspException("List tag found no logic:context ancestor tag");
    
    Collection tmObjs = InteractionELSupport.extendedGetValue(collection_name,
        pageContext);
    
    Iterator iter = tmObjs.iterator();
    while (iter.hasNext()) {
      Object obj = iter.next();
      if (!(obj instanceof TMObjectIF))
        throw new JspException("Collection in variable " + collection_name +
                               "contained non-topic map object: " + obj);
      
      TMObjectIF tmObj = (TMObjectIF) obj;
      elements.add(new NameIdContainer(tmObj.getObjectId(),
                                       Stringificator.toString(contextTag, tmObj),
                                       selectedObjs.contains(tmObj)));
    }
    vc.put("elements", elements);
    
    // all variables are now set, proceed with outputting
    TagUtils.processWithVelocity(pageContext, TEMPLATE_FILE, pageContext.getOut(), vc);

    // Continue processing this page
    return EVAL_BODY_INCLUDE;
  }

  /**
   * Release any acquired resources.
   */
  public void release() {
    super.release();
    id = null;
    readonly = null;
    action_name = null;
    params = null;
    collection_name = null;
    selected_name = null;
    type = "dropdown";
  }
  
  // ------------------------------------------------------------
  // tag attribute accessors
  // ------------------------------------------------------------
  
  /**
   * Sets the id of the tag. This value will be used as the value of
   * an ID attribute in the generated output.
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Sets the the readonly flag of the tag.
   */
  public void setReadonly(String readonly) {
    this.readonly = readonly;
  }
  
  /**
   * Sets the class attribute of the tag. This value will be used as
   * the value of the 'class' attribute in the generated output.
   */
  public void setClass(String klass) {
    this.klass = klass;
  }

  /**
   * Sets the name of the related action (required).
   */
  public void setAction(String action_name) {
    this.action_name = action_name;
  }
  
  /**
   * Sets the variable name(s) of the parameter(s) transmitted to the
   * action separated by whitespaces (optional).
   */
  public void setParams(String params) {
    this.params = params;
  }
  
  /**
   * Sets the variable name of the collection that contains the
   * candidates for the object's value.
   */
  public void setCollection(String collection_name) {
    this.collection_name = collection_name;
  }
  
  /**
   * Sets the variable name of the currently set value.
   */
  public void setSelected(String selected_name) {
    this.selected_name = selected_name;
  }
  
  /**
   * Sets which type of menu should be used when presenting the
   * selection list (allowed values are "dropdown" and "scrolling").
   *   
   * <p>Note: If 'type' is not set, "dropdown" will be the default
   * behaviour.
   */
  public void setType(String type) {
    if (type.equalsIgnoreCase("dropdown")
        || type.equalsIgnoreCase("scrolling")
        || type.equalsIgnoreCase("multiselect")
        || type.equalsIgnoreCase("checkbox")
        || type.equalsIgnoreCase("radio"))
      this.type = type;
    else
      throw new IllegalArgumentException("ListTag: type '" + type + "' " +
                                         "is not an allowed value.");
  }

  /**
   * Tells the tag what text to use for the '-unspecified-' item at
   * the top of the list. If set to 'none' there will be no
   * unspecified item. The default is '-unspecified-' as it was
   * before.
   */
  public void setUnspecified(String unspecified) {
    this.unspecified = unspecified;
  }

  // -------------------------------------------------------------------
  // Internal class which holds one information element for the list,
  // this makes access from the velocity template very easy.
  // -------------------------------------------------------------------

  public final class NameIdContainer {
    
    private String id;
    private String name;
    private boolean selected;

    public NameIdContainer(String id, String name, boolean selected) {
      this.id = id;
      this.name = name;
      this.selected = selected;
    }
    
    public void setId(String id) {
      this.id = id;
    }

    public String getId() {
      return id;
    }
    
    public void setName(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
    
    public void setSelected(boolean selected) {
      this.selected = selected;
    }

    public boolean getSelected() {
      return selected;
    }

  }
  
}
