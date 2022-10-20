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

import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import net.ontopia.topicmaps.core.NameIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.utils.GrabberStringifier;

/**
 * PUBLIC: Creates stringifiers that extract strings representing
 * names from topics, according to various criteria, including scope.
 */
public class TopicStringifiers {
  private static final Function<TopicIF, String> DEFAULT_STRINGIFIER
    = new GrabberStringifier<TopicIF, NameIF>(TopicCharacteristicGrabbers.getDisplayNameGrabber(),
                             new NameStringifier());

  private TopicStringifiers() {
    // don't call me
  }
  
  /**
   * PUBLIC: Gets a stringifier that will return a default name for each
   * topic it is applied to. 
   *
   * @return stringifierIF; the string this returns will be the
   *    display name of its given topic if present, otherwise
   *    the least constrained topic name.
   */
  public static Function<TopicIF, String> getDefaultStringifier() {
    return DEFAULT_STRINGIFIER;
  }

  /**
   * PUBLIC: Gets a stringifier that will return the topic name it
   * determines to match the given scope best. There are no guarantees
   * as to <em>which</em> topic name it will return if more than one
   * topic name match equally well.
   *
   * @param scope collection of TopicIF objects; the given scope
   * @return stringifierIF; the string this stringifier returns will be a
   *    topic name of its given topic, selected according to the 
   *    logic in TopicNameGrabber.
   */
  public static Function<TopicIF, String> getTopicNameStringifier(Collection<TopicIF> scope) {
    return new GrabberStringifier<TopicIF, TopicNameIF>(new TopicNameGrabber(scope),
                                  new NameStringifier());
  }

  /**
   * PUBLIC: Gets a stringifier that will return
   * the variant that it determines to match the given scope
   * best. There are no guarantees as to <em>which</em> variant name
   * it will return if more than one variant name matches equally well.
   *
   * @param scope collection of TopicIF objects; the given scope
   * @return stringifierIF; the string this stringifier returns will be a
   *    variant name of its given topic, selected according to the 
   *    logic in VariantNameGrabber.
   */  
  public static Function<TopicIF, String> getVariantNameStringifier(Collection<TopicIF> scope) {
    return new GrabberStringifier<TopicIF, VariantNameIF>(new TopicVariantNameGrabber(scope),
                                  new NameStringifier());
  }

  /**
   * PUBLIC: Gets a stringifier that will return the sort names of
   * topics, when they have one. If the topics have no sort name,
   * one of the topic names will be used instead.
   *
   * @return StringifierIF; the string this stringifier returns will be a
   *    variant name of its given topic, selected according to the 
   *    logic in SortNameGrabber.
   * @since 1.1
   */
  public static Function<TopicIF, String> getSortNameStringifier() {
    return new GrabberStringifier<TopicIF, NameIF>(TopicCharacteristicGrabbers.getSortNameGrabber(),
                                  new NameStringifier());
  }

  /**
   * PUBLIC: Gets a fast stringifier that will return the sort names
   * of topics, when they have one. If the topics have no sort name,
   * one of the topic names will be used instead. This stringifier is
   * the one used by tolog.
   * @since 5.1.0
   */
  public static Function<TopicIF, String> getFastSortNameStringifier(TopicMapIF tm) {
    return new FastSortNameStringifier(tm);
  }

  /**
   * PUBLIC: Gets a stringifier that will return the name it
   * determines matches the given scopes best. There is no guarantee
   * as to <em>which</em> name it will return if more than one name
   * matches equally well.
   *
   * @param tnscope collection of TopicIF objects; the scope applied to
   *                topic names
   * @param vnscope collection of TopicIF objects; the scope applied to
   *                variant names
   * @return the configured stringifier
   * @since 1.3.2
   */  
  public static Function<TopicIF, String> getStringifier(Collection<TopicIF> tnscope,
                                             Collection<TopicIF> vnscope) {
    if (tnscope == null || tnscope.isEmpty()) {
      if (vnscope == null || vnscope.isEmpty()) {
        return getDefaultStringifier();
      } else {
        return getVariantNameStringifier(vnscope);
      }
    } else if (vnscope == null || vnscope.isEmpty()) {
      return getTopicNameStringifier(tnscope);
    } else {
      return new GrabberStringifier<TopicIF, NameIF>(new NameGrabber(tnscope, vnscope, false),
              new NameStringifier());
    }
  }

