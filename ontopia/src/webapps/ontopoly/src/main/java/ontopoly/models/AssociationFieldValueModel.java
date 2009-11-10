package ontopoly.models;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.RoleField;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.Topic;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.TopicMap;
import net.ontopia.utils.ObjectUtils;
import ontopoly.utils.OntopolyContext;

import org.apache.wicket.model.LoadableDetachableModel;

public class AssociationFieldValueModel extends LoadableDetachableModel<RoleField.ValueIF> {

  private String[][] afvinfo; // [topicMapId, associationTypeId, roleTypeId,
                              // playerId]

  public AssociationFieldValueModel(RoleField.ValueIF afv) {
    super(afv);
    if (afv == null)
      throw new NullPointerException(
          "association field value  parameter cannot be null.");

    RoleField[] fields = afv.getRoleFields();    
    Topic[] players = afv.getPlayers();
    afvinfo = new String[fields.length][3];
    for (int i=0; i < fields.length; i++) {
      if (fields[i] == null) continue; // REALLY?
      afvinfo[i][0] = fields[i].getTopicMap().getId();
      afvinfo[i][1] = fields[i].getId();
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
