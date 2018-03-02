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
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.utils.CharacteristicUtils;
import net.ontopia.topicmaps.utils.MergeUtils;
import net.ontopia.topicmaps.utils.NullResolvingExternalReferenceHandler;
import net.ontopia.topicmaps.utils.TopicMapSynchronizer;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;
import net.ontopia.utils.CollectionUtils;
import net.ontopia.utils.CompactHashSet;
import net.ontopia.utils.OntopiaRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.InputSource;

/**
 * EXPERIMENTAL: An implementation that looks up topics on remote
 * servers using the TM RAP protocol.
 */
public class RemoteTopicIndex implements TopicIndexIF {

  public static final String VIRTUAL_URN = "urn:x-oks-virtual:";
  
  protected String editBaseuri;
  protected String viewBaseuri;
  protected TopicMapStoreFactoryIF storefactory;
  protected String tmid;

  public RemoteTopicIndex(String editBaseuri, String viewBaseuri) {
    this(editBaseuri, viewBaseuri,
         new net.ontopia.topicmaps.impl.basic.InMemoryStoreFactory());
  }

  public RemoteTopicIndex(String editBaseuri, String viewBaseuri,
                          TopicMapStoreFactoryIF factory) {
    this(editBaseuri, viewBaseuri, factory, null);
  }

  public RemoteTopicIndex(String editBaseuri, String viewBaseuri, 
                          TopicMapStoreFactoryIF factory, String tmid) {
    this.editBaseuri = editBaseuri;
    this.viewBaseuri = viewBaseuri;
    this.storefactory = factory;
    this.tmid = tmid;
  }
  
  @Override
  public Collection<TopicIF> getTopics(Collection<LocatorIF> indicators,
                              Collection<LocatorIF> sources,
                              Collection<LocatorIF> subjects) {

    if (indicators.isEmpty() && sources.isEmpty() && subjects.isEmpty())
      return Collections.emptySet();

    // lookup or create target topic
    TopicMapIF targetTopicMap = storefactory.createStore().getTopicMap();    
    TopicIF targetTopic = createTopic(targetTopicMap, indicators, sources, subjects);
    
    // make sure topic knows it's being loaded
    setLoaded(targetTopic);
    
    TopicMapIF sourceTopicMap = new InMemoryTopicMapStore().getTopicMap();
    sourceTopicMap.getStore().setBaseAddress(targetTopicMap.getStore().getBaseAddress());
    
    // send get-topic request
    try {
      String params = encodeIdentityParameters(indicators, sources, subjects);
      if (tmid != null)
        params = "topicmap=" + tmid + "&" + params;
      loadXTM("get-topic", params, false, sourceTopicMap);
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    }
    
    TopicIF sourceTopic = findTopic(sourceTopicMap, indicators, sources, subjects);
    TopicMapSynchronizer.update(targetTopicMap, sourceTopic);

    targetTopic = findTopic(targetTopicMap, indicators, sources, subjects);
    return (targetTopic == null ? null : Collections.singleton(targetTopic));
  }

  private TopicIF createTopic(TopicMapIF tm,
                              Collection<LocatorIF> indicators,
                              Collection<LocatorIF> sources,
                              Collection<LocatorIF> subjects) {

    // merge all topics that have any of the identities
    TopicIF topic = null;
    Collection<TopicIF> existing = findTopics(tm, indicators, sources, subjects);
    if (!existing.isEmpty()) {
      Iterator<TopicIF> iter = existing.iterator();
      topic = iter.next();
      if (iter.hasNext()) {
        MergeUtils.mergeInto(topic, iter.next());
      }
    }
    // if no topics was found then create a new one
    if (topic == null)
      topic = tm.getBuilder().makeTopic();

    // make sure topic has all the identities
    Iterator<LocatorIF> it = indicators.iterator();
    while (it.hasNext()) {
      LocatorIF loc = it.next();
      topic.addSubjectIdentifier(loc);
    }
    it = sources.iterator();
    while (it.hasNext()) {
      LocatorIF loc = it.next();
      topic.addItemIdentifier(loc);
    }
    it = subjects.iterator();
    while (it.hasNext()) {
      LocatorIF subject = it.next();
      topic.addSubjectLocator(subject);
    }

    return topic;
  }
  
