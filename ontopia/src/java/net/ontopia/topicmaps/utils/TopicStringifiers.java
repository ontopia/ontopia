
// $Id: TopicStringifiers.java,v 1.24 2008/12/04 11:30:33 lars.garshol Exp $

package net.ontopia.topicmaps.utils;

import java.util.Collection;
import java.util.Collections;
import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.TopicIF;

/**
 * PUBLIC: Creates stringifiers that extract strings representing
 * names from topics, according to various criteria, including scope.
 */

public class TopicStringifiers {

  private static final StringifierIF DEFAULT_STRINGIFIER
    = new GrabberStringifier(TopicCharacteristicGrabbers.getDisplayNameGrabber(),
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
  public static StringifierIF getDefaultStringifier() {
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
  public static StringifierIF getTopicNameStringifier(Collection scope) {
    return new GrabberStringifier(new TopicNameGrabber(scope),
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
  public static StringifierIF getVariantNameStringifier(Collection scope) {
    return new GrabberStringifier(new TopicVariantNameGrabber(scope),
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
  public static StringifierIF getSortNameStringifier() {
    return new GrabberStringifier(TopicCharacteristicGrabbers.getSortNameGrabber(),
                                  new NameStringifier());
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
  public static StringifierIF getStringifier(Collection tnscope,
                                             Collection vnscope) {
    if (tnscope == null || tnscope.isEmpty()) {
      if (vnscope == null || vnscope.isEmpty())
        return getDefaultStringifier();
      else
        return getVariantNameStringifier(vnscope);
    } else if (vnscope == null || vnscope.isEmpty())
      return getTopicNameStringifier(tnscope);
    else
      return new GrabberStringifier(new NameGrabber(tnscope, vnscope, false),
                                    new NameStringifier());
  }

  /**
   * PUBLIC: Returns the default name of the topic.
   *
   * @since 2.0
   */
  public static String toString(TopicIF topic) {
    return DEFAULT_STRINGIFIER.toString(topic);
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
    StringifierIF strfy = getStringifier(Collections.singleton(tntheme), null);
    return strfy.toString(topic);
  }

  /**
   * PUBLIC: Returns the name of the topic given the specified
   * topic name theme.
   *
   * @since 2.0
   */
  public static String toString(TopicIF topic, Collection tnscope) {
    StringifierIF strfy = getStringifier(tnscope, null);
    return strfy.toString(topic);
  }


  /**
   * PUBLIC: Returns the name of the topic given the specified
   * topic name and variant name themes.
   *
   * @since 2.0
   */
  public static String toString(TopicIF topic, TopicIF tntheme, TopicIF vntheme) {
    StringifierIF strfy = getStringifier(Collections.singleton(tntheme), Collections.singleton(vntheme));
    return strfy.toString(topic);
  }


  /**
   * PUBLIC: Returns the name of the topic given the specified
   * topic name and variant name themes.
   *
   * @since 2.0
   */
  public static String toString(TopicIF topic, Collection tnscope, TopicIF vntheme) {
    StringifierIF strfy = getStringifier(tnscope, Collections.singleton(vntheme));
    return strfy.toString(topic);
  }


  /**
   * PUBLIC: Returns the name of the topic given the specified
   * topic name and variant name themes.
   *
   * @since 2.0
   */
  public static String toString(TopicIF topic, Collection tnscope, Collection vnscope) {
    StringifierIF strfy = getStringifier(tnscope, vnscope);
    return strfy.toString(topic);
  }
  
}
