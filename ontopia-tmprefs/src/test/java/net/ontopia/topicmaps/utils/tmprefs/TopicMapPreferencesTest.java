
package net.ontopia.topicmaps.utils.tmprefs;

import org.junit.Test;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.IOException;
import java.util.prefs.Preferences;
import java.util.prefs.BackingStoreException;

import java.util.List;
import java.io.FileOutputStream;
import java.io.File;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.xml.CanonicalXTMWriter;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.TestFileUtils;
import net.ontopia.topicmaps.utils.ltm.LTMTopicMapWriter;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;

@RunWith(Parameterized.class)
public class TopicMapPreferencesTest {
	
	private final static String testdataDirectory = "tmprefs";
	private final static String DEFAULTVALUE = "____DEFAULT_VALUE____";
	
	@Parameters
	public static List generateTests() {
		return TestFileUtils.getTestInputFiles(testdataDirectory, "in", ".ltm");
	}

	private String base;
	private String filename;
        
	public TopicMapPreferencesTest(String root, String filename) {
		this.filename = filename;
		this.base = TestFileUtils.getTestdataOutputDirectory() + testdataDirectory;
	}
	
	@Test
	public void testFile() throws IOException, BackingStoreException {
		TestFileUtils.verifyDirectory(base, "out");
		TestFileUtils.verifyDirectory(base, "ltm");
		String in = TestFileUtils.getTestInputFile(testdataDirectory, "in", filename);
		String baseline = TestFileUtils.getTestInputFile(testdataDirectory, "baseline", filename + ".cxtm");
		String out = base + File.separator + "out" + File.separator + filename + ".cxtm";
		String ltm = base + File.separator + "ltm" + File.separator + filename + ".ltm";
		TestTMPrefsFactory.setFixedReference(in);
		
		runPreferencesMethods();
		
		TopicMapReferenceIF reference = TestTMPrefsFactory.getSystemTopicMapReference();
		TopicMapStoreIF store = reference.createStore(true);
		TopicMapIF topicmap = store.getTopicMap();
		
		FileOutputStream ltmfos = new FileOutputStream(ltm);
		(new LTMTopicMapWriter(ltmfos)).write(topicmap);
		ltmfos.close();

		FileOutputStream cxtmfos = new FileOutputStream(out);
		(new CanonicalXTMWriter(cxtmfos)).write(topicmap);
		cxtmfos.close();

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
