
package net.ontopia.topicmaps.impl.rdbms.index;

import java.util.Iterator;
import java.util.Collection;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.impl.utils.IndexManagerIF;
import net.ontopia.topicmaps.core.index.OccurrenceIndexIF;
import net.ontopia.utils.GrabberIF;
import net.ontopia.utils.GrabberIterator;

/**
 * INTERNAL: The rdbms occurrence index implementation.
 */
public class OccurrenceIndex extends RDBMSIndex implements OccurrenceIndexIF {

  OccurrenceIndex(IndexManagerIF imanager) {
    super(imanager);
  }

  // ---------------------------------------------------------------------------
  // OccurrenceIndexIF
  // ---------------------------------------------------------------------------

  public Collection<OccurrenceIF> getOccurrences(String value) {
    return (Collection<OccurrenceIF>)executeQuery("OccurrenceIndexIF.getOccurrences", new Object[] { getTopicMap(), value });
  }
  
  public Collection<OccurrenceIF> getOccurrences(String value, LocatorIF datatype) {
    return (Collection<OccurrenceIF>)executeQuery("OccurrenceIndexIF.getOccurrencesByDataType", new Object[] { getTopicMap(), value, datatype.getAddress() });
  }

  public Collection<OccurrenceIF> getOccurrencesByPrefix(String prefix) {
    //! String ltval = prefix + Character.MAX_VALUE;
    String ltval = (prefix == null ? null : prefix.substring(0, prefix.length()- 1) + (char)(prefix.charAt(prefix.length()-1)+1));
    return (Collection<OccurrenceIF>)executeQuery("OccurrenceIndexIF.getOccurrencesBetween", new Object[] { getTopicMap(), prefix, ltval });
  }
  
  public Collection<OccurrenceIF> getOccurrencesByPrefix(String prefix, LocatorIF datatype) {
    //! String ltval = prefix + Character.MAX_VALUE;
    String ltval = (prefix == null ? null : prefix.substring(0, prefix.length()- 1) + (char)(prefix.charAt(prefix.length()-1)+1));
    return (Collection<OccurrenceIF>)executeQuery("OccurrenceIndexIF.getOccurrencesBetween_datatype", new Object[] { getTopicMap(), prefix, ltval, datatype.getAddress() });
  }

  public Iterator<OccurrenceIF> getValuesGreaterThanOrEqual(String value) {
    Collection coll = (Collection)executeQuery("OccurrenceIndexIF.getOccurrencesGreaterThanOrEqual", new Object[] { getTopicMap(), value });
    return new GrabberIterator(coll.iterator(), new GrabberIF() {
        public Object grab(Object o) {
          return ((OccurrenceIF)o).getValue();					
        }
      });
  }  

  public Iterator<OccurrenceIF> getValuesSmallerThanOrEqual(String value) {
    Collection coll = (Collection)executeQuery("OccurrenceIndexIF.getOccurrencesLessThanOrEqual", new Object[] { getTopicMap(), value });
    return new GrabberIterator(coll.iterator(), new GrabberIF() {
        public Object grab(Object o) {
          return ((OccurrenceIF)o).getValue();					
        }
      });
  }  
}
