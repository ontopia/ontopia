/*
 * #!
 * Ontopia Vizigator
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

package net.ontopia.topicmaps.viz;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.impl.remote.RemoteTopic;
import net.ontopia.topicmaps.impl.remote.RemoteTopicMapStore;
import net.ontopia.topicmaps.utils.tmrap.RemoteTopicIndex;
import net.ontopia.topicmaps.utils.tmrap.TopicPage;
import net.ontopia.utils.CollectionUtils;
import net.ontopia.utils.URIUtils;

/**
 * EXPERIMENTAL: Application context for the Vizlet.
 */
public class AppletContext extends ApplicationContext {
  private Vizlet vizlet;

  public AppletContext(Vizlet aVizlet) {
    super();
    vizlet = aVizlet;
  }

  /**
   * Opens the supplied url string. The window used to display the url is
   * defined by the applet parameter 'gotoTarget'
   */
  public void goToTopicURL(String url) {
    try {
      URL absurl = new URL(vizlet.getCodeBase(), url);

      String target = vizlet.getParameter("gototarget");
      if (target == null || target.length() == 0)
        vizlet.getAppletContext().showDocument(absurl);
      else
        vizlet.getAppletContext().showDocument(absurl, target);

    } catch (MalformedURLException e) {
      ErrorDialog.showError(vizlet, Messages.getString("Viz.BadUrl") + url);
    }
  }

  public void goToTopic(TopicIF topic) {
    Collection pages = getView().getPagesFor(topic);
    if (!pages.isEmpty()) {
      TopicPage page = (TopicPage) pages.iterator().next();
      goToTopicURL(page.getURL());
    }
  }

  /**
   * Opens the supplied url string in a browser window. Which window is used
   * is defined by the 'propTarget' applet parameter
   * 
   * @param url String representing the target url
   */
  public void openPropertiesURL(String url) {
    try {
      String target = vizlet.getParameter("proptarget");
      if (target == null || target.length() == 0)
        target = "_blank";
      vizlet.getAppletContext().showDocument(new URL(url), target);
    } catch (MalformedURLException e) {
      ErrorDialog.showError(vizlet, Messages.getString("Viz.BadUrl") + url);
    }
  }

  public boolean isApplet() {
    return true;
  }

  public void setStartTopic(TopicIF aTopic) {
    throw new UnsupportedOperationException("Cannot set start node in Vizlet");
  }

  public TopicIF getTopicForLocator(LocatorIF aLocator, TopicMapIF topicmap) {
    return getTopicFor(topicmap, Collections.singletonList(aLocator),
        Collections.EMPTY_LIST, null);
  }

  public void loadTopic(TopicIF aTopic) {
    ((RemoteTopic) aTopic).checkLoad();
  }

  public void focusNode(TMAbstractNode aNode) {
    getView().focusNode(aNode);
  }

  public void setScopingTopic(TopicIF aScope) {
    // Currently the applet does not use the configured scope
  }

  public TopicIF getDefaultScopingTopic(TopicMapIF aTopicmap) {
    String scopeType = vizlet.getParameter("scopetype");
    String scopeValue = vizlet.getParameter("scopevalue");

    if (scopeType == null || scopeValue == null || scopeType.length() == 0
        || scopeValue.length() == 0)
      // Get the scope topic from the config topicmap
      return getConfiguredScopingTopic(aTopicmap);

    // Otherwise, resolve the scoping topic from the applet parameters
    return getTopicFrom(aTopicmap, scopeType, scopeValue);
  }

  private TopicIF getTopicFrom(TopicMapIF aTopicmap, String type, 
                               String value) {
    LocatorIF locator = URIUtils.getURILocator(value);
    Set srclocs = Collections.EMPTY_SET;
    Set subjids = Collections.EMPTY_SET;
    Set sublocs = Collections.EMPTY_SET;
    if (type.equals("source"))
      srclocs = Collections.singleton(locator);
    else if (type.equals("indicator"))
      subjids = Collections.singleton(locator);
    else
      sublocs = Collections.singleton(locator);
    return getTopicFor(aTopicmap, subjids, srclocs, sublocs);
  }

