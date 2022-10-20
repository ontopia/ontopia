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

package net.ontopia.topicmaps.utils.rdf;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.rdfxml.xmlinput.ALiteral;
import org.apache.jena.rdfxml.xmlinput.AResource;
import org.apache.jena.rdfxml.xmlinput.StatementHandler;

/**
 * INTERNAL: Used by the RDF2TM plugin.
 */
public class RDFIntroSpector {

  private static final Map defaults;
  static {
    defaults = new HashMap();
    defaults.put("http://www.w3.org/1999/02/22-rdf-syntax-ns#type",
                 RDFToTopicMapConverter.RTM_INSTANCE_OF);
    defaults.put("http://www.w3.org/2000/01/rdf-schema#subClassOf",
                 RDFToTopicMapConverter.RTM_ASSOCIATION);
    defaults.put("http://www.w3.org/2000/01/rdf-schema#seeAlso",
                 RDFToTopicMapConverter.RTM_OCCURRENCE);
  }
  
  public static Map getPropertyMappings(String infileurl, boolean recommend)
    throws IOException {
    return getPropertyMappings(infileurl, recommend, null);
  }

  public static Map getPropertyMappings(String infileurl, boolean recommend,
                                        Map mappings)
    throws IOException {
    if (mappings == null) {
      mappings = new HashMap();
    } else {
      mappings = new HashMap(mappings);
    }

    GrabMappingsHandler handler = new GrabMappingsHandler(mappings, recommend);

    if (infileurl.endsWith(".rdf")) {
      parseRDFXML(handler, infileurl);
    } else if (infileurl.endsWith(".n3")) {
      parseN3(handler, infileurl);
    } else if (infileurl.endsWith(".ntriple")) {
      parseN3(handler, infileurl);
    } else {
      parseRDFXML(handler, infileurl);
    }
    
    return handler.getMappings();
  }

  // Parser methods

  private static void parseRDFXML(GrabMappingsHandler handler, String infileurl)
    throws IOException {
    RDFUtils.parseRDFXML(new URL(infileurl), handler);
  }

  private static void parseN3(GrabMappingsHandler handler, String infileurl) {
    Model model = ModelFactory.createDefaultModel();
    model.read(infileurl, "N3");

    AResourceImpl sub = new AResourceImpl();
    AResourceImpl pred = new AResourceImpl();
    AResourceImpl objres = new AResourceImpl();
    ALiteralImpl objlit = new ALiteralImpl();
    StmtIterator it = model.listStatements();
    while (it.hasNext()) {
      Statement stmt = it.nextStatement();
      RDFNode object = stmt.getObject();
      sub.setResource(stmt.getSubject());
      pred.setResource(stmt.getPredicate());
      
      if (object instanceof Literal) {
        objlit.setLiteral((Literal) object);
        handler.statement(sub, pred, objlit);
      } else {
        objres.setResource((Resource) object);
        handler.statement(sub, pred, objres);
      }
    }
  }
  
  // Dummy AResource implementation to wrap Jena resources

  private static class AResourceImpl implements AResource {
    private Resource resource;

    @Override
    public String getAnonymousID() {
      return resource.getId().toString();
    }

    @Override
    public String getURI() {
      return resource.getURI();
    }

    @Override
    public Object getUserData() {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasNodeID() {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAnonymous() {
      return resource.isAnon();
    }

    @Override
    public void setUserData(Object object) {
      throw new UnsupportedOperationException();
    }

    public void setResource(Resource resource) {
      this.resource = resource;
    }
    
  }

  // Dummy AResource implementation to wrap Jena resources

  private static class ALiteralImpl implements ALiteral {
    private Literal literal;
    private boolean tainted;

    @Override
    public String getDatatypeURI() {
      throw new UnsupportedOperationException();
    }

    @Override
    public String getLang() {
      throw new UnsupportedOperationException();
    }

    @Override
    public String getParseType() {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean isWellFormedXML() {
      throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
      return literal.getString();
    }

    public void setLiteral(Literal literal) {
      this.literal = literal;
    }

    @Override
    public void taint() {
      tainted = true;
    }

    @Override
    public boolean isTainted() {
      return tainted;
    }
    
  }
  
  // Event handler used to analyze data
  
  private static class GrabMappingsHandler implements StatementHandler {
    private boolean recommend;
    private Map mappings;
    
    public GrabMappingsHandler(Map mappings, boolean recommend) {
      this.mappings = mappings;
      this.recommend = recommend;
    }

    public Map getMappings() {
      return mappings;
    }
    
    @Override
    public void statement(AResource sub, AResource pred, ALiteral lit) {
      String preduri = pred.getURI();      
      if (preduri.startsWith(RDFToTopicMapConverter.RTM_PREFIX)) {
        return;
      }
      
      if (recommend && !mappings.containsKey(preduri)) {
        String low = preduri.toLowerCase();
        String mapsto = RDFToTopicMapConverter.RTM_OCCURRENCE;
        if (low.endsWith("name") ||
            low.endsWith("title") ||
            low.endsWith("label")) {
          mapsto = RDFToTopicMapConverter.RTM_BASENAME;
        }
        getMapping(preduri).setMapsTo(mapsto);
      }
    }

    @Override
    public void statement(AResource sub, AResource pred, AResource obj) {      
      String preduri = pred.getURI();
      if (preduri.equals(RDFToTopicMapConverter.RTM_MAPSTO)) {
        getMapping(sub.getURI()).setMapsTo(obj.getURI());
        return;
      }
      
      if (preduri.startsWith(RDFToTopicMapConverter.RTM_PREFIX)) {
        RDFPropertyMapping mapping = getMapping(sub.getURI());

        if (preduri.equals(RDFToTopicMapConverter.RTM_IN_SCOPE)) {
          mapping.setInScope(obj.getURI());
        } else if (preduri.equals(RDFToTopicMapConverter.RTM_TYPE)) {
          mapping.setType(obj.getURI());
        } else if (preduri.equals(RDFToTopicMapConverter.RTM_SUBJECT_ROLE)) {
          mapping.setSubjectRole(obj.getURI());
        } else if (preduri.equals(RDFToTopicMapConverter.RTM_OBJECT_ROLE)) {
          mapping.setObjectRole(obj.getURI());
        }
      } else if (recommend && !mappings.containsKey(preduri)) {
        String mapsto = (String) defaults.get(preduri);
        if (mapsto == null) {
          mapsto = RDFToTopicMapConverter.RTM_ASSOCIATION;
        }
        getMapping(preduri).setMapsTo(mapsto);
      }
    }

    private RDFPropertyMapping getMapping(String preduri) {
      RDFPropertyMapping mapping = (RDFPropertyMapping) mappings.get(preduri);
      if (mapping == null) {
        mapping = new RDFPropertyMapping(preduri);
        mappings.put(preduri, mapping);
      }
      return mapping;
    }
  }
}
