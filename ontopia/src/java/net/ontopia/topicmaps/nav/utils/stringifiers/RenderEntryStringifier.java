// $Id: RenderEntryStringifier.java,v 1.8 2004/11/12 11:47:19 grove Exp $

package net.ontopia.topicmaps.nav.utils.stringifiers;

import java.util.*;

import net.ontopia.topicmaps.nav.utils.*;
import net.ontopia.topicmaps.nav.utils.structures.*;
import net.ontopia.utils.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * INTERNAL: Stringifier for a container which takes a render object.
 * 
 * <p>Supports the following templates:
 * <ul>
 * <li>all
 * <li>title 
 * <li>content
 * </ul>
 *
 * <p>Alternate implementations of this class should use an empty
 * constructor and ensure that an instantiation is held in
 * APPLICATION_SCOPE to ensure that containers are stringifed as
 * efficiently as possible.
 */
public class RenderEntryStringifier implements StringifierIF {
  
  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(RenderEntryStringifier.class.getName());

  protected String name;
  protected String template;
  protected Map strings;
  protected String title;
  protected String titleDesc;
  protected String showNone;
  protected String content;
  
  /** 
   * Empty constructor used by configuration objects on application startup.
   */
  public RenderEntryStringifier() {
  }
  
  /**
   * Returns a String if a Render object is submitted.
   */
  public String toString(Object object) {
    Render render;
    try {
      render = (Render) object;
    } catch (ClassCastException e){
      throw new OntopiaRuntimeException("RenderEntryStringifier Error: A Render object was not passed toString()");
    }

    String name = render.getName();
    String template = render.getTemplate();
    Map strings = render.getStrings();
   
    String title = (String)strings.get("title");
    String titleDesc = (String)strings.get("titleDesc");
    String showNone = (String)strings.get("showNone");
    String content = (String)strings.get("content");
    
    // need a template
    if (template==null)
      return "";
    
    // need content
    if ((content==null || content.trim().equals("")) && (showNone==null || !showNone.equals("true"))) 
      return "";
      
    StringBuffer s = new StringBuffer(128);

    // ----- all
    if (template.equals("all") || template.equals("")) {
      if (title!=null) {
        s.append("<strong>");
        if (titleDesc != null && !titleDesc.equals("")) {
          s.append("<span title='");
          s.append(titleDesc);
          s.append("'>");
          s.append(title);
          s.append("</span>");
        } else 
          s.append(title);

        s.append("</strong>");
        s.append("<br/ >\n");
      }
      s.append(content);
      if (content.trim().equals(""))
        s.append("None");
    } // ----- title
    else if (template.equals("title")){
      if (title != null) {
        s.append("<strong>");
        if (titleDesc != null && !titleDesc.equals("")) {
          s.append("<span title='");
          s.append(titleDesc);
          s.append("'>");
          s.append(title);
          s.append("</span>");
        } else 
          s.append(title);
        s.append("</strong>\n");
      }
    } // ----- content
    else if (template.equals("content")){
      s.append(content);
      if (content.trim().equals("")) 
        s.append("None");   
    } // ----- xml
    else if (template.equals("xml")){
      s.append("<entry>\n");
      s.append("  <entryHead>\n");
      if (title!=null && !title.equals(""))
        s.append("    <title>").append(title).append("</title>\n");
      if (titleDesc!=null && !titleDesc.equals(""))
        s.append("    <titleDesc>").append(titleDesc).append("</titleDesc>\n");
      s.append("  </entryHead>\n");
      s.append("  <entryBody>\n");
      if (content!=null && !content.equals(""))
        s.append(content);
      s.append("\n  </entryBody>\n");   
      s.append("</entry>\n");      
    }

    return s.toString();
  }

}






