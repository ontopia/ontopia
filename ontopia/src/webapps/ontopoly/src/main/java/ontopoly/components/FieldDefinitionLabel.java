package ontopoly.components;

import ontopoly.images.ImageResource;
import ontopoly.model.FieldDefinitionIF;
import ontopoly.model.IdentityFieldIF;
import ontopoly.model.NameFieldIF;
import ontopoly.model.OccurrenceFieldIF;
import ontopoly.model.RoleFieldIF;
import ontopoly.model.OntopolyTopicIF;
import ontopoly.models.FieldDefinitionModel;
import ontopoly.models.TopicModel;
import ontopoly.pages.AbstractOntopolyPage;

import org.apache.wicket.RequestCycle;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.value.ValueMap;

public class FieldDefinitionLabel extends Panel {
  
  public FieldDefinitionLabel(String id, final FieldDefinitionModel fieldDefinitionModel) {
    super(id);

    OntopolyTopicIF ontologyType = getPrimaryOntologyType(fieldDefinitionModel.getFieldDefinition());    
    
    add(new TopicLink("ontologyType", new TopicModel<OntopolyTopicIF>(ontologyType)) {
      @Override
      public String getLabel() {
        return fieldDefinitionModel.getFieldDefinition().getFieldName();
      }      
      @Override
      public boolean isEnabled() {
        return isOntologyTypeLinkEnabled(getTopic());
      }
    });
    
    add(new TopicLink("fieldDefinition", fieldDefinitionModel) {
      @Override
      protected String getLabel() {
        return "<img src=\"" +  RequestCycle.get().urlFor(new ResourceReference(ImageResource.class, "goto-details.gif"), ValueMap.EMPTY_MAP) + "\"/>";
      }
      @Override
      public boolean getEscapeLabel() {
        return false;
      }
      @Override
      public boolean isVisible() {
        return isFieldDefinitionLinkEnabled(getTopic()); 
      }
      @Override
      public boolean isEnabled() {
        return isFieldDefinitionLinkEnabled(getTopic());
      }
    });
    
  }

  protected boolean isFieldDefinitionLinkEnabled(OntopolyTopicIF topic) {
    AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
    return page.isShortcutsEnabled();    
  }

  protected boolean isOntologyTypeLinkEnabled(OntopolyTopicIF topic) {
    AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
    return page.isShortcutsEnabled();
  }

  // FIXME: this does not belong here, and can be done more cleanly
  private OntopolyTopicIF getPrimaryOntologyType(FieldDefinitionIF fieldDefinition) {
    switch (fieldDefinition.getFieldType()) {
    case FieldDefinitionIF.FIELD_TYPE_IDENTITY:
      return ((IdentityFieldIF)fieldDefinition).getIdentityType();
    case FieldDefinitionIF.FIELD_TYPE_NAME:
      return ((NameFieldIF)fieldDefinition).getNameType();
    case FieldDefinitionIF.FIELD_TYPE_OCCURRENCE:
      return ((OccurrenceFieldIF)fieldDefinition).getOccurrenceType();
    case FieldDefinitionIF.FIELD_TYPE_ROLE:
      return ((RoleFieldIF)fieldDefinition).getAssociationField().getAssociationType();
    default:
      throw new RuntimeException("Unknown field definition type: " + fieldDefinition);
    }
  }
  
}
