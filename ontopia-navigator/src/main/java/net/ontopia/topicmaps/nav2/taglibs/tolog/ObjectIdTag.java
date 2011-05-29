
package net.ontopia.topicmaps.nav2.taglibs.tolog;

import java.io.IOException;
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
  
  public final void generateOutput(JspWriter out, Object outObject)
    throws JspTagException, IOException {
    
    String objectId;
    
    // --- first try if object is instance of TMObjectIF
    try {      
      objectId = ((TMObjectIF) outObject).getObjectId();
    } catch (ClassCastException e) {
      // --- TopicMapReferenceIF
      if (outObject instanceof TopicMapReferenceIF) 
        objectId = ((TopicMapReferenceIF) outObject).getId();
      else {
        // --- otherwise signal error
        String msg = "<tolog:oid> expected collection which contains " +
          "object instances of TMObjectIF or TopicMapReferenceIF, but got " +
          "instance of " + outObject.getClass().getName() + ". Please " +
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

  public String getName() {
    return getClass().getName();
  }
}
