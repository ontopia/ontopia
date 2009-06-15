// $Id: TopicMapIdTag.java,v 1.15 2003/02/04 10:59:29 grove Exp $

package net.ontopia.topicmaps.nav2.taglibs.output;

import java.io.IOException;
import java.util.Iterator;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.entry.StoreRegistry;
import net.ontopia.topicmaps.nav2.core.NavigatorApplicationIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.utils.NavigatorUtils;
  
import org.apache.log4j.Logger;

/**
 * INTERNAL: Output Producing Tag for selecting the ID of the topicmap
 * the specified object belongs to and writing it out.
 *
 * @since 1.2.5
 */
public class TopicMapIdTag extends BaseOutputProducingTag {

  // initialization of logging facility
  private static Logger log = Logger
    .getLogger(TopicMapIdTag.class.getName());
  
  public final void generateOutput(JspWriter out, Iterator iter)
    throws JspTagException, IOException {

    String topicMapId = null;
    Object elem = null;
    
    // --- try if object is instance of TMObjectIF
    try {
      elem = iter.next();
      TopicMapIF tm = ((TMObjectIF) elem).getTopicMap();
      NavigatorApplicationIF navApp = contextTag.getNavigatorApplication();
      topicMapId = navApp.getTopicMapRefId(tm);
    } catch (ClassCastException e) {
      // --- signal error if from wrong object type
      String msg = "TopicMapIdTag expected collection which contains " +
        "object instances of TMObjectIF, but got " +
        "instance of " + elem.getClass().getName() + ". Please " +
        "control variable '" +
        ((variableName!= null) ? variableName : "_default_") + "'.";
      log.error(msg);
      throw new NavigatorRuntimeException(msg, e);
    } catch (NullPointerException ne) {
      String msg = "NullPointerException while trying to get topicmap id for  '" +
        ((variableName!= null) ? variableName : "_default_") + "': " + ne.getMessage();
      log.error(msg);
      // throw new NavigatorRuntimeException(msg, ne); //FIXME
    }
    
    // finally write out String with help of the Stringifier
    if (topicMapId != null)
      print2Writer( out, topicMapId );
  }

}
