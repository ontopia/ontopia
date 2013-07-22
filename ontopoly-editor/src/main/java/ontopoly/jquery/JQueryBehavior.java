/*
 * #!
 * Ontopoly Editor
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
package ontopoly.jquery;

import ontopoly.resources.Resources;

import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.resources.JavascriptResourceReference;

public abstract class JQueryBehavior extends AbstractDefaultAjaxBehavior {

  public static final JavascriptResourceReference JS_JQUERY = new JavascriptResourceReference(Resources.class, "jquery/jquery.js"); 
  public static final JavascriptResourceReference JS_JQUERY_UI = new JavascriptResourceReference(Resources.class, "jquery/jquery.ui.js"); 
  
  public void renderHead(IHeaderResponse response) {
    super.renderHead(response);
    response.renderJavascriptReference(JS_JQUERY);
    response.renderJavascriptReference(JS_JQUERY_UI);
  }
  
}
