// $Id: NameIndex.java,v 1.22 2008/06/24 12:43:40 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.rdbms.index;

import java.util.Collection;

import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.impl.utils.IndexManagerIF;
import net.ontopia.topicmaps.core.index.NameIndexIF;
import net.ontopia.infoset.core.LocatorIF;

/**
 * INTERNAL: The rdbms name index implementation.
 */

public class NameIndex extends RDBMSIndex implements NameIndexIF {

  NameIndex(IndexManagerIF imanager) {
    super(imanager);
  }
  
  // -----------------------------------------------------------------------------
  // NameIndexIF
  // -----------------------------------------------------------------------------
  
  public Collection getTopicNames(String value) {
		return (Collection)executeQuery("NameIndexIF.getTopicNames", new Object[] { getTopicMap(), value });
  }

  public Collection getVariants(String value) {
    return (Collection)executeQuery("NameIndexIF.getVariants", new Object[] { getTopicMap(), value });
  }

  public Collection getVariants(String value, LocatorIF datatype) {
    return (Collection)executeQuery("NameIndexIF.getVariantsByDataType", new Object[] { getTopicMap(), value, datatype.getAddress() });
	}

}





