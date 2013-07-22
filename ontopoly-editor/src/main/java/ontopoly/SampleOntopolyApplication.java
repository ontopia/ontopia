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
package ontopoly;

import org.apache.wicket.protocol.http.WebRequestCycleProcessor;
import org.apache.wicket.request.IRequestCycleProcessor;
import ontopoly.model.Topic;

public class SampleOntopolyApplication extends OntopolyApplication {

	@Override
	protected IRequestCycleProcessor newRequestCycleProcessor() {
		// NOTE: don't generate absolute URLs
		return new WebRequestCycleProcessor();
	}

	@Override
	protected OntopolyAccessStrategy newAccessStrategy() {
		return new OntopolyAccessStrategy() {
			@Override
			public User authenticate(String username, String password) {
		    // let users in if their password is the same as the username
				if (username != null && password != null && username.equals(password)) {
				    return new User(username, false);
				} else {
					return null;
				}
			}
			@Override
			public Privilege getPrivilege(User user, Topic topic) {
			  // protect the ontology
				if (topic.isOntologyTopic() || topic.isSystemTopic() || topic.isFieldDefinition() || topic.isTopicMap()) {
					return Privilege.READ_ONLY;
				} else {
					return Privilege.EDIT;
				}
					
			}
		};
	}

}
