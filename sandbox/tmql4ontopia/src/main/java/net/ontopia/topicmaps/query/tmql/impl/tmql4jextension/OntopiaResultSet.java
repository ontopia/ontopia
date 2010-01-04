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

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.topicmapslab.tmql4j.resultprocessing.model.IResult;
import de.topicmapslab.tmql4j.resultprocessing.model.IResultSet;

public class OntopiaResultSet implements IResultSet {

	private final List<IResult> results;

	public OntopiaResultSet() {
		results = new LinkedList<IResult>();
	}

	public OntopiaResultSet(Collection<IResult> results) {
		this.results = new LinkedList<IResult>();
		this.results.addAll(results);
	}

	public OntopiaResultSet(IResult... results) {
		this.results = Arrays.asList(results);
	}

	public void addResult(IResult result) {
		this.results.add(result);
	}

	public IResult first() {
		return this.results.iterator().next();
	}

	public IResult last() {
		return this.results.get(this.results.size() - 1);
	}

	public Iterator<IResult> iterator() {
		return this.results.iterator();
	}

	public int size() {
		return this.results.size();
	}

	public Class<? extends IResult> getResultClass() {
		return OntopiaResult.class;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{\r\n");
		Iterator<IResult> iterator = iterator();
		while (iterator.hasNext()) {
			builder.append(iterator.next().toString()
					+ (iterator.hasNext() ? "," : "") + "\r\n");
		}
		builder.append("}");
		return builder.toString();
	}

	public OntopiaResult[] getValues() {
		return results.toArray(new OntopiaResult[0]);
	}

	public OntopiaResult[] getValues(Integer... indizes) {
		List<Object> values = new LinkedList<Object>();
		for (Integer index : indizes) {
			if (index < size()) {
				values.add(results.get(index));
			}
		}
		return values.toArray(new OntopiaResult[0]);
	}

	public OntopiaResult getValue(Integer index) {
		if (index < size()) {
			return (OntopiaResult) results.get(index);
		}
		return null;
	}

	public void addResults(Collection<IResult> results) {
		for ( IResult result : results){
			addResult(result);
		}
	}

	public void addResults(IResult... results) {
		for ( IResult result : results){
			addResult(result);
		}
	}

	public boolean canReduceTo2Dimensions() {
		return false;
	}

	public void reduceTo2Dimensions() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

}
