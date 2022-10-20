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

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.TopicMaps;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import net.ontopia.xml.PrettyPrinter;
import org.xml.sax.helpers.AttributesImpl;

/**
 * INTERNAL: Experimental data integration search servlet. Servlet
 * parameters 'topicMapId' and 'query' must both be specified.
 */
public class SearchServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    response.setContentType("text/plain; charset=utf-8");

    // get topic map id
    String topicMapId = topicMapId = getInitParameter("topicMapId");
    if (topicMapId == null) {
      throw new ServletException("Topic map identifier is not specified.");
    }

    // get query and parameters
    String query = getInitParameter("query");
    if (query == null) {
      throw new ServletException("Query is not specified.");
    }
    String arg = request.getParameter("query");
    Map params = (arg == null ? Collections.EMPTY_MAP : Collections.singletonMap("query", arg));
    
    // open topic map
    TopicMapStoreIF store = TopicMaps.createStore(topicMapId, true);    
    try {
      // formatters
      DecimalFormat df = new DecimalFormat("0.#");
      AttributesImpl atts = new AttributesImpl();
      PrettyPrinter out = new PrettyPrinter(response.getWriter(), "utf-8");
      out.startElement("", "", "search-results", atts);

      // execute query
      QueryProcessorIF qp = QueryUtils.getQueryProcessor(store.getTopicMap());
      QueryResultIF qr = qp.execute(query, params);
      while (qr.next()) {
        Object object = (Object)qr.getValue(0);
        Float relevance = (Float)qr.getValue(1);

        if (object instanceof TMObjectIF) {
          atts.addAttribute("", "", "id", "CDATA", ((TMObjectIF)object).getObjectId());
        } else {
          atts.addAttribute("", "", "id", "CDATA", object.toString());
        }
        
        atts.addAttribute("", "", "relevance", "CDATA", df.format(relevance));        
        out.startElement("", "", "hit", atts);
        atts.clear();
        
        out.endElement("", "", "hit");
      }
      
      out.endElement("", "", "search-results");
    } catch (Exception e) {
      throw new ServletException(e);
    } finally {
      store.close();
    }
  }

}
