package ontopoly.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.ontopia.utils.ObjectUtils;
import ontopoly.model.AssociationTypeIF;
import ontopoly.model.FieldAssignmentIF;
import ontopoly.model.FieldDefinitionIF;
import ontopoly.model.NameFieldIF;
import ontopoly.model.NameTypeIF;
import ontopoly.model.OccurrenceFieldIF;
import ontopoly.model.OccurrenceTypeIF;
import ontopoly.model.RoleFieldIF;
import ontopoly.model.OntopolyTopicIF;
import ontopoly.model.TopicTypeIF;
import ontopoly.models.FieldAssignmentModel;
import ontopoly.models.FieldDefinitionModel;
import ontopoly.models.MutableLoadableDetachableModel;
import ontopoly.models.TopicTypeModel;
import ontopoly.pages.AbstractOntopolyPage;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxIndicatorAware;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;

public class FieldsEditor extends Panel {
  TopicTypeModel topicTypeModel;
  boolean readonly;
  
  ListView listView;
  MutableLoadableDetachableModel<List<FieldAssignmentModel>> fieldAssignmentModels;
  WebMarkupContainer addFieldsContainer;
  
  public FieldsEditor(String id, TopicTypeModel _topicTypeModel, final boolean readonly) {
    super(id);
    this.topicTypeModel = _topicTypeModel;
    this.readonly = readonly;   
    setOutputMarkupId(true);

    // existing fields
    this.fieldAssignmentModels = new MutableLoadableDetachableModel<List<FieldAssignmentModel>>() {
      @Override
      protected List<FieldAssignmentModel> load() {
        List<FieldAssignmentIF> fieldAssignments = topicTypeModel.getTopicType().getFieldAssignments();
        return FieldAssignmentModel.wrapInFieldAssignmentModels(fieldAssignments);
      }      
    };
    
    this.listView = new ListView<FieldAssignmentModel>("existingFields", fieldAssignmentModels) {
      public void populateItem(final ListItem<FieldAssignmentModel> item) {
        
        FieldAssignmentModel fieldAssignmentModel = item.getModelObject();
        item.setRenderBodyOnly(true);
        
        Component component = new FieldsEditorExistingPanel("field", topicTypeModel, fieldAssignmentModel, readonly) {
          @Override
          protected void onMoveAfter(FieldAssignmentModel fam_dg, FieldAssignmentModel fam_do, AjaxRequestTarget target) {
            // notify parent
            onUpdate(target);
          }

          @Override
          protected void onRemove(FieldAssignmentModel fam, AjaxRequestTarget target) {
            TopicTypeIF topicType  = topicTypeModel.getTopicType();
            FieldAssignmentIF fieldAssignment = fam.getFieldAssignment();
            topicType.removeField(fieldAssignment.getFieldDefinition());
            // notify parent
            onUpdate(target);
          }          
        };
        component.setRenderBodyOnly(true);
        item.add(component);
      }
    };
    listView.setReuseItems(true);
    add(listView);

    WebMarkupContainer actionsContainer = new WebMarkupContainer("actions") {
      @Override
      public boolean isVisible() {
        return !FieldsEditor.this.readonly;
      }      
    };
    add(actionsContainer);
    actionsContainer.add(new FieldDefinitionTypeLink("names") {
      @Override
      protected List<NameFieldIF> getFieldDefinitions() {
        List<NameFieldIF> fields = topicTypeModel.getTopicType().getTopicMap().getNameFields();
        filterFieldDefinitions(fields);               
        return fields;
      }      
    });
    actionsContainer.add(new OntopolyImageLink("create-name-field", "create.gif", new ResourceModel("create.new.name.type")) {
      @Override
      public void onClick(AjaxRequestTarget target) {
        TopicTypeIF topicType = topicTypeModel.getTopicType();
        NameTypeIF nameType = topicType.createNameType();
        redirectToTopic(nameType);
      }
    });
    actionsContainer.add(new FieldDefinitionTypeLink("occurrences") {
      @Override
      protected List<OccurrenceFieldIF> getFieldDefinitions() {
        List<OccurrenceFieldIF> fields = topicTypeModel.getTopicType().getTopicMap().getOccurrenceFields();
        filterFieldDefinitions(fields);
        return fields;
      }      
    });
    actionsContainer.add(new OntopolyImageLink("create-occurrence-field", "create.gif", new ResourceModel("create.new.occurrence.type")) {
      @Override
      public void onClick(AjaxRequestTarget target) {
        TopicTypeIF topicType = topicTypeModel.getTopicType();
        OccurrenceTypeIF occurrenceType = topicType.createOccurrenceType();
        redirectToTopic(occurrenceType);
      }
    });
    actionsContainer.add(new FieldDefinitionTypeLink("associations") {
      @Override
      protected List<? extends FieldDefinitionIF> getFieldDefinitions() {
        List<RoleFieldIF> fields = topicTypeModel.getTopicType().getTopicMap().getRoleFields();
        filterFieldDefinitions(fields);
        return fields;
      }      
    });
    actionsContainer.add(new OntopolyImageLink("create-role-field", "create.gif", new ResourceModel("create.new.association.type")) {
      @Override
      public void onClick(AjaxRequestTarget target) {
        TopicTypeIF topicType = topicTypeModel.getTopicType();
        AssociationTypeIF associationType = topicType.createAssociationType();
        redirectToTopic(associationType);
      }
    });
    actionsContainer.add(new FieldDefinitionTypeLink("identities") {
      @Override
      protected List<? extends FieldDefinitionIF> getFieldDefinitions() {
        // FIXME: shouldn't these be filtered also?
        return topicTypeModel.getTopicType().getTopicMap().getIdentityFields();               
      }      
    });

    this.addFieldsContainer = new WebMarkupContainer("addFieldsContainer");
    addFieldsContainer.setOutputMarkupId(true);    
    add(this.addFieldsContainer);        

    // add empty listview
    List<FieldDefinitionModel> fields = Collections.emptyList();
    ListView afListView = createListView(fields);
    addFieldsContainer.add(afListView);
  }

