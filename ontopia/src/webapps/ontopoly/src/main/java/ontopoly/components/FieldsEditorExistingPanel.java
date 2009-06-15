package ontopoly.components;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.AssociationField;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.AssociationType;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.Cardinality;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.FieldAssignment;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.FieldDefinition;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.OccurrenceField;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.RoleField;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.Topic;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.TopicType;
import net.ontopia.utils.ObjectUtils;
import ontopoly.jquery.DraggableBehavior;
import ontopoly.jquery.DroppableBehavior;
import ontopoly.models.FieldAssignmentModel;
import ontopoly.models.FieldDefinitionModel;
import ontopoly.models.TopicTypeModel;
import ontopoly.pages.InstancePage;
import ontopoly.utils.TopicChoiceRenderer;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;

public abstract class FieldsEditorExistingPanel extends Panel {

  private boolean readonly;
  
  public FieldsEditorExistingPanel(String id, final TopicTypeModel topicTypeModel, final FieldAssignmentModel fieldAssignmentModel, boolean readonly) {
    super(id);
    this.readonly = readonly;
    
    FieldAssignment fieldAssignment = fieldAssignmentModel.getFieldAssignment(); 

    WebMarkupContainer container = new WebMarkupContainer("field");
    container.setModel(fieldAssignmentModel);
    add(container);
    
    OntopolyImage icon = new OntopolyImage("icon", "dnd.gif", new ResourceModel("icon.dnd.reorder"));
    icon.setVisible(!readonly);
    container.add(icon);

    FieldDefinition fieldDefinition = fieldAssignment.getFieldDefinition();
    final FieldDefinitionModel fieldDefinitionModel = new FieldDefinitionModel(fieldDefinition);
    
    container.add(new FieldDefinitionLabel("fieldLabel", fieldDefinitionModel) {
      @Override
      protected boolean isFieldDefinitionLinkEnabled(Topic topic) {
        return true;
      }      
      @Override
      protected boolean isOntologyTypeLinkEnabled(Topic topic) {
        return true;
      }      
    });

    container.add(getFieldType("valueType", fieldAssignmentModel.getFieldAssignment().getFieldDefinition()));
    
    Component cardinalityComponent = getCardinality("cardinality", fieldAssignmentModel);
    cardinalityComponent.setEnabled(!readonly);
    container.add(cardinalityComponent);
    
    // drag and drop
    if (!readonly) {
      String dndId = "fields_" + topicTypeModel.getTopicType().getId();
      container.add(new DraggableBehavior(dndId));
      container.add(new DroppableBehavior(dndId) {
        @Override
        protected MarkupContainer getDropContainer() {
          return FieldsEditorExistingPanel.this.getParent().getParent();
        }
        @Override
        protected void onDrop(Component component, AjaxRequestTarget target) {
          FieldAssignmentModel fam_dg = (FieldAssignmentModel)component.getModel();
          FieldAssignmentModel fam_do = (FieldAssignmentModel)getComponent().getModel();
          FieldAssignment fa_dg = fam_dg.getFieldAssignment();
          FieldAssignment fa_do = fam_do.getFieldAssignment();
          fa_do.moveAfter(fa_dg);
          onMoveAfter(fam_dg, fam_do, target);
        }      
      });
    }
    
    TopicType topicType = topicTypeModel.getTopicType();
    TopicType declaredTopicType = fieldAssignmentModel.getFieldAssignment().getDeclaredTopicType();

    if (ObjectUtils.equals(topicType, declaredTopicType)) {
      OntopolyImageLink button = new OntopolyImageLink("button", "remove-value.gif", new ResourceModel("icon.remove.field")) {
        @Override
        public void onClick(AjaxRequestTarget target) {
          onRemove(fieldAssignmentModel, target);
        }
        @Override
        public boolean isVisible() {
          return !FieldsEditorExistingPanel.this.readonly;
          //! || (fieldDefinitionModel.getFieldDefinition().isSystemTopic() && ((OntopolySession)Session.get()).isAdministrationEnabled());
        }
      };
      container.add(button);
    } else {
      OntopolyImageLink button = new OntopolyImageLink("button", "goto.gif", new ResourceModel("icon.goto.assigning-type")) {
        @Override
        public void onClick(AjaxRequestTarget target) {
          Map pageParametersMap = new HashMap(2);
          TopicType declaredTopicType = fieldAssignmentModel.getFieldAssignment().getDeclaredTopicType();          
          pageParametersMap.put("topicMapId", declaredTopicType.getTopicMap().getId());
          pageParametersMap.put("topicId", declaredTopicType.getId());
          pageParametersMap.put("ontology", "true");
          setResponsePage(InstancePage.class, new PageParameters(pageParametersMap)); 
        }
      };
      container.add(button);      
    }
  }

  protected abstract void onMoveAfter(FieldAssignmentModel fam_dg, FieldAssignmentModel fam_do, AjaxRequestTarget target);    

  protected abstract void onRemove(FieldAssignmentModel fam, AjaxRequestTarget target);    
  
