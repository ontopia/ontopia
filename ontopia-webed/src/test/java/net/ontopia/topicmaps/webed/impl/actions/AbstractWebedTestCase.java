
package net.ontopia.topicmaps.webed.impl.actions;

import java.util.*;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.ontojsp.FakeServletRequest;
import net.ontopia.utils.ontojsp.FakeServletResponse;
import net.ontopia.topicmaps.webed.core.*;
import net.ontopia.topicmaps.webed.impl.basic.*;
import net.ontopia.topicmaps.webed.impl.actions.*;
import net.ontopia.topicmaps.webed.impl.actions.topicmap.*;
import net.ontopia.topicmaps.webed.impl.basic.Constants;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.utils.*;

import net.ontopia.utils.*;
import java.io.*;
import net.ontopia.topicmaps.xml.*;
import net.ontopia.topicmaps.query.core.*;
import net.ontopia.topicmaps.query.utils.*;

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
    javax.servlet.http.HttpServletRequest request = new FakeServletRequest();
    javax.servlet.http.HttpServletResponse response = new FakeServletResponse();
    return new ActionResponse(request, response);
  }  
  
  public TopicIF getTopicById(TopicMapIF topicmap, String id) {
    net.ontopia.infoset.core.LocatorIF base = topicmap.getStore().getBaseAddress();
    return (TopicIF)
      topicmap.getObjectByItemIdentifier(base.resolveAbsolute("#"+id));
  }
  
}
