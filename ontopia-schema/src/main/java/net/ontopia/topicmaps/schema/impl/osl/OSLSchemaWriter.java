/*
 * #!
 * Ontopia OSL Schema
 * #-
 * Copyright (C) 2001 - 2014 The Ontopia Project
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

package net.ontopia.topicmaps.schema.impl.osl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.schema.core.CardinalityConstraintIF;
import net.ontopia.topicmaps.schema.core.SchemaIF;
import net.ontopia.topicmaps.schema.core.SchemaWriterIF;
import net.ontopia.topicmaps.schema.core.TMObjectMatcherIF;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.xml.PrettyPrinter;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * PUBLIC: Writes out an OSL schema using the OSL schema syntax.
 */
public class OSLSchemaWriter implements SchemaWriterIF {
  protected Writer out;
  protected String encoding;

  protected LocatorIF base;
  protected AttributesImpl EMPTY_ATTR_LIST;
  
  /**
   * PUBLIC: Creates a schema writer bound to the given Writer object.
   * @param encoding The encoding in which the schema will be written.
   */
  public OSLSchemaWriter(Writer out, String encoding) {
    this.out = out;
    this.encoding = encoding;
    
    EMPTY_ATTR_LIST = new AttributesImpl();
  }
  
  /**
   * PUBLIC: Creates a schema writer bound to the given file.
   * @param encoding The encoding in which to write the file.
   */
  public OSLSchemaWriter(File file, String encoding)
    throws java.io.IOException {
    
    this.out = new OutputStreamWriter(new FileOutputStream(file), encoding);
    this.encoding = encoding;
    
    EMPTY_ATTR_LIST = new AttributesImpl();
  }

  // --- SchemaWriterIF methods
  
  /**
   * PUBLIC: Writes the schema.
   */
  @Override
  public void write(SchemaIF schema) throws java.io.IOException {
    PrintWriter print = new PrintWriter(out);
    try {
      base = schema.getAddress();
      export((OSLSchema) schema, new PrettyPrinter(print, encoding));
    } catch (SAXException e) {
      throw new net.ontopia.utils.OntopiaRuntimeException(e);
    }
    print.flush();
  }

  // --- Export methods

  protected void export(OSLSchema schema, ContentHandler dh)
    throws SAXException {
    dh.startElement("", "", "tm-schema", getAttributes(schema.isStrict(),
                                               "match", "strict", "loose"));

    Iterator it = schema.getRuleSets().iterator();
    while (it.hasNext()) 
      export((RuleSet) it.next(), dh);

    it = schema.getTopicClasses().iterator();
    while (it.hasNext()) 
      export((TopicClass) it.next(), dh);

    it = schema.getAssociationClasses().iterator();
    while (it.hasNext()) 
      export((AssociationClass) it.next(), dh);
    
    dh.endElement("", "", "tm-schema");
  }

  protected void export(RuleSet ruleset, ContentHandler dh)
    throws SAXException {
    dh.startElement("", "", "ruleset", getAttributes("id", ruleset.getId()));
    export((TopicConstraintCollection) ruleset, dh);
    dh.endElement("", "", "ruleset");
  }

  protected void export(TopicClass klass, ContentHandler dh)
    throws SAXException {
    AttributesImpl atts =
      (AttributesImpl) getAttributes(klass.isStrict(), 
                                        "match", "strict", "loose");
    if (klass.getId() != null)
      atts.addAttribute("", "", "id", "CDATA", klass.getId());
    dh.startElement("", "", "topic", atts);

    exportInstanceOf(klass.getTypeSpecification(), dh);

    // otherClass
    Iterator it = klass.getOtherClasses().iterator();
    while (it.hasNext()) {
      TypeSpecification typespec = (TypeSpecification) it.next();
      dh.startElement("", "", "otherClass", EMPTY_ATTR_LIST);
      export(typespec.getClassMatcher(), dh);
      dh.endElement("", "", "otherClass");
    }
    
    // superclass
    if (klass.getSuperclass() != null) {
      TopicClass superclass = klass.getSuperclass();
      // FIXME: what if no id?
      dh.startElement("", "", "superclass", getAttributes("ref", superclass.getId()));
      dh.endElement("", "", "superclass");
    }
    
    export((TopicConstraintCollection) klass, dh);
    
    dh.endElement("", "", "topic");
  }

