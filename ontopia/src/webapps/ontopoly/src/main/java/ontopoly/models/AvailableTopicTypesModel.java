package ontopoly.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.Topic;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.TopicMap;
import ontopoly.utils.TopicComparator;

import org.apache.wicket.model.LoadableDetachableModel;

public abstract class AvailableTopicTypesModel extends LoadableDetachableModel {

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
  protected Object load() {
    Collection types = new HashSet();
    Topic topic = topicModel.getTopic();
    TopicMap topicMap = topic.getTopicMap();
    types.addAll(topicMap.getTopicTypes());
    if (!getShouldIncludeSelf()) 
      types.remove(topic); // remove topic itself as it cannot be an instance of itself
    if (!getShouldIncludeExistingTypes())
      types.removeAll(topic.getTopicTypes());
    List result = new ArrayList(types.size()); 
    Iterator iter = types.iterator();
    while (iter.hasNext()) {
      Topic o = (Topic)iter.next();
      if (filter(o))
        result.add(o);
    }
    Collections.sort(result, TopicComparator.INSTANCE);
    return result;
  }
  
  protected abstract boolean filter(Topic o);
  
}
