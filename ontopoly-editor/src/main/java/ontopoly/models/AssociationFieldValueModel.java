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

import java.util.Objects;
import net.ontopia.topicmaps.core.TopicIF;
import ontopoly.OntopolyContext;
import ontopoly.model.RoleField;
import ontopoly.model.Topic;
import ontopoly.model.TopicMap;

import org.apache.wicket.model.LoadableDetachableModel;

public class AssociationFieldValueModel extends LoadableDetachableModel<RoleField.ValueIF> {

  private String[][] afvinfo; // [topicMapId, associationTypeId, roleTypeId,
                              // playerId]

  public AssociationFieldValueModel(RoleField.ValueIF afv) {
    super(afv);
    Objects.requireNonNull(afv, "association field value  parameter cannot be null.");

    RoleField[] rfields = afv.getRoleFields();    
    Topic[] players = afv.getPlayers();
    afvinfo = new String[rfields.length][3];
    for (int i=0; i < rfields.length; i++) {
      if (rfields[i] == null) continue; // REALLY?
      afvinfo[i][0] = rfields[i].getTopicMap().getId();
      afvinfo[i][1] = rfields[i].getId();
      afvinfo[i][2] = players[i].getId();
    }
  }

  public RoleField.ValueIF getAssociationFieldValue() {
    return (RoleField.ValueIF) getObject();
  }

  @Override
  protected RoleField.ValueIF load() {
    RoleField.ValueIF value = RoleField.createValue(afvinfo.length);
    for (int i = 0; i < afvinfo.length; i++) {
      // create field
      TopicMap tm = OntopolyContext.getTopicMap(afvinfo[i][0]);
      TopicIF fieldTopicIf = tm.getTopicIFById(afvinfo[i][1]);
      // create player
      TopicIF playerIf = tm.getTopicIFById(afvinfo[i][2]);
      value.addPlayer(new RoleField(fieldTopicIf, tm), new Topic(playerIf, tm));
    }
    return value;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof AssociationFieldValueModel)
      return Objects.equals(getAssociationFieldValue(),
          ((AssociationFieldValueModel) obj).getAssociationFieldValue());
    else
      return false;
  }

  @Override
  public int hashCode() {
    return getAssociationFieldValue().hashCode();
  }

}
