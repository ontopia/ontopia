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

package net.ontopia.topicmaps.webed.core;

/**
 * PUBLIC: Interface for server-side actions which update the topic
 * map or otherwise act on information received from client-side
 * forms.
 */
public interface ActionIF {
  
  /**
   * PUBLIC: Performs the action using the values provided by the
   * <code>params</code> parameter.
   *
   * <p>The whole set of available parameters and attributes are
   * summarized in the <code>request</code> object, to allow the
   * action access to further relevant information.
   *
   * @exception ActionRuntimeException Thrown if a problem occurs
   *            while executing the action.
   */
  public void perform(ActionParametersIF params, ActionResponseIF response)
    throws ActionRuntimeException;
  
}
