package ontopoly.components;

import java.util.Comparator;
import java.util.List;

import net.ontopia.utils.ObjectUtils;
import ontopoly.model.FieldAssignmentIF;
import ontopoly.model.FieldInstanceIF;
import ontopoly.model.RoleFieldIF;
import ontopoly.model.OntopolyTopicIF;
import ontopoly.models.FieldDefinitionModel;
import ontopoly.models.FieldInstanceModel;
import ontopoly.models.FieldValueModel;
import ontopoly.models.FieldValuesModel;
import ontopoly.models.FieldsViewModel;
import ontopoly.models.RoleFieldModel;
import ontopoly.models.TopicModel;
import ontopoly.pages.AbstractOntopolyPage;
import ontopoly.utils.RoleFieldsValueComparator;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

public class FieldInstanceAssociationNaryPanel extends AbstractFieldInstancePanel {
  
  public FieldInstanceAssociationNaryPanel(String id, 
    final FieldInstanceModel fieldInstanceModel, final FieldsViewModel fieldsViewModel, 
    final boolean readonly, final boolean traversable, final int arity) {
    super(id, fieldInstanceModel);

    FieldInstanceIF fieldInstance = fieldInstanceModel.getFieldInstance();
    FieldAssignmentIF fieldAssignment = fieldInstance.getFieldAssignment();
    RoleFieldIF roleField = (RoleFieldIF)fieldAssignment.getFieldDefinition(); 

    final RoleFieldModel roleFieldModel = new RoleFieldModel(roleField);
    final List otherRoleFieldModels = RoleFieldModel.wrapInRoleFieldModels(roleField.getFieldsForOtherRoles());
    
    final boolean allowRemove = !roleField.getEditMode().isNoEdit();
	  
    add(new FieldDefinitionLabel("fieldLabel", new FieldDefinitionModel(roleField)));
    
    // set up container
    this.fieldValuesContainer = new WebMarkupContainer("fieldValuesContainer");
    fieldValuesContainer.setOutputMarkupId(true);    
    add(fieldValuesContainer);

    // add feedback panel
    this.feedbackPanel = new FeedbackPanel("feedback", new AbstractFieldInstancePanelFeedbackMessageFilter());
    feedbackPanel.setOutputMarkupId(true);
    fieldValuesContainer.add(feedbackPanel);

    // add field values component(s)
    Comparator<Object> comparator = new RoleFieldsValueComparator(new TopicModel<OntopolyTopicIF>(fieldInstance.getInstance()), otherRoleFieldModels);
    this.fieldValuesModel = new FieldValuesModel(fieldInstanceModel, comparator);
    
    this.listView = new ListView<FieldValueModel>("fieldValues", fieldValuesModel) {
      @Override
      protected void onBeforeRender() {
        validateCardinality();        
        super.onBeforeRender();
      }
      public void populateItem(final ListItem<FieldValueModel> item) {
        FieldValueModel fieldValueModel = item.getModelObject();

        // TODO: make sure non-existing value field gets focus if last
        // edit happened there
        
        final WebMarkupContainer fieldValueButtons = new WebMarkupContainer("fieldValueButtons");
        fieldValueButtons.setOutputMarkupId(true);
        item.add(fieldValueButtons);
        
        FieldInstanceRemoveButton removeButton = 
          new FieldInstanceRemoveButton("remove", "remove-value.gif", fieldValueModel) { 
            @Override
            public boolean isVisible() {
              boolean visible = !readonly && fieldValueModel.isExistingValue() && allowRemove;
              if (visible) {
                
                // filter by player
                AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
                RoleFieldIF.ValueIF value = (RoleFieldIF.ValueIF)fieldValueModel.getObject();
                OntopolyTopicIF[] players = value.getPlayers();
                for (int i=0; i < players.length; i++) {
                  if (!page.filterTopic(players[i])) return false;
                }
              }
              return visible;
            }
            @Override
            public void onClick(AjaxRequestTarget target) {
              FieldInstanceIF fieldInstance = fieldValueModel.getFieldInstanceModel().getFieldInstance();
              Object value = fieldValueModel.getObject();

              OntopolyTopicIF currentTopic = fieldInstance.getInstance();
              RoleFieldIF currentField = (RoleFieldIF)fieldInstance.getFieldAssignment().getFieldDefinition();          

              RoleFieldIF.ValueIF valueIf = (RoleFieldIF.ValueIF)value;
              RoleFieldIF[] fields = valueIf.getRoleFields();
              // check with page to see if add is allowed
              boolean removeAllowed = true;
              AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
              for (int i=0;  i < fields.length; i++) {
                RoleFieldIF selectedField = fields[i];
                if (ObjectUtils.different(currentField, selectedField)) {
                  OntopolyTopicIF selectedTopic = valueIf.getPlayer(selectedField, fieldInstance.getInstance());                
                  if (!page.isRemoveAllowed(currentTopic, currentField, selectedTopic, selectedField)) {
                    removeAllowed = false;
                  }
                }
              }
              // perform removal
              if (removeAllowed) {
                fieldInstance.removeValue(value, page.getListener());              
                listView.removeAll();
                updateDependentComponents(target);
              }
            }
          };
        fieldValueButtons.add(removeButton);
        
        // n-ary
        FieldInstanceAssociationNaryField naryField = new FieldInstanceAssociationNaryField("fieldValue", FieldInstanceAssociationNaryPanel.this, 
            roleFieldModel, otherRoleFieldModels, fieldValueModel, fieldsViewModel, readonly, traversable, arity);
        item.add(naryField); 
        
        addNewFieldValueCssClass(item, fieldValuesModel, fieldValueModel);
      }
    };
    listView.setReuseItems(true);	  
    fieldValuesContainer.add(listView);

    // figure out which buttons to show
    this.fieldInstanceButtons = new WebMarkupContainer("fieldInstanceButtons");
    fieldInstanceButtons.setOutputMarkupId(true);
    add(fieldInstanceButtons);	  

    // "add" button
    OntopolyImageLink addButton = new OntopolyImageLink("add", "add.gif") { 
      @Override
      public void onClick(AjaxRequestTarget target) {
        boolean showExtraField = !fieldValuesModel.getShowExtraField();
        fieldValuesModel.setShowExtraField(showExtraField, true);
        listView.removeAll();
        updateDependentComponents(target);
      }
      @Override
      public boolean isVisible() {
        if (readonly) return false;
        return fieldValuesModel.containsExisting();
      }
      @Override public String getImage() {
        return fieldValuesModel.getShowExtraField() ? "remove.gif" : "add.gif";
      }
      @Override public IModel getTitleModel() {
        return new ResourceModel(fieldValuesModel.getShowExtraField() ? "icon.remove.hide-field" : "icon.add.add-value");
      }
    };
    fieldInstanceButtons.add(addButton);    
  }
 
}
