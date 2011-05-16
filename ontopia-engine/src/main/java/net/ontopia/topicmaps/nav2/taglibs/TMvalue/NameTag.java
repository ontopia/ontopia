
// $Id: NameTag.java,v 1.14 2004/01/13 09:16:34 grove Exp $

package net.ontopia.topicmaps.nav2.taglibs.TMvalue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.servlet.jsp.JspTagException;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.utils.NameGrabber;
import net.ontopia.topicmaps.utils.TopicCharacteristicGrabbers;
import net.ontopia.utils.GrabberIF;

import net.ontopia.topicmaps.nav2.utils.NavigatorUtils;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.topicmaps.nav2.taglibs.value.BaseValueProducingAndAcceptingTag;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;

/**
 * INTERNAL: Value Producing Tag for finding the most appropiate name
 * for each of all the topics in a collection.
 */
public class NameTag extends BaseValueProducingAndAcceptingTag {

  // tag attributes
  private GrabberIF nameGrabber;
  private String basenameScopeVarName;
  private String variantScopeVarName;

  public Collection process(Collection topics) throws JspTagException {
    if (topics == null || topics.isEmpty())
      return Collections.EMPTY_SET;
    else {      
      // find the most appropiate name for each of the topics in the collection
      ArrayList names = new ArrayList();
      
      // if no customer name grabber, setup the name grabber right here, right now.
      if (nameGrabber == null && basenameScopeVarName != null) {
        Collection scope = contextTag.getContextManager().getValue(basenameScopeVarName);
        // if name should be grabbed in accordance with scope  
        if (scope != null) 
          nameGrabber = new NameGrabber(scope);
      }
      if (nameGrabber == null)
        nameGrabber = TopicCharacteristicGrabbers.getDisplayNameGrabber();

      // iterate through topics
      Iterator iter = topics.iterator();
      Object obj = null;
      while (iter.hasNext()) {
        obj = iter.next();
        if (obj instanceof TopicIF) {
          Object name = nameGrabber.grab(obj);
          if (name != null)
            names.add(name);
        }
      } // while
      return names;
    }
  }

  // -----------------------------------------------------------------
  // set methods for tag attributes
  // -----------------------------------------------------------------

  public void setGrabber(String classname) throws NavigatorRuntimeException {
    ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);
    Object obj = contextTag.getNavigatorApplication().getInstanceOf(classname);
    if (obj != null && obj instanceof GrabberIF)
      this.nameGrabber = (GrabberIF) obj;
    else
      this.nameGrabber = null;
  }

  public final void setBasenameScope(String scopeVarName) {
    this.basenameScopeVarName = scopeVarName;
  }
  
  public final void setVariantScope(String scopeVarName) {
    this.variantScopeVarName = scopeVarName;
  }
  
}
