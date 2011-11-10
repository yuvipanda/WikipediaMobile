var Cache = function() {
}

Cache.prototype.getCachedPathForURI = function(uri, success, fail) {

   return PhoneGap.exec(
        success,
        fail,
        'URLCache',
        'getCachedPathForURI',
        [uri]
    );
};

Cache.prototype.getCachedPage = function(uri, success, fail) {

   return PhoneGap.exec(
        success,
        fail,
        'URLCache',
        'getCachedPage',
        [uri]
    );
};

PhoneGap.addPlugin('urlCache', new Cache());
