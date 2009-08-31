
// $Id: LocalParseContext.java,v 1.13 2008/06/23 09:31:24 lars.garshol Exp $

package net.ontopia.topicmaps.query.parser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.query.core.DeclarationContextIF;
import net.ontopia.topicmaps.query.core.BadObjectReferenceException;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.parser.TologOptions;

/**
 * INTERNAL: Represents the local context in which a tolog query or
 * rule file is being parsed. Context may be the context bound to a
 * QueryProcessorIF object, or it may be only a transient local
 * context for a query.
 */
public class LocalParseContext implements ParseContextIF, DeclarationContextIF {
  private ParseContextIF subcontext;
  private Map bindings;
  private Map predicates;
  Set loading_modules = new HashSet();

  public LocalParseContext(ParseContextIF subcontext) {
    this.subcontext = subcontext;
    this.bindings = new HashMap();
    this.predicates = new HashMap();
  }
  
  public void addPrefixBinding(String prefix, String uri, int qualification)
    throws AntlrWrapException {

    if (bindings.containsKey(prefix))
      throw new AntlrWrapException(new InvalidQueryException("Prefix " + prefix +
                                                             " already bound"));
    
    if (qualification == MODULE) {
      ModuleIF module = getModule(uri);
      if (module == null) {
        LocalParseContext modulectx = new LocalParseContext(subcontext);
        TologParser parser = new TologParser(modulectx, TologOptions.defaults);
        try {
          if (isLoading(uri))
            throw new InvalidQueryException("Importing an already imported module is not allowed: '" + uri + "'");

          // attempt to load module from class loader / classpath
          ClassLoader cl = Thread.currentThread().getContextClassLoader();
          InputStream istream = cl.getResourceAsStream(uri);
          try {
            // NOTE: this is a hack to prevent infinite recursion when
            // loading modules.
            modulectx.loading_modules.add(uri);            

            // resolve uri absolute against topic map base address
            if (istream == null)
              istream = new URL(absolutify(uri).getAddress()).openStream();
            
            parser.load(new InputStreamReader(istream));
                        
          } finally {
            modulectx.loading_modules.remove(uri);
            if (istream != null) istream.close();
          }

        } catch (IOException e) {
          throw new AntlrWrapException(e);
        } catch (InvalidQueryException e) {
          throw new AntlrWrapException(e);
        }
        module = modulectx.getModule();
      }

      bindings.put(prefix, new PrefixBinding(uri, module));      
    } else {
      absolutify(uri); // done to get error messages here
      bindings.put(prefix, new PrefixBinding(uri, qualification));
    }
  }
 
  public boolean isLoading(String uri) {
    return (subcontext.isLoading(uri) || loading_modules.contains(uri));
  }

  public boolean isBuiltInPredicate(String name) {
    return subcontext.isBuiltInPredicate(name);
  }
  
  public void addPredicate(PredicateIF predicate) throws AntlrWrapException {
    if (predicates.containsKey(predicate.getName()))
      throw new AntlrWrapException(new InvalidQueryException("Predicate " +
                                                             predicate.getName() +
                                                             " exists already"));
    if (subcontext.isBuiltInPredicate(predicate.getName()))
      throw new AntlrWrapException(new InvalidQueryException(
        "Predicate " + predicate.getName() + " is a built-in predicate, and cannot "+
        "be redefined."));
    
    predicates.put(predicate.getName(), predicate);
  }

  public TopicIF getTopic(QName qname) throws AntlrWrapException {
    TMObjectIF object = getObject(qname);
    if (!(object instanceof TopicIF))
      throw new AntlrWrapException(
              new InvalidQueryException("Found " + qname + ", referring to non-topic "
                                        + object));

    return (TopicIF) object;
  }

  public TMObjectIF getObject(QName qname) throws AntlrWrapException {
    return checkReference(getObject_(qname), qname.toString());
  }
  