  private TopicIF getConfiguredScopingTopic(TopicMapIF aTopicmap) {
    TopicIF scopingTopicHolder = getTmConfig().getScopingTopicHolder();
    Collection indicators = getCollectionFor(getLocatorFrom(getTmConfig()
        .getOccurrence(scopingTopicHolder, getTmConfig().getSubjectIndicator())));
    Collection locators = getCollectionFor(getLocatorFrom(getTmConfig()
        .getOccurrence(scopingTopicHolder, getTmConfig().getSourceLocator())));
    Collection subjects = getCollectionFor(getLocatorFrom(getTmConfig()
        .getOccurrence(scopingTopicHolder, getTmConfig().getSubject())));
    return getTopicFor(aTopicmap, indicators, locators, subjects);
  }

  private TopicIF getTopicFor(TopicMapIF aTopicmap,
                              Collection indicators,
                              Collection locators,
                              Collection subjects) {
    RemoteTopicMapStore store = (RemoteTopicMapStore)
      aTopicmap.getStore();
    RemoteTopicIndex tindex = store.getTopicIndex();
    Collection topics;
    if (!indicators.isEmpty() ||
        !locators.isEmpty() ||
        !subjects.isEmpty())
      topics = tindex.getTopics(indicators, locators, subjects);
    else
      return null;
    if (topics == null || topics.isEmpty())
      return null;
    return (TopicIF) CollectionUtils.getFirst(topics);
  }

  private Collection getCollectionFor(LocatorIF aLocator) {
    if (aLocator == null)
      return Collections.EMPTY_LIST;
    return Collections.singletonList(aLocator);
  }

  private LocatorIF getLocatorFrom(OccurrenceIF anOccurrence) {
    if (anOccurrence == null)
      return null;
    return anOccurrence.getLocator();
  }

  public TopicIF getStartTopic(TopicMapIF aTopicmap) {
    System.out.println("Loading start topic...");
    
    String idValue = vizlet.getParameter("idvalue");
    if (idValue == null)
      throw new VizigatorReportException("The required \"idvalue\" parameter" +
          " has not been set.");
    LocatorIF locator = URIUtils.getURILocator(idValue);
    
    String idtype = vizlet.getParameter("idtype");
    if (idtype == null)
      throw new VizigatorReportException("The required \"idtype\" parameter" +
          " has not been set. It should be set to \"indicator\", \"source\" " +
          "or \"subject\".");
    Collection indicators = (idtype.equals("indicator") ? Collections.singleton(locator) : Collections.EMPTY_SET);
    Collection sources = (idtype.equals("source") ? Collections.singleton(locator) : Collections.EMPTY_SET);
    Collection subject = (idtype.equals("subject") ? Collections.singleton(locator) : Collections.EMPTY_SET);
    RemoteTopicMapStore store = (RemoteTopicMapStore)aTopicmap.getStore();
    RemoteTopicIndex tindex = store.getTopicIndex();

    Collection topics = tindex.loadRelatedTopics(indicators, sources, subject,
                                                 true); // go 2 steps out
    return (TopicIF)CollectionUtils.getFirst(topics);
  }

  public String getConfigurl() {
    return vizlet.getResolvedParameter("config");
  }

  public String getTmrap() {
    return vizlet.getResolvedParameter("tmrap");
  }

  public String getTmid() {
    String retVal = vizlet.getParameter("tmid");
    if (retVal == null)
      throw new VizigatorReportException("The required \"tmid\" parameter " +
          "has not been set.");
    return retVal;
  }

  public int getDefaultLocality() {
    int locality = vizlet.getDefaultLocality();
    VizDebugUtils.debug("DesktopContext.getDefaultLocality - locality:" +
        locality);
    return locality;
  }

  public int getMaxLocality() {
    int maxLocality = vizlet.getMaxLocality();
    VizDebugUtils.debug("DesktopContext.getMaxLocality - maxLocality:" +
        maxLocality);
    return maxLocality;
  }

  public ParsedMenuFile getEnabledItemIds() {
    return vizlet.getEnabledItemIds();
  }

  public TypesConfigFrame getAssocFrame() {
    VizPanel vPanel = getVizPanel();
    return vPanel.getAssocFrame();
  }

  public TypesConfigFrame getTopicFrame() {
    VizPanel vPanel = getVizPanel();
    return vPanel.getTopicFrame();
  }
}
