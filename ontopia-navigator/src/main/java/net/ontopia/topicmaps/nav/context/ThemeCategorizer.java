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

package net.ontopia.topicmaps.nav.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.nav.utils.comparators.ContextNameGrabber;
import net.ontopia.topicmaps.nav.utils.comparators.TopicComparator;
import net.ontopia.topicmaps.utils.NameStringifier;
import net.ontopia.topicmaps.utils.PSI;
import net.ontopia.utils.GrabberStringifier;
import net.ontopia.utils.LexicalComparator;
import org.apache.commons.lang3.StringUtils;

/**
 * INTERNAL: Class for categorizing a collection of themes according to
 * their theme class.  The theme classes as well as the themes in each
 * theme class could be sorted.
 */
public class ThemeCategorizer {

  /** representation of theme class for themes which belong to no type */
  protected static final String STRING_NO_CLASS = "[unspecified]";
  
  /** Stringifier for theme class topics */
  protected Function<TopicIF, String> stringifier;
  /** for sorting the theme class strings */
  protected Comparator lexicalComparator;
  /** for sorting the theme topics */
  protected Comparator topicComparator;   

  /**
   * empty constructor.
   */
  public ThemeCategorizer(TopicMapIF tm, Collection context) {
    init(tm, context);
  }

  /**
   * INTERNAL: initialisation of Stringifier and Comparators
   */
  private void init(TopicMapIF tm, Collection context) {
    TopicIF display = tm.getTopicBySubjectIdentifier(PSI.getXTMDisplay());
    TopicIF sort = tm.getTopicBySubjectIdentifier(PSI.getXTMSort());
    
    // ----- initialisation
    // for output of theme class topic basename
    Collection vnc = new ArrayList(context);
    if (display != null) {
      vnc.add(display);
    }
    stringifier = new GrabberStringifier(new ContextNameGrabber(context, vnc),
                                         new NameStringifier());

    // for string sorting use case-insensitive Lexical Comparator
    lexicalComparator = LexicalComparator.CASE_SENSITIVE;

    // for topic sorting (themes)
    vnc = new ArrayList(context);
    if (sort != null) {
      vnc.add(sort);
    }    
    topicComparator = new TopicComparator(context, vnc);
  }

  /**
   * INTERNAL: Returns the internal stringifier used to stringify
   * topics correctly in the current context.
   */
  public Function<TopicIF, String> getTopicStringifier() {
    return stringifier;
  }
  
  /**
   * process <code>themes</code> and generate HashMap which
   * reflects theme categories.
   *
   * @return HashMap which contains as keys Strings
   *         of the theme type that lead to HasSet objects.
   *         These store TopicIF objects for the themes.
   */       
  public HashMap getThemeClasses(Collection themes) {
    // list of theme classes
    HashMap themeClassMap = new HashMap();
    themeClassMap.put(STRING_NO_CLASS, new HashSet());

    Iterator itThemes = themes.iterator();
    TopicIF actTheme = null;
    
    // ----- iterate through all base name themes
    while (itThemes.hasNext()) {
      actTheme = (TopicIF) itThemes.next();

      // find out theme classes of this theme
      Collection themeTypes = actTheme.getTypes();
      HashSet actSet;
      if (themeTypes.size() > 0) {
        Iterator itThemeTypes = themeTypes.iterator();
        TopicIF actThemeType = null;
        String actThemeTypeString;
        while (itThemeTypes.hasNext()) {
          actThemeType = (TopicIF) itThemeTypes.next();
          actThemeTypeString = stringifier.apply( actThemeType );
                                        
          // if theme class already exists, just get the set, otherwise create it
          if (themeClassMap.containsKey(actThemeTypeString)) {
            actSet = (HashSet) themeClassMap.get(actThemeTypeString);
          } else {
            actSet = new HashSet();
            themeClassMap.put(actThemeTypeString, actSet);
          }
          actSet.add(actTheme);

        } // while itThemeTypes
      } else {
        actSet = (HashSet) themeClassMap.get(STRING_NO_CLASS);
        actSet.add(actTheme);
      }
      
    } // while itThemes

    return themeClassMap;
  }

  /**
   * Generate a ordered list of theme classes.
   * Note: In every theme class there are at least one theme.
   */
  public String generateThemeList(HashMap themeClassMap,
                                  Function<String, String> stringifierThemeClass,
                                  Function<TopicIF, String> stringifierTheme) {
    return generateThemeList(themeClassMap, null,
                             stringifierThemeClass, stringifierTheme, null);
  }
  
