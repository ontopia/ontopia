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

package net.ontopia.topicmaps.utils.ltm;

import java.io.IOException;
import java.io.StringReader;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.StringTemplateUtils;
import org.apache.commons.lang3.StringUtils;

/**
* PUBLIC: Imports an LTM fragment with references to parameter values
* specified externally.
*
* @since 3.2.4
*/
public class LTMTemplateImporter {

  /**
   * PUBLIC: Imports an LTM fragment with references to parameter values
   * specified externally.
   * @param topicmap The topic map to import the LTM into.
   * @param ltm The LTM fragment.
   * @param parameters The %foo% parameters referenced from the LTM.
   * @return A Map containing references to the %new% topics created.
   */
  public static Map<String, TopicIF> read(TopicMapIF topicmap, String ltm, Map<String, Object> parameters)
    throws IOException {

    // wrap parameters with translation/quoting/ID-creating map
    ParameterWrapper parawrapper = new ParameterWrapper(parameters, topicmap);

    // produce the full LTM using the wrapper and parameters
    String tmp = StringTemplateUtils.replace(ltm, parawrapper);

    // then do the actual import
    LocatorIF base = topicmap.getStore().getBaseAddress();
    LTMTopicMapReader reader =
          new LTMTopicMapReader(new StringReader(tmp), base);
    reader.importInto(topicmap);

    // turn ID map into topic map
    Map<String, String> newids = parawrapper.getNewIds();
    Map<String, TopicIF> newtopics = new HashMap<String, TopicIF>(newids.size());
    Iterator<String> it = newids.keySet().iterator();
    while (it.hasNext()) {
      String key = it.next();
      String id = newids.get(key);

      newtopics.put(key, (TopicIF) topicmap.getObjectByItemIdentifier(base.resolveAbsolute("#" + id)));
    }
    return newtopics;
  }

  // ===== INTERNAL ==================================================

  /**
   * This class converts parameter values into strings which can
   * actually be safely inserted into the LTM, and also implements the
   * %new% and %newX% references.
   */
  static class ParameterWrapper extends AbstractMap<String, Object> {
    private Map<String, Object> wrapped;
    private Map<String, String> newids; // new35 -> ID of new35
    private TopicMapIF topicmap;

    public ParameterWrapper(Map<String, Object> wrapped, TopicMapIF topicmap) {
      this.wrapped = wrapped;
      this.newids = new HashMap<String, String>();
      this.topicmap = topicmap;
    }

    public Map<String, String> getNewIds() {
      return newids;
    }

    public Object get(String key) {
      if (key.startsWith("new")) {
        // reference to a new topic
        String id = newids.get(key);
        if (id == null) {
          id = makeRandomId(topicmap);
          newids.put(key, id);
        }
        return id;

      } else {
        // reference to a parameter value
        Object value = wrapped.get(key);
        if (value == null) {
          throw new OntopiaRuntimeException("No LTM parameter '" + key + "'");
        }

        if (value instanceof String) {
          // we need to escape the string so it'll fit nicely into the LTM
          return StringUtils.replace((String) value, "\"", "\"\"");

        } else if (value instanceof TopicIF) {
          // we need to turn this into a topic reference
          TopicIF topic = (TopicIF) value;
          return getId(topic);

        } else {
          throw new OntopiaRuntimeException("Bad LTM parameter value: " +
                                            value);
        }
      }
    }

    public boolean containsKey(String key) {
      return get(key) != null;
    }

    @Override
    public int size() {
      return 3; // a smallish number, that's all
    }

    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
      throw new net.ontopia.utils.OntopiaRuntimeException("INTERNAL ERROR");
    }

    // internal stuff
    private String getId(TMObjectIF object) {
      Iterator<LocatorIF> it = object.getItemIdentifiers().iterator();
      if (it.hasNext()) {
        // FIXME: this doesn't check the base address!
        LocatorIF loc = it.next();
        String address = loc.getAddress();
        int pos = address.indexOf('#');
        if (pos != -1) {
          return address.substring(pos + 1);
        }
      }

      // for the case where the object has no source locator at all, or
      // it doesn't contain '#'
      TopicMapIF topicmap = object.getTopicMap();
      String id = makeRandomId(topicmap);
      LocatorIF base = topicmap.getStore().getBaseAddress();
      object.addItemIdentifier(base.resolveAbsolute("#" + id));
      return id;
    }

    private String makeRandomId(TopicMapIF topicmap) {
      String id;
      TMObjectIF tmobj;
      LocatorIF base = topicmap.getStore().getBaseAddress();

      do {
        id = net.ontopia.utils.StringUtils.makeRandomId(10);
        tmobj = topicmap.getObjectByItemIdentifier(base.resolveAbsolute("#" + id));
      } while (tmobj != null);

      return id;
    }
  }
}
