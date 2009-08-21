
// $Id$

package net.ontopia.topicmaps.viz;

/**
 * EXPERIMENTAL: Common methods for all application contexts.
 */
public abstract class ApplicationContext implements ApplicationContextIF {
  private TopicMapView view; // null if no TM loaded
  private VizTopicMapConfigurationManager tmConfig;
  private VizPanel vpanel;
  
  /**
   * Get the view attached to this context
   *
   * @return Returns the view.
   */
  public TopicMapView getView() {
    return view;
  }
  
  /**
   * Set the view for this context
   *
   * @param view The view to set.
   */
  public void setView(TopicMapView view) {
    this.view = view;
  }
  
  /**
   * Get the Configuration Manager 
   *
   * @return Returns the tmConfig.
   */
  public VizTopicMapConfigurationManager getTmConfig() {
    return tmConfig;
  }
  
  /**
   * Set the Configuration Manager
   *
   * @param tmConfig The tmConfig to set.
   */
  public void setTmConfig(VizTopicMapConfigurationManager tmConfig) {
    this.tmConfig = tmConfig;
  }
  
  /**
   * Get the Panel 
   *
   * @return Returns the vpanel.
   */
  public VizPanel getVizPanel() {
    return vpanel;
  }
  
  /**
   * Set the Panel 
   *
   * @param vpanel The vpanel to set.
   */
  public void setVizPanel(VizPanel vpanel) {
    this.vpanel = vpanel;
  }
}
