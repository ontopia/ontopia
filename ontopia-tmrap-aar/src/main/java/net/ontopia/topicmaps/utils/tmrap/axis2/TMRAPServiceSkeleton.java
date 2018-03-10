/*
 * #!
 * Ontopia TMRAP Axis Archive
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
package net.ontopia.topicmaps.utils.tmrap.axis2;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.nav2.core.NavigatorApplicationIF;
import net.ontopia.topicmaps.nav2.utils.NavigatorUtils;
import net.ontopia.topicmaps.utils.tmrap.TMRAPConfiguration;
import net.ontopia.topicmaps.utils.tmrap.TMRAPImplementation;
import net.ontopia.topicmaps.xml.XTMTopicMapExporter;
import org.apache.axiom.om.impl.builder.SAXOMBuilder;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.xml.sax.ContentHandler;

public class TMRAPServiceSkeleton implements TMRAPServiceSkeletonInterface {

  @Override
  public GetTopicResponse getTopic(GetTopicRequest param) {
    try {
      NavigatorApplicationIF navapp = getNavigatorApplication();
      SAXOMBuilder builder = new SAXOMBuilder();
      ContentHandler handler = new ContentHandlerAdapter(builder);
      TMRAPImplementation.getTopic(
        navapp, 
        makeLocatorCollection(param.getItems()), 
        makeLocatorCollection(param.getSubjects()), 
        makeLocatorCollection(param.getIdentifiers()), 
        param.getTmids(), 
        param.getSyntax(), 
        param.getView(), 
        handler);
      GetTopicResponse response = new GetTopicResponse();
      response.setExtraElement(builder.getRootElement());
      return response;
    } catch (Throwable e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public GetTologResponse getTolog(GetTologRequest param) {
    try {
      NavigatorApplicationIF navapp = getNavigatorApplication();
      SAXOMBuilder builder = new SAXOMBuilder();
      ContentHandler handler = new ContentHandlerAdapter(builder);
      TMRAPImplementation.getTolog(
        navapp, 
        param.getQuery(), 
        param.getTmid(), 
        param.getSyntax(), 
        param.getView(), 
        handler);
      GetTologResponse response = new GetTologResponse();
      response.setExtraElement(builder.getRootElement());
      return response;
    } catch (Throwable e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public DeleteTopicResponse deleteTopic(DeleteTopicRequest param) {
    try {
      NavigatorApplicationIF navapp = getNavigatorApplication();
      String msg = TMRAPImplementation.deleteTopic(
        navapp, 
        makeLocatorCollection(param.getItems()), 
        makeLocatorCollection(param.getSubjects()), 
        makeLocatorCollection(param.getIdentifiers()), 
        param.getTmids());
      DeleteTopicResponse response = new DeleteTopicResponse();
      response.setDeleteTopicResponse(msg);
      return response;
    } catch (Throwable e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public void addFragment(AddFragmentRequest param) {
    try {
      NavigatorApplicationIF navapp = getNavigatorApplication();
      TMRAPImplementation.addFragment(
        navapp, 
        param.getFragment(), 
        param.getSyntax(), 
        param.getTmid());
    }
    catch (Throwable e) {
      e.printStackTrace();
    }
  }

  @Override
  public void updateTopic(UpdateTopicRequest param) {
    try {
      NavigatorApplicationIF navapp = getNavigatorApplication();
      TMRAPImplementation.updateTopic(
        navapp, 
        param.getFragment(), 
        param.getSyntax(), 
        param.getTmid(), 
        makeLocatorCollection(param.getIdentifiers()), 
        makeLocatorCollection(param.getItems()), 
        makeLocatorCollection(param.getSubjects()));
    }
    catch (Throwable e) {
      e.printStackTrace();
    }
  }

  @Override
  public GetTopicPageResponse getTopicPage(GetTopicPageRequest param) {
    try {
      NavigatorApplicationIF navapp = getNavigatorApplication();
      Map config = new HashMap();
      config.put("edit_uri", getServletContext().getInitParameter("edit_uri"));
      config.put("view_uri", getServletContext().getInitParameter("view_uri"));
      TMRAPConfiguration rapconfig = new TMRAPConfiguration(config);
      TopicMapIF tm = TMRAPImplementation.getTopicPage(
        navapp, 
        rapconfig, 
        makeLocatorCollection(param.getItems()), 
        makeLocatorCollection(param.getSubjects()), 
        makeLocatorCollection(param.getIdentifiers()), 
        param.getTmids());
      SAXOMBuilder builder = new SAXOMBuilder();
      ContentHandler handler = new ContentHandlerAdapter(builder);
      XTMTopicMapExporter exporter = new XTMTopicMapExporter();
      exporter.export(tm, handler);
      GetTopicPageResponse response = new GetTopicPageResponse();
      response.setExtraElement(builder.getRootElement());
      return response;
    } catch (Throwable e) {
      e.printStackTrace();
    }
    return null;
  }

  private Collection makeLocatorCollection(String[] uris) throws URISyntaxException {
    if (uris == null) {
      return Collections.EMPTY_SET;
    }
    Collection locs = new ArrayList(uris.length);
    for (int ix = 0; ix < uris.length; ++ix) {
      locs.add(new URILocator(uris[ix]));
    }
    return locs;
  }

  private ServletContext getServletContext() {
    return (ServletContext) MessageContext.getCurrentMessageContext().getProperty(HTTPConstants.MC_HTTP_SERVLETCONTEXT);
  }

  private NavigatorApplicationIF getNavigatorApplication() {
    return NavigatorUtils.getNavigatorApplication(getServletContext());
  }

}
