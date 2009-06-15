// $Id: DisplayVariantNameGrabber.java,v 1.11 2008/06/12 14:37:18 geir.gronmo Exp $

package net.ontopia.topicmaps.nav.utils.grabbers;

import java.io.*;
import java.util.*;
import java.net.URLEncoder;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.core.index.*;
import net.ontopia.topicmaps.entry.*;
import net.ontopia.topicmaps.utils.*;
import net.ontopia.topicmaps.nav.utils.deciders.*;
import net.ontopia.utils.*;

import org.apache.log4j.*;

/**
 * INTERNAL: Grabs the best variant name for a basename and will
 * return it if decider is ok.
 */
public class DisplayVariantNameGrabber implements GrabberIF {
  
  // Define a logging category.
  static Logger log = Logger.getLogger(DisplayVariantNameGrabber.class.getName());

  protected Collection context;
  protected DeciderIF decider;

  /**
   * Construction which will apply context and a decider to the
   * selection of the variant name.
   */
  public DisplayVariantNameGrabber(Collection context, DeciderIF decider) {
    this.context = context;
    this.decider = decider;
  }
  
  /**
   * Empty constructor which will apply no context to selection of the
   * best variant name.  
   */
  public DisplayVariantNameGrabber() {  
    this.context = Collections.EMPTY_LIST;
    this.decider = null;
  }     
        
  /**
   * Grabs the best variant name and will return it if the decider
   * supplied says its ok.
   */
  public Object grab(Object object) {  
    // check object
    if (!(object instanceof TopicNameIF)) 
      throw new OntopiaRuntimeException("DisplayVariantNameGrabber Error: A baseName must be passed in as the object");
    // do it
    TopicNameIF baseName = (TopicNameIF)object;
    VariantNameIF variantName = (VariantNameIF)(new VariantNameGrabber(context).grab(baseName));
    if (variantName == null || (decider != null && !decider.ok(variantName)))
      variantName = null;
    return variantName;
  }
}





