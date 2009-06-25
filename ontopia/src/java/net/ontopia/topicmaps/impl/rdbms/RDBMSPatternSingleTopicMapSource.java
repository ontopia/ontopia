
// $Id: RDBMSPatternSingleTopicMapSource.java,v 1.4 2007/11/15 09:42:01 geir.gronmo Exp $

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

  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(RDBMSPatternSingleTopicMapSource.class.getName());

  // --- TopicMapSourceIF implementation

  public TopicMapReferenceIF createTopicMap(String name, String baseAddress) {
    throw new UnsupportedOperationException();
  }

  public synchronized Collection getReferences() {
    if (reference == null) refresh();
    if (reference == null)
      return Collections.EMPTY_LIST;
    else
      return Collections.singleton(reference);
  }
  
  public synchronized void refresh() {
    if (match == null)
      throw new OntopiaRuntimeException("match property must be specified on source with id '" + getId() + "'.");
    if (pattern == null)
      throw new OntopiaRuntimeException("pattern property must be specified on source with id '" + getId() + "'.");
    if (referenceId == null)
      throw new OntopiaRuntimeException("referenceId property must be specified on source with id '" + getId() + "'.");
    
    boolean foundReference = false;
    long topicmap_id = -2;
    String _title = title;
    LocatorIF _base_address = base_address;
    
    try {
      RDBMSStorage storage = createStorage();

      // retrieve reference id from database
      Connection conn = conn = storage.getConnectionFactory(true).requestConnection();
      try {

        String sqlquery;
        if (match.equals("title"))
          sqlquery = "select max(M.id), M.title, M.base_address from TM_TOPIC_MAP M where M.title = ? group by M.title, M.base_address order by max(M.id) desc";
        else if (match.equals("comments"))
          sqlquery = "select max(M.id), M.title, M.base_address from TM_TOPIC_MAP M where M.comments = ? group by M.title, M.base_address order by max(M.id) desc";
        else
          throw new OntopiaRuntimeException("match property contains illegal value '" + match + "' on source with id '" + getId() + "'.");
        
        PreparedStatement stm = conn.prepareStatement(sqlquery);
        try {
          stm.setString(1, pattern);
          ResultSet rs = stm.executeQuery();
          if (rs.next()) {
            foundReference = true;
            topicmap_id = rs.getLong(1);
            if (_title == null)
              _title = rs.getString(2);
            if (_base_address == null) {
              String loc = rs.getString(3);
              if (loc != null)
                _base_address = Locators.getURILocator(loc);
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
        try { conn.close(); } catch (Exception e) { };
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
    } catch (MalformedURLException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
}
