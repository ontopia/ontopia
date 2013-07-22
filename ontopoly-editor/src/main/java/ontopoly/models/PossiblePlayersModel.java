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
package ontopoly.models;

import java.util.Collection;
import java.util.List;

import ontopoly.model.FieldInstance;
import ontopoly.model.RoleField;
import ontopoly.model.Topic;

import org.apache.wicket.model.LoadableDetachableModel;

public abstract class PossiblePlayersModel extends LoadableDetachableModel<List<Topic>> {

  private FieldInstanceModel fieldInstanceModel;
  private RoleFieldModel roleFieldModel;
  
  public PossiblePlayersModel(FieldInstanceModel fieldInstanceModel, RoleFieldModel roleFieldModel) {
    this.fieldInstanceModel = fieldInstanceModel;
    // NOTE: this is the association field that is to be assigned a player
    this.roleFieldModel = roleFieldModel; 
  }
  
  @Override
  protected List<Topic> load() {
    FieldInstance fieldInstance = fieldInstanceModel.getFieldInstance();
    RoleField roleField = roleFieldModel.getRoleField();    
    List<Topic> result = roleField.getAllowedPlayers(fieldInstance.getInstance());
    filterPlayers(result);
    return result;
  }

  protected abstract void filterPlayers(Collection<Topic> players);

  @Override
  protected void onDetach() {
    fieldInstanceModel.detach();
    roleFieldModel.detach();
    super.onDetach();
  }   
  
}
