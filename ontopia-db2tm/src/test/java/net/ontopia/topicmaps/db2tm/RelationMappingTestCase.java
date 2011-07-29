
package net.ontopia.topicmaps.db2tm;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.ontopia.utils.FileUtils;
import net.ontopia.utils.TestFileUtils;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.db2tm.*;

import org.junit.Test;
import org.junit.Assert;

public class RelationMappingTestCase {

  @Test
  public void testMapping() throws IOException {

    // Load relation mapping from classpath
    RelationMapping mapping = RelationMapping.readFromClasspath("net/ontopia/testdata/db2tm/rmtest.xml");

    Assert.assertTrue("Wrong number of data sources", mapping.getDataSources().size() == 1);
    
    // Relation: TEST
    Relation r = mapping.getRelation("TEST");
    Assert.assertEquals("Wrong relation name", r.getName(), "TEST");
    // <topic columns="..."/>
    Assert.assertTrue("Wrong number of columns", r.getColumns().length == 4);
    Assert.assertEquals("Wrong first column", r.getColumns()[0], "ID1");
    Assert.assertEquals("Wrong second column", r.getColumns()[1], "ID2");
    Assert.assertEquals("Wrong third column", r.getColumns()[2], "NAME");
    Assert.assertEquals("Wrong fourth column", r.getColumns()[3], "ADDRESS");
    
    List entities = r.getEntities();
    Entity e;

    Assert.assertTrue("Incorrect number of entities", entities.size() == 5);

    // primary entities: checking that defaulting works as expected
    e = (Entity)entities.get(0);
    Assert.assertTrue("First entity is not primary", e.isPrimary());
    Assert.assertTrue("Wrong entity type", e.getEntityType() == Entity.TYPE_TOPIC);
    Assert.assertEquals("First entity has wrong id", e.getId(), "primary");
    Assert.assertEquals("Wrong relation", e.getRelation(), r);

    // <topic type="..."/>
    Assert.assertTrue("Wrong topic type count", e.getTypes().length == 1);
    Assert.assertTrue("Wrong topic type", e.getTypes()[0].equals("local:tt"));

    // fields
    Assert.assertTrue("Wrong number of identity fields", e.getIdentityFields().size() == 1);
    Assert.assertTrue("Wrong number of characteristic fields", e.getCharacteristicFields().size() == 2);
    Assert.assertTrue("Wrong number of role fields", e.getRoleFields().size() == 0);

    Field f = ((Field)e.getIdentityFields().get(0));
    Assert.assertTrue("Wrong field type", f.getFieldType() == Field.TYPE_SUBJECT_IDENTIFIER);
    Assert.assertEquals("Wrong subject identifier pattern", f.getPattern(), "test:a:${ID1}");
    Assert.assertEquals("Wrong entity", f.getEntity(), e);

    f = ((Field)e.getCharacteristicFields().get(0));    
    Assert.assertEquals("Wrong name pattern", f.getPattern(), "${NAME}");
    Assert.assertEquals("Wrong name type", f.getType(), null);
    Assert.assertTrue("Wrong name scope", f.getScope().length == 0);
    f = ((Field)e.getCharacteristicFields().get(1));    
    Assert.assertEquals("Wrong occurrence pattern", f.getPattern(), "${ADDRESS}");
    Assert.assertEquals("Wrong occurrence type", f.getType(), "local:address");
    Assert.assertTrue("Wrong occurrence scope", f.getScope().length == 0);
    
    e = (Entity)entities.get(1);
    Assert.assertTrue("Second entity is primary", !e.isPrimary());
    Assert.assertEquals("Second entity has wrong id", e.getId(), "non-primary");

    Assert.assertTrue("Wrong number of identity fields", e.getIdentityFields().size() == 3);
    Assert.assertTrue("Wrong number of characteristic fields", e.getCharacteristicFields().size() == 0);
    Assert.assertTrue("Wrong number of role fields", e.getRoleFields().size() == 0);

    f = ((Field)e.getIdentityFields().get(0));
    Assert.assertTrue("Wrong field type", f.getFieldType() == Field.TYPE_SUBJECT_LOCATOR);
    Assert.assertEquals("Wrong subject locator pattern", f.getPattern(), "test:b:${ID2}");
    f = ((Field)e.getIdentityFields().get(1));
    Assert.assertTrue("Wrong field type", f.getFieldType() == Field.TYPE_ITEM_IDENTIFIER);
    Assert.assertEquals("Wrong item identifier pattern", f.getPattern(), "test:c:${ID2}");
    f = ((Field)e.getIdentityFields().get(2));
    Assert.assertTrue("Wrong field type", f.getFieldType() == Field.TYPE_SUBJECT_IDENTIFIER);
    Assert.assertEquals("Wrong subject identifier pattern", f.getPattern(), "test:d:${ID2}");

    // primary entities: checking that explicit attribute values works as expected
    e = (Entity)entities.get(2);
    Assert.assertTrue("Third entity is not primary", e.isPrimary());

    Assert.assertTrue("Wrong number of identity fields", e.getIdentityFields().size() == 1);
    Assert.assertTrue("Wrong number of characteristic fields", e.getCharacteristicFields().size() == 1);
    Assert.assertTrue("Wrong number of role fields", e.getRoleFields().size() == 0);

    f = ((Field)e.getCharacteristicFields().get(0));
    Assert.assertTrue("Wrong field type", f.getFieldType() == Field.TYPE_PLAYER);
    Assert.assertEquals("Wrong player type", f.getRoleType(), "local:rt1");
    Assert.assertEquals("Wrong association type", f.getAssociationType(), "local:at1");
    Assert.assertTrue("Wrong number of other role fields", f.getOtherRoleFields().size() == 1);
    Assert.assertTrue("Wrong scope size", f.getScope().length == 1);
    Assert.assertEquals("Wrong scope", f.getScope()[0], "#primary");

    f = ((Field)f.getOtherRoleFields().get(0));
    Assert.assertEquals("Wrong other role type", f.getRoleType(), "local:rt2");
    Assert.assertEquals("Wrong other player", f.getPlayer(), "#primary");
    
    e = (Entity)entities.get(3);
    Assert.assertTrue("Fourth entity is primary", !e.isPrimary());

    e = (Entity)entities.get(4);
    Assert.assertTrue("Fifth entity is not primary", e.isPrimary());
    Assert.assertTrue("Wrong entity type", e.getEntityType() == Entity.TYPE_ASSOCIATION);

    Assert.assertEquals("Wrong association type", e.getAssociationType(), "local:at2");
    Assert.assertTrue("Wrong scope size", e.getScope().length == 1);
    Assert.assertEquals("Wrong scope", e.getScope()[0], "#non-primary");

    Assert.assertTrue("Wrong number of identity fields", e.getIdentityFields().size() == 0);
    Assert.assertTrue("Wrong number of characteristic fields", e.getCharacteristicFields().size() == 0);
    Assert.assertTrue("Wrong number of role fields", e.getRoleFields().size() == 2);

    f = ((Field)e.getRoleFields().get(0));
    Assert.assertEquals("Wrong role type", f.getRoleType(), "local:rt3");
    Assert.assertEquals("Wrong player", f.getPlayer(), "#primary");

    f = ((Field)e.getRoleFields().get(1));
    Assert.assertEquals("Wrong role type", f.getRoleType(), "local:rt4");
    Assert.assertEquals("Wrong player", f.getPlayer(), "#non-primary");
    
    // <relation synctype="..."/> defaulting
    Assert.assertTrue("Wrong synctype on relation", r.getSynchronizationType() == Relation.SYNCHRONIZATION_CHANGELOG);

    Assert.assertTrue("Wrong number of syncs", r.getSyncs().size() == 1);

    Changelog sync = (Changelog)r.getSyncs().get(0);
    Assert.assertEquals("Wrong relation", sync.getRelation(), r);
    Assert.assertEquals("Wrong table", sync.getTable(), "TEST_CLOG");
    Assert.assertEquals("Wrong action column", sync.getActionColumn(), "CTYPE");
    Assert.assertEquals("Wrong order column", sync.getOrderColumn(), "CORDER");
    Assert.assertTrue("Wrong primary key size", sync.getPrimaryKey().length == 1);
    Assert.assertEquals("Wrong primary key", sync.getPrimaryKey()[0], "ID");
    Map actionMapping = sync.getActionMapping();
    Assert.assertTrue("Wrong number of action mappings", actionMapping.size() == 4);
    Assert.assertTrue("Wrong action mapping N", sync.getAction("N") == ChangelogReaderIF.CHANGE_TYPE_CREATE);
    Assert.assertTrue("Wrong action mapping U", sync.getAction("U") == ChangelogReaderIF.CHANGE_TYPE_DELETE);
    Assert.assertTrue("Wrong action mapping E1", sync.getAction("E1") == ChangelogReaderIF.CHANGE_TYPE_UPDATE);
    Assert.assertTrue("Wrong action mapping E2", sync.getAction("E2") == ChangelogReaderIF.CHANGE_TYPE_IGNORE);
    try {
      sync.getAction("FOO");
      Assert.fail("Didn't fail when accessing unknown action id FOO.");
    } catch (DB2TMInputException ex) {
      // yeah
    }
    
  }
  
}
