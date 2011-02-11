package ontopoly.rest.editor.spi;

import java.util.Collection;
import java.util.List;


public interface PrestoType {

  public String getId();
  
  public String getName();

  public PrestoSchemaProvider getSchemaProvider();

  public boolean isAbstract();

  public boolean isReadOnly();

  // TODO: public boolean delete();
  
  // TODO: getSuperType();
  
  public Collection<PrestoType> getDirectSubTypes();

  public List<PrestoField> getFields();

  public List<PrestoFieldUsage> getFields(PrestoView fieldsView);

  public PrestoField getFieldById(String fieldId);

  public PrestoFieldUsage getFieldById(String fieldId, PrestoView view);
  
  public PrestoView getDefaultView();

  public PrestoView getViewById(String viewId);

  public Collection<PrestoView> getViews(PrestoView fieldsView);

}
