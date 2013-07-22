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
package ontopoly.models;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.model.AbstractReadOnlyModel;

public class HelpLinkResourceModel extends AbstractReadOnlyModel<String> {

  private String resourceKey;
  
  public HelpLinkResourceModel(String resourceKey) {
    this.resourceKey = resourceKey;
  }

  @Override
  public String getObject() {
    return RequestCycle.get().getRequest().getRelativePathPrefixToContextRoot() + "doc/" +
      Application.get().getResourceSettings().getLocalizer().getString(resourceKey, (Component)null, (String)null);
  }
}
