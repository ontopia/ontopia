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

package net.ontopia.topicmaps.impl.rdbms;

import java.io.IOException;
import net.ontopia.persistence.proxy.RDBMSStorage;
import net.ontopia.persistence.proxy.StorageIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * INTERNAL: Tests that tests various aspects about the query caches
 * used by the RDBMS Backend Connector.
 */

public class QueryCacheTests {
  
  @Before
  public void setUp() throws Exception {
    RDBMSTestFactory.checkDatabasePresence();
  }

  /**
   * INTERNAL: Test TopicIF.getRolesByType(TopicIF rtype). Verify that
   * the query caches is reset whenever the roles of a topic
   * changes. This can either be if the role has been added, the role
   * deleted, or the role changes its type.
   */
  @Test
  public void testRoleTypeInvalidation() throws IOException, java.net.MalformedURLException {
    // initialize storage
    StorageIF storage = new RDBMSStorage();

    TopicMapStoreIF storeA = null;
    TopicMapStoreIF storeB = null;
    TopicMapStoreIF storeC = null;
    TopicMapStoreIF storeD = null;
    try {
      // create topic map
      storeA = new RDBMSTopicMapStore(storage);
      TopicMapIF tmA = storeA.getTopicMap();
      long tmid = Long.parseLong(tmA.getObjectId().substring(1));

      // add association to first topic map
      TopicIF p1 = tmA.getBuilder().makeTopic();
      String oid_p1 = p1.getObjectId();
      TopicIF p2 = tmA.getBuilder().makeTopic();
      TopicIF at = tmA.getBuilder().makeTopic();
      TopicIF rt1 = tmA.getBuilder().makeTopic();
      String oid_rt1 = rt1.getObjectId();
      TopicIF rt2 = tmA.getBuilder().makeTopic();

      Assert.assertTrue("p1.roles.size is not empty.", p1.getRoles().isEmpty());
      Assert.assertTrue("p2.roles.size is not empty.", p2.getRoles().isEmpty());

      TopicMapBuilderIF builder = tmA.getBuilder();

      AssociationIF a1 = builder.makeAssociation(at);
      builder.makeAssociationRole(a1, rt1, p1);
      builder.makeAssociationRole(a1, rt2, p2);

      Assert.assertTrue("p1.roles.size is not 1.", p1.getRoles().size() == 1);
      Assert.assertTrue("p2.roles.size is not 1.", p2.getRoles().size() == 1);

      Assert.assertTrue("p1.rolesByType(rt1).size is not 1.", p1.getRolesByType(rt1).size() == 1);

      storeA.commit();

      // open topic map store D
      storeD = new RDBMSTopicMapStore(storage, tmid);
      TopicMapIF tmD = storeD.getTopicMap();

      TopicIF p1D = (TopicIF)tmD.getObjectById(oid_p1);
      // no longer needed, there is a check in getRolesByType()
      //assertTrue("p1D.rolesByType(null).size is not 0.", p1D.getRolesByType(null).size() == 0);

      TopicIF rt1D = (TopicIF)tmD.getObjectById(oid_rt1);
      Assert.assertTrue("p1D.rolesByType(rt1).size is not 1.", p1D.getRolesByType(rt1D).size() == 1);

      // open topic map store B
      storeB = new RDBMSTopicMapStore(storage, tmid);
      TopicMapIF tmB = storeB.getTopicMap();

      TopicIF p1B = (TopicIF)tmB.getObjectById(oid_p1);
      // no longer needed, there is a check in getRolesByType()
      //assertTrue("p1B.rolesByType(null).size is not 0.", p1B.getRolesByType(null).size() == 0);

      TopicIF rt1B = (TopicIF)tmB.getObjectById(oid_rt1);
      Assert.assertTrue("p1B.rolesByType(rt1).size is not 1.", p1B.getRolesByType(rt1B).size() == 1);

      // remove player from role and recheck state
      AssociationRoleIF r1B = (AssociationRoleIF)p1B.getRoles().iterator().next();
      r1B.setPlayer(tmB.getBuilder().makeTopic());
      Assert.assertTrue("p1B.roles.size is not empty.", p1B.getRoles().size() == 0);
      Assert.assertTrue("p1B.rolesByType(rt1B).size is not 0.", p1B.getRolesByType(rt1B).size() == 0);
      // no longer needed, there is a check in getRolesByType()
      //assertTrue("p1B.rolesByType(null).size is not 0.", p1B.getRolesByType(null).size() == 0);

      storeB.commit();

      // open topic map store C
      storeC = new RDBMSTopicMapStore(storage, tmid);
      TopicMapIF tmC = storeC.getTopicMap();

      TopicIF p1C = (TopicIF)tmC.getObjectById(oid_p1);
      // no longer needed, there is a check in getRolesByType()
      //assertTrue("p1C.rolesByType(null).size is not 0.", p1C.getRolesByType(null).size() == 0);

      TopicIF rt1C = (TopicIF)tmC.getObjectById(oid_rt1);
      Assert.assertTrue("p1C.rolesByType(rt1).size is not 0.", p1C.getRolesByType(rt1C).size() == 0);

      storeC.commit();
      storeD.commit();

    } finally {
      if (storeD != null) storeD.close();
      if (storeC != null) storeC.close();
      if (storeB != null) storeB.close();
      if (storeA != null) storeA.delete(true);
    }
  }

