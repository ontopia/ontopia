/*
 * Copyright 2008 Lars Heuer (heuer[at]semagia.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ontopia.topicmaps.io;

import java.net.MalformedURLException;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.TypedIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.impl.basic.InMemoryStoreFactory;

import com.semagia.mio.AbstractMapHandlerTest;
import com.semagia.mio.IMapHandler;

/**
 * Tests against the {@link OntopiaMapHandler}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class TestOntopiaMapHandler extends AbstractMapHandlerTest {

    private TopicMapStoreFactoryIF _sysFactory;
    private TopicMapStoreIF _sys;
    private TopicMapIF _tm;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _sysFactory = new InMemoryStoreFactory();
        _sys =  _sysFactory.createStore();
        _tm = _sys.getTopicMap();
    }

    @Override
    protected IMapHandler makeMapHandler() {
        return new OntopiaMapHandler(_tm);
    }

    @Override
    protected Object getType(Object obj) {
        return ((TypedIF) obj).getType();
    }

    @Override
    protected int getAssociationSize() {
        return _tm.getAssociations().size();
    }

    @Override
    protected Object getConstructByItemIdentifier(String iid) {
        return _tm.getObjectByItemIdentifier(createLocator(iid));
    }

    @Override
    protected Object getParent(Object obj) {
        if (obj instanceof TopicIF || obj instanceof AssociationIF) {
            return _tm;
        }
        if (obj instanceof AssociationRoleIF) {
            return ((AssociationRoleIF) obj).getAssociation();
        }
        if (obj instanceof OccurrenceIF) {
            return ((OccurrenceIF) obj).getTopic();
        }
        if (obj instanceof TopicNameIF) {
            return ((TopicNameIF) obj).getTopic();
        }
        if (obj instanceof VariantNameIF) {
            return ((VariantNameIF) obj).getTopicName();
        }
        throw new RuntimeException("Unknown Topic Maps construct: " + obj);
    }

    @Override
    protected String getDatatypeAsString(Object obj) {
        if (obj instanceof OccurrenceIF) {
            return ((OccurrenceIF) obj).getDataType().getAddress();
        }
        else {
            return ((VariantNameIF) obj).getDataType().getAddress();
        }
    }

    @Override
    protected Object getTopicBySubjectIdentifier(String sid) {
        return _tm.getTopicBySubjectIdentifier(createLocator(sid));
    }

    @Override
    protected Object getTopicBySubjectLocator(String slo) {
        return _tm.getTopicBySubjectLocator(createLocator(slo));
    }

    @Override
    protected Object getTopicMapReifier() {
        return _tm.getReifier();
    }

    @Override
    protected String getValue(Object obj) {
        if (obj instanceof OccurrenceIF) {
            return ((OccurrenceIF) obj).getValue();
        }
        if (obj instanceof TopicNameIF) {
            return ((TopicNameIF) obj).getValue();
        }
        if (obj instanceof VariantNameIF) {
            return ((VariantNameIF) obj).getValue();
        }
        throw new RuntimeException("Unknwon object: " + obj);
    }

    @Override
    protected Object getReifier(Object obj) {
        return ((ReifiableIF) obj).getReifier();
    }

    @Override
    protected Object getReified(Object obj) {
        return ((TopicIF) obj).getReified();
    }

    @Override
    protected int getTopicSize() {
        return _tm.getTopics().size();
    }

    protected LocatorIF createLocator(final String reference) {
        try {
            return new URILocator(reference);
        }
        catch (MalformedURLException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }
}
