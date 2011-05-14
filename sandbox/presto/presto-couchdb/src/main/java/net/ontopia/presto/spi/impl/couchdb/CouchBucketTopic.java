package net.ontopia.presto.spi.impl.couchdb;

import net.ontopia.presto.spi.PrestoField;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

public class CouchBucketTopic extends CouchTopic {
  
  protected CouchBucketTopic(CouchBucketDataProvider dataProvider, ObjectNode data) {
    super(dataProvider, data);
  }

  @Override
  protected CouchBucketDataProvider getDataProvider() {
    return (CouchBucketDataProvider)super.getDataProvider();
  }

  // json field data access strategy

  @Override
  protected ArrayNode getFieldValue(PrestoField field) {
    ObjectNode data = getReadBucket(field);
    if (data != null) {
      JsonNode value = data.get(field.getId());
      return (ArrayNode)(value != null && value.isArray() ? value : null);
    }
    return null;
  }

  @Override
  protected void putFieldValue(PrestoField field, ArrayNode value) {
    getWriteBucket(field, true).put(field.getId(), value);
  }

  @Override
  protected void removeFieldValue(PrestoField field) {
    ObjectNode writeBucket = getWriteBucket(field, false);
    if (writeBucket != null) {
      writeBucket.remove(field.getId());
    }
  }

  protected ObjectNode getReadBucket(PrestoField field) {
    ObjectNode data = getData();
    // find the right bucket
    for (String bucketId : getDataProvider().getReadBuckets()) {
      if (data.has(bucketId)) {
        ObjectNode bucket = (ObjectNode)data.get(bucketId);
        if (bucket.has(field.getId())) {
          return bucket;
        }
      }
    }
    return null;    
  }

  protected ObjectNode getWriteBucket(PrestoField field, boolean create) {
    String writeBucket = getDataProvider().getWriteBucket();
    ObjectNode data = getData();
    ObjectNode bucket = null;
    if (data.has(writeBucket)) {
      bucket = (ObjectNode)data.get(writeBucket);
    }
    if (bucket == null && create) {
      bucket = getDataProvider().getObjectMapper().createObjectNode();
      data.put(writeBucket, bucket);
    }
    return bucket;
  }

}