  private TMObjectIF getObject_(QName qname) throws AntlrWrapException {
    String prefix = qname.getPrefix();
    String localname = qname.getLocalName();
      
    if (prefix == null)
      return subcontext.getObject(qname);

    PrefixBinding binding = (PrefixBinding) bindings.get(prefix);
    if (binding == null)
      return subcontext.getObject(qname);

    switch (binding.getQualification()) {
    case SUBJECT_LOCATOR:
      return getTopicBySubjectLocator(binding.getUri(localname));
    case SUBJECT_IDENTIFIER:
      return getTopicBySubjectIdentifier(binding.getUri(localname));
    case ITEM_IDENTIFIER:
      return getObjectByItemId(binding.getUri(localname));
    default:
      throw new AntlrWrapException(
              new InvalidQueryException("Prefix " + prefix + " bound to a module"));
    }    
  }

  public PredicateIF getPredicate(QName qname, boolean assoc)
    throws AntlrWrapException {
    if (qname.getPrefix() == null) {
      PredicateIF predicate = (PredicateIF) predicates.get(qname.getLocalName());
      if (predicate == null)
        predicate = subcontext.getPredicate(qname, assoc);
      return predicate;
    }

    PrefixBinding binding = (PrefixBinding) bindings.get(qname.getPrefix());
    if (binding == null)
      return subcontext.getPredicate(qname, assoc);

    if (binding.getQualification() == MODULE)
      return binding.getModule().getPredicate(qname.getLocalName());
    else
      return getPredicate(getTopic(qname), assoc);
  }

  public PredicateIF getPredicate(TopicIF topic, boolean assoc) {
    return subcontext.getPredicate(topic, assoc);
  }

  public PredicateIF getPredicate(ParsedRule rule) {
    return subcontext.getPredicate(rule);
  }

  public ModuleIF getModule(String uri) {
    return subcontext.getModule(uri);
  }
  
  public LocatorIF absolutify(String uriref) throws AntlrWrapException {
    return subcontext.absolutify(uriref);
  }
  
  public TMObjectIF getObjectByObjectId(String id) throws AntlrWrapException {
    return checkReference(subcontext.getObjectByObjectId(id), "@" + id);
  }

  public TopicIF getTopicBySubjectIdentifier(String uri) throws AntlrWrapException {
    return subcontext.getTopicBySubjectIdentifier(uri);
  }

  public TopicIF getTopicBySubjectLocator(String uri) throws AntlrWrapException {
    return subcontext.getTopicBySubjectLocator(uri);
  }

  public TMObjectIF getObjectByItemId(String uri) throws AntlrWrapException {
    return checkReference(subcontext.getObjectByItemId(uri), uri);
  }

  // --- Methods specific to LocalParseContext

  /**
   * INTERNAL: Creates a module consisting of all the rules loaded
   * into the local context.
   */
  private ModuleIF getModule() {
    return new RuleFileModule(predicates);
  }
  
  // --- Internal methods

  private TMObjectIF checkReference(TMObjectIF value, String token)
    throws AntlrWrapException {
    if (value == null)
      throw new AntlrWrapException(new BadObjectReferenceException("No object for " +
                                                             token));
    return value;
  }
  
  // --- Prefix binding
  
  static class PrefixBinding {
    private String uri;
    private int qualification;
    private ModuleIF module;
  
    public PrefixBinding(String uri, int qualification) {
      this.uri = uri;
      this.qualification = qualification;
    }

    public PrefixBinding(String uri, ModuleIF module) {
      this.uri = uri;
      this.qualification = MODULE;
      this.module = module;
    }
    
    public String getUri(String localpart) {
      return uri + localpart;
    }

    public int getQualification() {
      return qualification;
    }

    public ModuleIF getModule() {
      return module;
    }
  }

  // --- Rule file module

  static class RuleFileModule implements ModuleIF {
    private Map predicates;

    public RuleFileModule(Map predicates) {
      this.predicates = predicates;
    }
    
    public PredicateIF getPredicate(String name) {
      return (PredicateIF) predicates.get(name);
    }
  }
}
