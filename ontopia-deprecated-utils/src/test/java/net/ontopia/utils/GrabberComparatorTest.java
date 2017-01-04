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

package net.ontopia.utils;

import java.util.Comparator;
import org.junit.Test;

public class GrabberComparatorTest extends AbstractComparatorTest {

  @Test
  public void testGrabberComparator() {
    UpperCaseGrabber upg = new UpperCaseGrabber();
    GrabberIF<String, ?> log = String::toLowerCase;
    Comparator sc = LexicalComparator.CASE_SENSITIVE;
    Comparator isc = new GrabberComparator(new UpperCaseGrabber(), sc);

    assertComparator(new GrabberComparator(upg, sc).compare("foobar", "FOOBAR"), 0, 1);
    assertComparator(new GrabberComparator(upg, isc).compare("foobar", "FoOBAR"), 0, 1);
    assertComparator(new GrabberComparator(upg, log, sc).compare("foobar", "FoOBAR"), 
                   new GrabberComparator(log, upg, sc).compare("foobar", "FoOBAR") * -1, 
                   new GrabberComparator(upg, isc).compare("foobar", "FoOBAR "));
  }

}
