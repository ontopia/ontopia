
package net.ontopia.topicmaps.viz;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;

/**
 * INTERNAL: Abstract class which provides access to filtering themes
 * out which are not relevant to the user context.
 */
public abstract class AbstractUserThemeFilter {

  TopicMapIF topicMap;

  public AbstractUserThemeFilter() {
    this.topicMap = null;
  }

  public AbstractUserThemeFilter(TopicMapIF topicMap) {
    this.topicMap = topicMap;
  }

  public TopicMapIF getTopicMap() {
    return this.topicMap;
  }

  public void setTopicMap(TopicMapIF topicMap) {
    this.topicMap = topicMap;
  }

  //
  // helper methods
  //
  
  /**
   * Use method <code>shouldNotBeUsed</code> for every theme
   * of the collection of <code>unfilteredThemes</code> and
   * allows therefore to retrieve a filtered collection of themes.
   */
  public Collection filterThemes(Collection unfilteredThemes) {
    Collection themes = new ArrayList();
    TopicIF actTheme = null;
    Iterator it = unfilteredThemes.iterator();
    while (it.hasNext()) {
      actTheme = (TopicIF) it.next();
      if (!shouldNotBeUsed(actTheme))
        themes.add( actTheme ); 
    }

    return themes;
  }
  
  public abstract boolean shouldNotBeUsed(TopicIF actTheme);
  
}





