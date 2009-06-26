// $Id: BasenameUserThemeFilterTest.java,v 1.9 2008/06/12 14:37:17 geir.gronmo Exp $

package net.ontopia.topicmaps.nav.context.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.index.ScopeIndexIF;

import net.ontopia.topicmaps.utils.test.AbstractUtilsTestCase;
import net.ontopia.topicmaps.nav.context.BasenameUserThemeFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test case for class BasenameUserThemeFilter
 * which provides access to filtering themes out
 * which are not relevant to the user context.
 */
public class BasenameUserThemeFilterTest extends AbstractUtilsTestCase {

  // initialize logging category
  static Logger log = LoggerFactory.getLogger(BasenameUserThemeFilterTest.class.getName());

  
  public BasenameUserThemeFilterTest(String name) {
    super(name);
  }

  public void testOperaFilteredBasenames() {
    String filename = getTestDirectory() + FILE_SEPARATOR +
      "nav" + FILE_SEPARATOR + "context" + FILE_SEPARATOR + "opera.xtm";
    
    readFile( filename );
    // setBase( filename );
    
    // initialize
    ScopeIndexIF scopeIndex = (ScopeIndexIF) tm.getIndex("net.ontopia.topicmaps.core.index.ScopeIndexIF");

    // retrieve all base name themes
    Collection themes = scopeIndex.getTopicNameThemes();
    assertTrue("Expected 39 basename themes total. Found: " + themes.size(),
           themes.size() == 39);
    
    BasenameUserThemeFilter filter = new BasenameUserThemeFilter( tm );

    // filter out base name themes which are not useful for user context
    Collection filteredThemes = filter.filterThemes( themes );
    assertTrue("Expected 5 basename themes after filtering. Found: " + filteredThemes.size(),
           filteredThemes.size() == 5);

    // this should be:
    //  * full name
    //  * nom de guerre
    //  * English
    //  * French
    //  * Italian
    
    // assertTrue("Expected to find topic <english> in filtered theme set", getTopic("english") != null);
    
  }
  
}