  protected void export(AssociationClass klass, ContentHandler dh)
    throws SAXException {
    dh.startElement("", "", "association", EMPTY_ATTR_LIST);

    exportInstanceOf(klass.getTypeSpecification(), dh);
    Iterator it = klass.getRoleConstraints().iterator();
    while (it.hasNext()) 
      export((AssociationRoleConstraint) it.next(), dh);
    
    dh.endElement("", "", "association");
  }

  protected void export(TopicConstraintCollection constraint, ContentHandler dh)
    throws SAXException {

    Iterator it = constraint.getSubRules().iterator();
    while (it.hasNext()) {
      RuleSet subrule = (RuleSet) it.next();
      emptyElement(dh, "ruleref", getAttributes("rule", subrule.getId()));
    }
    
    it = constraint.getTopicNameConstraints().iterator();
    while (it.hasNext()) 
      export((TopicNameConstraint) it.next(), dh);

    it = constraint.getOccurrenceConstraints().iterator();
    while (it.hasNext()) 
      export((OccurrenceConstraint) it.next(), dh);

    it = constraint.getRoleConstraints().iterator();
    while (it.hasNext()) 
      export((TopicRoleConstraint) it.next(), dh);
  }

  protected void export(TopicNameConstraint constraint, ContentHandler dh)
    throws SAXException {
    dh.startElement("", "", "baseName", getMinMax(constraint));

    exportScope(constraint, dh);

    Iterator it = constraint.getVariantConstraints().iterator();
    while (it.hasNext()) 
      export((VariantConstraint) it.next(), dh);
    
    dh.endElement("", "", "baseName");
  }

  protected void export(VariantConstraint constraint, ContentHandler dh)
    throws SAXException {
    dh.startElement("", "", "variant", getMinMax(constraint));

    exportScope(constraint, dh);
    
    dh.endElement("", "", "variant");
  }

  protected void export(OccurrenceConstraint constraint, ContentHandler dh)
    throws SAXException {
    AttributesImpl atts = (AttributesImpl) getMinMax(constraint);
    String value = "either";
    if (constraint.getInternal() == OccurrenceConstraint.RESOURCE_INTERNAL)
      value = "yes";
    else if (constraint.getInternal() == OccurrenceConstraint.RESOURCE_EXTERNAL)
      value = "no";
    atts.addAttribute("", "", "internal", "CDATA", value);
    dh.startElement("", "", "occurrence", atts);

    exportInstanceOf(constraint.getTypeSpecification(), dh);
    exportScope(constraint, dh);
    
    dh.endElement("", "", "occurrence");
  }

  protected void export(TopicRoleConstraint constraint, ContentHandler dh)
    throws SAXException {
    dh.startElement("", "", "playing", getMinMax(constraint));

    exportInstanceOf(constraint.getTypeSpecification(), dh);
    Iterator it = constraint.getAssociationTypes().iterator();
    if (it.hasNext()) {
      dh.startElement("", "", "in", EMPTY_ATTR_LIST);
      while (it.hasNext()) {
        TypeSpecification spec = (TypeSpecification) it.next();
        exportInstanceOf(spec, dh);
      }
      dh.endElement("", "", "in");
    }
    
    dh.endElement("", "", "playing");
  }
  
  protected void export(AssociationRoleConstraint constraint, ContentHandler dh)
    throws SAXException {
    dh.startElement("", "", "role", getMinMax(constraint));

    exportInstanceOf(constraint.getTypeSpecification(), dh);
    Iterator it = constraint.getPlayerTypes().iterator();
    while (it.hasNext()) {
      TypeSpecification spec = (TypeSpecification) it.next();
      dh.startElement("", "", "player", getAttributes(spec.getSubclasses(),
                                              "subclasses", "yes", "no"));
      export(spec.getClassMatcher(), dh);
      dh.endElement("", "", "player");
    }
    
    dh.endElement("", "", "role");
  }

