
<!--

 This XSLT stylesheet converts gcapaper documents to HTML.

-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

  <!-- Variables -->

  <!-- This variable controls whether a table of contents is generated
       or not. -->
  <xsl:param name = "make-toc" select = "false" />

  <!-- This variable controls whether sections are numbered or not. -->
  <xsl:param name = "number-sects" select = "false" />

  <!-- Main style rule -->

  <xsl:template match="gcapaper | paper">
  <html>
  <head>
    <title><xsl:value-of select="front / title"/></title>
    <link rel="stylesheet" href="whitepapers.css" type="text/css"/>

    <!-- <meta name=keywords content=a,b,c -->
    <xsl:if test="front / keyword">
      <xsl:element name="meta">
        <xsl:attribute name="name">keywords</xsl:attribute>
        <xsl:attribute name="content">
          <xsl:for-each select="front / keyword">
            <xsl:value-of select="."/>,
          </xsl:for-each>
        </xsl:attribute>
      </xsl:element>
    </xsl:if>

    <!-- <meta name=description content=abstract/p[1] 
    <xsl:if test="front / abstract">
      <xsl:element name="meta">
        <xsl:attribute name="name">description</xsl:attribute>
        <xsl:attribute name="content">
	  <xsl:value-of select="front / abstract / para[1]"/>,
        </xsl:attribute>
      </xsl:element>
    </xsl:if-->
  </head>

  <body>
  <xsl:apply-templates/>

  <xsl:if test="rear / acknowl">
    <h2>Acknowledgements</h2>

    <xsl:apply-templates select=" rear / acknowl / para"/> 
  </xsl:if>

  <xsl:if test="rear / bibliog">
    <h2>Bibliography</h2>

    <dl>
      <xsl:for-each select="rear / bibliog / bibitem">
        <dt>
          <xsl:choose>
	    <xsl:when test="@id">
              <a name="{@id}"><xsl:value-of select="bib"/></a>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="bib"/>
            </xsl:otherwise>
          </xsl:choose>
        </dt>
        <dd><xsl:apply-templates select="pub"/></dd>
      </xsl:for-each>
    </dl>
  </xsl:if>

  <xsl:if test="descendant::ftnote">
    <h2>Footnotes</h2>
    <table>
      <xsl:apply-templates select="descendant::ftnote" mode="footnote"/>
    </table>
  </xsl:if>
  </body>
  </html>
  </xsl:template>

  <xsl:template match="gcapaper / front | paper / front">
  <h1><xsl:value-of select="title"/></h1>
  <xsl:if test="subt">
    <h2 class="subtitle"><xsl:value-of select="subt"/></h2>
  </xsl:if>

  <table width="100%"><tr><td>

  <table>
  <xsl:for-each select="author">
  <tr><th>By:</th>  <td><xsl:value-of select="fname"/>
                        <xsl:text> </xsl:text>
                        <xsl:value-of select="surname"/>
    <xsl:if test="jobtitle">
    <xsl:text>, </xsl:text> <xsl:value-of select="jobtitle"/>
    </xsl:if>
  </td></tr>

  <tr><th>Affiliation:</th>
      <td><xsl:value-of select="address/affil"/>
          <xsl:if test="address/subaffil">,
            <xsl:value-of select="address/subaffil"/>
          </xsl:if></td></tr>

  <xsl:if test="address/aline">
    <tr><th>Address:</th>
        <td><xsl:value-of select="address/aline"/>,
            <xsl:value-of select="address/postcode"/>
	    <xsl:text> </xsl:text>
            <xsl:value-of select="address/city"/>,
            <xsl:value-of select="address/cntry"/></td></tr>
  </xsl:if>

  <xsl:if test="address/email">
    <tr><th>Email:</th>
        <td><a href="mailto:{address/email}"><xsl:value-of select="address/email"/></a></td></tr>
  </xsl:if>
  <xsl:if test="address/web">
    <tr><th>Web:</th>
        <td><a href="{address/web}"><xsl:value-of select="address/web"/></a></td></tr>
  </xsl:if>

  <xsl:if test="address/phone">
    <tr><th>Phone:</th>
        <td><xsl:value-of select="address/phone"/></td></tr>
  </xsl:if>
  <xsl:if test="address/fax">
    <tr><th>Fax:</th>
        <td><xsl:value-of select="address/fax"/></td></tr>
  </xsl:if>
  </xsl:for-each>
  </table>

  </td><td><a href="http://www.ontopia.net">
           <img src="logo.gif" alt="Ontopia" border="0" align="right"/>
           </a></td></tr></table>
            

  <xsl:if test="$make-toc">
    <h2>Table of contents</h2>
    <ul>
    <xsl:apply-templates select="../body/section" mode="ToC"/>
    </ul>
  </xsl:if>

  <xsl:if test="abstract">
  <h2>Abstract</h2>

  <xsl:apply-templates select="abstract"/>
  </xsl:if>

  <xsl:if test="author / bio">
  <h2>Biography</h2>

  <xsl:apply-templates select="author / bio / *"/>
  </xsl:if>
  </xsl:template>


  <xsl:template match="section">
    <h2><a><xsl:call-template name="sect-id">
           <xsl:with-param name="attname" select='"name"'/>
           </xsl:call-template>
      <xsl:if test="$number-sects">
        <xsl:call-template name="sect-number"/>
      </xsl:if>
      <xsl:value-of select="title"/>
    </a></h2>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="subsec1">
    <h3><a><xsl:call-template name="sect-id">
           <xsl:with-param name="attname" select='"name"'/>
           </xsl:call-template>
      <xsl:if test="$number-sects">
        <xsl:call-template name="sect-number"/>
      </xsl:if>
      <xsl:value-of select="title"/>
    </a></h3>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="subsec2">
    <h4><a><xsl:call-template name="sect-id">
           <xsl:with-param name="attname" select='"name"'/>
           </xsl:call-template>
      <xsl:if test="$number-sects">
        <xsl:call-template name="sect-number"/>
      </xsl:if>
      <xsl:value-of select="title"/>
    </a></h4>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="subsec3">
    <h5><a><xsl:call-template name="sect-id">
           <xsl:with-param name="attname" select='"name"'/>
           </xsl:call-template>
      <xsl:if test="$number-sects">
        <xsl:call-template name="sect-number"/>
      </xsl:if>
      <xsl:value-of select="title"/>
    </a></h5>
    <xsl:apply-templates/>
  </xsl:template>

 <!-- Blocks -->

  <xsl:template match="para">
    <p><xsl:apply-templates/></p>
  </xsl:template>

  <xsl:template match="randlist">
    <ul>
      <xsl:if test="title">
      <b><xsl:value-of select="title"/></b>
      </xsl:if>

      <xsl:for-each select="li">
        <li><xsl:apply-templates/></li>
      </xsl:for-each>
    </ul>
  </xsl:template>

  <xsl:template match="deflist">
    <xsl:choose>
    <xsl:when test="title">
    <p><b><font size="+1"><xsl:value-of select="title"/></font></b></p>

    <blockquote>
    <dl>
      <xsl:for-each select="def.item">
	<dt><xsl:apply-templates select="def.term"/></dt>
	<dd><xsl:apply-templates select="def"/></dd>
      </xsl:for-each>
    </dl>  
    </blockquote>
    </xsl:when>

    <xsl:otherwise>
    <dl>
      <xsl:for-each select="def.item">
	<dt><xsl:apply-templates select="def.term"/></dt>
	<dd><xsl:apply-templates select="def"/></dd>
      </xsl:for-each>
    </dl>
    </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

    <xsl:template match="def.term | def">
      <xsl:apply-templates/>
    </xsl:template>

  <xsl:template match="seqlist">
    <ol>
      <xsl:if test="title">
      <b><xsl:value-of select="title"/></b>
      </xsl:if>

      <xsl:for-each select="li">
        <li><xsl:apply-templates/></li>
      </xsl:for-each>
    </ol>
  </xsl:template>

  <xsl:template match="code.block | verbatim | sgml.block">
    <pre>
    <xsl:apply-templates/>
    </pre>
  </xsl:template>

  <xsl:template match="lquote">
    <blockquote>
    <xsl:apply-templates/>
    </blockquote>
  </xsl:template>

  <xsl:template match="rear"/>

  <xsl:template match="bibref">
    <xsl:variable name="refloc" select="@refloc"/>
    <xsl:variable name="refnode" select="//bibitem[@id = $refloc]"/>
    <a href="#{@refloc}">
      <xsl:text>[</xsl:text>
      <xsl:value-of select="$refnode/bib"/>
      <xsl:text>]</xsl:text>
    </a>
  </xsl:template>

  <xsl:template match="figure">
    <xsl:if test="title">
      <p align="center"><b><xsl:value-of select="title"/></b></p>
    </xsl:if>

    <xsl:choose>
      <xsl:when test="graphic / @figname">
        <p align="center">
        <img src="{unparsed-entity-uri(string(graphic/@figname))}"/>
        </p>
      </xsl:when>
      <xsl:when test="graphic / @href">
        <p align="center">
        <img src="{graphic/@href}"/>
        </p>
      </xsl:when>
      <xsl:when test="code.block">
        <pre><xsl:apply-templates select="code.block"/></pre>
      </xsl:when>
      <xsl:when test="verbatim">
        <xsl:apply-templates select="verbatim"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:message>Problem in figure: unknown content!</xsl:message>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="warning">
    <dl><dt class="warning">Warning</dt>
    <dd><xsl:apply-templates/></dd>
    </dl>
  </xsl:template>

  <xsl:template match="note">
    <dl><dt class="note">Note</dt>
    <dd><xsl:apply-templates/></dd>
    </dl>
  </xsl:template>

  <!-- Inline -->

  <xsl:template match="acronym">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="expansion | keyword">
  </xsl:template>

  <xsl:template match="title"/>

  <xsl:template match="cit">
    <cite><xsl:apply-templates/></cite>
  </xsl:template>

  <xsl:template match="br"><br /></xsl:template>

  <xsl:template match="code">
    <code><xsl:apply-templates/></code>
  </xsl:template>

  <xsl:template match="super | sup">
    <sup><xsl:apply-templates/></sup>
  </xsl:template>

  <xsl:template match="a">
    <!-- NOTE: the 'a' element must be empty -->
    <a href="{@href}"><xsl:value-of select="@href"/></a>
  </xsl:template>

  <xsl:template match="highlight[@style='bold'] | b">
    <b><xsl:apply-templates/></b>
  </xsl:template>

  <xsl:template match="highlight[@style='ital'] | i">
    <i><xsl:apply-templates/></i>
  </xsl:template>

  <xsl:template match="highlight[@style='bital']">
    <b><i><xsl:apply-templates/></i></b>
  </xsl:template>

  <xsl:template match="highlight[not(@style)]">
    <i><xsl:apply-templates/></i>
  </xsl:template>

  <xsl:template match="acronym.grp">
    <xsl:value-of select="acronym"/> 
    <xsl:text> (</xsl:text>
      <xsl:value-of select="expansion"/>
    <xsl:text>)</xsl:text>
  </xsl:template>

  <xsl:template match="b">
    <b><xsl:apply-templates/></b>
  </xsl:template>

  <xsl:template match="i">
    <i><xsl:apply-templates/></i>
  </xsl:template>

  <xsl:template match="tt | sgml">
    <tt><xsl:apply-templates/></tt>
  </xsl:template>

  <xsl:template match="web">
    <a href="{.}"><xsl:apply-templates/></a>
  </xsl:template>

  <xsl:template match="ftnote">
    <sup><a href="#footnote{count(preceding::ftnote) + 1}">[<xsl:value-of select="count(preceding::ftnote) + 1"/>]</a></sup>
  </xsl:template>

  <xsl:template match="xref">
    <xsl:variable name = "linkend" select = "@refloc"/>
    <xsl:variable name = "refnode" select = '//* [@id = $linkend]'/>

    <a href="#{@refloc}">section 
      <xsl:call-template name="sect-number-ref">
        <xsl:with-param name="refnode" select="$refnode"/>
      </xsl:call-template></a>
  </xsl:template>

  <xsl:template match="para / verbatim">
    <tt><xsl:apply-templates/></tt>
  </xsl:template>

  <!-- Ignored -->

  <xsl:template match="body | pub | abstract">
    <xsl:apply-templates/>
  </xsl:template>

  <!-- Tables -->

  <xsl:template match="table | tgroup | thead | tbody | tr | td | th">
    <xsl:copy>
      <xsl:apply-templates select="node()"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="row">
    <tr><xsl:apply-templates/></tr>
  </xsl:template>

  <xsl:template match="entry">
    <td><xsl:apply-templates/></td>
  </xsl:template>


  <!-- ===== ToC mode ======================================== -->

  <xsl:template match="section" mode = "ToC">
  <li><a><xsl:call-template name="sect-id">
           <xsl:with-param name="attname" select='"href"'/>
         </xsl:call-template>
      <xsl:if test="$number-sects">
        <xsl:call-template name="sect-number"/>
      </xsl:if>
      <xsl:value-of select = "title" />
  </a></li>
  <ul>
    <xsl:apply-templates mode = "ToC" select = "subsec1" />
  </ul>
  </xsl:template>

  <xsl:template match="subsec1" mode = "ToC">
  <li><a><xsl:call-template name="sect-id">
           <xsl:with-param name="attname" select='"href"'/>
         </xsl:call-template>
      <xsl:if test="$number-sects">
        <xsl:call-template name="sect-number"/>
      </xsl:if>
      <xsl:value-of select = "title" />
  </a></li>
  <ul>
    <xsl:apply-templates mode = "ToC" select = "subsec2" />
  </ul>
  </xsl:template>

  <xsl:template match="subsec2" mode = "ToC">
  <li><a><xsl:call-template name="sect-id">
           <xsl:with-param name="attname" select='"href"'/>
         </xsl:call-template>
      <xsl:if test="$number-sects">
        <xsl:call-template name="sect-number"/>
      </xsl:if>
      <xsl:value-of select = "title" />
  </a></li>
  <!--ul>
    <xsl:apply-templates mode = "ToC" select = "subsec3" />
  </ul-->
  </xsl:template>


  <!-- ===== footnote mode ======================================== -->

  <xsl:template match="ftnote" mode="footnote">
    <tr><th valign="top"><a name="footnote{count(preceding::ftnote) + 1}"><xsl:value-of select="count(preceding::ftnote) + 1"/></a>&#160;</th>
        <td><xsl:apply-templates/></td></tr>
  </xsl:template>


  <!-- Named templates -->

  <xsl:template name="sect-number">
    <xsl:number level="multiple" count="section | subsec1  | subsec2" format="1."
                grouping-separator="."/>
    <xsl:text> </xsl:text>
  </xsl:template>

  <xsl:template name="sect-number-ref">
    <xsl:param name="refnode" select="."/>

    <!-- section | subsec1  | subsec2 -->

    <xsl:choose>
      <xsl:when test="local-name($refnode) = 'section'">
        <xsl:number value="count($refnode/preceding-sibling::section)+1" format="1."/>
      </xsl:when>

      <xsl:when test="local-name($refnode) = 'subsec1'">
        <xsl:call-template name="sect-number-ref">
          <xsl:with-param name="refnode" select='$refnode/..'/>
        </xsl:call-template>
        <xsl:number value="count($refnode/preceding-sibling::subsec1)+1" format="1."/>
      </xsl:when>

      <xsl:when test="local-name($refnode) = 'subsec2'">
        <xsl:call-template name="sect-number-ref">
          <xsl:with-param name="refnode" select='$refnode/..'/>
        </xsl:call-template>
        <xsl:number value="count($refnode/preceding-sibling::subsec2)+1" format="1."/>
      </xsl:when>
    </xsl:choose>
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

  <!-- Fallback template -->

  <xsl:template match="*">
  <font color="blue">[<xsl:value-of select="local-name(current())"/>: <xsl:apply-templates/>]</font>
  </xsl:template>

</xsl:stylesheet>
