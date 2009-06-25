
// $Id: SynchronizationTask.java,v 1.10 2006/12/18 12:59:37 grove Exp $

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
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: TimerTask that runs DB2TM synchronization.
 */

public class SynchronizationTask extends TimerTask {

  static Logger log = LoggerFactory.getLogger(SynchronizationTask.class.getName());

  protected static Map lastExecutions = Collections.synchronizedMap(new HashMap());
    
  protected String name;
  protected String rmappingfile;
  protected Collection relnames;
  protected TopicMapReferenceIF ref;
  protected LocatorIF baseloc;
  
  protected Timer timer;

  public SynchronizationTask(String name, long delay, long interval) {
    this.name = name;
    this.timer = new Timer();
    this.timer.schedule(this, delay, interval);
    log.info("Synchronization task '" + name + "' scheduled with delay.");
  }
  
  public SynchronizationTask(String name, Date startTime, long interval) {
    this.name = name;
    this.timer = new Timer();
    this.timer.schedule(this, startTime, interval);
    log.info("Synchronization task '" + name + "' scheduled with start time.");
  }
  
  public void setRelationMappingFile(String rmappingfile) {
    this.rmappingfile = rmappingfile;
  }

  public void setRelationNames(Collection relnames) {
    this.relnames = relnames;
  }

  public void setTopicMapReference(TopicMapReferenceIF ref) {
    this.ref = ref;
  }

  public void setBaseLocator(LocatorIF baseloc) {
    this.baseloc = baseloc;
  }
  
  public void run() {
    log.debug("Synchronization task '" + name + "' begins...");
    
    try {
      File cfgfile = new File(rmappingfile);
      RelationMapping rmapping;            
      if (cfgfile.exists())
        rmapping = RelationMapping.read(cfgfile);
      else
        rmapping = RelationMapping.readFromClasspath(rmappingfile);
      try {
        // perform synchronization
        TopicMapStoreIF store = ref.createStore(false);
        log.debug("rmapping: " + rmapping);
        log.debug("relnames: " + relnames);
        log.debug("store: " + store);
        log.debug("baseloc: " + baseloc);
        try {
          Processor.synchronizeRelations(rmapping, relnames, store.getTopicMap(), baseloc);
          store.commit();
        } catch (Exception e) {
          store.abort();
          throw e;
        } finally {
          if (store.isOpen()) store.close();
        }
      } finally {
        rmapping.close();
      }
      
    } catch (Exception e) {
      log.error("Synchronization task '" + name + "' failed...", e);
      // throw new OntopiaRuntimeException("Synchronization task '" + name + "' failed", e);
      return;
    }
    log.debug("Synchronization task '" + name + "' ends...");
    lastExecutions.put(name, new Date());
  }
  
  public void stop() {
    timer.cancel(); // terminate the timer thread
    log.info("Synchronization task '" + name + "' descheduled.");
  }

  public static Date getLastExecution(String taskname) {
    return (Date)lastExecutions.get(taskname);
  }
  
}
