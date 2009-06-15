// $Id: BlockRenderTag.java,v 1.10 2003/01/29 07:40:10 grove Exp $

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
 * PUBLIC: Retuns a block which contains the contents of the tag.
 * <p>This tag can be used where the designer wants to wrap some of their own content
 * in a block. Often this will be done by specifying a number of <item> tags but this
 * needn't be the case. The title, titleDec, and count are all set as attributes in the tag.
 * <p>Operates in any context.
 */
public class BlockRenderTag extends BodyTagSupport {
  
  // Define a logging category.
  static Logger log = Logger.getLogger(BlockRenderTag.class.getName());

  // attributes
  String renderName="";
  String renderTemplate="all";
  String renderStringifier="";
  Map    strings = new HashMap();
  String title="";
  String titleDesc="";
  String count="";
  String args=""; 
  
  /**
   * Sets the name for the block. This name is available to to render stringifier.
   */
  public void setRenderName(String s){
    this.renderName=s;
  }
 
  /**
   * Sets the template used by the render stringifier. The default stringifiers supports
   * values of all, title and content.
   */
  public void setRenderTemplate(String s){
    this.renderTemplate=s;
  }
  
  /**
   * Sets the full java path to the render stringifier
   */
  public void setRenderStringifier(String s){
    this.renderStringifier=s;
  } 

  /**
   * Sets the title of the block.
   */
  public void setTitle(String s) {
    this.strings.put("title", s);
  }
  
  /**
   * Sets the desciption of the title.
   */
  public void setTitleDesc(String s) {
    this.strings.put("titleDesc",s);
  }
  
  /**
   * Sets the count for the block.
   */
  public void setCount(String s){
    this.strings.put("count",s);
  }

  /**
   * Sets a space separated list of arguments for the use of the tag. Currently, only "showNone" 
   * is supported where a "None" item will be displayed even if there is no content available.
   */
  public void setArgs(String s) {
    if (s.indexOf("showNone") >- 1)
      this.strings.put("showNone", "true");
  }

  
  public int doAfterBody() throws JspTagException {
    // read in body content
    BodyContent body = getBodyContent();
    String content = body.getString();
    // output
    strings.put("content",content);
    try {
      JspWriter out = body.getEnclosingWriter();
      out.print(new RenderBlockStringifier().toString(new Render(renderTemplate, renderName, strings)));
    } catch (IOException e){
      throw new JspTagException("BlockTag Error: JspWriter not there: " + e);
    }
    body.clearBody();
    return SKIP_BODY;
  }
}





