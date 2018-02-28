/* global CodeMirror, Ontopia */

(function(win) {
	"use strict";
	
	function menu() {
		$('#sidebar').metisMenu();

		var url = window.location.href.replace(window.location.hash,"");
		var element = $('ul.nav a').filter(function() {
			return this.href === url;
		}).addClass('active').parent();

		while (true) {
			if (element.is('li')) {
				element = element.parent().addClass('in').parent();
			} else {
				break;
			}
		}
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
