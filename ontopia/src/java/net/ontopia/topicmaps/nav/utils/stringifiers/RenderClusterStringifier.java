// $Id: RenderClusterStringifier.java,v 1.17 2004/11/12 11:47:19 grove Exp $

package net.ontopia.topicmaps.nav.utils.stringifiers;

import java.util.*;

import net.ontopia.topicmaps.nav.utils.*;
import net.ontopia.topicmaps.nav.utils.structures.*;
import net.ontopia.utils.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * INTERNAL: Stringifier for a ClusterIF which takes a render object. 
 *
 * <p>Currently supports the following templates and slots
 * <ul>
 * <li>html: outputs an HTML table. Accepts slots: title, bracket, description, icon.
 * <li>text: outputs an in line text version. Accepts slots: title, bracket, description, icon.
 * <li>checkbox: outputs a checkbox form element (experimental). Accepts slots: title, id.
 *
 * <p>Alternate implementations
 * of this class should use an empty constructor and ensure that an instantiation
 * is held in APLLICATION_SCOPE to ensure that blocks are stringifed as
 * efficiently as possible.
 */ 
public class RenderClusterStringifier implements StringifierIF {

  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(RenderClusterStringifier.class.getName());

  final static String OPEN_BRACKET  = "(";
  final static String CLOSE_BRACKET = ")";
  
  /** 
   * Empty constructor used by configuration objects on application startup.
   */
  public RenderClusterStringifier() {
  }
  
  /**
   * Returns a String if a Render object is submitted.
   */
  public String toString(Object object) {
    Render render;
    try {
      render = (Render) object;
    } catch (ClassCastException e) {
      throw new OntopiaRuntimeException("RenderClusterStringifier Error: A Render object was not passed into toString()");
    }
    
    String template = render.getTemplate();
    String name = render.getName();
    Map strings = render.getStrings();
  
    String title = (String) strings.get("title");
    String bracket = (String) strings.get("bracket");
    String description = (String) strings.get("description");
    String icon = (String) strings.get("icon");
    String more = (String) strings.get("more");
    String sid = (String) strings.get("id");
    String so1 = (String) strings.get("o1");
    String so2 = (String) strings.get("o2");
    String so3 = (String) strings.get("o3");
    
    StringBuffer s = new StringBuffer(128);

    // need a template
    if (template == null)
      return "";

    // ----- template: default (html) --------------------------------------
    if (template.equals("html") || template.equals("")) {
      if (title.equals(""))
        return "";
      
      // table: icon, title, bracket, description, more
      s.append("\n<table class='entryItem' cellspacing='0'><tr class='entryItemRow'><td class='entryItemImageCell'>");
      if (icon != null && !icon.equals(""))
        s.append(icon);
      else 
        s.append("<img src='images/bullet.gif' alt='*' width='16' height='16' />");

      s.append("</td>\n<td class='entryItemCell'>");

      if (title!=null)
        s.append(title);

      if (bracket!=null && !bracket.equals("")) {
        s.append(" "+OPEN_BRACKET).append(bracket).append(CLOSE_BRACKET);
      }
      if (more != null) {
        s.append("&nbsp;&nbsp;").append(more); 
      }
      if (description!=null && !description.equals("")) {
        s.append("<br />").append(description);
      }
      s.append("\n</td></tr></table>\n");      
    }
    // ----- template: text ------------------------------------------------
    else if (template.equals("text")) {
      if (title.equals(""))
        return "";
      if (icon != null)
        s.append(icon);
      if (title != null) 
        s.append(title);
      if (bracket != null && !bracket.equals("")) {
        s.append(" "+OPEN_BRACKET).append(bracket).append(CLOSE_BRACKET);
      }
      if (description != null)
        s.append(description);
    }
    // ----- template: checkbox --------------------------------------------
    else if (template.equals("checkbox")){
      if (title.equals(""))
        return "";
      // checkbox
      s.append("<input type='checkbox'");
      if (name != null && !name.equals("")) {
        s.append(" name='").append(name).append("'");
      }
      if (sid != null && !sid.equals("")) {
        s.append(" value='").append(sid).append("'");
      }  
      s.append("> ")
        .append(title)
        .append("</input><br />\n"); 
    }
    // ----- template: xml -------------------------------------------------
    else if (template.equals("xml")){
      s.append("\n<" + name + ">\n");
      if (sid != null) 
        s.append(sid);
      if (so1 != null) 
        s.append(so1);
      if (so2 != null) 
        s.append(so2);
      if (so3 != null) 
        s.append(so3);
      s.append("\n</" + name + ">\n");
    } 

    return s.toString();   
  }

}





