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

package net.ontopia.topicmaps.webed.impl.utils;

import java.io.Serializable;

import net.ontopia.topicmaps.webed.impl.basic.ActionInGroup;

/**
 * INTERNAL: Container to store an action object together with a
 * action response type, which enables to store the forward
 * information as a triple consisting of (key: ActionResponseComposite
 * = action + response type, value: ActionForwardPageIF).
 *
 * @see net.ontopia.topicmaps.webed.impl.basic.ActionGroup
 */
public class ActionResponseComposite implements Serializable {

  protected ActionInGroup action;
  protected Integer responseType;
  
  public ActionResponseComposite(ActionInGroup action, int responseType) {
    this.action = action;
    this.responseType = new Integer(responseType);
  }

  public ActionInGroup getAction() {
    return action;
  }

  public void setAction(ActionInGroup action) {
    this.action = action;
  }

  public int getResponseType() {
    return responseType.intValue();
  }

  public void setResponseType(int responseType) {
    this.responseType = new Integer(responseType);
  }

  // --- overwrite methods from Object implementation

  @Override
  public int hashCode() {
    return action.hashCode() + responseType.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof ActionResponseComposite) {
      ActionResponseComposite comp = (ActionResponseComposite) obj;
      return (comp.getAction().equals(action)
              && (comp.getResponseType() == responseType.intValue()));
    } else
      return false;
  }
  
}
