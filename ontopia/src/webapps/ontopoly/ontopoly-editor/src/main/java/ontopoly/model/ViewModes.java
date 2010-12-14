package ontopoly.model;

import java.util.Collection;

import net.ontopia.topicmaps.core.TopicIF;
import ontopoly.utils.OntopolyModelUtils;

public class ViewModes {

  private final boolean readOnly;
  private final boolean traversable;
  private final boolean hidden;
  private final boolean embedded;
  
  ViewModes(FieldDefinition fieldDefinition, FieldsView view) {
    TopicMap tm = fieldDefinition.getTopicMap();
    Collection<TopicIF> viewModes = getSpecifiedViewModes(fieldDefinition, view);
    this.readOnly = viewModes.contains(OntopolyModelUtils.getTopicIF(tm, PSI.ON_VIEW_MODE_READ_ONLY));
    this.hidden = viewModes.contains(OntopolyModelUtils.getTopicIF(tm, PSI.ON_VIEW_MODE_HIDDEN));
    this.traversable = !viewModes.contains(OntopolyModelUtils.getTopicIF(tm, PSI.ON_VIEW_MODE_NOT_TRAVERSABLE));
    this.embedded = viewModes.contains(OntopolyModelUtils.getTopicIF(tm, PSI.ON_VIEW_MODE_EMBEDDED));
  }
  
  private Collection<TopicIF> getSpecifiedViewModes(FieldDefinition fieldDefinition, FieldsView view) {
    TopicMap tm = fieldDefinition.getTopicMap();
    TopicIF aType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "use-view-mode");
    TopicIF rType1 = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "field-definition");
    TopicIF player1 = fieldDefinition.getTopicIF();
    TopicIF rType2 = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "fields-view");
    TopicIF player2 = view.getTopicIF();
    TopicIF rType3 = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "view-mode");
    return OntopolyModelUtils.findTernaryPlayers(tm, aType, player1, rType1, player2, rType2, rType3);
  }
  
  public boolean isReadOnly() {
    return readOnly;
  }

  public boolean isTraversable() {
    return traversable;
  }

  public boolean isHidden() {
    return hidden;
  }

  public boolean isEmbedded() {
    return embedded;
  }
  
}
