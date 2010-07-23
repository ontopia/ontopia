
// $Id: RDBMSSingleTopicMapSource.java,v 1.25 2008/12/04 14:58:04 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.rdbms;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.*;
import java.util.*;
import net.ontopia.utils.*;
import net.ontopia.topicmaps.entry.*;
import net.ontopia.topicmaps.utils.*;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.core.Locators;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.persistence.proxy.*;

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
  
  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(RDBMSSingleTopicMapSource.class.getName());

  // --- TopicMapSourceIF implementation

  public TopicMapReferenceIF createTopicMap(String name, String baseAddress) {
    throw new UnsupportedOperationException();
  }

  public synchronized Collection getReferences() {
    if (reference == null) refresh();
    if (reference == null) return Collections.EMPTY_SET;
    return Collections.singleton(reference);
  }
  
  public synchronized void refresh() {
    // FIXME: for now don't recreate reference if already exists
    if (reference != null) return;

    boolean foundReference = false;
    try {
      RDBMSStorage storage = createStorage();
      
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
              if (_title == null)
                _title = rs.getString(1);
              if (_base_address == null) {
                String loc = rs.getString(2);
                if (loc != null)
                  _base_address = Locators.getURILocator(loc);
              }
            }
            rs.close();
          } finally {
            stm.close();
          }
        } catch (Exception e) {
          throw new OntopiaRuntimeException(e);
        } finally {
          try { conn.close(); } catch (Exception e) { };
        }
      }

      // create a reference id if not already exists
      if (foundReference) {
        String _referenceId = this.referenceId;
        if (_referenceId == null)
          _referenceId = getReferenceId(topicmap_id);
        
        // use reference id as title if not otherwise found
        _title = (_title != null ? _title : _referenceId);
        
        log.debug("Created new reference '" + _referenceId + "' to topic map " + topicmap_id);
        RDBMSTopicMapReference ref = new RDBMSTopicMapReference(_referenceId, _title, storage, topicmap_id, _base_address);
        ref.setSource(this);

        // register topic listeners
        if (topicListeners != null)
          ref.registerTopicListeners(topicListeners);
        
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

  public boolean supportsCreate() {
    return false;
  }

  public boolean supportsDelete() {
    return false;
  }

  // --- Internal helpers
  
  protected RDBMSStorage createStorage() throws IOException {
    if (propfile == null)
      throw new OntopiaRuntimeException("propertyFile property must be specified on source with id '" + getId() + "'.");
    return new RDBMSStorage(propfile);
  }
  
  protected String getReferenceId(long topicmap_id) {
    if (id == null)
      return "RDBMS-" + topicmap_id;
    else
      return id + "-" + topicmap_id;
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
    if (id.charAt(0) == 'M')
      setTopicMapId(Long.parseLong(id.substring(1)));
    else
      setTopicMapId(Long.parseLong(id));
  }

  /**
   * PUBLIC: Sets the id of the topic map referenced.
   */
  public void setTopicMapId(long id) {
    this.topicmap_id = id;
  }
  
  /**
   * INTERNAL: Gets the alias of the topic map reference.
   *
   * @deprecated Replaced by getReferenceId().
   */
  public String getAlias() {
    return getReferenceId();
  }

  /**
   * INTERNAL: Sets the alias of the topic map reference.
   *
   * @deprecated Replaced by setReferenceId(String).
   */
  public void setAlias(String alias) {
    setReferenceId(alias);
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
    } catch (MalformedURLException e) {
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
