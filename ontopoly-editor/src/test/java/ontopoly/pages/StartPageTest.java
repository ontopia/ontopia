/*
 * #!
 * Ontopoly Editor
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
package ontopoly.pages;

import ontopoly.OntopolyApplication;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;

public class StartPageTest {
  private WicketTester tester;
  
  @Before
  public void setUp() throws Exception {
    tester = new WicketTester(new OntopolyApplication());
    tester.startPage(StartPage.class);
    tester.assertNoErrorMessage();
  }
  
  @Test
  public void testDisplayStartPage() throws Exception {
    tester.assertComponent("titlePartPanel", Panel.class);
    tester.assertComponent("createNewTopicMapPanel", Panel.class);
  }

}
