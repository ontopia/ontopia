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

package net.ontopia.topicmaps.rest.v1;

import net.ontopia.topicmaps.rest.AbstractResourceTest;

public abstract class AbstractV1ResourceTest extends AbstractResourceTest {
	
	public static final String NAMES_LTM = "names.ltm";
	public static final String OCCURRENCES_LTM = "occurrences.ltm";
	public static final String VARIANTS_LTM = "variants.ltm";
	public static final String TOPICS_LTM = "topics.ltm";
	public static final String OPERA_TM = "ItalianOpera.ltm";

	public AbstractV1ResourceTest() {
	}

	public AbstractV1ResourceTest(String tmid, String path) {
		super(tmid, path);
	}

	@Override
	protected String getAPIRoot() {
		return super.getAPIRoot() + "v1/";
	}
}
