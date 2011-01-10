package ontopoly.rest.editor.spi.impl.ontopoly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import net.ontopia.utils.ObjectUtils;
import ontopoly.model.AssociationField;
import ontopoly.model.FieldDefinition;
import ontopoly.model.RoleField;
import ontopoly.model.TopicType;
import ontopoly.model.RoleField.ValueIF;
import ontopoly.model.Topic;
import ontopoly.rest.editor.spi.PrestoChangeSet;
import ontopoly.rest.editor.spi.PrestoField;
import ontopoly.rest.editor.spi.PrestoTopic;
import ontopoly.rest.editor.spi.PrestoType;
import ontopoly.utils.TopicIdComparator;

public class OntopolyChangeSet implements PrestoChangeSet {

  private final OntopolySession session;
  
  private PrestoTopic topic;
  private final PrestoType type;
  
  private final List<Change> changes = new ArrayList<Change>();
  private boolean saved;
  
  OntopolyChangeSet(OntopolySession session, PrestoType type) {
    this.session = session;
    this.topic = null;
    this.type = type;
  }
  
  OntopolyChangeSet(OntopolySession session, PrestoTopic topic) {
    this.session = session;
    this.topic = topic;
    this.type = null;
  }
  
  public void setValues(PrestoField field, Collection<Object> values) {
    if (saved) {
      throw new RuntimeException("Can only save a changeset once.");
    }
    changes.add(new Change(Change.ChangeType.SET, field, values));
  }

  public void addValues(PrestoField field, Collection<Object> values) {
    if (saved) {
      throw new RuntimeException("Can only save a changeset once.");
    }
    changes.add(new Change(Change.ChangeType.ADD, field, values));
  }

  public void removeValues(PrestoField field, Collection<Object> values) {
    if (saved) {
      throw new RuntimeException("Can only save a changeset once.");
    }
    changes.add(new Change(Change.ChangeType.REMOVE, field, values));
  }

  public PrestoTopic save() {
    this.saved = true;
    
    if (topic == null) {
      if (type != null) {
        TopicType topicType = session.getTopicMap().getTopicTypeById(type.getId());
        topic = new OntopolyTopic(session, topicType.createInstance(null));
      } else {
        throw new RuntimeException("No topic and no type. I'm sorry, Dave. I'm afraid I can't do that.");
      }
    }
    
    Topic topic_ = OntopolyTopic.getWrapped(topic);
    for (Change change : changes) {
      
      String fieldId = change.getField().getId();
      FieldDefinition fieldDefinition = FieldDefinition.getFieldDefinition(fieldId, session.getTopicMap());
      
      Collection<Object> values = change.getValues();
      switch(change.getType()) {
      case SET:
        
        Collection<Object> existingValues = new HashSet<Object>(topic.getValues(change.getField()));          
        Collection<Object> removableValues = new HashSet<Object>(existingValues);          
        Collection<Object> addableValues = new HashSet<Object>();

        for (Object value : values) {
          removableValues.remove(value);
          if (!existingValues.contains(value)) {
            addableValues.add(value);
          }
        }
        
        if (!removableValues.isEmpty()) {
          removeValues(topic_, fieldDefinition, removableValues);
        }
        if (!addableValues.isEmpty()) {
          addValues(topic_, fieldDefinition, addableValues);
        }
        break;
      case ADD:
        if (!values.isEmpty()) {
          addValues(topic_, fieldDefinition, values);
        }
        break;
      case REMOVE:
        if (!values.isEmpty()) {
          removeValues(topic_, fieldDefinition, values);
        }
        break;
      };
    }
    return topic;
  }
  
  private static class Change {
    
    static enum ChangeType { SET, ADD, REMOVE };
    
    private ChangeType type;
    private final PrestoField field;
    private final Collection<Object> values;
    
    Change(ChangeType type, PrestoField field, Collection<Object> values) {
      this.type = type;
      this.field = field;
      this.values = values;      
    }
    
    public ChangeType getType() {
      return type;
    }
    
    public PrestoField getField() {
      return field;
    }
    public Collection<Object> getValues() {
      return values;
    }
  }

