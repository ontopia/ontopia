// $Id: ContainerRenderTag.java,v 1.10 2003/01/29 07:40:10 grove Exp $

package net.ontopia.topicmaps.nav.taglibs.render;

import net.ontopia.topicmaps.nav.utils.stringifiers.*;
import net.ontopia.topicmaps.nav.utils.structures.*;
import net.ontopia.topicmaps.nav.utils.*;
import javax.servlet.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import java.io.*;
import java.util.*;

import org.apache.log4j.*;

/** 
 * PUBLIC: Used to output an information block which has a title,
 * content and count.
 *
 * <p>Used on JSP as a pure formatting tag. The tag outputs the
 * contents of the body along with the information collected from its
 * attributes. Generally this tag is used to wrap around a number of
 * blocks.
 *
 * <p>Operates in any context.
 *
 */
public class ContainerRenderTag extends BodyTagSupport {
  
  // Define a logging category.
  static Logger log = Logger.getLogger(ContainerRenderTag.class.getName());

  // attributes
  String renderName="";
  String renderTemplate="";
  String renderStringifier="";
  String title="";
  String titleDesc="";
  String count="";
  String args=""; 
  Map strings=new HashMap();
  
  /**
   * Sets the name which will be set in the Render object for the tag.
   */
  public void setRenderName(String s){this.renderName=s;}
  
  /**
   * Sets the template which will be set in the Render object for the tag.
   */
  public void setRenderTemplate(String s){this.renderTemplate=s;}
  
  /**
   * Sets the stringifier to be used for the Render object.
   * 
   */
  public void setRenderStringifier(String s){this.renderStringifier=s;}
  
  /**
   * Sets the title which will be put in Map of strings (key="title") in the Render object.
   */
  public void setTitle(String s){this.strings.put("title",s);}
  
  /**
   * Sets the title description which will be put in the Map of strings (key="titleDesc") in 
   * the Render object.
   */
  public void setTitleDesc(String s){this.strings.put("titleDesc",s);}
  
  /**
   * Sets the count which will be put in the Map of strings (key="count") in the render object.
   */
  public void setCount(String s){this.strings.put("count",s);}
  
  /**
   * Sets a space separated list of arguments to be used by the tag in its operation.
   * Currently, only "showNone" is supported.
   */
  public void setArgs(String s){if (s.indexOf("showNone")>-1) this.strings.put("showNone","true");}
        
  public int doAfterBody() throws JspTagException {
    // read in the body content
    BodyContent body = getBodyContent();
    String content = body.getString();
    // output
    if (content==null) content="";
    strings.put("content",content);  
    try {
      JspWriter out = body.getEnclosingWriter();
      out.print(new RenderContainerStringifier().toString(new Render(renderTemplate, renderName, strings)));
    } catch (IOException e){
      throw new JspTagException("ContainerTag Error: JspWriter not there: " + e);
    }
    body.clearBody();
    return SKIP_BODY;
  }
}





