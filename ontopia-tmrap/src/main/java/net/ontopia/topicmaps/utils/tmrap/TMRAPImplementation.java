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
import java.io.StringReader;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.nav2.core.NavigatorApplicationIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import net.ontopia.topicmaps.utils.TopicStringifiers;
import net.ontopia.topicmaps.utils.TopicMapSynchronizer;
import net.ontopia.topicmaps.utils.ltm.LTMTopicMapReader;
import net.ontopia.topicmaps.utils.ctm.CTMTopicMapReader;
import net.ontopia.topicmaps.xml.TMXMLWriter;
import net.ontopia.topicmaps.xml.XTMFragmentExporter;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;
import net.ontopia.utils.OntopiaRuntimeException;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * PRIVATE: A generic implementation of the TMRAP protocol,
 * independent of any particular way of invoking the services. It is
 * used by both the plain HTTP and SOAP interfaces to TMRAP to provide
 * their TMRAP functionality.
 *
 * @since 3.1
 */
public class TMRAPImplementation {
  
  // Used to generate unique IDs
  private static long counter;

  // ===== GET-TOPIC =======================================================
  // -----------------------------------------------------------------------
  // | Parameter | Required? | Repeatable? | Type   | Value      | Default |
  // -----------------------------------------------------------------------
  // | item      |           | yes         | URI    | locator    |         |
  // | subject   |           | yes         | URI    | locator    |         |
  // | indicator |           | yes         | URI    | locator    |         |
  // | topicmap  | no        | yes         | String | tm-handles |         |
  // | syntax    | no        | no          | String | "XTM" (+)  | XTM     |
  // | view      | no        | no          | String | "stub" (+) | stub    |
  // -----------------------------------------------------------------------
  // (+) means other values may be allowed later.
  public static void getTopic(NavigatorApplicationIF navapp,
                              Collection<LocatorIF> items,
                              Collection<LocatorIF> subjects,
                              Collection<LocatorIF> indicators,
                              String[] tmids, 
                              String syntax,
                              String view,
                              ContentHandler handler) throws Exception {

    // verify parameters
    if (syntax == null) {
      syntax = RAPServlet.SYNTAX_XTM; // default
    }
    if (!(RAPServlet.SYNTAX_XTM.equals(syntax) || 
          RAPServlet.SYNTAX_TM_XML.equals(syntax))) {
      throw new TMRAPException("Invalid value for 'syntax' parameter: '" +
                               syntax + "'");
    }
    if (view == null) {
      view = "stub";
    }
    if (!"stub".equals(view) && !"names".equals(view)) {
      throw new TMRAPException("Invalid value for 'view' parameter: '" +
                               view + "'");
    }

    // get on with it
    TopicIndexIF index = getTopicIndex(navapp, true, tmids);
    try {
      Collection<TopicIF> topics = index.getTopics(indicators, items, subjects);
      if (RAPServlet.SYNTAX_XTM.equals(syntax)) {
        generateXTM(handler, topics, subjects, items, indicators, true);
      } else {     
        generateTMXML(handler, topics, subjects, items, indicators,
                "names".equals(view),
                      tmids != null && tmids.length == 1);
      }
    } finally {
      index.close();
    }
  }
  
