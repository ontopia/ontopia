/*
 * #!
 * Ontopoly Editor
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
package ontopoly.conversion;

import ontopoly.OntopolyApplication;
import ontopoly.model.TopicMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import net.ontopia.utils.OntopiaRuntimeException;

public class UpgradeUtils {
  
  private static final Logger log = LoggerFactory.getLogger(UpgradeUtils.class);
  
  private static Object lock = new Object();
  
  protected static void upgradeTopicMap(TopicMap topicMap) {
    TopicMapIF topicmap = topicMap.getTopicMapIF();
    
    synchronized (lock) {     
      try {
        // check to see if we actually need to do an upgrade
        OccurrenceIF version_occ = getVersionOccurrence(topicmap);
        float version_number = getVersionNumber(version_occ);
        if (!needsUpgrade(version_number)) {
          return;
        }

        log.info("Topic map " + topicMap.getId() + " needs upgrade: " + version_number);
        
        if (version_number < 1.1f) {
          log.info("Upgrading topic map " + topicMap.getId() + " to version 1.1");
          new Upgrade_1_1(topicMap).upgrade();
        }

        if (version_number < 1.2f) {
          log.info("Upgrading topic map " + topicMap.getId() + " to version 1.2");
          new Upgrade_1_2(topicMap).upgrade();
        }

        if (version_number < 1.3f) {
          log.info("Upgrading topic map " + topicMap.getId() + " to version 1.3");
          new Upgrade_1_3(topicMap).upgrade();
        }

        if (version_number < 1.4f) {
          log.info("Upgrading topic map " + topicMap.getId() + " to version 1.4");
          new Upgrade_1_4(topicMap).upgrade();
        }

        if (version_number < 1.9f) {
          log.info("Upgrading topic map " + topicMap.getId() + " to version 1.9");
          new Upgrade_1_9(topicMap).upgrade();
        }

       if (version_number < 2.0f) {
         log.info("Upgrading topic map " + topicMap.getId() + " to version 2.0");
         new Upgrade_2_0(topicMap).upgrade();
       }

//       if (version_number < 2.1f) {
//         log.info("Upgrading topic map " + topicMap.getId() + " to version 2.0");
//         new Upgrade_2_1(topicMap).upgrade();
//       }
        
        // update version number
        version_occ.setValue(Float.toString(OntopolyApplication.CURRENT_VERSION_NUMBER));
      } catch (Exception e) {
        throw new OntopiaRuntimeException(e);
      }
    }
  }
   
  private static boolean needsUpgrade(float version_number) {
//    System.out.println("VV: " + OntopolyRepository.CURRENT_VERSION_NUMBER +" "+ version_number + " " + (OntopolyRepository.CURRENT_VERSION_NUMBER > version_number));
    return version_number != 0 && OntopolyApplication.CURRENT_VERSION_NUMBER > version_number;
  }
  
  private static OccurrenceIF getVersionOccurrence(TopicMapIF topicmap) throws Exception {
    QueryProcessorIF qp = QueryUtils.getQueryProcessor(topicmap);
    QueryResultIF qr = null;
    try {
      OccurrenceIF latest = null;
      float version = 0;
      qr =  qp.execute("select $O from topicmap($TM), subject-identifier($TOV, \"http://psi.ontopia.net/ontology/ted-ontology-version\"), reifies($R, $TM), occurrence($R, $O), type($O, $TOV)?");
      if (qr.next()) {
        latest = (OccurrenceIF)qr.getValue(0);
        version = getVersionNumber(latest);
      }
      // if there are multiple version occurrences retain only the latest one.
      if (!topicmap.isReadOnly()) {
        while (qr.next()) {
          OccurrenceIF _oc = (OccurrenceIF)qr.getValue(0);
          float _v = getVersionNumber(_oc);
          if (_v > version) {
            latest = _oc;
            version = _v;
          } else {
            _oc.remove();
          }
        }
      }      
      return latest;
      
    } finally {
      if (qr != null) {
        qr.close();
      }
    }
  }
  
  private static float getVersionNumber(OccurrenceIF versionOcc) {
    if (versionOcc == null) {
      return 0;
    }
    String versionNumber = versionOcc.getValue();
    if (versionNumber == null) {
      return 0;
    } else if ("[1.0]".equals(versionNumber)) {
      return 1.0f;
    }
    
    try {
      return Float.parseFloat(versionNumber);
    } catch (NumberFormatException e) {
      return 0;
    }
  }
 
}
