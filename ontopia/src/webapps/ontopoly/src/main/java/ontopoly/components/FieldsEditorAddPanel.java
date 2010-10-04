package ontopoly.components;

import ontopoly.model.FieldDefinition;
import ontopoly.model.Topic;
import ontopoly.models.FieldDefinitionModel;
import ontopoly.models.TopicTypeModel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;

public abstract class FieldsEditorAddPanel extends Panel {

  public FieldsEditorAddPanel(String id, final TopicTypeModel topicTypeModel, final FieldDefinitionModel fieldDefinitionModel) {
    super(id);
    
    FieldDefinition fieldDefinition = fieldDefinitionModel.getFieldDefinition(); 

    WebMarkupContainer container = new WebMarkupContainer("field", fieldDefinitionModel);
    add(container);

    container.add(new FieldDefinitionLabel("fieldLabel", fieldDefinitionModel) {
      @Override
      protected boolean isOntologyTypeLinkEnabled(Topic topic) {
        return true;
      }      
    });

    container.add(FieldsEditorExistingPanel.getFieldType("valueType", fieldDefinition));

    OntopolyImageLink button = new OntopolyImageLink("button", "add-left.gif", new ResourceModel("icon.add-left.assign-field")) {
      @Override
      public void onClick(AjaxRequestTarget target) {
        onAddField(topicTypeModel, fieldDefinitionModel, target);
      }
    };
    container.add(button);
     
  }

  protected abstract void onAddField(TopicTypeModel topicTypeModel, FieldDefinitionModel fdm, AjaxRequestTarget target);            

}