  static Component getFieldType(String id, FieldDefinition fieldDefinition) {
    int fieldType = fieldDefinition.getFieldType();

    if(fieldType == FieldDefinition.FIELD_TYPE_ROLE) {
      TopicType tt = null;
      RoleField rf = (RoleField)fieldDefinition;
      AssociationField afield = rf.getAssociationField();
      List fields = afield.getFieldsForRoles();
      int numberOfRoles = fields.size();  
      String fieldTypeAsString = "";
      if (numberOfRoles == 1) { // unary
        return new Label(id, new ResourceModel("FieldsEditorExistingPanel.valuetype.unary"));
      } else if(numberOfRoles == 2) { // binary
        AssociationType at = rf.getAssociationType();
        if (at.isSymmetric()) { // symmetric
          Collection allowedValueTypes = rf.getDeclaredPlayerTypes(); 
          if (allowedValueTypes.size() == 1) { // only one allowed player
            Iterator it = allowedValueTypes.iterator();         
            // It's only one element in the list
            while (it.hasNext()) {
              tt = (TopicType)it.next();
              fieldTypeAsString = tt.getName();
            }
          } else { // more than one allowed player
            return new Label(id, new ResourceModel("FieldsEditorExistingPanel.valuetype.many")).add(new SimpleAttributeModifier("title", getAllowedPlayerNames(rf))).add(new SimpleAttributeModifier("class", "italic"));
          }   
        } else { // binary
          Iterator it = fields.iterator();
          while (it.hasNext()) {
            RoleField rf2 = (RoleField) it.next();
            if (!rf.equals(rf2)) { // the other association field
              Collection allowedValueTypes = rf2.getDeclaredPlayerTypes(); 
              if (allowedValueTypes.size() == 1) { // only one allowed player
                Iterator it2 =  allowedValueTypes.iterator();         
                // It's only one element in the list
                while (it2.hasNext()) {
                  tt = (TopicType)it2.next();
                  fieldTypeAsString = tt.getName();
                }
              }
              else { // more than one allowed player  
                return new Label(id, new ResourceModel("FieldsEditorExistingPanel.valuetype.many")).add(new SimpleAttributeModifier("title", getAllowedPlayerNames(rf))).add(new SimpleAttributeModifier("class", "italic"));
              }  
            }
          }
        }
      } else if(numberOfRoles > 2) { // n-ary
        return new Label(id, new ResourceModel("FieldsEditorExistingPanel.valuetype.many")).add(new SimpleAttributeModifier("title", getAllowedPlayerNames(rf))).add(new SimpleAttributeModifier("class", "italic"));
      }
      
      // Create link if the association field is for a binary association
      // with only one topic type on the other side of the association.
      PageParameters params = new PageParameters();
      params.put("topicMapId", fieldDefinition.getTopicMap().getId());
      params.put("ontology", "true");
      if (tt != null)
        params.put("topicId", tt.getId());
      
      return new OntopolyBookmarkablePageLink(id, InstancePage.class, params, fieldTypeAsString);        
    }
    else if(fieldType == FieldDefinition.FIELD_TYPE_IDENTITY) {
      return new Label(id, new ResourceModel("FieldsEditorExistingPanel.valuetype.uri"));     
    }
    else if(fieldType == FieldDefinition.FIELD_TYPE_NAME) {
      return new Label(id, new ResourceModel("FieldsEditorExistingPanel.valuetype.name"));     
    } 
    else if(fieldType == FieldDefinition.FIELD_TYPE_OCCURRENCE) {
      return new Label(id, ((OccurrenceField)fieldDefinition).getDataType().getName());     
    }
    else { // Invalid field type
      return new Label(id, "invalid"); // TODO replace with exception or something
    }
  }
  
  private static String getAllowedPlayerNames(RoleField af) {
    Set topicTypeNames = new TreeSet();
    
    AssociationType at = af.getAssociationType();
    AssociationField afield = at.getTopicMap().getAssociationField(at);
    Iterator it = afield.getFieldsForRoles().iterator();
    while(it.hasNext()) {
      RoleField af2 = (RoleField) it.next();
      if(!af.equals(af2)) { // one of the other association fields
          Iterator it2 =  af2.getDeclaredPlayerTypes().iterator();         
          while(it2.hasNext()) {
            topicTypeNames.add(((TopicType)it2.next()).getName());
          } 
      }      
    }
    return topicTypeNames.toString();
  }
  
  private static Component getCardinality(String id, final FieldAssignmentModel fam) {    
//    Component cardinality = null;
//    if (isReadOnlyPage()) {
//      cardinality = new Label("cardinality", new PropertyModel(new FieldAssignmentModel(fa), "cardinality.name"));
//    } else {
      LoadableDetachableModel cardinalityChoicesModel = new LoadableDetachableModel() {
        @Override
        protected Object load() {
          return Cardinality.getCardinalityTypes(fam.getFieldAssignment().getFieldDefinition().getTopicMap());
        }   
      };
      
      AjaxOntopolyDropDownChoice choice = new AjaxOntopolyDropDownChoice("cardinality", new PropertyModel(fam, "fieldDefinition.cardinality"),
          cardinalityChoicesModel, TopicChoiceRenderer.INSTANCE);
      return choice;
//    }
  }
}
