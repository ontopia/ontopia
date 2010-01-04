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

public class WrappedOntopiaResultSet implements IResultSet {

	private final List<IResult> results;
	
	public WrappedOntopiaResultSet() {
		results = new LinkedList<IResult>();
	}

	public WrappedOntopiaResultSet(Collection<IResult> results) {
		this.results = new LinkedList<IResult>();
		this.results.addAll(results);
	}

	public WrappedOntopiaResultSet(IResult... results) {
		this.results = new LinkedList<IResult>();
		this.results.addAll(Arrays.asList(results));
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
		return WrappedOntopiaResult.class;
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

	public WrappedOntopiaResult[] getValues() {
		return results.toArray(new WrappedOntopiaResult[0]);
	}

	public WrappedOntopiaResult[] getValues(Integer... indizes) {
		List<Object> values = new LinkedList<Object>();
		for (Integer index : indizes) {
			if (index < size()) {
				values.add(results.get(index));
			}
		}
		return values.toArray(new WrappedOntopiaResult[0]);
	}

	public WrappedOntopiaResult getValue(Integer index) {
		if (index < size() && index != -1 ) {
			return (WrappedOntopiaResult) results.get(index);
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
