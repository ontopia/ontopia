
// $Id: NameField.java,v 1.5 2009/05/06 14:19:11 geir.gronmo Exp $

package ontopoly.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import ontopoly.utils.OntopolyModelUtils;

/**
 * Represents a query field.
 */
public class QueryField extends FieldDefinition {

  public QueryField(TopicIF topic, TopicMap tm) {
    super(topic, tm);
  }

  @Override
  public int getFieldType() {
    return FIELD_TYPE_QUERY;
  }


  @Override
  public String getFieldName() {
    return getTopicMap().getTopicName(getTopicIF(), null);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof QueryField))
      return false;
    
    QueryField other = (QueryField)obj;
    return (getTopicIF().equals(other.getTopicIF()));
  }

  /**
   * Returns the result from executing the values query.
   * 
   * @param topic the topic from which the values is retrieved.
   * @return a collection of objects.
   */
  @Override
  public List<Topic> getValues(Topic topic) {

    String query = getValuesQuery();
    if (query != null) {
      Map<String,TopicIF> params = new HashMap<String,TopicIF>(2);
      params.put("field", getTopicIF());
      params.put("topic", topic.getTopicIF());

      QueryMapper<Topic> qm = getTopicMap().newQueryMapper(Topic.class);
      return qm.queryForList(query, params);

    } else {
      return Collections.emptyList();
    }
  }

  private String getValuesQuery() {
    TopicIF topicIf = getTopicIF();   
    TopicIF typeIf = OntopolyModelUtils.getTopicIF(getTopicMap(), PSI.ON, "values-query");
    OccurrenceIF occ = OntopolyModelUtils.findOccurrence(typeIf, topicIf);
    return (occ == null ? null : occ.getValue());
  }

  /**
   * Not supported.
   */
  @Override
  public void addValue(FieldInstance fieldInstance, Object _value, LifeCycleListener listener) {
    throw new UnsupportedOperationException();
  }

  /**
   * Not supported.
   */
  @Override
  public void removeValue(FieldInstance fieldInstance, Object _value, LifeCycleListener listener) {
    throw new UnsupportedOperationException();
  }

}
