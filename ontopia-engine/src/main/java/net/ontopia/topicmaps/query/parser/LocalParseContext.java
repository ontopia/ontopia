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

package net.ontopia.topicmaps.query.parser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URL;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.ontopia.utils.CompactHashSet;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.DeclarationContextIF;
import net.ontopia.topicmaps.query.core.BadObjectReferenceException;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * INTERNAL: Represents the local context in which a tolog query or
 * rule file is being parsed. Context may be the context bound to a
 * QueryProcessorIF object, or it may be only a transient local
 * context for a query.
 */
public class LocalParseContext implements ParseContextIF, DeclarationContextIF {
  private ParseContextIF subcontext;
  private Map<String, PrefixBinding> bindings;
  private Map predicates;
  private Set loading_modules = new CompactHashSet();

  public LocalParseContext(ParseContextIF subcontext) {
    this.subcontext = subcontext;
    this.bindings = new HashMap<String, PrefixBinding>();
    this.predicates = new HashMap();
  }

  @Override
  public TopicMapIF getTopicMap() {
    return subcontext.getTopicMap();
  }

  @Override
  public LocatorIF resolveQName(QName qname) {
    PrefixBinding binding = bindings.get(qname.getPrefix());
    if (binding == null) {
      throw new OntopiaRuntimeException("No such prefix " + qname.getPrefix());
    }
    if (binding.getQualification() != SUBJECT_IDENTIFIER) {
      throw new OntopiaRuntimeException("Prefix " + qname.getPrefix() +
                                        " is not a subject identifier prefix");
    }
    try {
      return new URILocator(binding.getUri(qname.getLocalName()));
    } catch (URISyntaxException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  @Override
  public void addPrefixBinding(String prefix, String uri, int qualification)
    throws AntlrWrapException {

    if (bindings.containsKey(prefix)) {
      throw new AntlrWrapException(new InvalidQueryException("Prefix " + prefix +
                                                             " already bound"));
    }
    
    if (qualification == MODULE) {
      ModuleIF module = getModule(uri);
      if (module == null) {
        LocalParseContext modulectx = new LocalParseContext(subcontext);
        TologParser parser = new TologParser(modulectx, TologOptions.defaults);
        try {
          if (isLoading(uri)) {
            throw new InvalidQueryException("Importing an already imported module is not allowed: '" + uri + "'");
          }

          // attempt to load module from class loader / classpath
          ClassLoader cl = Thread.currentThread().getContextClassLoader();
          InputStream istream = cl.getResourceAsStream(uri);
          try {
            // NOTE: this is a hack to prevent infinite recursion when
            // loading modules.
            modulectx.loading_modules.add(uri);            

            // resolve uri absolute against topic map base address
            if (istream == null) {
              istream = new URL(absolutify(uri).getAddress()).openStream();
            }
            
            parser.load(new InputStreamReader(istream));
                        
          } finally {
            modulectx.loading_modules.remove(uri);
            if (istream != null) {
              istream.close();
            }
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
 
  @Override
  public boolean isLoading(String uri) {
    return (subcontext.isLoading(uri) || loading_modules.contains(uri));
  }

  @Override
  public boolean isBuiltInPredicate(String name) {
    return subcontext.isBuiltInPredicate(name);
  }
  
  @Override
  public void addPredicate(PredicateIF predicate) throws AntlrWrapException {
    if (predicates.containsKey(predicate.getName())) {
      throw new AntlrWrapException(new InvalidQueryException("Predicate " +
                                                             predicate.getName() +
                                                             " exists already"));
    }
    if (subcontext.isBuiltInPredicate(predicate.getName())) {
      throw new AntlrWrapException(new InvalidQueryException(
        "Predicate " + predicate.getName() + " is a built-in predicate, and cannot "+
        "be redefined."));
    }
    
    predicates.put(predicate.getName(), predicate);
  }

  @Override
  public TopicIF getTopic(QName qname) throws AntlrWrapException {
    TMObjectIF object = getObject(qname);
    if (!(object instanceof TopicIF)) {
      throw new AntlrWrapException(
              new InvalidQueryException("Found " + qname + ", referring to non-topic "
                                        + object));
    }

    return (TopicIF) object;
  }

  @Override
  public TMObjectIF getObject(QName qname) throws AntlrWrapException {
    return checkReference(getObject_(qname), qname.toString());
  }
  
  private TMObjectIF getObject_(QName qname) throws AntlrWrapException {
    String prefix = qname.getPrefix();
    String localname = qname.getLocalName();
      
    if (prefix == null) {
      return subcontext.getObject(qname);
    }

    PrefixBinding binding = bindings.get(prefix);
    if (binding == null) {
      return subcontext.getObject(qname);
    }

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

  @Override
  public PredicateIF getPredicate(QName qname, boolean assoc)
    throws AntlrWrapException {
    if (qname.getPrefix() == null) {
      PredicateIF predicate = (PredicateIF) predicates.get(qname.getLocalName());
      if (predicate == null) {
        predicate = subcontext.getPredicate(qname, assoc);
      }
      return predicate;
    }

    PrefixBinding binding = bindings.get(qname.getPrefix());
    if (binding == null) {
      return subcontext.getPredicate(qname, assoc);
    }

    if (binding.getQualification() == MODULE) {
      return binding.getModule().getPredicate(qname.getLocalName());
    } else {
      return getPredicate(getTopic(qname), assoc);
    }
  }

  @Override
  public PredicateIF getPredicate(TopicIF topic, boolean assoc) {
    return subcontext.getPredicate(topic, assoc);
  }

  @Override
  public PredicateIF getPredicate(ParsedRule rule) {
    return subcontext.getPredicate(rule);
  }

  @Override
  public ModuleIF getModule(String uri) {
    return subcontext.getModule(uri);
  }
  
  @Override
  public LocatorIF absolutify(String uriref) throws AntlrWrapException {
    return subcontext.absolutify(uriref);
  }
  
  @Override
  public TMObjectIF getObjectByObjectId(String id) throws AntlrWrapException {
    return checkReference(subcontext.getObjectByObjectId(id), "@" + id);
  }

  @Override
  public TopicIF getTopicBySubjectIdentifier(String uri) throws AntlrWrapException {
    return subcontext.getTopicBySubjectIdentifier(uri);
  }

  @Override
  public TopicIF getTopicBySubjectLocator(String uri) throws AntlrWrapException {
    return subcontext.getTopicBySubjectLocator(uri);
  }

  @Override
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
    if (value == null) {
      throw new AntlrWrapException(new BadObjectReferenceException("No object for " +
                                                             token));
    }
    return value;
  }

  @Override
  public void dump() {
    System.out.println("===== LocalParseContext " + this);
    for (String prefix : bindings.keySet()) {
      PrefixBinding bind = bindings.get(prefix);
      System.out.println(prefix + " : " + bind.uri);
    }
    subcontext.dump();
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
    
    @Override
    public PredicateIF getPredicate(String name) {
      return (PredicateIF) predicates.get(name);
    }
  }
}