  // ===== GET-TOLOG =======================================================
  // -----------------------------------------------------------------------
  // | Parameter | Required? | Repeatable? | Type   | Value      | Default |
  // -----------------------------------------------------------------------
  // | tolog     | yes       | no          | String | query      |         |
  // | topicmap  | yes       | no          | String | tm-handle  |         |
  // | syntax    | no        | no          | String | "XTM" (+)  | XTM     |
  // | view      | no        | no          | String | "stub" (+) | stub    |
  // -----------------------------------------------------------------------
  // (+) means other values may be allowed later.  
  public static void getTolog(NavigatorApplicationIF navapp,
                              String tolog,
                              String tmid,
                              String syntax,
                              String view,
                              ContentHandler handler) throws Exception {

    if (tmid == null) {
      throw new TMRAPException("No value given for required parameter " +
                               RAPServlet.TOPICMAP_PARAMETER_NAME);
    }
    if (tolog == null) {
      throw new TMRAPException("No value given for required parameter " +
                               RAPServlet.TOLOG_PARAMETER_NAME);
    }
    if (syntax == null) {
      syntax = RAPServlet.SYNTAX_TOLOG;
    }
    if (!syntax.equals(RAPServlet.SYNTAX_TOLOG) &&
        !syntax.equals(RAPServlet.SYNTAX_XTM) &&
        !syntax.equals(RAPServlet.SYNTAX_TM_XML)) {
      throw new TMRAPException("Unsupported syntax '" + syntax + "'");
    }
    
    TopicMapIF topicmap = navapp.getTopicMapById(tmid, true);
    try {
      QueryProcessorIF processor = QueryUtils.getQueryProcessor(topicmap);
      QueryResultIF result = processor.execute(tolog);
      if (syntax.equals(RAPServlet.SYNTAX_XTM) && result.getWidth() != 1) {
        throw new TMRAPException(
          "The tolog query must produce a query result of exactly one column," +
          "but produced " + result.getWidth() + " columns.");
      }

      if (syntax.equals(RAPServlet.SYNTAX_XTM)) {
        Collection<TopicIF> topics = getTopics(result);
        generateXTM(handler, topics, Collections.<LocatorIF>emptyList(),
                    Collections.<LocatorIF>emptyList(),
                    Collections.<LocatorIF>emptyList(), false);
      } else if (syntax.equals(RAPServlet.SYNTAX_TM_XML)) {
        Collection<TopicIF> topics = getTopics(result);
        generateTMXML(handler, topics, null, Collections.<LocatorIF>emptyList(),
                      Collections.<LocatorIF>emptyList(), false, false);
      } else {
        try {
          generateTolog(handler, result, view);
        } catch (SAXException e) {
          throw new TMRAPException("Unknown problem: " + e);
        }
      }
    } finally {
      // FIXME: need to close query result
      topicmap.getStore().close();
    }
  }

  // ===== DELETE-TOPIC =======================================================
  // -----------------------------------------------------------------------
  // | Parameter | Required? | Repeatable? | Type   | Value      | Default |
  // -----------------------------------------------------------------------
  // | item      | no        | yes         | URI    | locator    |         |
  // | subject   | no        | yes         | URI    | locator    |         |
  // | identifier| no        | yes         | URI    | locator    |         |
  // | topicmap  | no        | yes         | String | tm-handles |         |
  // -----------------------------------------------------------------------
  public static String deleteTopic(NavigatorApplicationIF navapp,
                                   Collection<LocatorIF> items,
                                   Collection<LocatorIF> subjects,
                                   Collection<LocatorIF> identifiers,
                                   String[] tmids) throws Exception {

    TopicIndexIF index = getTopicIndex(navapp, false, tmids);
    try {
      // The TopicIndexIF takes care of handling TM access

      // Delete the topic(s)
      Collection<TopicIF> topics = index.getTopics(identifiers, items, subjects);
      Iterator<TopicIF> it = topics.iterator();
      while (it.hasNext()) {
        it.next().remove();
      }

      // Return a message
      return "Deleted " + topics.size() + " topics";
    } finally {
      index.close();
    }
  }

  // ===== ADD-FRAGMENT ====================================================
  // -----------------------------------------------------------------------
  // | Parameter | Required? | Repeatable? | Type   | Value      | Default |
  // -----------------------------------------------------------------------
  // | syntax    | yes       | no          | String | "XTM"/     | XTM     |
  // |           |           |             |        |   "LTM"/   |         |
  // |           |           |             |        |   "CTM"(+) |         |
  // | fragment  | yes       | no          | String |            |         |
  // | topicmap  | yes       | no          | String | tm-handles |         |
  // -----------------------------------------------------------------------
  // (+) means other values may be allowed later.
  public static void addFragment(NavigatorApplicationIF navapp,
                                 String fragment,
                                 String syntax,
                                 String tmid)
    throws NavigatorRuntimeException, IOException, TMRAPException {

    TopicMapIF topicmap = navapp.getTopicMapById(tmid, false);
    try {
      LocatorIF base = topicmap.getStore().getBaseAddress();
      StringReader reader = new StringReader(fragment);
      if (syntax.equals(RAPServlet.SYNTAX_XTM)) {
        new XTMTopicMapReader(reader, base).importInto(topicmap);
      } else if (syntax.equals(RAPServlet.SYNTAX_LTM)) {
        new LTMTopicMapReader(reader, base).importInto(topicmap);
      } else if (syntax.equals(RAPServlet.SYNTAX_CTM)) {
        new CTMTopicMapReader(reader, base).importInto(topicmap);
      } else {
        throw new TMRAPException("Bad syntax value: '" + syntax + "'");
      }
      topicmap.getStore().commit();
    } finally {
      topicmap.getStore().close();
    }
  }

