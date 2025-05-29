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

package net.ontopia.topicmaps.nav2.utils;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.jsp.PageContext;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.entry.TopicMapRepositoryIF;
import net.ontopia.topicmaps.nav2.core.NavigatorApplicationIF;
import net.ontopia.topicmaps.nav2.impl.basic.NavigatorApplication;
import net.ontopia.topicmaps.query.core.DeclarationContextIF;
import net.ontopia.topicmaps.query.parser.ParseContextIF;
import net.ontopia.topicmaps.query.parser.QName;

/**
 * INTERNAL: A utility class with miscellaneous helper methods used by
 * the navigator tag-libraries and the framework.
 */
public final class NavigatorUtils {

  /**
   * INTERNAL: convert String entries separated by whitespaces
   * to a ordered collection.
   *
   * @param strList delimiter separated list of string entries.
   */
  public static Collection string2Collection(String strList) {
    return string2Collection(strList, null);
  }
  
  /**
   * INTERNAL: convert String entries separated by whitespaces (default)
   * to a ordered collection.
   *
   * @param strList delimiter separated list of string entries.
   * @param delimiter characters which are used to separate the list entries.
   */
  public static Collection string2Collection(String strList,
                                                   String delimiter) {
    Collection list = new ArrayList();
    StringTokenizer st;

    // check if arguments ok, otherwise return empty collection
    if (strList == null || strList.equals("")) {
      return list;
    }

    // setup StringTokenizer
    if (delimiter == null) { 
      st = new StringTokenizer(strList);
    } else {
      st = new StringTokenizer(strList, delimiter);
    }

    // iterate over all tokens and add to list
    String str;
    while (st.hasMoreTokens()) {
      str = st.nextToken();
      list.add(str);
    }
    
    return list;
  }

  /**
   * INTERNAL: Tries to convert a string which should contain a subject
   * identifier, XML ID, or object ID to a <code>TopicIF</code> object
   * in the following order:   
   * <ul>
   *  <li>try to match a subject indicator (tm.getTopicBySubjectIdentifier)</li>
   *  <li>try to match a source (tm.getObjectByItemIdentifier)</li>
   *  <li>try to match a topic id (tm.getObjectById)</li>
   * </ul>
   *
   * @param tm      the topic map object
   * @param s       the String which should be investigated.
   * @return A matched topic otherwise null.
   */
  public static TopicIF stringID2Topic(TopicMapIF tm, String s) {
    TopicIF t = null;

    if (tm == null || s == null || s.equals("")) {
      return null;
    }
    
    // 1. try to match a subject indicator
    try {
      t = tm.getTopicBySubjectIdentifier(new URILocator(s));
    } catch (URISyntaxException e) {
      // apparently not a URL, so try something else
    }
   
    // 2. try to match a source
    if (t == null) {
      try {
        LocatorIF uri = tm.getStore().getBaseAddress();
        if (uri != null) {
          uri = uri.resolveAbsolute("#" + s);
          t = (TopicIF) tm.getObjectByItemIdentifier(uri);
        }
      } catch (ClassCastException e) {
        // if it's not a topic, we don't want it
      }
    }
        
    // 3. try to match a topic id
    if (t == null) {
      try {
        t = (TopicIF) tm.getObjectById(s);
      } catch (ClassCastException e) {
        // it's ok; we don't want it if it's not a topic
      }
    }

    return t;
  }
  
  /**
   * INTERNAL: Converts from a string of subject identities, source IDs or
   * object IDs separated by spaces (default) to a Collection of topics.
   * 
   * @param tm      the topic map
   * @param strList  separated list of identities and/or ids
   * @param delimiter characters which are used to separate the list entries.
   * @return A Collection of matched topics in the order they were listed.
   *         Arguments that do not match will be ignored.
   */
  public static Collection stringIDs2Topics(TopicMapIF tm,
                                                  String strList,
                                                  String delimiter) {
    Collection list = new ArrayList();
    StringTokenizer st;

    // check if arguments ok, otherwise return empty collection
    if (strList == null || tm == null || strList.equals("")) {
      return list;
    }

    // setup StringTokenizer
    if (delimiter == null) { 
      st = new StringTokenizer(strList);
    } else {
      st = new StringTokenizer(strList, delimiter);
    }

    // iterate over all tokens and try to find appropiate topic
    String str;
    TopicIF topic;
    while (st.hasMoreTokens()) {
      str = st.nextToken();
      topic = stringID2Topic(tm, str);
      if (topic != null) {
        list.add(topic);
      }
    }
    
    return list;
  }

