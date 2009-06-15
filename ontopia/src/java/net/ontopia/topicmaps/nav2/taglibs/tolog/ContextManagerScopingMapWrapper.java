
// $Id: ContextManagerScopingMapWrapper.java,v 1.3 2005/10/11 08:48:11 larsga Exp $

package net.ontopia.topicmaps.nav2.taglibs.tolog;

import java.util.Collection;

import net.ontopia.topicmaps.nav2.core.ContextManagerIF;
import net.ontopia.utils.CollectionUtils;

/**
 * INTERNAL: Helper class used when creating QueryProcessors. Helps
 * the query processor map query variable names to the values of a
 * particular row, and make these available to the ContextManager.
 */
public class ContextManagerScopingMapWrapper extends ContextManagerMapWrapper {

  public ContextManagerScopingMapWrapper(ContextManagerIF contextManager) {
    super(contextManager);
  }

  public Object get(Object key) {
    Collection coll = (Collection) super.get(key);
    if (coll == null)
      return null;
    return CollectionUtils.getFirstElement(coll);
  }
}
