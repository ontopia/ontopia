package ontopoly.components;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

import net.ontopia.utils.ObjectUtils;
import ontopoly.LockManager;
import ontopoly.OntopolySession;
import ontopoly.jquery.DraggableBehavior;
import ontopoly.jquery.DroppableBehavior;
import ontopoly.model.CreateAction;
import ontopoly.model.EditMode;
import ontopoly.model.FieldAssignment;
import ontopoly.model.FieldInstance;
import ontopoly.model.InterfaceControl;
import ontopoly.model.RoleField;
import ontopoly.model.Topic;
import ontopoly.model.TopicMap;
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
  
  protected ConfirmDeletePanel confirmDeletePanel;
  
  public FieldInstanceAssociationBinaryPanel(String id, 
      final FieldInstanceModel fieldInstanceModel, final FieldsViewModel fieldsViewModel, 
      final boolean readonlyField, final boolean traversable) {
    this(id, fieldInstanceModel, fieldsViewModel, readonlyField, false, traversable);
  }
  
  protected FieldInstanceAssociationBinaryPanel(String id, 
	    final FieldInstanceModel fieldInstanceModel, final FieldsViewModel fieldsViewModel, 
	    final boolean readonlyField, final boolean embedded, final boolean traversable) {
    super(id, fieldInstanceModel);

  	FieldInstance fieldInstance = fieldInstanceModel.getFieldInstance();
  	FieldAssignment fieldAssignment = fieldInstance.getFieldAssignment();
  	RoleField roleField = (RoleField)fieldAssignment.getFieldDefinition(); 
  	final RoleFieldModel roleFieldModel = new RoleFieldModel(roleField);
  		
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
    
    RoleField ofield = (RoleField)roleField.getFieldsForOtherRoles().iterator().next();
    final RoleFieldModel ofieldModel = new RoleFieldModel(ofield);
    final TopicModel<Topic> topicModel = new TopicModel<Topic>(fieldInstance.getInstance());
    
    InterfaceControl interfaceControl = ofield.getInterfaceControl();

    WebMarkupContainer fieldValuesList = new WebMarkupContainer("fieldValuesList");
    fieldValuesContainer.add(fieldValuesList);
    
    final String fieldDefinitionId = roleField.getId();

    EditMode editMode = roleField.getEditMode();
    
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
        protected Collection getValues(FieldInstance fieldInstance) {
          Topic instance = fieldInstance.getInstance();
          RoleField roleField = (RoleField)fieldInstance.getFieldAssignment().getFieldDefinition();
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
		    final FieldValueModel fieldValueModel = item.getModelObject();

		    // get topic
		    Topic oplayer = null;
		    if (fieldValueModel.isExistingValue()) {
		      RoleField.ValueIF valueIf = (RoleField.ValueIF)fieldValueModel.getObject();              
		      RoleField ofield = ofieldModel.getRoleField();
		      oplayer = valueIf.getPlayer(ofield, fieldInstanceModel.getFieldInstance().getInstance());
		    }
        final TopicModel<Topic> oplayerModel = new TopicModel<Topic>(oplayer);

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
              RoleField.ValueIF rfv_dg = (RoleField.ValueIF)fvm_dg.getFieldValue();
              RoleField.ValueIF rfv_do = (RoleField.ValueIF)fvm_do.getFieldValue();
		          
//              System.out.println("DG: " + rfv_dg);            
//              System.out.println("DO: " + rfv_do);
              Topic topic = topicModel.getTopic();
              RoleField rfield = roleFieldModel.getRoleField();
              RoleField ofield = ofieldModel.getRoleField();
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
                RoleField.ValueIF value = (RoleField.ValueIF)fieldValueModel.getObject();
                Topic[] players = value.getPlayers();
                for (int i=0; i < players.length; i++) {
                  if (!page.filterTopic(players[i])) return false;
                }
                
//                // show remove button on 1:1 field unless just one value left
//                FieldInstance fi = fieldValueModel.getFieldInstanceModel().getFieldInstance();
//                Cardinality cardinality = fi.getFieldAssignment().getCardinality();
//                if (cardinality.isMinOne() && cardinality.isMaxOne() && 
//                    ((fieldValuesModel.size() == 1 && !fieldValuesModel.getShowExtraField()) ||
//                      (fieldValuesModel.size() == 2 && fieldValuesModel.getShowExtraField())))
//                  return false;
              }
              return visible;
            }
            @Override
            public void onClick(AjaxRequestTarget target) {
              // FIXME: could reuse some of these variable from above
              FieldInstance fieldInstance = fieldValueModel.getFieldInstanceModel().getFieldInstance();
              Object value = fieldValueModel.getObject();

              Topic currentTopic = fieldInstance.getInstance();
              
              RoleField currentField = (RoleField)fieldInstance.getFieldAssignment().getFieldDefinition();          
              RoleField selectedField = ofieldModel.getRoleField();

              RoleField.ValueIF valueIf = (RoleField.ValueIF)value;                
              Topic selectedTopic = valueIf.getPlayer(selectedField, fieldInstance.getInstance());

              // check with page to see if add is allowed
              boolean changesMade = false;
              AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
              if (page.isRemoveAllowed(currentTopic, currentField, selectedTopic, selectedField)) {
                if (ownedvalues) {
                  
                  // don't remove system topics
                  if (!selectedTopic.isSystemTopic()) {
//                    RoleField rfield = roleFieldModel.getRoleField();
//                    Topic rplayer = valueIf.getPlayer(rfield, fieldInstance.getInstance());
//                    System.out.println("Removing dependent object: " + rplayer + " -> " + selectedTopic);
                    //!Collection dependentObjects = selectedTopic.getDependentObjects();
                    //!if (!dependentObjects.isEmpty()) {
                      FieldInstanceAssociationBinaryPanel.this.confirmDeletePanel.setTopic(selectedTopic);
//                    } else {
//                      // remove object
//                      System.out.println("Removing selected: " + selectedTopic.getName());
//                      selectedTopic.remove(page);                      
//                    }
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
            return embedded && fieldValueModel.isExistingValue();  
          }
          @Override
          public void onClick(AjaxRequestTarget target) {
            // navigate to topic
            Topic oplayer = oplayerModel.getTopic();
            PageParameters pageParameters = new PageParameters();
            pageParameters.put("topicMapId", oplayer.getTopicMap().getId());
            pageParameters.put("topicId", oplayer.getId());
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
        // ISSUE: should not really pass in readonly-parameter here as it is only relevant if page is readonly
        FieldInstanceAssociationBinaryField binaryField = new FieldInstanceAssociationBinaryField("fieldValue", ofieldModel, fieldValueModel, fieldsViewModel, readonly, embedded, traversable, allowAdd) {
          @Override
          protected void performNewSelection(FieldValueModel fieldValueModel, RoleField selectedField, Topic selectedTopic) {
            RoleField.ValueIF value = FieldInstanceAssociationBinaryPanel.this.performNewSelection(selectedField, selectedTopic);
            fieldValueModel.setExistingValue(value); 
          }          
        };
        if (binaryField.getUpdateableComponent() != null)
          binaryField.getUpdateableComponent().add(new FieldUpdatingBehaviour(true));
        item.add(binaryField);           		      
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
          fieldValuesModel.setShowExtraField(showExtraField);
          listView.removeAll();
          updateDependentComponents(target);
        }
        @Override
        public boolean isVisible() {
          if (readonlyField) return false;
//          Cardinality cardinality = fieldInstanceModel.getFieldInstance().getFieldAssignment().getCardinality();
//          if (cardinality.isMaxOne())
//            return fieldValuesModel.size() <= 1;
//          else
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
          FieldInstance fieldInstance = fieldInstanceModel.getFieldInstance();
          RoleField currentField = (RoleField)fieldInstance.getFieldAssignment().getFieldDefinition();
          RoleField selectedField = (RoleField)currentField.getFieldsForOtherRoles().iterator().next();

          // check with page to see if add is allowed
          if (ObjectUtils.different(currentField, selectedField)) {

            Topic currentTopic = fieldInstance.getInstance();
            TopicMap topicMap = currentTopic.getTopicMap();

            boolean changesMade = false;
            Iterator iter = selected.iterator();
            while (iter.hasNext()) {
              String objectId = (String)iter.next();
              
              AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
              Topic selectedTopic = topicMap.getTopicById(objectId);
              
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
      
      CreateAction ca = roleField.getCreateAction();
      int createAction;
      if (embedded || ca.isNone())
        createAction = FieldInstanceCreatePlayerPanel.CREATE_ACTION_NONE;
      else if (ca.isNavigate())
        createAction = FieldInstanceCreatePlayerPanel.CREATE_ACTION_NAVIGATE;
      else
        createAction = FieldInstanceCreatePlayerPanel.CREATE_ACTION_POPUP;
      
      FieldInstanceCreatePlayerPanel createPanel = new FieldInstanceCreatePlayerPanel("create", fieldInstanceModel, fieldsViewModel, new RoleFieldModel(ofield), this, createAction) {
        @Override
        protected void performNewSelection(RoleFieldModel ofieldModel, Topic selectedTopic) {
          FieldInstanceAssociationBinaryPanel.this.performNewSelection(ofieldModel.getRoleField(), selectedTopic);           
        }          
        
      };
      createPanel.setOutputMarkupId(true);
      fieldInstanceButtons.add(createPanel);
    }
	}
  
  protected RoleField.ValueIF performNewSelection(RoleField selectedField, Topic selectedTopic) {
    FieldInstance fieldInstance = fieldInstanceModel.getFieldInstance();
    RoleField currentField = (RoleField)fieldInstance.getFieldAssignment().getFieldDefinition();
    Topic currentTopic = fieldInstance.getInstance();
    RoleField.ValueIF value = RoleField.createValue(2);
    value.addPlayer(currentField, currentTopic);            
    value.addPlayer(selectedField, selectedTopic);
    AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
    fieldInstance.addValue(value, page.getListener()); // currentField.addValue(fieldInstance, value, page.getListener());
    return value;
  }

}
