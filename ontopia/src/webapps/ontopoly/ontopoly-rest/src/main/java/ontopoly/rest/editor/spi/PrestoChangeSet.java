package ontopoly.rest.editor.spi;

import java.util.Collection;

public interface PrestoChangeSet {

  public void setValues(PrestoField field, Collection<Object> values);

  public void addValues(PrestoField field, Collection<Object> values);

  public void removeValues(PrestoField field, Collection<Object> values);

  public PrestoTopic save();
  
}
