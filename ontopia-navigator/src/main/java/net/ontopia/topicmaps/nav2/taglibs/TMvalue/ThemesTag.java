/*
 * #!
 * Ontopia Navigator
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

package net.ontopia.topicmaps.nav2.taglibs.TMvalue;

import java.util.Collection;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.index.ScopeIndexIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.core.ValueAcceptingTagIF;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;

/**
 * INTERNAL: Value Producing Tag for finding all topics that are used as
 * themes in scopes, either in general, or of a specific kind of
 * topic map object.
 */
public class ThemesTag extends TagSupport {

  // constants
  public static final String THEME_ASSOC    = "association";
  public static final String THEME_BASENAME = "basename";
  public static final String THEME_VARIANT  = "variant";
  public static final String THEME_OCC      = "occurrence";
  
  // tag attributes
  private String themeName;

  /**
   * Process the start tag for this instance.
   */
  @Override
  public int doStartTag() throws JspTagException {

    // retrieve parent tag which accepts the result of this value producing op.
    ValueAcceptingTagIF acceptingTag = (ValueAcceptingTagIF)
      findAncestorWithClass(this, ValueAcceptingTagIF.class);

    // try to retrieve default value from ContextManager
    ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);

    // get topicmap object on which we should compute 
    TopicMapIF topicmap = contextTag.getTopicMap();
    if (topicmap == null) {
      throw new NavigatorRuntimeException("ThemesTag found no topic map.");
    }
    
    // get class instance index
    ScopeIndexIF index = (ScopeIndexIF) topicmap.getIndex("net.ontopia.topicmaps.core.index.ScopeIndexIF");

    // find all topics used to define themes in scopes
    Collection resColl = null;
    if (themeName == null) {
      resColl = new java.util.HashSet();
      resColl.addAll(index.getAssociationThemes());
      resColl.addAll(index.getTopicNameThemes());
      resColl.addAll(index.getOccurrenceThemes());
      resColl.addAll(index.getVariantThemes());
    }
    else if (themeName.equals(THEME_ASSOC)) {
      resColl = index.getAssociationThemes();
    } else if (themeName.equals(THEME_BASENAME)) {
      resColl = index.getTopicNameThemes();
    } else if (themeName.equals(THEME_OCC)) {
      resColl = index.getOccurrenceThemes();
    } else if (themeName.equals(THEME_VARIANT)) {
      resColl = index.getVariantThemes();
    }
    
    // kick it over to the accepting tag
    acceptingTag.accept(resColl);

    // empty tag
    return SKIP_BODY;
  }

  /**
   * reset the state of the Tag.
   */
  @Override
  public void release() {
    // overwrite default behaviour
    // do not set parent to null!!!
  }

  // -----------------------------------------------------------------
  // set methods for tag attributes
  // -----------------------------------------------------------------

  /**
   * Sets the theme-type for topics that should looked up.
   *
   * @param themeName String which should contain one of the following
   *        values: topic | association | occurrence | basename | variant
   */
  public void setOf(String themeName) {
    if (themeName.equals(THEME_ASSOC)
        || themeName.equals(THEME_BASENAME)
        || themeName.equals(THEME_VARIANT)
        || themeName.equals(THEME_OCC)) {
        this.themeName = themeName;
    } else {
      throw new IllegalArgumentException("Invalid theme name '" + themeName +
                                         "' in attribute 'of' " +
                                         " of element 'scopes'.");
    }
  }

}
