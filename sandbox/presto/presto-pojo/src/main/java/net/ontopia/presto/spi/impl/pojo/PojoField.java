package net.ontopia.presto.spi.impl.pojo;

import java.util.Collection;
import java.util.HashSet;

import net.ontopia.presto.spi.PrestoField;
import net.ontopia.presto.spi.PrestoSchemaProvider;
import net.ontopia.presto.spi.PrestoType;
import net.ontopia.presto.spi.PrestoView;

public class PojoField implements PrestoField {

    private String id;
    private PrestoSchemaProvider schemaProvider;
    private String name;
    private boolean isNameField;
//    private boolean isPrimitiveField;
//    private boolean isReferenceField;
    private PrestoView valueView;
//    private String fieldType;
    private int minCardinality;
    private int maxCardinality;
    private String dataType;
    private String externalType;
    private String validationType;
    private boolean isEmbedded;
    private boolean isHidden;
    private boolean isTraversable = true;
    private boolean isReadOnly;
    private boolean isNewValuesOnly;
    private boolean isExistingValuesOnly;
    private String inverseFieldId;
    private String interfaceControl;

    private Collection<PrestoType> availableFieldCreateTypes;
    private Collection<PrestoType> availableFieldValueTypes = new HashSet<PrestoType>();

    // helper members
    private Collection<PrestoView> definedInViews = new HashSet<PrestoView>();
    
    PojoField(String id, PrestoSchemaProvider schemaProvider) {
        this.id = id;
        this.schemaProvider = schemaProvider;        
    }

    public String getId() {
        return id;
    }

    public PrestoSchemaProvider getSchemaProvider() {
        return schemaProvider;
    }

    public String getName() {
        return name;
    }

    public boolean isNameField() {
        return isNameField;
    }

    public boolean isPrimitiveField() {
        return !isReferenceField();
//        return isPrimitiveField;
    }

    public boolean isReferenceField() {
        return dataType != null && dataType.equals("reference");
//        return isReferenceField;
    }

    public PrestoView getValueView() {
        return valueView;
    }

    public int getMinCardinality() {
        return minCardinality;
    }

    public int getMaxCardinality() {
        return maxCardinality;
    }

    public String getDataType() {
        return dataType;
    }

    public String getExternalType() {
        return externalType;
    }

    public String getValidationType() {
        return validationType;
    }

    public boolean isEmbedded() {
        return isEmbedded;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public boolean isTraversable() {
        return isTraversable;
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    public boolean isNewValuesOnly() {
        return isNewValuesOnly;
    }

    public boolean isExistingValuesOnly() {
        return isExistingValuesOnly;
    }

    public String getInverseFieldId() {
        return inverseFieldId;
    }

    public String getInterfaceControl() {
        return interfaceControl;
    }

    public Collection<PrestoType> getAvailableFieldCreateTypes() {
        // fall back to value types if none specified
        return availableFieldCreateTypes == null ? getAvailableFieldValueTypes() : availableFieldCreateTypes;
    }

    public Collection<PrestoType> getAvailableFieldValueTypes() {
        return availableFieldValueTypes;
    }

    // -- helper methods
    
    boolean isInView(PrestoView view) {
        for (PrestoView definedInView : definedInViews) {
            if (definedInView.equals(view)) {
                return true;
            }
        }
        return false;
    }

    protected void addDefinedInView(PrestoView view) {
        this.definedInViews.add(view);
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public void setNameField(boolean isNameField) {
        this.isNameField = isNameField;
    }

//    public void setPrimitiveField(boolean isPrimitiveField) {
//        this.isPrimitiveField = isPrimitiveField;
//    }
//
//    public void setReferenceField(boolean isReferenceField) {
//        this.isReferenceField = isReferenceField;
//    }

    public void setValueView(PrestoView valueView) {
        this.valueView = valueView;
    }

//    public void setFieldType(String fieldType) {
//        this.fieldType = fieldType;
//    }

    public void setMinCardinality(int minCardinality) {
        this.minCardinality = minCardinality;
    }

    public void setMaxCardinality(int maxCardinality) {
        this.maxCardinality = maxCardinality;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public void setValidationType(String validationType) {
        this.validationType = validationType;
    }

    public void setEmbedded(boolean isEmbedded) {
        this.isEmbedded = isEmbedded;
    }

    public void setHidden(boolean isHidden) {
        this.isHidden = isHidden;
    }

    public void setTraversable(boolean isTraversable) {
        this.isTraversable = isTraversable;
    }

    public void setReadOnly(boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
    }

    public void setNewValuesOnly(boolean isNewValuesOnly) {
        this.isNewValuesOnly = isNewValuesOnly;
    }

    public void setExistingValuesOnly(boolean isExistingValuesOnly) {
        this.isExistingValuesOnly = isExistingValuesOnly;
    }

    public void setInverseFieldId(String inverseFieldId) {
        this.inverseFieldId = inverseFieldId;
    }

    public void setInterfaceControl(String interfaceControl) {
        this.interfaceControl = interfaceControl;
    }

    protected void addAvailableFieldCreateType(PrestoType type) {
        if (this.availableFieldCreateTypes == null) {
            this.availableFieldCreateTypes = new HashSet<PrestoType>();
        }
        this.availableFieldCreateTypes.add(type);
    }

    protected void addAvailableFieldValueType(PrestoType type) {
        this.availableFieldValueTypes.add(type);
    }

    public void setExternalType(String externalType) {
        this.externalType = externalType;
    }

}
