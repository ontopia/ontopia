
package net.ontopia.topicmaps.nav2.taglibs.output;

import java.io.IOException;
import java.util.Iterator;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Output Producing Tag for selecting the ID
 * of an object and writing it out.
 */
public class ObjectIdTag extends BaseOutputProducingTag {

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
      objectId = ((TMObjectIF) elem).getObjectId();
    } catch (ClassCastException e) {
      // --- TopicMapReferenceIF
      if (elem instanceof TopicMapReferenceIF) 
        objectId = ((TopicMapReferenceIF) elem).getId();
      else {
        // --- otherwise signal error
        String msg = "ObjectIdTag expected collection which contains " +
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





