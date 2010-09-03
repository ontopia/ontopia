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
		// NOTE: let users in if their password is the same as the username
		return new OntopolyAccessStrategy() {
			@Override
			public User authenticate(String username, String password) {
				if (username != null && password != null && username.equals(password)) {
				    return new User(username, false);
				} else {
					return null;
				}
			}
			@Override
			public Privilege getPrivilege(User user, Topic topic) {
				if (topic.isOntologyTopic() || topic.isSystemTopic()) {
					return Privilege.READ_ONLY;
				} else {
					return Privilege.EDIT;
				}
					
			}
		};
	}

}
