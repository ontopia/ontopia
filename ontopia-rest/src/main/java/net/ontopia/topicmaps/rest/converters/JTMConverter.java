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
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicMapReaderIF;
import net.ontopia.topicmaps.rest.Constants;
import net.ontopia.topicmaps.utils.jtm.JTMTopicMapReader;
import net.ontopia.topicmaps.utils.jtm.JTMTopicMapWriter;
import org.restlet.data.CharacterSet;
import org.restlet.engine.resource.VariantInfo;

public class JTMConverter extends AbstractConverter {

	public JTMConverter() {
		addVariant(variants, new VariantInfo(Constants.JTM_MEDIATYPE));
		addObjectClass(classes, TMObjectIF.class);
	}

	@Override
	protected TopicMapReaderIF getFragmentReader(InputStream stream, LocatorIF base_address) {
		return new JTMTopicMapReader(stream, base_address);
	}

	@Override
	protected void writeFragment(OutputStream outputStream, Object source, CharacterSet characterSet) throws IOException {
		JTMTopicMapWriter writer = new JTMTopicMapWriter(outputStream, characterSet.getName());
		writer.write((TMObjectIF) source);
	}
}
