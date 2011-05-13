package net.ontopia.presto.spi;

import java.util.Collection;
import java.util.List;


public interface PrestoType {

  String getId();
  
  String getName();

  PrestoSchemaProvider getSchemaProvider();

  boolean isReadOnly(); // can you edit it?

  boolean isHidden(); // will it show up? instances will show up if exposed as field values.

  boolean isCreatable(); // standalone creatable. default is yes. can still be created through a field if in createTypes.

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
