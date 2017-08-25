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

package net.ontopia.topicmaps.webed.impl.actions.occurrence;

import java.util.Iterator;

import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.index.OccurrenceIndexIF;
import net.ontopia.topicmaps.utils.TopicStringifiers;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.impl.utils.ActionSignature;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * PUBLIC: Action for setting the string value of an occurrence. Creates
 * a new occurrence if none already exists. Also checks whether or not
 * there already is another occurrence with the same scope and value,
 * and if there is it logs the fact.
 *
 * @since 2.0
 */
public class SetValueUnique extends SetValue {

  // initialization of logging facility
  private static Logger log = LoggerFactory.getLogger(SetValueUnique.class.getName());

  @Override
  public void perform(ActionParametersIF params, ActionResponseIF response) {
    log.debug("occurrence.SetValueUnique.perform() executing");

    // test params
    ActionSignature paramsType = ActionSignature.getSignature("o t? s? t?");
    paramsType.validateArguments(params, this);
    
    // do the job
    setValue(params, response, 3);
  }
  
  
  @Override
  protected boolean isUnique(ActionParametersIF params,
                             ActionResponseIF response, int typeParamIx) {
    log.debug("occurrence.SetValueUnique.isUnique() executing");
    OccurrenceIF occPar = (OccurrenceIF) params.get(0);
    TopicIF topic = (TopicIF) params.get(1);
    TopicIF type = (TopicIF)params.get(typeParamIx);
    String value = params.getStringValue();
    
    if (occPar != null) {
      if (topic == null)
        topic = occPar.getTopic();
      if (type == null)
        type = occPar.getType();
    }
    
    TopicMapIF tm = topic.getTopicMap();
    OccurrenceIndexIF occurrenceIdex = (OccurrenceIndexIF)tm
        .getIndex("net.ontopia.topicmaps.core.index.OccurrenceIndexIF");

    log.debug("occurrence.SetValueUnique.isUnique() topic(" +
        topic +
        "):" + TopicStringifiers.toString(topic));
    log.debug("occurrence.SetValueUnique.isUnique() type:(" +
        type +
        ")" + TopicStringifiers.toString(topic));
    Iterator it = occurrenceIdex.getOccurrences(value).iterator();
    while (it.hasNext()) {
      OccurrenceIF occ = (OccurrenceIF) it.next();
      log.debug("occurrence.SetValueUnique.isUnique() occ:" + occ.getValue());
      log.debug("occurrence.SetValueUnique.isUnique() occ.getTopic():" + TopicStringifiers.toString((occ.getTopic())));
      log.debug("occurrence.SetValueUnique.isUnique() occ.getType()" + TopicStringifiers.toString(occ.getType()));
      if (occ.getTopic().equals(topic)) {
        log.debug("occurrence.SetValueUnique.isUnique() ourselves");
        continue; // we've found ourselves
      }  

      TopicIF occType = occ.getType();
      if (type == occType || (occType != null && occType.equals(type))) {
        log.debug("occurrence.SetValueUnique.isUnique() if1");

        // Uh oh. We've found a duplicate.
        params.getRequest().getUser().addLogMessage("The occurrence '" + 
            value  + "' is already in use");
        
        String forwardTo = (String) params.get(2);
        if (forwardTo != null)
          response.setForward(forwardTo);
        
        log.debug("occurrence.SetValueUnique.isUnique() NOTunique");
        return false;
      }
    }
    log.debug("occurrence.SetValueUnique.isUnique() unique!");
    return true;
  }
}
