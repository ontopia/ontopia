package ontopoly.rest.editor.spi;

import java.util.Collection;
import java.util.List;


public interface PrestoType {

  public String getId();

  public String getDatabaseId();
  
  public String getName();

  public boolean isAbstract();

  public Collection<PrestoType> getDirectSubTypes();

  public List<PrestoField> getFields(PrestoView fieldsView);

  public Collection<PrestoView> getViews(PrestoView fieldsView);

}
