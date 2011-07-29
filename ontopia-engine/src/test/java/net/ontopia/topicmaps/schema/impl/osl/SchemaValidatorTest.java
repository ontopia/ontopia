
package net.ontopia.topicmaps.schema.impl.osl;

import java.io.IOException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.schema.core.*;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SchemaValidatorTest extends AbstractSchemaTestCase {
  protected TopicMapIF topicmap;
  protected TopicMapBuilderIF builder;
  
  @Before
  public void setUp() {
    InMemoryTopicMapStore store = new InMemoryTopicMapStore();
    topicmap = store.getTopicMap();   
    builder = topicmap.getBuilder();

    try {
      store.setBaseAddress(new URILocator("http://test.ontopia.net"));
    } catch (java.net.MalformedURLException e) {
      Assert.fail("couldn't create URI");
    }
  }

  // --- Helper methods

  protected LocatorIF getRelative(String relative) {
    return topicmap.getStore().getBaseAddress().resolveAbsolute(relative);
  }

  protected TopicIF makeType(String relative) {
    TopicIF type = builder.makeTopic();
    type.addItemIdentifier(getRelative(relative));
    return type;
  }

  
  protected void validate(SchemaIF schema, TMObjectIF container,
                          TMObjectIF offender) {
    SchemaValidatorIF validator = schema.getValidator();

    try {
      validator.validate(topicmap);
      Assert.fail("invalid topicmap validated with no errors");
    } catch (SchemaViolationException e) {
      Assert.assertTrue("wrong container when validating: " + e.getContainer(),
             e.getContainer() == container);
      Assert.assertTrue("wrong offender when validating: " + e.getOffender(),
             e.getOffender() == offender);
    }
  }
  
  protected void validate(SchemaIF schema) {
    SchemaValidatorIF validator = schema.getValidator();

    try {
      validator.validate(topicmap);
    } catch (SchemaViolationException e) {
      Assert.fail("valid topic map did not validate: " + e);
    }
  }
  
  // --- Test cases

  @Test
  public void testMinimumTopicNames() throws IOException, SchemaSyntaxException{
    OSLSchema schema = (OSLSchema) readSchema("in", "basename.xml");

    TopicIF type = makeType("#something");
    TopicIF instance = builder.makeTopic();
    instance.addType(type);

    validate(schema, instance, null);
  }  

  @Test
  public void testMinimumTopicNamesValid() throws IOException, SchemaSyntaxException{
    OSLSchema schema = (OSLSchema) readSchema("in", "basename.xml");

    TopicIF type = makeType("#something");
    TopicIF theme = makeType("#something-else");
    TopicIF instance = builder.makeTopic();
    instance.addType(type);

    TopicNameIF basename = builder.makeTopicName(instance, "");
    basename.addTheme(theme);

    validate(schema);
  }  

  @Test
  public void testMinimumTopicNamesUnconstrained() throws IOException, SchemaSyntaxException{
    OSLSchema schema = (OSLSchema) readSchema("in", "basename-unc.xml");

    TopicIF type = makeType("#something");
    TopicIF instance = builder.makeTopic();
    instance.addType(type);

    validate(schema, instance, null);
  }  

  @Test
  public void testMinimumTopicNamesValidUnconstrained() throws IOException, SchemaSyntaxException{
    OSLSchema schema = (OSLSchema) readSchema("in", "basename-unc.xml");

    TopicIF type = makeType("#something");
    TopicIF theme = makeType("#something-else");
    TopicIF instance = builder.makeTopic();
    instance.addType(type);
    builder.makeTopicName(instance, "");
    TopicNameIF basename = builder.makeTopicName(instance, "");
    basename.addTheme(theme);

    validate(schema);
  }  

  @Test
  public void testMinimumOccurrences() throws IOException, SchemaSyntaxException {
    OSLSchema schema = (OSLSchema) readSchema("in", "occurrence.xml");

    TopicIF type = makeType("#something");
    TopicIF instance = builder.makeTopic();
    instance.addType(type);

    validate(schema, instance, null);
  }  

  @Test
  public void testMinimumTopicRoles() throws IOException, SchemaSyntaxException {
    OSLSchema schema = (OSLSchema) readSchema("in", "trole.xml");

    TopicIF type = makeType("#type");
    TopicIF instance = builder.makeTopic();
    instance.addType(type);
    validate(schema, instance, null);
  }  

  @Test
  public void testMinimumAssociationRoles() throws IOException, SchemaSyntaxException {
    OSLSchema schema = (OSLSchema) readSchema("in", "arole.xml");
    
    AssociationIF instance = builder.makeAssociation(makeType("#type"));
    validate(schema, instance, null);
  }  
  
  @Test
  public void testExternalOccurrence() throws IOException, SchemaSyntaxException {
    OSLSchema schema = (OSLSchema) readSchema("in", "extocc.xml");

    TopicIF type = makeType("#something");
    TopicIF instance = builder.makeTopic();
    instance.addType(type);

    TopicIF occtype = makeType("#occtype");
    OccurrenceIF occ = builder.makeOccurrence(instance, occtype, "Hubba bubba");

    validate(schema, instance, occ);
  }  

  @Test
  public void testInternalOccurrence() throws IOException, SchemaSyntaxException {
    OSLSchema schema = (OSLSchema) readSchema("in", "intocc.xml");

    TopicIF type = makeType("#something");
    TopicIF instance = builder.makeTopic();
    instance.addType(type);

    TopicIF occtype = makeType("#occtype");
    OccurrenceIF occ = builder.makeOccurrence(instance, occtype, getRelative("http://www.ontopia.net/"));

    validate(schema, instance, occ);
  }  

  @Test
  public void testIllegalOccurrence() throws IOException, SchemaSyntaxException {
    OSLSchema schema = (OSLSchema) readSchema("in", "intocc.xml");

    TopicIF type = makeType("#something");
    TopicIF instance = builder.makeTopic();
    instance.addType(type);

    TopicIF occtype = makeType("#illegalocctype");
    OccurrenceIF occ = builder.makeOccurrence(instance, occtype, "This occurrence is internal, but of the wrong type.");

    validate(schema); // topic class is loose, so this is OK
  }  
  
  @Test
  public void testIllegalOccurrenceStrict() throws IOException, SchemaSyntaxException {
    OSLSchema schema = (OSLSchema) readSchema("in", "intocc-strict.xml");

    TopicIF type = makeType("#something");
    TopicIF instance = builder.makeTopic();
    instance.addType(type);

    TopicIF occtype = makeType("#illegalocctype");
    OccurrenceIF occ = builder.makeOccurrence(instance, occtype, "This occurrence is internal, but of the wrong type.");

    validate(schema, instance, occ); // topic class is strict, so fail
  }  

  @Test
  public void testNothingOccurrence() throws IOException, SchemaSyntaxException {
    OSLSchema schema = (OSLSchema) readSchema("in", "intocc.xml");

    TopicIF type = makeType("#something");
    TopicIF instance = builder.makeTopic();
    instance.addType(type);

    TopicIF occtype = makeType("#occtype");
    OccurrenceIF occ = builder.makeOccurrence(instance, occtype, "");

    validate(schema); // question is: should this validate?
  }  

  @Test
  public void testLevelOfScopeMatchingExact() throws IOException, SchemaSyntaxException {
    OSLSchema schema = (OSLSchema) readSchema("in", "scopelevel-exact.xml");

    TopicIF type = makeType("#something");
    TopicIF instance = builder.makeTopic();
    instance.addType(type);

    TopicIF occscope = makeType("#occ_scope");

    TopicIF occtype = makeType("#occtype");
    OccurrenceIF occ = builder.makeOccurrence(instance, occtype, "");

    occ.addTheme(occscope);

    validate(schema);
  }

  @Test
  public void testLevelOfScopeMatchingSuperset() throws IOException, SchemaSyntaxException {
    OSLSchema schema = (OSLSchema) readSchema("in", "scopelevel-superset.xml");

    TopicIF type = makeType("#something");
    TopicIF instance = builder.makeTopic();
    instance.addType(type);

    TopicIF occscope = makeType("#occ_scope1");

    TopicIF occtype = makeType("#occtype");
    OccurrenceIF occ = builder.makeOccurrence(instance, occtype, "");
    
    occ.addTheme(occscope);

    validate(schema);
  }

  @Test
  public void testLevelOfScopeMatchingSubset() throws IOException, SchemaSyntaxException {
    OSLSchema schema = (OSLSchema) readSchema("in", "scopelevel-subset.xml");

    TopicIF type = makeType("#something");
    TopicIF instance = builder.makeTopic();
    instance.addType(type);

    TopicIF occscope1 = makeType("#occ_scope1");
    TopicIF occscope2 = makeType("#occ_scope2");
    TopicIF occscope3 = makeType("#occ_scope3");


    TopicIF occtype = makeType("#occtype");
    OccurrenceIF occ = builder.makeOccurrence(instance, occtype, "");

    occ.addTheme(occscope1);
    occ.addTheme(occscope2);
    occ.addTheme(occscope3);

    validate(schema);
  }

  @Test
  public void testBug430() throws IOException, SchemaSyntaxException {

    topicmap = ImportExportUtils.getReader(TestFileUtils.getTestInputFile(testdataDirectory, "topicmaps", "bug430.ltm")).read();
    OSLSchema schema = (OSLSchema) readSchema("in", "bug430.xml");
    validate(schema);
  }

  @Test
  public void testVariantRequired() throws IOException, SchemaSyntaxException {
    OSLSchema schema = (OSLSchema) readSchema("in", "variant.xml");

    TopicIF type = makeType("#type");
    TopicIF instance = builder.makeTopic();
    instance.addType(type);

    TopicNameIF bn = builder.makeTopicName(instance, "Testtopic");

    validate(schema, bn, null);
  }  
}