  private void filterFieldDefinitions(List<? extends FieldDefinitionIF> result) {
    AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
    page.filterTopics(result);
  }
  
  private void redirectToTopic(OntopolyTopicIF topic) {
    AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
    setResponsePage(page.getPageClass(topic), page.getPageParameters(topic));
    setRedirect(true);
  }

  private ListView createListView(final List<FieldDefinitionModel> fieldDefinitionModels) {
    ListView listView = new ListView<FieldDefinitionModel>("addFields", fieldDefinitionModels) {
      public void populateItem(final ListItem<FieldDefinitionModel> item) {
        
        FieldDefinitionModel fieldDefinitionModel = item.getModelObject();
        item.setRenderBodyOnly(true);

        Component component = new FieldsEditorAddPanel("field", topicTypeModel, fieldDefinitionModel) {
          @Override
          protected void onAddField(TopicTypeModel topicTypeModel, FieldDefinitionModel fieldDefinitionModel, AjaxRequestTarget target) {
            // add field to topic type
            topicTypeModel.getTopicType().addField(fieldDefinitionModel.getFieldDefinition());

            // remove field definition from available list
            fieldDefinitionModels.remove(fieldDefinitionModel);
            ListView pListView = ((ListView)item.getParent()); 
            pListView.removeAll();
            onUpdate(target);
          }
        };
        component.setRenderBodyOnly(true);
        item.add(component);
      }
    };
    listView.setOutputMarkupId(true);
    listView.setReuseItems(true);
    return listView;
  }

  private String selectedTypeLinkId;
  
  private void replaceListView(FieldDefinitionTypeLink typeLink) {
    String typeLinkId = typeLink.getMarkupId();
    ListView afListView;
    if (ObjectUtils.different(typeLinkId, selectedTypeLinkId)) {      
      // replaces the existing listview with a new one
      selectedTypeLinkId = typeLinkId;
      afListView = createListView(filterAndWrapInFieldDefinitions(typeLink.getFieldDefinitions()));
    } else {
      List<FieldDefinitionModel> fields = Collections.emptyList();      
      afListView = createListView(fields);
      selectedTypeLinkId = null;
    }
    addFieldsContainer.replace(afListView);    
  }
  
  private List<FieldDefinitionModel> filterAndWrapInFieldDefinitions(List fieldDefinitions) {
    // resolve existing field definitions
    List<FieldAssignmentModel> fams = fieldAssignmentModels.getObject();
    Set<FieldDefinitionIF> existingFieldDefinitions = new HashSet<FieldDefinitionIF>(fams.size());
    Iterator iter = fams.iterator();
    while (iter.hasNext()) {
      FieldAssignmentModel fieldAssignmentModel = (FieldAssignmentModel)iter.next();
      existingFieldDefinitions.add(fieldAssignmentModel.getFieldAssignment().getFieldDefinition());
    }
    // filter and sort field definitions
    List<FieldDefinitionModel> result = new ArrayList<FieldDefinitionModel>(fieldDefinitions.size());
    iter = fieldDefinitions.iterator();
    while (iter.hasNext()) {
      FieldDefinitionIF fieldDefinition = (FieldDefinitionIF)iter.next();
      if (!existingFieldDefinitions.contains(fieldDefinition))
        result.add(new FieldDefinitionModel(fieldDefinition));
    }
    Collections.sort(result, new Comparator<Object>() {
      public int compare(Object o1, Object o2) {
        FieldDefinitionIF fd1 = ((FieldDefinitionModel)o1).getFieldDefinition();
        FieldDefinitionIF fd2 = ((FieldDefinitionModel)o2).getFieldDefinition();
        return ObjectUtils.compare(fd1.getFieldName(), fd2.getFieldName());
      }      
    });
    return result;
  }
  
  private abstract class FieldDefinitionTypeLink extends AjaxFallbackLink implements IAjaxIndicatorAware {
    FieldDefinitionTypeLink(String id) {
      super(id);
    }
    protected abstract List<? extends FieldDefinitionIF> getFieldDefinitions();
    
    @Override
    public void onClick(AjaxRequestTarget target) {
      replaceListView(this);
      target.addComponent(FieldsEditor.this);
    }
    public String getAjaxIndicatorMarkupId() {         
      return "ajaxIndicator";   
    }  
  }

  protected void onUpdate(AjaxRequestTarget target) {
    listView.removeAll();
    fieldAssignmentModels.detach(); // make sure list of field assignments is reloaded
    target.addComponent(FieldsEditor.this);
  }

  @Override
  public void onDetach() {
    topicTypeModel.detach();
    super.onDetach();
  }

}
