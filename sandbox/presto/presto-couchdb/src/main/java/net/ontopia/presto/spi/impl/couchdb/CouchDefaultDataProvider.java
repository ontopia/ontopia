package net.ontopia.presto.spi.impl.couchdb;

import net.ontopia.presto.spi.PrestoType;

import org.codehaus.jackson.node.ObjectNode;
import org.ektorp.CouchDbConnector;

public class CouchDefaultDataProvider extends CouchDataProvider {

  public CouchDefaultDataProvider(CouchDbConnector db) {
    super(db);
  }
  
  @Override
  protected CouchTopic existing(String topicId, ObjectNode doc) {
    return new CouchDefaultTopic(this, doc);
  }

  @Override
  protected CouchTopic newInstance(PrestoType type) {
    return new CouchDefaultTopic(this, CouchTopic.newInstanceObjectNode(this, type));
  }

  // builder pattern
  
  public CouchDefaultDataProvider designDocId(String designDocId) {    
    this.designDocId = designDocId;
    return this;
  }
  
  public CouchDefaultDataProvider fallbackViewId(String fallbackViewId) {    
    this.fallbackViewId = fallbackViewId;
    return this;
  }
  
}
