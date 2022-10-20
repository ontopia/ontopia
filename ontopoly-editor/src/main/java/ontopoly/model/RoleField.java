/*
 * #!
 * Ontopoly Editor
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package ontopoly.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import ontopoly.utils.OntopolyModelUtils;
import ontopoly.utils.Ordering;
import ontopoly.utils.TopicComparator;
import org.apache.commons.lang3.StringUtils;

/**
 * Represents a role field.
 */
public class RoleField extends FieldDefinition {
  private AssociationField associationField;
  private RoleType roleType;

  public RoleField(TopicIF topic, TopicMap tm) {
    this(topic, tm, null, null);
  }

  public RoleField(TopicIF topic, TopicMap tm, RoleType roleType, AssociationField associationField) {
    super(topic, tm);

    this.associationField = associationField;
    this.roleType = roleType;
  }

  @Override
  public int getFieldType() {
    return FIELD_TYPE_ROLE;
  }

  @Override
  public String getFieldName() {
    String name = getTopicMap().getTopicName(getTopicIF(), null);
    if (name != null) {
      return name;
    }

    AssociationType atype = getAssociationType();
    RoleType rtype = getRoleType();
    return (atype == null ? "" : atype.getName()) + " (" + (rtype == null ? "" : rtype.getName()) + ")";
  }

