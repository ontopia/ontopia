CodeMirror.defineSimpleMode("rest-uri", {
	start: [
		{regex: /^(GET|PUT|POST|DELETE)/, token: "rest-method"},
		{regex: /\{[\w\d]+\}/, token: "rest-parameter"},
		{regex: /\[[\w\d]+\]/, token: "rest-option"}
	]
});
//CodeMirror.defineMIME("rest-uri","rest-uri");
