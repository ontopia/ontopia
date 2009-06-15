// $Id: DefaultIfDecider.java,v 1.17 2004/11/12 11:25:35 grove Exp $

package net.ontopia.topicmaps.nav2.impl.basic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.ontopia.utils.CollectionUtils;
import net.ontopia.topicmaps.nav2.core.NavigatorDeciderIF;
import net.ontopia.topicmaps.nav2.core.NavigatorPageIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.utils.NavigatorUtils;

import org.apache.log4j.Logger;

/**
 * INTERNAL: class which implements interface NavigatorDeciderIF.
 * Execute some rudimentary testing against Collection:
 * <ul>
 *  <li>collection is non-empty? (<i>default</i>)
 *  <li>collection equals other collection specified by
 *      variable name (<code>equals</code>)
 *  <li>collection has <code>less-than</code> number of entries?
 *  <li>collection has <code>greater-than</code> number of entries?
 * </ul>
 *
 * @see net.ontopia.topicmaps.nav2.taglibs.logic.IfTag
 */
public class DefaultIfDecider implements NavigatorDeciderIF {

  // initialization of logging facility
  private static Logger log = Logger
    .getLogger(DefaultIfDecider.class.getName());
  
  // members
  private String equalsVariableName = null;
  private int    lessThanNumber     = -1;
  private int    greaterThanNumber  = -1;
  private int    equalsSize         = -1;
  
  /**
   * INTERNAL: empty constructor which set all comparision values to
   * their default state, it is then only checked if collection is non-empty. 
   */
  public DefaultIfDecider() {
    // use default values
  }
  
  /**
   * INTERNAL: Default constructor.
   */
  public DefaultIfDecider(String equalsVariableName, int equalsSize,
                          int lessThan, int greaterThan) {
    this.equalsVariableName = equalsVariableName;
    this.equalsSize = equalsSize;
    this.lessThanNumber = lessThan;
    this.greaterThanNumber = greaterThan;

    // Default operation is greaterThanNumber=0 if no criteria is specified.
    if (equalsVariableName == null &&
        equalsSize == -1 &&
        lessThanNumber == -1 &&
        greaterThanNumber == -1)
      greaterThanNumber = 0;
  }
  
  // -----------------------------------------------------------
  // Implementation of NavigatorDeciderIF
  // -----------------------------------------------------------

  public boolean ok(NavigatorPageIF contextTag, Object obj) {
    if (obj == null)
      return false;
    // log.debug("--> ok? " + obj + " class: " + obj.getClass().getName());
    if (obj instanceof Collection) {
      Collection collection = (Collection) obj;

      // Operations:
      //   is-empty: none
      //   exact-size: sizeEquals
      //   between: lessThan + greaterThan
      //   smaller-than: lessThan
      //   bigger-than: greaterThan
      //   equals: equals (variable name)

      // Perform operations
      if (greaterThanNumber != -1 && collection.size() <= greaterThanNumber)
          return false;
      
      if (equalsSize != -1 && collection.size() != equalsSize)
          return false;

      if (lessThanNumber != -1 && collection.size() >= lessThanNumber)
          return false;

      if (equalsVariableName != null) {
        // get collection to compare with (never null)
        Collection collection2 = contextTag.getContextManager().getValue(equalsVariableName);

        // Compare collections as described in comments to bug #599.
        return CollectionUtils.equalsUnorderedSet(collection, collection2);
      }
      
    } else {
      // FIXME: Support Maps? Useful, since tm:tolog can return a Map.
      log.warn("Object from unsupported class '" +
               obj.getClass().getName() + "' must be a Collection.");
      return false;
    }
    return true;
  }
  
}





