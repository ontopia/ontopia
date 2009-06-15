
// $Id: WebChew.java,v 1.39 2008/06/12 14:37:12 geir.gronmo Exp $

package net.ontopia.topicmaps.classify;

import java.io.*;
import java.util.*;
import java.text.*;

import javax.servlet.*;
import javax.servlet.http.*;
  
import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.utils.*;
import net.ontopia.topicmaps.classify.*;
import net.ontopia.topicmaps.nav2.utils.*;
import net.ontopia.topicmaps.query.core.*;
import net.ontopia.topicmaps.query.utils.*;

import org.apache.commons.fileupload.*;

/**
 * INTERNAL: 
 */
public class WebChew {

  HttpServletRequest request;
  HttpServletResponse response;
  int visibleRows = Integer.MAX_VALUE;

  String redirectURI; // link back to topic page
  
  public WebChew(HttpServletRequest request, HttpServletResponse response) {
    this.request = request;
    this.response = response;
  }

  public void setVisibleRows(int visibleRows) {
    this.visibleRows = visibleRows;
  }

  public void setRedirectURI(String redirectURI) {
    this.redirectURI = redirectURI;
  }
  
  public void processForm() {
    HttpSession session = request.getSession(true);
    String tmckey = getClassificationKey();
        
    // reclassify
    if (request.getParameter("reclassify") != null) {
      session.removeAttribute(tmckey);
    }
    
    // black list selected terms
    String blacklisted = request.getParameter("blacklisted");
    if (blacklisted  != null && blacklisted.length() > 0) {
      BlackList bl = getBlackList();
      bl.addStopWord(blacklisted);
      bl.save();
    }

    // remove selected association
    String removeAssociation = request.getParameter("removeAssociation");
    if (removeAssociation  != null) {
      // process form data
      try {
        TopicMapStoreIF store = NavigatorUtils.getTopicMapRepository(session.getServletContext()).getReferenceByKey(request.getParameter("tm")).createStore(false);
        try {
          TopicMapIF topicmap = store.getTopicMap();
          AssociationIF assoc = (AssociationIF)topicmap.getObjectById(removeAssociation);
          if (assoc != null)
            assoc.remove();
          store.commit();
        } finally {
          store.close();
        }
      } catch (Exception e) {
        throw new OntopiaRuntimeException(e);
      }        
    }

    String[] selected = request.getParameterValues("selected");
    
    if (request.getParameter("ok") != null ||
        request.getParameter("cancel") != null) {        

      try {
        
        // if ok pressed process form
        if (request.getParameter("ok") != null) {
          
          // create associations; look up existing classified document in session
          TopicMapClassification tmc = (TopicMapClassification)session.getAttribute(tmckey);
          if (tmc == null) return;
          
          // process form data
          TopicMapStoreIF store = NavigatorUtils.getTopicMapRepository(session.getServletContext()).getReferenceByKey(request.getParameter("tm")).createStore(false);
          try {
            TopicMapIF topicmap = store.getTopicMap();
            TopicMapBuilderIF builder = topicmap.getBuilder();
            
            // get document topic
            TopicIF dtopic = (TopicIF)topicmap.getObjectById(request.getParameter("id"));

            if (selected != null && selected.length > 0) {
              for (int i=0; i < selected.length; i++) {
              
                String termid = selected[i];
                String at = request.getParameter("at-" + termid);
                if (at == null || at.equals("-")) continue;
                String cn = request.getParameter("cn-" + termid);
                String ct = request.getParameter("ct-" + termid);
                if (ct == null || ct.equals("-")) continue;

                // create new candidate topic
                TopicIF ctopic;
                if (ct.startsWith("new:")) {
                  String ctoid = ct.substring("new:".length());
                  TopicIF ctype = (TopicIF)topicmap.getObjectById(ctoid);
                  if (ctype == null)
                    throw new OntopiaRuntimeException("Cannot find topic type: " + ct + " " + ctoid);
                  ctopic = builder.makeTopic(ctype);
                  TopicNameIF bname = builder.makeTopicName(ctopic, cn);
                } else if (ct.equals("-")) {
                  continue; // ignore
                } else {
                  ctopic = (TopicIF)topicmap.getObjectById(ct);
                }

                // create association
                String[] at_data = StringUtils.split(at, ":");
                if (at_data.length != 3) continue;
                
                TopicIF atype = (TopicIF)topicmap.getObjectById(at_data[0]);
                if (atype == null)
                  throw new OntopiaRuntimeException("Cannot find association type: " + at);
                
                TopicIF drtype = (TopicIF)topicmap.getObjectById(at_data[1]);
                if (drtype == null)
                  throw new OntopiaRuntimeException("Cannot find document roletype: " + at_data[1]);
                TopicIF crtype = (TopicIF)topicmap.getObjectById(at_data[2]);
                if (crtype == null)
                  throw new OntopiaRuntimeException("Cannot find concept roletype: " + at_data[2]);
                
                AssociationIF assoc = builder.makeAssociation(atype);
                builder.makeAssociationRole(assoc, drtype, dtopic);
                builder.makeAssociationRole(assoc, crtype, ctopic);
              }
              // remove duplicate associations
              DuplicateSuppressionUtils.removeDuplicateAssociations(dtopic);
              
              store.commit();
            }
            
          } finally {
            store.close();
          }
        }
        
        // clear classication
        session.removeAttribute(tmckey);      
        
        // redirect back to instance page
        response.sendRedirect(redirectURI);

      } catch (Exception e) {
        throw new OntopiaRuntimeException(e);
      }
    }
  }

