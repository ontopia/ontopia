/*
 * #!
 * Ontopia Webed
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

package net.ontopia.topicmaps.webed.impl.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import net.ontopia.topicmaps.webed.impl.basic.ActionRegistryIF;
import net.ontopia.topicmaps.webed.impl.basic.ConfigurationObservableIF;
import net.ontopia.topicmaps.webed.impl.basic.ConfigurationObserverIF;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.xml.DefaultXMLReaderFactory;
import net.ontopia.xml.Slf4jSaxErrorHandler;
import net.ontopia.xml.ValidatingContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * INTERNAL: Provide easy access for reading in an action
 * configuration file and generating an action registry object from
 * it.  Automatic observation of file changes are performed
 */
public class ActionConfigurator implements ConfigurationObservableIF {

  // initialization of log facility
  private static Logger log = LoggerFactory
    .getLogger(ActionConfigurator.class.getName());

  protected String contextPath;
  protected String realPath;
  protected String fileName;
  protected long delay;
  protected ActionRegistryIF registry;

  protected Collection observers;

  protected boolean logErrors = true;

  /**
   * Constructor which allows to specify the path of the servlet
   * context and the configuration file name. No further file change
   * observations are executed.
   */
  public ActionConfigurator(String contextPath, String realPath, String fileName) {
    this(contextPath, realPath, fileName, -1);
  }
  
  /**
   * See {@link #ActionConfigurator(String, String, String)} with automatic
   * observation for changes of the configuration file.
   *
   * @param delay - The delay in milliseconds between file change
   *        observations.
   */
  public ActionConfigurator(String contextPath, String realPath, String fileName,
                            long delay) {
    log.debug("ActionConfigurator initialised for '{}' delay: '{}' ms.",
              fileName, delay);
    this.contextPath = contextPath;
    this.realPath = realPath;
    this.fileName = fileName;
    this.delay = delay;
    this.observers = new ArrayList();
    this.registry = null;
  }

  public void logErrors(boolean logErrors) {
    this.logErrors = logErrors;
  }

  public void readAndWatchRegistry() {
    ActionConfigWatchdog cfgdog = new ActionConfigWatchdog(this);
    cfgdog.setDelay(delay);
    cfgdog.start();
  }

  public String getFileName() {
    if (realPath == null)
      return fileName;
    else
      return realPath + File.separator + fileName;
  }
  
  public ActionRegistryIF getRegistry() {
    return registry;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(89);
    sb.append("contextPath: ").append(contextPath)
      .append(", realPath: ").append(realPath)
      .append(", fileName: ").append(fileName)
      .append(", delay: ").append(delay).append(" ms.");
    return sb.toString();
  }
  
  // -----------------------------------------------------------------
  // implementation of ConfigurationObservableIF interface
  // -----------------------------------------------------------------

  @Override
  public void addObserver(ConfigurationObserverIF o) {
    observers.add(o);
  }
  
  @Override
  public void removeObserver(ConfigurationObserverIF o) {
    observers.remove(o);
  }
  
  // -----------------------------------------------------------------
  // internal methods
  // ----------------------------------------------------------------

  private InputStream getInputStream(String realpath, String name) throws IOException {
    //System.out.println("realpath: " + realpath + ", name: " + name);
    // adapted from StreamUtils.getInputStream(String)
    InputStream istream;
    if (name.startsWith("classpath:")) {
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      String resourceName = name.substring("classpath:".length());
      istream = cl.getResourceAsStream(resourceName);
      if (istream == null)
        throw new IOException("Resource '" + resourceName + "' not found through class loader.");
      log.debug("File loaded through class loader: {}", name);
    } else if (name.startsWith("file:")) {
      File f =  new File(name.substring("file:".length()));
      if (f.exists()) {
        log.debug("File loaded from file system: {}", name);
        istream = new FileInputStream(f);
      } else
        throw new IOException("File '" + f + "' not found.");
    } else {
      File f = (realPath == null ? null : new File(realPath, name));
      if (f != null && f.exists()) {
        log.debug("File loaded from file system: {}", f);
        //System.out.println("file system: " + f);
        istream = new FileInputStream(f);
      } else {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        istream = cl.getResourceAsStream(name);
        //System.out.println("class loader: " + f);
        if (istream != null)
          log.debug("File loaded through class loader: {}", name);
      }
    }
    //System.out.println("istream: " + istream);
    return istream;    
  }

