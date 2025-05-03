/*
 * #!
 * Ontopia Engine
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

package net.ontopia.topicmaps.impl.rdbms;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.Collections;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.persistence.proxy.RDBMSStorage;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.entry.TopicMapSourceIF;
import net.ontopia.utils.OntopiaRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PUBLIC: A topic map source that refers to single reference that is
 * located by pattern. If multiple topic maps match the pattern then
 * the topic map with the highest object id is chosen.  This source is
 * therefore able to swap to later versions of the same topic map when
 * the topic map repository is refreshed.<p>
 *
 * @since 3.4.2
 */
public class RDBMSPatternSingleTopicMapSource implements TopicMapSourceIF {
  protected String id;
  protected String referenceId;
  protected String title;  
  protected LocatorIF base_address;
  
  protected String propfile;

  protected String match = "title";
  protected String pattern;

  protected RDBMSTopicMapReference reference;

  protected RDBMSStorage storage;

  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(RDBMSPatternSingleTopicMapSource.class.getName());

  // --- TopicMapSourceIF implementation

  @Override
  public TopicMapReferenceIF createTopicMap(String name, String baseAddress) {
    throw new UnsupportedOperationException();
  }

  @Override
  public synchronized Collection getReferences() {
    if (reference == null) {
      refresh();
    }
    if (reference == null) {
      return Collections.EMPTY_LIST;
    } else {
      return Collections.singleton(reference);
    }
  }
  
  @Override
  public synchronized void refresh() {
    if (match == null) {
      throw new OntopiaRuntimeException("match property must be specified on source with id '" + getId() + "'.");
    }
    if (pattern == null) {
      throw new OntopiaRuntimeException("pattern property must be specified on source with id '" + getId() + "'.");
    }
    if (referenceId == null) {
      throw new OntopiaRuntimeException("referenceId property must be specified on source with id '" + getId() + "'.");
    }
    
    boolean foundReference = false;
    long topicmap_id = -2;
    String _title = title;
    LocatorIF _base_address = base_address;
    
    try {
      createStorage();

      // retrieve reference id from database
      Connection conn = storage.getConnectionFactory(true).requestConnection();
      try {

        String sqlquery;
        if ("title".equals(match)) {
          sqlquery = "select max(M.id), M.title, M.base_address from TM_TOPIC_MAP M where M.title = ? group by M.title, M.base_address order by max(M.id) desc";
        } else if ("comments".equals(match)) {
          sqlquery = "select max(M.id), M.title, M.base_address from TM_TOPIC_MAP M where M.comments = ? group by M.title, M.base_address order by max(M.id) desc";
        } else {
          throw new OntopiaRuntimeException("match property contains illegal value '" + match + "' on source with id '" + getId() + "'.");
        }
        
        PreparedStatement stm = conn.prepareStatement(sqlquery);
        try {
          stm.setString(1, pattern);
          ResultSet rs = stm.executeQuery();
          if (rs.next()) {
            foundReference = true;
            topicmap_id = rs.getLong(1);
            if (_title == null) {
              _title = rs.getString(2);
            }
            if (_base_address == null) {
              String loc = rs.getString(3);
              if (loc != null) {
                _base_address = new URILocator(loc);
              }
            }
          } else {
            log.warn("Source with id '" + getId() + "' could not find any matching topic maps with pattern '" + pattern + "'.");
          }
          rs.close();
        } finally {
          stm.close();
        }
      } catch (Exception e) {
        throw new OntopiaRuntimeException(e);
      } finally {
        try { conn.close(); } catch (Exception e) { }
      }

      // close existing reference if new topic map id
      if (reference != null && topicmap_id != reference.getTopicMapId()) {
        try {
          log.debug("Closing reference '" + referenceId + "' to topic map " + reference.getTopicMapId() + ".");
          reference.close();
        } catch (Exception e) {
          log.error("Error occurred when closing reference.", e);
        }        
      }
      
      // swap reference only if new or topic map id is different
      if (foundReference) {
        if (reference == null || topicmap_id != reference.getTopicMapId()) {
          log.debug("Created new reference '" + referenceId + "' to topic map " + topicmap_id);
          
          RDBMSTopicMapReference ref = new RDBMSTopicMapReference(referenceId, _title, storage, topicmap_id, _base_address);
          ref.setSource(this);
          this.reference = ref;
        } else {
          log.debug("Reference with id '" + referenceId + "' already refers to topic map " + topicmap_id);
        }
      } else {
        this.reference = null;
      }
      
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  @Override
  public void close() {
    if (storage != null) {
      storage.close();
    }
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }
  
  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * PUBLIC: Returns the match type used by the source.
   */
  public String getMatch() {
    return match;
  }

  /**
   * PUBLIC: Sets the match type used by the source. This can either
   * be 'title' or 'comments'. The default is 'title'.
   */
  public void setMatch(String match) {
    this.match = match;
  }
  
  /**
   * PUBLIC: Returns the pattern value used by the source.
   */
  public String getPattern() {
    return pattern;
  }

  /**
   * PUBLIC: Sets the pattern value used by the source. This is
   * typically the title of the topic map, but can also be the
   * comments attached to a topic map. Which of the two the pattern
   * matches depends on the match type given.
   */
  public void setPattern(String pattern) {
    this.pattern = pattern;
  }

  @Override
  public boolean supportsCreate() {
    return false;
  }

  @Override
  public boolean supportsDelete() {
    return false;
  }

  // --- Internal helpers
  
  protected RDBMSStorage createStorage() throws IOException {
    if (storage == null) {
      if (propfile == null) {
        throw new OntopiaRuntimeException("propertyFile property must be specified on source with id '" + getId() + "'.");
      }
      storage = new RDBMSStorage(propfile);
    }
    return storage;
  }

  // --- Extension properties

  /**
   * PUBLIC: Gets the database property file containing configuration
   * parameters for accessing the rdbms database.
   */
  public String getPropertyFile() {
    return propfile;
  }

  /**
   * PUBLIC: Sets the database property file containing configuration
   * parameters for accessing the rdbms database. The propfile given
   * with first be attempted loaded from the file system. If it does
   * not exist on the file system it will be loaded from the
   * classpath. If the access must be explicit then the property file
   * name can be prefixed by 'file:' or 'classpath:'.
   */
  public void setPropertyFile(String propfile) {
    this.propfile = propfile;
  }

  /**
   * PUBLIC: Gets the id of the topic map reference for this topic map
   * source.
   */
  public String getReferenceId() {
    return referenceId;
  }

  /**
   * PUBLIC: Sets the id of the topic map reference for this topic map
   * source.
   */
  public void setReferenceId(String referenceId) {
    this.referenceId = referenceId;
  }

  /**
   * PUBLIC: Gets the base address of the topic maps retrieved from
   * the source. The notation is assumed to be 'URI'.
   */
  public String getBaseAddress() {
    return (base_address == null ? null : base_address.getAddress());
  }

  /**
   * PUBLIC: Sets the base address of the topic maps retrieved from
   * the source. The notation is assumed to be 'URI'.
   */
  public void setBaseAddress(String base_address) {
    try {
      this.base_address = new URILocator(base_address);
    } catch (URISyntaxException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
}
