package net.ontopia.presto.spi;

public interface PrestoField {

  String getId();

  PrestoSchemaProvider getSchemaProvider();

  String getName();
  
  boolean isNameField();

  boolean isPrimitiveField();
  
  boolean isReferenceField();
  
  int getMinCardinality();

  int getMaxCardinality();

  String getDataType();

  String getExternalType();

  String getValidationType(); // ISSUE: or concreteType/actualType?

  boolean isEmbedded();

  boolean isHidden();

  boolean isTraversable();

  boolean isReadOnly();

  // reference fields

  boolean isNewValuesOnly();

  boolean isExistingValuesOnly();
  
  String getInverseFieldId();

  String getInterfaceControl();
  
}
