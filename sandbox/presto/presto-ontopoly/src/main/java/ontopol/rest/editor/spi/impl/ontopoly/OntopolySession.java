package ontopoly.rest.editor.spi.impl.ontopoly;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.TopicMaps;
import ontopoly.model.Topic;
import ontopoly.model.TopicMap;
import ontopoly.rest.editor.spi.PrestoDataProvider;
import ontopoly.rest.editor.spi.PrestoSchemaProvider;
import ontopoly.rest.editor.spi.PrestoSession;

public class OntopolySession implements PrestoSession {

  private final String topicMapId;
  private final TopicMapStoreIF store;
  private final TopicMap topicMap;
  
  private final PrestoSchemaProvider schemaProvider;
  private final PrestoDataProvider dataProvider;
  private String stableIdPrefix;
  
  public OntopolySession(String topicMapId) {
    this.topicMapId = topicMapId;
    
    this.store = TopicMaps.createStore(topicMapId, true);
    this.topicMap = new TopicMap(store.getTopicMap(), topicMapId);
    
    this.schemaProvider = new OntopolySchemaProvider(this);
    this.dataProvider = new OntopolyDataProvider(this);
  }
  
  public OntopolySession(String topicMapId, PrestoDataProvider dataProvider) {
    this.topicMapId = topicMapId;
    
    this.store = TopicMaps.createStore(topicMapId, true);
    this.topicMap = new TopicMap(store.getTopicMap(), topicMapId);
    
    this.schemaProvider = new OntopolySchemaProvider(this);
    this.dataProvider = dataProvider;
  }
  
  public String getStableIdPrefix() {
    return stableIdPrefix;
  }
  
  public void setStableIdPrefix(String stableIdPrefix) {
    this.stableIdPrefix = stableIdPrefix;
  }
  
  String getStableId(Topic topic) {
    if (stableIdPrefix == null) {
      return topic.getId();
    }
    String stableId = null;
    for (LocatorIF loc : topic.getTopicIF().getSubjectIdentifiers()) {
      String address = loc.getExternalForm();
      if (address.startsWith(stableIdPrefix)) {
        if (stableId == null || address.compareTo(stableId) < 0) {
          stableId = address;
        }
      }
    }
    return stableId != null ? stableId.substring(stableIdPrefix.length()) : topic.getId();
  }

  TopicIF getTopicById(String topicId) {
    TopicIF topic = topicMap.getTopicIFById(topicId);
    if (topic == null && stableIdPrefix != null) {
      topic = topicMap.getTopicIFById(stableIdPrefix + topicId);
    }
    if (topic == null) 
      throw new RuntimeException("Could not find topic with id '" + topicId + "'");
    return topic;      
  }
  public TopicMap getTopicMap() {
    return topicMap;   
  }
  
  public String getDatabaseId() {
    return topicMapId;
  }

  public String getDatabaseName() {
    return topicMap.getName();
  }

  public PrestoDataProvider getDataProvider() {
    return dataProvider;
  }

  public PrestoSchemaProvider getSchemaProvider() {
    return schemaProvider;
  }
  
  public void abort() {
    store.abort();
  }

  public void commit() {
    store.commit();
  }

  public void close() {
    dataProvider.close();
    store.close();
  }
  
}
