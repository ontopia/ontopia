package ontopoly.components;

import ontopoly.model.FieldAssignment;
import ontopoly.model.FieldDefinition;
import ontopoly.model.FieldInstance;
import ontopoly.models.FieldDefinitionModel;
import ontopoly.models.FieldInstanceModel;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

public class FieldInstanceErrorPanel extends AbstractFieldInstancePanel {

  public FieldInstanceErrorPanel(String id, final FieldInstanceModel fieldInstanceModel, Exception e) {
    super(id, fieldInstanceModel);

    FieldInstance fieldInstance = fieldInstanceModel.getFieldInstance();
    FieldAssignment fieldAssignment = fieldInstance.getFieldAssignment();
    FieldDefinition fieldDefinition = fieldAssignment.getFieldDefinition(); 

    add(new FieldDefinitionLabel("fieldLabel", new FieldDefinitionModel(fieldDefinition)));

    error(AbstractFieldInstancePanel.createErrorMessage(fieldInstanceModel, e));

    // set up container
    this.fieldValuesContainer = new WebMarkupContainer("fieldValuesContainer");
    fieldValuesContainer.setOutputMarkupId(true);    
    add(fieldValuesContainer);

    // add feedback panel
    this.feedbackPanel = new FeedbackPanel("feedback", new AbstractFieldInstancePanelFeedbackMessageFilter());
    feedbackPanel.setOutputMarkupId(true);
    fieldValuesContainer.add(feedbackPanel);
  }
  
}