  /**
   * PUBLIC: Returns the default name of the topic.
   *
   * @since 2.0
   */
  public static String toString(TopicIF topic) {
    return DEFAULT_STRINGIFIER.apply(topic);
  }

  // FIXME: The following methods must later be optimized by
  // refactoring stringifier code into static methods, so that we do
  // not allocate new objects every time.

  /**
   * PUBLIC: Returns the name of the topic given the specified
   * topic name theme.
   *
   * @since 2.0
   */
  public static String toString(TopicIF topic, TopicIF tntheme) {
    Function<TopicIF, String> strfy = getStringifier(Collections.singleton(tntheme), null);
    return strfy.apply(topic);
  }

  /**
   * PUBLIC: Returns the name of the topic given the specified
   * topic name theme.
   *
   * @since 2.0
   */
  public static String toString(TopicIF topic, Collection<TopicIF> tnscope) {
    Function<TopicIF, String> strfy = getStringifier(tnscope, null);
    return strfy.apply(topic);
  }

  /**
   * PUBLIC: Returns the name of the topic given the specified
   * topic name and variant name themes.
   *
   * @since 2.0
   */
  public static String toString(TopicIF topic, TopicIF tntheme, TopicIF vntheme) {
    Function<TopicIF, String> strfy = getStringifier(Collections.singleton(tntheme), Collections.singleton(vntheme));
    return strfy.apply(topic);
  }


  /**
   * PUBLIC: Returns the name of the topic given the specified
   * topic name and variant name themes.
   *
   * @since 2.0
   */
  public static String toString(TopicIF topic, Collection<TopicIF> tnscope, TopicIF vntheme) {
    Function<TopicIF, String> strfy = getStringifier(tnscope, Collections.singleton(vntheme));
    return strfy.apply(topic);
  }


  /**
   * PUBLIC: Returns the name of the topic given the specified
   * topic name and variant name themes.
   *
   * @since 2.0
   */
  public static String toString(TopicIF topic, Collection<TopicIF> tnscope, Collection<TopicIF> vnscope) {
    Function<TopicIF, String> strfy = getStringifier(tnscope, vnscope);
    return strfy.apply(topic);
  }

  // ===== INTERNAL

  public static class FastSortNameStringifier implements Function<TopicIF, String> {
    private TopicIF defnametype;
    private TopicIF sort;

    public FastSortNameStringifier(TopicMapIF tm) {
      this.defnametype = tm.getTopicBySubjectIdentifier(PSI.getSAMNameType());
      this.sort = tm.getTopicBySubjectIdentifier(PSI.getXTMSort());
    }

    @Override
    public String apply(TopicIF topic) {
      // 0: verify that we have a topic at all
      if (topic == null) {
        return "[No name]";
      }

      // 1: pick base name with the fewest topics in scope
      //    (and avoid typed names)
      TopicNameIF bn = null;
      int least = 0xEFFF;
      Collection<TopicNameIF> bns = topic.getTopicNames();
      if (!bns.isEmpty()) {
        Iterator<TopicNameIF> it = bns.iterator();
        while (it.hasNext()) {
          TopicNameIF candidate = it.next();
          int score = candidate.getScope().size() * 10;
          if (candidate.getType() != defnametype) {
            score++;
          }
          
          if (score < least) {
            bn = candidate;
            least = score;
          }
        }
      }
      if (bn == null) {
        return "[No name]";
      }
      
      // 2: if we have a sort name, pick variant with fewest topics in scope
      //    beyond sort name; penalty for no sort name = 0xFF topics
      if (sort == null) {
        return bn.getValue();
      }
      VariantNameIF vn = null;
      least = 0xEFFF;
      Collection<VariantNameIF> vns = bn.getVariants();
      if (!vns.isEmpty()) {
        Iterator<VariantNameIF> it = vns.iterator();
        while (it.hasNext()) {
          VariantNameIF candidate = it.next();
          Collection<TopicIF> scope = candidate.getScope();
          int themes;
          if (scope.contains(sort)) {
            themes = scope.size() - 1;
          } else {
            themes = 0xFF + scope.size();
          }
          if (themes < least) {
            vn = candidate;
            least = themes;
          }
        }
      }
      if (vn == null || vn.getValue() == null) {
        return bn.getValue();
      }
      return vn.getValue();
    }
  }
}
