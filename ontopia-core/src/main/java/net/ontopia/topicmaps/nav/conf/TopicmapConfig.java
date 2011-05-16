// $Id: TopicmapConfig.java,v 1.6 2002/05/29 13:38:40 hca Exp $

package net.ontopia.topicmaps.nav.conf;

/** 
 * PUBLIC: Holds configuration information for topicmap configuration
 */
public class TopicmapConfig {
  String charset;
  String title;
  
  // get
  public String getCharset() {
    return charset;
  }
  
  public String getTitle() {
    return title;
  }
  
  // set
  public void setCharset(String s) {
    charset = s;
  }
  
  public void setTitle(String s) {
    title = s;
  }
}