  private TopicIF findTopic(TopicMapIF tm,
                            Collection<LocatorIF> indicators,
                            Collection<LocatorIF> sources,
                            Collection<LocatorIF> subjects) {
    Iterator<LocatorIF> it = indicators.iterator();
    while (it.hasNext()) {
      LocatorIF loc = it.next();
      TopicIF topic = tm.getTopicBySubjectIdentifier(loc);
      if (topic != null) return topic;
    }
    it = sources.iterator();
    while (it.hasNext()) {
      LocatorIF loc = it.next();
      TopicIF topic = (TopicIF) tm.getObjectByItemIdentifier(loc);
      if (topic != null) 
				return topic;
			else {
  			// Resolve object by object id
  			String address = loc.getAddress();
  			if (RemoteTopicIndex.isVirtualReference(address)) {
					if (tmid.equals(RemoteTopicIndex
													.sourceTopicMapFromVirtualReference(address))) {
						topic = (TopicIF)tm.getObjectById(RemoteTopicIndex
																										.resolveVirtualReference(address, tmid));
						if (topic != null) return topic;			
					}
  			}
			}
    }
    it = subjects.iterator();
    while (it.hasNext()) {
      LocatorIF subject = it.next();
      TopicIF topic = tm.getTopicBySubjectLocator(subject);
      if (topic != null) return topic;
    }
    return null;
  }
  
  private Collection<TopicIF> findTopics(TopicMapIF tm,
                               Collection<LocatorIF> indicators,
                               Collection<LocatorIF> sources,
                               Collection<LocatorIF> subjects) {
    // find all topics that have any of the identities
    Collection<TopicIF> result = new HashSet<TopicIF>();    
    Iterator<LocatorIF> it = indicators.iterator();
    while (it.hasNext()) {
      LocatorIF loc = it.next();
      TopicIF topic = tm.getTopicBySubjectIdentifier(loc);
      if (topic != null) result.add(topic);
    }
    it = sources.iterator();
    while (it.hasNext()) {
      LocatorIF loc = it.next();
      TopicIF topic = (TopicIF) tm.getObjectByItemIdentifier(loc);
      if (topic != null) 
				result.add(topic);
			else {
			  // Resolve object by object id
			  String address = loc.getAddress();
			  if (RemoteTopicIndex.isVirtualReference(address)) {
		  		if (tmid.equals(RemoteTopicIndex
		  										.sourceTopicMapFromVirtualReference(address))) {
		  			topic = (TopicIF)tm.getObjectById(RemoteTopicIndex
		  																							.resolveVirtualReference(address, tmid));
						if (topic != null) result.add(topic);
		  		}
			  }
			}
    }
    it = subjects.iterator();
    while (it.hasNext()) {
      LocatorIF subject = it.next();
      TopicIF topic = tm.getTopicBySubjectLocator(subject);
      if (topic != null) result.add(topic);
    }
    return result;
  }

  private boolean isLoaded(TopicIF topic) { 
    if (topic instanceof net.ontopia.topicmaps.impl.remote.RemoteTopic)
      return ((net.ontopia.topicmaps.impl.remote.RemoteTopic) topic).isLoaded();
    return true;
  }
  
  private void setLoaded(TopicIF topic) { 
    if (topic instanceof net.ontopia.topicmaps.impl.remote.RemoteTopic) {
      boolean prev = ((net.ontopia.topicmaps.impl.remote.RemoteTopic) topic).isLoaded();
      ((net.ontopia.topicmaps.impl.remote.RemoteTopic) topic).setLoaded(true);
    }
  }

