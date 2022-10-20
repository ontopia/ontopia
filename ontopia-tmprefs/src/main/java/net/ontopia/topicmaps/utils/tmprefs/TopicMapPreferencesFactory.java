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

  @Override
	public Preferences systemRoot() {
		String key = getSystemKey();
		if (systemRoots.get(key) == null) {
			TopicMapReferenceIF topicMapReference = getSystemTopicMapReference(key);
			if (topicMapReference == null) {
        throw new RuntimeException(new BackingStoreException("System: Topicmap with key '" + key + "' was not resolved!"));
      }
			Preferences systemPreferences = TopicMapPreferences.createSystemRoot(topicMapReference, this);
			systemRoots.put(key, systemPreferences);
		}
		return systemRoots.get(key);
	}
	
  @Override
	public Preferences userRoot() {
		String key = getUserKey();
		if (userRoots.get(key) == null) {
			TopicMapReferenceIF topicMapReference = getUserTopicMapReference(key);
			if (topicMapReference == null) {
        throw new RuntimeException(new BackingStoreException("User: Topicmap with key '" + key + "' was not resolved!"));
      }
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
