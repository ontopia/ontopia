package ontopoly.rest.editor.spi.impl.ontopoly;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ontopoly.model.FieldDefinition;
import ontopoly.model.FieldsView;
import ontopoly.model.Topic;
import ontopoly.model.TopicMap;
import ontopoly.model.TopicType;
import ontopoly.rest.editor.spi.PrestoField;
import ontopoly.rest.editor.spi.PrestoSchemaProvider;
import ontopoly.rest.editor.spi.PrestoType;
import ontopoly.rest.editor.spi.PrestoView;

public class OntopolySchemaProvider implements PrestoSchemaProvider {

  OntopolySession session;
  
  public OntopolySchemaProvider(OntopolySession session) {
    this.session = session;
  }

  protected TopicMap getTopicMap() {
    return session.getTopicMap();
  }

  public String getDatabaseId() {
    return session.getDatabaseId();
  }
  
  public Collection<PrestoType> getRootTypes() {
    List<TopicType> rootTopicTypes = getTopicMap().getRootTopicTypes();
    List<PrestoType> result = new ArrayList<PrestoType>(rootTopicTypes.size());
    for (TopicType topicType : rootTopicTypes) {
      if (!topicType.isSystemTopic()) {
        result.add(new OntopolyType(session, topicType));
      }
    }
    return result;
  }

  public PrestoType getTypeById(String typeId) {
    TopicMap topicMap = getTopicMap();
    Topic topicType_ = topicMap.getTopicById(typeId);
    if (topicType_ == null && session.getStableIdPrefix() != null) {
      topicType_ = topicMap.getTopicById(session.getStableIdPrefix() + typeId);
    }
    if (topicType_ == null) {
      throw new RuntimeException("Unknown type: " + typeId);
    }
    TopicType topicType = new TopicType(topicType_.getTopicIF(), topicMap);
    return new OntopolyType(session, topicType);
  }

  public PrestoView getViewById(String viewId) {
    TopicMap topicMap = getTopicMap();
    Topic fieldsView_ = topicMap.getTopicById(viewId);
    if (fieldsView_ == null && session.getStableIdPrefix() != null) {
      fieldsView_ = topicMap.getTopicById(session.getStableIdPrefix() + viewId);
    }
    if (fieldsView_ == null) {
      throw new RuntimeException("Unknown view: " + viewId);
    }
    FieldsView fieldsView = new FieldsView(fieldsView_.getTopicIF(), topicMap);
    return new OntopolyView(session, fieldsView);
  }

  public PrestoField getFieldById(String fieldId, PrestoType type, PrestoView view) {
    return new OntopolyField(session, FieldDefinition.getFieldDefinition(session.getTopicById(fieldId), getTopicMap()), type, view);
  }

  public PrestoView getDefaultView() {
    return new OntopolyView(session, FieldsView.getDefaultFieldsView(getTopicMap()));
  }

}
