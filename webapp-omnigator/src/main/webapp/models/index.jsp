<%@ page language="java" import="net.ontopia.topicmaps.nav2.impl.basic.NavigatorApplication, net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag, net.ontopia.topicmaps.nav2.plugins.PluginIF, java.util.Calendar,java.text.SimpleDateFormat,net.ontopia.Ontopia" %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/logic'     prefix='logic'     %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/output'    prefix='output'    %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/value'     prefix='value'     %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tmvalue'   prefix='tm'        %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/framework' prefix='framework' %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<framework:response/>

<%-- Omnigator: Welcome page which provides an overview of topic maps --%>

<logic:context>
  <logic:include file="/functions/application.jsm"/>

  <template:insert template='/views/template_%view%.jsp'>

    <template:put name='title' body='true'>[Omnigator] Welcome Page</template:put>
    <template:put name='heading' body='true'>
      <h1 class="boxed">Welcome to the Omnigator</h1>
    </template:put>

    <template:put name='plugins' body='true'>
      <framework:pluginList separator=" | " group="welcome"/>
    </template:put>

    <template:put name='navigation' body='true'>

      <% net.ontopia.topicmaps.nav2.core.UserIF user =
         net.ontopia.topicmaps.nav2.utils.FrameworkUtils.getUser(pageContext); %>

      <table class="shboxed" width="100%"><tr><td>
        <h3>Index of Topic Maps<c:out value="${user}"/></h3>
        <ul>
        <c:forEach items="${okscontext.references}" var="reference">
          <c:if test="${!reference.hidden}">
            <li><a href="topicmap_<%= user.getModel() %>.jsp?tm=<c:out value="${reference.id}"/>"><c:out value="${reference.title}"/></a><br>
          </c:if>
        </c:forEach>
        </ul>
      </td></tr></table>

      <p>

      <table class="shboxed" width="100%"><tr><td>
        <h3>Version</h3>

        <table>
        <tr valign="top"><th align=left>Version</th>
            <td><%= Ontopia.getVersion() %>

        <tr valign="top"><th align=left>Build</th>
            <td><%= Ontopia.getBuild() %>
        </table>
      </table>

      <table class="shboxed" width="100%"><tr><td>
      <h3>Further Information</h3>
      <ul>
        <li><b>Documentation</b>
          <ul>
            <li><a href="../docs/navigator/userguide.html" title="Don't miss a single feature of the Omnigator!">Omnigator User Guide</a></li>
            <li><a href="../docs/query/tutorial.html" title="Learn how to write mind-blowing tolog queries...">Query Language Tutorial</a></li>
            <li><a href="../docs/schema/tutorial.html" title="How to ensure consistency in your own topic maps">Schema Language Tutorial</a></li>
          </ul>
        </li>
        <li><b>Articles</b>
          <ul>
            <li><a href="http://www.ontopia.net/topicmaps/materials/tao.html" title="A great introduction to Topic Maps">The TAO of Topic Maps</a></li>
            <li><a href="http://www.ontopia.net/topicmaps/materials/xmlconf.html" title="Packed with tips from a real-world Topic Maps project">The XML Papers</a></li>
            <li><a href="http://www.ontopia.net/topicmaps/materials/tmrdf.html" title="On the relationship between Topic Maps and the Semantic Web">Living with Topic Maps and RDF</a></li>
            <li><a href="http://www.ontopia.net/topicmaps/materials/tm-vs-thesauri.html" title="How Topic Maps relates to traditional knowledge organization">Metadata? Thesauri? Taxonomies? Topic Maps!</a></li>
            <li><a href="http://www.ontopia.net/topicmaps/materials/identitycrisis.html" title="On the problem of identity in the Semantic Web">Curing the Web's Identity Crisis</a></li>
          </ul>
        </li>
        <li><b>Websites</b>
          <ul>
            <li><a href="http://www.ontopia.net" title="A gold mine of information about Topic Maps">Ontopia</a></li>
            <li><a href="http://www.topicmap.com" title="Useful information for the TM user community">topicmap.com</a></li>
          </ul>
        </li>
        <li><b>Specifications</b>
          <ul>
            <li><a href="http://www.isotopicmaps.org/" title="The current edition of the Topic Maps standard">ISO 13250</a></li>
            <li><a href="http://www.ontopia.net/download/ltm.html" title="For people with an aversion to typing angle brackets">LTM Notation Specification</a></li>
            <li><a href="../docs/schema/spec.html" title="All the gen on the Ontopia Schema Language">OSL Reference Guide</a></li>
            <li><a href="http://www.ontopia.net/topicmaps/materials/rdf2tm.html" title="Ontopia's vocabulary for mapping RDF to Topic Maps">RTM: RDF to topic maps mapping</a></li>
          </ul>
        </li>
        <li><b>Examples</b>
            <ul>
              <li><a href="../docs/navigator/ItalianOpera.ltm" title="The world's most famous topic map, in LTM format">Italian Opera (LTM syntax)</a></li>
              <li><a href="../docs/i18n.ltm" title="An LTM topic map about the world's scripts and languages">Scripts and languages (LTM syntax)</a></li>
              <li><a href="../docs/jill.xtm" title="A simple topic map based on the tutorial in the Omnigator User Guide">Jill's First Topic Map (XTM syntax)</a></li>
            </ul>
        </li>
        <li><b>Example applications</b>
          <ul>
            <li><a href="http://www.ontopia.net/operamap/" title="The Italian Opera topic map with its own look-and-feel">OperaMap</a></li>
            <li><a href="http://www.ontopia.net/i18n/" title="The Scripts and languages topic map with its own look-and-feel">Scripts and languages</a></li>
          </ul>
        </li>
      </ul>
      </td></tr></table>

    </template:put>

    <template:put name='content' body='true'>

