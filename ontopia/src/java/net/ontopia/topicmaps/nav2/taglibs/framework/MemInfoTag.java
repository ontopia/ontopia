
// $Id: MemInfoTag.java,v 1.6 2003/08/29 12:32:44 larsga Exp $

package net.ontopia.topicmaps.nav2.taglibs.framework;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import net.ontopia.topicmaps.nav2.utils.NavigatorUtils;
import net.ontopia.topicmaps.nav2.core.ContextManagerIF;
import net.ontopia.topicmaps.nav2.core.OutputProducingTagIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;

import org.apache.log4j.Logger;

/**
 * INTERNAL: Framework related tag for logging information about
 * memory and CPU time usage to log channel.
 */
public final class MemInfoTag extends TagSupport {

  // initialization of logging facility
  private static Logger log = Logger.getLogger(MemInfoTag.class.getName());

  // constants
  private final static NumberFormat formatter =
    new DecimalFormat("###,###,###,###");

  // members
  private long startFreeMem;
  private long startTime;
  
  // tag attributes
  private String name = "";

  /**
   * Process the start tag for this instance.
   */
  public int doStartTag() throws JspTagException {
    log.debug( "/// " + name + ": " + generateStartMemInfo() );
    return EVAL_BODY_INCLUDE;
  }

  /**
   * Process the end tag for this instance.
   */
  public int doEndTag() throws JspTagException {
    log.debug( "\\\\\\ " + name + ": " + generateEndMemInfo() );
    return EVAL_PAGE;
  }

  
  // -----------------------------------------------------------------
  // set methods
  // -----------------------------------------------------------------

  /**
   * setting the tag attribute name: helps to give the output
   * a more understandable description.
   */
  public void setName(String name) {
    this.name = name;
  }

  // -----------------------------------------------------------
  // internal helper methods
  // -----------------------------------------------------------

  private String generateStartMemInfo() {
    long free = Runtime.getRuntime().freeMemory();
    long tot = Runtime.getRuntime().totalMemory();
    startTime = System.currentTimeMillis();
    startFreeMem = free;
    StringBuffer strBuf = new StringBuffer(32);
    strBuf.append( "Free Mem: " ).append( formatter.format(free) )
      .append( ", Allocated Mem: " ).append( formatter.format(tot) )
      .append(".");
    return strBuf.toString();
  }

  private String generateEndMemInfo() {
    long free = Runtime.getRuntime().freeMemory();
    long usedMem = startFreeMem - free;
    long endTime = System.currentTimeMillis();

    StringBuffer strBuf = new StringBuffer();
    if (usedMem > 0)
      strBuf.append("Used Mem: " + formatter.format(usedMem));
    else
      strBuf.append("garbage was collected in the meantime!");

    strBuf.append(" - required " + (endTime - startTime) + " ms.");
    return strBuf.toString();
  }
  
}
