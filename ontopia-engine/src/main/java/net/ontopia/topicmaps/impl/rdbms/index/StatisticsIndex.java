/*
 * #!
 * Ontopia Engine
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

package net.ontopia.topicmaps.impl.rdbms.index;

import net.ontopia.persistence.proxy.QueryResultIF;
import net.ontopia.topicmaps.core.index.StatisticsIndexIF;
import net.ontopia.topicmaps.impl.utils.IndexManagerIF;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: The RDBMS implementation of the statistics index.
 */
public class StatisticsIndex extends RDBMSIndex implements StatisticsIndexIF {

  StatisticsIndex(IndexManagerIF imanager) {
    super(imanager);
  }

  private int getCount(String query) {
    QueryResultIF result = null;
    try {
      result = (QueryResultIF) executeQuery(query, new Object[] { getTopicMap() });
      if (result.next()) {
        return ((Integer) result.getValue(0)).intValue();
      }
      throw new OntopiaRuntimeException("Statistics query " + query + " failed to produce result");
    } finally {
      if (result != null) {
        result.close();
      }
    }
  }

  // -----------------------------------------------------------------------------
  // StatisticsIndexIF
  // -----------------------------------------------------------------------------

  // ---------------------------------------------------------------------------
  // Topic stats
  // ---------------------------------------------------------------------------

  @Override
  public int getTopicCount() {
    return getCount("Stats.topics");
  }

  @Override
  public int getTypedTopicCount() {
    return getCount("Stats.topicTyped");
  }

  @Override
  public int getUntypedTopicCount() {
    return getCount("Stats.topicUntyped");
  }

  @Override
  public int getTopicTypeCount() {
    return getCount("Stats.topicTypes");
  }

  // ---------------------------------------------------------------------------
  // TopicName stats
  // ---------------------------------------------------------------------------

  @Override
  public int getTopicNameCount() {
    return getCount("Stats.names");
  }

  @Override
  public int getNoNameTopicCount() {
    return getCount("Stats.nonames");
  }

  @Override
  public int getTopicNameTypeCount() {
    return getCount("Stats.nameTypes");
  }

  // ---------------------------------------------------------------------------
  // VariantName stats
  // ---------------------------------------------------------------------------

  @Override
  public int getVariantCount() {
    return getCount("Stats.variants");
  }

  // ---------------------------------------------------------------------------
  // Occurrence stats
  // ---------------------------------------------------------------------------

  @Override
  public int getOccurrenceCount() {
    return getCount("Stats.occurrences");
  }

  @Override
  public int getOccurrenceTypeCount() {
    return getCount("Stats.occurrenceTypes");
  }

  // ---------------------------------------------------------------------------
  // Association stats
  // ---------------------------------------------------------------------------

  @Override
  public int getAssociationCount() {
    return getCount("Stats.associations");
  }

  @Override
  public int getAssociationTypeCount() {
    return getCount("Stats.associationTypes");
  }

  // ---------------------------------------------------------------------------
  // Association role stats
  // ---------------------------------------------------------------------------

  @Override
  public int getRoleCount() {
    return getCount("Stats.roles");
  }

  @Override
  public int getRoleTypeCount() {
    return getCount("Stats.roleTypes");
  }

  // ---------------------------------------------------------------------------
  // Locator stats
  // ---------------------------------------------------------------------------

  @Override
  public int getSubjectIdentifierCount() {
    return getCount("Stats.psis");
  }

  @Override
  public int getSubjectLocatorCount() {
    return getCount("Stats.sls");
  }

  @Override
  public int getItemIdentifierCount() {
    return getCount("Stats.iis");
  }

}