  // ===== UPDATE-TOPIC ====================================================
  // -----------------------------------------------------------------------
  // | Parameter | Required? | Repeatable? | Type   | Value      | Default |
  // -----------------------------------------------------------------------
  // | syntax    | yes       | no          | String | "XTM"/     | XTM     |
  // |           |           |             |        |   "LTM"(+) |         |
  // | fragment  | yes       | no          | String |            |         |
  // | topicmap  | yes       | no          | String | tm-handles |         |
  // -----------------------------------------------------------------------
  // (+) means other values may be allowed later.
  public static void updateTopic(NavigatorApplicationIF navapp,
                                 String fragment,
                                 String syntax,
                                 String tmid,
                                 Collection<LocatorIF> indicators,
                                 Collection<LocatorIF> items,
                                 Collection<LocatorIF> subjects)
    throws NavigatorRuntimeException, IOException, TMRAPException {

    TopicMapIF topicmap = navapp.getTopicMapById(tmid, false);
    try {
      LocatorIF base = topicmap.getStore().getBaseAddress();
      InMemoryTopicMapStore store = new InMemoryTopicMapStore();
      if (base != null) {
        store.setBaseAddress(base);
      }
      TopicMapIF ftm = store.getTopicMap();
      
      StringReader reader = new StringReader(fragment);
      if (syntax.equals(RAPServlet.SYNTAX_XTM)) {
        new XTMTopicMapReader(reader, base).importInto(ftm);
      } else if (syntax.equals(RAPServlet.SYNTAX_LTM)) {
        new LTMTopicMapReader(reader, base).importInto(ftm);
      } else {
        throw new TMRAPException("Bad syntax value: '" + syntax + "'");
      }

      // FIXME: THIS IS DIFFICULT IN PRACTICE!! NEED TO BE ABLE TO SPECIFY
      // RELATIVE ITEM IDENTIFIERS, METHINKS...
      
      TopicMapTopicIndex ix = new TopicMapTopicIndex(ftm, null, null, null);
      Collection<TopicIF> topics = ix.getTopics(indicators, items, subjects);
      if (topics.size() != 1) {
        throw new TMRAPException("Wrong number of topics identified in fragment");
      }
      TopicIF ft = topics.iterator().next();
      TopicMapSynchronizer.update(topicmap, ft);
      
      topicmap.getStore().commit();
    } finally {
      topicmap.getStore().close();
    }
  }
  
