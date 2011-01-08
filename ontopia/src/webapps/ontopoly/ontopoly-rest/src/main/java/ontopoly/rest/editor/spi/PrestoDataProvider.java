package ontopoly.rest.editor.spi;

import java.util.Collection;


public interface PrestoDataProvider {
  
  public PrestoTopic getTopicById(String id);

  public PrestoChangeSet createTopic(PrestoType type);

  public PrestoChangeSet updateTopic(PrestoTopic topic);
 
  public boolean removeTopic(PrestoTopic topic);
  
  public Collection<Object> getValues(PrestoTopic topic, PrestoField field);
  
  public void close();
  
}
