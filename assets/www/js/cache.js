function Application() {}

Application.prototype.loadAndCachePage = function(url) {
    var gotCachedPage = function(cachedPage) {
        $('#main').one('load', function() {
            addToHistory(url);
        });
        $('#main').attr('src', cachedPage.file);
        currentHistoryIndex += 1;
        window.history.pushState({page: currentHistoryIndex}, "", cachedPage.file);
    }
    var gotError = function(error) {
        alert(error.message + ' ' + error.status);
        noConnectionMsg();
        navigator.app.exitApp();
    }
    window.plugins.urlCache.getCachedPage(url, gotCachedPage, gotError);
}

var app = new Application();

window.onpopstate = function(event) {
    event.preventDefault();
    console.log("location: "+document.location+", state: "+JSON.stringify(event.state));
    if(event.state == null) {
        console.log("no more history to browse exiting...");
        navigator.app.exitApp();
    }
    $('#main').attr('src', document.location);
}
