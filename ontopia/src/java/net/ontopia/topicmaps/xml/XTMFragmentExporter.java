
// $Id: XTMFragmentExporter.java,v 1.26 2008/06/13 08:17:58 geir.gronmo Exp $

package net.ontopia.topicmaps.xml;

import java.util.Set;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import org.xml.sax.DocumentHandler;
import org.xml.sax.SAXException;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.StringUtils;
import net.ontopia.utils.CompactHashSet;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.infoset.core.LocatorIF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PUBLIC: Exports partial topic maps using an approach inspired
 * by the XTM Fragment Interchange 0.1 specification, but different.
 *
 * <p>Note that the TopicMapFragmentWriterIF implementation is not
 * this class, but XTMTopicMapFragmentWriter.
 *
 * <p><b>WARNING:</b> This class is not thread-safe.
 *
 * @see XTMTopicMapFragmentWriter
 */
public class XTMFragmentExporter extends XTMTopicMapExporter {
  
  public static final String VIRTUAL_URN = "urn:x-oks-virtual:";
  
  protected LocatorHandlerIF locator_handler;
  protected Collection reifiers;   // topics reifying tm constructs (for export)
  protected Set alreadyExported;   // objids topics & assocs already exported
  protected boolean use_local_ids; // see setUseLocalIds
  private String tmid;
  private static Logger log = LoggerFactory.getLogger(XTMTopicMapExporter.class.getName());
  
  /**
   * PUBLIC: Initializes the exporter.
   */
  public XTMFragmentExporter() {
    export_srclocs = true;
    locator_handler = null;
    reifiers = new ArrayList();
    alreadyExported = new CompactHashSet();
  }

  public XTMFragmentExporter(String tmid) {
    this();
    this.tmid = tmid;
  }

  /**
   * EXPERIMENTAL: Sets the locator handler. Currently only used for
   * occurrences.
   */
  public void setLocatorHandler(LocatorHandlerIF locator_handler) {
    this.locator_handler = locator_handler;
  }

  /**
   * PUBLIC: Controls whether or not internal references of the
   * form '#id' will be used. TMRAP cannot use this, whereas when
   * self-contained XTM fragment files are produced this should be
   * used.
   */
  public void setUseLocalIds(boolean use_local_ids) {
    this.use_local_ids = use_local_ids;
  }
   
  /**
   * PUBLIC: Exports an XTM Fragment (complete with root element) to
   * the given DocumentHandler, containing all the topics retrieved
   * from the Iterator. Duplicates do not cause problems.
   */
  public void exportAll(Iterator it, DocumentHandler dh) throws SAXException {    
    startTopicMap(dh);
    exportTopics(it, dh);    
    endTopicMap(dh);
  }

  /**
   * PUBLIC: Exports a set of topics without any wrapping element.
   */
  public void exportTopics(Iterator it, DocumentHandler dh) throws SAXException {
    while (it.hasNext()) {
      TopicIF topic = (TopicIF) it.next();
      if (filterOk(topic))
        writeTopic(topic, dh);
    }
  }

  /**
   * PUBLIC: Exports a set of topic names without any wrapping
   * element.
   */
  public void exportTopicNames(Iterator it, DocumentHandler dh) throws SAXException {
    while (it.hasNext())
      writeTopicNames((TopicIF) it.next(), dh);
  }

  /**
   * PUBLIC: Outputs the <topicMap ...> start tag.
   */
  public void startTopicMap(DocumentHandler dh) throws SAXException {
    reifiers.clear();
    alreadyExported.clear();
    
    dh.startDocument();

    // TOPICMAP

    // Calculate attributes
    atts.clear();
    atts.addAttribute("xmlns", "CDATA", "http://www.topicmaps.org/xtm/1.0/");
    atts.addAttribute("xmlns:xlink", "CDATA", "http://www.w3.org/1999/xlink");
    
    // Output element
    dh.startElement("topicMap", atts);
  }

