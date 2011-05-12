package net.ontopia.presto.spi.impl.couchdb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.ontopia.presto.spi.PrestoChangeSet;
import net.ontopia.presto.spi.PrestoDataProvider;
import net.ontopia.presto.spi.PrestoFieldUsage;
import net.ontopia.presto.spi.PrestoTopic;
import net.ontopia.presto.spi.PrestoType;

import org.codehaus.jackson.node.ObjectNode;
import org.ektorp.CouchDbConnector;
import org.ektorp.DocumentNotFoundException;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.ViewResult.Row;

public class CouchDataProvider implements PrestoDataProvider {

  private CouchDbConnector db;

  private final String designDocId;
  private final String fallbackViewId;

  private CouchDataStrategy dataStrategy = new CouchDataStrategy();

  public CouchDataProvider(CouchDbConnector db, String designDocId) {
      this(db, designDocId, null);
  }

  public CouchDataProvider(CouchDbConnector db, String designDocId, String fallbackViewId) {
    this.db = db;
    this.designDocId = designDocId;
    this.fallbackViewId = fallbackViewId;
  }

  CouchDbConnector getCouchConnector() {
    return db;
  }

  public PrestoTopic getTopicById(String topicId) {
    // look up by document id
    ObjectNode doc = null;
    try {
      doc = getCouchConnector().get(ObjectNode.class, topicId);
    } catch (DocumentNotFoundException e) {      
    }

    // look up document id in fallback view
    if (doc == null) {
      if (fallbackViewId != null) {
        ViewQuery query = new ViewQuery()
        .designDocId(designDocId)
        .viewName(fallbackViewId).includeDocs(true).key(topicId).limit(1);
        try {
          ViewResult viewResult = getCouchConnector().queryView(query);
          for (Row row : viewResult.getRows()) {
            doc = (ObjectNode)row.getValueAsNode();
            break;
          }
        } catch (DocumentNotFoundException e) {          
        }
      }
      if (doc == null) {
        throw new RuntimeException("Unknown topic: " + topicId);
      }
    }
    return existingTopic(topicId, doc);
  }

  public Collection<PrestoTopic> getAvailableFieldValues(PrestoFieldUsage field) {
    List<PrestoTopic> result = new ArrayList<PrestoTopic>();
    for (PrestoType type : field.getAvailableFieldValueTypes()) {
      ViewQuery query = new ViewQuery()
      .designDocId(designDocId)
      .viewName("by-type").includeDocs(true).key(type.getId());
      try {
        ViewResult viewResult = getCouchConnector().queryView(query);
        for (Row row : viewResult.getRows()) {
          String topicId = row.getId();
          ObjectNode doc = (ObjectNode)row.getDocAsNode();        
          result.add(existingTopic(topicId, doc));
        }
      } catch (DocumentNotFoundException e) {          
      }
    }
    Collections.sort(result, new Comparator<PrestoTopic>() {
      public int compare(PrestoTopic o1, PrestoTopic o2) {
        return compareComparables(o1.getName(), o2.getName());
      }
    });
    return result;
  }

  protected int compareComparables(String o1, String o2) {
    if (o1 == null)
      return (o2 == null ? 0 : -1);
    else if (o2 == null)
      return 1;
    else
      return o1.compareTo(o2);
  }
  
  public PrestoChangeSet createTopic(PrestoType type) {
    return new CouchChangeSet(this, type);
  }

  public PrestoChangeSet updateTopic(PrestoTopic topic) {
    return new CouchChangeSet(this, (CouchTopic)topic);
  }
  
  CouchTopic existingTopic(String topicId, ObjectNode doc) {
    return CouchTopic.existing(this, doc);
  }
  
  CouchTopic newInstance(PrestoType type) {
    return CouchTopic.newInstance(this, type);
  }

  public boolean removeTopic(PrestoTopic topic) {
    // TODO: Remove inverse references
    CouchTopic couchTopic = (CouchTopic)topic;
    getCouchConnector().delete(couchTopic.getData());
    return true;
  }

  public void close() {
  }

  public void setDataStrategy(CouchDataStrategy dataStrategy) {
    this.dataStrategy = dataStrategy;
  }

  public CouchDataStrategy getDataStrategy() {
    return dataStrategy;
  }

}
