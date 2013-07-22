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

package net.ontopia.topicmaps.webed.impl.basic;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import net.ontopia.topicmaps.webed.impl.utils.ActionData;

/**
 * INTERNAL: Container for all the ActionData carriers produced for
 * one HTML form.
 */
public class ActionDataSet {
  private String request_id;
  private Map actions; // request param -> ActionData

  public ActionDataSet(String request_id) {
    this.request_id = request_id;
    this.actions = new HashMap();
  }

  public void addActionData(ActionData data) {
    actions.put(data.getFieldName(), data);
  }
  
  public ActionData getActionData(String fieldName) {
    return (ActionData)actions.get(fieldName);
  }

  public Collection getAllActionData() {
    return actions.values();
  }

  public String getRequestId() {
    return request_id;
  }
}
