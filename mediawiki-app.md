Making a mobile app for your MediaWiki instance is a relatively simple task.

Note that the app is built assuming a multilingual wiki - you might want to strip out some content language related features if your wiki is available in only one language. 

## Prerequisites ##

You need to have [Extension:MobileFrontend][mf] installed and configured properly on your MediaWiki instance. Automatic mobile device redirection is not required. 

## Changing URLs ##

### Change Domain ###

You will need to edit `app.js` file, modifying the function `baseUrlForLanguage` to point to your MediaWiki instances' URL. 

For example, if your MediaWiki instance is hosted at `http://wiki.yuvi.in`, you will modify the function to read:
	
	function baseUrlForLanguage(lang) {
		return 'http://wiki.yuvi.in';
	}

You can ignore the `lang` parameter if your wiki does not use the interwiki system for languages. Both `http` and `https` wikis are supported

### Change API URL ###

The app assumes that the API URL is always `baseURL + '/w/api.php`. This might not be true for your installation. If you want to change the location of `api.php`, you need to edit the function `makeAPIRequest` in `app.js`.

If your wiki is at `http://wiki.yuvi.in` and your `api.php` is at `http://wiki.yuvi.in/wiki/api.php`, then you would change the current line:
	
	var url = app.baseUrlForLanguage(lang) + '/w/api.php';
	
to

	var url = app.baseUrlForLanguage(lang) + '/wiki/api.php';


## Remove unnecessary features ##

TODO: Provide diffs to remove these features. Or maintain separate branch without them.

The most Wikipedia specific feature in the app is the `Nearby` feature. You can remove it by deleting `geo.js` and all references to it - 1 in `app.js`, 1 in `menu.js`, 1 under `<menu>` in `index.html`.

### Single Language Wikis ###
If your Wiki is one language only, then the Interwiki Language features do not make much sense. While the app is built to be completely language aware at all parts, it is relatively easy to hide the functionality from the user. 

*On Android*: Removing the `<command>` inside `<menu>` in the `index.html` file with the id `languageCmd` should be sufficient to hide the menu item on Android
*On iOS*: Remove the entry for `read-in` from the list `menu_items` (*Warning*: Untested!)

## Swap Resources ##

You'd need to change Graphics and rename resources!

### Renaming ###

The entire app first needs to be renamed. The best way to do it is to open it in Eclipse and use its *rename* feature.

You should also look into `res/values/strings.xml` file and change any references to Wikipedia to your Wiki's name

### About Page ###

You should completely re-do the about page, to refer to your own Wiki. This can be done by replacing the contents of `<div id="about-page-overlay">` in `index.html`.

### Swapping Graphics ###

Final step would be replacing any Wikipedia specific images with images specific to your own wiki. The following list of images need to be changed:

1. `assets/www/image/spr.png` - contains a sprite of images, including a white 'W' logo that is used in the search bar of the app. Replace with similarly colored / styled logo in same position.
2. `res/drawable-{ldpi|mdpi|hdpi|xhdpi}/icon.pnh` -> App's Icon. Replace with appropriately sized images.

[mf]: https://www.mediawiki.org/wiki/Extension:MobileFrontend
