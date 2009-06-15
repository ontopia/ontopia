
// $Id: BaseOutputProducingTag.java,v 1.29 2004/01/13 09:16:34 grove Exp $

package net.ontopia.topicmaps.nav2.taglibs.output;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import net.ontopia.topicmaps.nav2.core.ContextManagerIF;
import net.ontopia.topicmaps.nav2.core.OutputProducingTagIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.utils.StringUtils;

import org.apache.log4j.Logger;

/**
 * INTERNAL: Abstract super-class of an Output-Producing Tag.
 */
public abstract class BaseOutputProducingTag extends TagSupport
  implements OutputProducingTagIF {

  // initialization of logging facility
  private static Logger log = Logger
    .getLogger(BaseOutputProducingTag.class.getName());

  // members
  protected ContextTag contextTag;  
  protected int numberOfElements;
  protected boolean escapeEntities;
  protected boolean suppressEmptyCollection;
  
  // tag attributes
  protected String variableName;

  public BaseOutputProducingTag() {
    this(true, true);
  }
  protected BaseOutputProducingTag(boolean escapeEntities, boolean suppressEmptyCollection) {
    // Whether the generated String should be escaped to care about
    // HTML/XML entities or not.
    this.escapeEntities = escapeEntities;
    // Wether the <code>generateOutput</code> method for String
    // generation should be called even there are the input collection
    // is empty. Default behaviour is to suppress calling the
    // <code>generateOutput</code> method.
    this.suppressEmptyCollection = suppressEmptyCollection;
  }

  /**
   */
  protected final void setEscapeEntities(boolean escapeEntities) {
  }

  /**
   */
  protected final void setSuppressEmptyCollection(boolean suppressEmptyCollection) {
  }
  
  /**
   * Process the start tag for this instance.
   */
  public int doStartTag() throws JspTagException {
    // retrieve collection from ContextManager by Name
    this.contextTag = FrameworkUtils.getContextTag(pageContext);
    ContextManagerIF ctxtMgr = contextTag.getContextManager();
    
    Collection coll;
    if (variableName != null) {
      coll = ctxtMgr.getValue(variableName);
    } else {
      coll = ctxtMgr.getDefaultValue();
    }

    numberOfElements = coll.size();
  
    // check if there is at least one entry
    if (!coll.isEmpty() || !suppressEmptyCollection) {
      // subclass writes out collection information to JspWriter
      try {
        JspWriter out = pageContext.getOut();
        // log.debug("output '" + ((variableName!=null) ? variableName : "<default>") +
        //           "' (" + coll + ") to page.");
        generateOutput(out, coll.iterator());
      } catch (IOException ioe) {
        String msg = "Error in BaseOutputProducingTag: " + ioe.getMessage();
        log.error(msg);
        throw new NavigatorRuntimeException(msg, ioe);
      }
    } else {
      log.warn(getClass().getName() + ": empty collection found. " +
               "Please control value of variable '" + variableName + "'.");
    }

    // reset members
    contextTag = null;
    
    return SKIP_BODY;
  }

  public final int doEndTag() {
    // reset members
    this.contextTag = null;
    
    return EVAL_PAGE;
  }
  
  /**
   * reset the state of the Tag.
   */
  public void release() {
    // overwrite default behaviour
    // do not set parent to null!!!
  }

  public abstract void generateOutput(JspWriter out, Iterator iterator)
    throws JspTagException, IOException;
  
  // -----------------------------------------------------------------
  // get methods
  // -----------------------------------------------------------------
  
  /**
   * INTERNAL: gets number of elements contained in processed
   * input collection.
   */
  protected final int getCollectionSize() {
    return numberOfElements;
  }

  // -----------------------------------------------------------------
  // set methods
  // -----------------------------------------------------------------

  /**
   * tag attribute for setting the variable name of the input collection
   * common to all subclasses.
   */
  public final void setOf(String variableName) {
    // log.debug(getClass().getName() + ".setOf(" + variableName + ")");
    this.variableName = variableName;
  }

  // -----------------------------------------------------------------
  // internal methods
  // -----------------------------------------------------------------

  /**
   * INTERNAL: prints out string to specified JspWriter object
   * with respect if the entities should be escaped.
   */
  protected final void print2Writer(JspWriter out, String string)
    throws IOException {

    if (escapeEntities)
      out.print( StringUtils.escapeHTMLEntities(string) );
    else
      out.print( string );
  }
  
}
