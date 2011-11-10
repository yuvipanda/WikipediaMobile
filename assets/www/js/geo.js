function getCurrentPosition() {
    PhoneGap.exec(geoNameSuccess, geoNameFailure, "NearMePlugin", "startNearMeActivity", []);
}

function geoNameSuccess(wikipediaUrl) {
    if(wikipediaUrl) {
        toggleProgress();
        app.loadAndCache(wikipediaUrl);
    }
}

function geoNameFailure(error) {
  console.log(error);
}