  /**
   * INTERNAL: Test TopicIF.getRolesByType(TopicIF rtype, TopicIF
   * atype). Verify that the query caches is reset whenever the roles
   * of a topic changes or the type of their associations. This can
   * either be if the role has been added, the role deleted, or the
   * role changes its type.
   */
  @Test
  public void testRoleTypeAssociationTypeInvalidation() throws IOException, java.net.MalformedURLException {
    // initialize storage
    StorageIF storage = new RDBMSStorage();

    TopicMapStoreIF storeA = null;
    TopicMapStoreIF storeB = null;
    TopicMapStoreIF storeC = null;
    TopicMapStoreIF storeD = null;
    TopicMapStoreIF storeE = null;
    try {
      // create topic map
      storeA = new RDBMSTopicMapStore(storage);
      TopicMapIF tmA = storeA.getTopicMap();
      long tmid = Long.parseLong(tmA.getObjectId().substring(1));

      // add association to first topic map
      TopicIF p1 = tmA.getBuilder().makeTopic();
      TopicIF p2 = tmA.getBuilder().makeTopic();
      String oid_p1 = p1.getObjectId();
      String oid_p2 = p2.getObjectId();
      TopicIF at1 = tmA.getBuilder().makeTopic();
      TopicIF at2 = tmA.getBuilder().makeTopic();
      String oid_at1 = at1.getObjectId();
      String oid_at2 = at2.getObjectId();
      TopicIF rt1 = tmA.getBuilder().makeTopic();
      TopicIF rt2 = tmA.getBuilder().makeTopic();
      TopicIF rt3 = tmA.getBuilder().makeTopic();
      String oid_rt1 = rt1.getObjectId();
      String oid_rt2 = rt2.getObjectId();
      String oid_rt3 = rt3.getObjectId();

      Assert.assertTrue("p1.roles.size is not empty.", p1.getRoles().isEmpty());
      Assert.assertTrue("p2.roles.size is not empty.", p2.getRoles().isEmpty());

      TopicMapBuilderIF builder = tmA.getBuilder();

      AssociationIF a1 = builder.makeAssociation(at1);
      builder.makeAssociationRole(a1, rt1, p1);
      builder.makeAssociationRole(a1, rt2, p2);

      Assert.assertTrue("p1.roles.size is not 1.", p1.getRoles().size() == 1);
      Assert.assertTrue("p2.roles.size is not 1.", p2.getRoles().size() == 1);

      Assert.assertTrue("p1.rolesByType(rt1,at1).size is not 1.", p1.getRolesByType(rt1, at1).size() == 1);
      Assert.assertTrue("p1.rolesByType(rt2,at1).size is not 0.", p1.getRolesByType(rt2, at1).size() == 0);
      Assert.assertTrue("p2.rolesByType(rt1,at1).size is not 0.", p2.getRolesByType(rt1, at1).size() == 0);
      Assert.assertTrue("p2.rolesByType(rt2,at1).size is not 1.", p2.getRolesByType(rt2, at1).size() == 1);

      storeA.commit();

      // open topic map store D
      storeD = new RDBMSTopicMapStore(storage, tmid);
      TopicMapIF tmD = storeD.getTopicMap();

      TopicIF p1D = (TopicIF)tmD.getObjectById(oid_p1);
      // no longer needed, there is a check in getRolesByType()
      //assertTrue("p1D.rolesByType(null,null).size is not 0.", p1D.getRolesByType(null, null).size() == 0);

      TopicIF rt1D = (TopicIF)tmD.getObjectById(oid_rt1);
      TopicIF at1D = (TopicIF)tmD.getObjectById(oid_at1);
      Assert.assertTrue("p1D.rolesByType(rt1,at1).size is not 1.", p1D.getRolesByType(rt1D, at1D).size() == 1);

      // open topic map store E
      storeE = new RDBMSTopicMapStore(storage, tmid);
      TopicMapIF tmE = storeE.getTopicMap();

      TopicIF p1E = (TopicIF)tmE.getObjectById(oid_p1);
      TopicIF p2E = (TopicIF)tmE.getObjectById(oid_p2);
      // no longer needed, there is a check in getRolesByType()
      //assertTrue("p1E.rolesByType(null,null).size is not 0.", p1E.getRolesByType(null, null).size() == 0);
      //assertTrue("p2E.rolesByType(null,null).size is not 0.", p2E.getRolesByType(null, null).size() == 0);

      TopicIF rt1E = (TopicIF)tmE.getObjectById(oid_rt1);
      TopicIF rt2E = (TopicIF)tmE.getObjectById(oid_rt2);
      TopicIF rt3E = (TopicIF)tmE.getObjectById(oid_rt3);
      TopicIF at1E = (TopicIF)tmE.getObjectById(oid_at1);
      TopicIF at2E = (TopicIF)tmE.getObjectById(oid_at2);
      // at1( p : rt1, o : rt2) -> at2( p : rt3, o : rt2)
      AssociationRoleIF r1E = (AssociationRoleIF)p1E.getRolesByType(rt1E, at1E).iterator().next();
      r1E.getAssociation().setType(at2E);
      r1E.setType(rt3E);
      Assert.assertTrue("p1E.rolesByType(rt1,at1).size is not 0.", p1E.getRolesByType(rt1E, at1E).size() == 0);
      Assert.assertTrue("p1E.rolesByType(rt1,at2).size is not 0.", p1E.getRolesByType(rt1E, at2E).size() == 0);
      Assert.assertTrue("p1E.rolesByType(rt3,at2).size is not 1.", p1E.getRolesByType(rt3E, at2E).size() == 1);
      Assert.assertTrue("p2E.rolesByType(rt2,at2).size is not 1.", p2E.getRolesByType(rt2E, at2E).size() == 1);

      storeE.commit();

      // open topic map store B
      storeB = new RDBMSTopicMapStore(storage, tmid);
      TopicMapIF tmB = storeB.getTopicMap();

      TopicIF p1B = (TopicIF)tmB.getObjectById(oid_p1);
      TopicIF p2B = (TopicIF)tmB.getObjectById(oid_p2);
      // no longer needed, there is a check in getRolesByType()
      //assertTrue("p1B.rolesByType(null, null).size is not 0.", p1B.getRolesByType(null, null).size() == 0);
      //assertTrue("p2B.rolesByType(null, null).size is not 0.", p2B.getRolesByType(null, null).size() == 0);

      TopicIF rt1B = (TopicIF)tmB.getObjectById(oid_rt1);
      TopicIF rt2B = (TopicIF)tmB.getObjectById(oid_rt2);
      TopicIF rt3B = (TopicIF)tmB.getObjectById(oid_rt3);
      TopicIF at1B = (TopicIF)tmB.getObjectById(oid_at1);
      TopicIF at2B = (TopicIF)tmB.getObjectById(oid_at2);
      Assert.assertTrue("p1B.rolesByType(rt3,at2).size is not 1.", p1B.getRolesByType(rt3B, at2B).size() == 1);

      AssociationRoleIF r1B = (AssociationRoleIF)p1B.getRoles().iterator().next();
      Assert.assertTrue("p1B.rolesByType(rt3,at1).size is not 0.", p1B.getRolesByType(rt3B,at1B).size() == 0);
      Assert.assertTrue("p1B.rolesByType(rt3,at2).size is not 1.", p1B.getRolesByType(rt3B,at2B).size() == 1);
      Assert.assertTrue("p1B.rolesByType(rt1,at1).size is not 0.", p1B.getRolesByType(rt1B,at1B).size() == 0);
      Assert.assertTrue("p1B.rolesByType(rt1,at2).size is not 0.", p1B.getRolesByType(rt1B,at2B).size() == 0);

      Assert.assertTrue("p1B.rolesByType(rt2,at1).size is not 0.", p1B.getRolesByType(rt2B,at1B).size() == 0);
      Assert.assertTrue("p1B.rolesByType(rt2,at2).size is not 0.", p1B.getRolesByType(rt2B,at2B).size() == 0);
      Assert.assertTrue("p2B.rolesByType(rt2,at1).size is not 0.", p2B.getRolesByType(rt2B,at1B).size() == 0);
      Assert.assertTrue("p2B.rolesByType(rt2,at2).size is not 1.", p2B.getRolesByType(rt2B,at2B).size() == 1);

      // at2( p : rt3, o : rt2) -> at1( p : rt3, o : rt2)
      r1B.getAssociation().setType(at1B);
      Assert.assertTrue("p1B.rolesByType(rt3,at1).size is not 1.", p1B.getRolesByType(rt3B,at1B).size() == 1);
      Assert.assertTrue("p1B.rolesByType(rt3,at2).size is not 0.", p1B.getRolesByType(rt3B,at2B).size() == 0);
      Assert.assertTrue("p1B.rolesByType(rt1,at1).size is not 0.", p1B.getRolesByType(rt1B,at1B).size() == 0);
      Assert.assertTrue("p1B.rolesByType(rt1,at2).size is not 0.", p1B.getRolesByType(rt1B,at2B).size() == 0);
      // at1( p : rt3, o : rt2) -> at1( p : rt1, o : rt2)
      r1B.setType(rt1B);
      Assert.assertTrue("p1B.rolesByType(rt3,at1).size is not 0.", p1B.getRolesByType(rt3B,at1B).size() == 0);
      Assert.assertTrue("p1B.rolesByType(rt3,at2).size is not 0.", p1B.getRolesByType(rt3B,at2B).size() == 0);
      Assert.assertTrue("p1B.rolesByType(rt1,at1).size is not 1.", p1B.getRolesByType(rt1B,at1B).size() == 1);
      Assert.assertTrue("p1B.rolesByType(rt1,at2).size is not 0.", p1B.getRolesByType(rt1B,at2B).size() == 0);

      // at1( p : rt1, o : rt2) -> at1( o : rt2)
      r1B.getAssociation();
      r1B.remove();
      Assert.assertTrue("p1B.rolesByType(rt3,at1).size is not 0.", p1B.getRolesByType(rt3B,at1B).size() == 0);
      Assert.assertTrue("p1B.rolesByType(rt3,at2).size is not 0.", p1B.getRolesByType(rt3B,at2B).size() == 0);
      Assert.assertTrue("p1B.rolesByType(rt1,at1).size is not 0.", p1B.getRolesByType(rt1B,at1B).size() == 0);
      Assert.assertTrue("p1B.rolesByType(rt1,at2).size is not 0.", p1B.getRolesByType(rt1B,at2B).size() == 0);
      Assert.assertTrue("p2B.rolesByType(rt2,at1).size is not 1.", p2B.getRolesByType(rt2B,at1B).size() == 1);
      Assert.assertTrue("p2B.rolesByType(rt2,at2).size is not 0.", p2B.getRolesByType(rt2B,at2B).size() == 0);
      //! aB.addRole(r1B);
      //! Assert.assertTrue("p1B.rolesByType(rt3,at1).size is not 0.", p1B.getRolesByType(rt3B,at1B).size() == 0);
      //! Assert.assertTrue("p1B.rolesByType(rt3,at2).size is not 0.", p1B.getRolesByType(rt3B,at2B).size() == 0);
      //! Assert.assertTrue("p1B.rolesByType(rt1,at1).size is not 1.", p1B.getRolesByType(rt1B,at1B).size() == 1);
      //! Assert.assertTrue("p1B.rolesByType(rt1,at2).size is not 0.", p1B.getRolesByType(rt1B,at2B).size() == 0);
      //! Assert.assertTrue("p2B.rolesByType(rt2,at1).size is not 1.", p2B.getRolesByType(rt2B,at1B).size() == 1);
      //! Assert.assertTrue("p2B.rolesByType(rt2,at2).size is not 0.", p2B.getRolesByType(rt2B,at2B).size() == 0);
      //! 
      //! // at1( p : rt1, o : rt2) -> at1( null : rt1, o : rt2)
      //! r1B.setPlayer(null);
      //! Assert.assertTrue("p1B.roles.size is not empty.", p1B.getRoles().size() == 0);
      //! Assert.assertTrue("p2B.roles.size is not 1.", p2B.getRoles().size() == 1);
      //! Assert.assertTrue("p1B.rolesByType(rt3,at1).size is not 0.", p1B.getRolesByType(rt3B,at1B).size() == 0);
      //! Assert.assertTrue("p1B.rolesByType(rt3,at2).size is not 0.", p1B.getRolesByType(rt3B,at2B).size() == 0);
      //! Assert.assertTrue("p1B.rolesByType(rt1,at1).size is not 0.", p1B.getRolesByType(rt1B,at1B).size() == 0);
      //! Assert.assertTrue("p1B.rolesByType(rt1,at2).size is not 0.", p1B.getRolesByType(rt1B,at2B).size() == 0);
      //! Assert.assertTrue("p1B.rolesByType(null,null).size is not 0.", p1B.getRolesByType(null,null).size() == 0);

      storeB.commit();

      // open topic map store C
      storeC = new RDBMSTopicMapStore(storage, tmid);
      TopicMapIF tmC = storeC.getTopicMap();

      TopicIF p1C = (TopicIF)tmC.getObjectById(oid_p1);
      // no longer needed, there is a check in getRolesByType()
      //assertTrue("p1C.rolesByType(null).size is not 0.", p1C.getRolesByType(null, null).size() == 0);

      TopicIF rt1C = (TopicIF)tmC.getObjectById(oid_rt1);
      TopicIF rt3C = (TopicIF)tmC.getObjectById(oid_rt3);
      TopicIF at1C = (TopicIF)tmC.getObjectById(oid_at1);
      TopicIF at2C = (TopicIF)tmC.getObjectById(oid_at2);
      // at1( null : rt1, o : rt2)
      // no longer needed, there is a check in getRolesByType()
      //assertTrue("p1C.rolesByType(null,at1).size is not 0.", p1C.getRolesByType(null,at1C).size() == 0);
      //assertTrue("p1C.rolesByType(null,at3).size is not 0.", p1C.getRolesByType(null,at2C).size() == 0);
      //assertTrue("p1C.rolesByType(rt1,null).size is not 0.", p1C.getRolesByType(rt1C, null).size() == 0);
      //assertTrue("p1C.rolesByType(rt3,null).size is not 0.", p1C.getRolesByType(rt3C, null).size() == 0);
      Assert.assertTrue("p1C.rolesByType(rt3,at1).size is not 0.", p1C.getRolesByType(rt3C,at1C).size() == 0);
      Assert.assertTrue("p1C.rolesByType(rt3,at2).size is not 0.", p1C.getRolesByType(rt3C,at2C).size() == 0);
      Assert.assertTrue("p1C.rolesByType(rt1,at1).size is not 0.", p1C.getRolesByType(rt1C,at1C).size() == 0);
      Assert.assertTrue("p1C.rolesByType(rt1,at2).size is not 0.", p1C.getRolesByType(rt1C,at2C).size() == 0);

      storeC.commit();
      storeD.commit();

    } finally {
      if (storeE != null) storeE.close();
      if (storeD != null) storeD.close();
      if (storeC != null) storeC.close();
      if (storeB != null) storeB.close();
      if (storeA != null) storeA.delete(true);
    }
  }
  
}
