module.exports = {
    isGPSEnabled: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "GPSCheck", "isGPSEnabled", []);
    },
    isNetworkEnabled: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "GPSCheck", "isNetworkEnabled", []);
    },
    getLocation: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "GPSCheck", "getLocation", []);
    }
};