/*
 * #!
 * Ontopia DB2TM
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
  static Logger log = LoggerFactory.getLogger(DB2TM.class);

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
    log.info("Reading DB2TM configuration file: {}", cfgfile);
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
    sync(cfgfile, topicmap, false);
  }

  /**
   * PUBLIC: Run synchronization from a configuration file against a
   * given topic map.
   *
   * @since 5.2.0
   * @param cfgfile File name of XML configuration file.
   * @param topicmap Topic map to synchronize the data against.
   * @param force_rescan Iff true, all relations are rescanned
   */
  public static void sync(String cfgfile, TopicMapIF topicmap,
                          boolean force_rescan)
    throws IOException {
    log.info("Reading DB2TM configuration file: {}", cfgfile);
    RelationMapping mapping;
    try {
      mapping = RelationMapping.read(new File(cfgfile));
    } catch (Exception e) {
      throw new DB2TMException("Error occurred while reading DB2TM configuration file: " + cfgfile, e);
    }
    LocatorIF baseloc = topicmap.getStore().getBaseAddress();
    Processor.synchronizeRelations(mapping, null, topicmap, baseloc,
                                   force_rescan);
  }
  
}
