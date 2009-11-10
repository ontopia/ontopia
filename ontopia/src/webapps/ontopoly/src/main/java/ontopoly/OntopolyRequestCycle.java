package ontopoly;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.entry.TopicMapRepositoryIF;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.TopicMap;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.sysmodel.OntopolyRepository;
import net.ontopia.utils.ObjectUtils;
import net.ontopia.utils.OntopiaRuntimeException;
import ontopoly.pages.ConvertPage;
import ontopoly.pages.InternalErrorPageWithException;
import ontopoly.pages.PageExpiredErrorPage;
import ontopoly.pages.StartPage;
import ontopoly.pages.UpgradePage;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.protocol.http.PageExpiredException;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebRequestCycle;

public class OntopolyRequestCycle extends WebRequestCycle {

  //! private static final Logger log = LoggerFactory.getLogger(OntopolyRequestCycle.class);
  
  private static ThreadLocal<Map<String,TopicMap>> topicmaps = new ThreadLocal<Map<String,TopicMap>>();

  private OntopolyRepository repository;
  
  public OntopolyRequestCycle(OntopolyRepository repository, WebApplication application, Request request, Response response) {
    super(application, (WebRequest)request, response);
    this.repository = repository;
  }
  
  @Override
  protected void onBeginRequest() {
    //! log.info("OKS: onBeginRequest: " + this);
    super.onBeginRequest();    
  }

  @Override
  protected void onEndRequest() {
    //! log.info("OKS: onEndRequest: " + this);
    super.onEndRequest();
    Map<String,TopicMap> tms = topicmaps.get();
    if (tms != null && !tms.isEmpty()) {
      Iterator iter = tms.values().iterator();
      while (iter.hasNext()) {
        TopicMap topicmap = (TopicMap)iter.next();
        TopicMapIF tm = topicmap.getTopicMapIF();
        TopicMapStoreIF store = tm.getStore();
        try {
          store.commit();
          //! log.info("OKS: c" + topicmap.getId());
        } catch (Exception ex) {
          // FIXME: log exception
        } finally {
          store.close();
        }
      }
    }
    if(tms != null) {
      tms.clear();
    }
    
  }
  
  @Override
  protected void logRuntimeException(RuntimeException e) {
    // do not log unauthorized exceptions
    if (!(e instanceof org.apache.wicket.authorization.UnauthorizedInstantiationException))
      super.logRuntimeException(e);
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public Page onRuntimeException(Page page, RuntimeException e) {
    
    //! log.info("OKS: onRuntimeException: " + this);
    Map<String,TopicMap> tms = topicmaps.get();
    if (tms != null && !tms.isEmpty()) {
      Iterator iter = tms.values().iterator();
      while (iter.hasNext()) {
        TopicMap topicmap = (TopicMap)iter.next();
        TopicMapIF tm = topicmap.getTopicMapIF();
        TopicMapStoreIF store = tm.getStore();
        try {
          store.abort();
          //! log.info("OKS: a" + topicmap.getId());
        } catch (Exception ex) {
          // FIXME: log exception
        } finally {
          store.close();
        }
      }
    }
    if(tms != null) {
      tms.clear();
    }
    
    Throwable cause = e;
    if(cause instanceof WicketRuntimeException) 
    cause = cause.getCause(); 
    if(cause instanceof InvocationTargetException) 
    cause = cause.getCause();
    if (e instanceof PageExpiredException) {

      String referer = ((WebRequest)this.getRequest()).getHttpServletRequest().getHeader("Referer");
      if (referer != null) {
        Pattern pattern = Pattern.compile("ontopoly.pages.\\w*");
        Matcher matcher = pattern.matcher(referer);
        String pageName = "";
        if (matcher.find())
          pageName = matcher.group();
        
        pattern = Pattern.compile("&([^&]*)=([^&]*)");
        matcher = pattern.matcher(referer);
        
        Map<String,String> pageParametersMap = new HashMap<String,String>();
        while (matcher.find())
          pageParametersMap.put(matcher.group(1), matcher.group(2));
        
        Class<? extends Page> classObjectForPage = null;
        try {
          classObjectForPage = (Class<? extends Page>)Class.forName(pageName);
        }
        catch(ClassNotFoundException cnfe) {
          //! System.out.println("Couldn't find a class with the name: "+pageName);
        }        
        return new PageExpiredErrorPage(classObjectForPage, new PageParameters(pageParametersMap));
      }
    }
    if (cause instanceof Exception) {
      return new InternalErrorPageWithException(page, e);
    }
    return super.onRuntimeException(page, e);
  }

  // ---

  public TopicMap getTopicMap(String topicMapId) {    
    // go ahead and hand out topic map
    Map<String,TopicMap> tms = topicmaps.get();
    TopicMap tm = (tms == null ? null : (TopicMap)tms.get(topicMapId));
    if (tm == null) {
      if (tms == null) {
        tms = new HashMap<String,TopicMap>();
        topicmaps.set(tms);
      }
      
      TopicMapRepositoryIF rep = repository.getTopicMapRepository();
      TopicMapStoreIF store = createStore(topicMapId, false, rep);
      //! log.info("OKS: +" + topicMapId);
      tm = new TopicMap(repository, store.getTopicMap(), topicMapId);
      tms.put(topicMapId, tm);
    }    
    // check if topic map contains ontopoly ontology
    Class pageClass = this.getResponsePageClass();
    if (pageClass != null) {
      boolean performingUpgrade = ObjectUtils.equals(ConvertPage.class, pageClass);
      boolean performingConvert = ObjectUtils.equals(UpgradePage.class, pageClass);
  
      //! boolean performingConvert = ;
      if (!performingUpgrade && !performingConvert) {
        if (!tm.containsOntology()) {
          // not an ontopoly topic map, so we'll have to redirect to the convert/upgrade page
          PageParameters pageParameters = new PageParameters();
          pageParameters.put("topicMapId", topicMapId);
          throw new RestartResponseException(ConvertPage.class, pageParameters);        
  //        performingConvert = true;
        }
        // if it is an old ontopoly topic map then do an upgrade
        if (!performingConvert) {
          float ontologyVersion = tm.getOntologyVersion(); 
          if (ontologyVersion < OntopolyRepository.CURRENT_VERSION_NUMBER) {
            // not an ontopoly topic map, so we'll have to redirect to the upgrade page
            PageParameters pageParameters = new PageParameters();
            pageParameters.put("topicMapId", topicMapId);
            throw new RestartResponseException(UpgradePage.class, pageParameters);       
          } else if (ontologyVersion > OntopolyRepository.CURRENT_VERSION_NUMBER) {
            // FIXME: should create separate page for warning about future topic maps
            PageParameters pageParameters = new PageParameters();
            pageParameters.put("message", "topicmap-is-created-by-a-newer-ontopoly");
//            throw new RestartResponseException(StartPage.class, pageParameters);                   
            setResponsePage(StartPage.class, pageParameters);
            setRedirect(true);
          }
        } 
      }
    }
    return tm;
  }

  private TopicMapStoreIF createStore(String topicmapId, boolean readOnly, TopicMapRepositoryIF repository) {
    TopicMapReferenceIF ref = repository.getReferenceByKey(topicmapId);
    if (ref == null)
      throw new OntopiaRuntimeException("Topic map '" + topicmapId +
                                        "' not found in ontopoly repository.");
    try {
      return ref.createStore(readOnly);
    } catch (java.io.IOException e) {
      throw new OntopiaRuntimeException(e);
    }    
  }
  

}

