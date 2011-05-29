
package net.ontopia.topicmaps.nav2.core;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import net.ontopia.utils.ontojsp.JSPTreeNodeIF;

/**
 * INTERNAL: Implemented by an object which represents a executable
 * function within the navigator framework. It is comparable to a
 * macro definition and can be used as an shortcut in a
 * JSP-environment together with Ontopia's taglibs.<p>
 *
 * See logic:externalFunction for how to register your own function
 * with the tag libraries. The function can later be executed using
 * logic:call.
 */
public interface FunctionIF {

  /**
   * INTERNAL: Return the names of the parameters as an ordered
   * <code>Collection</code>.
   */
  public Collection getParameters();

  /**
   * INTERNAL: Executes this function in the specified context.
   *
   * @return Collection The function return value collection. If null
   * is returned, no value will be given to the parent value accepting
   * tag.
   */
  public Collection execute(PageContext pageContext, TagSupport callingTag)
    throws IOException, JspException;

  // --- deprecated methods
  
  /**
   * INTERNAL: Executes this function in the specified context.
   *
   * @deprecated 1.3.4. Use <code>Object call(PageContext)</code>
   * instead.
   */
  public void call(PageContext pageContext, TagSupport callingTag)
    throws IOException, JspException;

  /**
   * INTERNAL: Gets the name of this function.
   *
   * @deprecated 1.3.4. Function names are now stored outside the
   * function object itself.
   */
  public String getName();

  /**
   * INTERNAL: Gets the name of the variable to which the return value
   * of the function should be assigned to. Returns null if no return
   * variable name was specified.
   *
   * @since 1.3
   *
   * @deprecated 1.3.4. Return function value from the <code>Object
   * call(PageContext)</code> method instead.
   */
  public String getReturnVariableName();
  
  /**
   * INTERNAL: Gets the rode node of this Function.
   *
   * @deprecated This method is not used, and need not be implemented.
   */
  public JSPTreeNodeIF getRootNode();

  /**
   * INTERNAL: Gets the reference to the Module this function belongs
   * to.
   *
   * @deprecated This method is not used, and need not be implemented.
   */
  public ModuleIF getModule();
  
}
