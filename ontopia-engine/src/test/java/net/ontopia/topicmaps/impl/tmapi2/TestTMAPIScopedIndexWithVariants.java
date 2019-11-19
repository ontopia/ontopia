/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
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

package net.ontopia.topicmaps.impl.tmapi2;

import java.util.Collection;
import java.util.Collections;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import org.junit.Assert;
import org.junit.Test;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;
import org.tmapi.core.Variant;
import org.tmapi.index.ScopedIndex;

/**
 * Test for issue <a href="https://github.com/ontopia/ontopia/issues/214">http://code.google.com/p/ontopia/issues/detail?id=214</a>
 */
public class TestTMAPIScopedIndexWithVariants {

    @Test
    public void testTMAPIVariantsIndex() throws Exception {
        final String themePSI = "http://www.example.org/theme";
        final String value = "the-variant";
        final InMemoryTopicMapStore store = new InMemoryTopicMapStore();
        store.setBaseAddress(new URILocator("http://www.example.org/map"));
        final TopicMapIF tm = store.getTopicMap();
        final TopicMapBuilderIF builder = tm.getBuilder();
        final TopicIF t = builder.makeTopic();
        final TopicIF theme = builder.makeTopic();
        theme.addSubjectIdentifier(new URILocator(themePSI));
        final TopicNameIF name = builder.makeTopicName(t, "name");
        Assert.assertNotNull(name);
        final VariantNameIF var = builder.makeVariantName(name, value, Collections.singleton(theme));
        Assert.assertNotNull(var);
        final TopicMapSystemFactory factory = TopicMapSystemFactory.newInstance();
        final TopicMapSystem sys = factory.newTopicMapSystem();
        final TopicMap topicmap = ((MemoryTopicMapSystemImpl) sys).createTopicMap(tm); 
        final ScopedIndex scopedIdx = topicmap.getIndex(ScopedIndex.class);
        scopedIdx.open();
        if (!scopedIdx.isAutoUpdated()) {
            scopedIdx.reindex();
        }
        final Topic theTheme = topicmap.getTopicBySubjectIdentifier(sys.createLocator(themePSI));
        Assert.assertNotNull(theTheme);
        final Collection<Variant> variants = scopedIdx.getVariants(theTheme);
        Assert.assertEquals(variants.size(), 1);
        // The following line raises the NPE.
        final Variant theVariant = variants.iterator().next();
        Assert.assertNotNull(theVariant);
        Assert.assertEquals(value, theVariant.getValue());
    }
}
