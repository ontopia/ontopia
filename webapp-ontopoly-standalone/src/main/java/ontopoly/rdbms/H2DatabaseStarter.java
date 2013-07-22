/*
 * #!
 * Ontopia Ontopoly webapplication (standalone)
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
