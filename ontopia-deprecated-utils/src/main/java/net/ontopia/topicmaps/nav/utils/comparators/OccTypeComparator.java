/*
 * #!
 * Ontopia Navigator
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

package net.ontopia.topicmaps.nav.utils.comparators;

import java.net.URISyntaxException;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.nav.utils.comparators.TopicComparator;
import net.ontopia.utils.DeciderIF;
import net.ontopia.topicmaps.nav2.impl.basic.TypeDecider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: A Comparator for ordering topics alphabetically. Note that
 * it does not look up the 'sort' topic for you, but that this must be
 * provided explicitly to the constructors.
 */
@Deprecated
public class OccTypeComparator extends TopicComparator {

  // initialization of logging facility
  private static final Logger log = LoggerFactory
    .getLogger(OccTypeComparator.class.getName());

  // members
  private static DeciderIF<TopicIF> metadataDecider = null;
  private static DeciderIF<TopicIF> descriptionDecider = null;

  public OccTypeComparator() {
    super();
    try {
      metadataDecider = new TypeDecider(TypeDecider.OCC_METADATA);
      descriptionDecider = new TypeDecider(TypeDecider.OCC_DESCRIPTION);
    } catch (URISyntaxException mue) {
      log.info("Could not find metadata occurrence type topic");
    }
  }

  @Override
  public int compare(TopicIF o1, TopicIF o2) {
    if (o1 == null)
      return 1;
    if (o2 == null)
      return -1;

    String n1 = nameStringifier.apply(nameGrabber.apply(o1));
    if (n1 == null)
      return 1;
    // prefix
    if (metadataDecider.ok(o1)) {
      n1 = "META" + n1;
    } else {
      if (descriptionDecider.ok(o1))
        n1 = "ZZZDESC" + n1;
    }

    String n2 = nameStringifier.apply(nameGrabber.apply(o2));
    if (n2 == null)
      return -1;
    // prefix
    if (metadataDecider.ok(o2)) {
      n2 = "META" + n2;
    } else {
      if (descriptionDecider.ok(o2))
        n2 = "ZZZDESC" + n2;
    }
    
    return n1.compareToIgnoreCase(n2);
  }  
  
}





