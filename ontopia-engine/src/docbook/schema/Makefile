
# $Id: Makefile,v 1.7 2004/12/01 10:28:51 larsga Exp $

all: schemaspec.html schema-lang-tut.html install.html

schemaspec.xml: schemaspec-src.xml schema.dtd schema-dtd-doc.xml
	./dbenrich.py schema.dtd schema-dtd-doc.xml schemaspec-src.xml schemaspec.xml

schemaspec.html: schemaspec.xml ../../stylesheets/docbook2html.xslt
	java com.icl.saxon.StyleSheet -o schemaspec.html schemaspec.xml ../../stylesheets/docbook2html.xslt make-toc=1 number-sects=1

schema-lang-tut.html: schema-lang-tut.xml ../../stylesheets/docbook2html.xslt
	java com.icl.saxon.StyleSheet -o schema-lang-tut.html schema-lang-tut.xml ../../stylesheets/docbook2html.xslt make-toc=1 number-sects=1

install.html: install.xml ../../stylesheets/docbook2html.xslt
	java com.icl.saxon.StyleSheet -o install.html install.xml ../../stylesheets/docbook2html.xslt make-toc=1 number-sects=1
