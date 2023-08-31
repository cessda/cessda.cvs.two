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
const webpackMerge = require('webpack-merge');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const BundleAnalyzerPlugin = require('webpack-bundle-analyzer').BundleAnalyzerPlugin;
const MomentLocalesPlugin = require('moment-locales-webpack-plugin');
const TerserPlugin = require('terser-webpack-plugin');
const WorkboxPlugin = require('workbox-webpack-plugin');

const utils = require('./utils.js');
const commonConfig = require('./webpack.common.js');

const ENV = 'production';
const sass = require('sass');

module.exports = webpackMerge(commonConfig({ env: ENV }), {
    // Enable source maps. Please note that this will slow down the build.
    // You have to enable it in Terser config below and in tsconfig.json as well
    // devtool: 'source-map',
    output: {
        path: utils.root('target/classes/static/'),
        filename: 'app/[name].[fullhash].bundle.js',
        chunkFilename: 'app/[id].[fullhash].chunk.js'
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
            use: [
                {
                    loader: MiniCssExtractPlugin.loader,
                    options: {
                        publicPath: '../'
                    }
                },
                'css-loader',
                'postcss-loader',
                {
                    loader: 'sass-loader',
                    options: { implementation: sass }
                }
            ]
        },
        {
            test: /\.css$/,
            use: ['to-string-loader', 'css-loader'],
            exclude: /(vendor\.css|global\.css)/
        },
        {
            test: /(vendor\.css|global\.css)/,
            use: [
                {
                    loader: MiniCssExtractPlugin.loader,
                    options: {
                        publicPath: '../'
                    }
                },
                'css-loader',
                'postcss-loader'
            ]
        }]
    },
    optimization: {
        runtimeChunk: false,
        minimizer: [
            new TerserPlugin({
                parallel: true,
                // sourceMap: true, // Enable source maps. Please note that this will slow down the build
                terserOptions: {
                    ecma: 6,
                    ie8: false,
                    toplevel: true,
                    module: true,
                    compress: {
                        dead_code: true,
                        warnings: false,
                        properties: true,
                        drop_debugger: true,
                        conditionals: true,
                        booleans: true,
                        loops: true,
                        unused: true,
                        toplevel: true,
                        if_return: true,
                        inline: true,
                        join_vars: true,
                        ecma: 6,
                        module: true
                    },
                    output: {
                        comments: false,
                        beautify: false,
                        indent_level: 2,
                        ecma: 6
                    },
                    mangle: {
                        module: true,
                        toplevel: true
                    }
                }
            })
        ]
    },
    plugins: [
        new MiniCssExtractPlugin({
            // Options similar to the same options in webpackOptions.output
            // both options are optional
            filename: 'content/[name].[contenthash].css',
            chunkFilename: 'content/[id].css'
        }),
        new MomentLocalesPlugin({
            localesToKeep: [
                    'en',
                    'sq',
                    'cs',
                    'da',
                    'nl',
                    'et',
                    'fi',
                    'fr',
                    'de',
                    'hu',
                    'it',
                    'ja',
                    'pl',
                    'pt-pt',
                    'ro',
                    'ru',
                    'sk',
                    'sr',
                    'es',
                    'sv'
                    // jhipster-needle-i18n-language-moment-webpack - JHipster will add/remove languages in this array
                ]
        }),
        new BundleAnalyzerPlugin({
            analyzerMode: 'static',
            openAnalyzer: false,
            // Webpack statistics in target folder
            reportFilename: '../stats.html'
        }),
        new webpack.LoaderOptionsPlugin({
            minimize: true,
            debug: false
        }),
        new WorkboxPlugin.GenerateSW({
            clientsClaim: true,
            skipWaiting: true,
            exclude: [/swagger-ui/]
        })
    ],
    mode: 'production'
});
