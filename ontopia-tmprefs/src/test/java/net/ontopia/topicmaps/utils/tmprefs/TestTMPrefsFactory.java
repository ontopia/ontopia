
package net.ontopia.topicmaps.utils.tmprefs;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.utils.ltm.LTMTopicMapReference;
import net.ontopia.utils.StreamUtils;
import java.io.IOException;

public class TestTMPrefsFactory extends TopicMapPreferencesFactory {

	private static final ThreadLocal<String> fixedReferences = new ThreadLocal<String>();
	private static final ThreadLocal<TopicMapReferenceIF> references = new ThreadLocal<TopicMapReferenceIF>();

	public static void setFixedReference(String key) {
		fixedReferences.set(key);
		references.set(null);
	}

	public static TopicMapReferenceIF getSystemTopicMapReference() {
		return references.get();
	}
	

	protected String getSystemKey() {
		return fixedReferences.get();
	}

	protected TopicMapReferenceIF getSystemTopicMapReference(String key) {
		TopicMapReferenceIF reference = references.get();
		if (reference == null) {
			try {
				reference = new LTMTopicMapReference(StreamUtils.getResource(key), key, key);
				references.set(reference);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return reference;
	}
	
	
	protected TopicMapReferenceIF getUserTopicMapReference(String key) {
		return null; // not used
	}
	protected String getUserKey() {
		return null; // not used
	}
	protected TopicIF createUserRootTopic(TopicMapIF topicmap) {
		return null; // not used
	}
	
}
