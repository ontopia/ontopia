package ontopoly.rest.editor.spi;

import java.util.Collection;


public interface PrestoField {

  public String getId();

  public PrestoSchemaProvider getSchemaProvider();

  public String getName();
  
  public boolean isNameField();

  public boolean isPrimitiveField();
  
  public boolean isReferenceField();

  public String getInverseFieldId();
  
  public PrestoType getType();
  
  public PrestoView getView();
  
  public PrestoView getValueView();
  
  public int getMinCardinality();

  public int getMaxCardinality();

  public String getInterfaceControl();
  
  public String getValidationType();

  public boolean isEmbedded();

  public boolean isTraversable();

  public boolean isReadOnly();

  public boolean isNewValuesOnly();

  public boolean isExistingValuesOnly();

  public String getFieldType();

  public String getDataType();

  public Collection<PrestoType> getAvailableFieldCreateTypes();

  public Collection<PrestoType> getAvailableFieldValueTypes();
  
}
