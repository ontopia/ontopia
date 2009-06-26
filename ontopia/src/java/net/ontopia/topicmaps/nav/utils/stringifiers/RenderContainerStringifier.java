// $Id: RenderContainerStringifier.java,v 1.10 2004/11/12 11:47:19 grove Exp $

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
 * </ul>
 *
 * <p>Alternate implementations
 * of this class should use an empty constructor and ensure that an instantiation
 * is held in APLLICATION_SCOPE to ensure that containers are stringifed as
 * efficiently as possible.
 */
public class RenderContainerStringifier implements StringifierIF {
  
  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(RenderContainerStringifier.class.getName());

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
  public RenderContainerStringifier(){}
  
  /**
   * Returns a String if a Render object is submitted.
   */
  public String toString(Object object){
    Render render;
    try {
      render = (Render)object;
    } catch (ClassCastException e){
      throw new OntopiaRuntimeException("RenderContainerStringifier Error: A Render object was not passed toString()");
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
      
    StringBuffer s = new StringBuffer(1024);

    // ----- all
    if (template.equals("all") || template.equals("")) {
      s.append("<div class='blockContainer'>\n");
      s.append("  <div class='blockContainerHead'>\n");
      s.append("    <div class='entryTitle'>\n");
      if (title!=null) {
        if (titleDesc!=null && !titleDesc.equals("")) {
          s.append("<span title='");
          s.append(titleDesc);
          s.append("'>");
          s.append(title);
          s.append("</span>");
        } else {
          s.append(title);
        }
      }
      s.append("\n    </div>\n");
      s.append("  </div>\n");
      s.append("  <div class='blockContainerBody'>\n");           
      s.append(content);
      if (content.trim().equals("")) s.append("None");
      s.append("\n  </div>\n");                   
      s.append("</div>\n");
    } // ----- xml
    else if (template.equals("xml")){
      s.append("<container>\n");
      s.append("  <containerHead>\n");
      if (title!=null && !title.equals("")) 
        s.append("    <title>").append(title).append("</title>\n");
      if (titleDesc!=null && !titleDesc.equals(""))
        s.append("    <titleDesc>").append(titleDesc).append("</titleDesc>\n");
      s.append("  </containerHead>\n");
      s.append("  <containerBody>\n");            
      if (content!=null && !content.equals("")) 
        s.append(content);
      s.append("\n  </containerBody>\n");       
      s.append("</container>\n");      
    }

    return s.toString();
  }
  
}





