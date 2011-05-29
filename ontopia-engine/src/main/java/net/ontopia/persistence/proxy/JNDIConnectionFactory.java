
package net.ontopia.persistence.proxy;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.PropertyUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
  
/** 
 * INTERNAL: Connection factory that retrieves javax.sql.DataSource
 * from the JNDI environment given a JNDI name.
 */

public class JNDIConnectionFactory implements ConnectionFactoryIF {

  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(JNDIConnectionFactory.class.getName());

  protected static final String propname = "net.ontopia.topicmaps.impl.rdbms.ConnectionPool.JNDIDataSource";
  protected String jndiname;

  public JNDIConnectionFactory(String jndiname) {
    this.jndiname = jndiname;
  }
  
  public JNDIConnectionFactory(Map properties) {    
    this.jndiname = PropertyUtils.getProperty(properties, propname);
    if (this.jndiname == null)
      throw new OntopiaRuntimeException("Property '" + propname+ "' is not set. Please update the RDBMS properties file.");
  }

  public Connection requestConnection() throws SQLException {
    log.debug("Requesting connection from jndi pool.");

    try {
      // Obtain environment naming context
      Context initCtx = new InitialContext(); // TODO: Support other initial contexts?
      //! Context envCtx = (Context)initCtx.lookup("java:comp/env");
      
      // Look up our data source
      DataSource ds = (DataSource)initCtx.lookup(jndiname);

      // Allocate and use a connection from the pool
      Connection conn = ds.getConnection();

      // disable auto-commit
      conn.setAutoCommit(false);

      return conn;

    } catch (NamingException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  public void close() {
    // Nothing to do there since we do not keep anything hanging around.
  }
  
}