  /**
   * PUBLIC: Outputs the </topicMap> end tag.
   */
  public void endTopicMap(DocumentHandler dh) throws SAXException {
    while (!reifiers.isEmpty()) {
      Iterator it = reifiers.iterator();
      reifiers = new ArrayList(); // for reifiers found during this export run
      exportTopics(it, dh);
    }
    
    // Close element
    dh.endElement("topicMap");
    dh.endDocument();
  }
  
  // --- Internal stuff

  //--------------------------------------------------------------------
  // Methods used on Topics
  //--------------------------------------------------------------------


  protected void writeTopic(TopicIF topic, DocumentHandler dh)
    throws SAXException {

    String objid = topic.getObjectId();
    if (alreadyExported.contains(objid))
      return;
    alreadyExported.add(objid);
    
    super.writeTopic(topic, dh);    

    Iterator it = topic.getRoles().iterator();
    while (it.hasNext()) {
      AssociationRoleIF role = (AssociationRoleIF) it.next();
      if (filterOk(role.getAssociation()))
        writeAssociation(role.getAssociation(), dh);
    }
  }

  protected void writeTopicNames(TopicIF topic, DocumentHandler dh)
    throws SAXException {

    String objid = topic.getObjectId();
    if (alreadyExported.contains(objid))
      return;
    alreadyExported.add(objid);
    
    atts.clear();    
    addId(atts, topic);
    
    dh.startElement("topic", atts);

    if (topic.getSubjectLocators().size() > 0 ||
        topic.getSubjectIdentifiers().size() > 0 ||
        (topic.getItemIdentifiers().size() > 0 && export_srclocs)) 
      writeSubjectIdentity(topic, dh);
   
    if ((topic.getTopicNames()).size() > 0)
      writeTopicNames(topic.getTopicNames(),dh);
    
    dh.endElement("topic");
    
  }
  
  protected void writeTopicNames(Collection names, DocumentHandler dh)
    throws SAXException {
    //Get names, and sort the out.
    Iterator iter = names.iterator();
    while (iter.hasNext()) {
      TopicNameIF basename = (TopicNameIF)iter.next();
      
      dh.startElement("baseName", EMPTY_ATTR_LIST);
      if ((basename.getScope()).size() > 0)
        writeScope(basename.getScope(), dh);
      
      //Write baseNameString
      dh.startElement("baseNameString", EMPTY_ATTR_LIST);
      if (basename.getValue() != null && !basename.getValue().equals("")) {
        char[] chars = basename.getValue().toCharArray();
        dh.characters(chars, 0, chars.length);
      }
      dh.endElement("baseNameString");

      //Write variant
      if ((basename.getVariants()).size() > 0)
        writeVariants(basename.getVariants(), dh);
      dh.endElement("baseName");
    }
  }



  protected void writeTopicRef(TopicIF topic, DocumentHandler dh)
    throws SAXException {

    // FIXME: non-URI locators
    if (!topic.getSubjectLocators().isEmpty()) {
      LocatorIF sub = (LocatorIF) topic.getSubjectLocators().iterator().next();
      atts.clear();
      atts.addAttribute("xlink:href", "CDATA", sub.getAddress());
      dh.startElement("resourceRef", atts);
      dh.endElement("resourceRef");
      return;
    }

    if (!topic.getSubjectIdentifiers().isEmpty()) {
      LocatorIF ind = (LocatorIF) topic.getSubjectIdentifiers().iterator().next();
      atts.clear();
      atts.addAttribute("xlink:href", "CDATA", ind.getAddress());
      dh.startElement("subjectIndicatorRef", atts);
      dh.endElement("subjectIndicatorRef");
      return;
    }


    LocatorIF base = topic.getTopicMap().getStore().getBaseAddress();
    String reference = null;

    // try to find an ID for this topic
    if (base != null) {
      String baseaddr = base.getExternalForm();
      Iterator it = topic.getItemIdentifiers().iterator();
      while (it.hasNext()) {
        LocatorIF loc = (LocatorIF) it.next();
        reference = loc.getExternalForm();
        if (!use_local_ids)
          break;
        
        if (reference.startsWith(baseaddr)) {
          reference = reference.substring(baseaddr.length()); // gives "#id"
          break;
        }
      }
    }

    // if no ID or source locator found, create a virtual one,
    // but do not assign it to the object
    if (reference == null)
      reference = makeVirtualReference(topic);

    // now we can output
    atts.clear();
    atts.addAttribute("xlink:href", "CDATA", reference);
    dh.startElement("topicRef", atts);
    dh.endElement("topicRef");
  }
  
