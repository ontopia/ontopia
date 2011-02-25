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

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.DocumentNotFoundException;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.ViewResult.Row;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;

public class CouchDataProvider implements PrestoDataProvider {

  private CouchDbConnector db;

  private final ObjectMapper mapper = new ObjectMapper();
  private final String designDocId;
  private final String fallbackViewId;

  public CouchDataProvider(String host, int port, String databaseName, String designDocId) {
      this(host, port, databaseName, designDocId, null);
  }
  
  public CouchDataProvider(String host, int port, String databaseName, String designDocId, String fallbackViewId) {

    this.designDocId = designDocId;
    this.fallbackViewId = fallbackViewId;
    
    HttpClient httpClient = new StdHttpClient.Builder()
    .host(host)
    .port(port)
    .build();

    CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);

    db = new StdCouchDbConnector(databaseName, dbInstance);
    db.createDatabaseIfNotExists();
  }

  CouchDbConnector getCouchConnector() {
    return db;
  }

  ObjectMapper getObjectMapper() {
    return mapper;
  }

  public PrestoTopic getTopicById(String topicId) {
    // look up by document id
    ObjectNode doc = null;
    try {
      doc = db.get(ObjectNode.class, topicId);
    } catch (DocumentNotFoundException e) {      
    }
    if (doc == null) {
      if (fallbackViewId != null) {
        // look up identity in in fallback view
        ViewQuery query = new ViewQuery()
        .designDocId(designDocId)
        .viewName(fallbackViewId).includeDocs(true).key(topicId).limit(1);
        try {
          ViewResult viewResult = db.queryView(query);
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
    return CouchTopic.existing(this, doc);
  }

  public Collection<PrestoTopic> getAvailableFieldValues(PrestoFieldUsage field) {
    List<PrestoTopic> result = new ArrayList<PrestoTopic>();
    for (PrestoType type : field.getAvailableFieldValueTypes()) {
      ViewQuery query = new ViewQuery()
      .designDocId(designDocId)
      .viewName("by-type").includeDocs(true).key(type.getId());
      try {
        ViewResult viewResult = db.queryView(query);
        for (Row row : viewResult.getRows()) {
          ObjectNode doc = (ObjectNode)row.getDocAsNode();        
          result.add(CouchTopic.existing(this, doc));
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

  public boolean removeTopic(PrestoTopic topic) {
    // TODO: Remove inverse references
    CouchTopic couchTopic = (CouchTopic)topic;
    db.delete(couchTopic.getData());
    return true;
  }

  public void close() {
  }

}
