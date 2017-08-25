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
package ontopoly.utils;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.html.IHeaderResponse;

/**
 * Behaviour that uses JavaScript to assign focus to component on page load.
 * 
 * @author grove
 *
 */
public class FocusOnLoadBehaviour extends AbstractBehavior { 
  private Component component; 

  @Override
  public void bind(Component component) { 
    this.component = component; 
    component.setOutputMarkupId(true); 
  } 

  @Override
  public void renderHead(IHeaderResponse iHeaderResponse) { 
    super.renderHead(iHeaderResponse); 
    iHeaderResponse.renderOnLoadJavascript("var focusElement = document.getElementById('"+ component.getMarkupId() + "'); if (focusElement != null) focusElement.focus();"); 
  } 
 
  @Override
  public boolean isTemporary() {
    // remove the behavior after component has been rendered       
    return true;
  }

}
