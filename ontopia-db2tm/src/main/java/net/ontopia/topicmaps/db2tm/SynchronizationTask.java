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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: TimerTask that runs DB2TM synchronization.
 */
public class SynchronizationTask extends TimerTask {

  private static Logger log = LoggerFactory.getLogger(SynchronizationTask.class);

  protected static Map<String, Date> lastExecutions = Collections.synchronizedMap(new HashMap<String, Date>());
    
  protected final String name;
  protected String rmappingfile;
  protected Collection<String> relnames;
  protected TopicMapReferenceIF ref;
  protected LocatorIF baseloc;
  
  protected final Timer timer;

  public SynchronizationTask(String name, long delay, long interval) {
    this.name = name;
    this.timer = new Timer();
    this.timer.schedule(this, delay, interval);
    log.info("Synchronization task '{}' scheduled with delay.", name);
  }
  
  public SynchronizationTask(String name, Date startTime, long interval) {
    this.name = name;
    this.timer = new Timer();
    this.timer.schedule(this, startTime, interval);
    log.info("Synchronization task '{}' scheduled with start time.", name);
  }
  
  public void setRelationMappingFile(String rmappingfile) {
    this.rmappingfile = rmappingfile;
  }

  public void setRelationNames(Collection<String> relnames) {
    this.relnames = relnames;
  }

  public void setTopicMapReference(TopicMapReferenceIF ref) {
    this.ref = ref;
  }

  public void setBaseLocator(LocatorIF baseloc) {
    this.baseloc = baseloc;
  }
  
  @Override
  public void run() {
    log.debug("Synchronization task '{}' begins...", name);
    
    try {
      File cfgfile = new File(rmappingfile);
      RelationMapping rmapping;            
      if (cfgfile.exists()) {
        rmapping = RelationMapping.read(cfgfile);
      } else {
        rmapping = RelationMapping.readFromClasspath(rmappingfile);
      }
      try {
        // perform synchronization
        TopicMapStoreIF store = ref.createStore(false);
        log.debug("rmapping: {}", rmapping);
        log.debug("relnames: {}", relnames);
        log.debug("store: {}", store);
        log.debug("baseloc: {}", baseloc);
        try {
          Processor.synchronizeRelations(rmapping, relnames, store.getTopicMap(), baseloc);
          store.commit();
        } catch (Exception e) {
          store.abort();
          throw e;
        } finally {
          if (store.isOpen()) {
            store.close();
          }
        }
      } finally {
        rmapping.close();
      }
      
    } catch (Exception e) {
      log.error("Synchronization task '" + name + "' failed...", e);
      // throw new OntopiaRuntimeException("Synchronization task '" + name + "' failed", e);
      return;
    }
    log.debug("Synchronization task '{}' ends...", name);
    lastExecutions.put(name, new Date());
  }
  
  public void stop() {
    timer.cancel(); // terminate the timer thread
    log.info("Synchronization task '{}' descheduled.", name);
  }

  public static Date getLastExecution(String taskname) {
    return lastExecutions.get(taskname);
  }
  
}
