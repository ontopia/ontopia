/*
 * #!
 * Ontopia Vizigator
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

package net.ontopia.topicmaps.viz;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.utils.CollectionUtils;
import net.ontopia.utils.URIUtils;

/**
 * PUBLIC: Description: Configuration Manager for General options
 */
public class VizGeneralConfigurationManager extends VizConfigurationManager {
  private TopicIF timestamp;
  private ClassInstanceIndexIF typeIndex;
  private TopicIF recentLoaded;
  private static final String RECENT_LOADED = BASE + "recent-loaded";
  private static final String TIMESTAMP = BASE + "timestamp";
  private TopicIF rdfMappingFile;
  private TopicIF tmDirectory;
  private TopicIF rdbmsDirectory;
  private TopicIF configDirectory;
  private static final String RDF_MAPPING_FILE = BASE + "rdf-mapping-file";
  private static final String TM_DIRECTORY = BASE + "tm-directory";
  private static final String RDBMS_DIRECTORY = BASE + "rdbms-directory";
  private static final String CONFIG_DIRECTORY = BASE + "config-directory";

  /**
   * Creates an empty configuration manager where everything is set to default.
   */
  public VizGeneralConfigurationManager() {
    super();
  }

  /**
   * Constructor initializes the configuration by loading a topic map from the
   * URL given in the parameter.
   */
  public VizGeneralConfigurationManager(File tmfile) throws IOException {
    super(tmfile);
  }

  /**
   * Constructor initializes the configuration by loading a topic map from the
   * URL given in the parameter.
   */
  public VizGeneralConfigurationManager(URL tmurl) throws IOException {
    super(tmurl);
  }

  public void updateRecentFiles(File f) {
    TopicIF recentFile = this.getRecentFile(f);
    if (recentFile == null) {
      recentFile = this.createRecentFile(f);
    }

    // Update timestamp
    OccurrenceIF lastVisited = this.getOccurrence(recentFile, timestamp);
    lastVisited.setValue(Long.toString(new Date().getTime()));

    // Limit recent files list to 10 items
    List recentFiles = this.getRecentFiles();
    if (recentFiles.size() == 10) {
      this.getRecentFile((File) recentFiles.get(9)).remove();
    }
  }

  public List getRecentFiles() {
    ArrayList topics = new ArrayList(typeIndex.getTopics(recentLoaded));

    Collections.sort(topics, new Comparator() {

      @Override
      public int compare(Object a, Object b) {

        TopicIF topicA = (TopicIF) a;
        TopicIF topicB = (TopicIF) b;

        String valueA = getOccurrenceValue(topicA, timestamp);
        String valueB = getOccurrenceValue(topicB, timestamp);

        return valueB.compareTo(valueA);
      }
    });

    ArrayList result = new ArrayList(topics.size());
    for (Iterator iter = topics.iterator(); iter.hasNext();) {
      TopicIF topic = (TopicIF) iter.next();
      LocatorIF locator = (LocatorIF)CollectionUtils.getFirst(topic.getSubjectLocators());
      File file = null;
      try {
        file = URIUtils.getURIFile(locator);
      } catch (MalformedURLException e) {
        // For 2.1 compatability
        file = new File(locator.getAddress());
      }
      if (file != null) {
        result.add(file);
      }
    }

    return result;
  }

  private TopicIF createRecentFile(File aFile) {
    TopicIF file = builder.makeTopic(recentLoaded);
    file.addSubjectLocator(VizUtils.makeLocator(aFile));
    builder.makeOccurrence(file, timestamp, "");
    return file;
  }

  private TopicIF getRecentFile(File aFile) {

    return topicmap.getTopicBySubjectLocator(VizUtils.makeLocator(aFile));
  }

  @Override
  protected void init() {

    super.init();

    typeIndex = (ClassInstanceIndexIF) topicmap.getIndex(
            "net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");

    recentLoaded = getTopic(RECENT_LOADED);
    timestamp = getTopic(TIMESTAMP);
    rdfMappingFile = getTopic(RDF_MAPPING_FILE);
    tmDirectory = getTopic(TM_DIRECTORY);
    rdbmsDirectory = getTopic(RDBMS_DIRECTORY);
    configDirectory = getTopic(CONFIG_DIRECTORY);
  }

  public String getRDFMappingFile() {
    return getOccurrenceValue(generalTopic, rdfMappingFile);
  }

  public void setRdfMappingFile(File aFile) {
    setOccurenceValue(generalTopic, rdfMappingFile, aFile
        .getAbsolutePath());
  }

  public String getCurrentTMDir() {
    return getOccurrenceValue(generalTopic, tmDirectory);
  }

  public void setCurrentTMDir(String currentTMDir) {
    setOccurenceValue(generalTopic, tmDirectory, currentTMDir);
  }

  public String getCurrentRDBMSDir() {
    return getOccurrenceValue(generalTopic, rdbmsDirectory);
  }

  public void setCurrentRDBMSDir(String dir) {
    setOccurenceValue(generalTopic, rdbmsDirectory, dir);
  }

  public String getCurrentConfigDir() {
    return getOccurrenceValue(generalTopic, configDirectory);
  }

  public void setCurrentConfigDir(String dir) {
    setOccurenceValue(generalTopic, configDirectory, dir);
  }
}
