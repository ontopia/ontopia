/*
 * #!
 * Ontopia Engine
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
package net.ontopia.topicmaps.core.events;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Objects;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.AbstractTopicMapTest;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.Test;

public abstract class AssociationEventsTest extends AbstractTopicMapTest {

  protected TopicMapReferenceIF topicmapRef;
  protected TopicMapIF topicmap;       // topic map of object being tested
  protected TopicMapBuilderIF builder; // builder used for creating new objects
  protected EventListener listener;
  

  @Override
  public void setUp() throws Exception {
    // get a new topic map object from the factory.
    factory = getFactory();
    try {
      topicmapRef = factory.makeTopicMapReference();
      listener = new EventListener();
      TopicMapEvents.addTopicListener(topicmapRef, listener);
      // load topic map
      topicmap = topicmapRef.createStore(false).getTopicMap();
      ImportExportUtils.getReader(TestFileUtils.getTestInputFile("various", "alumni.xtm")).importInto(topicmap);
      topicmap.getStore().commit();
      
      // get the builder of that topic map.
      builder = topicmap.getBuilder();
      
      // clean up the listener
      listener.reset();
      
    } catch (java.io.IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  @Override
  public void tearDown() {
    // Inform the factory that the topic map is not needed anymore.
    topicmap.getStore().close();
    TopicMapEvents.removeTopicListener(topicmapRef, listener);
    factory.releaseTopicMapReference(topicmapRef);
    // Reset the member variables.
    topicmap = null;
    builder = null;
  }

  protected TopicIF getTopicBySubjectIdentifier(LocatorIF si) {
    TopicIF result = topicmap.getTopicBySubjectIdentifier(si);
    if(result == null) {
      throw new RuntimeException("topic " + si.getAddress() + " not found in the test topic map");
    }
    return result;
  }
  
  // --- Test Cases
  
  @Test
  public void testRolePlayerEvent1() throws MalformedURLException {
    TopicIF johnDoe = getTopicBySubjectIdentifier(URILocator.create("http://psi.chludwig.de/playground#JohnDoe"));
    TopicIF graduatedFromAssocType = getTopicBySubjectIdentifier(URILocator.create("http://psi.chludwig.de/playground#graduatedFrom"));
    TopicIF alumnusRoleType = getTopicBySubjectIdentifier(URILocator.create("http://psi.chludwig.de/playground#Alumnus"));
  
    Assert.assertTrue("Unexpected events prior to any modifications", listener.traces.isEmpty());
    
    final String annotation1 = "create association";
    listener.traceAnnotation = annotation1;
    AssociationIF grad = builder.makeAssociation(graduatedFromAssocType);
    
    final String annotation2 = "make John Doe an alumnus";
    listener.traceAnnotation = annotation2;
    builder.makeAssociationRole(grad, alumnusRoleType, johnDoe);
    topicmap.getStore().commit();
    Assert.assertTrue("no event for " + johnDoe + " when it became a role player; event list: " + listener.traces, 
        listener.findTrace(null, annotation2, johnDoe) != null);
  }
  
  @Test
  public void testRolePlayerEvent2() throws MalformedURLException {
    TopicIF johnDoe = getTopicBySubjectIdentifier(URILocator.create("http://psi.chludwig.de/playground#JohnDoe"));
    TopicIF uOfSwhere = getTopicBySubjectIdentifier(URILocator.create("http://psi.chludwig.de/playground#UniversityOfSomewhere"));
    TopicIF graduatedFromAssocType = getTopicBySubjectIdentifier(URILocator.create("http://psi.chludwig.de/playground#graduatedFrom"));
    TopicIF alumnusRoleType = getTopicBySubjectIdentifier(URILocator.create("http://psi.chludwig.de/playground#Alumnus"));
    TopicIF almaMaterRoleType = getTopicBySubjectIdentifier(URILocator.create("http://psi.chludwig.de/playground#AlmaMater"));
  
    Assert.assertTrue("Unexpected events prior to any modifications", listener.traces.isEmpty());
    
    final String annotation1 = "create association";
    listener.traceAnnotation = annotation1;
    AssociationIF grad = builder.makeAssociation(graduatedFromAssocType);
    
    final String annotation2 = "make John Doe an alumnus";
    listener.traceAnnotation = annotation2;
    builder.makeAssociationRole(grad, alumnusRoleType, johnDoe);
    topicmap.getStore().commit();
    
    final String annotation3 = "make University of Somewhere an alma mater";
    listener.traceAnnotation = annotation3;
    builder.makeAssociationRole(grad, almaMaterRoleType, uOfSwhere);
    topicmap.getStore().commit();
    
    // Note: the first test ignores the annotation, so it only checks that johnDoe was modified at some point
    Assert.assertTrue("no event for " + johnDoe + " when it became a role player; event list: " + listener.traces, 
        listener.findTrace(null, null, johnDoe) != null);
    Assert.assertTrue("no event for " + uOfSwhere + " when it became a role player; event list: " + listener.traces, 
        listener.findTrace(null, annotation3, uOfSwhere) != null);
  }
  
  
  // --- Test Infrastructure
  
  public static class EventTrace {
    final public String event;
    final public String annotation;
    final public TMObjectIF tmObject;
    
    public EventTrace(String event, String annotation, TMObjectIF tmObject) {
      super();
      
      Objects.requireNonNull(event, "event must not be null");
      this.event = event;

      Objects.requireNonNull(annotation, "annotation must not be null");
      this.annotation = annotation;

      Objects.requireNonNull(tmObject, "tmObject must not be null");
      this.tmObject = tmObject;
    }
        
    @Override
    public String toString() {
      return "EventTrace(\"" + event + "\", \"" + annotation + "\", " + tmObject + ")";
    }
  }
  
  public static class EventListener extends AbstractTopicMapListener {
    private ArrayList<EventTrace> traces = new ArrayList<EventTrace>();
    private String traceAnnotation = "";

    final public static String ADD = "add";
    final public static String MODIFIED = "modified";
    final public static String REMOVED = "removed";
    
    public void reset() {
      traces.clear();
    }
    
    /**
     * Find the first trace entry that matches the arguments.  
     * @param event The requested event string or null if the event string does not matter.
     * @param annotation The requested annotation string or null if the annotation string does not matter.
     * @param tmObject The requested Topic Maps object or null if the object does not matter. 
     *                 The objects are compared by their object ids. 
     * @return The first trace entry that matches the given criteria or null if no such entry is found.
     */
    public EventTrace findTrace(String event, String annotation, TMObjectIF tmObject) {
      EventTrace result = null;
      for(EventTrace trace : traces) {
        if((event == null || trace.event.equals(event)) &&
            (annotation == null || trace.annotation.equals(annotation)) &&
            (tmObject == null || trace.tmObject.getObjectId().equals(tmObject.getObjectId()))) {
          result = trace;
          break;
        }
      }
      return result;
    }
    
    @Override
    public void objectAdded(TMObjectIF snapshot) {
      EventTrace trace = new EventTrace(ADD, traceAnnotation, snapshot);
      // System.out.println(trace);
      traces.add(trace);
    }

    @Override
    public void objectModified(TMObjectIF snapshot) {
      EventTrace trace = new EventTrace(MODIFIED, traceAnnotation, snapshot);
      // System.out.println(trace);
      traces.add(trace);
    }

    @Override
    public void objectRemoved(TMObjectIF snapshot) {
      EventTrace trace = new EventTrace(REMOVED, traceAnnotation, snapshot);
      // System.out.println(trace);
      traces.add(trace);
    }

  }

}
