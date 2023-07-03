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

package net.ontopia.topicmaps.xml;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import org.junit.Ignore;

/**
 * INTERNAL: Utility methods used by the various tests in this package.
 */
@Ignore
public class TestUtils {

  /**
   * INTERNAL: Fixes up internal item identifiers on topic maps that
   * have been imported, exported to a temporary XTM, and then
   * reimported.  The idea is to ensure that they canonicalize
   * correctly.
   */
  public static void fixItemIds(TopicMapIF tm, LocatorIF origbaseloc)
    throws URISyntaxException {
    String origbase = origbaseloc.getAddress();
    String base = tm.getStore().getBaseAddress().getAddress();
    Iterator it = tm.getTopics().iterator();
    while (it.hasNext()) {
      TopicIF topic = (TopicIF) it.next();
      Iterator it2 = new ArrayList(topic.getItemIdentifiers()).iterator();
      while (it2.hasNext()) {
        LocatorIF itemid = (LocatorIF) it2.next();
        String iid = itemid.getAddress();
        if (iid.startsWith(base)) {
          if (AbstractTopicMapExporter.mayCollide(iid.substring(base.length() + 1))) {
            topic.removeItemIdentifier(itemid);
          } else {
            topic.removeItemIdentifier(itemid);
            String tmp = iid.substring(base.length());
            topic.addItemIdentifier(new URILocator(origbase + tmp));
          }
        }
      }
    }

    if (tm.getStore() instanceof InMemoryTopicMapStore) {
      ((InMemoryTopicMapStore) tm.getStore()).setBaseAddress(origbaseloc);
    }
  }
  
}
