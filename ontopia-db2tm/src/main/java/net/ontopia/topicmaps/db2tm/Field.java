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
 * INTERNAL: Relation mapping concept that refers to a field
 * definition belonging to an entity. There are two categories of
 * fields: identity fields and characteristic fields.
 */
public class Field {

  // field type enumeration
  public static final int TYPE_SUBJECT_LOCATOR = 1;
  public static final int TYPE_SUBJECT_IDENTIFIER = 2;
  public static final int TYPE_ITEM_IDENTIFIER = 4;
  public static final int TYPE_TOPIC_NAME = 8;
  public static final int TYPE_OCCURRENCE = 16;
  public static final int TYPE_PLAYER = 32;
  public static final int TYPE_ASSOCIATION_ROLE = 64;

  // field type
  protected int ftype;
  protected Entity entity;
  
  // subject-locator, subject-identifier, item-identifier
  protected String column;
  protected String pattern;

  // occurrence,topic-name 
  protected String type;
  protected String[] scope;
  protected String datatype;

  // role+other
  protected String atype;
  protected String rtype;
  protected String player;
  protected List oroles = new ArrayList();

  public static final int OPTIONAL_DEFAULT = 0;
  public static final int OPTIONAL_TRUE = 1;
  public static final int OPTIONAL_FALSE = 2;
  protected int optional = OPTIONAL_DEFAULT;
  
  Field(int ftype, Entity entity) {
    this.ftype = ftype;
    this.entity = entity;
  }

  public void compile() {
    compileValue();
  }

  public Entity getEntity() {
    return entity;
  }
  
  public int getFieldType() {
    return ftype;
  }

  public String getColumn() {
    return column;
  }

  public void setColumn(String column) {
    this.column = column;
  }

  public String getPattern() {
    return pattern;
  }

  public void setPattern(String pattern) {
    this.pattern = pattern;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getDatatype() {
    return datatype;
  }

  public void setDatatype(String datatype) {
    this.datatype = datatype;
  }

  public String[] getScope() {
    return scope;
  }

  public void setScope(String[] scope) {
    this.scope = scope;
  }

  public int getOptional() {
    return optional;
  }

  public void setOptional(boolean optional) {
    this.optional = (optional ? OPTIONAL_TRUE : OPTIONAL_FALSE);
  }
  
  public String getAssociationType() {
    return atype;
  }

  public void setAssociationType(String atype) {
    this.atype = atype;
  }

  public String getRoleType() {
    return rtype;
  }

  public void setRoleType(String rtype) {
    this.rtype = rtype;
  }

  public String getPlayer() {
    return player;
  }

  public void setPlayer(String player) {
    this.player = player;
  }

  public void addOtherRoleField(Field orole) {
    oroles.add(orole);
  }
  
  public List getOtherRoleFields() { // TYPE_PLAYER only
    return oroles;
  }

  // -- pattern

  protected ValueIF cvalue;
  
  public String getValue(String[] tuple) {
    return cvalue.getValue(tuple);
  }
  
  protected void compileValue() {
    if (getFieldType() != TYPE_PLAYER &&
        getFieldType() != TYPE_ASSOCIATION_ROLE) {
      Relation relation = getEntity().getRelation();
      String column = getColumn();
      if (column != null)
        this.cvalue = Values.getColumnValue(relation, column);
      else
        this.cvalue = Values.getPatternValue(relation, getPattern());
    }
  }

}
