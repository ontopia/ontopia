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

import java.util.List;

import net.ontopia.utils.OntopiaRuntimeException;
import ontopoly.model.FieldAssignment;
import ontopoly.model.FieldDefinition;
import ontopoly.model.FieldInstance;
import ontopoly.model.FieldsView;
import ontopoly.model.RoleField;
import ontopoly.model.ViewModes;
import ontopoly.models.FieldInstanceModel;
import ontopoly.models.FieldsViewModel;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FieldInstancesPanel extends Panel {

  protected static Logger log = LoggerFactory.getLogger(FieldInstancesPanel.class);

  protected boolean readonly;
  private ListView<FieldInstanceModel> listView;
  
  public FieldInstancesPanel(String id, 
      List<FieldInstanceModel> fieldInstanceModels, final FieldsViewModel fieldsViewModel,
      final boolean readonly, final boolean traversable) {
    super(id);
    this.readonly = readonly;
    
    listView = new ListView<FieldInstanceModel>("fields", fieldInstanceModels) {
      @Override
      public void populateItem(final ListItem<FieldInstanceModel> item) {
        FieldInstanceModel fieldInstanceModel = item.getModelObject();
        item.setRenderBodyOnly(true);
        Component component;
        try {
          component = createFieldInstanceComponent("field", fieldInstanceModel, fieldsViewModel, traversable);
        } catch (Exception e) {
          log.error("Error occurred while creating field instance component", e);
          component = new FieldInstanceErrorPanel("field", fieldInstanceModel, e);
        }
        component.setRenderBodyOnly(true);
        item.add(component);
      }
    };
    listView.setReuseItems(true);
    add(listView);    
  }

  public ListView<FieldInstanceModel> getFieldList() {
    return listView;
  }
  
  protected Component createFieldInstanceComponent(String id, FieldInstanceModel fieldInstanceModel, FieldsViewModel fieldsViewModel, boolean _traversable) {
    FieldInstance fieldInstance = fieldInstanceModel.getFieldInstance();
    FieldAssignment fieldAssignment = fieldInstance.getFieldAssignment();
    FieldDefinition fieldDefinition = fieldAssignment.getFieldDefinition();
    if (fieldsViewModel == null) {
      throw new RuntimeException("Fields view not specified.");
    }
    
    FieldsView fieldsView = fieldsViewModel.getFieldsView();
    
    // change from parent view to child view, if specified
    //FieldsView valueView = ofieldModel.getRoleField().getValueView(fieldsViewModel.getFieldsView());
    fieldsView = fieldDefinition.getValueView(fieldsView);
    fieldsViewModel = new FieldsViewModel(fieldsView);
    
    // given the current view see if it is readonly and/or embedded
    ViewModes viewModes = fieldDefinition.getViewModes(fieldsView);
    final boolean rofield = (readonly  || viewModes.isReadOnly());
    final boolean embedded = viewModes.isEmbedded();
    
    // add field to panel
    switch (fieldDefinition.getFieldType()) {
    case FieldDefinition.FIELD_TYPE_ROLE: {
      RoleField roleField = (RoleField)fieldDefinition;
      int arity = roleField.getAssociationField().getArity();
      // unary
      if (arity == 1) {
        return new FieldInstanceAssociationUnaryPanel(id, fieldInstanceModel, rofield).setOutputMarkupId(true);        
      }
      // binary
      else if (arity == 2) {
        final boolean traversable = (_traversable ? viewModes.isTraversable() : false);

        if (embedded) {
          return new FieldInstanceAssociationBinaryEmbeddedPanel(id, fieldInstanceModel, fieldsViewModel, rofield, traversable).setOutputMarkupId(true);
        } else {
          return new FieldInstanceAssociationBinaryPanel(id, fieldInstanceModel, fieldsViewModel, rofield, traversable).setOutputMarkupId(true);
        }
      } 
      // n-ary
      else {
        final boolean traversable = (_traversable ? viewModes.isTraversable() : false);
        return new FieldInstanceAssociationNaryPanel(id, fieldInstanceModel, fieldsViewModel, rofield, traversable, arity).setOutputMarkupId(true);        
      }
    }
    case FieldDefinition.FIELD_TYPE_IDENTITY: {
      return new FieldInstanceIdentityPanel(id, fieldInstanceModel, rofield);
    }
    case FieldDefinition.FIELD_TYPE_NAME: {
      return new FieldInstanceNamePanel(id, fieldInstanceModel, rofield);
    }
    case FieldDefinition.FIELD_TYPE_OCCURRENCE: {
      return new FieldInstanceOccurrencePanel(id, fieldInstanceModel, rofield);
    }
    case FieldDefinition.FIELD_TYPE_QUERY: {
      final boolean traversable = (_traversable ? viewModes.isTraversable() : false);
      if (embedded) {
        return new FieldInstanceQueryEmbeddedPanel(id, fieldInstanceModel, fieldsViewModel, rofield, traversable);
      } else {
        return new FieldInstanceQueryPanel(id, fieldInstanceModel, fieldsViewModel, rofield, traversable);
      }
    }
    default:
      throw new OntopiaRuntimeException("Unknown field definition: " + fieldDefinition.getFieldType());
    }
    
  }
}
