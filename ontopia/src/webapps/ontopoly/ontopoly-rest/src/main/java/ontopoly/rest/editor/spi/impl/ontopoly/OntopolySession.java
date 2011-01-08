package ontopoly.rest.editor.spi.impl.ontopoly;

import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.TopicMaps;
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
