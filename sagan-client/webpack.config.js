const path = require('path');
const { CleanWebpackPlugin } = require('clean-webpack-plugin');
const webpack = require('webpack');
const TerserPlugin = require('terser-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const OptimizeCSSAssetsPlugin = require('optimize-css-assets-webpack-plugin');
const CopyPlugin = require('copy-webpack-plugin');
const WebpackPwaManifest = require('webpack-pwa-manifest')

module.exports = {
    entry: {
        main: './src/app/main.js',
        guide: './src/app/guide.js',
        project: './src/app/project.js',
        team: './src/app/team.js',
        profile: './src/app/profile.js',
        blog: './src/app/blog.js',
        admin: './src/app/admin.js'
    },

    output: {
        path: path.resolve(__dirname, 'build', 'dist'),
        filename: 'js/[name].js'
    },
    plugins: [
        new CleanWebpackPlugin(),
        new MiniCssExtractPlugin({
            filename: 'css/[name].css',
            chunkFilename: "[id].css"
        }),
        new CopyPlugin([
            {from: './src/images', to: 'images'}
        ]),
        new webpack.ProvidePlugin({
            $: "jquery",
            jQuery: "jquery",
            'window.jQuery': 'jquery',
            'window.$': 'jquery'
        }),
        new WebpackPwaManifest({
            name: 'spring',
            short_name: 'Spring',
            description: 'Level up your Java code and explore what Spring can do for you.',
            background_color: '#6db33f',
            inject: false,
            fingerprints: false,
            ios: true,
            crossorigin: null,
            icons: [
                {
                    src: path.resolve('./src/images/spring-logo.png'),
                    sizes: [48, 72, 96, 144, 192, 256, 384, 512],
                },
            ],
        })
    ],
    module: {
        rules: [
            {
                test: /\.css$/,
                use: [
                    {
                        loader: MiniCssExtractPlugin.loader,
                        options: {publicPath: '../'},
                    },
                    'css-loader',
                ]
            },
            {
                test: /.*\/webfonts\/.*$/,
                loader: 'file-loader',
                options: {
                    name: 'css/[name].[ext]',
                },
            },

            {
                test: /.*\/fonts\/.*/,
                loader: 'file-loader',
                options: {
                    name: 'fonts/[name].[ext]',
                },
            },
            {
                test: /^images/,
                loader: 'file-loader',
                options: {
                    name: '[path][name].[ext]',
                },
            }
        ]
    },
    optimization: {
        minimize: true,
        runtimeChunk: false,
        minimizer: [
            new OptimizeCSSAssetsPlugin({}),
            new TerserPlugin()
        ]
    }
};