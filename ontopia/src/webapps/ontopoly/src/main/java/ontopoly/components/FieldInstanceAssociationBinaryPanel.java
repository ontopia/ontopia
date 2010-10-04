package ontopoly.components;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

import net.ontopia.utils.ObjectUtils;
import ontopoly.LockManager;
import ontopoly.OntopolySession;
import ontopoly.jquery.DraggableBehavior;
import ontopoly.jquery.DroppableBehavior;
import ontopoly.model.CreateActionIF;
import ontopoly.model.EditModeIF;
import ontopoly.model.FieldAssignmentIF;
import ontopoly.model.FieldInstanceIF;
import ontopoly.model.InterfaceControlIF;
import ontopoly.model.RoleFieldIF;
import ontopoly.model.OntopolyTopicIF;
import ontopoly.model.OntopolyTopicMapIF;
import ontopoly.models.FieldDefinitionModel;
import ontopoly.models.FieldInstanceModel;
import ontopoly.models.FieldValueModel;
import ontopoly.models.FieldValuesModel;
import ontopoly.models.FieldsViewModel;
import ontopoly.models.RoleFieldModel;
import ontopoly.models.TopicModel;
import ontopoly.pages.AbstractOntopolyPage;
import ontopoly.pages.ModalFindPage;
import ontopoly.utils.RoleFieldValueComparator;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.PageParameters;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

public class FieldInstanceAssociationBinaryPanel extends AbstractFieldInstancePanel {

  protected final ConfirmDeletePanel confirmDeletePanel;
  protected final RoleFieldModel ofieldModel;
  protected final TopicModel<OntopolyTopicIF> topicModel;
  protected final RoleFieldModel roleFieldModel;

  public FieldInstanceAssociationBinaryPanel(String id, 
      final FieldInstanceModel fieldInstanceModel, final FieldsViewModel fieldsViewModel, 
      final boolean readonlyField, final boolean traversable) {
    this(id, fieldInstanceModel, fieldsViewModel, readonlyField, false, traversable);
  }