  @Override
  public LocatorIF getLocator() {
    return PSI.ON_ROLE_FIELD;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof RoleField)) {
      return false;
    }

    RoleField other = (RoleField)obj;
    return (getTopicIF().equals(other.getTopicIF()));
  }

  public boolean isSortable() {
    TopicMap tm = getTopicMap();
    TopicIF aType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "is-sortable-field");
    TopicIF rType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "field-definition");
    TopicIF player = getTopicIF();
    return OntopolyModelUtils.isUnaryPlayer(tm, aType, player, rType);
  }

  public EditMode getEditMode() {    
    TopicIF editModeIf = OntopolyModelUtils.findBinaryPlayer(getTopicMap(), 
        PSI.ON_USE_EDIT_MODE, getTopicIF(), PSI.ON_FIELD_DEFINITION, PSI.ON_EDIT_MODE);
    return (editModeIf == null ? EditMode.getDefaultEditMode(getTopicMap()) : new EditMode(editModeIf, getTopicMap()));
  }

  public CreateAction getCreateAction() {
    TopicIF createActionIf = OntopolyModelUtils.findBinaryPlayer(getTopicMap(), 
        PSI.ON_USE_CREATE_ACTION, getTopicIF(), PSI.ON_FIELD_DEFINITION, PSI.ON_CREATE_ACTION);
    return (createActionIf == null ? CreateAction.getDefaultCreateAction(getTopicMap()) : new CreateAction(createActionIf, getTopicMap()));
  }

  /**
   * Gets the association type.
   * 
   * @return the association type.
   */
  public AssociationType getAssociationType() {
    AssociationField afield = getAssociationField();
    return (afield == null ? null : getAssociationField().getAssociationType());
  }

  /**
   * Gets the role type.
   * 
   * @return the role type.
   */
  public RoleType getRoleType() {
    if (roleType == null) {
      TopicIF roleTypeIf = OntopolyModelUtils.findBinaryPlayer(getTopicMap(), 
          PSI.ON_HAS_ROLE_TYPE, getTopicIF(), PSI.ON_ROLE_FIELD, PSI.ON_ROLE_TYPE);
      this.roleType = (roleTypeIf == null ? null : new RoleType(roleTypeIf, getTopicMap()));      
    }
    return roleType;
  }

  public AssociationField getAssociationField() {
    if (associationField == null) {
      TopicIF associationFieldIf = OntopolyModelUtils.findBinaryPlayer(getTopicMap(), 
          PSI.ON_HAS_ASSOCIATION_FIELD, getTopicIF(), PSI.ON_ROLE_FIELD, PSI.ON_ASSOCIATION_FIELD);
      this.associationField = (associationFieldIf == null ? null : new AssociationField(associationFieldIf, getTopicMap()));
    }
    return associationField;
  }

  /**
   * Gets the other RoleField objects this object's association type topic takes part in.
   * 
   * @return the other RoleField objects this object's association type topic takes part in.
   */
  public Collection<RoleField> getFieldsForOtherRoles() {
    AssociationField afield = getAssociationField();
    Collection<RoleField> fields = afield.getFieldsForRoles();
    List<RoleField> ofields = new ArrayList<RoleField>(fields);
    ofields.remove(this);
    return ofields;
  }

  /**
   * Gets the interface control assigned for this association field. If no interface control object is assigned, the
   * method will return the default interface control, which is drop-down-list.
   * 
   * @return the interface control assigned to this association field. 
   */
  public InterfaceControl getInterfaceControl() {
    TopicIF interfaceControlIf = OntopolyModelUtils.findBinaryPlayer(getTopicMap(), 
        PSI.ON_USE_INTERFACE_CONTROL, getTopicIF(), PSI.ON_FIELD_DEFINITION, PSI.ON_INTERFACE_CONTROL);
    return interfaceControlIf == null ? InterfaceControl.getDefaultInterfaceControl(getTopicMap()) : new InterfaceControl(interfaceControlIf, getTopicMap());
  }

  /**
   * Gets the topic types that have been declared as valid and which
   * may play the other roles in this association type.
   * 
   * @return the topic types which may play the other roles in this association type.
   */
  public Collection<TopicType> getDeclaredPlayerTypes() {
    String query = "select $ttype from on:has-field(%FD% : on:field-definition, $ttype : on:field-owner)?";

    Map<String,TopicIF> params = Collections.singletonMap("FD", getTopicIF());

    QueryMapper<TopicType> qm = getTopicMap().newQueryMapper(TopicType.class);
    return qm.queryForList(query, params);
  }

  public Collection<TopicType> getAllowedPlayerTypes(Topic currentTopic) {
    String query = getAllowedPlayersTypesQuery();
    if (query == null) {
      query = "subclasses-of($SUP, $SUB) :- { "
        + "xtm:superclass-subclass($SUP : xtm:superclass, $SUB : xtm:subclass) | "
        + "xtm:superclass-subclass($SUP : xtm:superclass, $MID : xtm:subclass), "
        + "subclasses-of($MID, $SUB) }. ";
      query += "select $avtype from "
        + "on:has-field(%field% : on:field-definition, $ttype : on:field-owner), "
        + "{ $avtype = $ttype | subclasses-of($ttype, $avtype) }, "
        + "not(on:is-abstract($avtype : on:topic-type)) "
        + "order by $avtype?";
    }

    Map<String,TopicIF> params = new HashMap<String,TopicIF>(2);
    params.put("field", getTopicIF());
    if (currentTopic != null) {
      params.put("topic", currentTopic.getTopicIF());
    }

    QueryMapper<TopicType> qm = getTopicMap().newQueryMapper(TopicType.class);
    return qm.queryForList(query, params);
  }

  private String getAllowedPlayersQuery() {
    TopicIF topicIf = getTopicIF();   
    TopicIF typeIf = OntopolyModelUtils.getTopicIF(getTopicMap(), PSI.ON, "allowed-players-query");
    OccurrenceIF occ = OntopolyModelUtils.findOccurrence(typeIf, topicIf);
    return (occ == null ? null : occ.getValue());
  }

  private String getAllowedPlayersSearchQuery() {
    TopicIF topicIf = getTopicIF();   
    TopicIF typeIf = OntopolyModelUtils.getTopicIF(getTopicMap(), PSI.ON, "allowed-players-search-query");
    OccurrenceIF occ = OntopolyModelUtils.findOccurrence(typeIf, topicIf);
    return (occ == null ? null : occ.getValue());
  }

  private String getAllowedPlayersTypesQuery() {
    TopicIF topicIf = getTopicIF();   
    TopicIF typeIf = OntopolyModelUtils.getTopicIF(getTopicMap(), PSI.ON, "allowed-players-types-query");
    OccurrenceIF occ = OntopolyModelUtils.findOccurrence(typeIf, topicIf);
    return (occ == null ? null : occ.getValue());
  }

  public List<Topic> getAllowedPlayers() {
    return getAllowedPlayers(null);
  }
  
  public List<Topic> getAllowedPlayers(Topic currentTopic) {

    String query = getAllowedPlayersQuery();
    if (query == null) {
      query = "select $instance from " +
        "on:has-field(%field% : on:field-definition, $ttype : on:field-owner), " +
        "instance-of($instance, $ttype) order by $instance?";
    } 
    Map<String,TopicIF> params = new HashMap<String,TopicIF>(2);
    params.put("field", getTopicIF());
    if (currentTopic != null) {
      params.put("topic", currentTopic.getTopicIF());
    }
    
    QueryMapper<Topic> qm = getTopicMap().newQueryMapper(Topic.class);
    List<Topic> result = qm.queryForList(query, params);
    Collections.sort(result, TopicComparator.INSTANCE);
    return result;
  }

  /**
   * Search for the topics that match the given search term. Only topics of allowed
   * player types are returned.
   * 
   * @param searchTerm the search term used to search for topics.
   * @return a collection of Topic objects
   */
  public List<Topic> searchAllowedPlayers(String searchTerm) {
    try {
      String query = getAllowedPlayersSearchQuery();
      if (query == null) {
        query = "select $player, $score from "
          + "on:has-field(%field% : on:field-definition, $ttype : on:field-owner), "
          + "instance-of($player, $ttype), "
          + "topic-name($player, $tn), value-like($tn, %search%, $score) "
          + "order by $score desc, $player?";
      }

      Map<String,Object> params = new HashMap<String,Object>(2);
      params.put("field", getTopicIF());
      params.put("search", searchTerm);

      QueryMapper<TopicIF> qm = getTopicMap().newQueryMapperNoWrap();         
      Collection<TopicIF> rows = qm.queryForList(query, params);

      Iterator<TopicIF> it = rows.iterator();
      List<Topic> results = new ArrayList<Topic>(rows.size());
      Collection<TopicIF> duplicateChecks = new HashSet<TopicIF>(rows.size());

      while (it.hasNext()) {
        TopicIF topic = it.next();
        if (duplicateChecks.contains(topic)) {
          continue; // avoid duplicates
        }
        results.add(new Topic(topic, getTopicMap()));
        duplicateChecks.add(topic);
      } 
      return results;
    } catch (Exception e) {
      return Collections.emptyList();
    }
  }

  /**
   * Gets the instance topics on the other side of an association an instance topic takes part in.
   * 
   * @param topic the instance topic that takes part in the association.
   * @return the instance topics on the other side of an association an instance topic takes part in.
   */
  @Override
  public List<ValueIF> getValues(Topic topic) { 
    Collection<AssociationRoleIF> roles = getRoles(topic);

    List<ValueIF> result = new ArrayList<ValueIF>(roles.size());
    Iterator<AssociationRoleIF> iter = roles.iterator();
    while (iter.hasNext()) {
      AssociationRoleIF role = iter.next();
      ValueIF value = createValue(this, role);
      if (value != null) {
        result.add(value);
      }
    }
    return result;
  }

  private Collection<AssociationRoleIF> getRoles(Topic topic) {
    AssociationType atype = getAssociationType();
    if (atype == null) {
      return Collections.emptySet();
    }
    TopicIF associationTypeIf = atype.getTopicIF();

    RoleType rtype = getRoleType();
    if (rtype == null) {
      return Collections.emptySet();
    }
    TopicIF roleTypeIf = rtype.getTopicIF();

    TopicIF playerIf = topic.getTopicIF();
    Collection<TopicIF> scope = Collections.emptySet();

    return OntopolyModelUtils.findRoles(associationTypeIf, roleTypeIf, playerIf, scope);    
  }

  public List<ValueIF> getOrderedValues(Topic topic, RoleField ofield) { 
    List<ValueIF> values = getValues(topic);
    if (values.size() > 1) {
      Map<Topic,OccurrenceIF> topics_occs = getValuesWithOrdering(topic);
      Collections.sort(values, new MapValueComparator(topics_occs, ofield, topic));
    }
    return values;
  }

  private static class MapValueComparator implements Comparator<ValueIF> {
    private Map<Topic, OccurrenceIF> entries;
    private RoleField ofield;
    private Topic oplayer;
    MapValueComparator(Map<Topic,OccurrenceIF> entries, RoleField ofield, Topic oplayer) {
      this.entries = entries;
      this.ofield = ofield;
      this.oplayer = oplayer;
    }
    @Override
    public int compare(ValueIF v1, ValueIF v2) {
      try {
        Topic p1 = v1.getPlayer(ofield, oplayer);
        Topic p2 = v2.getPlayer(ofield, oplayer);
        OccurrenceIF oc1 = entries.get(p1);
        OccurrenceIF oc2 = entries.get(p2);
        return StringUtils.compare(oc1 == null ? null : oc1.getValue(), oc2 == null ? null : oc2.getValue());
      } catch (Exception e) {
        // should not fail when comparing. bergen kommune has had an issue where this happens. we thus ignore for now.
        //        e.printStackTrace();
        return 0;
      }
    }
  }

  private Map<Topic,OccurrenceIF> getValuesWithOrdering(Topic topic) {

    TopicIF topicIf = topic.getTopicIF();   
    TopicIF typeIf = OntopolyModelUtils.getTopicIF(topic.getTopicMap(), PSI.ON, "field-value-order");
    LocatorIF datatype = DataTypes.TYPE_STRING;

    TopicIF fieldDefinitionIf = getTopicIF();

    Map<Topic,OccurrenceIF> topics_occs = new HashMap<Topic,OccurrenceIF>();
    Iterator<OccurrenceIF> iter = OntopolyModelUtils.findOccurrences(typeIf, topicIf, datatype).iterator();
    while (iter.hasNext()) {
      OccurrenceIF occ = iter.next();
      Collection<TopicIF> scope = occ.getScope();
      if (scope.size() == 2 && scope.contains(fieldDefinitionIf)) { // note: this is value ordering
        Iterator<TopicIF> siter = scope.iterator();
        while (siter.hasNext()) {
          TopicIF theme = siter.next();
          if (!theme.equals(fieldDefinitionIf)) {
            // FIXME: if map already contains key, we might want to delete occ
            topics_occs.put(new Topic(theme, topic.getTopicMap()), occ);
            break;
          }
        }
      }
    }
    return topics_occs;
  }

  /**
   * Adds an instance topic to the other side of an association an instance topic takes part in.
   * 
   * @param topic the instance topic that takes part in the association.
   * @param _value an object representing the instance topic that will be added to the other
   * side of the association the instance topic (topic) takes part in.
   */
  @Override
  public void addValue(Topic topic, Object _value, LifeCycleListener listener) {
    ValueIF value = (ValueIF) _value;

    AssociationType atype = getAssociationType();
    if (atype == null) {
      return;
    }
    TopicIF atypeIf = atype.getTopicIF();
    TopicIF[] rtypes = getRoleTypes(value);
    TopicIF[] players = getPlayers(value);
    Collection<TopicIF> scope = Collections.emptySet();      

    // if cardinality is 0:1 or 1:1 then clear existing values
    if (getCardinality().isMaxOne()) {      
      // remove all existing values
      ValueIF existingValue = null;
      Collection<AssociationRoleIF> roles = getRoles(topic);
      boolean replaceValues = roles.size() == 1;
      Iterator<AssociationRoleIF> iter = roles.iterator();
      while (iter.hasNext()) {
        AssociationRoleIF role = iter.next();
        ValueIF valueIf = createValue(this, role);
        if (valueIf == null) {
          continue;
        } if (valueIf.equals(value)) {
          existingValue = valueIf;
        } else if (replaceValues) {
          // issue-204: only replace values if there is just a single value 
          removeValue(topic, valueIf, listener);
        }
      }

      // create new
      if (existingValue == null) {
        OntopolyModelUtils.makeAssociation(atypeIf, rtypes, players, scope);
      }
    } else {
      Collection<AssociationIF> assocs = OntopolyModelUtils.findAssociations(atypeIf, rtypes, players, scope);

      if (assocs.isEmpty()) {
        // create new
        OntopolyModelUtils.makeAssociation(atypeIf, rtypes, players, scope);
      } else {
        // remove all except the first one
        Iterator<AssociationIF> iter = assocs.iterator();
        iter.next();
        while (iter.hasNext()) {
          AssociationIF assoc = iter.next();
          assoc.remove();
        }
      }
    }
    if (listener != null) {
      listener.onAfterAdd(topic, this, value);
    }
  }

  //  protected void clear(FieldInstance fieldInstance, LifeCycleListener listener) {
  //    Collection roles = getRoles(fieldInstance.getInstance());
  //    Iterator iter = roles.iterator();
  //    while (iter.hasNext()) {
  //      AssociationRoleIF role = (AssociationRoleIF)iter.next();
  //      ValueIF valueIf = createValue(this, role);
  //      removeValue(fieldInstance, valueIf, listener);
  //    }
  //  }

  /**
   * Removes an instance topic from the other side of an association an instance topic takes part in.
   * 
   * @param _value an object representing the instance topic that will be removed from the other
   * side of the association the instance topic (topic) takes part in.
   */  
  @Override
  public void removeValue(Topic topic, Object _value, LifeCycleListener listener) {
    ValueIF value = (ValueIF) _value;

    AssociationType atype = getAssociationType();
    if (atype == null) {
      return;
    }
    TopicIF atypeIf = atype.getTopicIF();
    TopicIF[] rtypes = getRoleTypes(value);
    TopicIF[] players = getPlayers(value);

    if (listener != null) {
      listener.onBeforeRemove(topic, this, value);
    }

    Collection<TopicIF> scope = Collections.emptySet();          
    Collection<AssociationIF> assocs = OntopolyModelUtils.findAssociations(atypeIf, rtypes, players, scope);

    if (!assocs.isEmpty()) {
      // remove all the matching
      Iterator<AssociationIF> iter = assocs.iterator();
      while (iter.hasNext()) {
        AssociationIF assoc = iter.next();
        assoc.remove();
      }
    }
    // TODO: consider removing field value order also
  }

  /**
   * Factory method for creating a ValueIF object, which represent an instance topic on one side of an association.
   * 
   * @param roleField the role field containing the association type and the role type representing another side of the association.
   * @param role the role type on the side of the association that the instance topic is going to be created.
   * @return the ValueIF object that represent an instance topic on one side of an association. Will return null if role does not match role field definition.
   */
  private static ValueIF createValue(RoleField roleField, AssociationRoleIF role) {
    Collection<RoleField> fields = roleField.getAssociationField().getFieldsForRoles();
    int fieldCount = fields.size();

    TopicMap topicMap = roleField.getTopicMap();
    AssociationIF assoc = role.getAssociation();

    // ignore roles where the arity does not match
    Collection<AssociationRoleIF> aroles = assoc.getRoles();
    if (fieldCount != aroles.size()) {
      return null;
    }

    ValueIF value = createValue(fieldCount);
    value.addPlayer(roleField, new Topic(role.getPlayer(), roleField.getTopicMap()));

    Object[] roles = aroles.toArray();
    Collection<AssociationRoleIF> matched = new HashSet<AssociationRoleIF>(roles.length);
    matched.add(role);

    int selfMatch = 0;
    Iterator<RoleField> iter = fields.iterator();
    while (iter.hasNext()) {
      RoleField ofield = iter.next();
      // only match your own field once
      if (ofield.equals(roleField)) {
        if (++selfMatch == 1) {
          continue;
        }
      }
      RoleType ortype = ofield.getRoleType();
      if (ortype == null) {
        return null;
      }
      boolean match = false;
      for (int i = 0; i < roles.length; i++) {
        AssociationRoleIF orole = (AssociationRoleIF) roles[i];
        if (matched.contains(orole)) {
          continue;
        }
        if (Objects.equals(orole.getType(), ortype.getTopicIF())) {
          matched.add(orole);
          value.addPlayer(ofield, new Topic(orole.getPlayer(), topicMap));
          match = true;
        }
      }
      if (!match) {
        return null;
      }
    }
    return value;
  }

  /**
   * Factory method for creating a ValueIF object, which represent an instance topic on one side of an association.
   * 
   * @param arity the number of players that the association value should have.
   * @return the ValueIF object that represent an instance topic on one side of an association.
   */
  public static ValueIF createValue(int arity) {
    return new Value(arity);
  }

  /**
   * Interface. This interface is implemented by the Value class.
   */
  public interface ValueIF {

    int getArity();

    RoleField[] getRoleFields();

    Topic[] getPlayers();

    void addPlayer(RoleField roleField, Topic player);

    Topic getPlayer(RoleField roleField, Topic oplayer);

  }

  /**
   * Static inner class containing a Map object, which connects
   * instance topics to associations.
   */
  private static class Value implements RoleField.ValueIF {

    private int offset;
    private RoleField[] roleFields;
    private Topic[] players;

    Value(int arity) {
      this.roleFields = new RoleField[arity];
      this.players = new Topic[arity];
    }

    @Override
    public int getArity() {
      return roleFields.length;
    }

    @Override
    public RoleField[] getRoleFields() {
      return roleFields;
    }

    @Override
    public Topic[] getPlayers() {
      return players;
    }

    @Override
    public void addPlayer(RoleField roleField, Topic player) {
      roleFields[offset] = roleField;
      players[offset] = player;
      offset++;
    }

    @Override
    public Topic getPlayer(RoleField ofield, Topic oPlayer) {
      // NOTE: all this logic is here to cater for symmetric associations
      Topic xPlayer = null;
      for (int i=0; i < roleFields.length; i++) {
        RoleField rf = roleFields[i];
        if (rf.equals(ofield)) {
          Topic player = players[i];
          if (!Objects.equals(player, oPlayer)) {
            return player;
          } else {
            xPlayer = oPlayer;
          }
        }
      }
      if (xPlayer == null) {
        throw new RuntimeException("Could not find player for RoleField: " + ofield + " (" + oPlayer + ")");
      } else {
        return xPlayer;
      }
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("ValueIF(");
      sb.append(getArity());
      sb.append(": ");
      for (int i=0; i < roleFields.length; i++) {
        if (i > 0) {
          sb.append(", ");
        }
        if (roleFields[i] == null) {
          sb.append("null");
        } else {
          RoleType rtype = roleFields[i].getRoleType();
          sb.append((rtype == null ? null : rtype.getTopicIF()));
        }
        sb.append(":");
        if (players[i] == null) {
          sb.append("null");
        } else {
          sb.append(players[i].getTopicIF());
        }
      }
      sb.append(")");
      return sb.toString();
    }
  }

  private TopicIF[] getRoleTypes(ValueIF value) {
    RoleField[] roleFields = value.getRoleFields();
    int arity = value.getArity();
    TopicIF[] rtypes = new TopicIF[arity];
    for (int i=0; i < arity; i++) {
      rtypes[i] = roleFields[i].getRoleType().getTopicIF();
    }
    return rtypes;
  }

  private TopicIF[] getPlayers(ValueIF value) {
    Topic[] players = value.getPlayers();
    int arity = value.getArity();		
    TopicIF[] topics = new TopicIF[arity];
    for (int i=0; i < arity; i++) {
      topics[i] = players[i].getTopicIF();
    }
    return topics;
  }

  private Collection<Topic> getValues(Topic instance, RoleField ofield) {
    Collection<ValueIF> values = getValues(instance);
    Collection<Topic> result = new HashSet<Topic>(values.size());
    Iterator<ValueIF> iter = values.iterator();
    while (iter.hasNext()) {
      ValueIF rfv = iter.next();
      Topic player = rfv.getPlayer(ofield, instance);
      result.add(player);
    }
    return result;
  }

  /**
   * Change field value order so that the first value is ordered directly after the second value.
   **/
  public void moveAfter(Topic instance, RoleField ofield, RoleField.ValueIF rfv1, RoleField.ValueIF rfv2) {
    Topic p1 = rfv1.getPlayer(ofield, instance);
    Topic p2 = rfv2.getPlayer(ofield, instance);

    TopicIF typeIf = OntopolyModelUtils.getTopicIF(instance.getTopicMap(), PSI.ON, "field-value-order");
    LocatorIF datatype = DataTypes.TYPE_STRING;
    TopicIF fieldDefinitionIf = getTopicIF();

    TopicIF topicIf = instance.getTopicIF();
    TopicIF p1topic = p1.getTopicIF();
    TopicIF p2topic = p2.getTopicIF();

    Collection<Topic> alltopics = getValues(instance, ofield);

    Map<Topic,OccurrenceIF> topics_occs = getValuesWithOrdering(instance);

    List<OccurrenceIF> occs = new ArrayList<OccurrenceIF>(topics_occs.values());
    Collections.sort(occs, new Comparator<OccurrenceIF>() {
      @Override
      public int compare(OccurrenceIF occ1, OccurrenceIF occ2) {
        return StringUtils.compare(occ1.getValue(), occ2.getValue());
      }
    });

    TopicMapBuilderIF builder = topicIf.getTopicMap().getBuilder();

    OccurrenceIF maxOcc = (occs.isEmpty() ? null : occs.get(occs.size()-1));
    int fieldOrderMax = (maxOcc == null ? 0 : Ordering.stringToOrder(maxOcc.getValue()));

    // make sure this value has an order value
    OccurrenceIF p1occ = null;
    OccurrenceIF p2occ = topics_occs.get(p2);
    OccurrenceIF next_occ = null;
    int fieldOrderP2;
    int nextOrder = Ordering.MAX_ORDER;
    if (p2occ == null) {
      fieldOrderP2 = (fieldOrderMax == 0 ? 0 : fieldOrderMax + Ordering.ORDER_INCREMENTS);
      p2occ = builder.makeOccurrence(topicIf, typeIf, Ordering.orderToString(fieldOrderP2), datatype);
      p2occ.addTheme(fieldDefinitionIf);
      p2occ.addTheme(p2topic);
    } else {
      fieldOrderP2 = Ordering.stringToOrder(p2occ.getValue());
      // find occurrence after p2occ
      int indexP2occ = occs.indexOf(p2occ);
      if (indexP2occ < (occs.size()-1)) {
        next_occ = occs.get(indexP2occ+1);
      }
      if (next_occ != null) {
        // if next then average this and next field orders
        int fieldOrderNext = Ordering.stringToOrder(next_occ.getValue());
        nextOrder = (fieldOrderP2 + fieldOrderNext)/2;
        if (nextOrder != fieldOrderP2) {
          p1occ = topics_occs.get(p1);
          if (p1occ != null) {
            p1occ.setValue(Ordering.orderToString(nextOrder));
          } else {
            p1occ = builder.makeOccurrence(topicIf, typeIf, Ordering.orderToString(nextOrder), datatype);
            p1occ.addTheme(fieldDefinitionIf);
            p1occ.addTheme(p1topic);
          }
        }
      }
    }
    if (nextOrder == Ordering.MAX_ORDER) {
      nextOrder = fieldOrderP2;
    }
    if (p1occ == null) {
      nextOrder += Ordering.ORDER_INCREMENTS;
      p1occ = topics_occs.get(p1);
      if (p1occ != null) {
        p1occ.setValue(Ordering.orderToString(nextOrder));
      } else {
        p1occ = builder.makeOccurrence(topicIf, typeIf, Ordering.orderToString(nextOrder), datatype);
        p1occ.addTheme(fieldDefinitionIf);
        p1occ.addTheme(p1topic);
      }

      // we need to reshuffle all existing orders after p2
      int indexP2occ = occs.indexOf(p2occ);
      if (indexP2occ > 0) {
        for (int i=indexP2occ+1; i < occs.size(); i++) {
          OccurrenceIF occ = occs.get(i);
          nextOrder += Ordering.ORDER_INCREMENTS;
          occ.setValue(Ordering.orderToString(nextOrder));      
        }
      }
    }
    // assign ordering to all topics with no existing ordering
    alltopics.remove(p1);
    alltopics.remove(p2);
    Iterator<Topic> aiter = alltopics.iterator();
    while (aiter.hasNext()) {
      Topic atopic = aiter.next();
      if (!topics_occs.containsKey(atopic)) {
        nextOrder += Ordering.ORDER_INCREMENTS;
        OccurrenceIF occ = builder.makeOccurrence(topicIf, typeIf, Ordering.orderToString(nextOrder), datatype);
        occ.addTheme(fieldDefinitionIf);
        occ.addTheme(atopic.getTopicIF());
      }
    }
  }

  public Collection<RoleField> getOtherRoleFields() {
    AssociationField associationField = getAssociationField();
    List<RoleField> roleFields = associationField.getFieldsForRoles();
    List<RoleField> result = new ArrayList<RoleField>(roleFields.size());
    for (RoleField roleField : roleFields) {
      if (!roleField.equals(this)) {
        result.add(roleField);
      }
    }
    return result;
  }

}
