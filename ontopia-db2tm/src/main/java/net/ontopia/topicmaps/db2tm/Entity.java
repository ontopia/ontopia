/*
 * #!
 * Ontopia DB2TM
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

package net.ontopia.topicmaps.db2tm;

import java.util.ArrayList;
import java.util.List;

/**
 * INTERNAL: Relation mapping concept that represents topic or
 * association definitions. This class refer directly to the <topic>
 * and <association> elements in the XML schema. This class is used
 * only internally and is not intended to be used by end-users.
 */
public class Entity {

  // entity type enumeration
  public static final int TYPE_TOPIC = 1;
  public static final int TYPE_ASSOCIATION = 2;

  // entity type
  protected int etype;
  protected final Relation relation;
  protected Boolean primary;
  
  protected String id;
  protected ValueIF condition;
  protected String atype; // association type
  protected String[] types;  // topic types
  protected String[] scope;

  protected List<Field> ifields = new ArrayList<Field>();
  protected List<Field> cfields = new ArrayList<Field>();
  protected List<Field> rfields = new ArrayList<Field>();
  protected boolean requiresTopic;

  protected List<String> extents = new ArrayList<String>();
  
  Entity(int etype, Relation relation) {
    this.etype = etype;
    this.relation = relation;
  }

  public void compile() {
    for (Field field : ifields) {
      field.compile();
    }
    for (Field field : cfields) {
      field.compile();
    }
    for (Field field : rfields) {
      field.compile();
    }

    if (etype == TYPE_TOPIC) {
      this.requiresTopic = true;
    } else {
      // association entity require topic if there are subject
      // locator, subject identity or characteristics fields
      if (!cfields.isEmpty()) {
        this.requiresTopic = true;
      } else {
        for (int i=0; i < ifields.size(); i++) {
          int ftype = ifields.get(i).getFieldType();
          if (ftype == Field.TYPE_SUBJECT_LOCATOR ||
              ftype == Field.TYPE_SUBJECT_IDENTIFIER) {
            this.requiresTopic = true;
            break;
          }
        }
      }
    }
    // default to primary=true if both identity and characteristics/role fields
    if (primary == null) {
      if (!ifields.isEmpty() && (!cfields.isEmpty() || !rfields.isEmpty())) {
        primary = Boolean.TRUE;
      } else {
        primary = Boolean.FALSE;
      }      
    } else {
      // complain if <topic primary="false"> with characteristics and synctype is changelog
      if (etype == TYPE_TOPIC && primary == Boolean.FALSE && !ifields.isEmpty() && (!cfields.isEmpty() || !rfields.isEmpty())) {
        int synctype = relation.getSynchronizationType();
        if (synctype == Relation.SYNCHRONIZATION_UNKNOWN) {
          if (!relation.getSyncs().isEmpty()) {
            synctype = Relation.SYNCHRONIZATION_CHANGELOG;
          } else {
            synctype = Relation.SYNCHRONIZATION_RESCAN;
          }
        }
      }
    }
  }

  public Relation getRelation() {
    return relation;
  }
  
  public int getEntityType() {
    return etype;
  }

  public void setEntityType(int etype) {
    this.etype = etype;
  }

  public boolean isPrimary() {
    return primary.booleanValue();
  }

  public void setPrimary(Boolean primary) {
    this.primary = primary;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;    
  }

  public ValueIF getConditionValue() {
    return condition;
  }
  
  public void setConditionValue(ValueIF condition) {
    this.condition = condition;
  }

  public String getAssociationType() {
    return atype;
  }

  public void setAssociationType(String atype) {
    this.atype = atype;
  }

  public String[] getTypes() {
    return types;
  }

  public void setTypes(String[] types) {
    this.types = types;
  }

  public String[] getScope() {
    return scope;
  }

  public void setScope(String[] scope) {
    this.scope = scope;
  }

  public List<Field> getCharacteristicFields() {
    return cfields;
  }

  public List<Field> getIdentityFields() {
    return ifields;
  }

  public List<Field> getRoleFields() {
    return rfields;
  }

  public void addField(Field field) {
    switch (field.getFieldType()) {
    case Field.TYPE_SUBJECT_LOCATOR:
    case Field.TYPE_SUBJECT_IDENTIFIER:
    case Field.TYPE_ITEM_IDENTIFIER:
      this.ifields.add(field);
      break;
    case Field.TYPE_ASSOCIATION_ROLE:
      this.rfields.add(field);
      break;
    default:
      this.cfields.add(field);
    }
  }

  // true if the relation maps to a topic (ie: it's a topic relation,
  // or some other relation that requires reification)
  public boolean requiresTopic() {
    return requiresTopic;
  }

  // -----------------------------------------------------------------------------
  // Extents
  // -----------------------------------------------------------------------------

  public List<String> getExtentQueries() {
    return extents;
  }

  public void addExtentQuery(String extentQuery) {
    if (extentQuery == null) {
      throw new DB2TMConfigException("Extent query cannot be null (entity " + this + ").");
    }
    extents.add(extentQuery);
  }

  public void removeExtentQuery(String extentQuery) {
    extents.remove(extentQuery);
  }

}
