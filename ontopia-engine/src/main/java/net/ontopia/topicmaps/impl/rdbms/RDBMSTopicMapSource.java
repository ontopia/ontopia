
package net.ontopia.topicmaps.impl.rdbms;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.core.Locators;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.persistence.proxy.RDBMSStorage;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.entry.TopicMapSourceIF;
import net.ontopia.utils.OntopiaRuntimeException;

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
  
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

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
    } catch (MalformedURLException e) {
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

  public synchronized Collection<TopicMapReferenceIF> getReferences() {
    if (!isInitialized()) refresh();
    return refmap.values();
  }

  protected RDBMSStorage createStorage() throws IOException {
    if (storage == null) {
      if (propfile != null)
        this.storage = new RDBMSStorage(propfile);
      else if (properties != null)
        this.storage = new RDBMSStorage(properties);
      else
        throw new OntopiaRuntimeException("propertyFile property must be specified on source with id '" + getId() + "'.");
    }
    return storage;
  }
  
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
        
        if (_title == null)
          _title = referenceId;
        
        LocatorIF _base_address = this.base_address;
        if (_base_address == null) {
          if (loc != null)
            _base_address = Locators.getURILocator(loc);
        }
        
        // Create a new reference
        RDBMSTopicMapReference ref = createTopicMapReference(referenceId, _title, storage, topicmap_id, _base_address);
        ref.setSource(this);

        // register topic listeners
        if (topicListeners != null)
          ref.registerTopicListeners(topicListeners);
        
        newmap.put(referenceId, ref);
      }
      rs.close();
      stm.close();
      
      // Update reference map
      refmap = newmap;
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    } finally {
      if (conn != null) try { conn.close(); } catch (Exception e) { };
    }
  }

  @Override
  public void close() {
    if (storage != null) {
      storage.close();
    }
  }

  
  protected String getReferenceId(String baseAdress, String title, long topicmap_id) {
    if (id == null)
      return "RDBMS-" + topicmap_id;
    else
      return id + "-" + topicmap_id;
  }
  
  protected RDBMSTopicMapReference createTopicMapReference(String referenceId, String title, RDBMSStorage storage, long topicmapId, LocatorIF baseAddress) {
    return new RDBMSTopicMapReference(referenceId, title, storage, topicmapId, baseAddress);
  }

  public boolean supportsCreate() {
    return getSupportsCreate();
  }

  public boolean getSupportsCreate() {
    return supportsCreate;
  }
  
  public void setSupportsCreate(boolean supportsCreate) {
    this.supportsCreate = supportsCreate;
  }

  public boolean supportsDelete() {
    return getSupportsDelete();
  }

  public boolean getSupportsDelete() {
    return supportsDelete;
  }
  
  public void setSupportsDelete(boolean supportsDelete) {
    this.supportsDelete = supportsDelete;
  }

  public synchronized TopicMapReferenceIF createTopicMap(String name, String baseAddress) {
    if (!supportsCreate())
      throw new UnsupportedOperationException("This source does not support creating new topic maps.");
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
      if (topicListeners != null)
        ref.registerTopicListeners(topicListeners);
      
      if (refmap == null) refmap = new HashMap<String, TopicMapReferenceIF>();
      refmap.put(id, ref);
      return ref;
    
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    } finally {
      if (store != null) store.close();
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
