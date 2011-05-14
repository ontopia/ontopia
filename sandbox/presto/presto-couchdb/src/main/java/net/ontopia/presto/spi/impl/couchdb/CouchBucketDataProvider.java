package net.ontopia.presto.spi.impl.couchdb;

import net.ontopia.presto.spi.PrestoType;

import org.codehaus.jackson.node.ObjectNode;
import org.ektorp.CouchDbConnector;

public class CouchBucketDataProvider extends CouchDataProvider {

  protected String writeBucket;
  protected String[] readBuckets;

  public CouchBucketDataProvider(CouchDbConnector db, String writeBucket, String[] readBuckets) {
    super(db);
    this.writeBucket = writeBucket;
    this.readBuckets = readBuckets;
  }

  String getWriteBucket() {
    return writeBucket;
  }
  
  String[] getReadBuckets() {
    return readBuckets;
  }
  
  @Override
  protected CouchTopic existing(String topicId, ObjectNode doc) {
    return new CouchBucketTopic(this, doc);
  }

  @Override
  protected CouchTopic newInstance(PrestoType type) {
    return new CouchBucketTopic(this, CouchTopic.newInstanceObjectNode(this, type));
  }

  // builder pattern
  
  public CouchBucketDataProvider designDocId(String designDocId) {    
    this.designDocId = designDocId;
    return this;
  }
  
  public CouchBucketDataProvider fallbackViewId(String fallbackViewId) {    
    this.fallbackViewId = fallbackViewId;
    return this;
  }

}
