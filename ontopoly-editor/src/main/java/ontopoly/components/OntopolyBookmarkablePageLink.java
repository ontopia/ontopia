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

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.util.string.Strings;

public class OntopolyBookmarkablePageLink extends BookmarkablePageLink<Page> {
  private String label;

  public OntopolyBookmarkablePageLink(String id, Class<? extends Page> pageClass) {
    super(id, pageClass);
    label = null;
  }

  public OntopolyBookmarkablePageLink(String id, Class<? extends Page> pageClass,
      PageParameters parameters) {
    super(id, pageClass, parameters);
    label = null;
  }

  public OntopolyBookmarkablePageLink(String id, Class<? extends Page> pageClass, String label) {
    super(id, pageClass);
    this.label = label;
  }

  public OntopolyBookmarkablePageLink(String id, Class<? extends Page> pageClass,
      PageParameters parameters, String label) {
    super(id, pageClass, parameters);
    this.label = label;
  }

  @Override
  protected void onComponentTag(ComponentTag tag) {
    tag.setName("a");
    super.onComponentTag(tag);
  }

  @Override
  public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
    if (label != null) {
      replaceComponentTagBody(markupStream, openTag, 
          "<span>" + Strings.escapeMarkup(label) + "</span>");
    }
  }
}
