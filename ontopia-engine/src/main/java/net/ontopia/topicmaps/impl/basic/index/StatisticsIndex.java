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

package net.ontopia.topicmaps.impl.basic.index;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.core.index.StatisticsIndexIF;
import net.ontopia.topicmaps.impl.utils.BasicIndex;
import net.ontopia.topicmaps.impl.utils.IndexManagerIF;
import net.ontopia.topicmaps.impl.utils.TopicMapTransactionIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: The basic implementation of the statistics index.
 */
public class StatisticsIndex extends BasicIndex implements StatisticsIndexIF {

  private final ClassInstanceIndexIF index;
  private final TopicMapTransactionIF transaction;

  StatisticsIndex(IndexManagerIF imanager, TopicMapTransactionIF transaction) {
    index = (ClassInstanceIndexIF) imanager.getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");
    this.transaction = transaction;
  }

  private int queryCount(String query) {
    QueryProcessorIF queryProcessor = QueryUtils.getQueryProcessor(transaction.getTopicMap());
    QueryResultIF result = null;
    try {
      result = queryProcessor.execute(query);
      if (result.getWidth() == 0) {
        throw new OntopiaRuntimeException("Counting query did not produce a result");
      }
      if (result.next()) {
        return ((Integer) result.getValue(0)).intValue();
      }
      return 0;
    } catch (InvalidQueryException iqe) {
      throw new OntopiaRuntimeException("Invalid query in statistics index: " + iqe.getMessage(), iqe);
    } finally {
      if (result != null) {
        result.close();
      }
    }
  }

  // -----------------------------------------------------------------------------
  // StatisticsIndexIF
  // -----------------------------------------------------------------------------

  @Override
  public int getTopicCount() {
    return transaction.getTopicMap().getTopics().size();
  }

  @Override
  public int getTypedTopicCount() {
    int typed = 0;
    for (TopicIF tt : index.getTopicTypes()) {
      typed += index.getTopics(tt).size();
    }
    return typed;
  }

  @Override
  public int getUntypedTopicCount() {
    return index.getTopics(null).size();
  }

  @Override
  public int getTopicTypeCount() {
    return index.getTopicTypes().size();
  }

  @Override
  public int getAssociationCount() {
    return transaction.getTopicMap().getAssociations().size();
  }

  @Override
  public int getAssociationTypeCount() {
    return index.getAssociationTypes().size();
  }

  @Override
  public int getRoleCount() {
    return queryCount("select count($role) from association-role($a, $role)?");
  }

  @Override
  public int getRoleTypeCount() {
    return index.getAssociationRoleTypes().size();
  }

  @Override
  public int getOccurrenceCount() {
    return queryCount("select count($occurrence) from occurrence($t, $occurrence)?");
  }

  @Override
  public int getOccurrenceTypeCount() {
    return index.getOccurrenceTypes().size();
  }

  @Override
  public int getTopicNameCount() {
    return queryCount("select count($name) from topic-name($t, $name)?");
  }

  @Override
  public int getNoNameTopicCount() {
    return queryCount("select count($topic) from topic($topic), not(topic-name($topic, $n))?");
  }

  @Override
  public int getTopicNameTypeCount() {
    return index.getTopicNameTypes().size();
  }

  @Override
  public int getVariantCount() {
    return queryCount("select count($variant) from variant($t, $variant)?");
  }

  @Override
  public int getSubjectIdentifierCount() {
    return queryCount("select count($loc) from subject-identifier($t, $loc)?");
  }

  @Override
  public int getSubjectLocatorCount() {
    return queryCount("select count($loc) from subject-locator($t, $loc)?");
  }

  @Override
  public int getItemIdentifierCount() {
    return queryCount("select count($loc) from item-identifier($t, $loc)?");
  }
}
