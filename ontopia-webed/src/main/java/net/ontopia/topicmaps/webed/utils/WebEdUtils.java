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

package net.ontopia.topicmaps.webed.utils;

import java.util.Set;
import java.util.List;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspTagException;
import net.ontopia.topicmaps.webed.impl.utils.TagUtils;

/**
 * PUBLIC: Utility methods useful for writing web editor framework
 * applications.
 *
 * @since 2.0
 */
public class WebEdUtils {
  
  /**
   * PUBLIC: Binds the parameters to the specified action in the given
   * action group and returns the HTML form control name to be used to
   * invoke the action with the given parameters.
   *
   * @param pageContext The page context.
   * @param action_name The name of the action to bind to (same as the -action-
   *                    attribute on the JSP tags).
   * @param group_name The name of the action group the action belongs to (same
   *                   as the -actiongroup- attribute on the form tag).
   * @param paramlist The parameters to the action. A list of navigator variable
   *                  names.
   */
  public static String registerData(PageContext pageContext,
                                    String action_name, String group_name,
                                    List paramlist)
    throws JspTagException {
    return TagUtils.registerData(pageContext, action_name, group_name, paramlist, null);
  }

  /**
   * PUBLIC: Binds the parameters to the specified action in the given
   * action group and returns the HTML form control name to be used to
   * invoke the action with the given parameters.
   *
   * @param pageContext The page context.
   * @param action_name The name of the action to bind to (same as the -action-
   *                    attribute on the JSP tags).
   * @param group_name The name of the action group the action belongs to (same
   *                   as the -actiongroup- attribute on the form tag).
   * @param paramlist The parameters to the action. A list of navigator variable
   *                  names.
   * @param curvalue The current value of the form control. (Warning: getting
   *                 this value right can be tricky.)
   *
   * @since 2.0.3
   */
  public static String registerData(PageContext pageContext,
                                    String action_name, String group_name,
                                    List paramlist, Set curvalue)
    throws JspTagException {
    return TagUtils.registerData(pageContext, action_name, group_name, paramlist, curvalue);
  }
  
  /**
   * PUBLIC: Binds the parameters to the specified action in the given
   * action group and returns the HTML form control name to be used to
   * invoke the action with the given parameters.
   *
   * @param pageContext The page context.
   * @param action_name The name of the action to bind to (same as the -action-
   *                    attribute on the JSP tags).
   * @param group_name The name of the action group the action belongs to (same
   *                   as the -actiongroup- attribute on the form tag).
   * @param paramlist The parameters to the action. A list of navigator variable
   *                  names.
   * @param sub_actions A list of actions to be invoked when this
   * action is invoked.
   */
  public static String registerData(PageContext pageContext,
                                    String action_name, String group_name,
                                    List paramlist, List sub_actions)
    throws JspTagException {
    return TagUtils.registerData(pageContext, action_name, group_name, paramlist,
                                 sub_actions, null);
  }
}
