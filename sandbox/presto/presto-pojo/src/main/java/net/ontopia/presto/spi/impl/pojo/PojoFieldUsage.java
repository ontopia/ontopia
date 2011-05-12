package net.ontopia.presto.spi.impl.pojo;

import java.util.Collection;

import net.ontopia.presto.spi.PrestoFieldUsage;
import net.ontopia.presto.spi.PrestoSchemaProvider;
import net.ontopia.presto.spi.PrestoType;
import net.ontopia.presto.spi.PrestoView;

public class PojoFieldUsage implements PrestoFieldUsage {

    private final PojoField field;
    private final PrestoType type;
    private final PrestoView view;
    
    PojoFieldUsage(PojoField field, PrestoType type, PrestoView view) {
        this.field = field;
        this.type = type;
        this.view = view;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof PojoFieldUsage) {
            PojoFieldUsage o = (PojoFieldUsage)other;
            return field.equals(o.field) && type.equals(o.type) && view.equals(o.view);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return field.hashCode() + type.hashCode() + view.hashCode();
    }

    public PrestoType getType() {
        return type;
    }

    public PrestoView getView() {
        return view;
    }
    
    public String getId() {
        return field.getId();
    }

    public PrestoSchemaProvider getSchemaProvider() {
        return field.getSchemaProvider();
    }

    public String getName() {
        return field.getName();
    }

    public boolean isNameField() {
        return field.isNameField();
    }

    public boolean isPrimitiveField() {
        return field.isPrimitiveField();
    }

    public boolean isReferenceField() {
        return field.isReferenceField();
    }

    public PrestoView getValueView() {
        return field.getValueView();
    }

    public int getMinCardinality() {
        return field.getMinCardinality();
    }

    public int getMaxCardinality() {
        return field.getMaxCardinality();
    }

    public String getDataType() {
        return field.getDataType();
    }

    public String getExternalType() {
        return field.getExternalType();
    }

    public String getValidationType() {
        return field.getValidationType();
    }

    public boolean isEmbedded() {
        return field.isEmbedded();
    }

    public boolean isHidden() {
        return field.isHidden();
    }

    public boolean isTraversable() {
        return field.isTraversable();
    }

    public boolean isReadOnly() {
        return field.isReadOnly();
    }

    public boolean isNewValuesOnly() {
        return field.isNewValuesOnly();
    }

    public boolean isExistingValuesOnly() {
        return field.isExistingValuesOnly();
    }

    public String getInverseFieldId() {
        return field.getInverseFieldId();
    }

    public String getInterfaceControl() {
        return field.getInterfaceControl();
    }

    public Collection<PrestoType> getAvailableFieldCreateTypes() {
        return field.getAvailableFieldCreateTypes();
    }

    public Collection<PrestoType> getAvailableFieldValueTypes() {
        return field.getAvailableFieldValueTypes();
    }

}
