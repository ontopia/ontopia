
package net.ontopia.topicmaps.nav2.impl.basic;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import net.ontopia.utils.ontojsp.*;

import net.ontopia.topicmaps.nav2.core.ContextManagerIF;
import net.ontopia.topicmaps.nav2.core.FunctionIF;
import net.ontopia.topicmaps.nav2.core.ModuleIF;

/**
 * INTERNAL: A FunctionIF implementation used by the XML-based
 * ModuleIF reader code. The function evaluates the taglib tree node
 * in the context of the calling tag and the page context.
 *
 * @see net.ontopia.topicmaps.nav2.impl.basic.ModuleReader
 */
public final class Function extends AbstractFunction {

  private ModuleIF module;
  private String name;
  private JSPTreeNodeIF rootNode;
  private Collection params;
  private String returnVariableName;
  
  /**
   * Default constructor.
   */
  public Function(String name, JSPTreeNodeIF rootNode, Collection params) {
    this(null, name, rootNode, params);
  }
  
  public Function(ModuleIF parentModule, String name,
                  JSPTreeNodeIF rootNode, Collection params) {
    this(parentModule, name, rootNode, params, null);
  }
    
  public Function(ModuleIF parentModule, String name,
                  JSPTreeNodeIF rootNode, Collection params,
                  String returnVariableName) {
    this.module = parentModule;
    this.name = name;
    this.rootNode = rootNode;
    this.params = params;
    this.returnVariableName = returnVariableName;
  }

  // -------------------------------------------------------
  // FunctionIF implementation
  // -------------------------------------------------------
  
  public String getName() {
    return name;
  }

  public Collection getParameters() {
    return params;
  }

  public String getReturnVariableName() {
    return returnVariableName;
  }

  public void call(PageContext pageContext, TagSupport callingTag)
    throws IOException, JspException {

    JSPPageExecuter exec = new JSPPageExecuter();
    exec.run(pageContext, callingTag, rootNode);
  }

  // --- deprecated methods
  
  public JSPTreeNodeIF getRootNode() {
    return rootNode;
  }
  
  public ModuleIF getModule() {
    return module;
  }

  // -------------------------------------------------------
  // overwrite Object implementation 
  // -------------------------------------------------------

  public String toString() {
    StringBuffer sb = new StringBuffer(64);
    sb.append("[Function: ")
      .append( name ).append(", params ")
      .append( params ).append( "]");    
    return sb.toString();
  }

}
