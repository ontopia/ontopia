
package net.ontopia.topicmaps.nav2.taglibs.TMvalue;

import java.util.*;
import javax.servlet.jsp.JspTagException;

import net.ontopia.utils.CollectionMap;
import net.ontopia.topicmaps.core.*;

import net.ontopia.topicmaps.nav2.taglibs.value.BaseValueProducingAndAcceptingTag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Value Producing Tag for creating a collection of strings
 * (which are representing the first character of a TopicNameIF object)
 * of all the objects in a collection of TopicNameIF objects.
 */
public class SplitterTag extends BaseValueProducingAndAcceptingTag {

  // initialization of logging facility
  private static Logger log = LoggerFactory
    .getLogger(SplitterTag.class.getName());

  public Collection process(Collection names) throws JspTagException {
    
    if (names == null || names.isEmpty())
      return Collections.EMPTY_SET;
    else {
      // contains the upper case first character as key and a collection
      // of TopicNameIF objects as values.
      CollectionMap charNamesMap = new CollectionMap();
      
      Iterator iter = names.iterator();
      while (iter.hasNext()) {
        Object obj = iter.next();
        // --- for TopicNameIF objects
        if (obj instanceof TopicNameIF) {
          TopicNameIF name = (TopicNameIF) obj;
          String name_value = name.getValue();
          if (name_value != null && name_value.length() > 0) {
            // TODO: Should use String.charAt(0) and Character.toUpperCase instead         
            //! String firstChar = name_value.substring(0, 1).toUpperCase();
            //! charNamesMap.add(firstChar, name);
            charNamesMap.add(new Character(Character.toUpperCase(name_value.charAt(0))), name);
          }
        } else {
          log.warn("Expected instance of TopicNameIF, but got " +
                   obj.getClass().getName());
        }
      } // while

      // sort the collection by the keys (first characters)
      Map sorted = new TreeMap(charNamesMap);
      
      // generate a collection (per char) of a collection of TopicNameIF objects
      List result = new ArrayList();
      Iterator charIt = sorted.keySet().iterator();
      while (charIt.hasNext()) {
        Character curChar = (Character) charIt.next();
        // log.debug(" char " + curChar );
        // log.debug(" -->  " + sorted.get(curChar) );
        result.add( (Collection) sorted.get(curChar) );
      }      
      return result;
    }
  }

}
