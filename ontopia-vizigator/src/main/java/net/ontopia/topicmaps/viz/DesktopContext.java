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

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;

/**
 * EXPERIMENTAL: Information for the VizDesktop version of Vizigator
 */
public class DesktopContext extends ApplicationContext {
  private VizDesktop desktop;

  public DesktopContext(VizDesktop aDesktop) {
    super();
    desktop = aDesktop;
  }

  @Override
  public void goToTopic(TopicIF topic) {
    ErrorDialog.showError(getVizPanel(), Messages
        .getString("Viz.GotoTopicNotAvailable"));
  }

  @Override
  public boolean isApplet() {
    return false;
  }

  @Override
  public void openPropertiesURL(String aUrl) {
    // Not supported on Desktop mode.
  }

  @Override
  public void setStartTopic(TopicIF aTopic) {
    getTmConfig().setStartTopic(aTopic);
    desktop.resetStartTopicMenu();
    desktop.resetClearStartMenu();
  }

  @Override
  public TopicIF getTopicForLocator(LocatorIF aLocator, TopicMapIF topicmap) {
    return topicmap.getTopicBySubjectIdentifier(aLocator);
  }

  @Override
  public void loadTopic(TopicIF aTopic) {
    // In the desktop, all information is loaded up front.
    // No real need to do anything here.
  }

  @Override
  public void focusNode(TMAbstractNode aNode) {
    if (aNode != null) {
      getView().focusNode(aNode);
    }

    desktop.resetMapViewMenu();
    desktop.resetClearStartMenu();
    desktop.resetStartTopicMenu();
  }

  @Override
  public void setScopingTopic(TopicIF aScope) {
    desktop.setScopingTopic(aScope);
  }

  @Override
  public TopicIF getDefaultScopingTopic(TopicMapIF aTopicmap) {
    return getTmConfig().getScopingTopic(aTopicmap);
  }

  @Override
  public TopicIF getStartTopic(TopicMapIF aTopicmap) {
    return getTmConfig().getStartTopic(aTopicmap);
  }

  @Override
  public int getDefaultLocality() {
    int locality = 1;
    VizDebugUtils.debug("DesktopContext.getDefaultLocality - locality:" + 
        locality);
    return locality;
  }

  @Override
  public int getMaxLocality() {
    int maxLocality = 5;
    VizDebugUtils.debug("DesktopContext.getMaxLocality - maxLocality:" +
        maxLocality);
    return maxLocality;
  }

  @Override
  public ParsedMenuFile getEnabledItemIds() {
    VizDebugUtils.debug("VizController$ApplicationContext.getEnabledItemIds" +
        "() - null: " + null);
    return new ParsedMenuFile(null);
  }

  @Override
  public TypesConfigFrame getAssocFrame() {
    return desktop.getAssocFrame();
  }

  @Override
  public TypesConfigFrame getTopicFrame() {
    return desktop.getTopicFrame();
  }
}
