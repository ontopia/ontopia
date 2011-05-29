
package net.ontopia.xml;

import java.io.*;
import java.util.*;
import java.net.MalformedURLException;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.impl.basic.*;
import net.ontopia.topicmaps.utils.*;
import net.ontopia.infoset.core.*;
import net.ontopia.infoset.impl.basic.URILocator;

import org.xml.sax.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: A common base class for Reader implementations that can
 * read XML-based syntaxes.
 * @since 1.2
 */
public abstract class AbstractXMLFormatReader {
  protected InputSource source;
  protected LocatorIF base_address;
  protected XMLReaderFactoryIF xrfactory;

  /**
   * INTERNAL: Gets the SAX input source used by the reader.
   */
  public InputSource getInputSource() {
    return source;
  }

  /**
   * INTERNAL: Sets the SAX input source used by the reader.
   */
  public void setInputSource(InputSource source) {
    this.source = source;
  }
  
  /**
   * INTERNAL: Gets the top level base address of the input source.
   */
  public LocatorIF getBaseAddress() {
    return base_address;
  }

  /**
   * INTERNAL: Sets the top level base address of the input source.</p>
   *
   * The top level base address is used to resolve relative addresses
   * during input source processing. The top level base address can be
   * overriden by xml:base constructs in the input source. This
   * property need not be set if the input source specifies the base
   * address.</p>
   */
  public void setBaseAddress(LocatorIF base_address) {
    this.base_address = base_address;
  }

  /**
   * INTERNAL: Gets the XMLReaderFactoryIF that will be used to create
   * XML parser objects inside the reader.
   */
  public XMLReaderFactoryIF getXMLReaderFactory() {
    // Initialize default factory
    if (xrfactory == null) {
      ConfiguredXMLReaderFactory cxrfactory = new ConfiguredXMLReaderFactory();
      configureXMLReaderFactory(cxrfactory);
      xrfactory = cxrfactory;
    }
    return xrfactory;
  }
  
  /**
   * INTERNAL: Sets the XMLReaderFactoryIF that will be used to create
   * XML parser objects inside the reader.</p>
   *
   * <p>Default: {@link net.ontopia.xml.ConfiguredXMLReaderFactory} using an
   * {@link net.ontopia.topicmaps.xml.IgnoreTopicMapDTDEntityResolver}
   * entity resolver.</p>
   *
   * <p>The factory is free to configure the XML reader object as it
   * sees fit, but the reader will use its own internal content
   * handler.
   */
  public void setXMLReaderFactory(XMLReaderFactoryIF xrfactory) {
    this.xrfactory = xrfactory;
  }

  // --- Internal methods

  protected abstract void configureXMLReaderFactory(ConfiguredXMLReaderFactory cxrfactory);
  
}
