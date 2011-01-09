package ontopoly.rest.editor.spi;



public interface PrestoDataProvider {
  
  public PrestoTopic getTopicById(String id);

  public PrestoChangeSet createTopic(PrestoType type);

  public PrestoChangeSet updateTopic(PrestoTopic topic);
 
  public boolean removeTopic(PrestoTopic topic);
  
  public void close();
  
}
