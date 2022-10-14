/*
 * #!
 * Ontopia Navigator
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

package net.ontopia.topicmaps.nav2.portlets.pojos;

import java.io.StringReader;
import java.io.IOException;
import java.util.List;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.net.MalformedURLException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.topicmaps.utils.ltm.LTMTopicMapReader;
import net.ontopia.utils.TestFileUtils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RelatedTopicsTest {

  private final static String testdataDirectory = "nav2";

  private RelatedTopics portlet;

  @Before
  public void setUp() throws MalformedURLException {
    portlet = new RelatedTopics();
  }

  // --- Tests

  @Test
  public void testNoAssociations() throws IOException {
    TopicMapIF tm = load("screwed-up.ltm");
    TopicIF topic = getTopicById(tm, "no-name");
    List headings = portlet.makeModel(topic);
    Assert.assertTrue("topic with no associations had headings",
               headings.isEmpty());
  }

  @Test
  public void testVarious() throws IOException {
    // initialize
    TopicMapIF tm = load("association.xtm");
    TopicIF topic = getTopicById(tm, "oslo");
    List headings = portlet.makeModel(topic);
    Assert.assertTrue("wrong number of headings",
               headings.size() == 2);
    TopicIF contained_in = getTopicById(tm, "contained-in");
    TopicIF single_assoc = getTopicById(tm, "single-assoc");
    TopicIF containee = getTopicById(tm, "containee");

    // test first heading
    RelatedTopics.Heading heading =
      (RelatedTopics.Heading) headings.get(0);
    verifyHeading(heading, "first heading", "Contained in",
                  contained_in, containee, 3);

    List assocs = heading.getChildren();
    Assert.assertTrue("first heading did not have exactly two children",
               assocs.size() == 2);
    RelatedTopics.Association assoc =
      (RelatedTopics.Association) assocs.get(0);
    // FIXME: assocs not sorted...

    // test second heading
    heading = (RelatedTopics.Heading) headings.get(1);
    verifyHeading(heading, "second heading", "Single Association",
                  single_assoc, containee, 1);

    assocs = heading.getChildren();
    Assert.assertTrue("second heading did not have exactly one child",
               assocs.size() == 1);
    assoc = (RelatedTopics.Association) assocs.get(0);
    verifyAssociation(assoc, "assoc 2.1", 1, Collections.EMPTY_SET,
                      null, null, null);
    Assert.assertTrue("assoc 2.1 did not have empty role set",
               assoc.getRoles().isEmpty());
  }

  @Test
  public void testBinaries() throws IOException {
    // initialize
    TopicMapIF tm = load("bk-example.ltm");
    TopicIF topic = getTopicById(tm, "article1");
    List headings = portlet.makeModel(topic);
    Assert.assertTrue("wrong number of headings",
               headings.size() == 2);
    TopicIF created_by = getTopicById(tm, "created-by");
    TopicIF work = getTopicById(tm, "work");
    TopicIF is_about = getTopicById(tm, "is-about");
    TopicIF journo1 = getTopicById(tm, "journo1");
    TopicIF creator = getTopicById(tm, "creator");

    // test first heading
    RelatedTopics.Heading heading =
      (RelatedTopics.Heading) headings.get(0);
    verifyHeading(heading, "first heading", "Created by",
                  created_by, work, 2);

    List assocs = heading.getChildren();
    Assert.assertTrue("first heading did not have exactly one child",
               assocs.size() == 1);
    RelatedTopics.Association assoc =
      (RelatedTopics.Association) assocs.get(0);
    verifyAssociation(assoc, "assoc 1.1", 2, Collections.EMPTY_SET,
                      null, journo1, creator);
    Assert.assertTrue("assoc 1.1 did not have exactly one role",
               assoc.getRoles().size() == 1);
    AssociationRoleIF role = (AssociationRoleIF)
      assoc.getRoles().iterator().next();
    Assert.assertTrue("assoc 1.1 not of type creator",
               role.getType().equals(creator));
    Assert.assertTrue("assoc 1.1 not played by journo1",
               role.getPlayer().equals(journo1));

    // test second heading
    heading = (RelatedTopics.Heading) headings.get(1);
    verifyHeading(heading, "second heading", "Is about",
                  is_about, work, 2);

    assocs = heading.getChildren();
    Assert.assertTrue("second heading did not have exactly three children",
               assocs.size() == 3);
    // FIXME: no defined order for associations, unfortunately
  }

  /* Disabled because the file kb-example-ontopoly.xtm does not exist anywhere

  @Test
  public void testBinariesOntopoly() throws IOException { // same as testBinaries, except ontopoly topicmap
    // initialize
    TopicMapIF tm = load("bk-example-ontopoly.xtm");
    TopicIF topic = getTopicById(tm, "article1");
    RelatedTopics portlet = new RelatedTopics();
    portlet.setUseOntopolyNames(true);
    List headings = portlet.makeModel(topic);
    Assert.assertTrue("wrong number of headings",
               headings.size() == 2);
    TopicIF created_by = getTopicById(tm, "created-by");
    TopicIF work = getTopicById(tm, "work");
    TopicIF is_about = getTopicById(tm, "is-about");
    TopicIF journo1 = getTopicById(tm, "journo1");
    TopicIF creator = getTopicById(tm, "creator");

    // test first heading
    RelatedTopics.Heading heading =
      (RelatedTopics.Heading) headings.get(0);
    verifyHeading(heading, "first heading", "Created by Work",
                  created_by, work, 2);

    List assocs = heading.getChildren();
    Assert.assertTrue("first heading did not have exactly one child",
               assocs.size() == 1);
    RelatedTopics.Association assoc =
      (RelatedTopics.Association) assocs.get(0);
    verifyAssociation(assoc, "assoc 1.1", 2, Collections.EMPTY_SET,
                      null, journo1, creator);
    Assert.assertTrue("assoc 1.1 did not have exactly one role",
               assoc.getRoles().size() == 1);
    AssociationRoleIF role = (AssociationRoleIF)
      assoc.getRoles().iterator().next();
    Assert.assertTrue("assoc 1.1 not of type creator",
               role.getType().equals(creator));
    Assert.assertTrue("assoc 1.1 not played by journo1",
               role.getPlayer().equals(journo1));

    // test second heading
    heading = (RelatedTopics.Heading) headings.get(1);
    verifyHeading(heading, "second heading", "Is about Work",
                  is_about, work, 2);

    assocs = heading.getChildren();
    Assert.assertTrue("second heading did not have exactly three children",
               assocs.size() == 3);
    // FIXME: no defined order for associations, unfortunately
  }
  */

  @Test
  public void testBinariesWithExplicitHiding() throws IOException {
    // initialize
    TopicMapIF tm = load("bk-example.ltm");
    TopicIF topic = getTopicById(tm, "article1");
    TopicIF created_by = getTopicById(tm, "created-by");
    TopicIF work = getTopicById(tm, "work");
    TopicIF is_about = getTopicById(tm, "is-about");
    TopicIF journo1 = getTopicById(tm, "journo1");
    TopicIF creator = getTopicById(tm, "creator");
    portlet.setExcludeAssociationTypes(Collections.singleton(is_about));

    // start
    List headings = portlet.makeModel(topic);
    Assert.assertTrue("wrong number of headings",
               headings.size() == 1);
    
    // test first heading
    RelatedTopics.Heading heading =
      (RelatedTopics.Heading) headings.get(0);
    verifyHeading(heading, "first heading", "Created by",
                  created_by, work, 2);

    List assocs = heading.getChildren();
    Assert.assertTrue("first heading did not have exactly one child",
               assocs.size() == 1);
    RelatedTopics.Association assoc =
      (RelatedTopics.Association) assocs.get(0);
    verifyAssociation(assoc, "assoc 1.1", 2, Collections.EMPTY_SET,
                      null, journo1, creator);
    Assert.assertTrue("assoc 1.1 did not have exactly one role",
               assoc.getRoles().size() == 1);
    AssociationRoleIF role = (AssociationRoleIF)
      assoc.getRoles().iterator().next();
    Assert.assertTrue("assoc 1.1 not of type creator",
               role.getType().equals(creator));
    Assert.assertTrue("assoc 1.1 not played by journo1",
               role.getPlayer().equals(journo1));
  }

  @Test
  public void testBinariesWithExplicitRoleTypeHiding() throws IOException {
    // initialize
    TopicMapIF tm = load("bk-example.ltm");
    TopicIF topic = getTopicById(tm, "article2");
    TopicIF created_by = getTopicById(tm, "created-by");
    TopicIF work = getTopicById(tm, "work");
    TopicIF journo1 = getTopicById(tm, "journo1");
    TopicIF creator = getTopicById(tm, "creator");
    TopicIF object = getTopicById(tm, "object");    
    portlet.setExcludeRoleTypes(Collections.singleton(object));

    // start
    List headings = portlet.makeModel(topic);
    Assert.assertTrue("wrong number of headings",
               headings.size() == 1);
    
    // test first heading
    RelatedTopics.Heading heading =
      (RelatedTopics.Heading) headings.get(0);
    verifyHeading(heading, "first heading", "Created by",
                  created_by, work, 2);

    List assocs = heading.getChildren();
    Assert.assertTrue("first heading did not have exactly one child",
               assocs.size() == 1);
    RelatedTopics.Association assoc =
      (RelatedTopics.Association) assocs.get(0);
    verifyAssociation(assoc, "assoc 1.1", 2, Collections.EMPTY_SET,
                      null, journo1, creator);
    Assert.assertTrue("assoc 1.1 did not have exactly one role",
               assoc.getRoles().size() == 1);
    AssociationRoleIF role = (AssociationRoleIF)
      assoc.getRoles().iterator().next();
    Assert.assertTrue("assoc 1.1 not of type creator",
               role.getType().equals(creator));
    Assert.assertTrue("assoc 1.1 not played by journo1",
               role.getPlayer().equals(journo1));
  }

  @Test
  public void testBinariesWithImplicitHiding() throws IOException {
    // initialize
    TopicMapIF tm = load("bk-example.ltm");
    TopicIF topic = getTopicById(tm, "article1");
    TopicIF created_by = getTopicById(tm, "created-by");
    TopicIF work = getTopicById(tm, "work");
    getTopicById(tm, "is-about");
    TopicIF journo1 = getTopicById(tm, "journo1");
    TopicIF creator = getTopicById(tm, "creator");
    add(tm, "#PREFIX port @\"http://psi.ontopia.net/portlets/\" " +
            "port:is-hidden-association-type(is-about : port:type)");

    // start
    List headings = portlet.makeModel(topic);
    Assert.assertTrue("wrong number of headings",
               headings.size() == 1);
    
    // test first heading
    RelatedTopics.Heading heading =
      (RelatedTopics.Heading) headings.get(0);
    verifyHeading(heading, "first heading", "Created by",
                  created_by, work, 2);

    List assocs = heading.getChildren();
    Assert.assertTrue("first heading did not have exactly one child",
               assocs.size() == 1);
    RelatedTopics.Association assoc =
      (RelatedTopics.Association) assocs.get(0);
    verifyAssociation(assoc, "assoc 1.1", 2, Collections.EMPTY_SET,
                      null, journo1, creator);
    Assert.assertTrue("assoc 1.1 did not have exactly one role",
               assoc.getRoles().size() == 1);
    AssociationRoleIF role = (AssociationRoleIF)
      assoc.getRoles().iterator().next();
    Assert.assertTrue("assoc 1.1 not of type creator",
               role.getType().equals(creator));
    Assert.assertTrue("assoc 1.1 not played by journo1",
               role.getPlayer().equals(journo1));
  }
  
  @Test
  public void testBinariesWithExplicitWeak() throws IOException {
    // initialize
    TopicMapIF tm = load("bk-example.ltm");
    TopicIF topic = getTopicById(tm, "article1");
    TopicIF created_by = getTopicById(tm, "created-by");
    TopicIF work = getTopicById(tm, "work");
    TopicIF is_about = getTopicById(tm, "is-about");
    TopicIF journo1 = getTopicById(tm, "journo1");
    TopicIF employee = getTopicById(tm, "employee");
    TopicIF creator = getTopicById(tm, "creator"); 
    TopicIF taule = getTopicById(tm, "taule");
    TopicIF orgunit = getTopicById(tm, "org-unit");
    TopicIF itavd = getTopicById(tm, "it-avd");
    TopicIF service = getTopicById(tm, "service");
    TopicIF portal = getTopicById(tm, "portal");
    portlet.setWeakAssociationTypes(Collections.singleton(is_about));

    // start
    List headings = portlet.makeModel(topic);
    Assert.assertTrue("wrong number of headings",
               headings.size() == 4);
    
    // test first heading
    RelatedTopics.Heading heading =
      (RelatedTopics.Heading) headings.get(0);
    verifyHeading(heading, "first heading", "Created by",
                  created_by, work, 2);

    List assocs = heading.getChildren();
    Assert.assertTrue("first heading did not have exactly one child",
               assocs.size() == 1);
    RelatedTopics.Association assoc =
      (RelatedTopics.Association) assocs.get(0);
    verifyAssociation(assoc, "assoc 1.1", 2, Collections.EMPTY_SET,
                      null, journo1, creator);
    Assert.assertTrue("assoc 1.1 did not have exactly one role",
               assoc.getRoles().size() == 1);
    AssociationRoleIF role = (AssociationRoleIF)
      assoc.getRoles().iterator().next();
    Assert.assertTrue("assoc 1.1 not of type creator",
               role.getType().equals(creator));
    Assert.assertTrue("assoc 1.1 not played by journo1",
               role.getPlayer().equals(journo1));

    // test second heading
    heading = (RelatedTopics.Heading) headings.get(1);
    verifyHeading(heading, "second heading", "Employee",
                  employee, null, 2);

    assocs = heading.getChildren();
    Assert.assertTrue("second heading did not have exactly one child",
               assocs.size() == 1);
    assoc = (RelatedTopics.Association) assocs.get(0);
    verifyAssociation(assoc, "assoc 2.1", 2, Collections.EMPTY_SET,
                      null, taule, null);
    Assert.assertTrue("assoc 2.1 had roles",
               assoc.getRoles() == null);

    // test third heading
    heading = (RelatedTopics.Heading) headings.get(2);
    verifyHeading(heading, "third heading", "Organizational unit",
                  orgunit, null, 2);

    assocs = heading.getChildren();
    Assert.assertTrue("third heading did not have exactly one child",
               assocs.size() == 1);
    assoc = (RelatedTopics.Association) assocs.get(0);
    verifyAssociation(assoc, "assoc 3.1", 2, Collections.EMPTY_SET,
                      null, itavd, null);
    Assert.assertTrue("assoc 3.1 had roles",
               assoc.getRoles() == null);

    // test fourth heading
    heading = (RelatedTopics.Heading) headings.get(3);
    verifyHeading(heading, "fourth heading", "Service",
                  service, null, 2);

    assocs = heading.getChildren();
    Assert.assertTrue("fourth heading did not have exactly one child",
               assocs.size() == 1);
    assoc = (RelatedTopics.Association) assocs.get(0);
    verifyAssociation(assoc, "assoc 4.1", 2, Collections.EMPTY_SET,
                      null, portal, null);
    Assert.assertTrue("assoc 4.1 had roles",
               assoc.getRoles() == null);
  }  

  @Test
  public void testBinariesWithImplicitWeak() throws IOException {
    // initialize
    TopicMapIF tm = load("bk-example.ltm");
    TopicIF topic = getTopicById(tm, "article1");
    TopicIF created_by = getTopicById(tm, "created-by");
    TopicIF work = getTopicById(tm, "work");
    getTopicById(tm, "is-about");
    TopicIF journo1 = getTopicById(tm, "journo1");
    TopicIF employee = getTopicById(tm, "employee");
    TopicIF creator = getTopicById(tm, "creator"); 
    TopicIF taule = getTopicById(tm, "taule");
    TopicIF orgunit = getTopicById(tm, "org-unit");
    TopicIF itavd = getTopicById(tm, "it-avd");
    TopicIF service = getTopicById(tm, "service");
    TopicIF portal = getTopicById(tm, "portal");
    add(tm, "#PREFIX port @\"http://psi.ontopia.net/portlets/\" " +
            "port:not-semantic-type(is-about : port:type)");

    // start
    List headings = portlet.makeModel(topic);
    Assert.assertTrue("wrong number of headings: " + headings.size(),
               headings.size() == 4);
    
    // test first heading
    RelatedTopics.Heading heading =
      (RelatedTopics.Heading) headings.get(0);
    verifyHeading(heading, "first heading", "Created by",
                  created_by, work, 2);

    List assocs = heading.getChildren();
    Assert.assertTrue("first heading did not have exactly one child",
               assocs.size() == 1);
    RelatedTopics.Association assoc =
      (RelatedTopics.Association) assocs.get(0);
    verifyAssociation(assoc, "assoc 1.1", 2, Collections.EMPTY_SET,
                      null, journo1, creator);
    Assert.assertTrue("assoc 1.1 did not have exactly one role",
               assoc.getRoles().size() == 1);
    AssociationRoleIF role = (AssociationRoleIF)
      assoc.getRoles().iterator().next();
    Assert.assertTrue("assoc 1.1 not of type creator",
               role.getType().equals(creator));
    Assert.assertTrue("assoc 1.1 not played by journo1",
               role.getPlayer().equals(journo1));

    // test second heading
    heading = (RelatedTopics.Heading) headings.get(1);
    verifyHeading(heading, "second heading", "Employee",
                  employee, null, 2);

    assocs = heading.getChildren();
    Assert.assertTrue("second heading did not have exactly one child",
               assocs.size() == 1);
    assoc = (RelatedTopics.Association) assocs.get(0);
    verifyAssociation(assoc, "assoc 2.1", 2, Collections.EMPTY_SET,
                      null, taule, null);
    Assert.assertTrue("assoc 2.1 had roles",
               assoc.getRoles() == null);

    // test third heading
    heading = (RelatedTopics.Heading) headings.get(2);
    verifyHeading(heading, "third heading", "Organizational unit",
                  orgunit, null, 2);

    assocs = heading.getChildren();
    Assert.assertTrue("third heading did not have exactly one child",
               assocs.size() == 1);
    assoc = (RelatedTopics.Association) assocs.get(0);
    verifyAssociation(assoc, "assoc 3.1", 2, Collections.EMPTY_SET,
                      null, itavd, null);
    Assert.assertTrue("assoc 3.1 had roles",
               assoc.getRoles() == null);

    // test fourth heading
    heading = (RelatedTopics.Heading) headings.get(3);
    verifyHeading(heading, "fourth heading", "Service",
                  service, null, 2);

    assocs = heading.getChildren();
    Assert.assertTrue("fourth heading did not have exactly one child",
               assocs.size() == 1);
    assoc = (RelatedTopics.Association) assocs.get(0);
    verifyAssociation(assoc, "assoc 4.1", 2, Collections.EMPTY_SET,
                      null, portal, null);
    Assert.assertTrue("assoc 4.1 had roles",
               assoc.getRoles() == null);
  }  
  
  @Test
  public void testBinariesWithExplicitTopicHiding() throws IOException {
    // initialize
    TopicMapIF tm = load("bk-example.ltm");
    TopicIF topic = getTopicById(tm, "article1");
    TopicIF created_by = getTopicById(tm, "created-by");
    TopicIF work = getTopicById(tm, "work");
    TopicIF is_about = getTopicById(tm, "is-about");
    TopicIF journo1 = getTopicById(tm, "journo1");
    TopicIF creator = getTopicById(tm, "creator");
    TopicIF service = getTopicById(tm, "service");
    portlet.setExcludeTopicTypes(Collections.singleton(service));

    // start testing
    List headings = portlet.makeModel(topic);
    Assert.assertTrue("wrong number of headings",
               headings.size() == 2);
    
    // test first heading
    RelatedTopics.Heading heading =
      (RelatedTopics.Heading) headings.get(0);
    verifyHeading(heading, "first heading", "Created by",
                  created_by, work, 2);

    List assocs = heading.getChildren();
    Assert.assertTrue("first heading did not have exactly one child",
               assocs.size() == 1);
    RelatedTopics.Association assoc =
      (RelatedTopics.Association) assocs.get(0);
    verifyAssociation(assoc, "assoc 1.1", 2, Collections.EMPTY_SET,
                      null, journo1, creator);
    Assert.assertTrue("assoc 1.1 did not have exactly one role",
               assoc.getRoles().size() == 1);
    AssociationRoleIF role = (AssociationRoleIF)
      assoc.getRoles().iterator().next();
    Assert.assertTrue("assoc 1.1 not of type creator",
               role.getType().equals(creator));
    Assert.assertTrue("assoc 1.1 not played by journo1",
               role.getPlayer().equals(journo1));

    // test second heading
    heading = (RelatedTopics.Heading) headings.get(1);
    verifyHeading(heading, "second heading", "Is about",
                  is_about, work, 2);

    assocs = heading.getChildren();
    Assert.assertTrue("second heading had " + assocs.size() + " children",
               assocs.size() == 2);
    // FIXME: no defined order for associations, unfortunately
  }

  @Test
  public void testBinariesWithImplicitTopicHiding() throws IOException {
    // initialize
    TopicMapIF tm = load("bk-example.ltm");
    TopicIF topic = getTopicById(tm, "article1");
    TopicIF created_by = getTopicById(tm, "created-by");
    TopicIF work = getTopicById(tm, "work");
    TopicIF is_about = getTopicById(tm, "is-about");
    TopicIF journo1 = getTopicById(tm, "journo1");
    TopicIF creator = getTopicById(tm, "creator");
    getTopicById(tm, "service");
    add(tm, "#PREFIX port @\"http://psi.ontopia.net/portlets/\" " +
            "port:is-hidden-topic-type(service : port:type)");    

    // start testing
    List headings = portlet.makeModel(topic);
    Assert.assertTrue("wrong number of headings",
               headings.size() == 2);
    
    // test first heading
    RelatedTopics.Heading heading =
      (RelatedTopics.Heading) headings.get(0);
    verifyHeading(heading, "first heading", "Created by",
                  created_by, work, 2);

    List assocs = heading.getChildren();
    Assert.assertTrue("first heading did not have exactly one child",
               assocs.size() == 1);
    RelatedTopics.Association assoc =
      (RelatedTopics.Association) assocs.get(0);
    verifyAssociation(assoc, "assoc 1.1", 2, Collections.EMPTY_SET,
                      null, journo1, creator);
    Assert.assertTrue("assoc 1.1 did not have exactly one role",
               assoc.getRoles().size() == 1);
    AssociationRoleIF role = (AssociationRoleIF)
      assoc.getRoles().iterator().next();
    Assert.assertTrue("assoc 1.1 not of type creator",
               role.getType().equals(creator));
    Assert.assertTrue("assoc 1.1 not played by journo1",
               role.getPlayer().equals(journo1));

    // test second heading
    heading = (RelatedTopics.Heading) headings.get(1);
    verifyHeading(heading, "second heading", "Is about",
                  is_about, work, 2);

    assocs = heading.getChildren();
    Assert.assertTrue("second heading had " + assocs.size() + " children",
               assocs.size() == 2);
    // FIXME: no defined order for associations, unfortunately
  }

  @Test
  public void testBinariesWithFilter() throws IOException {
    // initialize
    portlet.setFilterQuery("instance-of(%topic%, employee)?");
    TopicMapIF tm = load("bk-example.ltm");
    TopicIF topic = getTopicById(tm, "article1");
    List headings = portlet.makeModel(topic);
    Assert.assertTrue("wrong number of headings",
               headings.size() == 2);
    TopicIF created_by = getTopicById(tm, "created-by");
    TopicIF work = getTopicById(tm, "work");
    TopicIF is_about = getTopicById(tm, "is-about");
    TopicIF journo1 = getTopicById(tm, "journo1");
    TopicIF creator = getTopicById(tm, "creator");

    // test first heading
    RelatedTopics.Heading heading =
      (RelatedTopics.Heading) headings.get(0);
    verifyHeading(heading, "first heading", "Created by",
                  created_by, work, 2);

    List assocs = heading.getChildren();
    Assert.assertTrue("first heading did not have exactly one child",
               assocs.size() == 1);
    RelatedTopics.Association assoc =
      (RelatedTopics.Association) assocs.get(0);
    verifyAssociation(assoc, "assoc 1.1", 2, Collections.EMPTY_SET,
                      null, journo1, creator);
    Assert.assertTrue("assoc 1.1 did not have exactly one role",
               assoc.getRoles().size() == 1);
    AssociationRoleIF role = (AssociationRoleIF)
      assoc.getRoles().iterator().next();
    Assert.assertTrue("assoc 1.1 not of type creator",
               role.getType().equals(creator));
    Assert.assertTrue("assoc 1.1 not played by journo1",
               role.getPlayer().equals(journo1));

    // test second heading
    heading = (RelatedTopics.Heading) headings.get(1);
    verifyHeading(heading, "second heading", "Is about",
                  is_about, work, 2);

    assocs = heading.getChildren();
    Assert.assertTrue("second heading did not have two children; had: " 
               + assocs.size(),
               assocs.size() == 2); // we filtered out the employee
    // FIXME: no defined order for associations, unfortunately
  }

  @Test
  public void testBinariesWithMax() throws IOException {
    // initialize
    TopicMapIF tm = load("bk-example.ltm");
    TopicIF topic = getTopicById(tm, "article1");
    portlet.setMaxChildren(2);
    List headings = portlet.makeModel(topic);
    Assert.assertTrue("wrong number of headings",
               headings.size() == 2);
    TopicIF created_by = getTopicById(tm, "created-by");
    TopicIF work = getTopicById(tm, "work");
    TopicIF is_about = getTopicById(tm, "is-about");
    getTopicById(tm, "journo1");
    getTopicById(tm, "creator");

    // test first heading
    RelatedTopics.Heading heading =
      (RelatedTopics.Heading) headings.get(0);
    verifyHeading(heading, "first heading", "Created by",
                  created_by, work, 2);

    List assocs = heading.getChildren();
    Assert.assertTrue("first heading did not have exactly one child",
               assocs.size() == 1);
    Assert.assertFalse("first heading claims to have more children",
                heading.getMoreChildren());

    // test second heading
    heading = (RelatedTopics.Heading) headings.get(1);
    verifyHeading(heading, "second heading", "Is about",
                  is_about, work, 2);

    assocs = heading.getChildren();
    Assert.assertTrue("second heading did not have exactly two children (max)",
               assocs.size() == 2);
    Assert.assertTrue("second heading claims not to have more children",
                heading.getMoreChildren());
  }  

  @Test
  public void testBinariesWithHeadingOrdering() throws IOException {
    // initialize
    TopicMapIF tm = load("bk-example.ltm");
    TopicIF topic = getTopicById(tm, "article1");
    portlet.setHeadingOrderQuery(
      "import \"http://psi.ontopia.net/tolog/string/\" as str " +
      "select $LENGTH from " +
      "  topic-name(%topic%, $TN), " +
      "  value($TN, $NAME), " +
      "  str:length($NAME, $LENGTH)" +
      "?");
    List headings = portlet.makeModel(topic);
    Assert.assertTrue("wrong number of headings",
               headings.size() == 2);
    TopicIF created_by = getTopicById(tm, "created-by");
    TopicIF work = getTopicById(tm, "work");
    TopicIF is_about = getTopicById(tm, "is-about");
    getTopicById(tm, "journo1");
    getTopicById(tm, "creator");

    // test SECOND heading
    RelatedTopics.Heading heading =
      (RelatedTopics.Heading) headings.get(1); // getting SECOND
    verifyHeading(heading, "second heading", "Created by",
                  created_by, work, 2);

    List assocs = heading.getChildren();
    Assert.assertTrue("second heading did not have exactly one child",
               assocs.size() == 1);
    Assert.assertFalse("second heading claims to have more children",
                heading.getMoreChildren());

    // test FIRST heading
    heading = (RelatedTopics.Heading) headings.get(0); // getting FIRST
    verifyHeading(heading, "first heading", "Is about",
                  is_about, work, 2);

    assocs = heading.getChildren();
    Assert.assertTrue("first heading did not have exactly two children (max)",
               assocs.size() == 3);
    Assert.assertFalse("first heading claims to have more children",
                heading.getMoreChildren());
  }

  @Test
  public void testBinariesWithChildOrdering() throws IOException {
    // initialize
    TopicMapIF tm = load("bk-example.ltm");
    TopicIF topic = getTopicById(tm, "article1");
    portlet.setChildOrderQuery("item-identifier(%topic%, $ITEMID)?");
    List headings = portlet.makeModel(topic);
    Assert.assertTrue("wrong number of headings",
               headings.size() == 2);
    TopicIF created_by = getTopicById(tm, "created-by");
    TopicIF work = getTopicById(tm, "work");
    TopicIF is_about = getTopicById(tm, "is-about");
    getTopicById(tm, "journo1");
    getTopicById(tm, "creator");
    TopicIF taule = getTopicById(tm, "taule");
    TopicIF it_avd = getTopicById(tm, "it-avd");
    TopicIF portal = getTopicById(tm, "portal");
    TopicIF subject = getTopicById(tm, "subject");

    // test first heading
    RelatedTopics.Heading heading =
      (RelatedTopics.Heading) headings.get(0);
    verifyHeading(heading, "first heading", "Created by",
                  created_by, work, 2);
    // only one child, so nothing further to test

    // test second heading
    heading = (RelatedTopics.Heading) headings.get(1);
    verifyHeading(heading, "second heading", "Is about",
                  is_about, work, 2);

    List assocs = heading.getChildren();
    Assert.assertTrue("second heading did not have exactly three children",
               assocs.size() == 3);

    // test assocs
    RelatedTopics.Association assoc =
      (RelatedTopics.Association) assocs.get(0);
    verifyAssociation(assoc, "first association", 2, Collections.EMPTY_SET,
                      null, it_avd, subject);
    
    assoc = (RelatedTopics.Association) assocs.get(1);    
    verifyAssociation(assoc, "second association", 2, Collections.EMPTY_SET,
                      null, portal, subject);
  
    assoc = (RelatedTopics.Association) assocs.get(2);    
    verifyAssociation(assoc, "third association", 2, Collections.EMPTY_SET,
                      null, taule, subject);
  }
  
  // --- Helpers

  private void add(TopicMapIF tm, String ltm) throws IOException {
    LocatorIF base = tm.getStore().getBaseAddress();
    StringReader in = new StringReader(ltm);
    LTMTopicMapReader reader = new LTMTopicMapReader(in, base);
    reader.importInto(tm);
  }
  
  private void verifyHeading(RelatedTopics.Heading heading,
                             String name,
                             String title, TopicIF assoctype, TopicIF roletype,
                             int arity) {
    boolean isassoctype = (roletype != null);
    Assert.assertTrue("wrong name of " + name,
               heading.getTitle().equals(title));
    Assert.assertTrue("wrong type of " + name + ": " + heading.getTopic(),
               heading.getTopic().equals(assoctype));
    Assert.assertTrue("wrong near role type of " + name + ": " +
               heading.getNearRoleType(),
               (roletype == null && heading.getNearRoleType() == null) ||
               (heading.getNearRoleType() != null &&
                heading.getNearRoleType().equals(roletype)));
    Assert.assertTrue(name + " is not association type",
               heading.getIsAssociationType() == isassoctype);
    Assert.assertTrue(name + " is topic type",
               !heading.getIsTopicType() == isassoctype);
    Assert.assertTrue(name + " does not have arity " + arity,
               heading.getArity() == arity);
    
  }
  
  private void verifyAssociation(RelatedTopics.Association assoc,
                                 String name, int arity, Collection scope,
                                 TopicIF reifier, TopicIF player,
                                 TopicIF ortype) {
    Assert.assertTrue(name + " has arity " + assoc.getArity() + ", not " + arity,
               assoc.getArity() == arity);
    Assert.assertTrue(name + " has bad scope", compare(scope, assoc.getScope()));
    Assert.assertTrue(name + " has bad reifier", reifier == assoc.getReifier());
    Assert.assertTrue(name + " has bad player " + assoc.getPlayer() + ", not " + player,
               player == assoc.getPlayer());
    Assert.assertTrue(name + " has bad role type: " + assoc.getRoleType(),
               ortype == assoc.getRoleType());
  }

  private boolean compare(Collection c1, Collection c2) {
    return c1.size() == c2.size(); // FIXME: only works for empty...
  }
  
  private TopicMapIF load(String filename) throws IOException {
    filename = TestFileUtils.getTestInputFile(testdataDirectory, "topicmaps", filename);
    return ImportExportUtils.getReader(filename).read();
  }

  private TopicIF getTopicById(TopicMapIF topicmap, String id) {
    LocatorIF srcloc =
      topicmap.getStore().getBaseAddress().resolveAbsolute("#" + id);
    return (TopicIF) topicmap.getObjectByItemIdentifier(srcloc);
  }
  
  private TopicIF getTopicByPSI(TopicMapIF topicmap, String psi) {
    return topicmap.getTopicBySubjectIdentifier(URILocator.create(psi));
  }

  @Test
  public void testBinariesWithMaxChildren() throws IOException {
    _testBinariesWithMaxChildren(1, true);
    _testBinariesWithMaxChildren(2, true);
    _testBinariesWithMaxChildren(3, false);
  }

  private void _testBinariesWithMaxChildren(int maxchildren, boolean morechildrenOnSecond) throws IOException {  
    // initialize
    portlet.setMaxChildren(maxchildren);
    try {
      TopicMapIF tm = load("bk-example.ltm");
      TopicIF topic = getTopicById(tm, "article1");
      List headings = portlet.makeModel(topic);
      Assert.assertTrue("wrong number of headings",
                 headings.size() == 2);
      
      TopicIF created_by = getTopicById(tm, "created-by");
      TopicIF is_about = getTopicById(tm, "is-about");
      
      // test first heading
      RelatedTopics.Heading heading =
        (RelatedTopics.Heading) headings.get(0);
      Assert.assertTrue("first heading refers to wrong topic", heading.getTopic().equals(created_by));    
      Assert.assertTrue("first heading did not have exactly one child",
                 heading.getChildren().size() == 1);
      Assert.assertTrue("first heading did not have morechildren set to false",
                 !heading.getMoreChildren());
      
      // test second heading
      heading = (RelatedTopics.Heading) headings.get(1);
      Assert.assertTrue("second heading refers to wrong topic", heading.getTopic().equals(is_about));    
      Assert.assertTrue("second heading with max number of children has incorrect morechildren property",
                 heading.getChildren().size() == maxchildren);
      Assert.assertTrue("second heading did not have morechildren set to " + morechildrenOnSecond,
                 heading.getMoreChildren() == morechildrenOnSecond);
    } finally {
      portlet.setMaxChildren(-1);
    }
  }

  @Test
  public void testSortKeys() throws IOException {
    // initialize
    portlet.setHeadingOrderQuery("select $KEY from sortkey(%topic%, $KEY)?");
    portlet.setChildOrderQuery("select $KEY from sortkey(%topic%, $KEY)?");
    // test: headings and children should come in reverse order
    try {      
      TopicMapIF tm = load("bk-example-sortkeys.ltm");
      TopicIF topic = getTopicById(tm, "article1");
      List headings = portlet.makeModel(topic);
      List children;
      Assert.assertTrue("wrong number of headings", headings.size() == 2);
      
      // test first heading
      RelatedTopics.Heading heading =
        (RelatedTopics.Heading) headings.get(0);
      Assert.assertTrue("first heading refers to wrong topic", heading.getTopic().equals(getTopicById(tm, "is-about")));    
      children = heading.getChildren();
      Assert.assertTrue("first heading did not have exactly three children", children.size() == 4);
      Assert.assertTrue("first heading first child incorrect", childPlayerEquals(children, 0, getTopicById(tm, "soccer")));
      Assert.assertTrue("first heading first child incorrect", childPlayerEquals(children, 1, getTopicById(tm, "javelin")));
      Assert.assertTrue("first heading second child incorrect", childPlayerEquals(children, 2, getTopicById(tm, "icehockey")));
      Assert.assertTrue("first heading third child incorrect", childPlayerEquals(children, 3, getTopicById(tm, "football")));
      
      // test second heading
      heading = (RelatedTopics.Heading) headings.get(1);
      Assert.assertTrue("second heading refers to wrong topic", heading.getTopic().equals(getTopicById(tm, "created-by")));
      children = heading.getChildren();
      Assert.assertTrue("second heading did not have exactly two children", children.size() == 2);
      Assert.assertTrue("second heading first child incorrect", childPlayerEquals(children, 0, getTopicById(tm, "john")));
      Assert.assertTrue("second heading second child incorrect", childPlayerEquals(children, 1, getTopicById(tm, "jane")));
      
    } finally {
      portlet.setHeadingOrderQuery(null);
      portlet.setChildOrderQuery(null);
    }
  }

  @Test
  public void testSortKeysDesc() throws IOException {
    // initialize
    portlet.setHeadingOrderQuery("select $KEY from sortkey(%topic%, $KEY)?");
    portlet.setHeadingOrdering(RelatedTopics.ORDERING_DESC);
    portlet.setChildOrderQuery("select $KEY from sortkey(%topic%, $KEY)?");
    portlet.setChildOrdering(RelatedTopics.ORDERING_DESC);
    // test: headings and children should come in reverse order
    try {      
      TopicMapIF tm = load("bk-example-sortkeys.ltm");
      TopicIF topic = getTopicById(tm, "article1");
      List headings = portlet.makeModel(topic);
      List children;
      Assert.assertTrue("wrong number of headings", headings.size() == 2);

      RelatedTopics.Heading heading;
      
      // test first heading
      heading = (RelatedTopics.Heading) headings.get(0);
      Assert.assertTrue("first heading refers to wrong topic", heading.getTopic().equals(getTopicById(tm, "created-by")));
      children = heading.getChildren();
      Assert.assertTrue("first heading did not have exactly two children", children.size() == 2);
      Assert.assertTrue("first heading first child incorrect", childPlayerEquals(children, 0, getTopicById(tm, "jane")));
      Assert.assertTrue("first heading second child incorrect", childPlayerEquals(children, 1, getTopicById(tm, "john")));
      
      // test second heading
      heading =
        (RelatedTopics.Heading) headings.get(1);
      Assert.assertTrue("second heading refers to wrong topic", heading.getTopic().equals(getTopicById(tm, "is-about")));    
      children = heading.getChildren();
      Assert.assertTrue("second heading did not have exactly three children", children.size() == 4);
      Assert.assertTrue("second heading first child incorrect", childPlayerEquals(children, 0, getTopicById(tm, "football")));
      Assert.assertTrue("second heading first child incorrect", childPlayerEquals(children, 1, getTopicById(tm, "icehockey")));
      Assert.assertTrue("second heading second child incorrect", childPlayerEquals(children, 2, getTopicById(tm, "javelin")));
      Assert.assertTrue("second heading third child incorrect", childPlayerEquals(children, 3, getTopicById(tm, "soccer")));
      
    } finally {
      portlet.setHeadingOrderQuery(null);
      portlet.setHeadingOrdering(RelatedTopics.ORDERING_ASC);
      portlet.setChildOrderQuery(null);
      portlet.setChildOrdering(RelatedTopics.ORDERING_ASC);
    }
  }

  @Test
  public void testAssociationAggregation() throws IOException {
    try {
      TopicMapIF tm = load("i18n-20070730.ltm");
     
      // initialize
      Set atypes = new HashSet();
      atypes.add(getTopicByPSI(tm, "http://psi.ontopia.net/i18n/#written-in"));
      atypes.add(getTopicByPSI(tm, "http://psi.ontopia.net/i18n/#supports"));
      atypes.add(getTopicByPSI(tm, "http://psi.ontopia.net/i18n/#writing-direction"));
      Assert.assertTrue("Could not find all association types.", atypes.size() == 3);

      TopicIF topic = getTopicById(tm, "sinitic");
      List headings;
      RelatedTopics.Heading heading;
      
      // aggregation=false
      portlet.setAggregateHierarchy(false);

      headings = portlet.makeModel(topic);
      Assert.assertTrue("Incorrect number of headings before aggregation.", headings.size() == 3);

      // aggregation=true, all associations
      portlet.setAggregateHierarchy(true);

      headings = portlet.makeModel(topic);
      Assert.assertTrue("Incorrect number of headings after full aggregation.", headings.size() == 5);

      // aggregation=true, some associations
      portlet.setAggregateHierarchy(true);
      portlet.setAggregateAssociations(atypes);

      headings = portlet.makeModel(topic);
      Assert.assertTrue("Incorrect number of headings after partial aggregation.", headings.size() == 3);

      heading = (RelatedTopics.Heading) headings.get(0);
      Assert.assertTrue("first heading refers to wrong topic", heading.getTopic().equals(getTopicByPSI(tm, "http://psi.ontopia.net/i18n/#supports")));
      Assert.assertTrue("Incorrect number of children under first heading.", heading.getChildren().size() == 11);

      heading = (RelatedTopics.Heading) headings.get(1);
      Assert.assertTrue("second heading refers to wrong topic", heading.getTopic().equals(getTopicByPSI(tm, "http://psi.ontopia.net/i18n/#written-in")));
      Assert.assertTrue("Incorrect number of children under second heading.", heading.getChildren().size() == 7);

      heading = (RelatedTopics.Heading) headings.get(2);
      Assert.assertTrue("third heading refers to wrong topic", heading.getTopic().equals(getTopicByPSI(tm, "http://psi.ontopia.net/i18n/#writing-direction")));
      Assert.assertTrue("Incorrect number of children under third heading.", heading.getChildren().size() == 1);
      
    } finally {
      portlet.setAggregateHierarchy(false);
      portlet.setAggregateAssociations(Collections.EMPTY_SET);
    }
  }

  // -- internal helpers
  
  private boolean childPlayerEquals(List children, int index, TopicIF player) {
    RelatedTopics.Association assoc = (RelatedTopics.Association) children.get(index);
    return assoc.getPlayer().equals(player);
  }
  
}
