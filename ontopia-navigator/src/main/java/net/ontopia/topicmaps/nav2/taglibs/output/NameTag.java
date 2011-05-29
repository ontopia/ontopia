
package net.ontopia.topicmaps.nav2.taglibs.output;

import java.io.IOException;
import java.util.Iterator;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspTagException;
import net.ontopia.topicmaps.nav2.utils.Stringificator;

/**
 * INTERNAL: Output Producing Tag for selecting the name of an object
 * and writing it out.<p>
 * 
 * Note: Only puts out <b>first</b> item retrieved by iterator.
 */
public class NameTag extends BaseOutputProducingTag {

  // tag attributes
  protected String nameGrabberCN = null;
  protected String nameStringifierCN = null;
  protected String basenameScopeVarName = null;
  protected String variantScopeVarName = null;
  
  public void generateOutput(JspWriter out, Iterator iter)
    throws JspTagException, IOException {
    
    Object elem = iter.next();
    print2Writer(out,
                 Stringificator.toString(contextTag, elem,
                                         nameGrabberCN, nameStringifierCN,
                                         basenameScopeVarName, variantScopeVarName));
  }
  
  // -----------------------------------------------------------------
  // set methods for tag attributes
  // -----------------------------------------------------------------

  public final void setGrabber(String classname) {
    this.nameGrabberCN = classname;
  }

  public final void setStringifier(String classname) {
    this.nameStringifierCN = classname;
  }
  
  public final void setBasenameScope(String scopeVarName) {
    this.basenameScopeVarName = scopeVarName;
  }
  
  public final void setVariantScope(String scopeVarName) {
    this.variantScopeVarName = scopeVarName;
  }

}
