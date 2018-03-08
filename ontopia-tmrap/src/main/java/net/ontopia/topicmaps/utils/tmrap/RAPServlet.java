/*
 * #!
 * Ontopia TMRAP
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

package net.ontopia.topicmaps.utils.tmrap;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.nav2.core.NavigatorApplicationIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.utils.NavigatorUtils;
import net.ontopia.topicmaps.xml.XTMTopicMapWriter;
import net.ontopia.xml.PrettyPrinter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EXPERIMENTAL: Implements the TMRAP protocol.
 */
public class RAPServlet extends HttpServlet {
  
  // Much wanted by Serializable. (The number is randomly typed).
  private static final long serialVersionUID = 3585458045457498992l;

  // initialization of logging facility
  private static Logger log = LoggerFactory.getLogger(RAPServlet.class.getName());
  
  // Static names for request parameters
  public static final String CLIENT_PARAMETER_NAME    = "client";
  public static final String FRAGMENT_PARAMETER_NAME  = "fragment";
  public static final String INDICATOR_PARAMETER_NAME = "identifier";
  public static final String SOURCE_PARAMETER_NAME    = "item";
  public static final String SUBJECT_PARAMETER_NAME   = "subject";
  public static final String SYNTAX_PARAMETER_NAME    = "syntax";
  public static final String TOLOG_PARAMETER_NAME     = "tolog";
  public static final String TOPICMAP_PARAMETER_NAME  = "topicmap";
  public static final String VIEW_PARAMETER_NAME      = "view";
  public static final String COMPRESS_PARAMETER_NAME  = "compress";
  public static final String STATEMENT_PARAMETER_NAME = "tolog";

  public static final String SYNTAX_ASTMA  = "text/x-astma";
  public static final String SYNTAX_LTM    = "text/x-ltm";
  public static final String SYNTAX_TM_XML = "text/x-tmxml";
  public static final String SYNTAX_TOLOG  = "text/x-tolog";
  public static final String SYNTAX_CTM    = "text/x-ctm";
  public static final String SYNTAX_XTM    = "application/x-xtm";
  
  public static final String RAP_NAMESPACE = "http://psi.ontopia.net/tmrap/";
  
  // Used to register type listeners
  private Map<TopicIF, Map<String, String>> clientListeners = new HashMap<TopicIF, Map<String, String>>();
  
  private TMRAPConfiguration rapconfig;

