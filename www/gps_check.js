module.exports = {
    isGPSEnabled: function (successCallback, errorCallback) {
    	console.log('run isGPSEnabled')
        cordova.exec(successCallback, errorCallback, "GPSCheck", "isGPSEnabled", []);
    },
    isNetworkEnabled: function (successCallback, errorCallback) {
    	console.log('run isNetworkEnabled')
        cordova.exec(successCallback, errorCallback, "GPSCheck", "isNetworkEnabled", []);
    },
    getLocation: function (successCallback, errorCallback) {
    	console.log('run getLocation')
        cordova.exec(successCallback, errorCallback, "GPSCheck", "getLocation", []);
    }

};