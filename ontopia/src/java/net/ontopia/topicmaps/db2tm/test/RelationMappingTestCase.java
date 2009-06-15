
package net.ontopia.topicmaps.db2tm.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.ontopia.test.TestCaseGeneratorIF;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.db2tm.*;
import net.ontopia.test.AbstractOntopiaTestCase;


public class RelationMappingTestCase extends AbstractOntopiaTestCase {

  public RelationMappingTestCase(String name) {
    super(name);
  }

  public void testMapping() throws IOException {

    String root = AbstractOntopiaTestCase.getTestDirectory();
    String base = root + File.separator + "db2tm" + File.separator;
      
    // Load relation mapping file
    RelationMapping mapping = RelationMapping.read(new File(base + "rmtest.xml"));

    assertTrue("Wrong number of data sources", mapping.getDataSources().size() == 1);
    
    // Relation: TEST
    Relation r = mapping.getRelation("TEST");
    assertEquals("Wrong relation name", r.getName(), "TEST");
    // <topic columns="..."/>
    assertTrue("Wrong number of columns", r.getColumns().length == 4);
    assertEquals("Wrong first column", r.getColumns()[0], "ID1");
    assertEquals("Wrong second column", r.getColumns()[1], "ID2");
    assertEquals("Wrong third column", r.getColumns()[2], "NAME");
    assertEquals("Wrong fourth column", r.getColumns()[3], "ADDRESS");
    
    List entities = r.getEntities();
    Entity e;

    assertTrue("Incorrect number of entities", entities.size() == 5);

    // primary entities: checking that defaulting works as expected
    e = (Entity)entities.get(0);
    assertTrue("First entity is not primary", e.isPrimary());
    assertTrue("Wrong entity type", e.getEntityType() == Entity.TYPE_TOPIC);
    assertEquals("First entity has wrong id", e.getId(), "primary");
    assertEquals("Wrong relation", e.getRelation(), r);

    // <topic type="..."/>
    assertTrue("Wrong topic type count", e.getTypes().length == 1);
    assertTrue("Wrong topic type", e.getTypes()[0].equals("local:tt"));

    // fields
    assertTrue("Wrong number of identity fields", e.getIdentityFields().size() == 1);
    assertTrue("Wrong number of characteristic fields", e.getCharacteristicFields().size() == 2);
    assertTrue("Wrong number of role fields", e.getRoleFields().size() == 0);

    Field f = ((Field)e.getIdentityFields().get(0));
    assertTrue("Wrong field type", f.getFieldType() == Field.TYPE_SUBJECT_IDENTIFIER);
    assertEquals("Wrong subject identifier pattern", f.getPattern(), "test:a:${ID1}");
    assertEquals("Wrong entity", f.getEntity(), e);

    f = ((Field)e.getCharacteristicFields().get(0));    
    assertEquals("Wrong name pattern", f.getPattern(), "${NAME}");
    assertEquals("Wrong name type", f.getType(), null);
    assertTrue("Wrong name scope", f.getScope().length == 0);
    f = ((Field)e.getCharacteristicFields().get(1));    
    assertEquals("Wrong occurrence pattern", f.getPattern(), "${ADDRESS}");
    assertEquals("Wrong occurrence type", f.getType(), "local:address");
    assertTrue("Wrong occurrence scope", f.getScope().length == 0);
    
    e = (Entity)entities.get(1);
    assertTrue("Second entity is primary", !e.isPrimary());
    assertEquals("Second entity has wrong id", e.getId(), "non-primary");

    assertTrue("Wrong number of identity fields", e.getIdentityFields().size() == 3);
    assertTrue("Wrong number of characteristic fields", e.getCharacteristicFields().size() == 0);
    assertTrue("Wrong number of role fields", e.getRoleFields().size() == 0);

    f = ((Field)e.getIdentityFields().get(0));
    assertTrue("Wrong field type", f.getFieldType() == Field.TYPE_SUBJECT_LOCATOR);
    assertEquals("Wrong subject locator pattern", f.getPattern(), "test:b:${ID2}");
    f = ((Field)e.getIdentityFields().get(1));
    assertTrue("Wrong field type", f.getFieldType() == Field.TYPE_ITEM_IDENTIFIER);
    assertEquals("Wrong item identifier pattern", f.getPattern(), "test:c:${ID2}");
    f = ((Field)e.getIdentityFields().get(2));
    assertTrue("Wrong field type", f.getFieldType() == Field.TYPE_SUBJECT_IDENTIFIER);
    assertEquals("Wrong subject identifier pattern", f.getPattern(), "test:d:${ID2}");

    // primary entities: checking that explicit attribute values works as expected
    e = (Entity)entities.get(2);
    assertTrue("Third entity is not primary", e.isPrimary());

    assertTrue("Wrong number of identity fields", e.getIdentityFields().size() == 1);
    assertTrue("Wrong number of characteristic fields", e.getCharacteristicFields().size() == 1);
    assertTrue("Wrong number of role fields", e.getRoleFields().size() == 0);

    f = ((Field)e.getCharacteristicFields().get(0));
    assertTrue("Wrong field type", f.getFieldType() == Field.TYPE_PLAYER);
    assertEquals("Wrong player type", f.getRoleType(), "local:rt1");
    assertEquals("Wrong association type", f.getAssociationType(), "local:at1");
    assertTrue("Wrong number of other role fields", f.getOtherRoleFields().size() == 1);
    assertTrue("Wrong scope size", f.getScope().length == 1);
    assertEquals("Wrong scope", f.getScope()[0], "#primary");

    f = ((Field)f.getOtherRoleFields().get(0));
    assertEquals("Wrong other role type", f.getRoleType(), "local:rt2");
    assertEquals("Wrong other player", f.getPlayer(), "#primary");
    
    e = (Entity)entities.get(3);
    assertTrue("Fourth entity is primary", !e.isPrimary());

    e = (Entity)entities.get(4);
    assertTrue("Fifth entity is not primary", e.isPrimary());
    assertTrue("Wrong entity type", e.getEntityType() == Entity.TYPE_ASSOCIATION);

    assertEquals("Wrong association type", e.getAssociationType(), "local:at2");
    assertTrue("Wrong scope size", e.getScope().length == 1);
    assertEquals("Wrong scope", e.getScope()[0], "#non-primary");

    assertTrue("Wrong number of identity fields", e.getIdentityFields().size() == 0);
    assertTrue("Wrong number of characteristic fields", e.getCharacteristicFields().size() == 0);
    assertTrue("Wrong number of role fields", e.getRoleFields().size() == 2);

    f = ((Field)e.getRoleFields().get(0));
    assertEquals("Wrong role type", f.getRoleType(), "local:rt3");
    assertEquals("Wrong player", f.getPlayer(), "#primary");

    f = ((Field)e.getRoleFields().get(1));
    assertEquals("Wrong role type", f.getRoleType(), "local:rt4");
    assertEquals("Wrong player", f.getPlayer(), "#non-primary");
    
    // <relation synctype="..."/> defaulting
    assertTrue("Wrong synctype on relation", r.getSynchronizationType() == Relation.SYNCHRONIZATION_CHANGELOG);

    assertTrue("Wrong number of syncs", r.getSyncs().size() == 1);

    Changelog sync = (Changelog)r.getSyncs().get(0);
    assertEquals("Wrong relation", sync.getRelation(), r);
    assertEquals("Wrong table", sync.getTable(), "TEST_CLOG");
    assertEquals("Wrong action column", sync.getActionColumn(), "CTYPE");
    assertEquals("Wrong order column", sync.getOrderColumn(), "CORDER");
    assertTrue("Wrong primary key size", sync.getPrimaryKey().length == 1);
    assertEquals("Wrong primary key", sync.getPrimaryKey()[0], "ID");
    Map actionMapping = sync.getActionMapping();
    assertTrue("Wrong number of action mappings", actionMapping.size() == 4);
    assertTrue("Wrong action mapping N", sync.getAction("N") == ChangelogReaderIF.CHANGE_TYPE_CREATE);
    assertTrue("Wrong action mapping U", sync.getAction("U") == ChangelogReaderIF.CHANGE_TYPE_DELETE);
    assertTrue("Wrong action mapping E1", sync.getAction("E1") == ChangelogReaderIF.CHANGE_TYPE_UPDATE);
    assertTrue("Wrong action mapping E2", sync.getAction("E2") == ChangelogReaderIF.CHANGE_TYPE_IGNORE);
    try {
      sync.getAction("FOO");
      fail("Didn't fail when accessing unknown action id FOO.");
    } catch (DB2TMInputException ex) {
      // yeah
    }
    
  }
  
}
