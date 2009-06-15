
// $Id: OSLSchemaContentHandler.java,v 1.11 2008/06/12 14:37:22 geir.gronmo Exp $

package net.ontopia.topicmaps.schema.impl.osl;

import java.util.Stack;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.xml.sax.Locator;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;
import org.xml.sax.helpers.DefaultHandler;
import net.ontopia.xml.XMLReaderFactoryIF;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.schema.core.TMObjectMatcherIF;
import net.ontopia.topicmaps.schema.core.CardinalityConstraintIF;
import net.ontopia.topicmaps.schema.core.SchemaSyntaxException;

/**
 * INTERNAL: SAX2 content handler used for importing OSL topic map
 * schemas into the schema object model.
 */
public class OSLSchemaContentHandler extends DefaultHandler {
  protected XMLReaderFactoryIF xrfactory;
  protected LocatorIF base_address;
  protected OSLSchema schema;
  protected String curelem;

  protected Locator saxlocator;
  protected Stack openElements;
  protected Stack openObjects;
  protected List forwardrefs;
  
  public OSLSchemaContentHandler(XMLReaderFactoryIF xrfactory,
                                    LocatorIF base_address) {
    this.xrfactory = xrfactory;
    this.base_address = base_address;
  }

  // --- Actual builder implementation

