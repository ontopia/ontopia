package ontopoly.components;

import ontopoly.model.FieldAssignmentIF;
import ontopoly.model.FieldInstanceIF;
import ontopoly.model.RoleFieldIF;
import ontopoly.models.FieldDefinitionModel;
import ontopoly.models.FieldInstanceModel;
import ontopoly.models.FieldValueModel;
import ontopoly.models.FieldValuesModel;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

public class FieldInstanceAssociationUnaryPanel extends AbstractFieldInstancePanel {
  
  public FieldInstanceAssociationUnaryPanel(String id, final FieldInstanceModel fieldInstanceModel, 
                                            final boolean readonly) {
    super(id, fieldInstanceModel);

    FieldInstanceIF fieldInstance = fieldInstanceModel.getFieldInstance();
    FieldAssignmentIF fieldAssignment = fieldInstance.getFieldAssignment();
    RoleFieldIF fieldDefinition = (RoleFieldIF)fieldAssignment.getFieldDefinition(); 
		
    add(new FieldDefinitionLabel("fieldLabel", new FieldDefinitionModel(fieldDefinition)));
    
    // set up container
    this.fieldValuesContainer = new WebMarkupContainer("fieldValuesContainer");
    fieldValuesContainer.setOutputMarkupId(true);    
    add(fieldValuesContainer);

    // add feedback panel
    this.feedbackPanel = new FeedbackPanel("feedback", new AbstractFieldInstancePanelFeedbackMessageFilter());
    feedbackPanel.setOutputMarkupId(true);
    fieldValuesContainer.add(feedbackPanel);
    
    // add field values component(s)
    this.fieldValuesModel = new FieldValuesModel(fieldInstanceModel); // NOTE: no need to do any sorting here
    this.listView = new ListView<FieldValueModel>("fieldValues", fieldValuesModel) {
      // NOTE: no need to validate cardinality here
      public void populateItem(final ListItem<FieldValueModel> item) {
        FieldValueModel fieldValueModel = item.getModelObject();

        // TODO: make sure non-existing value field gets focus if last
        // edit happened there
          
        // unary
        FieldInstanceAssociationUnaryField unaryField = new FieldInstanceAssociationUnaryField("fieldValue", fieldValueModel, readonly);
        unaryField.getCheckBox().add(new FieldUpdatingBehaviour(false));
        item.add(unaryField);
        
        addNewFieldValueCssClass(item, fieldValuesModel, fieldValueModel);
      }
    };
    listView.setReuseItems(true);	  
    fieldValuesContainer.add(listView);	  
  }

}
