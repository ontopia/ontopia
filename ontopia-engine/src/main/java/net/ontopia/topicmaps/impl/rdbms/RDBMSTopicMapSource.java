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
import java.io.InputStream;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.persistence.proxy.RDBMSStorage;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.entry.TopicMapSourceIF;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.StreamUtils;

/**
 * PUBLIC: A topic map source that holds the list of <i>all</i> topic
 * map references accessible by a rdbms session.
 */
public class RDBMSTopicMapSource implements TopicMapSourceIF {

  protected boolean supportsCreate;
  protected boolean supportsDelete;

  protected LocatorIF base_address;
  protected boolean hidden;

  protected String id;
  protected String title;
  protected Map<String, String> properties;
  protected String propfile;
  protected String queryfile;

  protected String topicListeners;
  
  protected Map<String, TopicMapReferenceIF> refmap;

  protected RDBMSStorage storage;

  /**
   * PUBLIC: Creates an rdbms topic map source. Use the setter methods
   * to provide the instance with the properties it needs.<p>
   *
   * If the property file has not been given the
   * 'net.ontopia.topicmaps.impl.rdbms.PropertyFile' system properties
   * will be used.<p>
   */
  public RDBMSTopicMapSource() {
  }

  /**
   * INTERNAL: Creates an rdbms topic map source with the database
   * property file set.
   */
  public RDBMSTopicMapSource(String propfile) {
    this.propfile = propfile;
  }

  /**
   * INTERNAL: Creates an rdbms topic map source with the specified
   * database properties.
   * @since 1.2.4
   */
  public RDBMSTopicMapSource(Map<String, String> properties) {
    this.properties = properties;
  }

