package ontopoly.rest.editor.spi.impl.ontopoly;

import java.util.Collection;
import java.util.Collections;

import net.ontopia.topicmaps.utils.PSI;
import ontopoly.model.Cardinality;
import ontopoly.model.EditMode;
import ontopoly.model.FieldDefinition;
import ontopoly.model.FieldsView;
import ontopoly.model.InterfaceControl;
import ontopoly.model.OccurrenceField;
import ontopoly.model.RoleField;
import ontopoly.model.ViewModes;
import ontopoly.rest.editor.spi.PrestoField;
import ontopoly.rest.editor.spi.PrestoTopic;
import ontopoly.rest.editor.spi.PrestoType;
import ontopoly.rest.editor.spi.PrestoView;

public class OntopolyField implements PrestoField {

  private final OntopolySession session;
  private final FieldDefinition fieldDefinition;
  private final PrestoType type;
  private final PrestoView view;
  
  // computed values
  private PrestoView valueView;
  private EditMode editMode;
  private ViewModes viewModes;
  private Cardinality cardinality;

  OntopolyField(OntopolySession session, FieldDefinition fieldDefinition, PrestoType type, PrestoView view) {
    this.session = session;
    this.fieldDefinition = fieldDefinition;
    this.type = type;
    this.view = view;
  }

  private EditMode getEditMode() {
    if (editMode == null) {
      if (fieldDefinition.getFieldType() == FieldDefinition.FIELD_TYPE_ROLE) {
        RoleField roleField = (RoleField)fieldDefinition;
        editMode = roleField.getEditMode();
      }
    }
    return editMode;
  }

  private ViewModes getViewModes() {
    if (viewModes == null) {
      viewModes = fieldDefinition.getViewModes(OntopolyView.getWrapped(getValueView()));
    }
    return viewModes;
  }

  private Cardinality getCardinality() {
    if (cardinality == null) {
      cardinality = fieldDefinition.getCardinality();
    }
    return cardinality;
  }

  public String getId() {
    return fieldDefinition.getId();
  }

  public String getDatabaseId() {
    return session.getDatabaseId();
  }

  public String getName() {
    return fieldDefinition.getFieldName();
  }

  public PrestoType getType() {
    return type;
  }

  public PrestoView getView() {
    return view;
  }

  public PrestoView getValueView() {
    if (valueView == null) {
      FieldsView fieldsView = OntopolyView.getWrapped(view);
      valueView = new OntopolyView(session, fieldDefinition.getValueView(fieldsView));          
    }
    return valueView;
  }

  public boolean isEmbedded() {
    ViewModes viewModes = getViewModes();
    return viewModes.isEmbedded();    
  }

  public boolean isTraversable() {
    ViewModes viewModes = getViewModes();
    return viewModes.isTraversable();
  }

  public Collection<PrestoType> getAvailableFieldTypes() {
    if (fieldDefinition.getFieldType() == FieldDefinition.FIELD_TYPE_ROLE) {
      RoleField roleField = (RoleField)fieldDefinition;
      int arity = roleField.getAssociationField().getArity();

      if (arity == 2) {

        FieldsView fieldsView = OntopolyView.getWrapped(view);        
        FieldsView childView = fieldDefinition.getValueView(fieldsView);
        EditMode editMode = roleField.getEditMode();
        ViewModes viewModes = fieldDefinition.getViewModes(childView);
        boolean allowCreate = !editMode.isNoEdit() && !editMode.isExistingValuesOnly() && !viewModes.isReadOnly();

        for (RoleField otherRoleField : roleField.getOtherRoleFields()) {

          if (allowCreate) {
            return OntopolyType.wrap(session, otherRoleField.getAllowedPlayerTypes(null));
          }
          break;
        }
      }
    }
    return Collections.emptyList();
  }

  public Collection<PrestoTopic> getAvailableFieldValues() {
    if (fieldDefinition.getFieldType() == FieldDefinition.FIELD_TYPE_ROLE) {
      RoleField roleField = (RoleField)fieldDefinition;
      int arity = roleField.getAssociationField().getArity();

      if (arity == 2) {

        FieldsView fieldsView = OntopolyView.getWrapped(view);
        FieldsView childView = fieldDefinition.getValueView(fieldsView);    

        EditMode editMode = roleField.getEditMode();
        ViewModes viewModes = fieldDefinition.getViewModes(childView);

        boolean allowAdd = !editMode.isNoEdit() && !editMode.isNewValuesOnly() && !viewModes.isReadOnly();

        for (RoleField otherRoleField : roleField.getOtherRoleFields()) {

          if (allowAdd) {
            return OntopolyTopic.wrap(session, otherRoleField.getAllowedPlayers(null));
          } else {
            return Collections.emptyList();          
          }
        }
      }
    }
    return Collections.emptyList();
  }

  public int getMinCardinality() {
    return getCardinality().getMinCardinality();
  }

  public int getMaxCardinality() {
    return getCardinality().getMaxCardinality();
  }

  public String getValidationType() {
    return fieldDefinition.getValidationType();
  }

  public boolean isReadOnly() {
    ViewModes viewModes = getViewModes();
    if (viewModes.isReadOnly()) return true;
    EditMode editMode = getEditMode();
    if (editMode != null && editMode.isNoEdit()) return true;
    return false;
  }

  public boolean isNewValuesOnly() {
    return getEditMode().isNewValuesOnly();
  }

  public boolean isExistingValuesOnly() {
    return getEditMode().isExistingValuesOnly();
  }

  public String getFieldType() {
    return fieldDefinition.getLocator().getExternalForm();
  }

  public String getDataType() {
    switch (fieldDefinition.getFieldType()) {
    case FieldDefinition.FIELD_TYPE_IDENTITY:
      return PSI.XSD_URI;
    case FieldDefinition.FIELD_TYPE_NAME:
      return PSI.XSD_STRING;
    case FieldDefinition.FIELD_TYPE_OCCURRENCE:
      OccurrenceField occurrenceField = (OccurrenceField)fieldDefinition;
      return occurrenceField.getDataType().getLocator().getExternalForm();
    case FieldDefinition.FIELD_TYPE_QUERY:
      return null;
    case FieldDefinition.FIELD_TYPE_ROLE:
      return null;
    default:
      throw new RuntimeException("Unknown field type: " + fieldDefinition);
    }
  }

  public boolean isPrimitiveField() {
    switch (fieldDefinition.getFieldType()) {
    case FieldDefinition.FIELD_TYPE_IDENTITY:
    case FieldDefinition.FIELD_TYPE_NAME:
    case FieldDefinition.FIELD_TYPE_OCCURRENCE:
      return true;
    default:
      return false;
    }
  }

  public boolean isReferenceField() {
    switch (fieldDefinition.getFieldType()) {
    case FieldDefinition.FIELD_TYPE_ROLE:
      RoleField roleField = (RoleField)fieldDefinition;
      return roleField.getAssociationField().getArity() == 2;
    default:
      return false;
    }

  }

  public String getInterfaceControl() {
    if (fieldDefinition.getFieldType() == FieldDefinition.FIELD_TYPE_ROLE) {
      RoleField roleField = (RoleField)fieldDefinition;
      for (RoleField otherRoleField : roleField.getOtherRoleFields()) {
        InterfaceControl interfaceControl = otherRoleField.getInterfaceControl();
        return interfaceControl.getLocator().getExternalForm();
      }
    }
    return null;
  }

}