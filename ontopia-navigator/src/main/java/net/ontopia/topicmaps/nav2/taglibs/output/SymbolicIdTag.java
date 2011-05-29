
package net.ontopia.topicmaps.nav2.taglibs.output;

import java.io.IOException;
import java.util.Iterator;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.utils.NavigatorUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Outputs a stable identifier to the object if possible,
 * if not outputs the object ID.
 */
public class SymbolicIdTag extends BaseOutputProducingTag {

  // initialization of logging facility
  private static Logger log = LoggerFactory
    .getLogger(ObjectIdTag.class.getName());
  
  public final void generateOutput(JspWriter out, Iterator iter)
    throws JspTagException, IOException {
    
    String objectId = null;
    Object elem = iter.next();
    if (elem == null) // FIXME. really needed?
      return;
    
    // --- first try if object is instance of TMObjectIF
    try {      
      objectId = NavigatorUtils.getStableId((TMObjectIF) elem);
    } catch (ClassCastException e) {
      // --- TopicMapReferenceIF
      if (elem instanceof TopicMapReferenceIF) 
        objectId = ((TopicMapReferenceIF) elem).getId();
      else {
        // --- otherwise signal error
        String msg = "SymbolicIdTag expected collection which contains " +
          "object instances of TMObjectIF or TopicMapReferenceIF, but got " +
          "instance of " + elem.getClass().getName() + ". Please " +
          "control variable '" +
          ((variableName!= null) ?  variableName : "_default_") + "'.";
        log.error(msg);
        throw new NavigatorRuntimeException(msg);
      }
    }
    
    // finally write out String with help of the Stringifier
    if (objectId != null)
      print2Writer( out, objectId );
  }
  
}
