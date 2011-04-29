
package net.ontopia.topicmaps.utils.sdshare.client;

import java.util.List;
import java.util.Properties;
import java.sql.Driver;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * INTERNAL: Backend which stores list of changed URIs in a database
 * via JDBC so that another process can get them from there.
 */
public class JDBCQueueBackend extends AbstractBackend
  implements ClientBackendIF {
  //static Logger log = LoggerFactory.getLogger(JDBCQueueBackend.class.getName());
  
  public void loadSnapshot(SyncEndpoint endpoint, Snapshot snapshot) {
    // FIXME: implement!
  }

  public void applyFragments(SyncEndpoint endpoint, List<Fragment> fragments) {
    Statement stmt = getConnection(endpoint.getHandle());
    try {
      try {
        for (Fragment f : fragments)
          writeResource(stmt,
                        f.getTopicSIs().iterator().next(),
                        findPreferredLink(f.getLinks()).getUri());
        stmt.getConnection().commit();
      } finally {
        stmt.close();
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private void writeResource(Statement stmt, String topicsi, String datauri)
    throws SQLException {
    stmt.executeUpdate("insert into UPDATED_RESOURCES values (" +
                       "  NULL, '" + topicsi + "', '" + datauri + "')");
  }

  public int getLinkScore(AtomLink link) {
    MIMEType mimetype = link.getMIMEType();
    // FIXME: this is too simplistic. we could probably support more
    // syntaxes than just this one, but for now this will have to do.
    if (mimetype.getType().equals("application/rdf+xml"))
      return 100;
    return 0;
  }

  private Statement getConnection(String jdbcuri) {
    try {
      Class driverclass = Class.forName("org.h2.Driver");
      Driver driver = (Driver) driverclass.newInstance();
      Properties props = new Properties();
      // props.put("user", username);
      // props.put("password", password);
      Connection conn = driver.connect(jdbcuri, props);
      Statement stmt = conn.createStatement();
      verifySchema(stmt); // check that tables exist & create if not
      return stmt;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    } catch (InstantiationException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  private void verifySchema(Statement stmt) throws SQLException {
    ResultSet rs = stmt.executeQuery("select * from information_schema.tables where " +
                                     "table_name = 'UPDATED_RESOURCES'");
    boolean present = rs.next();
    rs.close();

    if (present)
      return;

    stmt.executeUpdate("create table UPDATED_RESOURCES ( " +
                       "  id int auto_increment primary key, " +
                       "  uri varchar not null, " +
                       "  fragment_uri varchar not null )");
  }
}