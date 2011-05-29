
package net.ontopia.topicmaps.db2tm;

import java.io.File;
import java.io.IOException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicMapIF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PUBLIC: The driver class used to do conversions with DB2TM.
 */
public class DB2TM {
  
  // --- define a logging category.
  static Logger log = LoggerFactory.getLogger(DB2TM.class.getName());

  private DB2TM() {
    // not possible to instantiate
  }
  
  /**
   * PUBLIC: Run conversion from a configuration file into a given
   * topic map.
   *
   * @since 3.1.0
   * @param cfgfile File name of XML configuration file.
   * @param topicmap Topic map to add converted data to.
   */
  public static void add(String cfgfile, TopicMapIF topicmap)
    throws IOException {
    log.info("Reading DB2TM configuration file: " + cfgfile);
    RelationMapping mapping;
    try {
      mapping = RelationMapping.read(new File(cfgfile));
    } catch (Exception e) {
      throw new DB2TMException("Error occurred while reading DB2TM configuration file: " + cfgfile, e);
    }
    LocatorIF baseloc = topicmap.getStore().getBaseAddress();
    Processor.addRelations(mapping, null, topicmap, baseloc);
  }
  
  /**
   * PUBLIC: Run synchronization from a configuration file against a
   * given topic map.
   *
   * @since 3.2.0
   * @param cfgfile File name of XML configuration file.
   * @param topicmap Topic map to synchronize the data against.
   */
  public static void sync(String cfgfile, TopicMapIF topicmap)
    throws IOException {
    log.info("Reading DB2TM configuration file: " + cfgfile);
    RelationMapping mapping;
    try {
      mapping = RelationMapping.read(new File(cfgfile));
    } catch (Exception e) {
      throw new DB2TMException("Error occurred while reading DB2TM configuration file: " + cfgfile, e);
    }
    LocatorIF baseloc = topicmap.getStore().getBaseAddress();
    Processor.synchronizeRelations(mapping, null, topicmap, baseloc);
  }
  
}
