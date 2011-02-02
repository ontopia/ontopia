package ontopoly.rest.editor.spi;


public interface PrestoField {

  public String getId();

  public PrestoSchemaProvider getSchemaProvider();

  public String getName();
  
  public boolean isNameField();

  public boolean isPrimitiveField();
  
  public boolean isReferenceField();

  public String getFieldType();
  
  public int getMinCardinality();

  public int getMaxCardinality();

  public String getDataType();
  
  public String getValidationType();

  public boolean isEmbedded();

  public boolean isTraversable();

  public boolean isReadOnly();

  // reference fields

  public boolean isNewValuesOnly();

  public boolean isExistingValuesOnly();
  
  public String getInverseFieldId();

  public String getInterfaceControl();
  
}