  // ===== GET-TOPIC-PAGE ===================================================
  // ------------------------------------------------------------------------
  // | Parameter  | Required? | Repeatable? | Type   | Value      | Default |
  // ------------------------------------------------------------------------
  // | source     |           | yes         | URI    | locator    |         |
  // | subject    |           | yes         | URI    | locator    |         |
  // | identifier |           | yes         | URI    | locator    |         |
  // | topicmap   | no        | yes         | String | tm-handles |         |
  // | syntax     | no        | no          | String | "XTM" (+)  | XTM     |
  // ------------------------------------------------------------------------
  // (+) means other values may be allowed later.
  public static TopicMapIF getTopicPage(NavigatorApplicationIF navapp,
                                        TMRAPConfiguration config,
                                        Collection<LocatorIF> items,
                                        Collection<LocatorIF> subjects,
                                        Collection<LocatorIF> indicators,
                                        String[] tmids)
    throws IOException, NavigatorRuntimeException {
    
    TopicIndexIF index = getTopicIndex(navapp, true, tmids,
                                       config.getViewURI(),
                                       config.getEditURI());
    try {
      // Create a new topic map.
      TopicMapStoreIF store = new InMemoryTopicMapStore();
      TopicMapIF topicmap = store.getTopicMap();
      
      // Get builder that adds new TAOs to the topic map.
      TopicMapBuilderIF builder = topicmap.getBuilder();
  
      // Create topic type rap:server.
      TopicIF rapServerType = makeTopic(topicmap, "server");
      
      // Create topic of type rap:server to represent the server.
      TopicIF rapServerTopic = builder.makeTopic(rapServerType);
      if (config.getServerName() != null) {
        builder.makeTopicName(rapServerTopic, config.getServerName());
      }
      TopicPages pages = index.getTopicPages2(indicators, items, subjects);
      
      // If there are any matching topics:
      if (!pages.getTopicMapHandles().isEmpty()) {
        // Create a topic to represent the matching topics.
        TopicIF matchTopic = builder.makeTopic();
        
        if (pages.getName() != null) {
          builder.makeTopicName(matchTopic, pages.getName());
        }
        
        Iterator<LocatorIF> it = pages.getItemIdentifiers().iterator();
        while (it.hasNext()) {
          matchTopic.addItemIdentifier(it.next());
        }
        
        it = pages.getSubjectIdentifiers().iterator();
        while (it.hasNext()) {
          matchTopic.addSubjectIdentifier(it.next());
        }
        
        it = pages.getSubjectLocators().iterator();
        if (it.hasNext()) {
          matchTopic.addSubjectLocator(it.next());
        }
        
        // Create topic type rap:topicmap.
        TopicIF rapTopicMapType = makeTopic(topicmap, "topicmap");
  
        // Create occurrence type rap:handle.
        TopicIF rapHandleType = makeTopic(topicmap, "handle");
  
        // Create association type rap:contained-in.
        TopicIF rapContainedInType = makeTopic(topicmap, "contained-in");
  
        // Create association role type rap:container.
        TopicIF rapContainerType = makeTopic(topicmap, "container");
  
        // Create association role type rap:container.
        TopicIF rapContaineeType = makeTopic(topicmap, "containee");
  
        // For each topic map that has at least one matching topic.
        Iterator<String> topicmapHandlesIt = pages.getTopicMapHandles().iterator();
        while (topicmapHandlesIt.hasNext()) {
          String currentHandle = topicmapHandlesIt.next();
          
          TopicIF currentRAPTopicMap = builder.makeTopic(rapTopicMapType);
          
          // Add topic map handle occurrence to identify topic.
          builder.makeOccurrence(currentRAPTopicMap, rapHandleType,
              currentHandle);
          
          String tmName = pages.getTMName(currentHandle);
          if (tmName != null) {
            builder.makeTopicName(currentRAPTopicMap, tmName);
          }
          
          // This topic map is contained in the server it came from.
          AssociationIF tmInServerAssoc = builder.makeAssociation(rapContainedInType);
          builder.makeAssociationRole(tmInServerAssoc, rapContaineeType, currentRAPTopicMap);
          builder.makeAssociationRole(tmInServerAssoc, rapContainerType, rapServerTopic);
  
          TopicIF rapEditPageType = null;
          if (config.getEditURI() != null) {
            // Create topic type rap:edit-page
            rapEditPageType = makeTopic(topicmap, "edit-page");
          }
  
          TopicIF rapViewPageType = null;
          if (config.getViewURI() != null) {
            // Create topic type rap:view-page
            rapViewPageType = makeTopic(topicmap, "view-page");
          }
          
          // For each page. Each page object represents both view and edit page
          // when there's both.
          Iterator<TopicPage> pagesIt = pages.getPages(currentHandle).iterator();
          while (pagesIt.hasNext()) {
            TopicPage currentPage = pagesIt.next();
            
            String editURL = currentPage.getEditURL();
            String viewURL = currentPage.getViewURL();
            
            if (editURL != null) {
              LocatorIF editSubject = new URILocator(currentPage.getEditURL());
              if (topicmap.getTopicBySubjectLocator(editSubject) == null) {
                // Create a topic to to represent the current edit-page.
                TopicIF currentEditPageTopic = builder.makeTopic(rapEditPageType);
                
                // Set the subject locator to be the link text
                currentEditPageTopic.addSubjectLocator(editSubject);
                
                AssociationIF association = builder.makeAssociation(rapContainedInType);
                builder.makeAssociationRole(association, rapContaineeType, currentEditPageTopic);
                builder.makeAssociationRole(association, rapContainerType, currentRAPTopicMap);
              }
            }
            
            if (viewURL != null) {
              LocatorIF viewSubject = new URILocator(currentPage.getViewURL());
              if (topicmap.getTopicBySubjectLocator(viewSubject) == null) {
                // Create a topic to represent the current view-page.
                TopicIF currentViewPageTopic = builder.makeTopic(rapViewPageType);
                
                // Set the subject locator to be the link text
                currentViewPageTopic.addSubjectLocator(viewSubject);
                
                AssociationIF association = builder.makeAssociation(rapContainedInType);
                builder.makeAssociationRole(association, rapContaineeType, currentViewPageTopic);
                builder.makeAssociationRole(association, rapContainerType, currentRAPTopicMap);
              }
            }
          }
        }
      }

      return topicmap;
    } catch (URISyntaxException u) {
      throw new IOException(u);
    } finally {
      index.close();
    }
  }

