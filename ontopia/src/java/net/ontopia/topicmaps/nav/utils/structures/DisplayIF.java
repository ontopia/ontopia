// $Id: DisplayIF.java,v 1.11 2008/06/12 14:37:18 geir.gronmo Exp $

package net.ontopia.topicmaps.nav.utils.structures;

import java.util.Map;

/** 
 * INTERNAL: Interface which the selection, presentation and context
 * info for an object.  <p>This interface allows classes such as
 * DisplayWithContext to wrap a Display object which may have no
 * context of its own.
 */     
public interface DisplayIF {
         
  public String getObject();    
  public String getArgs();

  public String getRenderName();
  public String getRenderTemplate();
  public String getRenderStringifier();

  public String getTopicNameContext();
  public String getTopicNameGrabber();
  public String getTopicNameDecider();

  public String getVariantNameContext();
  public String getVariantNameGrabber();
  public String getVariantNameDecider();

  public String getDisplayTitle();
  public String getDisplayHref();
  public String getDisplayBehaviour(); 

  public Map getContext();

}





