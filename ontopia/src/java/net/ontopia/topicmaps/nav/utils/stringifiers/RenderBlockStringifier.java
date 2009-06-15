// $Id: RenderBlockStringifier.java,v 1.13 2004/11/12 11:47:19 grove Exp $

package net.ontopia.topicmaps.nav.utils.stringifiers;

import java.util.*;

import net.ontopia.topicmaps.nav.utils.*;
import net.ontopia.topicmaps.nav.utils.structures.*;
import net.ontopia.utils.*;

import org.apache.log4j.*;

/** 
 * INTERNAL: Stringifier for a Block object which takes a Render object.
 * 
 * <p>Currently supports the following templates:
 * <ul>
 * <li>all
 * <li>content
 * <li>title
 * </ul>
 *
 * <p>Alternate implementations
 * of this class should use an empty constructor and ensure that an instantiation
 * is held in APLLICATION_SCOPE to ensure that blocks are stringifed as
 * efficiently as possible.
 */     
public class RenderBlockStringifier implements StringifierIF {
 
  // Define a logging category.
  static Logger log = Logger.getLogger(RenderBlockStringifier.class.getName());

  /** 
   * Empty constructor used by configuration objects on application startup.
   */ 
  public RenderBlockStringifier() {
  }
  
  /**
   * Returns a String if a Render object is submitted.
   */
  public String toString(Object object) {
    Render render;
    try {
      render = (Render)object;
    } catch (ClassCastException e){
      throw new OntopiaRuntimeException("RenderBlockStringifier Error: " +
                                        "A Render Object was not submitted to the Stringifier.");
    }
    String name = render.getName();
    String template = render.getTemplate();
    Map    strings = render.getStrings();
    String title = (String) strings.get("title");
    String titleDesc = (String) strings.get("titleDesc");
    String count = (String) strings.get("count");
    String content = (String) strings.get("content"); 
    
    // we need a template
    if (template == null) 
      return "";
    
    StringBuffer s = new StringBuffer(1024);
    
    // ----- template: default (all) ---------------------------------------
    if (template.equals("all") || template.equals("")) {
      s.append("<div class='clsHasKids'>\n")
        .append("  <img src='images/open.gif' class='toggle' width='16' height='16' hspace='1' />\n")
        .append("  <span class='tocHead'");
      if (titleDesc!=null && !titleDesc.equals("")) {             
        s.append(" title='")
          .append(titleDesc)
          .append("'");
      }
      s.append(">")
        .append(title); 
      if (count!=null && !count.equals("")) {
        s.append(" (")
          .append(count)
          .append(")");
      }
      s.append("</span>\n");
      if (content!=null && !content.equals("")) {
        s.append("  <div class='toc'>\n")
          .append(content)
          .append("\n  </div>\n");
      }
      s.append("</div>\n");                 
    }
    // ----- template: content ---------------------------------------------
    else if (template.equals("content")) {
      s.append(content);
    }
    // ----- template: title -----------------------------------------------
    else if (template.equals("title")) {
      s.append(title);   
    }
    // ----- template: xml -------------------------------------------------
    else if (template.equals("xml")) {
      s.append("<block>\n");
      if (count!=null && !count.equals(""))
        s.append("  <count>").append(count).append("</count>\n");
      if (title!=null && !title.equals(""))
        s.append("  <title>").append(title).append("</title>\n");
      if (titleDesc!=null && !titleDesc.equals(""))
        s.append("  <titleDesc>").append(titleDesc).append("</titleDesc>\n");
      if (content!=null && !content.equals(""))
        s.append("  <content>").append(content).append("</content>\n");
      s.append("</block>\n");
    }
    
    return s.toString();
  }
  
}





