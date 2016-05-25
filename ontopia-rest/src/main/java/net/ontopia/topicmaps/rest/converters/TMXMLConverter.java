/*
 * #!
 * Ontopia Rest
 * #-
 * Copyright (C) 2001 - 2016 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.topicmaps.rest.converters;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapReaderIF;
import net.ontopia.topicmaps.rest.Constants;
import net.ontopia.topicmaps.rest.exceptions.OntopiaServerException;
import net.ontopia.topicmaps.xml.TMXMLReader;
import net.ontopia.topicmaps.xml.TMXMLWriter;
import org.restlet.data.CharacterSet;
import org.restlet.engine.resource.VariantInfo;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class TMXMLConverter extends AbstractConverter {

	public TMXMLConverter() {
		addVariant(variants, new VariantInfo(Constants.TMXML_MEDIA_TYPE));
		addObjectClass(classes, TopicMapIF.class);
		addObjectClass(classes, TopicIF.class);
	}
	
	@Override
	protected TopicMapReaderIF getFragmentReader(InputStream stream, LocatorIF base_address) {
		return new TMXMLReader(new InputSource(stream), base_address);
	}

	@Override
	protected void writeFragment(OutputStream outputStream, Object source, CharacterSet characterSet) throws IOException {
		TMXMLWriter writer = new TMXMLWriter(new OutputStreamWriter(outputStream), characterSet.getName());
		if (source instanceof TopicMapIF) {
			writer.write((TopicMapIF) source);
		}
		if (source instanceof TopicIF) {
			TopicIF topic = (TopicIF) source;
			try {
				writer.startTopicMap(topic.getTopicMap());
				writer.writeTopic(topic);
				writer.endTopicMap();
			} catch (SAXException sax) {
				throw new OntopiaServerException(sax);
			}
		}
	}
}
