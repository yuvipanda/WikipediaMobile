window.urlCache = function() {
	function error(e) {
		console.log("ERROR!" + JSON.stringify(e));
	}
	function getCachedPathForUrl(url) {
		var d = $.Deferred();

		var fileName = Crypto.MD5(url);
		var filePath = "/data/data/org.wikipedia/files/" + fileName;

		function cacheUrl() {
			function saveFile(fileEntry) {
				$.get(url, function(data) {
					fileEntry.createWriter(function(writer) {
						writer.write(data);
						console.log("Writing stuff to " + fileEntry.fullPath);
						writer.onwriteend = function() {
							console.log("written stuff!");
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

		window.resolveLocalFileSystemURI("file://" + filePath, 
				function(fileEntry) {
					console.log("Found!");
					d.resolve(fileEntry.fullPath);
				}, function(error) {
					console.log("Not found!");
					cacheUrl();
				}
		);
		return d;
	}

	function getCachedData(url) {
		var d = $.Deferred();

		//var fileName = Crypto.MD5(url);
		//var filePath = "/data/data/org.wikipedia/files/" + fileName;


		function readFile(fileEntry) {
			var reader = new FileReader();
			reader.onloadend = function(evt) {
				console.log(JSON.stringify(evt));
				d.resolve(evt.target.result);
			};
			console.log('file path is ' + JSON.stringify(fileEntry));
			fileEntry.file(function(file) {reader.readAsText(file); });
		}
		window.requestFileSystem(LocalFileSystem.PERSISTENT, 0, 
				function(fs){
					console.log(url.substring(7));
					fs.root.getFile(url.substring(7), {create: false}, readFile, error);
				});

		return d;
	}

	return {
		getCachedPathForUrl: getCachedPathForUrl,
		getCachedData: getCachedData
	};
}();
