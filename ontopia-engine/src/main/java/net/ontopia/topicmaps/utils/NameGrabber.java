/*
 * #!
 * Ontopia Engine
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

package net.ontopia.topicmaps.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.NameIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;

/**
 * INTERNAL: Grabber that grabs the most suitable name from a topic,
 * measured by whether it contains a particular theme in its scope.
 * If the topic has a variant name with the given theme the belonging
 * basename will be chosen. If not, the base name in the least
 * constrained scope will be chosen.
 *
 * @since 1.1
 */
public class NameGrabber implements Function<TopicIF, NameIF> {

  /**
   * PROTECTED: The subject indicator of the theme used to decide
   * suitability.
   */
  protected LocatorIF themeIndicator;

  protected boolean indicatorVariant;
  
  /**
   * INTERNAL: Alternatively to <code>themeIndicator</code> the
   * basename scope (collection of TopicIF objects) to decide
   * suitablity can be setup instantly.
   * @since 1.1.2
   */
  protected Collection<TopicIF> scope;
  
  /**
   * INTERNAL: A collection containing topic themes used for specifying
   * the variant name scope.
   * @since 1.2.1
   */
  protected Collection<TopicIF> variantScope;

  /**
   * INTERNAL: Determine if grab should deliver only the most
   * appropiate basename even if there is a more apropiate variant.
   * Default value is true.
   * @since 1.2.2
   */
  protected boolean grabOnlyTopicName = true;

  /**
   * INTERNAL: Creates the grabber and sets the comparator to be a
   * ScopedIFComparator using the least constrained scope. Grabs
   * names that have the topic defined by the given PSI locator
   * in their scope. By default the theme is used in the variant
   * scope.
   */
  public NameGrabber(LocatorIF themeIndicator) {
    this(themeIndicator, true);
  }
  
  /**
   * PUBLIC: Creates the grabber and sets the comparator to be a
   * ScopedIFComparator using the least constrained scope. Grabs
   * names that have the topic defined by the given PSI locator
   * in their scope.
   *
   * @param variant Whether to use the indicator to set the variant
   * scope (if false it is used for the base name scope)
   *
   * @since 2.0
   */
  public NameGrabber(LocatorIF themeIndicator, boolean variant) {
    this.themeIndicator = themeIndicator;
    this.indicatorVariant = variant;
    this.scope = Collections.emptySet();
    this.variantScope = Collections.emptySet();
  }
  
  /**
   * INTERNAL: Creates the grabber and sets the comparator to be a
   * ScopedIFComparator using the specified scope. Grabs
   * names that have most in common with specified scope.
   *
   * @since 1.1.2
   */
  public NameGrabber(Collection<TopicIF> scope) {
    this(scope, new HashSet<TopicIF>());
  }
  
  /**
   * INTERNAL: Creates the grabber and sets the comparator to be a
   * ScopedIFComparator using the specified scope. Grabs
   * base names that have most in common with specified scope.
   *
   * @since 1.2.1
   */
  public NameGrabber(Collection<TopicIF> basenameScope, Collection<TopicIF> variantScope) {
    this(basenameScope, variantScope, true);
  }

  /**
   * INTERNAL: Creates the grabber and sets the comparator to be a
   * ScopedIFComparator using the specified scope. Grabs names
   * (basename or variant, use <code>grabOnlyTopicName</code> to
   * specify the policy) that have most in common with specified
   * scope.
   *
   * @since 1.2.2
   */
  public NameGrabber(Collection<TopicIF> basenameScope, Collection<TopicIF> variantScope,
                     boolean grabOnlyTopicName) {
    this.themeIndicator = null;
    this.scope = basenameScope;
    this.variantScope = variantScope;
    this.grabOnlyTopicName = grabOnlyTopicName;
  }
  
  /**
   * INTERNAL: Sets the grab policy, if only instances of BasenameIF
   * should be returned by grab, or also the more appropiate
   * VariantIF.
   *
   * @since 1.2.2
   */
  public void setGrabOnlyTopicName(boolean grabOnlyTopicName) {
    this.grabOnlyTopicName = grabOnlyTopicName;
  }


  /**
   * INTERNAL: Gets the grab policy.
   * @see #setGrabOnlyTopicName(boolean)
   * @since 1.2.2
   */
  public boolean getGrabOnlyTopicName() {
    return grabOnlyTopicName;
  }
  
  /**
   * INTERNAL: Grabs the most appropiate base name (or if
   * <code>grabOnlyTopicName</code> is false allow also to return the
   * most appropiate VariantIF instance). The name returned is the
   * first suitable base name belonging to a variant name found, when
   * the basenames of the give topic have been sorted using the
   * comparator. If there is no suitable base name belonging to a
   * variant name, then the last base name found is returned,
   * corresponding to the least constrained scope.
   *
   * @param object The topic whose name is being grabbed; formally an object.
   * @return A name to display; an object implementing TopicNameIF
   *         (or VariantNameIF, see above) or null if the topic has no
   *         basenames.
   * @exception throws OntopiaRuntimeException if object is not a topic.
   */
  @Override
  public NameIF apply(TopicIF topic) {
    if (topic == null) {
      return null;
    }
    
    List<TopicNameIF> basenames = new ArrayList<TopicNameIF>(topic.getTopicNames());
    if (basenames.isEmpty()) {
      return null;
    }

    // If subject indicator of theme is set use this to setup scope
    if (themeIndicator != null) {
      // Get theme
      TopicMapIF tm = topic.getTopicMap();
      
      // Test if tm is null before getting theme. tm may be null if topic was
      // created (for internal purposes) in situations where no topic map was
      // available.
      if (tm != null) {
        TopicIF theme = tm.getTopicBySubjectIdentifier(themeIndicator);
        if (theme != null) {
          if (indicatorVariant) {
            variantScope = Collections.singleton(theme);
          } else {
            scope = Collections.singleton(theme);
          }
        }
      }
    }
    
    // sort the base names
    Collections.sort(basenames, new TopicNameComparator(scope));

    // TODO: Do we really have to create this grabber over and over again?
    VariantNameGrabber vngrabber = new VariantNameGrabber(variantScope);
    NameIF name = null;
    VariantNameIF vn = null;

    for (TopicNameIF current : basenames) {
      if (name == null) {
        name = current;
      }

      if (!variantScope.isEmpty()) {
        vn = vngrabber.apply(current);
        if (vn != null) {
          // TODO: Should not use intersection to find appropriate
          // variant, but rather exact matching, or perhaps ranking.
          
          // if there exists some overlap between variant name themes
          // and specified scope then we are delivering this variant
          Collection<TopicIF> interSection = new HashSet<TopicIF>(vn.getScope());
          interSection.retainAll(variantScope);
          if (!interSection.isEmpty()) {
            break;
          } else {
            vn = null;
          }
        }
      }
    } // while

    // if valid variant name get base name which belongs to this
    if (vn != null) {
      if (grabOnlyTopicName) {
        name = vn.getTopicName();
      } else {
        name = vn;
      }
    }

    return name;
  }

}
