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
import java.util.ArrayList;
import java.util.List;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapReaderIF;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.utils.OntopiaRuntimeException;
import org.restlet.data.CharacterSet;
import org.restlet.engine.converter.ConverterHelper;
import org.restlet.engine.resource.VariantInfo;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Resource;

public abstract class AbstractConverter extends ConverterHelper {
	
	protected List<VariantInfo> variants = new ArrayList<>(1);
	protected List<Class<?>> classes = new ArrayList<>();
	
	// mime to class
	@Override
	public List<Class<?>> getObjectClasses(Variant source) {
		return classes;
	}

	// class to mime
	@Override
	public List<VariantInfo> getVariants(Class<?> source) throws IOException {
		return variants;
	}

	@Override
	public float score(Object source, Variant target, Resource resource) {
		for (VariantInfo v : variants) {
			if (v.isCompatible(target)) {
				if (source != null) {
					return 1F;
				}
			}
		}
        return -1.0F;
	}

	@Override
	public <T> float score(Representation source, Class<T> target, Resource resource) {
		for (Variant v : variants) {
			if (v.getMediaType().isCompatible(source.getMediaType())) {
				return .5F;
			}
		}
		return -1F;
	}

	@Override
	public <T> T toObject(Representation source, Class<T> target, Resource resource) throws IOException {
		LocatorIF base_address = null; // todo from header/params
		
		try {
			TopicMapReaderIF reader = getFragmentReader(source.getStream(), base_address);
			TopicMapIF fragment = reader.read();
			return objectFromFragment(fragment, target, resource);
		} catch (OntopiaRuntimeException ore) {
			throw OntopiaRestErrors.COULD_NOT_READ_FRAGMENT.build(ore);
		}
	}

	@Override
	public Representation toRepresentation(final Object source, final Variant target, Resource resource) throws IOException {
		return new OutputRepresentation(target.getMediaType()) {
			@Override
			public void write(OutputStream outputStream) throws IOException {
				CharacterSet characterSet = target.getCharacterSet();
				if (characterSet == null) {
					characterSet = CharacterSet.UTF_8;
				}
				writeFragment(outputStream, source, characterSet);
			}
		};
	}

	protected abstract TopicMapReaderIF getFragmentReader(InputStream stream, LocatorIF base_address);
	protected abstract void writeFragment(OutputStream outputStream, Object source, CharacterSet characterSet) throws IOException;

	protected <T> T objectFromFragment(TopicMapIF fragment, Class<T> target, Resource resource) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
