
package net.ontopia.topicmaps.nav2.core;

import java.io.IOException;
import java.util.Iterator;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspTagException;

/**
 * INTERNAL: Implemented by a tag which produces
 * output from an input collection somehow.
 */
public interface OutputProducingTagIF {

  /**
   * INTERNAL: Generate information extracted from the input collection
   * provided access by specified iterator. This is expected to be
   * written to the <code>JspWriter</code> object.
   */
  public void generateOutput(JspWriter out,
                             Iterator iterator)
    throws JspTagException, IOException;
  
}





