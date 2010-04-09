
// $Id: TestableTMLoginModule.java,v 1.1 2005/03/04 09:42:27 grove Exp $

package net.ontopia.topicmaps.nav2.realm;

import java.io.InputStream;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.utils.ltm.LTMTopicMapReader;
import net.ontopia.utils.OntopiaRuntimeException;
import org.junit.Ignore;

/**
 * INTERNAL: Class extending TMLoginModule so that it can be properly
 * tested outside of a J2EE environment.
 */

@Ignore
public class TestableTMLoginModule extends TMLoginModule {
  
  /**
   * INTERNAL: Return a topicmap via a hard-coded file-path.
   */
	@Override
  protected TopicMapIF getTopicMap() {
    try {
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("net/ontopia/topicmaps/nav2/realm/" + topicmapId);
		LTMTopicMapReader reader = new LTMTopicMapReader(in, URILocator.create("test:tmloginmodule.ltm"));
		return reader.read();
    } catch (java.io.IOException e) {
      throw new OntopiaRuntimeException(e);
    } 
  }

}
