/*
 * #!
 * Ontopoly Editor
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */
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
