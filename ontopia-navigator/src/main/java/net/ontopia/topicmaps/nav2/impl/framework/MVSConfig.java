
package net.ontopia.topicmaps.nav2.impl.framework;

import java.util.Collection;
import java.util.Set;
import java.util.HashSet;

/** 
 * INTERNAL: A carrier class which provides configuration information
 * about the Model-/View/Skin-Settings used by the web-application.
 */
public class MVSConfig {

  Set models;
  Set views;
  Set skins;
  String defaultModel;
  String defaultView;
  String defaultSkin;

  public MVSConfig() {
    this("", "", "");
  }

  public MVSConfig(String model, String view, String skin) {
    defaultModel = model;
    defaultView = view;
    defaultSkin = skin;
    models = new HashSet();
    views = new HashSet();
    skins = new HashSet();
  }

  // --- model
  
  public String getModel() {
    return defaultModel;
  }
  
  public void setModel(String model) {
    defaultModel = model;
  }

  public void addModel(String id, String title) {
    models.add(new DefaultModel(id, title));
  }

  public Collection getModels() {
    return models;
  }

  // --- view
  
  public String getView() {
    return defaultView;
  }
  
  public void setView(String view) {
    defaultView = view;
  }

  public void addView(String id, String title) {
    views.add(new DefaultView(id, title));
  }

  public Collection getViews() {
    return views;
  }

  // --- skin
  
  public String getSkin() {
    return defaultSkin;
  }

  public void setSkin(String skin) {
    defaultSkin = skin;
  }

  public void addSkin(String id, String title) {
    skins.add(new DefaultSkin(id, title));
  }

  public Collection getSkins() {
    return skins;
  }

}






