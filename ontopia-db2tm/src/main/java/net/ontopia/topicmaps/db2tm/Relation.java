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
import java.util.HashMap;
import java.util.Map;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Relation mapping concept that represents a relation
 * definition. A relation definition will hold one or more entity
 * defintions.
 */
public class Relation {
  protected String name;      // relation name
  protected String[] columns; // all columns
  protected String[] pkey;    // primary key

  protected String condition;

  protected String commit = null;
  
  protected final RelationMapping mapping;
  protected List<Entity> entities = new ArrayList<Entity>(2);
  protected Map<String, ValueIF> virtualColumns = new HashMap<String, ValueIF>(2);

  // field type enumeration
  public static final int SYNCHRONIZATION_UNKNOWN = 0;
  public static final int SYNCHRONIZATION_NONE = 1;
  public static final int SYNCHRONIZATION_RESCAN = 2;
  public static final int SYNCHRONIZATION_CHANGELOG = 4;
  protected int synctype = SYNCHRONIZATION_UNKNOWN;
  
  protected List<Changelog> syncs = new ArrayList<Changelog>(1);
  
  Relation(RelationMapping mapping) {
    this.mapping = mapping;
  }

  /**
   * INTERNAL: Returns the relation mapping to which the relation
   * belongs.
   */
  public RelationMapping getMapping() {
    return mapping;
  }

  /**
   * INTERNAL: Returns the name of the relation.
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
                         
  public String[] getPrimaryKey() {
    return pkey;
  }

  public void setPrimaryKey(String[] pkey) {
    this.pkey = pkey;
  }

  public String getCondition() {
    return condition;
  }

  public void setCondition(String condition) {
    this.condition = condition;
  }

  public void setCommitMode(String commit) {
    this.commit = commit;
  }
  
  public String getCommitMode() {
    return commit;
  }
  
  // ---------------------------------------------------------------------------
  // Entities
  // ---------------------------------------------------------------------------

  public List<Entity> getEntities() {
    return entities;
  }

  public void addEntity(Entity entity) {
    entities.add(entity);
  }

  public void removeEntity(Entity entity) {
    entities.remove(entity);
  }

  public Entity getPrimaryEntity() {
    return entities.get(0);
  }

  // ---------------------------------------------------------------------------
  // Columns
  // ---------------------------------------------------------------------------
  
  public String[] getColumns() {
    return columns;
  }

  public void setColumns(String[] columns) {
    this.columns = columns;
  }
  
  public int getColumnIndex(String column) {
    for (int i=0; i < columns.length; i++) {
      if (columns[i].equals(column)) {
        return i;
      }
    }
    return -1;
  }

  public boolean isVirtualColumn(String name) {
    return virtualColumns.containsKey(name);
  }

  public ValueIF getVirtualColumn(String name) {
    ValueIF vcol = virtualColumns.get(name);
    if (vcol == null) {
      throw new DB2TMConfigException("Unknown virtual column: " + name);
    }
    return vcol;
  }
  
  public void addVirtualColumn(String name, ValueIF vcol) {
    this.virtualColumns.put(name, vcol);
  }

  // ---------------------------------------------------------------------------
  // Synchronization
  // ---------------------------------------------------------------------------

  public int getSynchronizationType() {
    return synctype;
  }

  public void setSynchronizationType(int synctype) {
    this.synctype = synctype;
  }

  public static String getSynchronizationTypeName(int synctype) {
    switch (synctype) {
    case Relation.SYNCHRONIZATION_UNKNOWN:
      return "unknown";
    case Relation.SYNCHRONIZATION_NONE:
      return "none";
    case Relation.SYNCHRONIZATION_RESCAN:
      return "rescan";
    case Relation.SYNCHRONIZATION_CHANGELOG:
      return "changelog";
    default:
      throw new OntopiaRuntimeException("Illegal synchronization type:" + synctype);
    }
  }
  
  public List<Changelog> getSyncs() {
    return syncs;
  }

  public void addSync(Changelog sync) {
    syncs.add(sync);
  }

  public void removeSync(Changelog sync) {
    syncs.remove(sync);
  }

  @Override
  public String toString() {
    return "Relation(" + getName() + ")";
  }

  // ---------------------------------------------------------------------------
  // Compiling
  // ---------------------------------------------------------------------------
  
  protected void compile() {
    for (Entity entity : entities) {
      entity.compile();
    }
  }

}
