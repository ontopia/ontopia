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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.QueryWrapper;
import net.ontopia.topicmaps.query.utils.RowMapperIF;
import net.ontopia.topicmaps.utils.TopicStringifiers;
import net.ontopia.topicmaps.xml.TMXMLReader;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.InputSource;

/**
 * PUBLIC: A component for including links to relevant pages from
 * other web sites via TMRAP.
 */
public class TMRAP {
  private Collection servers;
  private static Map cache = new HashMap();

  /**
   * PUBLIC: Creates the component and configures it with a set of
   * servers to query.
   * @param servers a collection of TMRAP endpoint URIs as strings
   */
  public TMRAP(Collection servers) {
    this.servers = servers;
  }

  /**
   * PUBLIC: Sends a query, returning a model of the result.
   */
  public Collection query(TopicIF topic)
    throws IOException, InvalidQueryException, URISyntaxException {
    Collection psis = new ArrayList();
    Iterator it = topic.getSubjectIdentifiers().iterator();
    while (it.hasNext()) {
      LocatorIF psi = (LocatorIF) it.next();
      psis.add(psi.getAddress());
    }
    return query(psis);
  }
  
  /**
   * PUBLIC: Sends a query, returning a model of the result.
   * @param psis a collection of PSIs as strings
   * @return a list of Server objects
   */
  public Collection query(Collection psis)
    throws IOException, InvalidQueryException, URISyntaxException {
    if (psis.isEmpty()) {
      return Collections.EMPTY_SET;
    }
    String psikey = makeKey(psis);
    
    Collection model = new ArrayList();    
    Iterator it = servers.iterator();
    while (it.hasNext()) {
      String endpoint = (String) it.next();

      // is it in the cache?
      if (cache.containsKey(endpoint + psikey)) {
        CacheEntry entry = (CacheEntry) cache.get(endpoint + psikey);
        model.add(entry.getObject());
        continue;
      }

      // not in cache, so go look it up
      InputSource src = getInputSource(endpoint, psis);
      TMXMLReader reader =
        new TMXMLReader(src, new URILocator(src.getSystemId()));
      TopicMapIF topicmap = reader.read();
      QueryWrapper qw = new QueryWrapper(topicmap);
      qw.setDeclarations("using tmrap for i\"http://psi.ontopia.net/tmrap/\" ");
      Server server = (Server)
        qw.queryForObject("instance-of($S, tmrap:server)?", new Factory());
      model.add(server);
      qw.queryForList("instance-of($TM, tmrap:topicmap)?",
                      new Factory(server));

      // process the topic maps
      Iterator it2 = server.getTopicMaps().iterator();
      while (it2.hasNext()) {
        TopicMap tm = (TopicMap) it2.next();
        Map params = qw.makeParams("tm", tm.getTopic());
        qw.queryForList("tmrap:contained-in(%tm% : tmrap:container, $P : tmrap:containee)?", new Factory(tm), params);
      }

      // update the cache
      cache.put(endpoint + psikey, new CacheEntry(server));
    }
    return model;
  }

  public Collection getAllPages(Collection model) {
    Collection pages = new ArrayList();
    Iterator it = model.iterator();
    while (it.hasNext()) {
      Server server = (Server) it.next();
      pages.addAll(server.getPages());
    }
    return pages;
  }

  public Map getPageMap(Collection model) {
    Map map = new HashMap();
    Iterator it = model.iterator();
    while (it.hasNext()) {
      Server server = (Server) it.next();
      Iterator it2 = server.getPages().iterator();
      while (it2.hasNext()) {
        Page page = (Page) it2.next();
        Collection pages;
        if (map.containsKey(page.getTypeURI())) {
          pages = (Collection) map.get(page.getTypeURI());
        } else {
          pages = new ArrayList();
          map.put(page.getTypeURI(), pages);
        }
        pages.add(page);
      }
    }
    return map;
  }

  /* ----- MODEL ----------------------------------------------------- */
  public class Server {
    private TopicIF topic;
    private Collection topicmaps;

