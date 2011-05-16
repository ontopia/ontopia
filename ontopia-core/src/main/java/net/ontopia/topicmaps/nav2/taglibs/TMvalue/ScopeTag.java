// $Id: ScopeTag.java,v 1.12 2008/01/10 11:08:49 geir.gronmo Exp $

package net.ontopia.topicmaps.nav2.taglibs.TMvalue;

import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import javax.servlet.jsp.JspTagException;

import net.ontopia.topicmaps.core.ScopedIF;

import net.ontopia.topicmaps.nav2.taglibs.value.BaseValueProducingAndAcceptingTag;

/**
 * INTERNAL: Value Producing Tag for finding all themes
 * in the scope of all the objects in a collection.
 *
 * @see net.ontopia.topicmaps.core.ScopedIF#getScope()
 */
public class ScopeTag extends BaseValueProducingAndAcceptingTag {
  
  public Collection process(Collection characteristics) throws JspTagException {
    // find all themes of all characteristics in collection
    if (characteristics == null)
      return Collections.EMPTY_SET;
    else {
      ArrayList themes = new ArrayList();
      Iterator iter = characteristics.iterator();
      ScopedIF object = null;
      while (iter.hasNext()) {
        object = (ScopedIF) iter.next();
        // just get the scope stated for this object
        if (object != null)
          themes.addAll( object.getScope() );
      } // while
      return themes;
    }
  }

  // -----------------------------------------------------------
  // tag attributes
  // -----------------------------------------------------------

  /**
   * DEPRECATED: should use effective scope, default: yes.
   */
  public void setEffective(String useEffectiveScope) {
    // ignore
  }
  
}