  // ===== TOLOG-UPDATE =====================================================
  // ------------------------------------------------------------------------
  // | Parameter  | Required? | Repeatable? | Type   | Value      | Default |
  // ------------------------------------------------------------------------
  // | topicmap   | yes       | no          | String | tm-handles |         |
  // | tolog      | yes       | no          | String | query      |         |
  // ------------------------------------------------------------------------
  public static int tologUpdate(NavigatorApplicationIF navapp,
                                String tmid,
                                String statement)
    throws IOException, NavigatorRuntimeException, InvalidQueryException {
    TopicMapIF topicmap = navapp.getTopicMapById(tmid, false);
    try {
      QueryProcessorIF processor = QueryUtils.getQueryProcessor(topicmap);
      int rows = processor.update(statement);
      topicmap.getStore().commit();
      return rows;
    } finally {
      topicmap.getStore().close();
    }
  }
  
  // --- Internal helpers

  private static TopicIF makeTopic(TopicMapIF tm, String id)
    throws URISyntaxException {
    TopicMapBuilderIF builder = tm.getBuilder();
    TopicIF topic = builder.makeTopic();
    topic.addSubjectIdentifier(new URILocator(RAPServlet.RAP_NAMESPACE + id));
    return topic;
  }
    
  private static TopicIndexIF getTopicIndex(NavigatorApplicationIF navapp, boolean readonly,
                                            String[] tmids)
    throws NavigatorRuntimeException {
    return getTopicIndex(navapp, readonly, tmids, null, null);
  }

  private static TopicIndexIF getTopicIndex(NavigatorApplicationIF navapp,
                                            boolean readonly,
                                            String[] tmids,
                                            String viewBaseuri,
                                            String editBaseuri)
    throws NavigatorRuntimeException {    
    if (tmids == null || tmids.length == 0) {
      return new RegistryTopicIndex(navapp.getTopicMapRepository(), readonly,
                                    editBaseuri, viewBaseuri);
    }

    List<TopicIndexIF> topicIndexes = new ArrayList<TopicIndexIF>();
    for (int i = 0; i < tmids.length; i++) {
      TopicMapIF topicmap = navapp.getTopicMapById(tmids[i], readonly);
      TopicIndexIF currentIndex =
        new TopicMapTopicIndex(topicmap, editBaseuri, viewBaseuri, tmids[i]);
      topicIndexes.add(currentIndex);
    }
    return new FederatedTopicIndex(topicIndexes);
  }

