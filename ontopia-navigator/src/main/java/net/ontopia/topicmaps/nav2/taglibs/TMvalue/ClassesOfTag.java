
package net.ontopia.topicmaps.nav2.taglibs.TMvalue;

import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import javax.servlet.jsp.JspTagException;

import net.ontopia.topicmaps.core.*;

import net.ontopia.topicmaps.nav2.taglibs.value.BaseValueProducingAndAcceptingTag;

/**
 * INTERNAL: Value Producing Tag for finding all the classes
 * of all the objects in a collection.
 */
public class ClassesOfTag extends BaseValueProducingAndAcceptingTag {

  public Collection process(Collection tmObjects) throws JspTagException {
    // find all the classes of all tmObjects in collection
    // avoid duplicate type entries therefore use a 'Set'
    if (tmObjects == null)
      return Collections.EMPTY_SET;
    else{
      Set types = new HashSet();
      Iterator iter = tmObjects.iterator();
      Object obj = null;
      while (iter.hasNext()) {
        obj = iter.next();
        // --- for occurrence, association, or association role objects
        if (obj instanceof TypedIF) {
          TypedIF singleTypedObj = (TypedIF) obj;
          TopicIF type = singleTypedObj.getType();
          if (type != null)
            types.add( type );
        }
        // --- for topic objects
        else if (obj instanceof TopicIF) {
          TopicIF topic = (TopicIF) obj;
          Collection _types = topic.getTypes();
          if (!_types.isEmpty())
            types.addAll( _types );
        }
      } // while    
      return new ArrayList(types);
    }
  }

}
