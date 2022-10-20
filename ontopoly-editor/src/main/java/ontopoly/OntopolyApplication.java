/*
 * #!
 * Ontopoly Editor
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
package ontopoly;

import java.io.Serializable;

import ontopoly.model.TopicMap;
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
import ontopoly.pages.SearchPage;
import ontopoly.pages.SignInPage;
import ontopoly.pages.SignOutPage;
import ontopoly.pages.StartPage;
import ontopoly.pages.TopicTypesPage;
import ontopoly.pages.UpgradePage;
import ontopoly.sysmodel.OntopolyRepository;
import ontopoly.utils.AbsoluteUrlRequestCodingStrategy;
import ontopoly.utils.OccurrenceImageRequestTargetUrlCodingStrategy;

import org.apache.wicket.Page;
import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequestCycleProcessor;
import org.apache.wicket.protocol.http.request.WebRequestCodingStrategy;
import org.apache.wicket.request.IRequestCodingStrategy;
import org.apache.wicket.request.IRequestCycleProcessor;
import org.apache.wicket.request.target.coding.MixedParamUrlCodingStrategy;
import org.apache.wicket.settings.IApplicationSettings;
import org.apache.wicket.settings.IExceptionSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OntopolyApplication extends WebApplication implements Serializable {

  private static final Logger log = LoggerFactory.getLogger(OntopolyApplication.class);
  
  protected OntopolyRepository repository;
  protected LockManager lockManager = new LockManager();

  public static final float CURRENT_VERSION_NUMBER = 2.0f;
  
  @Override
  public Class<? extends Page> getHomePage() {
    return StartPage.class;
  }

  @Override
  protected void onDestroy() {    
    // called when wicket servlet shuts down
    log.info("Ontopoly: Shutting down Wicket.");
    super.onDestroy();
    repository = null; // FIXME: or close the repository if possible
  }

  @Override
  public RequestCycle newRequestCycle(Request request, Response response) {
    return new OntopolyRequestCycle(this, request, response);
  }
  
  @Override
  protected IRequestCycleProcessor newRequestCycleProcessor() {
    if (getUseAbsoluteUrlRequestCodingStrategy()) {
      return new WebRequestCycleProcessor() {
        @Override
        protected IRequestCodingStrategy newRequestCodingStrategy() {
          return new AbsoluteUrlRequestCodingStrategy(new WebRequestCodingStrategy());
        }
      };
    } else {
      return super.newRequestCycleProcessor();
    }
  }

  protected boolean getUseAbsoluteUrlRequestCodingStrategy() {
    return false;
  }
  
  public synchronized OntopolyRepository getOntopolyRepository() {
    if (repository == null) {
      repository = new OntopolyRepository();
    }
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
    mount(new MixedParamUrlCodingStrategy("signin", SignInPage.class, new String[] {}));
    mount(new MixedParamUrlCodingStrategy("signout", SignOutPage.class, new String[] {}));

    mount(new MixedParamUrlCodingStrategy("search", SearchPage.class, new String[] {"topicMapId"}));
    
    mount(new MixedParamUrlCodingStrategy("convert-topicmap", ConvertPage.class, new String[] {"topicMapId"}));
    mount(new MixedParamUrlCodingStrategy("upgrade-topicmap", UpgradePage.class, new String[] {"topicMapId"}));
    
    mount(new MixedParamUrlCodingStrategy("admin", AdminPage.class, new String[] {"topicMapId"}));
    mount(new MixedParamUrlCodingStrategy("description", DescriptionPage.class, new String[] {"topicMapId"}));

    mount(new MixedParamUrlCodingStrategy("instance", InstancePage.class, new String[] {"topicMapId", "topicId"}));
    mount(new MixedParamUrlCodingStrategy("instance-embedded", EmbeddedInstancePage.class, new String[] {"topicMapId", "topicId"}));
    mount(new MixedParamUrlCodingStrategy("instance-embedded-hierarchical", EmbeddedHierarchicalInstancePage.class, new String[] {"topicMapId", "topicId"}));
    
    mount(new MixedParamUrlCodingStrategy("instances", InstancesPage.class, new String[] {"topicMapId", "topicId"}));
    mount(new MixedParamUrlCodingStrategy("instance-types", InstanceTypesPage.class, new String[] {"topicMapId"}));
    
    mount(new MixedParamUrlCodingStrategy("topic-types", TopicTypesPage.class, new String[] {"topicMapId"}));
    mount(new MixedParamUrlCodingStrategy("occurrence-types", OccurrenceTypesPage.class, new String[] {"topicMapId"}));
    mount(new MixedParamUrlCodingStrategy("association-types", AssociationTypesPage.class, new String[] {"topicMapId"}));
    mount(new MixedParamUrlCodingStrategy("role-types", RoleTypesPage.class, new String[] {"topicMapId"}));
    mount(new MixedParamUrlCodingStrategy("name-types", NameTypesPage.class, new String[] {"topicMapId"}));

    mount(new MixedParamUrlCodingStrategy("association-transform", AssociationTransformPage.class, new String[] {"topicMapId", "topicId"}));

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
    return new OntopolySession(request, response, this);
  }
  
  protected OntopolyAccessStrategy newAccessStrategy() {	  
    return new OntopolyAccessStrategy() {
      @Override
        public boolean isEnabled() {
        return false;
      }
    };
  }
  
  @Override
  public void sessionDestroyed(String sessionId) {
    super.sessionDestroyed(sessionId);
    lockManager.expireLocksForOwner(sessionId);
  }
    
}
