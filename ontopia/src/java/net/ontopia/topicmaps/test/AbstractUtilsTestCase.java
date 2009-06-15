
// $Id: AbstractUtilsTestCase.java,v 1.5 2008/06/13 08:36:28 geir.gronmo Exp $

package net.ontopia.topicmaps.test;

import java.io.*;
import java.net.*;
import java.util.*;

import net.ontopia.topicmaps.test.*;
import net.ontopia.infoset.core.*;
import net.ontopia.infoset.impl.basic.*;
import net.ontopia.topicmaps.xml.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.impl.basic.*;
import net.ontopia.topicmaps.utils.*;


public class AbstractUtilsTestCase extends AbstractTopicMapTestCase
{
    public AbstractUtilsTestCase(String name)
    {
        super(name);
    }

    protected LocatorIF baseAddress;
    protected TopicMapIF tm;
    
    protected void setBase(String uri)
    {
        try
        {
            baseAddress = new URILocator(uri);
        }
        catch (MalformedURLException ex)
        {
            assert(ex.getMessage(), false);
        }
    }

    protected TopicIF getTopic(String fragId)
    {
        LocatorIF l = baseAddress.resolveAbsolute("#" + fragId);
        return (TopicIF)tm.getObjectByItemIdentifier(l);
    }

    protected void readFile(String fileName)
    {
        File tmFile = new File(fileName);
        TopicMapReaderIF reader = new XTMTopicMapReader(tmFile);
        try
        {
            tm = reader.read();
        }
        catch(IOException ex)
        {
            assert("Topic map read failed!\n" + ex.getMessage(), false);
        }
    }



        
}
