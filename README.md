[<img src="https://img.shields.io/travis/com/ontopia/ontopia.svg" alt="build status"/>](https://travis-ci.com/ontopia/ontopia)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/72f4a97299d3481db797de3c56b24096)](https://app.codacy.com/gh/ontopia/ontopia?utm_source=github.com&utm_medium=referral&utm_content=ontopia/ontopia&utm_campaign=Badge_Grade_Settings)
[<img src="https://img.shields.io/github/license/ontopia/ontopia.svg" alt="license"/>](https://github.com/ontopia/ontopia/blob/master/LICENSE.txt)
[<img src="https://img.shields.io/github/release/ontopia/ontopia.svg" alt="release"/>](https://github.com/ontopia/ontopia/releases)
[<img src="https://img.shields.io/maven-central/v/net.ontopia/ontopia-engine.svg" alt="maven central"/>](https://search.maven.org/search?q=g:net.ontopia%20a:ontopia-engine)

![Ontopia Logo](https://ontopia.net/images/logoBig.gif)

Welcome to Ontopia, the open source tools for building, maintaining and deploying 
[Topic Maps](http://en.wikipedia.org/wiki/Topic_Maps)-based applications. 

If you are a starting user of Ontopia, or want more general information, we recommend you check out 
[the Ontopia.net website](http://ontopia.net).

## Get Ontopia
[<img src="https://ontopia.net/images/download-button.png"/>](../releases/latest)

### Older versions
 * [Ontopia 5.2.2](../releases/tag/ontopia-5.2.2)

> **Note**: Using older versions is not advised

---

## Using Ontopia
You can use the above link to download a full Ontopia package that includes a fully configured 
[Tomcat](http://tomcat.apache.org/) instance that you can run on your local machine. This will allow you to start 
working with Topic Maps. See the [requirements](https://github.com/ontopia/ontopia/wiki/InstallGuide#requirements) and [installation guide](https://github.com/ontopia/ontopia/wiki/InstallGuide).

### Maven
You can also use Ontopia as a [maven](https://maven.apache.org/) dependency, allowing you to build Topic Maps 
applications yourself:
```xml
<repositories>
    <repository>
        <id>ontopia-releases</id>
        <url>http://ontopia.googlecode.com/svn/maven-repository</url>
    </repository>
</repositories>
<dependencies>
    <dependency>
        <groupId>net.ontopia</groupId>
        <artifactId>ontopia-engine</artifactId>
        <version>5.3.0</version>
    </dependency>
</dependencies>
````
See the [[Modules]] page for more possible maven modules and their function.

## Building Ontopia
To build the current Ontopia distribution run

```
$ mvn clean install && mvn clean install -Pontopia-distribution-tomcat
```

from a terminal. The distribution can then be found in 
`ontopia-distribution-tomcat/target/ontopia-distribution-tomcat-X.Y.Z-SNAPSHOT/`
where X, Y and Z are the current development version numbers.

Once you build the current Ontopia distribution you will probably 
want to play with the software.

Everything you want will be inside the distribution you just built;
the rest of the subversion checkout serves only to generate that
distribution. You can find the documentation within the distribution 
under the 'doc' directory.

If you're not already familiar with Ontopia, a good document to start
with is the install.html in the doc directory of the build you're
using.

If you're just starting out, try starting the Tomcat server as
described in section 4.3 of the install.html document, then in your
Web browser navigate to http://localhost:8080/ -- the web-based
applications listed there will give you plenty to do.

---

## Need more help?
* We have a [home page](http://www.ontopia.net).
* We have a [blog](http://ontopia.wordpress.com/).
* A [mailing list](http://groups.google.com/group/ontopia) has been set up.
* Join us for a chat on IRC: irc.freenode.net#ontopia ([log](http://logs.subjektzentrisch.de/ontopia/)).
* We are on [Twitter](http://twitter.com/ontopia).


