
// $Id: Relation.java,v 1.21 2007/01/31 09:34:20 grove Exp $

package net.ontopia.topicmaps.db2tm;

import java.util.ArrayList;
import java.util.Iterator;
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

  protected String name; // relation name
  protected String[] columns; // all columns
  protected String[] pkey; // primary key

  protected String condition;

  protected String commit = null;
  
  protected RelationMapping mapping;
  protected List entities = new ArrayList(2);
  protected Map virtualColumns = new HashMap(2);

  // field type enumeration
  public static final int SYNCHRONIZATION_UNKNOWN = 0;
  public static final int SYNCHRONIZATION_NONE = 1;
  public static final int SYNCHRONIZATION_RESCAN = 2;
  public static final int SYNCHRONIZATION_CHANGELOG = 4;
  protected int synctype = SYNCHRONIZATION_UNKNOWN;
  
  protected List syncs = new ArrayList(1);
  
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
  // -----------------------------------------------------------------------------
  // Entities
  // -----------------------------------------------------------------------------

  public List getEntities() {
    return entities;
  }

  public void addEntity(Entity entity) {
    entities.add(entity);
  }

  public void removeEntity(Entity entity) {
    entities.remove(entity);
  }

  public Entity getPrimaryEntity() {
    return (Entity)entities.get(0);
  }

  // -----------------------------------------------------------------------------
  // Columns
  // -----------------------------------------------------------------------------
  
  public String[] getColumns() {
    return columns;
  }

  public void setColumns(String[] columns) {
    this.columns = columns;
  }
  
  public int getColumnIndex(String column) {
    for (int i=0; i < columns.length; i++) {
      if (columns[i].equals(column)) return i;
    }
    return -1;
  }

  public boolean isVirtualColumn(String name) {
    return virtualColumns.containsKey(name);
  }

  public ValueIF getVirtualColumn(String name) {
    ValueIF vcol = (ValueIF)virtualColumns.get(name);
    if (vcol == null)
      throw new DB2TMConfigException("Unknown virtual column: " + name);
    return vcol;
  }
  
  public void addVirtualColumn(String name, ValueIF vcol) {
    this.virtualColumns.put(name, vcol);
  }

  // -----------------------------------------------------------------------------
  // Synchronization
  // -----------------------------------------------------------------------------

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
  
  public List getSyncs() {
    return syncs;
  }

  public void addSync(Changelog sync) {
    syncs.add(sync);
  }

  public void removeSync(Changelog sync) {
    syncs.remove(sync);
  }

  public String toString() {
    return "Relation(" + getName() + ")";
  }

  // -----------------------------------------------------------------------------
  // Compiling
  // -----------------------------------------------------------------------------
  
  void compile() {
    Iterator iter = entities.iterator();
    while (iter.hasNext()) ((Entity)iter.next()).compile();
  }

}
