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
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapReaderIF;
import net.ontopia.topicmaps.rest.Constants;
import net.ontopia.topicmaps.xml.XTMTopicMapFragmentWriter;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;
import net.ontopia.topicmaps.xml.XTMTopicMapWriter;
import net.ontopia.topicmaps.xml.XTMVersion;
import org.restlet.data.CharacterSet;
import org.restlet.engine.resource.VariantInfo;

public class XTMConverter extends AbstractConverter {

	public XTMConverter() {
		addVariant(variants, new VariantInfo(Constants.XTM_MEDIA_TYPE));
		addObjectClass(classes, TopicIF.class);
		addObjectClass(classes, TopicMapIF.class);
	}

	@Override
	protected TopicMapReaderIF getFragmentReader(InputStream stream, LocatorIF base_address) {
		return new XTMTopicMapReader(stream, base_address);
	}

	@Override
	protected void writeFragment(OutputStream outputStream, Object source, CharacterSet characterSet)  throws IOException {
		if (source instanceof TopicMapIF) {
			XTMTopicMapWriter writer = new XTMTopicMapWriter(outputStream, characterSet.getName()); 
			writer.setVersion(XTMVersion.XTM_2_1); // todo: config
			writer.write((TopicMapIF) source);
		} 
		if (source instanceof TopicIF) {
			XTMTopicMapFragmentWriter writer = new XTMTopicMapFragmentWriter(outputStream, characterSet.getName());
			writer.startTopicMap();
			writer.exportTopic((TopicIF) source);
			writer.endTopicMap();
		}
		
		// todo: can source be any other class here?
	}
}