  protected void export(TMObjectMatcherIF matcher, ContentHandler dh)
    throws SAXException {

    if (matcher == null)
      return;
    
    else if (matcher instanceof InternalTopicRefMatcher) 
      emptyElement(dh, "internalTopicRef", getAttributes("href", ((InternalTopicRefMatcher) matcher).getRelativeURI()));

    else if (matcher instanceof SourceLocatorMatcher)
      emptyElement(dh, "topicRef", getAttributes("href", getRelativeLocator(base, ((SourceLocatorMatcher) matcher).getLocator())));

    else if (matcher instanceof AnyTopicMatcher) 
      emptyElement(dh, "any", EMPTY_ATTR_LIST);

    else if (matcher instanceof SubjectIndicatorMatcher) 
      emptyElement(dh, "subjectIndicatorRef", getAttributes("href", getRelativeLocator(base, ((SubjectIndicatorMatcher) matcher).getLocator())));

    else if (matcher instanceof TypeSpecification)
      exportInstanceOf((TypeSpecification) matcher, dh);
    
    else
      throw new OntopiaRuntimeException("INTERNAL: Unknown matcher " + matcher);    
  }

  protected void exportScope(ScopedConstraintIF constraint, ContentHandler dh)
    throws SAXException {
    if (constraint.getScopeSpecification() == null)
      return;
    
    int match = constraint.getScopeSpecification().getMatch();
    if (match == ScopeSpecification.MATCH_SUPERSET)
      dh.startElement("", "", "scope", getAttributes("match", "superset"));
    else if (match == ScopeSpecification.MATCH_SUBSET)
      dh.startElement("", "", "scope", getAttributes("match", "subset"));
    else
      dh.startElement("", "", "scope", EMPTY_ATTR_LIST);

    exportMatchers(constraint.getScopeSpecification().getThemeMatchers(), dh);
    dh.endElement("", "", "scope");   
  }

  protected void exportInstanceOf(TypeSpecification spec, ContentHandler dh)
    throws SAXException {
    dh.startElement("", "", "instanceOf", getAttributes(spec.getSubclasses(),
                                                "subclasses", "yes", "no"));
    export(spec.getClassMatcher(), dh);
    dh.endElement("", "", "instanceOf");   
  }

  protected void exportMatchers(Collection matchers, ContentHandler dh)
    throws SAXException {

    Iterator it = matchers.iterator();
    while (it.hasNext()) 
      export((TMObjectMatcherIF) it.next(), dh);
  }
  
  // --- Internal helpers

  protected Attributes getAttributes(String name, String value) {
    AttributesImpl atts = new AttributesImpl();
    if (value != null)
      atts.addAttribute("", "", name, "CDATA", value);
    return atts;
  }
  
  protected Attributes getAttributes(boolean setting, String name,
                                        String tvalue, String fvalue) {
    AttributesImpl atts = new AttributesImpl();
    if (setting)
      atts.addAttribute("", "", name, "CDATA", tvalue);
    else
      atts.addAttribute("", "", name, "CDATA", fvalue);
    return atts;
  }
  
  protected Attributes getMinMax(CardinalityConstraintIF constraint) {
    AttributesImpl atts = new AttributesImpl();

    if (constraint.getMinimum() != 0)
      atts.addAttribute("", "", "min", "CDATA",
                        Integer.toString(constraint.getMinimum()));

    if (constraint.getMaximum() != CardinalityConstraintIF.INFINITY)
      atts.addAttribute("", "", "max", "CDATA",
                        Integer.toString(constraint.getMaximum()));
    
    return atts;
  }

  protected void emptyElement(ContentHandler dh, String elem,
                              Attributes atts) throws SAXException {
    dh.startElement("", "", elem, atts);
    dh.endElement("", "", elem);
  }

  protected String getRelativeLocator(LocatorIF base, LocatorIF relative) {
    if (base == null || !base.getNotation().equals(relative.getNotation()))
      return relative.getAddress();
    
    String basea = base.getAddress();
    String relativea = relative.getAddress();
    if (relativea.startsWith(basea))
      return relativea.substring(basea.length());
    else
      return relativea;
  }
}
