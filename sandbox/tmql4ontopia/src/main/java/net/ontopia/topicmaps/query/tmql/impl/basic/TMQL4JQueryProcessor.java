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

import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.impl.tmapi2.MemoryTopicMapSystemImpl;
import net.ontopia.topicmaps.query.core.DeclarationContextIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.ParsedModificationStatementIF;
import net.ontopia.topicmaps.query.core.ParsedQueryIF;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.tmql.impl.tmql4jextension.WrappedOntopiaResult;
import net.ontopia.topicmaps.query.tmql.impl.tmql4jextension.WrappedOntopiaResultSet;
import net.ontopia.topicmaps.query.tmql.impl.util.ColumnHeaderGenerator;
import net.ontopia.utils.OntopiaException;

import org.tmapi.core.FactoryConfigurationException;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;

import de.topicmapslab.tmql4j.common.core.exception.TMQLRuntimeException;
import de.topicmapslab.tmql4j.common.core.process.TMQLRuntime;
import de.topicmapslab.tmql4j.common.core.query.TMQLQuery;
import de.topicmapslab.tmql4j.common.properties.TMQLRuntimeProperties;

public class TMQL4JQueryProcessor implements QueryProcessorIF {

	private final Collection<TopicMap> topicMaps;
	private TopicMapSystemFactory factory;
	private TopicMapSystem topicMapSystem;
	private final String tmid;
	private final TMQLRuntime runtime;

	public TMQL4JQueryProcessor(TopicMapIF topicMap) throws OntopiaException {
		try {
			factory = TopicMapSystemFactory.newInstance();
			topicMapSystem = factory.newTopicMapSystem();
			TopicMap topicmap = ((MemoryTopicMapSystemImpl) topicMapSystem)
					.createTopicMap(topicMap);
			topicMaps = new HashSet<TopicMap>();
			topicMaps.add(topicmap);
			this.tmid = "";
			this.runtime = new TMQLRuntime(topicMapSystem, topicMaps, true);
		} catch (TMAPIException e) {
			e.printStackTrace();
			throw new OntopiaException("Error", e);
		}

	}

	public TMQL4JQueryProcessor(TopicMapIF topicMap, final String tmid)
			throws OntopiaException {
		try {
			factory = TopicMapSystemFactory.newInstance();
			topicMapSystem = factory.newTopicMapSystem();
			TopicMap topicmap = ((MemoryTopicMapSystemImpl) topicMapSystem)
					.createTopicMap(topicMap);
			topicMaps = new HashSet<TopicMap>();
			topicMaps.add(topicmap);
			this.tmid = tmid;
			this.runtime = new TMQLRuntime(topicMapSystem, topicMaps, true);
		} catch (TMAPIException e) {
			e.printStackTrace();
			throw new OntopiaException("Error", e);
		}

	}

	public TMQL4JQueryProcessor(TopicMap topicMap) {
		this.topicMaps = new HashSet<TopicMap>();
		topicMaps.add(topicMap);
		this.tmid = "";

		try {
			this.factory = TopicMapSystemFactory.newInstance();
		} catch (FactoryConfigurationException e) {
			this.factory = new net.ontopia.topicmaps.impl.tmapi2.TopicMapSystemFactory();
			e.printStackTrace();
		}
		try {
			this.topicMapSystem = factory.newTopicMapSystem();
		} catch (TMAPIException e) {
			e.printStackTrace();
		}
		this.runtime = new TMQLRuntime(topicMapSystem, topicMaps, true);
	}

	public QueryResultIF execute(String query) throws InvalidQueryException {
		List<String> headers = new LinkedList<String>();
		try {
			TMQLRuntimeProperties properties = TMQLRuntimeProperties.instance();
			properties.setProperty(
					TMQLRuntimeProperties.RESULT_SET_IMPLEMENTATION_CLASS,
					WrappedOntopiaResultSet.class.getCanonicalName());
			properties.setProperty(
					TMQLRuntimeProperties.RESULT_TUPLE_IMPLEMENTATION_CLASS,
					WrappedOntopiaResult.class.getCanonicalName());
			properties.setProperty(TMQLRuntimeProperties.ONTOPIA_TOPICMAPID,
					this.tmid);
			runtime.run(new TMQLQuery(query));

			headers = ColumnHeaderGenerator.columnHeaders(runtime);

		} catch (TMQLRuntimeException e) {
			e.printStackTrace();
		}
		return new TMQL4JQueryResult(
				(WrappedOntopiaResultSet) runtime
						.getStoredValue(TMQLRuntime.TMQL_RUNTIME_RESULTPROCESSING_RESULT),
				headers);// ,
		// ColumnHeaderGenerator.columnHeaders(runtime));

	}

	public QueryResultIF execute(String query, DeclarationContextIF context)
			throws InvalidQueryException {
		return execute(query);
	}

	public QueryResultIF execute(String query, Map<String, ?> arg1)
			throws InvalidQueryException {
		return execute(query);
	}

	public QueryResultIF execute(String query, Map<String, ?> arg1,
			DeclarationContextIF arg2) throws InvalidQueryException {
		return execute(query);
	}

	public void load(String arg0) throws InvalidQueryException {
	}

	public void load(Reader arg0) throws InvalidQueryException, IOException {
	}

	public ParsedQueryIF parse(String query) throws InvalidQueryException {
		return new TMQL4JParsedQuery(topicMapSystem, topicMaps, runtime, query,
				tmid);
	}

	public ParsedQueryIF parse(String query, DeclarationContextIF arg1)
			throws InvalidQueryException {
		return new TMQL4JParsedQuery(topicMapSystem, topicMaps, runtime, query,
				tmid);
	}

	public int update(String arg0) throws InvalidQueryException {
		throw new UnsupportedOperationException();
	}

	public ParsedModificationStatementIF parseUpdate(String arg0)
			throws InvalidQueryException {
		throw new UnsupportedOperationException();
	}

	public ParsedModificationStatementIF parseUpdate(String arg0,
			DeclarationContextIF arg1) throws InvalidQueryException {
		throw new UnsupportedOperationException();
	}

	public int update(String arg0, DeclarationContextIF arg1)
			throws InvalidQueryException {
		throw new UnsupportedOperationException();
	}

	public int update(String arg0, Map<String, ?> arg1)
			throws InvalidQueryException {
		throw new UnsupportedOperationException();
	}

	public int update(String arg0, Map<String, ?> arg1,
			DeclarationContextIF arg2) throws InvalidQueryException {
		throw new UnsupportedOperationException();
	}

}
