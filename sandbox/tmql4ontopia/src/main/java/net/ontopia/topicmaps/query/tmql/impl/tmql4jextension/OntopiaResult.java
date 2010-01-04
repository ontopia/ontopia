package net.ontopia.topicmaps.query.tmql.impl.tmql4jextension;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.tmapi.core.Association;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;

import de.topicmapslab.tmql4j.resultprocessing.model.IResult;

public class OntopiaResult implements IResult {

	private final List<Object> results;
	private final List<Object> ontopiaStringRepresentations;

	public OntopiaResult() {
		this.results = new LinkedList<Object>();
		this.ontopiaStringRepresentations = new LinkedList<Object>();
	}

	public OntopiaResult(Collection<Object> results) {
		this.results = new LinkedList<Object>();
		this.ontopiaStringRepresentations = new LinkedList<Object>();
		this.results.addAll(results);
		for (Object o : results) {
			this.ontopiaStringRepresentations.add(tmapiObjectToString(o));
		}
	}

	public OntopiaResult(Object... results) {
		this.results = Arrays.asList(results);
		this.ontopiaStringRepresentations = new LinkedList<Object>();
		for (Object o : results) {
			this.ontopiaStringRepresentations.add(tmapiObjectToString(o));
		}
	}

	public Object first() {
		return ontopiaStringRepresentations.iterator().next();
		// return results.iterator().next();
	}

	public Object last() {
		return ontopiaStringRepresentations.get(ontopiaStringRepresentations
				.size() - 1);
		// return results.get(results.size() - 1);
	}

	public Iterator<Object> iterator() {
		return ontopiaStringRepresentations.iterator();
		// return results.iterator();
	}

	public void add(Object... values) {
		results.addAll(Arrays.asList(values));
		for (Object o : values) {
			this.ontopiaStringRepresentations.add(tmapiObjectToString(o));
		}
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		Iterator<Object> iterator = iterator();
		while (iterator.hasNext()) {
			builder.append(iterator.next().toString()
					+ (iterator.hasNext() ? ", " : ""));
		}
		builder.append("]");
		return builder.toString();
	}

	public int size() {
		return this.results.size();
	}

	public Object[] getValues() {
		return ontopiaStringRepresentations.toArray();
		// return results.toArray();
	}

	public Object[] getValues(Integer... indizes) {
		List<Object> values = new LinkedList<Object>();
		for (Integer index : indizes) {
			if (index < size()) {
				// values.add(results.get(index));
				values.add(ontopiaStringRepresentations.get(index));
			}
		}
		return values.toArray();
	}

	public Object getValue(Integer index) {
		if (index < size()) {
			return ontopiaStringRepresentations.get(index);
			// return results.get(index);
		}
		return null;
	}

	private String tmapiObjectToString(Object object) {
		if (object instanceof Topic) {
			Topic topic = (Topic) object;
			if (!topic.getItemIdentifiers().isEmpty()) {
				return topic.getItemIdentifiers().iterator().next()
						.getReference();
			} else if (!topic.getSubjectIdentifiers().isEmpty()) {
				return topic.getSubjectIdentifiers().iterator().next()
						.getReference();
			} else if (!topic.getSubjectLocators().isEmpty()) {
				return topic.getSubjectLocators().iterator().next()
						.getReference();
			}
		} else if (object instanceof Association) {
			Association association = (Association) object;
			if (!association.getItemIdentifiers().isEmpty()) {
				return association.getItemIdentifiers().iterator().next()
						.getReference();
			}
		} else if (object instanceof Name) {
			return ((Name) object).getValue();
		} else if (object instanceof Variant) {
			return ((Variant) object).getValue();
		} else if (object instanceof Occurrence) {
			return ((Occurrence) object).getValue();
		} else if (object instanceof Role) {
			Role role = (Role) object;
			return tmapiObjectToString(role.getType());
		} else if (object instanceof Locator) {
			return ((Locator) object).getReference();
		}
		return "FAILED: " + object.toString();
	}

	public boolean canReduceTo2Dimensions() {
		return false;
	}

	public List<Object> getResults() {
		return results;
	}

	public Collection<? extends Collection<? extends Object>> reduceTo2Dimensions()
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

}
