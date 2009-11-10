package ontopoly.components;

import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.FieldAssignment;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.FieldInstance;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.RoleField;
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

		FieldInstance fieldInstance = fieldInstanceModel.getFieldInstance();
		FieldAssignment fieldAssignment = fieldInstance.getFieldAssignment();
		RoleField fieldDefinition = (RoleField)fieldAssignment.getFieldDefinition(); 
		
    //! add(new Label("fieldLabel", new Model(fieldDefinition.getFieldName())));
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
//      @Override
//      protected void onBeforeRender() {
//        validateCardinality(FieldInstanceAssociationUnaryPanel.this);        
//        super.onBeforeRender();
//      }
		  public void populateItem(final ListItem<FieldValueModel> item) {
		    FieldValueModel fieldValueModel = item.getModelObject();

        // TODO: make sure non-existing value field gets focus if last edit happened there
          
        // unary
        FieldInstanceAssociationUnaryField unaryField = new FieldInstanceAssociationUnaryField("fieldValue", fieldValueModel, readonly);
        unaryField.getCheckBox().add(new FieldUpdatingBehaviour(false));
        item.add(unaryField);                    
	    }
		};
	  listView.setReuseItems(true);	  
	  fieldValuesContainer.add(listView);	  
	}

}
