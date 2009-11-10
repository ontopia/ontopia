package ontopoly.components;

import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.FieldDefinition;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.IdentityField;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.NameField;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.OccurrenceField;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.RoleField;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.Topic;
import ontopoly.images.ImageResource;
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
    
    add(new TopicLink("ontologyType", new TopicModel<Topic>(ontologyType)) {
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

  protected boolean isFieldDefinitionLinkEnabled(Topic topic) {
//    return false;
    AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
    return page.isShortcutsEnabled();    
  }

  protected boolean isOntologyTypeLinkEnabled(Topic topic) {
//    return false;
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
    default:
      throw new RuntimeException("Unknown field definition type: " + fieldDefinition);
    }
  }
  
}
