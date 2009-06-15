// $Id: ControlConfigIF.java,v 1.7 2002/05/29 13:38:40 hca Exp $

package net.ontopia.topicmaps.nav.conf;

/** 
 * PUBLIC: Interface for ControlConfig.
 */
public interface ControlConfigIF {

  public void update(String model, String view, String skin);

  public String getModelPath();
  public String getViewPath();
  public String getSkinPath();

  public String getModel();
  public String getView();
  public String getSkin();

  public String getBehaviour();
  public String getContentType();

}





