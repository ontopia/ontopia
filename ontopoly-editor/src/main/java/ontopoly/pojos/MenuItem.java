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
package ontopoly.pojos;

import java.io.Serializable;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;

/** Class for representing a menu item for our application. */
public class MenuItem implements Serializable {

  /** the caption of the menu item */
  private Label caption;

  /** the (bookmarkable) page the menu item links to */
  private Class<? extends Page> pageClass;

  /** the (bookmarkable) page the menu item links to */
  private PageParameters pageParameters;

  public MenuItem(Label caption, Class<? extends Page> destination,
      PageParameters pageParameters) {
    this.caption = caption;
    this.pageClass = destination;
    this.pageParameters = pageParameters;
  }
  
  public Label getCaption() {
    return caption;
  }

  public Class<? extends Page> getPageClass() {
    return pageClass;
  }

  public PageParameters getPageParameters() {
    return pageParameters;
  }
}
