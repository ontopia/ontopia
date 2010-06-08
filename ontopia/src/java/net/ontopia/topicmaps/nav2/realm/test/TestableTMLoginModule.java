
// $Id: TestableTMLoginModule.java,v 1.1 2005/03/04 09:42:27 grove Exp $

package net.ontopia.topicmaps.nav2.realm.test;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.nav2.realm.TMLoginModule;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Class extending TMLoginModule so that it can be properly
 * tested outside of a J2EE environment.
 */
public class TestableTMLoginModule extends TMLoginModule {
  
  /**
   * INTERNAL: Return a topicmap via a hard-coded file-path.
   */
  protected TopicMapIF getTopicMap() {
    try {
      return ImportExportUtils.getReader(new java.io.File(topicmapId)).read();
    } catch (java.io.IOException e) {
      throw new OntopiaRuntimeException(e);
    } 
  }

}