  /**
   * Reads in configuration file and try to generate a action registry
   * object.
   */
  public void readRegistryConfiguration() {
    ActionRegistryIF freshRegistry = null;
    try {
      XMLReader parser = DefaultXMLReaderFactory.createXMLReader();
      ActionConfigContentHandler handler = new ActionConfigContentHandler(contextPath);
      InputSource src = new InputSource(new StringReader(schema));
      src.setSystemId("http://www.ontopia.net/xtm-1.0/");
      ValidatingContentHandler ch = new ValidatingContentHandler(handler, src,
                                                                 false);
      parser.setContentHandler(ch);
      parser.setErrorHandler(new Slf4jSaxErrorHandler(log));
      // parse the XML instance, now.
      InputStream istream = getInputStream(realPath, fileName);
      parser.parse(new InputSource(istream));
      if (log.isDebugEnabled()) {
        log.debug("Read in action configuration from {}", fileName);
        log.debug("{}", handler.getRegistry());
      }
      freshRegistry = handler.getRegistry();
    }
    catch (SAXParseException e) {
      if (logErrors)
        log.error("Error in actions config file: " + e.toString() + " at: "+
                  e.getSystemId() + ":" + e.getLineNumber() + ":" +
                  e.getColumnNumber());
      throw new OntopiaRuntimeException(e);
    }
    catch (SAXException se) {
      throw new OntopiaRuntimeException(se);
    }
    catch (IOException ioe) {
      throw new OntopiaRuntimeException(ioe);
    }
    
    if (freshRegistry != registry) {
      this.registry = freshRegistry;
      this.notifyObserversConfigurationChanged();
    } else {
      log.info("Action registry did not change.");
    }
  }

  /**
   * Loops through and notifies each observer if a new item was
   * detected.
   */
  protected void notifyObserversConfigurationChanged() {
    Iterator it = observers.iterator();
    while (it.hasNext()) {
      ConfigurationObserverIF o = (ConfigurationObserverIF) it.next();
      o.configurationChanged(registry);
    }
  }

