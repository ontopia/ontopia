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

package net.ontopia.topicmaps.nav.conf;

/** 
 * PUBLIC: Provides model, view and skin information for the application
 * 
 * <p>Implementors wanting to provide special models, view or skins
 * can implement their own version which may use the controller.xml
 * configuration file. The new class must implement ControlConfigIF.
 */
public class ControlConfig implements ControlConfigIF {

  // defaults designed to be overriden by implementors
  private String model = "simple";
  private String view = "no_frames";
  private String skin = "blue";
  
  // the get and set variables used by the jsp
  private String modelPath;
  private String viewPath;
  private String skinPath;
  
  // derived from the M,V,S
  private String behaviour;
  private String contentType;
  
  // others
  private String resource;

  /**
   * Constructor which takes a path to the configuration file.
   */
  public ControlConfig(String resource) {
    // should set the defaults from the file
    this.resource = resource;
    makePaths();
  }
  
  /**
   * Updates the state of the object to include user preferences.
   *
   * The application makes its own default model, view and skin for a particular
   * request. <code>userUpdate</code> allows the user preferences to be 
   * incorporated
   *
   * @param model   a string representing the model choice
   * @param view    a string representing the view choice
   * @param skin    a string representing the skin choice   
   */
  @Override
  public void update(String model, String view, String skin) {
    if (model != null && !model.isEmpty()) {
      this.model = model;
    }
    if (view  != null && !view.isEmpty()) {
      this.view = view;
    }
    if (skin  != null && !skin.isEmpty()) {
      this.skin = skin;
    } 
    makePaths();
  }

  /**
   * Provides the internal logic for the object by making paths to the correct
   * model, view and skin given the application and user inputs. Called on 
   * construction and through ControlServlet when user updates preferences.
   */
  private void makePaths() {

    // fix dodgy path
    if (resource == null || "/index.html".equals(resource)) {
      resource = "/index.jsp";
    } 
                
    // ModelPath
    if ("/topic.jsp".equals(resource)) {
      modelPath = "/models/topic_" + model + ".jsp";
    } else if ("/topicmap.jsp".equals(resource)) {
      modelPath = "/models/topicmap_" + model + ".jsp";
    } else {
      modelPath = "/models" + resource;
    }
   
    // ViewPath
    if ("/def_topic_occ.jsp".equals(resource) || 
        "/def_topicmap_occ.jsp".equals(resource) || 
        "/blank.jsp".equals(resource)) {
      viewPath = "/views/template_plain.jsp";
    } else {
      viewPath = "/views/template_" + view + ".jsp";
    }

    // SkinPath
    skinPath = "skins/" + skin + ".css";

    // Behaviour
    behaviour = "no_frames";
    if ("frames".equals(view)) {
      behaviour = "frames";
    } 
    
    // ContentType
    contentType = "text/html";
    if ("xml".equals(view)) {
      contentType = "text/xml";
    }
  }

  
  // --- get methods
  @Override
  public String getModelPath() { return modelPath; }
  @Override
  public String getViewPath() { return viewPath; }
  @Override
  public String getSkinPath() { return skinPath; }
  @Override
  public String getBehaviour() { return behaviour; }
  @Override
  public String getContentType() { return contentType; }  

  @Override
  public String getModel() { return model; }
  @Override
  public String getView() { return view; }
  @Override
  public String getSkin() { return skin; }

}





