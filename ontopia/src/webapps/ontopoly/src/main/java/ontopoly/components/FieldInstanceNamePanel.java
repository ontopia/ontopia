package ontopoly.components;

import net.ontopia.topicmaps.core.TopicNameIF;
import ontopoly.model.CardinalityIF;
import ontopoly.model.FieldAssignmentIF;
import ontopoly.model.FieldDefinitionIF;
import ontopoly.model.FieldInstanceIF;
import ontopoly.model.NameFieldIF;
import ontopoly.models.FieldDefinitionModel;
import ontopoly.models.FieldInstanceModel;
import ontopoly.models.FieldValueModel;
import ontopoly.models.FieldValuesModel;
import ontopoly.utils.NameComparator;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;

public class FieldInstanceNamePanel extends AbstractFieldInstancePanel {

  public FieldInstanceNamePanel(String id, final FieldInstanceModel fieldInstanceModel, 
                                final boolean readonly) {
    super(id, fieldInstanceModel);

    FieldInstanceIF fieldInstance = fieldInstanceModel.getFieldInstance();
    FieldAssignmentIF fieldAssignment = fieldInstance.getFieldAssignment();
    FieldDefinitionIF fieldDefinition = fieldAssignment.getFieldDefinition(); 
    
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
    this.fieldValuesModel = new FieldValuesModel(fieldInstanceModel, NameComparator.INSTANCE);
    
    this.listView = new ListView<FieldValueModel>("fieldValues", fieldValuesModel) {
      @Override
      protected void onBeforeRender() {
        validateCardinality();		    
        super.onBeforeRender();
      }
      public void populateItem(final ListItem item) {
        final FieldValueModel fieldValueModel = (FieldValueModel)item.getModelObject();

        // TODO: make sure non-existing value field gets focus if last
        // edit happened there
        
        final WebMarkupContainer fieldValueButtons = new WebMarkupContainer("fieldValueButtons");
        fieldValueButtons.setOutputMarkupId(true);
        item.add(fieldValueButtons);

        // remove button
        FieldInstanceRemoveButton removeButton = 
          new FieldInstanceRemoveButton("remove", "remove-value.gif", fieldValueModel) { 
            @Override
            public boolean isVisible() {
              CardinalityIF cardinality = fieldValuesModel.getFieldInstanceModel().getFieldInstance().getFieldAssignment().getCardinality();
              if (fieldValuesModel.size() == 1 && cardinality.isMinOne())
                return false;
              else
                return !readonly && fieldValueModel.isExistingValue();
            }
            @Override
            public void onClick(AjaxRequestTarget target) {
              super.onClick(target);
              listView.removeAll();
              updateDependentComponents(target);
            }
          };
        fieldValueButtons.add(removeButton);  

        if (readonly) {
          item.add(new Label("fieldValue", new LoadableDetachableModel() {
            @Override
            protected Object load() {
              TopicNameIF tn = (TopicNameIF)fieldValueModel.getObject();
              return (tn == null ? null : tn.getValue());              
            }
            
          }));
        } else {
          NameFieldIF nf = (NameFieldIF)fieldInstanceModel.getFieldInstance().getFieldAssignment().getFieldDefinition();
          final FieldUpdatingBehaviour fuBehaviour = new FieldUpdatingBehaviour(true);

          int height = nf.getHeight(); 
          if (height > 1) {
            FieldInstanceTextArea nameField = new FieldInstanceTextArea("fieldValue", fieldValueModel);
            nameField.setCols(nf.getWidth());
            nameField.setRows(height);
            nameField.add(fuBehaviour);
            item.add(nameField);
            
          } else {
            FieldInstanceTextField nameField = new FieldInstanceTextField("fieldValue", fieldValueModel);
            nameField.setCols(nf.getWidth());
            nameField.add(fuBehaviour);
            item.add(nameField);           
          }
        }
        
        addNewFieldValueCssClass(item, fieldValuesModel, fieldValueModel);
      }
    };
    listView.setReuseItems(true);	  
    fieldValuesContainer.add(listView);

    this.fieldInstanceButtons = new WebMarkupContainer("fieldInstanceButtons");
    fieldInstanceButtons.setOutputMarkupId(true);
    add(fieldInstanceButtons);
    
    OntopolyImageLink addButton = new OntopolyImageLink("add", "add.gif") { 
      @Override
      public void onClick(AjaxRequestTarget target) {
        boolean showExtraField = !fieldValuesModel.getShowExtraField();
        fieldValuesModel.setShowExtraField(showExtraField, true);
        updateDependentComponents(target);
        listView.removeAll();
      }
      @Override
      public boolean isVisible() {
        if (readonly) return false;
        CardinalityIF cardinality = fieldValuesModel.getFieldInstanceModel().getFieldInstance().getFieldAssignment().getCardinality();
        return !cardinality.isMaxOne() && fieldValuesModel.containsExisting();
      }      
      @Override public String getImage() {
        return fieldValuesModel.getShowExtraField() ? "remove.gif" : "add.gif";
      }
      @Override public IModel getTitleModel() {
        return new ResourceModel(fieldValuesModel.getShowExtraField() ? "icon.remove.hide-field" : "icon.add.add-value");
      }      
    };  
    addButton.setOutputMarkupId(true);
    fieldInstanceButtons.add(addButton);
    
    CardinalityIF cardinality = fieldAssignment.getCardinality();
    if (cardinality.isMaxOne())
      addButton.setVisible(false);
  }
 
}
