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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JApplet;
import net.ontopia.Ontopia;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.impl.remote.RemoteTopicMapStore;
import net.ontopia.utils.CmdlineUtils;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.PropertyUtils;

/**
 * PUBLIC: Visualization applet. To learn how to use this applet, consult the
 * The Vizigator User's Guide.
 */
public class Vizlet extends JApplet implements VizFrontEndIF {
  private ParsedMenuFile enabledMenuItems;
  private boolean parsedMenuItems;
  private AppletContext appletContext = null;

  @Override
  public String getAppletInfo() {
    return "Ontopia Vizlet";
  }

  @Override
  public void init() {
    appletContext = new AppletContext(this);
    // FIXME: This logging should only go into the instrumented version of
    // Vizlet to find out what slows it down.
    VizDebugUtils.resetTimer();
    VizDebugUtils.instrumentedDebug("Vizlet.init() starting. Time: "
        + VizDebugUtils.getTimeDelta());
    
    // set up logging
    try {
      CmdlineUtils.initializeLogging();
      CmdlineUtils.setLoggingPriority("ERROR");
    } catch (Exception e) {
      e.printStackTrace();
    }
    outputVersionInfo();
    // get panel off the ground
    try {
      String tmrapParameter = getParameter("tmrap");
      if (tmrapParameter == null) {
        throw new VizigatorReportException("The required \"tmrap\" parameter " +
            "has not been set.");
      }
      // set the ui language
      Messages.setLanguage(getParameter("lang"));
      // create vizigator panel
      VizPanel vpanel = new VizPanel(this);
      getContentPane().add(vpanel);
      vpanel.configureDynamicMenus(new DynamicMenuListener(vpanel));
    } catch (VizigatorReportException e) {
      ErrorDialog.showMessage(this, e);
      throw e;
    } catch (Exception e) {
      ErrorDialog.showError(this, e);
      throw new OntopiaRuntimeException(e);
    }
  }

  @Override
  public boolean getDefaultControlsVisible() {
    return PropertyUtils.isTrue(getParameter("controlsVisible"), true);
  }

  public int getDefaultLocality() {
    int locality = PropertyUtils.getInt(getParameter("locality"), 1);
    VizDebugUtils.debug("getDefaultLocality - locality: " + locality);
    return locality;
  }

  protected class DynamicMenuListener implements ActionListener {
    protected VizPanel vpanel;
    
    protected DynamicMenuListener(VizPanel vpanel) {
      this.vpanel = vpanel;
    }
    
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
      vpanel.configureDynamicMenus(this);
    }
  }

  // --- Helpers

  /**
   * INTERNAL: Just output version info to sysout.
   */
  private void outputVersionInfo() {
    System.out.println(Ontopia.getInfo());
  }

  /**
   * INTERNAL: Resolves the URI relative to the applet's codebase URI.
   */
  public String resolve(String base) throws MalformedURLException {
    return new URL(getCodeBase(), base).toExternalForm();
  }

  public String getResolvedParameter(String param) {
    try {
      String paramValue = getParameter(param);
      if (paramValue == null) {
        return null;
      }
      return resolve(getParameter(param));
    } catch (MalformedURLException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  public int getMaxLocality() {
    int maxLocality = PropertyUtils.getInt(getParameter("max-locality"), 5);
    VizDebugUtils.debug("getMaxLocality - getParameter(\"max-locality\"): " + 
        getParameter("max-locality"));
    VizDebugUtils.debug("getMaxLocality - locality: " + maxLocality);
    return maxLocality;
  }

  /**
   * Process the menu file and get the enabled item ids from it.
   */
  public ParsedMenuFile getEnabledItemIds() {
    if (parsedMenuItems) {
      return enabledMenuItems;
    }
      
    String fileString = getParameter("menufile");
    if (fileString == null) {
      return new ParsedMenuFile(null);
    }

    URL codeBase = getCodeBase();
    String urlString = codeBase.toExternalForm() + fileString;
    MenuFileParser menuFileParser = new MenuFileParser(urlString);
    enabledMenuItems = menuFileParser.parse();
    parsedMenuItems = true;
    return enabledMenuItems;
  }
  
  @Override
  public TopicMapIF getTopicMap() {
    String tmrap = getResolvedParameter("tmrap");
    String tmid = getParameter("tmid");
    if (tmid == null) {
      throw new VizigatorReportException("The required \"tmid\" parameter " +
          "has not been set.");
    }
    RemoteTopicMapStore store = new RemoteTopicMapStore(tmrap, tmid);
    return store.getTopicMap();
  }

  // --- VizFrontEndIF implementation
  
  @Override
  public boolean mapPreLoaded() {
    return true;
  }

  @Override
  public void setNewTypeColor(TopicIF type, Color c) {
    throw new UnsupportedOperationException("Cannot change colours in Vizlet");
  }

  @Override
  public void configureFilterMenus() {
    throw new UnsupportedOperationException("No filter menus in Vizlet");
  }

  @Override
  public boolean useGeneralConfig() {
    return false;
  }
  
  @Override
  public String getWallpaper() {
    String wallpaperSrc = null;
    String wallpaperUnresolvedSrc = getParameter("wallpaper_image");
    try {
      if(wallpaperUnresolvedSrc != null) {
        wallpaperSrc = resolve(wallpaperUnresolvedSrc);
      }
    } catch (MalformedURLException mue) {
      throw new OntopiaRuntimeException("Invalid image path: " + wallpaperUnresolvedSrc);
    }
    return wallpaperSrc;
  }
  
  @Override
  public String getConfigURL() {
    return getResolvedParameter("config");
  }

  @Override
  public TypesConfigFrame getTypesConfigFrame(VizController controller, boolean isTopicConfig) {
    if(isTopicConfig) {
      return TypesConfigFrame.createTopicTypeConfigFrame(controller, null);
    } else {
      return TypesConfigFrame.createAssociationTypeConfigFrame(controller, null);
    }
  }

  @Override
  public ApplicationContextIF getContext() {
    return appletContext;
  }
}
