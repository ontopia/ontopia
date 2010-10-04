package ontopoly.models;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.utils.ObjectUtils;
import ontopoly.OntopolyContext;
import ontopoly.model.RoleFieldIF;
import ontopoly.model.OntopolyTopicIF;
import ontopoly.model.OntopolyTopicMapIF;

import org.apache.wicket.model.LoadableDetachableModel;

public class AssociationFieldValueModel extends LoadableDetachableModel<RoleFieldIF.ValueIF> {

  private String[][] afvinfo; // [topicMapId, associationTypeId, roleTypeId,
                              // playerId]

  public AssociationFieldValueModel(RoleFieldIF.ValueIF afv) {
    super(afv);
    if (afv == null)
      throw new NullPointerException(
          "association field value  parameter cannot be null.");

    RoleFieldIF[] rfields = afv.getRoleFields();    
    OntopolyTopicIF[] players = afv.getPlayers();
    afvinfo = new String[rfields.length][3];
    for (int i=0; i < rfields.length; i++) {
      if (rfields[i] == null) continue; // REALLY?
      afvinfo[i][0] = rfields[i].getTopicMap().getId();
      afvinfo[i][1] = rfields[i].getId();
      afvinfo[i][2] = players[i].getId();
    }
  }

  public RoleFieldIF.ValueIF getAssociationFieldValue() {
    return (RoleFieldIF.ValueIF) getObject();
  }

  @Override
  protected RoleFieldIF.ValueIF load() {
    RoleFieldIF.ValueIF value = null;
    for (int i = 0; i < afvinfo.length; i++) {
      // create field
      OntopolyTopicMapIF tm = OntopolyContext.getTopicMap(afvinfo[i][0]);
      RoleFieldIF roleField = tm.findRoleField(afvinfo[i][1]);
      OntopolyTopicIF player = tm.findTopic(afvinfo[i][2]);

      if (value == null)
        // using this to delay creation until we have a roleField
        value = roleField.createValue(afvinfo.length);
      
      value.addPlayer(roleField, player);
    }
    return value;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof AssociationFieldValueModel)
      return ObjectUtils.equals(getAssociationFieldValue(),
          ((AssociationFieldValueModel) obj).getAssociationFieldValue());
    else
      return false;
  }

  @Override
  public int hashCode() {
    return getAssociationFieldValue().hashCode();
  }

}
