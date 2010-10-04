package ontopoly.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.ontopia.utils.CompactHashSet;

import ontopoly.model.OntopolyTopicIF;
import ontopoly.model.OntopolyTopicMapIF;
import ontopoly.model.TopicTypeIF;
import ontopoly.utils.TopicComparator;

import org.apache.wicket.model.LoadableDetachableModel;

public abstract class AvailableTopicTypesModel extends LoadableDetachableModel<List<TopicTypeIF>> {
  private TopicModel topicModel;  
  
  public AvailableTopicTypesModel(TopicModel topicModel) {
    this.topicModel = topicModel;
  }
  
  protected boolean getShouldIncludeSelf() {
    return false;
  }
  
  protected boolean getShouldIncludeExistingTypes() {
    return false;
  }
  
  @Override
  protected List<TopicTypeIF> load() {
    // FIXME: use sorted set instead?
    Collection<TopicTypeIF> types = new CompactHashSet<TopicTypeIF>();
    OntopolyTopicIF topic = topicModel.getTopic();
    OntopolyTopicMapIF topicMap = topic.getTopicMap();
    types.addAll(topicMap.getTopicTypes());
    if (!getShouldIncludeSelf()) 
      types.remove(topic); // remove topic itself as it cannot be an instance of itself
    if (!getShouldIncludeExistingTypes())
      types.removeAll(topic.getTopicTypes());
    List<TopicTypeIF> result = new ArrayList<TopicTypeIF>(types.size()); 
    Iterator iter = types.iterator();
    while (iter.hasNext()) {
      TopicTypeIF o = (TopicTypeIF)iter.next();
      if (filter(o))
        result.add(o);
    }
    Collections.sort(result, TopicComparator.INSTANCE);
    return result;
  }
  
  protected abstract boolean filter(OntopolyTopicIF o);
  
}