  /**
   * INTERNAL: Tries to convert a string which should contain a subject
   * identifier, XML ID, or object ID to a <code>TMObjectIF</code>
   * object in the following order:   
   * <ul>
   *  <li>try to match a subject indicator (tm.getTopicBySubjectIdentifier)</li>
   *  <li>try to match a source locator (tm.getObjectByItemIdentifier)</li>
   *  <li>try to match a topic id (tm.getObjectById)</li>
   * </ul>
   *
   * @param tm      the topic map object
   * @param s       the String which should be investigated.
   * @return A matched object, or null.
   */
  public static TMObjectIF stringID2Object(TopicMapIF tm, String s) {
    return stringID2Object(tm, s, null);
  }

  /**
   * INTERNAL: Tries to convert a string which should contain a subject
   * identifier, XML ID, or object ID to a <code>TMObjectIF</code>
   * object in the following order:   
   * <ul>
   *  <li>try to match a subject indicator (tm.getTopicBySubjectIdentifier)</li>
   *  <li>try to match a source locator (tm.getObjectByItemIdentifier)</li>
   *  <li>try to match a topic id (tm.getObjectById)</li>
   *  <li>try to match a subject identifier with a prefix defined in the DeclarationContextIF</li>
   * </ul>
   *
   * @param tm      the topic map object
   * @param s       the String which should be investigated.
   * @param context the context to lookup the prefix in
   * @return A matched object, or null.
   */
  public static TMObjectIF stringID2Object(TopicMapIF tm, String s, DeclarationContextIF context) {
    TMObjectIF t = null;

    if (tm == null || s == null || s.equals("")) {
      return null;
    }
    
    // 1. try to match a subject indicator
    try {
      t = tm.getTopicBySubjectIdentifier(new URILocator(s));
    } catch (URISyntaxException e) {
      // apparently not a URL, so try something else
    }
   
    // 2. try to match a source
    if (t == null) {
      LocatorIF uri = tm.getStore().getBaseAddress();
      if (uri != null) {
        uri = uri.resolveAbsolute("#" + s);
        t = tm.getObjectByItemIdentifier(uri);
      }
    }
        
    // 3. try to match a topic id
    if (t == null) {
      t = tm.getObjectById(s);
    }

    // 4. try to match a prefixed subject identifier
    if ((t == null) && (context != null)) {
      try {
        ParseContextIF pc = (ParseContextIF) context;
        t = pc.getObject(new QName(s));
      } catch (Exception e) {
        // not found, ignore
      }
    }

    return t;
  }
  
  /**
   * INTERNAL: Gets the topic map repository used by the web application.
   *
   * @since 3.1
   */
  public static TopicMapRepositoryIF getTopicMapRepository(PageContext pageContext) {
    return getNavigatorApplication(pageContext.getServletContext()).getTopicMapRepository();
  }
  
  /**
   * INTERNAL: Gets the topic map repository used by the web application.
   *
   * @since 3.1
   */
  public static TopicMapRepositoryIF getTopicMapRepository(ServletContext servletContext) {
    return getNavigatorApplication(servletContext).getTopicMapRepository();
  }
  
  /**
   * INTERNAL: Gets the navigator application instance belonging to
   * the web application.
   */
  public static NavigatorApplicationIF getNavigatorApplication(PageContext pageContext) {
    return getNavigatorApplication(pageContext.getServletContext());
  }
  
  /**
   * INTERNAL: Gets the navigator application instance belonging to
   * the web application.
   */
  public static NavigatorApplicationIF getNavigatorApplication(ServletContext servletContext) {
  
    NavigatorApplicationIF navApp = (NavigatorApplicationIF)
      servletContext.getAttribute(NavigatorApplicationIF.NAV_APP_KEY);
  
    // If there is no current navigator application we need to create one.
    if (navApp == null) {
        // Initialise new configuration and register it with application context
      navApp = new NavigatorApplication(servletContext);
      servletContext.setAttribute(NavigatorApplicationIF.NAV_APP_KEY, navApp);
      servletContext.log("Setup navigator configuration and " +
                         "assigned it to application context.");
      }
    return navApp;
  }

  /**
   * INTERNAL: Returns a stable identifier for the topic map object.
   * This will either be the fragment of a source locator, or the
   * object ID if no suitable source locator is found.
   */
  public static String getStableId(TMObjectIF object) {
    if (!object.getItemIdentifiers().isEmpty()) {
      LocatorIF base = object.getTopicMap().getStore().getBaseAddress();
      if (base != null && base.getNotation().equals("URI")) {
        String basestr = base.getAddress() + "#";
        Iterator it = object.getItemIdentifiers().iterator();
        while (it.hasNext()) {
          LocatorIF loc = (LocatorIF) it.next();
          String locstr = loc.getAddress();
          if (locstr.startsWith(basestr)) {
            return locstr.substring(basestr.length());
          }
        }
      }
    }
    return object.getObjectId();
  }
  
}
