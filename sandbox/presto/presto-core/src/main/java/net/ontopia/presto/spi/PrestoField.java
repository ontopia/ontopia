package net.ontopia.presto.spi;

public interface PrestoField {

  String getId();

  PrestoSchemaProvider getSchemaProvider();

  String getName();
  
  boolean isNameField();

  boolean isPrimitiveField();
  
  boolean isReferenceField();

  String getFieldType();
  
  int getMinCardinality();

  int getMaxCardinality();

  String getDataType();
  
  String getValidationType();

  boolean isEmbedded();

  boolean isTraversable();

  boolean isReadOnly();

  // reference fields

  boolean isNewValuesOnly();

  boolean isExistingValuesOnly();
  
  String getInverseFieldId();

  String getInterfaceControl();
  
}
