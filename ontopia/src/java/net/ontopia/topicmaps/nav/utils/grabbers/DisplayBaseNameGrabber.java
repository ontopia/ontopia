// $Id: DisplayBaseNameGrabber.java,v 1.12 2008/06/12 14:37:18 geir.gronmo Exp $

package net.ontopia.topicmaps.nav.utils.grabbers;

import net.ontopia.topicmaps.nav.utils.deciders.*;
import java.io.*;
import java.util.*;
import java.net.URLEncoder;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.core.index.*;
import net.ontopia.topicmaps.entry.*;
import net.ontopia.topicmaps.utils.*;
import net.ontopia.utils.*;

/**
 * INTERNAL: Grabs the best baseName for a topic and will return it if
 * the decider is ok.  Has two constructors.
 * <ol>
 * <li>(Collection scopes, String args) for instantiation through the
 *     jsp tags
 * <li>empty for instatiation through the config object
 * </ol>
 * Other user defined grabbers should use this constructor signature as
 * well.
 */
public class DisplayTopicNameGrabber implements GrabberIF {
  
  protected Collection context;
  protected DeciderIF decider;

  /**
   * Constructor which will allow the instance to apply context to
   * TopicName selection.
   */
  public DisplayTopicNameGrabber(Collection context, DeciderIF decider) {
    this.context = context;
    this.decider = decider;
  }
  
  /**
   * Empty Constructor which is used for the selection of TopicNames
   * using no context.
   */
  public DisplayTopicNameGrabber() {     
    this.context = new ArrayList();
  }     
        
  /**
   * Grabs the best TopicName for the topic submitted and returns it if
   * the Decider says its ok.
   */
  public Object grab(Object object) {  
    // check object
    if (!(object instanceof TopicIF)) {
      String msg = "DisplayTopicNameGrabber Error: " +
        "A topic must be passed in as the object";
      throw new OntopiaRuntimeException(msg);
    }
    // do it
    TopicIF t = (TopicIF) object;
    TopicNameIF baseName = (TopicNameIF) (new TopicNameGrabber(context).grab(t));
    if (decider == null || decider.ok(baseName))
      return baseName;
    else
      return null;
  }
  
}






