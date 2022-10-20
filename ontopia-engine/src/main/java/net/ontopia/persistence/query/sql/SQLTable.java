/*
 * #!
 * Ontopia Engine
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

package net.ontopia.persistence.query.sql;

import java.util.Objects;

/**
 * INTERNAL: Represents a reference to a table in a relation
 * database. The reference consist of the name of the table and the
 * alias which it has been assigned in the query.
 */

public class SQLTable {

  protected String name;
  protected String alias;
  
  public SQLTable(String name, String alias) {
    Objects.requireNonNull(name, "Table name cannot be null (alias=" + alias + ").");
    // FIXME: Perhaps we should open for no table alias?
    Objects.requireNonNull(alias, "Table alias cannot be null (name=" + name + ").");
    this.name = name;
    this.alias = alias;
  }

  public String getName() {
    return name;
  }

  public String getAlias() {
    return alias;
  }

  @Override
  public int hashCode() {
    return name.hashCode() + alias.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof SQLTable)) {
      return false;
    }
    SQLTable table = (SQLTable)obj;
    if (alias == null ? table.getAlias() != null : !alias.equals(table.getAlias())) {
      return false;
    }
    if (name == null ? table.getName() != null : !name.equals(table.getName())) {
      return false;
    }
    return true;
  }
  
  @Override
  public String toString() {
    return getName() + " " + getAlias();
  }
  
}





