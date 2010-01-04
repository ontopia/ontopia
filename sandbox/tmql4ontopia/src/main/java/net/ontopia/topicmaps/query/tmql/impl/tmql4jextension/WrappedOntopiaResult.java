/**
 * TMQL4J Plugin for Ontopia
 * 
 * Copyright: Copyright 2009 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Author: Sven Krosse
 * 
 */
package net.ontopia.topicmaps.query.tmql.impl.tmql4jextension;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.impl.tmapi2.OntopiaToTMAPIWrapper;

import org.tmapi.core.Construct;
import org.tmapi.core.Locator;

import de.topicmapslab.tmql4j.common.model.tuplesequence.ITupleSequence;
import de.topicmapslab.tmql4j.resultprocessing.model.IResult;

public class WrappedOntopiaResult implements IResult {

	private final List<Object> results;

	public WrappedOntopiaResult() {
		this.results = new LinkedList<Object>();
	}

	public WrappedOntopiaResult(Collection<Object> results) {
		this.results = new LinkedList<Object>();
		add(results);
	}

	public WrappedOntopiaResult(Object... results) {
		this.results = new LinkedList<Object>();
		add(results);
	}

	public void add(Object... values) {
		for (Object o : values) {
			if (o instanceof ITupleSequence<?>) {
				this.results.add(OntopiaToTMAPIWrapper
						.toTMObjectIF((ITupleSequence<?>) o));
			} else if (o instanceof Construct) {
				Object obj = OntopiaToTMAPIWrapper.toTMObjectIF(o);
				if (obj instanceof OccurrenceIF) {
					this.results.add(OntopiaToTMAPIWrapper
							.toTMObjectIF((OccurrenceIF) obj));
				} else {
					this.results.add(obj);
				}

			} else if (o instanceof Locator) {
				this.results.add(((Locator) o).toExternalForm());
			} else {
				this.results.add(o);
			}
		}
	}

	public Object first() {
		return results.iterator().next();
	}

	public Object last() {
		return results.get(results.size() - 1);
	}

	public Iterator<Object> iterator() {
		return results.iterator();
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
		return results.toArray();
	}

	public Object[] getValues(Integer... indizes) {
		List<Object> values = new LinkedList<Object>();
		for (Integer index : indizes) {
			if (index < size()) {
				values.add(results.get(index));
			}
		}
		return values.toArray();
	}

	public Object getValue(Integer index) {
		if (index < size()) {
			return results.get(index);
		}
		return null;
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
