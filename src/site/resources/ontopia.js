/* global CodeMirror, Ontopia */

(function(win) {
	"use strict";
	
	function menu() {
		$('ul.nav a').filter((_,e) => e.href === window.location.href.replace(window.location.hash,""))
			.addClass('active') // direct li
			.parents('li').addClass('mm-active') // li's
			.children('a').attr('aria-expanded', true) // links
			.siblings('ul').addClass('mm-show'); // ul's
		$('#sidebar').metisMenu();
	}
	
	function highlight() {
		$('pre code[class]').each(function(i, block) {
			var modes = $(block).attr("class").trim().split(';');
			if (modes.length > 1) {
				$(block).addClass(modes[1]);
			}
			modes[0] = modes[0].indexOf('language-') === 0 ? modes[0].substring(9) : modes[0];
			$(block).attr('data-foo', modes[0]);
			CodeMirror.runMode($(block).text(), modes[0], block);
			$(block).addClass('cm-s-default');
		});
	};
	
	function footnotes() {
		var footnotes = $('span.footnote');
		if (footnotes.length > 0) {
			var dl = $('section').append('<h3>Footnotes</h3><dl></dl>').find('dl').last();
			footnotes.each(function(i, e) {
				$(dl).append('<dt><a name="footnote-' + (i+1) + '">[' + (i+1) + ']</a></dt><dd><p>' + $(this).text() + '</p></dd>');
				$(this).replaceWith('<sup><a href="#footnote-' + (i+1) + '" class="footnote" data-toggle="tooltip" data-placement="bottom" data-container="body" title="' + $(this).text() + '">[' + (i+1) + ']</a></sup>');
			});
			$('[data-toggle="tooltip"]').tooltip();
		}
	};
	
	function missingLinks() {
		var missing = $('a[href]').filter(function(e, a) {
			return a.href.indexOf('#') !== -1; 
		}).map(function(e, a) { 
			return a.href.substring(a.href.indexOf('#') + 1);
		}).filter(function(e, a) {
			return a !== '';
		}).filter(function(e, a) {
			return $('a[name="' + a + '"]').length === 0;
		});
		if (missing.length > 0) {
			console.error('Unresolvable internal links found!', missing);
		}

		var missing2 = $('a[href]').filter(function(e, a) {
			return $(a).html().indexOf('unknown') !== -1;
		}).map(function(e, a) {
			return a.href;
		});
		if (missing2.length > 0) {
			console.error('Unnamed internal links found!', missing2);
		}
	};
	
	win.Ontopia = {
		"highlight": highlight,
		"footnotes": footnotes,
		"missingLinks": missingLinks,
		"menu": menu
	};
})(this);

$(document).ready(function() {
	Ontopia.menu();
	Ontopia.highlight();
	Ontopia.footnotes();
//	Ontopia.missingLinks();
});

CodeMirror.defineSimpleMode("rest-uri", {
	start: [
		{regex: /^(GET|PUT|POST|DELETE)/, token: "rest-method"},
		{regex: /\{[\w\d]+\}/, token: "rest-parameter"},
		{regex: /\[[\w\d]+\]/, token: "rest-option"}
	]
});
//CodeMirror.defineMIME("rest-uri","rest-uri");

