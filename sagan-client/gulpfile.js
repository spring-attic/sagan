var cssmin = require('gulp-minify-css'),
    jspm = require('gulp-jspm-build'),
    gulp = require('gulp');

var paths = {
    app: 'src/app/',
    css: {
        files: ['src/css/*.css']
    },
    external_css: [
        'src/jspm_packages/npm/eonasdan-bootstrap-datetimepicker@4.15.35/build/css/bootstrap-datetimepicker.min.css',
        'src/jspm_packages/github/twbs/bootstrap@2.3.2/docs/assets/css/bootstrap.css',
        'src/jspm_packages/github/FortAwesome/font-awesome@3.2.1/css/font-awesome.min.css'
    ],
    assets: ['src/img*/**', 'src/*.txt', 'src/*.html', 'src/font*/**', 'src/css*/filterable-list.css',
    'src/config.js', 'src/jspm_packages*/system*', 'src/jspm_packages*/**/*.ttf', 'src/jspm_packages*/**/*.woff'],
    dest: './build/dist/'
};

// concat and minify CSS files
gulp.task('minify-css', function () {
    return gulp.src(paths.css.files)
        .pipe(cssmin({}))
        .pipe(gulp.dest(paths.dest + 'css'));
});

// copy external css
gulp.task('copy-external-css', function () {
    return gulp.src(paths.external_css)
        .pipe(gulp.dest(paths.dest + 'lib/css'));
});

// copy assets
gulp.task('copy-assets', function () {
    return gulp.src(paths.assets)
        .pipe(gulp.dest(paths.dest));
});

// build JSPM modules
gulp.task('jspm', function () {
    jspm({
        bundleOptions: {
            minify: true,
            mangle: true
        },
        bundles: [
            {src: 'app/main', dst: 'main.js'},
            {src: 'app/maps', dst: 'maps.js'},
            {src: 'app/admin', dst: 'admin.js'}
        ]
    })
    .pipe(gulp.dest(paths.dest + "app"));
});

gulp.task('build', ['minify-css', 'copy-external-css', 'jspm', 'copy-assets'], function () {});