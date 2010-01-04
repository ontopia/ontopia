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

import java.util.Map;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.QueryProcessorFactoryIF;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.utils.OntopiaException;

/**
 * PUBLIC: QueryProcessorFactory implementation for the TMQL query language.
 */
public class TMQLQueryProcessorFactory implements QueryProcessorFactoryIF {

	private final static String NAME = "TMQL"; 
	
	public QueryProcessorIF createQueryProcessor(TopicMapIF arg0,
			LocatorIF arg1, Map<String, String> arg2) {
		try {
			return new TMQL4JQueryProcessor(arg0);
		} catch (OntopiaException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getQueryLanguage() {
		return NAME;
	}

}
