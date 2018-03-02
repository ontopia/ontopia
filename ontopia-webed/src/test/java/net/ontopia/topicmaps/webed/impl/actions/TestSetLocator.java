/*
 * #!
 * Ontopia Webed
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

package net.ontopia.topicmaps.webed.impl.actions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;
import net.ontopia.topicmaps.webed.impl.actions.occurrence.SetLocator;

public class TestSetLocator extends AbstractWebedTestCase {
  protected ActionIF action; // see TestSetLocator2...
  
  public TestSetLocator(String name) {
    super(name);
  }

  @Override
  public void setUp() {
    super.setUp();
    action = new SetLocator();
  }
  
  /*
    1. Good, Normal use
    2. Good, Normal use, with scope
  */
  
  public void testNormalOperation() throws IOException {
    TopicIF topic = getTopicById(tm, "tromso");
    OccurrenceIF occ  = topic.getOccurrences().iterator().next();
    LocatorIF loc = occ.getLocator();
    Iterator<OccurrenceIF> occIT = topic.getOccurrences().iterator();
    while (loc == null && occIT.hasNext()){
      occ = occIT.next();
      loc = occ.getLocator();
    }
    
    //build parms
    ActionParametersIF params = makeParameters(occ, "http://www.sf.net");
    ActionResponseIF response = makeResponse();
    
    //execute    
    action.perform(params, response);
    
    //test              
    LocatorIF locNew = occ.getLocator();
    assertFalse("The locator is not correct",
                locNew.getAddress().equals(loc.getAddress()));
    
  }

  public void testNormalOperation2() throws IOException {
    
    TopicIF otherTopic = getTopicById(tm, "gamst");
    TopicIF otype = getTopicById(tm, "gamst");
    int numOcc = otherTopic.getOccurrences().size();
    
    // build params
    List plist = new ArrayList();
    plist.add(Collections.EMPTY_SET);
    plist.add(Collections.singleton(otherTopic));
    plist.add(Collections.singleton(otype));
    ActionParametersIF params = makeParameters(plist, "http://www.sf.net");
    ActionResponseIF response = makeResponse();
    
    // execute
    action.perform(params, response);
    
    //test              
    assertFalse("Occurrence not added", numOcc >= otherTopic.getOccurrences().size());
    Iterator<OccurrenceIF> i = otherTopic.getOccurrences().iterator();
    boolean hasit = false;
    while (i.hasNext()){
      OccurrenceIF foo = i.next();
      if (foo.getLocator().getAddress().equals("http://www.sf.net/"))
        hasit = true; 
    }
    assertFalse("Occurrence is not set for the topic", !(hasit));
  }

  public void testNormalOperation3() throws IOException {    
    TopicIF type = getTopicById(tm, "tromso");
    TopicIF otherTopic = getTopicById(tm, "gamst");
    int numOcc = otherTopic.getOccurrences().size();
    
    //build parms
    List plist = new ArrayList();
    plist.add(Collections.EMPTY_SET);
    plist.add(Collections.singleton(otherTopic));
    plist.add(Collections.singleton(type));
    ActionParametersIF params = makeParameters(plist, "http://www.sf.net");
    ActionResponseIF response = makeResponse();
    
    //execute    
    action.perform(params, response);
    
    //test              
    assertFalse("Occurrence not added", numOcc >= otherTopic.getOccurrences().size());
    Iterator<OccurrenceIF> i = otherTopic.getOccurrences().iterator();
    boolean hasit = false;
    while (i.hasNext()){
      OccurrenceIF foo = i.next();
      if ((foo.getLocator().getAddress().equals("http://www.sf.net/")) && 
          (foo.getType() == type))
        hasit = true; 
    }
    assertFalse("Occurrence is not set for the topic or type not correct", !(hasit));
  }

  public void testNoParam() throws IOException {
    
    //build parms
    ActionParametersIF params = makeParameters(Collections.EMPTY_LIST, "http://www.sf.net");
    ActionResponseIF response = makeResponse();
    try{
      //execute    
      action.perform(params, response);
      //test      
      fail("Collection is empty");
    }catch (ActionRuntimeException e){
    }
  }

  public void testBadParam() throws IOException {
    
    //build parms
    ActionParametersIF params = makeParameters("", "http://www.sf.net");
    ActionResponseIF response = makeResponse();
    try{
      //execute    
      action.perform(params, response);
      //test      
      fail("Bad OccurrenceType param (String)");
    }catch (ActionRuntimeException e){
    }
  }  
  
  public void testBadParam2() throws IOException {
    TopicIF topic = getTopicById(tm, "tromso");
    OccurrenceIF occ  = topic.getOccurrences().iterator().next();
    LocatorIF loc = occ.getLocator();
    Iterator<OccurrenceIF> occIT = topic.getOccurrences().iterator();
    while (loc == null && occIT.hasNext()){
      occ = occIT.next();
      loc = occ.getLocator();
    }
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(occ, ""), "http://www.sf.net");
    ActionResponseIF response = makeResponse();
    try{
      //execute    
      action.perform(params, response);
      //test      
      fail("Bad Topic param (String)");
    }catch (ActionRuntimeException e){
    }
  }
  
  public void testBadParam3() throws IOException {
    TopicIF topic = getTopicById(tm, "tromso");
    OccurrenceIF occ  = topic.getOccurrences().iterator().next();
    LocatorIF loc = occ.getLocator();
    Iterator<OccurrenceIF> occIT = topic.getOccurrences().iterator();
    while (loc == null && occIT.hasNext()){
      occ = occIT.next();
      loc = occ.getLocator();
    }
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(occ, topic, ""), "http://www.sf.net");
    ActionResponseIF response = makeResponse();
    try{
      //execute    
      action.perform(params, response);
      //test      
      fail("Bad Type param (String)");
    }catch (ActionRuntimeException e){
    }
  }  
  
  public void testMalformedURL() throws IOException {
    
    TopicIF topic = getTopicById(tm, "tromso");
    OccurrenceIF occ  = topic.getOccurrences().iterator().next();
    LocatorIF loc = occ.getLocator();
    Iterator<OccurrenceIF> occIT = topic.getOccurrences().iterator();
    while (loc == null && occIT.hasNext()){
      occ = occIT.next();
      loc = occ.getLocator();
    }
    
    //build parms
    ActionParametersIF params = makeParameters(occ, "snus");
    ActionResponseIF response = makeResponse();
    try{
      //execute    
      action.perform(params, response);
      fail("Malformed url for locator");
    }catch (ActionRuntimeException e){
    }  
  }
  
  public void testEmptyURL() throws IOException {
    TopicIF topic = getTopicById(tm, "tromso");
    OccurrenceIF occ = getOccurrenceWithLocator(topic);
    int occsbefore = topic.getOccurrences().size();
    
    ActionParametersIF params = makeParameters(occ, "");
    ActionResponseIF response = makeResponse();
    try {
      action.perform(params, response);
      fail("Malformed url for locator");
    } catch (ActionRuntimeException e) {
    }

    int occsnow = topic.getOccurrences().size();
    assertTrue("Occurrence deleted from parent topic",
               occsbefore == occsnow);
  }

  public void testEmptyURLNoOccurrence() throws IOException {
    TopicIF topic = getTopicById(tm, "tromso");
    TopicIF otype = getTopicById(tm, "tromso");
    int occsbefore = topic.getOccurrences().size();

    List plist = new ArrayList();
    plist.add(Collections.EMPTY_SET);
    plist.add(Collections.singleton(topic));
    plist.add(Collections.singleton(otype));
    ActionParametersIF params = makeParameters(plist, "");
    ActionResponseIF response = makeResponse();
    action.perform(params, response);

    int occsnow = topic.getOccurrences().size();
    assertTrue("Occurrence was created or deleted",
               occsbefore == occsnow);
  }
  
  public void testSetBothTopics() throws IOException {
    
    TopicIF topic = getTopicById(tm, "tromso");
    TopicIF otherTopic = getTopicById(tm, "gamst");
    TopicIF otype = getTopicById(tm, "gamst");
    int numOcc = otherTopic.getOccurrences().size();

    OccurrenceIF occ  = topic.getOccurrences().iterator().next();
    LocatorIF loc = occ.getLocator();
    Iterator<OccurrenceIF> occIT = topic.getOccurrences().iterator();
    while (loc == null && occIT.hasNext()){
      occ = occIT.next();
      loc = occ.getLocator();
    }
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(occ, topic, otype), "http://www.sf.net");
    ActionResponseIF response = makeResponse();
    
    //execute    
    action.perform(params, response);
    
    //test              
    LocatorIF locNew = occ.getLocator();
    assertFalse("The locator is not correct for topic which owns occurrence", 
                locNew.getAddress().equals(loc.getAddress()));
    
    assertFalse("Occurrence added to other topic", 
                numOcc < otherTopic.getOccurrences().size());
    Iterator<OccurrenceIF> i = otherTopic.getOccurrences().iterator();
    boolean hasit = false;
    while (i.hasNext()){
      OccurrenceIF foo = i.next();
      if (foo.getLocator().getAddress().equals("http://www.sf.net/"))
        hasit = true; 
    }
    assertFalse("Occurrence is set for the other topic", hasit);
  }

  // --- Helpers

  protected OccurrenceIF getOccurrenceWithLocator(TopicIF topic) {
    Iterator<OccurrenceIF> it = topic.getOccurrences().iterator();
    while (it.hasNext()) {
      OccurrenceIF occ = it.next();
      if (occ.getLocator() != null)
        return occ;
    }
    return null;
  }
  
}