<table class="shboxed" width="100%"><tr><td>
<h3>Read Me First</h3>

<div style="margin-left: 10px; margin-right: 10px">
<p>The Omnigator is a technology showcase and teaching aid designed to
demonstrate the power of <u><a
href="http://www.ontopia.net/topicmaps/index.html">Topic Maps</a></u>.
It is also used extensively as a topic map debugger and prototyping
tool. Now, with the introduction of RDF support, the Omnigator is
evolving into a multi-purpose Semantic Web Agent.</p>

<p><b>Features</b><br/> The Omnigator is omnivorous: It eats anything,
provided it is a topic map &ndash; or can be viewed as one! Its most
unique feature is that it lets you load and navigate any conforming
topic map, whether its format be XTM 1.0, XTM 2.0, LTM &ndash; or even RDF.
What's more, although it isn't optimized for any particular application,
the Omnigator is able to make reasonable sense out of any reasonably
sensible topic map!</p>

<p>But the Omnigator lets you do <u><a title="If you don't read the User
Guide you'll miss all kinds of goodies!"
href="../docs/navigator/userguide.html">far more</a></u> than simply
browse your topic maps. It supports all aspects of the Topic Maps
standard and has a host of powerful features, such as the ability to

<u><a href="/omnigator/plugins/merge/select.jsp?tm=ItalianOpera.ltm&redirect=%2Fomnigator%2Fmodels%2Ftopicmap_complete.jsp%3Ftm%3DItalianOpera.ltm">merge</a></u> topic maps on the fly;
<u><a href="/omnigator/plugins/query/form.jsp?tm=ItalianOpera.ltm&redirect=%2Fomnigator%2Fmodels%2Ftopicmap_complete.jsp%3Ftm%3DItalianOpera.ltm">search</a></u> in ways that make Google boggle;
<u><a href="/omnigator/plugins/export/config.jsp?tm=ItalianOpera.ltm&redirect=%2Fomnigator%2Fmodels%2Ftopicmap_complete.jsp%3Ftm%3DItalianOpera.ltm">export</a></u> to a range of syntaxes;
<!--generate reports of the topic map's "vital statistics";-->
<u><a href="/omnigator/plugins/customise/user.jsp?tm=ItalianOpera.ltm&redirect=%2Fomnigator%2Fmodels%2Ftopicmap_complete.jsp%3Ftm%3DItalianOpera.ltm">customize</a></u> different views;
produce <u><a href="/omnigator/plugins/filter/userContextFilter.jsp?tm=ItalianOpera.ltm&redirect=%2Fomnigator%2Fmodels%2Ftopicmap_complete.jsp%3Ftm%3DItalianOpera.ltm">filtered subsets</a></u> based on scope;
perform semantic <u><a href="/omnigator/plugins/validator/validate.jsp?tm=ItalianOpera.ltm&redirect=%2Fomnigator%2Fmodels%2Ftopicmap_complete.jsp%3Ftm%3DItalianOpera.ltm"/>validation</a></u>;
and much more besides.</p>

