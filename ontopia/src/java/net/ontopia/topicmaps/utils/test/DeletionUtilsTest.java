
// $Id: DeletionUtilsTest.java,v 1.11 2008/05/23 11:49:49 geir.gronmo Exp $

package net.ontopia.topicmaps.utils.test;

import java.io.*;
import java.util.*;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.test.*;
import net.ontopia.topicmaps.utils.*;
import net.ontopia.utils.*;
import net.ontopia.test.*;

public class DeletionUtilsTest extends AbstractTopicMapTestCase {

  public DeletionUtilsTest(String name) {
    super(name);
  }
    
  public void setUp() {
  }
    
  protected TopicMapIF makeTopicMap() {
    InMemoryTopicMapStore store = new InMemoryTopicMapStore();
    return store.getTopicMap();
  }

  public File[] getFiles() {
    List filenames = new ArrayList();
    String root = AbstractOntopiaTestCase.getTestDirectory();
    String base = root + File.separator + "canonical" + File.separator;
        
    File indir = new File(base + "in" + File.separator);
    if (!indir.exists())
      throw new OntopiaRuntimeException("Directory '" + base + "in" + File.separator +
                            "' does not exist!");
    
    File[] infiles = indir.listFiles();
    if (infiles == null)
      return new File[0];
    return infiles;
  }

  protected boolean filter(String filename) {
    if (filename != null &&
        (filename.endsWith(".ltm") ||
         filename.endsWith(".xtm")))
      return true;
    else
      return false;
  }
  
  // --- Test cases

  public void testTopicMapDeletion() throws Exception {
    File[] infiles = getFiles();
    for (int ix = 0; ix < infiles.length; ix++) {
      String name = infiles[ix].getAbsolutePath();
      if (filter(name)) {
        TopicMapIF tm = makeTopicMap();
        TopicMapImporterIF importer = ImportExportUtils.getImporter(name);
        if (name.endsWith(".xtm"))
          ((net.ontopia.topicmaps.xml.XTMTopicMapReader)importer).setValidation(false);
        importer.importInto(tm);
        clearTopicMap(tm);
        tm.getStore().close();
      }
    }
  }

  public void testTopicDeletion() {
    TopicMapIF topicmap = makeTopicMap();
    TopicMapBuilderIF builder = topicmap.getBuilder();

    TopicIF morituri = builder.makeTopic();
    morituri.remove();

    assertTrue("Topic still connected to topic map", morituri.getTopicMap() == null);
    assertTrue("Topic map not empty", topicmap.getTopics().isEmpty());
  }

  public void testTopicTypeDeletion() {
    TopicMapIF topicmap = makeTopicMap();
    TopicMapBuilderIF builder = topicmap.getBuilder();

    TopicIF morituri = builder.makeTopic();
    TopicIF instance = builder.makeTopic(morituri);

    morituri.remove();

    assertTrue("Topic still connected to topic map", morituri.getTopicMap() == null);
    assertTrue("Topic map not empty", topicmap.getTopics().size() == 0);
  }

  public void testTopicAssociationRolePlayerDeletion() {
    TopicMapIF topicmap = makeTopicMap();
    TopicMapBuilderIF builder = topicmap.getBuilder();

    TopicIF morituri = builder.makeTopic();
    TopicIF other = builder.makeTopic();
    
    AssociationIF assoc = builder.makeAssociation(builder.makeTopic());
    AssociationRoleIF role1 = builder.makeAssociationRole(assoc, builder.makeTopic(), morituri);

    AssociationRoleIF role2 = builder.makeAssociationRole(assoc, builder.makeTopic(), other);

    morituri.remove();

    assertTrue("Topic still connected to topic map", morituri.getTopicMap() == null);
    assertTrue("Topic map has too many topics", topicmap.getTopics().size() == 4);
    assertTrue("Role still part of topic map", role1.getTopicMap() == null);
    assertTrue("other still has role", other.getRoles().size() == 0);
    assertTrue("Topic map lost association", topicmap.getAssociations().size() == 0);
  }

  public void testTopicAssociationDeletion() {
    TopicMapIF topicmap = makeTopicMap();
    TopicMapBuilderIF builder = topicmap.getBuilder();

    TopicIF morituri = builder.makeTopic();
    TopicIF other = builder.makeTopic();
    
    AssociationIF assoc = builder.makeAssociation(builder.makeTopic());
    AssociationRoleIF role1 = builder.makeAssociationRole(assoc, builder.makeTopic(), morituri);

    AssociationRoleIF role2 = builder.makeAssociationRole(assoc, builder.makeTopic(), other);

    morituri.remove();

    assertTrue("Topic still connected to topic map", morituri.getTopicMap() == null);
    assertTrue("Topic map has too many topics", topicmap.getTopics().size() == 4);
    assertTrue("Role 1 still connected to topic map", role1.getTopicMap() == null);
    assertTrue("Role 2 still connected to topic map", role2.getTopicMap() == null);
    assertTrue("Association still connected to topic map", assoc.getTopicMap() == null);
    assertTrue("Topic map still has association", topicmap.getAssociations().size() == 0);
  }
  
  // --- Helper methods
  
  private void clearTopicMap(TopicMapIF tm) throws Exception {

    // Remove all the objects from the topic map
    tm.clear();

    assertTrue("Not all topics was deleted", tm.getTopics().isEmpty());
    assertTrue("Not all associations was deleted", tm.getAssociations().isEmpty());
  }
  
}