  protected void writeSubjectIdentity(TopicIF topic, DocumentHandler dh)
    throws SAXException {
    dh.startElement("subjectIdentity", EMPTY_ATTR_LIST);

    boolean identityFound = false;

    // Subject address(es)
    Iterator it = topic.getSubjectLocators().iterator();
    while (it.hasNext()) {
      LocatorIF subject = (LocatorIF) it.next();
      String notation = subject.getNotation();
      if (notation != null && notation.equals("URI")) {
        atts.clear();
        atts.addAttribute("xlink:href", "CDATA",
                          subject.getAddress());
        dh.startElement("resourceRef", atts);
        dh.endElement("resourceRef");
        identityFound = true;
      } else
        reportInvalidLocator(subject);
    }

    // Subject indicators
    it = topic.getSubjectIdentifiers().iterator();
    while (it.hasNext()) {
      LocatorIF indicator = (LocatorIF) it.next();
      String notation = indicator.getNotation();
      if (notation != null && notation.equals("URI")) {
        atts.clear();
        String ref = getSubjectIndicatorRef(topic, indicator);
        atts.addAttribute("xlink:href", "CDATA", ref);
        dh.startElement("subjectIndicatorRef", atts);
        dh.endElement("subjectIndicatorRef");

        if (ref.startsWith("#")) {
          atts.clear();
          atts.addAttribute("xlink:href", "CDATA", indicator.getAddress());
          dh.startElement("subjectIndicatorRef", atts);
          dh.endElement("subjectIndicatorRef");
        }
        identityFound = true;
      } else
        reportInvalidLocator(indicator);
    }

    // Source locators (only if configured)
    if (export_srclocs) {
      it = topic.getItemIdentifiers().iterator();
      if (it.hasNext()){
        while (it.hasNext()) {
          LocatorIF srcloc = (LocatorIF) it.next();
          String notation = srcloc.getNotation();
          if (notation != null && notation.equals("URI")) {
            atts.clear();
            atts.addAttribute("xlink:href", "CDATA",
                              srcloc.getAddress());
            dh.startElement("topicRef", atts);
            dh.endElement("topicRef");
            identityFound = true;
          } else
            reportInvalidLocator(srcloc);
        }
      }     
    }

    // output virtual source locator if no other identity found
    if (!identityFound) {
      String reference = makeVirtualReference(topic);
      
      atts.clear();
      atts.addAttribute("xlink:href", "CDATA", reference);
      dh.startElement("topicRef", atts);
      dh.endElement("topicRef");
    }
    
    dh.endElement("subjectIdentity");
  }



  protected void writeVariants(Collection variants, DocumentHandler dh)
    throws SAXException {
    
    Iterator iter = variants.iterator();
    while (iter.hasNext()) {
      VariantNameIF var = (VariantNameIF)iter.next();
      
      dh.startElement("variant", EMPTY_ATTR_LIST);

      // write parameters      
      if (var.getScope().size() > 0)
        writeParameters(var, dh);
      else {
        // FIXME : XTM violation!
      }
      
      // write variantName
      writeVariantName(var, dh);
      
      dh.endElement("variant");
    }
  }


