/*
 * Copyright © 2017-2023 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
const webpack = require('webpack');
const writeFilePlugin = require('write-file-webpack-plugin');
const webpackMerge = require('webpack-merge');
const BrowserSyncPlugin = require('browser-sync-webpack-plugin');
const SimpleProgressWebpackPlugin = require('simple-progress-webpack-plugin');
const path = require('path');
const sass = require('sass');

const utils = require('./utils.js');
const commonConfig = require('./webpack.common.js');

const ENV = 'development';

module.exports = (options) => webpackMerge(commonConfig({ env: ENV }), {
    devtool: 'eval-source-map',
    devServer: {
        devMiddleware: {
            stats: options.stats,
        },
        proxy: [{
            context: [
                '/api',
                '/v1',
                '/v2',
                '/services',
                '/management',
                '/swagger-resources',
                '/v2/api-docs',
                '/h2-console',
                '/auth'
            ],
            target: `http${options.tls ? 's' : ''}://localhost:8080`,
            secure: false,
            changeOrigin: options.tls
        }],
        static: {
            directory: './target/classes/static/',
            watch: {
                ignored: /node_modules/
            }
        },
        https: options.tls,
        historyApiFallback: {
            disableDotRule: true
        },
    },
    output: {
        path: utils.root('target/classes/static/'),
        filename: 'app/[name].bundle.js',
        chunkFilename: 'app/[id].chunk.js'
    },
    module: {
        rules: [
            {
                test: /\.scss$/,
                use: ['to-string-loader', 'css-loader', 'postcss-loader', {
                    loader: 'sass-loader',
                    options: { implementation: sass }
                }],
                exclude: /(vendor\.scss|global\.scss)/
            },
            {
                test: /(vendor\.scss|global\.scss)/,
                use: ['style-loader', 'css-loader', 'postcss-loader', {
                    loader: 'sass-loader',
                    options: { implementation: sass }
                }]
            }]
    },
    stats: process.env.JHI_DISABLE_WEBPACK_LOGS ? 'none' : options.stats,
    plugins: [
        process.env.JHI_DISABLE_WEBPACK_LOGS
            ? null
            : new SimpleProgressWebpackPlugin({ format: 'compact' }),
        new BrowserSyncPlugin({
            https: options.tls,
            host: 'localhost',
            port: 9000,
            proxy: {
                target: `http${options.tls ? 's' : ''}://localhost:9060`,
                proxyOptions: {
                    changeOrigin: false  //pass the Host header to the backend unchanged  https://github.com/Browsersync/browser-sync/issues/430
                }
            },
            socket: {
                clients: {
                    heartbeatTimeout: 60000
                }
            }
            /*
            ,ghostMode: { // uncomment this part to disable BrowserSync ghostMode; https://github.com/jhipster/generator-jhipster/issues/11116
                clicks: false,
                location: false,
                forms: false,
                scroll: false
            } */
        }, {
            reload: false
        }),
        new webpack.ContextReplacementPlugin(
            /angular(\\|\/)core(\\|\/)/,
            path.resolve(__dirname, './src/main/webapp/')
        ),
        new writeFilePlugin(),
        new webpack.WatchIgnorePlugin({
            paths: [utils.root('src/test')]
        })
    ].filter(Boolean),
    mode: 'development'
});