  private static void generateXTM(ContentHandler ser, Collection<TopicIF> topics,
                                  Collection<LocatorIF> subjectLocators,
                                  Collection<LocatorIF> srclocs,
                                  Collection<LocatorIF> indicators, boolean merge)
    throws SAXException {
        
    AttributesImpl atts = new AttributesImpl();
    atts.addAttribute("", "", "xmlns", "CDATA", "http://www.topicmaps.org/xtm/1.0/");
    atts.addAttribute("", "", "xmlns:xlink", "CDATA", "http://www.w3.org/1999/xlink");
    
    ser.startDocument();
        
    // Output element
    ser.startElement("", "", "topicMap", atts);

    // serialize as XTM Fragment 
    XTMFragmentExporter fragExporter = new XTMFragmentExporter();

    fragExporter.exportTopics(topics.iterator(), ser);
        
    // add binding topic if more than one result.
    if (merge && topics.size() > 1) {
      atts.clear();
      // id must be unique on this server for all time
      String suffix =
        new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()) +
        (counter++);
      atts.addAttribute("", "", "id", "CDATA", "unifying-topic-" + suffix);
      ser.startElement("", "", "topic", atts);

      atts.clear();
      ser.startElement("", "", "subjectIdentity", atts);

      Iterator<LocatorIF> sublociter = subjectLocators.iterator();
      while (sublociter.hasNext()) {
        LocatorIF uriloc = sublociter.next();            
        atts.clear();
        atts.addAttribute("", "", "xlink:href", "CDATA", uriloc.getExternalForm());
        ser.startElement("", "", "resourceRef", atts);  
        ser.endElement("", "", "resourceRef");
      }
                              
      Iterator<LocatorIF> srclociter = srclocs.iterator();
      while (srclociter.hasNext()) {
        LocatorIF uriloc = srclociter.next();            
        atts.clear();
        atts.addAttribute("", "", "xlink:href", "CDATA", uriloc.getExternalForm());         
        ser.startElement("", "", "topicRef", atts);   
        ser.endElement("", "", "topicRef");
      }
      
      Iterator<LocatorIF> indicatoriter = indicators.iterator();
      while (indicatoriter.hasNext()) {
        LocatorIF uriloc = indicatoriter.next();            
        atts.clear();
        atts.addAttribute("", "", "xlink:href", "CDATA", uriloc.getExternalForm());
        ser.startElement("", "", "subjectIndicatorRef", atts); 
        ser.endElement("", "", "subjectIndicatorRef");                  
      }
      ser.endElement("", "", "subjectIdentity");
      ser.endElement("", "", "topic");    
    }
        
