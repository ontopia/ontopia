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

/**
 * INTERNAL: A class implementing this interface specifies a
 * transformation rule to modify the forward URL.
 */
public interface ParamRuleIF {

  /**
   * INTERNAL: Transform the action name, based on the values of the
   * given current action and next action or information residing in
   * the context object.
   *
   * @return String containing the manipulated relative request URL
   * with the request parameter value-pairs (based on
   * <code>urlWithParams</code>).
   */
  public String generate(ActionContextIF context,
                         String actionName, String nextActionTemplate,
                         String urlWithParams);

}
