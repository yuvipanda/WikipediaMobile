REVISION=113645
remotes:
	mkdir assets/www/images
	curl -o assets/www/js/references.js \
		"https://gerrit.wikimedia.org/r/gitweb?p=mediawiki/extensions/MobileFrontend.git;a=blob_plain;f=javascripts/references.js;hb=77c2ba99dcf263e2f6d005b431d0a49ef10b2f8a"
	curl -o assets/www/js/toggle.js \
		"http://svn.wikimedia.org/viewvc/mediawiki/trunk/extensions/MobileFrontend/javascripts/toggle.js?view=co&revision=$(REVISION)&content-type=text%2Fplain&pathrev=$(REVISION)"
	curl -o assets/www/common.css \
		"http://svn.wikimedia.org/viewvc/mediawiki/trunk/extensions/MobileFrontend/stylesheets/common.css?view=co&revision=$(REVISION)&content-type=text%2Fplain&pathrev=$(REVISION)"
	curl -o assets/www/references.css \
		"https://gerrit.wikimedia.org/r/gitweb?p=mediawiki/extensions/MobileFrontend.git;a=blob_plain;f=stylesheets/references.css;hb=77c2ba99dcf263e2f6d005b431d0a49ef10b2f8a"
	curl -o assets/www/images/close-button-beta.png \
		"https://gerrit.wikimedia.org/r/gitweb?p=mediawiki/extensions/MobileFrontend.git;a=blob_plain;f=stylesheets/images/close-button-beta.png;hb=HEAD"

sitematrix:
	curl -o assets/www/sitematrix.json \
		"http://en.wikipedia.org/w/api.php?action=sitematrix&format=json"

clean:
	rm assets/www/js/toggle.js
	rm assets/www/common.css
	rm assets/www/js/references.js
	rm assets/www/references.css
	rm -rf assets/www/images
