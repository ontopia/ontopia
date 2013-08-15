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

package net.ontopia.topicmaps.query.impl.utils;

import java.util.Collection;
import net.ontopia.topicmaps.query.impl.basic.QueryContext;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;

/**
 * INTERNAL: Various utility methods for working with QueryMatches
 * objects.
 */
public class QueryMatchesUtils {

  public static QueryMatches createInitialMatches(QueryContext context,
                                                  Collection columnDefs) {
    QueryMatches matches = new QueryMatches(columnDefs, context);
    matches.last++; // enter a single empty match to seed the process
    matches.insertConstants();
    return matches;
  }
  
}
