
// $Id: DataIntegrationServlet.java,v 1.5 2008/01/14 11:37:14 geir.gronmo Exp $

package net.ontopia.topicmaps.nav2.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.ontopia.utils.StreamUtils;
import net.ontopia.utils.StringUtils;
import net.ontopia.utils.DeciderIF;
import net.ontopia.utils.ObjectUtils;
import net.ontopia.utils.ContainmentDecider;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.entry.TopicMaps;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.utils.TopicStringifiers;
import net.ontopia.topicmaps.utils.TopicMapSynchronizer;
import net.ontopia.topicmaps.utils.IdentityUtils;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.QueryUtils;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import java.io.File;
import java.io.PrintWriter;
import java.io.FileOutputStream;

/**
 * INTERNAL: Experimental data integration servlet.
 */
public class DataIntegrationServlet extends HttpServlet {

  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    response.setContentType("text/plain; charset=utf-8");

    // get topic map id
    String topicMapId = topicMapId = getInitParameter("topicMapId");
    if (topicMapId == null)
      throw new ServletException("Topic map identifier is not specified.");
    
    // parse path
    String path = request.getPathInfo();
    if (path == null)
      throw new ServletException("Path is missing.");
    path = path.substring(1);
    
    String[] args = StringUtils.split(path, "/");
    String name = args[0];
    String action = args[1];

    // get topics query
    String topicsQuery = getInitParameter("topicsQuery");
    if (topicsQuery == null)
      throw new ServletException("Parameter 'topicsQuery' is not specified.");

    // get characteristcs query
    String characteristicsQuery = getInitParameter("characteristicsQuery");
    if (characteristicsQuery == null)
      throw new ServletException("Parameter 'characteristicsQuery' is not specified.");
    
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

      if (action.equals("updated") || action.equals("created")) {

        DeciderIF tfilter = new DeciderIF() {
            public boolean ok(Object o) {
              return true;
            }
          };              
        
        Iterator iter = candidates.iterator();
        while (iter.hasNext()) {
          TopicIF src = (TopicIF)iter.next();

          DeciderIF sfilter;
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
            sfilter = new ContainmentDecider(characteristics);
          }
          // synchronize topic
          TopicMapSynchronizer.update(target, src, tfilter, sfilter);
        }
      } else if (action.equals("deleted")) {

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
    if (xsltstream == null)
      throw new ServletException("Could not find style sheet '" + transformId + ".xsl'");
    
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
