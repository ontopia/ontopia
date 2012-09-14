
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
      if (result.getWidth() == 0) throw new OntopiaRuntimeException("Counting query did not produce a result");
      if (result.next()) {
        return ((Integer) result.getValue(0)).intValue();
      }
      return 0;
    } catch (InvalidQueryException iqe) {
      throw new OntopiaRuntimeException("Invalid query in statistics index: " + iqe.getMessage(), iqe);
    } finally {
      if (result != null) result.close();
    }
  }

  // -----------------------------------------------------------------------------
  // StatisticsIndexIF
  // -----------------------------------------------------------------------------

  public int getTopicCount() {
    return transaction.getTopicMap().getTopics().size();
  }

  public int getTypedTopicCount() {
    int typed = 0;
    for (TopicIF tt : index.getTopicTypes()) {
      typed += index.getTopics(tt).size();
    }
    return typed;
  }

  public int getUntypedTopicCount() {
    return index.getTopics(null).size();
  }

  public int getTopicTypeCount() {
    return index.getTopicTypes().size();
  }

  public int getAssociationCount() {
    return transaction.getTopicMap().getAssociations().size();
  }

  public int getAssociationTypeCount() {
    return index.getAssociationTypes().size();
  }

  public int getRoleCount() {
    return queryCount("select count($role) from association-role($a, $role)?");
  }

  public int getRoleTypeCount() {
    return index.getAssociationRoleTypes().size();
  }

  public int getOccurrenceCount() {
    return queryCount("select count($occurrence) from occurrence($t, $occurrence)?");
  }

  public int getOccurrenceTypeCount() {
    return index.getOccurrenceTypes().size();
  }

  public int getTopicNameCount() {
    return queryCount("select count($name) from topic-name($t, $name)?");
  }

  public int getNoNameTopicCount() {
    return queryCount("select count($topic) from topic($topic), not(topic-name($topic, $n))?");
  }

  public int getTopicNameTypeCount() {
    return index.getTopicNameTypes().size();
  }

  public int getVariantCount() {
    return queryCount("select count($variant) from variant($t, $variant)?");
  }

  public int getSubjectIdentifierCount() {
    return queryCount("select count($loc) from subject-identifier($t, $loc)?");
  }

  public int getSubjectLocatorCount() {
    return queryCount("select count($loc) from subject-locator($t, $loc)?");
  }

  public int getItemIdentifierCount() {
    return queryCount("select count($loc) from item-identifier($t, $loc)?");
  }
}
