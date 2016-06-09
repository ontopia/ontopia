package net.ontopia.topicmaps.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashSet;
import java.util.Set;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestException;
import org.restlet.data.Status;

@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class Error {

	@JsonProperty
	private int httpcode;
	
	@JsonProperty
	private int code = -1;
	
	@JsonProperty
	private String message;
	
	@JsonProperty
	private String description;
	
	@JsonProperty
	private String[] causes = new String[0];

	public Error(Status status) {
		this.httpcode = status.getCode();
		this.message = status.toString();
		this.description = status.getDescription();
		if (status.getThrowable() != null) {
			Throwable t = status.getThrowable();
			if (t instanceof OntopiaRestException) {
				code = ((OntopiaRestException) t).getOntopiaCode();
			}
			Set<String> c = new HashSet<>();
			while (t.getCause() != null) {
				t = t.getCause();
				c.add(t.getClass().getName() + ": " + (t.getMessage() != null ? t.getMessage() : ""));
			}
			if (!c.isEmpty()) {
				causes = c.toArray(causes);
			}
		}
	}

	// for jackson
	public Error() {
	}

	public int getHttpcode() {
		return httpcode;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public String getDescription() {
		return description;
	}

	public String[] getCauses() {
		return causes;
	}
}
