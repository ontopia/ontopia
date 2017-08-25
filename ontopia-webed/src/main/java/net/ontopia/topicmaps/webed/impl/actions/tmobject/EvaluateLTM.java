/*
 * #!
 * Ontopia Webed
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

package net.ontopia.topicmaps.webed.impl.actions.tmobject;

import java.io.StringReader;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.utils.ltm.LTMTopicMapReader;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;
import net.ontopia.topicmaps.webed.core.WebEdRequestIF;
import net.ontopia.topicmaps.webed.impl.basic.Constants;
import net.ontopia.topicmaps.webed.impl.utils.ActionSignature;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.StringTemplateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PUBLIC: Action for adding information to a topic map by evaluating
 * an LTM fragment.
 *
 * @since 2.0
 */
public class EvaluateLTM implements ActionIF {

  // initialization of logging facility
  private static Logger log = LoggerFactory.getLogger(EvaluateLTM.class.getName());

  @Override
  public void perform(ActionParametersIF params, ActionResponseIF response) {
    
    // test params
    ActionSignature paramsType = ActionSignature.getSignature("m! s!");
    paramsType.validateArguments(params, this);
    
    TopicMapIF topicmap = (TopicMapIF) params.get(0);
    String template = (String) params.get(1);
    WebEdRequestIF request = params.getRequest();

    Collection values = null;
    TMObjectIF object = params.getTMObjectValue();
    if (object == null) {
      String strval = params.getStringValue();
      if (strval != null) {
        // escape quotes so LTM will parse correctly
        String value = StringUtils.replace(strval, "\"", "\"\"");
        values = Collections.singleton(value);
      }
    } else
      values = params.getTMObjectValues();

    String lastid = null;    
    if (values == null)
      // no object value; just do it anyway
      importFragment(template, topicmap);
    else {
      // repeat once for each object value
      Iterator it = values.iterator();      
      while (it.hasNext()) {
        lastid = makeRandomId(topicmap);
        Map map = new RequestMapWrapper(request, it.next(), lastid, topicmap);
        String tmp = StringTemplateUtils.replace(template, map);
        importFragment(tmp, topicmap);
      }
    }
    
    // register new topic in response
    TopicIF topic = getTopicById(topicmap, lastid);
    if (lastid != null && topic != null)
      response.addParameter(Constants.RP_TOPIC_ID, topic.getObjectId());
  }

  // --- Internal methods

  private void importFragment(String fragment, TopicMapIF topicmap) {
    log.debug("Importing fragment " + fragment);
    LocatorIF base = topicmap.getStore().getBaseAddress();
    LTMTopicMapReader reader = 
          new LTMTopicMapReader(new StringReader(fragment), base);
               
    try {
      reader.importInto(topicmap);
    } catch (java.io.IOException e) {
      log.error("Syntax error in LTM fragment", e);
      throw new ActionRuntimeException(e);
    }  
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

  private String getId(TMObjectIF object) {
    Iterator it = object.getItemIdentifiers().iterator();
    if (it.hasNext()) {
      LocatorIF loc = (LocatorIF) it.next();
      String address = loc.getAddress();
      int pos = address.indexOf("#");
      if (pos != -1)
        return address.substring(pos + 1);
    }

    // for the case where the object has no source locator at all, or
    // it doesn't contain '#'
    TopicMapIF topicmap = object.getTopicMap();
    String id = makeRandomId(topicmap);
    LocatorIF base = topicmap.getStore().getBaseAddress();
    object.addItemIdentifier(base.resolveAbsolute("#" + id));
    return id;
  }

  protected TopicIF getTopicById(TopicMapIF topicmap, String id) {
    LocatorIF base = topicmap.getStore().getBaseAddress();
    return (TopicIF) topicmap.getObjectByItemIdentifier(base.resolveAbsolute("#" + id));
  }

  // --- Wrapper class for request

  class RequestMapWrapper extends AbstractMap {
    private WebEdRequestIF request;
    private Object value;
    private String randid;
    private TopicMapIF topicmap;
    private Map newkeys;

    public RequestMapWrapper(WebEdRequestIF request, Object value, String randid,
                             TopicMapIF topicmap) {
      this.request = request;
      this.value = value;
      this.randid = randid;
      this.topicmap = topicmap;
      this.newkeys = new HashMap();
    }
    
    @Override
    public boolean containsKey(Object key) {
      return get(key) != null;
    }

    @Override
    public Object get(Object keyy) {
      String key = (String) keyy;
      Object o = null;
      if (key.equals("value") || key.equals("topic"))
        o = value;
      else if (key.equals("new"))
        o = randid;
      // keys of form newXXX where XXX is an integer
      else if (key.startsWith("new") && isInteger(key.substring(3))) {
        if (newkeys.containsKey(key))
          o = newkeys.get(key);
        else {
          o = makeRandomId(topicmap);
          newkeys.put(key, o);
        }
      } else {
        ActionParametersIF params = request.getActionParameters((String) key);
        if (params == null)
          throw new OntopiaRuntimeException("Reference in LTM template to undefined action: '" + key + "'");
        o = params.getTMObjectValue();
        if (o == null)
          o = params.getStringValue();
      }

      if (o instanceof TMObjectIF)
        o = getId((TMObjectIF) o);
      
      log.debug("RMW.get('" + key + "') -> " + o);
      return o;
    }

    @Override
    public int size() {
      return 3; // a smallish number, that's all
    }

    @Override
    public Set entrySet() {
      throw new net.ontopia.utils.OntopiaRuntimeException("INTERNAL ERROR");
    }
  }
  
  /**
   * INTERNAL: Returns true if the string is a valid integer.
   */
  private static boolean isInteger(String candidate) {
    try {
      Integer.parseInt(candidate);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }    
  }
}
