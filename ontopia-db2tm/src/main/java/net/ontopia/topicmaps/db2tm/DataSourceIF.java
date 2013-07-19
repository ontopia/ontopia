/*
 * #!
 * Ontopia DB2TM
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

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
