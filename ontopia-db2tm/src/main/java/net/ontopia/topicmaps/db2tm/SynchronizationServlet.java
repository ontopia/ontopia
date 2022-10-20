/*
 * #!
 * Ontopia DB2TM
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

package net.ontopia.topicmaps.db2tm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.entry.TopicMapRepositoryIF;
import net.ontopia.topicmaps.nav2.utils.NavigatorUtils;
import net.ontopia.utils.OntopiaRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PUBLIC: Servlet that sets up and schedules DB2TM synchronization
 * at regular intervals. Parameters: start-time, delay, interval, mapping,
 * relations and topicmap.
 *
 * Example web.xml configuation:
 *
 * <pre>
 *   &lt;servlet>
 *   &lt;servlet-name>MySynchronizationServlet&lt;/servlet-name>
 *   &lt;description>
 *     DB2TM synchronization
 *   &lt;/description>
 *   &lt;servlet-class>
 *     net.ontopia.topicmaps.db2tm.SynchronizationServlet
 *   &lt;/servlet-class>
 *   &lt;init-param>
 *     &lt;param-name>start-time&lt;/param-name>
 *     &lt;param-value>09:50&lt;/param-value>
 *   &lt;/init-param>
 *   &lt;init-param>
 *     &lt;param-name>interval&lt;/param-name>
 *     &lt;param-value>86400000&lt;/param-value>
 *   &lt;/init-param>
 *   &lt;init-param>
 *     &lt;param-name>mapping&lt;/param-name>
 *     &lt;param-value>bk.db2tm.xml&lt;/param-value>
 *   &lt;/init-param>
 *   &lt;init-param>
 *     &lt;param-name>topicmap&lt;/param-name>
 *     &lt;param-value>result.xtm&lt;/param-value>
 *   &lt;/init-param>
 *   &lt;load-on-startup>1&lt;/load-on-startup>
 * &lt;/servlet>
 * </pre>
 *
 * This configuration will make the DB2TM synchronization run at every
 * day at 09:50 in the morning. The mapping file must either be
 * adressed directly on the file system or it will be loaded by name
 * from the CLASSPATH.
 *
 * @since 3.3.0
 */
public class SynchronizationServlet extends HttpServlet {

  private static Logger log = LoggerFactory.getLogger(SynchronizationServlet.class);

  private static DateFormat df = new SimpleDateFormat("HH:mm");
  
  protected SynchronizationTask task;
  
  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    log.info("Initializing synchronization servlet.");

    try {
      // no default starttime
      Date time = null;
      long delay = 180*1000;
      String timeval = config.getInitParameter("start-time");
      if (timeval != null) {
        Date d = df.parse(timeval);
        Calendar c0 = Calendar.getInstance();
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 1);
        c.set(Calendar.MINUTE, 0);
        c.add(Calendar.MILLISECOND, (int)d.getTime());
        if (c.before(c0)) {
          c.add(Calendar.HOUR_OF_DAY, 24);
        }
        time = c.getTime();
        log.info("Setting synchronization start time to {} ms.", time);
      } else {
        // default delay is 180 sec.
        delay = getLongProperty(config, "delay", delay);
        log.info("Setting synchronization delay to {} ms.", delay);
      }
      
      // default interval is 24 hours.
      long interval = getLongProperty(config, "interval", 1000*60*60*24);
      log.info("Setting synchronization interval to {} ms.", interval);
      
      // load relation mapping file
      String mapping = config.getInitParameter("mapping");
      if (mapping == null) {
        throw new OntopiaRuntimeException("Servlet init-param 'mapping' must be specified.");
      }
      
      // get relation names (comma separated)
      Collection<String> relnames = null;
      String relations = config.getInitParameter("relations");
      if (relations != null) {
        relnames = Arrays.asList(StringUtils.split(relations, ","));
      }
      
      // get hold of the topic map
      String tmid = config.getInitParameter("topicmap");
      if (tmid == null) {
        throw new OntopiaRuntimeException("Servlet init-param 'topicmap' must be specified.");
      }
      TopicMapRepositoryIF rep = NavigatorUtils.getTopicMapRepository(config.getServletContext());
      TopicMapReferenceIF ref = rep.getReferenceByKey(tmid);
      
      // make sure delay is at least 10 seconds to make sure that it doesn't
      // start too early
      if (time == null) {
        task = new SynchronizationTask(config.getServletName(),
                                       (delay < 10000 ? 10000 : delay),
                                       interval);
      } else {
        task = new SynchronizationTask(config.getServletName(), time, interval);
      }
      
      task.setRelationMappingFile(mapping);
      task.setRelationNames(relnames);
      task.setTopicMapReference(ref);
      task.setBaseLocator(null);
      
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }
  
  private long getLongProperty(ServletConfig config, String propname, long defval) {
    String propval = config.getInitParameter(propname);
    if (propval != null) {
      try {
        return Long.parseLong(propval);
      } catch (NumberFormatException e) {
        log.warn("Invalid long in servlet parameter '{}': {}", propname, propval);
      }
    }
    return defval;
  }
  
  @Override
  public void destroy() {
    log.info("Destructing synchronization servlet.");
    if (task != null) {
      task.stop();
      task = null;
    }
  }
  
}