  protected void writeOccurrences(Collection occurrences, DocumentHandler dh)
    throws SAXException {
    Iterator iter = occurrences.iterator();
    while (iter.hasNext()) {
      OccurrenceIF occr = (OccurrenceIF)iter.next();

      TopicIF reifier = occr.getReifier();
      if (reifier != null) {
        reifiers.add(reifier); // make sure this is exported, too
      }

      atts.clear();
      addId(atts, occr);
      dh.startElement("occurrence", atts);

      if (occr.getType() != null)
        writeInstanceOf(occr, dh);
      if (occr.getScope().size() > 0)
        writeScope(occr.getScope(), dh);

      // write resourceRef
      LocatorIF occloc = occr.getLocator();
      if (occloc != null) {
        if (locator_handler != null)
          occloc = locator_handler.process(occloc);
        String notation = occloc.getNotation();
        if (notation != null && notation.equals("URI")) {
          atts.clear();
          atts.addAttribute("xlink:href", "CDATA", occloc.getAddress());
          dh.startElement("resourceRef", atts);
          dh.endElement("resourceRef");
        } else
          reportInvalidLocator(occloc);
      }

      // write resourceData
      else {
        dh.startElement("resourceData", EMPTY_ATTR_LIST);
        if (occr.getValue() != null && !occr.getValue().equals("")) {
          char[] chars = occr.getValue().toCharArray();
          dh.characters(chars, 0, chars.length);
        }
        dh.endElement("resourceData");
      }
      dh.endElement("occurrence");
    }
  }
  

  //--------------------------------------------------------------------
  // Methods used on associations
  //--------------------------------------------------------------------

  protected void writeAssociation(AssociationIF assoc, DocumentHandler dh) throws SAXException {

    String objid = assoc.getObjectId();
    if (alreadyExported.contains(objid))
      return;
    alreadyExported.add(objid);
    
    dh.startElement("association", EMPTY_ATTR_LIST);

    if (assoc.getType() != null)
      writeInstanceOf(assoc, dh);

    if (assoc.getScope().size() > 0)
      writeScope(assoc.getScope(), dh);

    if (assoc.getRoles().size() > 0 )
      writeMembers(assoc, dh);
    
    dh.endElement("association");
  }

  protected void writeMembers(AssociationIF assoc, DocumentHandler dh)
    throws SAXException {
    Iterator iter = assoc.getRoles().iterator();
    while (iter.hasNext()){
      AssociationRoleIF role = (AssociationRoleIF)iter.next();
            
      dh.startElement("member", EMPTY_ATTR_LIST);
      
      if (role.getType() != null) writeRoleSpec(role.getType(), dh);
      if (role.getPlayer() != null) writeTopicRef(role.getPlayer(), dh);
      dh.endElement("member");
    }
  }


  //-------------------------------------------------------------------
  // Virtual locators
  //--------------------------------------------------------------------
  
  public static boolean isVirtualReference(String address) {
    return address.startsWith(VIRTUAL_URN);
  }
  
  public static String resolveVirtualReference(String address, String tmid) {
    String topicMapIndex = XTMFragmentExporter.sourceTopicMapFromVirtualReference(address);
    if (!topicMapIndex.equals(tmid))
      throw new OntopiaRuntimeException("Topic map IDs do not match, requested=" + topicMapIndex +
                                        ", current=" + tmid);
    
    return address.substring(address.indexOf('#') + 1);
  }

  public static String sourceTopicMapFromVirtualReference(String address) {
    int index = address.indexOf('#');
    return address.substring(XTMFragmentExporter.VIRTUAL_URN.length(), index);
  }
  
  protected String makeVirtualReference(TopicIF topic) {
    String topicmap_id = tmid;
    if (topicmap_id == null) {
      // get hold of topic map reference and get topic map id from that
      TopicMapIF tm = topic.getTopicMap();
      TopicMapStoreIF store = tm.getStore();
      TopicMapReferenceIF ref = store.getReference();
      if (ref != null) {
        topicmap_id = ref.getId();
        //! if (topicmap_id == null)
        //!   throw new OntopiaRuntimeException("Not able to get the topic map reference from topic: " + topic);
      }
    }
    if (topicmap_id == null)
      topicmap_id = "";
    return makeVirtualReference(topic, topicmap_id);
  }

  public static String makeVirtualReference(TopicIF topic, String topicmap_id) {
    return VIRTUAL_URN + topicmap_id + "#" + topic.getObjectId();
  }

  //--------------------------------------------------------------------
  // LocatorHandlerIF
  //--------------------------------------------------------------------

  /**
   * EXPERIMENTAL: Handler class used for processing locators.
   */
  public interface LocatorHandlerIF {
    public LocatorIF process(LocatorIF locator);
  }  

}
