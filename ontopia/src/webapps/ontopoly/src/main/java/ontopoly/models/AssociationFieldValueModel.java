package ontopoly.models;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.utils.ObjectUtils;
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
    if (afv == null)
      throw new NullPointerException(
          "association field value  parameter cannot be null.");

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
