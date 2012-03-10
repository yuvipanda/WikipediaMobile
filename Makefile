REVISION=113442
remotes:
	curl -o assets/www/js/application.js \
		"http://svn.wikimedia.org/viewvc/mediawiki/trunk/extensions/MobileFrontend/javascripts/application.js?view=co&revision=113442&content-type=text%2Fplain&pathrev=$(REVISION)"
	curl -o assets/www/common.css \
		"http://svn.wikimedia.org/viewvc/mediawiki/trunk/extensions/MobileFrontend/stylesheets/common.css?view=co&revision=113442&content-type=text%2Fplain&pathrev=$(REVISION)"

clean:
	rm assets/www/js/application.js
	rm assets/www/common.css
