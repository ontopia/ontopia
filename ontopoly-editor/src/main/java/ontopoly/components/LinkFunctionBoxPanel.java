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
package ontopoly.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.ResourceModel;

public abstract class LinkFunctionBoxPanel extends FunctionBoxPanel {

  public LinkFunctionBoxPanel(String id) {
    super(id);
  }

  @Override
  protected List<List<Component>> getFunctionBoxComponentList(String id) {
    List<Component> heading = Arrays.asList(new Component[] { getLabel(id) }); 
    List<Component> box = Arrays.asList(new Component[] {
        new Label(id, new ResourceModel("arrow.right")), getLink(id) });
    
    List<List<Component>> result = new ArrayList<List<Component>>(2);
    result.add(heading);
    result.add(box);
    return result;
  }

  protected abstract Component getLabel(String id);

  protected abstract Component getLink(String id);
  
}
