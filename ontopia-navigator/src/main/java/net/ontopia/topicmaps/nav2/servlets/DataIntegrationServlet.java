/*
 * #!
 * Ontopia Navigator
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

package net.ontopia.topicmaps.nav2.servlets;

import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.TopicMaps;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import net.ontopia.topicmaps.utils.IdentityUtils;
import net.ontopia.topicmaps.utils.TopicMapSynchronizer;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;
import net.ontopia.utils.StreamUtils;
import org.apache.commons.lang3.StringUtils;


/**
 * INTERNAL: Experimental data integration servlet.
 */
public class DataIntegrationServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    response.setContentType("text/plain; charset=utf-8");

    // get topic map id
    String topicMapId = topicMapId = getInitParameter("topicMapId");
    if (topicMapId == null) {
      throw new ServletException("Topic map identifier is not specified.");
    }
    
    // parse path
    String path = request.getPathInfo();
    if (path == null) {
      throw new ServletException("Path is missing.");
    }
    path = path.substring(1);
    
    String[] args = StringUtils.split(path, "/");
    String name = args[0];
    String action = args[1];

    // get topics query
    String topicsQuery = getInitParameter("topicsQuery");
    if (topicsQuery == null) {
      throw new ServletException("Parameter 'topicsQuery' is not specified.");
    }

    // get characteristcs query
    String characteristicsQuery = getInitParameter("characteristicsQuery");
    if (characteristicsQuery == null) {
      throw new ServletException("Parameter 'characteristicsQuery' is not specified.");
    }
    
    TopicMapStoreIF targetStore = TopicMaps.createStore(topicMapId, false);
    try {
      final TopicMapIF target = targetStore.getTopicMap();

      // transform input document to topic map
      final TopicMapIF source = transformRequest(name, request.getInputStream(), targetStore.getBaseAddress());
      
      // find topics to synchronize
      QueryProcessorIF qp = QueryUtils.getQueryProcessor(source);
      
      List candidates = new ArrayList();
      QueryResultIF qr = qp.execute(topicsQuery);
      try {
        while (qr.next()) {
          // synchronize topic
          candidates.add(qr.getValue(0));
        }
      } finally {
        qr.close();
      }

      if ("updated".equals(action) || "created".equals(action)) {

        Predicate tfilter = (o) -> true;
        
        Iterator iter = candidates.iterator();
        while (iter.hasNext()) {
          TopicIF src = (TopicIF)iter.next();

          Predicate sfilter;
          if (characteristicsQuery == null) {
            // let everything through
            sfilter = tfilter;

          } else {
            // let the characteristics query decide what gets synchronized
            Collection characteristics = new HashSet();
            QueryResultIF cqr = qp.execute(characteristicsQuery, Collections.singletonMap("topic", src));
            try {
              while (cqr.next()) {
                // synchronize topic
                characteristics.add(cqr.getValue(0));
              }
            } finally {
              cqr.close();
            }
            sfilter = o -> characteristics.contains(o);
          }
          // synchronize topic
          TopicMapSynchronizer.update(target, src, tfilter, sfilter);
        }
      } else if ("deleted".equals(action)) {

        Iterator iter = candidates.iterator();
        while (iter.hasNext()) {
          TopicIF src = (TopicIF)iter.next();
          Collection affectedTopics = IdentityUtils.findSameTopic(target, src);
          Iterator aiter = affectedTopics.iterator();
          while (aiter.hasNext()) {
            TopicIF affectedTopic = (TopicIF)aiter.next();
            affectedTopic.remove();
          }
        }
      } else {
        throw new ServletException("Unsupported action: " + action);
      }

      targetStore.commit();      
    } catch (Exception e) {
      targetStore.abort();
      throw new ServletException(e);
    } finally {
      targetStore.close();
    }
  }
  
  public TopicMapIF transformRequest(String transformId, InputStream xmlstream, LocatorIF base) throws Exception {

    InputStream xsltstream = StreamUtils.getInputStream("classpath:" + transformId + ".xsl");    
    if (xsltstream == null) {
      throw new ServletException("Could not find style sheet '" + transformId + ".xsl'");
    }
    
    // set up source and target streams
    // Source xmlSource = new StreamSource(xmlstream);
    Source xmlSource = new StreamSource(xmlstream);
    Source xsltSource = new StreamSource(xsltstream);

    // the factory pattern supports different XSLT processors
    TransformerFactory transFact = TransformerFactory.newInstance();
    Transformer trans = transFact.newTransformer(xsltSource);

    CharArrayWriter cw = new CharArrayWriter();
    trans.transform(xmlSource, new StreamResult(cw));
    CharArrayReader cr = new CharArrayReader(cw.toCharArray());

    TopicMapStoreIF store = new InMemoryTopicMapStore();
    TopicMapIF topicmap = store.getTopicMap();
    store.setBaseAddress(base);
    XTMTopicMapReader xr = new XTMTopicMapReader(cr, base);
    xr.setValidation(false);
    xr.importInto(topicmap);
    
    return topicmap;
  }
  
}
