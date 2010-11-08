package ontopoly.components;

import ontopoly.images.ImageResource;
import ontopoly.model.FieldDefinition;
import ontopoly.model.IdentityField;
import ontopoly.model.NameField;
import ontopoly.model.OccurrenceField;
import ontopoly.model.QueryField;
import ontopoly.model.RoleField;
import ontopoly.model.Topic;
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

    Topic ontologyType = getPrimaryOntologyType(fieldDefinitionModel.getFieldDefinition());    
    
    add(new TopicLink<Topic>("ontologyType", new TopicModel<Topic>(ontologyType)) {
      @Override
      public String getLabel() {
        return fieldDefinitionModel.getFieldDefinition().getFieldName();
      }      
      @Override
      public boolean isEnabled() {
        return isOntologyTypeLinkEnabled(getTopic());
      }
    });
    
    add(new TopicLink<FieldDefinition>("fieldDefinition", fieldDefinitionModel) {
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

  protected boolean isFieldDefinitionLinkEnabled(Topic topic) {
    AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
    return page.isShortcutsEnabled();    
  }

  protected boolean isOntologyTypeLinkEnabled(Topic topic) {
    AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
    return page.isShortcutsEnabled();
  }

  private Topic getPrimaryOntologyType(FieldDefinition fieldDefinition) {
    switch (fieldDefinition.getFieldType()) {
    case FieldDefinition.FIELD_TYPE_IDENTITY:
      return ((IdentityField)fieldDefinition).getIdentityType();
    case FieldDefinition.FIELD_TYPE_NAME:
      return ((NameField)fieldDefinition).getNameType();
    case FieldDefinition.FIELD_TYPE_OCCURRENCE:
      return ((OccurrenceField)fieldDefinition).getOccurrenceType();
    case FieldDefinition.FIELD_TYPE_ROLE:
      return ((RoleField)fieldDefinition).getAssociationField().getAssociationType();
    case FieldDefinition.FIELD_TYPE_QUERY:
      return (QueryField)fieldDefinition;
    default:
      throw new RuntimeException("Unknown field definition type: " + fieldDefinition);
    }
  }
  
}
