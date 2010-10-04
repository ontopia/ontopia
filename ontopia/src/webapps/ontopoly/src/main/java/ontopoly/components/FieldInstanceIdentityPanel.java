package ontopoly.components;

import net.ontopia.infoset.core.LocatorIF;
import ontopoly.model.CardinalityIF;
import ontopoly.model.FieldAssignmentIF;
import ontopoly.model.FieldDefinitionIF;
import ontopoly.model.FieldInstanceIF;
import ontopoly.model.IdentityFieldIF;
import ontopoly.models.FieldDefinitionModel;
import ontopoly.models.FieldInstanceModel;
import ontopoly.models.FieldValueModel;
import ontopoly.models.FieldValuesModel;
import ontopoly.pages.AbstractOntopolyPage;
import ontopoly.utils.IdentityComparator;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;

public class FieldInstanceIdentityPanel extends AbstractFieldInstancePanel {

  public FieldInstanceIdentityPanel(String id, final FieldInstanceModel fieldInstanceModel, 
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
    this.fieldValuesModel = new FieldValuesModel(fieldInstanceModel, IdentityComparator.INSTANCE);
    
    this.listView = new ListView<FieldValueModel>("fieldValues", fieldValuesModel) {
      @Override
      protected void onBeforeRender() {
        validateCardinality();        
        super.onBeforeRender();
      }
      public void populateItem(final ListItem<FieldValueModel> item) {
        final FieldValueModel fieldValueModel = item.getModelObject();

        // TODO: make sure non-existing value field gets focus if last
        // edit happened there

        WebMarkupContainer fieldValueButtons = new WebMarkupContainer("fieldValueButtons");
        fieldValueButtons.setOutputMarkupId(true);
        item.add(fieldValueButtons);

        final FieldInstanceURIField identityField;
        if (readonly) {
          item.add(new Label("fieldValue", new LoadableDetachableModel() {
            @Override
            protected Object load() {
              LocatorIF identity = (LocatorIF)fieldValueModel.getObject();
              return (identity == null ? null : identity.getAddress());      
            }           
          }));
          identityField = null;
        } else {
          IdentityFieldIF ifield = (IdentityFieldIF)fieldInstanceModel.getFieldInstance().getFieldAssignment().getFieldDefinition();

          identityField = new FieldInstanceURIField("fieldValue", fieldValueModel) {
            @Override
            public boolean isEnabled() {
              if (readonly) return false;
              // make sure that internal psis are always protected
              return !isProtectedIdentity(oldValue);
            }		      
            };
          identityField.setCols(ifield.getWidth()); // NOTE: width not yet supported
          identityField.setOutputMarkupId(true);
          identityField.getTextField().add(new FieldUpdatingBehaviour(true));
          item.add(identityField);
        }
        
        // remove button
        FieldInstanceRemoveButton removeButton = 
          new FieldInstanceRemoveButton("remove", "remove-value.gif", fieldValueModel) { 
          @Override
          public boolean isVisible() {
            if (identityField == null || ! identityField.isEnabled()) return false;
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
        listView.removeAll();
        updateDependentComponents(target);
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
    fieldInstanceButtons.add(addButton);
    
    CardinalityIF cardinality = fieldAssignment.getCardinality();
    if (cardinality.isMaxOne())
      addButton.setVisible(false);
	}

  protected boolean isProtectedIdentity(String identity) {
    if (identity != null && identity.startsWith("http://psi.ontopia.net/ontology/")) {
      // identity is protected unless we're in admin mode
      AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
      return !page.isAdministrationEnabled();
    } else {
      return false;
    }
  }
  
}
