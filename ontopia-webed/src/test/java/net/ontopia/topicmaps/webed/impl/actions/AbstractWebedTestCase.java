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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.WebEdRequestIF;
import net.ontopia.topicmaps.webed.impl.basic.ActionParameters;
import net.ontopia.topicmaps.webed.impl.basic.ActionResponse;
import net.ontopia.topicmaps.xml.XTMTopicMapWriter;
import net.ontopia.utils.ontojsp.FakeServletRequest;
import net.ontopia.utils.ontojsp.FakeServletResponse;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.TestFileUtils;

import junit.framework.TestCase;
 
public abstract class AbstractWebedTestCase extends TestCase {
  protected TopicMapIF tm;

  private final static String testdataDirectory = "webed";
  
  public AbstractWebedTestCase(String name) {
    super(name);
  }
  
  // helper methods
  
  public void setUp() {
    try {
      tm = ImportExportUtils.getReader(TestFileUtils.getTestInputFile(testdataDirectory, "football.ltm")).read();
    } catch (java.io.IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  public void exportMap(TopicMapIF map) throws java.io.IOException{
    File file = new File("football_test.xtm");
    XTMTopicMapWriter daWriter = new XTMTopicMapWriter(file);
    daWriter.write(map);
  }
  
  public QueryResultIF runQuery(String query) throws InvalidQueryException{
    QueryProcessorIF processor= QueryUtils.getQueryProcessor(tm);
    QueryResultIF result = processor.execute(query);
    return result;
  }

  public void dbPrint(String string, Object var){
    System.out.println("\n\n" + var + "\n\n");
  }
  
  public void dbPrint(String string){
    System.out.println("\n\n" + string  + "\n\n");
  }
  
  // public void dbPrint(Object var){
  //  System.out.println("\n\n" + var  + "\n\n");
  //}
  
  public void dbPrint(Collection res){
    Iterator i = res.iterator();
    System.out.println("\n\n");
    while (i.hasNext()){
      System.out.println(i.next());
    }
    System.out.println("\n");
     
  }
  
  public TopicIF makeTopic(TopicMapIF map, String topicname) {
    
    TopicMapBuilderIF builder = map.getBuilder();
    
    // add topic to topic map
    TopicIF topic = builder.makeTopic();
    
    // builder adds base name to topic and sets name string
    builder.makeTopicName(topic, topicname);
    
    return topic;
  }
  
  public List makeList(Object param1) {
    param1 = Collections.singleton(param1); // params are lists of collections...
    return Collections.singletonList(param1);
  }
  
  public List makeList(Object param1, Object param2) {
    List list = new ArrayList(2);
    list.add(Collections.singleton(param1));
    list.add(Collections.singleton(param2));
    return list;
  }
  
  public List makeList(Object param1, Object param2, Object param3) {
    List list = new ArrayList(3);
    list.add(Collections.singleton(param1));
    list.add(Collections.singleton(param2));
    list.add(Collections.singleton(param3));
    return list;
  }
    
  public ActionParametersIF makeParameters(Object param1, String value) {
    if (param1 instanceof List)
      return makeParameters((List) param1, value);
    
    return makeParameters(makeList(param1), value);
  }
  
  public ActionParametersIF makeParameters(Object param1, String key, String value) {
    if (param1 instanceof List)
      return makeParameters((List) param1, key, value);
    
    return makeParameters(makeList(param1), key, value);
  }
  
  public ActionParametersIF makeParameters(List params) {
    return makeParameters(params, null);
  }
  
  public ActionParametersIF makeParameters(List params, String value) {
    return makeParameters(params, "boo1", value);
  }

  public ActionParametersIF makeParameters (List params, String value,
                                            WebEdRequestIF request) {
    String[] values = {value};
    return new ActionParameters("boo1", values, null, params, tm, request);
  }
  
  public ActionParametersIF makeParameters (List params, String key,
                                            String value) {
    String[] values = {value};
    return new ActionParameters(key, values, null, params, tm, null);
  }

  public ActionParametersIF makeParameters (List params, String key,
                                            String value,
                                            WebEdRequestIF request) {
    String[] values = {value};
    return new ActionParameters(key, values, null, params, tm, request);
  }
  
  public ActionResponseIF makeResponse() {
    HttpServletRequest request = new FakeServletRequest();
    HttpServletResponse response = new FakeServletResponse();
    return new ActionResponse(request, response);
  }  
  
  public TopicIF getTopicById(TopicMapIF topicmap, String id) {
    net.ontopia.infoset.core.LocatorIF base = topicmap.getStore().getBaseAddress();
    return (TopicIF)
      topicmap.getObjectByItemIdentifier(base.resolveAbsolute("#"+id));
  }
  
}
