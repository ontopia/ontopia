<!--

 This XSLT stylesheet converts DocBook whitepapers to HTML.
 $Id: docbook2html.xslt,v 1.53 2007/11/19 08:50:08 lars.garshol Exp $

-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

  <!-- ===== Parameters ============================================= -->

  <!-- This parameter controls whether a table of contents is generated
       or not. -->
  <xsl:param name = "make-toc">
    <xsl:value-of select="true"/>
  </xsl:param>

  <!-- This parameter controls whether sections are numbered or not. -->
  <xsl:param name = "number-sects">
    <xsl:value-of select="true"/>
  </xsl:param>

  <!-- Used for conditional includes -->
  <xsl:param name = "condition" select = "'UUUUUUUUUUUUUU'" />

  <!-- ===== Main style rule ======================================== -->

  <xsl:template match="/article | /book">
  <html>
  <head>
    <title><xsl:value-of select="title"/></title>
    <link rel="stylesheet" href="whitepapers.css" type="text/css"/>
  </head>

  <body>
  <h1><xsl:value-of select="title"/></h1>
  <xsl:if test="subtitle">
    <h2 class="subtitle"><xsl:value-of select="subtitle"/></h2>
  </xsl:if>

  <table width="100%"><tr><td>
  <xsl:choose>
    <xsl:when test="bookinfo">
      <xsl:call-template name="docinfo">
         <xsl:with-param name="container" select='bookinfo'/>
      </xsl:call-template>
    </xsl:when>
    <xsl:when test="articleinfo">
      <xsl:call-template name="docinfo">
         <xsl:with-param name="container" select='articleinfo'/>
      </xsl:call-template>
    </xsl:when>
  </xsl:choose>


  </td><td><a href="http://www.ontopia.net"><img src="logo.gif" alt="" align="right" border="0"/></a></td></tr></table>

  <xsl:apply-templates select="articleinfo / abstract | bookinfo / abstract"/>

  <xsl:if test="$make-toc and section">
    <h2>Table of contents</h2>
    <ul>
    <xsl:apply-templates select="section" mode="ToC"/>
    </ul>
  </xsl:if>

  <xsl:apply-templates/>

  <xsl:if test="descendant::footnote">
    <h2>Appendix: Footnotes</h2>
    <dl>
      <xsl:apply-templates select="*" mode="footnote"/>
    </dl>
  </xsl:if>

  <xsl:if test="articleinfo/printhistory">
    <h2>Appendix: Print history</h2>
    <xsl:apply-templates select="articleinfo/printhistory/para"/>
  </xsl:if>

  </body>
  </html>
  </xsl:template>

  <xsl:template match="title | subtitle"/>

  <xsl:template match="articleinfo | bookinfo"/>

  <xsl:template match="abstract">
    <h2>Abstract</h2>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="section | chapter">
    <xsl:if test='string(@condition) = "" or contains(@condition, $condition)'>
    <h2><a><xsl:call-template name="sect-id">
           <xsl:with-param name="attname" select='"name"'/>
           </xsl:call-template>
      <xsl:if test="$number-sects">
        <xsl:call-template name="sect-number"/>
      </xsl:if>
      <xsl:value-of select="title"/>
    </a></h2>

    <xsl:choose>
    <xsl:when test="refentry">
      <xsl:apply-templates select="para"/>
      
      <h3>Index</h3>
      <ul>
       <xsl:for-each select="refentry">
            <xsl:sort select="refnamediv / refname"/>
          <li>
            <xsl:choose>
              <xsl:when test="@id"><a href="#{@id}"><xsl:value-of select="refnamediv / refname"/></a></xsl:when>
              <xsl:otherwise><a href="#{generate-id()}"><xsl:value-of select="refnamediv / refname"/></a></xsl:otherwise>
            </xsl:choose>
          </li>
       </xsl:for-each>
      </ul>

      <xsl:apply-templates select="refentry">
        <xsl:sort select="refnamediv / refname"/>
      </xsl:apply-templates>
    </xsl:when>
    <xsl:otherwise>
      <xsl:apply-templates/>
    </xsl:otherwise>
    </xsl:choose>
    </xsl:if>
  </xsl:template>

  <xsl:template match="appendix">
    <xsl:if test='string(@condition) = "" or contains(@condition, $condition)'>
    <h2><a><xsl:call-template name="sect-id">
           <xsl:with-param name="attname" select='"name"'/>
           </xsl:call-template>
      <xsl:if test="$number-sects">
        <xsl:call-template name="app-number"/>
      </xsl:if>
      <xsl:value-of select="title"/>
    </a></h2>
    <xsl:apply-templates/>
    </xsl:if>
  </xsl:template>

  <xsl:template match="section[count(ancestor::section) = 1] | appendix/section">
    <xsl:if test='string(@condition) = "" or contains(@condition, $condition)'>
    <h3><a><xsl:call-template name="sect-id">
           <xsl:with-param name="attname" select='"name"'/>
           </xsl:call-template>
      <xsl:if test="$number-sects">
        <xsl:call-template name="sect-number"/>
      </xsl:if>
      <xsl:value-of select="title"/>
    </a></h3>

    <xsl:choose>
    <xsl:when test="refentry">
      <h3>Index</h3>
      <ul>
       <xsl:for-each select="refentry">
            <xsl:sort select="refnamediv / refname"/>
          <li>
            <xsl:choose>
              <xsl:when test="@id"><a href="#{@id}"><xsl:value-of select="refnamediv / refname"/></a></xsl:when>
              <xsl:otherwise><a href="#{generate-id()}"><xsl:value-of select="refnamediv / refname"/></a></xsl:otherwise>
            </xsl:choose>
          </li>
       </xsl:for-each>
      </ul>

      <xsl:apply-templates select="refentry">
        <xsl:sort select="refnamediv / refname"/>
      </xsl:apply-templates>
    </xsl:when>
    <xsl:otherwise>
      <xsl:apply-templates/>
    </xsl:otherwise>
    </xsl:choose>
    </xsl:if>
  </xsl:template>

  <xsl:template match="section[count(ancestor::section) = 2]">
    <xsl:if test='string(@condition) = "" or contains(@condition, $condition)'>
    <h4><a><xsl:call-template name="sect-id">
           <xsl:with-param name="attname" select='"name"'/>
           </xsl:call-template>
      <xsl:if test="$number-sects">
        <xsl:call-template name="sect-number"/>
      </xsl:if>
      <xsl:value-of select="title"/>
    </a></h4>
    <xsl:apply-templates/>
    </xsl:if>
  </xsl:template>

  <xsl:template match="section[count(ancestor::section) = 3]">
    <xsl:if test='string(@condition) = "" or contains(@condition, $condition)'>
    <h5><a><xsl:call-template name="sect-id">
           <xsl:with-param name="attname" select='"name"'/>
           </xsl:call-template>
      <xsl:if test="$number-sects">
        <xsl:call-template name="sect-number"/>
      </xsl:if>
      <xsl:value-of select="title"/>
    </a></h5>
    <xsl:apply-templates/>
    </xsl:if>
  </xsl:template>

 <!-- Blocks -->

  <xsl:template match="para">
    <xsl:if test='string(@condition) = "" or contains(@condition, $condition)'>
      <p><xsl:apply-templates/></p>
    </xsl:if>
  </xsl:template>

  <xsl:template match="formalpara">
    <xsl:if test='string(@condition) = "" or contains(@condition, $condition)'>
      <p><b><xsl:value-of select="title"/></b> <br />
        <xsl:apply-templates select="para"/></p>
    </xsl:if>
  </xsl:template>

  <xsl:template match="blockquote">
    <blockquote><xsl:apply-templates/></blockquote>
  </xsl:template>

  <xsl:template match="itemizedlist">
    <xsl:if test='string(@condition) = "" or contains(@condition, $condition)'>
    <xsl:choose>
    <xsl:when test="listitem / formalpara">
      <xsl:if test="title">
      <p><b><xsl:apply-templates select="title"/></b></p>
      </xsl:if>

      <dl>
      <xsl:for-each select="listitem">
        <dt><xsl:apply-templates select="formalpara / title / node()"/></dt>
        <dd><xsl:apply-templates select="formalpara / node()"/>
            <xsl:apply-templates select="itemizedlist"/>
        </dd>
      </xsl:for-each>
      </dl>
    </xsl:when>

    <xsl:otherwise>
    <ul>
      <xsl:if test="title">
      <b><xsl:value-of select="title"/></b>
      </xsl:if>

      <xsl:for-each select="listitem">
        <xsl:if test='string(@condition) = "" or contains(@condition, $condition)'>
        <li><xsl:apply-templates/></li>
        </xsl:if>
      </xsl:for-each>
    </ul>
    </xsl:otherwise>
    </xsl:choose>
    </xsl:if>
  </xsl:template>

  <xsl:template match="simplelist">
    <ul>
      <xsl:for-each select="member">
        <li><xsl:apply-templates/></li>
      </xsl:for-each>
    </ul>
  </xsl:template>

  <xsl:template match="orderedlist">
    <ol>
      <xsl:if test="title">
      <b><xsl:value-of select="title"/></b>
      </xsl:if>

      <xsl:for-each select="listitem">
        <li><xsl:apply-templates/></li>
      </xsl:for-each>
    </ol>
  </xsl:template>

  <xsl:template match="variablelist">
    <xsl:if test='string(@condition) = "" or contains(@condition, $condition)'>
    <dl>
    <xsl:for-each select = "varlistentry">
      <xsl:if test='string(@condition) = "" or contains(@condition, $condition)'>
      <dt><xsl:value-of select = "term"/></dt>
      <dd>
      <xsl:if test="count(listitem / *) = 1">
        <xsl:apply-templates select = "listitem / para / node()"/>
      </xsl:if>
      <xsl:if test="count(listitem / *) > 1">
        <xsl:apply-templates select="listitem / *"/>
      </xsl:if>
      </dd>
      </xsl:if>
    </xsl:for-each>
    </dl>
    </xsl:if>
  </xsl:template>

  <xsl:template match="synopsis | screen">
    <pre>
    <xsl:apply-templates/>
    </pre>
  </xsl:template>

  <xsl:template match="programlisting">
    <code><pre>
    <xsl:apply-templates/>
    </pre></code>
  </xsl:template>

  <xsl:template match="example">
    <xsl:if test="title">
      <p align="center"><b><xsl:value-of select="title"/></b></p>
    </xsl:if>

    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="informalexample">
    <xsl:apply-templates/> <!-- we do nothing special to render this -->
  </xsl:template>

  <xsl:template match="literallayout">
    <pre>
    <xsl:apply-templates/>
    </pre>
  </xsl:template>


  <!-- Inline -->

  <xsl:template match="ulink">
    <a href="{@url}"><xsl:apply-templates/></a>
  </xsl:template>

  <xsl:template match="link">
    <a href="#{@linkend}"><xsl:apply-templates/></a>
  </xsl:template>

  <xsl:template match="citetitle">
    <cite><xsl:apply-templates/></cite>
  </xsl:template>

  <xsl:template match="figure">
    <!--
      WARNING! Images disappear completely in MSIE if the width
      attribute is empty!
    -->
    <p align="center">
    <xsl:choose>
      <xsl:when test="@id">
        <a name="{@id}">
          <xsl:apply-templates/>
        </a>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates/>
      </xsl:otherwise>
    </xsl:choose>
    </p>
    <p align="center">
      <b>
        <xsl:if test="$number-sects">
          <xsl:value-of select="'Figure '"/>
          <xsl:call-template name="fig-number"/>
        </xsl:if>
        <xsl:value-of select = "title" />
      </b>
    </p>
  </xsl:template>

  <xsl:template match="graphic">
    <!--
      WARNING! Images disappear completely in MSIE if the width
      attribute is empty!
    -->
    <img src="{@fileref}" align="center">
      <xsl:if test="@width">
        <xsl:attribute name="width"><xsl:value-of select="@width"
           /></xsl:attribute>
      </xsl:if>
      <xsl:if test="@role">
        <xsl:attribute name="title"><xsl:value-of select="@role"
           /></xsl:attribute>
      </xsl:if>
    </img>
  </xsl:template>

  <xsl:template match="footnote">
    <sup><a href="#footnote{count(preceding::footnote) + 1}">[<xsl:value-of select="count(preceding::footnote) + 1"/>]</a></sup>
  </xsl:template>

  <xsl:template match="footnoteref">
    <xsl:variable name="fnid" select="@linkend"/>
    <xsl:variable name="fnno" select="count(//footnote[@id = $fnid] / preceding::footnote) + 1"/>
    <sup><a href="#footnote{$fnno}">[<xsl:value-of select="$fnno"/>]</a></sup>
  </xsl:template>

  <xsl:template match="remark">
    <span class="remark">[<b>Remark: </b> <xsl:apply-templates/>]</span>
  </xsl:template>

  <xsl:template match="emphasis">
    <xsl:choose>
      <xsl:when test="@role='bold'">
        <strong><xsl:apply-templates/></strong>
      </xsl:when>
      <xsl:otherwise>
        <em><xsl:apply-templates/></em>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="sgmltag">
    <xsl:choose>
      <xsl:when test="@class='att'">
        <tt><xsl:apply-templates/></tt>
      </xsl:when>
      <xsl:when test="@class='attribute'">
        <tt><xsl:apply-templates/></tt>
      </xsl:when>
      <xsl:when test="@class='attvalue'">
        <tt>'<xsl:apply-templates/>'</tt>
      </xsl:when>
      <xsl:when test="@class='element'">
        <tt>&lt;<xsl:apply-templates/>&gt;</tt>
      </xsl:when>
      <xsl:when test="@class='emptytag'">
        <tt>&lt;<xsl:apply-templates/>/&gt;</tt>
      </xsl:when>
      <xsl:when test="@class='endtag'">
        <tt>&lt;/<xsl:apply-templates/>&gt;</tt>
      </xsl:when>
      <xsl:when test="@class='genentity'">
        <tt>&amp;<xsl:apply-templates/>;</tt>
      </xsl:when>
      <xsl:when test="@class='numcharref'">
        <tt>&amp;#<xsl:apply-templates/>;</tt>
      </xsl:when>
      <xsl:when test="@class='paramentity'">
        <tt>%<xsl:apply-templates/>;</tt>
      </xsl:when>
      <xsl:when test="@class='pi'">
        <tt>&lt;?<xsl:apply-templates/>&gt;</tt>
      </xsl:when>
      <xsl:when test="@class='property'">
        <tt>[<xsl:apply-templates/>]</tt>
      </xsl:when>
      <xsl:when test="@class='sgmlcomment'">
        <tt>&lt;!--<xsl:apply-templates/>--&gt;</tt>
      </xsl:when>
      <xsl:when test="@class='starttag'">
        <tt>&lt;<xsl:apply-templates/>&gt;</tt>
      </xsl:when>
      <xsl:when test="@class='xmlpi'">
        <tt>&lt;?<xsl:apply-templates/>?&gt;</tt>
      </xsl:when>
      <xsl:otherwise>
        <tt>&lt;<xsl:apply-templates/>&gt;</tt>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="phrase">
    <xsl:if test='string(@condition) = "" or contains(@condition, $condition)'>
      <xsl:apply-templates/>
    </xsl:if>
  </xsl:template>

  <xsl:template match="quote">"<xsl:apply-templates/>"</xsl:template>

  <xsl:template match="italic">
    <!-- italic is *not* a DocBook element type -->
    <b>&lt;italic></b><xsl:apply-templates/><b>&lt;/italic></b>
  </xsl:template>

  <xsl:template match="firstterm">
    <dfn><xsl:apply-templates/></dfn>
  </xsl:template>

  <xsl:template match="guibutton">
    <strong><xsl:apply-templates/></strong>
  </xsl:template>

  <xsl:template match="guilabel">
    <strong><xsl:apply-templates/></strong>
  </xsl:template>

  <xsl:template match="symbol">
    <xsl:if test='string(@condition) = "" or contains(@condition, $condition)'>
    <tt class="symbol"><xsl:apply-templates/></tt>
    </xsl:if>
  </xsl:template>

  <xsl:template match="classname">
    <tt class="classname"><xsl:apply-templates/></tt>
  </xsl:template>

  <xsl:template match="property">
    <tt class="property"><xsl:apply-templates/></tt>
  </xsl:template>

  <xsl:template match="literal">
    <tt class="literal"><xsl:apply-templates/></tt>
  </xsl:template>

  <xsl:template match="parameter">
    <tt class="parameter"><xsl:apply-templates/></tt>
  </xsl:template>

  <xsl:template match="methodname">
    <tt class="methodname"><xsl:apply-templates/></tt>
  </xsl:template>

  <xsl:template match="interfacename">
    <tt class="interfacename"><xsl:apply-templates/></tt>
  </xsl:template>

  <xsl:template match="acronym"><xsl:apply-templates/></xsl:template>

  <xsl:template match="xref">
    <xsl:variable name = "linkend" select = "@linkend"/>
    <xsl:variable name = "refnode" select = '//* [@id = $linkend]'/>
    
    <xsl:if test="not($refnode)">
      <xsl:message terminate="no"
        >ERROR: Bad xref reference <xsl:value-of select="$linkend"/></xsl:message>
    </xsl:if>

    <a href="#{@linkend}"
      ><xsl:call-template name="ref-number">
        <xsl:with-param name="refnode" select="$refnode"/>
      </xsl:call-template></a>
  </xsl:template>

  <xsl:template match="citation">
    [<xsl:apply-templates/>]
  </xsl:template>

  <xsl:template match="exceptionname | function | para/screen | command | envar | filename | varname">
    <xsl:if test='string(@condition) = "" or contains(@condition, $condition)'>
    <tt><xsl:apply-templates/></tt>
    </xsl:if>
  </xsl:template>

  <xsl:template match="productname">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="email">
    <a href="mailto:{.}"><xsl:apply-templates/></a>
  </xsl:template>

  <xsl:template match="warning">
    <dl><dt class="warning">Warning</dt>
    <dd><xsl:apply-templates/></dd>
    </dl>
  </xsl:template>

  <xsl:template match="important">
    <dl><dt class="warning">Important</dt>
    <dd><xsl:apply-templates/></dd>
    </dl>
  </xsl:template>

  <xsl:template match="note">
    <dl><dt class="note">Note</dt>
    <dd><xsl:apply-templates/></dd>
    </dl>
  </xsl:template>

  <xsl:template match="para // programlisting | para // informalexample">
    <code><xsl:apply-templates/></code>
  </xsl:template>

  <!-- Table support -->

  <xsl:template match="table | informaltable">
    <xsl:if test="title">
      <p align="center">
      <b>
        <xsl:if test="$number-sects">
          <xsl:value-of select="'Table '"/>
          <xsl:call-template name="tabl-number"/>
        </xsl:if>
        <xsl:value-of select="title"/>
      </b>
      </p>
    </xsl:if>

    <table class="dbTable" border="1" cellspacing="0" cellpadding="4" width="{@width}">
    <tr class="dbTableHeaderRow">
      <xsl:for-each select="tgroup / thead / row / entry">
        <th class="dbTableHeaderEntry"><xsl:apply-templates/></th>
      </xsl:for-each>
    </tr>

    <xsl:for-each select="tgroup / tbody / row">
      <tr class="dbTableRow">
        <xsl:for-each select="entry">
          <td class="dbTableEntry"><xsl:apply-templates/></td>
        </xsl:for-each>
      </tr>
    </xsl:for-each>
    </table>
  </xsl:template>

  <!-- Bibliography support -->

  <xsl:template match="bibliography">
    <h2>Appendix: Bibliography</h2>

    <dl>
      <xsl:for-each select="biblioentry">
        <dt><a name="{@id}"><xsl:value-of select="abbrev"/></a></dt>
        <dd><xsl:apply-templates select="* [local-name() != 'abbrev']" mode="bib"
            />.</dd>
      </xsl:for-each>
    </dl>
  </xsl:template>

  <xsl:template match="title" mode="bib">
    <i><xsl:apply-templates/></i>
  </xsl:template>

  <xsl:template match="editor | orgname | city | country |
                       conftitle | confdates | pubdate | publishername |
                       isbn"
                mode="bib">
    <xsl:text>, </xsl:text><xsl:value-of select="."/>
  </xsl:template>

  <xsl:template match="releaseinfo | confgroup" mode="bib">
    <xsl:apply-templates mode="bib"/>
  </xsl:template>

  <xsl:template match="author" mode="bib">
    <xsl:text>; </xsl:text><xsl:value-of select="surname"/><xsl:text>, </xsl:text>
    <xsl:value-of select="firstname"/>
  </xsl:template>

  <xsl:template match="ulink" mode="bib">
    <xsl:text>, </xsl:text><a href="{@url}"><xsl:value-of select="@url"/></a>
  </xsl:template>

  <xsl:template match="text()" mode="bib"/>

  <!-- Fallback template -->

  <xsl:template match="*">
  <font color="blue">[<xsl:value-of select="local-name(current())"/>: <xsl:apply-templates/>]</font>
  </xsl:template>

  <!-- Footnote mode -->

  <xsl:template match="footnote/para[1]"
                mode="footnote">
    <dt><a name="footnote{count(preceding::footnote) + 1}">[<xsl:value-of select="count(preceding::footnote) + 1"/>]</a></dt>
    <dd><xsl:apply-templates/></dd>
  </xsl:template>

  <xsl:template match="section | itemizedlist | listitem | variablelist | varlistentry | term | para | symbol | literallayout | filename | classname | property | programlisting | packagename | entry | refname | refpurpose | methodname"
                mode="footnote">
    <xsl:apply-templates mode="footnote" select="*"/>
  </xsl:template>

  <xsl:template match="articleinfo | title | subtitle | citetitle | ulink |
                       bibliography | remark | emphasis | firstterm | interfacename |
                       literal | acronym"
                mode="footnote"/>


  <!-- ToC mode -->

  <xsl:template match = "article" mode = "ToC">
  <ul>
    <xsl:apply-templates mode = "ToC" select = "section" />
  </ul>
  </xsl:template>

  <xsl:template match = "section" mode = "ToC">
    <xsl:if test='string(@condition) = "" or contains(@condition, $condition)'>
      <xsl:if test="count(ancestor::section) &lt; 3">
        <li><a><xsl:call-template name="sect-id">
          <xsl:with-param name="attname" select='"href"'/>
        </xsl:call-template>
        <xsl:if test="$number-sects">
          <xsl:call-template name="sect-number"/>
        </xsl:if>
        <xsl:value-of select = "title" />
        </a></li>
        <ul>
          <xsl:apply-templates mode = "ToC" select = "section" />
        </ul>
      </xsl:if>
    </xsl:if>
  </xsl:template>


  <!-- RefEntry support -->

  <xsl:template match="chapter / refentry | section / refentry">
    <h3>
      <xsl:choose>
        <xsl:when test="@id"><a name="{@id}"><xsl:value-of select="refnamediv / refname"/></a></xsl:when>
        <xsl:otherwise><a name="{generate-id()}"><xsl:value-of select="refnamediv / refname"/></a></xsl:otherwise>
      </xsl:choose>
    </h3>

    <p><xsl:apply-templates select="refnamediv / refpurpose / node()"/></p>

    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="chapter / refentry / refsect1 | section / refentry / refsect1">
    <h4><xsl:value-of select="title"/></h4>

    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="refnamediv"/>

  <!-- Named templates -->

  <xsl:template name="fig-number">
    <xsl:value-of select="count(preceding::figure)+1"/>
    <xsl:text>. </xsl:text>
  </xsl:template>

  <xsl:template name="tabl-number">
    <xsl:number level="multiple" count="section[parent::article] | book[parent::*] | chapter | table"
                format="1."
                grouping-separator="."/>
    <xsl:text> </xsl:text>
  </xsl:template>

  <xsl:template name="sect-number">
    <xsl:number level="multiple"
                count='section[string(@condition) = "" or contains(@condition, $condition)] | book[parent::*] | chapter'
                format="1."
                grouping-separator="."/>
    <xsl:text> </xsl:text>
  </xsl:template>

  <xsl:template name="ref-number">
    <xsl:param name="refnode" select="."/>

    <xsl:choose>
      <xsl:when test='local-name($refnode) = "figure"'><xsl:text>figure </xsl:text>
        <xsl:call-template name="figure-number-ref">
          <xsl:with-param name="refnode" select="$refnode"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:when test='local-name($refnode) = "section"'>section
        <xsl:call-template name="sect-number-ref">
          <xsl:with-param name="refnode" select="$refnode"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:when test='local-name($refnode) = "biblioentry"'>
        <xsl:text>[</xsl:text>
        <a href="#{$refnode / @id}"><xsl:value-of select="$refnode / abbrev"/></a>
        <xsl:text>]</xsl:text>
      </xsl:when>
      <xsl:when test='local-name($refnode) = "refentry"'>
        <a href="#{$refnode / @id}"><xsl:value-of select="$refnode / refnamediv / refname"/></a>
      </xsl:when>
      <xsl:otherwise>appendix
        <xsl:call-template name="app-number-ref">
          <xsl:with-param name="refnode" select="$refnode"/>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="sect-number-ref">
    <xsl:param name="refnode" select="."/>

    <xsl:if test="$refnode/parent::section">
      <xsl:call-template name="sect-number-ref">
        <xsl:with-param name="refnode" select="$refnode/parent::section"/>
      </xsl:call-template>
      <xsl:text>.</xsl:text>
    </xsl:if>
    <xsl:number value="count($refnode/preceding-sibling::section)+1" format="1"/>
  </xsl:template>

  <xsl:template name="figure-number-ref">
    <xsl:param name="refnode" select="."/>

    <xsl:value-of select="count($refnode / preceding::figure) + 1"/>
  </xsl:template>

  <xsl:template name="app-number">
    <xsl:number level="multiple" count="appendix"
                format="A."
                grouping-separator="."/>
    <xsl:text> </xsl:text>
  </xsl:template>

  <xsl:template name="app-number-ref">
    <xsl:param name="refnode" select="."/>
    <xsl:number value="count($refnode/preceding-sibling::appendix)+1" format="A"/>
  </xsl:template>

  <xsl:template name="sect-id">
    <xsl:param name = "attname" select = '"name"'/>

    <xsl:attribute name="{$attname}">
      <xsl:if test='$attname = "href"'>#</xsl:if>
      <xsl:choose>
        <xsl:when test="@id"><xsl:value-of select="@id"/></xsl:when>
        <xsl:otherwise><xsl:value-of select="generate-id()"/></xsl:otherwise>
      </xsl:choose>
    </xsl:attribute>
  </xsl:template>

  <xsl:template name="docinfo">
  <xsl:param name="container" select='nothing'/>

  <table>
        <xsl:if test="$container/pubsnumber">
                <tr><th>Document #:</th>        <td><xsl:value-of select="$container/pubsnumber"/></td></tr>
        </xsl:if>
  <xsl:if test="$container/author/firstname">
  <tr><th>By:</th>  <td><xsl:value-of select="$container/author/firstname"/>
                        <xsl:text> </xsl:text>
                        <xsl:value-of select="$container/author/surname"/></td></tr>
  </xsl:if>
  <xsl:if test="$container/author/affiliation">
  <tr><th>
       <xsl:choose>
         <xsl:when test="$container/author/firstname">Affiliation:</xsl:when>
         <xsl:otherwise>Published by:</xsl:otherwise>
       </xsl:choose></th>
      <td><xsl:value-of select="$container/author/affiliation"/></td></tr>
  </xsl:if>
        <xsl:if test="$container/copyright">
                <tr><th>Copyright: </th> <td><xsl:value-of select="$container/copyright/year"/>
                                                                                                <xsl:text> </xsl:text>
                                                                                                <xsl:value-of select="$container/copyright/holder"/></td></tr>
        </xsl:if>
        <xsl:if test="$container/revhistory/revision/revnumber">
                <tr><th>Revision #:</th>        <td><xsl:value-of select="$container/revhistory/revision/revnumber"/></td></tr>
        </xsl:if>
        <xsl:if test="$container/revhistory/revision/date">
                <tr><th>Revision Date:</th>        <td><xsl:value-of select="$container/revhistory/revision/date"/></td></tr>
        </xsl:if>
  <xsl:if test="$container/pubdate">
  <tr><th>Date:</th><td><xsl:value-of select="$container/pubdate"/></td></tr>
  </xsl:if>
  <xsl:if test="$container/releaseinfo">
  <tr><th>Version:</th><td><xsl:value-of select="$container/releaseinfo"/></td></tr>
  </xsl:if>
  </table>
  </xsl:template>

</xsl:stylesheet>