  protected FieldInstanceAssociationBinaryPanel(String id, 
      final FieldInstanceModel fieldInstanceModel, final FieldsViewModel fieldsViewModel, 
      final boolean readonlyField, final boolean embedded, final boolean traversable) {
    super(id, fieldInstanceModel);

    FieldInstanceIF fieldInstance = fieldInstanceModel.getFieldInstance();
    FieldAssignmentIF fieldAssignment = fieldInstance.getFieldAssignment();
    RoleFieldIF roleField = (RoleFieldIF)fieldAssignment.getFieldDefinition(); 
    this.roleFieldModel = new RoleFieldModel(roleField);

    add(new FieldDefinitionLabel("fieldLabel", new FieldDefinitionModel(roleField)));

    // set up container
    this.fieldValuesContainer = new WebMarkupContainer("fieldValuesContainer");
    fieldValuesContainer.setOutputMarkupId(true);    
    add(fieldValuesContainer);

    // add feedback panel
    this.feedbackPanel = new FeedbackPanel("feedback", new AbstractFieldInstancePanelFeedbackMessageFilter());
    feedbackPanel.setOutputMarkupId(true);
    fieldValuesContainer.add(feedbackPanel);

    this.confirmDeletePanel = new ConfirmDeletePanel("confirm", fieldValuesContainer) {
      @Override
      protected void onDeleteTopic(AjaxRequestTarget target) {
        super.onDeleteTopic(target);
        FieldInstanceAssociationBinaryPanel.this.onUpdate(target);
      }      
    };
    confirmDeletePanel.setOutputMarkupId(true);
    fieldValuesContainer.add(confirmDeletePanel);

    RoleFieldIF ofield = (RoleFieldIF)roleField.getFieldsForOtherRoles().iterator().next();
    this.ofieldModel = new RoleFieldModel(ofield);
    this.topicModel = new TopicModel<OntopolyTopicIF>(fieldInstance.getInstance());

    InterfaceControlIF interfaceControl = ofield.getInterfaceControl();

    WebMarkupContainer fieldValuesList = new WebMarkupContainer("fieldValuesList");
    fieldValuesContainer.add(fieldValuesList);

    final String fieldDefinitionId = roleField.getId();

    EditModeIF editMode = roleField.getEditMode();

    final boolean ownedvalues = editMode.isOwnedValues();
    final boolean allowAdd = !(ownedvalues || editMode.isNewValuesOnly() || editMode.isNoEdit());
    final boolean allowCreate = !(editMode.isExistingValuesOnly() || editMode.isNoEdit());
    final boolean allowRemove = !editMode.isNoEdit();    
    final boolean sortable = roleField.isSortable(); 

    // add field values component(s)
    // TODO: consider moving ordering logic into object model
    if (sortable) {
      // HACK: retrieving values ourselves so that we can get them ordered
      this.fieldValuesModel = new FieldValuesModel(fieldInstanceModel) {
        @Override
        protected Collection getValues(FieldInstanceIF fieldInstance) {
          OntopolyTopicIF instance = fieldInstance.getInstance();
          RoleFieldIF roleField = (RoleFieldIF)fieldInstance.getFieldAssignment().getFieldDefinition();
          return roleField.getOrderedValues(instance, ofieldModel.getRoleField());
        }
      };
    } else {
      Comparator<Object> comparator = new RoleFieldValueComparator(topicModel, ofieldModel);
      this.fieldValuesModel = new FieldValuesModel(fieldInstanceModel, comparator);
    }

    this.listView = new ListView<FieldValueModel>("fieldValues", fieldValuesModel) {
      @Override
      protected void onBeforeRender() {
        validateCardinality();        
        super.onBeforeRender();
      }
      public void populateItem(final ListItem<FieldValueModel> item) {        
        FieldValueModel fieldValueModel = item.getModelObject();

        // get topic
        OntopolyTopicIF oplayer = null;
        if (fieldValueModel.isExistingValue()) {
          RoleFieldIF.ValueIF valueIf = (RoleFieldIF.ValueIF)fieldValueModel.getObject();              
          RoleFieldIF ofield = ofieldModel.getRoleField();
          oplayer = valueIf.getPlayer(ofield, fieldInstanceModel.getFieldInstance().getInstance());
        }
        final String topicMapId = (oplayer == null ? null : oplayer.getTopicMap().getId());
        final String topicId = (oplayer == null ? null : oplayer.getId());

        // acquire lock for embedded topic
        final boolean isLockedByOther;
        if (embedded && fieldValueModel.isExistingValue()) {
          OntopolySession session = (OntopolySession)Session.get();
          String lockerId = session.getLockerId(getRequest());
          LockManager.Lock lock = session.lock(oplayer, lockerId);
          isLockedByOther = !lock.ownedBy(lockerId);
        } else {
          isLockedByOther = false;
        }
        final boolean readonly = readonlyField || isLockedByOther;

        boolean itemSortable = !readonly && sortable && fieldValueModel.isExistingValue(); 
        if (itemSortable) {
          item.add(new DroppableBehavior(fieldDefinitionId) {
            @Override
            protected MarkupContainer getDropContainer() {
              return listView;
            }
            @Override
            protected void onDrop(Component component, AjaxRequestTarget target) {
              FieldValueModel fvm_dg = (FieldValueModel)component.getDefaultModelObject();
              FieldValueModel fvm_do = (FieldValueModel)getComponent().getDefaultModelObject();
              RoleFieldIF.ValueIF rfv_dg = (RoleFieldIF.ValueIF)fvm_dg.getFieldValue();
              RoleFieldIF.ValueIF rfv_do = (RoleFieldIF.ValueIF)fvm_do.getFieldValue();

              OntopolyTopicIF topic = topicModel.getTopic();
              RoleFieldIF rfield = roleFieldModel.getRoleField();
              RoleFieldIF ofield = ofieldModel.getRoleField();
              rfield.moveAfter(topic, ofield, rfv_dg, rfv_do);
              getModel().detach(); // FIXME: better if we could just tweak model directly without detaching
              listView.removeAll();
              target.addComponent(fieldValuesContainer);
            }
          });
          item.add(new DraggableBehavior(fieldDefinitionId));
        }

        item.setOutputMarkupId(true);
        WebMarkupContainer fieldIconContainer = new WebMarkupContainer("fieldIconContainer");
        fieldIconContainer.add(new OntopolyImage("fieldIcon", "dnd.gif", new ResourceModel("icon.dnd.reorder")));
        fieldIconContainer.setVisible(itemSortable);
        item.add(fieldIconContainer);

        final WebMarkupContainer fieldValueButtons = new WebMarkupContainer("fieldValueButtons");
        fieldValueButtons.setOutputMarkupId(true);
        item.add(fieldValueButtons);

        FieldInstanceRemoveButton removeButton = 
          new FieldInstanceRemoveButton("remove", "remove-value.gif", fieldValueModel) { 
          @Override
          public boolean isVisible() {
            boolean visible = !readonly && fieldValueModel.isExistingValue() && allowRemove; // && !isValueProtected;
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
            // FIXME: could reuse some of these variable from above
            FieldInstanceIF fieldInstance = fieldValueModel.getFieldInstanceModel().getFieldInstance();
            Object value = fieldValueModel.getObject();

            OntopolyTopicIF currentTopic = fieldInstance.getInstance();

            RoleFieldIF currentField = (RoleFieldIF)fieldInstance.getFieldAssignment().getFieldDefinition();          
            RoleFieldIF selectedField = ofieldModel.getRoleField();

            RoleFieldIF.ValueIF valueIf = (RoleFieldIF.ValueIF)value;                
            OntopolyTopicIF selectedTopic = valueIf.getPlayer(selectedField, fieldInstance.getInstance());

            // check with page to see if add is allowed
            boolean changesMade = false;
            AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
            if (page.isRemoveAllowed(currentTopic, currentField, selectedTopic, selectedField)) {
              if (ownedvalues) {

                // don't remove system topics
                if (!selectedTopic.isSystemTopic()) {
                  FieldInstanceAssociationBinaryPanel.this.confirmDeletePanel.setTopic(selectedTopic);
                  changesMade = true;
                }
              } else {
                fieldInstance.removeValue(value, page.getListener());        
                changesMade = true;
              }
            }
            // notify association panel so that it can update itself
            if (changesMade)
              FieldInstanceAssociationBinaryPanel.this.onUpdate(target);
          }
        };
        fieldValueButtons.add(removeButton);

        // embedded goto button
        OntopolyImageLink gotoButton = new OntopolyImageLink("goto", "goto.gif", new ResourceModel("icon.goto.topic")) {
          @Override
          public boolean isVisible() {
            FieldValueModel fieldValueModel = item.getModelObject();
            return embedded && fieldValueModel.isExistingValue();  
          }
          @Override
          public void onClick(AjaxRequestTarget target) {
            // navigate to topic
            PageParameters pageParameters = new PageParameters();
            pageParameters.put("topicMapId", topicMapId);
            pageParameters.put("topicId", topicId);
            setResponsePage(getPage().getClass(), pageParameters);
            setRedirect(true);
          }
        };
        fieldValueButtons.add(gotoButton);

        // embedded lock button
        OntopolyImageLink lockButton = new OntopolyImageLink("lock", "lock.gif", new ResourceModel("icon.topic.locked")) {
          @Override
          public boolean isVisible() {
            return embedded && isLockedByOther;  
          }
          @Override
          public void onClick(AjaxRequestTarget target) {
          }
        };
        fieldValueButtons.add(lockButton);

        // binary
        // ISSUE: should not really pass in readonly-parameter here as
        // it is only relevant if page is readonly       
        FieldInstanceAssociationBinaryField binaryField = new FieldInstanceAssociationBinaryField("fieldValue", ofieldModel, fieldValueModel, fieldsViewModel, readonly, embedded, traversable, allowAdd) {
          @Override
          protected void performNewSelection(FieldValueModel fieldValueModel, RoleFieldIF selectedField, OntopolyTopicIF selectedTopic) {
            RoleFieldIF.ValueIF value = FieldInstanceAssociationBinaryPanel.this.performNewSelection(selectedField, selectedTopic);
            fieldValueModel.setExistingValue(value); 
          }          
        };
        if (binaryField.getUpdateableComponent() != null)
          binaryField.getUpdateableComponent().add(new FieldUpdatingBehaviour(true));
        item.add(binaryField); 

        addNewFieldValueCssClass(item, fieldValuesModel, fieldValueModel);
      }
    };
    listView.setReuseItems(true);
    fieldValuesList.add(listView);

    // figure out which buttons to show
    this.fieldInstanceButtons = new WebMarkupContainer("fieldInstanceButtons");
    fieldInstanceButtons.setOutputMarkupId(true);
    add(fieldInstanceButtons);

    if (readonlyField || !allowAdd) {
      // unused components
      fieldInstanceButtons.add(new Label("add", new Model<String>("unused")).setVisible(false));
      fieldInstanceButtons.add(new Label("find", new Model<String>("unused")).setVisible(false));
      fieldInstanceButtons.add(new Label("findModal", new Model<String>("unused")).setVisible(false));

      // add/find button
    } else if (interfaceControl.isDropDownList() || interfaceControl.isAutoComplete()) {
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
          if (readonlyField) 
            return false;
          else
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

      // unused components
      fieldInstanceButtons.add(new Label("find", new Model<String>("unused")).setVisible(false));
      fieldInstanceButtons.add(new Label("findModal", new Model<String>("unused")).setVisible(false));

    } else if (interfaceControl.isSearchDialog() || interfaceControl.isBrowseDialog()) {

      // "search"/"browse" button
      final ModalWindow findModal = new ModalWindow("findModal");
      fieldInstanceButtons.add(findModal);

      int activeTab = (interfaceControl.isSearchDialog() ?
          ModalFindPage.ACTIVE_TAB_SEARCH : ModalFindPage.ACTIVE_TAB_BROWSE);

      findModal.setContent(new ModalFindPage(findModal.getContentId(), fieldInstanceModel, activeTab) {
        @Override
        protected void onSelectionConfirmed(AjaxRequestTarget target, Collection selected) {
          FieldInstanceIF fieldInstance = fieldInstanceModel.getFieldInstance();
          RoleFieldIF currentField = (RoleFieldIF)fieldInstance.getFieldAssignment().getFieldDefinition();
          RoleFieldIF selectedField = (RoleFieldIF)currentField.getFieldsForOtherRoles().iterator().next();

          // check with page to see if add is allowed
          if (ObjectUtils.different(currentField, selectedField)) {

            OntopolyTopicIF currentTopic = fieldInstance.getInstance();
            OntopolyTopicMapIF topicMap = currentTopic.getTopicMap();

            boolean changesMade = false;
            Iterator iter = selected.iterator();
            while (iter.hasNext()) {
              String objectId = (String)iter.next();

              AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
              OntopolyTopicIF selectedTopic = topicMap.getTopicById(objectId);

              if (page.isAddAllowed(currentTopic, currentField, selectedTopic, selectedField)) {
                performNewSelection(selectedField, selectedTopic);
                changesMade = true;
              }
            }          
            // notify association panel so that it can update itself
            if (changesMade)
              FieldInstanceAssociationBinaryPanel.this.onUpdate(target);
          }
        }        
        @Override
        protected void onCloseCancel(AjaxRequestTarget target) {
          findModal.close(target);              
        }
        @Override
        protected void onCloseOk(AjaxRequestTarget target) {
          findModal.close(target);
        }              
      });
      findModal.setTitle(new ResourceModel("ModalWindow.title.find.topic").getObject().toString());
      findModal.setCookieName("findModal");

      OntopolyImageLink findButton = new OntopolyImageLink("find", "search.gif", new ResourceModel("find.topic")) { 
        @Override
        public void onClick(AjaxRequestTarget target) {
          findModal.show(target);
        }
      };  
      fieldInstanceButtons.add(findButton);
      // unused components
      fieldInstanceButtons.add(new Label("add", new Model<String>("unused")).setVisible(false));
    } else {
      throw new RuntimeException("Unsupported interface control: " + interfaceControl);
    }

    // create button
    if (readonlyField || !allowCreate) {
      fieldInstanceButtons.add(new Label("create").setVisible(false));
    } else {

      CreateActionIF ca = roleField.getCreateAction();
      int createAction;
      if (embedded || ca.isNone())
        createAction = FieldInstanceCreatePlayerPanel.CREATE_ACTION_NONE;
      else if (ca.isNavigate())
        createAction = FieldInstanceCreatePlayerPanel.CREATE_ACTION_NAVIGATE;
      else
        createAction = FieldInstanceCreatePlayerPanel.CREATE_ACTION_POPUP;

      FieldInstanceCreatePlayerPanel createPanel = new FieldInstanceCreatePlayerPanel("create", fieldInstanceModel, fieldsViewModel, new RoleFieldModel(ofield), this, createAction) {
        @Override
        protected void performNewSelection(RoleFieldModel ofieldModel, OntopolyTopicIF selectedTopic) {
          FieldInstanceAssociationBinaryPanel.this.performNewSelection(ofieldModel.getRoleField(), selectedTopic);           
        }          

      };
      createPanel.setOutputMarkupId(true);
      fieldInstanceButtons.add(createPanel);
    }
  }

  protected RoleFieldIF.ValueIF performNewSelection(RoleFieldIF selectedField,
                                                    OntopolyTopicIF selectedTopic) {
    FieldInstanceIF fieldInstance = fieldInstanceModel.getFieldInstance();
    RoleFieldIF currentField = (RoleFieldIF)fieldInstance.getFieldAssignment().getFieldDefinition();
    OntopolyTopicIF currentTopic = fieldInstance.getInstance();
    RoleFieldIF.ValueIF value = currentField.createValue(2);
    value.addPlayer(currentField, currentTopic);            
    value.addPlayer(selectedField, selectedTopic);
    AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
    fieldInstance.addValue(value, page.getListener());
    return value;
  }

  @Override
  public void onDetach() {
    ofieldModel.detach();
    topicModel.detach();
    roleFieldModel.detach();
    super.onDetach();
  }

}
