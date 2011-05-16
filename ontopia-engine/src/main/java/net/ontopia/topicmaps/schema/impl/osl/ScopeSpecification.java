// $Id: ScopeSpecification.java,v 1.10 2008/01/10 11:08:49 geir.gronmo Exp $

package net.ontopia.topicmaps.schema.impl.osl;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.schema.core.ConstraintIF;
import net.ontopia.topicmaps.schema.core.TMObjectMatcherIF;

/**
 * INTERNAL: Represents a specification of a particular class of scopes.
 * Used by many constraint objects to represent their allowed scopes.
 */
public class ScopeSpecification {
  /**
   * INTERNAL: Means that the scope match must be exact.
   */
  public static final int MATCH_EXACT    = 0;
  /**
   * INTERNAL: Means that the allowed scope can be a superset of the
   * specified scope.
   */
  public static final int MATCH_SUPERSET = 1;
  /**
   * INTERNAL: Means that the allowed scope can be a subset of the
   * specified scope.
   */
  public static final int MATCH_SUBSET   = 2;
  
  protected Collection topicMatchers;
  protected int match;
  
  public ScopeSpecification() {
    this.topicMatchers = new ArrayList();
    this.match = MATCH_EXACT;
  }

  /**
   * INTERNAL: Returns a value indicating the match policy used.
   * The MATCH_* constants contain the allowed values.
   */
  public int getMatch() {
    return match;
  }
  
  /**
   * INTERNAL: Sets the match policy used. The MATCH_* constants
   * contain the allowed values.
   */
  public void setMatch(int match) {
    this.match = match;
  }

  /**
   * INTERNAL: Add a new allowed theme.
   */
  public void addThemeMatcher(TMObjectMatcherIF matcher) {
    topicMatchers.add(matcher);
  }

  /**
   * INTERNAL: Returns the matchers of the allowed themes.
   * @return A collection of TMObjectMatcherIF objects.
   */
  public Collection getThemeMatchers() {
    return topicMatchers;
  }

  /**
   * INTERNAL: Removes a topic matcher from the set of allowed themes.
   */
  public void removeThemeMatcher(TMObjectMatcherIF matcher) {
    topicMatchers.remove(matcher);
  }

  /**
   * INTERNAL: Matches the specified scope against that of a topic
   * map object (which must implement ScopedIF).
   */
  public boolean matches(TMObjectIF object) {
    if (!(object instanceof ScopedIF))
      return false;

    int number_found = 0;

    ScopedIF scoped = (ScopedIF) object;
    Collection themes = scoped.getScope();
    Collection matchers = new ArrayList(topicMatchers);
    Iterator it = themes.iterator();
    while (it.hasNext()) {
      TopicIF theme = (TopicIF)it.next();

      Iterator it2 = topicMatchers.iterator();
      while (it2.hasNext()) {
	TMObjectMatcherIF matcher = (TMObjectMatcherIF)it2.next();
	if (matcher.matches(theme)) {
	  matchers.remove(matcher);
	  number_found++;
	}
      }
    }

    boolean result = false;
    switch (match) {
    case MATCH_EXACT:
      // If the number we found equals the number of possible scopes
      // given in the schema, and the number of themes is the same,
      // then we have found a match
      result = (number_found == topicMatchers.size() && 
		number_found == scoped.getScope().size());
      break;
      
    case MATCH_SUPERSET:
      // If the number we found is less or equal to the possible
      // scopes given in the schema, and the number of themes is
      // equal, then we have found a match.
      result = (number_found <= topicMatchers.size() && 
		number_found == scoped.getScope().size());
      break;
      
    case MATCH_SUBSET:
      // If the number we found is equal (minimum) to the possible
      // scopes given in the schema, and the number of themes is equal
      // or greater, then we have found a match.
      result = (number_found == topicMatchers.size() && 
		number_found <= scoped.getScope().size());
      break;
      
    default:
      result = false;
    }
    return result;
  }
}






