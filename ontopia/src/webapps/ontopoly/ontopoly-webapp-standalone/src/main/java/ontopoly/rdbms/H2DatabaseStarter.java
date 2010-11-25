package ontopoly.rdbms;

import java.io.File;
import javax.servlet.ServletContextEvent;
import java.sql.Connection;
import org.h2.server.web.DbStarter;
import net.ontopia.persistence.rdbms.*;

public class H2DatabaseStarter extends DbStarter {

  @Override
  public void contextInitialized(ServletContextEvent servletContextEvent) {
    File dbfile = new File("ontopoly.h2.db");
    boolean dbFileExists = dbfile.exists();
    super.contextInitialized(servletContextEvent);

    if (!dbFileExists) {
      try {
        Project project = DatabaseProjectReader.loadProject("classpath:net/ontopia/topicmaps/impl/rdbms/config/schema.xml");
      
        String[] platforms = new String[] { "h2", "generic" };
        GenericSQLProducer producer = new GenericSQLProducer(project, platforms);
  
        Connection conn = getConnection();
        producer.executeCreate(conn);
        conn.commit();
  
      } catch (Exception e) {
          throw new RuntimeException(e);
      }
    }
  }

}
