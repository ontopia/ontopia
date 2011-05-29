
package net.ontopia.topicmaps.nav2.taglibs.output;

import java.io.IOException;
import java.util.Iterator;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

/**
 * INTERNAL: Output-Producing Tag, which writes out the number
 * of objects in a collection.
 */
public class CountTag extends BaseOutputProducingTag {

  public CountTag() {
    // a number needs not to be escaped
    // we are also interested putting out 0 for empty collections
    super(false, false);
  }

  public final void generateOutput(JspWriter out, Iterator iter)
    throws JspTagException, IOException {

    print2Writer( out, String.valueOf(getCollectionSize()) );
    
  }

}





