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

import java.io.IOException;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.utils.ltm.LTMTopicMapReference;
import net.ontopia.utils.StreamUtils;

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
	

  @Override
	protected String getSystemKey() {
		return fixedReferences.get();
	}

  @Override
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
	
	
  @Override
	protected TopicMapReferenceIF getUserTopicMapReference(String key) {
		return null; // not used
	}
  @Override
	protected String getUserKey() {
		return null; // not used
	}
  @Override
	protected TopicIF createUserRootTopic(TopicMapIF topicmap) {
		return null; // not used
	}
	
}
