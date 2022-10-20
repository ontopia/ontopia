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
import java.io.PrintWriter;
import java.util.Collections;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.TopicMaps;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import net.ontopia.topicmaps.utils.TopicStringifiers;
import org.apache.commons.lang3.StringUtils;

/**
 * INTERNAL: Experimental auto suggest servlet.
 */
public class AutoSuggestServlet extends HttpServlet {

  private int limit = 25;
  private String query = "select $T, $DESC from value-like($TN, %TERM%), topic-name($T, $TN), { i\"http://purl.org/dc/elements/1.1/description\"($T, $DESC) } order by $T, $DESC desc?";

  @Override
  public void init() throws ServletException {
   // no-op
  }
    
  @Override
  public void destroy() {
    // no-op
  }

  private String escape(String s) {
    return StringUtils.replace(StringUtils.replace(s, "\"", "\\\""), "\n", " ");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    response.setContentType("text/plain; charset=utf-8");

    String tmid = request.getParameter("tm");
    if (tmid == null) {
      throw new ServletException("Parameter tm is not given.");
    }
    String term = request.getParameter("term");
    if (term == null) {
      throw new ServletException("Parameter term is not given.");
    }

    TopicMapStoreIF store = TopicMaps.createStore(tmid, true);
	
    if (store instanceof net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore) {
      term = term + '*';
    }

    try {
      QueryProcessorIF qp = QueryUtils.getQueryProcessor(store.getTopicMap());
      QueryResultIF qr = qp.execute(query, Collections.singletonMap("TERM", term));
      System.out.println("query: " + query);
      PrintWriter w = response.getWriter();
      w.write("{ results: [\n");
      int count = 0;
      while (qr.next()) {
        if (count > limit) {
          break;
        }
        if (count > 0) {
          w.write(",\n");
        }
        TopicIF topic = (TopicIF)qr.getValue(0);
        String desc = (String)qr.getValue(1);
        w.write("    { id: \"");
        w.write(topic.getObjectId());
        w.write("\", value: \"");
        w.write(escape(TopicStringifiers.toString(topic)));
        w.write("\", info: \"");
        w.write((desc == null ? "" : escape(desc)));
        w.write("\" }");
        count++;
      }
      w.write("\n] }\n");

    } catch (Exception e) {
      throw new ServletException(e);
    } finally {
      store.close();
    }
  }

}
