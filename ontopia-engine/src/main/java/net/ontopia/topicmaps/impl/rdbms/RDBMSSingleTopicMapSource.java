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
 * PUBLIC: A topic map source that holds a reference to a single rdbms
 * topic map. Individual topic maps can thus be pointed to by this
 * source implementation.<p>
 *
 * @since 1.3.4
 */

public class RDBMSSingleTopicMapSource implements TopicMapSourceIF {

  protected String id;
  protected String referenceId;
  protected String title;
  protected String propfile;
  protected long topicmap_id;
  protected LocatorIF base_address;
  protected boolean hidden;

  protected String topicListeners;

  protected RDBMSTopicMapReference reference;
  
  protected RDBMSStorage storage;

  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(RDBMSSingleTopicMapSource.class.getName());

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
      return Collections.EMPTY_SET;
    }
    return Collections.singleton(reference);
  }
  
  @Override
  public synchronized void refresh() {
    // FIXME: for now don't recreate reference if already exists
    if (reference != null) {
      return;
    }

    boolean foundReference = false;
    try {
      createStorage();
      
      String _title = title;
      LocatorIF _base_address = base_address;
      
      // retrieve reference id from database
      if (_title == null || _base_address == null) {
        Connection conn = conn = storage.getConnectionFactory(true).requestConnection();
        try {
          PreparedStatement stm = conn.prepareStatement("select M.title, M.base_address from TM_TOPIC_MAP M where M.id = ?");
          try {
            stm.setLong(1, topicmap_id);
            ResultSet rs = stm.executeQuery();
            
            if (rs.next()) {
              foundReference = true;
              if (_title == null) {
                _title = rs.getString(1);
              }
              if (_base_address == null) {
                String loc = rs.getString(2);
                if (loc != null) {
                  _base_address = new URILocator(loc);
                }
              }
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
      }

      // create a reference id if not already exists
      if (foundReference) {
        String _referenceId = this.referenceId;
        if (_referenceId == null) {
          _referenceId = getReferenceId(topicmap_id);
        }
        
        // use reference id as title if not otherwise found
        _title = (_title != null ? _title : _referenceId);
        
        log.debug("Created new reference '" + _referenceId + "' to topic map " + topicmap_id);
        RDBMSTopicMapReference ref = new RDBMSTopicMapReference(_referenceId, _title, storage, topicmap_id, _base_address);
        ref.setSource(this);

        // register topic listeners
        if (topicListeners != null) {
          ref.registerTopicListeners(topicListeners);
        }
        
        this.reference = ref;
      } else {
        log.info("Could not create reference for single RDBMS source " +
                 id + " because no topic map found");
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
  
  protected String getReferenceId(long topicmap_id) {
    if (id == null) {
      return "RDBMS-" + topicmap_id;
    } else {
      return id + "-" + topicmap_id;
    }
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
   * PUBLIC: Gets the id of the topic map referenced. Note that this
   * id must be the string representation of a long.
   */
  public String getTopicMapId() {
    return Long.toString(topicmap_id);
  }

  /**
   * PUBLIC: Sets the id of the topic map referenced. Note that this
   * id must be the string representation of a long.
   */
  public void setTopicMapId(String id) {
    // strip out 'M'
    if (id.charAt(0) == 'M') {
      setTopicMapId(Long.parseLong(id.substring(1)));
    } else {
      setTopicMapId(Long.parseLong(id));
    }
  }

  /**
   * PUBLIC: Sets the id of the topic map referenced.
   */
  public void setTopicMapId(long id) {
    this.topicmap_id = id;
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

  public boolean getHidden() {
    return hidden;
  }

  public void setHidden(boolean hidden) {
    this.hidden = hidden;
  }

  public String getTopicListeners() {
    return topicListeners;
  }

  public void setTopicListeners(String topicListeners) {
    this.topicListeners = topicListeners;
  }
  
}