  private static final String schema =
    "<?xml version='1.0' encoding='UTF-8'?><!--  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  This is the DTD defining the Action Configuration  file syntax for Ontopia's Web Editor Framework.  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--><grammar xmlns:a='http://relaxng.org/ns/compatibility/annotations/1.0' xmlns='http://relaxng.org/ns/structure/1.0' datatypeLibrary='http://www.w3.org/2001/XMLSchema-datatypes'>  <define name='actionConfig'>    <element name='actionConfig'>      <ref name='attlist.actionConfig'/>      <ref name='classMap'/>      <optional>        <ref name='buttonMap'/>      </optional>      <optional>        <ref name='fieldMap'/>      </optional>      <optional>        <ref name='globalForwards'/>      </optional>      <optional>        <ref name='globalProperties'/>      </optional>      <oneOrMore>        <ref name='actionGroup'/>      </oneOrMore>    </element>  </define>  <define name='attlist.actionConfig' combine='interleave'>    <empty/>  </define>  <!--    ...................................................................    Global Mapping between class short name and fully    qualified java class path.  -->  <define name='classMap'>    <element name='classMap'>      <ref name='attlist.classMap'/>      <oneOrMore>        <ref name='class'/>      </oneOrMore>    </element>  </define>  <define name='attlist.classMap' combine='interleave'>    <empty/>  </define>  <define name='class'>    <element name='class'>      <ref name='attlist.class'/>      <empty/>    </element>  </define>  <define name='attlist.class' combine='interleave'>    <attribute name='shortcut'>      <data type='ID'/>    </attribute>  </define>  <define name='attlist.class' combine='interleave'>    <attribute name='fullname'/>  </define>  <!--    ...................................................................    Global Mapping between image short name and image properties    like location and dimension used by action buttons.  -->  <define name='buttonMap'>    <element name='buttonMap'>      <ref name='attlist.buttonMap'/>      <oneOrMore>        <ref name='image'/>      </oneOrMore>    </element>  </define>  <define name='attlist.buttonMap' combine='interleave'>    <empty/>  </define>  <define name='image'>    <element name='image'>      <ref name='attlist.image'/>      <empty/>    </element>  </define>  <!-- name: under which the image can be retrieved -->  <define name='attlist.image' combine='interleave'>    <attribute name='name'>      <data type='ID'/>    </attribute>  </define>  <!-- src: Relative URL of the image location -->  <define name='attlist.image' combine='interleave'>    <optional>      <attribute name='src'/>    </optional>  </define>  <define name='attlist.image' combine='interleave'>    <optional>      <attribute name='absolutesrc'/>    </optional>  </define>  <!-- width: image width in pixels -->  <define name='attlist.image' combine='interleave'>    <optional>      <attribute name='width'/>    </optional>  </define>  <!-- height: image height in pixels -->  <define name='attlist.image' combine='interleave'>    <optional>      <attribute name='height'/>    </optional>  </define>  <!-- border: image border in pixels (default: 0) -->  <define name='attlist.image' combine='interleave'>    <optional>      <attribute name='border'/>    </optional>  </define>  <!-- align: image align mode (default: 'middle') -->  <define name='attlist.image' combine='interleave'>    <optional>      <attribute name='align'/>    </optional>  </define>  <!--    ...................................................................    Global Mapping between field short name and field properties    like type and dimension used by action fields.  -->  <define name='fieldMap'>    <element name='fieldMap'>      <ref name='attlist.fieldMap'/>      <oneOrMore>        <ref name='field'/>      </oneOrMore>    </element>  </define>  <define name='attlist.fieldMap' combine='interleave'>    <empty/>  </define>  <define name='field'>    <element name='field'>      <ref name='attlist.field'/>      <empty/>    </element>  </define>  <!-- name: under which the field can be retrieved -->  <define name='attlist.field' combine='interleave'>    <attribute name='name'>      <data type='ID'/>    </attribute>  </define>  <!-- type: the type of this input field (text|textarea) -->  <define name='attlist.field' combine='interleave'>    <attribute name='type'/>  </define>  <!-- maxlength: maxium length of this field (only if rows==1) -->  <define name='attlist.field' combine='interleave'>    <optional>      <attribute name='maxlength'/>    </optional>  </define>  <!-- columns: number of field columns -->  <define name='attlist.field' combine='interleave'>    <optional>      <attribute name='columns'/>    </optional>  </define>  <!-- rows: number of field rows (default: 1) -->  <define name='attlist.field' combine='interleave'>    <optional>      <attribute name='rows'/>    </optional>  </define>  <!--    ...................................................................    Global Forward Definitions    used for an action if not explicitly defined forward exists.  -->  <define name='globalForwards'>    <element name='globalForwards'>      <ref name='attlist.globalForwards'/>      <oneOrMore>        <ref name='forward'/>      </oneOrMore>    </element>  </define>  <define name='attlist.globalForwards' combine='interleave'>    <empty/>  </define>  <define name='forward'>    <element name='forward'>      <ref name='attlist.forward'/>      <zeroOrMore>        <ref name='reqParam'/>      </zeroOrMore>    </element>  </define>  <define name='attlist.forward' combine='interleave'>    <attribute name='name'>      <data type='ID'/>    </attribute>  </define>  <!-- path: relative URI to forward to -->  <define name='attlist.forward' combine='interleave'>    <attribute name='path'/>  </define>  <!-- type: Action response type this forward page belongs to -->  <define name='attlist.forward' combine='interleave'>    <optional>      <attribute name='type' a:defaultValue='all'>        <choice>          <value>success</value>          <value>failure</value>          <value>all</value>        </choice>      </attribute>    </optional>  </define>  <!-- frame: the response of the forward should appear -->  <define name='attlist.forward' combine='interleave'>    <optional>      <attribute name='frame'>        <choice>          <value>edit</value>          <value>search</value>        </choice>      </attribute>    </optional>  </define>  <!--    nextAction: must be a valid action 'name' entry    the action name pattern is used as a template which    is processed by the specified parameter rule.  -->  <define name='attlist.forward' combine='interleave'>    <optional>      <attribute name='nextAction'>        <data type='IDREF'/>      </attribute>    </optional>  </define>  <!-- paramRule: Shortcut of parameter rule class (optional). -->  <define name='attlist.forward' combine='interleave'>    <optional>      <attribute name='paramRule'>        <data type='IDREF'/>      </attribute>    </optional>  </define>  <define name='reqParam'>    <element name='reqParam'>      <ref name='attlist.reqParam'/>      <empty/>    </element>  </define>  <define name='attlist.reqParam' combine='interleave'>    <attribute name='name'/>  </define>  <!-- value: if no fix value is given it will be taken from the request -->  <define name='attlist.reqParam' combine='interleave'>    <optional>      <attribute name='value'/>    </optional>  </define>  <!--    ...................................................................    Global Property Definitions for Actions and InputFields    that are method names and the related values.  -->  <define name='globalProperties'>    <element name='globalProperties'>      <ref name='attlist.globalProperties'/>      <oneOrMore>        <ref name='actionType'/>      </oneOrMore>    </element>  </define>  <define name='attlist.globalProperties' combine='interleave'>    <empty/>  </define>  <define name='actionType'>    <element name='actionType'>      <ref name='attlist.actionType'/>      <oneOrMore>        <ref name='actionProp'/>      </oneOrMore>    </element>  </define>  <!-- class: must be a valid  class 'shortcut' entry -->  <define name='attlist.actionType' combine='interleave'>    <attribute name='class'>      <data type='IDREF'/>    </attribute>  </define>  <define name='actionProp'>    <element name='actionProp'>      <ref name='attlist.actionProp'/>      <empty/>    </element>  </define>  <!--    name: correlates to java bean method-name in the specified    class/interface  -->  <define name='attlist.actionProp' combine='interleave'>    <attribute name='name'/>  </define>  <!-- value: the value with which the method will be invoked -->  <define name='attlist.actionProp' combine='interleave'>    <attribute name='value'/>  </define>  <!--    ...................................................................    Action Group for covering the modification actions on topic map    objects (like for example changing the topic type or removing    a subject indicator from a topic object).        Note that actions/inputFields will be executed in the order they    are given in the file.  -->  <define name='actionGroup'>    <element name='actionGroup'>      <ref name='attlist.actionGroup'/>      <zeroOrMore>        <choice>          <ref name='inputField'/>          <ref name='action'/>        </choice>      </zeroOrMore>      <zeroOrMore>        <ref name='forward'/>      </zeroOrMore>      <ref name='forwardRules'/>    </element>  </define>  <define name='attlist.actionGroup' combine='interleave'>    <attribute name='name'>      <data type='ID'/>    </attribute>  </define>  <define name='inputField'>    <element name='inputField'>      <ref name='attlist.inputField'/>      <empty/>    </element>  </define>  <!--    name: under which this input element can be used by the tags,    must be unique inside the same action group  -->  <define name='attlist.inputField' combine='interleave'>    <attribute name='name'/>  </define>  <!-- class: must be a valid class 'shortcut' entry -->  <define name='attlist.inputField' combine='interleave'>    <attribute name='class'>      <data type='IDREF'/>    </attribute>  </define>  <define name='action'>    <element name='action'>      <ref name='attlist.action'/>      <empty/>    </element>  </define>  <!--    name: under which this action can be triggered by forms,    must be unique inside the same action group  -->  <define name='attlist.action' combine='interleave'>    <attribute name='name'>      <data type='NMTOKEN'/>    </attribute>  </define>  <!-- class: must be a valid class 'shortcut' entry -->  <define name='attlist.action' combine='interleave'>    <attribute name='class'>      <data type='IDREF'/>    </attribute>  </define>  <!-- exclusive: whether the action is exclusive or not -->  <define name='attlist.action' combine='interleave'>    <optional>      <attribute name='exclusive'>        <choice>          <value>true</value>          <value>false</value>        </choice>      </attribute>    </optional>  </define>  <define name='forwardRules'>    <element name='forwardRules'>      <ref name='attlist.forwardRules'/>      <ref name='forwardDefault'/>      <optional>        <ref name='forwardLocked'/>      </optional>      <zeroOrMore>        <ref name='forwardRule'/>      </zeroOrMore>    </element>  </define>  <define name='attlist.forwardRules' combine='interleave'>    <empty/>  </define>  <define name='forwardDefault'>    <element name='forwardDefault'>      <ref name='attlist.forwardDefault'/>      <zeroOrMore>        <ref name='reqParam'/>      </zeroOrMore>    </element>  </define>  <!--    There are two ways of specifying the default forward    either by referencing to an existing forward definition    or directly by giving the path by an URI  -->  <!-- forward: must be a valid  forward 'name' entry -->  <define name='attlist.forwardDefault' combine='interleave'>    <optional>      <attribute name='forward'>        <data type='IDREF'/>      </attribute>    </optional>  </define>  <!--    path: relative URI to forward to, because this is a convenience-    shortcut, so no differentiation between success and failure page    can be made  -->  <define name='attlist.forwardDefault' combine='interleave'>    <optional>      <attribute name='path'/>    </optional>  </define>  <define name='forwardLocked'>    <element name='forwardLocked'>      <ref name='attlist.forwardLocked'/>      <empty/>    </element>  </define>  <!-- path: relative URI to forward to in case a lock is encountered -->  <define name='attlist.forwardLocked' combine='interleave'>    <attribute name='path'/>  </define>  <!-- frame: the response of the forward should appear -->  <define name='attlist.forwardLocked' combine='interleave'>    <optional>      <attribute name='frame'>        <choice>          <value>edit</value>          <value>search</value>        </choice>      </attribute>    </optional>  </define>  <define name='forwardRule'>    <element name='forwardRule'>      <ref name='attlist.forwardRule'/>      <empty/>    </element>  </define>  <!-- action: must be a valid action 'name' entry -->  <define name='attlist.forwardRule' combine='interleave'>    <attribute name='action'>      <data type='NMTOKEN'/>    </attribute>  </define>  <!-- forward: must be a valid forward 'name' entry -->  <define name='attlist.forwardRule' combine='interleave'>    <attribute name='forward'>      <data type='IDREF'/>    </attribute>  </define>  <start>    <choice>      <ref name='actionConfig'/>    </choice>  </start></grammar>";
}
