package net.ontopia.presto.spi;

import java.util.Collection;
import java.util.List;


public interface PrestoType {

  String getId();
  
  String getName();

  PrestoSchemaProvider getSchemaProvider();

  boolean isAbstract();

  boolean isReadOnly();

  // TODO: public boolean delete();
  
  // TODO: getSuperType();
  
  Collection<PrestoType> getDirectSubTypes();

  List<PrestoField> getFields();

  List<PrestoFieldUsage> getFields(PrestoView fieldsView);

  PrestoField getFieldById(String fieldId);

  PrestoFieldUsage getFieldById(String fieldId, PrestoView view);
  
  PrestoView getDefaultView();

  PrestoView getViewById(String viewId);

  Collection<PrestoView> getViews(PrestoView fieldsView);

}
