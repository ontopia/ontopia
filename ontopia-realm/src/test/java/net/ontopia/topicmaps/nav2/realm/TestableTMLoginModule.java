
package net.ontopia.topicmaps.nav2.realm;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.FileUtils;
import org.junit.Ignore;

/**
 * INTERNAL: Class extending TMLoginModule so that it can be properly
 * tested outside of a J2EE environment.
 */

@Ignore
public class TestableTMLoginModule extends TMLoginModule {

  private final static String testdataDirectory = "realm";
  
  /**
   * INTERNAL: Return a topicmap via a hard-coded file-path.
   */
  @Override
  protected TopicMapIF getTopicMap() {
    try {
      String topicmapFile = FileUtils.getTestInputFile(testdataDirectory, "tmloginmodule.ltm");
      return ImportExportUtils.getReader(topicmapFile).read();
    } catch (java.io.IOException e) {
      throw new OntopiaRuntimeException(e);
    } 
  }

}
