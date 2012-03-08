window.urlCache = function() {
	function error(e) {
		console.log("ERROR!" + JSON.stringify(e));
	}

	function dataUrlForImage(img) {
		var d = $.Deferred();
		// Create an empty canvas element
		var canvas = document.createElement("canvas");
		canvas.width = img.width;
		canvas.height = img.height;

		// Copy the image contents to the canvas
		var ctx = canvas.getContext("2d");
		ctx.drawImage(img, 0, 0);

		var dataURL = canvas.toDataURL("image/png");
		d.resolve(dataURL);
		//return d;
		return dataURL;
		//return dataURL.replace(/^data:image\/(png|jpg);base64,/, "");
	}

	function getCachedPathForUrl(url) {
		var d = $.Deferred();

		var fileName = Crypto.MD5(url);
		var filePath = fileName;

		function cacheUrl() {
			function saveFile(fileEntry) {
				$.get(url, function(data) {
					fileEntry.createWriter(function(writer) {
						writer.write(data);
						writer.onwriteend = function() {
							d.resolve(fileEntry.fullPath);
						};
					});
				});
			}
			window.requestFileSystem(LocalFileSystem.PERSISTENT, 0,
					function(fs){
						fs.root.getFile(filePath, {create: true}, saveFile, error);
					});
			return d;
		}

		window.resolveLocalFileSystemURI(filePath,
				function(fileEntry) {
					d.resolve(fileEntry.fullPath);
				}, function(error) {
					cacheUrl();
				}
		);
		return d;
	}

	function saveCompleteHtml(url, html) {
		// Converts images to Data URIs
		var d = $.Deferred();

		var fileName = Crypto.MD5(url);
		var filePath = fileName;

		var element = $(html);
		var replacements = {};
		function saveFile(fileEntry) {
			fileEntry.createWriter(function(writer) {
				writer.write(html);
				writer.onwriteend = function() {
					d.resolve(fileEntry.fullPath);
				};
			});
		}

		// Incredibly wasteful hack going to happen. I'm sorry
		// I am parsing the entire HTML again, *and* doing string replacement
		// FIXME: Do only one stupid thing, not two
		// TODO: Check if Images are actually loaded
		element.find("img").each(function(i, img) {
			replacements[$(img).attr("src")] =  urlCache.dataUrlForImage(img);
		});

		$.each(replacements, function(href, data) {
			html = html.replace(href, data);
		});

		window.requestFileSystem(LocalFileSystem.PERSISTENT, 0,
			function(fs){
				fs.root.getFile(filePath, {create: true}, saveFile, error);
			});
		return d;
	}

	function getCachedData(url) {
		var d = $.Deferred();

		var fileName = Crypto.MD5(url);
		var filePath = fileName;


		function readFile(fileEntry) {
			var reader = new FileReader();
			reader.onloadend = function(evt) {
				d.resolve(evt.target.result);
			};
			fileEntry.file(function(file) {reader.readAsText(file); });
		}
		window.requestFileSystem(LocalFileSystem.PERSISTENT, 0,
				function(fs){
					fs.root.getFile(filePath, {create: false}, readFile, error);
				});

		return d;
	}

	return {
		getCachedPathForUrl: getCachedPathForUrl,
		getCachedData: getCachedData,
		saveCompleteHtml: saveCompleteHtml,
		dataUrlForImage: dataUrlForImage
	};
}();
