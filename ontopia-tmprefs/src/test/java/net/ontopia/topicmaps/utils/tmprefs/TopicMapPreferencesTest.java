/*
 * #!
 * Ontopia TopicMapPreferences
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

package net.ontopia.topicmaps.utils.tmprefs;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.utils.ltm.LTMTopicMapWriter;
import net.ontopia.topicmaps.xml.CanonicalXTMWriter;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TopicMapPreferencesTest {
	
	private final static String testdataDirectory = "tmprefs";
	private final static String DEFAULTVALUE = "____DEFAULT_VALUE____";
	
	private String base;
	private String filename;

  @Parameters
	public static List generateTests() {
		return TestFileUtils.getTestInputFiles(testdataDirectory, "in", ".ltm");
	}

	public TopicMapPreferencesTest(String root, String filename) {
		this.filename = filename;
		this.base = TestFileUtils.getTestdataOutputDirectory() + testdataDirectory;
	}
	
	@Test
	public void testFile() throws IOException, BackingStoreException {
		TestFileUtils.verifyDirectory(base, "out");
		TestFileUtils.verifyDirectory(base, "ltm");
		String in = TestFileUtils.getTestInputFile(testdataDirectory, "in", filename);
		File out = new File(base + File.separator + "out" + File.separator + filename + ".cxtm");
		File ltm = new File(base + File.separator + "ltm" + File.separator + filename + ".ltm");
		TestTMPrefsFactory.setFixedReference(in);
		
		runPreferencesMethods();
		
		TopicMapReferenceIF reference = TestTMPrefsFactory.getSystemTopicMapReference();
		TopicMapStoreIF store = reference.createStore(true);
		TopicMapIF topicmap = store.getTopicMap();
		
		new LTMTopicMapWriter(ltm).write(topicmap);

		new CanonicalXTMWriter(out).write(topicmap);

		// // compare results
		// Assert.assertTrue("Canonicalizing the test file " + filename +
		// 	" produces " + out + " which is different from " +
		// 	baseline, FileUtils.compareFileToResource(out, baseline));

		TestTMPrefsFactory.setFixedReference(null);
		store.close();
	}
	
	private void runPreferencesMethods() throws BackingStoreException {

		Preferences prefs1 = Preferences.systemNodeForPackage(net.ontopia.topicmaps.core.TopicIF.class);
		Preferences prefs2 = Preferences.systemNodeForPackage(net.ontopia.topicmaps.impl.basic.Topic.class);

		prefs1.put("new-key", "new-value-prefs1");
		prefs2.put("new-key", "new-value-prefs2");
		prefs2.put("prop-a",  "existing-value-prefs2");
		
		Assert.assertEquals(prefs2.get("prop-a", DEFAULTVALUE), "existing-value-prefs2");
		
		Preferences removePrefs = Preferences.systemRoot().node("net/ontopia/topicmaps/remove");
		removePrefs.removeNode();
		removePrefs.flush();
		
		Preferences.systemRoot().flush();
	}
}