CodeMirror.defineMode("tolog", function(config) {
	
	var findWordExpr = /[\w:-]/;
	var findStringExpr = /[^"]/;
	var findParameterExpr = /[^%]/;
	var findVariableExpr = /\w/;
	var whitespaceRegex = /\s/;
	
	var keywords = ["import", "as", "using", "for", "select", "from",
					"order", "by", "asc", "desc", "count", "limit", "offset",
					"delete", "insert", "update", "merge"];
	
	var predicates = ["association", "association-role", "base-locator", "datatype", "direct-instance-of",
					"instance-of", "item-identifier", "object-id", "occurrence", "reifies", "resource", 
					"role-player", "scope", "subject-identifier", "subject-locator", "topic", "topicmap",
					"type", "value-like", "value", "variant", "not", "topic-name"];
	
	return {
		startState: function() {
			return {
				comment: false
			};
		},
		token: function(stream, state) {
			if (state.comment) {
				stream.eatWhile(/[^\*]/);
				stream.next();
				if (stream.peek() === '/') {
					stream.next();
					state.comment = false;
				}
				return 'tolog-comment';
			}

			var ch = stream.next();
			switch (ch) {
				case '(': case ')': return null;
				case '"':
					stream.eatWhile(findStringExpr);
					stream.next();
					return "tolog-string";
				case '%':
					stream.eatWhile(findParameterExpr);
					stream.next();
					return "tolog-parameter";
				case '?': return "tolog-questionmark";
				case '$': 
					stream.eatWhile(findVariableExpr);
					return "tolog-variable";
				case '/':
					if (stream.peek() === '*') {
						stream.next();
						stream.eatWhile(/[^\*]/);
						stream.next();
						if (stream.eol()) {
							state.comment = true;
						}
						if (stream.peek() === '/') {
							stream.next();
							state.comment = false;
						}
						return 'tolog-comment';
					}
					break;
				default:
					if (whitespaceRegex.test(ch)) return null;

					var word = "";
					do {
						if (ch) word = word + ch;
						ch = stream.eat(findWordExpr);
					} while (ch)

					if (word === "") stream.next();
					else if (word.indexOf(':') !== -1) return "tolog-identifier";
					else if (keywords.indexOf(word.toLowerCase()) !== -1) return "tolog-keyword";
					else if (predicates.indexOf(word.toLowerCase()) !== -1) return "tolog-predicate";
					else if (stream.peek() === '(') return "tolog-identifier";
			}
		}
	};
});
CodeMirror.defineMIME("text/x-tolog","tolog");

CodeMirror.defineMode("ltm", function(config) {

	var findStringExpr = /[^"]/;
	var dynamicAssoc = /([\w]+:)?[\w-_\.]+\(/;
	
	return {
		startState: function() {
			return {
				comment: false
			};
		},
		token: function(stream, state) {
			if (state.comment) {
				stream.eatWhile(/[^\*]/);
				stream.next();
				if (stream.peek() === '/') {
					stream.next();
					state.comment = false;
				}
				return 'ltm-comment';
			}
			
			if (stream.match(dynamicAssoc)) {
				return "ltm-assoc";
			}
			
			var first = stream.sol(), ch = stream.next();
			switch (ch) {
				case '"':
					stream.eatWhile(findStringExpr);
					stream.next();
					return "ltm-string";
				case '%': // indicator
					stream.next();
					stream.skipTo('"');
					stream.next();
					return "ltm-locator";
				case '@': // indicator
					stream.next();
					stream.skipTo('"');
					stream.next();
					return first ? "ltm-encoding" : "ltm-psi";
				case '#': // directive
					stream.skipTo(' ');
					return 'ltm-directive';
				case '/':
					if (stream.peek() === '*') {
						stream.next();
						stream.eatWhile(/[^\*]/);
						stream.next();
						if (stream.eol()) {
							state.comment = true;
						}
						if (stream.peek() === '/') {
							stream.next();
							state.comment = false;
						}
						return 'ltm-comment';
					} else {
						stream.eatSpace();
						stream.eatWhile(/[:\w-_\.]/);
						return "ltm-scope";
					}
				case '[':
					if (stream.peek() === '[') {
						stream.eatWhile(/[^\]]/);
						stream.next();
						if (stream.peek() === ']') {
							stream.next();
							return "ltm-occurrence-value";
						}
					} else {
						return "ltm-topic";
					}
				case ']': return "ltm-topic";
				case ')': return "ltm-assoc";
				case '{': 
				case '}': return "ltm-occurrence";
				case '~':
					stream.eatSpace();
					stream.eatWhile(/[:\w-_\.]/);
					return "ltm-reification";
			}
		}
	};
});
CodeMirror.defineMIME("text/x-ltm","ltm");
