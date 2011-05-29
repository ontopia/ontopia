
package net.ontopia.topicmaps.nav2.taglibs.TMvalue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.servlet.jsp.JspTagException;

import net.ontopia.topicmaps.core.TMObjectIF;

import net.ontopia.topicmaps.nav2.taglibs.value.BaseValueProducingAndAcceptingTag;

/**
 * INTERNAL: Value Producing Tag for finding all the source locators
 * (as LocatorIF objects) of all the topic map objects in a collection.
 */
public class SourceLocatorsTag extends BaseValueProducingAndAcceptingTag {

  public Collection process(Collection tmObjs) throws JspTagException {
    // find all source locators of all topic map objects in collection
    if (tmObjs == null || tmObjs.isEmpty())
      return Collections.EMPTY_SET;
    else {
      List sourceLocators = new ArrayList();
      Iterator iter = tmObjs.iterator();
      
      while (iter.hasNext()) {
        try {
          TMObjectIF tmObj = (TMObjectIF) iter.next();
          // get all source locators as LocatorIF objects
          if (tmObj != null)
            sourceLocators.addAll( tmObj.getItemIdentifiers() );
        } catch (ClassCastException e) {
        }
      }
      return sourceLocators;
    }
  }

}