  public void beginElement(String name, Attributes attrs)
    throws java.net.MalformedURLException, SAXException {
    curelem = name;
    
    if (name == "ruleset") {
      OSLSchema parent = getSchema();
      RuleSet ruleset = new RuleSet(parent, attrs.getValue("id"));
      parent.addRuleSet(ruleset);
      openObjects.push(ruleset);

    } else if (name == "tm-schema") {
      if (attrs.getValue("match") != null)
        schema.setIsStrict(getTrueFalse(attrs.getValue("match"), 
                                        "strict", "loose"));

      openObjects.push(schema);
      
    } else if (name == "baseName") {
      TopicConstraintCollection parent = getTopicConstraintCollection();
      TopicNameConstraint constraint = new TopicNameConstraint(parent);
      setMinMax(constraint, attrs);

      parent.addTopicNameConstraint(constraint);
      openObjects.push(constraint);
      
    } else if (name == "variant") {
      TopicNameConstraint parent = getTopicNameConstraint();
      VariantConstraint constraint = new VariantConstraint(parent);
      setMinMax(constraint, attrs);

      parent.addVariantConstraint(constraint);
      openObjects.push(constraint);
      
    } else if (name == "occurrence") {
      TopicConstraintCollection parent = getTopicConstraintCollection();
      OccurrenceConstraint constraint = new OccurrenceConstraint(parent);
      String internal = attrs.getValue("internal");
      if (internal == null || internal.equalsIgnoreCase("either"))
        constraint.setInternal(OccurrenceConstraint.RESOURCE_EITHER);
      else if (internal.equals("yes"))
        constraint.setInternal(OccurrenceConstraint.RESOURCE_INTERNAL);
      else if (internal.equals("no"))
        constraint.setInternal(OccurrenceConstraint.RESOURCE_EXTERNAL);
      else
        throw getException("Attribute 'internal' had illegal value " +
                           internal);
      setMinMax(constraint, attrs);

      parent.addOccurrenceConstraint(constraint);
      openObjects.push(constraint);
      
    } else if (name == "playing") {
      TopicConstraintCollection parent = getTopicConstraintCollection();
      TopicRoleConstraint constraint = new TopicRoleConstraint(parent);
      setMinMax(constraint, attrs);
      parent.addRoleConstraint(constraint);
      openObjects.push(constraint);
      
    } else if (name == "in") {
      TopicRoleConstraint parent = getTopicRoleConstraint();
      openObjects.push(parent);
      
    } else if (name == "scope") {
      ScopedConstraintIF parent = getScopedConstraint();
      ScopeSpecification spec = new ScopeSpecification();

      if (attrs.getValue("match") != null) {
        String match = attrs.getValue("match");
        if (match.equalsIgnoreCase("exact"))
          spec.setMatch(ScopeSpecification.MATCH_EXACT);
        else if (match.equalsIgnoreCase("subset"))
          spec.setMatch(ScopeSpecification.MATCH_SUBSET);
        else if (match.equalsIgnoreCase("superset"))
          spec.setMatch(ScopeSpecification.MATCH_SUPERSET);
        else
          throw getException("Attribute match had illegal value " + match);
      }

      parent.setScopeSpecification(spec);
      openObjects.push(spec);
      
    } else if (name == "topic") {
      OSLSchema parent = getSchema();
      TopicClass topicClass = new TopicClass(parent, attrs.getValue("id"));
      if (attrs.getValue("match") != null) 
        topicClass.setIsStrict(getTrueFalse(attrs.getValue("match"),
                                            "strict", "loose"));
      parent.addTopicClass(topicClass);
      openObjects.push(topicClass);

    } else if (name == "association") {
      OSLSchema parent = getSchema();
      AssociationClass assocClass = new AssociationClass(parent);
      parent.addAssociationClass(assocClass);
      openObjects.push(assocClass);

    } else if (name == "role") {
      AssociationClass parent = getAssociationClass();
      AssociationRoleConstraint constraint =
        new AssociationRoleConstraint(parent);
      setMinMax(constraint, attrs);
      parent.addRoleConstraint(constraint);
      openObjects.push(constraint);

    } else if (name == "player") {
      AssociationRoleConstraint parent = getAssociationRoleConstraint();
      TypeSpecification spec = new TypeSpecification();
      if (attrs.getValue("subclasses") != null)
        spec.setSubclasses(getTrueFalse(attrs.getValue("subclasses"),
                                        "yes", "no"));
      parent.addPlayerType(spec);
      openObjects.push(spec);

    } else if (name == "otherClass") {
      TopicClass parent = getTopicClass();
      TypeSpecification spec = new TypeSpecification();
      if (attrs.getValue("subclasses") != null)
        spec.setSubclasses(getTrueFalse(attrs.getValue("subclasses"),
                                        "yes", "no"));
      parent.addOtherClass(spec);
      openObjects.push(spec);
      
    } else if (name == "instanceOf") {
      TypeSpecification spec = new TypeSpecification();
      if (attrs.getValue("subclasses") != null)
        spec.setSubclasses(getTrueFalse(attrs.getValue("subclasses"),
                                        "yes", "no"));

      if (openElements.peek() == "scope") 
        getScopeSpecification().addThemeMatcher(spec);
      else if (openElements.peek() == "in")
        getTopicRoleConstraint().addAssociationType(spec);
      else 
        getTypedConstraint().setTypeSpecification(spec);
      
      openObjects.push(spec);
      
    } else if (name == "topicRef" || name == "subjectIndicatorRef" ||
               name == "internalTopicRef") {
      String href = attrs.getValue("href");
      if (href == null)
        throw getException("The href attribute on " + name + " is required");

      TMObjectMatcherIF matcher;
      if (name == "topicRef")
        matcher = new SourceLocatorMatcher(base_address.resolveAbsolute(href));
      else if (name == "subjectIndicatorRef")
        matcher = new SubjectIndicatorMatcher(base_address.resolveAbsolute(href));
      else if (name == "internalTopicRef")
        matcher = new InternalTopicRefMatcher(href);
      else
        throw new OntopiaRuntimeException("INTERNAL ERROR!");

      String parent = (String) openElements.peek();
      if (parent == "scope")
        getScopeSpecification().addThemeMatcher(matcher);
      else if (parent == "instanceOf" || parent == "player" ||
               parent == "otherClass")
        getTypeSpecification().setClassMatcher(matcher);
      else
        throw getException(name + " must have scope, instanceOf, otherClass, or player as parent");
      
      openObjects.push(null);
      
    } else if (name == "any") {
      TMObjectMatcherIF matcher = new AnyTopicMatcher();

      String parent = (String) openElements.peek();
      if (parent == "scope")
        getScopeSpecification().addThemeMatcher(matcher);
      else if (parent == "instanceOf" || parent == "player")
        getTypeSpecification().setClassMatcher(matcher);
      else
        throw getException("topicRef must have scope, instanceOf, or player as parent");
      
      openObjects.push(null);
      
    } else if (name == "ruleref") {
      String ruleid = attrs.getValue("rule");
      if (ruleid == null)
        throw getException("rule attribute on ruleref must have a value");
        
      RuleSet rule = schema.getRuleSet(ruleid);
      if (rule == null)
        throw getException("Reference to non-existent rule " + ruleid);
      
      getTopicConstraintCollection().addSubRule(rule);
      openObjects.push(rule);
      
    } else if (name == "superclass") {
      String refid = attrs.getValue("ref");
      if (refid == null)
        throw getException("ref attribute on superclass must have a value");
        
      TopicClass superclass = schema.getTopicClass(refid);
      if (superclass == null) {
        Locator location = null;
        if (saxlocator != null) 
          location = new LocatorImpl(saxlocator);
        forwardrefs.add(new ForwardReference(getTopicClass(), refid,
                                             location));
        // it's OK to let the null pass on
      }
      
      getTopicClass().setSuperclass(superclass);
      openObjects.push(superclass);
      
    } else
      throw getException("Unknown element " + name);

    openElements.push(name);
  }