  private boolean isInitialized() {
    return (refmap != null);
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
   * PUBLIC: Sets an additional SQL queries file to be loaded. Can be
   * used to override or extend the SQL query set used by Ontopia.
   * Warning: overriding queries should only be done by experts.
   * @param queryfile The file to load. Will be passed to 
   * {@link StreamUtils#getInputStream(java.lang.String)}
   * @since 5.4.0
   */
  public void setQueryfile(String queryfile) {
    this.queryfile = queryfile;
  }

  /**
   * PUBLIC: returns the additional SQL query file set by 
   * {@link #setQueryfile(java.lang.String)}.
   * @return The query file set.
   * @since 5.4.0
   */
  public String getQueryfile() {
    return queryfile;
  }

  @Override
  public synchronized Collection<TopicMapReferenceIF> getReferences() {
    if (!isInitialized()) {
      refresh();
    }
    return refmap.values();
  }

  protected RDBMSStorage createStorage() throws IOException {
    if (storage == null) {
      if (propfile != null) {
        this.storage = new RDBMSStorage(propfile);
      } else if (properties != null) {
        this.storage = new RDBMSStorage(properties);
      } else {
        throw new OntopiaRuntimeException("propertyFile property must be specified on source with id '" + getId() + "'.");
      }
      if (queryfile != null) {
        InputStream stream = StreamUtils.getInputStream(queryfile);
        if (stream == null) {
          throw new IOException("Could not find query file " + queryfile);
        }
        storage.getQueryDeclarations().loadQueries(stream);
      }  
    }
    return storage;
  }
  
  @Override
  public synchronized void refresh() {    
    Connection conn = null;
    try {
      
      createStorage();
      
      // Create connection for transaction
      conn = storage.getConnectionFactory(true).requestConnection();
      Statement stm = conn.createStatement();
      ResultSet rs = stm.executeQuery("select id, title, base_address from TM_TOPIC_MAP");
      
      // Loop over result rows
      Map<String, TopicMapReferenceIF> newmap = new HashMap<String, TopicMapReferenceIF>();
      while (rs.next()) {
        // Add row object to result collection
        long topicmap_id = rs.getLong(1);
        String _title = rs.getString(2);
        String loc = rs.getString(3);
        String referenceId = getReferenceId(loc, _title, topicmap_id);

        // Do not create new reference if active reference exist.
        if (refmap != null) {
          TopicMapReferenceIF ref = refmap.get(referenceId);
          if (ref != null && ref.isOpen()) {
            // Use existing reference
            newmap.put(referenceId, ref);
            continue;
          }
        }
        
        if (_title == null) {
          _title = referenceId;
        }
        
        LocatorIF _base_address = this.base_address;
        if (_base_address == null) {
          if (loc != null) {
            _base_address = new URILocator(loc);
          }
        }
        
        // Create a new reference
        RDBMSTopicMapReference ref = createTopicMapReference(referenceId, _title, storage, topicmap_id, _base_address);
        ref.setSource(this);

        // register topic listeners
        if (topicListeners != null) {
          ref.registerTopicListeners(topicListeners);
        }
        
        newmap.put(referenceId, ref);
      }
      rs.close();
      stm.close();

      if (refmap != null) {
        // Close open reference no longer in refmap
        Collection<TopicMapReferenceIF> danglingReferences = new HashSet(refmap.values());
        danglingReferences.removeAll(newmap.values());
        for (TopicMapReferenceIF danglingReference : danglingReferences) {
          if (danglingReference.isOpen()) {
            danglingReference.close();
          }
        }
      }
      
      // Update reference map
      refmap = newmap;
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    } finally {
      if (conn != null) try { conn.close(); } catch (Exception e) { }
    }
  }

  @Override
  public void close() {
    if (storage != null) {
      storage.close();
    }
  }

  
  protected String getReferenceId(String baseAdress, String title, long topicmap_id) {
    if (id == null) {
      return "RDBMS-" + topicmap_id;
    } else {
      return id + "-" + topicmap_id;
    }
  }
  
  protected RDBMSTopicMapReference createTopicMapReference(String referenceId, String title, RDBMSStorage storage, long topicmapId, LocatorIF baseAddress) {
    return new RDBMSTopicMapReference(referenceId, title, storage, topicmapId, baseAddress);
  }

  @Override
  public boolean supportsCreate() {
    return getSupportsCreate();
  }

  public boolean getSupportsCreate() {
    return supportsCreate;
  }
  
  public void setSupportsCreate(boolean supportsCreate) {
    this.supportsCreate = supportsCreate;
  }

  @Override
  public boolean supportsDelete() {
    return getSupportsDelete();
  }

  public boolean getSupportsDelete() {
    return supportsDelete;
  }
  
  public void setSupportsDelete(boolean supportsDelete) {
    this.supportsDelete = supportsDelete;
  }

  @Override
  public synchronized TopicMapReferenceIF createTopicMap(String name, String baseAddress) {
    if (!supportsCreate()) {
      throw new UnsupportedOperationException("This source does not support creating new topic maps.");
    }
    // create topic map instance
    RDBMSTopicMapStore store = null;
    long topicmap_id = -1;
    try {
      createStorage();
      store = new RDBMSTopicMapStore(storage);
      TopicMap tm = (TopicMap)store.getTopicMap();
      tm.setTitle(name);
      topicmap_id = tm.getLongId();
      LocatorIF _base_address = (baseAddress == null ? null : new URILocator(baseAddress));
      store.setBaseAddress(_base_address);
      store.commit();

      // create topic map reference
      String id = getReferenceId(baseAddress, name, topicmap_id);
      String title = (name == null ? id : name);
      RDBMSTopicMapReference ref = createTopicMapReference(id, title, storage, topicmap_id, _base_address);
      ref.setSource(this);

      // register topic listeners
      if (topicListeners != null) {
        ref.registerTopicListeners(topicListeners);
      }
      
      if (refmap == null) {
        refmap = new HashMap<String, TopicMapReferenceIF>();
      }
      refmap.put(id, ref);
      return ref;
    
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    } finally {
      if (store != null) {
        store.close();
      }
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
