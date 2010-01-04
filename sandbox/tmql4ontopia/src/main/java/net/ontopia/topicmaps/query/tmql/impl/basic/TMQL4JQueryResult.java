/**
 * TMQL4J Plugin for Ontopia
 * 
 * Copyright: Copyright 2009 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Author: Sven Krosse
 * 
 */
package net.ontopia.topicmaps.query.tmql.impl.basic;

import java.util.LinkedList;
import java.util.List;

import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.tmql.impl.tmql4jextension.WrappedOntopiaResult;
import net.ontopia.topicmaps.query.tmql.impl.tmql4jextension.WrappedOntopiaResultSet;

public class TMQL4JQueryResult implements QueryResultIF {

	private final WrappedOntopiaResultSet resultSet;
	private final List<String> columnNames;
	private int currentRow;

	public TMQL4JQueryResult(final WrappedOntopiaResultSet resultSet,
			final List<String> columnNames) {
		if (columnNames != null) {
			this.columnNames = columnNames;
		} else {
			this.columnNames = new LinkedList<String>();
		}

		if (resultSet != null) {
			this.resultSet = resultSet;
		} else {
			this.resultSet = new WrappedOntopiaResultSet();
		}

		long size = 0;
		if (this.resultSet.size() > 0) {
			size = ((WrappedOntopiaResult) this.resultSet.first()).size();
		}

		for (int index = this.columnNames.size(); index < size; index++) {
			this.columnNames.add("Column " + index);
		}

		this.currentRow = -1;

		System.out.println(getClass().getSimpleName() + ": headers ["
				+ columnNames.toString() + " ](" + columnNames.size()
				+ ") resultSet: " + this.resultSet.size() + " results: "
				+ this.getWidth());
	}

	public TMQL4JQueryResult(final WrappedOntopiaResultSet resultSet) {
		this.columnNames = new LinkedList<String>();
		if (resultSet != null) {
			this.resultSet = resultSet;
			long size = 0;
			if (this.resultSet.size() > 0) {
				size = ((WrappedOntopiaResult) this.resultSet.first()).size();
			}
			for (int index = 0; index < size; index++) {
				this.columnNames.add("Column " + index);
			}
		} else {
			this.resultSet = new WrappedOntopiaResultSet();
		}

		this.currentRow = -1;
	}

	public void close() {

	}

	public String getColumnName(int index) {
		if (index < columnNames.size()) {
			return columnNames.get(index);
		}
		return null;
	}

	public String[] getColumnNames() {
		return columnNames.toArray(new String[0]);
	}

	public Object getValue(int index) {
		WrappedOntopiaResult result = resultSet.getValue(currentRow);
		if (result != null) {
			return result.getValue(index);
		}
		return null;
	}

	public Object[] getValues() {
		WrappedOntopiaResult result = resultSet.getValue(currentRow);
		if (result != null) {
			return result.getValues();
		}
		return new Object[0];
	}

	public Object[] getValues(Object[] values) {
		Object[] row = getValues();
		System.arraycopy(row, 0, values, 0, row.length);
		return values;
	}

	public int getWidth() {
		if (resultSet.size() > 0) {
			return ((WrappedOntopiaResult) resultSet.iterator().next()).size();
		}
		return 0;
	}

	public boolean next() {
		currentRow++;
		return currentRow < resultSet.size();
	}

	public int getIndex(String columnName) {
		if (columnNames.contains(columnName)) {
			return columnNames.indexOf(columnName);
		}
		return -1;
	}

	public Object getValue(String columnName) {
		int index = getIndex(columnName);
		if (index != -1) {
			return getValue(index);
		}
		return null;
	}

}
