/**
 * Copyright (C) 2009 Space Applications Services
 *   <thomas.neidhart@spaceapplications.com>
 *
 * This file is part of the Ontopia project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ontopia.topicmaps.query.toma.parser.ast;

/**
 * INTERNAL: Represents the query order of a specific column in TOMA query.
 * 
 * Two different {@link SORT_ORDER} are defined:
 * <ul>
 * <li>ASC - ascending
 * <li>DESC - descending
 * </ul>
 * 
 * The default sort order is ASC.
 */
public class QueryOrder 
{
  public enum SORT_ORDER
  {
    ASC,
    DESC
  };
  
  private int column;
  private SORT_ORDER order;

  /**
   * Create a default ordering for a specific column.
   * @param column the column to be sorted.
   */
  public QueryOrder(int column) {
    this(column, SORT_ORDER.ASC);
  }
  
  /**
   * Create a specific ordering fo a column.
   * @param column the column to be sorted.
   * @param order the ordering to be used.
   */
  public QueryOrder(int column, SORT_ORDER order) {
    this.column = column;
    this.order = order;
  }

  public int getColumn() {
    return column;
  }

  public void setColumn(int column) {
    this.column = column;
  }

  public SORT_ORDER getOrder() {
    return order;
  }

  public void setOrder(SORT_ORDER order) {
    this.order = order;
  }
}
