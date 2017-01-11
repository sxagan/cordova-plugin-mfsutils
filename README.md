# Cordova plugin - MFS Utils

MFS utils

## Using


Install the plugin

    
    $ cordova plugin add https://github.com/sxagan/cordova-plugin-mfsutils.git
    

### isGPSEnabled

    gpscheck.isGPSEnabled(function(result){console.log("isGPSEnabled: ",result)}, function(err){console.log("isGPSEnabled err: ",err)});
### isNetworkEnabled

    gpscheck.isNetworkEnabled(function(result){console.log("isNetworkEnabled: ",result)}, function(err){console.log("isNetworkEnabled err: ",err)});
### getLocation

    gpscheck.getLocation(function(result){console.log("getLocation: ",result)}, function(err){console.log("getLocation err: ",err)});
