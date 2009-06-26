// $Id: RenderDisplayStringifier.java,v 1.14 2004/11/12 11:47:19 grove Exp $

package net.ontopia.topicmaps.nav.utils.stringifiers;

import java.util.*;

import net.ontopia.topicmaps.nav.utils.*;
import net.ontopia.topicmaps.nav.utils.structures.*;
import net.ontopia.utils.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * INTERNAL: Stringifier for a Display which takes a Render object.
 *
 * This class currently sypports the following templates:
 * <ul>
 * <li>html: Outputs an HTML table. Makes use of all of the behaviours.
 * <li>text: Outputs text. Only makes use of the title.
 * </ul>
 * 
 * <p>Alternate implementations
 * of this class should use an empty constructor and ensure that an instantiation
 * is held in APLLICATION_SCOPE to ensure that blocks are stringifed as
 * efficiently as possible.
 */     
public class RenderDisplayStringifier implements StringifierIF { 

  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(RenderDisplayStringifier.class.getName());

  /** 
   * Empty constructor used by configuration objects on application startup.
   */
  public RenderDisplayStringifier() {
  }
  
  /**
   * Returns a string if a Render object is submitted
   */
  public String toString(Object object) {
    Render render;
    StringBuffer s = new StringBuffer(128);
    
    try {
      render = (Render) object;
    } catch (ClassCastException e) {
      throw new OntopiaRuntimeException("RenderDisplayStringifier Error: A Render object was not passed to toString()");
    }
    
    String template = render.getTemplate();
    String cname = render.getName();
    Map strings = render.getStrings();
    
    // component properties
    String title = ((String) strings.get("title")).trim();
    String titleDesc = (String) strings.get("titleDesc");
    String href = (String) strings.get("href");
    String name = (String) strings.get("name");     
    String resource = (String) strings.get("resource");
    String target = (String) strings.get("target");
    String style = (String) strings.get("style");
    String klass = (String) strings.get("klass");
    String onclick = (String) strings.get("onclick");   
    String onmouseover = (String) strings.get("onmouseover");   
    String onmouseout = (String) strings.get("onmouseout");
    
    // need a template
    if (template == null)
      return "";
      
    // use template to generate output
    if (template.equals("html") || template.equals("link") || template.equals("")) {
      if (title!=null && !title.equals("")) {
        s.append("<span");
        if (!style.equals("")) {
          s.append(" style='").append(style).append("'");
        }
        if (!klass.equals("")) {
          s.append(" class='").append(klass).append("'");
        }
        if (!titleDesc.equals("")) {
          s.append(" title='").append(titleDesc).append("'");
        }                                 
        s.append(">");
        if (href!=null && !href.equals("")) {
          s.append("<a href='");
          s.append(href);
          s.append("'");
          if (!target.equals("")){
            s.append(" target='").append(target).append("'");
          }
          if (!onclick.equals("")) {
            s.append(" onClick='").append(onclick).append("'");
          }
          if (!onmouseover.equals("")) {
            s.append(" onMouseOver='").append(onmouseover).append("'");
          }
          if (!onmouseout.equals("")) {
            s.append(" onMouseOut='").append(onmouseout).append("'");
          }
          s.append(">");
        }
        if (title.equals("")) {
          title="No title";
        }

        s.append(title);

        if (href!=null && !href.equals("")){
          s.append("</a>");
        }
        s.append("</span>");
      }
    } // -----------------------------------------------------------
    else if (template.equals("text") || template.equals("noLink")) {

      s.append(title);

    } // -----------------------------------------------------------
    else if (template.equals("xml")) {
      s.append("<component>");
      if (title!=null && !title.equals(""))
        s.append("<title>").append(title).append("</title>"); 
      if (titleDesc!=null && !titleDesc.equals(""))
        s.append("<titleDesc>").append(titleDesc).append("</titleDesc>"); 
      if (href!=null && !href.equals(""))
        s.append("<href>").append(href).append("</href>"); 
      if (name!=null && !name.equals(""))
        s.append("<name>").append(name).append("</name>");
      s.append("</component>");
    } // -----------------------------------------------------------
    else {
      log.warn("RenderDisplayStringifier Warning: Unrecognized template: " + template);
    }
    
    return s.toString();         
  }
  
}