  private List<String> getIdPredicates(TopicIF topic, String varname) {
    List<String> idpredicates = new ArrayList<String>();
    Iterator<LocatorIF> ids = topic.getItemIdentifiers().iterator();
    while (ids.hasNext()) {
      LocatorIF loc = ids.next();
			String address = loc.getAddress();
			if (RemoteTopicIndex.isVirtualReference(address)) {
				String topicId = RemoteTopicIndex.resolveVirtualReference(address, tmid);
				if (topicId != null)
					idpredicates.add("$" + varname + " = @" + topicId);
				else
					System.err.println("Cannot resolve virtual reference: " + topicId + " (" + address + ")");
			} else {
				idpredicates.add("item-identifier($" + varname + ", \"" + loc.getAddress() + "\")");
			}
    }
    ids = topic.getSubjectIdentifiers().iterator();
    while (ids.hasNext()) {
      LocatorIF loc = ids.next();
      idpredicates.add("subject-identifier($" + varname + ", \"" + loc.getAddress() + "\")");
    }
    ids = topic.getSubjectLocators().iterator();
    while (ids.hasNext()) {
      LocatorIF loc = ids.next();
      idpredicates.add("subject-locator($" + varname + ", \"" + loc.getAddress() + "\")");
    }
    return idpredicates;
  }
  
