package ontopoly.rest.editor.spi;

import java.util.Collection;



public interface PrestoDataProvider {
  
  public PrestoTopic getTopicById(String id);

  public Collection<PrestoTopic> getAvailableFieldValues(PrestoField field);

  public PrestoChangeSet createTopic(PrestoType type);

  public PrestoChangeSet updateTopic(PrestoTopic topic);
 
  public boolean removeTopic(PrestoTopic topic);
  
  public void close();
  
}