  // --- Servlet interface implementation
  
  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    rapconfig = new TMRAPConfiguration(config);
  }

  /** 
   * Supported TMRAP protocol requests:
   * <pre>
   *  GET /xtm-fragment?topicmap=[]&source=[]&indicator=[]
   *  GET /topic-page?topicmap=[]&source=[]&indicator=[]
   * </pre>
   */         
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    doGet(request, response, request.getRequestURL().toString());
  }
  
  /** INTERNAL
   * A variant of 'doGet' that allows the caller to specify the URLString.
   * Useful when 'request' doesn't support getRequestURL() (e.g. when testing).
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response,
      String URLString) throws IOException, ServletException {
    if (URLString.endsWith("get-tolog")) {
      getTolog(request, response);
    } else if (URLString.endsWith("get-topic")) {
      getTopic(request, response);
    } else if (URLString.endsWith("get-topic-page")) {
      getTopicPage(request, response);
    } else {
      reportError(response, "No such GET request: " + URLString);
    }
  }
  
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    doPost(request, response, request.getRequestURL().toString());
  }
  
  public void doPost(HttpServletRequest request, HttpServletResponse response,
      String URLString) throws IOException {
    if (URLString.endsWith("add-fragment")) {
      addFragment(request, response);
    } else if (URLString.endsWith("update-topic")) {
      updateTopic(request, response);
    } else if (URLString.endsWith("delete-topic")) {
      deleteTopic(request, response);
    } else if (URLString.endsWith("add-type-listener")) {
      addTypeListener(request, response);
    } else if (URLString.endsWith("remove-type-listener")) {
      removeTypeListener(request, response);
    } else if (URLString.endsWith("tolog-update")) {
      tologUpdate(request, response);
    } else {
      reportError(response, "No such POST request" + URLString);
    }
  }

  // --- TMRAP request implementations
  
  private void getTopicPage(HttpServletRequest request,
                            HttpServletResponse response)
    throws IOException { 

    try {
      // get context
      NavigatorApplicationIF navapp =
        NavigatorUtils.getNavigatorApplication(getServletContext());
      
      // get parameters
      Collection<LocatorIF> indicators = getIndicators(request);
      Collection<LocatorIF> items = getItemIdentifiers(request);
      Collection<LocatorIF> subjects = getSubjectLocators(request);
      String allowedSyntaxes[] = new String[]{SYNTAX_XTM};
      getParameter(request, response, "get-topic-page", 
          SYNTAX_PARAMETER_NAME, false, allowedSyntaxes, SYNTAX_XTM);
      String[] tmids = request.getParameterValues(TOPICMAP_PARAMETER_NAME);

      TopicMapIF tm = TMRAPImplementation.getTopicPage(navapp, rapconfig,
                                                       items, subjects, indicators,
                                                       tmids);

      // write the response
      response.setContentType("application/xml; charset=utf-8");
      new XTMTopicMapWriter(response.getWriter(), "utf-8").write(tm);
    } catch (Exception e) {
      reportError(response, e);
    } 
  }

  /**
   * Get a tolog query result.
   */  
  private void getTolog(HttpServletRequest request, 
                        HttpServletResponse response)
    throws IOException {
    
    try {
      // get context
      NavigatorApplicationIF navapp =
        NavigatorUtils.getNavigatorApplication(getServletContext()); 
      
      // set up parameters
      String query = request.getParameter(TOLOG_PARAMETER_NAME);
      String tmid = request.getParameter(TOPICMAP_PARAMETER_NAME);
      String syntax = request.getParameter(SYNTAX_PARAMETER_NAME);
      String view = request.getParameter(VIEW_PARAMETER_NAME);
      String compress_string = request.getParameter(COMPRESS_PARAMETER_NAME);
      boolean compress = compress_string != null &&
                         compress_string.equals("true");

      // invoke real implementation
      if (compress) {
        response.setContentType("application/x-gzip");
        GZIPOutputStream out = new GZIPOutputStream(response.getOutputStream());
        PrettyPrinter pp = new PrettyPrinter(out);
        TMRAPImplementation.getTolog(navapp, query, tmid, syntax, view, pp);
        out.finish(); // ensures we get a complete gzip stream...
      } else {
        // not compressed
        response.setContentType("text/xml; charset=utf-8");
        PrettyPrinter pp = new PrettyPrinter(response.getWriter(), "utf-8");
        TMRAPImplementation.getTolog(navapp, query, tmid, syntax, view, pp);
      }
    } catch (Exception e) {
      reportError(response, e);
    }
  }

  /**
   * Add a fragment to a topic map.
   */
  private void addFragment(HttpServletRequest request, 
                          HttpServletResponse response) throws IOException {
    try {
      // get context
      NavigatorApplicationIF navapp =
        NavigatorUtils.getNavigatorApplication(getServletContext()); 
      
      // set up parameters
      String syntax = request.getParameter(SYNTAX_PARAMETER_NAME);
      String fragment = request.getParameter(FRAGMENT_PARAMETER_NAME);
      String tmid = request.getParameter(TOPICMAP_PARAMETER_NAME);

      TMRAPImplementation.addFragment(navapp, fragment, syntax, tmid);
    } catch (Exception e) {
      reportError(response, e);
    }
  }

  /**
   * Update a topic with a fragment.
   */
  private void updateTopic(HttpServletRequest request, 
                           HttpServletResponse response) throws IOException {
    try {
      // get context
      NavigatorApplicationIF navapp =
        NavigatorUtils.getNavigatorApplication(getServletContext()); 
      
      // set up parameters
      String syntax = request.getParameter(SYNTAX_PARAMETER_NAME);
      String fragment = request.getParameter(FRAGMENT_PARAMETER_NAME);
      String tmid = request.getParameter(TOPICMAP_PARAMETER_NAME);
      Collection<LocatorIF> indicators = getIndicators(request);
      Collection<LocatorIF> items = getItemIdentifiers(request);
      Collection<LocatorIF> subjects = getSubjectLocators(request);

      TMRAPImplementation.updateTopic(navapp, fragment, syntax, tmid,
                                      indicators, items, subjects);
    } catch (Exception e) {
      reportError(response, e);
    }
  }
    
  /**
   * Delete a topic.
   */  
  private void deleteTopic(HttpServletRequest request, 
                          HttpServletResponse response) throws IOException {
    try {
      // get context
      NavigatorApplicationIF navapp =
        NavigatorUtils.getNavigatorApplication(getServletContext()); 
      
      // set up parameters
      Collection<LocatorIF> subjectIndicators = getIndicators(request);
      Collection<LocatorIF> sourceLocators = getItemIdentifiers(request);
      Collection<LocatorIF> subjectLocators = getSubjectLocators(request);
      String[] tmids = request.getParameterValues(TOPICMAP_PARAMETER_NAME);

      String msg = TMRAPImplementation.deleteTopic(navapp,
                                                   sourceLocators,
                                                   subjectLocators,
                                                   subjectIndicators,
                                                   tmids);

      response.setContentType("text/plain; charset=us-ascii");
      response.getWriter().write(msg);
    } catch (Exception e) {
      reportError(response, e);
    }
  }
  
  /**
   * Write XTM response for topic fragment. The requested topic is
   * serialized as a fragment.  If more than one topic is located then
   * a unifying topic is added at the end of the XTM fragment.  This
   * topic has all the identities contained in the request.
   */  
  private void getTopic(HttpServletRequest request, 
                        HttpServletResponse response)
    throws IOException, ServletException {
    // get context
    NavigatorApplicationIF navapp =
      NavigatorUtils.getNavigatorApplication(getServletContext()); 
    
    try {
      // fetch topic identity uris from request parameters
      Collection<LocatorIF> indicators = getIndicators(request);
      Collection<LocatorIF> items = getItemIdentifiers(request);
      Collection<LocatorIF> subjects = getSubjectLocators(request);
      String[] tmids = request.getParameterValues(TOPICMAP_PARAMETER_NAME);
      String syntax = request.getParameter(SYNTAX_PARAMETER_NAME);
      String view = request.getParameter(VIEW_PARAMETER_NAME);

      // call real implementation
      response.setContentType("text/xml; charset=utf-8");
      PrettyPrinter pp = new PrettyPrinter(response.getWriter(), "utf-8");
      TMRAPImplementation.getTopic(navapp,
                                   items, subjects, indicators,
                                   tmids, syntax, view, pp);
    } catch (Exception e) {
      reportError(response, e);
    }
  }

  private void addTypeListener(HttpServletRequest request, 
                          HttpServletResponse response) throws IOException {
    // -----------------------------------------------------------------------
    // | Parameter  | Required? | Repeatable? | Type   | Value      | Default |
    // -----------------------------------------------------------------------
    // | item       | no        | yes         | URI    |            |         |
    // | subject    | no        | yes         | URI    |            |         |
    // | identifier | no        | yes         | URI    |            |         |
    // | topicmap   | yes       | no          | String |            |         |
    // | client     | yes       | no          | Handle |            |         |
    // | syntax     | no        | no          | String |            |         |
    // -----------------------------------------------------------------------
    // (+) means other values may be allowed later.
    
    TopicIndexIF topicIndex = null;
    try {
      // fetch topic identity uris from request parameters
      Collection<LocatorIF> subjectIndicators = getIndicators(request);
      Collection<LocatorIF> sourceLocators = getItemIdentifiers(request);
      Collection<LocatorIF> subjectLocators = getSubjectLocators(request);
        
      // Check that the topicmap parameter was given (since it's required).
      getParameter(request, response, "add-type-listener", 
          TOPICMAP_PARAMETER_NAME, true, null, null);
  
      // Once supported, syntax will determine the output syntax.
      String allowedSyntaxes[] = new String[]{SYNTAX_XTM};
      String syntax = getParameter(request, response, "add-type-listener", 
          SYNTAX_PARAMETER_NAME, false, allowedSyntaxes, SYNTAX_XTM);
      
      String client = getParameter(request, response, "add-type-listener", 
          CLIENT_PARAMETER_NAME, false, null, SYNTAX_XTM);
      
      // get topic(s)
      topicIndex = getTopicIndex(request.getParameterValues(
          TOPICMAP_PARAMETER_NAME));
      Collection<TopicIF> topics = topicIndex.getTopics(subjectIndicators, sourceLocators, subjectLocators);
      
      if (topics.size() != 1) {
        reportError(response, "add-type-listener: Wrong number of topics.");
      }
      
      TopicIF topic = topics.iterator().next();
      
      Map<String, String> currentTypeListeners = clientListeners.get(topic);
      if (currentTypeListeners == null) {
        currentTypeListeners = new HashMap<String, String>();
        clientListeners.put(topic, currentTypeListeners);
      }
      
      // Register the client as a listener for topics of type 'topic'.
      currentTypeListeners.put(client, syntax);
    } catch (RAPServletException e) {
      reportError(response, e);
    } finally {
      closeIndex(topicIndex);
    }
  }
  
  private void removeTypeListener(HttpServletRequest request, 
      HttpServletResponse response) throws IOException {
    // -----------------------------------------------------------------------
    // | Parameter  | Required? | Repeatable? | Type   | Value      | Default |
    // -----------------------------------------------------------------------
    // | item       | no        | yes         | URI    |            |         |
    // | subject    | no        | yes         | URI    |            |         |
    // | identifier | no        | yes         | URI    |            |         |
    // | topicmap   | yes       | no          | String |            |         |
    // | client     | yes       | no          | Handle |            |         |
    // | syntax     | no        | no          | String |            |         |
    // -----------------------------------------------------------------------
    // (+) means other values may be allowed later.
    
    TopicIndexIF topicIndex = null;
    try {
      // fetch topic identity uris from request parameters
      Collection<LocatorIF> subjectIndicators = getIndicators(request);
      Collection<LocatorIF> sourceLocators = getItemIdentifiers(request);
      Collection<LocatorIF> subjectLocators = getSubjectLocators(request);
      
      // Check that the topicmap parameter was given (since it's required).
      getParameter(request, response, "remove-type-listener",
          TOPICMAP_PARAMETER_NAME, true, null, null);
      
      String client = getParameter(request, response, "remove-type-listener", 
      CLIENT_PARAMETER_NAME, false, null, SYNTAX_XTM);
      
      // get topic(s)
      topicIndex = getTopicIndex(request.getParameterValues(
          TOPICMAP_PARAMETER_NAME));
      Collection<TopicIF> topics = topicIndex.getTopics(subjectIndicators, sourceLocators, subjectLocators);
      
      if (topics.size() != 1) {
        reportError(response, "remove-type-listener: Wrong number of topics.");
      }
      
      TopicIF topic = topics.iterator().next();
      
      Map<String, String> currentTypeListeners = clientListeners.get(topic);
      if (currentTypeListeners == null) {
        reportError(response, "remove-type-listener: " +
            "Listener not found. You have to register a listener before it can " +
            "be removed.");
      }
      
      String currentListener = currentTypeListeners.remove(client);
      if (currentListener == null) {
        reportError(response, "remove-type-listener: " +
            "Listener not found. You have to register a listener before it can " +
            "be removed.");
      }
    } catch (RAPServletException e) {
      reportError(response, e);
    } finally {
      closeIndex(topicIndex);
    }
  }

  /**
   * Run a tolog update statement.
   */  
  private void tologUpdate(HttpServletRequest request, 
                           HttpServletResponse response) throws IOException {
    try {
      // get context
      NavigatorApplicationIF navapp =
        NavigatorUtils.getNavigatorApplication(getServletContext()); 
      
      // set up parameters
      String[] tmids = request.getParameterValues(TOPICMAP_PARAMETER_NAME);
      if (tmids.length != 1) {
        reportError(response, "tolog-update: Exactly one topic map ID required");
        return;
      }
      String stmt = request.getParameter(STATEMENT_PARAMETER_NAME);
      int rows = TMRAPImplementation.tologUpdate(navapp, tmids[0], stmt);

      response.setContentType("text/plain; charset=us-ascii");
      response.getWriter().write("" + rows);
    } catch (Exception e) {
      reportError(response, e);
    }
  }  
  
  // --- Internal helpers

  /**
   * Gets and validates a request parameter.
   * @param request The source of the parameters.
   * @param response The receiver of any error messages.
   * @param operationName The name or the calling operation.
   * @param parameterName The name of the parameter.
   * @param required true iff this parameter is required.
   * @param supported true iff this parameter is supported 
            (allows others than the default value).
   * @param defaultValue The value used if no parameter value is found.
   * @return The parameter value, or defaultValue if it cannot be found.
   * @throws IOException If an error occurs and the error reporting doesn't work
   */
  private String getParameter(HttpServletRequest request,
      HttpServletResponse response, String operationName,
      String parameterName, boolean required, String supported[], 
      String defaultValue) throws RAPServletException {
    String parameters[] = request.getParameterValues(parameterName);
    
    if (parameters == null || parameters.length == 0) {
      if (required) {
        throw new RAPServletException("The '" + parameterName + 
            "'-parameter is required for the " + operationName + " operation.");
      }
      return defaultValue;
    } else if (parameters.length == 1) {
      String parameter = parameters[0];
      if (!(supported == null 
          || Arrays.asList(supported).contains(parameter))) {
        throw new RAPServletException("The '" + parameterName
            + "'-parameter of the " + operationName 
            + " does not support the value: \"" + parameter
            + "\". The supported values are "
            + makeSeparatedWords(supported, ", ", " and "));
      }
      
      return parameter;
    }
    // Never suport repeated values.
    throw new RAPServletException("The '" + parameterName 
        + "'-parameter of the " + operationName + " operation does not"
        + " support repeated values.");
  }
  
  private TopicIndexIF getTopicIndex(String[] tmids)
      throws RAPServletException {
    NavigatorApplicationIF navApp =
      NavigatorUtils.getNavigatorApplication(getServletContext()); 

    if (tmids == null || tmids.length == 0) {
      return new RegistryTopicIndex(navApp.getTopicMapRepository(), true,
                                    rapconfig.getEditURI(),
                                    rapconfig.getViewURI());
    }

    List<TopicIndexIF> topicIndexes = new ArrayList<TopicIndexIF>();
    for (int i = 0; i < tmids.length; i++) {
      TopicMapIF topicmap;
      try {
        topicmap = navApp.getTopicMapById(tmids[i], true);
      } catch (NavigatorRuntimeException e) {
        log.warn("Couldn't open topic map " + tmids[i] + " because of " +
            e.getClass().getName() + " with message: " + e.getMessage());
        throw new RAPServletException("Couldn't open topic map " + tmids[i]);
      }
      TopicIndexIF currentIndex =
        new TopicMapTopicIndex(topicmap, rapconfig.getEditURI(),
                               rapconfig.getViewURI(), tmids[i]);
      topicIndexes.add(currentIndex);
    }
    return new FederatedTopicIndex(topicIndexes);
  }
  
  private Collection<LocatorIF> getURICollection(HttpServletRequest request, 
      String paramName) throws RAPServletException {
    String[] value = request.getParameterValues(paramName);
    if (value == null) {
      return Collections.emptySet();
    }
      
    HashSet<LocatorIF> uriLocators = new HashSet<LocatorIF>();
    for (int i = 0; i < value.length; i++) {
      try {
        uriLocators.add(new URILocator(value[i]));
      } catch (URISyntaxException e) {          
        log.warn("MalformedURL: " + value[i]);
        throw new RAPServletException("Malformed URI: " + value[i]);  
      }  
    }       
    return uriLocators;     
  }
    
  private Collection<LocatorIF> getIndicators(HttpServletRequest request) 
    throws RAPServletException {
    return getURICollection(request, INDICATOR_PARAMETER_NAME);
  }

  private Collection<LocatorIF> getItemIdentifiers(HttpServletRequest request) 
    throws RAPServletException  {
    return getURICollection(request, SOURCE_PARAMETER_NAME);
  }

  private Collection<LocatorIF> getSubjectLocators(HttpServletRequest request) 
    throws RAPServletException  {
    return getURICollection(request, SUBJECT_PARAMETER_NAME);
  }

  
  private class RAPServletException extends Exception {
    // Much wanted by Serializable. (The number is randomly typed).
    private static final long serialVersionUID = 7912425438445764224l;

    public RAPServletException(String message) {
      super(message);
      log.warn(message, this);      
    }
  }
  
  private String makeSeparatedWords(String[] words, String separator,
      String lastSeparator) {
    if (words.length == 0) {
      return "";
    }
    if (words.length == 1) {
      return words[0];
    }
    
    int length = words.length;
    String retVal = words[length - 2] + lastSeparator + words[length - 1];
    
    for (int i = length - 3; i >= 0; i--) {
      retVal = words[i] + separator + retVal;
    }
    return retVal;
  }
  
  private void closeIndex(TopicIndexIF topicIndex) {
    if (topicIndex != null) {
      topicIndex.close();
    }
  }
  
  private void reportError(HttpServletResponse response, String message) 
      throws IOException {
    log.warn(message);
    try {
      response.sendError(400, message);
    } catch (IOException e) {
      log.warn("Failed to report error: " + message + 
          " because sendError gave " + IOException.class.getName());
      throw e;
    }
  }

  private void reportError(HttpServletResponse response, Throwable t) 
      throws IOException {
    log.warn("Error occurred.", t);
    try {
      response.sendError(400, t.toString());
    } catch (IOException e) {
      log.warn("Failed to report error: " + t.getMessage() + 
               " because sendError gave " + IOException.class.getName());
      throw e;
    }
  }
  
}