  @Override
  public Collection<TopicIF> loadRelatedTopics(Collection<LocatorIF> indicators,
                                      Collection<LocatorIF> sources,
                                      Collection<LocatorIF> subjects,
                                      boolean two_steps) {
    long start = System.currentTimeMillis();
    if (indicators.isEmpty() && sources.isEmpty() && subjects.isEmpty())
      return Collections.emptySet();
    
    try {
      // lookup or create target topic
      TopicMapIF targetTopicMap = storefactory.createStore().getTopicMap();    
      TopicIF targetTopic = createTopic(targetTopicMap, indicators, sources, subjects);
      int count = 0;
      
      // build identity predicates
      List<String> idpredicates_T = getIdPredicates(targetTopic, "T");
      if (idpredicates_T.isEmpty())
        return Collections.emptySet();
      
      StringBuilder query = new StringBuilder();
      query.append("related-to($T1, $T2) :- " +
                   "  role-player($R1, $T1), " +
                   "  association-role($A, $R1), " +
                   "  association-role($A, $R2), " +
                   "  $R1 /= $R2, " +
                   "  role-player($R2, $T2), " +
                   "  $T1 /= $T2 . ");

      query.append("select $O from ");
      if (idpredicates_T.size() > 1)
        query.append("  {");
      query.append(StringUtils.join(idpredicates_T, " | "));
      if (idpredicates_T.size() > 1)
        query.append("  }");
      
      query.append(", { ");
      query.append("  $O = $T | ");
      query.append("  related-to($T, $O) ");
      if (two_steps)
        query.append("| related-to($T, $TMP), related-to($TMP, $O) ");
      query.append("}?");

      TopicMapIF sourceTopicMap = new InMemoryTopicMapStore().getTopicMap();
      sourceTopicMap.getStore().setBaseAddress(targetTopicMap.getStore().getBaseAddress());

      String params = "syntax=application/x-xtm&tolog=" + URLEncoder.encode(query.toString());
      if (tmid != null)
        params = "topicmap=" + tmid + "&" + params;
      params += "&compress=true";

      loadXTM("get-tolog", params, true, sourceTopicMap);
      
      // make sure topic knows it's being loaded
      TopicIF sourceTopic = findTopic(sourceTopicMap, indicators, sources, subjects);
      if (!isLoaded(targetTopic)) {
        setLoaded(targetTopic);
				TopicMapSynchronizer.update(targetTopicMap, sourceTopic);
        count++;
      }

      // get loaded topics (possibly 2 steps out)
      Collection<TopicIF> loaded = new CompactHashSet<TopicIF>();
      Iterator<TopicIF> riter = CharacteristicUtils.getAssociatedTopics(sourceTopic).iterator();
      while (riter.hasNext()) {
        TopicIF topic = riter.next();
        loaded.add(topic);
        if (two_steps)
          loaded.addAll(CharacteristicUtils.getAssociatedTopics(topic));
      }
      
      // move topics over, and mark as loaded
      transferLoadedTopics(loaded, targetTopicMap);
      
      return Collections.singleton(targetTopic);
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  @Override
  public Collection<TopicPage> getTopicPages(Collection<LocatorIF> indicators,
                                  Collection<LocatorIF> sources,
                                  Collection<LocatorIF> subjects) {
    Collection<TopicPage> pages = new ArrayList<TopicPage>();
    
    try {
      String params = encodeIdentityParameters(indicators, sources, subjects);
      if (tmid != null)
        params = "topicmap=" + tmid + "&" + params;
      InputSource src = getInputSource("get-topic-page", params, false);
      String baseuri = viewBaseuri == null ? editBaseuri : viewBaseuri;
      LocatorIF base = new URILocator(baseuri + "get-topic-page");
      XTMTopicMapReader reader = new XTMTopicMapReader(src, base);
      reader.setValidation(false); // means we don't need Jing
      TopicMapIF tm = reader.read();
      ClassInstanceIndexIF ix = (ClassInstanceIndexIF)
        tm.getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");

      LocatorIF vpsi = new URILocator("http://psi.ontopia.net/tmrap/view-page");
      TopicIF vptype = tm.getTopicBySubjectIdentifier(vpsi);
      if (vptype == null)
        return pages;
      
      Iterator<TopicIF> it = ix.getTopics(vptype).iterator();
      while (it.hasNext()) {
        TopicIF vp = it.next();
        LocatorIF subject = CollectionUtils.getFirst(vp.getSubjectLocators());
        pages.add(new TopicPage(null, subject.getAddress(),
                                null, null, null));
      }
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    }

    return pages;
  }

  @Override
  public TopicPages getTopicPages2(Collection<LocatorIF> indicators,
                                   Collection<LocatorIF> sources,
                                   Collection<LocatorIF> subjects) {
    throw new UnsupportedOperationException("This method is not supported");
  }

  public void loadAssociationTypes(TopicMapIF topicmap) throws IOException {
    loadQuery(topicmap,
      "select $T from " +
      "association($A), " +
      "type($A, $T)?");
  }

  public void loadTopicTypes(TopicMapIF topicmap) throws IOException {
    loadQuery(topicmap,
      "select $T from " +
      "direct-instance-of($I, $T)?");
  }

  public void loadQuery(TopicMapIF topicmap, String query) throws IOException {
    // assemble get-tolog request
    query = URLEncoder.encode(query);

    TopicMapIF tmptm = new InMemoryTopicMapStore().getTopicMap();
    tmptm.getStore().setBaseAddress(topicmap.getStore().getBaseAddress());
    
    String params = "syntax=application/x-xtm&tolog=" + query;
    if (tmid != null)
      params = "topicmap=" + tmid + "&" + params;
    params += "&compress=true";
      
    // send request
    loadXTM("get-tolog", params, true, tmptm);
    
    // collect newly loaded topics
    Collection<TopicIF> loaded = new ArrayList<TopicIF>();
    Iterator<TopicIF> it = tmptm.getTopics().iterator();
    while (it.hasNext()) {
      TopicIF topic = it.next();
      if (!(topic.getTypes().isEmpty() &&
            topic.getTopicNames().isEmpty() &&
            topic.getOccurrences().isEmpty() &&
            topic.getRoles().isEmpty()))
        loaded.add(topic);
    }
    
    // mark as loaded and transfer
    transferLoadedTopics(loaded, topicmap);
  }

  @Override
  public void close() {
    // nothing we need to let go of
  }

  // Special configuration methods

  public void setStoreFactory(TopicMapStoreFactoryIF storefactory) {
    this.storefactory = storefactory;
  }

  // Internal methods
  
  protected InputSource getInputSource(String method, String params,
                                       boolean compress)
    throws IOException {
    
    String baseuri = viewBaseuri == null ? editBaseuri : viewBaseuri;
    String url = baseuri + method + "?" + params;
    URLConnection conn = this.getConnection(url, 0);
    InputSource src = new InputSource(url);

    if (!compress) {
      src.setByteStream(conn.getInputStream());
    
      String ctype = conn.getContentType();
      if (ctype != null && ctype.startsWith("text/xml")) {
        int pos = ctype.indexOf("charset=");
        if (pos != -1) src.setEncoding(ctype.substring(pos + 8));
      }
    } else
      src.setByteStream(new java.util.zip.GZIPInputStream(conn.getInputStream()));
    
    return src;
  }
  
  private URLConnection getConnection(String url, int count) {

    URLConnection conn = null;
    try {
      conn = new URL(url).openConnection();
    } catch (IOException e) {
      if (count < 5) {
        System.out.println();
        System.out.print("getConnection failed - ");
        System.out.println(count);
        System.out.println("Trying again ...");
        conn = this.getConnection(url, count++);
      } else {
        System.out.println("Giving up");
        throw new OntopiaRuntimeException(e);
      }
    }
    return conn;
  }

  private String encodeIdentityParameters(Collection<LocatorIF> indicators,
                                          Collection<LocatorIF> sources,
                                          Collection<LocatorIF> subjects) {

    boolean notfirst = false;
    StringBuilder buf = new StringBuilder();

    Iterator<LocatorIF> it = indicators.iterator();
    while (it.hasNext()) {
      LocatorIF locator = it.next();
      if (notfirst) buf.append("&");
      buf.append("identifier=" + URLEncoder.encode(locator.getExternalForm()));
      notfirst = true;
    }

    it = sources.iterator();
    while (it.hasNext()) {
      LocatorIF locator = it.next();
      if (notfirst) buf.append("&");
      buf.append("item=" + URLEncoder.encode(locator.getExternalForm()));
      notfirst = true;
    }

    it = subjects.iterator();
    while (it.hasNext()) {
      LocatorIF locator = it.next();
      if (notfirst) buf.append("&");
      buf.append("subject=" + URLEncoder.encode(locator.getExternalForm()));
    }
    return buf.toString();
  }

  private void loadXTM(String request, String params, boolean compress,
                       TopicMapIF topicmap)
    throws IOException {
    InputSource src = getInputSource(request, params, compress);
    String baseuri = viewBaseuri == null ? editBaseuri : viewBaseuri;
    LocatorIF base = new URILocator(baseuri + request);
    XTMTopicMapReader reader = new XTMTopicMapReader(src, base);
    reader.setExternalReferenceHandler(new NullResolvingExternalReferenceHandler());
    reader.setValidation(false); // means we don't need Jing
    reader.importInto(topicmap);
  }

  private void transferLoadedTopics(Collection<TopicIF> loaded,
                                    TopicMapIF targetTopicMap) {
    Iterator<TopicIF> it = loaded.iterator();
    while (it.hasNext()) {
      TopicIF sourceRelated = it.next();
      TopicIF targetRelated = createTopic(targetTopicMap, 
                                          sourceRelated.getSubjectIdentifiers(), 
                                          sourceRelated.getItemIdentifiers(),
                                          sourceRelated.getSubjectLocators());
      if (!isLoaded(targetRelated)) {
        setLoaded(targetRelated);
        TopicMapSynchronizer.update(targetTopicMap, sourceRelated);
      }
    }
  }

  //-------------------------------------------------------------------
  // Virtual locators
  //--------------------------------------------------------------------
  
  public static boolean isVirtualReference(String address) {
    return address.startsWith(VIRTUAL_URN);
  }
  
  public static String resolveVirtualReference(String address, String tmid) {
    String topicMapIndex = RemoteTopicIndex.sourceTopicMapFromVirtualReference(address);
    if (!topicMapIndex.equals(tmid))
      throw new OntopiaRuntimeException("Topic map IDs do not match, requested=" + topicMapIndex +
                                        ", current=" + tmid);
    
    return address.substring(address.indexOf('#') + 1);
  }

  public static String sourceTopicMapFromVirtualReference(String address) {
    int index = address.indexOf('#');
    return address.substring(RemoteTopicIndex.VIRTUAL_URN.length(), index);
  }

}
