package ontopoly.rest.editor.spi.impl.ontopoly;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ontopoly.model.FieldAssignment;
import ontopoly.model.FieldDefinition;
import ontopoly.model.FieldsView;
import ontopoly.model.Topic;
import ontopoly.model.TopicMap;
import ontopoly.model.TopicType;
import ontopoly.rest.editor.spi.PrestoField;
import ontopoly.rest.editor.spi.PrestoFieldUsage;
import ontopoly.rest.editor.spi.PrestoSchemaProvider;
import ontopoly.rest.editor.spi.PrestoType;
import ontopoly.rest.editor.spi.PrestoView;

public class OntopolyType implements PrestoType {

  private final OntopolySession session;
  private final TopicType topicType;

  OntopolyType(OntopolySession session, TopicType topicType) {
    this.session = session;
    this.topicType = topicType;
  }
  
  static TopicType getWrapped(PrestoType type) {
    return ((OntopolyType)type).topicType;
  }

  public String getId() {
    return session.getStableId(topicType);
  }

  public PrestoSchemaProvider getSchemaProvider() {
    return session.getSchemaProvider();
  }

  public String getName() {
    return topicType.getName();
  }

  public boolean isAbstract() {
    return topicType.isAbstract();
  }

  public Collection<PrestoType> getDirectSubTypes() {
    return wrap(session, topicType.getDirectSubTypes());
  }

  static Collection<PrestoType> wrap(OntopolySession session, Collection<TopicType> topicTypes) {
    List<PrestoType> result = new ArrayList<PrestoType>(topicTypes.size());
    for (TopicType topicType : topicTypes) {
      result.add(new OntopolyType(session, topicType));
    }
    return result;
  }

  public List<PrestoField> getFields() {
    PrestoView view = null;
    FieldsView fieldsView = null;
    List<FieldAssignment> fieldAssignments = topicType.getFieldAssignments(fieldsView);
    List<PrestoField> result = new ArrayList<PrestoField>(fieldAssignments.size());
    for (FieldAssignment fieldAssignment : fieldAssignments) {
      FieldDefinition fieldDefinition = fieldAssignment.getFieldDefinition();
      result.add(new OntopolyField(session, fieldDefinition, this, view));
    }
    return result;
  }
  
  public List<PrestoFieldUsage> getFields(PrestoView view) {
    FieldsView fieldsView = OntopolyView.getWrapped(view);
    List<FieldAssignment> fieldAssignments = topicType.getFieldAssignments(fieldsView);
    List<PrestoFieldUsage> result = new ArrayList<PrestoFieldUsage>(fieldAssignments.size());
    for (FieldAssignment fieldAssignment : fieldAssignments) {
      FieldDefinition fieldDefinition = fieldAssignment.getFieldDefinition();
      result.add(new OntopolyField(session, fieldDefinition, this, view));
    }
    return result;
  }

  public PrestoFieldUsage getFieldById(String fieldId, PrestoView view) {
    return new OntopolyField(session, FieldDefinition.getFieldDefinition(session.getTopicById(fieldId), session.getTopicMap()), this, view);
  }

  public PrestoView getDefaultView() {
    return new OntopolyView(session, FieldsView.getDefaultFieldsView(session.getTopicMap()));
  }

  public PrestoView getViewById(String viewId) {
    TopicMap topicMap = session.getTopicMap();
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
  
  public Collection<PrestoView> getViews(PrestoView view) {
    FieldsView fieldsView = OntopolyView.getWrapped(view);
    
    return OntopolyView.wrap(session, topicType.getFieldViews(fieldsView));
  }

}
