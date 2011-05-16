
// $Id: JavaModule.java,v 1.13 2007/11/27 09:13:38 geir.gronmo Exp $

package net.ontopia.topicmaps.query.impl.basic;

import java.io.InputStream;
import java.util.Properties;
import java.util.HashMap;
import java.util.Map;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.parser.ModuleIF;
import net.ontopia.topicmaps.query.parser.PredicateIF;
import net.ontopia.topicmaps.query.spi.SearcherIF;
import net.ontopia.topicmaps.query.spi.JavaPredicate;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.StringUtils;

/**
 * EXPERIMENTAL: A query module that is able to instantiate predicates
 * when given a java class name.<p>
 *
 * Example: import "urn:x-java:net.ontopia.topicmaps.query.spi.LuceneSearcher?index=/tmp/myindex" as lucene<p>
 */
public class JavaModule implements ModuleIF {

  public static final String MODULE_PREFIX = "urn:x-java:";

  protected TopicMapIF topicmap;
  protected String moduleURI;
  
  public JavaModule(TopicMapIF topicmap, String moduleURI) {
    this.topicmap = topicmap;
    this.moduleURI = moduleURI;
  }
  
  public PredicateIF getPredicate(String name) {
    // figure out which class to use
    String className = moduleURI.substring(MODULE_PREFIX.length());
    int qmix = className.indexOf('?');
    String parameters = null;
    if (qmix >= 0) {
      parameters = className.substring(qmix+1);
      className = className.substring(0, qmix);
    }
    Class klass = null;
    try {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      klass = Class.forName(className, true, classLoader);
    } catch (Exception e) {
      throw new OntopiaRuntimeException("Java module class '" + className + "' cannot be found.", e);
    }
    
    try {
      Object o = klass.newInstance();
      if (o instanceof SearcherIF) {
        SearcherIF searcher = (SearcherIF)o;
        searcher.setModuleURI(moduleURI);
        searcher.setPredicateName(name);
        searcher.setTopicMap(topicmap);
        searcher.setParameters(parseParameters(parameters));
        return new JavaSearcherPredicate(name, topicmap, searcher);
      } else if (o instanceof JavaPredicate) {
        JavaPredicate pred = (JavaPredicate) o;
        pred.setModuleURI(moduleURI);
        pred.setPredicateName(name);
        pred.setTopicMap(topicmap);
        pred.setParameters(parseParameters(parameters));
        return pred;
      }
      
    } catch (Exception e) {
      throw new OntopiaRuntimeException("Java module class '" + className + "' cannot be instantiated.", e);
    }
    throw new OntopiaRuntimeException("Java module class '" + className + "' of unknown type.");
  }

  protected Map parseParameters(String parameters) {
    Map result = new HashMap();
    if (parameters != null) {
      String[] tokens = StringUtils.split(parameters, "&");
      for (int i=0; i < tokens.length; i++) {
        String[] vals = StringUtils.split(tokens[i], "=");
        result.put(vals[0], vals[1]);
      }
    }
    return result;
  }
  
}