  private BlackList getBlackList() {
    HttpSession session = request.getSession(true);
    String blkey = "webchew-blacklist-" + request.getParameter("tm");
    synchronized (session) {
      BlackList bl = (BlackList)session.getAttribute(blkey);
      if (bl == null) {
        bl = new BlackList(new File(System.getProperty("user.home") + "/.oks/classify/blacklist." + request.getParameter("tm")));
        session.setAttribute(blkey, bl);
      }
      return bl;
    }
  }

  private String getClassificationKey() {
    return "webchew-" + request.getParameter("tm") + ":" + request.getParameter("id");
  }
  
  public WebClassification getClassification() {
    try {
      // look up existing classified document in session
      HttpSession session = request.getSession(true);
      String tmckey = getClassificationKey();
      TopicMapClassification tmc = (TopicMapClassification)session.getAttribute(tmckey);
      if (tmc == null) {
    
        // use document repository
        TopicMapIF topicmap = ContextUtils.getTopicMap(request);
        TopicIF topic = (TopicIF)topicmap.getObjectById(request.getParameter("id"));
        
        // get content via plug-in
        ClassifyPluginIF cp = WebChew.getPlugin(request);
        ClassifiableContentIF cc = cp.getClassifiableContent(topic);
        
        // if no plug-in content then delegate to file upload
        if (cc == null)
          cc = ClassifyUtils.getFileUploadContent(request);
        
        // classify content
        if (cc != null) {
          tmc = classifyContent(cc, topicmap);
          session.setAttribute(tmckey, tmc);
        }
      }
      return (tmc == null ? null : new WebClassification(tmc));
      
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  /**
   * INTERNAL: Returns the plug-in class instance used by the ontopoly
   * plugin. Used by classify/plugin.jsp.
   */
  public static ClassifyPluginIF getPlugin(HttpServletRequest request) {
    // create plugin by dynamically intantiating plugin class
    HttpSession session = request.getSession(true);
    ServletContext scontext = session.getServletContext();
    String pclass = scontext.getInitParameter("classify_plugin");
    if (pclass == null)
      pclass = "net.ontopia.topicmaps.classify.DefaultPlugin";
    ClassifyPluginIF cp = (ClassifyPluginIF)ObjectUtils.newInstance(pclass);
    if (cp instanceof HttpServletRequestAwareIF)
      ((HttpServletRequestAwareIF)cp).setRequest(request);
    return cp;
  }

  private TopicMapClassification classifyContent(ClassifiableContentIF cc, TopicMapIF topicmap) {
    try {

      TopicMapClassification tmc = new TopicMapClassification(topicmap);
      BlackList bl = getBlackList();
      if (bl != null) tmc.setCustomTermAnalyzer(bl);
      tmc.classify(cc);
      return tmc;
      
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  public class WebClassification {

    TopicMapClassification tmc;
    List topterms;
    
    WebClassification(TopicMapClassification tmc) {
      this.tmc = tmc;

      // get top terms
      Object[] terms = tmc.getTermDatabase().getTermsByRank();
      topterms = new ArrayList(visibleRows);

      // ignore black listed terms
      BlackList bl = getBlackList();
      for (int i=0; i < terms.length && topterms.size() < visibleRows; i++) {
        Term term = (Term)terms[i];
        if (bl == null || !bl.isStopWord(term.getPreferredName())) {
          topterms.add(new WebTerm(this, term, i));
        }
      }
    }

    public List getTerms() {
      return topterms;
    }

    public Collection getCandidateTypes() {
      return tmc.getCandidateTypes();
    }

    public Collection getAssociationTypes() {
      return tmc.getAssociationTypes();
    }

    public Collection getExistingAssociations() {
      Collection result = new ArrayList();
      
      try {
        TopicMapIF topicmap = ContextUtils.getTopicMap(request);
        QueryProcessorIF qp = QueryUtils.getQueryProcessor(topicmap);
        ParsedQueryIF pq = qp.parse("select $A, $O from role-player($R1, %TOPIC%), type($R1, %CRTYPE%), association-role($A, $R1), type($A, %ATYPE%), association-role($A, $R2), $R1 /= $R2, type($R2, %PRTYPE%), role-player($R2, $O)?");
        
        Map params = new HashMap();
        params.put("TOPIC", topicmap.getObjectById(request.getParameter("id")));
        
        Iterator iter = getAssociationTypes().iterator();
        while (iter.hasNext()) {
          TopicMapAnalyzer.AssociationType _atype = (TopicMapAnalyzer.AssociationType)iter.next();
          TopicIF atype = (TopicIF)topicmap.getObjectById(_atype.getAssociationTypeId());
          TopicIF crtype = (TopicIF)topicmap.getObjectById(_atype.getContentRoleTypeId());
          TopicIF prtype = (TopicIF)topicmap.getObjectById(_atype.getTopicRoleTypeId());
          params.put("ATYPE", atype);
          params.put("CRTYPE", crtype);
          params.put("PRTYPE", prtype);
          QueryResultIF qr = pq.execute(params);
          while (qr.next()) {
            AssociationIF assoc = (AssociationIF)qr.getValue(0);
            TopicIF player = (TopicIF)qr.getValue(1);

            ExistingAssociation x = new ExistingAssociation();            
            x.associationId = assoc.getObjectId();
            x.associationName = TopicStringifiers.toString(atype, crtype);
            x.associatedTopicName = TopicStringifiers.toString(player);
            result.add(x);
          }
        }
      } catch (Exception e) {
        throw new OntopiaRuntimeException(e);
      }
      return result;
    }
  }

  public class ExistingAssociation {

    protected String associationId;
    protected String associationName;
    protected String associatedTopicName;
    
    public String getAssociationId() {
      return associationId;
    }

    public String getAssociationName() {
      return associationName;
    }

    public String getAssociatedTopicName() {
      return associatedTopicName;
    }
    
  }
  
  public class WebTerm {

    WebClassification wc;
    Term term;
    int sequenceId;
    List candidates;
    
    WebTerm(WebClassification wc, Term term, int sequenceId) {
      this.wc = wc;
      this.term = term;
      this.sequenceId = sequenceId;
      this.candidates = new ArrayList();
      Object[] variants = term.getVariantsByRank();
      for (int i=0; i < variants.length; i++) {
        Collection cs = wc.tmc.getTopics((Variant)variants[i]);
        Iterator iter = cs.iterator();
        while (iter.hasNext()) {
          Object c = iter.next();
          if (!candidates.contains(c))
            candidates.add(c);
        }
      }
    }

    public String getId() {
      return term.getStem();
    }

    public int getSequenceId() {
      return sequenceId;
    }

    public boolean getSelected() {
      String id = getId();
      
      String[] selected = request.getParameterValues("selected");
      if (selected != null && selected.length > 0) {
        for (int i=0; i < selected.length; i++) {
          if (id.equals(selected[i])) return true;
        }
      }
      if (selected == null || selected.length == 0)
        return getHasCandidateTopics() && getDefaultAssociationType() != null;
      else
        return false;
    }
    
    public String getNameField() {
      return "cn-" + getId();
    }
    
    public String getNameValue() {
      String value = request.getParameter(getNameField());
      if (value != null)
        return value;
      else
        return term.getPreferredName();
    }
    
    public String getNameTitle() {
      return term.getStem() + ": " + StringUtils.join(term.getVariants(), " | ");
    }

    public double getScore() {
      return term.getScore();
    }

    public double getScorePercent() {
      return (100d*term.getScore());
    }

    public int getOccurrences() {
      return term.getOccurrences();
    }
    
    public String getCandidateTopicField() {
      return "ct-" + getId();
    }
    
    public boolean getHasCandidateTopics() {
      return !candidates.isEmpty();
    }

    public Collection getCandidateTopics() {
      return candidates;
    }
    
    public String getAssociationTypeField() {
      return "at-" + getId();
    }
    
    public TopicMapAnalyzer.AssociationType getDefaultAssociationType() {
      // select association type if score is lower than term score and lower than highest score
      boolean hasCandidates = getHasCandidateTopics();
      double tscore = getScore();
      TopicMapAnalyzer.AssociationType ttype = null;
      double ttscore = -1.0d;
      
      //! System.out.println("|----------------------------");
      Iterator iter = wc.getAssociationTypes().iterator();
      while (iter.hasNext()) {
        TopicMapAnalyzer.AssociationType xtype = (TopicMapAnalyzer.AssociationType)iter.next();
        double xscore = xtype.getScoreThreshold(hasCandidates);

        //! System.out.println("AT: " + xtype.atype + " " + xscore + "->" + tscore);

        if (xscore >= 0 && tscore > xscore &&
            (ttype == null || ttscore < xscore)) {
          ttype = xtype;
          ttscore = ttype.getScoreThreshold(hasCandidates);
        }
        //! System.out.println("    " + (ttype == null ? null : ttype.atype) + " " + (ttype == null ? -1.0d : ttype.getScoreThreshold(hasCandidates)));
      }
      //! System.out.println("TT: " + ttype + " " + (ttype == null ? -1.0d : ttype.getScoreThreshold(hasCandidates)));
      //! System.out.println("|----------------------------");
      return ttype;
    }
 
  }
  
}
