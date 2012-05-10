(function() {
	var replacements = {
		"table.gallery .thumb[style]": {
			border: "0",
			padding: "0",
			width: "auto",
			margin: "0 auto"
		},
		"table.geography[style] td[style], table.geography[style] td[colspan='2']": "geography-table-hack-1",
		"table[style]": {
			float: "none",
			"margin-left": "0",
			width: "100%"
		},
		"table.navbox div[style]": {
			"padding-left": "0",
			"padding-right": "0"
		},
		"table.gallery .gallerybox[style]": {
			width: "auto",
			"margin-bottom": "-16px"
		},
		"table.navbox div[style] a, table.navbox span[style]": {
			"white-space": "pre-wrap"
		},
		"table.infobox td[colspan='3'], table.infobox th[colspan='3']": {
			border: "1px solid #ccc"
		},
		".thumb .thumbinner[style]": {
			margin: "5px auto",
			"max-width": "100%",
			width: "auto"
		}
	};
	chrome.doStyleHacks = function(parent) {
		$.each(replacements, function(selector, cssProps) {
			$(selector, parent).css(cssProps);
		});
	};
})();
