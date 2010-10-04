package ontopoly.components;

import java.util.List;

import net.ontopia.utils.OntopiaRuntimeException;
import ontopoly.model.FieldAssignment;
import ontopoly.model.FieldDefinition;
import ontopoly.model.FieldInstance;
import ontopoly.model.FieldsView;
import ontopoly.model.RoleField;
import ontopoly.models.FieldInstanceModel;
import ontopoly.models.FieldsViewModel;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

public class FieldInstancesPanel extends Panel {

  protected boolean readonly;
  
  public FieldInstancesPanel(String id, 
      List<FieldInstanceModel> fieldInstanceModels, final FieldsViewModel fieldsViewModel,
      final boolean readonly, final boolean traversable) {
    super(id);
    this.readonly = readonly;
    
    ListView<FieldInstanceModel> listView = new ListView<FieldInstanceModel>("fields", fieldInstanceModels) {
      public void populateItem(final ListItem<FieldInstanceModel> item) {
        FieldInstanceModel fieldInstanceModel = item.getModelObject();
        item.setRenderBodyOnly(true);
        Component component = createFieldInstanceComponent(fieldInstanceModel, fieldsViewModel, traversable);
        component.setRenderBodyOnly(true);
        item.add(component);
      }
    };
    listView.setReuseItems(true);
    add(listView);    
  }
  
  protected Component createFieldInstanceComponent(FieldInstanceModel fieldInstanceModel, FieldsViewModel fieldsViewModel, boolean _traversable) {
    FieldInstance fieldInstance = fieldInstanceModel.getFieldInstance();
    FieldAssignment fieldAssignment = fieldInstance.getFieldAssignment();
    FieldDefinition fieldDefinition = fieldAssignment.getFieldDefinition();
    if (fieldsViewModel == null) throw new RuntimeException("Fields view not specified.");
    
    FieldsView fieldsView = fieldsViewModel.getFieldsView();
    
    // change from parent view to child view, if specified
    //FieldsView valueView = ofieldModel.getRoleField().getValueView(fieldsViewModel.getFieldsView());
    fieldsView = fieldDefinition.getValueView(fieldsView);
    fieldsViewModel = new FieldsViewModel(fieldsView);
    
    // given the current view see if it is readonly and/or embedded
    final boolean rofield = (readonly  || fieldDefinition.isReadOnly(fieldsView));
    final boolean embedded = fieldDefinition.isEmbedded(fieldsView);
    
    // add field to panel
    switch (fieldDefinition.getFieldType()) {
    case FieldDefinition.FIELD_TYPE_ROLE: {
      RoleField roleField = (RoleField)fieldDefinition;
      int arity = roleField.getAssociationField().getArity();
      // unary
      if (arity == 1) {
        return new FieldInstanceAssociationUnaryPanel("field", fieldInstanceModel, rofield).setOutputMarkupId(true);        
      }
      // binary
      else if (arity == 2) {
        final boolean traversable = (_traversable ? !fieldDefinition.isNotTraversable(fieldsView) : false);

        if (embedded)
          return new FieldInstanceAssociationBinaryEmbeddedPanel("field", fieldInstanceModel, fieldsViewModel, rofield, traversable).setOutputMarkupId(true);
        else
          return new FieldInstanceAssociationBinaryPanel("field", fieldInstanceModel, fieldsViewModel, rofield, traversable).setOutputMarkupId(true);
      } 
      // n-ary
      else {
        final boolean traversable = (_traversable ? !fieldDefinition.isNotTraversable(fieldsView) : false);
        return new FieldInstanceAssociationNaryPanel("field", fieldInstanceModel, fieldsViewModel, rofield, traversable, arity).setOutputMarkupId(true);        
      }
    }
    case FieldDefinition.FIELD_TYPE_IDENTITY: {
      return new FieldInstanceIdentityPanel("field", fieldInstanceModel, rofield);
    }
    case FieldDefinition.FIELD_TYPE_NAME: {
      return new FieldInstanceNamePanel("field", fieldInstanceModel, rofield);
    }
    case FieldDefinition.FIELD_TYPE_OCCURRENCE: {
      return new FieldInstanceOccurrencePanel("field", fieldInstanceModel, rofield);
    }
    default:
      throw new OntopiaRuntimeException("Unknown field definition: " + fieldDefinition.getFieldType());
    }
    
  }
}
