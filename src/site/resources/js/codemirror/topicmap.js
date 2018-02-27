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
