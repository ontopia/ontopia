package ontopoly.rest.editor.spi;

import java.util.Collection;

public interface PrestoChangeSet {

  public void setValues(PrestoFieldUsage field, Collection<Object> values);

  public void addValues(PrestoFieldUsage field, Collection<Object> values);

  public void removeValues(PrestoFieldUsage field, Collection<Object> values);

  public PrestoTopic save();
  
}