  private void addValues(Topic topic, FieldDefinition fieldDefinition, Collection<Object> values) {
    System.out.println("+V: " + fieldDefinition.getFieldName() + ": " + values);
    
    if (fieldDefinition.getFieldType() == FieldDefinition.FIELD_TYPE_ROLE) {
      // TODO: support unary associations with boolean value

      RoleField roleField = (RoleField)fieldDefinition;
      AssociationField associationField = roleField.getAssociationField();
      RoleFields roleFields =  new RoleFields(associationField);

      for (Object value : values) {
        Topic valueTopic = OntopolyTopic.getWrapped((PrestoTopic)value);
        RoleData roleData = getRoleFieldValue(topic, roleField, roleFields, valueTopic);
        fieldDefinition.addValue(topic, roleData.createValue(), null);
      }
    } else {
      for (Object value : values) {
        fieldDefinition.addValue(topic, value, null);
      }
    }
  }

  private void removeValues(Topic topic, FieldDefinition fieldDefinition, Collection<Object> values) {
    System.out.println("-V: " + fieldDefinition.getFieldName() + ": " + values);
    
    if (fieldDefinition.getFieldType() == FieldDefinition.FIELD_TYPE_ROLE) {
      // TODO: support unary associations with boolean value

      RoleField roleField = (RoleField)fieldDefinition;
      AssociationField associationField = roleField.getAssociationField();
      RoleFields roleFields =  new RoleFields(associationField);

      for (Object value : values) {
        Topic valueTopic = OntopolyTopic.getWrapped((PrestoTopic)value);
        RoleData roleData = getRoleFieldValue(topic, roleField, roleFields, valueTopic);
        fieldDefinition.removeValue(topic, roleData.createValue(), null);
      }
    } else {
      for (Object value : values) {
        fieldDefinition.removeValue(topic, value, null);
      }
    }
  }

  private static RoleData getRoleFieldValue(Topic topic,
      RoleField roleField, RoleFields roleFields, 
      Topic player) {

    int arity = roleFields.getArity();
    RoleField.ValueIF roleValue = RoleField.createValue(arity);
    roleValue.addPlayer(roleField, topic); 

    roleValue.addPlayer(roleField.getOtherRoleFields().iterator().next(), player);
    return roleFields.createData(roleValue.getRoleFields(), roleValue.getPlayers());
  }

  static class RoleFields {

    private final RoleField[] roleFields;

    public RoleFields(AssociationField associationField) {
      roleFields = new RoleField[associationField.getArity()];
      List<RoleField> fieldsForRoles = associationField.getFieldsForRoles();
      for (int i=0; i < fieldsForRoles.size(); i++) {
        roleFields[i] = fieldsForRoles.get(i);
      }
      Arrays.sort(roleFields, TopicIdComparator.INSTANCE);
    }  

    public int getArity() {
      return roleFields.length;
    }

    public RoleField getRoleFieldById(String id) {
      for (int i=0;i < roleFields.length; i++) {
        if (roleFields[i].getId().equals(id))
          return roleFields[i];
      }
      return null;
    }

    public RoleField[] getRoleFields() {
      return roleFields;
    }

    public RoleData createData(RoleField[] roles, Topic[] players) {
      Topic[] result = new Topic[players.length];
      for (int i=0; i < roles.length; i++) {
        int index = indexOf(roles[i]);
        result[index] = players[i];
      }
      return new RoleData(this, result);
    }

    private int indexOf(RoleField roleField) {
      for (int i=0; i < roleFields.length; i++) {
        if (roleFields[i].equals(roleField))
          return i;
      }
      return -1;
    }

  }

  static class RoleData {
    private final RoleFields roleFields;
    private final Topic[] players;
    private final int hashCode;

    public RoleData(RoleFields roleFields, Topic[] players) {
      this.roleFields = roleFields;
      this.players = players;
      int hc = 0;
      for (int i=0; i < players.length; i++) {
        hc += players[i].hashCode();
      }
      this.hashCode = hc;
    }

    public Topic getPlayer(int index) {
      return players[index];
    }

    public int hashCode() {
      return hashCode;
    }

    public boolean equals(Object o) {
      if (o instanceof RoleData) {
        RoleData other = (RoleData)o;
        if (this.players.length == other.players.length) {
          for (int i=0; i < this.players.length; i++) {
            if (!ObjectUtils.equals(this.players[i], other.players[i]))
              return false;
          }
          return true;
        }
      }
      return false;
    }

    public RoleField.ValueIF createValue() {
      RoleField[] fields = roleFields.getRoleFields();
      ValueIF value = RoleField.createValue(roleFields.getArity());
      for (int i=0; i < fields.length; i++) {
        value.addPlayer(fields[i], players[i]);
      }
      return value;
    }
  }

}
