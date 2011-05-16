
// $Id: BaseScopedTag.java,v 1.11 2003/07/28 10:07:26 larsga Exp $

package net.ontopia.topicmaps.nav2.taglibs.value;

import net.ontopia.utils.DeciderIF;
import net.ontopia.utils.FilterIF;
import net.ontopia.utils.DeciderFilter;

import net.ontopia.topicmaps.nav2.core.ScopeSupportIF;
import net.ontopia.topicmaps.nav2.utils.ScopeUtils;

/**
 * INTERNAL: Abstract Base class for value producing and accepting tags
 * which are taking the context filter into account.
 */
public abstract class BaseScopedTag extends BaseValueProducingAndAcceptingTag
  implements ScopeSupportIF {

  // tag attributes
  protected boolean useUserContextFilter;
  
  /**
   * INTERNAL: Get FilterIF object which provides the possibility to
   * decide if one topic map objects belongs to the wanted scope.
   * Default is to use the <code>IntersectionOfContextDecider</code>.
   *
   * @see net.ontopia.topicmaps.utils.IntersectionOfContextDecider
   * @see net.ontopia.topicmaps.utils.deciders.WithinScopeDecider
   */
  public DeciderIF getScopeDecider(int scopeType) {
    return ScopeUtils.getScopeDecider(pageContext, contextTag, scopeType);
  }

  /**
   * INTERNAL: Get FilterIF object which provides the possibility to
   * filter out topic map objects of a collection which have not the
   * wanted scope.
   */
  public FilterIF getScopeFilter(int scopeType) {
    DeciderIF decider = getScopeDecider(scopeType);
    if (decider == null)
      return null;
    else
      return new DeciderFilter(decider);
  }

  // -----------------------------------------------------------------
  // set methods for the tag attributes
  // -----------------------------------------------------------------

  /**
   * INTERNAL: sets up if the tag should use the context filter which is
   * implict contained in the user session. Default behaviour is to
   * not use the user context filter. Allowed values are:
   * <ul>
   *  <li>off</li>
   *  <li>user</li>
   * </ul>
   */
  public void setContextFilter(String contextFilter) {
    if (contextFilter.indexOf("off") != -1)
      this.useUserContextFilter = false;
    if (contextFilter.indexOf("user") != -1)
      this.useUserContextFilter = true;
  }
  
}
