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
package net.ontopia.topicmaps.query.toma.impl.basic;

import java.util.Comparator;
import java.util.List;

import net.ontopia.topicmaps.query.toma.impl.utils.Stringifier;
import net.ontopia.topicmaps.query.toma.parser.ast.QueryOrder;
import net.ontopia.topicmaps.query.toma.parser.ast.QueryOrder.SORT_ORDER;

/**
 * INTERNAL: Comparator for rows based on the order by definitions.
 */
public class RowComparator implements Comparator<Row> {

  private List<QueryOrder> ordering;
  
  public RowComparator(List<QueryOrder> ordering) {
    this.ordering = ordering;
  }
  
  // FIXME: needs to be revised.
  public int compare(Row row1, Row row2) {
    // if we have no ordering defined, 
    if (ordering.isEmpty()) {
      String s1 = Stringifier.toString(row1.getFirstValue());
      String s2 = Stringifier.toString(row2.getFirstValue());
      return s1.compareTo(s2);
    } else {
      for (QueryOrder order : ordering) {
        int col = order.getColumn() - 1;
        Comparable s1 = Stringifier.toSort(row1.getValue(col));
        Comparable s2 = Stringifier.toSort(row2.getValue(col));
        
        int cmp = 0;
        if (order.getOrder() == SORT_ORDER.ASC) {
          if (s1 == null) cmp = +1;
          else if (s2 == null) cmp = -1;
          else cmp = s1.compareTo(s2);
        } else {
          if (s1 == null) cmp = -1;
          else if (s2 == null) cmp = +1;
          else cmp = s2.compareTo(s1);
        }
        
        if (cmp != 0) 
          return cmp;
      }
    }
    
    return 0;
  }
}
