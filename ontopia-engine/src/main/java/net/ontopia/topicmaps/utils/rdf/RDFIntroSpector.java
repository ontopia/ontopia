
// $Id: RDFIntroSpector.java,v 1.3 2005/10/18 09:22:13 larsga Exp $

package net.ontopia.topicmaps.utils.rdf;

import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.io.InputStreamReader;

import net.ontopia.utils.OntopiaRuntimeException;

import com.hp.hpl.jena.rdf.arp.*;
import com.hp.hpl.jena.rdf.model.*;

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
    if (mappings == null)
      mappings = new HashMap();
    else
      mappings = new HashMap(mappings);

    GrabMappingsHandler handler = new GrabMappingsHandler(mappings, recommend);

    if (infileurl.endsWith(".rdf"))
      parseRDFXML(handler, infileurl);
    else if (infileurl.endsWith(".n3"))
      parseN3(handler, infileurl);
    else if (infileurl.endsWith(".ntriple"))
      parseN3(handler, infileurl);
    else
      parseRDFXML(handler, infileurl);
    
    return handler.getMappings();
  }

  // Parser methods

  private static void parseRDFXML(GrabMappingsHandler handler, String infileurl)
    throws IOException {
    RDFUtils.parseRDFXML(infileurl, handler);
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

    public String getAnonymousID() {
      return resource.getId().toString();
    }

    public String getURI() {
      return resource.getURI();
    }

    public Object getUserData() {
      throw new UnsupportedOperationException();
    }

    public boolean hasNodeID() {
      throw new UnsupportedOperationException();
    }

    public boolean isAnonymous() {
      return resource.isAnon();
    }

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

    public String getDatatypeURI() {
      throw new UnsupportedOperationException();
    }

    public String getLang() {
      throw new UnsupportedOperationException();
    }

    public String getParseType() {
      throw new UnsupportedOperationException();
    }

    public boolean isWellFormedXML() {
      throw new UnsupportedOperationException();
    }

    public String toString() {
      return literal.getString();
    }

    public void setLiteral(Literal literal) {
      this.literal = literal;
    }

    private boolean tainted;

    public void taint() {
      tainted = true;
    }

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
    
    public void statement(AResource sub, AResource pred, ALiteral lit) {
      String preduri = pred.getURI();      
      if (preduri.startsWith(RDFToTopicMapConverter.RTM_PREFIX))
        return;
      
      if (recommend && !mappings.containsKey(preduri)) {
        String low = preduri.toLowerCase();
        String mapsto = RDFToTopicMapConverter.RTM_OCCURRENCE;
        if (low.endsWith("name") ||
            low.endsWith("title") ||
            low.endsWith("label"))
          mapsto = RDFToTopicMapConverter.RTM_BASENAME;
        getMapping(preduri).setMapsTo(mapsto);
      }
    }

    public void statement(AResource sub, AResource pred, AResource obj) {      
      String preduri = pred.getURI();
      if (preduri.equals(RDFToTopicMapConverter.RTM_MAPSTO)) {
        getMapping(sub.getURI()).setMapsTo(obj.getURI());
        return;
      }
      
      if (preduri.startsWith(RDFToTopicMapConverter.RTM_PREFIX)) {
        RDFPropertyMapping mapping = getMapping(sub.getURI());

        if (preduri.equals(RDFToTopicMapConverter.RTM_IN_SCOPE))
          mapping.setInScope(obj.getURI());
        else if (preduri.equals(RDFToTopicMapConverter.RTM_TYPE))
          mapping.setType(obj.getURI());
        else if (preduri.equals(RDFToTopicMapConverter.RTM_SUBJECT_ROLE))
          mapping.setSubjectRole(obj.getURI());
        else if (preduri.equals(RDFToTopicMapConverter.RTM_OBJECT_ROLE))
          mapping.setObjectRole(obj.getURI());
      } else if (recommend && !mappings.containsKey(preduri)) {
        String mapsto = (String) defaults.get(preduri);
        if (mapsto == null)
          mapsto = RDFToTopicMapConverter.RTM_ASSOCIATION;
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
