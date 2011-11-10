function Application() {}

Application.prototype.loadAndCachePage = function(url) {
    var gotCachedPage = function(cachedPage) {
        $('#main').attr('src', cachedPage.file);
        currentHistoryIndex += 1;
    }
    var gotError = function(error) {
        alert(error.message + ' ' + error.status);
        noConnectionMsg();
        navigator.app.exitApp();
    }
    window.plugins.urlCache.getCachedPage(url, gotCachedPage, gotError);
}

var app = new Application();
