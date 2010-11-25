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
