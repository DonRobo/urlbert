// wrap is useful, because declaring variables in module can be already declared
// module creates own lexical environment
;(function (config) {
    const shouldRunServer = config.mode !== "production"
    const serverUrl = 'http://localhost:8081'

    if (shouldRunServer) {
        // __dirname = $ROOT/build/js/packages/$PACKAGE_NAME
        // rootProject = $ROOT
        let isBackendRun = false

        config.devServer = config.devServer || {}
        config.devServer.proxy = {
            '/api': serverUrl
        }
        config.devServer.watchOptions = config.devServer.watchOptions || {};
        config.devServer.watchOptions.aggregateTimeout = 5000
        config.devServer.watchOptions.poll = 5000
        config.devServer.open = false
        config.devtool = 'inline-source-map'

    }
})(config);
