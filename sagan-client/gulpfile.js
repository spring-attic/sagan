var cssmin = require('gulp-minify-css'),
    gulp_jspm = require('gulp-jspm'),
    rename = require("gulp-rename"),
    gulp = require('gulp');

var paths = {
    main: 'src/app/main.js',
    maps: 'src/app/maps.js',
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

gulp.task('jspm-main', function () {
    return gulp.src(paths.main)
        .pipe(gulp_jspm({minify: true}))
        .pipe(rename('main.js'))
        .pipe(gulp.dest(paths.dest + "app"));
});

gulp.task('jspm-maps', function () {
    return gulp.src(paths.maps)
        .pipe(gulp_jspm({minify: true}))
        .pipe(rename('maps.js'))
        .pipe(gulp.dest(paths.dest + "app"));
});

gulp.task('build', ['minify-css', 'copy-external-css', 'jspm-main', 'jspm-maps', 'copy-assets'], function () {
});