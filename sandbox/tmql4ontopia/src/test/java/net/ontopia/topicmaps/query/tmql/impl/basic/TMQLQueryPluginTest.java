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

import java.io.File;

import junit.framework.TestCase;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import net.ontopia.topicmaps.utils.ltm.LTMTopicMapReader;

import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;

public class TMQLQueryPluginTest extends TestCase {

	public void testStringTreeFunction() throws Exception {

		System.out.println(QueryUtils.getAvailableQueryLanguages());

		LTMTopicMapReader reader = new LTMTopicMapReader(new File(
				"src/test/resources/ItalianOpera.ltm"));
		TopicMapIF tm = reader.read();

		TMQL4JQueryProcessor processor = new TMQL4JQueryProcessor(tm);

		QueryResultIF result = processor
				.execute("FOR $variable IN // http://psi.ontopedia.net/Composer RETURN $variable ( . / tm:name , . / tm:occurrence )");

		System.out.println(result.getValues());

	}

}
