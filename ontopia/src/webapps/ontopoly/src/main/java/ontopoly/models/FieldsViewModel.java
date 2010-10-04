package ontopoly.models;

import net.ontopia.topicmaps.core.TopicIF;
import ontopoly.OntopolyContext;
import ontopoly.model.FieldsViewIF;
import ontopoly.model.OntopolyTopicMapIF;

import org.apache.wicket.model.LoadableDetachableModel;

public class FieldsViewModel extends LoadableDetachableModel<FieldsViewIF> {
  private String topicMapId;
  private String topicId;

  public FieldsViewModel(FieldsViewIF fieldsView) {
    super(fieldsView);
    if (fieldsView == null)
      throw new RuntimeException("fieldsView cannot be null.");
    if (fieldsView != null) {
      this.topicMapId = fieldsView.getTopicMap().getId();
      this.topicId = fieldsView.getId();
    }
  }
  
  public FieldsViewModel(String topicMapId, String topicId) {
    if (topicMapId == null)
      throw new NullPointerException("topicMapId parameter cannot be null.");
    if (topicId == null)
      throw new NullPointerException("topicId parameter cannot be null.");
    this.topicMapId = topicMapId;
    this.topicId = topicId;
  }

  public FieldsViewIF getFieldsView() {
    return (FieldsViewIF)getObject();
  }
  
  @Override
  protected FieldsViewIF load() {
    if (topicMapId == null) return null;
    OntopolyTopicMapIF tm = OntopolyContext.getTopicMap(topicMapId);
    return tm.findFieldsView(topicId);
  }
}
