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

import java.util.Set;
import java.util.List;
import net.ontopia.topicmaps.webed.impl.basic.ActionInGroup;

/**
 * INTERNAL: Server-side data carrier for actions that have been sent
 * to the user and for which we are waiting for a reply. 
 */
public class ActionData {
  private List params;
  private ActionInGroup action;
  private List sub_actions;
  private Set value; // the original value of the form control
  private String field_name;
  private boolean run_if_no_changes; // ie: should this run if no other action did?
  private String matchExpression;
  
  public ActionData(ActionInGroup action, List params) {
    this(action, params, null, null, null);
  }

  public ActionData(ActionInGroup action, List params, Set value,
                    List sub_actions, String field_name) {
    this.field_name = field_name;
    this.action = action;
    this.params = params;
    if (this.params == null)
      this.params = java.util.Collections.EMPTY_LIST;
    this.value = value;
    if (this.value == null)
      this.value = java.util.Collections.EMPTY_SET;
    this.sub_actions = sub_actions;
    if (sub_actions == null)
      this.sub_actions = java.util.Collections.EMPTY_LIST;
  }

  public String getFieldName() {
    return field_name;
  }
  
  public List getParameters() {
    return params;
  }

  public ActionInGroup getAction() {
    return action;
  }

  public Set getValue() {
    return value;
  }
  
  public List getSubActions() {
    return sub_actions;
  }

  public void setRunIfNoChanges(boolean run_if_no_changes) {
    this.run_if_no_changes = run_if_no_changes;
  }

  public boolean getRunIfNoChanges() {
    return run_if_no_changes;
  }
  
  public String getMatchExpression() {
    return matchExpression;
  }
  
  public void setMatchExpression(String matchExpression) {
    this.matchExpression = matchExpression;
  }
  
  public String toString() {
    return "[ActionData " + (action == null ? "null" : action.getName()) +
           " with " + params.size() + " param(s) and " + sub_actions.size() +
           " sub-action(s)]";
  }
  
}
