// $Id: ThemeCategorizerTest.java,v 1.12 2008/06/12 14:37:17 geir.gronmo Exp $

package net.ontopia.topicmaps.nav.context.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.index.ScopeIndexIF;

import net.ontopia.utils.StringifierIF;
import net.ontopia.topicmaps.utils.TopicStringifiers;
import net.ontopia.topicmaps.utils.test.AbstractUtilsTestCase;
import net.ontopia.topicmaps.nav.context.ThemeCategorizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * test case for class ThemeCategorizer
 */
public class ThemeCategorizerTest extends AbstractUtilsTestCase {

  // initialize logging category
  static Logger log = LoggerFactory.getLogger(ThemeCategorizerTest.class.getName());

  
  public ThemeCategorizerTest(String name) {
    super(name);
  }

  /**
   * Inner class for defining Stringifier for Theme Class String
   */
  public class TestThemeClassStringifier implements StringifierIF {
    public String toString(Object object) {
      if (!(object instanceof String))
        throw new IllegalArgumentException("Must be String object!");
      return "category: " +  object + "\n";
    }
  }

  /**
   * Inner class for defining Stringifier for Theme Topic
   */
  public class TestThemeStringifier implements StringifierIF {
    protected StringifierIF stringifier = TopicStringifiers.getDefaultStringifier();

    public String toString(Object object) {
      if (!(object instanceof TopicIF))
        throw new IllegalArgumentException("Must be TopicIF object!");
      return " * " + stringifier.toString( object ) + "\n";
    }
  }

  /**
   * Inner class for defining Stringifier for a selected Theme Topic
   */
  public class TestSelectedThemeStringifier implements StringifierIF {
    protected StringifierIF stringifier = TopicStringifiers.getDefaultStringifier();

    public String toString(Object object) {
      if (!(object instanceof TopicIF))
        throw new IllegalArgumentException("Must be TopicIF object!");
      return " * " + stringifier.toString( object ) + " [selected]\n";
    }
  }

  /**
   * setting up topicmap, read it in from file
   */
  protected void setUp() {
    String filename = getTestDirectory() +
      FILE_SEPARATOR + "nav" + FILE_SEPARATOR + "context" + FILE_SEPARATOR +
      "opera.xtm";
    
    readFile( filename );
  }

  /**
   * TestCase for ThemeCategorizer.generateThemeList, <b>without</b> pre-selection
   */ 
  public void testOperaBasenameThemes() {
    
    // initialize
    ScopeIndexIF scopeIndex = (ScopeIndexIF) tm
      .getIndex("net.ontopia.topicmaps.core.index.ScopeIndexIF");

    // retrieve all base name themes
    Collection themes = scopeIndex.getTopicNameThemes();

    ThemeCategorizer categorizer = new ThemeCategorizer(tm, Collections.EMPTY_LIST);
    
    String result = categorizer.generateThemeList(categorizer.getThemeClasses(themes), 
                                                  new TestThemeClassStringifier(),
                                                  new TestThemeStringifier() );
    // test against expected result string
    assertTrue("Should be equal. Got String <" + result + ">",
           result.equals(expectedUnselected));
  }
  
  /**
   * TestCase for ThemeCategorizer.generateThemeList, <b>with</b> pre-selection
   * without stringifiers
   */ 
  public void testOperaBasenameThemesWithPreselection2() {
    
    // initialize
    ScopeIndexIF scopeIndex = (ScopeIndexIF) tm
      .getIndex("net.ontopia.topicmaps.core.index.ScopeIndexIF");

    // retrieve all base name themes
    Collection themes = scopeIndex.getTopicNameThemes();

    // fake some selected themes
    Collection selectedThemes = new ArrayList();
    selectedThemes.add( getTopic("le-villi") );
    selectedThemes.add( getTopic("boito") );
    
    ThemeCategorizer categorizer = new ThemeCategorizer(tm, Collections.EMPTY_LIST);
    
    String result = categorizer.generateThemeList(categorizer.getThemeClasses(themes),
                                                  selectedThemes,
                                                  "category: %className%\n",
                                                  " * %themeName% %selected%\n",
                                                  "[selected]");

    System.out.println("result: <<" + result + ">>");
    System.out.println("expected: ++" + expectedSelected + "++");
    
    // test against expected result string
    assertTrue("Should be equal. Got String <" + result + ">",
           result.equals(expectedSelected));
  }

