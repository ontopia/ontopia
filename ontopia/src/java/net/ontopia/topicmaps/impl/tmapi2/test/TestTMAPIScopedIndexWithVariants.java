/*
 * 
 *
 * $Id:$
 */
package net.ontopia.topicmaps.impl.tmapi2.test;

import java.util.Collection;
import java.util.Collections;

import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;
import org.tmapi.core.Variant;
import org.tmapi.index.ScopedIndex;

import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.impl.tmapi2.MemoryTopicMapSystemImpl;
import junit.framework.TestCase;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public class TestTMAPIScopedIndexWithVariants extends TestCase {

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
        assertNotNull(name);
        final VariantNameIF var = builder.makeVariantName(name, value, Collections.singleton(theme));
        assertNotNull(var);
        final TopicMapSystemFactory factory = TopicMapSystemFactory.newInstance();
        final TopicMapSystem sys = factory.newTopicMapSystem();
        final TopicMap topicmap = ((MemoryTopicMapSystemImpl) sys).createTopicMap(tm); 
        final ScopedIndex scopedIdx = topicmap.getIndex(ScopedIndex.class);
        scopedIdx.open();
        if (!scopedIdx.isAutoUpdated()) {
            scopedIdx.reindex();
        }
        final Topic theTheme = topicmap.getTopicBySubjectIdentifier(sys.createLocator(themePSI));
        assertNotNull(theTheme);
        final Collection<Variant> variants = scopedIdx.getVariants(theTheme);
        assertEquals(variants.size(), 1);
        final Variant theVariant = variants.iterator().next();  // Boom!
        assertNotNull(theVariant);
        assertEquals(value, theVariant.getValue());
    }
}
