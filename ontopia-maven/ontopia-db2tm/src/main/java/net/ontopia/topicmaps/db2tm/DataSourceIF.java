
package net.ontopia.topicmaps.db2tm;

import java.util.Collection;

import net.ontopia.utils.*;

/**
 * INTERNAL: A data source interface. This interface is used to
 * represent a data source that is capable of returning a stream of
 * tuples for a limited number of relations.
 */
public interface DataSourceIF {

  /**
   * INTERNAL: Returns the relations that the data source knows
   * of. The collection return contains Relation instances.
   */
  public Collection getRelations();

  /**
   * INTERNAL: Returns a tuple reader for the given relation.
   */
  public TupleReaderIF getReader(String relation);

  /**
   * INTERNAL: Returns a changelog reader.
   */
  public ChangelogReaderIF getChangelogReader(Changelog changelog, String startOrder);

  /**
   * INTERNAL: Returns the maximum value of the order column for the
   * specified changelog.
   */
  public String getMaxOrderValue(Changelog changelog);

  /**
   * INTERNAL: Closes the data source so that it can release any open
   * resources.
   */
  public void close();
    
}
