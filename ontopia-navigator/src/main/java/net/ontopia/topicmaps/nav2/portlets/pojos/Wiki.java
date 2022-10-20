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

package net.ontopia.topicmaps.nav2.portlets.pojos;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import net.ontopia.topicmaps.utils.TopicStringifiers;
import net.ontopia.utils.OntopiaRuntimeException;
import org.apache.commons.lang3.StringUtils;

/**
 * PUBLIC: This component can render wiki formatting a la MediaWiki
 * into HTML.
 */
public class Wiki {
  private static Function<TopicIF, String> linker =
    new Linker();

  public static void render(String text, Writer out, TopicMapIF topicmap,
                            Map params) throws IOException {
    out.write("<p>");

    int old = 0;
    for (int ix = 0; ix < text.length(); ix++) {
      String output = null;
      int end = ix;

      if (text.charAt(ix) == '\n') {
        if (ix+1 == text.length()) {
          output = "";
          end = ix;
        } else if (text.charAt(ix+1) == '\n') {
          output = "</p><p>";
          end = ix + 1;
        } else if (text.charAt(ix+1) == '\r' && text.charAt(ix+2) == '\n') {
          output = "</p><p>";
          end = ix + 2;
        } else if (text.charAt(ix+1) == '*') {
          int pos = findNextDoubleNewline(text, ix+1, out);
          output = makeList(text.substring(ix+1, pos));
          end = pos + 1;
        }
      } else if (text.charAt(ix) == '=') {
        int pos = text.indexOf('=', ix + 1);
        output = "<h2>" + text.substring(ix+1, pos) + "</h2>";
        end = pos;
      } else if (text.charAt(ix) == '<') {
        if (text.substring(ix, ix+5).equals("<pre>")) {
          int pos = text.indexOf("</pre>", ix + 1);
          output = "<pre>" + escape(text.substring(ix + 5, pos)) + "</pre>";
          end = pos + 6;
        } else if (text.substring(ix, ix+7).equals("<tolog>")) {
          int pos = text.indexOf("</tolog>", ix + 1);
          String query = text.substring(ix+7, pos);
          output = runQuery(query, topicmap, params);
          end = pos + 7; // ?
        }
      } else if (text.charAt(ix) == '\'') {
        int length = 0;
        String gi = null;
        if (text.substring(ix, ix+3).equals("'''")) {
          length = 3;
          gi = "b";
        } else if (text.substring(ix, ix+2).equals("''")) {
          length = 2;
          gi = "i";
        }
        
        if (gi != null) {
          int pos = text.indexOf("''" + (length == 3 ? "'" : ""), ix + length);
          output = "<" + gi + ">" + text.substring(ix + length, pos) + "</" + gi + ">";
          end = pos + length - 1;
        }
      } else if (text.charAt(ix) == '[') {
        int pos = text.indexOf(']', ix);
        String name = text.substring(ix + 1, pos);
        TopicIF topic = getTopic(name, topicmap);
        output = getString(topic, params);
        end = pos;
      }
      
      if (output != null) {   
        out.write(text.substring(old, ix));
        out.write(output);
        ix = end;
        old = end + 1;
      }
    }
    out.write(escape(text.substring(old)));
    
    out.write("</p>");
  }
  
  private static String escape(String str) {
    str = StringUtils.replace(str, "<", "&lt;");
    str = StringUtils.replace(str, ">", "&gt;");
    return str;
  }

  private static String makeList(String rawlist) {
    return "<ul>\n" + StringUtils.replace(rawlist, "*", "<li>") + "</ul>";
  }

  private static int findNextDoubleNewline(String text, int ix, Writer out)
    throws IOException {
    for (; ix < text.length(); ix++) {
      if (text.charAt(ix) == '\n') {
        int start = ix++;
        while (text.charAt(ix) == ' ' || text.charAt(ix) == '\r') {
          ix++;
        }
        if (text.charAt(ix) == '\n') {
          return start;
        }
      }
    }
    return ix - 2;
  }
  
  private static String runQuery(String query, TopicMapIF topicmap, Map params) {
    try {
      StringBuilder out = new StringBuilder();
      out.append("<table>\n");
      
      QueryProcessorIF proc = QueryUtils.getQueryProcessor(topicmap);
      QueryResultIF result = proc.execute(query, params);
      
      out.append("<tr>");
      for (int ix = 0; ix < result.getWidth(); ix++) {
        out.append("<th>" + result.getColumnName(ix));
      }
      
      while (result.next()) {
        out.append("<tr>");
        for (int ix = 0; ix < result.getWidth(); ix++) {
          out.append("<td>" + getString(result.getValue(ix), params));
        }
      }
      
      out.append("</table>");
      return out.toString();
    } catch (InvalidQueryException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  private static String getString(Object value, Map params) {
    if (value instanceof TopicIF) {
      TopicIF topic = (TopicIF) value;
      Function<TopicIF, String> l = (Function) params.get("linker");
      if (l == null) {
        l = linker;
      }
      return l.apply(topic);
    } else {
      return value.toString();
    }
  }

  private static TopicIF getTopic(String name, TopicMapIF topicmap) {
    try {
      Map params = new HashMap();
      params.put("value", name);
      
      String query = "select $TOPIC from topic-name($TOPIC, $TN), " +
        "value($TN, %value%)?";
      QueryProcessorIF proc = QueryUtils.getQueryProcessor(topicmap);
      QueryResultIF result = proc.execute(query, params);
      if (result.next()) {
        return (TopicIF) result.getValue(0);
      } else {
        return null;
      }
    } catch (InvalidQueryException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  static class Linker implements Function<TopicIF, String> {
    @Override
    public String apply(TopicIF topic) {
      return "<a href=\"topic.jsp?id=" + getId(topic) +
        "\">" + TopicStringifiers.toString(topic) + "</a>";    
    }

    private String getId(TopicIF topic) {
      String base = topic.getTopicMap().getStore().getBaseAddress().getAddress();
      Iterator it = topic.getItemIdentifiers().iterator();
      while (it.hasNext()) {
        LocatorIF loc = (LocatorIF) it.next();
        String addr = loc.getAddress();
        if (addr.startsWith(base)) {
          return addr.substring(base.length() + 1);
        }
      }
      return topic.getObjectId();
    }
  }
}