  /**
   * TestCase for ThemeCategorizer.generateThemeList, <b>with</b> pre-selection
   */ 
  public void testOperaBasenameThemesWithPreselection() {
    
    // initialize
    ScopeIndexIF scopeIndex = (ScopeIndexIF) tm
      .getIndex("net.ontopia.topicmaps.core.index.ScopeIndexIF");

    // retrieve all base name themes
    Collection themes = scopeIndex.getTopicNameThemes();

    // fake some selected themes
    Collection selectedThemes = new ArrayList();
    selectedThemes.add( getTopic("le-villi") );
    selectedThemes.add( getTopic("boito") );
    
    ThemeCategorizer categorizer = new ThemeCategorizer(tm, Collections.EMPTY_LIST);
    
    String result = categorizer.generateThemeList(categorizer.getThemeClasses(themes),
                                                  selectedThemes,
                                                  new TestThemeClassStringifier(),
                                                  new TestThemeStringifier(),
                                                  new TestSelectedThemeStringifier());

    //System.out.println("result: <<" + result + ">>");
    //System.out.println("expected: ++" + expectedSelected + "++");
    
    // test against expected result string
    assertTrue("Should be equal. Got String <" + result + ">",
           result.equals(expectedSelected));
  }

  
  // -------------------------------------------------------------------------
  
  // expected result string
  String expectedUnselected = "category: [unspecified]\n" +
    " * character\n" +
    " * city\n" +
    " * composer\n" +
    " * containee\n" +
    " * container\n" +
    " * full name\n" +
    " * ISO 639-2:1996 Alpha-3 language codes (bibliographic)\n" +
    " * ISO 639-2:1996 Alpha-3 language codes (terminological)\n" +
    " * ISO 639:1988 language codes\n" +
    " * librettist\n" +
    " * method\n" +
    " * nom de guerre\n" +
    " * nom de plume\n" +
    " * normal form\n" +
    " * perpetrator\n" +
    " * place\n" +
    " * publisher\n" +
    " * pupil\n" +
    " * source\n" +
    " * style\n" +
    " * subclass\n" +
    " * superclass\n" +
    " * teacher\n" +
    " * theatre\n" +
    " * work\n" +
    " * writer\n" +
    "category: art form\n" +
    " * novel\n" +
    " * opera\n" +
    " * play\n" +
    " * poem\n" +
    "category: composer\n" +
    " * Boito, Arrigo\n" +
    " * Leoncavallo, Ruggero\n" +
    " * Mascagni, Pietro\n" +
    "category: language\n" +
    " * English\n" +
    " * French\n" +
    " * Italian\n" +
    "category: librettist\n" +
    " * Boito, Arrigo\n" +
    " * Leoncavallo, Ruggero\n" +
    "category: opera\n" +
    " * L'amico Fritz\n" +
    " * Pagliacci\n" +
    " * Le Villi\n";
  
  // expected result string (with some selections)
  String expectedSelected = "category: [unspecified]\n" +
    " * character\n" +
    " * city\n" +
    " * composer\n" +
    " * containee\n" +
    " * container\n" +
    " * full name\n" +
    " * ISO 639-2:1996 Alpha-3 language codes (bibliographic)\n" +
    " * ISO 639-2:1996 Alpha-3 language codes (terminological)\n" +
    " * ISO 639:1988 language codes\n" +
    " * librettist\n" +
    " * method\n" +
    " * nom de guerre\n" +
    " * nom de plume\n" +
    " * normal form\n" +
    " * perpetrator\n" +
    " * place\n" +
    " * publisher\n" +
    " * pupil\n" +
    " * source\n" +
    " * style\n" +
    " * subclass\n" +
    " * superclass\n" +
    " * teacher\n" +
    " * theatre\n" +
    " * work\n" +
    " * writer\n" +
    "category: art form\n" +
    " * novel\n" +
    " * opera\n" +
    " * play\n" +
    " * poem\n" +
    "category: composer\n" +
    " * Boito, Arrigo [selected]\n" +
    " * Leoncavallo, Ruggero\n" +
    " * Mascagni, Pietro\n" +
    "category: language\n" +
    " * English\n" +
    " * French\n" +
    " * Italian\n" +
    "category: librettist\n" +
    " * Boito, Arrigo [selected]\n" +
    " * Leoncavallo, Ruggero\n" +
    "category: opera\n" +
    " * L'amico Fritz\n" +
    " * Pagliacci\n" +
    " * Le Villi [selected]\n";

}