<p align="center"><a
href="/omnigator/plugins/viz/viz.jsp?tm=ItalianOpera.ltm&id=puccini&redirect=%2Fomnigator%2Fmodels%2Ftopic_complete.jsp%3Ftm%3DItalianOpera.ltm%26id%3Dpuccini"><img
border="0" src="../images/tosca-2.png" title="Vizigator screenshot
showing a portion of the Italian Opera Topic Map"/></a></p>

<p>The most eye-catching new Omnigator feature is the Vizigator, which
produces a graphic visualization of your topic map. The Vizigator is
accessed via the <u><a
href="/omnigator/plugins/viz/viz.jsp?tm=ItalianOpera.ltm&id=puccini&redirect=%2Fomnigator%2Fmodels%2Ftopic_complete.jsp%3Ftm%3DItalianOpera.ltm%26id%3Dpuccini">Vizigate</a></u>
plug-in on any Topic Page. In addition, support for RDF has been
improved through the addition of the <u><a href="/omnigator/plugins/rdf2tm/configure.jsp?tm=KanzakisConcerts.rdf">RDF2TM</a></u> plug-in
which allows you to configure your mappings.</p>

<p><b>Documentation</b><br/>

All these features are documented in the <a
href="../docs/navigator/userguide.html">Omnigator User Guide</a>, which also
includes a short practical tutorial on how to create your own topic map
and load it into the Omnigator. The User Guide also describes all the
plug-ins that extend the functionality of the Omnigator in a number of
ways. We encourage you to look through the User Guide in order to avoid
missing out on interesting features that you might not otherwise
discover.</p>

<p><b>User Interface</b><br/>

The Omnigator's interface has not been designed for end users and
Ontopia does not therefore recommend using the Omnigator for end user
applications. End users should not be aware that the application they
are using is driven by a topic map: They should simply experience an
interface that for once makes it possible to find the information they
are looking for, quickly, easily, and intuitively. The reason you will
see technical terms (like "topic type") in the Omnigator is because it
is intended as a teaching aid and debugger for people like yourself who
want to know what is going on under the covers.</p>

<p><b>Technology</b><br/>
The Omnigator is built using the tools in the
<a href="http://www.ontopia.net/">Ontopia</a> Java toolkit for Topic Maps application
development. It is built on top of the <a
href="http://www.ontopia.net/solutions/engine.html">Ontopia Topic Maps
Engine</a> using the <a
href="http://www.ontopia.net/solutions/navigator.html">Ontopia Navigator
Framework</a>, a toolkit for building web delivery applications. We
recommend using the Navigator Framework for end-user applications
designed around a specific ontology; it's like XSLT for Topic Maps. You
can see examples of such custom applications on our <a
href="http://www.ontopia.net/i18n/">Scripts and Languages</a> and
<a href="http://www.ontopia.net/operamap/">OperaMap</a> web sites. For
more information about these products and Ontopia's partners around the
world, contact
<a href="mailto:info@ontopia.net">info@ontopia.net</a> or visit <a
href="http://www.ontopia.net">www.ontopia.net</a>.</p>
</div>
</td></tr></table>

    </template:put>

    <%-- Constants --%>
    <template:put name='application' content='/fragments/application.jsp'/>
    <template:put name='header-tagline' content='/fragments/tagline-header.jsp'/>
    <template:put name='footer-tagline' content='/fragments/tagline-footer.jsp'/>

    <%-- Unused --%>
    <template:put name='intro' body='true'><!-- topic-page: subjectIdentity --></template:put>
    <template:put name='outro' body='true'><!-- outro --></template:put>
    <template:put name='head' body='true'><!-- link to scripts etc. --></template:put>

  </template:insert>

</logic:context>
