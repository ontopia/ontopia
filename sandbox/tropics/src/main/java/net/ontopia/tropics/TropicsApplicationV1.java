package net.ontopia.tropics;

import net.ontopia.topicmaps.entry.TopicMapRepositoryIF;
import net.ontopia.tropics.resources.GroupResource;
import net.ontopia.tropics.resources.SearchResource;
import net.ontopia.tropics.resources.TopicMapResource;
import net.ontopia.tropics.resources.TopicMapsResource;
import net.ontopia.tropics.resources.TopicResource;
import net.ontopia.tropics.resources.TopicsResource;
import net.ontopia.tropics.resources.Tracer;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

public class TropicsApplicationV1 extends Application {
  private final TopicMapRepositoryIF tmRepository;
  
  // Create a router Restlet that routes each call.
  private final Router router = new Router(getContext());;

  public TropicsApplicationV1(TopicMapRepositoryIF tmRepository) {
    this.tmRepository = tmRepository;
  }

  /**
   * Creates a root Restlet that will receive all incoming calls.
   */
  @Override
  public synchronized Restlet createInboundRoot() {
    // Define test routes
    router.attach("/trace", Tracer.class);

    // Define topic maps routes
    router.attach("/topicmaps", TopicMapsResource.class);
    router.attach("/topicmaps/", TopicMapsResource.class);
    router.attach("/topicmaps/{topicmapId}", TopicMapResource.class);

    // Define topic map group routes
    router.attach("/groups/{groupId}", GroupResource.class);

    // Define topic routes
    router.attach("/topics", TopicsResource.class);
    router.attach("/topics/", TopicsResource.class);
    router.attach("/topics/{topicId}", TopicResource.class);

    // Define querying routes
    router.attach("/search", SearchResource.class);

    return router;
  }

  public TopicMapRepositoryIF getTopicMapRepository() {
    return tmRepository;
  }
  
  public Router getRouter() {
    return router;
  }
}
