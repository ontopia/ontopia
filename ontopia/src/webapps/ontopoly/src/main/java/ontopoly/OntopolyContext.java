package ontopoly;

import ontopoly.model.TopicMap;
import ontopoly.sysmodel.OntopolyRepository;

import org.apache.wicket.Application;

/**
 * INTERNAL: Utility class for getting hold of the current threads
 * repository and topic map.
 */

public class OntopolyContext {

  private OntopolyContext() {
    // hidden
  }
  
  public static OntopolyRepository getOntopolyRepository() {
    OntopolyApplication app = (OntopolyApplication)Application.get();
    return app.getOntopolyRepository();
  }
  
  public static TopicMap getTopicMap(String topicMapId) {
    OntopolyApplication app = (OntopolyApplication)Application.get();
    return app.getTopicMap(topicMapId);
  }

  public static LockManager getLockManager() {
    OntopolyApplication app = (OntopolyApplication)Application.get();
    return app.getLockManager();
  }
  
}

