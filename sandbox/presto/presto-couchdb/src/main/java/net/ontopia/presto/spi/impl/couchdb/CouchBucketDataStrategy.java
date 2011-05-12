package net.ontopia.presto.spi.impl.couchdb;

import net.ontopia.presto.spi.PrestoField;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

public class CouchBucketDataStrategy extends CouchDataStrategy {

  protected String writeBucket;
  protected String[] readBuckets;

  public CouchBucketDataStrategy(String writeBucket, String[] readBuckets) {
    this.writeBucket = writeBucket;
    this.readBuckets = readBuckets;
  }

  // json field data access strategy

  @Override
  public ArrayNode getFieldValue(CouchTopic topic, PrestoField field) {
    ObjectNode data = getReadBucket(topic, field);
    if (data != null) {
      JsonNode value = data.get(field.getId());
      return (ArrayNode)(value != null && value.isArray() ? value : null);
    }
    return null;
  }

  @Override
  public void putFieldValue(CouchTopic topic, PrestoField field, ArrayNode value) {
    getWriteBucket(topic, field, true).put(field.getId(), value);
  }

  @Override
  public void removeFieldValue(CouchTopic topic, PrestoField field) {
    ObjectNode writeBucket = getWriteBucket(topic, field, false);
    if (writeBucket != null) {
      writeBucket.remove(field.getId());
    }
  }

  protected ObjectNode getReadBucket(CouchTopic topic, PrestoField field) {
    ObjectNode data = topic.getData();
    // find the right bucket
    for (String bucketId : readBuckets) {
      if (data.has(bucketId)) {
        ObjectNode bucket = (ObjectNode)data.get(bucketId);
        if (bucket.has(field.getId())) {
          return bucket;
        }
      }
    }
    return null;    
  }

  protected ObjectNode getWriteBucket(CouchTopic topic, PrestoField field, boolean create) {
    ObjectNode data = topic.getData();
    ObjectNode bucket = null;
    if (data.has(writeBucket)) {
      bucket = (ObjectNode)data.get(writeBucket);
    }
    if (bucket == null && create) {
      bucket = getObjectMapper().createObjectNode();
      data.put(writeBucket, bucket);
    }
    return bucket;
  }

}
