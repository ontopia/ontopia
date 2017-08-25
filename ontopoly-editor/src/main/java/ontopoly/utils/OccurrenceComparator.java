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
package ontopoly.utils;

import java.io.Serializable;
import java.util.Comparator;
import net.ontopia.topicmaps.core.OccurrenceIF;
import org.apache.commons.lang3.StringUtils;

public class OccurrenceComparator implements Comparator<Object>, Serializable {

  public static final OccurrenceComparator INSTANCE = new OccurrenceComparator();
  
  @Override
  public int compare(Object o1, Object o2) {
    OccurrenceIF occ1 = (OccurrenceIF)o1;
    OccurrenceIF occ2 = (OccurrenceIF)o2;    
    return StringUtils.compareIgnoreCase(occ1.getValue(), occ2.getValue());
  }

}
