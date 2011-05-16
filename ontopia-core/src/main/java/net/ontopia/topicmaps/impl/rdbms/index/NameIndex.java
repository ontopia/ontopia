
package net.ontopia.topicmaps.impl.rdbms.index;

import java.util.Collection;

import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
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
  
  // ---------------------------------------------------------------------------
  // NameIndexIF
  // ---------------------------------------------------------------------------
  
  public Collection<TopicNameIF> getTopicNames(String value) {
    return (Collection<TopicNameIF>)executeQuery("NameIndexIF.getTopicNames",
                                    new Object[] { getTopicMap(), value });
  }

  public Collection<VariantNameIF> getVariants(String value) {
    return (Collection<VariantNameIF>)executeQuery("NameIndexIF.getVariants",
                                         new Object[] { getTopicMap(), value });
  }

  public Collection<VariantNameIF> getVariants(String value, LocatorIF datatype) {
    return (Collection<VariantNameIF>)executeQuery("NameIndexIF.getVariantsByDataType", new Object[] { getTopicMap(), value, datatype.getAddress() });
	}

}





