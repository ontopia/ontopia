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

package net.ontopia.topicmaps.webed.taglibs;

import net.ontopia.topicmaps.webed.impl.utils.ActionData;

/**
 * INTERNAL: Implemented by tags which accept sub-actions that will be
 * invoked after their main action.
 *
 * @since 2.0
 */
public interface ActionInvokingTagIF {

  /**
   * Accepts the action and adds it to the list of actions to be
   * invoked together with the primary action.
   */
  public void addAction(ActionData action);

}
