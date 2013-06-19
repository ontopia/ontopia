
package net.ontopia.topicmaps.utils.tmprefs;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;

public abstract class TopicMapPreferencesFactory implements PreferencesFactory {

	protected Map<String, Preferences> systemRoots = new HashMap<String, Preferences>();
	protected Map<String, Preferences> userRoots   = new HashMap<String, Preferences>();
	
	public static final String FIXEDSYSTEMKEY = "SYSTEM";

	public Preferences systemRoot() {
		String key = getSystemKey();
		if (systemRoots.get(key) == null) {
			TopicMapReferenceIF topicMapReference = getSystemTopicMapReference(key);
			if (topicMapReference == null) throw new RuntimeException(new BackingStoreException("System: Topicmap with key '" + key + "' was not resolved!"));
			Preferences systemPreferences = TopicMapPreferences.createSystemRoot(topicMapReference, this);
			systemRoots.put(key, systemPreferences);
		}
		return systemRoots.get(key);
	}
	
	public Preferences userRoot() {
		String key = getUserKey();
		if (userRoots.get(key) == null) {
			TopicMapReferenceIF topicMapReference = getUserTopicMapReference(key);
			if (topicMapReference == null) throw new RuntimeException(new BackingStoreException("User: Topicmap with key '" + key + "' was not resolved!"));
			Preferences userPreferences = TopicMapPreferences.createUserRoot(topicMapReference, this); 
			userRoots.put(key, userPreferences);
		}
		return userRoots.get(key);
	}

	protected String getSystemKey() {
		return FIXEDSYSTEMKEY;
	}
	protected abstract String getUserKey();

	protected abstract TopicMapReferenceIF getSystemTopicMapReference(String key);
	protected abstract TopicMapReferenceIF getUserTopicMapReference(String key);

	protected TopicIF createSystemRootTopic(TopicMapIF topicmap) throws BackingStoreException {
		return TopicMapPreferencesOntology.SYSTEMROOT.t(topicmap);
	}
	protected abstract TopicIF createUserRootTopic(TopicMapIF topicmap) throws BackingStoreException;

}
