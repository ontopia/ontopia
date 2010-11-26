package net.ontopia.tropics;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import net.ontopia.topicmaps.entry.TopicMapRepositoryIF;
import net.ontopia.topicmaps.entry.TopicMaps;

import org.restlet.Component;
import org.restlet.data.Protocol;

public class TropicsServer {

  /* DEFAULTS */
  private static final int DEFAULT_PORT = 8182;

  /* RESTLET */
  private final Component component;

  /**
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    String propertiesFilename = (args.length > 0) ? args[0] : null;

    TropicsServer server = TropicsServer.create(propertiesFilename);
    server.start();
  }

  public static TropicsServer create(String propertiesFilename) {
    int port = DEFAULT_PORT;

    if (propertiesFilename != null) {
      try {
        Properties tropicsProperties = new Properties();

        ClassLoader cloader = TropicsServer.class.getClassLoader();
        InputStream istream = cloader.getResourceAsStream("tropics.props");
        tropicsProperties.load(istream);

        port = Integer.parseInt(tropicsProperties
            .getProperty("net.ontopia.tropics.Port"));
      } catch (IOException e) {
        e.printStackTrace();
        return null;
      }
    }

    // Create a new Topic Maps Repository.
    TopicMapRepositoryIF tmRepository = TopicMaps.getRepository();

    // Create a new Component.
    Component component = new Component();

    // Add a new HTTP server listening on port 8182.
    component.getServers().add(Protocol.HTTP, port);

    // Attach the sample application.
    component.getDefaultHost().attach("/api/v1",
        new TropicsApplicationV1(tmRepository));

    return new TropicsServer(component);
  }

  private TropicsServer(Component component) {
    this.component = component;
  }

  public void start() throws Exception {
    component.start();
  }

  public void stop() throws Exception {
    component.stop();
  }
}
