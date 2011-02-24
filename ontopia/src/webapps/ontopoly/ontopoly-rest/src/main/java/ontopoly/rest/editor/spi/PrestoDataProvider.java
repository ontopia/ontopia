package ontopoly.rest.editor.spi;

import java.util.Collection;



public interface PrestoDataProvider {
  
  PrestoTopic getTopicById(String id);

  Collection<PrestoTopic> getAvailableFieldValues(PrestoFieldUsage field);

  PrestoChangeSet createTopic(PrestoType type);

  PrestoChangeSet updateTopic(PrestoTopic topic);
 
  boolean removeTopic(PrestoTopic topic);
  
  void close();
  
}
