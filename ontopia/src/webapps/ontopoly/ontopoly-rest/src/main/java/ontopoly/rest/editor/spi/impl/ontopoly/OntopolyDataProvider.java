package ontopoly.rest.editor.spi.impl.ontopoly;

import java.util.Collection;
import java.util.Collections;

import ontopoly.model.EditMode;
import ontopoly.model.FieldDefinition;
import ontopoly.model.FieldsView;
import ontopoly.model.RoleField;
import ontopoly.model.Topic;
import ontopoly.model.ViewModes;
import ontopoly.rest.editor.spi.PrestoChangeSet;
import ontopoly.rest.editor.spi.PrestoDataProvider;
import ontopoly.rest.editor.spi.PrestoFieldUsage;
import ontopoly.rest.editor.spi.PrestoTopic;
import ontopoly.rest.editor.spi.PrestoType;

public class OntopolyDataProvider implements PrestoDataProvider {

  OntopolySession session;
  
  public OntopolyDataProvider(OntopolySession session) {
    this.session = session;
  }

  public PrestoTopic getTopicById(String id) {
    Topic topic = session.getTopicMap().getTopicById(id);
    if (topic == null) {
      throw new RuntimeException("Unknown topic: " + id);
    }
    return new OntopolyTopic(session, topic);
  }

  public Collection<PrestoTopic> getAvailableFieldValues(PrestoFieldUsage field) {
    FieldDefinition fieldDefinition = FieldDefinition.getFieldDefinition(session.getTopicById(field.getId()), session.getTopicMap());
    
    if (fieldDefinition.getFieldType() == FieldDefinition.FIELD_TYPE_ROLE) {
      RoleField roleField = (RoleField)fieldDefinition;
      int arity = roleField.getAssociationField().getArity();

      if (arity == 2) {

        FieldsView fieldsView = OntopolyView.getWrapped(field.getView());
        FieldsView childView = fieldDefinition.getValueView(fieldsView);    

        EditMode editMode = roleField.getEditMode();
        ViewModes viewModes = fieldDefinition.getViewModes(childView);

        boolean allowAdd = !editMode.isNoEdit() && !editMode.isNewValuesOnly() && !viewModes.isReadOnly();

        for (RoleField otherRoleField : roleField.getOtherRoleFields()) {

          if (allowAdd) {
            return OntopolyTopic.wrap(session, otherRoleField.getAllowedPlayers(null));
          } else {
            return Collections.emptyList();          
          }
        }
      }
    }
    return Collections.emptyList();    
  }

  public PrestoChangeSet createTopic(PrestoType type) {
    return new OntopolyChangeSet(session, type);
  }
  
  public PrestoChangeSet updateTopic(PrestoTopic topic) {
    return new OntopolyChangeSet(session, topic);
  }

  public boolean removeTopic(PrestoTopic topic) {
    OntopolyTopic.getWrapped(topic).remove(null);
    return true;
  }

  public void close() {
    // no-op
  }

}