    public Server(TopicIF topic) {
      this.topic = topic;
      this.topicmaps = new ArrayList();
    }

    public String getName() {
      return TopicStringifiers.toString(topic);
    }

    public TopicIF getTopic() {
      return topic;
    }

    public Collection getTopicMaps() {
      return topicmaps;
    }

    public Collection getPages() {
      Collection pages = new ArrayList();
      Iterator it = topicmaps.iterator();
      while (it.hasNext()) {
        TopicMap tm = (TopicMap) it.next();
        pages.addAll(tm.getPages());
      }
      return pages;
    }

    private void addTopicMap(TopicMap tm) {
      topicmaps.add(tm);
    }
  }

  public class TopicMap {
    private TopicIF topic;
    private Collection pages;

    public TopicMap(Server server, TopicIF topic) {
      this.topic = topic;
      this.pages = new ArrayList();
      server.addTopicMap(this);
    }

    public TopicIF getTopic() {
      return topic;
    }

    public Collection getPages() {
      return pages;
    }
    
    private void addPage(Page page) {
      pages.add(page);
    }
  }

  public class Page { // could be edit-page, view-page, or something else
    private TopicIF topic;

    public Page(TopicMap topicmap, TopicIF topic) {
      this.topic = topic;
      topicmap.addPage(this);
    }

    public String getName() {
      return TopicStringifiers.toString(topic);
    }

    public String getURI() {
      LocatorIF loc = topic.getSubjectLocators().iterator().next();
      return loc.getAddress();
    }

    public String getTypeURI() { // tells us if it's edit-page, view-page ...
      if (topic.getTypes().isEmpty()) {
        return null;
      }

      TopicIF type = topic.getTypes().iterator().next();
      if (type.getSubjectIdentifiers().isEmpty()) {
        return null;
      }

      LocatorIF psi = type.getSubjectIdentifiers().iterator().next();
      return psi.getAddress();
    }
  }

  class Factory implements RowMapperIF {
    private Server server;
    private TopicMap topicmap;

    private Factory() {
    }    
    
    private Factory(Server server) {
      this.server = server;
    }

    private Factory(TopicMap topicmap) {
      this.topicmap = topicmap;
    }
    
    @Override
    public Object mapRow(QueryResultIF result, int rowno) {
      if (server == null && topicmap == null) {
        return new Server((TopicIF) result.getValue(0));
      }
      if (server != null && topicmap == null) {
        return new TopicMap(server, (TopicIF) result.getValue(0));
      } else {
        return new Page(topicmap, (TopicIF) result.getValue(0));
      }
    }
  }
  
  /* ----- CACHE ENTRY ----------------------------------------------- */

  class CacheEntry {
    private Server object;

    public CacheEntry(Server object) {
      this.object = object;
    }

    public Server getObject() {
      return object;
    }
  }
  
  /* ----- INTERNAL -------------------------------------------------- */

  private InputSource getInputSource(String endpoint, Collection psis)
    throws IOException {
    String uri = addParameters(endpoint, psis);
    URL url = new URL(uri);
    InputSource src = new InputSource(uri);
    src.setByteStream(url.openStream());
    return src;
  }
  
  private String addParameters(String endpoint, Collection psis) {
    StringBuilder buf = new StringBuilder(endpoint);
    buf.append('?');
    Iterator it = psis.iterator();
    while (it.hasNext()) {
      String psi = (String) it.next();
      buf.append("identifier=");
      buf.append(StringUtils.replace(psi, "#", "%23"));
      if (it.hasNext()) {
        buf.append("&");
      }
    }
    return buf.toString();
  }

  private String makeKey(Collection psis) {
    List uris = new ArrayList(psis);
    Collections.sort(uris);
    StringBuilder sb = new StringBuilder();
    for (int ix = 0; ix < uris.size(); ix++) {
      sb.append((String) uris.get(ix));
    }
    return sb.toString();
  }
}