  /**
   * Generate a ordered list of theme classes.
   * The themes which are found in the collection of selectedThemes use
   * their own stringifier <code>stringifierSelectedTheme</code>.
   * Note: In every theme class there are at least one theme.
   */
  public String generateThemeList(HashMap themeClassMap,
                                  Collection selectedThemes,
                                  Function<String, String> stringifierThemeClass,
                                  Function<TopicIF, String> stringifierTheme,
                                  Function<TopicIF, String> stringifierSelectedTheme) {
    if (themeClassMap == null
        || stringifierThemeClass == null
        || stringifierTheme == null) {
      return "";
    }
    
    StringBuilder strBuf = new StringBuilder();

    // ----- loop over all themes classes
    List themeClassList = new ArrayList( themeClassMap.keySet() );
    Collections.sort( themeClassList, lexicalComparator );
    Iterator itThemeClasses = themeClassList.iterator();
    String actThemeClass;

    HashSet actSet;
    List themeList;
    Iterator itRelThemes;
    TopicIF actTheme;
    
    while (itThemeClasses.hasNext()) {
      actThemeClass = (String) itThemeClasses.next();
      actSet = (HashSet) themeClassMap.get(actThemeClass);

      // only proceed if category name has related themes
      if (actSet.size() > 0) {
        // append string representation for theme class
        strBuf.append( stringifierThemeClass.apply( actThemeClass) );
        themeList = new ArrayList( actSet );
        Collections.sort( themeList, topicComparator );
        itRelThemes = themeList.iterator();
        while (itRelThemes.hasNext()) {
          actTheme = (TopicIF) itRelThemes.next();
          // append string representation for theme
          if (selectedThemes != null && stringifierSelectedTheme != null) {
            if (selectedThemes.contains(actTheme)) {
              strBuf.append( stringifierSelectedTheme.apply( actTheme ));
            } else {
              strBuf.append( stringifierTheme.apply( actTheme ));
            }  
          } else {
            strBuf.append( stringifierTheme.apply( actTheme ));
          }
        } // while itRelThemes
      }

    } // while itThemeClasses
    
    return strBuf.toString();
  }


  /**
   * Generate a ordered list of theme classes.
   * Use template strings to render result string, this approach
   * is used by the navigator framework 2nd generation.
   * Note: In every theme class there are at least one theme.
   */
  public String generateThemeList(HashMap themeClassMap,
                                  Collection selectedThemes,
                                  String templThemeClass,
                                  String templTheme,
                                  String templSelectedTheme) {
    if (themeClassMap == null
        || templThemeClass == null
        || templTheme == null) {
      return "";
    }
    if (templSelectedTheme == null) {
      templSelectedTheme = "";
    }
    
    StringBuilder strBuf = new StringBuilder();

    // ----- loop over all themes classes
    List themeClassList = new ArrayList( themeClassMap.keySet() );
    Collections.sort( themeClassList, lexicalComparator );
    Iterator itThemeClasses = themeClassList.iterator();
    String actThemeClass;

    HashSet actSet;
    List themeList;
    Iterator itRelThemes;
    TopicIF actTheme;
    String tmp;
    
    while (itThemeClasses.hasNext()) {
      actThemeClass = (String) itThemeClasses.next();
      actSet = (HashSet) themeClassMap.get(actThemeClass);

      // only proceed if category name has related themes
      if (actSet.size() > 0) {
        // -- append string representation for theme class
        strBuf.append(StringUtils.replace(templThemeClass, "%className%",
                                          actThemeClass));

        themeList = new ArrayList( actSet );
        Collections.sort( themeList, topicComparator );
        itRelThemes = themeList.iterator();
        while (itRelThemes.hasNext()) {
          actTheme = (TopicIF) itRelThemes.next();
          // a: replace theme name
          tmp = StringUtils.replace(templTheme, "%themeName%",
                                    stringifier.apply(actTheme));
          // b: replace theme id
          tmp = StringUtils.replace(tmp, "%themeId%",
                                    actTheme.getObjectId());
          // c: if selected, replace selected template
          if (selectedThemes != null && selectedThemes.contains(actTheme)) {
            tmp = StringUtils.replace(tmp, "%selected%",
                                      templSelectedTheme);
          } else {
            tmp = StringUtils.replace(tmp, "%selected%", "");
          }

          // -- append string representation for theme
          strBuf.append( tmp );

        } // while itRelThemes
      }

    } // while itThemeClasses
    
    return strBuf.toString();
  }
}
