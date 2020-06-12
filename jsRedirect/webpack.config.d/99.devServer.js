// wrap is useful, because declaring variables in module can be already declared
// module creates own lexical environment
;(function (config) {
    // __dirname = $ROOT/build/js/packages/$PACKAGE_NAME
    // rootProject = $ROOT
    let isBackendRun = false

    config.devServer = config.devServer || {}
    config.devServer.watchOptions = config.devServer.watchOptions || {};
    config.devServer.watchOptions.aggregateTimeout = 500;
    config.devServer.watchOptions.poll = 500;
    config.devServer.open = false;
    config.devServer.port = 8082
    // config.devServer.historyApiFallback = true;
    config.devtool = 'inline-source-map';
})(config);