    // output XTM close
    ser.endElement("", "", "topicMap");    
    ser.endDocument();
  }

  private static void generateTMXML(ContentHandler ser, Collection<TopicIF> topics,
                                    Collection<LocatorIF> subjectLocators,
                                    Collection<LocatorIF> srclocs,
                                    Collection<LocatorIF> indicators,
                                    boolean names,
                                    boolean singletm)
    throws SAXException {
    try {
      TMXMLWriter writer = new TMXMLWriter(ser);
      writer.gatherPrefixes(topics);
      writer.startTopicMap(null); // no topic map for now. this means that we don't get topic map reificaiton
      writer.writeTopics(topics);
      
      // FIXME: add binding topic here if multiple
      
      if (names) {
      // also output names of referenced topics
        InMemoryTopicMapStore store = new InMemoryTopicMapStore();
        TopicMapIF tm = store.getTopicMap();
        Iterator<TopicIF> it = topics.iterator();
        while (it.hasNext()) {
          TopicIF topic = it.next();
          copyReferencedTopics(tm, topic);
        }
        
        // yes, I know this isn't pretty; sorry. I'll work this out
        // when I have time. I need this for the TMRAP tutorial :-(
        if (singletm) {
          // ie: it's all from the same topic map, *therefore* we can
          // set the base locator of the new TM, and ensure that the ID
          // references from the fragment already created match the IDs
          // in the fragment we are about to create.
          TopicIF atopic = topics.iterator().next();
          LocatorIF base = atopic.getTopicMap().getStore().getBaseAddress();
          store.setBaseAddress(base);
        }
        
        writer.writeTopics(tm.getTopics());
      }
    
      writer.endTopicMap();
      writer.close();
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  private static void copyReferencedTopics(TopicMapIF tm, TopicIF source) {
    Iterator<AssociationRoleIF> it = source.getRoles().iterator();
    while (it.hasNext()) {
      AssociationRoleIF role = it.next();
      AssociationIF assoc = role.getAssociation();
      Iterator<AssociationRoleIF> it2 = assoc.getRoles().iterator();
      while (it2.hasNext()) {
        AssociationRoleIF other = it2.next();
        TopicIF refd = other.getPlayer();
        if (!refd.equals(source)) {
          copyTopic(tm, refd);
        }
      }
    }
  }

  private static void copyTopic(TopicMapIF tm, TopicIF referenced) {
    TopicMapBuilderIF builder = tm.getBuilder();
    TopicIF topic = builder.makeTopic();

    // copy identity
    Iterator<LocatorIF> it = referenced.getSubjectLocators().iterator();
    while (it.hasNext()) {
      topic.addSubjectLocator(it.next());
    }
    it = referenced.getSubjectIdentifiers().iterator();
    while (it.hasNext()) {
      topic.addSubjectIdentifier(it.next());
    }
    it = referenced.getItemIdentifiers().iterator();
    while (it.hasNext()) {
      topic.addItemIdentifier(it.next());
    }
    
    // copy name
    builder.makeTopicName(topic, TopicStringifiers.toString(referenced));
  }

  private static Collection<TopicIF> getTopics(QueryResultIF result)
    throws TMRAPException {
    Collection<TopicIF> topics = new ArrayList<TopicIF>();
    while (result.next()) {
      Object current = result.getValue(0);
      if (!(current instanceof TopicIF)) {
        throw new TMRAPException(
          "The query result produced by the tolog query must only contain " +
          "topics, but contained an object of type " + 
          current.getClass().getName() + ".");
      }
      
      topics.add((TopicIF) current);
    }
    return topics;
  }
  
  private static void generateTolog(ContentHandler handler,
                                    QueryResultIF result,
                                    String view)
    throws SAXException, TMRAPException {
    AttributesImpl atts = new AttributesImpl();
    handler.startDocument();

    atts.addAttribute("", "", "xmlns:x", "CDATA", "http://www.topicmaps.org/xtm/1.0/");
    atts.addAttribute("", "", "xmlns:l", "CDATA", "http://www.w3.org/1999/xlink");
    handler.startElement("", "", "result", atts);

    atts.clear();
    handler.startElement("", "", "head", atts);
    for (int ix = 0; ix < result.getWidth(); ix++) {
      handler.startElement("", "", "column", atts);
      String name = result.getColumnName(ix);
      char[] chars = name.toCharArray();
      handler.characters(chars, 0, chars.length);      
      handler.endElement("", "", "column");
    }
    handler.endElement("", "", "head");

    while (result.next()) {
      handler.startElement("", "", "row", atts);
      for (int ix = 0; ix < result.getWidth(); ix++) {
        handler.startElement("", "", "value", atts);
        Object value = result.getValue(ix);
        if (value == null) {
          // use empty element
        } else if (value instanceof TopicIF) {
          TopicIF topic = (TopicIF) value;
          if (view == null) {
            view = "stub";
          }
          if ("stub".equals(view)) {
            makeStub(topic, handler);
          } else if ("full-name".equals(view)) {
            makeFullName(topic, handler);
          }
        } else if (value instanceof String || value instanceof Number) {
          String svalue = value.toString();
          char[] chars = svalue.toCharArray();
          handler.characters(chars, 0, chars.length);
        } else {
          throw new TMRAPException("Unsupported value: " + value);
        }
        
        handler.endElement("", "", "value");
      }
      handler.endElement("", "", "row");
    }
    
    handler.endElement("", "", "result");
    handler.endDocument();
  }

  public static void makeStub(TopicIF topic, ContentHandler handler)
    throws TMRAPException, SAXException {
    AttributesImpl atts = new AttributesImpl(); // this is slow!!
    LocatorIF loc;
    String elemname;
    if (!topic.getSubjectLocators().isEmpty()) {
      loc = topic.getSubjectLocators().iterator().next();
      elemname = "x:resourceRef";
    } else if (!topic.getSubjectIdentifiers().isEmpty()) {
      loc = topic.getSubjectIdentifiers().iterator().next();
      elemname = "x:subjectIndicatorRef";
    } else if (!topic.getItemIdentifiers().isEmpty()) {
      loc = topic.getItemIdentifiers().iterator().next();
      elemname = "x:topicRef";
    } else {
      throw new TMRAPException("Not implemented yet"); // FIXME!!
    }

    atts.addAttribute("", "", "l:href", "CDATA", loc.getExternalForm());
    handler.startElement("", "", elemname, atts);
    handler.endElement("", "", elemname);
  }

  public static void makeFullName(TopicIF topic, ContentHandler handler)
    throws SAXException {
    generateTMXML(handler, Collections.singleton(topic),
                  null, Collections.<LocatorIF>emptyList(),
                  Collections.<LocatorIF>emptyList(), false, false);
  }
}
