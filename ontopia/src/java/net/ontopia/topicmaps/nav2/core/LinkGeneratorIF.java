// $Id: LinkGeneratorIF.java,v 1.9 2004/11/29 19:22:58 grove Exp $

package net.ontopia.topicmaps.nav2.core;

import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;

/**
 * INTERNAL: interface for classes which
 * implement generating URI links.
 * Used by the <code>link</code> tag.
 *
 * @see net.ontopia.topicmaps.nav2.taglibs.output.LinkTag
 */
public interface LinkGeneratorIF {

  /**
   * INTERNAL: Constant String value representing the name of a
   * template variable being replaced with the object id.
   */
  public static final String LINK_ID_KEY = "%id%";

  /**
   * INTERNAL: Constant String value representing the name of a
   * template variable being replaced with the topicmap id.
   */
  public static final String LINK_TOPICMAP_KEY = "%topicmap%";
  
  /**
   * INTERNAL: create a String which contains link
   * information for a Topic Map Object.
   * <p>
   * Note: This String gets converted
   * to an URL encoded format by the <code>link</code>
   * tag automatically afterwards.
   *
   * @param contextTag A ContextTag object providing access to
   *             all important information to the application.
   * @param tmObj An Object implementing TMObjectIF to which the link
   *              should direct.
   * @param topicmapId The id that the topic map has in the registry.
   * @param template A template string which may contain
   *                 template variable(s) which are replaced.
   */
  public String generate(ContextTag contextTag, TMObjectIF tmObj,
                         String topicmapId, String template)
    throws NavigatorRuntimeException;
  
  /**
   * INTERNAL: create a String which contains link
   * information to a Topicmap retrieved with the help
   * of an TopicMapReferenceIF object.
   * <p>
   * Note: This String gets converted
   * to an URL encoded format by the <code>link</code>
   * tag automatically afterwards.
   *
   * @param contextTag A ContextTag object providing access to
   *             all important information to the application.
   * @param tmRefObj An Object implementing TopicMapReferenceIF
   *                 to which beloning Topic Map the link should direct.
   * @param template A template string which may contain
   *                 template variable(s) which are replaced.
   */
  public String generate(ContextTag contextTag, TopicMapReferenceIF tmRefObj,
                         String template)
    throws NavigatorRuntimeException;
  
}





