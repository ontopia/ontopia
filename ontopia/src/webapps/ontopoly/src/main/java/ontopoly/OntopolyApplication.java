package ontopoly;


import net.ontopia.topicmaps.entry.TopicMaps;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.TopicMap;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.sysmodel.OntopolyRepository;
import ontopoly.pages.AdminPage;
import ontopoly.pages.AssociationTransformPage;
import ontopoly.pages.AssociationTypesPage;
import ontopoly.pages.ConvertPage;
import ontopoly.pages.DescriptionPage;
import ontopoly.pages.EmbeddedHierarchicalInstancePage;
import ontopoly.pages.EmbeddedInstancePage;
import ontopoly.pages.InstancePage;
import ontopoly.pages.InstanceTypesPage;
import ontopoly.pages.InstancesPage;
import ontopoly.pages.InternalErrorPage;
import ontopoly.pages.NameTypesPage;
import ontopoly.pages.OccurrenceTypesPage;
import ontopoly.pages.PageExpiredErrorPage;
import ontopoly.pages.RoleTypesPage;
import ontopoly.pages.SignInPage;
import ontopoly.pages.SignOutPage;
import ontopoly.pages.StartPage;
import ontopoly.pages.TopicTypesPage;
import ontopoly.pages.UpgradePage;
import ontopoly.utils.OccurrenceImageRequestTargetUrlCodingStrategy;

import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.settings.IApplicationSettings;
import org.apache.wicket.settings.IExceptionSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OntopolyApplication extends WebApplication {

  private static final Logger log = LoggerFactory.getLogger(OntopolyApplication.class);
  
  protected OntopolyRepository repository = new OntopolyRepository(TopicMaps.getRepository());
  protected LockManager lockManager = new LockManager();
  
  protected OntopolyAccessStrategy accessStrategy = new OntopolyAccessStrategy() {
    @Override
    public boolean isEnabled() {
      return false;
    }
  };
  
  public OntopolyApplication() {
  }
  
  public Class getHomePage() {
    return StartPage.class;
    //return SignInPage.class;
  }

  @Override
  protected void onDestroy() {    
    // called when wicket servlet shuts down
    log.info("OKS: Shutting down Wicket.");
    super.onDestroy();
    repository = null; // FIXME: or close the repository if possible
  }

  @Override
  public RequestCycle newRequestCycle(Request request, Response response) {
    return new OntopolyRequestCycle(repository, this, request, response);
  }

  public OntopolyRepository getOntopolyRepository() {
    return repository;
  }
  
  public LockManager getLockManager() {
    return lockManager;
  }
  
  public TopicMap getTopicMap(String topicMapId) {
    OntopolyRequestCycle rc = (OntopolyRequestCycle)RequestCycle.get();
    return rc.getTopicMap(topicMapId);
  }
  
  @Override 
  protected void init() {
    mount(new OntopolyUrlCodingStrategy("signin", SignInPage.class, new String[] {}));
    mount(new OntopolyUrlCodingStrategy("signout", SignOutPage.class, new String[] {}));

    mount(new OntopolyUrlCodingStrategy("convert-topicmap", ConvertPage.class, new String[] {"topicMapId"}));
    mount(new OntopolyUrlCodingStrategy("upgrade-topicmap", UpgradePage.class, new String[] {"topicMapId"}));
    
    mount(new OntopolyUrlCodingStrategy("admin", AdminPage.class, new String[] {"topicMapId"}));
    mount(new OntopolyUrlCodingStrategy("description", DescriptionPage.class, new String[] {"topicMapId"}));

    mount(new OntopolyUrlCodingStrategy("instance", InstancePage.class, new String[] {"topicMapId", "topicId"}));
    mount(new OntopolyUrlCodingStrategy("instance-embedded", EmbeddedInstancePage.class, new String[] {"topicMapId", "topicId"}));
    mount(new OntopolyUrlCodingStrategy("instance-embedded-hierarchical", EmbeddedHierarchicalInstancePage.class, new String[] {"topicMapId", "topicId"}));
    
    mount(new OntopolyUrlCodingStrategy("instances", InstancesPage.class, new String[] {"topicMapId", "topicId"}));
    mount(new OntopolyUrlCodingStrategy("instance-types", InstanceTypesPage.class, new String[] {"topicMapId"}));
    
    mount(new OntopolyUrlCodingStrategy("topic-types", TopicTypesPage.class, new String[] {"topicMapId"}));
    mount(new OntopolyUrlCodingStrategy("occurrence-types", OccurrenceTypesPage.class, new String[] {"topicMapId"}));
    mount(new OntopolyUrlCodingStrategy("association-types", AssociationTypesPage.class, new String[] {"topicMapId"}));
    mount(new OntopolyUrlCodingStrategy("role-types", RoleTypesPage.class, new String[] {"topicMapId"}));
    mount(new OntopolyUrlCodingStrategy("name-types", NameTypesPage.class, new String[] {"topicMapId"}));

    mount(new OntopolyUrlCodingStrategy("association-transform", AssociationTransformPage.class, new String[] {"topicMapId", "topicId"}));

    mount(new OccurrenceImageRequestTargetUrlCodingStrategy("occurrenceImages"));
    
    IApplicationSettings settings = getApplicationSettings();
    //! settings.setAccessDeniedPage(AccessDeniedPage.class);
    settings.setAccessDeniedPage(SignInPage.class);
    settings.setPageExpiredErrorPage(PageExpiredErrorPage.class);
    
    // comment: The InternalErrorPage.java should never be seen, because the method OntopolyRequestCycle.onRuntimeException()
    // overrides these settings. InternalErrorPageWithException.java are selected as the internal error page instead.
    // This was done to make the exception available to the internal error page. 
    settings.setInternalErrorPage(InternalErrorPage.class);
    // show internal error page rather than default developer page 
    getExceptionSettings().setUnexpectedExceptionDisplay(IExceptionSettings.SHOW_INTERNAL_ERROR_PAGE); 
  } 
  
  @Override
  public Session newSession(Request request, Response response) {
    return new OntopolySession(request, accessStrategy);
  }
  
  @Override
  public void sessionDestroyed(String sessionId) {
    super.sessionDestroyed(sessionId);
    lockManager.expireLocksForOwner(sessionId);
  }
    
}
