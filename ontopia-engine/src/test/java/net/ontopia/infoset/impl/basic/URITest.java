/*
 * #!
 * Ontopia Engine
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

package net.ontopia.infoset.impl.basic;

import java.net.MalformedURLException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.utils.OntopiaRuntimeException;
import org.junit.Assert;
import org.junit.Test;

public class URITest {

  // --- normalization

  @Test
  public void testHttpOrdinary() {
    assertNormalizesTo("http://www.ontopia.net", "http://www.ontopia.net/");
  }

  @Test
  public void testHttpOrdinarySlash() {
    assertNormalizesTo("http://www.ontopia.net/", "http://www.ontopia.net/");
  }

  @Test
  public void testHttpPort80() {
    assertNormalizesTo("http://www.ontopia.net:80", "http://www.ontopia.net/");
  }

  @Test
  public void testHttpCaseSensitive() {
    assertNormalizesTo("http://www.ONTOPIA.net/temp.html#README",
		 "http://www.ontopia.net/temp.html#README");
  }

  @Test
  public void testHttpPort80Slash() {
    assertNormalizesTo("http://www.ontopia.net:80/", "http://www.ontopia.net/");
  }

  @Test
  public void testHttpPort8080() {
    assertNormalizesTo("http://www.ontopia.net:8080",
		 "http://www.ontopia.net:8080/");
  }

  @Test
  public void testHttpPort8080Slash() {
    assertNormalizesTo("http://www.ontopia.net:8080/",
		 "http://www.ontopia.net:8080/");
  }

  @Test
  public void testFtpPort21() {
    assertNormalizesTo("ftp://ftp.ontopia.net:21", "ftp://ftp.ontopia.net/");
  }

  @Test
  public void testFtpPort21Dir() {
    assertNormalizesTo("ftp://ftp.ontopia.net:21/pub",
		 "ftp://ftp.ontopia.net/pub");
  }

  @Test
  public void testFtpOrdinary() {
    assertNormalizesTo("ftp://ftp.ontopia.net/pub",
		 "ftp://ftp.ontopia.net/pub");
  }

  @Test
  public void testFtpOrdinarySlash() {
    assertNormalizesTo("ftp://ftp.ontopia.net/pub/",
		 "ftp://ftp.ontopia.net/pub/");
  }

  @Test
  public void testFileOrdinary() {
    assertNormalizesTo("file:///ifikurs.xtm#in105",
		 "file:/ifikurs.xtm#in105");
  }

  @Test
  public void testFileJavaStyle() {
    assertNormalizesTo("file:/home/larsga/cvs-co/src/java/tst.py",
		 "file:/home/larsga/cvs-co/src/java/tst.py");
  }

  @Test
  public void testFileOperaStyle() {
    assertNormalizesTo("file://localhost/home/larsga/.bashrc",
		 "file://localhost/home/larsga/.bashrc");
  }
  
  @Test
  public void testFileOperaStyleUpcase() {
    assertNormalizesTo("file://LOCALHOST/home/larsga/.bashrc",
		 "file://LOCALHOST/home/larsga/.bashrc");
  }
  
//    public void testFileWithDriveColon() {
//      normalizesTo("file:///c:/something/blah.txt",
//  		 "file:/c|/something/blah.txt");
//    }

  @Test
  public void testUNCFileNames() {
    assertNormalizesTo("file://server/directory/file.doc",
                 "file://server/directory/file.doc");
  }

  @Test
  public void testPercentEscapeAtEnd() {
    assertNormalizesTo("gopher://spinaltap.micro.umn.edu/00/Weather/California/Los%20",
		 "gopher://spinaltap.micro.umn.edu/00/Weather/California/Los ");
  }

  @Test
  public void testGopher70() {
    assertNormalizesTo("gopher://spinaltap.micro.umn.edu:70/00/Weather/California",
		 "gopher://spinaltap.micro.umn.edu/00/Weather/California");
  }

  @Test
  public void testPercentEscapeWithUpAlpha() {
    assertNormalizesTo("http://www.ontopia.net/%4a",
		 "http://www.ontopia.net/J");
  }

  @Test
  public void testPercentEscapeWithLowAlpha() {
    assertNormalizesTo("http://www.ontopia.net/%4A",
		 "http://www.ontopia.net/J");
  }

  @Test
  public void testDoubleSlash() {
    assertNormalizesTo("http://www.ontopia.net/a//b/c.html",
		 "http://www.ontopia.net/a/b/c.html");
  }

  @Test
  public void testUpOneDir() {
    assertNormalizesTo("http://www.ontopia.net/a/../b/c.html",
		 "http://www.ontopia.net/b/c.html");
  }

  @Test
  public void testUpTwoDirs() {
    assertNormalizesTo("http://www.ontopia.net/a/d/../../b/c.html",
		 "http://www.ontopia.net/b/c.html");
  }

  @Test
  public void testUpThreeDirs() {
    assertNormalizesTo("http://www.ontopia.net/a/d/e/../../../b/c.html",
		 "http://www.ontopia.net/b/c.html");
  }

  @Test
  public void testUpOneDirTooFar() {
    assertNormalizesTo("http://www.ontopia.net/a/d/e/../../../../b/c.html",
		 "http://www.ontopia.net/b/c.html");
  }

  @Test
  public void testSingleDotDir() {
    assertNormalizesTo("http://www.ontopia.net/a/./b/c.html",
		 "http://www.ontopia.net/a/b/c.html");
  }

  @Test
  public void testUppercaseUserAndPassword() {
    assertNormalizesTo("http://JUSTIN:PASSWORD@WWW.VLC.COM.AU/ABC",
		 "http://JUSTIN:PASSWORD@www.vlc.com.au/ABC");
  }

//    public void testCommonMistake2() {
//      normalizesTo("http:www.vlc.com.au",
//  		 "http://www.vlc.com.au");
//    }
  
  @Test
  public void testVLC1() {
    assertNormalizesTo("http://www.vlc.com.au/something",
		 "http://www.vlc.com.au/something");
  }

  @Test
  public void testVLC2() {
    assertNormalizesTo("http://www.vlc.com.au/something?query=another+thisthing",
		 "http://www.vlc.com.au/something?query=another thisthing");
  }

  @Test
  public void testVLC3() {
    assertNormalizesTo("http://www.vlc.com.au/something?query=another+thisthing#ref",
		 "http://www.vlc.com.au/something?query=another thisthing#ref");
  }
  
  @Test
  public void testVLC4() {
    assertNormalizesTo("http://www.vlc.com.au?query",
		 "http://www.vlc.com.au/?query");
  }

  @Test
  public void testVLC5() {
    assertNormalizesTo("http://www.vlc.com.au?query=another+thisthing",
		 "http://www.vlc.com.au/?query=another thisthing");
  }

  @Test
  public void testVLC6() {
    assertNormalizesTo("http://www.vlc.com.au?query=another+thisthing#ref",
		 "http://www.vlc.com.au/?query=another thisthing#ref");
  }

  @Test
  public void testVLC7() {
    assertNormalizesTo("http://www.vlc.com.au:80?query=another+thisthing#ref",
		 "http://www.vlc.com.au/?query=another thisthing#ref");
  }

  @Test
  public void testVLC8() {
    assertNormalizesTo("http://www.vlc.com.au?query#ref",
		 "http://www.vlc.com.au/?query#ref");
  }

  @Test
  public void testVLC9() {
    assertNormalizesTo("http://www.vlc.com.au#ref",
		 "http://www.vlc.com.au/#ref");
  }

  @Test
  public void testVLC10() {
    assertNormalizesTo("http://www.vlc.com.au/",
		 "http://www.vlc.com.au/");
  }

  @Test
  public void testVLC11() {
    assertNormalizesTo("http://www.vlc.com.au:8080/",
		 "http://www.vlc.com.au:8080/");
  }

  @Test
  public void testVLC12() {
    assertNormalizesTo("http://www.vlc.com.au:8080",
		 "http://www.vlc.com.au:8080/");
  }

  @Test
  public void testVLC13() {
    assertNormalizesTo("http://justin@www.vlc.com.au:8080/",
		 "http://justin@www.vlc.com.au:8080/");
  }

  @Test
  public void testVLC14() {
    assertNormalizesTo("http://justin@www.vlc.com.au:8080",
		 "http://justin@www.vlc.com.au:8080/");
  }

  @Test
  public void testVLC15() {
    assertNormalizesTo("http://justin:password@www.vlc.com.au:8080/",
		 "http://justin:password@www.vlc.com.au:8080/");
  }

  @Test
  public void testVLC16() {
    assertNormalizesTo("http://justin:password@www.vlc.com.au:8080",
		 "http://justin:password@www.vlc.com.au:8080/");
  }

  @Test
  public void testVLC17() {
    assertNormalizesTo("http://justin:password@www.vlc.com.au/",
		 "http://justin:password@www.vlc.com.au/");
  }

  @Test
  public void testVLC18() {
    assertNormalizesTo("http://justin:password@www.vlc.com.au",
		 "http://justin:password@www.vlc.com.au/");
  }

  @Test
  public void testVLC19() {
    assertNormalizesTo("http://justin@www.vlc.com.au",
		 "http://justin@www.vlc.com.au/");
  }

  @Test
  public void testVLC20() {
    assertNormalizesTo("file:///c|/something/blah.txt",
		 "file:/c|/something/blah.txt");
  }

  @Test
  public void testVLC21() {
    assertNormalizesTo("file:/c|/something/blah.txt",
		 "file:/c|/something/blah.txt");
  }
  
  @Test
  public void testRFC2396_1() {
    assertNormalizesTo("ftp://ftp.is.co.za/rfc/rfc1808.txt",
		 "ftp://ftp.is.co.za/rfc/rfc1808.txt");
  }

  @Test
  public void testRFC2396_2() {
    assertNormalizesTo("gopher://spinaltap.micro.umn.edu/00/Weather/California/Los%20Angeles",
		 "gopher://spinaltap.micro.umn.edu/00/Weather/California/Los Angeles");
  }

  @Test
  public void testRFC2396_3() {
    assertNormalizesTo("http://www.math.uio.no/faq/compression-faq/part1.html",
		 "http://www.math.uio.no/faq/compression-faq/part1.html");
  }

  @Test
  public void testRFC2396_4() {
    assertNormalizesTo("mailto:mduerst@ifi.unizh.ch",
		 "mailto:mduerst@ifi.unizh.ch");
  }

  @Test
  public void testRFC2396_5() {
    assertNormalizesTo("news:comp.infosystems.www.servers.unix",
		 "news:comp.infosystems.www.servers.unix");
  }

  @Test
  public void testRFC2396_6() {
    assertNormalizesTo("telnet://melvyl.ucop.edu/",
		 "telnet://melvyl.ucop.edu/");
  }
  
  @Test
  public void testSingleDotAtEnd() {
    assertNormalizesTo("http://www.math.uio.no/.",
		 "http://www.math.uio.no/");
  }

  @Test
  public void testSingleDotAtEndWithQuery() {
    assertNormalizesTo("http://www.math.uio.no/.?query",
		 "http://www.math.uio.no/?query");
  }

  // --- check illegal URIs

  @Test
  public void testEmpty() {
    assertIllegal("");
  }
  
  @Test
  public void testEmpty2() {
    assertIllegal(":");
  }
  
  @Test
  public void testNoTermination() {
    assertIllegal("http");
  }
  
  @Test
  public void testWrongTermination() {
    assertIllegal("http/");
  }

  @Test
  public void testIllegalCharacterInScheme() {
    assertIllegal("URI|file:/tst.txt");
  }

  @Test
  public void testTwoHashCharacters() {
    assertIllegal("http://www.viessmann.com#test#again");
  }

//   public void testWhitespace() {
//     verifyIllegal("  ftp://ftp.ontopia.net/pub/  ");
//   }

  @Test
  public void testNonAsciiCharsInFragment() {
    assertIllegal("http://www.math.uio.no/abc/#f\u00F8\u00F8");
  }
  
  // --- relative URI resolution

  @Test
  public void testAbsoluteResolution() {
    assertResolvesTo("http://www.ontopia.net:8080/ugga/bugga.xtm",
	       "http://www.garshol.priv.no/rock.xtm",
	       "http://www.garshol.priv.no/rock.xtm");
  }

  @Test
  public void testFragmentResolution() {
    assertResolvesTo("http://www.ontopia.net:8080/ugga/bugga.xtm",
	       "#boogie",
	       "http://www.ontopia.net:8080/ugga/bugga.xtm#boogie");
  }

  @Test
  public void testFileResolution() {
    assertResolvesTo("http://www.ontopia.net:8080/ugga/bugga.xtm",
	       "boggu.xtm",
	       "http://www.ontopia.net:8080/ugga/boggu.xtm");
  }

  @Test
  public void testDownDirResolution() {
    assertResolvesTo("http://www.ontopia.net:8080/ugga/bugga.xtm",
	       "ugga/boggu.xtm",
	       "http://www.ontopia.net:8080/ugga/ugga/boggu.xtm");
  }

  @Test
  public void testUpDirResolution() {
    assertResolvesTo("http://www.ontopia.net:8080/ugga/bugga.xtm",
	       "../boggu.xtm",
	       "http://www.ontopia.net:8080/boggu.xtm");
  }

  @Test
  public void testSameFileFragmentResolution() {
    assertResolvesTo("http://www.ontopia.net:8080/ugga/bugga.xtm",
	       "bugga.xtm#rongo",
	       "http://www.ontopia.net:8080/ugga/bugga.xtm#rongo");
  }

  @Test
  public void testFileSameFileFragmentResolution() {
    assertResolvesTo("file:/home/larsga/tmp/out.xtm",
	       "out.xtm#in",
	       "file:/home/larsga/tmp/out.xtm#in");
  }

  @Test
  public void testFileFragmentResolution() {
    assertResolvesTo("file:/home/larsga/tmp/out.xtm",
	       "#in",
	       "file:/home/larsga/tmp/out.xtm#in");
  }

  @Test
  public void testFileOperaFuckup() throws MalformedURLException {
    LocatorIF base =
      new URILocator("file:/home/larsga/cvs-co/topicmaps/opera/opera.xtm");
    LocatorIF base2 = base.resolveAbsolute("opera-template.xtm");
    LocatorIF abs = base2.resolveAbsolute("geography.xtm");

    Assert.assertTrue("Two-step normalization produced wrong result",
	   abs.getAddress().equals("file:/home/larsga/cvs-co/topicmaps/opera/geography.xtm"));
  }

  @Test
  public void testFileDownDirResolution() {
    assertResolvesTo("file:/home/larsga/tmp/out.xtm",
	       "out/out.xtm",
	       "file:/home/larsga/tmp/out/out.xtm");
  }

  @Test
  public void testFileUpDirResolution() {
    assertResolvesTo("file:/home/larsga/tmp/out.xtm",
	       "../out.xtm",
	       "file:/home/larsga/out.xtm");
  }

  @Test
  public void testFileUpOneDirTooFarResolution() {
    assertResolvesTo("file:/home/out.xtm",
	       "../../out.xtm",
	       "file:/out.xtm");
  }

  @Test
  public void testFragmentWithLatin1() {
    assertResolvesTo("file:/home/larsga/tmp/out.xtm",
	       "#V_AM\u00F8ller",
	       "file:/home/larsga/tmp/out.xtm#V_AM\u00F8ller");
  }

  @Test
  public void testFragmentWithNonLatin1() {
    assertResolvesTo("file:/home/larsga/tmp/out.xtm",
	       "#V_AM\u01F8ller",
	       "file:/home/larsga/tmp/out.xtm#V_AM\u01F8ller");
  }

  @Test
  public void testMailTo() {
    assertResolvesTo("mailto:larsga@ontopia.net",
	       "http://www.ontopia.net:8080/ugga/bugga.xtm",
	       "http://www.ontopia.net:8080/ugga/bugga.xtm");
  }

  @Test
  public void testMailToInvalid() {
    assertResolveInvalid("mailto:larsga@ontopia.net",
			 "//www.ontopia.net:8080/ugga/bugga.xtm");
  }

  //public void testCommonMistake() {
  //  resolvesTo("http:www.ontopia.net",
  //             "index.html",
  //             "http://www.ontopia.net/index.html");
  //}

  @Test
  public void testRFC2396C_1() {
    assertResolvesTo("http://a/b/c/d;p?q",
	       "g:h",
	       "g:h");
  }
  
  @Test
  public void testRFC2396C_2() {
    assertResolvesTo("http://a/b/c/d;p?q",
	       "g",
	       "http://a/b/c/g");
  }
  
  @Test
  public void testRFC2396C_3() {
    assertResolvesTo("http://a/b/c/d;p?q",
	       "./g",
	       "http://a/b/c/g");
  }
  
  @Test
  public void testRFC2396C_4() {
    assertResolvesTo("http://a/b/c/d;p?q",
	       "g/",
	       "http://a/b/c/g/");
  }
  
  @Test
  public void testRFC2396C_5() {
    assertResolvesTo("http://a/b/c/d;p?q",
	       "/g",
	       "http://a/g");
  }

  // FIXME: A minor bug. Costly to fix.
//    public void testRFC2396C_5Variant() {
//      resolvesTo("http://a/b/c/d;p?q",
//  	       "/g/../y",
//  	       "http://a/y");
//    }
  
  @Test
  public void testRFC2396C_6() {
    assertResolvesTo("http://a/b/c/d;p?q",
	       "//g",
	       "http://g");
  }
  
  @Test
  public void testRFC2396C_7() {
    assertResolvesTo("http://a/b/c/d;p?q",
	       "?y",
	       "http://a/b/c/?y");
  }
  
  @Test
  public void testRFC2396C_8() {
    assertResolvesTo("http://a/b/c/d;p?q",
	       "g?y",
	       "http://a/b/c/g?y");
  }
  
  @Test
  public void testRFC2396C_9() {
    assertResolvesTo("http://a/b/c/d;p?q",
	       "#s",
	       "http://a/b/c/d;p?q#s");
  }
  
  @Test
  public void testRFC2396C_10() {
    assertResolvesTo("http://a/b/c/d;p?q",
	       "g#s",
	       "http://a/b/c/g#s");
  }
  
  @Test
  public void testRFC2396C_11() {
    assertResolvesTo("http://a/b/c/d;p?q",
	       "g?y#s",
	       "http://a/b/c/g?y#s");
  }
  
  @Test
  public void testRFC2396C_12() {
    assertResolvesTo("http://a/b/c/d;p?q",
	       ";x",
	       "http://a/b/c/;x");
  }
  
  @Test
  public void testRFC2396C_13() {
    assertResolvesTo("http://a/b/c/d;p?q",
	       "g;x",
	       "http://a/b/c/g;x");
  }
  
  @Test
  public void testRFC2396C_14() {
    assertResolvesTo("http://a/b/c/d;p?q",
	       "g;x?y#s",
	       "http://a/b/c/g;x?y#s");
  }
  
  @Test
  public void testRFC2396C_15() {
    assertResolvesTo("http://a/b/c/d;p?q",
	       ".",
	       "http://a/b/c/");
  }
  
  @Test
  public void testRFC2396C_16() {
    assertResolvesTo("http://a/b/c/d;p?q",
	       "./",
	       "http://a/b/c/");
  }
  
  @Test
  public void testRFC2396C_17() {
    assertResolvesTo("http://a/b/c/d;p?q",
	       "../",
	       "http://a/b/");
  }
  
  @Test
  public void testRFC2396C_18() {
    assertResolvesTo("http://a/b/c/d;p?q",
	       "..",
	       "http://a/b/");
  }
  
  @Test
  public void testRFC2396C_19() {
    assertResolvesTo("http://a/b/c/d;p?q",
	       "../g",
	       "http://a/b/g");
  }
  
  @Test
  public void testRFC2396C_20() {
    assertResolvesTo("http://a/b/c/d;p?q",
	       "../..",
	       "http://a/");
  }
  
  @Test
  public void testRFC2396C_21() {
    assertResolvesTo("http://a/b/c/d;p?q",
	       "../../",
	       "http://a/");
  }
  
  @Test
  public void testRFC2396C_22() {
    assertResolvesTo("http://a/b/c/d;p?q",
	       "../../g",
	       "http://a/g");
  }
  
  @Test
  public void testRFC2396C_23() {
    assertResolvesTo("http://a/b/c/d;p?q",
	       "",
	       "http://a/b/c/d;p?q");
  }
  
  @Test
  public void testRFC2396C_24() {
    assertResolvesTo("http://a/b/c/d;p?q",
	       "../../../g",
	       "http://a/g"); // slight deviation from RFC here
  }
  
  @Test
  public void testRFC2396C_25() {
    assertResolvesTo("http://a/b/c/d;p?q",
	       "../../../../g",
	       "http://a/g"); // slight deviation from RFC here
  }
  
  @Test
  public void testRFC2396C_26() {
    assertResolvesTo("http://a/b/c/d;p?q",
	       "g.",
	       "http://a/b/c/g.");
  }
  
  @Test
  public void testRFC2396C_27() {
    assertResolvesTo("http://a/b/c/d;p?q",
	       ".g",
	       "http://a/b/c/.g");
  }
  
  @Test
  public void testRFC2396C_28() {
    assertResolvesTo("http://a/b/c/d;p?q",
	       "g..",
	       "http://a/b/c/g..");
  }
  
  @Test
  public void testRFC2396C_29() {
    assertResolvesTo("http://a/b/c/d;p?q",
	       "..g",
	       "http://a/b/c/..g");
  }
  
  @Test
  public void testRFC2396C_30() {
    assertResolvesTo("http://a/b/c/d;p?q",
	       "./../g",
	       "http://a/b/g");
  }
  
  @Test
  public void testRFC2396C_31() {
    assertResolvesTo("http://a/b/c/d;p?q",
	       "./g/.",
	       "http://a/b/c/g/");
  }
  
  @Test
  public void testRFC2396C_32() {
    assertResolvesTo("http://a/b/c/d;p?q",
	       "g/./h",
	       "http://a/b/c/g/h");
  }
  
  @Test
  public void testRFC2396C_33() {
    assertResolvesTo("http://a/b/c/d;p?q",
	       "g/../h",
	       "http://a/b/c/h");
  }
  
  @Test
  public void testRFC2396C_34() {
    assertResolvesTo("http://a/b/c/d;p?q",
	       "g;x=1/./y",
	       "http://a/b/c/g;x=1/y");
  }
  
  @Test
  public void testRFC2396C_35() {
    assertResolvesTo("http://a/b/c/d;p?q",
	       "g;x=1/../y",
	       "http://a/b/c/y");
  }
  
  @Test
  public void testRFC2396C_36() {
    assertResolvesTo("http://a/b/c/d;p?q",
	       "g?y/./x",
	       "http://a/b/c/g?y/./x");
  }
  
  @Test
  public void testRFC2396C_37() {
    assertResolvesTo("http://a/b/c/d;p?q",
	       "g?y/../x",
	       "http://a/b/c/g?y/../x");
  }
  
  @Test
  public void testRFC2396C_38() {
    assertResolvesTo("http://a/b/c/d;p?q",
	       "g#s/./x",
	       "http://a/b/c/g#s/./x");
  }
  
  @Test
  public void testRFC2396C_39() {
    assertResolvesTo("http://a/b/c/d;p?q",
	       "g#s/../x",
	       "http://a/b/c/g#s/../x");
  }

  @Test
  public void testExposedByLTM() {
    assertResolvesTo("http://psi.ontopia.net/",
	       "ontopia/ontopia.xtm#ontopia",
	       "http://psi.ontopia.net/ontopia/ontopia.xtm#ontopia");
  }

  @Test
  public void testExposedByLTM2() {
    assertResolvesTo("http://psi.ontopia.net",
	       "ontopia",
	       "http://psi.ontopia.net/ontopia");
  }

  @Test
  public void testExposedByLTM3() {
    assertResolvesTo("http://psi.ontopia.net/",
	       "ontopia",
	       "http://psi.ontopia.net/ontopia");
  }

  // http://www.apache.org/~fielding/uri/rev-2002/issues.html#017-rdf-fragment
  @Test
  public void testRDFCaseI() {
    assertResolvesTo("http://example.org/dir/file#frag",
	       "#foo",
	       "http://example.org/dir/file#foo");
  }

  @Test
  public void testRDFCaseJ() {
    assertResolvesTo("http://example.org/dir/file#frag",
	       "",
	       "http://example.org/dir/file");
  }

  @Test
  public void testNormalizationTrickery() {
    assertResolvesTo("%68ttp://www.m%61th.uio.no/%61bc/#foo", "",
               "http://www.math.uio.no/abc/");
  }

//   public void testEscapedCharsInFragment() {
//     normalizesTo("http://www.math.uio.no/abc/#f%F8%F8",
//                  "http://www.math.uio.no/abc/#f\u00F8\u00F8");
//   }
  
  // --- equals

  @Test
  public void testEqual() throws MalformedURLException {
    URILocator loc1 = new URILocator("http://www.ontopia.net");    
    URILocator loc2 = new URILocator("http://www.ontopia.net");
    Assert.assertTrue("URILocator does not equal itself",
	   loc1.equals(loc2));
  }

  @Test
  public void testNotEqual2() throws MalformedURLException {
    URILocator loc1 = new URILocator("http://www.ontopia.net");    
    URILocator loc2 = new URILocator("http://www.ontopia.com");
    Assert.assertTrue("URILocator equals different URI",
	   !loc1.equals(loc2));
  }

  @Test
  public void testNotEqual() throws MalformedURLException {
    URILocator loc1 = new URILocator("http://www.ontopia.net");    
    Assert.assertTrue("URILocator equals null",
	   !loc1.equals(null));
  }

  // --- constructors

  @Test
  public void testConstructorNull() throws MalformedURLException {
    try {
      new URILocator((String) null);
      Assert.fail("URILocator accepted null argument to constructor"); 
    }
    catch (NullPointerException e) {
    }
  }
  
  // --- internal methods
  
  private void assertIllegal(String uri) {
    try {
      new URILocator(uri);
      Assert.fail("URI '" + uri + "' considered legal");
    }
    catch (MalformedURLException e) {
    }
  }

  private void assertNormalizesTo(String url, String result) {
    try {
      String normalized = new URILocator(url).getAddress();
      Assert.assertTrue("'" + url + "' normalized to '" + normalized + "'",
	     normalized.equals(result));
    }
    catch (MalformedURLException e) {
      throw new OntopiaRuntimeException("ERROR: " + e);
    }
  }

  private void assertResolveInvalid(String base, String uri) {
    try {
      new URILocator(base).resolveAbsolute(uri);
      Assert.fail("URI '" + uri + "' relative to '" + base + "' considered legal");
    }
    catch (OntopiaRuntimeException e) {
    }
    catch (MalformedURLException e) {
      Assert.fail("Base URI '" + base + "' considered illegal");
    }
  }

  private void assertResolvesTo(String base, String url, String result) {
    try {
      String resolved = new URILocator(base).resolveAbsolute(url).getAddress();
      Assert.assertTrue("'" + url + "' relative to '" + base + "' became '" +
	     resolved + "'",
	     resolved.equals(result));
    }
    catch (MalformedURLException e) {
      Assert.fail("IMPOSSIBLE ERROR: " + e);
    }
  }
}