  public void stopElement(String name) throws SAXException {   
    if ((name == "topic" || name == "role" || name == "playing" ||
         name == "association" || name == "occurrence") &&
         getTypedConstraint().getTypeSpecification() == null)
      throw getException("<" + name + "> element with no type specification");
    else if ((name == "variant" || name == "baseName") &&
             getScopedConstraint().getScopeSpecification() == null)
      throw getException("<" + name + "> element with no scope specification");

    if (name == "variant") {
      VariantConstraint variant = getVariantConstraint();
      inheritScope(variant.getParent(), variant);
    }
    
    openElements.pop();
    openObjects.pop();
  }
  
  // --- ContentHandler implementation

  public void startDocument() {
    schema = new OSLSchema(base_address);
    openElements = new Stack();
    openObjects = new Stack();
    forwardrefs = new ArrayList();
  }

  public void startElement(String name, Attributes attrs) throws SAXException {
    try {
      beginElement(name, attrs);
    }
//     catch (ClassCastException e) { // FIXME: remove!
//       e.printStackTrace();
//       throw e;
//     }
//     catch (NullPointerException e) { // FIXME: remove!
//       e.printStackTrace();
//       throw e;
//     }
    catch (java.net.MalformedURLException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  public void endElement(String name) throws SAXException {
    stopElement(name);
  }

  public void endDocument() throws SAXException {
    Iterator it = forwardrefs.iterator();
    while (it.hasNext()) {
      ForwardReference ref = (ForwardReference) it.next();
      TopicClass superclass = schema.getTopicClass(ref.refid);

      if (superclass == null) {
        String message = "Superclass reference to undefined class";
        throw new SAXException(new SchemaSyntaxException(message,
                                                         ref.location));
      }

      ref.subclass.setSuperclass(superclass);
    }
  }

  // --- For namespace-insistent parsers

  public void startElement(String uri, String lname, String qname, Attributes attrs) throws SAXException {
    startElement(qname, attrs);
  }
  
  public void endElement(String uri, String lname, String qname)
    throws SAXException {
    endElement(qname);
  }

  // --- Retrieving current objects

  public OSLSchema getSchema() {
    return schema;
  }

  protected TopicConstraintCollection getTopicConstraintCollection()
    throws SAXException {
    
    verifyParent("ruleset", "topic");
    return (TopicConstraintCollection) openObjects.peek();
  }
  
  protected TopicClass getTopicClass() throws SAXException {
    verifyParent("topic");
    return (TopicClass) openObjects.peek();
  }

  protected AssociationClass getAssociationClass() throws SAXException { 
    verifyParent("association");
    return (AssociationClass) openObjects.peek();
  }

  protected TopicRoleConstraint getTopicRoleConstraint() throws SAXException {
    verifyParent("playing", "in");
    return (TopicRoleConstraint) openObjects.peek();
  }

  protected AssociationRoleConstraint getAssociationRoleConstraint()
    throws SAXException {
    verifyParent("role");
    return (AssociationRoleConstraint) openObjects.peek();
  }

  protected TopicNameConstraint getTopicNameConstraint()
    throws SAXException {
    verifyParent("baseName");
    return (TopicNameConstraint) openObjects.peek();
  }

  protected VariantConstraint getVariantConstraint()
    throws SAXException {
    verifyParent("variant");
    return (VariantConstraint) openObjects.peek();
  }

  protected ScopedConstraintIF getScopedConstraint()
    throws SAXException {
    String[] parents ={"baseName", "variant", "occurrence", "association"};
    verifyParent(parents);
    return (ScopedConstraintIF) openObjects.peek();
  }
  
  protected TypedConstraintIF getTypedConstraint()
    throws SAXException {
    String[] parents ={"topic", "playing", "in", "occurrence", "association",
                       "role"};
    verifyParent(parents);
    return (TypedConstraintIF) openObjects.peek();
  }

  protected ScopeSpecification getScopeSpecification() 
    throws SAXException {
    verifyParent("scope");
    return (ScopeSpecification) openObjects.peek();
  }

  protected TypeSpecification getTypeSpecification() throws SAXException {
    String[] parents = {"instanceOf", "player", "otherClass"};
    verifyParent(parents);
    return (TypeSpecification) openObjects.peek();
  }

  // --- SAX helpers

  public void setDocumentLocator(Locator locator) {
    saxlocator = locator;
  }
  
  protected void verifyParent(String name) throws SAXException {
    if (!name.equals(openElements.peek()))
      throw getException("Expected parent of element " + curelem + " to be " +
                         name + ", but it was " + openElements.peek());
  }

  protected void verifyParent(String name1, String name2) throws SAXException {
    if (!name1.equals(openElements.peek()) &&
        !name2.equals(openElements.peek()))
      throw getException("Expected parent of element " + curelem +" to be " +
                         name1 + " or " + name2 + ", but it was " +
                         openElements.peek());
  }

  protected void verifyParent(String[] names) throws SAXException {
    String parent = (String) openElements.peek();

    for (int ix = 0; ix < names.length; ix++)
      if (names[ix].equals(parent))
        return;
    
    throw getException("Element " + curelem + " had illegal parent " + parent);
  }
  
  protected SAXException getException(String message) {
    Locator location = null;
    if (saxlocator != null)
      location = new LocatorImpl(saxlocator);

    return new SAXException(new SchemaSyntaxException(message, location));
  }
  
  // --- Population helpers

  protected void inheritScope(ScopedConstraintIF parent,
                              ScopedConstraintIF child) {
    ScopeSpecification spec = parent.getScopeSpecification();
    ScopeSpecification childspec = child.getScopeSpecification();
    
    Iterator it = spec.getThemeMatchers().iterator();
    while (it.hasNext()) {
      TMObjectMatcherIF matcher = (TMObjectMatcherIF) it.next();
      childspec.addThemeMatcher(matcher);
    }
  }
  
  protected void setMinMax(CardinalityConstraintIF constraint,
                           Attributes attrs) throws SAXException {
    String curattr = "min"; // used for exception error message
    try {
      int min = 0;
      if (attrs.getValue("min") != null)
        min = Integer.parseInt(attrs.getValue("min"));

      int max = CardinalityConstraintIF.INFINITY;
      curattr = "max";
      if (attrs.getValue("max") != null) {
        if (!attrs.getValue("max").equalsIgnoreCase("inf")) {
          max = Integer.parseInt(attrs.getValue("max"));
          if (max == -1)
            throw getException("Cannot set max to negative value");
        }
      }

      if (max != -1 && min > max)
        throw getException("min(" + min + ") > max(" + max + ")");
      constraint.setMinimum(min);
      constraint.setMaximum(max);
    } catch (NumberFormatException e) {
      throw getException("Attribute " + curattr + " contained '" +
                         attrs.getValue(curattr) + "', not an integer");
    }
  }

  protected boolean getTrueFalse(String value, String tvalue, String fvalue)
    throws SAXException {
    
    if (value.equalsIgnoreCase(tvalue))
      return true;
    else if (value.equalsIgnoreCase(fvalue))
      return false;
    else
      throw getException("Expected " + tvalue + " or " + fvalue +
                         " as attribute values, found " + value);
  }

  // --- Internal data holder class

  class ForwardReference {
    public TopicClass subclass;
    public String     refid;
    public Locator    location;

    public ForwardReference(TopicClass subclass, String refid, 
                            Locator location) {
      this.subclass = subclass;
      this.refid = refid;
      this.location = location;
    }

    // this is just a data holder, so we don't bother with get methods
  }
  
}
